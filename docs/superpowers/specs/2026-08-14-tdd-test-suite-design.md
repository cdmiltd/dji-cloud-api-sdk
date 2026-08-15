# DJI Cloud API SDK — TDD 测试套件设计

- **状态**：已修订（v2）— 反映 OsdField/StateField `fromFieldName` 代码更新
- **日期**：2026-08-14（v1）/ 2026-08-14（v2 修订）
- **作者**：AI 协作生成
- **关联**：
  - `dji-cloud-api-doc-comparison.html`（文档对比分析报告）
  - `docs/architecture-design.md`（项目架构设计文档）
- **修订记录**：
  - v1：初始设计，覆盖 40 类 + 3 package-info，登记问题 #1-#5
  - v2：范围扩展至 285+ 源文件、16 顶层包，新增 command/capture/http/websocket 包，telemetry.enumtype 从 5 扩展到 22 枚举。OsdField/StateField 新增 `fromFieldName` + `BY_FIELD_NAME` 查找表（与 `telemetry.enumtype` 的 `fromCode` 模式对齐），登记为问题 #6（已处理）

---

## 1. 背景与目标

`dji-cloud-api-sdk` 是从 DJI Dock 模拟器抽取的协议定义层，作为模拟器与 hivemind 平台的共享
"单一真相源"。当前项目**零测试代码**（`src/test` 不存在），协议正确性完全依赖人工核对。

本设计的目标：以 TDD 思路为全项目建立测试套件，用测试用例文档先行锁定预期行为，再以测试代码
实现回归网，并显式揭示"代码与官方文档不一致"或"实现存在缺陷"的项。

### 非目标
- 不重构源码（遵循项目硬约束："不确定内容不改代码，记录之"）。发现的缺陷以 `@Disabled` 失败测试
  标红，作为待修复清单，不在本阶段修改源码。
- 不引入 Mockito（当前代码多为静态工具/枚举/record，无 mock 需求）。

---

## 2. 范围

覆盖 `src/main/java/ltd/cdmi/dji/cloudapi/sdk/` 下全部 **285+ 个源文件**（含 package-info），分布于 16 个顶层包：

| 包 | 类数 | 代表类 |
|---|---|---|
| `annotation` | 3 | DocUrl / Verified / Inferred |
| `capture` | 3 | CaptureConfig / CaptureRecorder / package-info |
| `codec` | 3 | MessageCodec / TopicResolver / package-info |
| `command`（含子包） | ~190 | DRC 控制/上行 + events + property + requests + services + status |
| `command/drc` | ~41 | StickControlRequest / DroneControlRequest / OsdInfoPushData / CameraStatePushData 等 |
| `command/event` | ~46 | OtaProgressData / FlighttaskProgressData / CameraPhotoTakeProgressData / ServiceProgressData 等 |
| `command/property` | 4 | PropertySetRequest / PropertySetReply / PropertySetResult |
| `command/request` | ~18 | ConfigRequest / AirportBindStatusRequest / FlighttaskResourceGetRequest 等 |
| `command/service` | ~107 | CameraModeSwitchRequest / FlighttaskPrepareRequest / LiveStartPushRequest 等 |
| `command/status` | 3 | UpdateTopoData / UpdateTopoReplyData / package-info |
| `flow` | 4 | RegistrationStep / OnlineFlow / Dock/PilotRegistrationFlow |
| `http` | 3 | HttpApiPath / HttpResponseEnvelope / StsCredentials |
| `model` | 7 | DeviceModel / DroneModel / DockModel / DeviceCompatibility 等 |
| `protocol`（含子包） | 18 | TopicChannel / ServiceMethod / RequestEnvelope / DjiErrorCode 等 |
| `telemetry` | 5 | OsdField / StateField / DroneOsd / DockOsd / ControllerOsd |
| `telemetry.enumtype` | 23 | Gear / DroneModeCode / CameraMode / DongleType / BatteryStoreMode 等 22 枚举 |
| `websocket` | 8 | WsBizCode / WsPushMessage / DeviceOsdPushData 等 |

> **演进说明**：v1 设计于 2026-08-14，覆盖 40 类 + 3 package-info。后续补全 DJI Cloud API 协议覆盖后扩展至 285+ 源文件，新增 `command`（含 drc/event/property/request/service/status 子包）、`capture`、`http`、`websocket` 4 个顶层包，`telemetry.enumtype` 从 5 扩展到 22 个枚举。

---

## 3. 已对齐的决策

经与用户对齐，确定以下四项决策（作为本设计的既定前提）：

1. **覆盖范围**：全项目覆盖（16 个顶层包，285+ 个编译单元）。
2. **正确性基准**：混合策略 — 协议核心层（codec/protocol.*/telemetry.enumtype/annotation）
   以 DJI 官方文档为规范基准；model/flow/telemetry record 等无逐字段文档依据的按当前实现行为
   特征化锁定。
3. **测试库**：JUnit 5（已存在）+ AssertJ（新增）。
4. **交付顺序**：先交付 TDD 测试用例文档（`docs/tdd-test-cases.md`），用户确认后再写测试代码。

---

## 4. 构建配置变更

### 4.1 新增依赖
`pom.xml` `<dependencies>` 增加 AssertJ（与现有 JUnit 5 同 `test` scope）：

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.25.3</version>
    <scope>test</scope>
</dependency>
```

在 `<properties>` 增加 `<assertj.version>3.25.3</assertj.version>`，引用改为 `${assertj.version}`，
与现有 `junit.version` 风格一致（符合"无硬编码值"偏好）。

### 4.2 surefire 标签分组
`maven-surefire-plugin` 增加配置，支持按 `@Tag` 选择性运行：

```xml
<configuration>
    <groups>spec, characterization</groups>
</configuration>
```

默认运行全部；可用 `-Dgroups=spec` 仅运行规范测试。

---

## 5. 测试架构

### 5.1 目录结构
镜像 main 包结构（v2 扩展后）：
```
src/test/java/ltd/cdmi/dji/cloudapi/sdk/
├── annotation/        DocUrlTest, VerifiedTest, InferredTest
├── capture/           CaptureRecorderTest
├── codec/             MessageCodecTest, TopicResolverTest
├── command/
│   ├── drc/up/        DrcUpPushDataParseTest, DrcUpMethodTest
│   ├── event/         EventSupplementParseTest, AirSenseWarningDataTest
│   ├── property/      PropertySetTest（待实现）
│   ├── request/        RequestParseTest（待实现）
│   ├── service/       ServiceSupplementParseTest, ServiceParamParseTest
│   │   └── camera/    CameraModeSwitchRequestTest
│   └── status/        UpdateTopoParseTest（待实现）
├── http/              HttpApiPathTest（待实现）
├── model/             DeviceDomainTest, DeviceModelTest, DroneModelTest, DockModelTest,
│                      ControllerModelTest, DeviceCompatibilityTest
├── protocol/
│   ├── topic/         TopicChannelTest, TopicDirectionTest, TopicTemplateTest, TopicBuilderTest
│   ├── method/        StatusMethodTest, RequestsMethodTest, EventMethodTest, DrcMethodTest,
│   │                  DrcUpMethodTest, ServiceMethodTest, PropertySetMethodTest
│   ├── envelope/      RequestEnvelopeTest, ReplyEnvelopeTest, EventEnvelopeTest
│   └── error/         DjiErrorCodeTest, ErrorInfoTest
├── telemetry/         OsdFieldTest, StateFieldTest, DroneOsdTest, DockOsdTest, ControllerOsdTest
│   └── enumtype/      GearTest, DroneModeCodeTest, DockModeCodeTest, PositionStateTest,
│                      DroneChargeStateTest, BatteryStoreModeTest, ModeCodeReasonTest,
│                      CameraModeTest, DongleTypeTest, ThermalGainModeTest,
│                      RainfallTest, VideoQualityTest, NetworkTypeTest, NetworkQualityTest,
│                      PositionQualityTest, SourceTypeTest, HomePositionIsValidTest（已实现 12/22）
└── flow/              RegistrationStepTest, OnlineFlowTest, DockRegistrationFlowTest, PilotRegistrationFlowTest
```

> **演进说明**：v1 目录结构覆盖 10 包 25 测试类。v2 扩展后新增 `capture`、`command`（含 6 子包）、`http` 3 个顶层测试目录，`telemetry/enumtype` 从 5 扩展到 22 个测试类（已实现 12 个）。`DeviceModelProvider`（接口）不单独建测试类，其 `toModel()` 契约由 3 个实现枚举的测试类覆盖。

### 5.2 命名约定
- 测试类：`<源类名>Test`
- 测试方法：`should_<期望行为>_when_<前置条件>()`
- 参数化方法：`<行为>Provider()`（`@MethodSource` 引用）

### 5.3 测试数据策略
- JSON fixtures 优先用 Java 21 文本块（`"""..."""`）内联，避免引入资源文件。
- 枚举值映射测试统一用 `@ParameterizedTest + @MethodSource`，逐值断言 code/fieldName/methodName
  与 description，避免遗漏。
- UUID/timestamp 等非确定性字段，断言"存在且类型正确"而非具体值。

---

## 6. 分包测试策略

| 包 | 基准 | 标签 | 关键测试点 |
|---|---|---|---|
| `annotation` | 文档规范 | `spec` | `@Retention(RUNTIME)`、`@Target({TYPE,FIELD,METHOD})`、属性默认值（`basis()`/`verifyPoint()` 默认 `""`、`reason()` 必填） |
| `capture` | 特征化 | `characterization` | `CaptureRecorder` 录制行为、`CaptureConfig` 配置项 |
| `codec` | 文档规范 | `spec` | 信封字段提取、JSON 往返、snake_case↔camelCase 命名策略、`extractResult` 返回 -1 哨兵 |
| `command/drc` | 文档规范 | `spec` | DRC 上行推送 11 个 POJO 反序列化、DRC 下行控制 5 子包（camera/flight/light/safety/speaker）POJO、`DrcUpMethod` 枚举双向映射 |
| `command/event` | 文档规范 | `spec` | events 通道 POJO 反序列化（含 system/flight/wayline/media/alert/speaker/psdk/esdk/flightarea 子包）、进度上报通用结构、`EventMethod` 枚举 |
| `command/property` | 文档规范 | `spec` | `property/set` 通道 `PropertySetRequest`/`PropertySetReply`/`PropertySetResult`、`PropertySetMethod` 枚举 15 个可设置属性 |
| `command/request` | 文档规范 | `spec` | requests 通道 POJO（config/flightarea/registration/wayline 子包）、`RequestsMethod` 枚举 |
| `command/service` | 文档规范 | `spec` | services 通道 POJO（camera/flight/live/wayline/debug/drc/esdk/esim/firmware/flysafe/log/media/pilot/psdk 子包）、`ServiceMethod` 枚举 96 个方法 |
| `command/status` | 文档规范 | `spec` | `update_topo` 通道 `UpdateTopoData`/`UpdateTopoReplyData`、`StatusMethod` 枚举 |
| `http` | 文档规范 | `spec` | `HttpApiPath` 路径常量、`StsCredentials` record、`HttpResponseEnvelope` 信封 |
| `model` | 特征化 | `characterization` | 型号三元组、`modelKey()` 格式、`isDock/Controller/Aircraft`、兼容矩阵全覆盖 |
| `protocol.topic` | 文档规范 | `spec` | 14 通道 suffix/direction、`@Deprecated` 标注、sys/product vs thing/product 前缀规则 |
| `protocol.method` | 文档规范 | `spec` | method 字符串↔枚举双向映射、`fromMethodName` 返回 Optional、null 返回 empty、`@Inferred` 项标注 |
| `protocol.envelope` | 文档规范 | `spec` | record 组件、`ReplyData.result`/`output` 结构、JSON 往返 |
| `protocol.error` | 文档规范 | `spec` | 5 个错误码常量值、`ErrorInfo` record |
| `telemetry`（record + field 枚举） | 混合 | `characterization` + `spec` | record 组件可 null、JSON 往返；**OsdField/StateField 的 `fromFieldName` 行为**（已知值/未知值抛 `IllegalArgumentException`/null），与 `telemetry.enumtype` 的 `fromCode` 模式对齐 |
| `telemetry.enumtype` | 文档规范 | `spec` | **22 枚举** code↔值映射、`fromCode` 抛 `IllegalArgumentException`、未知 code 异常、Jackson 绑定（`@JsonValue`/`@JsonCreator`，含 `CameraMode`/`BatteryStoreMode`/`ModeCodeReason`） |
| `flow` | 混合 | 混合 | step 序列按文档（5 步 method 名）、`buildUpdateTopoPayload` 报文结构锁定、`@Inferred` 标注 |
| `websocket` | 文档规范 | `spec` | `WsBizCode` 枚举、`WsPushMessage` 信封、`DeviceOsdPushData`/`MapElementPushData` 等推送 POJO |

---

## 7. 缺陷标记机制

对"规范应为 X、当前实现为 Y"的项，采用 **失败测试 + `@Disabled`** 显式标红：

```java
@Test
@Tag("spec")
@Disabled("待修复：MessageCodec 未配 PropertyNamingStrategy，snake_case JSON 反序列化到 camelCase record 时字段全部为 null")
void shouldDeserializeSnakeCaseJson_whenToCamelCaseRecord() {
    // 断言按文档规范（字段正确映射），当前实现会失败
}
```

**理由**：
- 符合"以官方文档为准"与"不确定内容不改代码、记录之"的硬约束。
- `@Disabled` 测试在 surefire 报告中可见，构成待修复清单；修复后移除 `@Disabled` 即转绿。
- 与"特征化测试"区分：特征化锁定当前行为（绿），`@Disabled` 标红期望规范未满足（红）。

---

## 8. 待揭示问题清单（基于最新代码）

通读全部源码后，识别以下项。**问题 2/4/5/6 已在最近代码更新中处理**，仅 1、3 仍存在：

### 8.1 仍存在 — 测试将标红

| # | 位置 | 问题 | 严重度 | 处理 |
|---|---|---|---|---|
| 1 | `MessageCodec` | `ObjectMapper` 仅配 `FAIL_ON_UNKNOWN_PROPERTIES=false`，**未配 `PropertyNamingStrategy`**。DJI OSD/state JSON 字段为 snake_case（`mode_code`），而 `DroneOsd`/`DockOsd`/`ControllerOsd` record 字段为 camelCase（`modeCode`）。`fromJson(json, DroneOsd.class)` 时字段名不匹配，**所有字段为 null**。 | **关键 bug** | `@Disabled` 标红 + 用例文档登记 |
| 3 | `DeviceCompatibility.isCompatible(ControllerModel, DroneModel)` | switch 表达式仅覆盖 `RC_PLUS`/`RC_PLUS_2`/`RC_PRO`，**未覆盖 `SMART_CONTROLLER_ENTERPRISE`**。传入该值触发 `IllegalStateException`（switch 表达式要求穷尽）。 | 重要 | `@Disabled` 标红 + 用例文档登记 |

### 8.2 已处理 — 测试锁定为绿/验证标注

| # | 位置 | 原问题 | 当前状态 | 测试处理 |
|---|---|---|---|---|
| 2 | `PositionState` / `DroneChargeState` | 缺 `fromCode` 与 `BY_CODE`，与其余 3 枚举不一致 | **已修复**：两者均补齐 `fromCode`（抛 `IllegalArgumentException`）+ `BY_CODE` 查找表 | 规范测试锁定为绿，5 枚举统一覆盖 |
| 4 | `TopicBuilder.build(sn, channel)` | 默认 thing/product，传 `STATUS` 与 `TopicTemplate.STATUS`(sys/product) 不一致 | **已部分处理**：方法标 `@Deprecated` 并文档说明 STATUS/STATUS_REPLY 需 sys/product | 特征化测试锁定 thing/product 行为 + 验证 `@Deprecated` 注解存在；规范测试覆盖 `build(sn,ch,useSysPrefix)` 与 `buildWithSysPrefix` |
| 5 | 错误处理风格不一致 | `fromCode` 抛异常 / `fromMethodName` 返回 Optional / `extractResult` 返回 -1 | **已文档化**：`codec/package-info.java` 说明 `extractResult` 返回 -1 是有意哨兵设计 | 测试锁定三种风格的实际行为 + 验证 `package-info` 文档存在 |
| 6 | `OsdField` / `StateField` | 缺 `fromFieldName` 与 `BY_FIELD_NAME`，与 `telemetry.enumtype` 的 `fromCode` 模式不一致 | **已修复**（v2 新增）：两者均补齐 `fromFieldName`（抛 `IllegalArgumentException`）+ `BY_FIELD_NAME` 查找表 | 特征化测试锁定为绿，覆盖已知值/未知值/null；与 `fromCode` 统一覆盖 |

### 8.3 历史缺陷（项目记忆记录）— 已修复，测试防回归

| 项 | 历史问题 | 当前状态 |
|---|---|---|
| `Gear` | 值映射错误且缺 7 档 | **已修复**：含 10 档（0-9，A/P/NAV/FPV/FARM/S/F/M/G/T），与 `@Verified` 文档定义一致。测试锁定为绿 |
| `ModeCode` | 合并机场/飞行器定义 | **已修复**：拆分为 `DroneModeCode`（0-20）与 `DockModeCode`（0-5）。测试锁定为绿 |

---

## 9. 交付物

### 9.1 TDD 测试用例文档（已交付）
- **路径**：`docs/tdd-test-cases.md`
- **状态**：两批均已完成（spec 24 类 + characterization 15 类 = 39 测试类）
- **结构**：按包→类→测试场景，每场景列出：
  - 场景名（对应 `should_..._when_...`）
  - 输入
  - 预期
  - 基准（文档依据 URL 或"特征化：当前实现"）
  - 标签（`spec` / `characterization`）
  - 状态（`green` / `@Disabled 待修复：原因`）
- v2 修订：OsdField/StateField 的 `fromFieldName` 用例已纳入 10.1/10.2 节（characterization 标签）

### 9.2 测试代码（用例文档确认后）
- 39 个 `*Test` 类，覆盖全部源类（40 个编译单元减去 `DeviceModelProvider` 接口，其契约由 3 个实现枚举的测试覆盖）。
- `pom.xml` 增 AssertJ 依赖与 surefire 标签配置。
- 验收：`mvn test` 通过（`@Disabled` 项不计失败）。

---

## 10. 验收标准

1. 全部 40 个源类至少有 1 个对应测试类（`DeviceModelProvider` 接口除外，由实现枚举覆盖）。
2. 所有枚举的值映射有参数化测试逐值覆盖。
3. 5 个 enumtype 枚举的 `fromCode` 行为统一覆盖（含未知 code 抛异常）。
4. **OsdField/StateField 的 `fromFieldName` 行为覆盖**（含未知 fieldName 抛异常、null 输入）— v2 新增。
5. 第 8.1 节的 2 个问题以 `@Disabled` 失败测试登记，`@Disabled` reason 清晰。
6. 第 8.2 节已处理项（含 #6）的测试为绿或验证标注存在。
7. `mvn test` 全绿（`@Disabled` 项除外）。
8. 用例文档与测试代码一一对应，无遗漏。

---

## 11. 风险与备注

- **MessageCodec bug（#1）影响范围广**：所有 OSD/state record 反序列化受影响。测试标红后，
  修复需在 `MessageCodec` 配 `PropertyNamingStrategy.SNAKE_CASE`，但需评估对序列化方向（record
  →JSON）的副作用（会输出 snake_case）。修复方案不在本设计范围，仅在用例文档中登记并提示。
- **`@Inferred` 项**（`DrcMethod.HEART_BEAT`、`ServiceMethod.CLOUD_CONTROL_AUTH_REQUEST`、
  `PilotRegistrationFlow.UPDATE_TOPO`）：这些是"推断定义，待真机验证"，测试应验证 `@Inferred`
  注解存在且 `reason`/`verifyPoint` 非空，但不对其协议正确性做规范断言（因无文档基准）。
- **package-info**：3 个新增 package-info 不单独测试，但其文档内容（如 codec 的 -1 哨兵说明）
  作为相关测试的断言依据。
- **`fromFieldName` 一致性**（v2 新增）：OsdField/StateField 的 `fromFieldName` 与 `telemetry.enumtype`
  的 `fromCode` 采用相同模式（`BY_XXX` 查找表 + 未知值抛 `IllegalArgumentException`）。测试中
  OsdField/StateField 标 `characterization`（锁定当前实现），而非 `spec`，因 fieldName→枚举的映射
  虽基于 DJI 文档但属于实现侧的便利方法，非协议规范要求。若后续需提升为 spec，仅需改标签。
