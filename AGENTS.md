# AGENTS.md — AI 协作指南

本文件供 AI 编码助手阅读，明确 AI 编程约定与必须遵守的硬约束。

---

## 1. 项目概述

DJI Cloud API SDK 是 DJI 上云协议的 Java 类型安全 POJO 库，覆盖两个独立场景：机场上云（Dock ↔ 平台，MQTT 5 通道 services/drc/events/requests/status）与 Pilot 上云（Pilot ↔ 平台，HTTP + WebSocket），共 188 个方法、250 个 POJO。

> 项目定位（协议定义层 / 单一真相源）见 [架构设计文档 §1](docs/architecture-design.md)。核心价值：将 DJI Cloud API 的协议常量、数据结构以纯 Java 类型（record + enum）表达，消除模拟器与 hivemind 平台间协议定义的重复维护。

- **语言**：Java 21（record + sealed interface + pattern matching）
- **构建**：Maven，JDK 21+
- **依赖**：仅 Jackson（JSON 编解码），零运行时框架依赖
- **序列化**：`MessageCodec` 统一配置 Jackson `SNAKE_CASE`，snake_case ↔ camelCase 双向映射
- **版本**：跟随 DJI Cloud API 协议版本（当前 1.16.1），格式 `1.16.1.X`（X 为 SDK 修复版本号）

> 架构详情（包结构、协议覆盖、设计目标）见 [架构设计文档](docs/architecture-design.md)。
> 规格测试用例（TDD 驱动开发）见 [TDD 测试用例文档](docs/tdd-test-cases.md)。

---

## 2. AI 必须遵守的硬约束

### 协议忠实性
- POJO 字段必须与 DJI Cloud API 官方文档**完全匹配**，包括字段名、类型、必填/选填、嵌套结构
- 所有 ServiceMethod / DrcMethod / DrcUpMethod / EventMethod / RequestsMethod / StatusMethod 枚举必须包含 DJI 文档中定义的全部方法
- 枚举值域必须与 DJI 文档完全一致（如 mode_code 0-17、landing_type 0-4 等），不得遗漏或增减
- DJI 文档标记为 deprecated 的方法，只有在 DJI 文档**明确声明**废弃时才标记废弃，不得自行推断废弃
- 不确定的协议字段、类型、枚举值，必须向开发者确认后再决策，禁止凭猜测自行确定

### 注解体系（@DocUrl / @Verified / @Inferred）
- 每个协议 POJO 必须标注 `@DocUrl`，指向 DJI 官方文档 URL
- 已通过 DJI 文档核实的标注 `@Verified(basis="...")`，说明核实依据
- 未得官方文档确认的推断实现标注 `@Inferred(reason="...", verifyPoint="...")`，说明推断理由和待验证点
- `@Inferred` 标注的协议元素，在真机验证通过后必须及时更新为 `@Verified`
- 枚举常量需要独立标注验证状态时，在常量声明上直接添加注解（与类级注解独立）

### 工程约束（按模块分级适用）

本 SDK 为多模块结构，各模块定位不同，约束分级适用：

| 约束 | sdk 模块（协议定义层） | sdk-wayline 模块（航线工具套件） |
|---|---|---|
| **零业务逻辑** | **严格适用** — 只定义"协议是什么"，不实现"怎么用" | **不适用** — 工具套件的价值就是提供"怎么用"的便捷工具（Builder/Template/校验/转换） |
| **零 MQTT/HTTP 耦合** | 适用 | 适用（不绑定具体客户端库） |
| **不可变** | 适用（record + final） | 适用（Template 构建完成后不可变，Builder 本身可变但 build() 后产物不可变） |
| **无硬编码值** | 适用 | 适用（协议常量用枚举或常量类，校验范围用常量） |
| **正常业务逻辑返回明确拒绝原因而非抛异常** | 适用 | 适用（参数校验拒绝时返回明确原因） |
| **不适用的值用 `-` 显示** | 适用 | 适用 |
| **未使用的接口相关代码必须删除** | 适用 | 适用 |

> **关键区分**：`sdk-wayline` 模块作为工具套件，Template/Builder 类的参数校验、全局/局部参数回退、XML/KMZ 序列化、文件 IO 等功能是工具套件的合理职责，不属于"业务逻辑"越界。判定依据：模块定位由"概念核心"决定——sdk-wayline 的概念核心是"航线文件生成工具"而非"协议定义"。

### 序列化约定
- Jackson `SNAKE_CASE` 全局配置，Java 字段 camelCase ↔ JSON snake_case 自动映射
- 可选字段（如版本新增、设备特有）使用包装类型（`Integer`/`String` 等）而非基本类型（`int`），缺席时为 null
- `PropertySetRequest` / `PropertySetReply` 使用 `@JsonAnyGetter` + `@JsonCreator(DELEGATING)` 实现 DJI 扁平 map 格式（非 `{"properties": {...}}` 包裹）
- record 的 `@JsonInclude(NON_NULL)` 确保可选字段缺席时不序列化

### 版本兼容性
- DJI 文档新增字段（如 v1.16.1 的 `photo_format`）使用包装类型（`Integer`），旧固件未推送时反序列化为 null
- 旧固件兼容性必须有测试覆盖（field 缺席 → null 断言）
- DJI 协议版本升级时，前三段版本号同步递增，SDK 修复版本号归零

---

## 3. AI 代码修改前置流程约定

1. **先读后改**：修改任何文件前必须先 Read 理解现有代码，不在未读文件上提建议
2. **分析先行（中文）**：复杂任务必须用**中文**分析方案，列出每个可选方案的优缺点（对代码结构的影响、维护成本、兼容性风险等），让开发者清楚知道选择该方案会导致哪些代码变动及其代价。AI 负责分析，开发者拥有方案选择权，达成一致后再编码
3. **依赖官方文档**：涉及 DJI 协议的问题必须查阅 DJI Cloud API 官方文档（https://developer.dji.com/doc/cloud-api-tutorial/cn/），不得凭猜测下结论
4. **共性优先**：遇到问题先分析是否为同类共性问题，架构优化能解决的优先提供优化建议（经用户确认后执行），不打补丁式修改
5. **TDD 驱动**：新增功能或修复 Bug 时，先在 [TDD 测试用例文档](docs/tdd-test-cases.md) 编写测试用例（基于 DJI 文档规格，非实现代码），再实现代码；优化阶段以该文档为依据确保不破坏已有行为
6. **编译与测试验证**：Java 改动后执行 `mvn test`（含编译），汇报通过/失败/跳过数。新增功能必须同步补充对应的 Java 单元测试
7. **修改后文档对齐**：每次代码修改完成后，必须核对改动是否符合 DJI Cloud API 官方文档，发现偏差立即更正。同步更新 architecture-design.md 和 tdd-test-cases.md
8. **方案审查**：开发者提供修改方案时，AI 必须先分析方案的自洽性（内部是否矛盾）、合理性（是否符合架构设计和 DJI 文档）、破坏性（是否会破坏现有功能或引入新问题），不能盲目按方案立即修改代码。开发者拥有方案选择权，方案分析由 AI 负责
9. **错误代码即删**：确定为错误的设计和实现，注解说明原因后立即删除，不保留注释标记的废弃代码，避免 AI 后续优化时将错误代码改回
10. **架构演进同步**：架构升级导致原有类名、方法名、变量名、文档描述失去准确含义时，必须及时更新命名和表述，避免历史命名误导后续 AI 决策。涵盖范围：代码命名、文档表述、配置字段名、注释说明
11. **不确定即确认**：涉及协议字段、字段类型、枚举值、接口结构等任何不确定的内容，必须向开发者确认后再决策，禁止凭猜测或推断自行确定。AI 的职责是识别并列出不确定项，由开发者提供权威依据（官方文档或明确决策）后执行
12. **推断项记录**：涉及 DJI 协议但未得到官方文档确认的实现决策，选择最优方案完成后，必须用 `@Inferred` 注解标记，并附 `reason()` 和 `verifyPoint()`。判断标准：DJI 文档是否明确规定了该行为/字段/错误码——如果没有明确，即使选择了合理推断方案，也必须标记

---

## 4. 修改结果与影响分析汇报

每次完成代码修改后，AI 必须汇报：

- **修改清单**：列出改动的文件及具体位置（带文件链接）
- **修改内容**：每个文件改了什么、为什么改
- **影响分析**：改动对现有功能的影响（是否破坏既有流程、是否影响其他模块）
- **文档对齐核实**：每次修改必须核对是否符合 DJI 官方文档，注明依据的文档 URL 及关键原文（涉及协议改动时尤其严格）
- **验证结果**：`mvn test` 结果（Tests run: N, Failures: N, Errors: N, Skipped: N），无 Java 改动时注明"无 Java 改动"
- **待确认项**：如有不确定的影响或需用户决策的点，明确列出

---

## 5. 文档更新策略

四类文档各有单一职责，内容不重复，通过引用关联：

| 文档 | 职责 | 真相源范围 |
|---|---|---|
| README.md | 用户手册 + 开发导航 | Maven 依赖、快速开始、版本说明、下载地址、文档索引 |
| architecture-design.md | 设计详情定义源 | 架构、包结构、协议覆盖、设计目标、类说明表、已知缺陷 |
| AGENTS.md | AI 编程约定 | 修改流程、硬约束、汇报要求、文档更新策略、命名规范 |
| tdd-test-cases.md | 规格测试用例 | Given-When-Then 测试用例、容易搞错的陷阱 |

### 更新时机

| 场景 | 更新顺序 | 更新内容 |
|---|---|---|
| 新增功能 | tdd-test-cases.md → architecture-design.md → 代码 | 先写测试用例，再更新设计，再实现 |
| 修复 Bug | tdd-test-cases.md → 代码 | 添加回归测试用例，再修复 |
| 协议变更 | architecture-design.md → tdd-test-cases.md → 代码 | 先更新类说明/字段表，再更新测试用例，再实现 |
| AI 约定变更 | AGENTS.md | 更新修改流程/硬约束/策略 |
| 项目结构变更 | architecture-design.md → README.md | 设计文档更新详细版，README.md 更新导航概览 |
| 优化重构 | tdd-test-cases.md（核对）→ 代码 | 以测试用例为依据，确保不破坏已有行为 |

### 更新原则

1. **TDD 先行**：任何功能/Bug 变更，先更新 tdd-test-cases.md 测试用例
2. **单一真相源**：每类内容只在一个文档中定义，其余引用
   - 架构/包结构/协议覆盖 → architecture-design.md
   - AI 约定/文档策略 → AGENTS.md
   - 规格行为/陷阱 → tdd-test-cases.md
   - 用户操作/配置 → README.md
3. **不跨文档复制**：通过链接引用，不粘贴重复内容
4. **修改后核对**：按 §3 第7条文档对齐要求，核对改动是否符合 DJI 官方文档

---

## 6. 命名规范与包结构约定

### 6.1 核心原则

1. **避免歧义命名**：不用 "Controller"（与 MVC Controller 混淆）、"Other"（过于模糊）等无法准确表达职责的名称。遥控器用 `RcModel`（Remote Controller 缩写），不用 `ControllerModel`
2. **DJI 术语对齐**：类名、方法名应能对应 DJI Cloud API 文档中的分类和术语
3. **注释与代码一致**：注释必须准确描述代码行为，发现矛盾时立即修正
4. **枚举命名对齐 DJI 文档**：枚举常量名应能反映 DJI 文档中的枚举值含义

### 6.2 设备型号命名

| 类 | 命名 | 说明 |
|---|---|---|
| 遥控器枚举 | `RcModel` | 用 "Rc"（Remote Controller），不用 "Controller"（避免 MVC 混淆） |
| 机场枚举 | `DockModel` | 对齐 DJI "Dock" 术语 |
| 飞行器枚举 | `DroneModel` | 对齐 DJI "Drone" 术语 |
| 设备型号接口 | `DeviceModelProvider` | 提供 `toModel()` + default 委托方法（`domain()`/`type()`/`subType()`/`modelKey()`/`isDock()`/`isController()`/`isAircraft()` 等） |
| 反查方法 | `fromType(int, int)` / `fromModelKey(String)` | 从 (type, subType) 或 "domain-type-subType" 反查枚举常量 |

### 6.3 包结构约定

| 包 | 职责 | 包含内容 |
|---|---|---|
| `annotation` | 文档追溯注解 | `@DocUrl`、`@Verified`、`@Inferred` |
| `codec` | JSON 编解码 | `MessageCodec`、`DjiMessage` |
| `protocol` | 协议定义层 | topic/、method/、envelope/、error/ |
| `command` | 指令 POJO 层 | service/、drc/、event/、property/、request/、status/ |
| `model` | 设备型号层 | `DeviceModel`、`DeviceModelProvider`、`DockModel`、`DroneModel`、`RcModel`、`DeviceCompatibility` |
| `telemetry` | 遥测数据层 | `OsdField`、`StateField`、`DockOsd`、`DroneOsd`、`RcOsd` + enumtype/ |
| `wayline` | 航线模板层 | **sdk 模块**：WPML model/、enumtype/（纯 POJO）；**sdk-wayline 模块**：Builder、Template、Codec（工具层） |
| `flow` | 注册流程 | `DockRegistrationFlow`、`PilotRegistrationFlow` |
| `http` | HTTP API | `HttpApiPath`、`StsCredentials` |
| `websocket` | WebSocket 推送 | `WsBizCode`、`WsPushMessage` |
| `capture` | 录制配置 | `CaptureConfig`、`CaptureRecorder` |

### 6.4 POJO 命名规则

- **Request/Reply 对称**：每个指令有 `XxxRequest` 和 `XxxReply`（无 output 的用 `NoOutputReply`，无参数的用 `NoParameterRequest`）
- **方法名转类名**：DJI `snake_case` 方法名转 `PascalCase` 类名（如 `flighttask_prepare` → `FlighttaskPrepareRequest`）
- **推送数据**：DRC 上行推送用 `XxxPushData`（如 `CameraStatePushData`、`DroneStatePushData`）
- **事件数据**：事件用 `XxxData`（如 `FlighttaskProgressData`、`OtaProgressData`）

### 6.5 枚举命名规则

- **Method 枚举**：`ServiceMethod`、`DrcMethod`、`DrcUpMethod`、`EventMethod`、`RequestsMethod`、`StatusMethod`、`PropertySetMethod`
- **字段枚举**：`OsdField`（pushMode=0）、`StateField`（pushMode=1）
- **设备型号枚举**：`DockModel`、`DroneModel`、`RcModel`
- **枚举常量**：`UPPER_SNAKE_CASE`，名称应反映 DJI 文档枚举值含义
- **反查方法**：`fromFieldName(String)`、`fromType(int, int)`、`fromModelKey(String)` — null/未知值抛 `IllegalArgumentException`

### 6.6 Jackson 序列化策略

- **全局 SNAKE_CASE**：`MessageCodec` 统一配置，Java camelCase ↔ JSON snake_case
- **扁平 map**：`PropertySetRequest`/`PropertySetReply` 用 `@JsonAnyGetter` + `@JsonCreator(DELEGATING)`，不产生 `{"properties": {...}}` 包裹
- **可选字段**：包装类型（`Integer`/`String`）+ `@JsonInclude(NON_NULL)`，缺席时不序列化
- **枚举序列化**：DJI 枚举值为 int 时用 `int` 字段 + `@JsonValue`；为 string 时用 `String` 字段
