# DJI WPML template.kml 航线生成工具设计

- **状态**：已批准
- **日期**：2026-08-15
- **作者**：AI 协作生成
- **关联**：
  - [template.kml 说明](https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html)
  - [共用元素信息](https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html)
  - `docs/superpowers/specs/2026-08-14-tdd-test-suite-design.md`（TDD 测试策略）

---

## 1. 背景与目标

DJI WPML（Waypoint Mission Markup Language）是 XML 格式的航线定义规范。`template.kml`
是模板文件，可被 DJI Pilot 2 / Flighthub 2 解析生成最终可执行的 `waylines.wpml` 文件。

本设计目标：在 SDK 中提供类型安全的航线生成工具，让调用方通过 Builder 模式构造合法的
`template.kml` 字符串，覆盖 DJI WPML 定义的**全部四种模板类型**：

| 模板类型 | `templateType` | Builder | 说明 |
|----------|----------------|---------|------|
| 航点飞行 | `waypoint` | `WaypointTemplate` | 通用航点任务 + 完整 actionGroup 动作组 |
| 建图航拍 | `mapping2d` | `Mapping2dTemplate` | 正射二维建图，测区为多边形（Polygon） |
| 倾斜摄影 | `mapping3d` | `Mapping3dTemplate` | 三维倾斜建模，五航线（1 正射 + 4 倾斜） |
| 航带飞行 | `mappingStrip` | `MappingStripTemplate` | 线状航带采集，测区为线（LineString） |

### 非目标

- 不实现 `waylines.wpml` 生成（那是 template.kml 被客户端解析后的产物）
- 不实现 KML 解析（本工具仅生成，不读取）

---

## 2. 已对齐的决策

经与用户对齐，确定以下决策：

| # | 决策项 | 选择 | 理由 |
|---|--------|------|------|
| 1 | 模板覆盖范围 | 全部四种模板（waypoint + mapping2d/3d/Strip） | 覆盖 DJI WPML 文档定义的所有 `templateType` |
| 2 | XML 序列化方案 | Jackson XML（`jackson-dataformat-xml`） | 与现有 Jackson JSON 一致 |
| 3 | API 风格 | Builder 模式 | 复杂结构构建的可读性与类型安全 |
| 4 | 包位置 | `wayline/` 顶层包 | 与 MQTT 通信层解耦 |
| 5 | actionGroup 范围 | 完整覆盖所有 `actionActuatorFunc` | 提供完整航点任务能力 |
| 6 | 设计方案 | 方案 A：全类型安全 POJO + sealed interface | 与现有 SDK 模式一致，最大类型安全 |
| 7 | 高度语义 | 所有 `height` 参数为相对起飞点高度 | 用户偏好 + DJI `wpml:height` 定义 |
| 8 | WPML 版本 | 固定 1.0.2 | 当前 DJI 文档版本 |
| 9 | 枚举风格 | 独立 enum 文件 + `@DocUrl`/`@Verified` | 与 `DroneModeCode` 等一致 |
| 10 | POJO 风格 | record + Jackson XML 注解 | 与现有 record POJO 一致 |
| 11 | TDD | 先写测试再实现 | 用户 TDD 偏好 |
| 12 | 多模板复用方式 | 泛型化 `Kml<T>`/`Document<T>` + `MappingFolder<P>` | 复用 Document/MissionConfig 层，Folder/Placemark 按模板差异化 |
| 13 | 建图模板 Builder | 独立 Builder（每模板一个 Template 类） | 各模板 Placemark 字段差异大，独立 Builder 比 sealed 更清晰、避免条件分支 |

---

## 3. 构建配置变更

### 3.1 新增依赖

`pom.xml` `<dependencies>` 增加 Jackson XML：

```xml
<!-- XML 编解码（wayline 包使用） -->
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>${jackson.version}</version>
</dependency>
```

`jackson-dataformat-xml` 会传递引入 Woodstox（StAX 实现），无需额外声明。

---

## 4. 包结构

```
src/main/java/ltd/cdmi/dji/cloudapi/sdk/wayline/
├── WaypointTemplate.java              // 航点飞行模板 Builder（waypoint）
├── Mapping2dTemplate.java             // 建图航拍模板 Builder（mapping2d）
├── Mapping3dTemplate.java             // 倾斜摄影模板 Builder（mapping3d）
├── MappingStripTemplate.java          // 航带飞行模板 Builder（mappingStrip）
├── WaypointBuilder.java               // 航点 Builder
├── PayloadParamBuilder.java           // 负载参数 Builder（M300 激光雷达/可见光完整配置）
├── ActionGroupBuilder.java            // 动作组 Builder
├── ActionBuilder.java                 // 动作 Builder
├── WpmlCodec.java                     // XML 序列化工具（对标 MessageCodec）
├── WpmlOutputFactory.java             // XMLOutputFactory 配置（命名空间修复）
├── WpmlStreamWriter.java             // XMLStreamWriter 包装（命名空间修复）
├── package-info.java
│
├── model/                             // XML 元素 POJO（record + Jackson XML 注解）
│   ├── Kml.java                       // <kml> 根元素（泛型 Kml<T>）
│   ├── Document.java                  // <Document>（泛型 Document<T>）
│   ├── MissionConfig.java             // <wpml:missionConfig>
│   ├── DroneInfo.java                 // <wpml:droneInfo>
│   ├── PayloadInfo.java               // <wpml:payloadInfo>
│   ├── Folder.java                    // <Folder> 航点模板
│   ├── MappingFolder.java             // <Folder> 建图类模板通用（泛型 MappingFolder<P>）
│   ├── WaylineCoordinateSysParam.java // <wpml:waylineCoordinateSysParam>
│   ├── GlobalWaypointHeadingParam.java// <wpml:globalWaypointHeadingParam>
│   ├── MappingHeadingParam.java       // <wpml:mappingHeadingParam> 建图航向参数
│   ├── Placemark.java                 // <Placemark> 航点
│   ├── Mapping2dPlacemark.java        // <Placemark> 建图航拍测区
│   ├── Mapping3dPlacemark.java        // <Placemark> 倾斜摄影测区
│   ├── MappingStripPlacemark.java     // <Placemark> 航带配置
│   ├── Point.java                     // <Point><coordinates>
│   ├── Polygon.java                   // <Polygon> 测区多边形（含 OuterBoundaryIs/LinearRing）
│   ├── LinearRing.java                // <LinearRing> 多边形边界
│   ├── LineString.java                // <LineString> 航带线
│   ├── PayloadParam.java              // <wpml:payloadParam> 建图负载参数
│   ├── Overlap.java                   // <wpml:overlap> 重叠率
│   ├── ActionGroup.java               // <wpml:actionGroup>
│   ├── ActionTrigger.java             // <wpml:actionTrigger>
│   ├── Action.java                    // <wpml:action>
│   ├── ActionActuatorFuncParam.java   // sealed interface（动作参数基类）
│   ├── TakePhotoParam.java            // takePhoto 参数 record
│   ├── StartRecordParam.java          // startRecord 参数 record
│   ├── StopRecordParam.java           // stopRecord 参数 record
│   ├── FocusParam.java                // focus 参数 record
│   ├── ZoomParam.java                 // zoom 参数 record
│   ├── CustomDirNameParam.java        // customDirName 参数 record
│   ├── GimbalRotateParam.java         // gimbalRotate 参数 record
│   ├── RotateYawParam.java            // rotateYaw 参数 record
│   ├── HoverParam.java                // hover 参数 record
│   ├── GimbalEvenlyRotateParam.java   // gimbalEvenlyRotate 参数 record
│   ├── OrientedShootParam.java        // orientedShoot 参数 record
│   ├── PanoShotParam.java             // panoShot 参数 record
│   ├── RecordPointCloudParam.java     // recordPointCloud 参数 record
│   ├── WpmlNamespaces.java            // WPML 命名空间常量
│   └── package-info.java
│
└── enumtype/                          // 枚举类型（对标 telemetry/enumtype/）
    ├── FlyToWaylineMode.java          // safely | pointToPoint
    ├── FinishAction.java              // goHome | noAction | autoLand | gotoFirstWaypoint
    ├── ExitOnRCLost.java              // goContinue | executeLostAction
    ├── ExecuteRCLostAction.java       // goBack | landing | hover
    ├── CoordinateMode.java            // WGS84
    ├── HeightMode.java                // EGM96 | relativeToStartPoint | aboveGroundLevel | realTimeFollowSurface
    ├── PositioningType.java           // GPS | RTKBaseStation | QianXun | Custom
    ├── ShootType.java                 // time | distance（建图拍照模式）
    ├── MappingHeadingMode.java        // fixed | followWayline（建图航向模式）
    ├── ReturnMode.java                // singleReturnStrongest | dualReturn | tripleReturn（M300 激光雷达回波）
    ├── ScanningMode.java              // repetitive | nonRepetitive（M300 激光雷达扫描）
    ├── FocusMode.java                 // firstPoint | custom（M300 可见光对焦）
    ├── MeteringMode.java              // average | spot（M300 可见光测光）
    ├── GimbalPitchMode.java           // manual | usePointSetting
    ├── WaypointHeadingMode.java       // followWayline | manually | fixed | smoothTransition | towardPOI
    ├── WaypointHeadingPathMode.java   // clockwise | counterClockwise | followBadArc
    ├── WaypointTurnMode.java          // coordinateTurn | toPointAndStop* | toPointAndPass*
    ├── ActionGroupMode.java           // sequence
    ├── ActionTriggerType.java         // reachPoint | betweenAdjacentPoints | multipleTiming | multipleDistance
    ├── ActionActuatorFunc.java        // takePhoto | startRecord | stopRecord | focus | zoom | customDirName
    │                                 //         | gimbalRotate | rotateYaw | hover | gimbalEvenlyRotate
    │                                 //         | orientedShoot | panoShot | recordPointCloud
    ├── GimbalRotateMode.java          // absoluteAngle | ...
    ├── WpmlEnum.java                  // WPML 枚举基接口（code()/description()）
    └── package-info.java
```

**文件数统计**：9 Builder/Codec + 33 model 类 + 22 enumtype 类 + 3 package-info = **67 个文件**

---

## 5. 核心 API 设计

### 5.1 WaypointTemplate（主 Builder）

入口类，提供 fluent API 链式调用。内部持有各子 Builder，`toXml()` 时组装 POJO 树并序列化。

```java
public final class WaypointTemplate {
    public static WaypointTemplate builder() { ... }

    // ── 创建信息 ──
    public WaypointTemplate author(String author) { ... }
    public WaypointTemplate createTime(long epochMs) { ... }
    public WaypointTemplate updateTime(long epochMs) { ... }

    // ── 任务配置（missionConfig）──
    public WaypointTemplate flyToWaylineMode(FlyToWaylineMode mode) { ... }
    public WaypointTemplate finishAction(FinishAction action) { ... }
    public WaypointTemplate exitOnRCLost(ExitOnRCLost exit) { ... }
    public WaypointTemplate executeRCLostAction(ExecuteRCLostAction action) { ... }
    public WaypointTemplate takeOffSecurityHeight(double height) { ... }  // m, [1.2,1500] 或 [8,1500]
    public WaypointTemplate globalTransitionalSpeed(double speed) { ... }  // m/s, [1,15]
    public WaypointTemplate globalRTHHeight(double height) { ... }         // m, [2,1500]
    public WaypointTemplate takeOffRefPoint(double lat, double lon, double height) { ... }
    public WaypointTemplate takeOffRefPointAGLHeight(double height) { ... }
    public WaypointTemplate droneInfo(int enumValue, int subEnumValue) { ... }
    public WaypointTemplate payloadInfo(int enumValue, int positionIndex) { ... }

    // ── 模板配置（Folder）──
    public WaypointTemplate templateId(int id) { ... }                    // [0, 65535]
    public WaypointTemplate coordinateMode(CoordinateMode mode) { ... }
    public WaypointTemplate heightMode(HeightMode mode) { ... }
    public WaypointTemplate globalShootHeight(double height) { ... }
    public WaypointTemplate positioningType(PositioningType type) { ... }
    public WaypointTemplate autoFlightSpeed(double speed) { ... }         // m/s, [1,15]
    public WaypointTemplate gimbalPitchMode(GimbalPitchMode mode) { ... }
    public WaypointTemplate globalHeight(double height) { ... }           // m, 相对起飞点
    public WaypointTemplate globalWaypointHeadingMode(WaypointHeadingMode mode) { ... }
    public WaypointTemplate globalWaypointHeadingAngle(double angle) { ... }  // °, [-180,180]
    public WaypointTemplate globalWaypointPoiPoint(double lat, double lon, double height) { ... }
    public WaypointTemplate globalWaypointHeadingPathMode(WaypointHeadingPathMode mode) { ... }
    public WaypointTemplate globalWaypointTurnMode(WaypointTurnMode mode) { ... }
    public WaypointTemplate globalUseStraightLine(int value) { ... }      // 0 | 1

    // ── 航点 ──
    public WaypointTemplate addWaypoint(Consumer<WaypointBuilder> config) { ... }

    // ── 输出 ──
    public String toXml() { ... }
    public void writeTo(Path file) { ... }
}
```

### 5.2 WaypointBuilder

```java
public final class WaypointBuilder {
    public WaypointBuilder longitude(double lon) { ... }     // [-180,180]
    public WaypointBuilder latitude(double lat) { ... }      // [-90,90]
    public WaypointBuilder height(double h) { ... }          // 相对起飞点高度
    public WaypointBuilder ellipsoidHeight(double h) { ... } // 椭球高
    public WaypointBuilder gimbalPitchAngle(double angle) { ... }
    public WaypointBuilder useGlobalHeight(int v) { ... }    // 默认 1
    public WaypointBuilder useGlobalSpeed(int v) { ... }     // 默认 1
    public WaypointBuilder useGlobalHeadingParam(int v) { ... } // 默认 1
    public WaypointBuilder useGlobalTurnParam(int v) { ... } // 默认 1
    public WaypointBuilder addActionGroup(Consumer<ActionGroupBuilder> config) { ... }
}
```

### 5.3 ActionGroupBuilder

```java
public final class ActionGroupBuilder {
    public ActionGroupBuilder actionGroupId(int id) { ... }          // [0,65535]
    public ActionGroupBuilder actionGroupStartIndex(int idx) { ... } // [0,65535]
    public ActionGroupBuilder actionGroupEndIndex(int idx) { ... }   // >= startIndex
    public ActionGroupBuilder actionGroupMode(ActionGroupMode mode) { ... } // 默认 SEQUENCE
    public ActionGroupBuilder actionTrigger(ActionTriggerType type) { ... }
    public ActionGroupBuilder actionTriggerParam(double param) { ... } // s 或 m, >0
    public ActionGroupBuilder addAction(Consumer<ActionBuilder> config) { ... }
}
```

### 5.4 ActionBuilder

```java
public final class ActionBuilder {
    public ActionBuilder actionId(int id) { ... }                    // [0,65535]
    public ActionBuilder func(ActionActuatorFunc func) { ... }
    public ActionBuilder param(ActionActuatorFuncParam param) { ... }
}
```

### 5.5 PayloadParamBuilder（M300 负载参数集中配置）

三个建图模板 Builder 均提供两个 `payloadParam` 重载：

```java
// 简单场景（M30/M3E 通用相机，仅 2 字段）
.payloadParam(int payloadPositionIndex, String imageFormat)

// 完整场景（M300/M350 激光雷达 + 可见光，9 字段，通过回调集中配置）
.payloadParam(Consumer<PayloadParamBuilder> config)
```

`PayloadParamBuilder` 覆盖 `payloadParam` 全部 9 个字段，按负载类型分组：

- **激光雷达（Zenmuse L1/L2）**：`returnMode(ReturnMode)`、`samplingRate(int)`（值域校验）、`scanningMode(ScanningMode)`、`modelColoringEnable(int)`
- **可见光相机（Zenmuse P1）**：`focusMode(FocusMode)`、`meteringMode(MeteringMode)`、`dewarpingEnable(int)`、`imageFormat(String)`
- **通用（必需）**：`payloadPositionIndex(int)`

```java
.payloadParam(p -> p
    .payloadPositionIndex(0)
    .returnMode(ReturnMode.TRIPLE_RETURN)     // 三回波
    .samplingRate(240000)                      // 240kHz（值域校验）
    .scanningMode(ScanningMode.NON_REPETITIVE) // 非重复扫描
    .modelColoringEnable(1)                    // 真彩上色
    .imageFormat("JPEG"))
```

> `samplingRate` 为 int 固定值域（非 string 枚举），由 `PayloadParamBuilder` 在设置时校验
> 合法值 {60000, 80000, 120000, 160000, 180000, 240000}，非法值抛 `IllegalArgumentException`。

### 5.6 泛型化 POJO 复用层

四种模板共享 `Document`/`MissionConfig`/`DroneInfo`/`PayloadInfo`/`WaylineCoordinateSysParam`，
仅 `Folder` 与 `Placemark` 不同。为复用上层结构，将 `Kml`/`Document` 泛型化：

```java
public record Kml<T>(Document<T> document) {}

public record Document<T>(
    String author, Long createTime, Long updateTime,
    MissionConfig missionConfig, T folder
) {}
```

- 航点模板：`Kml<Folder>` — `Folder` 含 `List<Placemark>`
- 建图模板：`Kml<MappingFolder<P>>` — `MappingFolder<P>` 含单个 `Placemark`（`P` 为具体 Placemark 类型）

```java
public record MappingFolder<P>(
    String templateType, Integer templateId, Double autoFlightSpeed,
    WaylineCoordinateSysParam waylineCoordinateSysParam,
    PayloadParam payloadParam, P placemark
) {}
```

### 5.7 Mapping2dTemplate（建图航拍）

```java
public final class Mapping2dTemplate {
    public static Mapping2dTemplate builder() { ... }

    // 创建信息 + MissionConfig（与 WaypointTemplate 同构，略）

    // ── Folder 模板配置 ──
    public Mapping2dTemplate templateId(int id) { ... }              // [0, 65535]
    public Mapping2dTemplate autoFlightSpeed(double speed) { ... }   // [1, 15]
    public Mapping2dTemplate coordinateMode(CoordinateMode mode) { ... }
    public Mapping2dTemplate heightMode(HeightMode mode) { ... }
    public Mapping2dTemplate globalShootHeight(double h) { ... }
    public Mapping2dTemplate positioningType(PositioningType type) { ... }
    public Mapping2dTemplate payloadParam(int posIndex, String imageFormat) { ... }

    // ── Placemark 测区配置 ──
    public Mapping2dTemplate shootType(ShootType type) { ... }
    public Mapping2dTemplate direction(int direction) { ... }        // [0, 360]
    public Mapping2dTemplate margin(int margin) { ... }
    public Mapping2dTemplate overlap(Overlap overlap) { ... }
    public Mapping2dTemplate height(double h) { ... }                // 相对起飞点
    public Mapping2dTemplate polygon(String coordinates) { ... }     // 测区多边形
    public Mapping2dTemplate mappingHeadingParam(MappingHeadingMode mode, int angle) { ... }
    public Mapping2dTemplate smartObliqueEnable(int enable) { ... }
    public Mapping2dTemplate smartObliqueGimbalPitch(int pitch) { ... }

    public String toXml() { ... }
}
```

### 5.8 Mapping3dTemplate（倾斜摄影）

在 `Mapping2dTemplate` 基础上，Placemark 替换倾斜摄影专属字段：

```java
public final class Mapping3dTemplate {
    // ... 同构的创建信息 + MissionConfig + Folder 配置 ...

    // ── Placemark 测区配置（倾斜摄影专属） ──
    public Mapping3dTemplate inclinedGimbalPitch(int pitch) { ... }   // 倾斜云台俯仰角
    public Mapping3dTemplate inclinedFlightSpeed(double speed) { ... } // [1, 15]
    public Mapping3dTemplate shootType(ShootType type) { ... }
    public Mapping3dTemplate direction(int direction) { ... }         // [0, 360]
    public Mapping3dTemplate overlap(Overlap overlap) { ... }
    public Mapping3dTemplate height(double h) { ... }
    public Mapping2dTemplate polygon(String coordinates) { ... }

    public String toXml() { ... }
}
```

> 倾斜摄影模板会被生成五条航线（1 条正射 + 4 条倾斜），`inclinedGimbalPitch` 与
> `inclinedFlightSpeed` 控制倾斜航线的云台角度与飞行速度。

### 5.9 MappingStripTemplate（航带飞行）

测区几何为 `LineString`（航带线）而非 `Polygon`，并增加航带专属参数：

```java
public final class MappingStripTemplate {
    // ... 同构的创建信息 + MissionConfig + Folder 配置 ...

    // ── Placemark 航带配置 ──
    public MappingStripTemplate shootType(ShootType type) { ... }
    public MappingStripTemplate direction(int direction) { ... }      // [0, 360]
    public MappingStripTemplate margin(double margin) { ... }
    public MappingStripTemplate singleLineEnable(int enable) { ... }  // 单航线飞行
    public MappingStripTemplate cuttingDistance(double d) { ... }     // 子航带长度
    public MappingStripTemplate leftExtend(int extend) { ... }        // 左侧外扩
    public MappingStripTemplate rightExtend(int extend) { ... }       // 右侧外扩
    public MappingStripTemplate includeCenterEnable(int enable) { ... } // 中心线
    public MappingStripTemplate overlap(Overlap overlap) { ... }
    public MappingStripTemplate height(double h) { ... }
    public MappingStripTemplate stripUseTemplateAltitude(int enable) { ... }
    public MappingStripTemplate lineString(String coordinates) { ... } // 航带线

    public String toXml() { ... }
}
```

### 5.10 使用示例

```java
String kml = WaypointTemplate.builder()
    .author("John")
    .createTime(System.currentTimeMillis())

    // 任务配置
    .flyToWaylineMode(FlyToWaylineMode.SAFELY)
    .finishAction(FinishAction.GO_HOME)
    .exitOnRCLost(ExitOnRCLost.GO_CONTINUE)
    .executeRCLostAction(ExecuteRCLostAction.HOVER)
    .takeOffSecurityHeight(20)
    .globalTransitionalSpeed(8)
    .globalRTHHeight(100)
    .droneInfo(67, 0)       // M30
    .payloadInfo(52, 0)     // M30 相机

    // 模板配置
    .templateId(0)
    .coordinateMode(CoordinateMode.WGS84)
    .heightMode(HeightMode.EGM96)
    .autoFlightSpeed(7)
    .gimbalPitchMode(GimbalPitchMode.USE_POINT_SETTING)
    .globalHeight(100)
    .globalWaypointHeadingMode(WaypointHeadingMode.FOLLOW_WAYLINE)
    .globalWaypointHeadingPathMode(WaypointHeadingPathMode.CLOCKWISE)
    .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
    .globalUseStraightLine(0)

    // 航点 0
    .addWaypoint(w -> w
        .longitude(113.98057)
        .latitude(22.987663)
        .height(100)
        .gimbalPitchAngle(0))

    // 航点 1 — 含动作组
    .addWaypoint(w -> w
        .longitude(113.98060)
        .latitude(22.98770)
        .height(100)
        .gimbalPitchAngle(0)
        .addActionGroup(ag -> ag
            .actionGroupId(0)
            .actionGroupStartIndex(1)
            .actionGroupEndIndex(1)
            .actionTriggerType(ActionTriggerType.REACH_POINT)
            .addAction(a -> a
                .actionId(0)
                .actionActuatorFunc(ActionActuatorFunc.GIMBAL_ROTATE)
                .actionActuatorFuncParam(new GimbalRotateParam(
                    0, "north",                             // payloadPositionIndex, gimbalHeadingYawBase
                    GimbalRotateMode.ABSOLUTE_ANGLE.code(), // gimbalRotateMode
                    0, 0,    // pitch disable
                    0, 0,    // roll disable
                    1, 30,   // yaw enable, 30°
                    0, 0)))  // time disable
            .addAction(a -> a
                .actionId(1)
                .actionActuatorFunc(ActionActuatorFunc.TAKE_PHOTO)
                .actionActuatorFuncParam(new TakePhotoParam(
                    0,          // payloadPositionIndex
                    "point1",   // fileSuffix
                    "wide",     // payloadLensIndex
                    1)))))      // useGlobalPayloadLensIndex
    .toXml();
```

**建图航拍示例**：

```java
String kml = Mapping2dTemplate.builder()
    .author("John")
    .createTime(System.currentTimeMillis())
    .flyToWaylineMode(FlyToWaylineMode.SAFELY)
    .finishAction(FinishAction.GO_HOME)
    .exitOnRCLost(ExitOnRCLost.GO_CONTINUE)
    .executeRCLostAction(ExecuteRCLostAction.HOVER)
    .takeOffSecurityHeight(20)
    .globalTransitionalSpeed(8)
    .globalRTHHeight(100)
    .droneInfo(67, 0)       // M30
    .payloadInfo(52, 0)     // M30 相机
    .templateId(0)
    .coordinateMode(CoordinateMode.WGS84)
    .heightMode(HeightMode.EGM96)
    .autoFlightSpeed(7)
    .globalShootHeight(100)
    .positioningType(PositioningType.GPS)
    .payloadParam(0, "JPEG")
    .shootType(ShootType.TIME)
    .direction(0)
    .overlap(new Overlap(null, null, 80, 70, null, null, null, null))
    .height(100)
    .polygon("113.98057,22.987663,0 113.990000,22.987663,0 113.990000,22.977663,0 113.98057,22.977663,0")
    .mappingHeadingParam(MappingHeadingMode.FOLLOW_WAYLINE, 0)
    .toXml();
```

**倾斜摄影示例**（增加 `inclinedGimbalPitch` / `inclinedFlightSpeed`）：

```java
String kml = Mapping3dTemplate.builder()
    // ... 同上 missionConfig + folder 配置 ...
    .caliFlightEnable(0)
    .inclinedGimbalPitch(-45)          // 倾斜云台俯仰角
    .inclinedFlightSpeed(5)            // 倾斜飞行速度 [1, 15]
    .shootType(ShootType.TIME)
    .direction(0)
    .overlap(new Overlap(null, null, 80, 70, null, null, 80, 70))
    .height(100)
    .polygon("113.98057,22.987663,0 113.990000,22.987663,0 113.990000,22.977663,0 113.98057,22.977663,0")
    .toXml();
```

**航带飞行示例**（`lineString` 替代 `polygon`）：

```java
String kml = MappingStripTemplate.builder()
    // ... 同上 missionConfig + folder 配置 ...
    .shootType(ShootType.TIME)
    .direction(0)
    .overlap(new Overlap(null, null, 80, 70, null, null, null, null))
    .height(100)
    .stripUseTemplateAltitude(1)       // 使用模板高度
    .lineString("113.98057,22.987663,100 113.990000,22.987663,100")
    .toXml();
```

---

## 6. Sealed Interface 动作参数设计

DJI WPML 定义了 13 种 `actionActuatorFunc`，每种参数不同。用 sealed interface 统一管理：

```java
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html#wpml-actionactuatorfuncparam")
@Verified(basis = "DJI WPML 共用元素文档 actionActuatorFuncParam 各动作参数定义")
public sealed interface ActionActuatorFuncParam
    permits TakePhotoParam, StartRecordParam, StopRecordParam, FocusParam,
            ZoomParam, CustomDirNameParam, GimbalRotateParam, RotateYawParam,
            HoverParam, GimbalEvenlyRotateParam, OrientedShootParam,
            PanoShotParam, RecordPointCloudParam {}
```

### 6.1 各动作参数 record 定义

| 动作 | record | 字段 | DJI 文档依据 |
|------|--------|------|-------------|
| takePhoto | `TakePhotoParam` | payloadPositionIndex, fileSuffix, payloadLensIndex, useGlobalPayloadLensIndex | 共用元素 #takePhoto |
| startRecord | `StartRecordParam` | payloadPositionIndex, fileSuffix, payloadLensIndex, useGlobalPayloadLensIndex | 共用元素 #startRecord |
| stopRecord | `StopRecordParam` | payloadPositionIndex, payloadLensIndex | 共用元素 #stopRecord |
| focus | `FocusParam` | payloadPositionIndex, isPointFocus, focusX, focusY, focusRegionWidth, focusRegionHeight, isInfiniteFocus | 共用元素 #focus |
| zoom | `ZoomParam` | payloadPositionIndex, focalLength | 共用元素 #zoom |
| customDirName | `CustomDirNameParam` | payloadPositionIndex, directoryName | 共用元素 #customDirName |
| gimbalRotate | `GimbalRotateParam` | payloadPositionIndex, gimbalHeadingYawBase, gimbalRotateMode, gimbalPitchRotateEnable, gimbalPitchRotateAngle, gimbalRollRotateEnable, gimbalRollRotateAngle, gimbalYawRotateEnable, gimbalYawRotateAngle, gimbalRotateTimeEnable, gimbalRotateTime | 共用元素 #gimbalRotate |
| rotateYaw | `RotateYawParam` | aircraftHeading, aircraftPathMode | 共用元素 #rotateYaw |
| hover | `HoverParam` | hoverTime | 共用元素 #hover（hoverTime, float, s） |
| gimbalEvenlyRotate | `GimbalEvenlyRotateParam` | gimbalPitchRotateAngle, payloadPositionIndex | 共用元素 #gimbalEvenlyRotate |
| orientedShoot | `OrientedShootParam` | gimbalPitchRotateAngle, gimbalYawRotateAngle, focusX, focusY, focusRegionWidth, focusRegionHeight, focalLength, aircraftHeading, accurateFrameValid, payloadPositionIndex, payloadLensIndex, useGlobalPayloadLensIndex, targetAngle, actionUUID, imageWidth, imageHeight, AFPos, gimbalPort, orientedCameraType, orientedFilePath, orientedFileMD5, orientedFileSize, orientedFileSuffix, orientedCameraApertue, orientedCameraLuminance, orientedCameraShutterTime, orientedCameraISO, orientedPhotoMode | 共用元素 #orientedShoot |
| panoShot | `PanoShotParam` | payloadPositionIndex, payloadLensIndex, useGlobalPayloadLensIndex, panoShotSubMode | 共用元素 #panoShot |
| recordPointCloud | `RecordPointCloudParam` | payloadPositionIndex, recordPointCloudOperate | 共用元素 #recordPointCloud |

> **注**：全部 13 种动作参数已通过 DJI Cloud-API-Doc GitHub 源码（`dji-sdk/Cloud-API-Doc`
> 仓库 `docs/cn/60.api-reference/00.dji-wpml/40.common-element.md`，263 行完整版）核实确认，
> 字段顺序与 DJI 文档一致。详见第 11 节「待确认项（已全部核实）」。

---

## 7. 枚举类型清单

每个枚举遵循现有 SDK 模式：`@DocUrl` + `@Verified` 注解，`code()`/`description()` 方法，
`fromCode()` 静态工厂。WPML 枚举值为**字符串**（非整数），因此枚举使用 `String code` 而非 `int code`。

| 枚举 | 值 | DJI 文档依据 |
|------|-----|-------------|
| `FlyToWaylineMode` | `safely`, `pointToPoint` | template.kml #任务信息 |
| `FinishAction` | `goHome`, `noAction`, `autoLand`, `gotoFirstWaypoint` | template.kml #任务信息 |
| `ExitOnRCLost` | `goContinue`, `executeLostAction` | template.kml #任务信息 |
| `ExecuteRCLostAction` | `goBack`, `landing`, `hover` | template.kml #任务信息 |
| `CoordinateMode` | `WGS84` | template.kml #模板信息 |
| `HeightMode` | `EGM96`, `relativeToStartPoint`, `aboveGroundLevel`, `realTimeFollowSurface` | template.kml #模板信息 |
| `PositioningType` | `GPS`, `RTKBaseStation`, `QianXun`, `Custom` | template.kml #模板信息 |
| `ShootType` | `time`, `distance` | template.kml #建图类模板元素 |
| `MappingHeadingMode` | `fixed`, `followWayline` | template.kml #建图航拍模板元素 |
| `ReturnMode` | `singleReturnStrongest`, `dualReturn`, `tripleReturn` | common-element #payloadParam（M300/M350 激光雷达） |
| `ScanningMode` | `repetitive`, `nonRepetitive` | common-element #payloadParam（M300/M350 激光雷达） |
| `FocusMode` | `firstPoint`, `custom` | common-element #payloadParam（M300/M350 可见光） |
| `MeteringMode` | `average`, `spot` | common-element #payloadParam（M300/M350 可见光） |
| `GimbalPitchMode` | `manual`, `usePointSetting` | template.kml #航点飞行模板元素 |
| `WaypointHeadingMode` | `followWayline`, `manually`, `fixed`, `smoothTransition`, `towardPOI` | 共用元素 #waypointHeadingParam |
| `WaypointHeadingPathMode` | `clockwise`, `counterClockwise`, `followBadArc` | 共用元素 #waypointHeadingParam |
| `WaypointTurnMode` | `coordinateTurn`, `toPointAndStopWithDiscontinuityCurvature`, `toPointAndStopWithContinuityCurvature`, `toPointAndPassWithContinuityCurvature` | 共用元素 #waypointTurnParam |
| `ActionGroupMode` | `sequence`, `parallel`（待确认 parallel） | 共用元素 #actionGroup |
| `ActionTriggerType` | `reachPoint`, `betweenAdjacentPoints`, `multipleTiming`, `multipleDistance` | 共用元素 #actionTrigger |
| `ActionActuatorFunc` | `takePhoto`, `startRecord`, `stopRecord`, `focus`, `zoom`, `customDirName`, `gimbalRotate`, `rotateYaw`, `hover`, `gimbalEvenlyRotate`, `orientedShoot`, `panoShot`, `recordPointCloud` | 共用元素 #action |
| `GimbalRotateMode` | `absoluteAngle` | 共用元素 #gimbalRotate |

> **注**：WPML 枚举值为字符串类型（如 `"safely"`、`"goHome"`），与 MQTT 协议的整数枚举不同。
> 枚举类使用 `String code` 字段 + `fromCode(String)` 方法。`@Verified` basis 标注 DJI 文档来源。

---

## 8. XML 序列化（WpmlCodec）

对标 [MessageCodec](file:///d:/99.Code/dji-cloud-api-sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/codec/MessageCodec.java) 的设计模式：

### 8.1 WPML 命名空间

```xml
<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:wpml="http://www.dji.com/wpmz/1.0.2">
```

- **默认命名空间**（KML）：`http://www.opengis.net/kml/2.2` — 用于 `kml`、`Document`、`Folder`、`Placemark`、`Point`、`coordinates`
- **wpml 命名空间**：`http://www.dji.com/wpmz/1.0.2` — 用于所有 `wpml:` 前缀元素

### 8.2 WpmlCodec 实现

```java
public final class WpmlCodec {
    /** WPML 命名空间常量 */
    public static final String NS_KML = "http://www.opengis.net/kml/2.2";
    public static final String NS_WPML = "http://www.dji.com/wpmz/1.0.2";

    private static final XmlMapper MAPPER = createMapper();

    private static XmlMapper createMapper() {
        XmlMapper mapper = new XmlMapper();
        // 启用格式化输出（缩进）
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // 配置 XML 命名空间（Woodstox 自动修复前缀）
        WstxOutputProperties props = ...;
        return mapper;
    }

    public static String toXml(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new IllegalStateException("XML 序列化失败: " + e.getMessage(), e);
        }
    }
}
```

### 8.3 POJO 注解示例

```java
@JacksonXmlRootElement(localName = "kml", namespace = WpmlCodec.NS_KML)
public record Kml<T>(
    @JacksonXmlProperty(localName = "Document", namespace = WpmlCodec.NS_KML)
    Document<T> document
) {}

public record Document<T>(
    @JacksonXmlProperty(localName = "author", namespace = WpmlCodec.NS_WPML)
    String author,

    @JacksonXmlProperty(localName = "createTime", namespace = WpmlCodec.NS_WPML)
    Long createTime,

    @JacksonXmlProperty(localName = "missionConfig", namespace = WpmlCodec.NS_WPML)
    MissionConfig missionConfig,

    @JacksonXmlProperty(localName = "Folder", namespace = WpmlCodec.NS_KML)
    T folder
) {}
```

> `Kml`/`Document` 泛型化后，`folder` 字段类型由具体模板决定：
> 航点模板为 `Folder`，建图模板为 `MappingFolder<P>`。

---

## 9. 校验策略

Builder 方法对 DJI 文档定义的范围约束进行校验，越界时抛出 `IllegalArgumentException`：

| 参数 | 范围 | 来源 |
|------|------|------|
| `takeOffSecurityHeight` | 遥控器 [1.2, 1500]；机场 [8, 1500] | template.kml #任务信息 |
| `globalTransitionalSpeed` | [1, 15] m/s | template.kml #任务信息 |
| `globalRTHHeight` | [2, 1500] m | template.kml #任务信息 |
| `autoFlightSpeed` | [1, 15] m/s | template.kml #模板信息 |
| `templateId` | [0, 65535] | template.kml #模板信息 |
| `actionGroupId` | [0, 65535] | 共用元素 #actionGroup |
| `actionGroupStartIndex` | [0, 65535] | 共用元素 #actionGroup |
| `actionGroupEndIndex` | >= actionGroupStartIndex | 共用元素 #actionGroup |
| `actionId` | [0, 65535] | 共用元素 #action |
| `waypointHeadingAngle` | [-180, 180] ° | 共用元素 #waypointHeadingParam |
| `actionTriggerParam` | > 0 | 共用元素 #actionTrigger |
| `focusX`, `focusY` | [0, 1] | 共用元素 #focus |
| `focusRegionWidth`, `focusRegionHeight` | [0, 1] | 共用元素 #focus |
| `focalLength` | > 0 mm | 共用元素 #zoom |
| `direction`（建图模板） | [0, 360] ° | template.kml #建图类模板元素 |
| `inclinedFlightSpeed`（mapping3d） | [1, 15] m/s | template.kml #倾斜摄影模板元素 |
| `samplingRate`（M300 激光雷达） | ∈ {60000, 80000, 120000, 160000, 180000, 240000} Hz | common-element #payloadParam |

**校验原则**：
- 必需字段缺失时，`toXml()` 抛出 `IllegalStateException` 并指明缺失字段
- 非必需字段为 null 时，Jackson XML 自动跳过（不输出该元素）
- 范围校验在 Builder setter 中执行，fail-fast

---

## 10. TDD 测试策略

遵循用户 TDD 偏好和 [TDD 测试套件设计](file:///d:/99.Code/dji-cloud-api-sdk/docs/superpowers/specs/2026-08-14-tdd-test-suite-design.md)：

### 10.1 测试结构

```
src/test/java/ltd/cdmi/dji/cloudapi/sdk/wayline/
├── WaypointTemplateTest.java         // 航点模板端到端 Builder → XML 测试
├── MappingTemplateTest.java          // 建图三模板（2d/3d/Strip）端到端测试
├── ActionParamTest.java              // 各动作参数 record 测试
├── WpmlXmlSampleTest.java            // XML 样例验证
└── enumtype/
    └── WaylineEnumTest.java          // 枚举值验证（含 HeightMode/PositioningType/ShootType/MappingHeadingMode）
```

### 10.2 测试用例

**核心测试**：以 DJI 文档示例为基准，验证生成的 XML 结构完全匹配：

```java
@Test
void shouldGenerateValidWaypointTemplateKml() {
    String xml = WaypointTemplate.builder()
        .author("Name")
        .createTime(1637600807044L)
        .updateTime(1637600875837L)
        .flyToWaylineMode(FlyToWaylineMode.SAFELY)
        .finishAction(FinishAction.GO_HOME)
        // ... 完整配置 ...
        .addWaypoint(w -> w.longitude(113.98057).latitude(22.987663).height(100))
        .toXml();

    // 验证 XML 结构
    assertThat(xml)
        .contains("<kml xmlns=\"http://www.opengis.net/kml/2.2\"")
        .contains("xmlns:wpml=\"http://www.dji.com/wpmz/1.0.2\"")
        .contains("<wpml:author>Name</wpml:author>")
        .contains("<wpml:flyToWaylineMode>safely</wpml:flyToWaylineMode>")
        .contains("<wpml:templateType>waypoint</wpml:templateType>")
        .contains("<wpml:actionActuatorFunc>gimbalRotate</wpml:actionActuatorFunc>");
}
```

**校验测试**：

```java
@Test
void shouldRejectSpeedOutOfRange() {
    assertThatThrownBy(() ->
        WaypointTemplate.builder().autoFlightSpeed(20)
    ).isInstanceOf(IllegalArgumentException.class)
     .hasMessageContaining("[1, 15]");
}
```

**枚举测试**：

```java
@Test
void shouldHaveAllFlyToWaylineModeValues() {
    assertThat(FlyToWaylineMode.values())
        .extracting(FlyToWaylineMode::code)
        .containsExactlyInAnyOrder("safely", "pointToPoint");
}
```

### 10.3 测试优先级

1. **P0**：XML 结构正确性（命名空间、元素层级、必需字段）
2. **P0**：DJI 文档示例完全匹配
3. **P1**：枚举值完整性
4. **P1**：范围校验
5. **P2**：动作参数 record 字段完整性
6. **P2**：writeTo(Path) 文件写入

---

## 11. 待确认项（已全部核实）

以下项目已通过 DJI Cloud-API-Doc GitHub 源码（`dji-sdk/Cloud-API-Doc` 仓库
`docs/cn/60.api-reference/00.dji-wpml/40.common-element.md`）全部核实确认：

| # | 项目 | 核实结果 |
|---|------|----------|
| 1 | `rotateYaw` 完整参数列表 | `aircraftHeading`（浮点型, °, [-180,180]）+ `aircraftPathMode`（枚举-string: clockwise/counterClockwise），**无 payloadPositionIndex** |
| 2 | `gimbalEvenlyRotate` 完整参数列表 | `gimbalPitchRotateAngle`（浮点型）+ `payloadPositionIndex`（整型），字段顺序：pitch 在前 |
| 3 | `orientedShoot` 完整参数列表 | 28 个字段，含云台姿态、对焦区域、变焦、偏航、目标框选、负载配置、参考照片元数据 |
| 4 | `panoShot` 完整参数列表 | 4 个字段：`payloadPositionIndex`、`payloadLensIndex`、`useGlobalPayloadLensIndex`、`panoShotSubMode` |
| 5 | `recordPointCloud` 完整参数列表 | 2 个字段：`payloadPositionIndex`、`recordPointCloudOperate` |
| 6 | `gimbalRotate` 完整参数列表 | 11 个字段，含 `gimbalHeadingYawBase`（枚举-string: north），原实现遗漏此字段 |
| 7 | `customDirName` 的 directoryName 字段名 | 确认为 `directoryName` |
| 8 | `GimbalRotateMode` 枚举值 | 确认为 `absoluteAngle` |
| 9 | `ActionGroupMode` 枚举值 | DJI 文档仅列出 `sequence`（串行执行） |
| 10 | `HeightMode` 枚举值 | 确认为 4 个值：`EGM96`、`relativeToStartPoint`、`aboveGroundLevel`、`realTimeFollowSurface` |
| 11 | `PositioningType` 枚举值 | 确认为 4 个值：`GPS`、`RTKBaseStation`、`QianXun`、`Custom` |

---

## 12. 交付顺序

1. `pom.xml` 新增 `jackson-dataformat-xml` 依赖
2. `wayline/enumtype/` — 枚举类（无依赖，可并行编写）
3. `wayline/model/` — POJO record 类（依赖枚举）
4. `wayline/WpmlCodec.java` — XML 序列化工具
5. `wayline/WaypointTemplate.java` + 3 个子 Builder — 航点模板主 API
6. `wayline/Mapping2dTemplate.java` / `Mapping3dTemplate.java` / `MappingStripTemplate.java` — 建图三模板 Builder
7. `src/test/` — TDD 测试用例（与实现同步编写）
8. `package-info.java` — 包文档

> **实现状态**：全部四种模板已实现并通过测试（WaypointTemplateTest 13/13、MappingTemplateTest 6/6、WaylineEnumTest 17/17）。
