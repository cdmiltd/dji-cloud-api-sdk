# waylines.wpml 生成能力设计

- **日期**：2026-08-15
- **状态**：已确认，待实现
- **关联文档**：[2026-08-15-wayline-template-kml-design.md](./2026-08-15-wayline-template-kml-design.md)（template.kml 生成）
- **DJI 文档**：[waylines-wpml.html](https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/waylines-wpml.html)
- **参考实现**：`hivemind/backend/adapter-drone/.../wpml/KmzGenerator.java`

---

## 1. 目标与非目标

### 目标

在 SDK 中提供 `waylines.wpml`（飞机直接执行文件）的生成能力，作为现有 `template.kml` 生成（`WaypointTemplate.toXml()`）的对称补充：

- `WaypointTemplate.toWpml()`：从 Builder 内存模型转换生成机型匹配的 `waylines.wpml` 字符串
- 机型适配：通过已有的 `droneInfo()`/`payloadInfo()` 参数注入，直接写入 `missionConfig`
- 输出 `String`，**文件保存位置由调用方控制**，SDK 不负责

### 非目标

- **不实现 KML 解析**：waylines.wpml 从 Builder 内存对象生成，不从 template.kml 文本解析（与现有设计一致）
- **不实现占位符派生**：不提供 hivemind 的"模板 + 派生"两阶段模式（调用方如需可在业务层实现）
- **不实现 KMZ 打包**：不提供 template.kml + waylines.wpml 的 zip 打包（调用方自行处理）
- **不支持 mapping2d/3d/strip 模板**：先仅支持 waypoint 模板（与 hivemind 一致、DJI 文档主要描述 waypoint 结构）
- **不实现 `useGlobalXxx=0` 的航点级参数覆盖**：现有 `WaypointBuilder` 无航点级速度/航向/转弯字段，后续扩展
- **不实现 `autoRerouteInfo`**（M3D/M4D 特有）和 `isRisky`（需 Pilot 2 安全计算）：后续扩展

---

## 2. 背景与动机

### template.kml 与 waylines.wpml 的关系

DJI WPML 航线文件包含两种形态：

| 文件 | 用途 | 特点 |
|---|---|---|
| `template.kml` | 航线模板（人类可编辑） | 含 `templateType`、全局参数、`useGlobalXxx` 标志、`height` |
| `waylines.wpml` | 飞机直接执行 | 含 `executeHeightMode`、`index`、`waylineId`、展开的 `waypointSpeed`/`waypointHeadingParam`/`waypointTurnParam` |

`waylines.wpml` 由 DJI Pilot 2 / Flighthub 2 或其他软件生成。SDK 补充此能力，使调用方无需依赖 DJI 客户端即可生成可执行航线。

### hivemind 参考实现

hivemind 的 `KmzGenerator` 采用"占位符模板 + 派生"两阶段模式：`generate()` 生成带 `{{DRONE_ENUM}}` 占位符的 KMZ，`derive()` 按机型替换占位符。

本设计采用更简洁的方案 A（直接参数注入），理由见方案对比。hivemind 如需替换现有 `KmzGenerator`，可在执行时 `toWpml()` 重新生成（KMZ 生成非高频操作，性能可接受）。

---

## 3. 方案对比与决策

### 方案 A（已采纳）：Builder 增强 — `toWpml()` + 机型参数注入

在现有 `WaypointTemplate` 上增加 `toWpml()`，从 Builder 内存模型直接生成 `waylines.wpml`，与 `toXml()` 对称。机型从已有的 `droneInfo()` 读取直接写入。

**理由**：
1. **恰如其分**：最小改动满足核心需求
2. **类型安全**：机型通过 `droneInfo()` 枚举值注入，优于字符串占位符替换
3. **API 一致**：与现有 `toXml()` 对称，学习成本零
4. **场景适配**：占位符派生是调用方优化，非 SDK 必须能力

### 未采纳方案

- **方案 B**（独立 `WpmlGenerator` + 占位符派生）：引入新抽象，与现有 Builder 并行重叠，占位符非类型安全
- **方案 C**（Builder 双模式）：Builder 职责膨胀，复杂度高

---

## 4. 架构设计

### API 形态

```java
public final class WaypointTemplate {
    // 现有
    public String toXml() { ... }     // 生成 template.kml（可编辑）

    // 新增
    public String toWpml() { ... }    // 生成 waylines.wpml（机型匹配）
}
```

调用方工作流（SDK 不管文件保存）：

```java
String wpml = WaypointTemplate.builder()
    .droneInfo(67, 0)          // M30
    .payloadInfo(52, 0)
    .autoFlightSpeed(7)
    .globalWaypointHeadingMode(WaypointHeadingMode.FOLLOW_WAYLINE)
    .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
    .addWaypoint(w -> w.longitude(113.98).latitude(22.98).height(100))
    .toWpml();                  // 返回 String，文件保存由调用方控制
```

### 内部流程

```
WaypointTemplate Builder 状态（内存）
    ├── toXml()  → Kml<Folder> POJO 树        → WpmlCodec → template.kml
    └── toWpml() → Kml<ExecuteFolder> POJO 树  → WpmlCodec → waylines.wpml
                     ↑
              转换 + 全局参数展开 + 校验
```

`toWpml()` 内部三步：
1. **校验**：检查必需字段、机型、useGlobalXxx 标志
2. **转换**：template.kml 的 `Folder`/`Placemark`/`MissionConfig` → waylines.wpml 的 `ExecuteFolder`/`ExecutePlacemark`/`ExecuteMissionConfig`
3. **序列化**：`WpmlCodec.writeKmlAsString(kml)` 输出 String

### 新增组件

```
wayline/model/execute/                    // waylines.wpml 专有 POJO
├── ExecuteDocument.java                  // <Document>
├── ExecuteMissionConfig.java             // <missionConfig>（移除 takeOffRefPoint）
├── ExecuteFolder.java                    // <Folder>
├── ExecutePlacemark.java                 // <Placemark>
├── WaypointHeadingParam.java             // <wpml:waypointHeadingParam>
├── WaypointTurnParam.java                // <wpml:waypointTurnParam>
└── package-info.java

wayline/enumtype/
└── ExecuteHeightMode.java                 // WGS84 | relativeToStartPoint | realTimeFollowSurface
```

**复用现有**：`Kml<T>` 泛型（T=ExecuteDocument）、`Point`、`DroneInfo`、`PayloadInfo`、`ActionGroup`/`Action`/`ActionTrigger`/`ActionActuatorFuncParam`、`WpmlCodec`、`WpmlNamespaces`

---

## 5. 转换规则

### 5.1 Document 级

| template.kml | waylines.wpml | 规则 |
|---|---|---|
| `author` | `author` | 直接复制 |
| `createTime`/`updateTime` | 同 | 直接复制 |
| `missionConfig` | `ExecuteMissionConfig` | 见 5.2 |
| `Folder` | `ExecuteFolder` | 见 5.3 |

### 5.2 missionConfig 转换

`ExecuteMissionConfig` = `MissionConfig` 移除以下两个字段，其余 9 个字段直接复制：

| 移除字段 | 原因 |
|---|---|
| `takeOffRefPoint` | waylines.wpml 不需要起飞点参考坐标（executeHeight 已是执行高度） |
| `takeOffRefPointAGLHeight` | 同上 |

保留字段：`flyToWaylineMode`、`finishAction`、`exitOnRCLost`、`executeRCLostAction`、`takeOffSecurityHeight`、`globalTransitionalSpeed`、`globalRTHHeight`、`droneInfo`、`payloadInfo`

### 5.3 Folder 级

| template Folder 字段 | ExecuteFolder 字段 | 规则 |
|---|---|---|
| `templateType` | (移除) | waylines 不需要模板类型 |
| `templateId` | `templateId` | 直接复制 |
| `heightMode` | `executeHeightMode` | 见 5.5 映射规则 |
| (无) | `waylineId` | 新增，默认 `0` |
| `autoFlightSpeed` | `autoFlightSpeed` | 直接复制 |
| `globalWaypointHeadingParam` | (展开到航点) | useGlobalHeadingParam=1 时展开 |
| `globalWaypointTurnMode` | (展开到航点) | useGlobalTurnParam=1 时展开 |
| `globalShootHeight`/`globalUseStraightLine`/`gimbalPitchMode` | (移除) | waylines 不输出 |

### 5.4 Placemark 级

| template Placemark 字段 | ExecutePlacemark 字段 | 规则 |
|---|---|---|
| `Point`/`coordinates` | 同 | 直接复制 |
| `index` | `index` | 直接复制（无则按序号 0,1,2... 自动生成） |
| `height` | `executeHeight` | 直接复制（相对起飞点高度值不变） |
| `gimbalPitchAngle` | `gimbalPitchAngle` | 直接复制（hivemind 保留；DJI 文档未明确列出，标注待真机验证） |
| `ellipsoidHeight` | (移除) | waylines 不需要 |
| `useGlobalHeight` | (移除) | 展开后不需要 |
| `useGlobalSpeed` | (移除) | 展开后不需要 |
| `useGlobalHeadingParam` | (移除) | 展开后不需要 |
| `useGlobalTurnParam` | (移除) | 展开后不需要 |
| (展开自全局) | `waypointSpeed` | 全局 `autoFlightSpeed` 值 |
| (展开自全局) | `waypointHeadingParam` | 全局 `globalWaypointHeadingParam` 全部字段 |
| (展开自全局) | `waypointTurnParam` | `waypointTurnMode`=全局值 + `waypointTurnDampingDist`=`0` |
| `ActionGroup` | `ActionGroup` | 直接复制（动作组结构不变） |

### 5.5 heightMode → executeHeightMode 映射

| heightMode（template） | executeHeightMode（waylines） |
|---|---|
| `relativeToStartPoint` | `relativeToStartPoint` |
| `WGS84` | `WGS84` |
| `EGM96` | `WGS84` |
| 其他/未设置 | `WGS84` |

---

## 6. 机型适配机制

机型通过已有的 `droneInfo()`/`payloadInfo()` 设置，`toWpml()` 直接写入 `missionConfig`：

```xml
<!-- 生成的 waylines.wpml missionConfig 片段 -->
<wpml:droneInfo>
  <wpml:droneEnumValue>67</wpml:droneEnumValue>   <!-- M30 -->
  <wpml:droneSubEnumValue>0</wpml:droneSubEnumValue>
</wpml:droneInfo>
<wpml:payloadInfo>
  <wpml:payloadEnumValue>52</wpml:payloadEnumValue>
  <wpml:payloadSubEnumValue>0</wpml:payloadSubEnumValue>
</wpml:payloadInfo>
```

### 适配范围

| 适配项 | 实现 | 说明 |
|---|---|---|
| droneInfo/payloadInfo 字段值 | ✅ 直接写入 | 核心适配 |
| takeOffSecurityHeight 参数范围校验 | ✅ 按机型校验 | 遥控器 `[1.2,1500]` vs 机场 `[8,1500]` |
| `autoRerouteInfo`（M3D/M4D 特有） | ❌ 后续扩展 | waylines.wpml 专有字段 |
| `isRisky`（危险点标记） | ❌ 不实现 | 需 Pilot 2 安全计算逻辑 |

**结论**：机型适配 = 字段值注入 + 参数范围校验，不涉及航点结构变化。同一航线为不同机型生成，重建 Builder 设置不同 `droneInfo()` 即可。

---

## 7. POJO 模型定义

均使用 `record`，放 `wayline/model/execute/` 包，标注 `@Verified` 与 `@DocUrl`（与现有 template.kml POJO 一致）。

### ExecuteDocument

```java
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/waylines-wpml.html")
@Verified(basis = "DJI WPML waylines.wpml 文档 Document 元素")
public record ExecuteDocument(
    @JacksonXmlProperty(localName = "author", namespace = WpmlNamespaces.WPML) String author,
    @JacksonXmlProperty(localName = "createTime", namespace = WpmlNamespaces.WPML) Long createTime,
    @JacksonXmlProperty(localName = "updateTime", namespace = WpmlNamespaces.WPML) Long updateTime,
    @JacksonXmlProperty(localName = "missionConfig", namespace = WpmlNamespaces.WPML) ExecuteMissionConfig missionConfig,
    @JacksonXmlProperty(localName = "Folder", namespace = WpmlNamespaces.WPML) ExecuteFolder folder
) {}
```

### ExecuteMissionConfig

`MissionConfig` 移除 `takeOffRefPoint`/`takeOffRefPointAGLHeight`，保留 9 个字段：

```java
public record ExecuteMissionConfig(
    String flyToWaylineMode,
    String finishAction,
    String exitOnRCLost,
    String executeRCLostAction,
    Double takeOffSecurityHeight,
    Double globalTransitionalSpeed,
    Double globalRTHHeight,
    DroneInfo droneInfo,
    PayloadInfo payloadInfo
) {}
```

### ExecuteFolder

```java
public record ExecuteFolder(
    String templateId,
    String executeHeightMode,     // ExecuteHeightMode.code()
    Integer waylineId,             // 默认 0
    Double autoFlightSpeed,
    List<ExecutePlacemark> placemarks
) {}
```

### ExecutePlacemark

```java
public record ExecutePlacemark(
    Point point,
    Integer index,
    Double executeHeight,
    Double waypointSpeed,
    Double gimbalPitchAngle,
    WaypointHeadingParam waypointHeadingParam,
    WaypointTurnParam waypointTurnParam,
    List<ActionGroup> actionGroups
) {}
```

### WaypointHeadingParam

字段集与 `GlobalWaypointHeadingParam` 一致（4 个字段）：

```java
public record WaypointHeadingParam(
    String waypointHeadingMode,
    Double waypointHeadingAngle,
    String waypointPoiPoint,
    String waypointHeadingPathMode
) {}
```

### WaypointTurnParam

```java
public record WaypointTurnParam(
    String waypointTurnMode,         // WaypointTurnMode.code()
    Double waypointTurnDampingDist   // 默认 0
) {}
```

### ExecuteHeightMode 枚举

```java
public enum ExecuteHeightMode {
    WGS84("WGS84"),
    RELATIVE_TO_START_POINT("relativeToStartPoint"),
    REAL_TIME_FOLLOW_SURFACE("realTimeFollowSurface");
    // ... code() 方法
}
```

---

## 8. 校验策略

`toWpml()` 生成前校验，违反时抛出明确异常：

| 校验项 | 违反时行为 |
|---|---|
| `droneInfo` 必须设置具体枚举值 | 抛 `IllegalStateException` |
| `payloadInfo` 必须设置 | 抛 `IllegalStateException` |
| 至少 1 个航点 | 抛 `IllegalStateException` |
| 每个航点有经度/纬度/高度 | 抛 `IllegalStateException` |
| 任一航点 `useGlobalXxx=0` | 抛 `UnsupportedOperationException("waylines.wpml 生成暂不支持 useGlobalXxx=0 的航点级参数覆盖")` |
| `useGlobalSpeed=1` 但 `autoFlightSpeed` 未设置 | 抛 `IllegalStateException` |
| `useGlobalHeadingParam=1` 但 `globalWaypointHeadingMode` 未设置 | 抛 `IllegalStateException` |
| `useGlobalTurnParam=1` 但 `globalWaypointTurnMode` 未设置 | 抛 `IllegalStateException` |
| `takeOffSecurityHeight` 超出机型范围 | 抛 `IllegalArgumentException` |

---

## 9. TDD 测试策略

测试类：`WaypointTemplateToWpmlTest`（`src/test/java/.../wayline/`）

### 测试用例

| 用例 | 验证点 |
|---|---|
| 基本转换 | 完整 WaypointTemplate → toWpml()，断言 ExecuteFolder/ExecutePlacemark 字段齐全 |
| 全局参数展开 | useGlobalSpeed/HeadingParam/TurnParam=1 时，航点字段值来自全局 |
| useGlobalXxx 标志移除 | 生成的 wpml 中无 useGlobalHeight/Speed/HeadingParam/TurnParam 元素 |
| heightMode→executeHeightMode | relativeToStartPoint→保持；WGS84→保持；EGM96→WGS84 |
| missionConfig 转换 | 无 takeOffRefPoint/takeOffRefPointAGLHeight，其余字段保留 |
| 机型适配 | droneInfo/payloadInfo 枚举值正确写入 missionConfig |
| 参数范围校验 | takeOffSecurityHeight 超范围抛异常 |
| 缺失机型 | droneInfo 为 null 抛异常 |
| useGlobalXxx=0 | 抛 UnsupportedOperationException（含明确提示信息） |
| 全局参数缺失 | useGlobalSpeed=1 但 autoFlightSpeed 未设置抛异常 |
| DJI 文档示例对照 | 用 DJI waylines.wpml 文档示例数据构造，验证输出结构匹配 |
| template/wpml 一致性 | 同一 Builder 的 toXml() 与 toWpml() 航点坐标/动作数一致 |

### 测试数据来源

- DJI 文档 [waylines-wpml.html](https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/waylines-wpml.html) 示例
- hivemind 已验证的 `KmzGenerateParam` 数据

---

## 10. 实现顺序（TDD）

1. 先写 `WaypointTemplateToWpmlTest`（全部测试用例，未实现项 `@Disabled` 标注）
2. 定义 `execute/` POJO + `ExecuteHeightMode` 枚举
3. 实现 `WaypointTemplate.toWpml()`（校验 + 转换 + 展开 + 序列化）
4. 逐个启用测试，直到全部通过
5. 回归现有 36 个 template.kml 测试不受影响

---

## 11. 待确认项

### 已核实（基于 DJI 文档 + hivemind 实现）

- waylines.wpml 的 Folder/Placemark 字段集（DJI 文档 waylines-wpml.html）
- heightMode → executeHeightMode 映射规则（hivemind 实现验证）
- 全局参数展开逻辑（DJI 文档 + hivemind 实现）
- missionConfig 移除 takeOffRefPoint（hivemind 实现）

### 待真机验证

- `gimbalPitchAngle` 在 waylines.wpml Placemark 中是否保留（DJI 文档未明确列出，hivemind 保留）
- `waypointTurnDampingDist` 默认值是否为 0（DJI 示例为 0，hivemind 用 0）
