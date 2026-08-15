# DJI Cloud API SDK — command 子包示范 POJO 设计

- **状态**：待审查
- **日期**：2026-08-14
- **版本**：v1.0（示范 5 个核心指令 POJO 模板）
- **关联文档**：
  - [SDK 架构设计](../../architecture-design.md)
  - [SDK TDD 测试用例](../../tdd-test-cases.md)
  - [simulator 设计文档](../../../../../hivemind-simulator/docs/superpowers/specs/2026-08-08-dji-dock-simulator-design.md)

---

## 1. 项目概述

### 1.1 背景与定位

`command/` 子包是 SDK 协议定义层的延伸，定义 DJI services/drc/events 通道指令的**请求 data 字段**与**回复 output 字段**的强类型 record。只定义「字段是什么」，不实现「如何处理」。

### 1.2 设计目标

| 目标 | 说明 |
|---|---|
| **强类型 POJO** | 用 record 替代 simulator 现有 `Map<String,Object>` + `JsonNode` 弱类型处理 |
| **协议可追溯** | 每个字段标注 `@Verified`（simulator 已对接）或 `@Inferred`（待真机/文档确认） |
| **与现有模块对齐** | 与 `protocol/{topic,method,envelope,error}` 风格一致，纯 record 不引入接口抽象 |
| **示范模板** | 本次 5 个核心指令覆盖同步/异步、多字段/无字段、简单/复杂嵌套、无 output/有 output 场景 |

### 1.3 非目标

- **不实现指令处理逻辑** — simulator handler 职责
- **不定义信封结构** — `protocol/envelope/` 职责
- **不定义 method 名称** — `protocol/method/` 职责
- **不做字段校验** — POJO 只定义字段，校验是 simulator/handler 职责
- **不做默认值兜底** — 如 `max_speed` 默认 10 是 simulator 业务逻辑，不在 POJO 实现
- **不引入 marker interface** — 不定义 `ServiceRequest`/`ServiceReply` 接口，保持纯 record

---

## 2. 架构与定位

### 2.1 与现有模块的关系

- 与 `protocol/envelope/` 组合使用：`XxxRequest` 作为 `RequestEnvelope.data`，`XxxReply` 作为 `ReplyEnvelope.data.output`
- 与 `protocol/method/` 对齐：每个 POJO 对应一个 `ServiceMethod`/`DrcMethod`/`EventMethod` 枚举值
- 不引入接口抽象（不定义 `ServiceRequest` marker interface），保持纯 record 风格，与 `DroneOsd`/`DockOsd` 一致

### 2.2 与 MessageCodec 的协作

依赖 `MessageCodec` 已配置的：
- `FAIL_ON_UNKNOWN_PROPERTIES=false` — 兼容协议字段增量，未知字段不报错
- `PropertyNamingStrategies.SNAKE_CASE` — snake_case JSON ↔ camelCase record 双向匹配（已修复缺陷 #1）

---

## 3. 包结构

```
ltd.cdmi.dji.cloudapi.sdk.command
├── service/                    ← services 通道指令 POJO（本次 5 个示范）
│   ├── FlighttaskPrepareRequest.java
│   ├── FlighttaskPrepareReply.java
│   ├── FlighttaskExecuteRequest.java
│   ├── FlighttaskExecuteReply.java
│   ├── LiveStartPushRequest.java
│   ├── LiveStartPushReply.java
│   ├── DroneOpenRequest.java
│   ├── DroneOpenReply.java
│   ├── TakeoffToPointRequest.java
│   ├── TakeoffToPointReply.java
│   └── package-info.java       ← 标注策略说明
├── drc/                         ← drc 通道指令 POJO（后续批次）
├── event/                       ← events 通道事件 POJO（后续批次）
└── package-info.java            ← command 子包总说明
```

### 3.1 命名规则

- `XxxRequest` = 指令请求 data（如 `FlighttaskPrepareRequest` 对应 `flighttask_prepare` 的 data）
- `XxxReply` = 指令回复 output（如 `FlighttaskPrepareReply` 对应 `flighttask_prepare` 的 output）
- 嵌套对象用独立 record（如 `FlighttaskFile`、`ReadyConditions`、`MultiDockTask`、`SimulateMission`），与主 Request 同包

---

## 4. POJO 设计模式

### 4.1 字段命名与类型

- Java record 用 camelCase（如 `flightId`），依赖 `MessageCodec` 的 `SNAKE_CASE` 命名策略与 DJI JSON snake_case 双向匹配
- 一律用包装类型（`Integer`/`Double`/`Long`/`String` 而非 `int`/`double`/`long`），允许 `null` 表示「字段缺失」
- 嵌套对象用 record 引用，缺失时为 `null`
- **必填字段校验**：record compact constructor 用 `Objects.requireNonNull` 校验必填字段，缺失即抛 `NullPointerException` 快速失败，避免调用方拿 null 误判

### 4.2 标注策略

- 类级 `@DocUrl`：指向 DJI 官方文档
- 类级 `@Verified basis="simulator 已对接 hivemind 验证"`：simulator 代码已实现并验证的字段
- 字段级或类级 `@Inferred reason/verifyPoint`：simulator 未覆盖或字段含义不确定的项

### 4.3 R1 空 Reply 对称性约定

**约定**：每个指令恒有 `XxxRequest` + `XxxReply`。对于 DJI 协议规定「services_reply 仅返回 result=0、无 output 字段」的指令，`XxxReply` 为空 record（占位），以保证调用方代码模式统一。

**缓解开发者困扰的 3 项文档约定**：

1. **空 record 用 Javadoc 醒目标注**：
   ```java
   /**
    * flighttask_prepare 指令回复 output。
    *
    * <p><b>本指令无 output 字段</b>：services_reply 仅返回 {@code data.result=0} 表示成功，
    * 本 record 为占位以保证 Request/Reply 对称性，无业务字段。
    *
    * <p>若 DJI 协议后续为本指令补充 output 字段，将在此 record 扩展。
    */
   @Verified(basis = "simulator WaylineTaskSimulator.handlePrepare 已对接 hivemind 验证：返回 result=0 无 output")
   public record FlighttaskPrepareReply() {}
   ```

2. **package-info.java 统一约定**（见 §6.2）

3. **调用方使用示例**（写入 package-info）：
   ```java
   // 反序列化 services_reply
   ReplyEnvelope envelope = MessageCodec.fromJson(json, ReplyEnvelope.class);
   // 无 output 指令：envelope.data().result() 即足够，XxxReply 为空 record 不承载信息
   if (envelope.data().result() == 0) { /* 成功 */ }
   // 有 output 指令：进一步反序列化 output
   DroneOpenReply reply = MessageCodec.convert(envelope.data().output(), DroneOpenReply.class);
   ```

---

## 5. 错误处理

### 5.1 反序列化容错

- **必填字段缺失** → record compact constructor 中 `Objects.requireNonNull` 抛 `NullPointerException` 快速失败，调用方立即知道哪个字段缺失（不撞墙）
- **可选字段缺失** → record 字段为 null，调用方自行判空（如 `multi_dock_task` 非蛙跳时为 null 合理）
- 类型不匹配（如期望 Integer 收到 String）→ Jackson 抛 `JsonProcessingException`，由 `MessageCodec.fromJson` 包装为 `IllegalStateException`（现有行为，不新增）
- POJO 不做默认值兜底（如 `max_speed` 默认 10 是 simulator 业务逻辑，不在 POJO 实现）

### 5.1.1 必填字段校验清单

| Request | 必填字段 | DJI JSON 字段名 |
|---|---|---|
| `FlighttaskPrepareRequest` | `flightId` | `flight_id` |
| `FlighttaskExecuteRequest` | `flightId` | `flight_id` |
| `LiveStartPushRequest` | `videoId`/`url`/`urlType`/`videoQuality` | `video_id`/`url`/`url_type`/`video_quality` |
| `DroneOpenRequest` | 无参数，无必填 | — |
| `TakeoffToPointRequest` | `flightId`/`targetLatitude`/`targetLongitude`/`targetHeight` | `flight_id`/`target_latitude`/`target_longitude`/`target_height` |

### 5.2 @Inferred 标注策略

- simulator 已对接验证的字段 → 类级 `@Verified basis="simulator XxxSimulator.handleXxx 已对接 hivemind 验证"`
- simulator 未覆盖/字段含义不确定 → 字段级 `@Inferred reason=... verifyPoint=...`
- 本次 5 个示范指令均来自 simulator 已实现代码，整体标 `@Verified`；个别字段（如 `wayline_precision_type` 字段含义）若 DJI 文档未明确，标 `@Inferred`

---

## 6. 示范指令字段清单

### 6.1 flighttask_prepare（同步，无 output）

**请求 data 字段**（依据 [WaylineTaskSimulator.handlePrepare](../../../../../hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/WaylineTaskSimulator.java) + logFlightTaskPrepareParams）：

| 字段名 | 类型 | 必填 | 嵌套 record | 依据 |
|---|---|---|---|---|
| flight_id | String | 是 | — | @Verified basis=simulator 已对接 |
| task_type | Integer | 否 | — | @Verified（0=立即/1=定时/2=条件） |
| execute_time | Long | 否 | — | @Verified（定时任务） |
| file | FlighttaskFile | 否 | FlighttaskFile(url, fingerprint) | @Verified |
| rth_altitude | Integer | 否 | — | @Verified（min=20 max=1500 相对起飞点 ALT） |
| rth_mode | Integer | 否 | — | @Verified |
| out_of_control_action | Integer | 否 | — | @Verified |
| exit_wayline_when_rc_lost | Integer | 否 | — | @Verified |
| wayline_precision_type | Integer | 否 | — | @Inferred reason="字段含义 DJI 文档未明确" |
| ready_conditions | ReadyConditions | 否 | ReadyConditions(batteryCapacity, beginTime, endTime) | @Verified（task_type=2 时必填） |
| executable_conditions | ExecutableConditions | 否 | ExecutableConditions(storageCapacity) | @Verified |
| break_point | BreakPoint | 否 | BreakPoint(index, state, progress, waylineId) | @Verified |
| simulate_mission | SimulateMission | 否 | SimulateMission(isEnable, latitude, longitude, altitude) | @Verified |
| flight_safety_advance_check | Integer | 否 | — | @Verified |

**回复 output**：无（result=0）
**特性**：同步，无 output
**Reply**：`FlighttaskPrepareReply()` 空 record

### 6.2 flighttask_execute（异步，无 output）

**请求 data 字段**（依据 [WaylineTaskSimulator.handleExecute](../../../../../hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/WaylineTaskSimulator.java) + parseMultiDockTask）：

| 字段名 | 类型 | 必填 | 嵌套 record | 依据 |
|---|---|---|---|---|
| flight_id | String | 是 | — | @Verified |
| multi_dock_task | MultiDockTask | 否 | MultiDockTask(wirelessLinkTopo, dockInfos) | @Verified |

**MultiDockTask 嵌套结构**：
- `wireless_link_topo`: WirelessLinkTopo(secretCode, centerNode, leafNodes)
  - `center_node`: CenterNode(sn)
  - `leaf_nodes`: List<LeafNode>
- `dock_infos`: List<DockInfo>(sn, dockType, index, latitude, longitude, height)

**回复 output**：无（result=0，异步进度走 flight_task_progress 事件）
**特性**：异步（启动 startProgressTask），无 output
**Reply**：`FlighttaskExecuteReply()` 空 record

### 6.3 live_start_push（同步，无 output）

**请求 data 字段**（依据 [LiveStreamSimulator.handleStartPush](../../../../../hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/LiveStreamSimulator.java)）：

| 字段名 | 类型 | 必填 | 嵌套 record | 依据 |
|---|---|---|---|---|
| video_id | String | 是 | — | @Verified |
| url | String | 是 | — | @Verified |
| url_type | Integer | 是 | — | @Verified（1=RTMP/4=WebRTC） |
| video_quality | Integer | 是 | — | @Verified |

**回复 output**：无（result=0）
**特性**：同步，无 output
**Reply**：`LiveStartPushReply()` 空 record

### 6.4 drone_open（异步，无参数，特殊无 progress）

**请求 data 字段**：无（drone_open 是无参数指令）

**回复 output**：drone_open 特殊处理。依据 [RemoteDebugSimulator](../../../../../hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/RemoteDebugSimulator.java) L288 `else if (!"drone_open".equals(method))`，drone_open 不走通用 progress 结构。

**待确认**：services_reply 的 output 结构（空或特殊）。simulator 代码未明确 drone_open 的 output 字段，标 `@Inferred` 待真机/文档确认。

**特性**：异步（RemoteDebugSimulator 处理），无参数，特殊无 progress
**Reply**：`DroneOpenReply()` 空 record，类级 `@Inferred reason="drone_open services_reply output 结构 simulator 未明确，待真机/文档确认"`

### 6.5 takeoff_to_point（异步双阶段，多字段）

**请求 data 字段**（依据 [FlightCommandSimulator.handleTakeoffToPoint](../../../../../hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/handler/FlightCommandSimulator.java) L208-L266）：

| 字段名 | 类型 | 必填 | 嵌套 record | 依据 |
|---|---|---|---|---|
| flight_id | String | 是 | — | @Verified |
| max_speed | Integer | 否 | — | @Verified（simulator 默认 10，不在 POJO 兜底） |
| target_latitude | Double | 是 | — | @Verified |
| target_longitude | Double | 是 | — | @Verified |
| target_height | Double | 是 | — | @Verified |
| security_takeoff_height | Double | 否 | — | @Verified（相对起飞点 ALT） |
| rth_altitude | Integer | 否 | — | @Verified |
| rth_mode | Integer | 否 | — | @Verified（0=智能高度，机场不支持，simulator 拒绝） |
| rc_lost_action | Integer | 否 | — | @Verified |
| commander_mode_lost_action | Integer | 否 | — | @Verified |
| commander_flight_mode | Integer | 否 | — | @Verified |
| commander_flight_height | Double | 否 | — | @Verified |
| flight_safety_advance_check | Integer | 否 | — | @Verified |
| simulate_mission | SimulateMission | 否 | SimulateMission(isEnable, latitude, longitude, altitude) | @Verified |

**注**：`SimulateMission` record 统一定义含 4 字段（isEnable/latitude/longitude/altitude），与 flighttask_prepare 共用。takeoff_to_point 在 simulator 仅解析 3 字段（无 altitude），但协议可能含 altitude，统一 record 定义，altitude 缺失时为 null。

**回复 output**：无（result=0，异步进度走 takeoff_to_point_progress 事件）
**特性**：异步双阶段确认，无 output
**Reply**：`TakeoffToPointReply()` 空 record

---

## 7. 测试设计

SDK 无 `src/test/java/`，TDD 用例文档为规划稿（与现有 §4 protocol.method 一致）。本次新增 §10 command 包章节到 [tdd-test-cases.md](../../tdd-test-cases.md)。

### 7.1 测试模式（每指令 5 类用例）

| 用例 | 内容 | 基准 |
|---|---|---|
| `shouldHaveNComponents_whenRecord` | `getRecordComponents()` 组件数与名称 | char \| spec |
| `shouldDeserializeSnakeCase_whenJsonGiven` | snake_case JSON → record 字段匹配（验证 MessageCodec SNAKE_CASE 协作） | doc \| spec |
| `shouldSerializeToSnakeCase_whenRecordGiven` | record → JSON 含 snake_case 字段名 | doc \| spec |
| `shouldBeAnnotatedWithVerifiedOrInferred_whenClassLevel` | 类级 `@Verified`/`@Inferred` 注解存在性 | meta \| spec |
| `shouldRoundTrip_whenSerializeThenDeserialize` | 往返一致性 | char \| spec |

### 7.2 空 Reply 专项测试

```
#### shouldHaveZeroComponents_whenEmptyReply
- 输入：`FlighttaskPrepareReply.class.getRecordComponents()`
- 预期：空数组（本指令无 output）
- 断言：`assertThat(components).isEmpty()`
- 基准：char | spec | green

#### shouldHaveJavadocMarkingNoOutput_whenInspected
- 输入：读 `FlighttaskPrepareReply` 类 Javadoc（反射或读源文件）
- 预期：Javadoc 含「无 output 字段」标注
- 断言：`assertThat(javadoc).contains("无 output")`
- 基准：char | spec | green
```

### 7.3 TDD 章节规划

- §10.1 LiveStartPushRequestTest（4 必填字段模板）
- §10.2 LiveStartPushReplyTest（空 Reply 模板）
- §10.3 FlighttaskPrepareRequestTest（多嵌套字段模板）
- §10.4 FlighttaskPrepareReplyTest（空 Reply）
- §10.5 FlighttaskExecuteRequestTest（multi_dock_task 复杂嵌套模板）
- §10.6 FlighttaskExecuteReplyTest（空 Reply）
- §10.7 DroneOpenRequestTest（无参数模板）
- §10.8 DroneOpenReplyTest（异步有 output 模板，@Inferred 待确认）
- §10.9 TakeoffToPointRequestTest（异步双阶段多字段模板）
- §10.10 TakeoffToPointReplyTest（空 Reply）

---

## 8. 待确认项

1. **drone_open services_reply output 结构**：simulator 代码未明确，标 `@Inferred` 待真机/文档确认
2. **wayline_precision_type 字段含义**：DJI 文档未明确，标 `@Inferred`
3. **SimulateMission 是否含 altitude 字段**：simulator takeoff_to_point 未解析 altitude，但 flighttask_prepare 有；统一 record 含 altitude，缺失为 null，待 DJI 文档确认是否协议级一致
4. **airport_takeoff POJO**：simulator 未实现，本次换为 takeoff_to_point 作为示范；airport_takeoff 作为后续 @Inferred 补充项
5. **drc/event 通道 POJO**：本次仅做 service 通道 5 个示范，drc/event 通道后续批次

---

## 9. 后续扩展

本次 5 个示范 POJO 验证设计模式后，后续批量扩展：
- **services 通道剩余 64 个 method**：按本次模板批量补全
- **drc 通道 19 个 method**：command/drc/ 子包
- **event 通道 4 个 method**：command/event/ 子包
- **airport_takeoff 等 simulator 未实现指令**：标 @Inferred 从 DJI 文档推断或留空待补
