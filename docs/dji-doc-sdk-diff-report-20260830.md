# DJI Cloud API 文档 vs SDK 代码对比报告

> 生成时间：2026-08-30
> 文档来源：https://developer.dji.com/doc/cloud-api-tutorial/cn/（v1.16.1，2025-12-17 发布）
> SDK 版本：1.16.1.1-SNAPSHOT
> 对比原则：协议忠实性 —— 以 DJI 官方文档为唯一真相源，差异项按严重程度标记，不确定内容记录在案，不主动修改代码

---

## 一、概述

本次对比覆盖 DJI Cloud API 官方文档网站（中文）与 `dji-cloud-api-sdk` 协议定义层代码（`sdk/` 模块）。

由于 DJI 文档网站结构限制（`events.html`/`services.html`/`status.html`/`requests.html` 等聚合页面返回 404 或 Cookie 拦截），**无法通过爬虫获取完整的方法清单**，只能从各功能子页面（`drc.html`、`properties.html`、`remote-control.html`、`media.html` 等）提取信息。因此本报告聚焦于**可获取到的文档内容**与代码的对比，不排除存在文档中已定义但本次未抓取到的差异项。

---

## 二、已确认一致的项（✅ 无差异）

| 类别 | 项 | 文档定义 | SDK 实现 | 结论 |
|---|---|---|---|---|
| 协议版本 | v1.16.1 | 2025-12-17 发布 | `pom.xml` 版本 `1.16.1.1-SNAPSHOT` | ✅ 一致 |
| 照片格式 | `photo_format` 新增 DLT-664 | 值 `16` | `PhotoFormat.DLT664(16)` | ✅ 一致 |
| 机场模式码 | `mode_code`（机场） | 0-5 | `DockModeCode` 0-5 | ✅ 一致 |
| 任务状态码 | `flighttask_step_code` | 0-5, 255, 256 | `FlighttaskStepCode` 0-5, 255, 256 | ✅ 一致 |
| 返航预留电量 | `remaining_power_for_return_home` | v1.16.1 新增，rw 属性 | `PropertySetMethod.REMAINING_POWER_FOR_RETURN_HOME` | ✅ 一致 |
| 废弃事件 | `drc_status_notify` | 文档标注已废弃 | `EventMethod.DRC_STATUS_NOTIFY` 标注 `@Inferred(Abandoned)` | ✅ 一致 |
| DRC 链路状态 | `drc_state` | 0-2 | `DrcState` 0-2 | ✅ 一致 |
| 摇杆无效通知 | `joystick_invalid_notify.reason` | 0-4 | `JoystickInvalidNotifyData` reason 0-4 | ✅ 一致 |
| 云控授权通知 | `cloud_control_auth_notify` | output.status: ok/failed/canceled | `CloudControlAuthNotifyData.Output.status` | ✅ 一致 |

---

## 三、明确差异（🔴 需关注）

### 3.1 CameraMode 枚举缺少 `4=定时拍照`

- **文档依据**：Dock3 `drc_camera_state_push` 页面明确列出 `camera_mode` 值域：
  - `0` = 拍照
  - `1` = 录像
  - `2` = 智能低光
  - `3` = 全景拍照
  - `4` = 定时拍照
- **SDK 现状**：`CameraMode` 枚举仅定义 0-3（`PHOTO`/`VIDEO`/`HYPER_LIGHT`/`PANORAMA`），**缺少 `4=定时拍照`**。
- **影响分析**：
  - `CameraStatePushData.CameraState.cameraMode` 字段类型为 `int`（非 `CameraMode` 枚举），因此 JSON 反序列化时**不会抛异常**，运行时可用。
  - 但如果调用方使用 `CameraMode.fromCode(4)` 反查枚举，会抛出 `IllegalArgumentException`。
  - `CameraModeSwitchRequest` 等使用 `CameraMode` 枚举作为字段类型的类，无法表达"定时拍照"模式。
- **严重程度**：🔴 **中** —— 不会导致反序列化失败，但枚举不完整会限制调用方表达能力。
- **建议行动**：补充 `INTERVAL_PHOTO(4, "定时拍照")` 到 `CameraMode` 枚举，并更新 `@Verified` 依据。

---

## 四、不确定 / 待验证项（🟡 需确认）

### 4.1 DroneModeCode 值域 18-20 是否属于 Dock3 文档范围

- **文档依据**：Dock3 `drc_drone_state_push` 页面列出的 `mode_code` 值域为 **0-17**（待机、起飞准备、起飞准备完毕、手动飞行、自动起飞、航线飞行、全景拍照、智能跟随、ADS-B 躲避、自动返航、自动降落、强制降落、三桨叶降落、升级中、未连接、APAS、虚拟摇杆状态、指令飞行）。
- **SDK 现状**：`DroneModeCode` 枚举定义了 0-20，额外包含：
  - `18` = 空中 RTK 收敛模式
  - `19` = 机场选址中
  - `20` = POI 环绕
- **分析**：`DroneModeCode` 注释明确说明"Pilot 上云飞行器设备属性多 18=空中 RTK 收敛模式"，且枚举的 `@Verified` 依据为"机场上云 M30 文档完整定义（0-20）"。Dock3 文档可能未完整列出 Pilot 场景或特定机型的模式码。
- **严重程度**：🟡 **低** —— 值域扩展不会导致兼容性问题（旧固件不会发送未定义值），但需确认 Dock3 是否也会上报 18-20。
- **建议行动**：无需立即修改，保持现有实现；若后续 Dock3 真机上报 18-20 导致 `fromCode()` 抛异常，再核实文档更新枚举。

### 4.2 `remaining_power_for_return_home` 的适用设备范围

- **文档依据**：v1.16.1 发布说明指出该属性适用于"大疆机场3、大疆机场2"，用于自定义飞行器返航预留电量。
- **SDK 现状**：`PropertySetMethod.REMAINING_POWER_FOR_RETURN_HOME` 的注释标注为"M3D/M4D（v1.16.1 新增）"，未提及 Dock2。
- **分析**：该属性是**飞行器属性**（非机场属性），Dock2/Dock3 搭载 M3D/M4D 飞行器时均可设置。文档说"大疆机场3、大疆机场2"是指通过这两个机场上云的场景支持此属性，而非属性属于机场本身。
- **严重程度**：🟡 **低** —— 枚举常量存在且名称正确，注释的设备范围标注不影响功能。
- **建议行动**：核实后更新注释，明确为"Dock2/Dock3 搭载的 M3D/M4D 飞行器"。

### 4.3 DockOsd 是否应包含 `remaining_power_for_return_home`

- **文档依据**：文档将该字段列在**设备属性（properties）**中，accessMode=rw，未明确出现在 OSD（pushMode=0）字段列表中。
- **SDK 现状**：`DockOsd` 不包含此字段，`DroneOsd` 也不包含。
- **分析**：`remaining_power_for_return_home` 是可设置的飞行器属性，通常通过 `property/set` 设置、通过 `property/set_reply` 返回结果，**不一定通过 OSD 周期推送**。Dock2/Dock3 的 properties 页面是否将其列为 pushMode=0 或 pushMode=1 的字段，本次抓取未完全确认。
- **严重程度**：🟡 **低** —— 如果文档确认该字段属于 OSD/State 推送，则需在 `DockOsd`/`DroneOsd` 中补充；如果仅属于 property，则当前实现正确。
- **建议行动**：查阅 Dock2/Dock3 properties 页面确认该字段的 `pushMode` 标记。

### 4.4 `CameraStatePushData.CameraState.cameraMode` 使用 `int` 而非 `CameraMode` 枚举

- **文档依据**：`drc_camera_state_push` 的 `camera_mode` 为枚举值（0-4）。
- **SDK 现状**：`CameraState.cameraMode` 字段类型为 `int`，而非 `CameraMode` 枚举。
- **分析**：这是设计选择 —— `CameraState` 作为 DRC 推送数据结构，使用原生 `int` 可避免未知值反序列化失败。`CameraMode` 枚举用于 services 指令（如 `CameraModeSwitchRequest`）。两者职责分离合理。
- **严重程度**：🟡 **信息** —— 非差异，但值得记录。如果后续 `CameraMode` 补充了 4=定时拍照，可考虑是否将 `CameraState.cameraMode` 升级为枚举类型以增强类型安全。

---

## 五、SDK 已标注的推断项（已有 @Inferred 标记）

以下项 SDK 代码已主动标注 `@Inferred`，与文档对比后状态无变化，继续保持待验证状态：

| 位置 | 内容 | 推断原因 | 验证点 |
|---|---|---|---|
| `DrcMethod` | `HEART_BEAT` 等 9 个常量 | 协议格式与发起方未在 DJI 文档明确 | 真机确认 |
| `EventMethod` | `FLIGHTTASK_READY` | DJI 文档列出但 simulator 未实现 | 真机验证 |
| `EventMethod` | `IN_FLIGHT_WAYLINE_PROGRESS` | 同上 + need_reply 推断 | 真机验证 |
| `EventMethod` | `RETURN_HOME_INFO` | 同上 | 真机验证 |
| `EventMethod` | `POI_CIRCLE_STATUS` | 同上 | 真机验证 |
| `EventMethod` | `SPEAKER_TTS_PLAY_START_PROGRESS` | need_reply 推断 | 真机验证 |
| `EventMethod` | `SPEAKER_AUDIO_PLAY_START_PROGRESS` | need_reply 推断 | 真机验证 |
| `AiInfoPushData` | `aiWaylineState` 字段 | Data 表未列出，基于 Example 推断 | 文档补充或真机验证 |
| `DroneControlRequest` | 整类 | 已废弃，字段基于 simulator 实现 | DJI 文档最终确认 |
| `HeartBeatReply` | 整类 | 回复结构基于 simulator 推断 | 真机确认 |
| `DrcSpeakerTtsSetRequest` | 整类 | TTS 逻辑需 text 字段但 simulator 未解析 | DJI 文档确认 |
| `ConfigReply` | 整类 | 回复结构直接含 app_id | 真机验证 |
| `FlighttaskProgressGetReply` | 整类 | simulator 未解析 output 结构 | 真机验证 |
| `NoOutputReply` | 整类 | 27 个无参数指令共用 | 真机验证独立 Reply 需求 |
| `SimulateMission` | `altitude` 字段 | takeoff_to_point 未解析 altitude | DJI 文档确认 |
| `LeafNode` | 整类 | simulator 仅记录 size 未解析字段 | DJI 文档确认 |
| `ServiceProgressData` | 整类 | 13 个方法共用通用结构 | 真机验证各方法 ext 字段 |
| `DeviceExitHomingNotifyData` | `reason` 字段类型 | simulator 未验证 | 真机验证 |
| `ReturnHomeInfoData` | 整类 | simulator 未实现验证 | 真机验证 |
| `UpdateTopoReplyData` | 整类 | 字段待验证 | 真机验证 |
| `DockOsd` | `selfConvergeCoordinate` | 文档列为 struct 但未定义子字段 | 文档补充或真机验证 |

---

## 六、文档抓取限制说明

以下文档页面因网站限制（返回 404 / Cookie 页面 / 未展开抓取）未能获取完整内容，导致无法进行全面方法级对比：

| 页面 | 状态 | 影响 |
|---|---|---|
| `dock-to-cloud/mqtt-dock.html` (events 聚合页) | 404 | 无法获取完整 events 方法清单，只能从功能页提取部分事件 |
| `dock-to-cloud/mqtt/services.html` | 404 | 无法获取完整 services 方法清单 |
| `dock-to-cloud/mqtt/status.html` | 404 | 无法获取完整 status 方法清单 |
| `dock-to-cloud/mqtt/requests.html` | 404 | 无法获取完整 requests 方法清单 |
| Dock 2 / Pilot 的更多功能页 | 未展开 | 可能遗漏部分方法/字段定义 |

**建议**：如需完整对比，需人工逐页浏览 DJI 文档或获取离线文档包，重点核对：
1. `ServiceMethod` 97 个常量是否覆盖文档全部 services 方法
2. `DrcMethod` 42 个常量是否覆盖文档全部 drc/down 方法
3. `EventMethod` 29 个常量是否覆盖文档全部 events 方法（不含与 services 同名的进度上报）
4. `RequestsMethod` 8 个常量是否覆盖文档全部 requests 方法
5. `StatusMethod` 1 个常量（`update_topo`）是否为 status 通道唯一方法

---

## 七、差异汇总（按严重程度排序）

| 序号 | 差异项 | 严重程度 | 是否需修改代码 | 备注 |
|---|---|---|---|---|
| 1 | `CameraMode` 枚举缺少 `4=定时拍照` | 🔴 中 | 是（补充枚举常量） | 反序列化不失败，但限制表达能力 |
| 2 | `DroneModeCode` 18-20 是否在 Dock3 文档范围内 | 🟡 低 | 否（保持观察） | Pilot/特定机型可能已定义 |
| 3 | `remaining_power_for_return_home` 注释设备范围 | 🟡 低 | 否（仅更新注释） | 功能正确，注释可细化 |
| 4 | DockOsd/DroneOsd 是否需包含 `remaining_power_for_return_home` | 🟡 低 | 待确认 | 取决于文档 pushMode 标记 |
| 5 | `CameraState.cameraMode` 使用 `int` 非枚举 | 🟡 信息 | 否（设计选择） | 可后续升级为枚举 |

---

## 八、建议后续行动

1. **立即行动**：补充 `CameraMode.INTERVAL_PHOTO(4, "定时拍照")`，更新 `@Verified` 依据为 Dock3 DRC 文档。
2. **短期跟进**：人工浏览 DJI 文档 `properties.html` 确认 `remaining_power_for_return_home` 的 `pushMode` 标记，决定是否补充到 `DockOsd`/`DroneOsd`。
3. **中期跟进**：人工逐项核对 `ServiceMethod`/`DrcMethod`/`EventMethod`/`RequestsMethod` 与 DJI 文档的覆盖度，尤其关注 v1.16.1 新增方法和字段。
4. **持续跟进**：对 `@Inferred` 标注项，在真机验证或 simulator 补全后及时更新为 `@Verified`。
