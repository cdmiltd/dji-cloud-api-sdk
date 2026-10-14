# DJI Cloud API 文档与 SDK 实现差异跟踪报告

> 生成日期：2026-09-07
> 文档基准：https://developer.dji.com/doc/cloud-api-tutorial/cn/
> SDK 基准：dji-cloud-api-sdk（协议版本 1.16.1.x）
> 对比范围：发布记录、Dock3 远程控制/指令飞行/属性、M4D 属性、Pilot 上云 DRC

---

## 1. 严重差异（协议忠实性风险）

### 1.1 `remaining_power_for_return_home` — 文档未定义，SDK 已实现

| 维度 | 详情 |
|------|------|
| **文档来源** | v1.16.1 发布记录明确说明："支持自定义飞行器的返航预留电量，可设置飞行器可读写设备属性 remaining_power_for_return_home" |
| **文档实际** | M4D/M4TD 设备属性页面（`m4d-properties.html`）**未包含该字段**；Dock3 属性页面（`dock3/properties.html`）**也未包含该字段** |
| **SDK 现状** | `PropertySetMethod.REMAINING_POWER_FOR_RETURN_HOME`（第85行）、`StateField.REMAINING_POWER_FOR_RETURN_HOME`（第158行）均已实现 |
| **风险分析** | SDK 基于发布记录提前实现了文档中未列出的属性。若 DJI 文档后续更新时字段名/类型/适用范围发生变更，SDK 将产生协议偏差 |
| **建议** | 保持当前实现（发布记录是官方声明），但需持续跟踪 M4D properties 文档更新；当前 `@Verified` 依据为"Dock3 + M3D properties.html"，但实际文档中未找到该字段，建议将 `@Verified` 改为 `@Inferred` 或修正依据说明 |

### 1.2 `drc_status_notify` — 文档标注"已废弃"，SDK 未标记 `@Deprecated`

| 维度 | 详情 |
|------|------|
| **文档来源** | Dock3 drc.html："DRC 链路状态通知（已废弃）"；Pilot DRC："DRC 链路状态通知（已废弃）" |
| **SDK 现状** | `EventMethod.DRC_STATUS_NOTIFY`（第195-199行）存在，仅通过 `@Inferred` 的 `reason` 和描述字符串注明"Abandoned"，**未使用 Java `@Deprecated` 注解** |
| **风险分析** | 缺少语言层面的 `@Deprecated` 标记，调用方无法通过编译器警告感知该方法已废弃；与 DJI 文档的"已废弃"声明不匹配 |
| **建议** | 在枚举常量上增加 `@Deprecated` 注解，并保留 `@Inferred` 说明废弃原因 |

---

## 2. 中等差异（实现不完整或记录缺失）

### 2.1 AI 目标识别 — 缺少 3 个 Request POJO 类

| 维度 | 详情 |
|------|------|
| **文档来源** | v1.16 发布记录："AI目标识别，目标跟随"；Dock3 remote-control 页面 Event 部分已定义 `drc_ai_info_push` |
| **SDK 现状** | `DrcMethod` 枚举包含 11 个 AI 方法，但 `command/drc/ai/` 目录下**缺少 3 个 Request 类**：<br>1. `AiIdentifyScoreResetRequest`（对应 `drc_ai_identify_score_reset`）<br>2. `AiSpotlightZoomConfirmRequest`（对应 `drc_ai_spotlight_zoom_confirm`）<br>3. `AiSpotlightZoomStopRequest`（对应 `drc_ai_spotlight_zoom_stop`） |
| **补充说明** | 后两个方法（confirm/stop）可能为无参数指令，但 SDK 中未明确复用 `NoParameterDrcRequest`；`AiIdentifyScoreResetRequest` 应为无参数或有特定字段，当前完全缺失 |
| **建议** | 确认 DJI 文档中这 3 个方法的 data 结构后补全 Request 类；若为无参数，应显式在代码中建立与 `NoParameterDrcRequest` 的映射关系 |

### 2.2 `heart_beat` — v1.11.0 超时变更未在 SDK 中记录

| 维度 | 详情 |
|------|------|
| **文档来源** | v1.11.0 发布记录："指令飞行功能 DRC-心跳协议（Method: heart_beat）变更了超时时间" |
| **SDK 现状** | `DrcMethod.HEART_BEAT`（第74-83行）仅标注 `@Inferred`，`reason` 为"协议格式与发起方未在 DJI 文档明确"，**完全没有提及超时时间变更** |
| **风险分析** | 超时时间是 DRC 链路稳定性的关键参数，SDK 未记录意味着调用方无法依据 SDK 获知心跳间隔/超时判断逻辑 |
| **建议** | 查阅 DJI 文档 drc.html 中 heart_beat 的具体超时数值，更新 `HEART_BEAT` 的注释或相关 POJO 文档 |

### 2.3 `poi_circle_status` vs `poi_status_notify` — 方法名可能不一致

| 维度 | 详情 |
|------|------|
| **文档来源** | Pilot 上云 DRC 文档（`dji-rc-plus-2/drc.html`）使用 `poi_status_notify` 作为事件方法名 |
| **SDK 现状** | `EventMethod.POI_CIRCLE_STATUS`（第106行）使用 `poi_circle_status` 作为方法名；`PoiCircleStatusData` 类标注 `@Inferred`，注释说明"simulator 实现的方法名为 poi_status_notify" |
| **风险分析** | 文档方法名与 SDK 枚举方法名不一致，可能导致真机对接时事件无法正确解析 |
| **建议** | 待真机验证确认 DJI 实际推送的 method 字段值；若真机使用 `poi_status_notify`，需修正 `EventMethod` 枚举常量 |

---

## 3. 轻微差异 / 已确认实现

### 3.1 红外照片 DLT-664 格式 — SDK 已支持，但 Service API 存疑

| 维度 | 详情 |
|------|------|
| **文档来源** | v1.16.1 发布记录："红外照片支持保存为中国电力行业推荐的标准格式（DLT-664）"；Dock3 remote-control 页面 `drc_camera_state_push` 中 `photo_format` 枚举包含 `{"7":"RJPEG","16":"DLT664"}` |
| **SDK 现状** | `PhotoFormat.DLT664(16)` 已实现；`CameraStatePushData` 包含 `photoFormat` 字段（`Integer` 类型，旧固件兼容为 null） |
| **存疑点** | Dock3 remote-control 页面**仅有 Event（drc/up）部分，没有 Service（drc/down）部分**，无法确认 DJI 是否提供了独立的 `photo_format` 设置指令。SDK 中 `DrcMethod` 和 `command/drc/camera/` 下均未发现对应 Service 方法 |
| **结论** | 状态上报层面已实现；若需支持指令下发设置红外格式，需确认 DJI 是否有对应的 Service API |

### 3.2 M400 设备支持 — SDK 已实现

| 维度 | 详情 |
|------|------|
| **文档来源** | v1.15 发布记录："新增支持 DJI Matrice 400 设备类型" |
| **SDK 现状** | `DroneModel.M400(0, 103, 0, "Matrice 400", "M400", ...)` 已实现；`DeviceCompatibility` 中 `RC_PLUS_2 ↔ M400` 兼容性矩阵已配置 |
| **结论** | 完整实现，无差异 |

### 3.3 POI 环绕模式 — SDK 已实现

| 维度 | 详情 |
|------|------|
| **文档来源** | v1.14 发布记录："指令飞行 - 支持 POI 环绕模式" |
| **SDK 现状** | `ServiceMethod` 包含 `POI_MODE_ENTER`、`POI_MODE_EXIT`、`POI_CIRCLE_SPEED_SET`；`EventMethod` 包含 `POI_CIRCLE_STATUS`；`PoiCircleStatusData` 字段结构与文档一致 |
| **结论** | 完整实现，仅方法名一致性待验证（见 2.3） |

### 3.4 避障记录上报 — SDK 已实现

| 维度 | 详情 |
|------|------|
| **文档来源** | Dock3 drc.html：`obstacle_avoidance_notify` |
| **SDK 现状** | `EventMethod.OBSTACLE_AVOIDANCE_NOTIFY` 已实现；`ObstacleAvoidanceNotifyData` 包含 `waylineUuid`、`flightId`、`obstacles`（嵌套 `ObstacleInfo`）、`isFinalReport`，与文档一致 |
| **结论** | 完整实现，无差异 |

### 3.5 AI 目标识别推送数据 — SDK 已实现

| 维度 | 详情 |
|------|------|
| **文档来源** | Dock3 remote-control 页面：`drc_ai_info_push` |
| **SDK 现状** | `AiInfoPushData` 包含 `identifyOn`、`spotlightZoomOn`、`aiSpotlightZoom`、`aiModelList`、`selectedAiModel`、`aiWaylineState`，与文档 Event 结构对齐 |
| **结论** | 完整实现，无差异 |

---

## 4. 不确定项（待官方文档或真机确认）

### 4.1 `remaining_power_for_return_home` 的文档归属

- v1.16.1 发布记录明确声明该属性，但 M4D properties 页面和 Dock3 properties 页面均未列出
- **可能原因**：DJI 文档未及时更新；或该属性属于 M3D 飞行器而非 M4D
- **验证建议**：直接联系 DJI 开发者支持确认该属性的准确文档位置；或等待 DJI 文档更新

### 4.2 AI 识别 Service API 的完整列表

- Dock3 remote-control 页面仅包含 Event（drc/up）推送定义，未包含 Service（drc/down）指令定义
- SDK 中 11 个 AI DRC 方法的字段结构基于 simulator 实现和文档片段推断
- **验证建议**：获取 DJI 完整的 remote-control Service API 文档（若有独立页面），核对 11 个方法的 data 结构

### 4.3 `heart_beat` 超时时间的具体数值

- v1.11.0 发布记录提到超时时间变更，但当前 DJI 文档未给出具体数值
- SDK 中 `HeartBeatRequest`/`HeartBeatReply` 的字段结构也标注为 `@Inferred`
- **验证建议**：查阅 DJI drc.html 中 heart_beat 的详细说明，或通过真机抓包确认超时参数

### 4.4 `drc_status_notify` 是否应彻底移除

- DJI 文档标注"已废弃"且"不再维护"
- 但 SDK 中仍保留 `EventMethod.DRC_STATUS_NOTIFY`，理由是"若真机仍上报可按文档补全 POJO"
- **验证建议**：确认真机固件是否仍推送该事件；若已彻底移除，应从 SDK 中删除以减少误导

---

## 5. 版本兼容性矩阵核对

| 文档声明（v1.16.1） | SDK 实现 | 状态 |
|---------------------|----------|------|
| Dock3 ↔ M4D/M4TD | `DeviceCompatibility.isCompatible(DOCK3, M4D/M4TD)` | 已实现 |
| Dock2 ↔ M3D/M3TD | `DeviceCompatibility.isCompatible(DOCK2, M3D/M3TD)` | 已实现 |
| Dock1 ↔ M30/M30T | `DeviceCompatibility.isCompatible(DOCK1, M30/M30T)` | 已实现 |
| RC_PLUS_2 ↔ M400/M4E/M4T | `DeviceCompatibility.isCompatible(RC_PLUS_2, M400/M4E/M4T)` | 已实现 |
| RC_PRO ↔ MAVIC_3E/3T/3TA | `DeviceCompatibility.isCompatible(RC_PRO, MAVIC_3E/3T/3TA)` | 已实现 |
| M350_RTK ↔ RC_PLUS | `DeviceCompatibility.isCompatible(RC_PLUS, M350_RTK)` | 已实现 |

---

## 6. 建议行动清单

| 优先级 | 事项 | 行动 | 责任方 |
|--------|------|------|--------|
| P1 | `drc_status_notify` 废弃标记 | 增加 `@Deprecated` 注解，保留 `@Inferred` 说明 | AI 辅助修改 |
| P2 | AI Request 类补全 | 确认 `drc_ai_identify_score_reset`、`drc_ai_spotlight_zoom_confirm`、`drc_ai_spotlight_zoom_stop` 的 data 结构后补全 Request 类 | 需开发者确认文档 |
| P2 | `heart_beat` 超时记录 | 查阅 DJI 文档或真机确认超时数值，更新注释 | 需开发者提供依据 |
| P3 | `remaining_power_for_return_home` 注解修正 | 核实文档后修正 `@Verified` 依据，或改为 `@Inferred` | 需开发者确认文档 |
| P3 | `poi_status_notify` 方法名验证 | 真机验证实际推送的 method 字段值 | 需真机测试 |
| P3 | `drc_status_notify` 去留决策 | 确认真机是否仍推送该事件，决定是否移除 | 需真机测试 |

---

*本报告遵循 AGENTS.md 硬约束：对不确定内容记录在案，不主动修改代码。所有修改建议需经开发者确认后执行。*
