# DJI Cloud API v1.18 升级差异分析报告

> 生成日期：2026-10-14
> 文档基准：https://developer.dji.com/doc/cloud-api-tutorial/cn/ （v1.18，2026.10.13 发布）
> SDK 基准：dji-cloud-api-sdk `1.16.1.3` → 目标 `1.18.0.0`
> 对比范围：v1.18 发布列表全部新增项（O4 地面站 / Dock3 FTS / 机场 2·3 相机分辨率）

---

## 0. 决策记录（已与开发者确认）

| 议题 | 决策 |
|---|---|
| 同步范围 | **全量同步**：O4 地面站全模块 + Dock3 FTS + 相机录像/照片分辨率 + 版本与文档元数据 |
| O4 地面站建模 | **独立 `GroundStationModel`**；`DeviceDomain` 新增 `GROUND_STATION(8)` |
| 不确定项处理 | 文档明确项直接落地；不确定/冲突项以 `@Inferred` 标注并汇总待确认清单 |
| `ReplyEnvelope` 扁平回复承载（§5-21） | **方案 B**：新增 `FlatReplyEnvelope`，否决泛型化（2026-10-14） |
| `ReplyEnvelope.gateway`（§5-22） | **方案 A**：补 `String gateway`（末位、允许 null，零破坏）（2026-10-14） |
| O4 地面站范围 | **全量纳入**（与「同步范围」同义，自始确定、无需再议）；结论见 §5-30（已结案） |
| `property/set_reply` 承载（§5-29） | **29-A**：复用 `FlatReplyEnvelope`（`data` 为 `Object`），不新增第三信封（2026-10-14） |
| Cloud-API-2 SDK 交叉验证源 | **A**：登记为交叉验证源并逐项结案落盘（A28），冲突以 v1.18 官方文档为准（2026-10-14） |
| `home_position_is_valid` 机型差异（§5-31 / §5-36 / §5-38） | **按设备分别建模（三枚举）**：`Dock2HomePositionIsValid`(2) + `Dock3HomePositionIsValid`(4，由 `HomePositionIsValid` 改名) + `GroundStationHomePositionIsValid`(2)（2026-10-14） |
| Dock 属性 `rainfall` 类型（§5-39） | **改 `Integer`**（文档 enum_int `{0:无雨..3:大雨}`，非 mm）；随 `1.18.0.0` 发布（2026-10-14） |
| `drc_camera_state_push` 扩展字段 / `photo_format` 值域（§5-33 / §5-34） | **不新增扩展字段**；`photo_format` **零改动**；API2 值域与扩展族**不予采纳**（A29 结案，2026-10-14） |
| `linkage_zoom_state` 类型（§5-35） | **保持 `int`**（与文档示例 `0` 及值域表述一致）（2026-10-14） |
| `rid_infos` / `adsb_infos` 的 `need_reply`（§5-37） | **取 `0`（单向通知）** + `@Inferred`，真机核对后转 `@Verified`（2026-10-14） |
| Dock3 `return_home_info` 返航信息原文（A32） | **SDK 已覆盖 ✓**（字段/类型全一致，无结构改动）；`ReturnHomeInfoData` 类级 `@Inferred` **升 `@Verified`**（§5-18 结案）；新增 §5-42 / §5-43（枚举是否补建 + `PathPoint` 文档追溯，均非阻断）（2026-10-14） |
| Dock2 `return_home_info` 原文（A33） | **与 Dock3（A32）逐字段完全一致** → 该事件**无 Dock2/Dock3 机型分叉**，单一 POJO 覆盖（仅约束列排版差异，无语义影响）；二次印证 §5-18 结案、加强 §5-43，§5-42 不受影响；**无新增项**（2026-10-14） |
| Dock1 `return_home_info` 原文（A34） | 字段表**仅 3 项**（**无** `home_dock_sn` / `multi_dock_home_info`）→ **三机型核心字段一致**、机型专属字段可选 → 单一 POJO 覆盖成立；**§5-18 结案成立但勘误理由**（原 `@Inferred`「Dock1 无此字段」**属实**，属**标注类型不当**）；新增 §5-44（Dock1 独有描述语义，非阻断）（2026-10-14） |
| Dock3 DRC AI 框选坐标类型（§5-45） | **改 `Integer`**（文档 Type=`int`、constraint `[0,10000]`、示例整数；原 `Double` 系旧 basis `{max:1,min:0}`×10000 误读）；同步更正 `@Verified(basis)` 并删除过时 Javadoc；属 breaking（`Double`→`Integer`），随 `1.18.0.0` 发布（2026-10-14） |
| Dock2 DRC 页原文（A38） | **Event 段 4 项全部已覆盖 ✓**（method + POJO 齐备、字段/类型一致）；Service 段 **4 个 `DrcMethod` + 14 个 Request POJO 缺失**，**全部落在 §4 既有清单内**（≠新缺口，Dock2 页二次印证）；**新发现 2 项**（§5-46 `IrLense` 缺 `thermalSupportedPaletteStyles`、§5-47 `irZoomFactor` `int`→`Double`，**均非阻断、待开发者裁决**）；文档自身不一致 4 处记录在案（不改 SDK）；`interval` 口径补充入 §5-15（2026-10-14） |
| O4 固件升级原文（A39） | **`ota_progress` 全字段已覆盖 ✓**（method + POJO、字段/类型/枚举一致，`need_reply=1` 与 example 含 tid/bid 一致）；**`ota_create` Request 存在但有 2 处偏差**（`file_size` `Long` vs 文档 `int`；`firmware_upgrade_type` 原始 `int` 无枚举）；reply `output.status` 无专属 POJO（既有 `NoOutputReply` 约定）；4 项新登记（§5-48~51，均非阻断）；§3.1.5「完全一致」表述已修正（2026-10-14） |
| O4 远程日志原文（A40） | **4 个 method 全部存在 ✓**（`FILEUPLOAD_PROGRESS`/`FILEUPLOAD_LIST`/`FILEUPLOAD_START`/`FILEUPLOAD_UPDATE`）；`FileUploadListReply`/`FileUploadListRequest`/`FileUploadUpdateRequest` **结构一致 ✓**；`FileUploadStartRequest` **确缺** `file_store_dir`/`file_type`/`params.files[]` 子字段（§4 已列）；`FileUploadProgressData` **确缺**（悬空 `@link`）；**新登记 3 项**（§5-52~54：`long`/`Long` vs `int`、`module` 三口径、`fileupload_progress` 表 vs example 冲突）；**提示 §5-6 决策需复核**（毫秒 13 位超 int32）（2026-10-14） |
| Dock3 航线管理完整原文（A41） | **Event 5 项全已覆盖 ✓**（`return_home_info`/`flighttask_ready`/`flighttask_progress`/`device_exit_homing_notify`/`in_flight_wayline_progress`）；**Service 14 项方法常量全存在 ✓**；**`multi_dock_task` 子结构存在真实缺口**（`DockInfo` 缺 4 字段、`CenterNode` 缺 `sdrId`、`LeafNode` 空 record、`RtcmInfo` 缺失）→ 新登记 §5-55；`flighttask_create`（废弃）完全缺失 → §5-56；`FlighttaskProgressGetRequest` 多出必填 `flightId` → §5-57；`FlighttaskProgressGetReply.output` 为 `Object` 占位（文档已给出结构）→ §5-58；关闭 A19「Service 段截断」缺口；批量 `@Verified` basis 由 simulator 升为官方文档（2026-10-14） |
| Dock2 航线管理原文（A42） | **Event 5 项全已覆盖 ✓**、**Service 14 项方法常量全存在 ✓**、**Requests 2 项方法常量全存在 ✓**；`return_home_info`/`flighttask_ready`/`flighttask_progress`/`device_exit_homing_notify`/`in_flight_wayline_progress` 字段与 SDK 一致；**关键新发现**：`flighttask_progress_get` 请求 data **Dock2（`target_sn`+`flight_id`）与 Dock3（仅 `sn`）不一致** → SDK `sn` 与 Dock2 example wire key `target_sn` **错配**（新登记 §5-59，修订 §5-57）；`multi_dock_task` 缺口同 A41（§5-55）；`return_home` reply `output.status` 经 Dock2 页确认（强化 §5-51）；更正 A41-4（`AlternateLandPoint` 实为 5 字段，含 `height`）（2026-10-14） |
| 支持产品列表与固件版本原文（A43） | 开发者提供「支持产品列表与固件版本」页原文（对应 `overview/product-support.html`，与 `DeviceCompatibility` 的 `@DocUrl` 一致）。机场上云产品族与 SDK `DeviceCompatibility` **吻合**（机场3↔M4D/4TD；机场2↔M3D/3TD+M30/30T；机场↔M30/30T）→ **Dock↔Drone 搭配无需新增/修改 ✓**；**§5-12 结案**；**新决策点**：①「最低固件版本」是否纳入 SDK（§5-60）②`RC Pro 行业版` 亦列机场上云 → `isCompatible(RC_PRO)` 是否扩展（§5-61）（2026-10-14） |
| WPML 共用元素原文（A44） | 开发者提供「航线文件共用元素信息」页（= `dji-wpml/common-element.html`，SDK `wayline` 模型 `@DocUrl` 所指页）。**WPML 模型与文档逐一吻合**：全部元素组均有对应 SDK 类，字段数与类型一致（orientedShoot 28、accurateShoot 26、megaphone 10、searchlight 4、payloadParam 9…），**无缺失类/字段** ✓；**仅 2 项记录**（§5-62 WPML 布尔字段 int 承载口径、§5-63 `ActionActuatorFunc`/`ActionGroupMode` 枚举超集，**均无需改代码**）；**无代码改动**（2026-10-14） |
| template.kml 说明原文（A45） | 开发者提供「template.kml 说明」页（= `dji-wpml/template-kml.html`，SDK `wayline` 模型 `@DocUrl` 所指页）。审计确认 **4 项差异**：①`Placemark` 缺 `isRisky`/`useStraightLine`（§5-64）②`Mapping2dPlacemark` 缺 `quickOrthoMappingEnable`/`quickOrthoMappingPitch`（M4E，§5-65）③`MappingStripPlacemark.margin` 文档**浮点型** vs SDK `int`（§5-66）④`Folder`（航点）缺 `payloadParam`（§5-67，`MappingFolder` 已含 ✓、航点示例未含 → 层级待确认）；另 `Document.createTime/updateTime`（`Long`、承载 13 位 ms ✓）、`MissionConfig`（12 字段）、`WaylineCoordinateSysParam`（6 字段含 `globalShootHeight`/`surfaceFollowModeEnable`/`surfaceRelativeHeight`）、`Overlap`（8 字段）、`MappingHeadingParam`（2）、`Mapping3dPlacemark`（10）、枚举 `HeightMode`(4)/`PositioningType`(4，含 `QianXun`)/`CoordinateMode` **均与文档一致 ✓**；**均属实现阶段待落地（未写代码）**（2026-10-14） |
| waylines.wpml 说明原文（A46） | 开发者提供「waylines.wpml 说明」页（= `dji-wpml/waylines-wpml.html`，SDK `ExecuteFolder`/`ExecutePlacemark`/`ExecuteHeightMode` 的 `@DocUrl`）。审计确认 **4 项差异**：①`ExecuteFolder` 缺 `startActionGroup`（航线初始动作，§5-68）②`ExecutePlacemark` 缺 `isRisky`/`useStraightLine`（§5-69，与 §5-64 同源执行层）③waylines.wpml **多 Folder**（倾斜摄影生成 5 条航线）vs SDK `ParsedKmz.waylines`=`Kml<ExecuteFolder>` **单数** → 潜在缺口（§5-70）④`ExecutePlacemark.gimbalPitchAngle` 文档该页未列（引用 template.kml Placemark）→ 待确认（§5-71）；另 `missionConfig`（同 A45 ✓）、`ExecuteFolder` 的 `templateId/executeHeightMode/waylineId/autoFlightSpeed`、`ExecutePlacemark` 的 `Point/index/executeHeight/waypointSpeed/waypointHeadingParam/waypointTurnParam`、`ExecuteHeightMode`（**3 值**，正确排除 `aboveGroundLevel`）**均与文档一致 ✓**（2026-10-14） |
| DJI Cloud-API-2 SDK 风格对齐指示（A47） | 开发者指示：DJI `github.com/dji-sdk/Cloud-API-2/tree/main/sdk` 与本 SDK 属**包含关系**，本 SDK 应「尽量往 DJI 上云 API2 SDK 风格去套」。调研结论：DJI 仓库 `sdk/` 为 C++ 核心 + `sdk/platform/java/flight_hub_sdk`（SWIG JNI 绑定，包 `com.dji.flighthubsdk`）；**全仓库无任何 WPML/KMZ XML POJO 模型**（`wpml`/`actionActuatorFunc`/`waylineCoordinateSysParam` 代码搜索命中 **0**），KMZ 仅作二进制 blob（`ImportKmzItem`/`WaylineDetailKmzData`）；真实重叠点为 **MQTT 消息定义**（`sdk/deps/message_engine/.../message_define_user/generated/*`，snake_case 结构体）与 **HTTP bean**（`com.dji.flighthubsdk.bean`）。**开发者裁决对齐范围＝协议模型命名与结构**（不含版权头/许可证/仓库布局；保留 record/不可变/零依赖/Jackson 架构）。**待办**：逐项比对 DJI generated 结构体命名/字段 ↔ 本 SDK `command/*`、`protocol/method/*`（A47 附录） |
| §5-74 DRC 通道方法名前缀口径（A47） | **方案 A（采纳前缀）**：DRC 通道 wire method **逐字对齐官方文档**（`drc_camera_*` 等带 `drc_` 前缀），与 services 通道无前缀名（`camera_aim`…）**并存为两套常量**；飞行 4 件套（`heart_beat`/`stick_control`/`drone_emergency_stop` 及已废 `drone_control`）沿用文档**无前缀**写法；DRC 专属 POJO 与 services 版字段一致时**复用同一 record**；同步**更正 `DrcMethod` 类级 Javadoc**「同名复用」表述（2026-10-14） |
| 交付顺序 | **先产出本报告**，再按 TDD 流程实现 |
| 记录勘误（2026-10-14） | §5-7 原记「O4 `cmd.html` live/camera 外设段截断」**已过期**——A5 早已提供该页完整原文（`live_*` 5 + `camera_*` 10）→ 改记「已结案（A5）」；§5-30 原列为待确认项与 §0「同步范围：全量同步」自相矛盾 → 改记「已结案」 |
| M4D/M4TD 飞行器属性原文（A49） | 开发者提供「M4D/M4TD 设备属性」页原文（= `aircraft/m4d-properties.html`，与 `DroneOsd`/`OsdField`/`StateField`/`PropertySetMethod` 同族）。56 个顶层属性**绝大多数已覆盖 ✓**（字段名/类型/pushMode 归属一致）；**缺口 5 项**：①新增 pushMode=1 字段 `best_link_gateway` / `flysafe_database_version`（§5-77）②`fts_info` OSD 结构（**本页首次给出完整定义 → §5-8 前提撤销，已裁决建模（见下）**，§5-78）③`DroneModeCode` 缺 `mode_code=21`（§5-79）④`PropertySetMethod` 缺 2 个 rw 属性（§5-80）⑤`CameraInfo` 缺 20 字段（§5-81）；**Javadoc 级 4 处**（§5-82）。**未写代码**（2026-10-14） |
| §5-78 `fts_info` 建模裁决（A49） | **已裁决（2026-10-14）→ 方案 A（建模）**：M4D/M4TD 页已完整定义 `fts_info`（Type=`struct`、pushMode=0）→ **新建** `FtsInfo` record（`fts_status`/`fts_verification_code`/`fts_valid_duration`/`fts_timestamp`/`fts_sn`/`fts_firmware_version`）+ `FtsStatus` 枚举（6 值 `{0:未连接,1:已就绪,2:正在连接服务器,3:已触发停桨,4:未连接 dongle,99:未知错误}`）+ `OsdField.FTS_INFO` + `DroneOsd.ftsInfo`；§5-8 原结案依据「该 struct 从未被任何官方页面定义」**撤销**，改记「文档已完整定义 → 具备建模条件」；**待原文收齐后按 TDD 落地**（AGENTS §3-5） |
| Dock1 设备属性原文（A50） | 开发者提供 Dock1「设备属性」页原文（页面日期 2025-03-19；wire `thing/product/{device_sn}` 双通道；**46 个顶层属性**）。**绝大多数已覆盖 ✓**（`mode_code` 0-5 ✓、`flighttask_step_code` {0-5,255,256} ✓、`network_state` quality 6 档 ✓、`position_state`（含 `is_calibration`）✓、`maintain_status`（Dock 版 `{0,17,18}`）✓、`alternate_land_point` 含 `height` ✓、`wireless_link` `4g_*` ✓、`cover_state`/`putter_state`/`drone_charge_state`/`backup_battery` 等均已收录）。**缺口/偏差 2 项**：①`electric_supply_voltage`（页表 `int`/V）vs SDK `Double` → 并**修订 §5-1**「dock1/2/3 均无」前提（§5-83）②`drone_authority_info`（pushMode=0）在 `DockOsd` **无模型字段**、无 `DroneAuthorityInfo` record（§5-84）；**Javadoc 级 4 处**（§5-85~88）。**未写代码**（2026-10-14） |
| Dock1 设备管理原文（A51） | 开发者提供 Dock1「设备管理」页原文（页面日期 2025-03-19；Topic `sys/product/{gateway_sn}/status`（up）+ `sys/product/{gateway_sn}/status_reply`（down）；method `update_topo`）。**数据字段完全覆盖 ✓**：`UpdateTopoData` 7 顶层字段（`domain`/`type`/`subType`/`deviceSecret`/`nonce`/`thingVersion`/`subDevices`）与 `SubDevice` 8 字段（`sn`/`domain`/`type`/`subType`/`index`/`deviceSecret`/`nonce`/`thingVersion`）**逐字段一致**；`StatusMethod.UPDATE_TOPO` ✓、`sys/product` Topic 前缀 ✓、空 `sub_devices`=下线 ✓。**口径/元数据 3 项**：①`status_reply` data 本页**仅 `result`**，SDK `UpdateTopoReplyData` 多 `subType`（`@Inferred`）→ 依据冲突（§5-89）②`StatusMethod` Javadoc「data 含 sub_type 区分上线/下线」与 UP data `sub_type`＝产品子类型 **语义冲突**（§5-90）③`base_info` 本页**无**（O4 页有）→ 归属待核（§5-91）。**未写代码**（2026-10-14） |
| Dock1 组织管理原文（A52） | 开发者提供 Dock1「组织管理」页原文（页面日期 2025-03-19；Topic `thing/product/{gateway_sn}/requests`（up）+ `requests_reply`（down））含 3 条注册指令：`airport_bind_status` / `airport_organization_get` / `airport_organization_bind`。**Request 侧 3 个 POJO 与文档完全一致 ✓**（`AirportBindStatusRequest.Device`=`sn`；`AirportOrganizationGetRequest`=`deviceBindingCode`/`organizationId`；`AirportOrganizationBindRequest.BindDevice`=5 字段）。**Reply 侧 3 缺口**（与 A4 同源、本页**二次印证**）：①`AirportOrganizationBindReply.ErrorInfo.errSn` wire key `err_sn` ≠ 文档 `sn`（§5-92）②`AirportOrganizationGetReply` 缺 `Output{organizationName}` 且 `@Inferred` 假设 `output.airports` 错误（§5-93）③`AirportBindStatusReply` 缺 `Output{bindStatus[]}` + Javadoc 语义冲突（§5-94）。**A4 结论此前漏登记 §5 → 本页补齐 §5-92~94**。**未写代码**（2026-10-14） |
| Dock1 直播功能原文（A53） | 开发者提供 Dock1「直播功能」页原文（页面日期 **2025-11-12**，较前三页更新；Topic `thing/product/{gateway_sn}/services`（down）+ `services_reply`（up））含 4 条 services：`live_start_push` / `live_stop_push` / `live_set_quality` / `live_lens_change`。**4/4 方法已覆盖 ✓**（`ServiceMethod` 均存在；`LiveStartPushRequest`(4 字段)/`LiveStopPushRequest`/`LiveSetQualityRequest`/`LiveLensChangeRequest`；`VideoQuality` enum 0-4 ✓；reply 均仅 `result` ✓）——与 A5（O4 cmd live 段）**同源、二次印证**。**2 项待确认/非阻塞**：①`url_type` enum_int{0声网,1 RTMP,**3 GB28181,4 WebRTC，跳 2**} 与 `video_type` enum_string{ir,normal,wide,zoom} **未建模为 Java 枚举**（SDK 现裸 `Integer`/`String`）→ 是否补枚举（§5-95）②`LiveStartPushRequest`/`LiveStartPushReply` Javadoc 取值不全 + `url` 按 `url_type` 的格式说明缺失（§5-96）。另 `live_lens_change` 页表标 reply `data: null` 而 example 含 `result:0`（文档自身不一致，SDK `NoOutputReply` 正确处理）。**未写代码**（2026-10-14） |
| Dock1 媒体管理原文（A54） | 开发者提供 Dock1「媒体管理」页原文（页面日期 **2026-03-19**；含 2 events + 1 service + 1 request）。**4/4 方法已覆盖 ✓**：`HighestPriorityUploadFlighttaskMediaData`(`flight_id`)/`FileUploadCallbackData`(4 层嵌套)/`UploadFlighttaskMediaPrioritizeRequest`(`flight_id`)/`StorageConfigGetRequest`+`StorageConfigGetReply`；`EventMethod`/`ServiceMethod`/`RequestsMethod` 枚举齐全。**2 处差异**：①`StorageConfigGetReply.Credentials.expireTime`（SNAKE_CASE→`expire_time`）**≠** 文档 wire key `expire`（§5-97）②`FileUploadCallbackData.MediaFileMetadata.gimbalYawDegree` 文档 Type=`float` 但示例为字符串 `"-91.40"`，SDK `String`（§5-98）。**1 项可选**：`provider` enum_string{ali,aws,minio} 未建模（§5-99）。**未写代码**（2026-10-14） |
| Dock1 航线管理原文（A55） | 开发者提供 Dock1「航线管理」页原文（页面日期 **2025-11-12**；15 条指令：Events 5 + Services 9 + Requests 1）。**方法常量 15/15 存在 ✓**（`EventMethod`/`ServiceMethod`/`RequestsMethod`）；**多数 POJO 已覆盖 ✓**（`FlighttaskProgressData`/`FlighttaskReadyData`/`ReturnHomeInfoData`/`DeviceExitHomingNotifyData`/`FlighttaskPrepareRequest`(+`ReadyConditions`/`ExecutableConditions`/`BreakPoint`/`FlighttaskFile`/`SimulateMission`)/`FlighttaskExecute`/`FlighttaskUndo`/`FlighttaskResourceGet`）。**缺口/偏差 4 项**：①`flight_setup_exception_notify`（事件）SDK `EventMethod` 常量**已存在**但 Javadoc 记「data 含 `error_code`/`error_reason`」**与文档（`sn`/`timeout_time`/`timestamp`/`flight_type`）矛盾**、且**无 POJO**（§5-100）②`flight_setup_abort`（Dock1 独有 service）常量 ✓ 但 reply 错误码 `326108`/`326109` 未入 Javadoc（§5-101）③`simulate_mission` Dock1 页**仅 3 字段（无 `altitude`）** vs SDK 4 字段（`altitude` `@Inferred`）（§5-102）④Dock1 `flighttask_prepare` 页**无** `rth_mode`/`wayline_precision_type`/`flight_safety_advance_check`（§5-103）。**强化既有**：`flighttask_create`（废弃）三度印证 §5-56、`return_home` reply 三度印证 §5-16/§5-51、`device_exit_homing_notify.reason` enum_int vs 示例字符串（同 A41）。**未写代码**（2026-10-14） |
| Dock1 HMS 管理原文（A56） | 开发者提供 Dock1「HMS 管理」页原文（页面日期 **2025-03-19**；唯一 method `hms`，events up）。**与 A20（`dock3/hms.html`，同日期）内容逐字一致 → 重复页复核**：`EventMethod.HMS`(`hms`, need_reply=0 ✓) + [`HmsData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/alert/HmsData.java)（`list`→`Item`→`Args`）已覆盖 ✓。**无新增项**；**复认** §5-19（`HmsData.Item.code` SDK `Integer` vs 文档 `text`/示例 `"0x16100083"` → 待改 `String`）、§5-20（`hms_inner` **不补登**，结案）；`args.component_index/sensor_index` constraint 标 `length` 系文案变量说明残留，按 `int` 处理无需改动。**未写代码**（2026-10-14） |
| Dock1 远程调试原文（A57） | 开发者提供 Dock1「远程调试」页原文（页面日期 **2025-08-05**；Topic `thing/product/{gateway_sn}/events`+`events_reply` / `services`+`services_reply`；**12 个同名进度事件 + 21 条 services**）。**services 方法常量 21/21 存在 ✓**（`ServiceMethod`）；12 进度事件按既有约定（`EventMethod` Javadoc L38-40）**不重复登记**、data（`result`+`output{status,progress{percent,step_key?}}`）由 [`ServiceProgressData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/system/ServiceProgressData.java) 覆盖 ✓；services_reply `result`+`output{status}` 沿用 [`NoOutputReply`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/NoOutputReply.java) 约定 ✓（其 Javadoc L27-32 已声明 Job 类 `output.status=sent`、进度走 events）。**偏差 2 项**：①[`AirConditionerModeSwitchRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/debug/AirConditionerModeSwitchRequest.java) / [`BatteryStoreModeSwitchRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/debug/BatteryStoreModeSwitchRequest.java) 请求字段名 **`mode`≠文档 `action`**（§5-104，A57 系该 4 字段**首次获官方原文**）②[`ActionSwitchRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/ActionSwitchRequest.java) **全仓无引用（疑似死代码）**、`battery_maintenance_switch` 无专用请求 POJO（§5-105）；**可选（§5-106）**：12 事件 `step_key` 各方法值域未建模枚举。**复认**：§缺口2/§缺口4（A5）。**未写代码**（2026-10-14） |
| Dock1 固件升级原文（A58） | 开发者提供 Dock1「固件升级」页原文（页面日期 **2025-03-19**；Event `ota_progress` + Service `ota_create`+`services_reply`，Topic `thing/product/{gateway_sn}/…`）。**判定：A39（O4 地面站 `firmware.html`）的 Dock1 镜像** —— 字段/类型/示例逐字一致：`ota_progress`（`result`+`output{status,progress{percent,current_step}}`）由 [`OtaProgressData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/system/OtaProgressData.java) 覆盖 ✓、[`OtaCreateRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/firmware/OtaCreateRequest.java) 覆盖 ✓。**无新增缺口**，**复认 §5-48~51**。**唯一新证据（并入 §5-49）**：Dock1 页 `firmware_upgrade_type` 值域 **仅 `{2:一致性,3:普通}`（无 PSDK `4`）**，而 A39 O4 页为 `{2,3,4}` → 建议枚举取**并集 `{2,3,4}`**（Dock1 为子集）；另 `devices` 示例含 **2 设备**（`drone_sn`+`dock_sn`），第二设备**仅 3 字段**（`sn`/`product_version`/`firmware_upgrade_type`）→ 印证 `file_url`/`md5`/`file_size`/`file_name` **可选（null）** ✓。**未写代码**（2026-10-14） |
| Dock1 远程日志原文（A59） | 开发者提供 Dock1「远程日志」页原文（页面日期 **2025-03-19**；Event `fileupload_progress` + Service `fileupload_list`/`fileupload_start`/`fileupload_update`，各含 `services_reply`）。**判定：A40（O4 地面站 `log.html`）的 Dock1 镜像** —— 4 个 method 常量**全部存在 ✓**（`EventMethod.FILEUPLOAD_PROGRESS`（`need_reply=0` ✓）/ `ServiceMethod.FILEUPLOAD_LIST`/`FILEUPLOAD_START`/`FILEUPLOAD_UPDATE`）；[`FileUploadListRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/log/FileUploadListRequest.java)/[`FileUploadListReply`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/log/FileUploadListReply.java)/[`FileUploadUpdateRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/log/FileUploadUpdateRequest.java)/[`FileUploadStartRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/log/FileUploadStartRequest.java) 结构一致 ✓。**复认既有**：§4 的 `FileUploadProgressData` **确缺**（悬空 `@link`，A59 二次印证 → 仍待新建）、§5-52（`LogFile.startTime/endTime/size`、`Credentials.expire` 现 `long`/`Long` vs 文档 `int`；Dock1 页 `start_time`/`end_time`/`expire` 标 unit **秒/s** 但 example 均 13 位毫秒 → 自相矛盾）、§5-53（`module`/`module_list` 口径不一）、§5-54（`fileupload_progress` 表 vs example 冲突，**三次印证**）。**新差异 1 项（§5-107）**：Dock1 页 `fileupload_start` 字段集为 O4 页**子集**（缺 `file_store_dir`/`file_type`；`params.files[]` 缺 `device_sn`/`result`；`list[]` 缺 `start_time`/`end_time`/`size`）。**未写代码**（2026-10-14） |
| Dock1 配置更新原文（A60） | 开发者提供 Dock1「配置更新」页原文（页面日期 **2025-06-09**；Topic `thing/product/{gateway_sn}/requests`（up）+ `requests_reply`（down），method `config`）。**判定：A7（O4 Requests 获取配置 `config`）的 Dock1 镜像页**。**对照结论**：[`ConfigRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/registration/ConfigRequest.java)（`configType`/`configScope`）与页**完全一致 ✓**；[`ConfigReply`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/registration/ConfigReply.java)（`{result, appId, appLicense, url, token}`）与页**不符** → **需重建**（`url`→`ntp_server_host`、`token`→`ntp_server_port`（文档 `int`）、补 `app_key`/`ntp_server_port`；文档回复 **无 `result`**）。**A7 此前仅在 §3 记录、漏登记 §4/§5 → 本页补齐**（新增 §5-108 + §4 行）。**未写代码**（2026-10-14） |
| Dock1 指令飞行原文（A61） | 开发者提供 Dock1「指令飞行」页原文（页面日期 **2025-11-12**，最新；Topic `thing/product/{gateway_sn}/events`（up）/`services`（down）/`services_reply`（up）/`drc/down`/`drc/up`；**6 events + 32 services + 6 DRC**）。**method 全量覆盖 ✓**（`EventMethod` 6、`ServiceMethod` 32、`DrcMethod`/`DrcUpMethod` 6 均存在）；多数 POJO 已覆盖 ✓。**新结论 3 项**：①`drone_control`/`heart_beat.seq` **废弃口径跨页冲突**（Dock1 页 2025-11-12 视二者 active vs O4 页 A15 视废弃，§5-109）②`hsi_info_push` SDK Javadoc 偏差（`around_distances` 12→360、`up`/`down_distance` 米→毫米，§5-110）③`takeoff_to_point` Dock1 页为 O4（A10）页**子集**（§5-111）。**复认**：§5-102（simulate_mission 无 `altitude`）、§5-74（飞行 4 件套无前缀）、§4 `CameraPhotoTakeReply`（`camera_photo_take` 表有 `output.status` 但 example 仅 `result`）。**未写代码**（2026-10-14） |
| Dock1 AirSense 原文（A62） | 开发者提供 Dock1「AirSense」页原文（页面日期 **2025-03-19**；Topic `thing/product/{gateway_sn}/events`（up），唯一 method `airsense_warning`）。**判定：A30-1（O4 `airspace-safety.html`）的同一 `airsense_warning` 事件 Dock1 类目镜像** —— 10 字段（`icao`/`warning_level`/`latitude`/`longitude`/`altitude`/`altitude_type`/`heading`/`relative_altitude`/`vert_trend`/`distance`）、类型、`data` 为数组、example `need_reply=1` **逐字一致**。**无新增字段/口径结论**（已被 A30-1 覆盖并经 §5-11 结案）：`EventMethod.AIRSENSE_WARNING`(need_reply=1 ✓) + [`AirSenseWarningData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/alert/AirSenseWarningData.java)（自定义反序列化器按 `List<Alert>` 处理 ✓）已覆盖；本页 `warning_level` 完整 5 值 `{0:无危险,1:等级一,2:等级二,3:等级三,4:等级四}` ✓。**新发现 1 项（元数据，§5-112）**：`@DocUrl` 现指 `dock1/wayline.html`，但 A55 确证该页（航线管理）15 指令**不含** `airsense_warning` → **归属待核**（本页才是归属页，URL 待提供）。**复认 §5-11**（`Alert.altitude`/`relativeAltitude`/`distance` `Double`→`Integer`、类 Javadoc `need_reply` 0→1）。**未写代码**（2026-10-14） |
| Dock1 自定义飞行区原文（A63） | 开发者提供 Dock1「自定义飞行区」页原文（页面日期 **2025-03-19**；Topic `thing/product/{gateway_sn}/events`（up）/ `services`（down）/ `requests`（up）；含 **2 events + 1 service + 1 request**）。**method 存在性**：`EventMethod.FLIGHT_AREAS_SYNC_PROGRESS`(need_reply=1 ✓) / `FLIGHT_AREAS_DRONE_LOCATION`(need_reply=0 ✓) / `RequestsMethod.FLIGHT_AREAS_GET` ✓ 均存在；**`ServiceMethod.FLIGHT_AREAS_UPDATE` 缺失 ❌**（新登记 §5-113，并更正 §5-76「仓库独有」误记——本页为官方原文）。**POJO 对照**：`FlightAreasSyncProgressData`（status/reason/file{name,checksum}）**与文档一致 ✓**；`FlightAreasDroneLocationData`（droneLocations[].areaDistance/areaId/isInArea）**一致 ✓**；`FlightAreasGetRequest`（空 record，data=null）**可承载**；**`FlightAreasGetReply` 口径偏差 ❌**——SDK `output.file`（**单数** struct `{name,checksum}`）vs 文档 `output.files`（**数组**，项 `{name,url,checksum,size}`）（新登记 §5-114，**breaking**）。**元数据 1 项（§5-115）**：4 个 flight-area POJO `@DocUrl` 现指 `dock3/wayline.html`，但 A41 确证该页**不含** flight_areas → 归属待核。**规范 1 项（§5-116）**：`FlightAreasGetRequest` 专用空 record 未复用 `NoParameterRequest` → 待裁决。**复认**：§5-76（`flight_areas_update` 移出「仓库独有」）。**未写代码**（2026-10-14） |
| Dock1 PSDK 功能原文（A64） | 开发者提供 Dock1「PSDK 功能」页原文（页面日期 **2025-03-19**；Topic `thing/product/{gateway_sn}/events`（up）/ `services`（down）/ `requests`（up）；含 **4 events + 8 services + 1 request**）。**method 存在性：全部 13 项已覆盖 ✓**（events：`PSDK_UI_RESOURCE_UPLOAD_RESULT`/`PSDK_FLOATING_WINDOW_TEXT`/`SPEAKER_AUDIO_PLAY_START_PROGRESS`/`SPEAKER_TTS_PLAY_START_PROGRESS`；services：`PSDK_WIDGET_VALUE_SET`/`PSDK_INPUT_BOX_TEXT_SET`/`SPEAKER_AUDIO_PLAY_START`/`SPEAKER_TTS_PLAY_START`/`SPEAKER_REPLAY`/`SPEAKER_PLAY_STOP`/`SPEAKER_PLAY_MODE_SET`/`SPEAKER_PLAY_VOLUME_SET`；requests：`STORAGE_CONFIG_GET`）。**判定：本页为已审计 Dock3「PSDK 功能（psdk.html）」页的 Dock1 镜像**——字段结构一致 ✓，无缺失 method。**新登记 2 项（元数据/口径，非阻断）**：**§5-117**（`storage_config_get.module` 语义：本页 `{0 媒体, 1 psdk ui 资源}` vs SDK Javadoc `1=日志`）；**§5-118**（`speaker_*` 事件 3 类 POJO `@DocUrl` 现指 `dock3/wayline.html`，与 services 侧 `dock3/psdk.html` 不一致 → 待核）。**复认**：§5-52/§5-97（`expire` 字段名/类型）、§5-99（`provider` 未枚举）、§5-106（`speaker.progress.step_key` 未枚举）、A6 文档自相矛盾（tts example `status="success"` 不在枚举 `{in_progress,ok}`）。**未写代码**（2026-10-14） |
| Dock1 PSDK 互联互通原文（A65） | 开发者提供 Dock1「PSDK 互联互通」页原文（页面日期 **2025-03-19**；Topic `thing/product/{gateway_sn}/events`（up）/ `services`（down）；含 **1 event + 1 service**）。**method 存在性：2/2 已覆盖 ✓**（event `EventMethod.CUSTOM_DATA_TRANSMISSION_FROM_PSDK`（need_reply=0 `@Inferred`）/ service `ServiceMethod.CUSTOM_DATA_TRANSMISSION_TO_PSDK`）；**字段一致 ✓**（`value(text, 长度<256)`；service reply `{result(int, 非0错误)}` → `NoOutputReply`）。**首次官方原文确认**这两个 method（报告此前**无任何附录覆盖**）。**新登记 1 项（元数据，§5-119）**：[`CustomDataTransmissionToPsdkRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/psdk/CustomDataTransmissionToPsdkRequest.java) 的 `@Verified(basis)` 误称「DJI 文档未显式列出此方法」（**本页为官方原文，该声明不成立**）+ `@DocUrl` 应为 `psdk-transmit-custom-data.html`。**未写代码**（2026-10-14） |
| Dock1 ESDK 互联互通原文（A66） | 开发者提供 Dock1「ESDK 互联互通」页原文（页面日期 **2025-03-19**；Topic `thing/product/{gateway_sn}/events`（up）/ `services`（down）；含 **1 event + 1 service**）。**method 存在性：2/2 已覆盖 ✓**（event `EventMethod.CUSTOM_DATA_TRANSMISSION_FROM_ESDK`（need_reply=0 `@Inferred`）/ service `ServiceMethod.CUSTOM_DATA_TRANSMISSION_TO_ESDK`）；**字段一致 ✓**（`value(text, <256)`；service reply `{result(int, 非0错误)}` → `NoOutputReply`）。**首次官方原文确认**这两个 method（报告此前无覆盖）。**新登记 1 项（元数据，§5-120）**：[`CustomDataTransmissionToEsdkRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/esdk/CustomDataTransmissionToEsdkRequest.java) 的 `@Verified(basis)` 误称「**DJI 文档访问受限**（cookie 验证）」（**本页为官方原文，该声明不成立**）+ `@DocUrl` 归属核对。**未写代码**（2026-10-14） |
| Dock1 远程解禁原文（A67） | 开发者提供 Dock1「远程解禁」页原文（页面日期 **2025-03-19**；Topic `thing/product/{gateway_sn}/services`（down）/ `services_reply`（up）；含 **3 services**：`unlock_license_switch`/`unlock_license_update`/`unlock_license_list`）。**判定：A21（Dock3 `flysafe.html` 解禁证书 3 项）的 Dock1 类目镜像**——3 个 method 均存在 ✓；3 个**请求** POJO 均一致 ✓（[`UnlockLicenseSwitchRequest{licenseId, enable}`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/flysafe/UnlockLicenseSwitchRequest.java) / [`UnlockLicenseListRequest{deviceModelDomain}`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/flysafe/UnlockLicenseListRequest.java) / [`UnlockLicenseUpdateRequest{LicenseFile{url, fingerprint}}`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/flysafe/UnlockLicenseUpdateRequest.java)）；**回复 POJO 仍缺失（A21 缺口 1+2）**——`UnlockLicenseListReply`+`License`+`CommonFields`+7 类 unlock 子 record + `UnlockLicenseSwitchReply`（共 12 类）**及 `FlatReplyEnvelope` 均未创建**（§4 L317–319 仍为**计划项**，受「原文收齐前不编码」门禁）。**本页二次确证 §5-21/§5-22 决策**（扁平 `services_reply` data；example 含 `gateway:""`）。**未写代码**（2026-10-14） |
| Dock1 航线管理原文投喂（A68） | 开发者投喂 Dock1「航线管理」页原文（页面日期 **2025-11-12**）→ **判定与 A55 同页，逐字复核，新增差异 0**。15 指令（Events 5 + Services 9 + Requests 1）方法常量 **15/15 ✓**、POJO 13/15 ✓（例外同 A55）；**A55 来源由「AI 直接审计官方页」升级为「开发者投喂原文逐字复核」**；复认 §5-100~103 / §5-56 / §5-16 / §5-51（均**无新增项**）。**未写代码**（2026-10-14） |
| Dock1 自定义飞行区原文投喂（A69） | 开发者投喂 Dock1「自定义飞行区」页原文（页面日期 **2025-03-19**）→ **判定与 A63 同页，逐字复核，新增差异 0**。2 events ✓ / 1 service（`flight_areas_update` **仍缺失**）/ 1 request（`flight_areas_get` method+请求 ✓，回复形态偏差）；复认 §5-113~116（均**无新增项**）。**未写代码**（2026-10-14） |
| Dock3 媒体管理原文投喂（A70） | 开发者投喂 Dock3「媒体管理」页原文（页面日期 **2026-08-19**）→ **判定与 A35（`dock3/media.html`）/A54（Dock1 媒体管理）同源，逐字复核，新增差异 0**。4 指令（2 events + 1 service + 1 request）方法常量 **4/4 ✓**、POJO 全覆盖 ✓、字段/example 一致 ✓；页面日期较 A35/A54（2026-03-19）**更新至 2026-08-19，内容未变**。复认 §5-97（`expireTime`→`expire`）/§5-98（`gimbalYawDegree` Type vs 示例）/§5-99（`provider` 未枚举）/§5-117（`module` 仅 `{0 媒体}`，未定义 `1`）。**未写代码**（2026-10-14） |
| Dock3 PSDK 功能原文投喂（A71） | 开发者投喂 Dock3「PSDK 功能」页原文（页面日期 **2026-08-19**）→ **内容与 A64（Dock1 PSDK 功能，2025-03-19）逐字一致 → 跨 Dock 镜像复核，新增差异 0**。4 events + 8 services + 1 request 方法常量 **13/13 ✓**、POJO 全覆盖 ✓。**并更正 A64 批次性质**：A64 称「已审计 Dock3 `psdk.html` 页」，但 §8 无对应独立附录 → **A71 为 Dock3 `psdk.html` 首个独立原文记录**。**§5-118 获新证据**：`speaker_*` 事件确在本「PSDK 功能」页原文内（非 `wayline.html`）→ 支撑其 `@DocUrl` 应为 `psdk.html` 的推测。**未写代码**（2026-10-14） |
| Dock3 PSDK 互联互通原文投喂（A72） | 开发者投喂 Dock3「PSDK 互联互通」页原文（页面日期 **2025-03-19**）→ **内容与 A65（Dock1 PSDK 互联互通，2025-03-19）逐字一致 → 跨 Dock 镜像复核，新增差异 0**。1 event + 1 service 方法常量 **2/2 ✓**、字段一致 ✓（`value(text <256)`；service reply `{result}` → `NoOutputReply`）。**A72 为 Dock3 `psdk-transmit-custom-data.html` 首个独立原文记录**；**§5-119 获新证据**（本页确为 `custom_data_transmission_*` 的 Dock3 归属页）→ 佐证其目标 `@DocUrl` 正确。**未写代码**（2026-10-14） |
| Dock3 ESDK 互联互通原文投喂（A73） | 开发者投喂 Dock3「ESDK 互联互通」页原文（页面日期 **2025-03-19**）→ **内容与 A66（Dock1 ESDK 互联互通，2025-03-19）逐字一致 → 跨 Dock 镜像复核，新增差异 0**。1 event + 1 service 方法常量 **2/2 ✓**、字段一致 ✓（`value(text <256)`；service reply `{result}` → `NoOutputReply`）。**A73 为 Dock3 `esdk-transmit-custom-data.html` 首个独立原文记录**；**§5-120 获新证据**（本页确为 ESDK `custom_data_transmission_*` 的 Dock3 归属页）→ 佐证其目标 `@DocUrl` 正确。**未写代码**（2026-10-14） |
| Dock1 AirSense 原文投喂复核（A74） | 开发者**重复投喂** Dock1「AirSense」页原文（页面日期 **2025-03-19**）→ **与 A62 同页，逐字一致 → 重复页复核，新增差异 0**。`airsense_warning` **1/1 ✓**（`EventMethod.AIRSENSE_WARNING` need_reply=1）、10 字段一致 ✓、`warning_level` 5 值完整 ✓。**§5-112 未解除**：本页为 `airsense_warning` 归属页，但**本次未含 URL** → [`AirSenseWarningData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/alert/AirSenseWarningData.java) `@DocUrl`（现误指 `dock1/wayline.html`）更正**仍待 URL**。**未写代码**（2026-10-14） |
| Dock1 设备属性原文投喂复核（A75） | 开发者以文件**重复投喂** Dock1「设备属性」页原文（页面日期 **2025-03-19**，410 行）→ **与 A50 同页、内容一致 → 重复页复核，新增差异 0**。复认 §5-83（`electric_supply_voltage` 页 `int`/V）、§5-84（`drone_authority_info` 在页）、§5-88（页日期仍 2025-03-19）。**§5-88 未解除**：只能证明所持副本未变，**不能证明 DJI 现行页未更新** → 仍需开发者确认（内容无需再投）。**未写代码**（2026-10-14） |
| M4D/M4TD 设备属性原文投喂复核（A76） | 开发者以文件投喂 M4D/M4TD「设备属性」页原文（页面日期 **2026-09-01**，225 行 / 56 顶层属性）→ **与 A49 同页、内容一致**（来源由「AI 直接审计」升级为「开发者投喂原文」）。**新增差异 0**；**新增证据 1 项**：`commander_mode_lost_action` **值域 `{0:继续执行指点飞行任务, 1:退出指点飞行任务，执行普通失控行为}`（0-1）** → **§5-80 原文前提解除**；另核实 `commander_flight_height`（float 米/m `{max:3000,min:2,step:0.1}`、rw、pushMode=1）、`commander_flight_mode`（SDK 已存在 ✓）。**未写代码**（2026-10-14） |
| §5-88 裁决（2026-10-14） | **开发者确认**：Dock1「设备属性」页（页面日期 **2025-03-19**，A50/A75）**即 v1.18 现行版**（DJI 未更新该页）→ **§5-88 关闭**（内容无需再投）。**推论**：§5-83（`electric_supply_voltage` Dock1 页 `int` / O4 页 `double` / SDK `Double`）为**真实跨页口径冲突**（非过期页假象）→ 其裁决独立保留。**未写代码**（2026-10-14） |
| Dock3 自定义飞行区原文投喂复核（A77） | 开发者投喂 Dock3「自定义飞行区」页原文（页面日期 **2025-03-19**）→ **与 A63（Dock1 自定义飞行区，2025-03-19）逐字一致 → 跨 Dock 镜像复核，新增差异 0**。2 events ✓ + 1 request（method ✓、reply 偏差 §5-114）+ 1 service（`flight_areas_update` 缺 §5-113）。**§5-115 追加证据**：4 方法与 POJO 归属页在 **Dock1/Dock3 两侧均已获原文**（确证应指向「自定义飞行区」页，非 `dock3/wayline.html`）；**但两页 URL 仍未提供 → §5-115 未解除**。**未写代码**（2026-10-14） |
| Dock1 AirSense 原文投喂复核（A78） | 开发者**第 3 次投喂** Dock1「AirSense」页原文（页面日期 **2025-03-19**）→ **与 A62/A74 逐字一致 → 重复页复核，新增差异 0**（`airsense_warning` 1/1 ✓、10 字段 ✓）。**§5-112 仍未解除**：内容已三次一致，**唯一缺口 = 该页 URL**（内容不能替代 URL）→ 待开发者给出 **URL**。**未写代码**（2026-10-14） |
| §5-112 裁决（2026-10-14） | **开发者提供 URL**：Dock1「AirSense」页 = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/airsense.html`（前缀与现 `.../dock1/wayline.html` 一致，**仅文件名差异**）→ **§5-112 关闭**：[`AirSenseWarningData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/alert/AirSenseWarningData.java#L55) `@DocUrl` 更正目标 = `.../dock1/airsense.html`；A55 已确证 wayline 页（航线管理）不含 `airsense_warning` → **归属页确认**。**Java 更正待统一落地**（遵循「原文收齐前不编码」）。**未写代码**（2026-10-14） |
| §5-115 裁决（2026-10-14） | **开发者提供 URL**：Dock3「自定义飞行区」页 = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/custom-fly-region.html` → **§5-115 关闭**：4 类 flight-area POJO（`FlightAreasSyncProgressData`/`FlightAreasDroneLocationData`/`FlightAreasGetRequest`/`FlightAreasGetReply`）`@DocUrl` 更正目标 = `.../dock/dock3/custom-fly-region.html`（现 `.../dock3/wayline.html`）；`@Verified(basis)` 由 simulator 升为官方文档；A41 已确证 wayline 页不含 flight_areas 内容 → **归属页确认**。**Java 更正待统一落地**（「原文收齐前不编码」）。**未写代码**（2026-10-14） |
| §5-118 裁决（2026-10-14） | **开发者提供 URL**：Dock3「PSDK 功能」页 = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/psdk.html` → **§5-118 关闭**：3 类 speaker 事件 POJO（`SpeakerOutput`/`SpeakerTtsPlayStartProgressData`/`SpeakerAudioPlayStartProgressData`）`@DocUrl` 更正目标 = `.../dock3/psdk.html`（现 `.../dock3/wayline.html`，与 services 侧 `psdk.html` 一致）。**Java 更正待统一落地**。**未写代码**（2026-10-14） |
| §5-119 裁决（2026-10-14） | **开发者提供 URL**：Dock3「PSDK 互联互通」页 = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/psdk-transmit-custom-data.html` → **§5-119 关闭**：[`CustomDataTransmissionToPsdkRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/psdk/CustomDataTransmissionToPsdkRequest.java) `@DocUrl` 更正目标 = `.../dock3/psdk-transmit-custom-data.html`（现 `.../dock3/psdk.html`）；`@Verified(basis)` 删「DJI 文档未显式列出」→ 改「DJI 官方文档显式列出（Dock3 psdk-transmit-custom-data.html）」。**Java 更正待统一落地**。**未写代码**（2026-10-14） |
| §5-120 裁决（2026-10-14） | **开发者提供 URL**：Dock3「ESDK 互联互通」页 = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/esdk-transmit-custom-data.html` → **§5-120 关闭**：[`CustomDataTransmissionToEsdkRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/esdk/CustomDataTransmissionToEsdkRequest.java) `@DocUrl` 更正目标 = `.../dock3/esdk-transmit-custom-data.html`（现 `.../dock3/esdk.html`）；`@Verified(basis)` 删「DJI 文档访问受限」→ 改「DJI 官方文档显式列出（Dock3 esdk-transmit-custom-data.html）」。**Java 更正待统一落地**。**未写代码**（2026-10-14） |
| §5-74 遗留结案（2026-10-14，A79） | 开发者投喂 **Pilot 上云「RC Pro 行业版 远程控制」页**原文（URL = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/rc-pro/remote-control.html`）→ 该页 3 项 DRC 方法 `drc_drone_state_push`/`drc_camera_osd_info_push`/`drc_initial_state_subscribe` **均带 `drc_` 前缀** → **Pilot 上云 DRC 通道前缀口径与 Dock 一致**，§5-74「Pilot 侧待核」遗留**结案**；方案 A（采纳前缀）对 Pilot 侧同样成立。**新增差异 0**（A14/A16 已覆盖）。**未写代码**（2026-10-14） |
| RC Plus 2 行业版「远程控制」页原文投喂复核（A80） | 开发者投喂 Pilot 上云「RC Plus 2 行业版 远程控制」页原文（URL = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/remote-control.html`，页面日期 **2026-10-13 = v1.18 现行版**，1902 行）→ 含 **4 Event** + **38 Service**。逐项比对 → **全部已覆盖 ✓（新增差异 0）**：4 Event 全在 `DrcUpMethod`；38 Service 全在 `DrcMethod`/§4（16 项既有 + 22 项已由 A15–A18/A25/A38 登记）。**关键：§5-74 全量印证**——本页 22 个相机/红外 DRC 方法**全部带 `drc_` 前缀**（非仅 A79 的 3 项）→ 方案 A「DRC 前缀常量 + services 无前缀常量并存」在 Pilot 侧获全量证据。**防重复登记**：此前疑为「SDK 缺 22 项」实为「§4 已登记待补」。**未写代码**（2026-10-14） |
| 三页「设备管理」原文投喂复核 + §5-89 结案（A81） | 开发者一次性投喂 **3 页设备管理**原文（Dock1 `dock1/device.html` + RC Pro `rc-pro/device.html` + RC Plus 2 `dji-rc-plus-2/device.html`，均 2025-03-19）→ **§5-89 结案**：三页 `status_reply` data **均仅 `{result}`、皆无 `sub_type`** → SDK `UpdateTopoReplyData.subType` 的 `@Inferred` 依据**不成立**（且「0=上线/1=下线」与 UP data 的 `sub_type`=产品子类型语义矛盾）→ **裁决：删除 `subType`（breaking）**。§5-90/§5-91 加档（三页 UP data 均无 `base_info` → 确证 O4 专属）。**新发现 §5-121**：RC Plus 2 页 `update_topo` topic = `thing/product/...`（另两页 `sys/product/...`）+ 顶层缺 `domain`（SDK `requireNonNull` 冲突）。**未写代码**（2026-10-14） |
| RC Pro 行业版「设备属性」页原文投喂复核（A82） | 开发者投喂 RC Pro「设备属性」页原文（`rc-pro/properties.html`，2025-09-03，osd pushMode=0 + state pushMode=1）+ RC Pro「设备管理」页（`rc-pro/device.html`，重复 A81）→ **新增差异 1**：文档 osd = **`capacity_percent`**，SDK `RcOsd` 字段名 **`battery`**（wire `battery`）→ **wire key 不符，电量反序列化丢失**（→ §5-122）；另 `RcOsd.modeCode` 本页未列、`RcOsd.@DocUrl`（`dji-rc-plus-2/properties.html`）与 Javadoc 参考链接（`remote-controller/properties.html`）不一致；`StateField` 中 live_capacity/dongle_infos/live_status/firmware_version 未注 RC 归属（→ §5-123）。其余（latitude/longitude/country/height/wireless_link/drc_state/dongle_infos/DongleType/live_*/cloud_control_auth）**已覆盖 ✓**。**未写代码**（2026-10-14） |
| RC Plus 2「设备管理」页完整版复核 + §5-121 结案（A83） | 开发者澄清本次投喂归属（标注「设备属性」实为「**设备管理**」）→ 投喂 RC Plus 2「设备管理」页**完整版**原文：topic = **`sys/product/{gateway_sn}/status`**、UP data 顶层 **7 字段（含 `domain`）**、`sub_devices[]` **8 字段（含 `domain`/`index`）**、`status_reply` 仅 `{result}` → **与 Dock1/RC Pro 完全一致** → **§5-121 结案（撤销）**：A81 记载的 `thing/product` + 缺 `domain` 系该页旧版/转录差异，非现行口径 → SDK `UpdateTopoData.domain` 必填、`sys/product` topic 均正确，**无 SDK 改动**。§5-89 依据再确认。**RC Plus 2「设备属性」页仍未获**（§5-122 待投喂）。**未写代码**（2026-10-14） |
| RC Plus 2 行业版「设备属性」页原文投喂复核 + §5-122 裁决（A84） | 开发者投喂 RC Plus 2「设备属性」页原文（`dji-rc-plus-2/properties.html`，2025-09-03）→ **与 RC Pro「设备属性」页逐字一致**（osd：country/capacity_percent/height/wireless_link/latitude/longitude/drc_state；state：live_capacity/dongle_infos/live_status/firmware_version/cloud_control_auth；**两页均无 `mode_code`**）→ **§5-122-①裁决：`RcOsd.battery` → `capacityPercent`（对齐 `capacity_percent`，breaking）**；③Javadoc 参考链接更正为 `dji-rc-plus-2/properties.html`；②`RcOsd.modeCode` 两页均无 → **仍待确认**。**§5-123 结案**（Javadoc 补 RC 归属）。**未写代码**（2026-10-14） |
| 「负载控制—切换相机模式」页 + RC Pro / RC Plus 2「远程控制」页重复复核（A85） | 开发者投喂三页原文：① RC Pro「远程控制」页全文（`.../rc-pro/remote-control.html`，2025-03-19）= **重复 A79**；② RC Plus 2「远程控制」页（文件《远程控制\_\_2026….txt》，`.../dji-rc-plus-2/remote-control.html`）= **重复 A80**；③ **「负载控制—切换相机模式」段**（文件，875 行；**开发者确认 = ② 同页锚点** `#负载控制—切换相机模式`，topic `thing/product/{gateway_sn}/drc/down` + `drc/up`，含 **20 个 `drc_camera_*` / `drc_ir_metering_*` / `drc_gimbal_reset` 方法**）→ 逐项比对：③ 20 方法**全部已在 §4 登记**（A15–A18/A25/A38 的「DRC 侧缺失」缺口）→ **新增差异 0**；**§5-74 再次印证**（20/20 全带 `drc_` 前缀，DRC 版 data 与 services 版一致）。**无新增 §5 项**。**未写代码**（2026-10-14） |
| 通用遥控器「设备属性」页原文投喂复核（A86） | 开发者投喂 DJI 通用遥控器设备属性页（`https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/others/rc/properties.html`，2025-07-23）→ 逐字比对 A82/A84（RC Pro / RC Plus 2 属性页）**顶层字段差异 0**（同源通用页）；**§5-122-② 证据闭环**：本页 osd/state 亦无 `mode_code` → 三页（RC Pro / RC Plus 2 / 通用 RC）全部无 → `RcOsd.modeCode` **无任何官方依据 → 建议裁决删除**（breaking）；**§5-122-③ URL 落实**：`RcOsd` 类 Javadoc 参考链接更正目标 = `.../others/rc/properties.html`（原写 `remote-controller/properties.html` 系**错误路径**），`@DocUrl` 统一目标待裁决；新增 **§5-124**（`dongle_infos` 5 子枚举未建模，非阻断）。**未写代码**（2026-10-14） |

| §9 待裁决清单**全批采纳**（2026-10-14） | 开发者裁决「按建议全批」→ **§9.1–§9.7 全部 62 项均按「建议默认」执行**（含 breaking：wire key/字段名/类型修正、协议缺口补充、可选枚举化、机型超集维持、DJI 仓库差集暂不纳入、新增功能暂不纳入）。**下一步按 TDD 统一落地**（`tdd-test-cases.md` → `architecture-design.md` → 代码 → `mvn test`），随 `1.18.0.0` 发布。**未写代码**（裁决记录） |

---

## 1. 版本判定

官方发布记录顺序为 **v1.18 → v1.16.1**（**不存在 v1.17**），本 SDK 当前对齐 v1.16.1，故升级基线为 v1.16.1 → v1.18。

- v1.18 发布日期：2026.10.13
- 版本号：`1.16.1.3` → `1.18.0.0`（前三段跟随协议版本递增，SDK 修复版本号归零，见 AGENTS.md §2 版本号规则）

v1.18 发布列表共 **3 类功能变更**（拆分为 5 个协议面）：

| # | 发布项 | 涉及文档页面 |
|---|---|---|
| 1 | 新增设备 O4 地面站（漫游基站模式） | `ground-station/o4/*`（properties / device / cmd / firmware / log / airspace-safety） |
| 2 | 大疆机场 3 飞行终止系统（FTS） | `dock/dock3/fts.html` |
| 3 | 机场 2 / 机场 3 设置相机录像清晰度与照片分辨率 | `dock/dock2/remote-control.html`、`dock/dock3/remote-control.html` |

---

## 2. 变更总览

| 协议面 | SDK 现状 | 需改动 |
|---|---|---|
| O4 设备标识 | `DeviceDomain` 无 domain=8；无地面站型号枚举 | 新增 domain + 型号枚举 |
| O4 属性/状态 | 多数新字段缺失，部分可复用 | 新增 OSD/State 字段 + 嵌套 struct + 新枚举 |
| O4 子设备 | `update_topo` 已存在 | 补 `base_info.country_code` |
| O4 远程调试 | 除 `low_power_switch` 外均已存在 | 新增 1 method + 重写 `RtkCalibrationRequest` + module 枚举扩展 |
| O4 固件升级 | `ota_create`/`ota_progress` 已存在；`ota_progress` 与文档一致，`ota_create` 的 `file_size`/`firmware_upgrade_type` 与 reply 有偏差（A39） | 复用 POJO；修正 `file_size` 类型 + 补 `FirmwareUpgradeType` 枚举 + reply 待裁决；升级 `@Verified`（§5-48~51） |
| O4 日志管理 | 4 method 存在；`FileUploadListReply`/`List/Update Request` 结构一致；`FileUploadStartRequest` 缺 `file_store_dir`/`file_type`/`params.files[]` 子字段；`FileUploadProgressData` 缺失（A40） | 扩展 `FileUploadStartRequest`，新增 `FileUploadProgressData`；核对 `long`/`Long` 类型与 `module` 口径（§5-52~54） |
| O4 空域安全 | `airsense_warning` 已存在 | 新增 `rid_infos`、`adsb_infos` + `Gnss` |
| Dock3 FTS | 完全缺失 | 新增 2 method + POJO + 枚举 |
| 相机分辨率 | `drc_video_resolution_set` 已存在（`@Inferred`） | 升级 `@Verified`；新增 `drc_camera_photo_size_set` + 枚举 |
| Dock3 DRC AI 框选坐标 | `AiSpotlightZoomSelectRequest` 四字段为 `Double`（旧 basis `{max:1,min:0}`×10000） | 改 `Integer`（文档 Type=int、`[0,10000]`）（§5-45 已决策） |
| Dock2 DRC 事件 POJO（`drc_camera_osd_info_push`） | `CameraOsdInfoPushData.IrLense` 9 字段、**无**调色盘集合；`irZoomFactor` 为 `int` | **待确认**：补 `thermalSupportedPaletteStyles`（`array<enum_int>`）+ `irZoomFactor` 改 `Double`（§5-46 / §5-47，A38 Dock2 页二次印证） |
| Dock3 航线管理（`wayline.html`，补充 A19 截断段） | `multi_dock_task` 子结构缺口（`DockInfo` 缺 4 字段/`CenterNode` 缺 `sdrId`/`LeafNode` 空/`RtcmInfo` 缺失）；`flighttask_create`（废弃）缺失；`FlighttaskProgressGetRequest` 多必填 `flightId`；`FlighttaskProgressGetReply.output` 为 `Object` | 补 `dock_infos`/`RtcmInfo`/`LeafNode` 字段；裁决 `flighttask_create`；修 `flighttask_progress_get` 请求/回复；批量 `@Verified` 升官方依据（§5-55~58，A41） |
| Dock2 航线管理（`wayline.html`） | 与 Dock3 页绝大多数一致（Event 5/Service 14/Requests 2，方法常量与 POJO 已覆盖）；**差异点**：`flighttask_progress_get` 请求 data 为 `target_sn`+`flight_id`（Dock3 页仅 `sn`）；`alternate_land_point` 字段表含 `height`（Dock3 页漏列） | **待确认**：`FlighttaskProgressGetRequest` 的 `sn` → `targetSn`（对齐 Dock2 wire key，避免反序列化丢失）或兼容；`flighttask_progress_get` 修正如 §5-57/§5-59（A42） |
| 支持产品列表与固件版本（`overview/product-support.html`） | **§5-12 结案**：机场上云产品族与 `DeviceCompatibility` 搭配**吻合**（机场3↔M4D/4TD；机场2↔M3D/3TD+M30/30T；机场↔M30/30T）→ **Dock↔Drone 无需新增** ✓；**新增决策点**：①「最低固件版本」是否纳入 SDK（§5-60）②`RC PRO` 值域是否扩展（§5-61） | **待裁决**：`DeviceCompatibility` 是否补「最低固件版本」常量/查询；`@Verified` basis 可具体化为本页来源（A43） |
| WPML 共用元素（`dji-wpml/common-element.html`） | WPML `wayline` 模型与文档**逐一吻合**（全部元素组有对应类、字段数/类型一致，无缺失）→ **无改动** ✓；仅 2 项记录（§5-62 布尔字段 int 口径、§5-63 枚举超集） | **无待办**（A44：确认完整） |
| WPML template.kml（`dji-wpml/template-kml.html`） | `wayline` 模型与文档比对：**多数一致**（`Document`/`MissionConfig`/`WaylineCoordinateSysParam`/`Overlap`/`MappingHeadingParam`/`Mapping3dPlacemark`/枚举 `HeightMode`/`PositioningType`/`CoordinateMode` 均吻合）；**4 项差异待落地**：①`Placemark` 缺 `isRisky`/`useStraightLine`②`Mapping2dPlacemark` 缺 `quickOrthoMappingEnable`/`quickOrthoMappingPitch`（M4E）③`MappingStripPlacemark.margin` `int`→`double`④`Folder`（航点）缺 `payloadParam`（`MappingFolder` 已含 ✓，层级待确认） | **待确认**：补上述字段；`isRisky`/`useStraightLine`/`quickOrthoMappingEnable` 按 WPML 布尔口径用 `Integer`（§5-62）；`quickOrthoMapping*` 仅 M4E，用包装类型 + NON_NULL；属 **breaking**，随 `1.18.0.0` 发布（A45，§5-64~67） |
| WPML waylines.wpml（`dji-wpml/waylines-wpml.html`） | `execute/` 模型与文档比对：**多数一致**（`ExecuteFolder` 4 字段 + `ExecutePlacemark` 6 字段 + `ExecuteHeightMode` 3 值吻合）；**4 项差异待落地**：①`ExecuteFolder` 缺 `startActionGroup`②`ExecutePlacemark` 缺 `isRisky`/`useStraightLine`（与 §5-64 同源）③**多 Folder** 支持（`Kml<ExecuteFolder>` 单数 vs 倾斜摄影 5 Folder）④`gimbalPitchAngle` 层级待确认 | **待确认**：补 `startActionGroup`（复用 `ActionGroup`）、`isRisky`/`useStraightLine`（`Integer`，按 §5-62）；裁决多 Folder 设计；属 **breaking**，随 `1.18.0.0` 发布（A46，§5-68~71） |
| **§5-70 裁决** | waylines.wpml 多 `Folder` 采用 **方案 B**：新增 `WaylinesDocument(missionConfig, List<ExecuteFolder>)` + 根 `WaylinesWpml`；`ParsedKmz.waylines` → `WaylinesWpml`；`WpmlCodec.parseWaylinesWpml` 返回类型同步变更；template 侧 `Kml<Folder>`/`Document<T>` **零改动**（A46/§5-70，2026-10-14） |
| DJI Cloud-API-2 协议分类清单 + DRC 命名差异样例（A47） | 分类清单已产出：`generated/` 475 文件（**drc 227 / services 150 / events 73 / requests 22 / status 3**）+ `custom/` 6 组（osd/state/set_property/compressed_sticks_control/airsense_warning）；DRC 命名差异 6 类样例已产出（通道前缀 / 方向后缀 / 回包 / 类名 / 字段名 / 类型） | **§5-74 已裁决（2026-10-14）：采纳方案 A（DRC 通道保留 `drc_` 前缀）**；**待裁决（§5-72/73/75，非阻断）**：①`compressed_sticks_control` 方法 SDK 缺失②DJI 仓库独有 DRC 方法（105 下行 vs SDK 42 / 18 推送 vs SDK 12）是否纳入③AI 框选坐标 DJI 实现 `double` vs 文档 `int`（维持 §5-45 `Integer`）；**均未写代码**（A47，2026-10-14） |
| DJI Cloud-API-2 分类命名差异样例（续：services/events/requests/status/custom，A48） | 已产出同构样例：分类总表 + 各分类代表性样例 + 分类特有结论；计数校验 services 150（75+75）/ events 73（38+35）/ requests 22（11+11）/ status 3 / custom 9 | **新增待确认（§5-76，非阻断）**：非 DRC 分类的「仓库↔SDK 覆盖差集」（services ~35 项 / requests DJI 独有 7 项 / status `static_topo` / events 若干，仅见于仓库、未见于 v1.18 文档）；**未写代码**（A48，2026-10-14） |
| M4D/M4TD 飞行器设备属性（`aircraft/m4d-properties.html`，A49） | SDK `DroneOsd`/`OsdField`/`StateField`/`PropertySetMethod` 覆盖该页 **56 个顶层属性**中的绝大多数（`mode_code_reason` 0-23 ✓、`gear` 0-9 ✓、`dongle_type` {6,10} ✓、`position_state.quality` {1-5,10} ✓、`camera_mode` 0-3 ✓、`battery`/`obstacle_avoidance`/`distance_limit_status`/`maintain_status`/`position_state`/`storage`/`liveview_world_region` 嵌套 struct 均 ✓）；**缺口**：`best_link_gateway`、`flysafe_database_version`（新 state 字段）、`fts_info`（新 osd struct）、`mode_code`=21、`PropertySetMethod` 缺 `commander_flight_height`/`commander_mode_lost_action`、`CameraInfo`（`cameras`）缺 20 字段 | **待确认（§5-77~82，非阻断）**：补字段 + 扩枚举 + `fts_info` 建模**已裁决 → 方案 A**（§5-78）；**未写代码**（2026-10-14） |
| **§5-78 裁决** | `fts_info` 采用 **方案 A（建模）**：新增 `FtsInfo` record + `FtsStatus` 枚举（6 值）+ `OsdField.FTS_INFO` + `DroneOsd.ftsInfo`；撤销 §5-8「struct 从未定义」结案依据；`@DocUrl` → M4D/M4TD `aircraft/m4d-properties.html`（A49/§5-78，2026-10-14） |
| Dock1 设备属性（`dock1/properties.html`，A50） | SDK `DockOsd`/`OsdField`/`StateField`/`PropertySetMethod` 覆盖该页 **46 个顶层属性** 的绝大多数（`mode_code` 0-5 ✓、`flighttask_step_code` {0-5,255,256} ✓、`network_state`/`position_state`/`maintain_status`/`alternate_land_point`/`wireless_link`/`cover_state`/`putter_state` 等 ✓）；**偏差**：`electric_supply_voltage` `Double` vs 页 `int`/V、`DockOsd` 缺 `drone_authority_info` 模型字段 | **待确认（§5-83~88，非阻断）**：`electric_supply_voltage` 类型/归属**跨页冲突**（Dock1 `int`/V vs O4 `double`/V）、`drone_authority_info` 补模型、Javadoc 级修正 + Dock1 页时效性核对；**未写代码**（2026-10-14） |
| Dock1 设备管理（`dock1/device.html`，A51） | SDK `UpdateTopoData`/`SubDevice`/`UpdateTopoReplyData`/`StatusMethod.UPDATE_TOPO` 与 `update_topo` 页**字段完全一致 ✓**（7 顶层 + 8 子字段；`sys/product` Topic ✓、空 `sub_devices`=下线 ✓）；**无字段缺口**；**口径/元数据 3 项**：`status_reply` 仅 `result` vs SDK 多 `subType`（§5-89）、`StatusMethod` Javadoc 语义冲突（§5-90）、`base_info` 归属（§5-91） | **待确认（§5-89~91，非阻断）**：`subType` 来源核对、Javadoc 更正、`base_info` 是否 O4 专属；**未写代码**（2026-10-14） |
| Dock1 组织管理（`dock1/organization.html`，A52） | Request 侧 `AirportBindStatusRequest`/`AirportOrganizationGetRequest`/`AirportOrganizationBindRequest` **逐字段一致 ✓**；Reply 侧 **3 缺口**（同 A4、本页二次印证）：`AirportOrganizationBindReply.ErrorInfo` 字段名 `errSn`(→`err_sn`) vs 文档 `sn`、`AirportOrganizationGetReply` 仅 `result` 缺 `output{organization_name}`、`AirportBindStatusReply` 仅 `result` 缺 `output{bind_status[]}` 且 Javadoc 语义冲突 | **待确认（§5-92~94，非阻断）**：改 `errSn`→`sn`、补两 Reply 的 `Output` 结构 + 修 Javadoc；属 **breaking**，随 `1.18.0.0` 发布；**A4 漏登记 §5 已补齐**；**未写代码**（2026-10-14） |
| Dock1 直播功能（`dock1/live.html`，A53） | 4 条直播 services **4/4 已覆盖 ✓**（同 A5）：`ServiceMethod` 齐全 + 4 个 Request + `VideoQuality`(0-4) + reply 仅 `result`；`LiveLensChangeRequest.videoId` 对 Dock 不适用（`@JsonInclude(NON_NULL)`）✓、`url_type=4` WebRTC(WHIP)/`=3` GB28181 ✓ | **待确认（§5-95，非阻断）**：`url_type`/`video_type` 是否补 Java 枚举；**非阻塞（§5-96）**：live Javadoc 取值/格式补全；页面 2025-11-12（较新）；**未写代码**（2026-10-14） |
| Dock1 媒体管理（`dock1/media.html`，A54） | 4 条指令 **4/4 已覆盖 ✓**（同 A35 的 dock3/media.html）：2 events（`HighestPriorityUploadFlighttaskMediaData`/`FileUploadCallbackData`）+ 1 service（`UploadFlighttaskMediaPrioritizeRequest`）+ 1 request（`StorageConfigGetRequest`/`Reply`）；**差异 2 项**：`StorageConfigGetReply.Credentials.expireTime` wire key `expire_time` vs 文档 `expire`（§5-97）、`FileUploadCallbackData...gimbalYawDegree` Type `float` vs 示例字符串、SDK `String`（§5-98）；`provider` 未建模枚举（§5-99） | **待确认（§5-97~99，非阻断）**：`expireTime`→`expire` 改名（与 §5-52 同字段）、`gimbalYawDegree` 类型裁决、`provider` 是否枚举化；页面 2026-03-19；**未写代码**（2026-10-14） |
| Dock1 航线管理（`dock1/wayline.html`，A55） | 15 条指令方法常量 **15/15 存在 ✓**、多数 POJO 已覆盖 ✓（`FlighttaskProgressData`/`ReturnHomeInfoData`/`DeviceExitHomingNotifyData`/`FlighttaskPrepareRequest`/`FlighttaskExecute`/`FlighttaskUndo`/`FlighttaskResourceGet` 等）；**缺口 1 项**：`flight_setup_exception_notify` **无 POJO** 且 `EventMethod` 常量 Javadoc 描述**与文档矛盾**（§5-100）；**偏差 2 项**：`flight_setup_abort` reply 错误码未入 Javadoc（§5-101）、`simulate_mission`/`flighttask_prepare` 字段集 Dock1 页为 Dock3 页子集（§5-102/§5-103） | **待确认（§5-100~103，非阻断）**：新建 `FlightSetupExceptionNotifyData` + 更正 `EventMethod` Javadoc/`@Inferred` reason；补 `flight_setup_abort` reply 码 Javadoc；裁决 `simulate_mission.altitude` 与 Dock1 prepare 字段子集；页面 2025-11-12；**未写代码**（2026-10-14） |
| Dock1 HMS 管理（`dock1/hms.html`，A56） | 与 A20（`dock3/hms.html`）**同页同日期 2025-03-19**，字段/示例逐字一致 → **重复页复核，无新增差异**：`hms` 事件 `EventMethod.HMS`(need_reply=0) + `HmsData`（`list`/`Item`/`Args`）已覆盖 ✓；`HmsData.Item.code` 类型缺陷复认（§5-19，待改 `String`）；`hms_inner` 不补登结案（§5-20） | **无新增待确认**（复核 §5-19/§5-20 即可）；页面 2025-03-19；**未写代码**（2026-10-14） |
| Dock1 远程调试（`dock1/remote-control.html`，A57） | 21 条 services 方法常量 **21/21 存在 ✓** + 12 个同名进度事件（`ServiceProgressData` 覆盖 ✓）；`battery_maintenance_switch`/`alarm_state_switch`/`sdr_workmode_switch` 请求已覆盖 ✓（`ActionSwitchRequest`/专用 POJO） | **待确认（§5-104~106，非阻断）**：`air_conditioner_mode_switch`/`battery_store_mode_switch` 字段名 `mode`→`action`、清理/统一 `ActionSwitchRequest`、`step_key` 是否枚举化；页面 2025-08-05；**未写代码**（2026-10-14） |
| Dock1 固件升级（`dock1/firmware.html`，A58） | `ota_progress`/`ota_create` 已覆盖 ✓（`OtaProgressData`/`OtaCreateRequest`）；判定为 A39（O4 `firmware.html`）的 Dock1 镜像页，字段逐字一致 | **无新增**；复认 §5-48~51（`file_size` 类型、`firmware_upgrade_type` 枚举取并集 `{2,3,4}`、`@DocUrl`、reply）；页面 2025-03-19；**未写代码**（2026-10-14） |
| Dock1 远程日志（`dock1/log.html`，A59） | 4 条 method **4/4 存在 ✓**（同 A40 O4 `log.html`）：`fileupload_list`/`fileupload_start`/`fileupload_update`（services）+ `fileupload_progress`（events，`need_reply=0`）；`FileUploadListRequest/Reply`/`FileUploadUpdateRequest`/`FileUploadStartRequest` 结构一致 ✓；`FileUploadProgressData` 确缺（§4） | **待确认（§5-107，非阻断）**：Dock1 `fileupload_start` 字段集为 O4 子集（`file_store_dir`/`file_type`/`params.files[].device_sn`/`result`）→ §4 补齐是否仍按超集；复认 §5-52~54；页面 2025-03-19；**未写代码**（2026-10-14） |
| Dock1 配置更新（`dock1/...config`，A60） | `config` 请求/回复 **判定为 A7（O4 获取配置 `config`）的 Dock1 镜像页**：`ConfigRequest{configType, configScope}` 与页**完全一致 ✓**；`ConfigReply{result, appId, appLicense, url, token}` 与页**不符** → **需重建**（`url`→`ntp_server_host`、`token`→`ntp_server_port`（文档 `int`）、补 `app_key`/`ntp_server_port`；文档回复**无 `result`**） | **待确认（§5-108，非阻断，breaking）**：`ConfigReply` 重建为 `{appId, appKey, appLicense, ntpServerHost, ntpServerPort(Integer)}`，`result` 去留须裁决；**A7 漏登记 §4/§5 → 本页补齐**；随 `1.18.0.0` 发布；页面 2025-06-09；**未写代码**（2026-10-14） |
| Dock1 指令飞行（`dock1/...drc.html`，A61） | 6 events + 32 services + 6 DRC **method 全部存在 ✓**（`EventMethod`/`ServiceMethod`/`DrcMethod`/`DrcUpMethod`）；POJO 多数已覆盖 ✓（`FlyToPointProgressData`/`TakeoffToPointProgressData`/`CameraPhotoTakeProgressData`/`PoiCircleStatusData`/`JoystickInvalidNotifyData`/`DrcModeEnterRequest`/`FlyToPoint*Request`/`TakeoffToPointRequest`/`HsiInfoPushData`/`HeartBeat*`/`DroneControlRequest`） | **待确认（§5-109~111，非阻断）**：①`drone_control`/`heart_beat.seq` 废弃口径跨页冲突（Dock1 最新页视 active vs O4 页视废弃）→ §4 L281 `DRONE_CONTROL → @Deprecated` / L283 `DroneControlReply` 复核；②`hsi_info_push` Javadoc 更正（12→360、米→毫米）；③`takeoff_to_point` Dock1 为 O4 子集；页面 2025-11-12；**未写代码**（2026-10-14） |
| Dock1 AirSense（Dock1「AirSense」页，A62） | `airsense_warning` 事件已在 A30-1 覆盖 ✓（同 O4 `airspace-safety.html`）：`EventMethod.AIRSENSE_WARNING`(need_reply=1) + `AirSenseWarningData`（`List<Alert>` 自定义反序列化 ✓，10 字段齐全）；**内容逐字一致、无新增** | **待确认（§5-112，非阻断，元数据）**：`@DocUrl` 现指 `dock1/wayline.html`（航线管理，A55 确证不含 `airsense_warning`）→ 归属待核；**复认 §5-11**（`Double`→`Integer`、Javadoc `need_reply` 0→1）；页面 2025-03-19；**未写代码**（2026-10-14） |
| Dock1 自定义飞行区（Dock1「自定义飞行区」页，A63） | 2 events + 1 request method **已覆盖 ✓**（`FLIGHT_AREAS_SYNC_PROGRESS`/`FLIGHT_AREAS_DRONE_LOCATION`/`FLIGHT_AREAS_GET`）+ 事件 POJO **一致 ✓**；**`flight_areas_update` service 缺失 ❌**；`FlightAreasGetReply` `output.file`（单数，仅 name/checksum）vs 文档 `output.files`（**数组**，含 name/url/checksum/size）**❌** | **待确认（§5-113~116，非阻断）**：补 `ServiceMethod.FLIGHT_AREAS_UPDATE`（复用 `NoParameterRequest`/`NoOutputReply`）；重建 `FlightAreasGetReply.Output` 为数组（补 `url`/`size`，**breaking**）；4 类 `@DocUrl` 归属更正；`FlightAreasGetRequest` 是否复用 `NoParameterRequest`；页面 2025-03-19；**未写代码**（2026-10-14） |
| Dock1 PSDK 功能（Dock1「PSDK 功能」页，A64） | **13 method 全量覆盖 ✓**（4 events + 8 services + 1 request）+ 字段结构一致 ✓（与已审计 Dock3 `psdk.html` 页镜像） | **待确认（§5-117~118，非阻断，元数据/口径）**：①`storage_config_get.module` 语义（本页 `1=psdk ui 资源` vs SDK Javadoc `1=日志`）核对/更正；②`speaker_*` 事件 POJO `@DocUrl`（`wayline.html` → `psdk.html`）待核；页面 2025-03-19；**未写代码**（2026-10-14） |
| Dock1 PSDK 互联互通（Dock1「PSDK 互联互通」页，A65） | **2 method 全量覆盖 ✓**（event `custom_data_transmission_from_psdk` + service `custom_data_transmission_to_psdk`）+ 字段一致 ✓；**首次官方原文确认**（报告原无覆盖） | **待确认（§5-119，非阻断，元数据）**：`CustomDataTransmissionToPsdkRequest` 的 `@Verified(basis)` 误称「文档未显式列出」→ 更正 basis + `@DocUrl`（→ `psdk-transmit-custom-data.html`）；页面 2025-03-19；**未写代码**（2026-10-14） |
| Dock1 ESDK 互联互通（Dock1「ESDK 互联互通」页，A66） | **2 method 全量覆盖 ✓**（event `custom_data_transmission_from_esdk` + service `custom_data_transmission_to_esdk`）+ 字段一致 ✓；**首次官方原文确认**（报告原无覆盖） | **待确认（§5-120，非阻断，元数据）**：`CustomDataTransmissionToEsdkRequest` 的 `@Verified(basis)` 误称「文档访问受限」→ 更正 basis + `@DocUrl` 归属核对（→ `esdk-transmit-custom-data.html`）；页面 2025-03-19；**未写代码**（2026-10-14） |
| Dock1 远程解禁（Dock1「远程解禁」页，A67） | **3 method 均存在 ✓ + 3 请求 POJO 一致 ✓**（与已审计 Dock3 `flysafe.html` 页镜像）；**回复 POJO（12 类）+ `FlatReplyEnvelope` 仍未创建 ❌**（A21 缺口 1+2/3，§4 L317–319 待执行） | **无新增 §5 项**：缺口已由 A21（§5-21/§5-22 决策）登记；本页二次确证字段表（`device_model_domain{0 飞行器,3 机场}`、`type{0–6}`、`rid_unlock.level{1 欧盟,2 中国}`、`power_unlock:{}`、example `gateway:""`）；页面 2025-03-19；**未写代码**（2026-10-14） |
| Dock1 航线管理原文投喂复核（A68） | 开发者投喂页 = A55 同页（2025-11-12），**逐字一致 → 重复页复核，新增差异 0**；15 方法常量 ✓、13 POJO ✓（例外同 A55） | **无新增待确认**：复认 §5-100~103（`flight_setup_exception_notify` 无 POJO/Javadoc 矛盾、`flight_setup_abort` reply 码、`simulate_mission`/prepare 字段子集）、§5-56、§5-16/§5-51；**未写代码**（2026-10-14） |
| Dock1 自定义飞行区原文投喂复核（A69） | 开发者投喂页 = A63 同页（2025-03-19），**逐字一致 → 重复页复核，新增差异 0**；2 events ✓、1 request ✓、`flight_areas_update` 仍缺失 | **无新增待确认**：复认 §5-113（`flight_areas_update` 缺失）、§5-114（`FlightAreasGetReply` files 数组 breaking）、§5-115（@DocUrl 归属）、§5-116（专用空 record 未复用）；**未写代码**（2026-10-14） |
| Dock3 媒体管理原文投喂复核（A70） | 开发者投喂页 = A35/A54 同源（`dock3/media.html`，**页面日期更新 2026-03-19 → 2026-08-19**），**内容逐字一致 → 重复页复核，新增差异 0**；4 方法常量 ✓、POJO 全覆盖 ✓ | **无新增待确认**：复认 §5-97（`expireTime`→`expire` breaking）、§5-98（`gimbalYawDegree` float/示例 string）、§5-99（`provider` 未枚举）、§5-117（`module`：本页 `{0 媒体}`，未定义 1）；**未写代码**（2026-10-14） |
| Dock3 PSDK 功能原文投喂复核（A71） | 开发者投喂页 = A64 同内容（Dock3 版，页面日期 2026-08-19），**逐字一致 → 跨 Dock 镜像复核，新增差异 0**；13 方法常量 ✓、POJO 全覆盖 ✓；**A71 为 Dock3 `psdk.html` 首个独立原文记录**（更正 A64「已审计」表述） | **无新增待确认**：复认 §5-117（`module`）、§5-118（`speaker_*` 事件 `@DocUrl` 归属，本页佐证应为 `psdk.html`）、§5-52/§5-97（`expire`）、§5-99（`provider`）、§5-106（`step_key` 未枚举）；**未写代码**（2026-10-14） |
| Dock3 PSDK 互联互通原文投喂复核（A72） | 开发者投喂页 = A65 同内容（Dock3 版，页面日期 2025-03-19），**逐字一致 → 跨 Dock 镜像复核，新增差异 0**；2 方法常量 ✓、字段一致 ✓；**A72 为 Dock3 `psdk-transmit-custom-data.html` 首个独立原文记录** | **无新增待确认**：复认 §5-119（`CustomDataTransmissionToPsdkRequest` `@Verified(basis)`/`@DocUrl`，本页佐证 Dock3 归属 URL）；**未写代码**（2026-10-14） |
| Dock3 ESDK 互联互通原文投喂复核（A73） | 开发者投喂页 = A66 同内容（Dock3 版，页面日期 2025-03-19），**逐字一致 → 跨 Dock 镜像复核，新增差异 0**；2 方法常量 ✓、字段一致 ✓；**A73 为 Dock3 `esdk-transmit-custom-data.html` 首个独立原文记录**；与 A72 对称（PSDK/ESDK 的 Dock3 版均已完成原文记录） | **无新增待确认**：复认 §5-120（`CustomDataTransmissionToEsdkRequest` `@Verified(basis)`/`@DocUrl`，本页佐证 Dock3 归属 URL）；**未写代码**（2026-10-14） |
| Dock1 AirSense 原文投喂复核（A74） | 开发者投喂页 = A62 同页（2025-03-19），**逐字一致 → 重复页复核，新增差异 0**；1 method ✓、10 字段 ✓ | **§5-112 未解除**：本页为归属页但**未提供 URL** → `AirSenseWarningData.@DocUrl`（现误指 `dock1/wayline.html`）更正仍待 URL；复认 §5-11；**未写代码**（2026-10-14） |
| Dock1 设备属性原文投喂复核（A75） | 开发者投喂页 = A50 同页（2025-03-19，410 行），**逐字一致 → 重复页复核，新增差异 0**；顶层属性表一致 ✓（`mode_code` 0-5、`electric_supply_voltage` int/V、`drone_authority_info`） | **§5-88 未解除**：页日期仍 2025-03-19，只能证明所持副本未变，不能证明 DJI 现行页未更新；复认 §5-83/§5-84；**未写代码**（2026-10-14） |
| M4D/M4TD 设备属性原文投喂复核（A76） | 开发者投喂页 = A49 同页（2026-09-01，225 行 / 56 顶层属性），**逐字一致 → 新增差异 0**；复认 §5-77/§5-78/§5-79/§5-81/§5-82 | **§5-80 原文前提解除**：`commander_mode_lost_action` 值域 = **{0,1}**（本页第 184 行）；`commander_flight_height`（float 米/m）需补常量，`commander_flight_mode` 已有；**未写代码**（2026-10-14） |
| §5-88 裁决（2026-10-14） | 开发者确认 Dock1「设备属性」页（2025-03-19，A50/A75）**即 v1.18 现行版** → **§5-88 关闭**；内容无需再投 | **推论**：§5-83 三方口径冲突（Dock1 页 `int` / O4 页 `double` / SDK `Double`）为**真实差异**（非过期页假象），裁决独立保留；**未写代码**（2026-10-14） |
| Dock3 自定义飞行区原文投喂复核（A77） | 开发者投喂页 = A63 同内容（Dock3 版，2025-03-19），**逐字一致 → 跨 Dock 镜像复核，新增差异 0**；2 events ✓、1 request ✓（reply 偏差 §5-114）、1 service 缺（§5-113） | **§5-115 追加证据**：归属页 Dock1/Dock3 两侧均已获原文，但 **URL 仍未提供 → 未解除**；复认 §5-113/§5-114/§5-116；**未写代码**（2026-10-14） |
| Dock1 AirSense 原文投喂复核（A78） | 开发者第 3 次投喂页 = A62/A74 同页（2025-03-19），**逐字一致 → 重复页复核，新增差异 0** | **§5-112 仍未解除**：内容已完整（三次一致），**仅缺 URL** → 待 URL；**未写代码**（2026-10-14） |
| §5-112 裁决（2026-10-14） | 开发者提供 Dock1「AirSense」页 URL → **§5-112 关闭**；`@DocUrl` 更正目标 = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/airsense.html`（现 `wayline.html`，**仅文件名差异、前缀一致**）；`@Verified(basis)` 归属表述同步更正 | **结论**：归属页确认，Java 层更正待统一落地；**未写代码**（2026-10-14） |
| §5-115 裁决（2026-10-14） | 开发者提供 Dock3「自定义飞行区」页 URL → **§5-115 关闭**；4 类 flight-area POJO `@DocUrl` 更正目标 = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/custom-fly-region.html`（现 `dock3/wayline.html`）；`@Verified(basis)` 由 simulator 升为官方文档 | **结论**：归属页确认（A41 佐证 wayline 页不含 flight_areas 内容），Java 层更正待统一落地；**未写代码**（2026-10-14） |
| §5-118 裁决（2026-10-14） | 开发者提供 Dock3「PSDK 功能」页 URL → **§5-118 关闭**；3 类 speaker 事件 POJO `@DocUrl` 更正目标 = `.../dock3/psdk.html`（现 `wayline.html`） | **结论**：归属页确认（与 services 侧一致），Java 层更正待统一落地；**未写代码**（2026-10-14） |
| §5-119 裁决（2026-10-14） | 开发者提供 Dock3「PSDK 互联互通」页 URL → **§5-119 关闭**；`@DocUrl` = `.../dock3/psdk-transmit-custom-data.html`；`@Verified(basis)` 删「文档未列出」 | **结论**：官方协议确认，Java 层更正待统一落地；**未写代码**（2026-10-14） |
| §5-120 裁决（2026-10-14） | 开发者提供 Dock3「ESDK 互联互通」页 URL → **§5-120 关闭**；`@DocUrl` = `.../dock3/esdk-transmit-custom-data.html`；`@Verified(basis)` 删「文档访问受限」 | **结论**：官方协议确认，Java 层更正待统一落地；**未写代码**（2026-10-14） |
| §5-74 遗留结案（2026-10-14，A79） | 开发者投喂 Pilot 上云 RC Pro 行业版「远程控制」页原文 → Pilot DRC 方法同样带 `drc_` 前缀（3 项印证），与 Dock 一致 → §5-74 遗留结案 | **结论**：跨通道前缀口径统一，方案 A 对 Pilot 侧成立；**未写代码**（2026-10-14） |
| RC Plus 2 行业版「远程控制」页原文投喂复核（A80） | 开发者投喂 Pilot 上云「RC Plus 2 行业版 远程控制」页原文（v1.18，2026-10-13，1902 行），**逐字复核 A15–A18/A25/A38** → 4 Event + 38 Service **全部已覆盖**（新增差异 0）；**§5-74 全量印证**（22 相机方法均带 `drc_` 前缀）；复认 §5-46/§5-47（`ir_lense`，第 4 次）、`CameraMode` 值 4（§5-13）、`ApertureValue` 值域（A16，60+ 项一致） | **无新增待确认**；§5-109 加档（本页 `heart_beat` down/up Data 均仅 `{timestamp}`，`seq` 在信封层）；**未写代码**（2026-10-14） |
| 三页「设备管理」原文投喂复核 + §5-89 结案（A81） | 开发者投喂 Dock1/RC Pro/RC Plus 2 三页「设备管理」原文（均 2025-03-19）→ **§5-89 结案**（三页 reply 均仅 `{result}`，无 `sub_type` → 裁决删除 `subType`，breaking）；§5-90/§5-91 加档；**新发现 §5-121**（RC Plus 2 `update_topo` topic/字段口径差异） | **§5-89 裁决待统一落地**（删除 `UpdateTopoReplyData.subType`，breaking，随 `1.18.0.0`）；**§5-121 待裁决**（RC Plus 2 topic = `thing/product` 是否笔误 + 顶层 `domain` 是否可空）；RC Plus 2 页投喂不完整（缺 Example/`status_reply`）→ 需补全定稿；**未写代码**（2026-10-14） |
| RC Pro 行业版「设备属性」页原文投喂复核（A82） | 开发者投喂 RC Pro「设备属性」页原文（`rc-pro/properties.html`，2025-09-03）+ RC Pro「设备管理」页（`rc-pro/device.html`，重复 A81 复核 0 差异）→ **新增差异 1**（`RcOsd.battery` vs 文档 `capacity_percent`）+ 2 处 metadata/Javadoc → 新 §5-122/§5-123；其余字段全覆盖 | **§5-122 待确认**（`RcOsd` 电量 wire key + `mode_code` 去留 + `@DocUrl` 归属）→ **需补投喂 RC Plus 2 行业版「设备属性」页（`dji-rc-plus-2/properties.html`）**；**未写代码**（2026-10-14） |
| RC Plus 2「设备管理」页完整版复核 + §5-121 结案（A83） | 开发者澄清本次内容归属 = RC Plus 2「设备管理」完整版（标注「设备属性」为笔误）→ topic `sys/product/...` + 顶层含 `domain` + `sub_devices[]` 含 `index` → **§5-121 结案（撤销，无 SDK 改动）**；§5-89 依据再确认（reply 仅 `{result}`） | **无新增待确认**；**仍缺 RC Plus 2 行业版「设备属性」页（`dji-rc-plus-2/properties.html`）→ §5-122 定稿**；**未写代码**（2026-10-14） |
| RC Plus 2 行业版「设备属性」页原文投喂复核 + §5-122 裁决（A84） | 开发者投喂 RC Plus 2「设备属性」页原文（`dji-rc-plus-2/properties.html`，2025-09-03），**与 RC Pro「设备属性」页逐字一致**（新增差异 0）→ §5-122-①裁决 `battery` → `capacityPercent`（breaking）、③`@DocUrl`/参考链接更正；§5-123 结案（Javadoc） | **§5-122-② 仍待确认**：`RcOsd.modeCode`（两页均无 `mode_code`）→ 需 `remote-controller/properties.html` 原文佐证或裁决删除；**未写代码**（2026-10-14） |
| 「负载控制—切换相机模式」页 + RC Pro / RC Plus 2「远程控制」页重复复核（A85） | ① RC Pro「远程控制」页（2025-03-19 全文）= 重复 A79；② RC Plus 2「远程控制」页（文件）= 重复 A80；③ **「负载控制—切换相机模式」段**（875 行；**开发者确认 = ② 同页锚点**）= 20 个 DRC 相机/红外方法（`drc_camera_*` / `drc_ir_metering_*` / `drc_gimbal_reset`），**全部已在 §4 登记（DRC 侧缺口）** → 新增差异 0；**§5-74 再次印证**（20/20 全带 `drc_` 前缀） | **无新增待确认**；**未写代码**（2026-10-14） |
| 通用遥控器「设备属性」页原文投喂复核（A86） | 通用 RC 属性页（2025-07-23）= A82/A84 同源页 → 顶层字段差异 0；**§5-122-② 闭环（三页均无 `mode_code` → 建议删 `RcOsd.modeCode`）**；**§5-122-③ URL 落实（`.../others/rc/properties.html`；原 Javadoc 链接路径 `remote-controller` 系错误）**；新增 **§5-124**（`dongle_infos` 子枚举，非阻断） | **§5-122-③** `@DocUrl` 统一目标待开发者裁决；**未写代码**（2026-10-14） |

| §9 全批采纳（2026-10-14） | 开发者裁决「按建议全批」→ §9.1–§9.7 **62 项全部按建议默认执行**（详见 §9） | **裁决完成**：全部转入 TDD 落地阶段（`tdd-test-cases.md` → `architecture-design.md` → 代码 → `mvn test`），随 `1.18.0.0` 发布；**未写代码**（2026-10-14） |

---

## 3. 逐项差异分析

### 3.1 O4 地面站（漫游基站）

#### 3.1.1 设备标识（domain / type / sub_type）

| 项 | 文档取值 | SDK 现状 | 处置 |
|---|---|---|---|
| domain | `8` | `DeviceDomain` 仅 0/2/3 | 新增 `GROUND_STATION(8)` |
| type | `135` | 无 | 新增 `GroundStationModel` 常量 |
| sub_type | `0` | 无 | 同上 |

> 依据 `ground-station/o4/device.html` 与 `properties.html` 的设备标识说明。

#### 3.1.2 属性 / 状态上报（`properties.html`）

**pushMode=0（osd）新增字段：**

| 字段（snake_case） | 类型 | 枚举/约束 | SDK 现状 |
|---|---|---|---|
| `mode_code` | enum_int | 0 空闲 / 1 现场调试 / 2 远程调试 / 3 固件升级 / 4 作业 / 5 待标定 / 6 低功耗 / 9 日志上报 | 字段名已存在，**值域与 `DockModeCode` 不同 → 需地面站专用枚举** |
| `low_power_switch_state` | enum_int | 0 关闭 / 1 打开 | **缺** |
| `electric_supply_power` | int | W | **缺** |
| `power_mode` | enum_int | 0 电池 / 1 PD / 2 POE | **缺** |
| `battery_voltage` | int | mV | **缺** |
| `tilt_angle` | struct | `value` / `valid`(0 无效 / 1 有效) | **缺**（嵌套 struct） |
| `basestation_work_mode` | enum_int | 0 漫游基站 / 1 外置机场 / 2 简易机场 | **缺** |
| `under_work_mode` | enum_int | 0 非棚下 / 1 棚下 | **缺** |
| `lan_status` | enum_int | 0 未连接 / 1 速率过低 / 2 100M / 3 1000M / 4 不支持 | **缺** |
| `poe_status` | enum_int | 0 无 PoE / 1 过低 / 2 25.5W / 3 51W / 4 71W | **缺** |
| `battery` | struct | `capacity_percent` / `voltage` / `current` / `temperature` | 字段名已存在，**嵌套 struct 缺** |
| `self_converge_coordinate` | struct | `longitude` / `latitude` / `height` | 字段名已存在，嵌套 struct 缺 |
| `electric_supply_voltage` | double | V | 已存在，**SDK 注为 int/mV → 类型冲突（见 §5）** |
| `acc_time` | int | 秒 | SDK 归 state，文档为 osd → 归属冲突（见 §5） |

**pushMode=1（state）新增字段：**

| 字段 | 类型 | 枚举/约束 | SDK 现状 |
|---|---|---|---|
| `electric_supply_mode` | enum_int | 0 电池 / 1 PD / 2 POE | **缺** |
| `is_beidou_version` | bool | — | **缺** |
| `peripherals_info` | array | `device_sn` / `name` / `port`(0 LAN /1 WAN) / `type`(0 其它/1 相机/2 DAA/3 雷达/4 电源/5 第三方机库/6 边缘计算/7 RID) / `ip_addr` / `need_auth` / `main_device` | **缺** |
| `peripheral_list` | array | `sn` / `rid{supported,version}` / `adsb{supported,version}` / `dock_control{supported,version}` | **缺** |
| `sub_device` | struct | `device_sn` / `device_model_key` / `device_online_status` / `device_paired` | 字段名已存在，**归属通道相反（见 §5）** |
| `rtcm_info` | struct | `mount_point` / `port` / `host` / `rtcm_device_type`(0 遥控器/1 机场/2 中继) / `source_type`(0 未标定/1 自收敛/2 手动/3 网络 RTK) | 已存在，嵌套待补 |
| `wireless_link_topo` | struct | `secret_code[28]` / `center_node{sdr_id,sn,model,device_type}` / `leaf_nodes[{sdr_id,sn,control_source_index(1-A/2-B),model,device_type}]` | 已存在，嵌套需新 struct |
| `heading` | double | 0–360（**文档标注已废弃**） | 已存在（SDK int）→ 类型冲突（见 §5） |

> 可复用字段（无需新增）：`longitude` / `latitude` / `height` / `position_state` / `alternate_land_point` / `network_state` / `storage` / `home_position_is_valid`。

#### 3.1.3 子设备上报（`device.html`）

- Topic：`sys/product/{gateway_sn}/status`，method `update_topo`（up）。
- Data：`domain` / `type` / `sub_type` / `device_secret` / `nonce` / `thing_version` / **`base_info{country_code}`（AES-128-ECB+Base64）** / `sub_devices[]{sn,domain,type,sub_type,index,device_secret,nonce,thing_version}`。
- Reply：`status_reply` 通道，data 为 `result`。
- **SDK 现状**：`StatusMethod.UPDATE_TOPO` + `UpdateTopoData`（含 `SubDevice`）+ `UpdateTopoReplyData` 已存在。
- **唯一缺口**：`UpdateTopoData` 缺 `base_info`（`country_code`）字段。

#### 3.1.4 远程调试（`cmd.html`）

- **新增 method（1 个）**：`low_power_switch`（services / down），data `action`(0 关闭 / 1 打开) → 可复用现有 `ActionSwitchRequest`。
- **已存在可复用**：`debug_mode_open` / `debug_mode_close`、`rtk_calibration`、`sdr_workmode_switch`、`sim_slot_switch`、`esim_activate` / `esim_operator_switch`、`device_reboot` / `device_format`、`drone_format`、`live_*`、`camera_*`（均已在 `ServiceMethod`）。
- **需重写**：`RtkCalibrationRequest`（现为 `@Inferred` 单字段 `caliType`）；O4 实际结构为 `devices[]{sn, type(1 手动标定), module(3 机场 / 6 中继), data{longitude,latitude,height}}`。
- **需扩展**：`module` 字段枚举 `{0 飞行器, 3 机场, 6 中继, 8 基站, 9 机库}`（现仅 0/3）。
- **需新增 POJO**：`rtk_calibration` 的 events 回包（含 `ext.devices[]{sn,type,module,result,status}` + `version`），现有 `ServiceProgressData` 不足以覆盖。
- 事件侧：`device_format` / `drone_format` / `esim_activate` / `esim_operator_switch` / `device_reboot` / `camera_photo_take_progress` 均为「同名进度事件」，按 SDK 约定不重复登记（沿用现有 `ServiceProgressData`）。

#### 3.1.5 固件升级（`firmware.html`）

- `ota_create`（services/down）：`devices[]{sn, product_version, file_url, md5, file_size, file_name, firmware_upgrade_type{2 一致性/3 普通/4 PSDK}}`；reply `result` + `output.status{canceled,failed,in_progress,ok,paused,rejected,sent,timeout}`。
- `ota_progress`（events/up）：`result` + `output{status, progress{percent, current_step{download_firmware,upgrade_firmware}}}`。
- **SDK 现状（A39 修正）**：`OtaCreateRequest` / `OtaProgressData` 已存在；`ota_progress` 与文档**完全一致** ✓；`ota_create` **非「完全一致」**——`file_size` 文档 `int` vs SDK `Long`、`firmware_upgrade_type` 文档 `enum_int{2,3,4}` vs SDK 原始 `int` 无枚举、reply `output.status` 无专属 POJO → 见 §5-48~51（详见 [附录 A39](#a39-o4-地面站固件升级页firmwarehtml原文2026-10-14-提供)）。

#### 3.1.6 日志管理（`log.html`）

- Method：`fileupload_list` / `fileupload_start` / `fileupload_update`（services/down）+ `fileupload_progress`（events/up）。
- **`FileUploadStartRequest` 需扩展**：新增 `file_store_dir`、`file_type`（固定 `text_log`）；`params.files[]` 增 `device_sn` / `result` / `list[]{boot_index, start_time, end_time, size}`。
- **`FileUploadProgressData` 缺失**（`command/event/system/package-info.java` 存在悬空 `@link` 引用）→ 需新增；结构：`result` + `output{status, ext.files[]{module,size,device_sn,key,fingerprint,progress{progress,finish_time,upload_rate}}}`，`need_reply=0`。
- **`FileUploadListReply` 结构一致可复用**，但 `start_time` / `end_time` 单位 O4 为**秒**、Dock3 为**毫秒** → 需核对类型（见 §5）。
- `module` 枚举需扩展（8 基站 / 9 机库）。

> **A40 原文核实（2026-10-14）**：本页完整原文已投喂。`fileupload_list`/`fileupload_update` 的 Request 与 `FileUploadListReply` **结构一致 ✓**；`FileUploadStartRequest` **确缺** `file_store_dir`/`file_type`/`params.files[]` 子字段；`FileUploadProgressData` **确缺**；新登记 §5-52（`long`/`Int` vs `int`，含 §5-6 复核）、§5-53（`module` 三口径）、§5-54（progress 表 vs example 冲突）；**SDK 现无 `Module` 枚举**（本节原「module 枚举需扩展」表述待核实）。

#### 3.1.7 空域安全（`airspace-safety.html`）

全部为 `events` / up 通道，Topic `thing/product/{gateway_sn}/events`：

| method | SDK 现状 | 处置 |
|---|---|---|
| `rid_infos` | **完全缺失** | 新增 `EventMethod.RID_INFOS` + `RidInfosData`（含 `Gnss` + `RidItem`） |
| `adsb_infos` | **完全缺失** | 新增 `EventMethod.ADSB_INFOS` + `AdsbInfosData`（含 `AdsbItem`） |
| `airsense_warning` | 已存在（`EventMethod.AIRSENSE_WARNING` + `AirSenseWarningData`） | 复用，核对类型与 needReply 注释 |

- **`gnss` struct 缺失**：`engine / flag / time(String, YYYYMMDD-HHmmss) / lat / lon / alt / satellite / hdop`。两法共用 → 新建 `Gnss` record。
- `rid_infos` data：`capacity_percent` / `power_supply` / `device_sn` / `gnss` / `rid[]`（≤50），Item：`osid,rssi,fre,uatype,status,heading,speed,uprate,lon,lat,altgeo,altbaro,height,op_lon,op_lat,op_alt,uatime`。
- `adsb_infos` data：`capacity_percent` / `device_sn` / `power_supply` / `gnss` / `adsb[]`，Item：`icao,callsign,height,lat,lon,speed,heading,uprate`。
- 建议包：`command/event/peripheral/`（对齐现有按域分包）。

### 3.2 大疆机场 3 飞行终止系统（FTS）

`dock3/fts.html` 仅定义 **2 个 services 指令**（无 events / property-set）：

| method | 通道 | data |
|---|---|---|
| `fts_config` | services / down | `fts_server_type`(0 DJI FTS / 1 自定义)；`mqtt_broker{address,username,password}`（`fts_server_type=1` 时必填） |
| `drone_propeller_stop_v1` | services / down | `fts_verification_code`（8 位数字+大写字母，取自飞行器 OSD `fts_info.fts_verification_code`） |
| 两者 reply | services_reply / up | 仅 `result` |

- **SDK 现状**：`ServiceMethod` / `EventMethod` / `PropertySetMethod` / OSD 均无 fts 相关定义 → **完全缺失**。
- 需新增：`ServiceMethod.FTS_CONFIG`、`ServiceMethod.DRONE_PROPELLER_STOP_V1`。
- 需新增 POJO（建议 `command/service/fts/`）：`FtsConfigRequest(ftsServerType, FtsMqttBroker)`、`FtsMqttBroker(address, username, password)`、`DronePropellerStopV1Request(ftsVerificationCode)`。Reply 可复用 `NoOutputReply`。
- **`FtsMqttBroker` 不可复用 `DrcMqttBroker`**（后者含 `client_id` / `enable_tls` / `expire_time`，FTS 的 `mqtt_broker` 无 `client_id`）。
- 枚举 `FtsServerType{DJI_FTS_SERVER(0), CUSTOM_FTS_SERVER(1)}`。

### 3.3 机场 2 / 机场 3：相机录像清晰度 + 照片分辨率

均为 **DRC `drc/down`** 方法（文档列于 remote-control 页 Service 段；dock2、dock3 页面均已包含）：

| 用途 | method | data |
|---|---|---|
| 视频（录像）分辨率设置 | `drc_video_resolution_set` | `payload_index`（text）+ `video_resolution` enum（0:1920*1080 / 1:3840*2160；红外仅 640*512 不可设置） |
| 照片分辨率设置 | `drc_camera_photo_size_set` | `payload_index`（text）+ `photo_size` enum_int（**0:4K / 4:8K**） |
| 回执 | `drc/up` | `result`（int，非 0 为错误） |

- **`DRC_VIDEO_RESOLUTION_SET`**：method 名与文档**完全一致**，文档确认为 DRC 通道 → **由 `@Inferred` 升级为 `@Verified`**。
- **`drc_camera_photo_size_set`**：SDK **完全缺失**（无枚举常量、无 POJO）。
- **枚举缺失**：无 `VideoResolution`、无 `PhotoResolution/PhotoSize`（现有 `PhotoFormat` 7=RJPEG/16=DLT664 与 `VideoQuality` 直播码流语义不同，**不可复用**）。
- 需新增枚举：`VideoResolution{FHD_1080P(0), UHD_4K(1)}`、`PhotoResolution{R4K(0), R8K(4)}`。

### 3.4 M4D/M4TD 飞行器设备属性（`aircraft/m4d-properties.html`）

该页为 M4D/M4TD 飞行器属性页（**56 个顶层属性**），SDK `DroneOsd`/`OsdField`/`StateField`/`PropertySetMethod` 已覆盖绝大多数（字段名、类型、pushMode 归属一致）：

| 类别 | 结论 |
|---|---|
| osd 字段（pushMode=0） | `mode_code`/`latitude`/`longitude`/`height`/`elevation`/`attitude_*`/`horizontal_speed`/`vertical_speed`/`wind_speed`/`wind_direction`/`home_distance`/`gear`/`height_limit`/`rth_altitude`/`is_near_area_limit`/`is_near_height_limit`/`night_lights_state`/`obstacle_avoidance`/`distance_limit_status`/`total_flight_distance`/`total_flight_time`/`total_flight_sorties`/`track_id`/`position_state`/`storage`/`battery`/`maintain_status`/`activation_time` 等 **均 ✓** |
| state 字段（pushMode=1） | `firmware_version`/`firmware_upgrade_status`/`compatible_status`/`control_source`/`mode_code_reason`/`current_rth_mode`/`rth_mode`/`commander_flight_height`/`commander_flight_mode`/`commander_mode_lost_action`/`camera_watermark_settings`/`psdk_ui_resource`/`psdk_widget_values`/`low_battery_warning_threshold`/`serious_low_battery_warning_threshold`/`home_latitude`/`home_longitude`/`remaining_power_for_return_home`/`wireless_link_topo`/`dongle_infos`/`offline_map_enable` 等 **均 ✓** |
| 枚举值域 | `mode_code_reason` 0-23 ✓、`gear` 0-9 ✓、`dongle_type` {6,10} ✓、`camera_mode` 0-3 ✓、`position_state.quality` {1-5,10} ✓、`position_state.is_fixed` 0-3 ✓ |
| 嵌套 struct | `battery`（含 `batteries[]` 10 字段）✓、`obstacle_avoidance` ✓、`distance_limit_status` ✓、`maintain_status` ✓、`position_state` ✓、`storage` ✓、`liveview_world_region` ✓ |

**缺口 5 项 + Javadoc 级 4 处**（详见附录 A49、§5-77~82）：

1. **新增 state 字段**：`best_link_gateway`（text/r/pushMode=1，蛙跳任务图传最优网关 SN）、`flysafe_database_version`（text/r/pushMode=1）→ SDK 双缺（§5-77）。
2. **`fts_info` OSD 结构**：struct/r/pushMode=0（`fts_status` enum_int 6 值 + `fts_verification_code`/`fts_valid_duration`/`fts_timestamp`/`fts_sn`/`fts_firmware_version`）→ SDK 完全缺失；**本页首次给出该 struct 完整定义，推翻 §5-8「从未被定义」前提 → 已裁决建模（方案 A，新建 `FtsInfo` + `FtsStatus`）**（§5-78）。
3. **`mode_code` 值 21**：`21 = 进离场航线飞行过程中` → `DroneModeCode` 现仅 0-20（§5-79）。
4. **`PropertySetMethod` 缺 2 rw 属性**：`commander_flight_height`、`commander_mode_lost_action`（本页 accessMode=rw；其余 13 项已收录 ✓）（§5-80）。
5. **`cameras[]` 结构缺 20 字段**：SDK `CameraInfo` 仅 11 字段，缺 `photo_storage_settings`/`video_storage_settings`/`wide_*`(4)/`zoom_*`(8)/`ir_metering_*`(3)（含新 struct `IrMeteringPoint`/`IrMeteringArea`）（§5-81）。
6. **Javadoc 级**（§5-82）：`StateField.DONGLE_INFOS`/`OFFLINE_MAP_ENABLE` 注释「M400 only」应补 M4D/M4TD；`DronePositionState.quality` 注释「Drone 仅 1-5、不含 10」应补 `10=RTK fixed`。

---

## 4. SDK 改动清单（文件级）

| 模块 | 文件 | 改动 |
|---|---|---|
| 根 | `pom.xml` | 版本 `1.16.1.3` → `1.18.0.0`（`mvn versions:set -DprocessAllModules=true`） |
| model | `DeviceDomain.java` | 新增 `GROUND_STATION(8)` |
| model | `GroundStationModel.java`（新建） | 地面站型号枚举（domain=8, type=135, sub_type=0） |
| model | `DeviceCompatibility.java` | **Dock↔Drone 搭配不新增条目**（A43 结案：产品族吻合）；**新增决策点**：是否增「最低固件版本」（§5-60）、`RC_PRO` 值域是否扩展（§5-61）；`@Verified` basis 可具体化为 `overview/product-support.html`（A43） |
| wayline/* | WPML 共用元素（`dji-wpml/common-element.html`，A44） | **无代码改动** ✓：`wayline/` 全部 model/action/enumtype 与文档逐一吻合（无缺失类/字段、类型一致）；仅记录布尔字段 int 口径（§5-62）与枚举超集（§5-63） |
| wayline/model | `Placemark.java` / `Mapping2dPlacemark.java` / `MappingStripPlacemark.java` / `Folder.java`（**字段补全 / 类型修正**，A45） | **待确认（§5-64~67）**：`Placemark` 补 `isRisky`(Integer)/`useStraightLine`(Integer)；`Mapping2dPlacemark` 补 `quickOrthoMappingEnable`(Integer)/`quickOrthoMappingPitch`(Integer，**仅 M4E**，包装类型 + NON_NULL)；`MappingStripPlacemark.margin` `int` → **`double`**（文档航带飞行 `margin` 为浮点型）；`Folder`（航点）补 `payloadParam`（复用 `PayloadParam`；`MappingFolder` 已含 ✓，**层级待确认**）。属 **breaking**（record 扩字段 / 类型变更），随 `1.18.0.0` 发布 |
| wayline/model/execute | `ExecuteFolder.java` / `ExecutePlacemark.java`（**字段补全**，A46） | **待确认（§5-68~71）**：`ExecuteFolder` 补 `startActionGroup`（复用 `ActionGroup`）；`ExecutePlacemark` 补 `isRisky`(Integer)/`useStraightLine`(Integer)；`ParsedKmz.waylines` 是否由 `Kml<ExecuteFolder>` 改为支持**多 Folder**（倾斜摄影 5 条）；`gimbalPitchAngle` 层级待确认。属 **breaking**，随 `1.18.0.0` 发布。**已裁决（2026-10-14）：多 Folder 采用方案 B** —— 新增 `WaylinesDocument`+`WaylinesWpml`（见 §5-70） |
| protocol/method | `DrcMethod.java`（**命名口径已裁决**，A47/§5-74） | **已裁决（方案 A）**：DRC 通道新增常量 wire **逐字对齐官方文档**（`drc_camera_aim` / `drc_camera_mode_switch` / `drc_camera_photo_take` / `drc_gimbal_reset` … 带 `drc_` 前缀），与 `ServiceMethod` 无前缀名**并存**；飞行 4 件套沿用无前缀；DRC 专属 POJO 字段一致时复用同一 record；**更正 `DrcMethod` Javadoc「同名复用」表述**。**待裁决（§5-72/73/75，非阻断）**：`compressed_sticks_control` 是否新增、DJI 仓库独有 DRC 方法是否纳入、AI 框选坐标维持 `Integer`。**尚未写代码**（A47） |
| protocol/method | `ServiceMethod.java`（**分类归属差异记录**，A48） | **无改动（仅记录）**：`ServiceMethod` 无前缀 wire 与 DJI `services` 分类一致 ✓；DJI 将相机细项（`camera_photo_take`/`camera_mode_switch`/`camera_aim`/`gimbal_reset`/`camera_frame_zoom` 等）归入 **`drc` 分类**、本 SDK 归 `ServiceMethod` → **分类归属差异**（§5-74 双通道命名成因）；**待裁决（§5-76，非阻断）**：DJI `services` 分类独有方法（`open_parachute`/`relay_*`/`offline_map_update` 等 ~35 项）是否纳入。**尚未写代码**（A48） |
| protocol/method | `ServiceMethod.java` | + `LOW_POWER_SWITCH`、`FTS_CONFIG`、`DRONE_PROPELLER_STOP_V1` |
| protocol/method | `DrcMethod.java` | + `DRC_CAMERA_PHOTO_SIZE_SET`、`DRC_PSDK_INPUT_BOX_TEXT_SET`、`DRC_PSDK_WIDGET_VALUE_SET`、`DRC_VIDEO_STORAGE_SET`、`DRC_CAMERA_AIM`、`DRC_CAMERA_EXPOSURE_MODE_SET`、`DRC_CAMERA_EXPOSURE_SET`、`DRC_CAMERA_FOCAL_LENGTH_SET`、`DRC_CAMERA_FOCUS_MODE_SET`、`DRC_CAMERA_FOCUS_VALUE_SET`、`DRC_CAMERA_FRAME_ZOOM`、`DRC_CAMERA_LOOK_AT`（共 12 个）；`DRC_VIDEO_RESOLUTION_SET`/`DRC_CAMERA_APERTURE_VALUE_SET`/`DRC_CAMERA_DEWARPING_SET`/`DRC_CAMERA_ISO_SET` → `@Verified`；`DRONE_CONTROL` → `@Deprecated`；`HEART_BEAT` → `@Verified`（见 A15–A17）；另 + `DRC_CAMERA_MODE_SWITCH`、`DRC_CAMERA_PHOTO_FORMAT_SET`、`DRC_CAMERA_PHOTO_STOP`、`DRC_CAMERA_PHOTO_TAKE`、`DRC_CAMERA_POINT_FOCUS_ACTION`、`DRC_CAMERA_RECORDING_START`、`DRC_CAMERA_RECORDING_STOP`、`DRC_CAMERA_SCREEN_DRAG`、`DRC_CAMERA_SCREEN_SPLIT`、`DRC_GIMBAL_RESET`、`DRC_PHOTO_STORAGE_SET`、`DRC_IR_METERING_AREA_SET`、`DRC_IR_METERING_MODE_SET`、`DRC_IR_METERING_POINT_SET`（共 14 个，见 A18） |
| command/drc/psdk | `DrcPsdkInputBoxTextSetRequest.java` / `DrcPsdkWidgetValueSetRequest.java`（新建） | DRC 文本框（A15 缺口 1）+ DRC 控件值（A16 缺口 1） |
| command/drc/flight | `DroneControlReply.java`（新建） | `drone_control` 失败回包 `{result, output{seq}}`（A15 缺口 3，待决策） |
| command/drc/flight（状态） | `DrcNightLightsStateSetRequest.java` / `DrcStealthStateSetRequest.java`（新建） | 夜航灯/隐蔽模式（A16 缺口 2） |
| protocol/method | `EventMethod.java` | + `RID_INFOS`、`ADSB_INFOS`（A30 定稿：两事件 example 与字段表均未标 `need_reply` → **取 `0` + `@Inferred`**，见 §5-37 已决策） |
| command/event/wayline | `ReturnHomeInfoData.java`（**注解更正**） | A32/A34：删除类级 `@Inferred`（**标注类型不当**：字段存在性与可选性已由 Dock2/Dock3 文档明确声明；「Dock1 无此字段」经 Dock1 页核实属实）→ **升 `@Verified`**；字段 / 类型**零改动**；可选补建 `LastPointType` / `HomePlanStatus` 枚举见 §5-42 |
| telemetry | `OsdField.java` / `StateField.java` | O4 新字段常量 |
| telemetry/osd | `GroundStationOsd.java`（新建） | O4 遥测 record + 嵌套 struct |
| telemetry/enumtype | `GroundStationModeCode.java` / `PowerMode` / `BasestationWorkMode` / `LanStatus` / `PoeStatus` / `UnderWorkMode`（新建） | O4 枚举 |
| command/status | `UpdateTopoData.java` | + `base_info`（`country_code`） |
| command/service | `RtkCalibrationRequest.java` | 重写为 `devices[]` 结构；新增 events 回包 POJO |
| command/service | `OtaCreateRequest.java` / `OtaProgressData.java`（**类型 / 枚举修正**，A39） | **待确认（§5-48 / §5-49）**：`OtaDevice.fileSize` `Long` → **`Integer`**（文档 `int`）；`firmwareUpgradeType` 补 `FirmwareUpgradeType` 枚举（文档 `enum_int{2,3,4}`）；`@DocUrl`/`@Verified(basis)` 改指 O4 `firmware.html`（§5-50）；reply 见 §5-51 |
| command/service | `FileUploadStartRequest.java` / `FileUploadListReply.java` / `FileUploadListRequest.java` / `FileUploadUpdateRequest.java`（**扩展 / 类型核对**，A40） | `FileUploadStartRequest` **确缺**：`file_store_dir`、`file_type`（固定 `text_log`）、`params.files[]` 的 `device_sn`/`result`/`object_key`/`list[]{boot_index, start_time, end_time, size}`；`FileUploadListReply.LogFile` 的 `startTime`/`endTime`/`size` 与 `Credentials.expire` 现 `long`/`Long` vs 文档 `int`（§5-52）；`module`/`moduleList` 现 `String` vs 文档 `enum_int`/`text` 口径不一（§5-53）；`@DocUrl` 可补 O4 `log.html`；**Dock1 页（A59，2025-03-19）为 O4 子集**（无 `file_store_dir`/`file_type`；`params.files[]` 无 `device_sn`/`result`；`list[]` 仅 `boot_index`）→ 补齐是否仍按超集见 §5-107 |
| command/event/system | `FileUploadProgressData.java`（**新建**，A40） | 补悬空 `@link`（`command/event/system/package-info.java`）；结构按 O4 `log.html`：`result` + `output{status, ext{files[]{module, size, device_sn, key, fingerprint, progress{progress, finish_time, upload_rate}}}}`，`need_reply=0`；**字段集待裁决**（表 vs example 冲突，见 §5-54）；`@DocUrl` 指 O4 `log.html`；**Dock1 页（A59，2025-03-19）二次印证仍缺** |
| command/event/peripheral | `RidInfosData.java`（含 `RidTarget`）/ `AdsbInfosData.java`（含 `AdsbTarget`）/ `Gnss.java`（新建） | 空域安全（**A30 原文定稿**）：`Gnss` 两事件共享，**`time` 必须为 `String`（`YYYYMMDD-HHmmss`，非时间戳）**；`RidTarget.speed/uprate` 为 `Double`（文档 float），`AdsbTarget.height/speed/heading/uprate` 为 `Integer`；`Gnss` 子字段类型未逐项标注 → `@Inferred`；`need_reply` 见 §5-37 |
| command/event/alert | `AirSenseWarningData.java`（**类型/Javadoc 修正**） | A30（§5-11 结案）：Javadoc `need_reply` **0 → 1**（与 `EventMethod.AIRSENSE_WARNING`=1 及文档示例=1 对齐，原 Javadoc 属 SDK 内部缺陷）；`Alert.altitude`/`relativeAltitude`/`distance` **`Double` → `Integer`**（文档 int）；`latitude`/`longitude`/`heading` **保持 `Double`**（文档 float → Double 既定映射）；**另（A62，2025-03-19）**：`@DocUrl` 现指 `dock1/wayline.html`，但该页（航线管理，A55）15 条指令**不含** `airsense_warning` → **归属待核**（本页 Dock1「AirSense」才是该事件归属页）→ 更正为独立 AirSense 页 URL（§5-112） |
| command/service/fts | `FtsConfigRequest.java`（内含嵌套 `MqttBroker`）/ `DronePropellerStopV1Request.java`（新建）+ `fts/package-info.java` | FTS；**A22 已收原文确认**：+ `FtsServerType` 枚举；回复复用 `NoOutputReply`（原计划的独立 `FtsMqttBroker.java` 按 SDK 嵌套 record 风格并入 `FtsConfigRequest`） |
| command/service/wayline | `ReturnHomeReply.java`（新建） | A19 缺口：`return_home` 回包 `{result, output{status}}`，SDK 现仅 `NoOutputReply` |
| command/service/wayline | `DockInfo.java` / `CenterNode.java` / `LeafNode.java` / `RtcmInfo.java`（**字段补全 / 新建**，A41） | **待确认（§5-55）**：`DockInfo` 补 `heading`(Double)/`homePositionIsValid`(Integer)/`rtcmInfo`/`alternateLandPoint`（复用 `telemetry/nested/AlternateLandPoint`）；`CenterNode` 补 `sdrId`(Integer)；`LeafNode` 由空 record 补 `{sdrId, sn, controlSourceIndex}`；**新建** `RtcmInfo{mountPoint, port, host, rtcmDeviceType, sourceType}`；属 **breaking**，随 `1.18.0.0` 发布 |
| command/service/wayline | `FlighttaskCreateRequest.java` / `FlighttaskCreateReply.java` + `ServiceMethod.FLIGHTTASK_CREATE`（**新建，待裁决**，A41） | **待确认（§5-56）**：文档仍列 `flighttask_create`（标注「废弃」），SDK 完全缺失；补入则需在常量上标 `@Deprecated` |
| command/request/wayline | `FlighttaskProgressGetRequest.java` / `FlighttaskProgressGetReply.java`（**修正 / 具体化**，A41） | **待确认（§5-57 / §5-58）**：Request 的 `flightId`（文档外字段）删除或改可选；Reply 的 `output`(`Object`) 具体化为 `Output{flightId, Progress{currentStep,percent}, status}` 并升 `@Verified`；均属 **breaking** |
| command/request/wayline | `FlighttaskProgressGetRequest.java`（**字段名修正**，A42） | **待确认（§5-59）**：Dock2 页请求 data 为 `target_sn`+`flight_id`（含 example），SDK 现 `sn` 与 wire key `target_sn` 不匹配 → 字段改名 `sn` → `targetSn`（或兼容处理）；随 `1.18.0.0` 发布 |
| command/{service,event,request}/wayline | 多个类（**`@Verified` 依据升级**，A41） | 将 `FlighttaskReadyData`/`DeviceExitHomingNotifyData`/`InFlightWaylineProgressData`/`FlighttaskProgressData`/`FlighttaskExecuteRequest`/`FlighttaskPrepareRequest`/`FlighttaskStopRequest`/`FlighttaskUndoRequest`/`InFlightWaylineDeliverRequest` 等的 basis 从「simulator 已对接 hivemind」升级为「DJI Dock3 wayline.html Data 表」（`@Inferred` 项保留） |
| command/service/flysafe | `UnlockLicenseListReply.java` / `License.java` / `CommonFields.java` / `AreaUnlock.java` / `CircleUnlock.java` / `CountryUnlock.java` / `HeightUnlock.java` / `PolygonUnlock.java` / `PolygonPoint.java` / `PowerUnlock.java` / `RidUnlock.java` / `UnlockLicenseSwitchReply.java`（新建，共 12 个） | A21 缺口 1+2：两个回复 POJO 缺失 |
| protocol/envelope | `FlatReplyEnvelope.java`（新建） | A21 缺口 3（**2026-10-14 决策 → 方案 B**）：承载扁平 services_reply data（`{result, ...}`）→ 新增专用信封（`data` 为 `Object`，Reply 自带 `result`），对齐既有 `RequestReplyEnvelope` 模式；**否决 `ReplyEnvelope` 泛型化**；并按 §5-22 方案 A 一并含 `String gateway`（末位、允许 null） |
| protocol/envelope | `ReplyEnvelope.java` | A21 缺口 4（**2026-10-14 决策 → 方案 A**）：补 `String gateway`（**末位**、允许 null）+ `@Verified` 注记 A21 原文；**保留 5 参辅助构造与 `ok()` 旧重载**（零破坏）；`data` 保持 `ReplyData{result, output}` 不变 |
| command/event/alert | `HmsData.java`（**类型修正**） | A20：`Item.code` `Integer` → `String`（文档 Type=text，示例 `0x16100083`） |
| command/drc/camera | `DrcVideoResolutionSetRequest` / `DrcCameraPhotoSizeSetRequest` / `DrcVideoStorageSetRequest` / `DrcCameraAimRequest` / `DrcCameraApertureValueSetRequest` / `DrcCameraDewarpingSetRequest` / `DrcCameraExposureModeSetRequest` / `DrcCameraExposureSetRequest` / `DrcCameraFocalLengthSetRequest` / `DrcCameraFocusModeSetRequest` / `DrcCameraFocusValueSetRequest` / `DrcCameraFrameZoomRequest` / `DrcCameraLookAtRequest` / `DrcCameraIsoSetRequest`（新建） | 分辨率 / 存储 / AIM / 光圈 / 去畸变（A16）+ 曝光/焦距/对焦/框选/LookAt/ISO（A17），结构与 services 侧 POJO 镜像；**A25 原文确认**：`drc_video_resolution_set`/`drc_camera_photo_size_set` 上行回包均为 `{result}` → **复用 `DrcResultReply`，不新增 Reply POJO** |
| command/drc/camera | `DrcCameraMechanicalShutterSetRequest` / `DrcCameraShutterSetRequest` / `DrcIntervalPhotoSetRequest` / `DrcCameraModeSwitchRequest` / `DrcCameraPhotoFormatSetRequest` / `DrcCameraPhotoStopRequest` / `DrcCameraPhotoTakeRequest` / `DrcCameraPointFocusActionRequest` / `DrcCameraRecordingStartRequest` / `DrcCameraRecordingStopRequest` / `DrcCameraScreenDragRequest` / `DrcCameraScreenSplitRequest` / `DrcGimbalResetRequest` / `DrcPhotoStorageSetRequest` / `DrcIrMeteringAreaSetRequest` / `DrcIrMeteringModeSetRequest` / `DrcIrMeteringPointSetRequest`（新建，共 17 个） | A18：3 项 DRC 独有缺 POJO + 13 项 DRC 侧缺失（镜像 services POJO）+ 1 项两通道均缺；结构与 services 侧一一对应 |
| command/drc/camera | `DrcCameraPhotoTakeReply.java`（新建） | A18 矛盾 2：DRC 回包多 `status`，不能复用 `DrcResultReply`/`NoOutputReply` |
| command/drc/camera enumtype | `VideoResolution.java` / `PhotoResolution.java` / `CameraType.java` / `ApertureValue.java` / `DewarpingState.java` / `ExposureMode.java` / `ExposureValue.java` / `FocusMode.java` / `IsoValue.java`（新建） | 分辨率/相机类型/光圈/去畸变（A16）+ 曝光模式/曝光值/对焦模式/ISO（A17）+ 快门值/定时拍间隔/红外测温模式/云台复位模式/夜景模式/开关状态（A18） |
| telemetry/enumtype | `CameraMode.java` | + `TIMED_PHOTO(4, "定时拍")`（A18：文档 camera_mode 新增值 4，现枚举仅 0–3） |
| command/drc/up | `CameraStatePushData.java`（**类型/Javadoc 修正**） | A24：`photoSize` `String` → `Integer`（enum_int {0:4K,4:8K}）；`intervalPhotoInterval` `int` → `Double`（示例 2.5）；`cameraMode`/`photoFormat` 两处 Javadoc 修正；**保留** `nightModeSettings`/`photoFormat`（本批为精简原文，不得据此删字段）。**A29 全量原文复核（2026-10-14）**：①**确认不新增任何扩展字段**（§5-33 结案）；②`photoFormat` 值域 `{7:RJPEG,16:DLT664}` 与 SDK 一致（§5-34 结案）；③**补 Javadoc**：`nightModeSettings.nightMode` 值域补 `2=自动`、`denoiseLevel` 值域补 4 档、`isWorking`/`nightSceneModeSuggestion` 值域补全；④`MediaStorage` 存储类型集合补 `vision`（`{current, vision, ir}`）；⑤`linkageZoomState` 保持 `int` + Javadoc 注明值域（见 §5-35） |
| command/drc/up | `CameraOsdInfoPushData.java`（**字段补全 / 类型修正**，A38） | **待确认（§5-46 / §5-47）**：`IrLense` 补 `List<Integer> thermalSupportedPaletteStyles`（文档 `ir_lense.thermal_supported_palette_styles` Type=`array`、`item_type=enum_int`，A14 + A38 两页均有定义）；`IrLense.irZoomFactor` `int` → **`Double`**（文档 `ir_zoom_factor` Type=`float`、`{"max":20,"min":2}`，`float`→`Double` 既定映射）；`@Verified(basis)` 指向 remote-control 页；属 **breaking**，随 `1.18.0.0` 发布 |
| command/drc/ai | `AiSpotlightZoomSelectRequest.java`（**类型修正**） | §5-45 已决策（2026-10-14）：`centerX`/`centerY`/`width`/`height` **`Double` → `Integer`**（文档 Type=`int`、constraint `[0,10000]`）；`@Verified(basis)` 更正为 `{"max":10000,"min":0}`（原 basis `{max:1,min:0}`×10000 已过时）；**删除** Javadoc「文档原文 height/width 单位互换、已修正」段（本批文档单位描述正确）；同步改 `AiRequestTest` / `tdd-test-cases.md` / `architecture-design.md` |
| model | `GroundStationModel.java`（**新建**） | A26：O4 地面站型号枚举（SDK 现无 `GroundStation`/`Basestation`/`O4` 型号） |
| telemetry | `GroundStationOsd.java` / `GroundStationState.java`（**新建**） | A26：O4 osd/state 属性（约 15 个子结构：`position_state`/`battery`/`rtcm_info`/`wireless_link_topo`/`peripherals_info`/`peripheral_list`/`sub_device`/`alternate_land_point`/`network_state`/`storage`/`tilt_angle`/`self_converge_coordinate` 等） |
| command/peripheral（**新建包**） | `PeripheralInfo.java` / `PeripheralCapability.java` / `RidCapability.java` / `AdsbCapability.java` / `DockControlCapability.java` | A26：外设接入登记（`peripherals_info`）与能力集（`peripheral_list`）POJO + `package-info.java` |
| telemetry/enumtype | `GroundStationModeCode.java` / `SupplyPowerMode.java` / `FixedState.java` / `CalibrationState.java` / `BasestationWorkMode.java` / `UnderWorkMode.java` / `RtcmDeviceType.java` / `RtcmSourceType.java` / `PeripheralPort.java` / `PeripheralType.java` / `LanStatus.java` / `PoeStatus.java`（**新建**） | A26：O4 枚举族（`NetworkType` 值域须与现有网络类型核对，见 §5-9） |
| telemetry/enumtype | `GroundStationHomePositionIsValid.java`（**新建**） | §5-31/§5-38 决策（2026-10-14）：O4 地面站 2 值枚举 `{0:无效, 1:有效}`，`@DocUrl` O4 地面站属性页；**不复用** Dock 枚举 |
| telemetry/enumtype | `Dock2HomePositionIsValid.java`（**新建**）+ `HomePositionIsValid.java` → `Dock3HomePositionIsValid.java`（**改名**） | §5-38/§5-36 决策（三枚举）：Dock2 2 值 `{0:无效,1:有效}` / Dock3 4 值 `{0:都无效,1:都有效,2:航向有效,3:经纬度有效}`，设备专属命名（AGENTS §6.1）；运行时零引用（仅 `HomePositionIsValidTest` + `tdd-test-cases.md` §7.17）→ 同步改测试与文档；属 public API 改名（breaking），随 `1.18.0.0` 发布 |
| protocol/method | `StatusMethod.java`（**核对**） | A26：`update_topo` Topic 为 `sys/product/{gateway_sn}/status`（`sys/` 前缀，非 `thing/`），须与现有实现核对 |
| telemetry | `OsdField.java` / `StateField.java` | A26：补 O4 地面站属性常量；**A27**：`HEADING` 注释「0-359」→ **`-180~180`**、`HOME_POSITION_IS_VALID` 注释「0=无效/1=有效」→ **按设备分别说明**（Dock2 2 值 / Dock3 4 值，见 §5-38）；**A31**：`RAINFALL` 描述「降雨量（mm）」→ **改 `Integer`** + 描述「降雨量（降雨等级）」（文档 enum_int，§5-39 已决策）；`WIND_SPEED` 注释「0.1 m/s，需除以 10」→ 更正为文档 **float / m/s**（§5-40）；`COVER_STATE` 注释补 `2=半开 / 3=舱盖状态异常`（§5-40） |
| telemetry | `DockOsd.java`（**类型 / Javadoc 修正**） | A27-1：`heading` `Integer` → **`Double`**（Dock2/Dock3 页均 `double` / -180~180 / pushMode 0；**Dock 侧不加 `@Deprecated`**，文档未声明废弃）；**A31**：`rainfall` `Double`（注释 mm）→ **改 `Integer`**（文档 enum_int，§5-39 已决策）；`coverState` 注释补 `2=半开 / 3=舱盖状态异常`（§5-40） |
| telemetry | `StateField.java` | **待确认（§5-77 / §5-82，A49）**：M4D/M4TD 新增 pushMode=1 字段 → + `BEST_LINK_GATEWAY`（text）、`FLYSAFE_DATABASE_VERSION`（text）；`DONGLE_INFOS`/`OFFLINE_MAP_ENABLE` 注释「M400 only」→ 补 M4D/M4TD 适用范围 |
| telemetry | `DroneOsd.java` / `OsdField.java` | **已决策（§5-78）+ 待确认（§5-81），A49**：`DroneOsd` + `ftsInfo`（**新建 `FtsInfo` record + `FtsStatus` 枚举**，§5-78 方案 A）、`cameras`（`CameraInfo` 扩 20 字段，§5-81 待确认）；`OsdField` + `FTS_INFO` |
| telemetry/nested | `CameraInfo.java` / `IrMeteringPoint.java` / `IrMeteringArea.java` + `IrMeteringMode.java`（新建，A49） | **待确认（§5-81）**：`CameraInfo` 补 20 字段（`photoStorageSettings`/`videoStorageSettings`/`wide*`/`zoom*`/`irMetering*`）；新 struct `IrMeteringPoint{x,y,temperature}`/`IrMeteringArea{x,y,width,height,averTemperature,minTemperaturePoint,maxTemperaturePoint}`；属 **breaking**，随 `1.18.0.0` 发布 |
| telemetry/enumtype | `DroneModeCode.java` | **待确认（§5-79，A49）**：+ `AIRPORT_AREA_WAYLINE(21, "进离场航线飞行过程中")`；同步更正 `@Verified(basis)` 值域至 0-21 |
| protocol/method | `PropertySetMethod.java` | **待确认（§5-80，A49）**：+ `COMMANDER_FLIGHT_HEIGHT`、`COMMANDER_MODE_LOST_ACTION`（M4D/M4TD 页 accessMode=rw；现 18 项仅据 Dock3+M3D 文档） |
| telemetry/nested | `DronePositionState.java` + `PositionQuality.java` | **待确认（§5-82，A49）**：`DronePositionState` Javadoc「quality 仅 1-5、不含 10」→ 补 `10=RTK fixed`（M4D/M4TD 页含之）；`PositionQuality` basis/描述补飞行器适用性（枚举值域已含 10 ✓，`Integer` 字段可承载，**不改类型**） |
| telemetry | `DockOsd.java`（**类型核对 + Javadoc**） | **待确认（§5-83，A50）**：`electricSupplyVoltage` `Double` vs Dock1 页 `int`/V（跨页冲突）；**§5-86**：`coverState`/`putterState` Javadoc 补 4 值 |
| telemetry | `DockOsd.java` / `DroneAuthorityInfo.java`（新建，A50） | **待确认（§5-84）**：`DockOsd` 补 `droneAuthorityInfo` 字段 + 新建 `DroneAuthorityInfo` record（`controlSource`/`locked`/`payloads[]`）；核对 `OsdField`/`StateField` 对该字段的重复定义 |
| telemetry | `StateField.java`（**Javadoc**） | **非阻塞（§5-87，A50）**：`WPMZ_VERSION` 归属注释由「飞行器 state」改为「机场/飞行器 state」 |
| protocol/method | `StatusMethod.java`（**Javadoc 修正**） | **非阻塞（§5-90，A51）**：`UPDATE_TOPO` 描述「data 含 sub_type 区分上线 0/下线 1」→ 更正为「上线/下线由 `sub_devices` 是否为空区分；`sub_type` 为网关子设备的产品子类型（参考产品支持）」 |
| command/status | `UpdateTopoReplyData.java`（**字段集核对**） | **待确认（§5-89，A51）**：Dock1 页 `status_reply` data **仅 `result`**，SDK 多 `subType`（`@Inferred`）→ 核对 `subType` 权威来源；另 `UpdateTopoData`「+ `base_info`」（§4 line 271，源自 O4 页）归属见 §5-91 |
| command/request/registration | `AirportOrganizationBindReply.java`（**字段名修正**） | **待确认（§5-92，A4/A52）**：`ErrorInfo.errSn`（SNAKE_CASE → `err_sn`）**不匹配**文档 wire key `sn` → 改字段名为 `sn`；属 **breaking**，随 `1.18.0.0` 发布 |
| command/request/registration | `AirportOrganizationGetReply.java`（**补 `Output`**） | **待确认（§5-93，A4/A52）**：现仅 `result` + `@Inferred`「output.airports」（假设**错误**）→ 补 `Output{organizationName}`（文档 `output.organization_name`）+ 升 `@Verified`；属 **breaking** |
| command/request/registration | `AirportBindStatusReply.java`（**补 `Output` + Javadoc**） | **待确认（§5-94，A4/A52）**：现仅 `result`，缺 `Output{bindStatus[]{sn, isDeviceBindOrganization, organizationId, organizationName, deviceCallsign}}`；Javadoc「result 0=未绑定/1=已绑定」**语义冲突**（文档 result=返回码 0 成功/非 0 错误）→ 补 `Output`/`BindStatus` + 修 Javadoc；属 **breaking** |
| command/service/live | `LiveStartPushRequest.java`（**Javadoc**） | **非阻塞（§5-95/96，A53）**：`url_type` Javadoc 现仅列「1=RTMP、4=WebRTC」→ 补全「0=声网、1=RTMP、3=GB28181、4=WebRTC（**无 2**）」+ `url` 按 `url_type` 的四类格式说明（RTMP/GB28181/Agora/WebRTC-WHIP）；枚举化见 §5-95 |
| command/service/live | `LiveLensChangeRequest.java`（**Javadoc**） | **非阻塞（§5-96，A53）**：`video_type` enum_string{ir,normal,wide,zoom} Javadoc 补全取值；是否补镜头类型枚举见 §5-95 |
| command/request/config | `StorageConfigGetReply.java`（**字段名修正**） | **待确认（§5-97，A54）**：`Credentials.expireTime`（SNAKE_CASE → `expire_time`）**不匹配**文档 wire key `expire` → 改字段名为 `expire`（或加 `@JsonProperty("expire")`）；与 §5-52（type `Long` vs `int`）为**同一字段**，需一并处理；属 **breaking**，随 `1.18.0.0` 发布 |
| command/event/media | `FileUploadCallbackData.java`（**类型核对**） | **待确认（§5-98，A54）**：`MediaFileMetadata.gimbalYawDegree` 文档 Type=`float` 但同页示例为字符串 `"-91.40"`，SDK 现 `String`（对齐示例）→ 是否改 `Double`（对齐 Type），或保持 `String` + `@Inferred` |
| command/event/wayline | `FlightSetupExceptionNotifyData.java`（**新建**）+ `EventMethod.java`（**Javadoc/`@Inferred` 更正**） | **待确认（§5-100，A55）**：`flight_setup_exception_notify` 事件 data = `{sn, timeout_time, timestamp, flight_type(enum_int 1航线/2指令飞行)}`（Dock1 页）；SDK `EventMethod.FLIGHT_SETUP_EXCEPTION_NOTIFY` 常量**已存在**（needReply=1 ✓）但 Javadoc 记「data 含 `error_code`/`error_reason`」**与文档矛盾**，且**无 POJO** → 新建 record（4 字段）+ **更正常量 Javadoc 字段描述**（L75）；类级 `@Verified`(L46) 与常量 `@Inferred` 自相矛盾需厘清（该 `@Inferred` 实为 `need_reply`）；纯新增（字段/类型无破坏） |
| command/service/wayline | `ServiceMethod.FLIGHT_SETUP_ABORT`（**Javadoc**，复用 `NoParameterRequest`+`NoOutputReply`） | **非阻塞（§5-101，A55）**：`ServiceMethod.FLIGHT_SETUP_ABORT` 存在 ✓；data=null、reply 仅 `result`（0 成功 / 326108 当前状态不支持 / 326109 飞行器已起飞不支持取消）→ reply 错误码补入 Javadoc；**无新建 POJO**（`NoParameterRequest`/`NoOutputReply` 已覆盖） |
| command/service/wayline | `SimulateMission.java` / `FlighttaskPrepareRequest.java`（**字段集核对**） | **待确认（§5-102/§5-103，A55）**：Dock1 `flighttask_prepare` 页 `simulate_mission` **仅 3 字段（`is_enable`/`latitude`/`longitude`，无 `altitude`）** vs SDK `SimulateMission` 4 字段（`altitude` `@Inferred`，Dock3 页有）→ 核实 `altitude` 是否 Dock3 专属；同页**无** `rth_mode`/`wayline_precision_type`/`flight_safety_advance_check`（Dock3 页有）→ 核实是否 Dock3 专属（字段均包装类型，缺席 null，统一 POJO 超集暂可） |
| command/service/debug | `AirConditionerModeSwitchRequest.java` / `BatteryStoreModeSwitchRequest.java`（**字段名修正**） | **待确认（§5-104，A57）**：请求字段名 `mode` **≠** 文档（Dock1 远程调试页）`action` → 改名 `mode` → **`action`**（JSON `action`），与 `alarm_state_switch`/`ActionSwitchRequest` 统一；`@Inferred` 依据由「coverage-review 推断」改为「DJI 文档 A57」；属 **breaking**，随 `1.18.0.0` 发布 |
| command/service | `ActionSwitchRequest.java`（**清理/统一**） | **待确认（§5-105，A57）**：全仓无引用（疑似死代码，AGENTS §2）；裁决「删除并新建 `BatteryMaintenanceSwitchRequest`」或「保留为唯一通用 POJO（删除 3 个专用 POJO）」 |
| command/event/system | `ServiceProgressData.java`（**Javadoc 更正**） | **非阻塞（§缺口4，A5/A57 二次印证）**：类 Javadoc 方法名 `power_on`/`power_off`/`format_storage` 与文档实际 `drone_open`/`device_reboot`/`device_format` 不一致 → 更正；**不新增** `EventMethod`（进度事件按同名约定） |
| command/event/system | `ServiceProgressData.java`（**可选枚举**） | **可选（§5-106，A57）**：`progress.step_key` 各方法值域不同、现裸 `String` → 是否建模枚举（同 §5-95/§5-99） |
| command/request/registration | `ConfigReply.java`（**重建**） | **待确认（§5-108，A60/A7）**：现 `{Integer result(必填), appId, appLicense, url, token}` 与文档（Dock1 配置更新页 / O4 `config` 页）不符 → 重建 `{appId, appKey, appLicense, ntpServerHost, ntpServerPort(Integer)}`：`url`→`ntp_server_host`（名不符）、`token`→`ntp_server_port`（名+类型不符，文档 `int`）、补 `appKey`/`ntpServerPort`；文档回复 data **无 `result`** → 是否移除（现 `Objects.requireNonNull` 必填）待裁决；`@Inferred`（非 output 包裹）可据原文定稿；属 **breaking**，随 `1.18.0.0` 发布；同步改 `ConfigReplyTest` |
| command/drc/up | `HsiInfoPushData.java`（**Javadoc 更正**） | **非阻塞（§5-110，A61）**：`aroundDistances` 注释「每 30° 一个值（共 12 个）」→「**360 个**（每 1° 一个值）」，空数组=任意角度无障碍；`upDistance`/`downDistance` 单位「米」→「**毫米 / mm**」；**不改字段/类型**（字段名 `aroundDistances`→wire `around_distances` 与文档字段表一致；示例单数 `around_distance` 属文档自身偏差待 DJI 修订） |
| command/drc/flight | `HeartBeatRequest.java` / `HeartBeatReply.java` / `DrcMethod.DRONE_CONTROL` + `DroneControlRequest.java`（**废弃口径复核**） | **待确认（§5-109，A61/A15）**：Dock1 页（2025-11-12）`drone_control` 与 `heart_beat.seq` **均无废弃标注** → 与 O4 页（A15）「`drone_control` 已废弃、`heart_beat.seq`【废弃】」**冲突** → 复核 §4 L281（`DRONE_CONTROL → @Deprecated`）与 L283（`DroneControlReply` 新建）；`heart_beat` 是否补 `seq`（A61 down/up 均含）待裁决 |
| command/service/flight | `TakeoffToPointRequest.java`（**字段集核对**） | **待确认（§5-111，A61）**：Dock1 页 `takeoff_to_point` 为 O4（A10）页**子集**——**无** `rth_mode`/`commander_flight_mode`/`flight_safety_advance_check`（O4 有、SDK 有）、SDK **无** `exit_wayline_when_rc_lost`（文档标【废弃】）；核实是否 O4/Dock3 专属（与 §5-102/§5-103 同模式）；不改字段（或裁决按机型拆分） |
| protocol/method | `ServiceMethod.java`（**新增常量**） | **待确认（§5-113，A63）**：文档 Dock1「自定义飞行区」页 service `flight_areas_update`（请求 Data=null；reply `{result(int, 非0错误)}`）在 SDK `ServiceMethod` **不存在 ❌** → 补 `FLIGHT_AREAS_UPDATE("flight_areas_update", "自定义飞行区更新")`（并入「三 Dock 共有」或按机型归属待核）；请求复用 `NoParameterRequest`、回复复用 `NoOutputReply`；**更正 §5-76**（该 method 曾被列为「仅见 DJI 仓库、未见官方文档」→ 本页证实为官方协议） |
| command/request/flightarea | `FlightAreasGetReply.java`（**重建 `Output`**） | **待确认（§5-114，A63，breaking）**：文档 reply `output.files` 为**数组**，项 4 字段 `{name(text), url(text), checksum(text SHA256), size(int)}`（空数组=无自定义飞行区）；SDK 现 `output.file`（**单数** struct）仅 `{name, checksum}` → 改为**数组** `List<File>` 并补 `url`/`size`；同步改 `FlightAreasGetReplyTest`（现 SAMPLE_JSON 用单数 `file` 且无 `url`/`size`）；随 `1.18.0.0` 发布 |
| command/event/flightarea + command/request/flightarea | `FlightAreasSyncProgressData` / `FlightAreasDroneLocationData` / `FlightAreasGetRequest` / `FlightAreasGetReply`（**`@DocUrl` 更正**） | **待确认（§5-115，A63，元数据）**：4 类 `@DocUrl` 现统一指 `dock3/wayline.html`，但 A41 确证该页（航线管理）**不含任何 flight_areas 内容** → 更正为「自定义飞行区」页 URL（本页为 Dock1，三 Dock 归属待核）；`@Verified(basis)` 由 simulator 升为官方文档 |
| command/request/flightarea | `FlightAreasGetRequest.java`（**去留/复用核对**） | **待确认（§5-116，A63，非阻断）**：`flight_areas_get` 请求 Data=null，SDK 用专用空 record `FlightAreasGetRequest()`，未复用既有 `NoParameterRequest`（其 Javadoc 已将 data=null 的 `poi_mode_exit` 纳入复用清单）→ 裁决复用（消重，与 services/Pilot 惯例一致）或保留（requests 通道类型自洽）；两者均序列化 `{}`，非协议偏差 |
| command/service/psdk | `StorageConfigGetRequest.java`（**Javadoc 修正**） | **待确认（§5-117，A64）**：文档 Dock1「PSDK 功能」页 `storage_config_get.module` = `{0 媒体, 1 psdk ui 资源}`；SDK [`StorageConfigGetRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/config/StorageConfigGetRequest.java) Javadoc 现记「`0=媒体`、`1=日志`」（`@DocUrl` = `dock3/media.html`）→ 核对 `media.html` 原文后统一为「1=psdk ui 资源」（或注明上下文差异）；可选将 `module` 建模为枚举（现 `Integer`） |
| command/event/speaker | `SpeakerOutput.java` / `SpeakerTtsPlayStartProgressData.java` / `SpeakerAudioPlayStartProgressData.java`（**`@DocUrl` 更正**） | **待确认（§5-118，A64，元数据）**：3 类 [`@DocUrl`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/speaker/SpeakerOutput.java) 现指 `dock3/wayline.html`；但 `speaker_*_play_start_progress` 事件归属「PSDK 功能」页（Dock3 对应 `dock3/psdk.html`，与 services 侧 `speaker_*` POJO 一致）→ 待开发者确认归属后更正（URL 待提供） |
| command/service/psdk | `CustomDataTransmissionToPsdkRequest.java`（**basis / `@DocUrl` 更正**） | **待确认（§5-119，A65，元数据）**：文档 Dock1「PSDK 互联互通」页**显式列出** service `custom_data_transmission_to_psdk`（down，`{value(text <256)}`；reply `{result}`）→ 删去现 `@Verified(basis)` 中「**DJI 文档未显式列出此方法**」表述（该声明不成立）；`@DocUrl` 由 `dock3/psdk.html` 改为 `dock3/psdk-transmit-custom-data.html`（与事件侧 `CustomDataFromPsdkData` 一致） |
| command/service/esdk | `CustomDataTransmissionToEsdkRequest.java`（**basis / `@DocUrl` 更正**） | **待确认（§5-120，A66，元数据）**：文档 Dock1「ESDK 互联互通」页**显式列出** service `custom_data_transmission_to_esdk`（down，`{value(text <256)}`；reply `{result}`）→ 删去现 `@Verified(basis)` 中「**DJI Dock3 esdk.html 文档访问受限**（cookie 验证），依据 hivemind 实测」表述（该声明不成立）；`@DocUrl` 核对为 `dock3/esdk-transmit-custom-data.html`（与事件侧 `CustomDataFromEsdkData` 一致） |
| docs | `architecture-design.md` / `tdd-test-cases.md` / `README.md` | 同步协议覆盖与版本 |

> TDD 先行：实现前先在本报告与 `tdd-test-cases.md` 补全 Given-When-Then 用例（见 §7）。

---

## 5. 待确认项（`@Inferred` 清单）

> **2026-10-14 A35 二次文档核实**：§5-4 / 6 / 12 / 13 / 14 / 15 / 16 / 17 / 20 / 24 / 25 / 42 / 43 / 44 已回 DJI 官方文档逐项复核，结论见 [附录 A35](#a35-待确认项二次文档核实2026-10-14ai-直接抓取-dji-官方页)。

| # | 项 | 冲突/不确定点 | 处置 |
|---|---|---|---|
| 1 | `electric_supply_voltage` | SDK 注 mV/int，文档 double/V | **已结案（A26）**：该字段属 **O4 地面站**（dock1/2/3 均无），Type=`double`、单位 **伏 / V** → 按 `double` 建模 + `@Verified`。**修订（2026-10-14，A50）**：Dock1「设备属性」页**明确含 `electric_supply_voltage`**（Type=`int`、单位 V、pushMode=0）→ 原「dock1/2/3 均无」表述**不成立**；SDK 现为 `Double`，与 Dock1 页 `int` 存在类型差异（O4 页为 `double`）→ 跨页冲突待裁决（见 §5-83） |
| 2 | `heading` | SDK `int` + 注释「0-359，Dock2/Dock3 only」；**文档两处均有** | **已更正（A27）**：**Dock3 机场**属性 `heading` = `double`、**-180~180**、pushMode=0、**未声明废弃**；**O4 地面站** `heading` = `double`、0~360、pushMode=1、**明确已废弃**（A26）→ SDK 改 `double` + 修正注释范围为 **-180~180**；**Dock 侧不得标 `@Deprecated`**（文档未声明废弃），O4 侧建模时按废弃字段处理 |
| 3 | `acc_time` / `sub_device` 归属 | SDK 归 state，文档归 osd（相反） | **A26 佐证**：O4 页 `acc_time` pushMode=0（→ osd）、`sub_device` pushMode=1（→ state）→ 以文档为准，`@Inferred` 标注 |
| 4 | `video_resolution` 类型 | 文档标 `enum_string`，示例为数字 `0` | `@Inferred`，真机验证（A23 原文复现矛盾；**A29 三度复现**：Type=`enum_string` 值域 `{"0":"19201080","1":"38402160"}` vs 示例 `0`；**保持 `String`**，对齐 Type 列） |
| 5 | `drc_camera_state_push.photo_size` 语义 | 文档原文 `enum_int` {0:4K, 4:8K}；SDK 现为 `String` | **A24 原文确认 → 改 `Integer`** |
| 6 | `fileupload start_time/end_time` 单位 | O4 秒 vs Dock3 毫秒 | **A35 核实**：`log.html` 的 `fileupload_list` 回包 `list[].start_time/end_time` Type=`int`、单位「**毫秒 / ms**」（Dock3）；O4 为秒。→ **字段统一 `Integer`**，单位差异仅 Javadoc 注明 + `@Inferred`，不按机型分叉。**A40 复核提示**：毫秒口径下值为 13 位（超 int32），「统一 `Integer`」**不可行** → 须修订（见 §5-52） |
| 7 | O4 `cmd.html` live/camera 外设段字段 | 文档抓取截断，字段未取全 | **已结案（A5）**：O4 `cmd.html`（events + services）完整原文已由 A5 提供——`live_*` 5 项（`live_start_push`/`live_camera_change`/`live_lens_change`/`live_set_quality`/`live_stop_push`）+ `camera_*` 10 项（`camera_aim`/`camera_look_at`/`camera_frame_zoom`/`camera_mode_switch`/`camera_photo_take`/`camera_photo_stop`/`camera_recording_start`/`camera_recording_stop`/`camera_screen_split`/`camera_screen_drag`）字段/枚举均已取全 → **不再阻塞**（原「截断」记录已过期，属记录错误，2026-10-14 修正） |
| 8 | 飞行器 OSD `fts_info` 结构 | FTS 页仅引用，未定义字段/枚举 | **已降级（2026-10-14，开发者质疑后修正）**：**非阻塞项**。FTS 页（`.../dock/dock3/fts.html`，2026-10-10）中，`fts_verification_code` 是服务请求 `data` 的**顶层扁平字段**；`fts_info.fts_verification_code` 仅出现在 **Description 说明文字**（「从飞行器 OSD 的 `fts_info.fts_verification_code` 中获取…」），属**取数来源表述，非结构定义**。全站检索 `fts_info` 零命中 → **该 struct 从未被任何官方页面定义**。处置：**服务侧 `drone_propeller_stop_v1` 不受阻塞**（`DronePropellerStopV1Request{String ftsVerificationCode}` 可直接建模）；OSD 侧 `fts_info` **已定案（2026-10-14）→ 方案 B：暂不建模**（不新增 `FtsInfo` record，也不做单字段 `@Inferred` 占位；待 DJI 补定义页后再评估）。**本项结案** ｜ **修订（2026-10-14，A49/§5-78）**：M4D/M4TD 设备属性页（`aircraft/m4d-properties.html`）**已首次完整定义 `fts_info` struct**（`fts_status` enum_int 6 值 + `fts_verification_code`/`fts_valid_duration`/`fts_timestamp`/`fts_sn`/`fts_firmware_version`）→ 原「**全站零命中 / 从未被任何官方页面定义**」依据**撤销**；**已裁决 → 方案 A：建模**（新建 `FtsInfo` record + `FtsStatus` 枚举 + `OsdField.FTS_INFO` + `DroneOsd.ftsInfo`，见 §5-78）→ **原结案撤销** |
| 9 | `network_state.type` 值域 | 地面站 1=4G/2=以太网，SDK `NetworkType` 值域不同 | **A26 已给值域** `{1:4G, 2:以太网}`（`quality` 仅 4G 时有值）→ 按文档建立/复用枚举 |
| 10 | `drc_camera_photo_format_set`（v1.16.1） | 文档存在，SDK `DrcMethod` 未收录 | **A18 已收原文**，一并补录（`photo_format` 复用 `PhotoFormat` 枚举） |
| 11 | `AirSenseWarningData` 类型 / `need_reply` | 文档 int/float vs SDK `Double`；Javadoc `need_reply=0` 与 `EventMethod`(=1)/页面示例(=1) 矛盾 | **已结案（A30，2026-10-14）**：①`need_reply` 以示例 **1** 为准 → **改 `AirSenseWarningData` Javadoc 为 1**（`EventMethod.AIRSENSE_WARNING` 已是 1 ✓，原 Javadoc 为 SDK 内部缺陷）；②`altitude`/`relativeAltitude`/`distance` 文档 **int** vs SDK `Double` → **改 `Integer`**；③`latitude`/`longitude`/`heading` 文档 **float** → **保持 `Double`**（DJI `float` → Java `Double` 既定映射，保 6 位小数） |
| 12 | 地面站与飞行器兼容性矩阵 | `DeviceCompatibility` 是否需新增条目 | **已结案（A43，2026-10-14）**：开发者提供 DJI「支持产品列表与固件版本」页原文（对应 `overview/product-support.html`，与本类 `@DocUrl` 一致）。该页为「产品 + 最低固件版本」表，**非逐对搭配矩阵**；其机场上云产品族与 `DeviceCompatibility` 的 Dock↔Drone 搭配**吻合**（机场3↔M4D/4TD、机场2↔M3D/3TD+M30/30T、机场↔M30/30T）→ **Dock↔Drone 无需新增/修改 ✓**。**新决策点**：①「最低固件版本」是否纳入 SDK（§5-60）；②`RC Pro 行业版` 亦列于机场上云表 → `isCompatible(RC_PRO)` 现仅映射 Mavic3E/3T，是否需补 Dock 侧机型（§5-61） |
| 13 | `drc_camera_mode_switch.camera_mode` 值 4 | 文档新增 `4=定时拍`，SDK `CameraMode` 仅 0–3 | **A35 确认（remote-control 页 `drc_camera_state_push.camera_state.camera_mode` = {0..4}）**：值 4 有据 → **扩展 `CameraMode` +`4=定时拍照`**；events 侧 `camera_mode` 无 4，不影响既有反序列化 |
| 14 | `drc_camera_photo_take` 回包 `status` | DRC 回包比 services 多 `status`（enum_string） | 新增专用 Reply，待确认出现条件 |
| 15 | `drc_interval_photo_set.interval` | 文档键混合（含小数 `0.7`） | **A35 结案**：Cloud-API-2 交叉源 `IntervalTakePhotoSetParam.interval` = `double`（method `drc_interval_photo_set`）→ **承载 `Double`**（文档含 `0.7` 小数档，`Double` 唯一无损）；**A38 口径补充（Dock2 页）**：文档 Type 出现**三口径不一**——Dock3 页 `int` / Dock2 页 `enum_string`（键为数字串 `"1"`…`"0.7"`）/ Cloud-API-2 `double` → **处置不变，仍取 `Double`**（无损容纳整数与 `0.7` 小数，规避 `enum_string` 键「数字串 vs 数字」二义） |
| 16 | `return_home` 回包 `output.status` | 文档 8 值 `enum_string`，SDK 仅有 `NoOutputReply` | 新增 `ReturnHomeReply`（A19） |
| 17 | `status` 枚举值域两处不一致 | 进度类 9 值（含 `partially_done`）vs `return_home` 8 值 | 补 Javadoc 时区分 |
| 18 | `return_home_info.home_dock_sn/multi_dock_home_info` | 现 `@Inferred`，A19 原文已给出完整字段 | **已结案（A32，2026-10-14）**：Dock3 `return_home_info` 原文明确 `home_dock_sn`（"蛙跳任务中当前选择的机场 home 点 SN，普通任务无该字段"）与 `multi_dock_home_info`（`array size:2`，普通任务无）的字段与类型 → `ReturnHomeInfoData` 类级 `@Inferred` **升 `@Verified`**（**A34 勘误**：原 `@Inferred` 理由「Dock1 无此字段」**属实**——Dock1 页确未定义该两字段——但**标注类型不当**：存在性与可选性已由 Dock2/Dock3 文档明确声明，应 `@Verified`；结案结论不变）（**Dock2 页 A33 二次印证**：字段 / 类型 / 可选性描述与 Dock3 完全一致，无双机型分叉） |
| 19 | `HmsData.Item.code` 类型 | SDK `Integer` vs 文档 `text`（示例 `"0x16100083"`） | **修正为 `String`**（A20） |
| 20 | `EventMethod` 是否补登 `hms_inner` | 文档尾注提及，SDK 未登记 | **A35 结案（hms.html）**：全文（Data 表 + Example）**无 `hms_inner`** → **不补登**（原「尾注提及」系误记） |
| 21 | `ReplyEnvelope` 能否承载扁平回复 | `unlock_license_list`/`switch` 回复 data 为扁平字段，非 `{result, output}` | **已决策（2026-10-14）→ 方案 B**：**否决泛型化**，新增 `FlatReplyEnvelope`（`data` 为 `Object`，Reply 自带 `result`），对齐既有 `RequestReplyEnvelope` 模式；`ReplyEnvelope` 结构不改（见 §4） |
| 22 | `ReplyEnvelope` 缺 `gateway` | 文档 services_reply 示例含 `gateway`（A21），`EventEnvelope` 已有、`ReplyEnvelope` 无 | **已决策（2026-10-14）→ 方案 A**：`ReplyEnvelope` 补 `String gateway`（**置于末位**、允许 null、NON_NULL 缺席不输出），与 `EventEnvelope` 对齐；**保留 5 参辅助构造 + `ok()` 旧重载**（既有调用点零破坏）；新 `FlatReplyEnvelope` 一并带 `gateway`；`RequestReplyEnvelope` 无文档依据**不动** |
| 23 | `PowerUnlock` 空 struct `{}` | 文档 power_unlock 无字段 | 空 record 占位 vs 省略字段 |
| 24 | `FtsServerType` 是否引入枚举 | `fts_server_type` enum_int {0,1} | **A35 确认（fts.html）**：值域 `{0:DJI FTS 服务器,1:自定义 FTS 服务器}` → 引入 `FtsServerType` 枚举（A22） |
| 25 | `fts_config.mqtt_broker` 条件必填 | `fts_server_type=1` 时必填 | **A35 确认（fts.html）**：Description「当 fts_server_type 为 1（自定义）时必填」→ 允许 null + Javadoc 说明，**不做校验**（A22） |
| 26 | `drc_camera_photo_size_set`（命令）是否存在 | A24 时开发者反馈文档中未找到该 DRC 指令 | **已结案（A25）**：原文确认该 DRC down 指令存在 → 恢复收录 `DRC_CAMERA_PHOTO_SIZE_SET` + `DrcCameraPhotoSizeSetRequest` |
| 27 | `interval` 类字段小数承载 | `interval_photo_interval` 示例 `2.5` vs SDK `int`；A18 `interval` 键含 `"0.7"` | 建议统一改 `Double`（A24）；**A29 复核**：文档 Type 列为 `int`、示例为 `2.5`（**文档自相矛盾**）→ 仍按 `Double` 承载（可兼容整数与小数，不丢精度） |
| 28 | `CameraStatePushData.cameraMode` Javadoc | SDK 注「2=拍照+录像」，文档为 2=智能低光/3=全景拍照/4=定时拍照 | **修正注释**（A24，不改字段）；**A29 原文复现**值域 `{0:拍照,1:录像,2:智能低光,3:全景拍照,4:定时拍照}` → 确认为定稿值域 |
| 29 | `property/set_reply` 的 data 形态 | 文档为「按属性名嵌套、叶子 `{result}`」（如 `{"distance_limit_status":{"state":{"result":0}}}`），既非 `{result, output}` 亦非扁平（A26） | **已决策（2026-10-14）→ 29-A**：**复用 `FlatReplyEnvelope`**（`data` 为 `Object` 可承载任意嵌套），不新增第三信封；§4 不新增信封类 |
| 30 | O4 地面站是否纳入本次 v1.18 升级范围 | 本项**非真实待确认项**：§0 决策记录自始已定为「全量同步：O4 地面站全模块」，此条属**记录冗余** | **已结案（2026-10-14）**：O4 **全量纳入**（无需再议）；按 §4 清单新建型号枚举 + `GroundStationOsd`/`GroundStationState` + 外设 POJO（5 类）+ 枚举族（12 个）+ `OsdField`/`StateField` 常量。**O4 接口原文已全部收齐**（A1–A14 + A26/A27，覆盖 `properties`/`device`/`requests`/`cmd`/`drc`/`airspace-safety`） |
| 31 | `home_position_is_valid` 值域按设备不同 | Dock2 **2 值** `{0:无效,1:有效}`；Dock3 **4 值** `{0:都无效,1:都有效,2:航向有效,3:经纬度有效}`；O4 **2 值**（A27 / A31） | **已决策（2026-10-14）→ 按设备分别建模（三枚举）**：**新建** `Dock2HomePositionIsValid`（2 值，`@DocUrl` Dock2 页）+ **`HomePositionIsValid` 改名 `Dock3HomePositionIsValid`**（4 值，`@Verified` Dock3 页）+ **新建** `GroundStationHomePositionIsValid`（2 值，`@DocUrl` O4 页）；三枚举**互不复用**（各设备侧 `fromCode` 值域精确、无误判，如 O4/Dock2 侧不会误判 2/3）；`OsdField.HOME_POSITION_IS_VALID` 注释按设备分别说明（Dock2 2 值 / Dock3 4 值 / O4 2 值）；POJO 字段类型保持 `Integer`（枚举仅作反查助手）；命名消歧见 §5-36（已按本决策修订） |
| 32 | `network_state` 值域 / 类型两页不同 | Dock3：`quality` 6 档 `{0..5}`、`rate` **float**；O4：`quality` 3 档 `{0:差,1:中,2:好}`、`rate` **int**（A27-4） | SDK 现按 Dock3 建模（6 档 / `Double`）；O4 已全量纳入 → O4 侧 `@Inferred` 标注值域差异，不新建第二份 `NetworkState` |
| 33 | `drc_camera_state_push.camera_state` 是否需扩展字段 | API2 SDK `CameraState` 含 `videoResolutionWide/Zoom`、`photoSizeWide/Zoom`、`thermalTempExceedRangeValid`、`thermalSuperResolutionEnable`、`isothermEnable/RoiBasedRenderingEnable/ColorRenderingType/LowerThreshold/UpperThreshold`（A28），本 SDK 无 | **已结案（A29，2026-10-14）**：v1.18 文档 `camera_state` **无任何扩展字段**（无 `*_wide`/`*_zoom`/`thermal_*`/`isotherm_*`）→ **不新增**；API2 扩展字段族属跨代专有，**不予采纳** |
| 34 | `photo_format` 值域 | 本 SDK `PhotoFormat{RJPEG(7), DLT664(16)}`；API2 SDK `RAW(0)/JPEG(1)/DLT664(16)`（A28） | **已结案（A29，2026-10-14）**：v1.18 文档值域 `{"7":"RJPEG","16":"DLT664"}` 与 SDK **逐值一致** → **保持零改动**；API2 值域**不予采纳**（跨代差异） |
| 35 | `linkage_zoom_state` 承载类型 | 文档 Type=**bool** 但值域 `{"0":"关闭","1":"开启"}`、示例 `0`（自相矛盾）；SDK 为 `int` | **已决策（2026-10-14）→ 保持 `int`**：与文档示例 `0`（数字）及值域表述一致，Jackson 可安全承载；Javadoc 注明值域 `{0:关闭, 1:开启}`；**不改字段类型** |
| 36 | `HomePositionIsValid` 枚举命名 / 拆分 | §5-38 决策为三枚举后，现有泛化名 `HomePositionIsValid`（实为 Dock3 定义，4 值）含义模糊、未标设备归属 | **已决策（2026-10-14，随 §5-38 修订）→ 拆分 + 改名**：现有 `HomePositionIsValid`（4 值）**改名 `Dock3HomePositionIsValid`**；另**新建** `Dock2HomePositionIsValid`（2 值）与 `GroundStationHomePositionIsValid`（2 值）→ 三个设备专属名（AGENTS §6.1「避免歧义命名」）；**运行时零引用**（全仓核实：仅 `HomePositionIsValidTest` + `tdd-test-cases.md` §7.17 引用，`DockOsd` 字段为 `Integer` 未引用）→ 同步改测试与文档；属 **public API 改名（breaking）**，随 `1.16.1.3 → 1.18.0.0` 主版本一并发布 |
| 37 | `rid_infos` / `adsb_infos` 的 `need_reply` 取值 | 文档 example **无 `need_reply` 字段**（与 `airsense_warning` 的 `need_reply=1` 不同），字段表亦未标注（A30） | **已决策（2026-10-14）→ 取 `0`（单向通知）**：`EventMethod.RID_INFOS` / `ADSB_INFOS` 的 need_reply 参数取 `0`，并在枚举常量上标注 `@Inferred(reason="DJI 文档 example 与字段表均未标注 need_reply，按本 SDK events 通道「未标注即不要求回复」惯例推断为 0", verifyPoint="真机报文核对 events 通道是否回 reply")`；真机核对后转 `@Verified` |
| 38 | `home_position_is_valid` **机场家族内部机型差异** | Dock2 **2 值** `{0:无效,1:有效}` vs Dock3 **4 值** `{0:都无效,1:都有效,2:航向有效,3:经纬度有效}`（A31，A27-2 悬置假设已证实）；O4 亦为 2 值 | **已决策（2026-10-14）→ 方案 A（三枚举，最忠实）**：**新建** `Dock2HomePositionIsValid`(2 值) + **新建** `Dock3HomePositionIsValid`(4 值，由现 `HomePositionIsValid` 改名) + **新建** `GroundStationHomePositionIsValid`(2 值)；三枚举互不复用，各设备侧 `fromCode` 值域精确；与「按设备分别建模」原则完全一致。**代价（已知并接受）**：枚举由 1 个增至 3 个，Dock2 与 O4 值域相同则各自独立定义（有意重复，换取设备归属清晰）；使用者需按设备选枚举 |
| 39 | `rainfall` 类型 / 语义 | Dock2+Dock3 两页均为 **enum_int** `{0:无雨,1:小雨,2:中雨,3:大雨}`；SDK 为 `Double rainfall` 且注释「降雨量（mm）」（A31） | **已决策（2026-10-14）→ 改 `Integer`（文档口径）**：`DockOsd.rainfall` `Double` → `Integer` + Javadoc 枚举值 `{0:无雨,1:小雨,2:中雨,3:大雨}`；`OsdField.RAINFALL` 描述同步由「降雨量（mm）」改为「降雨量（降雨等级）」。属 **breaking（`Double`→`Integer`）**，随 `1.18.0.0` 发布 |
| 40 | `cover_state` / `wind_speed` Javadoc 与文档不符 | `cover_state` 文档 **4 值** `{0:关闭,1:打开,2:半开,3:舱盖状态异常}`，SDK 注释仅 2 值；`wind_speed` 文档 **float / m/s**，`OsdField` 注释却为「0.1 m/s，上报值需除以 10」（与 `DockOsd` 注释「m/s」内部矛盾）（A31） | **非阻塞 → Javadoc 更正**（不改字段类型）：`cover_state` 补 2/3 值；`wind_speed` 统一为「m/s，文档 Type=float」 |
| 41 | Dock3 页示例与字段表自相矛盾 | Dock3 页示例用 `air_conditioner_mode`（扁平 int），而其字段表为 `air_conditioner` struct；示例另含字段表未列的 `electric_supply_voltage` / `flighttask_prepare_capacity`（A31-2） | **记录在案，不阻塞实现**：以**字段表**为准（`air_conditioner` struct 保留现状）；示例偏差待 DJI 修订 |
| 42 | `return_home_info.last_point_type` / `multi_dock_home_info.plan_status` 是否建枚举 | 文档均为 `enum_int`：`last_point_type` 2 值 `{0:最后一点在返航点上空, 1:不在返航点上空}`；`plan_status` 4 值 `{0:规划失败或正在规划中, 1:规划路径不可达, 2:规划路径因电量不可达, 3:目标可达}`。SDK 现为裸 `Integer`，**无对应 enumtype 枚举**（A32-1） | **待确认**：是否按本 SDK「`enum_int` → 提供反查枚举助手 + 字段保持 `Integer`」惯例补建（`LastPointType` / `HomePlanStatus`，命名须避与 `FlighttaskProgressStatus` 歧义）；**不补建亦不违反协议忠实性**（字段类型已一致），仅影响便捷性 |
| 43 | 共享 `PathPoint` 的 `@DocUrl` 与 `height` 语义 | `PathPoint`（`latitude`/`longitude`/`height`）被 `ReturnHomeInfoData` / `FlyToPointProgressData` / `TakeoffToPointProgressData` 三事件复用，但 `@DocUrl` 现指向 `dock3/drc.html`；本批 events 原文明确 `height` = **椭球高**（float / 米）（A32-1） | **待确认（非阻断）**：①`@DocUrl` 是否改指向 events 页（或保留 `drc.html`，如确为 DRC 与 events 多页共用同一 `planned_path_points` 结构）；②Javadoc 补 `height`「椭球高」语义。仅涉注释 / 文档追溯准确性。（**Dock2 页 A33 亦标注 `height` = 椭球高 → 双页印证**） |
| 44 | Dock1 页 `planned_path_points` / `last_point_type` 的描述语义未入 Javadoc | Dock1 页明确：`planned_path_points`「**每次推送为全量更新**」（非增量）；`last_point_type` 是**终端显示判定**依据——0=「可以用此字段决定显示方式…可以显示轨迹最后一个点到返航点的连线」，1=「**不应显示**该连线」，且「无法到达返航点的原因：返航点在**禁飞区或障碍物**内」（A34-1） | **待确认（非阻断）**：是否将上述语义补入 `ReturnHomeInfoData.plannedPathPoints` 的 Javadoc（全量更新）与 `last_point_type` 枚举（若按 §5-42 补建）的 Javadoc；不改字段 / 类型 |
| 45 | `drc_ai_spotlight_zoom_select` 四字段类型 | 文档 Type=**`int`**、constraint `{"max":10000,"min":0}`、示例整数 `0` / `10000`；SDK [`AiSpotlightZoomSelectRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/ai/AiSpotlightZoomSelectRequest.java#L50-L55) 四字段为 **`Double`**，且 `@Verified(basis)` 记约束 `{max:1,min:0}`*10000（**basis 已过时**，与当前文档不一致）（A37-3） | **已决策（2026-10-14）→ 改 `Integer`**（开发者裁决）：文档 Type=`int`、constraint `[0,10000]`、示例整数（原文三度复现，本批再次确认）→ 四字段 `Double` **改 `Integer`**；`@Verified(basis=...)` 更正为 `{"max":10000,"min":0}`；**删除**过时的 Javadoc「`height`/`width` 单位互换、已修正」段（本批文档单位描述已正确、未互换）。属 **breaking（`Double`→`Integer`）**，随 `1.18.0.0` 发布；同步改 `AiRequestTest` / `tdd-test-cases.md` / `architecture-design.md`（见 §4） |
| 46 | `CameraOsdInfoPushData.IrLense` 缺 `thermalSupportedPaletteStyles` | 文档 `ir_lense.thermal_supported_palette_styles` Type=**`array`**、`item_type=enum_int`（「设备支持的调色盘样式集合」，值域同 `thermal_current_palette_style`）；SDK [`IrLense`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/up/CameraOsdInfoPushData.java) 现仅 9 字段、无此集合（A14 首次提及，A38 Dock2 页二次印证 → 首次**项化**为缺口） | **待确认（非阻断）**：是否按文档补 `List<Integer> thermalSupportedPaletteStyles`（A14 + A38 两页均定义，非机型专属）；补建后 `@Verified(basis)` 指向 remote-control 页。**📌 第三次印证（A79，2026-10-14）**：Pilot 上云 RC Pro 行业版 `remote-control.html` 页 `ir_lense.thermal_supported_palette_styles`（`array`/`item_type=enum_int`）**再次显式列出** → 缺口确证，非机型专属（A14 + A38 + A79 三页）。 |
| 47 | `CameraOsdInfoPushData.IrLense.irZoomFactor` 类型 | 文档 `ir_zoom_factor` Type=**`float`**、`{"max":20,"min":2}`；SDK 现为 **`int`**（A38 Dock2 页给出显式 Type 与约束） | **待确认（非阻断）**：`int` → **`Double`**（`float`→`Double` 既定映射）；属 **breaking**，随 `1.18.0.0` 发布，同步改测试与文档。**📌 第三次印证（A79，2026-10-14）**：Pilot 上云 RC Pro 页 `ir_zoom_factor` Type=**`float`** `{"max":20,"min":2}` → 支持 `int`→`Double`。 |
| 48 | `OtaCreateRequest.OtaDevice.fileSize` 类型 | 文档 `file_size` Type=**`int`**（示例 `653467234`）；SDK 现为 **`Long`**（A39 O4 firmware 页给出显式 Type） | **待确认（非阻断）**：`Long` → **`Integer`**（`int`→`Integer` 既定映射，协议忠实性）；属 **breaking**，随 `1.18.0.0` 发布，同步改 `ServiceSupplementParseTest`（现断言 `123456789L`）；**Dock1 页（A58，2025-03-19）`file_size` Type 亦为 `int`、示例 `653467234` → 二次印证** |
| 49 | `OtaCreateRequest.OtaDevice.firmwareUpgradeType` 枚举 / 包装类型 | 文档 `firmware_upgrade_type` Type=**`enum_int`** `{2:一致性升级, 3:普通升级, 4:PSDK升级}`；SDK 现为**原始 `int`**、无对应枚举（A39；**Dock1 页 A58 仅列 `{2,3}`，无 PSDK `4`**） | **待确认（非阻断）**：是否按 SDK「`enum_int` → `Integer` 包装 + 反查枚举助手」惯例补 `FirmwareUpgradeType`（值域 `{2,3,4}`）；字段可保持 `int` 亦可改 `Integer`，不影响反序列化；**值域建议取并集 `{2,3,4}`**（Dock1 为子集，O4 含 PSDK） |
| 50 | `OtaProgressData` / `OtaCreateRequest` 的 `@DocUrl` / `@Verified` 依据 | `OtaProgressData` `@DocUrl` 指 `dock3/config.html`、`@Verified(basis)`=simulator；`OtaCreateRequest` `@DocUrl` 指 `dock3/firmware.html`、basis=Dock3 页 + simulator；本次 **O4 `firmware.html`** 给出权威字段表（A39） | **待确认（非阻断，元数据）**：建议 `@DocUrl` 改指 O4 `firmware.html`（或并列保留 Dock3 页），`@Verified(basis)` 升级为「DJI O4 firmware.html Data 表」 |
| 51 | `ota_create` services_reply 的 `output.status` 无专属 Reply POJO | 文档 reply = `{result, output{status}}`，`status` 为 8 值 `enum_string`（Job 类：`{canceled,failed,in_progress,ok,paused,rejected,sent,timeout}`）；SDK 无 `OtaCreateReply`，仅 `OtaCreateRequest`（A39） | **待确认（非阻断，跨切面一致性）**：SDK 既有 `NoOutputReply` 约定**不承载** `output.status`（其 Javadoc 已声明调用方从 `result` 判断）→ 本身合规；但 **A19 曾为同类 Job 指令 `return_home` 新建 `ReturnHomeReply`** → 提请一致性裁决：`ota_create` 是否也补专属 Reply |
| 52 | `FileUploadListReply.LogFile` / `FileUploadStartRequest.Credentials` 的 `long`/`Long` | 文档 Type 均为 **`int`**：`LogFile.start_time`/`end_time`/`size`（O4 页）与 `Credentials.expire`（毫秒）；SDK 现为 **`long`**/**`Long`**。**关键**：`expire` example `1659432522000`（13 位毫秒）**超 int32**；Dock3 `start_time/end_time` 单位毫秒亦为 13 位（A40） | **待确认（非阻断，需复核 §5-6）**：`size` 文档 `int`（example 皆在 int32 内）→ 可改 `Integer`；但 `start_time`/`end_time`/`expire` 在**毫秒**口径下为 13 位、**超 int32** → 必须 `long`。**§5-6 原「字段统一 `Integer`」决策与之冲突 → 须复核/修订**（建议按 O4/Dock3 分别：O4 秒可 `Integer`，毫秒页用 `long`，或统一 `long` 保守）。文件字段改动见 §4。**A41 同类扩展（Dock3 wayline）**：`FlighttaskPrepareRequest.executeTime`、`ReadyConditions.beginTime`/`endTime` 同为 `Long` vs 文档 `int`（13 位毫秒，文档 Type 失真）→ 并入本口径；**Dock1 页（A59，2025-03-19）**`start_time`/`end_time`/`expire` 均标 unit **秒/s**、但 example 均为 13 位毫秒（`1659427398806`/`1659432522000`）→ **文档自相矛盾**，进一步支持「须 `long`」 |
| 53 | `module` / `module_list` 承载类型 | 文档**同批三种口径**：`fileupload_list` 页 Request `module_list` 与 Reply `module` 标 **`enum_int`**；`fileupload_start` 页 `params.files[].module` 标 **`text`**；三者 example **均为字符串**（`["3"]` / `"3"` / `"8"`）；SDK 统一 **`String`**（与 example 一致）（A40） | **待确认（非阻断）**：SDK 采用 `String`（对齐 example，兼容数字串），记录文档口径不一；是否引入模块枚举（值域 `{0:飞行器,3:机场,6:中继,8:基站,9:机库}`）待裁决 —— 注意 §3.1.6 提及「module 枚举需扩展 8/9」，但 SDK **现无 `Module` 枚举**，须一并核实该表述来源 |
| 54 | `fileupload_progress` 文档字段表 vs example 不一致 | 表：`output` **仅** `ext`（**无** `status`）、`progress` 子结构**仅 3 字段**（`progress`/`finish_time`/`upload_rate`）；example：`output` **含** `status:"ok"`、`progress` 子结构 **7 字段**（`current_step`/`finish_time`/`progress`/`result`/`status`/`total_step`/`upload_rate`）（A40） | **待确认（非阻断）**：新建 `FileUploadProgressData` 时须裁决字段集 —— 建议按 **example 全字段**建模（含 `output.status`、`progress` 7 字段），差异处 `@Inferred` 标注，待 DJI 修订字段表 |
| 55 | `flighttask_execute.multi_dock_task` 子结构缺口 | 文档 `dock_infos[]` = `{dock_type, latitude, longitude, height, heading, home_position_is_valid, index, sn, rtcm_info{mount_point,port,host,rtcm_device_type,source_type}, alternate_land_point{longitude,latitude,height,safe_land_height,is_configured}}`、`wireless_link_topo.center_node{sdr_id, sn}`、`leaf_nodes[]{sdr_id, sn, control_source_index}`；SDK：`DockInfo` 仅 6 字段（`sn/dockType/index/latitude/longitude/height`）**缺 4 项**（`heading`/`homePositionIsValid`/`rtcmInfo`/`alternateLandPoint`）；`CenterNode` 仅 `sn` **缺 `sdrId`**；`LeafNode` 为**空 record `LeafNode()`** **缺全部 3 字段**；`RtcmInfo` record **不存在**（A41，Dock3 wayline 完整原文首次给出） | **待确认（非阻断，但属协议缺口）**：补 `DockInfo` 4 字段 + 新建 `RtcmInfo` + `CenterNode.sdrId` + `LeafNode{sdrId,sn,controlSourceIndex}`；属 **breaking**（record 扩字段），随 `1.18.0.0` 发布。注：`AlternateLandPoint` 已存在于 `telemetry/nested/`（可复用） |
| 56 | `flighttask_create`（文档标注「废弃」）完全缺失 | 文档仍列出 `flighttask_create`（标题「创建航线任务（废弃）」，data = `{flight_id, type, file{url, sign}}`）；SDK **无** `ServiceMethod.FLIGHTTASK_CREATE` 常量、**无** `FlighttaskCreateRequest`/Reply（A41） | **待确认（非阻断）**：协议忠实性要求枚举含文档全部方法 → 是否补入 `FLIGHTTASK_CREATE`（并标 `@Deprecated`）+ POJO；或按「废弃方法不实现」策略维持缺失（须明确记录该取舍） |
| 57 | `FlighttaskProgressGetRequest` 请求 data 字段（**经 A42 修订**，详见 §5-59） | **Dock3 页**（A41）字段表**仅 `sn`**、无 example；**Dock2 页**（A42）字段表为 **`target_sn` + `flight_id`** 且含 example（wire key `target_sn`/`flight_id`）；SDK `FlighttaskProgressGetRequest(String flightId, String sn)`、`flightId` 被 `Objects.requireNonNull` 设为必填 | **待确认（非阻断）**：原判「`flightId` 为文档外字段」**经 Dock2 页修订后不成立**（Dock2 明确含 `flight_id`）→ `flightId` 可保留；核心问题转移至 **§5-59**（`sn` vs `target_sn` wire key 错配）；仍属 **breaking**，随 `1.18.0.0` 发布 |
| 58 | `FlighttaskProgressGetReply.output` 为 `Object` 占位 | SDK `FlighttaskProgressGetReply(Integer result, Object output)` + `@Inferred(reason="simulator 返回原始 JsonNode 未解析 output 结构")`；**文档现已给出** `output{flight_id, progress{current_step(int), percent(int)}, status(text)}`（A41，Dock2 页 A42 二次确认） | **待确认（非阻断）**：可据文档具体化为嵌套 record 并把类级 `@Inferred` 升 `@Verified`（依据 Dock3 wayline Requests 页）；属 **breaking**（类型变更） |
| 59 | `flighttask_progress_get` 请求 data **跨页不一致**（Dock2 vs Dock3） | **Dock2 页**（A42）：`target_sn`（目标设备 sn）+ `flight_id`（目标航线任务 uuid），**含 example**（wire key = `target_sn`/`flight_id`）；**Dock3 页**（A41）：字段表**仅 `sn`**、无 example；SDK `FlighttaskProgressGetRequest(String flightId, String sn)`（`@DocUrl`→dock3） | **待确认（非阻断，但含反序列化风险）**：Jackson SNAKE_CASE 下 SDK `sn` → JSON key `sn`，与 Dock2 example 的 **`target_sn` 不匹配** → Dock2 蛙跳场景下 `target_sn` 将**反序列化丢失**；`flightId` 在 Dock2 页**有定义**（非文档外，修订 §5-57）。建议字段改名 `sn` → `targetSn`（JSON `target_sn`），或做兼容处理；属 **breaking**，随 `1.18.0.0` 发布 |
| 60 | 「最低固件版本」是否纳入 SDK | 开发者提供支持产品/固件版本矩阵（A43） | `DeviceCompatibility` 现仅含机型搭配（`isCompatible(dock,drone)` / `isCompatible(rc,drone)`），**不含固件版本**；A43 矩阵给出各产品最低固件版本 | **待裁决（新增功能，非阻断）**：①维持现状（SDK 只做机型搭配）；②新增最低固件版本常量/查询（如 `minimumFirmwareVersion(model)`）。属**新增功能**，须开发者确认范围后再实现（TDD 先行） |
| 61 | `DeviceCompatibility.isCompatible(RC_PRO, drone)` 值域是否需扩展 | `RC Pro 行业版` 同时列于 A43「DJI Pilot 2 上云」与「机场上云」产品表 | SDK 现 `RC_PRO → {MAVIC_3E, MAVIC_3T}`；A43 机场上云表含 `RC Pro 行业版`（暗示其亦用于 Dock 侧机型 M3D/3TD/M4D/4TD） | **待确认（非阻断）**：A43 页**未给出逐对搭配**，仅列「支持产品」→ **不能据产品列表推断搭配**（AGENTS §3-11）；待开发者确认 RC Pro 行业版在机场上云的搭配机型后，再决定是否扩展 |
| 62 | WPML「布尔型」字段承载类型（`int`/`Integer` vs `Boolean`） | 文档 A44「共用元素」页将 `dewarpingEnable`/`modelColoringEnable`/`isPointFocus`/`isInfiniteFocus`/`gimbalPitch(Roll/Yaw)RotateEnable`/`gimbalRotateTimeEnable`/`useGlobalPayloadLensIndex`/`accurateFrameValid`/`missionAutoRerouteMode`/`transitionalAutoRerouteMode`/`megaphoneOperateLoop` 等统一标注为**布尔型**（取值 0/1） | SDK WPML 模型**统一用 `int`/`Integer` 承载**（如 `AutoRerouteInfo.missionAutoRerouteMode`=`int`、`PayloadParam.dewarpingEnable`=`Integer`） | **记录（非阻断，建议维持现状）**：WPML XML 线格式即 `0/1`，若改 `Boolean` 将序列化为 `true/false` **反而不符协议** → SDK 现状正确；AGENTS §6.6「bool→Boolean」仅适用 MQTT 层。**建议**：在 architecture-design.md 记录该分层差异（WPML 层 bool→int / MQTT 层 bool→Boolean），**无需改代码** |
| 63 | `ActionActuatorFunc`/`ActionGroupMode` 枚举值域**超出** A44 文档列表 | 文档 `wpml:actionActuatorFunc` 列表**仅 14 值**（未列 `megaphone`/`searchlight`）；`wpml:actionGroupMode` **仅列 `sequence`** | SDK `ActionActuatorFunc` 含 **16 值**（+`MEGAPHONE`/`SEARCHLIGHT`，`ActionActuatorFunc.java:82-85`）；`ActionGroupMode` 含 `PARALLEL`（`@Inferred`，`ActionGroupMode.java:43-45`） | **记录（非阻断，无需改动）**：文档虽未列入枚举列表，但**另设 `#megaphone`(L348)/`#searchlight`(L371) 参数表** → SDK 更全，属**文档枚举列表缺口**；`parallel` 已 `@Inferred` 待真机。**不改 SDK** |
| 64 | `Placemark`（航点）缺 `isRisky` / `useStraightLine` | A45「航点信息」表：`wpml:isRisky`（**布尔型**，是否危险点，0 正常/1 危险；支持 M30/M30T、M3D/M3TD、M4D/M4TD、M4E/M4T）、`wpml:useStraightLine`（**布尔型**，该航段是否贴合直线，0 全程曲线/1 尽量贴合两点连线；当且仅当 `waypointTurnParam.waypointTurnMode` 为 `toPointAndStopWithContinuityCurvature`/`toPointAndPassWithContinuityCurvature` 时必需，局部覆盖全局）；SDK `Placemark` 仅 13 字段（`point/index/ellipsoidHeight/height/useGlobalHeight/useGlobalSpeed/useGlobalHeadingParam/useGlobalTurnParam/waypointSpeed/waypointHeadingParam/waypointTurnParam/gimbalPitchAngle/actionGroups`），**两字段均缺**（注：`Folder` 已有全局 `globalUseStraightLine`，但航点级 `useStraightLine` 缺失）（A45） | **待确认（非阻断）**：补 `isRisky`(Integer)、`useStraightLine`(Integer)；按 **WPML 布尔口径**用 `Integer` 承载（§5-62，XML 线格式 `0`/`1`）；属 **breaking**（record 扩字段），随 `1.18.0.0` 发布 |
| 65 | `Mapping2dPlacemark`（建图航拍）缺 `quickOrthoMappingEnable` / `quickOrthoMappingPitch`（M4E） | A45「建图航拍模板元素」表：`wpml:quickOrthoMappingEnable`（**布尔型**，正射智能摆拍开关，0 不开启/1 开启，**仅 M4E**）、`wpml:quickOrthoMappingPitch`（正射智能摆拍角度，单位 `°`、范围 `[10,30]`，**仅 M4E**，`quickOrthoMappingEnable` 开启时必需；**文档「类型」列疑似缺失**，未明标整型/浮点型）；SDK `Mapping2dPlacemark`（`Mapping2dPlacemark.java:38-69`）15 字段**均无此二者**（A45） | **待确认（非阻断）**：补 `quickOrthoMappingEnable`（`Integer`，按 §5-62）+ `quickOrthoMappingPitch`（类型待裁决：文档类型列未明，取值 `[10,30]` 整数区间 → 建议 `Integer`；用包装类型 + NON_NULL，非 M4E 缺席为 null） |
| 66 | `MappingStripPlacemark.margin` 类型不符（航带飞行） | A45「航带飞行模板元素」表 `wpml:margin`（测区外扩距离）标注 **浮点型**、单位 m；SDK `MappingStripPlacemark.margin` 为 **`int`**（`MappingStripPlacemark.java:46`）。注：同页 mapping2d（`wpml:margin` 整型）/mapping3d（`wpml:margin` 整型）的 `margin` 均为**整型** → **仅航带飞行需改 `double`**（A45） | **待确认（非阻断）**：`int` → **`double`**；属 **breaking**（类型变更），随 `1.18.0.0` 发布 |
| 67 | `Folder`（航点飞行）缺 `payloadParam`（模板共用元素层级） | A45「模板共用的元素（父元素 `<Folder>`）」表列出 `wpml:payloadParam`（负载设置）；SDK **航点 `Folder`（`Folder.java:38-59`）无 `payloadParam`**，但**建图类 `MappingFolder`（`MappingFolder.java:50-51`）已含 `payloadParam` ✓**（`PayloadParam` 类 9 字段，A44 已确认 ✓）。**不确定性**：航点飞行**示例 XML 未出现** `payloadParam`（示例 Folder 仅 `waylineCoordinateSysParam`/`autoFlightSpeed`/`gimbalPitchMode`/`globalWaypointHeadingParam`/`globalWaypointTurnMode`/`globalUseStraightLine`/`Placemark`），且 `PayloadParam` 类 Javadoc 自述「用于建图航拍、倾斜摄影、航带飞行模板」（**不含航点**）（A45） | **待确认（非阻断）**：`payloadParam` 是否属**航点 `Folder`**（文档表列 vs 示例缺席 + `PayloadParam` Javadoc 限定建图类，**文档自身不一致**）→ 需开发者确认其层级归属；**倾向现状正确**（航点无、建图有），若确认属共用则补入 `Folder`（AGENTS §3-11 禁止据表/示例推断） |
| 68 | `ExecuteFolder`（waylines.wpml 航线）缺 `startActionGroup` | A46「航线信息（父元素 `<Folder>`）」表列出 `wpml:startActionGroup`（**航线初始动作**：规划一系列**航线开始前执行**的动作；航线中断恢复时**先执行初始动作，再执行航点动作**；引用共用元素 `<wpml:actionGroup>`；支持 M30/M30T、M3E/M3T/M3M、M3D/M3TD、M4D/M4TD、M4E/M4T）；SDK `ExecuteFolder`（`ExecuteFolder.java:37-49`）仅 5 字段（`templateId`/`executeHeightMode`/`waylineId`/`autoFlightSpeed`/`placemarks`），**缺 `startActionGroup`**（A46） | **待确认（非阻断）**：补 `startActionGroup`（复用 `ActionGroup`）；属 **breaking**（record 扩字段），随 `1.18.0.0` 发布 |
| 69 | `ExecutePlacemark`（waylines.wpml 航点）缺 `isRisky` / `useStraightLine` | A46「航点信息（`<Placemark>`）」表列出 `wpml:isRisky`（**布尔型**，0 正常/1 危险；M30/M3D/M4D/M4E）、`wpml:useStraightLine`（**布尔型**，受 `waypointTurnMode` 连续性条件约束，局部覆盖全局）；SDK `ExecutePlacemark`（`ExecutePlacemark.java:43-61`）8 字段（`point`/`index`/`executeHeight`/`waypointSpeed`/`gimbalPitchAngle`/`waypointHeadingParam`/`waypointTurnParam`/`actionGroups`）**两字段均缺**；与 §5-64（template.kml `Placemark`）**同源**，执行层同样缺失（A46） | **待确认（非阻断）**：补 `isRisky`(Integer)、`useStraightLine`(Integer)（WPML 布尔口径 §5-62）；**建议与 §5-64 一并落地**，避免 template/execute 两层不一致 |
| 70 | waylines.wpml **多 `Folder`** 支持（倾斜摄影 5 条航线） | A46 文件介绍：「每个 `Folder` 代表一条可执行的航线；**倾斜摄影模板将生成 5 条可执行航线，对应 waylines.wpml 内的 5 个 `Folder` 元素**」；SDK `ParsedKmz.waylines` = `Kml<ExecuteFolder>`（**单数**，`ParsedKmz.java:33`），`Document<T>.folder` 为**单个 `T`**（`Document.java:53`）→ **多 Folder 仅保留 1 个、其余丢失**（A46） | **待确认（非阻断，但属协议缺口）**：waylines.wpml 的 `<Document>` 是否需支持**多个 `Folder`**（`List<ExecuteFolder>`）；template.kml 恒为单 Folder → 可仅对 waylines 层扩展（如新增 waylines 专用 Document 或使 folder 支持 List）；属 **breaking**，设计方式须开发者裁决。**协议规范调研（2026-10-14）**：官方 `waylines-wpml.html` 文件介绍原文确认「**每个 `Folder` 代表一条可执行的航线**……倾斜摄影……将生成 **5 条**可执行航线，对应 waylines.wpml 内的 **5 个 `Folder` 元素**」，且 waylines.wpml **无创建信息**（示例 `<Document>` 直接从 `wpml:missionConfig` 开始，不含 `author`/`createTime`/`updateTime`）；官方 `template-kml.html` 文件介绍称「模板信息：主要包含 `Folder` 元素」且示例为**单个** `<Folder>`（全页无「多 Folder」表述）→ **template.kml 恒单 Folder / waylines.wpml 为 N Folder**。**裁决（2026-10-14）：采用方案 B** —— 新增 `WaylinesDocument(missionConfig, List<ExecuteFolder>)` + 根 `WaylinesWpml`；`ParsedKmz.waylines` 由 `Kml<ExecuteFolder>` 改为 `WaylinesWpml`；`WpmlCodec.parseWaylinesWpml` 返回类型同步变更；template 侧 `Kml<Folder>`/`Document<T>` 零改动；waylines 侧不再复用 `Document<T>`（顺带移除 `author`/`createTime`/`updateTime`，符合 waylines 无创建信息）。**待实现**（原文收齐后按 TDD 落地） |
| 71 | `ExecutePlacemark.gimbalPitchAngle` 层级（文档该页未列） | A46「航点信息」表**未列** `gimbalPitchAngle`，示例 waylines.wpml 航点亦未含；但该页 `Placemark(Point)` 行注明「请阅读 …template.kml 说明 > Placemark」（A45 该表**含** `gimbalPitchAngle`，浮点型、`gimbalPitchMode=usePointSetting` 时必需）；SDK `ExecutePlacemark` **含 `gimbalPitchAngle`(Double)**（`ExecutePlacemark.java:52-53`）（A46） | **待确认（非阻断，记录文档表述差异）**：waylines.wpml 航点是否实际携带 `gimbalPitchAngle`（该页表未列 vs 引用 template Placemark 含之）；**倾向保留**（template→execute 转换需携带逐点云台角）；待官方/真机确认 |
| 72 | `compressed_sticks_control` 方法本 SDK 缺失 | DJI Cloud-API-2 `message_define_user/custom/compressed_sticks_control_{south,north}bound.h`（**手写层**，非 `generated/`）；本 SDK 无对应 method/POJO（仅有 `DrcMethod.STICK_CONTROL` 普通摇杆 `stick_control`） | **待确认（非阻断）**：v1.18 官方文档**未列**该方法，仅存于 DJI 仓库 → 是否为对上云开放接口待开发者确认；**禁止据仓库代码推断协议**（AGENTS §3-11 / §2 协议忠实性）。注意：DJI 仓库代码与官方文档版本不必然一致 |
| 73 | DJI 仓库独有 DRC 方法（本 SDK 未覆盖，63 + 6） | DJI `generated/` DRC 文件拆分：`_southbound`（非 `reply_`）**105** 下行 method、`_northbound`（非 `reply_`、`*_push`）**18** 推送；本 SDK `DrcMethod`＝**42**、`DrcUpMethod`＝**12** → 缺口 **63 下行 + 6 推送**；缺口集中于 `drc_camera_*`（相机参数细项）、`drc_*_isotherm_*`（热成像等温线 6 项）、`drc_speaker_*`（语音 9 项）、`drc_algorithm_dehaze_*`、`drc_assistant_video_*`、`drc_ir_metering_*`、`drc_pre_recorded_*`、`drc_light_type_set`/`drc_light_wide_field_mode_set`/`drc_light_light_gimbal_control_set` 等 | **待裁决（非阻断）**：这些方法**声明于 DJI 仓库但未见于 v1.18 官方文档**（属专业能力/新版固件）→ 是否纳入 SDK 须开发者确认；**不得据仓库代码自行补齐**（AGENTS §3-11） |
| 74 | DRC 通道方法名 `drc_` 前缀口径（命名差异，核心） | DJI Cloud-API-2 DRC 通道 wire method **保留 `drc_` 前缀**：`drc_drc_camera_aim_southbound` → `method_="drc_camera_aim"`、`drc_camera_photo_take`、`drc_camera_mode_switch`、`drc_gimbal_reset`、`drc_camera_state_push` 等；本 SDK **跨通道去前缀**（`ServiceMethod.CAMERA_AIM`=`camera_aim`、`CAMERA_PHOTO_TAKE`=`camera_photo_take`、`GIMBAL_RESET`=`gimbal_reset`）；仅上行 `DrcUpMethod.DRC_CAMERA_STATE_PUSH`=`drc_camera_state_push` **保留**前缀；飞行/心跳类（`heart_beat`/`stick_control`/`drone_emergency_stop`）DJI **不加**前缀、与本 SDK 一致 | **已裁决（2026-10-14，方案 A：采纳前缀）**：DRC 通道常量 wire **逐字对齐官方文档**（`drc_camera_aim` / `drc_camera_mode_switch` / `drc_camera_photo_take` / `drc_gimbal_reset` / `drc_camera_exposure_mode_set` … 带 `drc_` 前缀），与 services 通道无前缀名（`camera_aim` …）**并存为两套常量**（**放弃「同名跨通道复用」策略**）；飞行 4 件套（`heart_beat`/`stick_control`/`drone_emergency_stop` 及已废 `drone_control`）沿用文档**无前缀**写法；DRC 专属 POJO 字段与 services 版一致时**复用同一 record**（不复制）；同步**更正 `DrcMethod` 类级 Javadoc**（删除「同名复用、由调用方按 topic 判归属」，改为「DRC 与 services 通道 method 字符串不同，各自独立定义」）。**依据**：A10–A12（services，无前缀）vs A16–A18（Dock DRC `drc/down`，带前缀）原文 + DJI Cloud-API-2 `method_="drc_camera_photo_take"`。**遗留已结案（2026-10-14，A79）**：开发者投喂 Pilot 上云「RC Pro 行业版 远程控制」页原文 → Pilot RC DRC 通道方法 `drc_drone_state_push`/`drc_camera_osd_info_push`/`drc_initial_state_subscribe` **均带 `drc_` 前缀**，与 Dock 侧一致 → **方案 A（采纳前缀）对 Pilot 侧同样成立**，无跨通道口径差异（javadoc 提及 Pilot 的表述已获原文印证）。 |
| 75 | AI 框选坐标类型冲突复核（DJI 实现 vs v1.18 文档） | DJI Cloud-API-2 `DrcDrcAiSpotlightZoomSelectSouthboundMsg`：**`double center_x` / `double center_y` / `double height` / `double width`**（默认 `0.0`）；v1.18 官方文档（§5-45）：Type=`int`、constraint `[0,10000]`、示例为整数 | **维持 §5-45 决策 `Integer`**（依 §0「冲突以 v1.18 官方文档为准」）；此处**仅登记冲突事实供开发者知情**，DJI 实现属旧版本/内部实现差异，**不据此改回 `Double`** |
| 76 | 非 DRC 分类的「仓库↔SDK 覆盖差集」 | DJI Cloud-API-2 `generated/` 非 DRC 分类中存在本 SDK 无对应的方法（仅见于仓库、未见于 v1.18 官方文档）：`services` ~35 项（代表性：`open_parachute`/`relay_connect`/`relay_disconnect`/`relay_scan`/`relay_passwd_hash`/`offline_map_update`/`rid_avoidance_enable`/`spotlight_zoom_open`/`spotlight_zoom_close`/`ai_box_app_list`/`ai_box_app_operate`/`load_ai_model`/`unload_ai_model`/`self_diagnostic`/`sdr_debug_switch`/`sdr_link_set_config`/`command_send`/`cleared_to_land`/`flysafe_database_upgrade`/`drc_voice_enter`/`dongle_power_switch`/`aircraft_power_ctrl`/`device_v1_forward_*`/`ccc_debug_command`/`clear_sdr_topo_info`/`write_sdr_topo_info`/`measure_sdr_signal_enter`/`rt_agent_channel_join`/`set_relay_occupied_status`）／`requests` 7 项（`offline_map_get`/`offline_map_get_v2`/`rt_agent_channel_open`/`rt_agent_channel_status_get`/`request_landing_permission`/`requests_ai_model_url`/`in_flight_task_request`）／`status` 1 项（`static_topo`）／`events` 若干（`ai_object_detection_alert`/`rid_warning`/`self_diagnostic`/`station_measure_report`/`uom_fly_data_info` 等） | **待裁决（非阻断）**：这些方法**仅见于 DJI 仓库、未见于 v1.18 官方文档**（属专业能力/新版固件/私有部署）→ 是否纳入 SDK 须开发者确认；**不得据仓库代码自行补齐**（AGENTS §3-11 / §2 协议忠实性）。登记事实供知情（A48）。**勘误（A63）**：`flight_areas_update` 经 Dock1「自定义飞行区」页（2025-03-19）确认为**官方原文**协议，已从本清单移出 → 转 §5-113 |
| 77 | `best_link_gateway` / `flysafe_database_version` 补登 `StateField` | M4D/M4TD 页（A49）定义 `best_link_gateway`（Type=`text`、accessMode=`r`、pushMode=**1**，最佳链路网关）与 `flysafe_database_version`（Type=`text`、`r`、pushMode=**1**，飞行安全数据库版本）；SDK **无**对应 `StateField` 常量、`DroneOsd` **无**字段 | **待确认（非阻断）**：按 pushMode=1 → 归 state 通道，补 `StateField.BEST_LINK_GATEWAY` / `StateField.FLYSAFE_DATABASE_VERSION` + `DroneOsd` 两字段（`String`）。双向缺（枚举 + 模型） |
| 78 | `fts_info` struct 建模（**已决策 · 方案 A**） | M4D/M4TD 页（A49）**首次完整定义** `fts_info`（Type=`struct`、`r`、pushMode=0）：子字段 `fts_status`（`enum_int`，6 值 `{0:未连接,1:已就绪,2:正在连接服务器,3:已触发停桨,4:未连接 dongle,99:未知错误}`）+ `fts_verification_code`/`fts_valid_duration`/`fts_timestamp`/`fts_sn`/`fts_firmware_version`；SDK 现无建模 | **已决策（2026-10-14）→ 方案 A（建模）**：新建 `FtsInfo` record + `FtsStatus` 枚举（6 值）+ `OsdField.FTS_INFO` + `DroneOsd.ftsInfo`；§5-8 原结案依据「该 struct **从未被任何官方页面定义** → 方案 B：暂不建模」**撤销**，改记「文档已完整定义 → 具备建模条件」；`@DocUrl` → M4D/M4TD `aircraft/m4d-properties.html`；**待原文收齐后按 TDD 落地** |
| 79 | `DroneModeCode` 缺 `mode_code=21` | M4D/M4TD 页（A49）`mode_code` 值域**含 21=进离场航线飞行过程中**；SDK `DroneModeCode` 现 0-20（`@Verified(basis)` 记「0-20」）（A49） | **待确认（非阻断）**：补 `AIRPORT_AREA_WAYLINE(21, "进离场航线飞行过程中")` 并将 `@Verified(basis)` 更新为「0-21」 |
| 80 | `PropertySetMethod` 缺 `commander_flight_height` / `commander_mode_lost_action` | M4D/M4TD 页（A49）rw 属性含 `commander_flight_height`（指点飞行高度）与 `commander_mode_lost_action`（指点模式失控动作）；SDK `PropertySetMethod` 现 18 个 rw 属性、**无此二者**（A49） | **待裁决/待落地（非阻断，原文前提已解除）**：补两常量 `COMMANDER_FLIGHT_HEIGHT`、`COMMANDER_MODE_LOST_ACTION`；**📌 原文核实（A76，M4D/M4TD「设备属性」页 2026-09-01，本文件第 182–184 行）：`commander_mode_lost_action` 值域 = {0:"继续执行指点飞行任务", 1:"退出指点飞行任务，执行普通失控行为"}（值域 0-1）**；`commander_flight_height` = float 米/m `{max:3000,min:2,step:0.1}`、accessMode=rw、pushMode=1；另核实 `commander_flight_mode`（enum_int {0:"智能高度飞行",1:"设定高度飞行"}）**SDK 已存在** ✓ → **仅需补 2 常量**，值域已定（不再待原文） |
| 81 | `CameraInfo` 缺 20 字段 + 新 struct | M4D/M4TD 页（A49）`cameras[]` 元素共 **31 字段**，含 `photo_storage_settings`/`video_storage_settings`/`wide_exposure_mode`/`wide_iso`/`wide_shutter_speed`/`wide_exposure_value`/`zoom_*` 系列/`ir_metering_mode`/`ir_metering_point`/`ir_metering_area` 等；SDK [`CameraInfo`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/nested/CameraInfo.java) 现仅 **11 字段** | **待确认（非阻断）**：补 20 字段 + 新建 struct `IrMeteringPoint`/`IrMeteringArea`（若为 struct 类型）；属 **breaking**（record 扩字段），随 `1.18.0.0` 发布 |
| 82 | Javadoc 级机型范围修正（4 处） | M4D/M4TD 页（A49）证据：①`dongle_infos`/`offline_map_enable` 在 M4D/M4TD 页**亦有定义**，但 SDK `StateField.DONGLE_INFOS`/`OFFLINE_MAP_ENABLE` 注释为「M400 only」（M400 为**独立型号** (0,103,0)，非 M4D/MT4D）；②本页 `position_state.quality` **含 10=RTK fixed**，而 SDK [`DronePositionState`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/nested/DronePositionState.java) 注释「Drone 仅 1-5、不含 10」；③[`PositionQuality`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/PositionQuality.java) 未标飞行器适用性；④`cameras[].type_subtype_gimbalindex` 含热成像字段（附注） | **非阻塞 → Javadoc 更正**（不改字段/类型）：`StateField.DONGLE_INFOS`/`OFFLINE_MAP_ENABLE` 机型范围补 M4D/M4TD；`DronePositionState.quality` 补 10=RTK fixed；`PositionQuality` 补飞行器适用性说明 |
| 83 | `electric_supply_voltage` 类型/归属**跨页冲突** | Dock1「设备属性」页（A50）列 `electric_supply_voltage` 为 **Type=`int`**、单位 **伏 / V**、accessMode=`r`、pushMode=0；O4 页（A26）为 **`double`** / V；SDK [`DockOsd.electricSupplyVoltage`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/DockOsd.java#L83-L84) 现为 **`Double`**（`OsdField` 注释「市电电压(V)」） | **待确认（非阻断）**：①SDK `Double` 与 Dock1 页 `int` 类型不符 → 是否改 `Integer`？改则与 O4 页 `double` **冲突**（跨页口径须开发者裁决）；②**修订 §5-1**：原「该字段属 O4（dock1/2/3 均无）」表述**不成立**（Dock1 页确有）；③注意 Dock1 页日期 2025-03-19（见 §5-88） |
| 84 | `drone_authority_info` 模型缺口（Dock1 OSD 专属） | Dock1 页（A50）列 `drone_authority_info`（Type=`struct`、pushMode=**0**）：子字段 `control_source`（`enum_string` `{"" 未获取, A A控, B B控}`）/`locked`（`bool`）/`payloads[]`（`control_source`/`payload_index`/`sn`）；SDK 有 `OsdField.DRONE_AUTHORITY_INFO` + `StateField.DRONE_AUTHORITY_INFO` **两处枚举**，但 **`DockOsd` 无 `droneAuthorityInfo` 字段、无 `DroneAuthorityInfo` record**（全仓无建模） | **待确认（非阻断）**：①新建 `DroneAuthorityInfo` record（`controlSource`/`locked`/`payloads[]`）+ `DockOsd.droneAuthorityInfo` 字段；②核对枚举归属：文档该 struct pushMode=0 → 应仅在 `OsdField`（`StateField` 重复定义待清理）；③`payloads` 子项 pushMode=1（文档子行特殊口径） |
| 85 | `sub_device` 文档口径不一（表 vs 示例） | Dock1 页（A50）**字段表**列子字段 **`product_type`**（`text`，格式 `{domain-type-subtype}`），但**同页示例** wire key 为 **`device_model_key`**（示例 `"device_model_key":"0-67-0"`）；SDK [`SubDevice`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/nested/SubDevice.java#L24) 采用 `deviceModelKey`（对齐示例 wire key） | **记录（非阻断，不改 SDK）**：SDK 取 `device_model_key`（与示例一致，反序列化正确）；文档表列 `product_type` 属**文档自身不一致**（同 §3.1.6 提及 `product_type`）；待 DJI 修订 |
| 86 | Dock OSD 若干枚举字段 Javadoc 值域不全 | Dock1 页（A50）：`cover_state` 4 值 `{0:关闭,1:打开,2:半开,3:舱盖状态异常}`、`putter_state` 4 值 `{0:关闭,1:打开,2:半开,3:推杆状态异常}`、`air_conditioner.air_conditioner_state` **16 值 `{0..15}`**；SDK [`DockOsd`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/DockOsd.java) Javadoc：`coverState`「0=关闭，1=打开」、`putterState`「0=收回，1=展开」、[`AirConditioner`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/nested/AirConditioner.java#L18)「0=空闲,1=制冷,2=制热,3=除湿,...」均不全 | **非阻塞 → Javadoc 更正**（字段均 `Integer`，不改类型）：`coverState` 补 `2=半开/3=舱盖状态异常`（§5-40 已记）；`putterState` 补 4 值；`airConditionerState` 补全 16 值描述 |
| 87 | `wpmz_version` 设备归属注释 | Dock1 页（A50）将 `wpmz_version`（机场的航线解析库版本号，`text`、pushMode=1）列于**机场**属性；SDK [`StateField.WPMZ_VERSION`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/StateField.java#L170-L171) 注释为「飞行器 state」 | **非阻塞 → Javadoc 更正**：注释改为「机场/飞行器 state 均可能上报（Dock1 页列于机场属性）」；字段名不变 |
| 88 | Dock1 页时效性（2025-03-19 vs v1.18） | Dock1「设备属性」页（A50）页面标注日期 **2025-03-19**，早于 v1.18 发布（2026.10.13）；所列字段相对稳定（`mode_code` 0-5、`flighttask_step_code` {0-5,255,256}、`position_state` 等与 SDK 一致） | **✅ 已结案（2026-10-14 开发者确认）**：确认该 Dock1「设备属性」页（页面日期 2025-03-19）**即 v1.18 现行版**（DJI 未更新该页）→ **§5-88 关闭**。**推论**：`electric_supply_voltage`（§5-83：Dock1 页 `int` / O4 页 `double` / SDK `Double`）属**真实跨页口径冲突**（非过期页假象）→ 其裁决独立保留；本行「所列字段与 SDK 一致」结论**成立**。**📌 复核追加（A75，Dock1「设备属性」2025-03-19，文件投喂 410 行）：与 A50 同页、内容一致（差异 0）** |
| 89 | `update_topo` 的 `status_reply` data 字段集（`result` vs `result`+`sub_type`） | Dock1「设备管理」页（A51）`status_reply`（down，method `update_topo`）data **仅 `result`**（int，非 0 代表错误），**无 `sub_type`**；SDK [`UpdateTopoReplyData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/status/UpdateTopoReplyData.java#L39-L45) = `result` + `subType`，`subType` 标 `@Inferred(reason="DJI 文档中 status_reply data 含 sub_type（0=上线/1=下线）…")` | **待确认（非阻断）**：本页 reply **不含** `sub_type` → `@Inferred` 依据「DJI 文档…含 sub_type」在此页**不成立**；`subType` 来源可能为 Pilot 设备管理页或其他页 → 请开发者确认 `subType` 的权威来源；若确属推断，是否保留该字段（包装类型可空）或降级。**✅ 已结案（2026-10-14，A81）**：开发者投喂**三页**设备管理原文（Dock1「设备管理」`dock1/device.html` + RC Pro 行业版 `rc-pro/device.html` + RC Plus 2 行业版 `dji-rc-plus-2/device.html`）→ **三页 `status_reply` data 均仅 `{result}`，皆无 `sub_type`** → `@Inferred` 依据「DJI 文档中 status_reply data 含 `sub_type`（0=上线/1=下线）」**不成立**（且与 UP data 的 `sub_type`=网关子设备产品子类型语义矛盾）→ **确证 `subType` 属无依据推断**。**裁决：删除 [`UpdateTopoReplyData.subType`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/status/UpdateTopoReplyData.java) 字段 + 移除 `@Inferred` + 修正 Javadoc**（现 Javadoc「`sub_type` 取值 0=上线/1=下线」错误）；属 **breaking**（record 构造器 `(result, subType)` → `(result)`），随 `1.18.0.0` 发布，同步改测试。**未写代码** |
| 90 | `StatusMethod.UPDATE_TOPO` Javadoc 语义冲突（`sub_type` 含义） | SDK [`StatusMethod.UPDATE_TOPO`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/protocol/method/StatusMethod.java#L37-L38) 描述「设备拓扑上下线通知（设备→云，**data 含 sub_type 区分上线 0/下线 1**）」；Dock1 页（A51）**UP data** 的 `sub_type` = **网关子设备的产品子类型**（`int`，参考产品支持；示例 `sub_type:0`），**非上线/下线标志**；上下线由 **`sub_devices` 是否为空**区分（页内 `//sub_devices offline` 示例 `sub_devices:[]`），且 SDK `UpdateTopoData` Javadoc 已正确表述「`sub_devices` 为空列表表示下线」 | **非阻塞 → Javadoc 更正**：`UPDATE_TOPO` 描述改为「上下线由 `sub_devices` 是否为空区分；`sub_type` 为网关子设备产品子类型」；**不改枚举/字段**。**📌 追加证据（A81，三页「设备管理」2026-10-14）：Dock1 + RC Pro + RC Plus 2 三页 UP `update_topo` data 的 `sub_type` 均为「网关子设备的产品子类型」（`int`，示例 `sub_type:0`）→ 第 4 次确证 Javadoc 表述错误，须更正** |
| 91 | `base_info` 归属（Dock1 页无 / O4 页有） | Dock1「设备管理」页（A51）`update_topo` Data **仅 7 顶层字段**（`domain`/`type`/`sub_type`/`device_secret`/`nonce`/`thing_version`/`sub_devices`），**无 `base_info`**；O4 `device.html`（A26/§3.1.3）Data **含 `base_info{country_code}`（AES-128-ECB+Base64）**；SDK 现无 `base_info`（与 Dock1 页一致 ✓；§4 line 271「+ `base_info`」源自 O4 页） | **待确认（非阻断）**：`base_info` 是否 **O4 专属**——若仅 O4 → §4 既定「+ `base_info`」维持、Dock1 页无需；若为通用字段 → Dock1/O4 页文档不一致，须开发者裁决；注意 Dock1 页时效性（§5-88）。**📌 追加证据（A81，三页「设备管理」2026-10-14）：Dock1 + RC Pro + RC Plus 2 三页 UP `update_topo` data **均无 `base_info`** → 确证 `base_info` 为 **O4 专属**（Dock1/RC 页无需），§4「+ `base_info`」仅适用 O4 页** |
| 92 | `AirportOrganizationBindReply.ErrorInfo` 字段 wire key 不匹配 | SDK [`AirportOrganizationBindReply.ErrorInfo`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/registration/AirportOrganizationBindReply.java#L48-L51) = `Integer errCode` + `String errSn`；Jackson SNAKE_CASE 将 `errSn` 序列化为 **`err_sn`**；文档（A4 O4 requests 页 / A52 Dock1 组织管理页）wire key 为 **`sn`** → 反序列化将**丢失 `sn`** | **待确认（非阻断 / A4 漏登记 §5，本页补齐）**：字段改名 `errSn` → **`sn`**（JSON `sn`）；属 **breaking**（record 字段名变更），随 `1.18.0.0` 发布 |
| 93 | `AirportOrganizationGetReply` 缺 `output` + `@Inferred` 依据错误 | SDK [`AirportOrganizationGetReply`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/registration/AirportOrganizationGetReply.java#L41-L46) **仅 `result`** + `@Inferred(reason="simulator 仅读取 data.result，未解析 output.airports（机场列表）…")`；文档（A4/A52）reply = `result` + `output{ organization_name }` → `@Inferred` 假设的 `output.airports` **错误** | **待确认（非阻断 / A4 漏登记 §5）**：补 `Output{organizationName}`（文档 `output.organization_name`），删除错误的 `@Inferred` 并升 `@Verified(basis=文档 A4/A52)`；属 **breaking** |
| 94 | `AirportBindStatusReply` 缺 `output` + Javadoc 语义冲突 | SDK [`AirportBindStatusReply`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/registration/AirportBindStatusReply.java#L33-L38) **仅 `result`**，Javadoc「result 取值：0=未绑定、1=已绑定」；文档（A4/A52）reply = `result`（返回码，0 成功/非 0 错误）+ `output{ bind_status[]{ sn, is_device_bind_organization, organization_id, organization_name, device_callsign } }` → 缺 `output`，且 Javadoc 把 `result` 误释为绑定状态 | **待确认（非阻断 / A4 漏登记 §5）**：补 `Output` + `BindStatus`（5 字段，`isDeviceBindOrganization`=`Boolean`，其余 `String`）；Javadoc 更正为「`result`=返回码（0 成功/非 0 错误），绑定状态见 `output.bindStatus[].isDeviceBindOrganization`」；属 **breaking** |
| 95 | `url_type` / `video_type` 未建模为 Java 枚举 | SDK [`LiveStartPushRequest.urlType`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/live/LiveStartPushRequest.java#L36-L41) = 裸 `Integer`（文档 `enum_int{0声网,1 RTMP,3 GB28181,4 WebRTC}`，**跳 2**）；[`LiveLensChangeRequest.videoType`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/live/LiveLensChangeRequest.java#L43-L46) = 裸 `String`（文档 `enum_string{ir,normal,wide,zoom}`）；SDK 现无 `LiveUrlType` / 镜头类型枚举 | **待确认（非阻断，可选增强）**：是否补 `LiveUrlType`（含 `3=GB28181`/`4=WebRTC`，并注明**无 2**）与镜头类型枚举；与 SDK 现有「部分枚举用裸 `Integer`/`String`」模式一致，可由开发者决定是否统一枚举化 |
| 96 | `live_*` Javadoc 取值/格式不全 | `LiveStartPushRequest` Javadoc 仅列 `url_type` 的 1/4；`url` 的四类（RTMP/GB28181/Agora/WebRTC-WHIP）格式说明、`video_id` 格式 `{sn}/{camera_index}/{video_index}`、`video_quality` 分辨率/码率表（流畅 960×540/512Kbps、标清 1280×720/1Mbps、高清 1280×720/1.5Mbps、超清 1920×1080/3Mbps）均未入 Javadoc | **非阻塞 → Javadoc 补全**：补齐 `url_type` 全值、`url` 四类格式、`video_id` 格式、`video_quality` 分辨率/码率；不改字段/类型 |
| 97 | `StorageConfigGetReply.Credentials` 字段 wire key 不匹配（`expireTime`→`expire_time` vs 文档 `expire`） | SDK [`StorageConfigGetReply.Credentials`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/config/StorageConfigGetReply.java#L52-L57) = `accessKeyId`/`accessKeySecret`/`securityToken`/`expireTime`（`Long`）；Jackson SNAKE_CASE 将 `expireTime` 序列化为 **`expire_time`**；文档（Dock1 媒体管理页 A54 / 同 log 页）wire key 为 **`expire`** → 反序列化将**丢失 `expire`**（`access_key_id`/`access_key_secret`/`security_token` 三者命名正确 ✓） | **待确认（非阻断 / 与 §5-52 同字段）**：字段改名 `expireTime` → **`expire`**（或加 `@JsonProperty("expire")`）；类型 `Long` vs 文档 `int` 见 §5-52；属 **breaking**，随 `1.18.0.0` 发布 |
| 98 | `FileUploadCallbackData.MediaFileMetadata.gimbalYawDegree` Type 冲突（`float` vs 字符串示例） | 文档 `file.metadata.gimbal_yaw_degree` Type=**`float`**，但同页 example 为 **`"-91.40"`（带引号字符串）**；SDK [`MediaFileMetadata.gimbalYawDegree`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/media/FileUploadCallbackData.java#L71-L77) = **`String`**（对齐示例） | **待确认（非阻断）**：取 Type(`float`→`Double`) 还是 example(`String`)；与 §5-4（Type/示例冲突）同类；本页示例为字符串 → SDK 现 `String` 有据，待开发者确认 |
| 99 | `storage_config_get` reply `provider` 未建模为枚举 | SDK [`StorageConfigGetReply.Output.provider`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/config/StorageConfigGetReply.java#L42-L49) = 裸 `String`；文档 `enum_string{ali阿里云,aws亚马逊云,minio}` | **待确认（非阻断，可选增强）**：是否补 `StorageProvider` 枚举；与 §5-95 同类，由开发者决定是否统一枚举化 |
| 100 | `flight_setup_exception_notify`（事件）**缺 POJO** + `EventMethod` Javadoc 冲突 | Dock1 页（A55）事件 data = `sn`(text)/`timeout_time`(int, 2-10 分钟)/`timestamp`(double)/`flight_type`(int 1航线/2指令飞行)，example 另含 `flight_id`、`need_reply=1`，reply `{result}`；SDK [`EventMethod.FLIGHT_SETUP_EXCEPTION_NOTIFY`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/protocol/method/EventMethod.java#L84) 常量**已存在**（needReply=1 ✓）但 Javadoc 记「data 含 `error_code`/`error_reason`」**与文档矛盾**，且**无 `FlightSetupExceptionNotifyData` POJO** | **待确认（非阻断，但属协议缺口）**：①**新建** `FlightSetupExceptionNotifyData{sn, timeoutTime, timestamp, flightType}`；②**更正** `EventMethod` 常量 **Javadoc 字段描述**（L75 记 `error_code`/`error_reason` → 应为 `sn`/`timeout_time`/`timestamp`/`flight_type`）；③**厘清矛盾**：类级 `@Verified`(basis L46 含该方法) 与常量级 `@Inferred` 并存；该 `@Inferred` 实为 `need_reply`（Dock1 页 Example 含 `need_reply=1` → 可升级 `@Verified`）；④`flight_type` 是否建模枚举（值域 1/2）；纯新增（无破坏） |
| 101 | `flight_setup_abort`（Dock1 独有）reply 错误码未入 Javadoc | Dock1 页（A55）service `flight_setup_abort`（data=null；reply `result`=0 成功 / **326108** 当前状态不支持 / **326109** 因飞行器已起飞不支持取消，可通过返航按钮取消；仅限 prepare 阶段 `current_step=21` 调用，区别于 `flighttask_undo`）；SDK [`ServiceMethod.FLIGHT_SETUP_ABORT`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/protocol/method/ServiceMethod.java#L181) 存在 ✓，data/reply 由 `NoParameterRequest`+`NoOutputReply` 覆盖 ✓ | **非阻塞 → Javadoc 补充**：将 reply 错误码 326108/326109 与调用时机（prepare 阶段 `current_step=21`）补入 `FLIGHT_SETUP_ABORT` 常量注释；**无新建 POJO**（`NoParameterRequest`/`NoOutputReply` 已覆盖 data=null + reply `result`） |
| 102 | `simulate_mission.altitude` 字段机型归属 | Dock1 页（A55）`flighttask_prepare.simulate_mission` = **仅 3 字段** `{is_enable(enum_int 0不开启/1开启), latitude(double), longitude(double)}`，**无 `altitude`**；SDK [`SimulateMission`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/SimulateMission.java#L40-L45) 含 4 字段（`altitude`，`@Inferred(reason="takeoff_to_point 未解析 altitude…")`）；Dock3 页含 `altitude` | **待确认（非阻断）**：`altitude` 是否 **Dock3 专属**；SDK 统一 record（`Double` 包装类型，Dock1 缺席 null）暂可承载；将 Dock1 页证据补入 `@Inferred` reason/verifyPoint，或按机型拆分（**倾向保留统一 + `@Inferred`**） |
| 103 | Dock1 `flighttask_prepare` 字段集为 Dock3 页**子集** | Dock1 页（A55）`flighttask_prepare` 顶层字段 = `{flight_id, execute_time, task_type, file{url,fingerprint}, ready_conditions{battery_capacity,begin_time,end_time}, executable_conditions{storage_capacity}, break_point{index,state,progress,wayline_id}, rth_altitude(20-1500), out_of_control_action(enum 0返航/1悬停/2降落), exit_wayline_when_rc_lost(enum 0继续/1退出), simulate_mission}`；**无** `rth_mode`/`wayline_precision_type`/`flight_safety_advance_check`（Dock3 页有）；SDK [`FlighttaskPrepareRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/wayline/FlighttaskPrepareRequest.java#L45-L61) 为三页**并集**（含上述 3 字段，均包装类型） | **待确认（非阻断）**：`rth_mode`/`wayline_precision_type`/`flight_safety_advance_check` 是否 **Dock3 专属**；统一 POJO 超集在 Dock1 侧缺席为 null（可承载）；`wayline_precision_type` 既有 `@Inferred`（文档未明确）保留；不改字段（或裁决是否按机型拆分） |
| 104 | `air_conditioner_mode_switch` / `battery_store_mode_switch` 请求字段名 `mode` **≠** 文档 `action` | Dock1「远程调试」页（A57，页面日期 2025-08-05）services 请求 data：`air_conditioner_mode_switch` = **`action`**（enum_int {0空闲,1制冷,2制热,3除湿}）、`battery_store_mode_switch` = **`action`**（enum_int {1计划,2待命}）、`alarm_state_switch` = **`action`**（enum_int {0关闭,1开启}）、`battery_maintenance_switch` = **`action`**（enum_int {0关闭,1开启}）；SDK [`AirConditionerModeSwitchRequest.mode`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/debug/AirConditionerModeSwitchRequest.java#L41-L44) / [`BatteryStoreModeSwitchRequest.mode`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/debug/BatteryStoreModeSwitchRequest.java#L57-L60) 用 **`mode`**（均 `@Inferred`，字段名取自 coverage-review.html §1.3）；[`AlarmStateSwitchRequest.action`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/debug/AlarmStateSwitchRequest.java#L41-L44) 已用 `action` ✓。**本页为该 4 字段首次获 DJI 官方原文**（A5 O4 `cmd.html` 未列） | **待确认（非阻断，但属协议忠实性）**：字段名 `mode` → **`action`**（JSON `action`），对齐文档并与 `alarm_state_switch`/`ActionSwitchRequest` 统一；`BatteryStoreModeSwitchRequest` 的类型化枚举 `BatteryStoreMode` + `@JsonValue` **保留**（值域已核实）；`@Inferred` 依据改为「DJI 文档 A57」；属 **breaking**（record 字段名变更），随 `1.18.0.0` 发布 |
| 105 | `ActionSwitchRequest` 疑似**死代码** + `battery_maintenance_switch` 无专用请求 POJO | SDK [`ActionSwitchRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/ActionSwitchRequest.java) = `record(Integer action)`，Javadoc 称「适用于 air_conditioner/alarm_state/battery_store/battery_maintenance」，但全仓**无任何引用**（仅自身声明；测试与 `debug/package-info` 只引用 3 个专用 POJO）；Dock1 页（A57）`battery_maintenance_switch` data = **`action`** enum_int{0关闭,1开启} → SDK **无** `BatteryMaintenanceSwitchRequest`（若不用 `ActionSwitchRequest` 则无请求 POJO 承载） | **待确认（非阻断）**：裁决 `ActionSwitchRequest` 去留——①**删除**（AGENTS §2「未使用的接口相关代码必须删除」，职责由 3 个专用 POJO 承担；则须为 `battery_maintenance_switch` 新建 POJO）②**保留为唯一通用 POJO**（则删除 3 个专用 POJO、统一用 `ActionSwitchRequest`，但会丢失 `BatteryStoreMode` 类型化枚举优势）；二者取一，避免「通用 + 专用」双份 |
| 106 | 12 进度事件 `progress.step_key` 值域未建模枚举（可选增强） | Dock1 页（A57）各进度事件的 `progress.step_key`（enum_string）**值域因方法而异**（`drone_close` 7 值、`device_reboot` 6 值、`cover_close` 9 值、`cover_open` 5 值、`charge_open` 6 值、`charge_close` 4 值、`putter_close`/`putter_open` 各 5 值；`cover_force_close`/`drone_format`/`device_format` 无或仅示例 `"xxx"`）；SDK [`ServiceProgressData.Progress.stepKey`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/system/ServiceProgressData.java#L68-L71) = 裸 `String` | **待确认（非阻塞，可选增强）**：是否按各方法建模 `step_key` 枚举（如 `DroneCloseStep`/`CoverCloseStep`…）；与 §5-95/§5-99 口径一致，SDK 现裸 `String` 可承载，由开发者决定是否枚举化 |
| 107 | Dock1 页 `fileupload_start` 字段集为 O4 页**子集** + `credentials.expire` 单位口径差异 | Dock1 页（A59，2025-03-19）`fileupload_start` Data **无** `file_store_dir`/`file_type`（O4 页 A40 有）；`params.files[]` **无** `device_sn`/`result`（仅 `object_key`/`module`/`list`）、`list[]` 仅 `boot_index`（无 `start_time`/`end_time`/`size`）；`credentials.expire` 标 **秒/s**（O4 页标 ms），但两页 example 均 **13 位 ms**（`1659432522000`）→ 自相矛盾 | **待确认（非阻断）**：§4 原「依 O4 页（A40）补齐 [`FileUploadStartRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/log/FileUploadStartRequest.java) 的 `file_store_dir`/`file_type`/`params.files[].device_sn`/`result`/`list[]` 字段」是否仍执行——①**按超集保留**（Dock1 为子集，缺省 null）②按机型拆分（违背「统一 POJO 超集」惯例）；建议 ①，Javadoc 注明 Dock1 页未定义这些字段 |
| 108 | `ConfigReply` 字段集与文档严重不符（`url`/`token`/`result`） | Dock1「配置更新」页（A60，2025-06-09；= A7 O4 `config` 页镜像）回复 data = `{ntp_server_host(text), app_id(text), app_key(text), app_license(text), ntp_server_port(int, 缺省 123)}`，**无 `result`**；SDK [`ConfigReply`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/registration/ConfigReply.java) = `{Integer result(必填), String appId, String appLicense, String url, String token}` | **待确认（非阻断 / A7 此前仅记 §3、漏登记 §4/§5，本页补齐）**：重建为 `{appId, appKey, appLicense, ntpServerHost, ntpServerPort(Integer)}` —— `url`→`ntp_server_host`（字段名不符）、`token`→`ntp_server_port`（字段名+类型不符，文档 `int`）、补 `appKey`/`ntpServerPort`；文档回复 **无 `result`** → 是否移除 `result`（现 `Objects.requireNonNull` 必填，移除属 breaking）须裁决；`@Inferred(reason/verifyPoint)` 可据本原文定稿/升级为 `@Verified`；属 **breaking**，随 `1.18.0.0` 发布；同步改 `ConfigReplyTest` |
| 109 | `drone_control` / `heart_beat.seq` 废弃口径**跨页冲突** | Dock1「指令飞行」页（A61，**2025-11-12**，最新）`drone_control`（drc/down+up）**无废弃标注**、up 回包 `{result, output{seq}}`；`heart_beat`（drc/down+up）**均含 `seq`**（无废弃标注）。O4 页（A15）标 `drone_control` **已废弃**、`heart_beat.seq` **【废弃】**。SDK [`DrcMethod.DRONE_CONTROL`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/protocol/method/DrcMethod.java) **未标 `@Deprecated`**；[`DroneControlRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/flight/DroneControlRequest.java) Javadoc「已废弃 — Dock1 有效，Dock2/Dock3 已废弃」**部分印证 A61**；[`HeartBeatRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/flight/HeartBeatRequest.java)/[`HeartBeatReply`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/flight/HeartBeatReply.java) 仅 `{timestamp}`，**缺 `seq`** | **待确认（非阻断；breaking 相关，须裁决）**：①§4 L281「`DRONE_CONTROL → @Deprecated`」与 L283「`DroneControlReply`（新建）」**须复核**——Dock1（**最新页**）视 `drone_control` active 且给出 up `{result, output{seq}}`，O4 页视废弃 → 以哪页为准（`@Deprecated` 是否仅针对 Dock2/3 而未标 Dock1？）②`heart_beat` 是否补 `Integer seq`（包装类型，A61 down/up 均含）或维持 `{timestamp}`。**📌 追加证据（A80，Pilot RC Plus 2 远程控制页 2026-10-13/v1.18）：`heart_beat` down/up 的 Data 表均仅 `{timestamp}`（`seq` 为信封层字段，与 data 同级）→ 与 SDK `HeartBeatRequest`/`HeartBeatReply` 现态一致，支持不向 data 补 `seq`；A61「均含 seq」应理解为信封 `seq`** |
| 110 | `hsi_info_push` SDK Javadoc 偏差（`around_distances` 数量 / `up`·`down_distance` 单位） | Dock1 页（A61）`around_distances` Type=`array`、constraint **`{"size": 360}`**（"水平方向观察点，分布在 [0,360) 角度区间…60000 表示该角度没有障碍物"）、`up_distance`/`down_distance` unit **毫米 / mm**。SDK [`HsiInfoPushData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/up/HsiInfoPushData.java) Javadoc：`aroundDistances` 注释「每 30° 一个值（**共 12 个**）」、`upDistance`/`downDistance` 注释「（**米**，整数）」 | **非阻塞 → Javadoc 更正**（**不改字段/类型**）：`aroundDistances` 改「**360 个**值（每 1° 一个值），空数组=任意角度无障碍」；`up`/`downDistance` 单位改「**毫米 / mm**」；**文档自身不一致**：字段表名 `around_distances`（复数）vs 同页 example `around_distance`（单数）→ SDK 字段名 `aroundDistances`（wire `around_distances`）**对齐字段表**，记录示例偏差待 DJI 修订 |
| 111 | Dock1 `takeoff_to_point` 字段集为 O4 页**子集** | Dock1 页（A61）`takeoff_to_point` 顶层 = `{target_latitude, target_longitude, target_height(float 2-1500), security_takeoff_height(20-1500), rth_altitude(int 2-1500), rc_lost_action(enum 0悬停/1着陆/2返航), exit_wayline_when_rc_lost(**【废弃】** enum 0/1), commander_mode_lost_action(enum 0/1), commander_flight_height(float 2-3000), flight_id, max_speed(int 1-15), simulate_mission{is_enable, latitude, longitude}}`；**无** `rth_mode`/`commander_flight_mode`/`flight_safety_advance_check`（O4 页 A10 有）、SDK **无** `exitWaylineWhenRcLost`；SDK [`TakeoffToPointRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/flight/TakeoffToPointRequest.java) 为三页**并集**（含上述 3 字段，均包装类型） | **待确认（非阻断）**：与 §5-102（simulate_mission `altitude`）/§5-103（flighttask_prepare 子集）**同「Dock1 页为子集」模式** → `rth_mode`/`commander_flight_mode`/`flight_safety_advance_check` 是否 **O4/Dock3 专属**；统一 POJO 超集在 Dock1 侧缺席为 null（可承载）；`exit_wayline_when_rc_lost`（文档**已废**）**SDK 不建模正确** ✓；`simulate_mission` 无 `altitude` **二次印证 §5-102**；不改字段（或裁决是否按机型拆分） |
| 112 | `AirSenseWarningData` 的 `@DocUrl` 归属待核 | 本页（Dock1「AirSense」，**2025-03-19**）为 `airsense_warning` 事件归属页；而 SDK [`AirSenseWarningData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/alert/AirSenseWarningData.java#L55) `@DocUrl` = `.../dock/dock1/wayline.html`、`@Verified(basis)` 称「归在 dock1/wayline.html 类目下」。**但 A55 已确证 `dock1/wayline.html`＝「航线管理」页，其 15 条指令中 *无* `airsense_warning`** | **✅ 已结案（2026-10-14，非阻断，元数据）**：**开发者已提供本页 URL** = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/airsense.html` → 更正目标确认：`@DocUrl` 由 `.../dock1/wayline.html` 改为 `.../dock1/airsense.html`（**仅文件名差异，前缀一致**）；`@Verified(basis)` 归属表述同步更正（A62）；**Java 层更正待统一落地**（遵循「原文收齐前不编码」）；不影响字段/类型/枚举（§5-11 已结案）。**📌 复核追加（A74，Dock1「AirSense」2025-03-19）：本页内容逐字复现 A62（重复页复核，差异 0）；本页为 `airsense_warning` 归属页但本次投喂仍未附 URL → §5-112（`@DocUrl` 归属更正）仍未解除，待开发者给出该页 URL（不猜测）**。**📌 再次复核（A78，第 3 次投喂）：仍为同一页、逐字一致 → 内容已完备，唯一缺口确认为「该页 URL」；提供浏览器地址栏链接即可结案 → **✅ 2026-10-14 该链接已提供（结案依据见本行判定）** |
| 113 | `ServiceMethod` 缺 `flight_areas_update`（Dock1 自定义飞行区 service） | Dock1「自定义飞行区」页（A63，2025-03-19）service `flight_areas_update`：Topic `thing/product/{gateway_sn}/services`（down）/ `services_reply`（up），请求 **Data=null**、回复 data `{result(int, 非 0 错误)}`。SDK [`ServiceMethod`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/protocol/method/ServiceMethod.java) **无** `FLIGHT_AREAS_UPDATE`（全仓无对应 POJO） | **待确认（非阻断）**：①补 `ServiceMethod.FLIGHT_AREAS_UPDATE("flight_areas_update", "自定义飞行区更新")`（机型归属 **Dock1 独有 / 三 Dock 共有** 待核，与 `flight_areas_get` 同源）；②请求复用 `NoParameterRequest`、回复复用 `NoOutputReply`；③**更正 §5-76**：`flight_areas_update` 曾被列为「仅见于 DJI 仓库、未见于官方文档」→ 本页为**官方原文**，已移出该清单 |
| 114 | `FlightAreasGetReply.Output` 字段形态与文档不符（`file` 单数 → `files` 数组；缺 `url`/`size`） | Dock1「自定义飞行区」页（A63）`flight_areas_get` requests_reply data：`output{ files: array<struct>{ name(text), url(text), checksum(text SHA256), size(int) } }`（**空数组=无自定义飞行区**）；示例 `"output":{"files":[{"checksum":"sha256","name":"geofence_xxx.json","size":500,"url":"…"}]}`（确证 `files` 为**数组**含 `url`/`size`）。SDK [`FlightAreasGetReply.Output`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/flightarea/FlightAreasGetReply.java) = `{ SyncFile file }`（**单数** struct），`SyncFile` 仅 `{name, checksum}`（**缺 `url`/`size`**） | **待确认（非阻断，breaking）**：重建 `Output` 为 `files`（**数组**，项 4 字段含 `url`/`size`）；同步改 [`FlightAreasGetReplyTest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/test/java/ltd/cdmi/dji/cloudapi/sdk/command/request/flightarea/FlightAreasGetReplyTest.java)（现 `SAMPLE_JSON` 用单数 `file`、无 `url`/`size`）；属 **breaking**，随 `1.18.0.0` 发布 |
| 115 | flight-area 4 类 `@DocUrl` 归属待核 | 本页（Dock1「自定义飞行区」，2025-03-19）为 4 个方法（`flight_areas_sync_progress`/`flight_areas_drone_location`/`flight_areas_update`/`flight_areas_get`）的归属页；SDK [`FlightAreasSyncProgressData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/flightarea/FlightAreasSyncProgressData.java) / [`FlightAreasDroneLocationData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/flightarea/FlightAreasDroneLocationData.java) / [`FlightAreasGetRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/flightarea/FlightAreasGetRequest.java) / [`FlightAreasGetReply`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/flightarea/FlightAreasGetReply.java) `@DocUrl` 统一 = `.../dock/dock3/wayline.html`。**但 A41 已确证 `dock3/wayline.html`（航线管理）原文不含任何 flight_areas 内容** | **✅ 已结案（2026-10-14，非阻断，元数据）**：**开发者已提供该页 URL** = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/custom-fly-region.html`（Dock3 版「自定义飞行区」）→ 更正目标确认：4 类 `@DocUrl` 由 `.../dock/dock3/wayline.html` 改为 `.../dock/dock3/custom-fly-region.html`（与 A41「wayline 页不含任何 flight_areas 内容」一致）；`@Verified(basis)` 由 simulator 升为官方文档；**Java 层更正待统一落地**（遵循「原文收齐前不编码」）；4 方法 Dock1/三 Dock 归属（内容两侧均已获原文，A63/A77）→ 取现 `@DocUrl` 同 Dock（Dock3）修正页名，与 §5-112 同模式。**📌 追加证据（A77，Dock3「自定义飞行区」2025-03-19）：Dock3 版页面原文亦已投喂（与 A63 Dock1 版逐字一致，差异 0）→ 4 方法与 POJO 的归属页在 Dock1/Dock3 两侧均已获原文**；**但两页 URL 仍未提供 → §5-115 曾待 URL（不猜测）** → **✅ 2026-10-14 该 URL 已提供（Dock3 版，结案依据见本行判定）** |
| 116 | `FlightAreasGetRequest` 专用空 record 与「复用 `NoParameterRequest`」惯例 | SDK [`FlightAreasGetRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/flightarea/FlightAreasGetRequest.java) = `record FlightAreasGetRequest() {}`（`command.request.flightarea`）；DJI 文档请求 **Data=null**；既有 [`NoParameterRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/NoParameterRequest.java) Javadoc 已将 data=null 的 `poi_mode_exit` 纳入复用清单（「无参数指令复用 NoParameterRequest，避免为每个无参数指令创建空 record 文件」）。全仓仅 **3 个空 record**（`NoParameterRequest`/`NoParameterDrcRequest`/`FlightAreasGetRequest`） | **待确认（非阻断，非协议）**：裁决 `flight_areas_get` 请求**复用 `NoParameterRequest`**（消重，与 services/Pilot 惯例一致）或**保留专用 record**（requests 通道类型自洽）；两者均序列化 `{}`，**非协议偏差** |
| 117 | `storage_config_get.module` 取值语义与 SDK Javadoc 不符 | Dock1「PSDK 功能」页（A64，2025-03-19）requests `storage_config_get` 请求 data：`module` `enum_int` `{"0":"媒体","1":"psdk ui 资源"}`（本页用于获取 PSDK UI 资源包上传临时凭证）。SDK [`StorageConfigGetRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/config/StorageConfigGetRequest.java) Javadoc 现记「`module` 取值：`0=媒体`、`1=日志`」（`@DocUrl` = `dock3/media.html`）；A54 记 `module` `enum_int`{0 媒体} | **待确认（非阻断，口径）**：本页确证 **1 = psdk ui 资源**；SDK Javadoc 的「1=日志」疑源自 `media.html` 上下文 → 比对 `media.html` 原文后统一表述（若两页 `module=1` 语义不同，属文档自相矛盾，需记录）；可选：补 `module` 枚举（现裸 `Integer`，同 §5-99 口径）。**📌 追加核对（A70，Dock3 媒体管理 2026-08-19）：`dock3/media.html` 原文 `module` 仅 `{"0":"媒体"}`（未定义 `1`）→ SDK Javadoc「1=日志」无官方依据（`1=psdk ui 资源` 仅见 A64 PSDK 页）→ 待开发者裁定 `module=1` 权威语义** |
| 118 | `speaker_*` 事件 3 类 POJO `@DocUrl` 归属待核 | 本页（Dock1「PSDK 功能」，2025-03-19）含 events `speaker_audio_play_start_progress`/`speaker_tts_play_start_progress`（+ services `speaker_replay`/`speaker_play_stop`/`speaker_play_mode_set`/`speaker_play_volume_set`/`speaker_audio_play_start`/`speaker_tts_play_start`）。SDK [`SpeakerOutput`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/speaker/SpeakerOutput.java) / [`SpeakerTtsPlayStartProgressData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/speaker/SpeakerTtsPlayStartProgressData.java) / [`SpeakerAudioPlayStartProgressData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/speaker/SpeakerAudioPlayStartProgressData.java) 的 `@DocUrl` 统一指 `.../dock/dock3/wayline.html`；而 services 侧 `speaker_*` POJO（`SpeakerReplayRequest` 等）指 `.../dock/dock3/psdk.html` | **✅ 已结案（2026-10-14，非阻断，元数据）**：**开发者已提供本页 URL** = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/psdk.html` → 更正目标确认：3 类事件 POJO `@DocUrl` 由 `.../dock3/wayline.html` 改为 `.../dock3/psdk.html`（与 services 侧一致）；**Java 层更正待统一落地**（遵循「原文收齐前不编码」）。**📌 追加证据（A71，Dock3「PSDK 功能」2026-08-19）：`speaker_audio_play_start_progress`/`speaker_tts_play_start_progress` 事件确在本「PSDK 功能」页原文内（与 services 侧同页）→ 佐证 3 类事件 POJO `@DocUrl` 应为 `psdk.html`（非 `wayline.html`）；URL 归属仍待开发者确认** |
| 119 | `CustomDataTransmissionToPsdkRequest` 的 `@Verified(basis)` 误称「文档未列出」+ `@DocUrl` 归属 | Dock1「PSDK 互联互通」页（A65，2025-03-19）**显式列出** service `custom_data_transmission_to_psdk`（`services`/down，data `{value(text, 长度<256)}`；`services_reply` `{result(int, 非0错误)}`）+ event `custom_data_transmission_from_psdk`（`events`/up，同 `{value}`）。SDK [`CustomDataTransmissionToPsdkRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/psdk/CustomDataTransmissionToPsdkRequest.java) 现 `@Verified(basis = "simulator … 已对接 hivemind 验证（**DJI 文档未显式列出，依据 hivemind 实测**）")`、`@DocUrl` = `dock3/psdk.html` | **✅ 已结案（2026-10-14，非阻断，元数据）**：**开发者已提供本页 URL** = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/psdk-transmit-custom-data.html` → ①`@Verified(basis)` 更正——该 service 确为官方文档协议（Dock3 `psdk-transmit-custom-data.html`），删去「DJI 文档未显式列出此方法」表述；②`@DocUrl` 由 `.../dock3/psdk.html` 更正为 `.../dock3/psdk-transmit-custom-data.html`（与事件侧 [`CustomDataFromPsdkData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/psdk/CustomDataFromPsdkData.java) 的 `@DocUrl` 一致）；字段/类型/枚举无偏差；**Java 层更正待统一落地**（遵循「原文收齐前不编码」）。**📌 追加证据（A72，Dock3「PSDK 互联互通」2025-03-19）：本页为其 Dock3 归属页 `dock3/psdk-transmit-custom-data.html` 原文（1 event + 1 service 逐字复核 A65）→ 佐证本项目标 `@DocUrl` 正确（事件侧 `CustomDataFromPsdkData` 已指向该 URL）** |
| 120 | `CustomDataTransmissionToEsdkRequest` 的 `@Verified(basis)` 误称「文档访问受限」+ `@DocUrl` 归属 | Dock1「ESDK 互联互通」页（A66，2025-03-19）**显式列出** service `custom_data_transmission_to_esdk`（`services`/down，data `{value(text, <256)}`；`services_reply` `{result(int, 非0错误)}`）+ event `custom_data_transmission_from_esdk`（`events`/up，同 `{value}`）。SDK [`CustomDataTransmissionToEsdkRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/esdk/CustomDataTransmissionToEsdkRequest.java) 现 `@Verified(basis = "simulator EsdkSimulator … （**DJI Dock3 esdk.html 文档访问受限**（cookie 验证），依据 hivemind 实测）")`、`@DocUrl` = `dock3/esdk.html` | **✅ 已结案（2026-10-14，非阻断，元数据）**：**开发者已提供本页 URL** = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/esdk-transmit-custom-data.html` → ①`@Verified(basis)` 更正——该 service 确为官方协议（Dock3 `esdk-transmit-custom-data.html`），删去「DJI 文档访问受限」表述；②`@DocUrl` 由 `.../dock3/esdk.html` 更正为 `.../dock3/esdk-transmit-custom-data.html`（与事件侧 [`CustomDataFromEsdkData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/esdk/CustomDataFromEsdkData.java) 一致）；字段/类型/枚举无偏差（与 §5-119 对称）；**Java 层更正待统一落地**（遵循「原文收齐前不编码」）。**📌 追加证据（A73，Dock3「ESDK 互联互通」2025-03-19）：本页为其 Dock3 归属页 `dock3/esdk-transmit-custom-data.html` 原文（1 event + 1 service 逐字复核 A66）→ 佐证本项目标 `@DocUrl` 正确（事件侧 `CustomDataFromEsdkData` 已指向该 URL）** |
| 121 | RC Plus 2「设备管理」页 `update_topo` 口径与 Dock1/RC Pro 不一致 | 三页原文（A81）：Dock1/RC Pro 设备管理页 `update_topo` topic = `sys/product/{gateway_sn}/status`、UP data 顶层 7 字段（含 `domain`）、`sub_devices[]` 8 字段（含 `index`）；RC Plus 2 页 topic = **`thing/product/{gateway_sn}/status`**（非 `sys`）、UP data 顶层 **6 字段（缺 `domain`）**、`sub_devices[]` **6 字段（缺 `domain`/`index`）**。SDK [`UpdateTopoData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/status/UpdateTopoData.java) 顶层 `domain` 为 `Objects.requireNonNull`（必填）→ 若 RC Plus 2 场景确不发 `domain`，反序列化将 **NPE 抛错**；[`SubDevice.index`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/status/UpdateTopoData.java#L60-L82) 已可空（Javadoc 已述「RC Plus 2 不上报 domain/index」✓，本页获原文佐证） | **待确认（非阻断；breaking 相关）**：①RC Plus 2 的 `update_topo` topic 用 `thing/product` 是否**文档笔误**（其余页为 `sys/product`，SDK `StatusMethod`/`PilotRegistrationFlow` 按 `sys/product` 构建）？②顶层 `domain` 在 RC Plus 2 是否**可空**——若可空须放宽 `UpdateTopoData.domain` 的 `requireNonNull`（breaking），与 `SubDevice.domain/index` 已可空口径对齐；③RC Plus 2 页本次投喂**不完整**（缺 Example/`status_reply`）→ 需补全原文后定稿。**✅ 已结案（2026-10-14，A83，撤销）**：开发者澄清并投喂 RC Plus 2「设备管理」页**完整版**原文 → topic = `sys/product/{gateway_sn}/status`、UP data 顶层 **7 字段（含 `domain`）**、`sub_devices[]` **8 字段（含 `domain`/`index`）**、`status_reply` 仅 `{result}` → **与 Dock1/RC Pro 完全一致** → 本项 A81 记载的 `thing/product` + 缺 `domain` **系该页旧版/转录差异，非现行口径** → **撤销 §5-121**（SDK `UpdateTopoData.domain` 必填、`sys/product` topic 均正确，**无 SDK 改动**）。**未写代码** |
| 122 | `RcOsd` 电量 wire key（`battery` vs 文档 `capacity_percent`）+ `mode_code` 去留 + `@DocUrl` 归属 | RC Pro「设备属性」页（A82，2025-09-03）osd（pushMode=0）列 **`capacity_percent`**（遥控器剩余电量，`int` 0-100，`r`）→ SDK [`RcOsd`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/RcOsd.java) 字段名为 **`battery`**（wire = `battery`，Javadoc「电池电量百分比（0-100）」）→ **wire key 与文档不符，反序列化丢失电量**；本页 osd 列表 **未含 `mode_code`** → SDK `RcOsd.modeCode` 来源待核；`@DocUrl` = `dji-rc-plus-2/properties.html` 而类 Javadoc 参考链接 = `remote-controller/properties.html`（第三页） | **待确认（非阻断；wire 修正属 breaking）**：①裁决 `battery` → **`capacityPercent`**（对齐文档 `capacity_percent`，breaking）或确认文档口径；②`modeCode` 是否为 RC 字段（是否删除）；③`@DocUrl`/参考链接统一。**需补投喂 RC Plus 2 行业版「设备属性」页原文（`dji-rc-plus-2/properties.html`）**定稿。**📌 追加裁决（A84，RC Plus 2「设备属性」页 2025-09-03）：该页与本项（RC Pro 页）逐字一致，osd 同为 `capacity_percent`、state 同为 live_capacity/dongle_infos/live_status/firmware_version/cloud_control_auth，**两页均无 `mode_code`** → ①**裁决：`RcOsd.battery` → `capacityPercent`**（对齐文档 `capacity_percent`，breaking，随 `1.18.0.0`；若 simulator/hivemind 实测 wire 为 `battery`，需评估 `@JsonAlias("battery")` 兼容并在 TDD 覆盖两种 wire → **待确认**）；③`@DocUrl`（`dji-rc-plus-2/properties.html`）**已确认正确**，Javadoc 参考链接 `remote-controller/properties.html` 应更正统一；②`RcOsd.modeCode` **两项 RC 属性页均无 `mode_code`** → **仍待确认**：需 `remote-controller/properties.html` 原文佐证，或裁决**删除 `modeCode`**。**📌 证据闭环（A86，通用 RC「设备属性」页 `.../mqtt/others/rc/properties.html` 2025-07-23）：该页 osd/state 列表亦**无 `mode_code`** → 三页（RC Pro / RC Plus 2 / 通用 RC）全部无 → `RcOsd.modeCode` **无任何官方文档依据 → 建议裁决删除 `modeCode`**（breaking，随 `1.18.0.0`，移除后 `RcOsd` 余 7 字段）；③`@DocUrl`/类 Javadoc 链接更正目标 = `.../pilot-to-cloud/mqtt/others/rc/properties.html`（通用页，开发者提供；原 Javadoc 链接 `remote-controller/properties.html` 系**错误路径**）→ **`@DocUrl` 统一目标待开发者最终裁决**（通用页 vs 机型页 `dji-rc-plus-2/properties.html`） |
| 123 | `StateField` 若干字段 Javadoc 归属未含 RC | RC Pro「设备属性」页（A82）state（pushMode=1）含 `live_capacity`/`dongle_infos`/`live_status`/`firmware_version`/`cloud_control_auth`；SDK [`StateField`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/StateField.java) 注释：`LIVE_CAPACITY`「Dock state」、`DONGLE_INFOS`「Dock2/Dock3 + M400」、`LIVE_STATUS`「Dock state」、`FIRMWARE_VERSION`「Dock state + 飞行器 state」**均未列 RC**；`CLOUD_CONTROL_AUTH`「Pilot 特有」✓ | **非阻塞 → Javadoc 更正**：4 字段注释补「RC state 亦上报」（`cloud_control_auth` 已正确）；枚举/常量不变。**✅ 已结案（2026-10-14，A84）**：RC Plus 2「设备属性」页 state 段与 RC Pro 页逐字一致（同样上报 `live_capacity`/`dongle_infos`/`live_status`/`firmware_version`）→ 确证 4 字段 Javadoc 应补「RC state 亦上报」；**Java 层 Javadoc 更正待统一落地**（枚举/常量不变）。**未写代码** |
| 124 | `dongle_infos` 5 个子枚举未建模 | 通用 RC「设备属性」页（A86，2025-07-23）`dongle_infos` 子字段枚举：`esim_activate_state`{0未激活,1已激活}、`sim_card_state`{0未插入,1已插入}、`sim_slot`{0未知,1实体SIM卡,2eSIM}、`esim_infos[].telecom_operator`/`sim_info.telecom_operator`{0未知,1移动,2联通,3电信}、`sim_info.sim_type`{0未知,1其他普通SIM卡,2三网卡}；SDK 仅 `DongleType{6,10}`（对应 `dongle_type`）✓，其余 5 者**无对应枚举**（全仓无 `SimSlot`/`SimCardState`/`TelecomOperator`/`SimType`/`EsimActivateState`） | **待确认（非阻断，可选）**：A82 已用 `StateField.DONGLE_INFOS` + `DongleType` 捕获该字段（**字段不缺失**）；若需强类型建模 dongle 内层，建议补 5 枚举 + `DongleInfo`/`EsimInfo`/`SimInfo` record；不补亦不违反协议忠实性（字段名已由 `StateField` 登记）→ **由开发者裁决** |

---

## 6. 版本与文档同步

- `pom.xml`：`1.16.1.3` → `1.18.0.0`
- `architecture-design.md`：更新协议覆盖统计（method 数、包结构新增 `fts/`、`peripheral/`、`GroundStationOsd`）、版本标注 v1.18
- `tdd-test-cases.md`：补全 v1.18 全部新增项的 Given-When-Then 用例（TDD 先行）
- `README.md`：版本说明与协议版本对齐

---

## 7. 验证计划

1. **TDD 先行**：在 `tdd-test-cases.md` 写入 v1.18 新增项的规格用例（字段缺席 → null、枚举值域、snake_case 映射）。
2. **实现**：按 §4 改动清单分模块落地，不确定项一律 `@Inferred`。
3. **验证**：`mvn test`，汇报 Tests run / Failures / Errors / Skipped；新增功能同步补充 Java 单元测试。
4. **文档对齐**：核对每项与 DJI 官方文档一致性，更新 `architecture-design.md` / `tdd-test-cases.md`。

---

## 8. 附录：开发者提供的官方文档原文（权威依据）

### A1. O4 地面站存储格式化进度（2026-10-14 提供）

- Topic：`thing/product/{gateway_sn}/events`，Direction：up，Method：`device_format`
- Data：`result`(int, 非 0 错误) + `output{ status(enum_string: canceled/failed/in_progress/ok/paused/rejected/sent/timeout), progress{ percent(int, 0–100) } }`

**对照结论**：SDK 现有 `ServiceProgressData`（`command/event/system/`）结构完全覆盖，**无需新增 POJO**。
**唯一待修**：`ServiceProgressData` 的 Javadoc 现写 `output.status` 枚举仅 `in_progress/ok/failed`（3 值），文档实为 **8 值** → 实现时补全枚举说明（字段为 `String`，类型不变）。

### A2. O4 地面站设备属性（2026-10-14 提供）— `ground-station/o4/properties.html`

Topic：osd=`thing/product/{device_sn}/osd`（pushMode=0 定频）、state=`thing/product/{device_sn}/state`（pushMode=1 变化上报）；Direction：up。
property/set：O4 自身 osd/state 属性**均为只读**，`property/set` 仅用于将可写属性**透传**给所连接飞行器（`{gateway_sn}` 为地面站 SN）→ **无新增 `PropertySetMethod`**。
另：基站模式下地面站透传所连飞行器负载属性（相机云台俯仰/偏航/横滚等），结构见 Dock3 负载属性 example。

**osd（pushMode=0）字段：**

| 字段 | 类型 | 枚举/约束 | SDK 现状 |
|---|---|---|---|
| `mode_code` | enum_int | 0 空闲中/1 现场调试/2 远程调试/3 固件升级中/4 作业中/5 待标定/6 低功耗/9 日志上报中 | 字段已有，**枚举值域为地面站专用，需新建枚举** |
| `low_power_switch_state` | enum_int | 0 关闭/1 打开 | **新增** |
| `acc_time` | int | 秒 | 已有（`StateField`，**归属需移至/兼列 `OsdField`**） |
| `longitude` / `latitude` / `height` | double | — | 已有 |
| `electric_supply_power` | int | W | **新增** |
| `electric_supply_voltage` | double | V | 已有（确认 **double/V**） |
| `power_mode` | enum_int | 0 电池/1 PD/2 POE | **新增** |
| `battery_voltage` | int | mV | **新增** |
| `position_state` | struct | `is_calibration`{0,1} / `is_fixed`{0,1,2,3} / `quality`1–5 / `gps_number` / `rtk_number` | 已有，嵌套待补 |
| `battery` | struct | `capacity_percent`0–100 / `voltage`mV / `current`mA / `temperature`double℃ | 已有，嵌套待补 |
| `self_converge_coordinate` | struct | `longitude`/`latitude`/`height` | 已有，嵌套待补 |
| `tilt_angle` | struct | `value`(double°) / `valid`{0 无效,1 有效} | **新增** |
| `basestation_work_mode` | enum_int | 0 漫游基站/1 外置机场/2 简易机场 | **新增** |
| `under_work_mode` | enum_int | 0 非棚下/1 棚下 | **新增** |
| `alternate_land_point` | struct | `latitude`/`longitude`/`height`/`safe_land_height`int/`is_configured`{0,1} | 已有 |
| `network_state` | struct | `type`{1 4G,2 以太网} / `quality`{0 差,1 中,2 好} / `rate`int KB/s | 已有（**type 值域为地面站专用**） |
| `lan_status` | enum_int | 0 未连接/1 速率过低/2 100M/3 1000M/4 不支持 | **新增** |
| `poe_status` | enum_int | 0 无 PoE/1 过低/2 25.5W/3 51W/4 71W | **新增** |
| `storage` | struct | `total`KB / `used`KB | 已有 |

**state（pushMode=1）字段：**

| 字段 | 类型 | 枚举/约束 | SDK 现状 |
|---|---|---|---|
| `electric_supply_mode` | enum_int | 0 电池/1 PD/2 POE | **新增** |
| `is_beidou_version` | bool | — | **新增** |
| `home_position_is_valid` | enum_int | 0 无效/1 有效 | 已有 |
| `heading` | double | 0–360°，**已废弃** | 已有（`OsdField`，**归属需移至/兼列 `StateField`**） |
| `rtcm_info` | struct | `mount_point`/`port`/`host` text / `rtcm_device_type`{0 遥控器,1 机场,2 中继} / `source_type`{0 未标定,1 自收敛,2 手动,3 网络 RTK} | 已有，嵌套待补 |
| `wireless_link_topo` | struct | `secret_code`int[28] / `center_node`{sdr_id, sn, model, device_type} / `leaf_nodes`[]{sdr_id, sn, control_source_index 1–2, model, device_type} | 已有，嵌套需新 struct |
| `peripherals_info` | array | `device_sn`/`name`/`port`{0 LAN,1 WAN}/`type`{0 其它,1 相机,2 DAA,3 雷达,4 电源,5 第三方机库,6 边缘计算,7 RID}/`ip_addr`/`need_auth`bool/`main_device`bool | **新增** |
| `peripheral_list` | array | `sn` / `rid`{supported,version} / `adsb`{supported,version} / `dock_control`{supported,version} | **新增** |
| `sub_device` | struct | `device_sn`/`device_model_key`(domain-type-sub_type,如 0-130-0)/`device_online_status`{0,1}/`device_paired`{0,1} | 已有（`OsdField`，**归属需移至/兼列 `StateField`**） |

> **关键结论（pushMode 按设备类型区分）**：`acc_time` / `heading` / `sub_device` 三个字段在 SDK 现分别位于 `StateField`(acc_time) 与 `OsdField`(heading/sub_device)；O4 文档的 pushMode 与 SDK 归属**相反**。因这些字段在 Dock 机型下归属依据与 O4 不同（同名字段可能需在 `OsdField` 与 `StateField` **双列**），实现时须按各机型文档分别归类，不可盲目迁移。
>
> **已解决的不确定项**：`electric_supply_voltage`=double/V、`heading`=double 且已废弃、`network_state.type`{1:4G,2:以太网}、`acc_time`/`sub_device` 的通道归属 —— 均以本原文为准（更新 §5 第 1/2/3/9 项）。

### A3. O4 设备拓扑更新（2026-10-14 提供）— `ground-station/o4/device.html`

- Topic：`sys/product/{gateway_sn}/status`（up，method `update_topo`）/ `sys/product/{gateway_sn}/status_reply`（down，method `update_topo`）。
- Data：`domain`(string, **`"8"`) / `type`(int, **135**) / `sub_type`(int, **0**) / `device_secret` / `nonce` / `thing_version` / **`base_info{ country_code }`（AES-128-ECB 加密后 Base64）** / `sub_devices[]{ sn, domain, type, sub_type, index, device_secret, nonce, thing_version }`；reply：`result`(int)。
- 基站独立上云时 `sub_devices` 可为空数组。

**对照结论**：SDK `UpdateTopoData`（`command/status/`）字段与文档**除 `base_info` 外完全一致**；`SubDevice` 的 8 个字段（`sn/domain/type/subType/index/deviceSecret/nonce/thingVersion`）与文档**逐字段一致**；Reply（`result`）已有。
**唯一缺口**：`UpdateTopoData` 缺 `base_info`（新增 `BaseInfo{ countryCode }` 嵌套 record）→ 确认 §3.1.3 结论。

### A4. O4 Requests 注册/绑定（2026-10-14 提供）— `ground-station/o4/requests.html`

三条指令：`airport_organization_bind` / `airport_organization_get` / `airport_bind_status`（Topic：`thing/product/{gateway_sn}/requests` up + `requests_reply` down）。

> **更正 §3 结论**：此前判断「SDK `RequestsMethod` 缺这三条指令」**有误**。实际 SDK **均已存在**：`RequestsMethod.AIRPORT_ORGANIZATION_BIND/GET/AIRPORT_BIND_STATUS` + `command/request/registration/` 下 6 个 POJO。

**Request 侧 —— 全部匹配**：

| POJO | SDK 字段 | 文档 | 结论 |
|---|---|---|---|
| `AirportOrganizationBindRequest.BindDevice` | `sn, deviceModelKey, deviceCallsign, organizationId, deviceBindingCode` | 同 5 字段 | ✅ 一致 |
| `AirportOrganizationGetRequest` | `deviceBindingCode, organizationId` | 同 2 字段 | ✅ 一致 |
| `AirportBindStatusRequest.Device` | `sn` | `sn` | ✅ 一致 |

**Reply 侧 —— 3 处缺口**：

| POJO | 问题 | 文档要求 | 处理 |
|---|---|---|---|
| `AirportOrganizationBindReply.ErrorInfo` | 字段名 `errSn`（SNAKE_CASE 将序列化为 `err_sn`，**不匹配**） | `sn` | **改字段名为 `sn`** |
| `AirportOrganizationGetReply` | 仅 `result`，缺 `output`；原 `@Inferred`「output.airports」假设**错误** | `output{ organization_name }` | **补 `Output{organizationName}`，改 `@Verified`** |
| `AirportBindStatusReply` | 仅 `result`，缺 `output`；且 Javadoc「result 0=未绑定/1=已绑定」与文档**语义冲突** | `output{ bind_status[]{ sn, is_device_bind_organization, organization_id, organization_name, device_callsign } }`；`result`=返回码（0 成功/非 0 错误） | **补 `Output`/`BindStatus`，修正 Javadoc** |

**其他确认**：地面站 `device_model_key` = **`8-135-0`**（`GroundStationModel` 定义依据）；示例含地面站 `8-135-0` + 飞行器 `0-130-0` 两个绑定项。

### A5. O4 cmd（events + services）完整原文（2026-10-14 提供）— **阻塞项 1 解除**

**Events（`thing/product/{gateway_sn}/events`, up）**

| method | data 结构 |
|---|---|
| `rtk_calibration` | `result`(int,固定0)；`output{ status`enum_string{failed,ok}`, progress{ percent`int 0–100(ok=100/failed=0)`, currentStep`int固定1`}, ext{ devices[]{ sn, type`enum_int{1手动标定}`, module`**enum_string**{3机场,6中继}`, result`int`, status`enum_string 8 值`}, version`int } } }` |
| `device_format` / `drone_format` / `esim_activate` / `esim_operator_switch` | `result`(int)；`output{ status`enum_string 8 值`, progress{ percent`int`} }` |
| `device_reboot` | `result`(int)；`output{ status`enum_string 8 值`, progress{ percent`int`, step_key`enum_string{get_bid,check_ac_input_state,upgrading_prevent_reboot,check_work_mode,check_task_state,write_reboot_param_file}`} }` |
| `camera_photo_take_progress` | `result`(int)；`output{ status`enum_string{fail失败,in_progress执行中,ok完成}`, progress{ current_step`enum_int{3000未开始/已结束,3002拍摄中,3005合成中}`, percent`int`}, ext{ camera_mode`enum_int{3全景}`} }`（example 含 `need_reply=1`） |

> 8 值状态枚举 = `canceled/failed/in_progress/ok/paused/rejected/sent/timeout`（同 A1）。

**Services（down = `services`，up = `services_reply`）**

| method | 请求 data | 回复 data |
|---|---|---|
| `debug_mode_open` / `debug_mode_close` | null | `output{ status`8 值`}` |
| `rtk_calibration` | `devices[]{ sn, type`enum_int{1手动}`, module`enum_string{3机场,6中继}`, data{ longitude, latitude, height`椭球高`} }` | `output{ status`8 值`}` |
| `low_power_switch` | `action`enum_int{0关闭,1打开} | `result`；`output{ status`**text**（如 "ok"）`}` |
| `sdr_workmode_switch` | `link_workmode`enum_int{0仅SDR,1 4G增强} | `result` |
| `sim_slot_switch` | `imei`, `device_type`enum_string{dock,drone}, `sim_slot`enum_int{1实体SIM,2 eSIM} | `result` |
| `esim_operator_switch` | `imei`, `device_type`{dock,drone}, `esim_operator`enum_int{1移动,2联通,3电信} | `result` |
| `esim_activate` | `imei`, `device_type`{dock,drone} | `result`；`output{ status`8 值`}` |
| `device_reboot` / `device_format` | null | `output{ status`8 值`}` |
| `live_start_push` | `url_type`enum_int{0声网,1 RTMP,3 GB28181,4 WebRTC}, `url`, `video_id`({sn}/{camera_index}/{video_index}), `video_quality`enum_int{0自适应..4超清} | `result` |
| `live_camera_change` | `video_id`, `camera_position`enum_int{0舱内,1舱外} | `result` |
| `live_lens_change` | `video_type`enum_string{ir,normal,wide,zoom} | `result` |
| `live_set_quality` | `video_id`, `video_quality`enum_int{0–4} | `result` |
| `live_stop_push` | `video_id` | `result` |
| `camera_aim` | `payload_index`({type-subtype-gimbalindex}), `camera_type`enum_string{ir,wide,zoom}, `locked`bool, `x`double, `y`double | `result` |
| `camera_look_at` | `payload_index`, `locked`bool, `latitude`, `longitude`, `height`float{2–10000,椭球高} | `result` |
| `camera_frame_zoom` | `payload_index`, `camera_type`{ir,wide,zoom}, `locked`bool, `x`/`y`/`width`/`height`float | `result` |
| `camera_mode_switch` | `payload_index`, `camera_mode`enum_int{0拍照,1录像,2智能低光,3全景拍照} | `result` |
| `camera_photo_take` | `payload_index` | `result`；`output{ status`enum_string{in_progress}（全景等持续拍照，后续走 camera_photo_take_progress）`}` |
| `camera_photo_stop` / `camera_recording_start` / `camera_recording_stop` | `payload_index` | `result` |
| `camera_screen_split` | `payload_index`, `enable`bool | `result` |
| `camera_screen_drag` | `payload_index`, `locked`bool, `pitch_speed`double, `yaw_speed`double（仅不锁机头生效） | `result` |

> 直播录制当前版本暂不开放。

**SDK 对照结论**

- `live_*`（5）、`camera_*`（10）、`debug_mode_open/close`、`rtk_calibration`(service)、`esim_activate/esim_operator_switch`、`sim_slot_switch`、`device_reboot/device_format/drone_format`、`sdr_workmode_switch` → `ServiceMethod` **均已存在**（另有 `camera_focal_length_set` / `gimbal_reset`）。
- **缺口 1（新增）**：`low_power_switch` —— SDK **完全缺失**（`ServiceMethod` 与 Request/Reply 均无）→ 新增 `LOW_POWER_SWITCH` + `LowPowerSwitchRequest{action}` + `LowPowerSwitchReply{result, Output{String status}}`。
- **缺口 2（决策项）**：`EventMethod` **未登记** O4 进度事件 `rtk_calibration` / `device_format` / `drone_format` / `esim_activate` / `esim_operator_switch` / `device_reboot` → 需确认 SDK 约定：是否补登（若补登，事件 method 与 service 同名）。
- **缺口 3（新增 POJO）**：`rtk_calibration` **事件结果 POJO 缺失**；且其结构（`ext.devices[]` / `version` / `progress.currentStep`）与通用 `ServiceProgressData` **不同** → 需**独立 Data record**。
- **缺口 4（修正）**：`ServiceProgressData` Javadoc 方法名 `format_storage` / `power_off` 与文档实际 method `device_format` / `device_reboot` **不一致**，且未覆盖 `drone_format` → 修正。
- **待逐一核对**：`camera_photo_take_progress` POJO（文档为 `output{ext.camera_mode, progress{current_step,percent}, status}` 嵌套，SDK Javadoc 描述似为平铺）、`CameraPhotoTakeReply`、`LiveCameraChangeRequest.cameraPosition` 枚举、`RtkCalibrationRequest`（service）字段与文档的 `module` enum_string 归属。

### A6. O4 空域安全三事件（2026-10-14 提供）— `ground-station/o4/airspace-safety.html`

Topic：`thing/product/{gateway_sn}/events`，Direction：up。

| method | data 结构 |
|---|---|
| `rid_infos` | `capacity_percent`int0–100 / `power_supply`int / `device_sn` / `gnss`{ engine, flag, `time`**String `YYYYMMDD-HHmmss`**（非时间戳）, lat, lon, alt, satellite, hdop } / `rid[]`（**单次最多 50 条**）{ osid, rssi, fre, uatype, status, heading, `speed`**float**, `uprate`**float**, lon, lat, altgeo, altbaro, height, op_lon, op_lat, op_alt, uatime } |
| `adsb_infos` | `capacity_percent`int0–100 / `device_sn` / `power_supply`int / `gnss`（同 RID）/ `adsb[]`{ icao, callsign, `height`int, lat, lon, `speed`int, `heading`int, `uprate`int } |
| `airsense_warning` | data **本身是数组**，元素 { icao, `warning_level`enum_int{0无危险,1..4}, `latitude`float, `longitude`float, `altitude`int（米）, `altitude_type`enum_int{0椭球高,1海拔高}, `heading`float, `relative_altitude`int, `vert_trend`enum_int{0不变,1上升,2下降}, `distance`int（米）}；example `need_reply=1` |

**SDK 对照结论**

- `airsense_warning`：`EventMethod.AIRSENSE_WARNING` **已存在**（need_reply=1 ✓）；`AirSenseWarningData` 用自定义反序列化器将 data 当 **List** 处理 ✓（与文档 data 为数组一致）；`Alert` 的 **10 个字段全部对齐** ✓。
  - **修正 1**：`AirSenseWarningData` 类 Javadoc 写「`need_reply=0` 单向通知」，与 `EventMethod` 的 `need_reply=1` **自相矛盾**，文档 example 为 1 → **修正 Javadoc 为 1**。
  - **待核对**：`Alert.altitude` / `relativeAltitude` / `distance` 在 SDK 为 `Double`，文档标 `int` → 类型差异（JSON number 兼容，建议按文档统一或标 `@Inferred`）。
- `rid_infos` / `adsb_infos`：SDK **完全缺失** → **新增** `EventMethod.RID_INFOS` / `ADSB_INFOS` + `RidInfosData` / `AdsbInfosData`（含共享 `Gnss` 嵌套 record）。
  - 注意类型差异：`rid.speed` / `rid.uprate` 为 **float**，`adsb.speed` / `heading` / `uprate` 为 **int**；`gnss.time` 是字符串 `YYYYMMDD-HHmmss`，**不可当时间戳反序列化**。
- 备注：外设接入登记 / 能力集 / 名称编辑见《设备管理》「外设接入与状态」（已并入 §8-A2 的 `peripherals_info` / `peripheral_list`）。

### A7. O4 Requests —— 获取配置 `config`（2026-10-14 提供）

- 请求 data：`config_type`enum_string{json} / `config_scope`enum_string{product}
- 回复 data：`ntp_server_host` / `app_id` / `app_key` / `app_license` / `ntp_server_port`(int，缺省 123) —— **文档回复 data 中无 `result` 字段**

**对照结论**：`ConfigRequest{configType, configScope}` **一致 ✓**；`ConfigReply` **需重建**：

| SDK 现值 | 文档 | 问题 |
|---|---|---|
| `result`(Integer, 必填) | — | **文档未定义 `result`** → 待确认是否移除 |
| `appId` | `app_id` | ✓ |
| `appLicense` | `app_license` | ✓ |
| `url` | `ntp_server_host` | **字段名不符** |
| `token` | `ntp_server_port` | **字段名不符（且类型 int，非 String）** |
| — | `app_key` | **缺失** |
| — | `ntp_server_port` | **缺失** |

→ 重建 `ConfigReply = { appId, appKey, appLicense, ntpServerHost, ntpServerPort(Integer) }`；原 `@Inferred`（非 output 包裹）可据本原文定稿。

### A8. O4 DRC 负载控制（2026-10-14 提供）— `ground-station/o4/drc.html`

所有 DRC 指令：Topic `thing/product/{gateway_sn}/drc/down`（down）/ `.../drc/up`（up，回复 `{result}`）；**`seq` 与 `data` 同级**（递增序号，保证指令顺序执行）。

| 分类 | method 与要点 |
|---|---|
| 探照灯 | `drc_light_brightness_set`{psdk_index, `group`enum_int{0主探照灯}, `brightness`1–100}；`drc_light_calibration`{psdk_index}；`drc_light_fine_tuning_set`{psdk_index, `position`enum_int{0左灯,1右灯}, `value`int(-3~+3), `saved`bool}；`drc_light_mode_set`{psdk_index, `group`{0主}, `mode`enum_int{0关闭,1常亮,2爆闪,3快速爆闪,4交替爆闪}} |
| 红外/联动 | `drc_linkage_zoom_set`{`payload_index`({type-subtype-gimbalindex}), `state`bool}（仅 M3TD） |
| AI 识别 | `drc_ai_identify_filter_set`{`filters`int[]（三方模型需 **label index +128 偏移**）}；`drc_ai_identify_score_mode_set`{`score_mode`enum_int{0无效,1计数,2搜救,3用户自定义}}；`drc_ai_identify_score_reset`{null}；`drc_ai_identify_score_set`{`score`0–100}；`drc_ai_identify_set`{`on`{0,1}}；`drc_ai_model_select`{`index`int} |
| AI 框选跟随 | `drc_ai_spotlight_zoom_confirm`{null}；`drc_ai_spotlight_zoom_select`{`center_x/y/width/height`int 0–10000（单位 1/10000×画面尺寸）}；`drc_ai_spotlight_zoom_set`{`on`{0,1}}；`drc_ai_spotlight_zoom_stop`{null}；`drc_ai_spotlight_zoom_track`{`target_index`int} |
| 喊话器 | `drc_speaker_play_mode_set`{psdk_index, `play_mode`enum_int{0单次,1循环}}；`drc_speaker_play_stop`{psdk_index}；`drc_speaker_play_volume_set`{psdk_index, `play_volume`1–100}；`drc_speaker_replay`{psdk_index}；`drc_speaker_tts_set`{psdk_index, `volume`0–100, `type`{0男声,1女声}, `language`{0中文,1英文}, `speed`0–100}；**`drc_speaker_tts_play_start`{psdk_index, `tts`{name, text, md5}}** |

**对照结论**：除下列一项外，`DrcMethod` **全部已存在** ✓（探照灯 4、`drc_linkage_zoom_set`、AI 12、喊话器 5 中 4）。
- **缺口（新增）**：`drc_speaker_tts_play_start` —— **DRC 通道**变体缺失。SDK 仅有 **services 通道** 的 `ServiceMethod.SPEAKER_TTS_PLAY_START`，`DrcMethod` 无对应项 → 新增 `DRC_SPEAKER_TTS_PLAY_START` + `DrcSpeakerTtsPlayStartRequest{psdkIndex, Tts{name,text,md5}}` + Reply{result}。

### A9. O4 远程控制（events + services）（2026-10-14 提供）— `ground-station/o4/drc.html`

**events（up，均 `need_reply=1`）**

| method | 结构要点 |
|---|---|
| `fly_to_point_progress` | fly_to_id / `status`enum_string{wayline_cancel,failed,ok,progress} / result / way_point_index / remaining_distance(float 米) / remaining_time(float 秒) / `planned_path_points[]`{lat,lon,height(float 椭球高)} |
| `joystick_invalid_notify` | `reason`int{0遥控器失联,1低电量返航,2低电量降落,3靠近限飞区,4遥控器夺权} |
| `obstacle_avoidance_notify` | wayline_uuid / flight_id / `obstacles[]`{ id, `type`enum_int{0绕行开始,1绕行结束,2避障刹停}, `timestamp`**int64**, lat, lon, height, wayline_id, waypoint_index } / `is_final_report`bool |
| `poi_status_notify` | `status`enum_string{failed,in_progress,ok} / `reason`int{0正常…8丢失遥控器或图传信号} / circle_radius / circle_speed / max_circle_speed(float) |
| `takeoff_to_point_progress` | status{含 task_finish,task_ready,wayline_*} / result / flight_id / track_id / way_point_index / remaining_distance / remaining_time / planned_path_points[] |
| `camera_photo_take_progress` | result / `output`{ `status`enum_string{fail,in_progress,ok}, `progress`{ `current_step`enum_int{3000未开始/已结束,3002拍摄中,3005合成中}, percent }, `ext`{ `camera_mode`enum_int{3全景拍照} } } |
| `speaker_audio_play_start_progress` | result / output{ psdk_index, `status`{in_progress,ok}, md5, `progress`{percent, `step_key`{change_work_mode,download,encoding,play,upload}} } |
| `speaker_tts_play_start_progress` | result / output{ psdk_index, status, md5, progress{percent, `step_key`{change_work_mode,play,upload}} } |

**services（down → reply{result}）**：`drc_mode_enter`{ mqtt_broker{address, client_id, username, password, `expire_time`int(秒), `enable_tls`bool}, osd_frequency 1–30, hsi_frequency 1–30 }；`flight_authority_grab`（data 空）；`fly_to_point`{fly_to_id, max_speed 0–15, points[] 仅 1 点}；`fly_to_point_stop`（data 空）；`fly_to_point_update`{max_speed 1–15, points[]}；`payload_authority_grab`{payload_index}；`poi_circle_speed_set`{circle_speed(float，负数顺时针)}。`points[]` 元素 {lat, lon, height(float 2–10000，椭球高 WGS84)}。

**对照结论**：**method 全量覆盖 ✓**（EventMethod 8 项全在；ServiceMethod `drc_mode_enter`/`fly_to_point`/`fly_to_point_stop`/`fly_to_point_update`/`flight_authority_grab`/`payload_authority_grab`/`poi_circle_speed_set` 全在）。

**POJO 结构已存在且对齐 ✓**（抽查确认）：`PathPoint`（planned_path_points/points 共享）、`FlyToPointProgressData`、`TakeoffToPointProgressData`、`ObstacleAvoidanceNotifyData`（含 `isFinalReport` + `Obstacle{waylineId, waypointIndex}`）、`PoiCircleStatusData`（circleRadius/circleSpeed/maxCircleSpeed）、`CameraPhotoTakeProgressData`（**嵌套** output/progress/ext，非平铺 —— 更正 §8-A5 的疑点）、`SpeakerOutput`（psdkIndex/stepKey）、`DrcModeEnterRequest` + `DrcMqttBroker`（enableTls/expireTime）、`FlyToPointRequest`/`FlyToPointUpdateRequest`/`FlyToPointTarget`/`TakeoffToPointRequest`。

**待实现时逐一核对的枚举常量完整性**（文档给值域，SDK 可能仅为 int/String 无枚举）：
`joystick_invalid_notify.reason`(0–4)、`obstacle.type`(0–2)、`poi_status.reason`(0–8)、`poi_status.status`、`camera_photo.progress.current_step`(3000/3002/3005)、`camera_photo.ext.camera_mode`(3)、`speaker.progress.step_key`。

**文档自相矛盾**（记录待反馈官方）：`speaker_tts_play_start_progress` example 的 `status` 值为 `"success"`，但同页 `status` 枚举只定义 `{in_progress, ok}`。

### A10. O4 DRC/Payload services + `query_method_data`（2026-10-14 提供）— `ground-station/o4/drc.html`

| method | 通道 | 结构要点 | SDK |
|---|---|---|---|
| `poi_mode_enter` | services | {latitude(double), longitude(double), height(float 2–10000 椭球高)} | `ServiceMethod.POI_MODE_ENTER` ✓ |
| `poi_mode_exit` | services | data 空 | `POI_MODE_EXIT` ✓ |
| `takeoff_to_point` | services | target_latitude/longitude/height(float ≤1500), security_takeoff_height(20–1500), rth_mode(int{0智能高度,1设定高度}), rth_altitude(2–1500), rc_lost_action{0悬停,1着陆,2返航}, commander_mode_lost_action{0,1}, commander_flight_mode{0,1}, commander_flight_height(float ≤3000), flight_id, max_speed(1–15), simulate_mission{is_enable{0,1}, latitude, longitude}, flight_safety_advance_check(bool), exit_wayline_when_rc_lost{0,1} | `TAKEOFF_TO_POINT` ✓ + `TakeoffToPointRequest`（含 securityTakeoffHeight/commanderFlightHeight/simulateMission）+ `SimulateMission` 共享 ✓ |
| `video_storage_set` | services | {payload_index, `video_storage_settings` 数组 enum_string{current, wide, zoom, ir}} | `VIDEO_STORAGE_SET` ✓ + `VideoStorageSetRequest(List<String>)` ✓ |
| `camera_aim` | services | {payload_index, `camera_type`enum_string{ir, wide, zoom}, locked(bool), x/y(double 0–1)} | `CAMERA_AIM` ✓（另见 DrcMethod 同名） |
| `camera_exposure_mode_set` | services | {payload_index, camera_type{wide, zoom}, `exposure_mode`int{1自动,2快门优先,3光圈优先,4手动}} | `CAMERA_EXPOSURE_MODE_SET` ✓ |
| `camera_exposure_set` | services | {payload_index, camera_type{wide, zoom}, `exposure_value`（类型标 **enum_string**，值 1–31 / 255=FIXED）} | `CAMERA_EXPOSURE_SET` ✓ + `CameraExposureSetRequest(String)` ✓（String 承载与 enum_string 一致；文档标 numeric 值是文档笔误） |
| **`query_method_data`** | **requests** | 请求 { **`"drone sn"`（字段名含空格！）**, track_id }；回复 { flight_id, `fly_to_task`{id, status, task_seq(int)} } | ❌ **完全缺失** |

**查询任务语义**：设备收到飞行器广播的 fly_to 进度但未匹配到本地 task id 时，**主动向云平台查询**一次 —— 故 `requests` 方向 up（设备→云）、`requests_reply` 方向 **down**（云→设备）。

**需新增**：
1. `RequestsMethod.QUERY_METHOD_DATA`
2. `QueryMethodDataRequest{ String droneSn（**必须用 `@JsonProperty("drone sn")`** 显式映射，因 SNAKE_CASE 无法产生含空格键名）, String trackId }`
3. `QueryMethodDataReply{ String flightId, FlyToTask flyToTask }`，`FlyToTask{ id, status, taskSeq }`

**⚠ 语义待确认**：`query_method_data` 的 reply 由**云端下发**（Direction: down），与 SDK 其余 `requests` 类（reply 为设备回包、方向 up）**相反**。实现时须确认该 Reply 的归属与方向语义，避免与既有 `requests` POJO 约定冲突。

**待逐一核对**：`TakeoffToPointRequest` 是否含全部 15 字段；`rth_mode`/`rc_lost_action`/`commander_*`/`exit_wayline_when_rc_lost`/`flight_safety_advance_check` 的枚举常量是否齐全。

### A11. O4 相机服务族（2026-10-14 提供）— `ground-station/o4/drc.html`

| method | 请求 air | SDK |
|---|---|---|
| `camera_focal_length_set` | {payload_index, `camera_type`{ir,wide,zoom}, `zoom_factor`double(2–200，红外 2–20)} | `CameraFocalLengthSetRequest(Double zoomFactor)` ✓ |
| `camera_focus_mode_set` | {payload_index, camera_type{wide,zoom}, `focus_mode`int{0MF,1AFS,2AFC}} | `CameraFocusModeSetRequest(Integer focusMode)` ✓ |
| `camera_focus_value_set` | {payload_index, camera_type{wide,zoom}, `focus_value`int（范围见 zoom_max/min_focus_value）} | `CameraFocusValueSetRequest` ✓ |
| `camera_frame_zoom` | {payload_index, camera_type{ir,wide,zoom}, locked(bool), x/y/width/height float(0–1)} | `CameraFrameZoomRequest` ✓ |
| `camera_look_at` | {payload_index, locked(bool), latitude, longitude, height(float 椭球高)} | `CameraLookAtRequest` ✓ |
| `camera_mode_switch` | {payload_index, `camera_mode`int{0拍照,1录像,2智能低光,3全景拍照}} | `CameraModeSwitchRequest` ✓ |
| `camera_photo_stop` | {payload_index} | `CameraPhotoStopRequest` ✓ |
| `camera_photo_take` | {payload_index}；**reply 含 `output.status`enum_string{in_progress}** | `CameraPhotoTakeRequest` ✓ / **Reply ❌** |

**结论**：8 个 method 与 Request POJO **全部已存在** ✓。

- **唯一缺口（需新增）**：`camera_photo_take` 的 **reply** 文档定义为 `{result, output{status}}`（`status=in_progress` 表示后续会有持续性进度事件，见 `camera_photo_take_progress`），但 SDK 对该指令使用通用 `NoOutputReply`，**丢失 `output.status`** → 需新增专属 `CameraPhotoTakeReply{ Integer result, Output output }` + `Output{ String status }`，并把 `CameraPhotoTakeRequest` 的 Javadoc「仅返回 result=0，无 output 字段」更正。
- **共性提示**：`command/service/camera/` 下**每一个** Request 的 Javadoc 都写有「Reply 使用 NoOutputReply（services_reply 仅返回 result=0，无 output 字段）」——该表述对 `camera_photo_take` 为**错误**，实现时需仅修正此一处（其余相机指令文档确实无 output）。

### A12. O4 相机/云台服务续（2026-10-14 提供）— `ground-station/o4/drc.html`

| method | 请求 | SDK |
|---|---|---|
| `camera_point_focus_action` | {payload_index, camera_type{wide,zoom}, x(double 0–1), y(double 0–1)} | `CameraPointFocusActionRequest` ✓ |
| `camera_recording_start` | {payload_index} | `CameraRecordingStartRequest` ✓ |
| `camera_recording_stop` | {payload_index} | `CameraRecordingStopRequest` ✓ |
| `camera_screen_drag` | {payload_index, locked(bool), pitch_speed(double °/s), yaw_speed(double °/s，仅不锁机头生效)} | `CameraScreenDragRequest` ✓ |
| `camera_screen_split` | {payload_index, enable(bool)} | `CameraScreenSplitRequest` ✓ |
| `gimbal_reset` | {payload_index, `reset_mode`int{0回中,1向下,2偏航回中,3俯仰向下}} | `GimbalResetRequest` ✓ |

**结论：全部一致 ✓ 无需新增/改动**。6 项 reply 文档均为 `{result}`（无 output），与 SDK 通用 `NoOutputReply` 一致 ✓。

**待实现时核对**：`gimbal_reset.reset_mode`(0–3) 枚举常量是否齐全。

### A13. O4 照片存储 / 红外测温 / 喊话器 services（2026-10-14 提供）— `ground-station/o4/drc.html`

| method | 请求 | SDK |
|---|---|---|
| `photo_storage_set` | {payload_index, `photo_storage_settings` 数组 enum_string{current, vision, ir}} | `ServiceMethod.PHOTO_STORAGE_SET` ✓ + `PhotoStorageSetRequest` ✓ |
| `ir_metering_area_set` | {payload_index, x, y, width, height（double 0–1）} | `IR_METERING_AREA_SET` ✓ + `IrMeteringAreaSetRequest` ✓ |
| `ir_metering_mode_set` | {payload_index, `mode`int{0关闭测温,1点测温,2区域测温}} | `IR_METERING_MODE_SET` ✓ + `IrMeteringModeSetRequest` ✓ |
| `ir_metering_point_set` | {payload_index, x, y（double 0–1）} | `IR_METERING_POINT_SET` ✓ + `IrMeteringPointSetRequest` ✓ |
| `speaker_audio_play_start` | {psdk_index, `file`{name, **url**, md5, `format`enum_string{pcm}}} | `SPEAKER_AUDIO_PLAY_START` ✓ + `service/psdk/SpeakerAudioPlayStartRequest` ✓ |
| `speaker_play_mode_set` | {psdk_index(0–3), `play_mode`int{0单次播放,1循环播放(单曲)}} | `SPEAKER_PLAY_MODE_SET` ✓ + `SpeakerPlayModeSetRequest` ✓ |
| `speaker_play_stop` | {psdk_index} | `SPEAKER_PLAY_STOP` ✓ + `SpeakerPlayStopRequest` ✓ |
| `speaker_play_volume_set` | {psdk_index(0–3), play_volume(0–100)} | `SPEAKER_PLAY_VOLUME_SET` ✓ + `SpeakerPlayVolumeSetRequest` ✓ |
| `speaker_replay` | {psdk_index} | `SPEAKER_REPLAY` ✓ + `SpeakerReplayRequest` ✓ |
| `speaker_tts_play_start` | {psdk_index, `tts`{name, text, md5}} | `SPEAKER_TTS_PLAY_START` ✓ + `SpeakerTtsPlayStartRequest` ✓ |

**结论：全部一致 ✓ 无需新增/改动**。10 项 reply 均为 `{result}`，与通用 `NoOutputReply` 一致 ✓。POJO 归置：喊话器 6 项位于 `command/service/psdk/`，红外 3 项位于 `command/service/camera/`。

**再次确认 A8 缺口**：`command/drc/speaker/` 下有 `DrcSpeakerPlayModeSetRequest`/`DrcSpeakerTtsSetRequest`/`DrcSpeakerPlayVolumeSetRequest`/`DrcSpeakerPlayStopRequest`/`DrcSpeakerReplayRequest` **5 个**，**无 `DrcSpeakerTtsPlayStartRequest`** → `drc_speaker_tts_play_start` 确为真实缺口。

**待实现时核对**：`speaker_audio_play_start.file` 嵌套 record 的字段名与 `url`/`format` 是否齐全；`ir_metering_mode_set.mode`(0–2)、`speaker_play_mode_set.play_mode`(0–1) 枚举常量是否齐全。

### A14. O4 DRC 上行推送（`drc/up`）（2026-10-14 提供）— `ground-station/o4/drc.html`

| method | SDK |
|---|---|
| `drc_psdk_floating_window_text` | `DRC_PSDK_FLOATING_WINDOW_TEXT` ✓ + `PsdkFloatingWindowTextData` ✓ |
| `drc_speaker_play_progress` | `DRC_SPEAKER_PLAY_PROGRESS` ✓ + `SpeakerPlayProgressData` ✓ |
| `drc_psdk_state_info` | `DRC_PSDK_STATE_INFO` ✓ + `PsdkStateInfoData` ✓ |
| `drc_psdk_ui_resource` | `DRC_PSDK_UI_RESOURCE` ✓ + `PsdkUiResourceData` ✓ |
| `drc_drone_state_push` | `DRC_DRONE_STATE_PUSH` ✓ + `DroneStatePushData` ✓ |
| `drc_camera_state_push` | `DRC_CAMERA_STATE_PUSH` ✓ + `CameraStatePushData` ✓ |
| `drc_camera_osd_info_push` | `DRC_CAMERA_OSD_INFO_PUSH` ✓ + `CameraOsdInfoPushData` ✓ |
| `drc_ai_info_push` | `DRC_AI_INFO_PUSH` ✓ |
| `hsi_info_push` | `HSI_INFO_PUSH` ✓ + `HsiInfoPushData` ✓ |

**结论：9 项 method 全部已存在 ✓**。`CameraStatePushData` 已含 **v1.18 关注字段**：`mediaStorage`（photo/video storage settings）、`nightModeSettings`、`photoFormat(Integer，v1.16.1 新增，null=旧固件未推送)` ✓。

**⚠ 文档类型与示例矛盾（实现时须处置，避免反序列化失败）**：

1. `drc_camera_state_push.camera_state.interval_photo_interval` 文档类型 **int**，示例值为 **2.5**（小数）→ SDK 现为 `int`，遇 2.5 会反序列化失败，建议改 `Double`。
2. `drc_camera_state_push.camera_state.photo_size` 文档类型 **enum_int**{0=4K, 4=8K}，SDK 现为 `String` → 类型不符，建议改 `Integer`。
3. `drc_camera_osd_info_push.zoom_lense.zoom_factor` 文档类型 **int**，示例值 **6.999994214380596**（小数）→ 建议按 `Double`。
4. `drc_camera_state_push.camera_state.video_resolution` 文档类型 **enum_string** 但枚举值是数字串 `"19201080"`/`"38402160"` → SDK 用 `String` 承载，与 enum_string 一致（保持）。

**⚠ 文档自身不一致（待反馈官方）**：`drc_ai_info_push` 的 Data 表**未列出** `ai_wayline_state`{ sequence_shot, wait_control, record, normal_shot, count_down_time, alert_uuid }，但 Example **包含**了该字段 → 需确认是否应纳入 SDK。

**新增字段/结构（v1.18，需逐字段核对 SDK 覆盖度）**：
- `drc_camera_state_push`：`camera_state.night_mode_settings`{night_mode, denoise_level, night_vision_enable, infrared_fill_light_enable, night_scene_mode_suggestion, is_working}；`media_storage`{photo_storage_settings[], video_storage_settings[]}
- `drc_ai_info_push`：`ai_spotlight_zoom`{state(0–3), state_reason(0–15 + 160–168)}、`ai_model_list[]`{index, signed_name}、`selected_ai_model`{index, score, score_mode, **`image_source`/`digital_effect` 为 enum_list 数组**, filters[], labels[]{index, name}}
- `drc_camera_osd_info_push`：`wide_lense`{5}、`zoom_lense`{15}、`measure_target`{5}、`ir_lense`{12 + thermal_supported_palette_styles[]}、`liveview.liveview_world_region{left,top,right,bottom}`
- `hsi_info_push`：18 个 bool + up/down_distance(int) + `around_distances[]`（**size 360**，值 60000=无障碍；上报 4 个值表示 TOF 数据——红外失效如夜间场景）
- `drc_speaker_play_progress`：{psdk_index, result, status{failed,in_progress,**success**}, progress{**step_key 含 `tts_processing`**, percent 1–100}, md5}

**待实现时核对**：`PsdkStateInfoData`（psdk_type/psdk_name/psdk_sn/psdk_version/psdk_lib_version + speaker{10}/light{8}/values{2}）、`CameraOsdInfoPushData`、AI 相关 POJO 的字段完整性与 `image_source`/`digital_effect` 的数组类型表达。

---

### A15. DRC 上行推送（`delay_info_push` / `osd_info_push`）+ DRC 下行指令 7 项（2026-10-14 提供）— `dock-drc` / `remote-control`

**一、DRC 上行推送（`drc/up`，2 项）**

| method | SDK |
|---|---|
| `delay_info_push` | `DrcUpMethod.DELAY_INFO_PUSH` ✓ + `DelayInfoPushData` ✓ |
| `osd_info_push` | `DrcUpMethod.OSD_INFO_PUSH` ✓ + `OsdInfoPushData` ✓ |

- **`delay_info_push`**：`sdr_cmd_delay`(int, ms) + `liveview_delay_list[]{video_id, liveview_delay_time(int, ms)}` → SDK `DelayInfoPushData{sdrCmdDelay(int), liveviewDelayList(List<LiveviewDelay{videoId, liveviewDelayTime(int)})}` **完全对齐 ✓**；示例 `video_id` 格式 `<drone_sn>/<payload_index>/<stream_type>-<index>` 与 SDK Javadoc 一致 ✓。
- **`osd_info_push`**：10 个 double（attitude_head/latitude/longitude/height/speed_x/y/z/gimbal_pitch/roll/yaw）→ SDK `OsdInfoPushData` 10 个 **double** 字段名逐一对应 **完全对齐 ✓**。
  - ⚠ **Javadoc 语义待核**：SDK `OsdInfoPushData.height` 注释写「高度（**相对起飞点**，米）」，文档 Description 写「飞行器**海拔高度**」→ 二者口径不一致（用户偏好「相对起飞点」，但需以协议为准），列为待确认项。

**二、DRC 下行指令（`drc/down`，7 项）+ 上行回包**

| method | down data | SDK down | up 回包（文档） | SDK up |
|---|---|---|---|---|
| `drone_control`（**已废弃**） | `{seq, x, y, h, w}` | `DrcMethod.DRONE_CONTROL` ✓ + `DroneControlRequest` ✓ | `{result, output{seq}}`（失败时） | ❌ 无 Reply |
| `drone_emergency_stop` | null (`{}`) | `NoParameterDrcRequest` ✓ | `{result}` | `DrcResultReply` ✓ |
| `heart_beat` | `seq`【废弃】+ `timestamp`(int, ms) | `HeartBeatRequest{timestamp}` ✓ | `{timestamp}` | `HeartBeatReply{timestamp}` ✓ |
| `stick_control` | `{roll, pitch, throttle, yaw}`（int [364,1684]） | `DrcMethod.STICK_CONTROL` ✓ + `StickControlRequest` ✓（4×Integer） | 无回包 | 无 Reply ✓ |
| `drc_emergency_landing` | null (`{}`) | `NoParameterDrcRequest` ✓ | `{result}` | `DrcResultReply` ✓ |
| `drc_force_landing` | null (`{}`) | `NoParameterDrcRequest` ✓ | `{result}` | `DrcResultReply` ✓ |
| `drc_psdk_input_box_text_set` | `{psdk_index, value}`（text ≤128） | ❌ **缺失** | `{result}` | — |

**结论：6/7 已覆盖，`drc_psdk_input_box_text_set` 缺失。**

**缺口 1（method + POJO）— `drc_psdk_input_box_text_set`（DRC 通道）完全缺失**
- SDK 仅有 **services 通道** 的 `ServiceMethod.PSDK_INPUT_BOX_TEXT_SET("psdk_input_box_text_set")` + `command/service/psdk/PsdkInputBoxTextSetRequest`；**无 DRC 通道** method 与 POJO（load 于 `command/drc/`）。
- 需新增：`DrcMethod.DRC_PSDK_INPUT_BOX_TEXT_SET("drc_psdk_input_box_text_set")`、`command/drc/psdk/DrcPsdkInputBoxTextSetRequest{psdkIndex(int), value(String, ≤128)}`，up 回包复用 `DrcResultReply`（`{result}`）。
- **与 A8 缺口 `drc_speaker_tts_play_start` 同性质**：DRC 通道 method 于 SDK 缺失（SDK 只登记了同名 services 通道 method）。→ 触发一次系统性复核：**凡文档同时存在于 services 与 DRC 两通道的同名指令，SDK 是否仅登记了 services 侧**。

**缺口 2（废弃标注）— `drc_method.DRONE_CONTROL` 未标 `@Deprecated`**
- 文档标题明确「**DRC-飞行控制（已废弃）**」+「注意：**本协议已不再维护**，使用『DRC-杆量控制』可获得更好的响应性能」→ 满足 AGENTS.md「DJI 文档**明确声明**废弃才标记废弃」的条件。
- SDK 现状：`command/drc/flight/DroneControlRequest` Javadoc 已述「已废弃 — Dock1 有效，Dock2/Dock3 已废弃」，但 `DrcMethod.DRONE_CONTROL` 常量**未标** `@Deprecated` → 需补标注（并核实是否需在 `DrcMethod` 类级 Javadoc 的「三 Dock 共有 4 个」描述中调整为 3 个）。

**缺口 3（Reply POJO）— `drone_control` 失败回包 `output{seq}` 未建模**
- 文档 up Data = `result`(int) + `output{seq}`(int)；示例 `{ "result": 319033, "output": { "seq": -1 } }`（非 0 错误：无飞行控制权/无虚拟摇杆权限/序号不对）。
- SDK 现状：`DroneControlRequest` Javadoc 写「**无回包机制**：成功不回复，仅异常时回包 `result=非0`。**本指令无 Reply record**」→ 与文档 up 表矛盾（文档明确给出 up Data 结构）。
- 需处置：新增 `DroneControlReply{result(Integer), output(Output{seq(Integer)})}`（或复用一个含 `output{seq}` 的通用 DRC 失败回包），并修正 `DroneControlRequest` Javadoc 的「无回包机制」表述。
- ⚠ 属**已废弃**指令，是否建模失败回包请开发者决策（见待确认）。

**三、SDK 既有约定确认（重要）**
- DRC 上行回包（控制类）**不在 `DrcUpMethod` 登记**：`DrcUpMethod` 只登记 12 个**推送类** method；控制类回包由「`DrcMethod` 的 method 名 + `DrcResultReply`/`HeartBeatReply` 的 data」表达（见 `DrcResultReply` Javadoc 列出的 15 个指令）。本次文档的 `drone_emergency_stop`/`drc_emergency_landing`/`drc_force_landing` up `{result}` 均落在此约定内 ✓，约定成立。

**四、`@Inferred` 可升级项**
- `DrcMethod.HEART_BEAT` 与 `HeartBeatRequest`/`HeartBeatReply` 现标 `@Inferred(reason="heart_beat 协议格式与发起方未在 DJI 文档明确")`；本次 O4 文档已**明确**给出 `heart_beat` 的 Topic/Direction/Method/Data（`seq`【废弃】+ `timestamp` ms，up 回显 `timestamp`）→ 可**由 `@Inferred` 升级为 `@Verified`**（依据本次 O4 remote-control 文档）。同时文档标注 `seq` 为**【废弃】** → SDK 不建模 `seq` 的处理正确 ✓。

**五、文档自身不一致（待反馈官方 / 待确认）**
1. `stick_control` 的 Data 表仅列 `roll`/`pitch`/`throttle`/`yaw` 4 通道，但 Example **多出 `gimbal_pitch`**（表外字段）→ SDK 现按表建模 4 字段 ✓；`gimbal_pitch` 是否为遗漏字段待确认。
2. `stick_control` 的 `pitch` 行中出现 Markdown 残留 `**`（`杆量范围[1024±660]**`）→ 文档排版问题，不影响取值域。
3. `drc_psdk_input_box_text_set` 的 `value` 文档约束 `{"length":"128"}` 未注明单位（字符/字节）→ 实现按「字符 ≤128」处理，待确认。

**待实现时核对**：`DelayInfoPushData`/`OsdInfoPushData` 字段类型（int vs double）与文档逐一比对（本批已比对通过）；`LiveviewDelay.videoId` 命名格式。

---

### A16. DRC 指令 10 项（PSDK 控件值 / 初始状态订阅 / 夜航灯 / 隐蔽模式 / 视频分辨率 / 视频存储 / AIM / 光圈 / 降噪 / 去畸变）（2026-10-14 提供）— `remote-control`

全部为 **DRC `drc/down`** 指令，回包统一为 `drc/up` 的 `{result}`。

| method | 文档 down data | SDK method 枚举 | SDK Request POJO | 判定 |
|---|---|---|---|---|
| `drc_psdk_widget_value_set` | `{psdk_index, index, value}`（int） | ❌ | ❌ | **完全缺失** |
| `drc_initial_state_subscribe` | `{}`（null） | ✓ `DRC_INITIAL_STATE_SUBSCRIBE` | ❌ | POJO 缺失（可复用 `NoParameterDrcRequest`） |
| `drc_night_lights_state_set` | `{night_lights_state}` enum_int{0,1} | ✓ `DRC_NIGHT_LIGHTS_STATE_SET` | ❌ | POJO 缺失 |
| `drc_stealth_state_set` | `{stealth_state}` enum_int{0,1} | ✓ `DRC_STEALTH_STATE_SET` | ❌ | POJO 缺失 |
| `drc_video_resolution_set` | `{payload_index, video_resolution}` enum_string | ✓ `DRC_VIDEO_RESOLUTION_SET`（现 `@Inferred`） | ❌ | POJO + 枚举缺失 |
| `drc_video_storage_set` | `{payload_index, video_storage_settings[]}` | ❌ | ❌ | **完全缺失** |
| `drc_camera_aim` | `{payload_index, camera_type, locked, x, y}` | ❌ | ❌ | **完全缺失** |
| `drc_camera_aperture_value_set` | `{payload_index, camera_type, aperture_value}` enum_int | ✓ `DRC_CAMERA_APERTURE_VALUE_SET`（现 `@Inferred`） | ❌ | POJO + 枚举缺失 |
| `drc_camera_denoise_level_set` | `{payload_index, level}` enum_int{2,3} | ✓ `DRC_CAMERA_DENOISE_LEVEL_SET` | ✓ `DrcCameraDenoiseLevelSetRequest` | **已覆盖 ✓** |
| `drc_camera_dewarping_set` | `{payload_index, camera_type(wide), dewarping_state}` | ✓ `DRC_CAMERA_DEWARPING_SET`（现 `@Inferred`） | ❌ | POJO + 枚举缺失 |

**结论：10 项中仅 1 项（`drc_camera_denoise_level_set`）完整覆盖 ✓；9 项需处置。**

**缺口 1（method + POJO 完全缺失，3 项）**
- `drc_psdk_widget_value_set`：需 `DrcMethod.DRC_PSDK_WIDGET_VALUE_SET` + `command/drc/psdk/DrcPsdkWidgetValueSetRequest{psdkIndex(int), index(int), value(int)}`（文档说明开关/进度等控件值由开发者自行定义）。
- `drc_video_storage_set`：需 `DrcMethod.DRC_VIDEO_STORAGE_SET` + `command/drc/camera/DrcVideoStorageSetRequest{payloadIndex(String), videoStorageSettings(List<String>{current,vision,ir})}`。
  - **与 A15/A8 缺口同性质**：services 通道已有 `ServiceMethod.VIDEO_STORAGE_SET` + `VideoStorageSetRequest`，但 **DRC 通道（`drc_video_storage_set`）未登记**。
- `drc_camera_aim`：需 `DrcMethod.DRC_CAMERA_AIM` + `command/drc/camera/DrcCameraAimRequest{payloadIndex, cameraType, locked(Boolean), x(Double), y(Double)}`（结构与 services 的 `CameraAimRequest` 一致，可考虑共用/镜像）。
  - **同性质**：services 已有 `ServiceMethod.CAMERA_AIM` + `CameraAimRequest`，DRC 通道未登记。

**缺口 2（method 枚举已登记、Request POJO 缺失，5 项）**
> 这是一个**结构性问题**：`DrcMethod` 在「DRC 状态与相机参数控制（11 个）」段登记了 method 名，但**只有 4 个镜头发光/夜景类 POJO**，以下 5 项**无对应 Request POJO**（全库 grep 类名零命中）：
- `drc_initial_state_subscribe` → 复用 `NoParameterDrcRequest`（data 为空）即可，无需新文件。
- `drc_night_lights_state_set` → 新增 `DrcNightLightsStateSetRequest{nightLightsState(Integer, 0/1)}`。
- `drc_stealth_state_set` → 新增 `DrcStealthStateSetRequest{stealthState(Integer, 0/1)}`。
- `drc_video_resolution_set` → 新增 `DrcVideoResolutionSetRequest{payloadIndex(String), videoResolution(...)}`（见缺口 3 枚举）。
- `drc_camera_aperture_value_set` → 新增 `DrcCameraApertureValueSetRequest{payloadIndex(String), cameraType(...), apertureValue(...)}`（见缺口 3 枚举）。
- `drc_camera_dewarping_set` → 新增 `DrcCameraDewarpingSetRequest{payloadIndex(String), cameraType(...), dewarpingState(Integer, 0/1)}`。

**缺口 3（新增枚举，4 个）**
- `VideoResolution{HD_1080P("0","19201080"), UHD_4K("1","38402160")}`（文档 Type 为 **enum_string**，值为**数字串**；⚠ 见文档矛盾 1）。
- `PhotoResolution`（**已在 §3.3 记录**，`drc_camera_photo_size_set` 用，与本批无重叠）。
- `CameraType{WIDE("wide"), ZOOM("zoom"), IR("ir")}`（doc：`drc_camera_aim` 支持 ir/wide/zoom；`drc_camera_aperture_value_set` 支持 wide/zoom；`drc_camera_dewarping_set` 仅 wide）。SDK 现无该枚举（services `CameraAimRequest.cameraType` 现为裸 `String`）→ 是否引入枚举统一表达 **待确认**（会影响 services 侧既有 POJO）。
- `ApertureValue{...}`（枚举值域：`0=F_AUTO, 50=F0.5, 70=F0.7 … 25600=F256`，共 60+ 项，见文档原文）。**与 A14 的 `drc_camera_osd_info_push.wide_aperture_value` 值域完全一致** → 可**共享**同一枚举（A14 该字段现为 `int`）。
- `DewarpingState{OFF(0), ON(1)}`（enum_int{0,1}）。
- `StealthState{NONE(0), ON(1)}` / `NightLightsState{OFF(0), ON(1)}` 视是否引入枚举统一表达（与 `mode_code`/`camera_state` 等既有 int 字段风格一致性 **待确认**）。

**缺口 4（`@Inferred` 可升级，3 项）**
- `DRC_CAMERA_DEWARPING_SET` / `DRC_CAMERA_APERTURE_VALUE_SET` / `DRC_VIDEO_RESOLUTION_SET` 现标 `@Inferred(reason="未确认 Services 通道是否也存在同名方法")`。
- 本次文档在 **DRC 通道**明确给出三者完整 Data 与回包 → DRC 侧定义成立；而此前收集的 services 相机族文档（A11–A13）**未出现** `camera_dewarping_set`/`camera_aperture_value_set`/`video_resolution_set` → 可**由 `@Inferred` 升级为 `@Verified`**（依据：本批 DRC 文档 + services 文档未见同名 method）。
  - ⚠ 但「services 通道是否存在」的**完全证明**依赖 services 全量文档，本批仅给出 DRC 侧 → 建议升级为 `@Verified(basis="DRC 通道文档确认（remote-control）")`，同时保留一句「services 侧未见同名 method（基于已收集文档）」。

**文档自身矛盾 / 待确认（3 点）**
1. **`drc_video_resolution_set.video_resolution`**：Data 表 Type 写 **enum_string**（`"0":"19201080"`/`"1":"38402160"`），但 Example 写 `"video_resolution": 0`（**数字，非字符串**）→ 与 A14 `drc_camera_state_push.video_resolution` 同样问题。需确认承载类型：`String`（与 enum_string 一致）或 `Integer`（与示例一致）。
2. **`drc_camera_aperture_value_set.aperture_value`**：Data 表 enum_int 值域从 `0`(F_AUTO)/`50`(F0_5) 起，但 Example 写 `"aperture_value": 5` → `5` **不在**枚举值域内（0 与 50 之间）→ 示例与枚举不一致，待反馈官方。
3. **`drc_initial_state_subscribe`**：Data 表写 `null`（列 `null/null/double`），Example 写 `"data": {}` → 空对象表达一致，可按 `NoParameterDrcRequest` 处理 ✓。

**待实现时核对**：`DrcCameraDenoiseLevelSetRequest` 已含 `payloadIndex + level` ✓（Javadoc 枚举 0=关闭/1=标准/2=增强(15fps)/3=超强(5fps)，文档本批只列 2/3，与 Javadoc 的 0/1 来自 `camera_state.night_mode_settings.denoise_level` 一致，兼容 ✓）。

---

### A17. DRC 相机控制 8 项（曝光模式/曝光值/焦距/对焦模式/对焦值/框选变焦/ISO/Look At）（2026-10-14 提供）— `remote-control`

全部为 **DRC `drc/down`** 指令，回包统一为 `drc/up` 的 `{result}`。

| method | 文档 down data | SDK `DrcMethod` | SDK Request POJO | services 侧对应 | 判定 |
|---|---|---|---|---|---|
| `drc_camera_exposure_mode_set` | `{payload_index, camera_type, exposure_mode}` | ❌ | ❌ | ✓ `CAMERA_EXPOSURE_MODE_SET` + POJO | **DRC 侧完全缺失** |
| `drc_camera_exposure_set` | `{payload_index, camera_type, exposure_value}` | ❌ | ❌ | ✓ `CAMERA_EXPOSURE_SET` + POJO | **DRC 侧完全缺失** |
| `drc_camera_focal_length_set` | `{payload_index, camera_type, zoom_factor(double 2–200)}` | ❌ | ❌ | ✓ `CAMERA_FOCAL_LENGTH_SET` + POJO | **DRC 侧完全缺失** |
| `drc_camera_focus_mode_set` | `{payload_index, camera_type, focus_mode}` enum_int{0,1,2} | ❌ | ❌ | ✓ `CAMERA_FOCUS_MODE_SET` + POJO | **DRC 侧完全缺失** |
| `drc_camera_focus_value_set` | `{payload_index, camera_type, focus_value(int)}` | ❌ | ❌ | ✓ `CAMERA_FOCUS_VALUE_SET` + POJO | **DRC 侧完全缺失** |
| `drc_camera_frame_zoom` | `{payload_index, camera_type, locked, x, y, width, height}` | ❌ | ❌ | ✓ `CAMERA_FRAME_ZOOM` + POJO | **DRC 侧完全缺失** |
| `drc_camera_iso_set` | `{payload_index, camera_type, iso_value}` enum_int | ✓ `DRC_CAMERA_ISO_SET`（现 `@Inferred`） | ❌ | ❌（services 无 `camera_iso_set`） | **POJO 缺失** |
| `drc_camera_look_at` | `{payload_index, locked, latitude, longitude, height(float m)}` | ❌ | ❌ | ✓ `CAMERA_LOOK_AT` + POJO | **DRC 侧完全缺失** |

**结论：8 项全部需处置（7 项 DRC 侧 method+POJO 完全缺失，1 项仅有 method 枚举、POJO 缺失）。**

**核心发现 — 「services 有、DRC 侧无」模式已确认为系统性问题（累计 11 例）**
- A8 `drc_speaker_tts_play_start`、A15 `drc_psdk_input_box_text_set`、A16 `drc_video_storage_set`/`drc_camera_aim`，本批 **7 例**（exposure_mode/exposure/focal_length/focus_mode/focus_value/frame_zoom/look_at）。
- 规律：DJI 文档在 **services 与 DRC 两通道**都定义了同名（去 `drc_` 前缀后同名）相机控制指令，SDK 只登记了 **services 侧**（`ServiceMethod` + POJO），**DRC 侧（`drc/down`）缺失**。
- → **建议（待你确认）**：对全部 `camera_*` / `video_*` 类 services 指令做一次 DRC 侧全量复核，一次性补齐，而非逐批打补丁。

**缺口 1（新增 `DrcMethod` 枚举 + POJO，7 项）**
- 需新增 7 个 DRC method 常量：`DRC_CAMERA_EXPOSURE_MODE_SET` / `DRC_CAMERA_EXPOSURE_SET` / `DRC_CAMERA_FOCAL_LENGTH_SET` / `DRC_CAMERA_FOCUS_MODE_SET` / `DRC_CAMERA_FOCUS_VALUE_SET` / `DRC_CAMERA_FRAME_ZOOM` / `DRC_CAMERA_LOOK_AT`。
- 需新增 7 个 Request POJO（建议 `command/drc/camera/`）：`DrcCameraExposureModeSetRequest{payloadIndex, cameraType, exposureMode}`、`DrcCameraExposureSetRequest{payloadIndex, cameraType, exposureValue}`、`DrcCameraFocalLengthSetRequest{payloadIndex, cameraType, zoomFactor(Double)}`、`DrcCameraFocusModeSetRequest{payloadIndex, cameraType, focusMode}`、`DrcCameraFocusValueSetRequest{payloadIndex, cameraType, focusValue}`、`DrcCameraFrameZoomRequest{payloadIndex, cameraType, locked, x, y, width, height}`、`DrcCameraLookAtRequest{payloadIndex, locked, latitude, longitude, height(Float)}`。
- 回包统一复用 `DrcResultReply`。
- **结构与 services 侧 POJO 完全一致**（可镜像 `CameraExposureModeSetRequest` 等）→ 实现时可直接对照既有 services POJO 建字段，避免二次推导。

**缺口 2（`drc_camera_iso_set` POJO 缺失，1 项）**
- `DrcMethod.DRC_CAMERA_ISO_SET` 已存在（现 `@Inferred`），但**无 Request POJO**（全库 grep 零命中）。
- 需新增 `command/drc/camera/DrcCameraIsoSetRequest{payloadIndex(String), cameraType(...), isoValue(...)}`；回包 `DrcResultReply`。
- 注意：services 侧**无** `camera_iso_set`（与上表 7 项不同）→ ISO 为 DRC 独有，`@Inferred` 中「Services 通道是否存在」可据本批文档**否定**（services 相机族文档 A11–A13 未出现 ISO）→ 可升级 `@Verified`。

**缺口 3（枚举，4 个）**
- `CameraType`（**A16 已记**，本批 5 处复用：exposure_mode/exposure/focal_length/focus_mode/focus_value/frame_zoom/iso）→ 优先级提升，建议统一引入。
- `ExposureMode{AUTO(1), SHUTTER_PRIORITY(2), APERTURE_PRIORITY(3), MANUAL(4)}`——**与 A14 `drc_camera_osd_info_push.wide_exposure_mode` 值域完全一致** → 可共享。
- `ExposureValue`（文档 **enum_string**，值 `1:"-5.0EV" … 31:"5.0EV", 255:"FIXED"`）——**与 A14 `wide_exposure_value` enum_int 值域一致**（A14 标 enum_int）→ 两处类型标注矛盾（见文档矛盾 1）。
- `FocusMode{MF(0), AFS(1), AFC(2)}`。
- `IsoValue`（见文档矛盾 2）。

**文档自身矛盾 / 待确认（3 点）**
1. **`drc_camera_exposure_set.exposure_value`**：Data 表 Type 标 **enum_string**，但枚举键与值均为**数字**（`"1":"-5.0EV"`），Example 写 `"exposure_value": 8`（数字）→ 与 A14 `wide_exposure_value` 标 enum_int 冲突。**同一组值域在两份文档中类型标注不同**，需统一（建议 `Integer`）。
2. **ISO 值域两份文档不一致**：本批 `iso_value` = `{0:ISO_AUTO, 2:ISO_50, 3:ISO_100, 4:ISO_200, 5:ISO_400, 6:ISO_800, 7:ISO_1600, 8:ISO_3200, 9:ISO_6400, 10:ISO_12800, 11:ISO_25600, 12:ISO_51200, 13:ISO_102400, 255:ISO_FIXED}`；A14 `drc_camera_osd_info_push.wide_iso` = `{0:Auto, 1:Auto(High Sense), 2:50, … 11:25600, 255:FIXED}`。二者 **index 2–11 映射一致 ✓**，但本批**无 index 1** 且**新增 12/13（51200/102400）** → 是否需要为「Auto(High Sense)」保留 index 1 待确认。
3. **`drc_camera_frame_zoom.locked`** 文档 constraint 写成 `{"0":"仅云台转，机身不转","1":"锁定机头…"}`（以 0/1 描述 bool），与 `drc_camera_aim.locked` 的 `{"false":…,"true":…}` 写法不一致 → 仅描述风格差异，语义一致（bool），按 `Boolean` 承载 ✓。

**待实现时核对**：`drc_camera_look_at` 无 `camera_type` 字段（与其余 7 项不同，文档确认）✓；`drc_camera_focal_length_set.zoom_factor` 为 `double`（A14 `zoom_lense.zoom_factor` 文档标 int 但示例小数——见 A14 矛盾 3，与本批 double 一致，支持 A14 改 `Double`）。

### A18. DRC 相机控制 20 项（机械快门 / 模式切换 / 夜景 / 夜视 / 照片格式 / 拍照·停止 / 点对焦 / 录像启停 / 画面拖动 / 分屏 / 快门 / 云台复位 / 定时拍 / 照片存储 / 近红外补光 / 红外测温×3）（2026-10-14 提供）— `remote-control`

全部为 **DRC `drc/down`** 指令，回包统一为 `drc/up` 的 `{result}`（唯一例外：`drc_camera_photo_take` 回包另含 `status`，见矛盾 2）。

| method | 文档 down data | SDK `DrcMethod` | SDK DRC POJO | services 侧 method | services POJO | 判定 |
|---|---|---|---|---|---|---|
| `drc_camera_mechanical_shutter_set` | `{payload_index, camera_type(wide), mechanical_shutter_state(0/1)}` | ✓（现 `@Inferred`） | ❌ | ❌ | ❌ | **仅缺 POJO** |
| `drc_camera_mode_switch` | `{payload_index, camera_mode(0–4)}` | ❌ | ❌ | ✓ `CAMERA_MODE_SWITCH` | ✓ | **DRC 侧缺失** |
| `drc_camera_night_mode_set` | `{payload_index, mode(0/1/2)}` | ✓ | ✓ | — | — | 已覆盖 ✓ |
| `drc_camera_night_vision_enable` | `{payload_index, enable(bool)}` | ✓ | ✓ | — | — | 已覆盖 ✓ |
| `drc_camera_photo_format_set` | `{payload_index, photo_format(7/16)}` | ❌ | ❌ | ❌ | ❌ | **两通道均缺（§5 项 10）** |
| `drc_camera_photo_stop` | `{payload_index}` | ❌ | ❌ | ✓ `CAMERA_PHOTO_STOP` | ✓ | **DRC 侧缺失** |
| `drc_camera_photo_take` | `{payload_index}` | ❌ | ❌ | ✓ `CAMERA_PHOTO_TAKE` | ✓ | **DRC 侧缺失**（回包多 `status`） |
| `drc_camera_point_focus_action` | `{payload_index, camera_type, x, y}` | ❌ | ❌ | ✓ `CAMERA_POINT_FOCUS_ACTION` | ✓ | **DRC 侧缺失** |
| `drc_camera_recording_start` | `{payload_index}` | ❌ | ❌ | ✓ `CAMERA_RECORDING_START` | ✓ | **DRC 侧缺失** |
| `drc_camera_recording_stop` | `{payload_index}` | ❌ | ❌ | ✓ `CAMERA_RECORDING_STOP` | ✓ | **DRC 侧缺失** |
| `drc_camera_screen_drag` | `{payload_index, locked, pitch_speed, yaw_speed}` | ❌ | ❌ | ✓ `CAMERA_SCREEN_DRAG` | ✓ | **DRC 侧缺失** |
| `drc_camera_screen_split` | `{payload_index, enable(bool)}` | ❌ | ❌ | ✓ `CAMERA_SCREEN_SPLIT` | ✓ | **DRC 侧缺失** |
| `drc_camera_shutter_set` | `{payload_index, camera_type, shutter_value(0–60)}` | ✓（现 `@Inferred`） | ❌ | ❌ | ❌ | **仅缺 POJO** |
| `drc_gimbal_reset` | `{payload_index, reset_mode(0–3)}` | ❌ | ❌ | ✓ `GIMBAL_RESET` | ✓ `GimbalResetRequest` | **DRC 侧缺失** |
| `drc_interval_photo_set` | `{payload_index, interval(enum_string)}` | ✓（现 `@Inferred`） | ❌ | ❌ | ❌ | **仅缺 POJO** |
| `drc_photo_storage_set` | `{payload_index, photo_storage_settings[]}` | ❌ | ❌ | ✓ `PHOTO_STORAGE_SET` | ✓ | **DRC 侧缺失** |
| `drc_infrared_fill_light_enable` | `{payload_index, enable(bool)}` | ✓ | ✓ | — | — | 已覆盖 ✓ |
| `drc_ir_metering_area_set` | `{payload_index, x, y, width, height}` | ❌ | ❌ | ✓ `IR_METERING_AREA_SET` | ✓ | **DRC 侧缺失** |
| `drc_ir_metering_mode_set` | `{payload_index, mode(0/1/2)}` | ❌ | ❌ | ✓ `IR_METERING_MODE_SET` | ✓ | **DRC 侧缺失** |
| `drc_ir_metering_point_set` | `{payload_index, x, y}` | ❌ | ❌ | ✓ `IR_METERING_POINT_SET` | ✓ | **DRC 侧缺失** |

**结论统计（20 项）**：已覆盖 **3** 项（night_mode_set / night_vision_enable / infrared_fill_light_enable）；**仅缺 POJO 3** 项（mechanical_shutter_set / shutter_set / interval_photo_set，DRC 独有）；**DRC 侧完全缺失 13** 项；**两通道均缺失 1** 项（photo_format_set）。

**核心发现 — 系统性问题「services 有、DRC 侧无」累计升至 24 例**
- A8 1 例、A15 1 例、A16 2 例、A17 7 例，本批 **+13 例** = 24 例。
- 本批特征：**services 相机族（A11–A13）已全覆盖的指令，DRC 侧成批缺失**（mode_switch / photo_stop / photo_take / point_focus_action / recording_start / recording_stop / screen_drag / screen_split / gimbal_reset / photo_storage_set / ir_metering ×3）。
- 另有 **4 项 DRC 独有**（services 族文档未出现）：mechanical_shutter_set / shutter_set / interval_photo_set / photo_format_set。
- → **再次强化建议（待确认）**：对 `camera_*` / `photo_*` / `ir_metering_*` / `video_*` 全部 services 指令做一次 **DRC 侧全量复核**，一并补齐 24 例，而非逐批打补丁。

**缺口 1（新增 `DrcMethod` 枚举常量，14 个）**
`DRC_CAMERA_MODE_SWITCH` / `DRC_CAMERA_PHOTO_FORMAT_SET` / `DRC_CAMERA_PHOTO_STOP` / `DRC_CAMERA_PHOTO_TAKE` / `DRC_CAMERA_POINT_FOCUS_ACTION` / `DRC_CAMERA_RECORDING_START` / `DRC_CAMERA_RECORDING_STOP` / `DRC_CAMERA_SCREEN_DRAG` / `DRC_CAMERA_SCREEN_SPLIT` / `DRC_GIMBAL_RESET` / `DRC_PHOTO_STORAGE_SET` / `DRC_IR_METERING_AREA_SET` / `DRC_IR_METERING_MODE_SET` / `DRC_IR_METERING_POINT_SET`。

**缺口 2（新增 DRC Request POJO，17 个）— 建议置于 `command/drc/camera/`**
- 本批 3 项「仅缺 POJO」：`DrcCameraMechanicalShutterSetRequest{payloadIndex, cameraType, mechanicalShutterState}`、`DrcCameraShutterSetRequest{payloadIndex, cameraType, shutterValue}`、`DrcIntervalPhotoSetRequest{payloadIndex, interval}`。
- 13 项「DRC 侧缺失」（**结构与 services 侧 POJO 完全一致，可直接镜像**）：`DrcCameraModeSwitchRequest`、`DrcCameraPhotoStopRequest`、`DrcCameraPhotoTakeRequest`、`DrcCameraPointFocusActionRequest`、`DrcCameraRecordingStartRequest`、`DrcCameraRecordingStopRequest`、`DrcCameraScreenDragRequest`、`DrcCameraScreenSplitRequest`、`DrcGimbalResetRequest`、`DrcPhotoStorageSetRequest`、`DrcIrMeteringAreaSetRequest`、`DrcIrMeteringModeSetRequest`、`DrcIrMeteringPointSetRequest`。
- 1 项两通道均缺：`DrcCameraPhotoFormatSetRequest{payloadIndex, photoFormat}`（复用已有 `PhotoFormat` 枚举）。

**缺口 3（新增/扩展枚举）**
- **`CameraMode` 扩展**：文档 `camera_mode` = `{0:拍照,1:录像,2:智能低光,3:全景拍照,4:定时拍}`，SDK `CameraMode`（`telemetry/enumtype/`）现仅 **0–3**，**缺 `4=TIMED_PHOTO(定时拍)`** → 需新增常量（该枚举已被 `CameraModeSwitchRequest` / `CameraPhotoTakeProgressData` / `CameraPhotoInfoPushData` 引用，扩展向后兼容，不破坏既有行为）。
- `ShutterValue`（enum_int **0–60 共 61 值**，60=Auto；值域表见本批原文）。
- `IntervalPhoto`（enum_string，键混合：`1/2/3/5/7/10/15/20/30/60` 与 `"0.7"` → 见矛盾 4）。
- `IrMeteringMode{CLOSE(0), POINT(1), AREA(2)}`。
- `GimbalResetMode{RETURN_CENTER(0), DOWN(1), YAW_RETURN_CENTER(2), PITCH_DOWN(3)}`。
- `NightMode{CLOSE(0), OPEN(1), AUTO(2)}`（若为 `drc_camera_night_mode_set.mode` / `drc_ir_metering_mode_set.mode` 类型化，注意两处 mode 值域不同，不可共用）。
- `MechanicalShutterState`（enum_int 0/1）——与 A16 `DewarpingState`、A17 曝光等同属 0/1 开关型，**建议统一为一个通用开关枚举**（如 `SwitchState`）以避免枚举爆炸。
- `CameraType`（**A16 已记**，本批 3 处复用：mechanical_shutter_set（仅 wide）/ point_focus_action / shutter_set）。

**文档自身矛盾 / 待确认（4 点）**
1. **`drc_camera_mode_switch.camera_mode` 新增值 4=定时拍** → `CameraMode` 枚举必须扩展；同时 `camera_photo_take` 事件 `camera_mode` 未注明是否含 4，**扩展后是否影响 events 侧反序列化需确认**（现 `FromCode` 未知值抛 `IllegalArgumentException`）。
2. **`drc_camera_photo_take` 回包含 `status`**（enum_string `{"in_progress":"执行中"}`，用于全景等持续拍照场景，后续进度走 `camera_photo_take_progress` 事件）。services 侧 SDK 现用 `NoOutputReply`（无 status）→ **DRC 侧回包不能复用 `DrcResultReply`/`NoOutputReply`，需专用 `DrcCameraPhotoTakeReply{result, status}`**。待确认：`status` 是否仅在 `result=0` 且持续拍照时出现（包装类型 `String`）。
3. **`drc_camera_screen_split` 文档 up 示例错误**：up 段 Example 的 `data` 误写为 down 的 `{enable, payload_index}`（应为 `{result: 0}`）。→ 以 Data 表 `{result}` 为准建模。
4. **`drc_interval_photo_set.interval` 键类型混合**：constraint 列键为 `1,2,3,5,7,10,15,20,30,60,0.7`（JS 对象数字键 + `"0.7"` 小数键）→ 语义上是「间隔秒数」（0.7/1/2/3/5/7/10/15/20/30/60 共 11 档）。待确认：字段用 `Integer` 无法承载 0.7 → 建议 `String`/`Double` 承载，且 8K 照片不支持 0.7s/1s（机型约束不建模）。

**文档笔误（不影响建模）**
- `drc_gimbal_reset.payload_index` 的 constraint 列写作 `{{type-subtype-gimbalindex}_aembLbhPpc}`（模板占位符残留）→ 实为 `text`，格式 `{type-subtype-gimbalindex}`。
- `drc_camera_screen_drag.pitch_speed/yaw_speed` 无 min/max（仅单位 rad/s）；`yaw_speed` 仅「不锁机头」时生效（业务约束，不建模）。

**待实现时核对**：`drc_camera_photo_format_set.photo_format`（7=RJPEG / 16=DLT664）**与 SDK 已有 `PhotoFormat` 枚举值域完全一致** ✓ 可直接复用；`drc_camera_mechanical_shutter_set.camera_type` 仅 `{wide}` → `CameraType` 需支持单值场景；`drc_ir_metering_*` 三项 services 侧 POJO（`IrMeteringModeSetRequest` / `IrMeteringPointSetRequest` / `IrMeteringAreaSetRequest`）均已存在，可 1:1 镜像为 DRC 版。

### A19. 航线任务事件 4 项 + services 7 项 + requests 1 项（2026-10-14 提供）— `wayline`

覆盖 events（up）`flighttask_progress` / `flighttask_ready` / `device_exit_homing_notify` / `return_home_info`；services（down）`flighttask_prepare` / `flighttask_execute` / `flighttask_pause` / `flighttask_recovery` / `return_home` / `return_home_cancel` / `flighttask_undo`；requests（up，应答 down）`flighttask_resource_get`。

| method | 通道 | SDK 枚举 | SDK POJO | 判定 |
|---|---|---|---|---|
| `flighttask_progress` | events up | ✓ `FLIGHTTASK_PROGRESS`(need_reply=1) | ✓ `FlighttaskProgressData`（Output/Ext/BreakPoint/Progress） | 已覆盖 ✓ |
| `flighttask_ready` | events up | ✓ `FLIGHTTASK_READY`(0) | ✓ `FlighttaskReadyData` | 已覆盖 ✓ |
| `device_exit_homing_notify` | events up | ✓ `DEVICE_EXIT_HOMING_NOTIFY`(1) | ✓ `DeviceExitHomingNotifyData` | 已覆盖 ✓（`reason` 类型矛盾见下） |
| `return_home_info` | events up | ✓ `RETURN_HOME_INFO`(0) | ✓ `ReturnHomeInfoData`（PathPoint/MultiDockHomeInfo） | 已覆盖 ✓（可升 `@Verified`） |
| `flighttask_prepare` | services down | ✓ `FLIGHTTASK_PREPARE` | ✓ `FlighttaskPrepareRequest` + `FlighttaskPrepareReply` | 已覆盖 ✓ |
| `flighttask_execute` | services down | ✓ `FLIGHTTASK_EXECUTE` | ✓ `FlighttaskExecuteRequest` + `FlighttaskExecuteReply` | 已覆盖 ✓ |
| `flighttask_pause` | services down | ✓ `FLIGHTTASK_PAUSE` | ✓ `NoParameterRequest` + `NoOutputReply` | 已覆盖 ✓ |
| `flighttask_recovery` | services down | ✓ `FLIGHTTASK_RECOVERY` | ✓ `NoParameterRequest` + `NoOutputReply` | 已覆盖 ✓ |
| `return_home` | services down | ✓ `RETURN_HOME` | ⚠️ 回包含 `output.status`，**无对应 POJO** | **缺口：Reply POJO 缺失** |
| `return_home_cancel` | services down | ✓ `RETURN_HOME_CANCEL` | ✓ `NoParameterRequest` + `NoOutputReply` | 已覆盖 ✓ |
| `flighttask_undo` | services down | ✓ `FLIGHTTASK_UNDO` | ✓ `FlighttaskUndoRequest` + `NoOutputReply` | 已覆盖 ✓ |
| `flighttask_resource_get` | requests up / reply down | ✓ `FLIGHTTASK_RESOURCE_GET` | ✓ `FlighttaskResourceGetRequest` + `FlighttaskResourceGetReply` | 已覆盖 ✓ |

**结论：12 项中 11 项已覆盖，仅 `return_home` 回包存在 1 处缺口。**

**缺口 1（新增 `ReturnHomeReply`，1 个 POJO）**
- 文档 `return_home` 的 `services_reply` data = `{result, output{ status }}`，`status` 为 `enum_string` **8 值**（`canceled/failed/in_progress/ok/paused/rejected/sent/timeout`）。
- SDK 现仅有 [`NoOutputReply`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/NoOutputReply.java)（无 output）→ **`return_home` 回包无法用现有类型表达**。
- 建议新增 `command/service/wayline/ReturnHomeReply{Integer result, Output output}` + `Output{String status}`（或按 SDK 既有风格并入单 record）。**这是本批唯一新增文件。**
- 待确认：`status` 是否复用 `ServiceProgressData` 的 9 值枚举（见矛盾 2），此处仅 8 值（**无 `partially_done`**）。

**可升级项（本批原文已构成权威依据）**
- `ReturnHomeInfoData` 的 `home_dock_sn` / `multi_dock_home_info` 现标 `@Inferred`（"仅 Dock2/3 支持，Dock1 无此字段"）→ 本批原文给出完整字段表（`sn` / `plan_status{0–3}` / `estimated_battery_consumption` / `home_distance`）与示例（含 2 元素数组）→ **可升 `@Verified`**（仅"机型差异"保留描述）。
- `FlighttaskPrepareRequest.flight_safety_advance_check` / `exit_wayline_when_rc_lost` 已存在 ✓，与本批原文一致（`flight_safety_advance_check` 默认 0，可选）。
- `EventEnvelope.gateway` 字段已存在 ✓ → `return_home_info` 示例中的信封级 `gateway` 字段**已被覆盖**，无需改动。

**文档自身矛盾 / 待确认（3 点）**
1. **`device_exit_homing_notify.reason` 类型**：Data 表 Type 列标 `enum_int`，但 Example 为字符串 `"reason": "0"`。SDK 现按 `int` 承载并标 `@Inferred` → **本批原文再次确认该矛盾，`@Inferred` 维持**（不加分，待真机）。
2. **`status` 枚举值域不一致**：`flighttask_progress.status` 列 **9 值**（含 `partially_done`），`return_home` 的 `output.status` 列 **8 值**（无 `partially_done`）→ SDK `ServiceProgressData` Javadoc 现记 8 值（A1 已记录待补全）→ **补 Javadoc 时需区分两处值域**（进度类 9 值 / return_home 8 值）。
3. **`flighttask_execute` 本批 Data 表仅列 `flight_id`**，未含蛙跳字段（`multi_dock_task` / `wireless_link_topo` / `leaf` / `center` / `dock`）→ SDK 已按 Dock3 wayline 页实现，**本批为精简版原文，非缺口**（无需改动）。

**运营提示（非协议改动）**：requests 章节明确——平台若不应答 `requests_reply`，`flighttask_execute` / `takeoff_to_point` 仍返回成功，但设备会因超时中止任务（表现为"任务下发成功却不起飞"）；`flighttask_resource_get` 不应答时设备停在 `current_step=16`。SDK 为协议定义层，不含应答逻辑，仅需文档说明。

### A20. 健康告警事件 `hms`（1 项）（2026-10-14 提供）— events up

| method | 通道 | SDK 枚举 | SDK POJO | 判定 |
|---|---|---|---|---|
| `hms` | events up | ✓ `EventMethod.HMS`(need_reply=0) | ✓ [`HmsData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/alert/HmsData.java)（Item / Item.Args） | 已覆盖，**但 `Item.code` 类型错误** |

**缺口（1 处类型修正，非新增文件）**
- **`HmsData.Item.code` 现为 `Integer`，文档明确规定 Type = `text`（`{"length":"10240"}`），示例为 `"code": "0x16100083"`（十六进制字符串）** → **必须改为 `String`**。
  - 依据：`Integer` 无法反序列化 `"0x16100083"`（Jackson 抛 `JsonMappingException`）；`String` 可同时兼容数字型 code（Jackson 默认标量强转），是严格更安全的类型。
  - 影响面：`HmsData.Item.code()` 返回类型变更 → 若 simulator / hivemind 侧存在 `Integer` 依赖需同步调整；SDK 内部除本类外无其他引用。
  - 附带：`@Verified(basis="simulator HmsSimulator.publishHmsEvent L158-L176 ...")` 依据仅为 simulator，应补充 DJI 官方文档依据（`@DocUrl` 已指向 `dock3/hms.html`，即本批原文）。
- 其余字段核对**全部一致** ✓：`level`(0/1/2) / `module`(0–3) / `in_the_sky`(0/1) / `device_type`(text) / `imminent`(0/1) / `args{component_index, sensor_index}`。

**待确认（2 点）**
1. **`hms_inner` 未登记**：文档尾注——飞行器自身的 `hms_inner` 内部健康事件以飞行器 SN 上报（`thing/product/{drone_sn}/events`，method `hms_inner`），并指向"对应机型的飞行器上云 API"。SDK `EventMethod` 现无 `HMS_INNER` → **待确认是否补登**（涉及 SDK 对 Device-to-Cloud 飞行器侧的覆盖边界）。
2. `args.component_index / sensor_index` 文档 Type=`int` 但 constraint 标 `{"length":"10240"}`（疑为"用于填充 hms.json 文案变量"说明残留）→ 类型仍按 `int` 处理，**无需改动**。
3. `list` 文档 `{"size": 20}` 为数组上限，SDK 不做校验（符合"零业务逻辑"约束）。

### A21. 解禁证书 services 3 项（`unlock_license_list` / `unlock_license_switch` / `unlock_license_update`）（2026-10-14 提供）— services

| method | SDK 枚举 | SDK Request POJO | SDK Reply POJO | 判定 |
|---|---|---|---|---|
| `unlock_license_list` | ✓ `UNLOCK_LICENSE_LIST` | ✓ `UnlockLicenseListRequest{int deviceModelDomain}` | ❌ | **回复 POJO 缺失（缺口 1）** |
| `unlock_license_switch` | ✓ `UNLOCK_LICENSE_SWITCH` | ✓ `UnlockLicenseSwitchRequest{int licenseId, boolean enable}` | ❌ | **回复 POJO 缺失（缺口 2）** |
| `unlock_license_update` | ✓ `UNLOCK_LICENSE_UPDATE` | ✓ `UnlockLicenseUpdateRequest{LicenseFile{url, fingerprint}}` | —（仅 `{result}`） | 已覆盖 ✓ |

**3 个 Request POJO 字段与文档逐项核对一致 ✓**（`deviceModelDomain` 0=飞行器/3=机场；`license_id`+`enable`；`file{url, fingerprint}`）。

**缺口 1（`unlock_license_list` 回复，约 10 个 record 需新建）**
文档回复 data = `{result, device_model_domain, consistence, licenses[]}`，其中每项 = `common_fields` + **7 种解禁类型之一的子结构**。需新建：
- `UnlockLicenseListReply{Integer result, Integer deviceModelDomain, Boolean consistence, List<License> licenses}`
- `License{CommonFields commonFields, AreaUnlock areaUnlock, CircleUnlock circleUnlock, CountryUnlock countryUnlock, HeightUnlock heightUnlock, PolygonUnlock polygonUnlock, PowerUnlock powerUnlock, RidUnlock ridUnlock}`
- `CommonFields{Integer licenseId, String name, Integer type(0–6), Integer groupId, String userId, String deviceSn, Integer beginTime, Integer endTime, Boolean userOnly, Boolean enabled}`
- `AreaUnlock{List<Integer> areaIds}` / `CircleUnlock{Integer radius, Double latitude, Double longitude, Integer height}` / `CountryUnlock{Integer countryNumber, Integer height}` / `HeightUnlock{Integer height}` / `PolygonUnlock{List<PolygonPoint> points}` / `PolygonPoint{Double latitude, Double longitude}` / `PowerUnlock{}` / `RidUnlock{Integer level(1/2)}`
- `begin_time`/`end_time` 为**秒级** UNIX 时间戳（文档明确 "秒级UNIX时间戳"）。

**缺口 2（`unlock_license_switch` 回复）**
文档回复 data = `{result, license_id}`（**扁平，非 output 包裹**）→ 需新建 `UnlockLicenseSwitchReply{Integer result, Integer licenseId}`。

**缺口 3（架构问题，需决策）— `ReplyEnvelope` 无法承载本批回复**
- 现有 [`ReplyEnvelope`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/protocol/envelope/ReplyEnvelope.java) 的 `data` **强类型为 `ReplyData{int result, Object output}`** → 而 `unlock_license_list` / `unlock_license_switch` 的回复 data 是**扁平字段**（`{result, device_model_domain, consistence, licenses}` / `{result, license_id}`），**必须塞进 `output` 才能表达，与文档 JSON 形态不符**。
- **决策（2026-10-14）→ 方案 B**：**否决 (a) `ReplyEnvelope` 泛型化**，采纳 **(b) 专用信封**，命名为 **`FlatReplyEnvelope`**（`data` 为 `Object`、Reply 记录自带 `result`），与既有 `RequestReplyEnvelope`（requests 通道同形态，`data` 亦为 `Object`）风格一致。
  - 理由：① 零破坏性——`ReplyEnvelope`/`ReplyData`/`NoOutputReply`/examples/README/既有测试及**已发布 SDK 的外部调用方**均不受影响；② 泛型化会破坏已发布 API、并使 `fromJson(json, ReplyEnvelope.class)` 退化为 raw 类型（`data` 变 `LinkedHashMap`，须改用 `TypeReference`），且既有断言 `getRecordComponents()[4].getType()==ReplyData.class` 失效；③ 扁平 Reply 自带 `result`，序列化后与文档 JSON 逐字段一致，忠实性最佳（方案 E「塞进 output」因与文档形态不符而否决）。
  - **复审补充（2026-10-14，开发者要求重审泛型化后确认维持方案 B）**：泛型化另含 6 项硬约束 ——
    ① **双重泛型困境**：`ReplyData.output` 仍为 `Object` → 类型安全仅在扁平一侧成立（收益不对称）；若彻底泛型化为 `ReplyData<O>`，签名变 `ReplyEnvelope<ReplyData<O>>`，可读性与工厂推断崩塌；
    ② **`T` 是 union**：`{result, output}` 与扁平 `{result, ...}` 结构互不兼容，仍需按 method 手选类型，**未降低出错面**；
    ③ **工厂签名冲突**：`ok(..., Object output)` 无法推断 `T`（保留 `Object` 入参则收益归零；改 `<T> ok(..., T output)` 则破坏入参灵活性与 `null` 调用）；
    ④ **擦除退化**：`fromJson(json, ReplyEnvelope.class)` 得 raw 类型、`data` 变 `LinkedHashMap`，类型安全仅在显式 `TypeReference` 时成立；
    ⑤ **既有断言失效**：`ReplyEnvelopeTest`/`RequestReplyEnvelopeTest` 中 `getRecordComponents()[4].getType()==ReplyData.class` 必然失败，需重写；
    ⑥ **破坏已发布 API**（`1.16.1.3` 已发布，外部直接 `new ReplyEnvelope(...)`/`.class` 引用受影响）。
    → **最终维持方案 B**（`FlatReplyEnvelope`，零破坏、与 `RequestReplyEnvelope` 一致）。
  - 详见 §5 项 21 与 §4。

**缺口 4（`ReplyEnvelope` 缺 `gateway` 字段）**
- 本批 `unlock_license_list` 回复示例信封含 `"gateway": ""`（与 method/tid/timestamp 同级），而 [`EventEnvelope`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/protocol/envelope/EventEnvelope.java#L65) **已有 `gateway`**、`ReplyEnvelope` **没有** → 两信封不一致，需对齐（补 `gateway`，允许 null）。

**待确认（4 点）**
1. `power_unlock` 为**空 struct** `{}`（PowerUnlock 无字段）→ 建模为空 record（承载结构占位）还是省略字段？（建议空 record，保持 7 类型结构对称）
2. `area_unlock.area_ids` 为 `enum_int` 数组（区域编号）→ 值域文档未给出（示例 `115001769` / `8724`）→ 按 `Integer` 承载，**不引入枚举**。
3. `country_unlock.country_number` 为 ISO-3166-1 数字码（示例 156=中国）→ 按 `Integer`，**不引入枚举**（值域为国际标准，非协议枚举）。
4. `licenses` 数组 `size: -`（无上限）→ 不做校验。

### A22. FTS 飞行终止系统 services 2 项（`fts_config` / `drone_propeller_stop_v1`）（2026-10-14 提供）— services

| method | SDK `ServiceMethod` | SDK Request POJO | SDK Reply POJO | 判定 |
|---|---|---|---|---|
| `fts_config` | ❌ | ❌ | —（仅 `{result}`） | **两通道均无覆盖，需新增** |
| `drone_propeller_stop_v1` | ❌ | ❌ | —（仅 `{result}`） | **两通道均无覆盖，需新增** |

**SDK 现状核实**：全 SDK（含 `sdk-wayline`）对 `fts` / `propeller` / `fts_info` 的检索**零命中** → FTS 属**全新协议面**，非漏字段而是整块缺失。

**缺口 1（新增 2 个 `ServiceMethod` 常量，§4 已登记待确认）**
`FTS_CONFIG("fts_config", ...)`、`DRONE_PROPELLER_STOP_V1("drone_propeller_stop_v1", ...)`。

**缺口 2（新增 3 个 POJO，建议置于 `command/service/fts/`，并新建 `package-info.java`）**
- `FtsConfigRequest{Integer ftsServerType(0=DJI FTS/1=自定义), MqttBroker mqttBroker}`（`mqttBroker` 条件必填，允许 null）
- `FtsConfigRequest.MqttBroker{String address, String username, String password}`
- `DronePropellerStopV1Request{String ftsVerificationCode}`
- 两者回复均仅 `{result}` → 复用 `NoOutputReply` ✓（无需新 Reply 类型）。

**关于 §5-8（`fts_info`）的再澄清（2026-10-14，开发者质疑后修正）**
- 本页 Description 仅以「**取数来源**」口吻提及飞行器 OSD 的 `fts_info.fts_verification_code`（「从飞行器 OSD 的 `fts_info.fts_verification_code` 中获取的验证码，包含数字和大写字母，共 8 位」）→ 该路径出现在**说明文字**中，**不是** `fts_info` 的结构定义。
- 服务请求侧 `data` 中 `fts_verification_code` 为**顶层扁平字段**（示例 `{"fts_verification_code":"ABCD1234"}`）→ `DronePropellerStopV1Request{String ftsVerificationCode}` 可**确定建模，不受阻塞**（原 A22 记录措辞「`fts_info` 结构至少含该字段」属过度解读，已更正）。
- `fts_info` struct **从未在任何 DJI 官方页面被定义**（全站检索零命中）→ 飞行器 OSD 侧**已定案（2026-10-14）方案 B：暂不建模**（不新增 `FtsInfo`，不做单字段占位），不构成服务侧阻塞 → §5-8 **已降级为非阻塞项并结案**。

**待确认（3 点）**
1. `fts_server_type` 为 `enum_int {0, 1}` → 是否引入 `FtsServerType` 枚举（建议引入，值域明确且封闭；符合 SDK "枚举值域须与文档一致"的风格）。
2. `mqtt_broker` 的**条件必填**（`fts_server_type=1` 必填）→ 建议字段允许 null + Javadoc 说明条件必填，**不做运行时校验**（符合"零业务逻辑"约束）。
3. `password` 为敏感字段 → 协议层原样承载（不做脱敏，SDK 不实现日志/掩码逻辑）。

**运营提示（非协议改动）**：FTS 触发需飞行器接入 **4G 图传**（仅 SDR 链路无法触发）；验证码默认**每 120 分钟更新**，单架次内有效。SDK 为协议定义层，不承载该业务约束，建议在 Javadoc 注明。

### A23. 重复批次复核（`drc_video_resolution_set` / `drc_camera_photo_size_set`）（2026-10-14 提供）— DRC `drc/down`

**本批为重复批次**：两方法已在 §3.3（报告 L172–L177）与 **A16** 中核对完毕，本批原文与既有记录**完全一致，无新增缺口、无新增文件**。记录于此以防后续重复补录。

| method | 既有结论（A16 / §3.3） | 本批复核 |
|---|---|---|
| `drc_video_resolution_set` | SDK 有 `DRC_VIDEO_RESOLUTION_SET`（现 `@Inferred`）但**无 Request POJO**；`video_resolution` 枚举缺失 | 一致 ✓（原文再次确认） |
| `drc_camera_photo_size_set` | SDK **完全缺失**（无常量、无 POJO）；`photo_size` enum_int **{0:4K, 4:8K}** | 一致 ✓（值域得到原文确认） |

**对 §5 待确认项的影响**
- **§5-4 `video_resolution` 类型**：本批 Data 表仍标 `enum_string`（`"0":"19201080"`/`"1":"38402160"`）而 Example 仍为数字 `0` → **文档自身矛盾复现，无新增结论** → 维持按 `Integer` 承载 + `@Inferred`，待真机验证（与 A16 待确认 1 同一问题）。
- **§5-5 `drc_camera_state_push.photo_size` 语义**：本批 `drc_camera_photo_size_set` 原文给出 `photo_size enum_int {0:4K, 4:8K}`，与 §3.3 记录一致 → 支持"值域为分辨率档位"的判定；但**上行推送字段（`camera_state_push.photo_size`）类型仍未被本批原文覆盖**（本批仅下行设置指令），§5-5 **仍待真机**（SDK 现为 `String`）。

**机型归属澄清（2026-10-14 开发者补充）**
- 本批共收到**两组同定义原文**：**前一组（`drc_video_resolution_set` + `drc_camera_photo_size_set`）出自 Dock2 文档，后一组（`drc_camera_photo_size_set` + `drc_video_resolution_set`）出自 Dock3 文档**。
- **关键结论：Dock2 与 Dock3 文档对这两个方法的字段定义完全一致**（字段名、类型、`enum_int`/`enum_string` 标注、值域 `{0:19201080, 1:38402160}` 与 `{0:4K, 4:8K}`、示例、回复 `{result}` 均无差异）。
- → **SDK 建模无需按机型分叉**：单一 `DrcVideoResolutionSetRequest` / `DrcCameraPhotoSizeSetRequest` + 统一枚举即可同时覆盖 Dock2/Dock3，**不存在机型差异型 `@Inferred`**。
- → 附带更正：`DrcMethod` 现有 Javadoc 把本组方法（`DRC_VIDEO_RESOLUTION_SET` 等）置于"DRC 状态与相机参数控制（11 个）"段但未标机型，实现时应标注为 **"Dock2/Dock3 共有"**，避免后续误判为 Dock3 独有而按机型分叉。

### A24. `drc_camera_state_push` 相机状态推送（DRC up）（2026-10-14 提供）— DRC `drc/up`

| 项 | SDK 枚举 | SDK POJO | 判定 |
|---|---|---|---|
| `drc_camera_state_push` | ✓ `DrcUpMethod.DRC_CAMERA_STATE_PUSH` | ✓ [`CameraStatePushData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/up/CameraStatePushData.java)（CameraState / NightModeSettings / MediaStorage） | 已覆盖 ✓，**但有 1 处类型需修正、2 处 Javadoc 需修正** |

**字段级核对（文档 → SDK）**

| 文档字段 | 文档类型 | SDK 现状 | 结论 |
|---|---|---|---|
| `camera_mode`（0–4，含 **4=定时拍照**） | enum_int | `int cameraMode` | 类型可容纳 ✓，但 **Javadoc 表述错误**（见下） |
| `interval_photo_interval`（示例 **2.5**） | int（示例小数） | **`int`** | ⚠️ **类型风险（缺口 A）** |
| `video_resolution`（示例 `0`） | enum_string | `String` | 与 Type 一致、与示例矛盾 → 维持 `@Inferred`（§5-4） |
| `linkage_zoom_state`（示例 `0`） | bool（示例数字） | `int` | 文档 bool/数字矛盾 → `int` 可兼容（Jackson 标量强转）✓，标注 `@Inferred` |
| `photo_size`（0=4K/4=8K） | **enum_int** | **`String`** | ⚠️ **类型不符，应改 `Integer`（缺口 B，本批可结案 §5-5）** |
| `record_time` / `remain_photo_num` / `remain_record_duration` | int | int ✓ | 一致 ✓ |
| `recording_state` / `photo_state` | enum_int 0/1 | int ✓ | 一致 ✓ |
| `payload_index` / `media_storage.{photo,video}_storage_settings[]` | text / text 数组 | `String` / `List<String>` ✓ | 一致 ✓ |

**缺口 A（`interval_photo_interval` 小数被截断）**
文档 Type 标 `int` 但**示例为 `2.5`（小数秒）** → SDK 现为 `int`，**反序列化会丢失小数（0.5s 间隔语义丢失）**，且与 **A18 `drc_interval_photo_set.interval` 含 `"0.7"` 档**同源 → 建议统一改为 `Double`，并标 `@Inferred`（文档 int 与示例小数冲突）。**两个 interval 字段应一并处理。**

**缺口 B（`photo_size` 类型修正，§5-5 结案）**
文档明确 `photo_size` 为 **`enum_int {0:4K, 4:8K}`** 且示例为数字 `0` → SDK 现为 `String` **不符**，应改 **`Integer`**（并配 `PhotoResolution` 枚举，§3.3 已登记）。本批原文已构成权威依据，**§5-5 可结案**。

**Javadoc 修正（2 处，AGENTS 硬约束「注释与代码一致」）**
1. `CameraState.cameraMode` Javadoc 现写 **"2=拍照+录像"**，而文档为 **2=智能低光 / 3=全景拍照 / 4=定时拍照** → **表述错误，需修正**（并与 A18 `CameraMode` 扩展 `TIMED_PHOTO(4)` 对齐）。
2. `CameraState.photoFormat` Javadoc 存在**字面 `\n` 转义残留**（`；null 表示旧固件未推送）`n     *   @see ...`）→ 需修正格式。

**SDK 字段比本批文档更全（非缺口，需注记）**
- SDK 已有 **`nightModeSettings`**（Dock3 特有）与 **`photoFormat`**（v1.16.1 新增，7=RJPEG/16=DLT664）两字段，而**本批文档字段表未列出** → 本批为**精简/旧版原文**，**不得据本批原文删字段**（与 A16–A18 同类判定）。

**文档笔误（不影响建模）**：值域文本 `"19201080 "` 带**尾随空格**（应为 `1920*1080`）；`linkage_zoom_state` 的 bool 约束用 `{"0":"关闭","1":"开启"}`（数字键）描述布尔，与示例 `0` 一致但与 Type `bool` 冲突。

**待确认（本批新增）**
- **`drc_camera_photo_size_set`（命令）是否存在于官方文档**：开发者反馈"当前文档没有找到 `drc_camera_photo_size_set`"，而本批 `drc_camera_state_push` 文档中 `photo_size` **仅作为推送字段**出现、**未出现该设置命令** → 与 A23 所贴原文（含该命令）存在冲突 → **暂按"待确认"处理：不新建 `DrcCameraPhotoSizeSetRequest`，待文档确认后收录**（见 §5-26）。

### A25. `drc_video_resolution_set` / `drc_camera_photo_size_set` 复核（含 `drc/up` 回包）（2026-10-14 提供）— DRC `drc/down` + `drc/up`

**本批性质**：A23 两方法的**第三次原文**，且**首次包含 `drc/up` 上行回包段**（A23 仅含下行 Data 表）→ 回包结构为新增信息，其余字段与 A16/A23 **完全一致**。

| method | SDK 枚举 | SDK POJO | 判定 |
|---|---|---|---|
| `drc_video_resolution_set` | ✓ [`DrcMethod.DRC_VIDEO_RESOLUTION_SET`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/protocol/method/DrcMethod.java#L254-L262)（现 `@Inferred`） | ✗ `DrcVideoResolutionSetRequest` 未实现 | DRC 通道**存在性由原文确认** → 升 `@Verified`；补 Request POJO + `VideoResolution` 枚举 |
| `drc_camera_photo_size_set` | ✗ `DRC_CAMERA_PHOTO_SIZE_SET` 缺失 | ✗ `DrcCameraPhotoSizeSetRequest` 未实现 | **完全缺失，需新增**（原文权威确认该指令存在） |

**关键结论：§5-26 结案 —— `drc_camera_photo_size_set` 命令确实存在**
- A24 时因开发者反馈"文档中找不到该指令"而暂停收录；**本批原文（含完整 `drc/down` Data 表 + Example）已构成权威依据** → **恢复收录**：`DrcMethod` 新增 `DRC_CAMERA_PHOTO_SIZE_SET`，新建 `DrcCameraPhotoSizeSetRequest`（§4 L192 / L210 原计划条目**现在具备依据**）。
- 字段：`payload_index`（text，相机枚举 `{type-subtype-gimbalindex}`）+ `photo_size`（**enum_int {0:4K, 4:8K}**，配 `PhotoResolution{R4K(0), R8K(4)}`，§3.3 已登记）。

**两指令上行回包均为 `{result}`（新增信息）**
- `drc_video_resolution_set` 与 `drc_camera_photo_size_set` 的 `drc/up` data 均仅含 `result`（int，非 0 代表错误）→ **无需新增 Reply POJO**，复用 [`DrcResultReply`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/DrcResultReply.java)（现 `Integer result`，已适配）。
- 注：`DrcResultReply` Javadoc 列的"适用指令 15 个"清单需在实现时**补入这两项**（实际应再复核其余相机类 DRC 指令回包，见 A15–A18 系统性缺口）。

**§5-4 `video_resolution` 类型矛盾（第三次复现）**
- Data 表仍标 `enum_string {0:19201080, 1:38402160}`，Example 仍为**数字 `0`** → 文档自身矛盾第三次复现，**无新增结论** → 维持按 `Integer` 承载 + `@Inferred`（配 `VideoResolution{FHD_1080P(0), UHD_4K(1)}`），待真机验证。
- 补充约束原文：**红外相机仅支持 640*512，无法设置**（建模时写入 Javadoc，不做运行时校验——零业务逻辑）。

**`DRC_VIDEO_RESOLUTION_SET` 的 `@Inferred` 处置**
- 现有 `@Inferred(reason="未确认是否在 Services 通道是否存在 video_resolution_set")` 针对的是"Services 侧同名方法"，**本批原文仅确认 DRC 侧存在** → DRC 通道存在性可升 `@Verified`；Services 侧同名问题**仍不涉及本批**，按 §5 既有口径保留待确认（若确认 services 侧无同名指令，则删除该 `@Inferred`；若存在则另立 `ServiceMethod` 常量）。

**文档笔误（不影响建模）**：`payload_index` 两组示例分别为 `81-0-0`（视频分辨率）/ `80-0-0`（照片分辨率），仅示例差异。

---

### A26. O4 地面站 设备属性（osd / state 推送 + property/set_reply）（2026-10-14 提供）— O4 地面站 `device.html`

**归属澄清（§5-1 结案依据）**：`electric_supply_voltage` 在 **dock1/2/3 文档中均无**，仅出现在 **O4 地面站设备属性** 表 → Type=`double`，单位 **伏 / V**。

**通道与信封**
- `pushMode=0` → 定频推送，Topic `thing/product/{device_sn}/osd`；`pushMode=1` → 变化时推送，Topic `thing/product/{device_sn}/state`
- 两类上行示例信封**均含 `gateway`** → 佐证 §5-22（方案 A：`ReplyEnvelope`/`FlatReplyEnvelope` 补 `gateway`）
- **`update_topo` 的 Topic 为 `sys/product/{gateway_sn}/status`**（`sys/` 前缀，非 `thing/`）→ 实现时须与现有 `StatusMethod`/`UpdateTopoData` 的路由口径核对
- 子设备（飞行器）连接/配对状态经 `thing/product/{gateway_sn}/state` 的 `sub_device` 上报

**覆盖判定（实测 SDK 检索）**：全 SDK **无** `GroundStation` / `Basestation` / `O4` 命名 → **O4 地面站在 SDK 中零覆盖**（无型号枚举、无 OSD/State POJO、无外设 POJO）。仅 `command/service/wayline/WirelessLinkTopo.java` 同名，需逐字段核对。

**字段级要点（按 `osd` / `state` 分组）**

| 通道 | 字段 | Type/值域 |
|---|---|---|
| osd | `mode_code` | enum_int {0:空闲中,1:现场调试,2:远程调试,3:固件升级中,4:作业中,5:待标定,6:低功耗,9:日志上报中} |
| osd | `low_power_switch_state` | enum_int {0:关闭,1:打开} |
| osd | `acc_time` | int，秒 / s |
| osd | `longitude` / `latitude` / `height` | double（度 / 米） |
| osd | `electric_supply_power` | int，瓦 / W |
| osd | **`electric_supply_voltage`** | **double，伏 / V**（§5-1） |
| osd | `power_mode` | enum_int {0:电池供电,1:PD 供电,2:POE 供电}（与 state 侧 `electric_supply_mode` 同义） |
| osd | `battery_voltage` | int，毫伏 / mV |
| osd | `position_state` | struct{is_calibration(0/1), is_fixed(0:未开始/1:收敛中/2:收敛成功/3:收敛失败), quality(1~5), gps_number, rtk_number} |
| osd | `battery` | struct{capacity_percent, voltage(mV), current(mA), temperature(double ℃)} |
| osd | `self_converge_coordinate` | struct{longitude, latitude, height} |
| osd | `tilt_angle` | struct{value(double °), valid(int 0/1)} |
| osd | `basestation_work_mode` | enum_int {0:漫游基站,1:外置机场（连接机场）,2:简易机场} |
| osd | `under_work_mode` | enum_int {0:非棚下部署,1:棚下部署} |
| osd | `alternate_land_point` | struct{latitude, longitude, height, safe_land_height(int), is_configured(0/1)} |
| osd | **`network_state`** | struct{**type** enum_int **{1:4G, 2:以太网}**, quality {0:差,1:中,2:好}（仅 4G）, rate int KB/秒}（§5-9） |
| osd | `lan_status` | enum_int {0:未连接,1:速率过低,2:100M,3:1000M,4:不支持} |
| osd | `poe_status` | enum_int {0:无 PoE,1:PoE 功率过低,2:25.5W,3:51W,4:71W} |
| osd | `storage` | struct{total(int KB), used(int KB)} |
| state | `electric_supply_mode` | enum_int {0:电池供电,1:PD 供电,2:POE 供电} |
| state | `is_beidou_version` | bool |
| state | `home_position_is_valid` | enum_int {0:无效,1:有效} |
| state | **`heading`** | **double（度 / °）**，文档明确：**该字段已废弃，不建议依赖其取值**（§5-2） |
| state | `rtcm_info` | struct{mount_point(text), port(text), host(text), rtcm_device_type {0:遥控器,1:机场,2:中继}, source_type {0:未标定,1:自收敛标定,2:手动标定,3:网络 RTK 标定}} |
| state | `wireless_link_topo` | struct{secret_code int[28], center_node{sdr_id,sn,model,device_type}, leaf_nodes[]{sdr_id, control_source_index(1=A 槽/2=B 槽), sn, model, device_type}} |
| state | **`peripherals_info`** | array[]{device_sn(text), name(text), port{0:LAN 口,1:WAN 口}, type{0:其它,1:相机,2:DAA,3:雷达,4:电源,5:第三方机库,6:边缘计算设备,7:RID}, ip_addr(text), need_auth(bool), main_device(bool, 可选)} |
| state | **`peripheral_list`** | array[]{sn(text), rid{supported(bool), version(text)}, adsb{supported(bool), version(text)}, dock_control{supported(bool), version(text)}（第三方机库时上报）} |
| state | `sub_device` | struct{device_sn, **device_model_key**(`domain-type-sub_type`，如 `0-130-0`，离线可能为空串), device_online_status(0/1), device_paired(0/1)}（§5-3 归属佐证：`acc_time` 属 osd、`sub_device` 属 state） |

**关联说明**：外设采集的空中目标数据及外设自身电量/供电状态随《空域安全》的 `rid_infos` / `adsb_infos` 事件上报（与 A9 结论一致）；基站（网关）模式下 O4 还会**透传所连飞行器负载属性**（±速度/云台角度等），负载属性结构体详见「机场 3 设备属性」的负载 example。

**`property/set_reply` 形态（新增待确认）**
```json
{"bid":"...","data":{"distance_limit_status":{"state":{"result":0}}},"tid":"...","timestamp":1643268212187}
```
- `property/set` 的 `{gateway_sn}` 为地面站自身 SN，**O4 自身 osd/state 全部只读**，`property/set` 仅用于**向所连飞行器透传可写属性**
- 其 `data` 既非 `{result, output}` 也非扁平字段，而是「**按属性名嵌套、叶子为 `{result}`**」→ 与 §5-21 方案 B（`FlatReplyEnvelope`）**不冲突**，但**需确认是否有第三个信封形态**（见 §5-29）

**新增缺口（已同步 §4）**：① O4 地面站型号枚举；② `GroundStationOsd` / `GroundStationState` POJO（含约 15 个子结构）；③ 外设 POJO（`PeripheralInfo` / `PeripheralCapability` / `RidCapability` / `AdsbCapability` / `DockControlCapability`）；④ O4 枚举族（`GroundStationModeCode`、`SupplyPowerMode`、`FixedState`、`CalibrationState`、`NetworkType`（值域核对）、`BasestationWorkMode`、`UnderWorkMode`、`RtcmDeviceType`、`RtcmSourceType`、`PeripheralPort`、`PeripheralType`、`AlternateLandPointConfigured` 等）；⑤ `OsdField` / `StateField` 新增 O4 常量。

---

### A27. 机场 3（Dock3）设备属性页 —— `heading` 更正 + 跨页值域差异（2026-10-14 提供）

**来源**：机场 3 设备属性页（页面日期 2026-10-13），开发者本地原文 `...\workspaceStorage\...\mv74389s-hxf0\设备属性__2026....txt`。该页为 **Dock3 设备属性全量页**，`pushMode` 定义：`0` = 定频推送 0.5Hz（Topic `thing/product/{device_sn}/osd`）、`1` = 变化上报（Topic `.../state`）；`accessMode`：`r` 只读、`rw` 可读写。

#### A27-1 `heading` —— 更正 A26「仅 O4 地面站」的结论（§5-2）

`heading` **两处并存**，字段类型同为 `double`，但值域 / pushMode / 废弃声明不同：

| 归属设备 | 字段 | 类型 | 值域 | 单位 | accessMode | pushMode | 废弃声明 |
|---|---|---|---|---|---|---|---|
| **Dock3 机场** | `heading` | **double** | **max 180 / min -180** | 度 / ° | r | **0（osd）** | **未声明废弃** |
| O4 地面站（A26） | `heading` | double | 0~360 | 度 / ° | - | **1（state）** | **明确已废弃** |

**SDK 影响（Dock 侧）**：
- `DockOsd.heading`：`Integer` → **`Double`**（原注释「0-359」有误）
- `OsdField.HEADING` 注释：`机场朝向角（0-359，Dock2/Dock3 only）` → 修正为 **`-180~180`**
- **Dock 侧不得标注 `@Deprecated`**：Dock3 页未声明废弃（AGENTS.md 硬约束：仅在文档明确声明废弃时才标注）；O4 侧建模时才按废弃字段处理

#### A27-2 `home_position_is_valid` 值域跨页差异（§5-31）

| 归属设备 | 类型 | 值域 |
|---|---|---|
| **Dock3 机场** | enum_int | `{0:航向和经纬度坐标都无效, 1:都有效, 2:航向有效/经纬度无效, 3:经纬度有效/航向无效}`（**4 值**） |
| O4 地面站（A26） | enum_int | `{0, 1}`（**2 值**） |

**SDK 现状**：`OsdField.HOME_POSITION_IS_VALID` 注释「（0=无效，1=有效，Dock2/Dock3 only）」—— 注释仅覆盖 2 值，**与 Dock3 页的 4 值不符**；`DockOsd.homePositionIsValid` 为 `Integer` 可承载，但注释需按 Dock3 页修正为 4 值语义。

**2026-10-14 补核（A31）→ 悬置假设已证实**：Dock2 页（页名「设备属性列表」）该字段为 **2 值** `{0:无效, 1:有效}`，Dock3 页为 **4 值** → **机场家族内部亦存在机型差异**（非仅 Dock 与 O4 的跨族差异）。语义上 Dock2 的 `0/1` 与 Dock3 的 `0/1` 等价，Dock3 额外区分 `2/3` → 建模方案见 **§5-38（已决策：方案 A 三枚举）**。

**决策（2026-10-14，A31 补核后定稿）→ 按设备分别建模（三枚举，§5-31 / §5-38 / §5-36）**：
- **Dock2**：**新建** `Dock2HomePositionIsValid`（2 值 `{0:无效, 1:有效}`，`@DocUrl` Dock2 页）。
- **Dock3**：现 `HomePositionIsValid`（4 值 `{0:都无效, 1:都有效, 2:航向有效, 3:经纬度有效}`，`@Verified` Dock3 页）**改名 `Dock3HomePositionIsValid`**。
- **O4 地面站**：**新建** `GroundStationHomePositionIsValid`（2 值 `{0:无效, 1:有效}`，`@DocUrl` O4 属性页）。
- 三枚举**互不复用**（各设备侧 `fromCode` 值域精确，如 O4 / Dock2 侧不会误判 2/3）；`OsdField.HOME_POSITION_IS_VALID` 注释改为「（Dock2：0 无效 / 1 有效；Dock3：0 都无效 / 1 都有效 / 2 航向有效 / 3 经纬度有效；O4：0 无效 / 1 有效）」。
- POJO 字段类型统一保持 `Integer`（对齐 SDK 既有模式：`NetworkState.quality` / `DockPositionState.quality` 等均以 `Integer` 承载，枚举仅作独立反查助手）。
- 命名同步：`HomePositionIsValidTest` + `tdd-test-cases.md` §7.17 随之改名；运行时零引用（属 **public API 改名，breaking**，随 `1.18.0.0` 发布）。

#### A27-3 文档瑕疵：`rtcm_info` pushMode 父子不一致

`rtcm_info` 父级标 `pushMode=1`（state），但其 5 个子字段（`mount_point` / `port` / `host` / `rtcm_device_type` / `source_type`）均标 `0`，**疑文档笔误**。SDK 现状 `StateField.RTCM_INFO`（pushMode=1）**与父级一致**，保持不变；不据此改动。

#### A27-4 跨页值域 / 类型差异（Dock3 页 vs O4 页）

| 字段 | Dock3 机场页 | O4 地面站页（A26） | SDK 现状 |
|---|---|---|---|
| `network_state.quality` | enum_int **6 档** `{0:无信号, 1:差, 2:较差, 3:一般, 4:较好, 5:好}` | enum_int **3 档** `{0:差, 1:中, 2:好}` | `NetworkState.quality` 注释为 6 档（**与 Dock3 一致**）→ O4 复用需 `@Inferred`（见 §5-32） |
| `network_state.rate` | **float**（KB/s） | **int**（KB/s） | `NetworkState.rate` 为 `Double`（与 Dock3 一致） |
| `position_state.quality` | `{1..5, 10:RTK fixed}` | `{1..5}` | `DockPositionState.quality` 注释含 10=RTK fixed（**与 Dock3 一致**） |

#### A27-5 附带核对：SDK Dock3 字段覆盖已在页内得到印证

Dock3 页内字段（`home_position_is_valid` / `heading` / `rtcm_info` / `wireless_link_topo` / `position_state`（含 10=RTK fixed）/ `network_state` / `storage` / `alternate_land_point` / `backup_battery` / `maintain_status` / `drone_charge_state` / `live_capacity` / `live_status` / `wireless_link` / `media_file_detail` / `cover_state` / `drone_in_dock` / `supplement_light_state` / `sub_device` 等）与 SDK `DockOsd` / `StateField` 现有建模方向一致 → 佐证既有覆盖基本正确，仅上述 A27-1 / A27-2 注释与类型需修正。

---

### A28. 交叉验证源登记：DJI Cloud-API-2 官方 Java SDK（2026-10-14 决策 → A）

**来源登记**
- 仓库：`https://github.com/dji-sdk/Cloud-API-2`（`sdk/platform/java/flight_hub_sdk`）
- 下载页：`https://developer.dji.com/doc/cloud-api-2-tutorial/cn/downloads.html`
- 本地：临时克隆（`%TEMP%\cloud-api-2`，2631 个 `.java`；735 个 bean；44 个 `value` 枚举），**未进入工作区**
- **定位与效力**：属**上云 API 2（另一产品线）**的官方 Java SDK（JNI/SWIG 封装），**仅作交叉印证**；与 v1.16.1/v1.18 冲突时**以 DJI Cloud API 官方文档为唯一依据**（AGENTS.md 硬约束「协议忠实性 / 不确定即确认」）

**逐项结案**

| # | 项目 | 本 SDK 现状 | API2 SDK 佐证 | 结论 |
|---|---|---|---|---|
| 1 | `CameraMode` 值域 | 仅 0–3，且 Javadoc 注「2=拍照+录像」 | `PHOTO(0) / VIDEO(1) / SMART_PHOTO(2)=智能低光拍照 / PANO_PHOTO(3)=全景拍照 / INTERVAL_PHOTO(4)=定时拍照 / UNKNOWN(65535)` | **双重佐证**（与 A18/A24 文档一致）→ §5-13 补 `4=定时拍`、§5-28 修正 Javadoc 为「2=智能低光拍照」（不改字段） |
| 2 | `intervalPhotoInterval` | `int` | `double` | **佐证** → §5-27 改 `Double`（与 A24 示例 `2.5` 一致） |
| 3 | `photoSize` | `String` | `int` | **佐证** → §5-5 改 `Integer` |
| 4 | `videoResolution` | `String` | `int` | 支持改 `Integer`，但 v1.18 文档标 `enum_string` 与示例数字矛盾 → **仍 `@Inferred`**（§5-4），以文档原文为准 |
| 5 | `photoFormat` 值域 | Javadoc `7=RJPEG, 16=DLT664` | `RAW(0) / JPEG(1) / DLT664(16) / UNKNOWN(65535)` | **冲突** → 以 v1.18 官方文档为准（§5-34 新增待确认） |
| 6 | `recordingState` / `photoState` | `int`（0/1） | `boolean` | 语义一致，仅承载类型差异 → **保持 `int`**（与文档一致），不改 |
| 7 | `linkageZoomState` | `int`（已有） | `int` | 一致，不改 |
| 8 | `camera_state` 扩展字段族（`videoResolutionWide/Zoom`、`photoSizeWide/Zoom`、`thermalTempExceedRangeValid`、`thermalSuperResolutionEnable`、`isotherm*` 5 项） | 本 SDK 无 | API2 `CameraState` 有（自注「B4 扩展（`drc_camera_state_push.camera_state` 全量）」） | **仅线索**：待 v1.18 原文核对后方可新增（§5-33），**不得据交叉源直接落代码** |
| 9 | `fts_info` / `ftsInfo` / `propeller` | 本 SDK 无 | **零命中** | **不能佐证** → §5-8 已结案（2026-10-14 方案 B：OSD 侧 `fts_info` 暂不建模；服务侧 `drone_propeller_stop_v1` 照常实现） |
| 10 | `O4` / `GroundStation` 命名 | 本 SDK 无 | **零命中** | **不能佐证** → O4 全模块以 v1.18 官方文档为准（O4 已全量纳入，见 §5-30 已结案） |
| 11 | bean 命名参考 | — | `DeviceUnlockLicense*`（8 类）、`HmsData`/`HmsDataItem`/`HmsArgs`、`DockAccTime`、`LowPowerSwitchResult`、`RtkCalibrationResult`、`DockStatusNetworkStateResult`、`CameraPhotoFormatSetParams`、`IntervalTakePhoto*`、`IrMetering*`、`SetIsotherm*`、`NightModeSettings`、`CameraMediaStorage` 等 | **命名/结构参考**（供类命名对齐，不构成字段依据） |

**结论**：API2 SDK 可佐证「**枚举值域 / 字段类型 / 命名惯例**」，但**不构成协议依据**；凡与 v1.18 官方文档冲突，一律以 v1.18 为准。

---

### A30. 空域安全三事件原文再投喂（页面版本 2026-10-10，2026-10-14 提供）— events

**复核结论**：本批原文与 **A6**（`ground-station/o4/airspace-safety.html`）**逐字一致**（method / Topic / 字段 / 类型 / 示例 / 备注均同）→ **确认 A6 记录无误**，并据此**闭环 §5-11**。

**A30-1 `airsense_warning` —— §5-11 结案**

- `need_reply`：文档示例 `1` == `EventMethod.AIRSENSE_WARNING`（=1 ✓）→ 但 **`AirSenseWarningData` 类 Javadoc 写「`need_reply=0` 单向通知」**（[L29](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/alert/AirSenseWarningData.java#L29)）与二者**均矛盾** → **属 SDK 内部缺陷，须改为 1**。
- data 直接是**数组**（`data: [...]`）→ SDK 用自定义反序列化器当 `List<Alert>` 处理 ✓，与文档一致。

| 文档字段 | 文档 Type | SDK `Alert` | 判定 |
|---|---|---|---|
| `icao` | text | `String` | ✓ |
| `warning_level` | enum_int `{0:无危险,1..4}` | `Integer` | ✓ |
| `latitude` | float（6 位小数） | `Double` | ✓（DJI `float` → Java `Double` 既定映射，保精度） |
| `longitude` | float | `Double` | ✓ |
| `altitude` | **int**（米） | `Double` | ✗ → **改 `Integer`** |
| `altitude_type` | enum_int `{0:椭球高,1:海拔高}` | `Integer` | ✓ |
| `heading` | float | `Double` | ✓ |
| `relative_altitude` | **int**（米） | `Double` | ✗ → **改 `Integer`** |
| `vert_trend` | enum_int `{0:不变,1:上升,2:下降}` | `Integer` | ✓ |
| `distance` | **int**（米） | `Double` | ✗ → **改 `Integer`** |

**A30-2 `rid_infos` / `adsb_infos` —— 新建 POJO 规格（据本批原文定稿）**

- 共享 **`Gnss`**：`engine`(Integer) / `flag`(Integer) / **`time`(String，`YYYYMMDD-HHmmss` 格式，如 `20250723-142355`，非时间戳)** / `lat`(Double) / `lon`(Double) / `alt`(Double) / `satellite`(Integer) / `hdop`(Double)。
  - 字段名由文档 Description 明确列举 → `@Verified`；**子字段类型未在文档表格逐项标注**（仅明确 `time` 为字符串）→ 依 example 取值推断 → **`@Inferred`** 注记，`verifyPoint`=真机报文核对。
- **`RidInfosData`**：`capacityPercent`(Integer) / `powerSupply`(Integer) / `deviceSn`(String) / `gnss`(Gnss) / `rid`(**List\<RidTarget\>**，单次最多 50 条)
  - `RidTarget`：`osid`(String) / `rssi`(Integer) / `fre`(Integer) / `uatype`(Integer) / `status`(Integer) / `heading`(Integer) / **`speed`(Double ← 文档 float，ESDK 带小数如 5.4)** / **`uprate`(Double ← 文档 float，如 1.5)** / `lon`(Double) / `lat`(Double) / `altgeo`(Double) / `altbaro`(Double) / `height`(Double) / `opLon`(Double) / `opLat`(Double) / `opAlt`(Double) / `uatime`(Integer)
- **`AdsbInfosData`**：`capacityPercent`(Integer) / `powerSupply`(Integer) / `deviceSn`(String) / `gnss`(Gnss) / `adsb`(**List\<AdsbTarget\>**)
  - `AdsbTarget`：`icao`(String) / `callsign`(String) / **`height`(Integer)** / `lat`(Double) / `lon`(Double) / **`speed`(Integer)** / **`heading`(Integer)** / **`uprate`(Integer)**
- Topic 均为 `thing/product/{gateway_sn}/events`（up）；**两事件 example 中均无 `need_reply` 字段** → 见 §5-37。

**A30-3 归属确认**：外设接入登记 / 能力集 / 名称编辑属《设备管理》「外设接入与状态」（A2 / A26），**不在本页** → 与 §4 现有划分一致（`command/event/peripheral` 仅放空域安全 3 事件 + 共享 `Gnss`）。

---

### A29. Dock3 DRC 上行 `drc_camera_state_push` 全量原文（2026-10-14 提供）— drc/up

**原文要点**：Topic `thing/product/{gateway_sn}/drc/up`，Direction up，信封 `{method, data, seq}`（`seq` 与 `data` 同级、**无 bid/tid/timestamp**；`seq` 为递增序号，保证指令顺序执行）。

**A29-1 §5-33 结案 —— `camera_state` 无扩展字段**
- 文档 `data.camera_state` 仅含 12 个字段（见下表），**不含** `video_resolution_wide`/`video_resolution_zoom`/`photo_size_wide`/`photo_size_zoom`/`thermal_temp_exceed_range_valid`/`thermal_super_resolution_enable`/`isotherm_*`（5 项）。
- → **不新增任何扩展字段**；A28 记录的 API2 扩展字段族属 **Cloud-API-2 跨代专有**，非本协议依据，**不予采纳**。**§5-33 结案。**
- SDK 现有 `CameraStatePushData` 字段集合与文档**字段名/层次完全对应**（12 字段 + 2 子结构），无遗漏、无多余。

**A29-2 §5-34 结案 —— `photo_format` 值域与 SDK 一致**
- 文档 `»photo_format`：Type `enum_int`，值域 **`{"7":"RJPEG","16":"DLT664"}`**（示例未含该字段，属可选）。
- SDK [PhotoFormat](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/PhotoFormat.java) = `RJPEG(7)` / `DLT664(16)` → **逐值一致，零改动**。
- → API2 交叉源的 `RAW(0)/JPEG(1)/DLT664(16)` **不予采纳**（§5-34 结案）；`drc_camera_photo_format_set`（下行）复用同一枚举，值域同源。

**A29-3 字段级逐项核对表（文档 v1.18 vs SDK 现状）**

| 文档字段 | 文档 Type / 值域 | SDK 现状 | 判定 |
|---|---|---|---|
| `payload_index` | text（`{type-subtype-gimbalindex}`） | `String payloadIndex` | ✓ |
| `camera_mode` | enum_int `{0:拍照,1:录像,2:智能低光,3:全景拍照,4:定时拍照}` | `int cameraMode` | ✓ 类型对；**Javadoc 错**（现「2=拍照+录像」）→ 修正 |
| `interval_photo_interval` | **int**（秒/s）但示例 `2.5` | `int` | ⚠ **文档自相矛盾** → 按 §5-27 改 **`Double`** |
| `video_resolution` | **enum_string** `{"0":"19201080","1":"38402160"}` 但示例 `0` | `String` | ⚠ **文档自相矛盾** → **保持 `String`** + `@Inferred`（§5-4） |
| `linkage_zoom_state` | **bool** `{"0":"关闭","1":"开启"}` 示例 `0` | `int` | ⚠ **文档三处互斥** → **保持 `int`** + Javadoc 注明值域（§5-35 新增） |
| `photo_size` | enum_int `{"0":"4K","4":"8K"}` | `String` | ✗ 需改 **`Integer`**（A24/§5-5 已定） |
| `photo_format` | enum_int `{"7":"RJPEG","16":"DLT664"}` | `Integer` + `PhotoFormat` | ✓ **完全一致 → §5-34 结案** |
| `record_time` | int（秒/s） | `int` | ✓ |
| `recording_state` | enum_int `{0:空闲,1:录像中}` | `int` | ✓ |
| `photo_state` | enum_int `{0:空闲,1:拍照中}` | `int` | ✓ |
| `remain_photo_num` | int | `int` | ✓ |
| `remain_record_duration` | int（秒/s） | `int` | ✓ |
| `»night_mode` | enum_int `{0:关闭,1:开启,**2:自动**}` | `int` | ✓ 类型对；**Javadoc 缺 `2=自动`** → 补 |
| `»denoise_level` | enum_int `{0:关闭,1:标准降噪,2:增强降噪,3:超强降噪}` | `int` | ✓ 类型对；Javadoc 值域待补全 |
| `»night_vision_enable` | bool（示例 `true`） | `boolean` | ✓ |
| `»infrared_fill_light_enable` | bool（示例 `true`） | `boolean` | ✓ |
| `»night_scene_mode_suggestion` | enum_int `{0:无建议,1:建议开启夜景模式}` | `int` | ✓ 类型对；Javadoc 值域待补全 |
| `»is_working` | enum_int `{0:未生效,1:生效中}` | `int` | ✓ 类型对；Javadoc 值域待补全 |
| `»photo_storage_settings` | array\<text\> `{current, **vision**, ir}` | `List<String>` | ✓ 类型对；**Javadoc 举例缺 `vision`** → 补 |
| `»video_storage_settings` | array\<text\> `{current, **vision**, ir}` | `List<String>` | ✓ 类型对；同上 → 补 |
| 信封 `seq` | 与 `data` 同级（示例 `"seq": 1`） | `DjiMessage` 信封**未含 `seq`**；`command/drc/up/package-info.java` 已注明「需自行从 JSON 顶层取 `seq`」 | ✓ **既有设计已文档化**，非缺口；本次原文再确认信封为 `{method, data, seq}` |

**A29-4 新发现的文档自相矛盾（3 处，需开发者知悉）**
1. `interval_photo_interval`：Type 列 `int` ↔ 示例 `2.5`（小数）→ 按 `Double` 承载（§5-27）。
2. `video_resolution`：Type 列 `enum_string`（值域为字符串形式的数字）↔ 示例 `0`（数字）→ 保持 `String` + `@Inferred`（§5-4）。
3. `linkage_zoom_state`：Type 列标 `bool` ↔ 值域以 `0/1` 表述 ↔ 示例 `0` → **已决策（2026-10-14）保持 `int`**（与示例一致；若改 `boolean` 与示例冲突）→ §5-35 结案。

**A29-5 结论**
- §5-33 **结案**（不新增扩展字段）、§5-34 **结案**（`photo_format` 值域与 SDK 一致，零改动）。
- SDK `CameraStatePushData` 结构**无缺字段、无多余字段**；需改动的是 3 处**类型/注释**（`photoSize`→`Integer`、`intervalPhotoInterval`→`Double`、Javadoc 多处值域补全）。

---

### A31. 机场 2（Dock2）设备属性页原文（页名「设备属性列表」，2026-10-14 提供）— 机场 `dock2/properties.html`

**身份判定**：本批页名「设备属性列表」= **Dock2 机场**；A27 所据页名「设备属性」（2026-10-13）= **Dock3 机场**。两页字段表高度重合（同一批 `pushMode`/`accessMode` 说明 + 同一批字段 + 同一 3 段示例 + 同一 `property/set` / `set_reply`），以下差异项证实二者为**不同机型页**。

#### A31-1 关键差异：`home_position_is_valid` 值域（§5-31 / A27-2 悬置假设**已证实**）

| 设备页 | 类型 | 值域 |
|---|---|---|
| **Dock2**（本批） | enum_int | `{0:无效, 1:有效}`（**2 值**） |
| **Dock3**（A27） | enum_int | `{0:航向和经纬度坐标都无效, 1:都有效, 2:航向有效/经纬度无效, 3:经纬度有效/航向无效}`（**4 值**） |
| O4 地面站（A26） | enum_int | `{0:无效, 1:有效}`（2 值） |

- A27-2 原记「Dock2 页尚未核对，若 Dock2 为 2 值则存在机型差异」→ **现已证实：机场家族内部亦有机型差异**（非仅 Dock 与 O4 的跨族差异）。
- 语义关系：Dock2 的 `0/1` 与 Dock3 的 `0/1` **语义等价**（都无效 / 都有效）；Dock3 额外区分 `2/3`。O4 的 `0/1` 与 Dock2 同义。
- → 建模方案：**已决策（2026-10-14）方案 A（三枚举）**，见 **§5-38**。
- 另：`rainfall` 类型分歧已决策改 `Integer`（§5-39）。

#### A31-2 Dock2 vs Dock3 字段表差异（仅列有差异项）

| 项 | Dock2（本批） | Dock3（A27 页） |
|---|---|---|
| `home_position_is_valid` | 2 值 | 4 值 |
| `wireless_link_topo` 的 `center_node` / `leaf_nodes` | 仅 `sdr_id` / `sn`（`leaf_nodes` 另有 `control_source_index`） | 另含 `model`（机型 model-key）/ `device_type` |
| `self_converge_coordinate` | **无此字段** | 有（`latitude` / `longitude` / `height`，pushMode=0） |
| 示例含 `electric_supply_voltage` / `flighttask_prepare_capacity` | **无** | 示例有（`231` / `1`；二者**均未出现在 Dock3 页字段表**中 → 佐证 Dock3 示例与字段表存在偏差） |
| 示例中的空调字段 | `air_conditioner` struct（`air_conditioner_state` / `switch_time`，**与字段表一致**） | `air_conditioner_mode`（扁平 int，**与自身字段表矛盾**） |

#### A31-3 Dock2 页与 SDK 现状核对（4 处不一致 + 其余一致）

| 字段 | Dock2 文档 | SDK 现状 | 判定 |
|---|---|---|---|
| `home_position_is_valid` | enum_int **2 值** | `DockOsd.homePositionIsValid` `Integer`；`OsdField` 注释「0=无效/1=有效」 | 注释与 Dock2 一致、与 Dock3 冲突 → 须**按设备分别说明**；枚举建模见 §5-38 |
| `heading` | double `{min:-180, max:180}` | `DockOsd.heading` **`Integer`**；注释「0-359」 | ✗ → 改 `Double` + 注释 `-180~180`（**与 A27-1 结论一致，本页为二次印证**） |
| `rainfall` | **enum_int** `{0:无雨, 1:小雨, 2:中雨, 3:大雨}` | `DockOsd.rainfall` **`Double`**；注释「降雨量（mm）」 | ✗ 类型/语义分歧（Dock2/Dock3 两页均为 enum_int）→ 见 §5-39 |
| `cover_state` | enum_int **4 值** `{0:关闭, 1:打开, 2:半开, 3:舱盖状态异常}` | 注释仅「0=关闭，1=打开」 | Javadoc 值域需补全（§5-40） |
| `wind_speed` | float，单位 **m/s** | `OsdField` 注释「0.1 m/s，上报值需除以 10」 ↔ `DockOsd` 注释「m/s」 | ✗ SDK 内部矛盾 → 以文档为准更正 `OsdField` 注释（§5-40） |
| 其余全部字段（`mode_code` 6 值 / `position_state` `{1..5,10}` / `network_state` `{1:4G,2:以太网}`+6 档 / `storage` KB / `alternate_land_point` / `backup_battery` / `maintain_status` / `drone_charge_state` / `live_capacity` / `live_status` / `rtcm_info` / `wireless_link_topo` / `dongle_infos` / `acc_time` / `compatible_status` / `silent_mode` / `user_experience_improvement` / `air_transfer_enable` / `firmware_version` / `firmware_upgrade_status` / `drc_state` / `first_power_on` / `activation_time` / `job_number` / `height`(double) / `working_current`(float mA) / `working_voltage`(int mV) 等） | — | SDK `DockOsd` + `OsdField` + `StateField` 覆盖**方向一致** ✓（字段名 / pushMode 归属 / 值域均可对上） |

#### A31-4 结论

- Dock2 页**确证** SDK 既有 Dock 建模方向基本正确（面覆盖一致）。
- **唯一实质冲突**：`home_position_is_valid` 的机型值域差异（Dock2 **2 值** / Dock3 **4 值**）→ **§5-38 已决策：方案 A（三枚举）**。
- 另需修正：`heading` 类型（已在 A27-1 / §5-2 记录，本页二次印证）、`rainfall` 类型 → **改 `Integer`**（§5-39 已决策）、`cover_state` / `wind_speed` Javadoc（§5-40）。
- 记录在案、不阻塞实现：Dock3 页示例 `air_conditioner_mode` 与自身字段表矛盾；Dock3 示例含字段表未列的 `electric_supply_voltage` / `flighttask_prepare_capacity`（§5-41）。

---

### A32. Dock3 `return_home_info` 返航信息事件原文（2026-10-14 提供）— 机场 `dock3/wayline.html`（events up）

**投喂范围**：events 上行单事件 `return_home_info`，字段 `planned_path_points` / `last_point_type` / `flight_id` / `home_dock_sn` / `multi_dock_home_info`。
**SDK 对位**：[ReturnHomeInfoData.java](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/wayline/ReturnHomeInfoData.java)（`command/event/wayline/`）＋ 跨包共享 [PathPoint.java](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/PathPoint.java)（`command/event/`）。

#### A32-1 逐字段核对（文档 → SDK）

| 文档字段 | Type | SDK 现状 | 判定 |
|---|---|---|---|
| `planned_path_points[]` | array(struct) | `List<PathPoint> plannedPathPoints` | 已覆盖 ✓ |
| »`latitude` / `longitude` | double | `PathPoint.Double latitude` / `Double longitude` | ✓（文档 double → `Double`） |
| »`height` | float（**椭球高**，米） | `PathPoint.Double height` | ✓ 类型（float → `Double` 既定映射）；**Javadoc 未注「椭球高」语义** → §5-43 |
| `last_point_type` | enum_int `{0:最后一点在返航点上空, 1:不在返航点上空}` | `Integer lastPointType` ✓ 类型一致；**无 enumtype 枚举** | 类型 ✓；枚举缺失 → §5-42 |
| `flight_id` | text | `String flightId` | 已覆盖 ✓ |
| `home_dock_sn` | text（"蛙跳任务中当前选择的机场 home 点 SN，普通任务无该字段"） | `String homeDockSn`（类级 `@Inferred`） | ✓ → **升 `@Verified`**（§5-18 结案） |
| `multi_dock_home_info[]` | array **size:2**(struct)（"普通任务无该字段"） | `List<MultiDockHomeInfo>` | ✓；`size:2` 为文档展示约束，SDK 不校验（非阻断） |
| »`sn` | text | `String sn` | 已覆盖 ✓ |
| »`plan_status` | enum_int `{0:规划失败或正在规划中, 1:规划路径不可达, 2:规划路径因电量不可达, 3:目标可达}` | `Integer planStatus` ✓ 类型一致；**无 enumtype 枚举** | 类型 ✓；枚举缺失 → §5-42 |
| »`estimated_battery_consumption` | int(0–100) | `Integer estimatedBatteryConsumption` | 已覆盖 ✓ |
| »`home_distance` | float（米） | `Double homeDistance` | ✓（float → `Double`） |
| 信封 `gateway`（示例含） | text | `EventEnvelope.gateway`（A19 已补） | 已覆盖 ✓ |

#### A32-2 结论

- **SDK 已覆盖 ✓**：字段名、类型、归属与 Dock3 原文**逐项一致**，本批**无新增字段、无类型冲突、无结构改动**（`sdk/` 侧仅需注解更正）。
- **1 项结案（§5-18）**：`ReturnHomeInfoData` 类级 `@Inferred` **升 `@Verified`**。
- **2 项新待确认（非阻断）**：§5-42（`last_point_type` / `plan_status` 是否补建枚举）、§5-43（共享 `PathPoint` 的 `@DocUrl` 归属与 `height` 语义）。
- **1 项确认非阻断**：`multi_dock_home_info` 的 `size:2` 属文档展示约束，SDK 按「不校验数组长度」惯例不落校验（AGENTS.md 零业务逻辑）。

#### A32-3 标注类型勘误说明（§5-18 依据；经 A34-3 更正）

原文对两字段的表述为「蛙跳任务中当前选择的机场 home 点 SN，**普通任务无该字段**」/「**普通任务无该字段**」——DJI 已**明确声明**其可选性（可选，非"未定义"）。而 [ReturnHomeInfoData.java](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/wayline/ReturnHomeInfoData.java#L49-L52) 的类级 `@Inferred(reason = "home_dock_sn/multi_dock_home_info 仅 Dock2/3 支持，Dock1 无此字段")`，该 `@Inferred` 属**标注类型不当**：其事实描述「Dock1 无此字段」经 Dock1 页（A34-1）核实**属实**，但两字段的存在性与可选性已由 Dock2/Dock3 官方文档**明确声明**（"普通任务无该字段"），不满足 `@Inferred` 适用条件 → **删除 `@Inferred`，改 `@Verified`**（理由更正见 A34-3）；字段可选性由包装类型 + `@JsonInclude(NON_NULL)` 承载（缺席即 null），无需推断标注。

---

### A33. Dock2 `return_home_info` 返航信息事件原文（2026-10-14 提供）— 机场 `dock2/wayline.html`（events up）

**投喂范围**：与 A32 同一事件（Dock2 页）。
**核心结论**：**Dock2 页与 Dock3 页（A32）该事件定义逐字段完全一致** —— 字段名、类型、可选性描述（含 `home_dock_sn` / `multi_dock_home_info` 的"普通任务无该字段"）、`multi_dock_home_info` 的 `size:2` 全部相同。**逐字段核对表见 A32-1，本处不重复抄录**。

差异仅在**约束列排版**（非语义、不影响建模）：

| 差异点 | Dock2 页 | Dock3 页（A32） | 影响 |
|---|---|---|---|
| `flight_id` 约束 | 空 | `{"length":""}` | 无（纯展示层） |
| `home_distance` 约束 | `{"unit_name":"米 / m"}` | `{"max":"","min":"","step":"","unit_name":"米 / m"}` | 无（纯展示层） |

#### A33 同步结论

- **§5-18 二次印证**：Dock2 页同样明确 `home_dock_sn` / `multi_dock_home_info`（均"普通任务无该字段"）→ 「类级 `@Inferred` 升 `@Verified`」由 **Dock2 + Dock3 双页共同支持**，**无机型分叉**。
- **§5-42 不受影响**：Dock2 页 `last_point_type` 同为 2 值 `{0,1}`、`plan_status` 同为 4 值 `{0,1,2,3}` → 枚举补建决策不因机型而异。
- **§5-43 加强**：Dock2 页 `height` 同标注「椭球高」→ `PathPoint.height` 语义由**双页印证**（仍属注释 / 文档追溯更正，非阻断）。
- **本批无新增字段、无类型冲突、无新增待确认项。**
- 印证既有硬约束：同一 method 在 Dock2 / Dock3 文档中定义一致时，SDK **无需按机型分叉**，单一 POJO 覆盖两类机型。

---

### A34. Dock1 `return_home_info` 返航信息事件原文（2026-10-14 提供）— 机场 `dock1/wayline.html`（events up）

**投喂范围**：与 A32 / A33 同一事件（Dock1 页）。
**核心差异**：Dock1 页字段表**仅 3 项**（`planned_path_points` / `last_point_type` / `flight_id`），**无** `home_dock_sn`、**无** `multi_dock_home_info`。

#### A34-1 三机型对照（同一 method，字段 / 类型）

| 字段 | Dock1（A34） | Dock2（A33） | Dock3（A32） |
|---|---|---|---|
| `planned_path_points[]`（»`latitude`/`longitude` double、»`height` float=椭球高） | ✓ | ✓ | ✓ |
| `last_point_type` enum_int `{0,1}` | ✓ | ✓ | ✓ |
| `flight_id` | ✓（"当前正在执行的航线任务 ID"） | ✓ | ✓ |
| `home_dock_sn` | **✗ 字段表未定义** | ✓（仅蛙跳任务） | ✓（仅蛙跳任务） |
| `multi_dock_home_info[]`（`size:2`） | **✗ 字段表未定义** | ✓（仅蛙跳任务） | ✓（仅蛙跳任务） |
| 示例信封 `gateway` | **未出现** | 有 | 有 |
| 约束列排版 | `height {"step":0.1,"unit_name":"米 / m"}` | 见 A33 | `{"max":"","min":"","step":"0.1",…}` |

#### A34-2 结论

1. **§5-18 结案成立，理由须更正**：Dock1 页**确未定义** `home_dock_sn` / `multi_dock_home_info` → 原 `@Inferred` 中「Dock1 无此字段」的**事实描述正确**；但该两字段的存在性与可选性已由 Dock2/Dock3 文档**明确声明**（"普通任务无该字段"），属「官方文档已确认」→ 仍应 `@Verified`，`@Inferred` 属**标注类型不当**（非"理由与文档冲突"）。**结案结论不变**；`@Verified.basis` 应写明「Dock2/Dock3 页明确定义；Dock1 页无该字段」。
2. **单一 POJO 覆盖仍成立**：三机型核心字段（`planned_path_points` / `last_point_type` / `flight_id`）完全一致；机型专属字段（`home_dock_sn` / `multi_dock_home_info`）设计为**可选**（包装类型 / 空集合 + `@JsonInclude(NON_NULL)`）→ 无需按机型分叉，与既有硬约束一致。
3. **§5-42 不受影响**：`last_point_type` 三机型同为 2 值 `{0,1}`；Dock1 页并给出该字段的**显示用途**语义，可作枚举 Javadoc 依据。
4. **新增 §5-44（非阻断）**：Dock1 页独有描述语义 → `planned_path_points`「**每次推送为全量更新**」（非增量）；`last_point_type` 终端**显示判定**语义（0=可用此字段决定显示方式、可显示轨迹末点→返航点连线；1=**不应显示**该连线；不可达因「**返航点在禁飞区或障碍物内**」）→ 供 Javadoc 完善。
5. **`EventEnvelope.gateway` 非阻断**：Dock1 示例未含 `gateway`、Dock2/Dock3 示例含 → 属示例可选性，SDK 现有 `gateway`（允许 null + NON_NULL）不受影响。

#### A34-3 勘误：修正 A32-3 的表述

A32-3 原述「原理由与文档『普通任务无该字段』表述冲突，属误标」——经 Dock1 页核实，该表述**不准确**，现更正为：

- 原 `@Inferred` 中「Dock1 无此字段」**属实**（Dock1 页确未定义该两字段）；
- 正确表述为：**标注类型不当** —— 两字段存在性与可选性已由 Dock2/Dock3 官方文档**明确声明**，不满足 `@Inferred` 适用条件（"未得官方文档确认的推断"）→ 应改 `@Verified`；
- **结案结论不变**，仅理由更正。

---

### A35. 待确认项二次文档核实（2026-10-14，AI 直接抓取 DJI 官方页）

**核实范围**：对 §5 仍开放的待确认项，重新**直接抓取** DJI 官方文档页面（记录页内标注的版本日期），逐项核对，不依赖此前投喂的原文。

**本轮抓取页面（页内标注版本）**：

| 页面 | 页版本 | 用途 |
|---|---|---|
| `dock3/wayline.html` | 2026-10-10 | `return_home_info` / `flighttask_progress` |
| `dock3/drc.html`（标题「指令飞行」） | 2026-03-19 | `fly_to_point_progress` / `takeoff_to_point_progress` / `obstacle_avoidance_notify` / `camera_photo_take_progress` |
| `dock3/remote-control.html`（标题「远程控制」） | 2026-01-28 | `drc/up` 推送（`drc_camera_state_push` 等） |
| `dock3/hms.html` | 2025-03-19 | `hms` |
| `dock3/fts.html` | 2026-10-10 | `fts_config` / `drone_propeller_stop_v1` |
| `dock3/media.html` | 2026-03-19 | `file_upload_callback` |
| `dock3/log.html` | 2025-03-19 | `fileupload_list` / `_start` / `_update` / `_progress` |
| `dock3/device.html` | 2025-03-19 | `update_topo`（查机型兼容矩阵） |
| Cloud-API-2 SDK（交叉源） | — | `interval` 承载类型 |

#### A35-1 本轮核实结论

| §5 | 项 | 本轮 fresh 文档事实 | 结论 |
|---|---|---|---|
| 4 | `video_resolution` 类型 | `remote-control`：`drc_camera_state_push.camera_state.video_resolution` Type=**enum_string** `{"0":"1920*1080 ","1":"3840*2160"}`，**同页 Example 写作数字 `0`**（文档自相矛盾**第三次复现**） | **保持 `String`**（对齐 Type 列）+ `@Inferred`；矛盾记于案 |
| 6 | `fileupload start_time/end_time` 单位 | `log.html`：`fileupload_list` 回包 `list[]{ boot_index(int), start_time(int, 「毫秒 / ms」), end_time(int, 「毫秒 / ms」), size(int, byte) }`（**Dock3 = 毫秒**）；O4 侧为**秒**（§3.1.6） | **字段类型统一 `Integer`**（两页同为 `int`）；单位差异**仅 Javadoc 注明**（Dock3 毫秒 / O4 秒）+ `@Inferred`；**不按机型分叉** |
| 12 | 地面站与飞行器兼容性矩阵 | `device.html`（2025-03-19）**仅** `update_topo`（status + reply），**无任何机型兼容矩阵** | **已结案（A43，2026-10-14）**：`device.html` 无矩阵 ✓（原判正确）；开发者**另行提供**「支持产品列表与固件版本」页（`overview/product-support.html`）→ 机场上云产品族与 `DeviceCompatibility` 搭配**吻合**，**Dock↔Drone 无需新增**；余见 §5-60/§5-61 |
| 13 | `drc_camera_mode_switch.camera_mode` 值 4 | `remote-control` 页 `drc_camera_state_push.camera_state.camera_mode` = enum_int `{0:拍照,1:录像,2:智能低光,3:全景拍照,4:定时拍照}` | **值 4 存在已确认**（与 A18/A29 一致）→ 扩展 `CameraMode` 增加 `4=定时拍照`；events 侧事件 `camera_mode` 无 4 → **不影响既有反序列化** |
| 14 | `drc_camera_photo_take` 回包 `status` | 本轮 `dock3/remote-control.html` **仅含 `drc/up` 推送**（无 `drc/down`）；`dock-to-cloud/drc/dock-drc.html` 经 WebFetch **仅返回 cookie 外壳** → **回包原文本轮未 fresh 取回** | A18 已载原文（`status` enum_string `{"in_progress":"执行中"}`）；「`status` 出现条件」**官方文档未规定** → 维持 `@Inferred`（`String status` 包装类型），待真机验证 |
| 15 | `drc_interval_photo_set.interval` 承载类型 | **Cloud-API-2 交叉源**：`IntervalTakePhotoSetParam.interval` = **`double`**（其 `drcSetIntervalTakePhoto` 链路注释即 `method: drc_interval_photo_set`）；另 `CameraState.intervalPhotoInterval` = `double` | **可结案：承载 `Double`**（文档 constraint 键含小数 `0.7`，`Double` 唯一无损；与 API2 一致） |
| 16 | `return_home` 回包 `output.status` | 本轮 `wayline.html` 抓取**截断于 Service 段前**，未取到 `return_home` | A19 原文已载 `{result, output{status}}`、status **8 值** → 结论不变：**新增 `ReturnHomeReply`**（§4 已列） |
| 17 | `status` 值域 9 vs 8 | `wayline.html`：`flighttask_progress.status` = enum_string **9 值** `{canceled,failed,in_progress,ok,partially_done,paused,rejected,sent,timeout}`（逐字确认） | 与 A19 `return_home` **8 值**（无 `partially_done`）→ **两处值域确认不同** → 补 Javadoc 时逐处区分 |
| 20 | `EventMethod` 是否补登 `hms_inner` | `hms.html`（2025-03-19）：唯一 Method `hms`，Data 表与 Example **全文无 `hms_inner`** | **不补登** `hms_inner`（官方文档从未定义该 method；原 §5-20「文档尾注提及」系误记） |
| 23 | `PowerUnlock` 空 struct | 本轮未再抓取（非本次页面范畴）；A21 原文记 `power_unlock` 为**空 struct `{}`** | 文档确认存在空 struct → **处置待开发者裁决**（空 record 占位 vs 省略字段） |
| 24 | `FtsServerType` 是否引入枚举 | `fts.html`（2026-10-10）：`fts_server_type` = enum_int `{0:"DJI FTS 服务器",1:"自定义 FTS 服务器"}` | **确认值域 {0,1} → 建议引入 `FtsServerType` 枚举**；字段保持 `Integer` |
| 25 | `fts_config.mqtt_broker` 条件必填 | `fts.html`：`mqtt_broker` Type=struct，Description「**当 fts_server_type 为 1（自定义）时必填**」 | **确认条件必填** → 字段允许 null + Javadoc 说明「`fts_server_type=1` 时必填」；SDK **不做运行时校验** |
| 42 | `last_point_type` / `plan_status` 是否建枚举 | `wayline.html`：`last_point_type` = enum_int `{0:"轨迹最后一个点在返航点的上空",1:"轨迹最后一个点不在返航点上空"}`；`plan_status` = enum_int `{0:"规划失败或正在规划中",1:"规划路径不可达",2:"规划路径因电量不可达",3:"目标可达"}` | 值域**逐字确认**（与 A32 一致）；**是否补建枚举待开发者裁决**（不补亦不违反协议忠实性） |
| 43 | `PathPoint` 的 `@DocUrl` 与 `height` 语义 | `dock3/drc.html` 的 `fly_to_point_progress` / `takeoff_to_point_progress` **与** `dock3/wayline.html` 的 `return_home_info` **均**定义 `planned_path_points`（»`height` Type=**float**、Description=「**椭球高**」） | **`@DocUrl` 指向 `drc.html` 合理**（该页确含该结构，属多页共用）；**Javadoc 需补 `height`「椭球高」**；多页归属记于 Javadoc |
| 44 | Dock1 页描述语义入 Javadoc | 本轮 `dock3/wayline.html` 的 `return_home_info` **字段表 Description 列为空**，`planned_path_points` / `last_point_type` **无「全量更新」/ 显示判定**描述 → 与 A34 所述 Dock1 页存在**页间描述差异** | 「全量更新」「终端显示判定」等语义**仅见于 Dock1 页**；**是否补入 Javadoc 待开发者裁决**（须注明"取自 Dock1 页描述"以区分页差异） |

#### A35-2 本轮未能核实 / 待开发者裁决

**A. 因页面缺失或抓取限制未取到 fresh 原文的项**

1. `dock3/remote-control.html`（2026-01-28）**仅含 `drc/up` 推送**，**不含 `drc/down` 指令**（`drc_camera_mode_switch` / `drc_camera_photo_take` / `drc_interval_photo_set` 等均不在其中；该页 TOC 仅 6 个 event 小节）；`dock-to-cloud/drc/dock-drc.html` 经 WebFetch **仅返回 cookie 政策外壳**（SPA 未渲染）→ **DRC 下行相机指令的 fresh 原文本轮未取回**。
   - 相关项（§5-13 / 14 / 15）的原文依据仍为**开发者已投喂的 A16/A17/A18/A29**（均标注来源 `remote-control`）+ **Cloud-API-2 交叉**。
   - 如需彻底核实，请开发者再投喂 **DRC 下行指令页（`dock-drc` / 「远程控制」Service 段）**原文，或授权用浏览器子代理渲染该页。
2. `dock3/wayline.html` 本轮抓取**截断于 Service 段前**（未含 `return_home` 的 `services_reply`），`return_home` 回包以 **A19 原文**为准。

**B. 需开发者裁决的项（非阻断）**：§5-23（`PowerUnlock` 空 struct 建模）、§5-42（是否补建 `LastPointType` / `HomePlanStatus` 枚举）、§5-43（Javadoc 补「椭球高」）、§5-44（Dock1 页语义是否入 Javadoc）。**（注：§5-12 已于 2026-10-14 结案，来源见 A43）**

#### A35-3 附带核对（非缺口）

- `ObstacleAvoidanceNotifyData.ObstacleInfo.height`：`dock3/drc.html` `obstacle_avoidance_notify` 的 »`height` Type=**double**、Description「避障发生位置 **WGS84 椭球高**」；SDK 该字段（`double height`）**类型一致** → **非缺口，无需改动**。
- 同页 `planned_path_points.height` 为 **float**、`obstacle.height` 为 **double** —— 二者文档 Type **本就不同**，SDK 均映射为 Java `Double`（`float`→`Double` 既定映射；`obstacle.height` 文档未标可选故用基本类型），**均正确**。

---

### A36. Dock3 DRC Service（down）全量原文完整性核对（39 项）（2026-10-14 提供）— `dock3` DRC 通道 `drc/down`

**批次性质**：本批为 **Dock3 DRC 通道 `drc/down`（Service 段）全量原文**，共 **39 个 method**（每条 `drc/down` 指令对应 `drc/up` 回包 `{result}`；`seq` 与 `data` 同级）。**原文在页末 `#` 处截断** —— AI 目标识别后半段（`drc_ai_identify_score_mode_set` / `_score_set` / `_score_reset` / `_filter_set` / `drc_ai_spotlight_zoom_select` / `_confirm` / `_stop`）与相机拍照类（`drc_camera_photo_take` / `_stop` / `drc_camera_focus*` / `drc_camera_zoom*` / `drc_camera_aim` / `drc_gimbal_reset` / `drc_screen_drag` / `drc_screen_split` / `drc_ir_metering_*` 等）**未包含在本批**（已分别记录于 A8 / A16 / A17 / A18）。

**核对方式**：本批各 method 与字段此前已**分批**收录于 **A8 / A14–A27**；本批为**合并全量**，故本次执行**完整性交叉核对**（逐 method 比对 SDK `DrcMethod` 常量 + Request POJO + 报告既有记录）。

#### A36-1 逐项对照（39 项）

| # | method | 文档字段（要点） | `DrcMethod` 常量 | Request POJO | 报告出处 |
|---|---|---|---|---|---|
| 1 | `drc_camera_night_mode_set` | `payload_index`(text) + `mode`enum_int{0关闭,1开启,2自动} | ✓ | ✓ `DrcCameraNightModeSetRequest` | A18 |
| 2 | `drc_camera_denoise_level_set` | `payload_index` + `level`enum_int{2,3} | ✓ | ✓ `DrcCameraDenoiseLevelSetRequest` | A16 |
| 3 | `drc_camera_night_vision_enable` | `payload_index` + `enable`bool | ✓ | ✓ `DrcCameraNightVisionEnableRequest` | A18 |
| 4 | `drc_infrared_fill_light_enable` | `payload_index` + `enable`bool | ✓ | ✓ `DrcInfraredFillLightEnableRequest` | A18 |
| 5 | `drc_force_landing` | Data **null**（示例 `{}`） | ✓ | ✓ `NoParameterDrcRequest` | A8 |
| 6 | `drone_emergency_stop` | Data **null** | ✓ | ✓ `NoParameterDrcRequest` | A8 |
| 7 | `drc_emergency_landing` | Data **null** | ✓ | ✓ `NoParameterDrcRequest` | A8 |
| 8 | `drc_light_brightness_set` | `psdk_index` + `group`{0主探照灯} + `brightness`1–100 | ✓ | ✓ `DrcLightBrightnessSetRequest` | A8 |
| 9 | `drc_light_mode_set` | `psdk_index` + `group`{0主} + `mode`{0关闭,1常亮,2爆闪,3快速爆闪,4交替爆闪} | ✓ | ✓ `DrcLightModeSetRequest` | A8 |
| 10 | `drc_light_fine_tuning_set` | `psdk_index` + `position`{0左灯,1右灯} + `value`int(-3~+3) + `saved`bool | ✓ | ✓ `DrcLightFineTuningSetRequest` | A8 |
| 11 | `drc_light_calibration` | `psdk_index` | ✓ | ✓ `DrcLightCalibrationRequest` | A8 |
| 12 | `drc_speaker_play_mode_set` | `psdk_index` + `play_mode`{0单次,1循环} | ✓ | ✓ `DrcSpeakerPlayModeSetRequest` | A8 |
| 13 | `drc_speaker_tts_set` | `psdk_index` + `volume`(0–100) + `type`{0男声,1女声} + `language`{0中文,1英文} + `speed`(0–100) | ✓ | ✓ `DrcSpeakerTtsSetRequest` | A8 |
| 14 | `drc_speaker_play_volume_set` | `psdk_index` + `play_volume`(1–100) | ✓ | ✓ `DrcSpeakerPlayVolumeSetRequest` | A8 |
| 15 | `drc_speaker_play_stop` | `psdk_index` | ✓ | ✓ `DrcSpeakerPlayStopRequest` | A8 |
| 16 | `drc_speaker_replay` | `psdk_index` | ✓ | ✓ `DrcSpeakerReplayRequest` | A8 |
| 17 | `drc_speaker_tts_play_start` | `psdk_index` + `tts`struct{`name`,`text`,`md5`} | ✗ **缺** | ✗ **缺** | A8（缺口）/ A15 |
| 18 | `drc_psdk_input_box_text_set` | `psdk_index` + `value`text(≤128) | ✗ **缺** | ✗ **缺** | A15（缺口 1） |
| 19 | `drc_psdk_widget_value_set` | `psdk_index` + `index`int + `value`int | ✗ **缺** | ✗ **缺** | A16（缺口） |
| 20 | `drc_initial_state_subscribe` | Data **null**（示例 `{}`） | ✓ | ✓ `NoParameterDrcRequest` | A16 |
| 21 | `drc_night_lights_state_set` | `night_lights_state`{0关闭,1开启} | ✓ | ✗ **缺** | A16（缺口 2） |
| 22 | `drc_stealth_state_set` | `stealth_state`{0关闭,1开启} | ✓ | ✗ **缺** | A16（缺口 2） |
| 23 | `drc_camera_aperture_value_set` | `payload_index` + `camera_type`enum_string{wide,zoom} + `aperture_value`enum_int(60+ 档) | ✓(`@Inferred`) | ✗ **缺** | A16 |
| 24 | `drc_camera_shutter_set` | `payload_index` + `camera_type` + `shutter_value`enum_int(0–60) | ✓(`@Inferred`) | ✗ **缺** | A18 |
| 25 | `drc_camera_iso_set` | `payload_index` + `camera_type` + `iso_value`enum_int | ✓(`@Inferred`) | ✗ **缺** | A17 |
| 26 | `drc_camera_mechanical_shutter_set` | `payload_index` + `camera_type`{wide} + `mechanical_shutter_state`{0,1} | ✓(`@Inferred`) | ✗ **缺** | A18 |
| 27 | `drc_camera_dewarping_set` | `payload_index` + `camera_type`{wide} + `dewarping_state`{0,1} | ✓(`@Inferred`) | ✗ **缺** | A16 |
| 28 | `drc_camera_photo_size_set` | `payload_index` + `photo_size`enum_int{0:4K,4:8K} | ✗ **缺** | ✗ **缺** | A23/A24/A25（§5-26 已结案） |
| 29 | `drc_video_resolution_set` | `payload_index` + `video_resolution`enum_string{0,1} | ✓(`@Inferred`) | ✗ **缺** | A16/A23/A25 |
| 30 | `drc_camera_photo_format_set` | `payload_index` + `photo_format`enum_int{7:RJPEG,16:DLT664} | ✗ **缺** | ✗ **缺** | A18（§5-10） |
| 31 | `drc_linkage_zoom_set` | `payload_index` + `state`bool(仅 M3TD) | ✓(`@Inferred`) | ✗ **缺** | A8/A16（§5-35） |
| 32 | `drc_video_storage_set` | `payload_index` + `video_storage_settings`array<text>{current,vision,ir} | ✗ **缺** | ✗ **缺** | A16（缺口） |
| 33 | `drc_photo_storage_set` | `payload_index` + `photo_storage_settings`array<text>{current,vision,ir} | ✗ **缺** | ✗ **缺** | A18/A16 |
| 34 | `drc_interval_photo_set` | `payload_index` + `interval`enum_string{1,2,3,5,7,10,15,20,30,60,0.7} | ✓(`@Inferred`) | ✗ **缺** | A18/A35（§5-15 已结案→`Double`） |
| 35 | `drc_camera_mode_switch` | `payload_index` + `camera_mode`enum_int{0拍照,1录像,2智能低光,3全景拍照,**4定时拍**} | ✗ **缺** | ✗ **缺** | A18（§5-13） |
| 36 | `drc_ai_model_select` | `index`int | ✓ | ✓ `AiModelSelectRequest` | A8 |
| 37 | `drc_ai_identify_set` | `on`{0关闭,1开启} | ✓ | ✓ `AiIdentifySetRequest` | A8 |
| 38 | `drc_ai_spotlight_zoom_set` | `on`{0关闭,1开启} | ✓ | ✓ `AiSpotlightZoomSetRequest` | A8 |
| 39 | `drc_ai_spotlight_zoom_track` | `target_index`int | ✓ | ✓ `AiSpotlightZoomTrackRequest` | A8 |

#### A36-2 结论

1. **`DrcMethod` 常量**：39 项中 **31 项已存在**；**缺失 8 项** = `drc_speaker_tts_play_start` / `drc_psdk_input_box_text_set` / `drc_psdk_widget_value_set` / `drc_camera_photo_size_set` / `drc_camera_photo_format_set` / `drc_video_storage_set` / `drc_photo_storage_set` / `drc_camera_mode_switch` —— **与 §4 改动清单既有登记一致**。
2. **Request POJO**：**21 项已存在**；**缺失 18 项**（第 17–19、21–35 项），亦均已在 §4 改动清单登记。
3. **无新增缺口、无新增字段冲突**：本批全部 method 与字段均可回指既有附录（A8 / A14–A27），**本批未提供此前未记录的新字段或新值域**。
4. `drc_camera_photo_size_set` 本批**再次出现**（`photo_size` enum_int `{0:4K,4:8K}`）→ **再次印证 §5-26 已在 A25 结案**（该 DRC 下行命令确实存在）。
5. **本批价值**：作为 Dock3 `drc/down` **全量清单**，佐证报告对 DRC 下行方法的盘点**无遗漏**（截至本批 39 项；页末截断部分见 A8/A18）。

#### A36-3 本批附带确认（细节 / 笔误）

- `drc_camera_denoise_level_set.level` 文档仅列 `{2:增强降噪 15fps, 3:超强降噪 5fps}`；与 A16 已记 Javadoc 全值域 `{0:关闭,1:标准,2:增强,3:超强}` 兼容（0/1 来自 `camera_state.night_mode_settings.denoise_level`）→ **保持 0–3**。
- `drc_linkage_zoom_set.state`：Type=`bool`、值域以 `{0:关闭,1:开启}` 表述、示例 `0` → **§5-35 已决策保持 `int`**。
- `drc_initial_state_subscribe`：Data 表列 `null/null/double`、示例 `"data": {}` → 复用 `NoParameterDrcRequest`（§5 已记，A16）。
- **通道间差异**：本批 DRC `drc_video_storage_set.video_storage_settings` 为 `{current, vision, ir}`；而 services 侧（§3 已记）为 `{current, wide, zoom, ir}` → **两通道值域不同**，SDK 均以 `List<String>` 承载（不做枚举），Javadoc 需分通道注明。
- **文档展示笔误（不影响建模）**：`drc_light_brightness_set.brightness` constraint 写作 `{"max":100","min":"1"}`（引号错位）；`drc_speaker_tts_set` 的 `volume`/`speed` constraint 括号错乱（`{"max"100:""...}`）；`drc_light_fine_tuning_set.value` constraint `{"max":"+3°","min":-3}`；`drc_camera_aperture_value_set` Example `"aperture_value": 5`（`5` 不在枚举值域内，A16 已记）。

---

### A37. Dock3 DRC AI 目标识别后半段（7 项）补录（2026-10-14 提供）— `dock3` DRC 通道 `drc/down`

**批次性质**：本批为 **A36 页末截断处之后的 AI 目标识别段**（7 个 method），使 A36 的 Dock3 `drc/down` 清单**补全至 46 项**（A36 的 39 项 + 本批 7 项）。

#### A37-1 逐项对照（7 项）

| # | method | 文档字段（要点） | `DrcMethod` 常量 | Request POJO | 报告出处 | 判定 |
|---|---|---|---|---|---|---|
| 40 | `drc_ai_spotlight_zoom_select` | `center_x`/`center_y`/`height`/`width` **int** `{max:10000,min:0}`（单位 1/10000×画面宽/高；示例整数 `0`/`10000`） | ✓ | ✓ `AiSpotlightZoomSelectRequest`（**字段为 `Double`** ✗ 与文档 `int` 不符） | A8 | **需核对类型**（见 A37-3） |
| 41 | `drc_ai_spotlight_zoom_confirm` | Data **null**（示例 `{}`） | ✓ | 复用 `NoParameterDrcRequest` | A8 | 已覆盖 ✓ |
| 42 | `drc_ai_spotlight_zoom_stop` | Data **null** | ✓ | 复用 `NoParameterDrcRequest` | A8 | 已覆盖 ✓ |
| 43 | `drc_ai_identify_score_mode_set` | `score_mode` enum_int`{0无效值,1计数模式,2搜救模式,3用户自定义模式}` | ✓ | ✓ `AiIdentifyScoreModeSetRequest`(`AiScoreMode`) | A8 | 已覆盖 ✓ |
| 44 | `drc_ai_identify_score_set` | `score` int `{max:100,min:0,step:1}` | ✓ | ✓ `AiIdentifyScoreSetRequest`(`Integer`) | A8 | 已覆盖 ✓ |
| 45 | `drc_ai_identify_score_reset` | Data **null** | ✓ | 复用 `NoParameterDrcRequest` | A8 | 已覆盖 ✓ |
| 46 | `drc_ai_identify_filter_set` | `filters` array<int>（三方模型 label index +128 偏移） | ✓ | ✓ `AiIdentifyFilterSetRequest`(`List<Integer>`) | A8 | 已覆盖 ✓ |

#### A37-2 结论

1. A36 截断处的 7 个 AI method **全部已记录于 A8**；`DrcMethod` 的 **11 个 AI 方法常量全部存在**；POJO 已存在 4 个（`select` / `score_mode_set` / `score_set` / `filter_set`），3 个 null-data 复用 `NoParameterDrcRequest`。
2. **无遗漏**：Dock3 `drc/down` 清单至此完整（46 项），与报告既有盘点一致。

#### A37-3 本批新发现（1 项，需开发者确认）

**`AiSpotlightZoomSelectRequest` 字段类型与文档不符**（新增 §5-45）：

- **文档（本批 Dock3）**：`drc_ai_spotlight_zoom_select` 的 `center_x` / `center_y` / `height` / `width` Type=**`int`**、constraint `{"max":10000,"min":0}`、示例为整数 `0` / `10000`，单位 1/10000×画面宽/高。
- **SDK 现状**：[`AiSpotlightZoomSelectRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/ai/AiSpotlightZoomSelectRequest.java#L50-L55) 四字段为 **`Double`**（`@Verified`，basis 记「文档约束 `{max:1, min:0}`*10000」）。
- **冲突**：文档 Type=`int` / 值域 `[0,10000]` / 示例整数 ↔ SDK `Double`（且 SDK basis 所述 `{max:1,min:0}` 与当前文档 `{max:10000,min:0}` **不一致**，basis 已过时）。
- **处置（待开发者裁决）**：**(a) 改为 `Integer`**（对齐当前文档 Type 列与整数示例，推荐）；**(b) 保留 `Double`**（若真机确为归一化浮点值）。无论哪种，`@Verified(basis=...)` 需同步更新为当前文档约束。

**另附（元数据过时，非协议差异）**：该 POJO Javadoc 记有「文档原文 `height` 单位写'画面宽度'、`width` 单位写'画面高度'，已修正」——**本批文档单位描述已正确**（`height`→画面高度、`width`→画面宽度，未互换）→ 该「文档修正」说明**已过时**，实现时应删除或改写为「经 v1.18 文档核实，单位描述正确」。

**A37-3 结案（2026-10-14，本批原文二次投喂后开发者裁决）**：四字段 **改 `Integer`**。

- 原文三度复现（A8 首记 → A37 首次识别 → 本批二次投喂）均一致：Type=`int`、constraint `{"max":10000,"min":0}`、Example 整数 `0` / `10000`，单位 1/10000 × 画面宽/高。
- 处置：`Double` → **`Integer`**（对协议忠实性）；`@Verified(basis)` 更正为当前约束 `{"max":10000,"min":0}`（原 basis `{max:1,min:0}`×10000 系旧口径误读）；上述 Javadoc「文档修正」段删除。
- 登记位置：§0 决策记录 / §2 变更总览 / §4 改动清单 / §5-45（均 2026-10-14）。

---

### A38. Dock2 DRC 页（Events + Service 段）原文（2026-10-14 提供）— 机场 `dock2` DRC 通道（`drc/down` + `drc/up`）

**批次性质**：Dock2 侧 DRC 页原文，**Event 段 4 项 + Service 段 18 项**（页末截断于 `drc_camera_dewarping_set`）。用途：①核对 Dock2 机型与 SDK 现有（Dock3 口径）建模是否一致；②补齐发布项 3（机场2/3 相机录像清晰度 / 照片分辨率）的 **Dock2 侧依据**。

#### A38-1 Event 段（`drc/up`，4 项）

| # | method | 文档字段（要点） | SDK | 判定 |
|---|---|---|---|---|
| 1 | `drc_camera_photo_info_push` | `countdown_time`int / `result`int / `status`enum_string`{fail,in_progress,ok}` / `progress{current_step`enum_int`{3000,3002,3005}, percent`int`}` / `ext{camera_mode`enum_int`{3:全景拍照}}` | `DRC_CAMERA_PHOTO_INFO_PUSH` ✓ + `CameraPhotoInfoPushData` ✓（字段/类型全一致） | 已覆盖 ✓ |
| 2 | `drc_camera_state_push` | `payload_index`text / `camera_state{`camera_mode`enum_int`{0..4}, interval_photo_interval`int`(示例 2.5), video_resolution`enum_string`(示例 `0`), linkage_zoom_state`bool`(示例 `0`), photo_size`enum_int`{0:4K,4:8K}, record_time, recording_state, photo_state, remain_photo_num, remain_record_duration}` / `media_storage{photo_storage_settings[], video_storage_settings[]}`（**无** `night_mode_settings` / `photo_format`） | `DRC_CAMERA_STATE_PUSH` ✓ + `CameraStatePushData` ✓ | 类型差异见下（既有项） |
| 3 | `drc_drone_state_push` | `stealth_state`bool / `night_lights_state`bool / `mode_code`enum_int`{0..17}` / `landing_type`enum_int`{0..4}` / `landing_protection_type`enum_int`{0..3}` | `DRC_DRONE_STATE_PUSH` ✓ + `DroneStatePushData` ✓（5 字段一致） | 已覆盖 ✓ |
| 4 | `drc_camera_osd_info_push` | `payload_index` / `wide_lense`{5} / `zoom_lense`{13} / `measure_target`{5} / `ir_lense`{…含 `thermal_supported_palette_styles[]`} / `liveview.liveview_world_region{left,top,right,bottom}` | `DRC_CAMERA_OSD_INFO_PUSH` ✓ + `CameraOsdInfoPushData` ✓ | **2 处不符**（见 A38-3） |

**既有类型差异（本批二次复现，非新项）**：①`interval_photo_interval` 文档 `int` 示例 `2.5`（§5-27）；②`photo_size` 文档 `enum_int` vs SDK `String`（§5-5）；③`video_resolution` 文档 `enum_string` 示例数字 `0`（§5-4，**四度复现**）；④`linkage_zoom_state` 文档 `bool` 值域 `{0/1}` 示例 `0`（§5-35 保持 `int`）。**注**：本页 `camera_state` 字段集合较 Dock3 小（无 `night_mode_settings`/`photo_format`）→ 按既有约定**不得据此删字段**（属机型差异，非文档精简）。

#### A38-2 Service 段（`drc/down`，18 项）

| # | method | 文档 Data（要点） | `DrcMethod` | Request POJO | 判定 |
|---|---|---|---|---|---|
| 1 | `drc_camera_mode_switch` | `payload_index`text + `camera_mode`enum_int`{0,1,2,3,4}` | ✗ **缺** | ✗ 缺 | 需新增（§4 已列，§5-13 值 4） |
| 2 | `drc_force_landing` | Data **null** | ✓ | `NoParameterDrcRequest` | 已覆盖 ✓ |
| 3 | `drone_emergency_stop` | Data **null** | ✓ | `NoParameterDrcRequest` | 已覆盖 ✓ |
| 4 | `drc_emergency_landing` | Data **null** | ✓ | `NoParameterDrcRequest` | 已覆盖 ✓ |
| 5 | `drc_linkage_zoom_set` | `payload_index` + `state`bool`{0/1}`（仅 M3TD） | ✓(`@Inferred`) | ✗ 缺 | **POJO 需新增**（A36 行 31） |
| 6 | `drc_video_resolution_set` | `payload_index` + `video_resolution`enum_string`{"0":"19201080","1":"38402160"}` | ✓ | ✗ 缺 | 需新增（§4） |
| 7 | `drc_camera_photo_size_set` | `payload_index` + `photo_size`enum_int`{0:4K,4:8K}` | ✗ **缺** | ✗ 缺 | 需新增（§4，§5-26） |
| 8 | `drc_video_storage_set` | `payload_index` + `video_storage_settings`array<text>`{current,vision,ir}` | ✗ **缺** | ✗ 缺 | 需新增（§4，A16） |
| 9 | `drc_photo_storage_set` | `payload_index` + `photo_storage_settings`array<text>`{current,vision,ir}` | ✗ **缺** | ✗ 缺 | 需新增（§4，A18） |
| 10 | `drc_interval_photo_set` | `payload_index` + `interval`**enum_string**`{"1":"1s",…,"0.7":"0.7s"}`（示例数字 `1`） | ✓(`@Inferred`) | ✗ 缺 | POJO 需新增（口径见 A38-3） |
| 11 | `drc_initial_state_subscribe` | Data **null** | ✓ | `NoParameterDrcRequest` | 已覆盖 ✓ |
| 12 | `drc_night_lights_state_set` | `night_lights_state`enum_int`{0/1}` | ✓ | ✗ 缺 | 需新增（A16 缺口 2） |
| 13 | `drc_stealth_state_set` | `stealth_state`enum_int`{0/1}` | ✓ | ✗ 缺 | 需新增（A16 缺口 2） |
| 14 | `drc_camera_aperture_value_set` | `payload_index` + `camera_type`enum_string`{wide,zoom}` + `aperture_value`enum_int（示例 `5` 不在值域） | ✓(`@Inferred`) | ✗ 缺 | 需新增（A16） |
| 15 | `drc_camera_shutter_set` | `payload_index` + `camera_type` + `shutter_value`enum_int`{0..59,60:"Auto"}` | ✓(`@Inferred`) | ✗ 缺 | 需新增（A18） |
| 16 | `drc_camera_iso_set` | `payload_index` + `camera_type` + `iso_value`enum_int`{0..13,255}` | ✓(`@Inferred`) | ✗ 缺 | 需新增（A17） |
| 17 | `drc_camera_mechanical_shutter_set` | `payload_index` + `camera_type`enum_string`{wide}` + `mechanical_shutter_state`enum_int`{0/1}` | ✓(`@Inferred`) | ✗ 缺 | 需新增（A18） |
| 18 | `drc_camera_dewarping_set` | `payload_index` + `camera_type`enum_string`{wide}` + `dewarping_state`enum_int`{0/1}` | ✓(`@Inferred`) | ✗ 缺 | 需新增（A16） |

#### A38-3 结论与新发现

1. **Event 段 4 项全部已覆盖 ✓**（method + POJO 齐备，字段/类型与文档一致）。
2. **Service 段 4 个 `DrcMethod` 常量缺失**（`drc_camera_mode_switch` / `drc_camera_photo_size_set` / `drc_video_storage_set` / `drc_photo_storage_set`），**与 §4 待补清单完全一致** → Dock2 页二次印证（≠新缺口）。
3. **Service POJO 缺失 14 项**（除 null-data 复用的 4 项外）→ 全部落在 §4 新建清单内；其中 **`drc_linkage_zoom_set` 的 Request POJO 此前未在 §4 单列** → 已由 A36 行 31 登记，本批二次确认。

**新发现 2 项（需开发者裁决，非阻断）**：

- **§5-46**：`CameraOsdInfoPushData.IrLense` **缺** `thermalSupportedPaletteStyles` 字段 —— 文档 `ir_lense.thermal_supported_palette_styles` Type=`array`、`item_type=enum_int`（「设备支持的调色盘样式集合」，值域同 `thermal_current_palette_style`），SDK `IrLense` 现仅 9 字段、无此集合。（A14 已提及文档含该集合，本批给出 Dock2 页二次印证 → 首次**项化**为缺口）
- **§5-47**：`CameraOsdInfoPushData.IrLense.irZoomFactor` **类型不符** —— 文档 `ir_zoom_factor` Type=**`float`**、`{"max":20,"min":2}`，SDK 现为 **`int`**（应为 `Double`，`float`→`Double` 既定映射）。

**文档自身不一致（记录在案，不做 SDK 改动）**：

- `drc_camera_photo_info_push`：Example `camera_mode: 2`（Data 表值域仅 `{3:全景拍照}`）、`progress.current_step: 0`（值域 `{3000,3002,3005}`）→ 示例偏离值域。
- `drc_interval_photo_set.interval`：Type=**`enum_string`**（键为数字串 `"1"`…`"0.7"`）但 Example 为**数字** `1`；与 §5-15 原依据（Cloud-API-2 `double`）口径不同 → **§5-15 补充说明**（见下）。
- `drc_video_resolution_set.video_resolution`：Type=`enum_string`、Example 数字 `0`（§5-4 四度复现）。
- `drc_camera_shutter_set.shutter_value`：本页值域 `{0..59, 60:"Auto"}` → 实现 `ShutterValue` 枚举时需与 **Dock3/A18 页**逐值核对（本页含 `60=Auto`）。

**§5-15 口径补充（已登记入 §5）**：`drc_interval_photo_set.interval` 文档 Type 出现**三口径不一**——Dock3 页 `int`、Dock2 页 `enum_string`、Cloud-API-2 `double`；处置**仍取 `Double` 承载**（可无损容纳整数与 `0.7` 小数，规避 `enum_string` 键「数字串 vs 数字」二义）。

---

### A39. O4 地面站固件升级页（`firmware.html`）原文（2026-10-14 提供）

**批次性质**：O4 地面站（`ground-station/o4/firmware.html`）固件升级原文，**Event 段 1 项（`ota_progress`）+ Service 段 1 项（`ota_create` + `services_reply`）**。用途：①核对 §3.1.5 原「可直接复用、完全一致」结论；②补齐发布项 1（O4 地面站）的 **firmware 面依据**。

#### A39-1 Event 段 `ota_progress`（`events`/up，need_reply=1）

| 文档字段 | Type | SDK | 判定 |
|---|---|---|---|
| `result` | `int`（非 0 错误） | `OtaProgressData.result`(`Integer`) | ✓ |
| `output.status` | `enum_string` `{canceled,failed,in_progress,ok,paused,rejected,sent,timeout}` | `Output.status`(`String`) | ✓ |
| `output.progress.percent` | `int` `{0..100}` | `Progress.percent`(`Integer`) | ✓ |
| `output.progress.current_step` | `enum_string` `{download_firmware,upgrade_firmware}` | `Progress.currentStep`(`String`) | ✓ |

- method：`EventMethod.OTA_PROGRESS` ✓（`need_reply=1`，与 example 含 `tid`/`bid` 一致）
- **判定：已覆盖 ✓**（method + POJO 齐备，字段/类型/枚举值全一致）
- **元数据偏差（§5-50）**：`OtaProgressData` `@DocUrl` 现指 `dock3/config.html`、`@Verified(basis)`=simulator 依据；本次 O4 `firmware.html` 为权威字段表 → 建议改指 + 升级 basis

#### A39-2 Service 段 `ota_create`（`services`/down）+ `services_reply`（up）

**Request（`services`/down）**

| 文档字段 | Type | SDK `OtaDevice` | 判定 |
|---|---|---|---|
| `devices` | `array`(`size:1`, `item_type:struct`) | `List<OtaDevice>` | ✓ |
| `»sn` | `text` | `String` | ✓ |
| `»product_version` | `text` | `String` | ✓ |
| `»file_url` | `text` | `String` | ✓ |
| `»md5` | `text` | `String` | ✓ |
| `»file_size` | **`int`** | **`Long`** | **✗ 类型不符（§5-48）** |
| `»file_name` | `text` | `String` | ✓ |
| `»firmware_upgrade_type` | `enum_int` `{2:一致性升级,3:普通升级,4:PSDK升级}` | 原始 `int`，**无枚举** | **△ 缺枚举助手（§5-49）** |

- method：`ServiceMethod.OTA_CREATE` ✓
- 字段集合与顺序：文档顺序（`sn`/`product_version`/`file_url`/`md5`/`file_size`/`file_name`/`firmware_upgrade_type`）与 SDK 顺序不同，但 record 按名映射，**无影响**。

**Reply（`services_reply`/up）**

| 文档字段 | Type | SDK | 判定 |
|---|---|---|---|
| `result` | `int` | 无专属 Reply（`OtaCreateRequest` 未含 reply） | 见 §5-51 |
| `output.status` | `enum_string`（同 8 值） | **未建模** | 见 §5-51 |

- **判定**：`ota_create` 的 `output.status` **无专属 Reply POJO**；SDK 既有 `NoOutputReply` 约定**不承载** `output.status`（其 Javadoc 已声明调用方从 `ReplyEnvelope.data.result` 判断）→ **约定本身合规**；但同类 Job 指令 `return_home` 已在 A19 建 `ReturnHomeReply` → **一致性提请**（§5-51）。

#### A39-3 结论与新发现

1. **`ota_progress` 全字段已覆盖 ✓**（无需结构改动）。
2. **`ota_create` Request 存在但非「完全一致」**：`file_size` 类型（§5-48）、`firmware_upgrade_type` 枚举（§5-49）两处偏差。
3. **reply 无专属 POJO**（§5-51，既有约定下合规，一致性待裁决）。
4. **元数据偏差**：`@DocUrl` / `@Verified(basis)` 依据非 O4 页（§5-50）。
5. **文档自身无不一致**：example 值域正常（`file_size` `653467234` 在 int32 范围内；`firmware_upgrade_type` `3` 在值域内；`status` `in_progress`、`current_step` `download_firmware` 均在值域内）。
6. **修正既有表述**：§3.1.5 原「字段与枚举与 Dock3 完全一致 → 可直接复用」→ 已按本批修正（见 §3.1.5）。

**新登记 4 项（§5-48 ~ §5-51，均非阻断、待开发者裁决）**：见 §5 表与 §0 / §2 / §4。

---

### A40. O4 地面站远程日志页（`log.html`）原文（2026-10-14 提供）

**批次性质**：O4 地面站（`ground-station/o4/log.html`）远程日志原文，**Event 段 1 项（`fileupload_progress`）+ Service 段 3 项（`fileupload_list`/`fileupload_start`/`fileupload_update`，各含 `services_reply`）**。用途：核对 §3.1.6 结论、关闭 **v1.18 发布面最后一页**。

#### A40-1 `fileupload_progress`（events/up，need_reply=0）

| 项 | 文档 | SDK | 判定 |
|---|---|---|---|
| method | `fileupload_progress` | `EventMethod.FILEUPLOAD_PROGRESS`（`need_reply=0`） | ✓ |
| 结构 | `result` + `output{status?, ext{files[]{module, size, device_sn, key, fingerprint, progress{progress, finish_time, upload_rate}}}}` | **`FileUploadProgressData` 缺失**（`command/event/system/package-info.java` 悬空 `@link`） | **✗ 需新建**（§4） |
| `gateway` / `need_reply` | example 含 `gateway:"ground_station_sn"`、`need_reply:0` | `EventEnvelope` 有 gateway；`FILEUPLOAD_PROGRESS` need_reply=0 | ✓ |

- **文档字段表 vs example 不一致（§5-54）**：表 `output` 仅 `ext`（无 `status`）、`progress` 仅 3 字段；example `output` 含 `status:"ok"`、`progress` 7 字段（`current_step`/`finish_time`/`progress`/`result`/`status`/`total_step`/`upload_rate`）。

#### A40-2 `fileupload_list`（services/down + `services_reply`）

**Request**：`module_list`（array，文档 `item_type=enum_int`，example `["3"]` 字符串）→ SDK `FileUploadListRequest.moduleList`(`List<String>`) → **口径见 §5-53**。

**Reply**：`{files[]{device_sn, result(int), module(enum_int), list[]{boot_index(int), start_time(int,秒), end_time(int,秒), size(int)}}, result(int)}`

| 文档字段 | Type | SDK `FileUploadListReply` | 判定 |
|---|---|---|---|
| `files` | array | `List<FileGroup>` | ✓ |
| `»device_sn` | text | `String` | ✓ |
| `»result` | int | `int` | ✓ |
| `»module` | enum_int | `String` | 口径见 §5-53 |
| `»list` | array | `List<LogFile>` | ✓ |
| `»»boot_index` | int | `int` | ✓ |
| `»»start_time` | **int**（秒） | **`long`** | **✗（§5-52）** |
| `»»end_time` | **int**（秒） | **`long`** | **✗（§5-52）** |
| `»»size` | **int**（byte） | **`long`** | **✗（§5-52）** |
| 顶层 `result` | int | `int` | ✓ |

- 其余**结构一致 ✓**（`FileUploadListReply` 原 Javadoc 记 Dock3「毫秒」，O4 页单位**秒** → 见 §5-6/§5-52）。

#### A40-3 `fileupload_start`（services/down + `services_reply`）

**Request**：`bucket` / `region` / `credentials{access_key_id, access_key_secret, expire(int,ms), security_token}` / `endpoint` / `file_store_dir` / `file_type`(固定 `text_log`) / `provider`(enum_string `{ali,aws,minio}`) / `params{files[]{device_sn, object_key, module(**text**), result(int), list[]{boot_index, start_time(int,秒), end_time(int,秒), size(int)}}}`

| 文档字段 | SDK `FileUploadStartRequest` | 判定 |
|---|---|---|
| `bucket` / `region` / `endpoint` / `provider` | ✓ `String` | ✓ |
| `credentials.{access_key_id, access_key_secret, security_token}` | ✓ `String` | ✓ |
| `credentials.expire`（int，ms） | **`Long`** | **✗（§5-52，example 13 位超 int32）** |
| `file_store_dir` / `file_type` | **缺** | **✗ 需新增**（§4） |
| `params.files[].{sn→device_sn, object_key, module, result}` | **缺** `deviceSn`/`result`（`module`=✓`String`、`objectKey`=✓） | **✗ 需新增**（§4） |
| `params.files[].list[]{boot_index, start_time, end_time, size}` | 现仅 `bootIndex` | **✗ 需新增**（§4） |
| reply `{result}` | `NoOutputReply` 约定 | ✓ |

> 注：`fileupload_start` 页 `params.files[].module` 标 **`text`**（example `"8"`），与 `fileupload_list` 页 `enum_int` 口径不同 → §5-53。

#### A40-4 `fileupload_update`（services/down + `services_reply`）

**Request**：`status`(enum_string `{cancel}`) + `module_list`(array<text>)

| 文档字段 | SDK `FileUploadUpdateRequest` | 判定 |
|---|---|---|
| `status` | ✓ `String` | ✓ |
| `module_list` | ✓ `List<String>`（文档 `array<text>`） | ✓ |

- reply `{result}` → `NoOutputReply` ✓。**结构一致 ✓**。

#### A40-5 结论与新发现

1. **4 个 method 全部存在 ✓**；`fileupload_list`/`fileupload_update` 的 Request 与 `FileUploadListReply` 结构一致 ✓。
2. **`FileUploadStartRequest` 确缺** `file_store_dir`/`file_type`/`params.files[]` 子字段 → 落在 §4 清单内（≠新缺口）。
3. **`FileUploadProgressData` 确缺**（悬空 `@link`）→ 需新建（§4）。
4. **新登记 3 项（§5-52 ~ §5-54，均非阻断、待开发者裁决）**：
   - **§5-52**：`LogFile.startTime/endTime/size` 与 `Credentials.expire` 现 `long`/`Long` vs 文档 `int`；**关键**——毫秒口径 13 位超 int32 → **§5-6「统一 Integer」决策须复核/修订**。
   - **§5-53**：`module`/`module_list` 文档三口径不一（enum_int / text / example 字符串）；SDK 取 `String`；SDK 现无 `Module` 枚举（§3.1.6 原表述待核实）。
   - **§5-54**：`fileupload_progress` 文档字段表 vs example 不一致（`output.status` 表缺行；`progress` 子结构 3 vs 7 字段）→ 新建时裁决字段集。
5. **文档自身不一致**：`fileupload_start` 页 `module` 标 `text`；`fileupload_progress` 表/example 冲突（均已记录，不改 SDK）。

**至此 v1.18 发布面原文全部收齐**（O4 `properties`/`device`/`requests`/`cmd`/`drc`/`airspace-safety`/`firmware`/`log` + Dock3 FTS + Dock2/Dock3 `remote-control`）。

---

### A41. Dock3 航线管理页（`dock3/wayline.html`）完整原文（2026-10-14 提供）

**批次性质**：Dock3 航线管理页**完整**原文（**Event 5 项 + Service 14 项 + Requests 2 项**）。用途：①关闭 **A19 遗留的「Service 段截断」缺口**（A19 当时仅覆盖截断前的 11 条指令）；②首次给出 `flighttask_execute.multi_dock_task` 的**完整子结构**（`dock_infos[]`/`wireless_link_topo`），据此定位真实协议缺口；③批量 `@Verified` basis 由 simulator 升为官方文档（2026-10-14）。

> 注：本页此前记为「截断、A19 代」；本批为完整原文，与 A19 的 11 条不重复，仅**补充**截断段（`flighttask_execute` 及其子结构、`flighttask_create`、Requests 页）。

#### A41-1 Event 段 5 项（`events`/up）

| 文档 method | need_reply | SDK `EventMethod` | SDK POJO | 判定 |
|---|---|---|---|---|
| `return_home_info` | 0 | `RETURN_HOME_INFO`(99,0) ✓ | `ReturnHomeInfoData` ✓ | ✓（A32 已核，`@Verified` 待升） |
| `flighttask_ready` | 0 | `FLIGHTTASK_READY`(63,0) ✓ | `FlighttaskReadyData` ✓（`List<String> flightIds`） | ✓（basis=simulator 待升） |
| `flighttask_progress` | 1 | `FLIGHTTASK_PROGRESS`(56,1) ✓ | `FlighttaskProgressData` ✓ | ✓（A19 已核，字段全） |
| `device_exit_homing_notify` | 1 | `DEVICE_EXIT_HOMING_NOTIFY`(174,1) ✓ | `DeviceExitHomingNotifyData` ✓（`sn`/`action`/`reason`） | ✓，`@Inferred`（`reason` 文档 enum_int vs example 字符串，见 A41-4） |
| `in_flight_wayline_progress` | 1 | `IN_FLIGHT_WAYLINE_PROGRESS`(70,1) ✓ | `InFlightWaylineProgressData` ✓ | ✓（basis=simulator 待升） |

- **判定：Event 段 5 项全部已覆盖 ✓**（method 常量 + POJO 齐备）。

#### A41-2 Service 段 14 项（`services`/down + `services_reply`）

| # | 文档 method | SDK `ServiceMethod` | SDK Request POJO | 判定 |
|---|---|---|---|---|
| 1 | `flighttask_execute` | `FLIGHTTASK_EXECUTE`(56) ✓ | `FlighttaskExecuteRequest` ✓（`flightId` + `multiDockTask`） | **字段齐 ✓，子结构缺口见 §5-55** |
| 2 | `return_specific_home` | `RETURN_SPECIFIC_HOME`(192) ✓ | — | ✓（A19 已覆盖） |
| 3 | `flighttask_stop` | `FLIGHTTASK_STOP`(190) ✓ | `FlighttaskStopRequest` ✓ | ✓ |
| 4 | `return_home_cancel` | `RETURN_HOME_CANCEL`(66) ✓ | `NoParameterRequest` 复用 ✓ | ✓ |
| 5 | `return_home` | `RETURN_HOME`(64) ✓ | （`ReturnHomeReply` 计划新建，见 §4/§5-16） | ✓，Reply 落地待实现阶段 |
| 6 | `flighttask_recovery` | `FLIGHTTASK_RECOVERY`(60) ✓ | — | ✓ |
| 7 | `flighttask_pause` | `FLIGHTTASK_PAUSE`(58) ✓ | — | ✓ |
| 8 | `flighttask_undo` | `FLIGHTTASK_UNDO`(62) ✓ | `FlighttaskUndoRequest` ✓（`List<String> flightIds`） | ✓ |
| 9 | `flighttask_prepare` | `FLIGHTTASK_PREPARE`(54) ✓ | `FlighttaskPrepareRequest` ✓（字段全覆盖） | ✓ |
| 10 | `flighttask_create`（**文档标注「废弃」**） | **✗ 无 `FLIGHTTASK_CREATE`** | **✗ 无 `FlighttaskCreateRequest`/Reply** | **✗ 缺失（§5-56）** |
| 11 | `in_flight_wayline_deliver` | `IN_FLIGHT_WAYLINE_DELIVER`(216) ✓ | `InFlightWaylineDeliverRequest` ✓ | ✓ |
| 12 | `in_flight_wayline_stop` | `IN_FLIGHT_WAYLINE_STOP`(218) ✓ | — | ✓ |
| 13 | `in_flight_wayline_recover` | `IN_FLIGHT_WAYLINE_RECOVER`(220) ✓ | — | ✓ |
| 14 | `in_flight_wayline_cancel` | `IN_FLIGHT_WAYLINE_CANCEL`(222) ✓ | `NoParameterRequest` 复用 ✓ | ✓ |

- **判定：14 项中 13 项方法常量全存在 ✓**；**唯一缺失 = `flighttask_create`**（文档标注「废弃」，§5-56）。

#### A41-3 Requests 段 2 项

| 文档 method | SDK `RequestsMethod` | SDK Request/Reply | 判定 |
|---|---|---|---|
| `flighttask_progress_get` | `FLIGHTTASK_PROGRESS_GET`(65) ✓ | `FlighttaskProgressGetRequest`（多出必填 `flightId`，§5-57）/ `Reply`（`output` 为 `Object` 占位，§5-58） | **✗ 两处偏差（§5-57/§5-58）** |
| `flighttask_resource_get` | `FLIGHTTASK_RESOURCE_GET`(68) ✓ | （A19 已覆盖） | ✓ |

#### A41-4 结论与新发现

1. **Event 段 5 项全已覆盖 ✓**；**Service 段 14 项方法常量 13 项存在 ✓**（唯一缺失为废弃 `flighttask_create`）；**Requests 段 2 项 method 常量齐全 ✓**。
2. **真实协议缺口（本批首次项化，§5-55）**：`flighttask_execute.multi_dock_task` 子结构：
   - `DockInfo` 现仅 6 字段，**缺 4 项**：`heading`/`homePositionIsValid`/`rtcmInfo`/`alternateLandPoint`；
   - `CenterNode` 现仅 `sn`，**缺 `sdrId`**；
   - `LeafNode` 为**空 record `LeafNode()`**，**缺全部 3 字段**（`sdrId`/`sn`/`controlSourceIndex`）；
   - `RtcmInfo` record **不存在**（需新建，字段 `mountPoint`/`port`/`host`/`rtcmDeviceType`/`sourceType`）；
   - 可复用 `AlternateLandPoint`（已存在于 `telemetry/nested/`，**5 字段**：`longitude`/`latitude`/`height`/`safeLandHeight`/`isConfigured`）—— **更正**：本附录原记「4 字段」有误（漏 `height`）；经 A42 Dock2 页确认 SDK 已含全部 5 字段 ✓。注：Dock3 页 `alternate_land_point` 字段表**漏列 `height`**（示例含 `height:0`），Dock2 页字段表**含 `height`** → 文档自身不一致，SDK 无需改动。
3. **`flighttask_create`（废弃）完全缺失 → §5-56**：无 `ServiceMethod.FLIGHTTASK_CREATE`、无 POJO；须裁决定「补入并标 `@Deprecated`」或「按废弃策略维持缺失」。
4. **`FlighttaskProgressGetRequest` 多出文档外必填 `flightId` → §5-57**：文档请求 data **仅 `sn`**，SDK 却将 `flightId` 设为 `Objects.requireNonNull` 必填 → 删除或改可选。**（经 A42 修订）**：Dock2 页请求 data 实为 `target_sn`+`flight_id`，故 `flightId` **并非文档外字段**；核心问题转为 SDK `sn` 与 Dock2 wire key `target_sn` 错配 → 详见 §5-59。
5. **`FlighttaskProgressGetReply.output` 为 `Object` 占位 → §5-58**：文档已给出 `output{flight_id, progress{current_step(int), percent(int)}, status(text)}` → 可具体化为嵌套 record 并升 `@Verified`。
6. **关闭 A19「Service 段截断」缺口**：本批完整原文补齐 A19 截断段（`flighttask_execute` 及其子结构、`flighttask_create`、Requests 页），A19 遗留标记可结案。
7. **批量 `@Verified` basis 升级（列入 §4）**：`FlighttaskPrepareRequest`/`FlighttaskExecuteRequest`/`InFlightWaylineDeliverRequest`/`FlighttaskStopRequest`/`FlighttaskUndoRequest`/`FlighttaskReadyData`/`InFlightWaylineProgressData`/`FlighttaskProgressData`/`DeviceExitHomingNotifyData` 等，basis 由「simulator 已对接 hivemind 验证」升为「DJI Dock3 wayline 页 Data 表（2026-10-14）」，`@DocUrl` 统一指向 `dock3/wayline.html`。
8. **文档自身不一致（记录在案，不做 SDK 改动）**：
   - `device_exit_homing_notify.reason`：字段表 Type=`enum_int`，example 为字符串 `"0"`（SDK 按 `enum_int` 用 `int` + `@Inferred`）。
   - `in_flight_wayline_progress`：example 缺 `tid`/`bid`（与 `need_reply=1` 应含回复标识的惯例不符）→ 待 DJI 修订。

**新登记 4 项（§5-55 ~ §5-58，均非阻断、待开发者裁决）**：见 §5 表与 §0 / §2 / §4。

---

### A42. Dock2 航线管理页（`dock2/wayline.html`）原文（2026-10-14 提供）

**批次性质**：Dock2 航线管理页原文（**Event 5 项 + Service 14 项 + Requests 2 项**），与 A41（Dock3 同页）**同结构**。用途：①与 A41 逐项对照，确认 Dock2/Dock3 是否存在机型分叉；②核查 `flighttask_progress_get` 请求 data 的字段名（两页是否一致）；③强化 §5-51（`return_home` reply 一致性）。

> 结论先行：**Dock2 与 Dock3 页绝大多数一致**，仅 **2 处差异**（`flighttask_progress_get` 请求 data、`alternate_land_point` 字段表），且均与 SDK 现状存在交集 → 见 A42-4。

#### A42-1 Event 段 5 项（`events`/up）

| 文档 method | need_reply | SDK `EventMethod` | SDK POJO | 判定 |
|---|---|---|---|---|
| `return_home_info` | 0 | `RETURN_HOME_INFO`(99,0) ✓ | `ReturnHomeInfoData` ✓ | ✓（`plannedPathPoints`/`lastPointType`/`flightId`/`homeDockSn`/`multiDockHomeInfo{sn,planStatus,estimatedBatteryConsumption,homeDistance}` 全覆盖） |
| `flighttask_ready` | 0 | `FLIGHTTASK_READY`(63,0) ✓ | `FlighttaskReadyData` ✓（`List<String> flightIds`） | ✓ |
| `flighttask_progress` | 1 | `FLIGHTTASK_PROGRESS`(56,1) ✓ | `FlighttaskProgressData` ✓ | ✓（`ext`/`break_point`/`progress` 全字段；`wayline_mission_state` enum_int 10 值、`wayline_id` in ext 均覆盖） |
| `device_exit_homing_notify` | 1 | `DEVICE_EXIT_HOMING_NOTIFY`(174,1) ✓ | `DeviceExitHomingNotifyData` ✓（`sn`/`action`/`reason`） | ✓，`@Inferred`（`reason` 文档 enum_int vs example 字符串 `"0"`，与 Dock3 页同） |
| `in_flight_wayline_progress` | 1 | `IN_FLIGHT_WAYLINE_PROGRESS`(70,1) ✓ | `InFlightWaylineProgressData` ✓（`inFlightWaylineId`/`progress`/`status`/`result`/`wayPointIndex`） | ✓（example 缺 `bid`/`tid`，与 Dock3 页同） |

- **判定：Event 段 5 项全部已覆盖 ✓**，与 Dock3（A41-1）逐项一致。

#### A42-2 Service 段 14 项（`services`/down + `services_reply`）

| # | 文档 method | SDK `ServiceMethod` | SDK Request POJO | 判定 |
|---|---|---|---|---|
| 1 | `flighttask_execute` | `FLIGHTTASK_EXECUTE`(56) ✓ | `FlighttaskExecuteRequest` ✓ | 字段齐 ✓；**`multi_dock_task` 子结构缺口同 A41（§5-55）** |
| 2 | `return_specific_home` | `RETURN_SPECIFIC_HOME`(192) ✓ | — | ✓（`home_dock_sn`） |
| 3 | `flighttask_stop` | `FLIGHTTASK_STOP`(190) ✓ | `FlighttaskStopRequest` ✓（`flightId`/`reason` enum_int `{0,1}`） | ✓ |
| 4 | `return_home_cancel` | `RETURN_HOME_CANCEL`(66) ✓ | `NoParameterRequest` 复用 ✓（data=null） | ✓ |
| 5 | `return_home` | `RETURN_HOME`(64) ✓ | （`ReturnHomeReply` 计划新建，见 §4/§5-16） | ✓；**reply `{result, output{status}}` 经 Dock2 页确认** → 强化 §5-51 |
| 6 | `flighttask_recovery` | `FLIGHTTASK_RECOVERY`(60) ✓ | — | ✓（data=null） |
| 7 | `flighttask_pause` | `FLIGHTTASK_PAUSE`(58) ✓ | — | ✓（data=null） |
| 8 | `flighttask_undo` | `FLIGHTTASK_UNDO`(62) ✓ | `FlighttaskUndoRequest` ✓（`flightIds`） | ✓ |
| 9 | `flighttask_prepare` | `FLIGHTTASK_PREPARE`(54) ✓ | `FlighttaskPrepareRequest` ✓ | ✓（`flightId`/`executeTime`/`taskType`/`file{url,fingerprint}`/`readyConditions`/`executableConditions{storageCapacity}`/`breakPoint`/`rthAltitude`/`rthMode`/`outOfControlAction`/`exitWaylineWhenRcLost`/`waylinePrecisionType`/`simulateMission`/`flightSafetyAdvanceCheck` 全覆盖） |
| 10 | `flighttask_create`（**文档标注「废弃」**） | **✗ 无 `FLIGHTTASK_CREATE`** | **✗ 无 POJO** | **✗ 缺失（§5-56）** |
| 11 | `in_flight_wayline_deliver` | `IN_FLIGHT_WAYLINE_DELIVER`(216) ✓ | `InFlightWaylineDeliverRequest` ✓ | ✓ |
| 12 | `in_flight_wayline_stop` | `IN_FLIGHT_WAYLINE_STOP`(218) ✓ | — | ✓ |
| 13 | `in_flight_wayline_recover` | `IN_FLIGHT_WAYLINE_RECOVER`(220) ✓ | — | ✓ |
| 14 | `in_flight_wayline_cancel` | `IN_FLIGHT_WAYLINE_CANCEL`(222) ✓ | `NoParameterRequest` 复用 ✓（data=`{}`） | ✓ |

- **判定：14 项中 13 项方法常量存在 ✓**（唯一缺失 = 废弃 `flighttask_create`），与 Dock3（A41-2）一致。

#### A42-3 Requests 段 2 项

| 文档 method | SDK `RequestsMethod` | 文档请求 data | SDK Request | 判定 |
|---|---|---|---|---|
| `flighttask_progress_get` | `FLIGHTTASK_PROGRESS_GET`(65) ✓ | **`target_sn` + `flight_id`**（Dock3 页仅 `sn`） | `FlighttaskProgressGetRequest(String flightId, String sn)` | **✗ 字段名错配（§5-59）** |
| `flighttask_resource_get` | `FLIGHTTASK_RESOURCE_GET`(68) ✓ | `flight_id`；reply `{result, output{file{url, fingerprint}}}` | （A19 已覆盖） | ✓ |

- **关键差异**：`flighttask_progress_get` 请求 data —— **Dock2 页为 `target_sn`+`flight_id`**（含 example，wire key=`target_sn`），而 **Dock3 页仅 `sn`**（A41）；两页**不一致**。
- `flighttask_progress_get` **reply** Dock2 = Dock3：`{result, output{flight_id, progress{current_step(int), percent(int)}, status(text)}}` → 二次确认 §5-58 可具体化。

#### A42-4 结论与新发现

1. **Event 5 项 / Service 14 项 / Requests 2 项 —— 方法常量与 POJO 基本全已覆盖 ✓**（唯一缺失为废弃 `flighttask_create`，同 A41）。
2. **Dock2 与 Dock3 两页仅 2 处差异**：
   - **`flighttask_progress_get` 请求 data**：Dock2 = `target_sn`+`flight_id`；Dock3 = 仅 `sn` → 新登记 **§5-59**，并**修订 §5-57**。
   - **`alternate_land_point` 字段表**：Dock2 含 `height`（5 字段）；Dock3 字段表漏列 `height`（示例含 `height:0`）→ **文档自身不一致**；SDK `telemetry/nested/AlternateLandPoint` **已含全部 5 字段 ✓**，**无改动**。
3. **真实缺口（含反序列化风险）→ §5-59**：SDK `FlighttaskProgressGetRequest.sn`（JSON key `sn`）与 Dock2 example 的 `target_sn` **不匹配** → Dock2 蛙跳场景下 `target_sn` 反序列化丢失；且 `flightId` 在 Dock2 页**有定义**（原 §5-57 判为「文档外字段」**不成立**）。
4. **强化既有结论**：`return_home` reply `{result, output{status}}` 经 Dock2 页确认 → 强化 **§5-51**（`return_home` vs `ota_create` 专属 Reply 一致性裁决）；`FlighttaskProgressGetReply` 结构经 Dock2 二次确认 → 强化 **§5-58**。
5. **`multi_dock_task` 缺口二次印证**：Dock2 页与 Dock3 页字段表**完全一致** → §5-55 缺口（`DockInfo` 缺 4 字段/`CenterNode` 缺 `sdrId`/`LeafNode` 空/`RtcmInfo` 缺失）**两页共同确认**，非 Dock3 专属。
6. **更正 A41-4**：`AlternateLandPoint` 实为 **5 字段**（含 `height`），A41-4 原记「4 字段」有误（已修）。
7. **文档自身不一致（记录在案，不做 SDK 改动）**：①`device_exit_homing_notify.reason` enum_int vs example 字符串（A41/A42 同）；②`in_flight_wayline_progress` example 缺 `bid`/`tid`（A41/A42 同）；③Dock3 页 `alternate_land_point` 字段表漏 `height`（示例含）；④`flighttask_progress_get` 请求 data 跨页不一致。

**新登记 1 项（§5-59，非阻断、含反序列化风险、待开发者裁决）**：见 §5 表与 §0 / §2 / §4；并修订 §5-57。

---

### A43. 支持产品列表与固件版本页（`overview/product-support.html`）原文（2026-10-14 提供）

**批次性质**：开发者提供的 DJI「支持产品列表与固件版本」页原文 —— **即 `DeviceCompatibility` 的 `@DocUrl` 所指页**。用途：**结案 §5-12**（兼容矩阵来源）。特征：该页为 **「产品 + 最低固件版本」表**，**非 dock↔drone 逐对搭配矩阵**；共 **2 张表**（DJI Pilot 2 上云 / 机场上云）。

> 说明：本页标题为「支持产品列表与固件版本」，Path 属 `overview/`（非 `api-reference/`）。表格中「固件版本」为**能支持开发的最低版本**；固件需另行从大疆官网下载。

#### A43-1 DJI Pilot 2 上云

| 产品 | 固件版本 |
|---|---|
| DJI Pilot 2 | 10.1.0.30 |
| Matrice 350 RTK | 10.01.00.09 |
| Matrice 30/30T | 10.01.00.14 |
| Matrice 300 RTK（搭配 DJI RC Plus） | 60.01.00.03 |
| Matrice 300 RTK（搭配 DJI 带屏遥控器行业版） | 09.00.05.03 |
| DJI RC Plus | 05.02.11.06 |
| DJI 带屏遥控器行业版 | 09.00.05.01 |
| DJI Matrice 4E/4T | 12.00.01.06 |
| DJI RC Plus 2 | 01.41.02.06 |
| Mavic 3E/3T | 10.01.00.14 |
| DJI RC Pro 行业版 | 02.01.05.07 |

#### A43-2 机场上云

| 产品 | 固件版本 |
|---|---|
| DJI Matrice 4D/4TD | 13.01.00.02 |
| 大疆机场 3 | 13.01.00.02 |
| Matrice 3D/3TD | 10.01.32.05 |
| DJI RC Pro 行业版 | 02.01.05.15 |
| 大疆机场 2 | 10.01.32.05 |
| Matrice 30/30T | 10.01.0014 |
| DJI RC Plus | 05.02.1106 |
| 大疆机场 | 10.01.0205 |

#### A43-3 结论与裁决

1. **§5-12 结案**：官方 Cloud API **API 参考**页（`device.html`）确无兼容矩阵（原判正确）；本页（`overview/product-support.html`）给出**支持产品清单**。对 `DeviceCompatibility` 的裁决：**Dock↔Drone 搭配无需新增/修改 ✓** —— 机场上云产品族与 SDK 现有搭配**吻合**：
   - 大疆机场 3（Dock3）↔ M4D/4TD ✓（且**未**列 M30/M30T → 印证 Dock3 不支持 M30 系列 ✓）
   - 大疆机场 2（Dock2）↔ M3D/3TD + M30/30T ✓
   - 大疆机场（Dock1）↔ M30/30T ✓
2. **新决策点 §5-60**：本页含**各产品最低固件版本**，SDK `DeviceCompatibility` **不含固件版本** → 是否纳入 SDK 待裁决（新增功能）。
3. **新决策点 §5-61**：`RC Pro 行业版` **同时**列于「DJI Pilot 2 上云」（02.01.05.07）与「机场上云」（02.01.05.15）→ 暗示其亦用于 Dock 侧机型；SDK `isCompatible(RC_PRO, drone)` 现仅映射 `MAVIC_3E`/`MAVIC_3T`。**但本页未给逐对搭配**，不能据产品列表推断 → 待开发者确认后再决定是否扩展（AGENTS §3-11 禁止猜测）。
4. **格式待核对（来源粘贴疑缺损，非 SDK 问题）**：机场上云表中 3 处版本号疑似**小数点缺失**——`Matrice 30/30T 10.01.0014`（疑 `10.01.00.14`）、`DJI RC Plus 05.02.1106`（疑 `05.02.11.06`）、`大疆机场 10.01.0205`（疑 `10.01.02.05`）。若 §5-60 选「纳入固件版本」，需以官方为准逐字复核。
5. **`@Verified` 加固（候选）**：`DeviceCompatibility` 现 `@Verified(basis=「DJI Cloud API 官方文档产品搭配关系…」)`；可将 basis 具体化为「`overview/product-support.html` 支持产品表（2026-10-14）」，使来源可追溯（列入 §4，待 §5-12 裁决一并落地）。

**新登记 2 项（§5-60、§5-61，非阻断、待开发者裁决）**：见 §5 表与 §0 / §2 / §4。

---

### A44. WPML「航线文件共用元素信息」页（`dji-wpml/common-element.html`）原文（2026-10-14 提供）

**批次性质**：开发者提供 DJI **WPML**「共用元素」（`template.kml` 与 `waylines.wpml` 共用字段）页原文 —— **即 SDK `wayline` 模型的 `@DocUrl` 所指页**（类上标注 `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html`）。用途：审计 **sdk 模块 `wayline/`（WPML model + enumtype）** 的字段/枚举覆盖。

> 说明：本页仅含**共用元素**；`template.kml` 专有字段另见「template.kml 说明」页、`waylines.wpml` 专有字段另见「waylines.wpml 说明」页（本轮未涉及）。

#### A44-1 覆盖汇总（元素组 → SDK 类）

| 文档元素组 | SDK 类 | 结果 |
|---|---|---|
| `wpml:droneInfo` | `wayline/model/DroneInfo` | ✅ |
| `wpml:payloadInfo` | `wayline/model/PayloadInfo` | ✅ |
| `wpml:payloadParam` | `wayline/model/PayloadParam` | ✅ |
| `wpml:waypointHeadingParam` / `wpml:globalWaypointHeadingParam` | `WaypointHeadingParam` / `GlobalWaypointHeadingParam` | ✅ |
| `wpml:waypointTurnParam` | `WaypointTurnParam` | ✅ |
| `wpml:autoRerouteInfo` | `AutoRerouteInfo` | ✅ |
| `wpml:actionGroup` | `ActionGroup` | ✅ |
| `wpml:actionTrigger` | `ActionTrigger` | ✅ |
| `wpml:action` | `Action` | ✅ |
| `wpml:actionActuatorFuncParam`（16 动作） | `wayline/model/action/` 下 16 个 param record | ✅ |
| 字段枚举（trigger/turn/heading/gimbal/focus/metering/scanning/return 等） | 对应 `wayline/enumtype/*` | ✅ 值域吻合 |

#### A44-2 字段数核对（逐类与文档一致）

| 动作/元素 | 文档字段数 | SDK 字段数 | 判定 |
|---|---|---|---|
| `orientedShoot` | 28 | 28（`OrientedShootParam`） | ✅ |
| `accurateShoot` | 26 | 26（`AccurateShootParam`） | ✅ |
| `megaphone` | 10 | 10（`MegaphoneParam`） | ✅ |
| `searchlight` | 4 | 4（`SearchlightParam`） | ✅ |
| `payloadParam` | 9 | 9（`PayloadParam`） | ✅ |
| `waypointHeadingParam` | 4 | 4（`WaypointHeadingParam`：`waypointHeadingMode`/`waypointHeadingAngle`(Double)/`waypointPoiPoint`/`waypointHeadingPathMode`） | ✅ |
| `recordPointCloud` | 2 | 2（`RecordPointCloudParam`） | ✅ |

→ **无缺失类、无缺失字段、类型一致**（`focusX`/`focusRegionWidth`/`imageWidth` 等 px 整型 → `int`；`targetAngle`/`focalLength`/`*ShutterTime` → `double`；`payloadLensIndex`/`imageFormat`/`orientedPhotoMode` → `String`）。

#### A44-3 记录（2 项，均**无需改代码**）

1. **WPML 布尔字段 `int` 承载口径（§5-62）**：文档将 `dewarpingEnable`/`modelColoringEnable`/`isPointFocus`/`isInfiniteFocus`/`gimbal*RotateEnable`/`useGlobalPayloadLensIndex`/`accurateFrameValid`/`missionAutoRerouteMode`/`transitionalAutoRerouteMode`/`megaphoneOperateLoop` 等标为**布尔型**（取值 **0/1**），SDK **统一以 `int`/`Integer` 承载** —— 与 **WPML XML 线格式（`0`/`1`）一致**；若改 `Boolean` 将序列化为 `true`/`false`，**反而不符协议** → **维持现状正确**。建议在 architecture-design.md 记录**分层差异**：WPML 层 `bool→int`、MQTT 层 `bool→Boolean`。
2. **枚举超集（§5-63）**：`ActionActuatorFunc` 含 **16 值**（文档 `wpml:actionActuatorFunc` 列表**仅 14**，未列 `megaphone`/`searchlight`），`ActionGroupMode` 含 `PARALLEL`（`@Inferred`，文档仅列 `sequence`）→ **不改 SDK**。

#### A44-4 文档自身缺口（记录在案）

- `wpml:actionActuatorFunc` **枚举列表未包含** `megaphone`/`searchlight`，但同页**另设** `#megaphone`、`#searchlight` 参数表 → 属**文档枚举列表缺口**，SDK 已补全（正确）。
- `megaphoneFileBitrate` 文档列 1–6 档（注明**实际仅支持 4**）→ SDK 为 `int` 无值域约束；**可选**是否加枚举/校验（低优先，非字段差异）。
- 各类枚举「支持机型」列在文档中重复冗长（如 `focus` 段 6 次相同机型列表），属排版问题，不影响字段定义。

**登记 2 项记录（§5-62、§5-63，非阻断、无需改代码）**：见 §5 表与 §0 / §2 / §4。**SDK `wayline/` 无改动**。

---

### A45. WPML「template.kml 说明」页（`dji-wpml/template-kml.html`）原文（2026-10-14 提供）

**批次性质**：开发者提供 DJI **WPML**「template.kml 说明」页原文 —— **即 SDK `wayline` 模型（`Document`/`MissionConfig`/`Folder`/`Placemark` 等）的 `@DocUrl` 所指页**（`https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html`）。用途：审计 **template.kml 专有字段**（创建信息 / 任务信息 `missionConfig` / 模板信息 `Folder` 及各模板 Placemark）的覆盖。

> 说明：本页为 `template.kml` **专有**字段说明；两文件**共用元素**见 A44（`common-element.html`）；`waylines.wpml` 专有字段另见「waylines.wpml 说明」页（本轮未涉及）。

#### A45-1 覆盖汇总（页面区段 → SDK 类/字段）

| 文档区段（父元素） | SDK 类 | 结果 |
|---|---|---|
| 创建信息（`<Document>`）：`author`/`createTime`/`updateTime` | `Document`（`createTime`/`updateTime` = `Long`，承载 13 位 ms ✓） | ✅ |
| 任务信息（`<wpml:missionConfig>`）：`flyToWaylineMode`/`finishAction`/`exitOnRCLost`/`executeRCLostAction`/`takeOffSecurityHeight`/`globalTransitionalSpeed`/`globalRTHHeight`/`takeOffRefPoint`/`takeOffRefPointAGLHeight`/`droneInfo`/`payloadInfo`/`autoRerouteInfo` | `MissionConfig`（12 字段全匹配 ✓） | ✅ |
| 模板共用（`<Folder>`）：`templateType`/`templateId`/`autoFlightSpeed`/`waylineCoordinateSysParam`/`payloadParam` | 航点 `Folder` **缺 `payloadParam`**（建图 `MappingFolder` 已含 ✓） | ⚠️ §5-67 |
| 航点飞行模板（`<Folder>`）：`globalWaypointTurnMode`/`globalUseStraightLine`/`gimbalPitchMode`/`globalHeight`/`globalWaypointHeadingParam`/`Placemark` | `Folder` | ✅ |
| 航点信息（`<Placemark>`）：`isRisky`/`Point`/`index`/`useGlobalHeight`/`ellipsoidHeight`/`height`/`useGlobalSpeed`/`waypointSpeed`/`useGlobalHeadingParam`/`waypointHeadingParam`/`useGlobalTurnParam`/`waypointTurnParam`/`useStraightLine`/`gimbalPitchAngle` | `Placemark`（`isRisky`/`useStraightLine` **缺**） | ⚠️ §5-64 |
| 建图航拍模板（`<Placemark>`）：`caliFlightEnable`/`elevationOptimizeEnable`/`smartObliqueEnable`/`smartObliqueGimbalPitch`/`shootType`/`direction`/`margin`/`overlap`/`ellipsoidHeight`/`height`/`facadeWaylineEnable`/`Polygon`/`mappingHeadingParam`/`gimbalPitchMode`/`gimbalPitchAngle`/`quickOrthoMappingEnable`/`quickOrthoMappingPitch` | `Mapping2dPlacemark`（`quickOrthoMapping*` **缺**） | ⚠️ §5-65 |
| 倾斜摄影模板（`<Placemark>`）：`caliFlightEnable`/`inclinedGimbalPitch`/`inclinedFlightSpeed`/`shootType`/`direction`/`margin`/`overlap`/`ellipsoidHeight`/`height`/`Polygon` | `Mapping3dPlacemark`（10 字段全匹配 ✓） | ✅ |
| 航带飞行模板（`<Placemark>`）：`caliFlightEnable`/`shootType`/`direction`/`margin`/`singleLineEnable`/`cuttingDistance`/`boundaryOptimEnable`/`leftExtend`/`rightExtend`/`includeCenterEnable`/`overlap`/`ellipsoidHeight`/`height`/`stripUseTemplateAltitude`/`LineString` | `MappingStripPlacemark`（`margin` 类型**不符**） | ⚠️ §5-66 |
| 坐标系参数（`<wpml:waylineCoordinateSysParam>`）：`coordinateMode`/`heightMode`/`positioningType`/`globalShootHeight`/`surfaceFollowModeEnable`/`surfaceRelativeHeight` | `WaylineCoordinateSysParam`（6 字段全匹配 ✓） | ✅ |
| 重叠率（`<wpml:overlap>`）：8 个 `*OverlapH/W` | `Overlap`（8 字段全匹配 ✓） | ✅ |
| 建图航向参数（`<wpml:mappingHeadingParam>`）：`mappingHeadingMode`/`mappingHeadingAngle` | `MappingHeadingParam`（2 字段全匹配 ✓） | ✅ |

#### A45-2 确认差异（4 项）

1. **`Placemark`（航点）缺 `isRisky` / `useStraightLine`（§5-64）**：文档「航点信息」表列 `wpml:isRisky`（**布尔型**，是否危险点 0/1，仅 M30/M3D/M4D/M4E）、`wpml:useStraightLine`（**布尔型**，该航段是否贴合直线，受 `waypointTurnMode` 连续性条件约束，局部覆盖全局）；SDK `Placemark`（`Placemark.java:42-70`）13 字段**均无**。补则用 `Integer`（WPML 布尔口径 §5-62）。
2. **`Mapping2dPlacemark`（建图航拍）缺 `quickOrthoMappingEnable` / `quickOrthoMappingPitch`（§5-65）**：文档「建图航拍模板元素」表末列两项，**均仅 M4E**；SDK `Mapping2dPlacemark`（`Mapping2dPlacemark.java:38-69`）15 字段**均无**；`quickOrthoMappingPitch` 文档「类型」列疑似缺失（未明标整型/浮点型，仅给范围 `[10,30]`）。
3. **`MappingStripPlacemark.margin` 类型不符（§5-66）**：文档航带飞行 `wpml:margin` = **浮点型**（m）；SDK 为 **`int`**（`MappingStripPlacemark.java:46`）。同页 mapping2d/mapping3d `margin` 均为整型 → **仅航带飞行需改 `double`**。
4. **`Folder`（航点）缺 `payloadParam`（§5-67）**：文档「模板共用的元素」表列 `wpml:payloadParam`（负载设置）；SDK 航点 `Folder`（`Folder.java:38-59`）**无**，但建图类 `MappingFolder`（`MappingFolder.java:50-51`）**已含 ✓**。**文档自身不一致**：航点飞行**示例 XML 未含** `payloadParam`，且 `PayloadParam` Javadoc 自述限「建图航拍、倾斜摄影、航带飞行」→ 层级归属待开发者确认（**倾向现状正确**）。

#### A45-3 核对一致项（无需改动）

- **`Document`**：`createTime`/`updateTime` = `Long` —— 文档标「整型」但示例为 13 位 ms（`1637600807044`/`1637600875837`）**超 int32** → SDK `Long` **正确**（印证 §5-6 复核方向）；`author`(String) ✓。
- **`MissionConfig`** 12 字段匹配：枚举字段（`flyToWaylineMode`/`finishAction`/`exitOnRCLost`/`executeRCLostAction`）为 `String`；`takeOffSecurityHeight`/`globalTransitionalSpeed`/`globalRTHHeight`/`takeOffRefPointAGLHeight` 为 `Double`；`takeOffRefPoint` 为 `String`；`droneInfo`/`payloadInfo`/`autoRerouteInfo` 嵌套 ✓。
- **`Folder`（航点）** 其余 10 字段匹配（`globalUseStraightLine` = `Integer`，按 §5-62）✓。
- **`WaylineCoordinateSysParam`** 6 字段匹配：`globalShootHeight`(Double)、`surfaceFollowModeEnable`(Integer)、`surfaceRelativeHeight`(Double) ✓。
- **`Overlap`** 8 字段（`orthoLidarOverlapH/W`、`orthoCameraOverlapH/W`、`inclinedLidarOverlapH/W`、`inclinedCameraOverlapH/W`，均 `Integer`）✓。
- **`MappingHeadingParam`** 2 字段（`mappingHeadingMode`(String)/`mappingHeadingAngle`(Integer)）✓。
- **`Mapping3dPlacemark`** 10 字段：`inclinedGimbalPitch`(int)、`inclinedFlightSpeed`(double)、`margin`(int，文档整型 ✓) ✓。
- **`PayloadParam`**（`Folder`/`MappingFolder` 共用元素）9 字段 ✓（A44 已确认）。
- **枚举全对齐**：`HeightMode` **4 值**（`EGM96`/`relativeToStartPoint`/`aboveGroundLevel`/`realTimeFollowSurface`）、`PositioningType` **4 值**（`GPS`/`RTKBaseStation`/`QianXun`/`Custom`）、`CoordinateMode`（`WGS84`）✓。
- **`Folder` 全局 `globalUseStraightLine` 与 `Placemark` 局部 `useStraightLine` 的对应**：SDK 有全局、缺局部（§5-64）。

#### A45-4 文档自身缺口（记录在案）

- **`payloadParam` 层级歧义（§5-67）**：「模板共用的元素（父元素 `<Folder>`）」表列有 `payloadParam`，但航点示例 XML 未含，且 SDK 分置（航点无 / 建图 `MappingFolder` 有）→ 属**文档表列与示例不一致**。
- **`quickOrthoMappingPitch`「类型」列缺失（§5-65）**：文档仅给单位/范围（`°`、`[10,30]`），未明标整型/浮点型（同页 `gimbalPitchAngle` 类型列亦缺失）。
- 各元素「支持机型」列重复冗长，属排版问题，不影响字段定义。

**登记 4 项（§5-64~67，均非阻断、待开发者裁决）**：见 §5 表与 §0 / §2 / §4。**A45 为审计记录，未写代码（遵守「原文收齐前不编码」硬约束）**。

---

### A46. WPML「waylines.wpml 说明」页（`dji-wpml/waylines-wpml.html`）原文（2026-10-14 提供）

**批次性质**：开发者提供 DJI **WPML**「waylines.wpml 说明」页原文 —— **即 SDK `wayline/model/execute/`（`ExecuteFolder`/`ExecutePlacemark`）与 `ExecuteHeightMode` 的 `@DocUrl` 所指页**（`https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/waylines-wpml.html`）。`waylines.wpml` 是**飞机直接执行**的文件（由 template.kml 生成），用途：审计 **waylines.wpml 专有字段**（`waylineId`/`executeHeightMode`/`executeHeight`/`startActionGroup`等）的覆盖。

> 关系：`waylines.wpml` 由 `template.kml`（A45）生成；两文件**共用元素**见 A44（`common-element.html`）。

#### A46-1 覆盖汇总（页面区段 → SDK 类/字段）

| 文档区段（父元素） | SDK 类 | 结果 |
|---|---|---|
| 任务信息（`<wpml:missionConfig>`）：`flyToWaylineMode`/`finishAction`/`exitOnRCLost`/`executeRCLostAction`/`takeOffSecurityHeight`/`globalTransitionalSpeed`/`globalRTHHeight`/`droneInfo`/`payloadInfo`/`autoRerouteInfo` | `MissionConfig`（同 A45，12 字段 ✓） | ✅ |
| 航线信息（`<Folder>`）：`templateId`/`waylineId`/`autoFlightSpeed`/`executeHeightMode`/`Placemark`/`startActionGroup` | `ExecuteFolder`（`startActionGroup` **缺**） | ⚠️ §5-68 |
| 航点信息（`<Placemark>`）：`isRisky`/`Point`/`index`/`executeHeight`/`waypointSpeed`/`waypointHeadingParam`/`waypointTurnParam`/`useStraightLine` | `ExecutePlacemark`（`isRisky`/`useStraightLine` **缺**） | ⚠️ §5-69 |
| waylines.wpml `<Document>` 可含**多个 `Folder`**（倾斜摄影 5 条航线） | `Kml<ExecuteFolder>` **单数**（仅存 1 个） | ⚠️ §5-70 |

#### A46-2 确认差异（4 项）

1. **`ExecuteFolder` 缺 `startActionGroup`（§5-68）**：文档「航线信息」表列 `wpml:startActionGroup`（**航线初始动作**，航线开始前执行；中断恢复时先执行初始动作再执行航点动作；引用共用元素 `<wpml:actionGroup>`）；SDK `ExecuteFolder`（`ExecuteFolder.java:37-49`）仅 `templateId`/`executeHeightMode`/`waylineId`/`autoFlightSpeed`/`placemarks` **5 字段**，**缺**。
2. **`ExecutePlacemark` 缺 `isRisky` / `useStraightLine`（§5-69）**：文档「航点信息」表列两项（均**布尔型**）；SDK `ExecutePlacemark`（`ExecutePlacemark.java:43-61`）8 字段**均无**；与 **§5-64（template.kml `Placemark`）同源**，执行层同样缺失。
3. **waylines.wpml 多 `Folder` 支持（§5-70）**：文档「文件介绍」明确「**倾斜摄影模板将生成 5 条可执行航线，对应 waylines.wpml 内的 5 个 `Folder` 元素**」；SDK `ParsedKmz.waylines`（`ParsedKmz.java:33`）= `Kml<ExecuteFolder>`（**单数**）→ **多 Folder 仅保留 1 个**，属潜在协议缺口。
4. **`ExecutePlacemark.gimbalPitchAngle` 层级（§5-71）**：文档该页「航点信息」表**未列** `gimbalPitchAngle`（示例亦未含），但该页 `Placemark(Point)` 行注明引用 template.kml 的 Placemark（A45 该表**含** `gimbalPitchAngle`）；SDK **含**（`ExecutePlacemark.java:52-53`）→ 文档表述差异，**倾向保留**。

#### A46-3 核对一致项（无需改动）

- **`ExecuteFolder`** 4 字段匹配：`templateId`(Integer)、`executeHeightMode`(String)、`waylineId`(Integer)、`autoFlightSpeed`(Double) ✓；且正确**移除**了 template 层的 `waylineCoordinateSysParam`/`gimbalPitchMode`/`globalHeight` 等 ✓。
- **`ExecutePlacemark`** 6 字段匹配：`point`/`index`/`executeHeight`(Double，浮点型 m ✓)/`waypointSpeed`(Double)/`waypointHeadingParam`/`waypointTurnParam`/`actionGroups` ✓；且正确**移除**了 template 层的 `useGlobalXxx`/`ellipsoidHeight`/`height` ✓（Javadoc 说明 `height`→`executeHeight`）。
- **`ExecuteHeightMode`** 枚举 **3 值**匹配：`WGS84`/`relativeToStartPoint`/`realTimeFollowSurface` ✓ —— 文档 `executeHeightMode` **仅列此 3 值**（**不含** `aboveGroundLevel`/`EGM96`，与 template 层 `HeightMode` 的 4 值**有意不同**）→ SDK 正确区分两层 ✓。
- **`WaypointHeadingParam` / `WaypointTurnParam`**：两 `@DocUrl` 均指 waylines-wpml.html，作为展开后的航点参数复用 ✓（A44 已确认字段）。
- **`missionConfig`**：与 template.kml 同一 `MissionConfig` 记录复用 ✓（A45 已确认 12 字段）。

#### A46-4 文档自身缺口（记录在案）

- **多 `Folder` 与 SDK 单体模型冲突（§5-70）**：文档明确倾斜摄影生成 5 个 `Folder`，SDK 单数模型无法承载 → 属**模型设计待裁决**。
- **`gimbalPitchAngle` 表列缺失（§5-71）**：waylines.wpml 该页「航点信息」表未列 `gimbalPitchAngle`，但引用 template Placemark（含之）→ 表述不一致。
- missionConfig 示例未含 `takeOffRefPoint`/`takeOffRefPointAGLHeight`/`globalRTHHeight`，但字段表列出 → 属示例省略，不影响定义。

**登记 4 项（§5-68~71，均非阻断、待开发者裁决）**：见 §5 表与 §0 / §2 / §4。**A46 为审计记录，未写代码（遵守「原文收齐前不编码」硬约束）**。

---

### A47. DJI Cloud-API-2 SDK 风格对齐：协议分类清单 + DRC 命名差异样例（2026-10-14 决策 → A）

> **依据**：§0「DJI Cloud-API-2 SDK 风格对齐指示」——开发者指示 DJI `github.com/dji-sdk/Cloud-API-2/tree/main/sdk` 与本 SDK 属**包含关系**，本 SDK 应「尽量往 DJI 上云 API2 SDK 风格去套」；**对齐范围裁定＝协议模型命名与结构**（保留 record/不可变/零依赖/Jackson 架构）。
> **本节性质**：比对**口径校准样例**（分类清单 + DRC 一类命名差异示例），供后续逐分类比对复用。**未写代码**（遵守「原文收齐前不编码」硬约束）。
> **比对基线**：DJI `sdk/deps/message_engine/src/cloud_message_manager/message_define_user/`（C++ 结构体，snake_case 字段 + `AIGC_JSON_HELPER`，`method_` 注册 wire 字符串）；本 SDK `protocol/method/*` + `command/**`。

#### A47-1 分类清单（DJI `message_define_user` → 本 SDK 载体）

DJI 协议定义分两层目录：

| 层 | 目录 | 内容 |
|---|---|---|
| 脚本生成 | `.../message_define_user/generated/` | 每 wire method 一个 `.h/.cpp`，文件名 = `<分类>_<wireMethod>_<方向>` |
| 手写 | `.../message_define_user/custom/` | osd / state / property / 压缩摇杆 / 空域告警等特殊消息（非逐方法生成） |

**`generated/` 分类清单**（仅计 `.h`，共 475 文件）：

| DJI 分类前缀 | 文件数 | 命名形态 | 本 SDK 对应载体 |
|---|---|---|---|
| `drc` | 227 | `drc_<method>_<dir>`；wire method 自带 `drc_` 前缀者文件名成 `drc_drc_...` | `DrcMethod`（down 42）+ `DrcUpMethod`（up 12）+ `command/drc/**` |
| `services` | 150 | `services_<method>_<dir>` | `ServiceMethod` + `command/service/**` |
| `events` | 73 | `events_<method>_<dir>` | `EventMethod` + `command/event/**` |
| `requests` | 22 | `requests_<method>_<dir>` / `requests_reply_<method>_<dir>` | `RequestsMethod` + `command/request/**` |
| `status` | 3 | `status_<method>_<dir>`（`static_topo` / `update_topo` / `reply_update_topo`） | `StatusMethod` + `protocol/envelope` |

**`custom/` 清单**（手写）：

| DJI 文件 | wire method | 本 SDK 对应载体 |
|---|---|---|
| `osd_northbound` | `osd` | `StatusMethod` + `OsdField` / `DockOsd` / `DroneOsd` / `RcOsd` |
| `state_northbound` / `state_southbound` | `state` | `StatusMethod` + `StateField` |
| `set_property_southbound` / `set_reply_property_northbound` | `property/set` / `property/set_reply` | `PropertySetMethod` + `PropertySetRequest` / `PropertySetReply` |
| `compressed_sticks_control_{south,north}bound` | `compressed_sticks_control` | **本 SDK 缺失**（§5-72） |
| `events_airsense_warning_northbound` / `events_reply_airsense_warning_southbound` | `airsense_warning` | `EventMethod` + `AirsenseWarningData` |

**方向 / 回包编码对照**：

| DJI 形态 | 含义 | 本 SDK 承载 |
|---|---|---|
| `<分类>_<method>_southbound` | 云→设备（下行指令 / 请求） | `DrcMethod` / `ServiceMethod` / `RequestsMethod`（request） |
| `<分类>_<method>_northbound` | 设备→云（上行推送） | `DrcUpMethod` / `EventMethod` / `StatusMethod`（push data） |
| `<分类>_reply_<method>_<dir>` | 回包 / ACK | `XxxReply` record（`command/**`） |

**命名形态总对照**：

| 维度 | DJI Cloud-API-2 | 本 SDK | 性质 |
|---|---|---|---|
| 分类载体 | 文件名前缀（`drc_`/`services_`/…） | 独立 enum 类（`XxxMethod`） | 结构差异 |
| 方向编码 | 文件名后缀 `_southbound`/`_northbound` | 拆两个 enum（down/up）+ topic | 结构差异 |
| 回包 | 独立结构 `reply_<method>_<dir>` | `XxxReply` record | 结构差异 |
| 通道前缀 | 保留（`drc_camera_aim`） | 跨通道去前缀（`camera_aim`） | **语义差异**（§5-74） |
| 类 / 结构体名 | PascalCase + `…Msg`（`DrcDrcCameraPhotoTakeSouthboundMsg`） | `XxxRequest` / `XxxReply` / `XxxData` | 命名差异 |
| 常量名 | 无（C++ 结构体） | `UPPER_SNAKE_CASE` 枚举常量 | 命名差异 |
| 字段名 | `snake_case`（struct 成员） | `camelCase`（Jackson SNAKE_CASE 映射） | 命名差异 |
| 可变性 | 可变 struct + `AIGC_JSON_HELPER` | record 不可变 | 架构差异（**保留**） |
| 序列化 | `AIGCJson` 宏 | Jackson | 架构差异（**保留**） |

#### A47-2 DRC 分类口径（227 文件拆分）

- **文件名 → 结构体**：`drc_drc_camera_photo_take_southbound` → `struct DrcDrcCameraPhotoTakeSouthboundMsg` + `class DrcDrcCameraPhotoTakeSouthbound`（`…Msg` 为数据体，类为消息壳）。
- **wire method 注册**：类构造函数内 `this->method_ = "drc_camera_photo_take"`（即**文件名去掉分类前缀与方向后缀＝wire 字符串**，故 `drc_` 双前缀其实是 `分类 drc_` + `wire drc_camera_photo_take`）。
- **文件计数**：`_southbound`（非 `reply_`）＝ **105** 下行 method；`reply_*_northbound` ＝ **104** 回包；`*_northbound`（非 `reply_`/`*_push`… 实为 `*` 非 reply）＝ **18** 推送。
- **本 SDK 对应量**：`DrcMethod` ＝ **42**（down）、`DrcUpMethod` ＝ **12**（up）。
- **覆盖率口径**：下行 105 vs 42 → 缺 **63**；推送 18 vs 12 → 缺 **6**。缺口方法**仅存于 DJI 仓库、未见于 v1.18 官方文档** → 是否纳入待裁决（§5-73）。

#### A47-3 DRC 命名差异样例（逐条，供校准比对口径）

**样例 1 · 通道前缀差异（核心，§5-74）**

| DJI 文件 | DJI wire（`method_`） | 本 SDK 常量 | 本 SDK wire | 差异 |
|---|---|---|---|---|
| `drc_drc_camera_photo_take_southbound` | `drc_camera_photo_take` | `ServiceMethod.CAMERA_PHOTO_TAKE` | `camera_photo_take` | DJI 保留 `drc_` 前缀；SDK 去前缀 |
| `drc_drc_camera_aim_southbound` | `drc_camera_aim` | `ServiceMethod.CAMERA_AIM` | `camera_aim` | 同上 |
| `drc_drc_camera_mode_switch_southbound` | `drc_camera_mode_switch` | `ServiceMethod.CAMERA_MODE_SWITCH` | `camera_mode_switch` | 同上 |
| `drc_drc_camera_focal_length_set_southbound` | `drc_camera_focal_length_set` | `ServiceMethod.CAMERA_FOCAL_LENGTH_SET` | `camera_focal_length_set` | 同上 |
| `drc_drc_gimbal_reset_southbound` | `drc_gimbal_reset` | `ServiceMethod.GIMBAL_RESET` | `gimbal_reset` | 同上 |

**样例 2 · 无前缀（飞行 / 心跳）—— 一致 ✓**

| DJI 文件 | DJI wire | 本 SDK 常量 | 一致性 |
|---|---|---|---|
| `drc_heart_beat_southbound` | `heart_beat` | `DrcMethod.HEART_BEAT` | ✓ |
| `drc_stick_control_southbound` | `stick_control` | `DrcMethod.STICK_CONTROL` | ✓ |
| `drc_drone_emergency_stop_southbound` | `drone_emergency_stop` | `DrcMethod.DRONE_EMERGENCY_STOP` | ✓ |

**样例 3 · 本 SDK 独有（DJI 仓库无对应文件）**

| DJI 文件 | 本 SDK 常量 | 说明 |
|---|---|---|
| （无） | `DrcMethod.DRONE_CONTROL`（wire `drone_control`） | DJI `generated/` 树中搜索 `drone_control` **命中 0** → SDK 独有；§4 已列 `DRONE_CONTROL → @Deprecated`，此处登记为「仓库无对应」事实 |

**样例 4 · 回包命名**

| DJI 文件 | DJI wire | 本 SDK | 差异 |
|---|---|---|---|
| `drc_reply_drc_camera_photo_take_northbound` | `drc_camera_photo_take` | `DrcResultReply`（**通用回包**） | DJI 每 method 独立 reply 结构；SDK 用统一 `DrcResultReply` |

**样例 5 · 上行推送（前缀保留）**

| DJI 文件 | DJI wire | 本 SDK 常量 | 一致性 |
|---|---|---|---|
| `drc_osd_info_push_northbound` | `osd_info_push` | `DrcUpMethod.OSD_INFO_PUSH` | ✓ |
| `drc_drc_camera_state_push_northbound` | `drc_camera_state_push` | `DrcUpMethod.DRC_CAMERA_STATE_PUSH` | ✓（前缀保留） |

**样例 6 · 字段类型差异（AI 框选坐标，§5-75）**

| 项 | DJI `DrcDrcAiSpotlightZoomSelectSouthboundMsg` | v1.18 官方文档（§5-45） | 本 SDK |
|---|---|---|---|
| 字段 / 类型 | `double center_x` / `double center_y` / `double height` / `double width`（默认 `0.0`） | Type = `int`、constraint `[0,10000]`、示例整数 | `AiSpotlightZoomSelectRequest`（已决策 `Integer`） |
| 结论 | ─ | **以文档为准** | **维持 `Integer`**（§0「冲突以 v1.18 文档为准」；冲突事实已登记 §5-75） |

**登记 4 项（§5-72~75，均非阻断、待开发者裁决）**：见 §5 表与 §0 / §2 / §4。**A47 为比对口径校准记录，未写代码（遵守「原文收齐前不编码」硬约束）**。
**后续**：按本清单对 `services` / `events` / `requests` / `status` / `custom` 各分类逐一产出同构差异样例 —— **已完成，见 A48**。

---

### A48. DJI Cloud-API-2 协议分类命名差异样例（续 A47：services / events / requests / status / custom）（2026-10-14）

> **依据**：与 A47 同源（DJI `dji-sdk/Cloud-API-2` → `sdk/deps/message_engine/src/cloud_message_manager/message_define_user/` 树：`generated/` 475 `.h` + `custom/` 9 `.h` + 根 `message_all.h`）。
> **wire 提取口径**：DJI 文件名＝`<分类>_<wire>_<方向>`；回包＝`<分类>_reply_<wire>_<方向>`；**分类前缀（`drc_`/`services_`/`events_`/`requests_`/`status_`）与方向后缀（`_southbound`/`_northbound`）均不进 wire**；wire 自身亦以分类词开头时文件名成双前缀（`drc_drc_*`、`requests_requests_*`）。
> **方向约定**：`_southbound`＝云→设备（down/请求）；`_northbound`＝设备→云（up/上报）；`reply_*` 为对端回包。

**A48-0 分类总表**

| 分类 | DJI `.h` 文件 | 拆分 | 本 SDK 载体 | SDK 常量 | 主要差异 |
|---|---|---|---|---|---|
| services | 150 | 75 request(`_southbound`) + 75 reply(`reply_*`) | `ServiceMethod` + `command/service/**` + `XxxReply` | 97 | **相机细项分类归属**（DJI 归 `drc`，SDK 归 `ServiceMethod`）；SDK legacy 独有；DJI 独有 ~35 项 |
| events | 73 | 38 非 reply + 35 reply | `EventMethod` + `command/event/**` | 29 | SDK 以 `need_reply` 承载回复语义（无独立 reply 常量）；DJI 每事件独立 `events_reply_*`；DJI 方向混列 |
| requests | 22 | 11 request(`_northbound`) + 11 reply(`reply_*`) | `RequestsMethod` + `command/request/**` | 8 | SDK 注册 4 项 DJI 本分类未见；DJI 独有 `offline_map_*`/`rt_agent_*` 等 |
| status | 3 | `static_topo` / `update_topo` / `reply_update_topo` | `StatusMethod` + `protocol/envelope` | 1 | SDK 仅 `update_topo`；DJI 另有 `static_topo` + 独立 reply |
| custom | 9 | osd(1) / state(2) / set_property(2) / compressed_sticks_control(2) / airsense(2) | `OsdField`+`DockOsd`/`DroneOsd`/`RcOsd`、`StateField`、`PropertySetMethod`+`PropertySetRequest/Reply`、`EventMethod`；`compressed_sticks_control` **无** | — | `osd`/`state`/`set_property` 为 **topic/数据模型**（不携带 wire method 字段）；`compressed_sticks_control` 缺失（§5-72） |

**A48-1 services 命名差异样例（代表性）**

| DJI 文件 | DJI wire | 本 SDK 常量 | 本 SDK wire | 差异 |
|---|---|---|---|---|
| `services_flighttask_prepare_southbound` | `flighttask_prepare` | `ServiceMethod.FLIGHTTASK_PREPARE` | `flighttask_prepare` | ✓ 一致 |
| `services_camera_focal_length_set_southbound` | `camera_focal_length_set` | `ServiceMethod.CAMERA_FOCAL_LENGTH_SET` | `camera_focal_length_set` | ✓ 一致 |
| `services_live_lens_change_southbound` | `live_lens_change` | `ServiceMethod.LIVE_LENS_CHANGE` | `live_lens_change` | ✓ 一致 |
| `services_return_specific_home_southbound` | `return_specific_home` | `ServiceMethod.RETURN_SPECIFIC_HOME` | `return_specific_home` | ✓ 一致 |
| `services_reply_flighttask_prepare_northbound` | `flighttask_prepare` | `FlighttaskPrepareReply`（record） | — | 回包：DJI 独立结构 vs SDK `XxxReply` |
| `services_open_parachute_southbound` | `open_parachute` | （SDK 无） | — | DJI 仓库独有（未见于 v1.18 文档） |
| `drc_drc_camera_photo_take_southbound`（**DJI 归 drc 分类**） | `drc_camera_photo_take` | `ServiceMethod.CAMERA_PHOTO_TAKE` | `camera_photo_take` | **分类归属 + 前缀差异**（§5-74 成因） |

分类特有结论：①services 通道 wire **无前缀**，与 SDK 一致 ✓；②相机细项（`camera_photo_take`/`camera_mode_switch`/`camera_aim`/`gimbal_reset`/`camera_frame_zoom`/`camera_recording_*`/`camera_exposure_*`/`camera_focus_*`/`camera_point_focus_action`/`ir_metering_*` 等）DJI 置于 **`drc` 分类**、SDK 置于 `ServiceMethod` → **分类归属差异**，即 §5-74「双通道命名」的直接来源。

**A48-2 events 命名差异样例（代表性）**

| DJI 文件 | DJI wire | 本 SDK 常量 | 差异 |
|---|---|---|---|
| `events_flighttask_progress_northbound` | `flighttask_progress` | `EventMethod.FLIGHTTASK_PROGRESS`（needReply=1） | ✓ 一致 |
| `events_fileupload_progress_southbound` | `fileupload_progress` | `EventMethod.FILEUPLOAD_PROGRESS`（needReply=0） | ✓ 一致 |
| `events_hms_northbound` | `hms` | `EventMethod.HMS`（needReply=0） | ✓ 一致 |
| `events_airsense_warning_northbound` | `airsense_warning` | `EventMethod.AIRSENSE_WARNING`（needReply=1） | ✓ 一致（DJI 归 `custom/`） |
| `events_reply_airsense_warning_southbound` | `airsense_warning` | （无独立 reply 常量） | 回包：DJI 独立结构 vs SDK `needReply` 字段 + `EventEnvelope` |
| `events_ai_object_detection_alert_northbound` | `ai_object_detection_alert` | （SDK 无） | DJI 仓库独有 |

分类特有结论：DJI events 文件**方向混列**（`_northbound` 上报与 `_southbound` 下发并存），SDK `EventMethod` **不编码方向**（由 topic + `needReply` 推断）；回复语义 DJI 用独立 `events_reply_*` 结构、SDK 用 `needReply` 字段承载 → 结构差异（保留）。

**A48-3 requests 命名差异样例（代表性）**

| DJI 文件 | DJI wire | 本 SDK 常量 | 差异 |
|---|---|---|---|
| `requests_storage_config_get_northbound` | `storage_config_get` | `RequestsMethod.STORAGE_CONFIG_GET` | ✓ 一致 |
| `requests_flighttask_resource_get_northbound` | `flighttask_resource_get` | `RequestsMethod.FLIGHTTASK_RESOURCE_GET` | ✓ 一致 |
| `requests_flight_areas_get_northbound` | `flight_areas_get` | `RequestsMethod.FLIGHT_AREAS_GET` | ✓ 一致 |
| `requests_requests_ai_model_url_northbound` | `requests_ai_model_url` | （SDK 无） | **双前缀**（分类 `requests_` + wire `requests_ai_model_url`）；SDK 无 |
| `requests_offline_map_get_northbound` | `offline_map_get` | （SDK 无） | DJI 仓库独有 |
| `requests_rt_agent_channel_open_northbound` | `rt_agent_channel_open` | （SDK 无） | DJI 仓库独有 |
| （DJI `requests` 分类无对应文件） | — | `RequestsMethod.CONFIG` / `AIRPORT_BIND_STATUS` / `AIRPORT_ORGANIZATION_GET` / `AIRPORT_ORGANIZATION_BIND` | SDK 独有（注册 4 项） |

分类特有结论：requests 通道 wire **无前缀** ✓；DJI 存在 **`requests_requests_`** 双前缀（同 `drc_drc_` 规律，印证「wire 自身可含分类词」）；SDK 注册 4 方法与 DJI `offline_map_*`/`rt_agent_*`/`request_landing_permission`/`in_flight_task_request` 互为差集。

**A48-4 status 命名差异样例（全量，仅 3 文件）**

| DJI 文件 | DJI wire | 本 SDK 常量 | 差异 |
|---|---|---|---|
| `status_update_topo_northbound` | `update_topo` | `StatusMethod.UPDATE_TOPO` | ✓ 一致 |
| `status_static_topo_northbound` | `static_topo` | （SDK 无） | DJI 独有 |
| `status_reply_update_topo_southbound` | `update_topo` | （无独立 reply 常量） | 回包结构差异 |

**A48-5 custom 命名差异样例（全量，仅 9 文件）**

| DJI 文件 | DJI wire / topic | 本 SDK 载体 | 差异 |
|---|---|---|---|
| `osd_northbound` | `osd`（topic） | `OsdField` + `DockOsd`/`DroneOsd`/`RcOsd` | ✓ 概念一致；SDK 以字段枚举 + 分设备模型建模 |
| `state_northbound` / `state_southbound` | `state`（topic） | `StateField` | ✓ 概念一致 |
| `set_property_southbound` | `set_property`（topic，**无 method 字段**） | `PropertySetRequest`（`@JsonAnyGetter` 扁平 map）+ `PropertySetMethod` | ✓ 概念一致；SDK 扁平 map（非 `{"properties":{}}` 包裹） |
| `set_reply_property_northbound` | `set_reply_property` | `PropertySetReply`（DELEGATING） | ✓ 概念一致 |
| `compressed_sticks_control_{south,north}bound` | `compressed_sticks_control` | （SDK 无） | **缺失**（§5-72） |
| `events_airsense_warning_northbound` / `events_reply_airsense_warning_southbound` | `airsense_warning` | `EventMethod.AIRSENSE_WARNING` | ✓（DJI 归 `custom/`，SDK 归 `EventMethod`） |

分类特有结论：`custom/` 中 `osd`/`state`/`set_property` 是**数据/属性 topic**（不携带 wire method 字段），SDK 分别以「字段枚举 + 数据模型」与「属性名枚举 + 扁平 map」建模 → **概念一致、结构差异（保留）**。

**A48-6 新增登记（§5-76）**：**非 DRC 分类的仓库↔SDK 覆盖差集** —— `services` ~35 项、`requests` DJI 独有 7 项、`status` 1 项（`static_topo`）、`events` 若干（`ai_object_detection_alert`/`rid_warning`/`self_diagnostic`/`station_measure_report` 等），**仅见于 DJI 仓库、未见于 v1.18 官方文档** → 同 §5-73 口径：**是否纳入须开发者确认，不得据仓库代码自行补齐**（AGENTS §3-11 / §2 协议忠实性）。

> **A48 为比对口径校准记录，未写代码**（遵守「原文收齐前不编码」硬约束）；新增待确认项见 §5-76。

### A49. M4D/M4TD 飞行器设备属性页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：`M4D/M4TD 设备属性` 官方文档页（26303 字节 / 227 行，**56 个顶层属性**），逐项与 SDK 比对。

**A49-0 覆盖结论（56 顶层属性）**

| 结论 | 数量 | 说明 |
|---|---|---|
| 已覆盖 ✓ | 绝大多数 | 字段名 / 类型 / pushMode / accessMode 与 SDK 逐项一致，含 `mode_code`（0-20 段）、`position_state`、`battery`、`obstacle_avoidance`、`distance_limit_status` 等 |
| 缺口 ❌ | **5 项** | 见 A49-1（含 1 项**已裁决建模**） |
| Javadoc 级偏差 | 4 处 | 见 A49-2（不改字段/类型） |

**A49-1 缺口清单（5 项）**

| # | 缺口 | 对应待确认项 |
|---|---|---|
| 1 | `best_link_gateway` / `flysafe_database_version`（pushMode=1）未入 `StateField` + `DroneOsd` | §5-77 |
| 2 | `fts_info` struct **首次被完整定义** → §5-8 前提撤销，**已裁决建模（方案 A）** | §5-78 |
| 3 | `DroneModeCode` 缺 `21=进离场航线飞行过程中` | §5-79 |
| 4 | `PropertySetMethod` 缺 `commander_flight_height` / `commander_mode_lost_action` | §5-80 |
| 5 | `CameraInfo` 缺 20 字段（`wide_*`/`zoom_*`/`ir_metering_*`/存储设置等）+ 新 struct | §5-81 |

**A49-2 Javadoc 级修正（4 处，非阻塞）**

| # | 位置 | 问题 |
|---|---|---|
| 1 | `StateField.DONGLE_INFOS` / `OFFLINE_MAP_ENABLE` | 注释「M400 only」，但 M4D/M4TD 页亦有定义 |
| 2 | `DronePositionState.quality` | 注释「Drone 仅 1-5、不含 10」，本页含 `10=RTK fixed` |
| 3 | `PositionQuality` | 未标飞行器适用性 |
| 4 | `cameras[].type_subtype_gimbalindex` | 含热成像字段（附注） |

**A49-3 附注**

- `wireless_link_topo` 字段与 §5-55（Dock3 wayline 页）**同源**，互为佐证（`center_node{sdr_id,sn}` / `leaf_nodes[]{sdr_id,sn,control_source_index}`）。
- `cameras[].type_subtype_gimbalindex` 为热成像/云台索引字段，与 `ir_metering_*` 系列同属 M4D/M4TD 热成像能力簇。
- `mode_code=21` 与 `mode_code_reason`（SDK 现 0-23 ✓）**并存不冲突**：前者为飞行状态码，后者为状态原因码，值域各自独立。

> **A49 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）；新增待确认项见 §5-77~82。
> **§5-78 已裁决（2026-10-14）→ 方案 A（建模）**：见 §0 决策记录 / §5-78；`fts_info` 建模待原文收齐后按 TDD 落地（AGENTS §3-5）。

---

### A50. Dock1 设备属性页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：`设备属性`（Dock1）官方文档页（页面日期 **2025-03-19**，wire `thing/product/{device_sn}` 双通道 —— osd/state），**46 个顶层属性**，逐项与 SDK 比对。

**A50-0 覆盖结论（46 顶层属性）**

| 结论 | 数量 | 说明 |
|---|---|---|
| 已覆盖 ✓ | 绝大多数 | 字段名 / 类型 / pushMode / accessMode 与 SDK 逐项一致，含 `mode_code`（0-5）、`flighttask_step_code`（0-5/255/256）、`network_state`（quality 0-5 六档）、`position_state`、`maintain_status`（Dock 版）、`alternate_land_point`（含 `height`）、`wireless_link` 的 `4g_*`、`silent_mode`/`user_experience_improvement`（rw）等 |
| 缺口/偏差 ❌ | **2 项** | 见 A50-1 |
| Javadoc 级偏差 | 4 处 | 见 A50-2（不改字段/类型） |
| 文档自身不一致 | 3 处 | 见 A50-3（以字段表为准，记录不改） |
| 时效性提示 | 1 处 | 见 A50-4 |

**A50-1 缺口/偏差清单（2 项）**

| # | 缺口/偏差 | 对应待确认项 |
|---|---|---|
| 1 | `electric_supply_voltage` **跨页类型冲突**：Dock1 页 Type=`int`/V/pushMode=0，O4 页 `double`/V，SDK `Double` | §5-83（并修订 §5-1） |
| 2 | `drone_authority_info`（struct，pushMode=0）**模型缺口**：SDK 有 `OsdField`/`StateField` 两处枚举，但 `DockOsd` 无字段、无 `DroneAuthorityInfo` record | §5-84 |

**A50-2 Javadoc 级修正（4 处，非阻塞）**

| # | 位置 | 问题 |
|---|---|---|
| 1 | `DockOsd.coverState` | Javadoc 仅 2 值，Dock1 页为 4 值（§5-86） |
| 2 | `DockOsd.putterState` | Javadoc 仅 2 值，Dock1 页为 4 值（§5-86） |
| 3 | `AirConditioner.airConditionerState` | Javadoc 列 0-3，Dock1 页为 16 值（0-15）（§5-86） |
| 4 | `StateField.WPMZ_VERSION` | 注释「飞行器 state」，Dock1 页列于机场属性（§5-87） |

**A50-3 文档自身不一致（3 处，以字段表为准，记录不改）**

| # | 位置 | 问题 |
|---|---|---|
| 1 | `sub_device` | 字段表列 `product_type`，示例 wire key 为 `device_model_key`；SDK 用 `deviceModelKey` 对齐示例（§5-85） |
| 2 | `air_conditioner` | 字段表为 `air_conditioner` struct（含 `air_conditioner_state`），示例用扁平 `air_conditioner_mode`；以字段表为准（同 §5-41 模式） |
| 3 | `drone_authority_info.control_source` | 示例含 `""`（未获取）空值，enum_string 口径待核（§5-84 附） |

**A50-4 时效性提示（1 处）**

- Dock1 页日期 **2025-03-19**，早于当前 v1.18（2026.10.13）→ 待开发者确认该页是否为 v1.18 现行版（§5-88）；若为历史版，则 A50-1/A50-2 结论需以最新 Dock1 页复核。

> **A50 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）；`electric_supply_voltage` 类型跨页冲突（§5-83）与 `drone_authority_info` 模型缺口（§5-84）需开发者裁决后再按 TDD 落地（AGENTS §3-5/§3-11）。

---

### A51. Dock1 设备管理页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：`设备管理`（Dock1）官方文档页（页面日期 **2025-03-19**；Topic `sys/product/{gateway_sn}/status`（up）+ `sys/product/{gateway_sn}/status_reply`（down）；method `update_topo`）。

**A51-0 覆盖结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| 已覆盖 ✓ | 全部（7 顶层 + 8 子字段） | `UpdateTopoData` / `SubDevice` / `UpdateTopoReplyData` / `StatusMethod.UPDATE_TOPO` 与文档**逐字段一致** |
| 口径/元数据偏差 | 3 项 | 见 A51-1（均非字段缺口） |

**A51-1 逐项比对**

| 项 | 文档 | SDK | 判定 |
|---|---|---|---|
| Topic（up） | `sys/product/{gateway_sn}/status` | `TopicTemplate`/`TopicBuilder` 支持 `sys/product` 前缀 | ✓ |
| Topic（down） | `sys/product/{gateway_sn}/status_reply` | `TopicChannel.STATUS_REPLY` | ✓ |
| method | `update_topo` | `StatusMethod.UPDATE_TOPO` | ✓ |
| Data 顶层 | `domain`(string)/`type`(int)/`sub_type`(int)/`device_secret`(text)/`nonce`(text)/`thing_version`(text)/`sub_devices`(array&lt;struct&gt;) | `UpdateTopoData`：`domain`(String)/`type`(Integer)/`subType`(Integer)/`deviceSecret`(String)/`nonce`(String)/`thingVersion`(String)/`subDevices`(List&lt;SubDevice&gt;) | ✓ |
| `sub_devices[]` | `sn`(text)/`domain`(string)/`type`(int)/`sub_type`(int)/`index`(string)/`device_secret`(text)/`nonce`(text)/`thing_version`(text) | `SubDevice`：`sn`/`domain`/`type`/`subType`/`index`/`deviceSecret`/`nonce`/`thingVersion`（8 字段） | ✓ |
| 下线语义 | `sub_devices:[]` 表示下线 | `UpdateTopoData` Javadoc「`sub_devices` 为空列表表示下线」 | ✓ |
| Reply data | `result`(int，非 0 错误) | `UpdateTopoReplyData.result`(Integer) ✓；**另有 `subType`（`@Inferred`）** | 偏差（A51-2 #1） |
| 字段缺口 | — | — | **无** |

**A51-2 口径/元数据（3 项）**

| # | 项 | 对应待确认项 |
|---|---|---|
| 1 | `status_reply` data 本页**仅 `result`**，SDK `UpdateTopoReplyData` 多 `subType`（`@Inferred` 依据「DJI 文档含 sub_type」与本页矛盾） | §5-89 |
| 2 | `StatusMethod.UPDATE_TOPO` Javadoc「data 含 sub_type 区分上线 0/下线 1」= 语义冲突（UP data `sub_type` 实为**产品子类型**；上下线由 `sub_devices` 空/非空区分） | §5-90 |
| 3 | `base_info`：本页 Data **无**；O4 `device.html`(A26) **有**（`base_info{country_code}`）→ 归属待核（是否 O4 专属） | §5-91 |

**A51-3 附注**

- 本页 Topic `sys/product/{gateway_sn}/status` 与 A26（O4 设备属性页）表述**一致**，二次印证 §4 line 303「`StatusMethod` 须核对 Topic 前缀」→ SDK 支持 `sys/product` 前缀 ✓。
- 本页**未提** `static_topo`（该差异维持 §5-76 记录）。
- `domain` 类型为 `string`（非数值），与 SDK `UpdateTopoData.domain`(String) 一致 ✓。

> **A51 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）；新增待确认项见 §5-89~91。

---

### A52. Dock1 组织管理页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：`组织管理`（Dock1）官方文档页（页面日期 **2025-03-19**；Topic `thing/product/{gateway_sn}/requests`（up）+ `requests_reply`（down））。3 条注册指令：`airport_bind_status` / `airport_organization_get` / `airport_organization_bind`。

**A52-0 覆盖结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| Request 侧已覆盖 ✓ | 3/3 | 3 个 Request POJO 与文档**逐字段一致** |
| Reply 侧缺口 ❌ | 3 项 | 见 A52-2（与 A4 同源，本页**二次印证**） |

**A52-1 Request 侧比对（全部一致 ✓）**

| 指令 | SDK POJO | 文档字段 | 判定 |
|---|---|---|---|
| `airport_bind_status` | `AirportBindStatusRequest.Device` | `sn` | ✓ |
| `airport_organization_get` | `AirportOrganizationGetRequest` | `device_binding_code` / `organization_id` | ✓ |
| `airport_organization_bind` | `AirportOrganizationBindRequest.BindDevice` | `device_binding_code` / `organization_id` / `device_callsign` / `sn` / `device_model_key`（5 字段） | ✓ |

**A52-2 Reply 侧缺口（3 项，与 A4 同源）**

| # | POJO | 问题 | 文档要求 | 对应待确认项 |
|---|---|---|---|---|
| 1 | `AirportOrganizationBindReply.ErrorInfo` | `errSn` → `err_sn`（SNAKE_CASE）**不匹配** wire key `sn` | `sn` | §5-92 |
| 2 | `AirportOrganizationGetReply` | 仅 `result`，缺 `output`；原 `@Inferred` 假设 `output.airports` **错误** | `output{ organization_name }` | §5-93 |
| 3 | `AirportBindStatusReply` | 仅 `result`，缺 `output`；Javadoc「result 0=未绑定/1=已绑定」**语义冲突** | `output{ bind_status[]{ sn, is_device_bind_organization, organization_id, organization_name, device_callsign } }`；`result`=返回码（0 成功/非 0 错误） | §5-94 |

**A52-3 附注**

- 本页 3 指令与 **A4（`ground-station/o4/requests.html`）内容一致** → **二次印证 A4 的 3 处 Reply 缺口**；A4 结论此前**仅在附录记录、未列入 §5**，本页一并补齐 §5-92~94（修正 §5 单一真相源的完整性）。
- Reply header 键 `result`/`output` 与 SDK `RequestReplyEnvelope` 约定一致 ✓。
- `is_device_bind_organization` 文档 Type=`bool` → SDK 建模应采用 `Boolean`。

> **A52 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）；新增/补齐待确认项见 §5-92~94。

---

### A53. Dock1 直播功能页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：`直播功能`（Dock1）官方文档页（页面日期 **2025-11-12**；Topic `thing/product/{gateway_sn}/services`（down）+ `services_reply`（up））。4 条 services：`live_start_push` / `live_stop_push` / `live_set_quality` / `live_lens_change`。

**A53-0 覆盖结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| 已覆盖 ✓ | 4/4 | `ServiceMethod` 齐全 + 4 个 Request POJO + `VideoQuality`(0-4)；reply 均仅 `result`（`NoOutputReply`/空占位） |
| 待确认/非阻塞 | 2 项 | 见 A53-2（enum 建模 + Javadoc） |

**A53-1 逐方法比对（4/4 一致 ✓）**

| method | 文档请求 data | SDK | 判定 |
|---|---|---|---|
| `live_start_push` | `url_type`enum_int{0声网,1 RTMP,3 GB28181,4 WebRTC}、`url`、`video_id`、`video_quality`enum_int{0-4} | `LiveStartPushRequest(videoId,url,urlType,videoQuality)` | ✓（`urlType`/`videoQuality` 裸 `Integer`，见 A53-2） |
| `live_stop_push` | `video_id` | `LiveStopPushRequest(videoId)` | ✓ |
| `live_set_quality` | `video_id`、`video_quality` | `LiveSetQualityRequest(videoId,videoQuality)` | ✓ |
| `live_lens_change` | `video_type`enum_string{ir,normal,wide,zoom} | `LiveLensChangeRequest(videoType,videoId)`（`videoId` 对 Dock 不适用，`NON_NULL` 不序列化） | ✓ |

**A53-2 待确认/非阻塞（2 项）**

| # | 项 | 对应待确认项 |
|---|---|---|
| 1 | `url_type`（enum_int，**跳 2**）/ `video_type`（enum_string）**未建模为 Java 枚举**（SDK 裸 `Integer`/`String`） | §5-95（是否补枚举，可选） |
| 2 | `live_*` Javadoc 取值/格式不全（`url_type` 全值、`url` 四类格式、`video_id` 格式、`video_quality` 分辨率/码率） | §5-96（非阻塞 Javadoc） |

**A53-3 附注**

- 本页 4 方法与 **A5（O4 `cmd.html` live 段）一致** → **二次印证**；`VideoQuality`(0-4) 与文档 {0自适应,1流畅,2标清,3高清,4超清} ✓。
- `live_lens_change` 页表标 reply `data: null`，而同页 example 含 `{"result":0}` → **文档自身不一致**；SDK 按「仅 `result`、无 output」处理（`NoOutputReply`）正确。
- 页面日期 **2025-11-12**，晚于 Dock1 设备属性/设备管理/组织管理三页（2025-03-19），为本批最新 Dock1 页。
- `url_type=4` WebRTC 仅支持 WHIP 信令；`url_type` **无值 2** 是本页明确口径。

> **A53 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）；新增待确认项见 §5-95~96。

---

### A54. Dock1 媒体管理页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：`媒体管理`（Dock1）官方文档页（页面日期 **2026-03-19**）。2 events + 1 service + 1 request。

**A54-0 覆盖结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| 已覆盖 ✓ | 4/4 | 4 条指令 POJO + `EventMethod`/`ServiceMethod`/`RequestsMethod` 枚举齐全 |
| 差异 ❌ | 2 项 | 见 A54-2 |
| 可选增强 | 1 项 | 见 A54-2 |

**A54-1 逐方法比对**

| 通道 | method | 文档 data | SDK | 判定 |
|---|---|---|---|---|
| events | `highest_priority_upload_flighttask_media` | `flight_id` | `HighestPriorityUploadFlighttaskMediaData(flightId)` | ✓ |
| events | `file_upload_callback` | `file{ object_key, path, name, ext{ flight_id, drone_model_key, payload_model_key, is_original }, metadata{ gimbal_yaw_degree, absolute_altitude, relative_altitude, create_time, shoot_position{ lat, lng } } }` | `FileUploadCallbackData(file→MediaFile{cloudToCloudId, ext, metadata, name, objectKey, path})` + `MediaFileExt`(4)/`MediaFileMetadata`(5)/`ShootPosition`(2) | ✓（除 `gimbal_yaw_degree`，见 A54-2） |
| services | `upload_flighttask_media_prioritize` | `flight_id`；reply `result` | `UploadFlighttaskMediaPrioritizeRequest(flightId)`；`NoOutputReply` | ✓ |
| requests | `storage_config_get` | req `module`enum_int{0媒体}；reply `result` + `output{ bucket, credentials{ access_key_id, access_key_secret, expire, security_token }, endpoint, provider, region, object_key_prefix }` | `StorageConfigGetRequest(module)`；`StorageConfigGetReply(result, Output{bucket, endpoint, region, provider, objectKeyPrefix, credentials})` + `Credentials(accessKeyId, accessKeySecret, securityToken, expireTime)` | 偏差（`expireTime`，见 A54-2） |

**A54-2 差异 / 可选（3 项）**

| # | 项 | 对应待确认项 |
|---|---|---|
| 1 | `StorageConfigGetReply.Credentials.expireTime`：SNAKE_CASE → **`expire_time`**，文档 wire key = **`expire`**（`access_key_id`/`access_key_secret`/`security_token` 三者命名正确） | §5-97（改 `expire`；与 §5-52 同字段） |
| 2 | `FileUploadCallbackData.MediaFileMetadata.gimbalYawDegree`：文档 Type=`float`，同页示例为字符串 `"-91.40"`；SDK=`String`（对齐示例） | §5-98（Type vs 示例冲突） |
| 3 | `storage_config_get` reply `provider` enum_string{ali,aws,minio} 未建模为枚举（裸 `String`） | §5-99（可选，非阻断） |

**A54-3 附注**

- 本页 4 指令与 **A35（`dock3/media.html`）内容一致** → **二次印证**；`file_upload_callback` 示例含 `cloud_to_cloud_id:"DEFAULT"`，SDK `MediaFile.cloudToCloudId` ✓。
- `highest_priority_upload_flighttask_media` / `file_upload_callback` 均 `need_reply=1`（示例含 `gateway` 字段，属 envelope 范畴）。
- 页面日期 **2026-03-19**，早于 v1.18（2026.10.13），属现行版。

> **A54 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）；新增待确认项见 §5-97~99。

---

### A55. Dock1 航线管理页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：`航线管理`（Dock1）官方文档页（页面日期 **2025-11-12**）。**15 条指令**：Events（up）5 + Services（down）9 + Requests（up）1。

**A55-0 覆盖结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| 方法常量存在 ✓ | 15/15 | `EventMethod`/`ServiceMethod`/`RequestsMethod` 枚举齐全 |
| POJO 已覆盖 ✓ | 13/15 | 仅 `flight_setup_exception_notify`（无 POJO）、`flight_setup_abort`/四组 data=null（复用 `NoParameterRequest`）例外 |
| 缺口 / 偏差 ❌ | 4 项 | 见 A55-2 |
| 强化既有 | 3 项 | `flighttask_create`（§5-56）、`return_home` reply（§5-16/§5-51）、`device_exit_homing_notify.reason` 类型（同 A41） |

**A55-1 逐指令比对**

| 通道 | method | 文档 data / reply | SDK | 判定 |
|---|---|---|---|---|
| events | `flight_setup_exception_notify` | data `sn`/`timeout_time`(2-10)/`timestamp`/`flight_type`(1航线/2指令飞行)；example 含 `flight_id`、`need_reply=1`；reply `result` | `EventMethod.FLIGHT_SETUP_EXCEPTION_NOTIFY`(needReply=1) ✓，**Javadoc 记 `error_code`/`error_reason`、无 POJO** | **❌ 缺口（§5-100）** |
| events | `device_exit_homing_notify` | `sn`/`action`(enum 0/1)/`reason`(enum 0-10)；example `reason:"0"`(字符串)、`need_reply=1` | `DeviceExitHomingNotifyData(sn, action:int, reason:int)` + `@Inferred` | ✓（类型冲突同 A41） |
| events | `flighttask_progress` | data→`output{ext{current_waypoint_index, flight_id, media_count, track_id, wayline_id, wayline_mission_state(0-9), break_point{index,state(0/1),progress,wayline_id,break_reason(超大),latitude,longitude,height,attitude_head}}, progress{current_step(0-48+65533-5), percent}, status(9值enum_string)}` + `result` | `FlighttaskProgressData(result, Output{ext, progress, status})` + `Ext`/`BreakPoint`(9)/`Progress` | ✓ 全字段一致 |
| events | `flighttask_ready` | `flight_ids`(array) | `FlighttaskReadyData(flightIds)` | ✓ |
| events | `return_home_info` | `planned_path_points[]{latitude(double),longitude(double),height(float)}`/`last_point_type`(enum 0/1)/`flight_id` | `ReturnHomeInfoData(plannedPathPoints, lastPointType, flightId, homeDockSn, multiDockHomeInfo)` | ✓（Dock1 页仅 3 字段，同 A34） |
| services | `flight_setup_abort` | data null；reply `result`(0/326108/326109) | `ServiceMethod.FLIGHT_SETUP_ABORT` ✓ + `NoParameterRequest` + `NoOutputReply` | ✓（reply 码未入 Javadoc，§5-101） |
| services | `flighttask_create`（**废弃**） | `flight_id`/`type`/`file{url,sign}`；reply `result` | **无** `ServiceMethod.FLIGHTTASK_CREATE` | **✗ 缺失（§5-56，三度印证）** |
| services | `flighttask_prepare` | `flight_id`/`execute_time`(13位)/`task_type`(0/1/2)/`file{url,fingerprint}`/`ready_conditions{battery_capacity,begin_time,end_time}`/`executable_conditions{storage_capacity}`/`break_point{index,state,progress,wayline_id}`/`rth_altitude`(20-1500)/`out_of_control_action`(0/1/2)/`exit_wayline_when_rc_lost`(0/1)/`simulate_mission{is_enable,latitude,longitude}` | `FlighttaskPrepareRequest`（**并集**，含 Dock1 页无的 `rthMode`/`waylinePrecisionType`/`flightSafetyAdvanceCheck`）+ `FlighttaskFile`/`ReadyConditions`/`ExecutableConditions`/`BreakPoint`/`SimulateMission`(4 字段含 `altitude`) | ✓（`simulate_mission`/字段子集差异，§5-102/§5-103） |
| services | `flighttask_execute` | `flight_id`；reply `result` | `FlighttaskExecuteRequest` + `FlighttaskExecuteReply` | ✓ |
| services | `flighttask_undo` | `flight_ids`(array)；reply `result` | `FlighttaskUndoRequest(flightIds)` + `NoOutputReply` | ✓ |
| services | `flighttask_pause` | data null；reply `result` | `NoParameterRequest` + `NoOutputReply` | ✓ |
| services | `flighttask_recovery` | data null；reply `result` | `NoParameterRequest` + `NoOutputReply` | ✓ |
| services | `return_home` | data null；reply `result` + `output{status}`(8值 enum_string，无 `partially_done`) | `ServiceMethod.RETURN_HOME` ✓，**无 `ReturnHomeReply`** | ✓（reply 缺口，§5-16，三度印证） |
| services | `return_home_cancel` | data null；reply `result` | `NoParameterRequest` + `NoOutputReply` | ✓ |
| requests | `flighttask_resource_get` | `flight_id`；reply `result` + `output{file{url,fingerprint}}` | `FlighttaskResourceGetRequest` + `FlighttaskResourceGetReply(result, Output{File{url,fingerprint}})` | ✓ |

**A55-2 差异 / 缺口（4 项）**

| # | 项 | 对应待确认项 |
|---|---|---|
| 1 | `flight_setup_exception_notify`：`EventMethod` 常量已存在但 Javadoc 记「`error_code`/`error_reason`」（**与文档 `sn`/`timeout_time`/`timestamp`/`flight_type` 矛盾**），且**无 POJO** | §5-100（新建 POJO + 更正 Javadoc） |
| 2 | `flight_setup_abort` reply 错误码 `326108`/`326109` 与调用时机未入 Javadoc | §5-101（Javadoc 补充） |
| 3 | `simulate_mission` Dock1 页仅 3 字段（无 `altitude`）vs SDK 4 字段（`@Inferred`） | §5-102（机型归属裁决） |
| 4 | Dock1 `flighttask_prepare` 无 `rth_mode`/`wayline_precision_type`/`flight_safety_advance_check`（Dock3 页有） | §5-103（Dock3 专属裁决） |

**A55-3 附注**

- 本页与 **A41（`dock3/wayline.html`）/A42（`dock2/wayline.html`）同结构** → 多数结论一致，**新增仅 Dock1 专属 `flight_setup_exception_notify` / `flight_setup_abort`**（A41/A42 页均未含）→ 首次审计。
- **强化既有（非新增）**：`flighttask_create`（文档仍标「废弃」）**三度印证 §5-56**；`return_home` reply `{result, output{status}}` **三度印证 §5-16/§5-51**；`device_exit_homing_notify.reason` 文档 `enum_int` vs example 字符串 `"0"`（同 A41/A42）；`flighttask_progress` 的 `wayline_mission_state`(0-9)/`break_reason`(超大)/`current_step`(0-48+65533-5) 均以 `Integer` 承载 ✓。
- 页面日期 **2025-11-12**（与 A53 同日），早于 v1.18（2026.10.13），属现行版。

> **A55 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）；新增待确认项见 §5-100~103。

---

### A56. Dock1 HMS 管理页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：Dock1「HMS 管理」官方文档页（页面日期 **2025-03-19**）。唯一 method：`hms`（events，up，`thing/product/{gateway_sn}/events`）。

**A56-0 覆盖结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| 方法常量存在 ✓ | 1/1 | `EventMethod.HMS("hms", "HMS 告警", 0)` ✓（need_reply=0） |
| POJO 已覆盖 ✓ | 1/1 | `HmsData(list)` → `Item` → `Args` ✓（`@Verified`） |
| 新增差异 | **0** | **与 A20（`dock3/hms.html`，2025-03-19）内容逐字一致 → 重复页复核** |

**A56-1 逐项比对**

| 字段 | 文档（A56 页） | SDK | 判定 |
|---|---|---|---|
| `list` | array(size 20, struct) | `List<Item> list` | ✓ |
| `»level` | enum_int 0通知/1提醒/2警告 | `Integer level` | ✓ |
| `»module` | enum_int 0飞行任务/1设备管理/2媒体/3hms | `Integer module` | ✓ |
| `»in_the_sky` | enum_int 0地上/1天上 | `Integer inTheSky` | ✓ |
| `»code` | **text**（length 10240；示例 `"0x16100083"`） | **`Integer code`** | **❌ 类型不符（§5-19；A20 已裁「修正为 `String`」，代码待落）** |
| `»device_type` | text（`{domain-type-subtype}`） | `String deviceType` | ✓ |
| `»imminent` | enum_int 0否/1是 | `Integer imminent` | ✓ |
| `»args` | struct{`component_index` int, `sensor_index` int} | `Args`(Integer, Integer) | ✓（constraint 标 `length` 系文案变量说明残留，无需改动） |

**A56-2 附注**

- **本页 = A20 的 Dock1 树镜像**：字段/示例/日期（2025-03-19）与 A20（`dock3/hms.html`）逐字一致 → 仅作重复页复核，**不新增 §5 项**。
- **复认既有**：§5-19（`Item.code` 类型缺陷，待改 `String`）、§5-20（`hms_inner` 不补登，结案）。
- `EventMethod.HMS` Javadoc（L152）记「data 含 `level`/`code`/`module`/`key` 等」——`key` **未见于本页字段表**（`level`/`module`/`in_the_sky`/`code`/`device_type`/`imminent`/`args`）→ Javadoc 表述不精确（无功能影响，可随 §5-19 一并修正）。

> **A56 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A57. Dock1 远程调试页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：Dock1「远程调试」官方文档页（页面日期 **2025-08-05**）。Topic：`thing/product/{gateway_sn}/events`（up）+ `events_reply`（down）；`thing/product/{gateway_sn}/services`（down）+ `services_reply`（up）。**12 个同名进度事件（events）+ 21 条 services**。

**A57-0 覆盖结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| services 方法常量存在 ✓ | 21/21 | `ServiceMethod` 全覆盖（cover/charge/putter/debug/format/…） |
| events 同名进度事件 | 12（约定不重复登记） | 按 [`EventMethod`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/protocol/method/EventMethod.java#L38-L40) 约定（services 进度用同名 method）→ 不重复定义；data 由 `ServiceProgressData` 覆盖 ✓ |
| 参数化 services 请求 POJO | 5（2 处字段名偏差） | `battery_maintenance_switch`(action)/`alarm_state_switch`(action)/`sdr_workmode_switch`(link_workmode) ✓；`air_conditioner_mode_switch`/`battery_store_mode_switch` 字段名 `mode` ≠ 文档 `action` ✗（§5-104） |
| data=null services | 16 | `NoParameterRequest` ✓ |
| services_reply | 21 | `NoOutputReply`（result + output{status} 按约定）✓；`sdr_workmode_switch` 仅 result ✓ |
| 新增差异 | **2（+1 可选）** | §5-104 / §5-105 / §5-106 |

**A57-1 Events 段（12 个同名进度事件）**

| method | data（文档） | SDK | 判定 |
|---|---|---|---|
| `cover_force_close` | result + output{status(8值), progress{percent}} | `ServiceProgressData` | ✓ |
| `drone_open` | result + output{status(8值)}（**无 progress**） | `ServiceProgressData`（progress 可 null） | ✓ |
| `drone_close` | result + output{status, progress{percent, step_key(7值)}} | `ServiceProgressData` | ✓ |
| `device_reboot` | result + output{status, progress{percent, step_key(6值)}} | `ServiceProgressData` | ✓ |
| `cover_close` | result + output{status, progress{percent, step_key(9值)}} | `ServiceProgressData` | ✓ |
| `cover_open` | result + output{status, progress{percent, step_key(5值)}} | `ServiceProgressData` | ✓ |
| `charge_open` | result + output{status, progress{percent, step_key(6值)}} | `ServiceProgressData` | ✓ |
| `charge_close` | result + output{status, progress{percent, step_key(4值)}} | `ServiceProgressData` | ✓ |
| `drone_format` | result + output{status, progress{percent}}（example 含 `step_key:"xxx"`） | `ServiceProgressData` | ✓ |
| `device_format` | 同 `drone_format` | `ServiceProgressData` | ✓ |
| `putter_close` | result + output{status, progress{percent, step_key(5值)}} | `ServiceProgressData` | ✓ |
| `putter_open` | result + output{status, progress{percent, step_key(5值)}} | `ServiceProgressData` | ✓ |

> 8 值 status 枚举 = `canceled/failed/in_progress/ok/paused/rejected/sent/timeout`（同 A1/A5）。
> 11 个事件带 `events_reply`（down，data `{result}`）——仅 `cover_force_close` 未列 `events_reply`（文档省略，无功能影响）。

**A57-2 Services 段（21 条）**

| method | 请求 data（文档） | reply（文档） | SDK | 判定 |
|---|---|---|---|---|
| `cover_force_close` | null | result + output{status} | `NoParameterRequest` + `NoOutputReply` | ✓ |
| `debug_mode_open` / `debug_mode_close` | null | result + output{status} | 同上 | ✓ |
| `supplement_light_open` / `supplement_light_close` | null | result + output{status} | 同上 | ✓ |
| `battery_maintenance_switch` | **`action`** enum_int{0关闭,1开启} | result + output{status} | `ActionSwitchRequest(action)`（**全仓无引用**） | ⚠ 覆盖/去留见 §5-105 |
| `air_conditioner_mode_switch` | **`action`** enum_int{0空闲,1制冷,2制热,3除湿} | result + output{status} | `AirConditionerModeSwitchRequest.mode`（**≠ action**） | **❌ §5-104** |
| `alarm_state_switch` | **`action`** enum_int{0关闭,1开启} | result + output{status} | `AlarmStateSwitchRequest.action` | ✓ |
| `battery_store_mode_switch` | **`action`** enum_int{1计划,2待命} | result + output{status} | `BatteryStoreModeSwitchRequest.mode`（**≠ action**） | **❌ §5-104** |
| `device_reboot` / `drone_open` / `drone_close` / `device_format` / `drone_format` / `cover_open` / `cover_close` / `charge_open` / `charge_close` / `putter_open` / `putter_close` | null | result + output{status} | `NoParameterRequest` + `NoOutputReply` | ✓ |
| `sdr_workmode_switch` | **`link_workmode`** enum_int{0仅SDR,1 4G增强} | **result（无 output）** | `SdrWorkmodeSwitchRequest(int linkWorkmode)` | ✓ |

**A57-3 附注**

- **本页与 A5（O4 `cmd.html`）为不同机型的远程调试页**：A5 覆盖 O4 的 `debug_mode_*`/`device_reboot`/`device_format`/`drone_format`/`sdr_workmode_switch`/`live_*`/`camera_*`；A57 覆盖 Dock1 的 cover/charge/putter/debug/format 全量 → 方法常量**两页共同印证**；A57 **新增** A5 未给官方原文的 4 个 action 类字段（`battery_maintenance_switch`/`air_conditioner_mode_switch`/`alarm_state_switch`/`battery_store_mode_switch`）。
- **复认既有**：§缺口2（12 进度事件不重复登记 `EventMethod`，A5 已记）；§缺口4（`ServiceProgressData` Javadoc 方法名 `power_on`/`power_off`/`format_storage` 与文档实际 `drone_open`/`device_reboot`/`device_format` 不一致，A5 已记，本页**二次印证**）；services_reply `output{status}` 沿用 `NoOutputReply` 约定（A39/A5；`NoOutputReply` Javadoc L27-32 已显式声明「Job 类指令 `output.status=sent`，进度走 events」）✓。
- **新增待确认项见 §5-104~106**。

> **A57 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A58. Dock1 固件升级页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：Dock1「固件升级」官方文档页（页面日期 **2025-03-19**）。Topic `thing/product/{gateway_sn}/events`（up） / `services`（down）+ `services_reply`（up）。**Event 1 项（`ota_progress`）+ Service 1 项（`ota_create` + `services_reply`）**。

**判定**：**A39（O4 地面站 `firmware.html`）的 Dock1 镜像页** —— 字段集合、类型、示例值与 A39 逐字一致，**无新增结构缺口**。

**A58-1 逐项比对**

| 项 | 文档（A58 Dock1） | SDK | 判定 |
|---|---|---|---|
| `ota_progress`（events/up） | `result`+`output{status(8值), progress{percent(0-100), current_step(download_firmware/upgrade_firmware)}}` | `OtaProgressData`（`Output.status`/`Progress.percent`/`Progress.currentStep`） | ✓（复认 §5-50 元数据） |
| `ota_create`（services/down） | `devices[]{sn, product_version, file_url, md5, file_size(int), file_name, firmware_upgrade_type(enum_int)}` | `OtaCreateRequest.OtaDevice` | ✓ 结构；`file_size` **`Long`≠`int`**（§5-48）、`firmware_upgrade_type` **无枚举**（§5-49） |
| `firmware_upgrade_type` 值域 | Dock1 = **`{2:一致性升级,3:普通升级}`**（**无 4**）；A39 O4 = `{2,3,4:PSDK升级}` | 原始 `int` | **并集 `{2,3,4}`**（并入 §5-49） |
| `ota_create` reply | `result`+`output{status}` | `NoOutputReply`（无专属 POJO） | §5-51 一致性提请（复认） |

**A58-2 附注**

- **复认既有**：§5-48（`file_size` `Long`→`Integer`，Dock1 页 Type 亦 `int`、示例 `653467234` → 二次印证）、§5-49（补 `FirmwareUpgradeType` 枚举，值域取并集）、§5-50（`OtaProgressData` `@DocUrl` 现指 `config.html`，应改指 `firmware.html`）、§5-51（reply `output.status` 一致性）。
- **印证可选字段**：Dock1 `ota_create` 示例第二设备（`sn:"dock_sn"`）仅给 `sn`/`product_version`/`firmware_upgrade_type` 三字段 → `file_url`/`md5`/`file_size`/`file_name` 缺席为 null 的处理 ✓（与 `OtaDevice` 包装类型一致）。
- **无新增 §4/§5 项**。

> **A58 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A59. Dock1 远程日志页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：Dock1「远程日志」官方文档页（页面日期 **2025-03-19**）。Topic `thing/product/{gateway_sn}/events`/`services`/`services_reply`。**Event 1 项（`fileupload_progress`）+ Service 3 项（`fileupload_list`/`fileupload_start`/`fileupload_update`，各含 `services_reply`）**。

**判定**：**A40（O4 地面站 `log.html`）的 Dock1 镜像页**：4 method 常量齐备、POJO 结构一致；**新差异 1 项（§5-107，Dock1 `fileupload_start` 为 O4 子集）**。

**A59-1 逐项比对**

| 项 | 文档（A59 Dock1） | SDK | 判定 |
|---|---|---|---|
| `fileupload_progress`（events/up） | `result` + `output{status?, ext{files[]{module(enum_int{0,3}), size(int), device_sn, key, fingerprint, progress{progress, finish_time, upload_rate}}}}`；example `output.status:"ok"`、`progress` **7 字段** | **`FileUploadProgressData` 缺失**（`event/system/package-info.java` 悬空 `@link`） | **✗ 需新建**（§4；字段集裁决 §5-54，**三次印证**） |
| method `fileupload_progress` | example `need_reply:0` | `EventMethod.FILEUPLOAD_PROGRESS`（`need_reply=0`） | ✓ |
| `fileupload_list`（services/down） | `module_list`（array `enum_int`，example `["0","3"]` 字符串） | `FileUploadListRequest.moduleList`(`List<String>`) | ✓（口径 §5-53） |
| `fileupload_list`（reply/up） | `files[]{device_sn, result(int), module(enum_int), list[]{boot_index(int), start_time(int,秒/s), end_time(int,秒/s), size(int,byte)}}, result(int)` | `FileUploadListReply`（`FileGroup`/`LogFile`） | ✓ 结构；`LogFile.startTime/endTime/size`=`long` vs 文档 `int`（§5-52） |
| `fileupload_start`（services/down） | `bucket, region, credentials{access_key_id, access_key_secret, expire(int,秒/s), security_token}, endpoint, provider(enum_string{ali,aws,minio}), params{files[]{object_key, module(text), list[]{boot_index}}}` | `FileUploadStartRequest`（`Credentials`/`FileUploadParams`/`FileUploadFile`/`FileUploadBoot`） | ✓ 结构；**Dock1 为 O4 子集**（缺 `file_store_dir`/`file_type`/`device_sn`/`result`/`list` 3 字段）→ §5-107 |
| `fileupload_start`（reply/up） | `result` | `NoOutputReply` | ✓ |
| `fileupload_update`（services/down） | `status`(enum_string `{cancel}`) + `module_list`(array, item_type 空=文档笔误) | `FileUploadUpdateRequest`(`status`/`moduleList`) | ✓ |
| `fileupload_update`（reply/up） | `result` | `NoOutputReply` | ✓ |

**A59-2 附注**

- **复认既有**：§4（`FileUploadProgressData` 确缺，**二次印证**仍待新建）；§5-52（`long`/`Long` vs `int`；Dock1 页 `start_time`/`end_time`/`expire` 标 **秒/s** 但 example 均 13 位毫秒 → 自相矛盾，支持「须 `long`」）；§5-53（`module` 口径：Dock1 `fileupload_list` `enum_int` vs `fileupload_start` `text`，example 均字符串，SDK `String`）；§5-54（`fileupload_progress` 表 vs example 冲突，**三次印证**）。
- **新增待确认项见 §5-107**。
- **无新增 §4 项**（`FileUploadProgressData` 新建已在 §4 清单）。

> **A59 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A60. Dock1 配置更新页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：Dock1「配置更新」官方文档页（页面日期 **2025-06-09**）。Topic `thing/product/{gateway_sn}/requests`（up）/ `thing/product/{gateway_sn}/requests_reply`（down）。**Requests 1 项（`config`，含 `requests_reply`）**。

**判定**：**A7（O4 Requests 获取配置 `config`）的 Dock1 镜像页** —— 请求/回复字段逐字一致；[`ConfigRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/registration/ConfigRequest.java) 一致 ✓、[`ConfigReply`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/registration/ConfigReply.java) **需重建**。**A7 此前仅在 §3 记录、漏登记 §4/§5 → 本页补齐（新增 §5-108 + §4 行）**。

**A60-1 逐项比对**

| 项 | 文档（A60 Dock1 配置更新） | SDK | 判定 |
|---|---|---|---|
| `config`（requests/up） | `config_type`(enum_string{json}) + `config_scope`(enum_string{product}) | [`ConfigRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/registration/ConfigRequest.java)（`configType`/`configScope`） | ✓ 完全一致 |
| method `config` | `RequestsMethod.CONFIG`=`"config"`（L46） | ✓ |
| `config`（requests_reply/down） | `data` = `ntp_server_host`(text) / `app_id`(text) / `app_key`(text) / `app_license`(text) / `ntp_server_port`(int, 缺省 123)；**无 `result` 字段** | [`ConfigReply`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/registration/ConfigReply.java) = `{Integer result, String appId, String appLicense, String url, String token}` | **✗ 需重建**（§5-108） |

**A60-2 字段对照（`ConfigReply`）**

| SDK 现值 | 文档 | 问题 |
|---|---|---|
| `result`(Integer, 必填) | — | **文档未定义 `result`** → 移除待裁决（现 `Objects.requireNonNull`） |
| `appId` | `app_id` | ✓ |
| `appLicense` | `app_license` | ✓ |
| `url` | `ntp_server_host` | **字段名不符** |
| `token` | `ntp_server_port` | **字段名不符（且文档类型 `int`，非 `String`）** |
| — | `app_key` | **缺失** |
| — | `ntp_server_port` | **缺失** |

**A60-3 附注**

- **重建建议**：`ConfigReply = { appId, appKey, appLicense, ntpServerHost, ntpServerPort(Integer) }`（按文档口径）；`result` 去留须裁决。
- **复认 A7**：§3「O4 Requests —— 获取配置 `config`」（L648-666）结论一致；本页为 Dock1 同内容**二次印证**。
- **元数据**：`ConfigReply` 原 `@Inferred`（「非 output 包裹」）与 `@Verified`（simulator basis）可据本原文定稿/升级。
- **新增待确认项见 §5-108**；**新增 §4 行**（`ConfigReply` 重建，`command/request/registration`）。

> **A60 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A61. Dock1 指令飞行页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：Dock1「指令飞行」官方文档页（页面日期 **2025-11-12**，Dock1 页中最新）。Topic：`thing/product/{gateway_sn}/events`（up）/ `services`（down）/ `services_reply`（up）/ `drc/down`（down）/ `drc/up`（up）。**6 events + 32 services + 6 DRC**。

**判定**：**method 全量覆盖 ✓**（`EventMethod` 6、`ServiceMethod` 32、`DrcMethod`/`DrcUpMethod` 6 均存在）；多数 POJO 已覆盖 ✓。**新结论 3 项**：①`drone_control`/`heart_beat.seq` **废弃口径跨页冲突**（§5-109）②`hsi_info_push` Javadoc 偏差（§5-110）③`takeoff_to_point` Dock1 页为 O4 页子集（§5-111）。

**A61-1 Events（6）**

| method | SDK | 判定 |
|---|---|---|
| `fly_to_point_progress` | `EventMethod.FLY_TO_POINT_PROGRESS` ✓ + `FlyToPointProgressData` ✓（`need_reply=1`） | ✓ |
| `takeoff_to_point_progress` | `EventMethod.TAKEOFF_TO_POINT_PROGRESS` ✓ + `TakeoffToPointProgressData` ✓ | ✓ |
| `drc_status_notify`（**已废弃**，文档注明"本协议不再维护且不准确"） | `EventMethod.DRC_STATUS_NOTIFY` ✓（无 POJO） | ✓ 不建模（文档明确不再维护） |
| `joystick_invalid_notify` | `EventMethod.JOYSTICK_INVALID_NOTIFY` ✓ + `JoystickInvalidNotifyData` ✓ | ✓ |
| `poi_status_notify` | `EventMethod.POI_STATUS_NOTIFY` ✓ + `PoiCircleStatusData` ✓（`@Inferred`，方法名 `poi_circle_status` vs 文档 `poi_status_notify`） | ✓ |
| `camera_photo_take_progress` | `EventMethod.CAMERA_PHOTO_TAKE_PROGRESS` ✓ + `CameraPhotoTakeProgressData` ✓（嵌套 output/progress/ext） | ✓ |

**A61-2 Services（32）** —— method **32/32 存在 ✓**；reply 均仅 `result`（`NoOutputReply` 覆盖 ✓，`camera_photo_take` 除外见 A61-4）。data 结构：`flight_authority_grab`(null)/`payload_authority_grab`(payload_index)/`drc_mode_enter`(mqtt_broker{address, client_id, username, password, expire_time(int,秒), enable_tls(bool)}, osd_frequency, hsi_frequency)/`drc_mode_exit`(null)/`takeoff_to_point`(见 A61-5)/`fly_to_point`/`fly_to_point_stop`(null)/`fly_to_point_update`/`camera_mode_switch`/`camera_photo_take`/`camera_photo_stop`/`camera_recording_start`/`camera_recording_stop`/`camera_screen_drag`/`camera_aim`/`camera_focal_length_set`/`gimbal_reset`/`camera_look_at`/`camera_screen_split`/`photo_storage_set`/`video_storage_set`/`camera_exposure_mode_set`/`camera_exposure_set`/`camera_focus_mode_set`/`camera_focus_value_set`/`camera_point_focus_action`/`ir_metering_mode_set`/`ir_metering_point_set`/`ir_metering_area_set`/`poi_mode_enter`/`poi_mode_exit`(null)/`poi_circle_speed_set` —— 与 A10/A11 相机服务族一致 ✓。

**A61-3 DRC（6）**

| method | 通道 | SDK |
|---|---|---|
| `drone_control` | drc/down + drc/up | `DrcMethod.DRONE_CONTROL` ✓ + `DroneControlRequest` ✓；up `{result, output{seq}}` **无 Reply POJO**（§5-109 / §4） |
| `drone_emergency_stop` | drc/down(null) + drc/up(result) | ✓ |
| `heart_beat` | drc/down + drc/up | `DrcMethod.HEART_BEAT` ✓；`{seq, timestamp(int, ms)}` → SDK `HeartBeatRequest`/`HeartBeatReply` **缺 `seq`**（§5-109） |
| `hsi_info_push` | drc/up | `DrcUpMethod.HSI_INFO_PUSH` ✓ + `HsiInfoPushData` ✓；Javadoc 偏差（§5-110） |
| `delay_info_push` | drc/up | `DrcUpMethod.DELAY_INFO_PUSH` ✓ + `DelayInfoPushData` ✓ |
| `osd_info_push` | drc/up | `DrcUpMethod.OSD_INFO_PUSH` ✓ + `OsdInfoPushData` ✓ |

**A61-4 复认既有**

- `camera_photo_take` reply：文档**字段表**含 `output{status{in_progress}}`，但**同页 example 仅 `{result}`** → **文档自身不一致**；SDK `NoOutputReply` 取 example 口径 → §4 `CameraPhotoTakeReply`（新建）仍待裁决（§3 L748-753）。
- §5-102（`simulate_mission` 无 `altitude`）：A61 `takeoff_to_point.simulate_mission` **亦仅 3 字段**（`is_enable`/`latitude`/`longitude`，无 `altitude`）→ **二次印证**。
- §5-74（DRC 前缀）：A61 `drc/down` wire 为 `drone_control`/`drone_emergency_stop`/`heart_beat`（**无 `drc_` 前缀**，飞行 4 件套）→ 与 §5-74 裁决一致 ✓。
- `poi_status_notify.reason` 0-8 / `camera_photo_take_progress` `current_step`{3000,3002,3005}·`camera_mode`{3} / `joystick_invalid_notify.reason` 0-4 / `camera_mode_switch.camera_mode`{0,1,2,3} → 与 §3.2 一致 ✓。

**A61-5 `takeoff_to_point` 字段（Dock1 页）**

`target_latitude`/`target_longitude`/`target_height`(float 2-1500)/`security_takeoff_height`(20-1500)/`rth_altitude`(int 2-1500)/`rc_lost_action`(enum 0悬停/1着陆/2返航)/`exit_wayline_when_rc_lost`(**【废弃】** enum 0/1)/`commander_mode_lost_action`(enum 0/1)/`commander_flight_height`(float 2-3000)/`flight_id`/`max_speed`(int 1-15)/`simulate_mission`{`is_enable`/`latitude`/`longitude`}。**无** `rth_mode`/`commander_flight_mode`/`flight_safety_advance_check`（O4 页 A10 有）→ §5-111。

**A61-6 附注**

- **新增待确认项见 §5-109 / §5-110 / §5-111**；**新增 §4 行**（`HsiInfoPushData` Javadoc、`HeartBeat*`/`DRONE_CONTROL` 废弃口径复核、`TakeoffToPointRequest` 字段集核对）。
- `drc_status_notify` 文档标题明确「（已废弃）本协议不再维护且不准确」→ SDK 不建模 POJO 正确 ✓，复认既有决策。
- Dock1 页**多为既有 `dock3/*`/O4 页的镜像/子集**（与 A57/A58/A59/A60 同现象），但本页 `drc` 段与 O4 页在 `drone_control`/`heart_beat` 废弃口径上**首次出现实质性冲突**（§5-109）。

> **A61 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A62. Dock1 AirSense 页（2026-10-14，AI 直接审计 DJI 官方页原文）

**来源**：Dock1「AirSense」官方文档页（页面日期 **2025-03-19**；Topic `thing/product/{gateway_sn}/events`（up），唯一 method `airsense_warning`）。

**判定**：**A30-1（O4 `airspace-safety.html`）的同一 `airsense_warning` 事件 Dock1 类目镜像** —— 10 字段（`icao`/`warning_level`/`latitude`/`longitude`/`altitude`/`altitude_type`/`heading`/`relative_altitude`/`vert_trend`/`distance`）、类型、`data` 为数组、example `need_reply=1` **逐字一致**。**无新增字段/口径结论**（全部已被 A30-1 覆盖并经 §5-11 结案）。**新发现 1 项（元数据，§5-112）**：`@DocUrl` 归属待核。

**A62-1 逐字段对照（与 A30-1 一致 ✅）**

| 文档字段 | 文档 Type / 值域 | SDK `Alert` | 判定 |
|---|---|---|---|
| `icao` | text | `String` | ✓ |
| `warning_level` | enum_int `{0:无危险,1:等级一,2:等级二,3:等级三,4:等级四}`（5 值） | `Integer` | ✓（值域 0-4；本页首次给出完整 5 值标签，与 A30-1「0,1..4」一致） |
| `latitude` | float `{min:-90,max:90}` | `Double` | ✓ |
| `longitude` | float `{min:-180,max:180}` | `Double` | ✓ |
| `altitude` | **int**（米） | `Double` | ✗ → **改 `Integer`**（§5-11） |
| `altitude_type` | enum_int `{0:椭球高,1:海拔高}` | `Integer` | ✓ |
| `heading` | float | `Double` | ✓ |
| `relative_altitude` | **int**（米） | `Double` | ✗ → **改 `Integer`**（§5-11） |
| `vert_trend` | enum_int `{0:相对高度不变,1:相对高度上升,2:相对高度下降}` | `Integer` | ✓ |
| `distance` | **int** `{unit:米/m}` | `Double` | ✗ → **改 `Integer`**（§5-11） |

- `EventMethod.AIRSENSE_WARNING`（need_reply=**1** ✓）；[`AirSenseWarningData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/alert/AirSenseWarningData.java) 以自定义反序列化器按 `List<Alert>` 处理 `data: [...]` ✓（与文档 data 为数组一致）。
- **复认 §5-11**（A30 结案，待 TDD 落地）：`Alert.altitude`/`relativeAltitude`/`distance` `Double`→`Integer`；类 Javadoc `need_reply` **0 → 1**（现 [L29](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/alert/AirSenseWarningData.java#L29) 仍写 0）。

**A62-2 `@DocUrl` 归属待核（§5-112，元数据）**

- SDK [`AirSenseWarningData` `@DocUrl`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/alert/AirSenseWarningData.java#L55) = `.../dock/dock1/wayline.html`，`@Verified(basis=...)` 称「DJI 文档把 `airsense_warning` 归在 dock1/wayline.html 类目下」。
- 但 **A55 已确证 `dock1/wayline.html`＝「航线管理」页，其 15 条指令中 *无* `airsense_warning`**；本页（Dock1「AirSense」，2025-03-19）才是该事件归属页 → **`@DocUrl` 疑似指错页**。
- **待确认（非阻断）**：本页 URL 未提供 → **不猜测**；需开发者给出该 AirSense 页 URL 后据实更正 `@DocUrl` 与 `@Verified(basis)`。

**A62-3 附注**

- 本页为 Dock1 类目**独立 AirSense 页**（非 `dock1/wayline.html`），与 A58/A59/A60/A61 同属「Dock1 页多为既有 O4/Dock3 页镜像」现象，但**内容层面无新增**。
- **无新增 §4 行**（仅**修正既有** `AirSenseWarningData.java` 行以补记 `@DocUrl` 归属）；**新增 §5-112**。

> **A62 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A63. Dock1 自定义飞行区页（2026-10-14，AI 直接审计 DJI 官方页原文）

**批次性质**：Dock1「自定义飞行区」页原文（页面日期 **2025-03-19**；Topic `thing/product/{gateway_sn}/events`（up）/ `services`（down）/ `requests`（up）；含 **2 events + 1 service + 1 request**）。用途：**首次以官方原文核对 SDK 4 个 flight-area 方法/POJO**（此前 4 类 `@Verified(basis)` 仅据 simulator `FlightAreaSimulator`）。另：页首声明「自定义飞行区文件名需满足 `geofence_{fileMD5}.json`；文件协议见模板」（模板本身未在本页给出，SDK 无 geofence 文件模型，**不在本 SDK 范围**）。

#### A63-1 Event 段 2 项（`events`/up）

| 文档 method | need_reply | SDK `EventMethod` | SDK POJO | 判定 |
|---|---|---|---|---|
| `flight_areas_sync_progress` | 1 | `FLIGHT_AREAS_SYNC_PROGRESS`(need_reply=1) ✓ | `FlightAreasSyncProgressData`（`status`/`reason`/`file{name,checksum}`）✓ | ✓ 一致 |
| `flight_areas_drone_location` | 0 | `FLIGHT_AREAS_DRONE_LOCATION`(need_reply=0) ✓ | `FlightAreasDroneLocationData`（`droneLocations[].areaDistance/areaId/isInArea`）✓ | ✓ 一致 |

- **判定：Event 段 2 项全部已覆盖 ✓**（method 常量 + POJO、字段/类型一致）。
- `flight_areas_sync_progress` 字段核对：`status`（`enum_string` `{fail 失败, switch_fail 使能开关失败, synchronized 已同步, synchronizing 同步中, wait_sync 待同步}` ✓，SDK 裸 `String`）、`reason`（`int`，1-13 返回码；SDK `Integer` ✓，**未建模枚举**，见 A63-4）、`file`（**`struct`** `{name(text), checksum(text SHA256)}` ✓，SDK 嵌套 `SyncFile` 一致）；示例 `need_reply=1` ✓。
- `flight_areas_drone_location` 字段核对：`drone_locations`（字段表 Type=`struct` 而 **example 为数组** → SDK `List<DroneLocationItem>` ✓ **处理正确**）、项 `{area_id(text→String), area_distance(float→Double), is_in_area(bool→Boolean)}` ✓；示例 `need_reply=0` ✓。

#### A63-2 Service 段 1 项（`services`/down + `services_reply`/up）

| 文档 method | SDK `ServiceMethod` | SDK Request POJO | 判定 |
|---|---|---|---|
| `flight_areas_update` | **✗ 缺失** | （无） | **✗ 缺失（§5-113）** |

- 文档：请求 **Data=null**；`services_reply` data `{result(int, 非 0 代表错误)}`。
- **判定：`flight_areas_update`（自定义飞行区更新指令）在 SDK 完全缺失 ❌** → 新登记 **§5-113**（补 `ServiceMethod.FLIGHT_AREAS_UPDATE` + 复用 `NoParameterRequest`/`NoOutputReply`）。
- **并更正 §5-76**：该 method 此前被列入「仅见于 DJI 仓库、未见于 v1.18 官方文档」的差集清单（§5-76 / A48）→ **本页证实其为官方协议**，从该清单移出。

#### A63-3 Requests 段 1 项（`requests`/up + `requests_reply`/down）

| 文档内容 | SDK | 判定 |
|---|---|---|
| 请求 `flight_areas_get` **Data=null** | `RequestsMethod.FLIGHT_AREAS_GET` ✓ + `FlightAreasGetRequest(){}` | ✓ method 存在；请求空 record **可承载**（惯例问题见 §5-116） |
| 回复 data `{result(int), output{ files: array<struct{name(text), url(text), checksum(text), size(int)}> }}` | `FlightAreasGetReply{ result(Integer), output{ SyncFile file } }`，`SyncFile{name, checksum}` | **✗ 口径偏差（§5-114）**：`file`（**单数** struct）≠ 文档 `files`（**数组**），且**缺 `url`/`size`** |

- **判定：method 常量 + 请求 ✓；回复 record 字段形态与文档不符 ❌** → **§5-114（breaking）**。
- example wire：`"output":{"files":[{"checksum":"sha256","name":"geofence_xxx.json","size":500,"url":"https://…"}]}`（**确证 `files` 为数组、含 `url`/`size`**）。
- 请求侧：文档 `Data: null`，SDK 用专用空 record `FlightAreasGetRequest()`（全仓仅 3 个空 record 之一）→ **§5-116**（是否复用 `NoParameterRequest`）。

#### A63-4 元数据与文档自身不一致

1. **`@DocUrl` 归属待核（§5-115，元数据）**：4 类 flight-area POJO `@DocUrl` 现统一 = `.../dock/dock3/wayline.html`；**A41 已确证 `dock3/wayline.html`（航线管理）原文不含任何 flight_areas 内容** → 归属应为「自定义飞行区」页（本页为 Dock1，URL 待开发者提供；`@Verified(basis)` 可由 simulator 升为官方文档）。
2. **文档自身不一致（记录在案，不做 SDK 改动）**：`flight_areas_sync_progress.reason` 字段表 constraint 列 **1-13**（返回码），但同页 example 为 **`reason: 0`**（0 未列于约束）→ 与 §5-55 同类文档偏差，SDK `Integer` 可承载 0。
3. **`reason` 未建模枚举（附注，可选）**：文档给出 1-13 语义（1 解析云端文件失败 / 2 获取飞行器端文件失败 / 3 从云端下载失败 / 4 链路翻转失败 / 5 传输文件失败 / 6 disable 失败 / 7 自定义飞行区删除失败 / 8 飞行器端加载作业区域数据失败 / 9 enable 失败 / 10 机场增强图传无法关闭 / 11 飞行器开机失败 / 12 checksum 校验失败 / 13 同步异常超时），SDK 现裸 `Integer`；是否枚举化由开发者决定（同 §5-106 口径）。

**新登记 4 项（§5-113 ~ §5-116，均非阻断、待开发者裁决）**：见 §5 表与 §0 / §2 / §4。

> **A63 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A64. Dock1 PSDK 功能页（2026-10-14，AI 直接审计 DJI 官方页原文）

**批次性质**：Dock1「PSDK 功能」页原文（页面日期 **2025-03-19**；Topic `thing/product/{gateway_sn}/events`（up）/ `services`（down）/ `requests`（up）；含 **4 events + 8 services + 1 request**）。**判定：本页为已审计 Dock3「PSDK 功能（`psdk.html`）」页的 Dock1 镜像**（换 dock 型号，内容一致）；SDK 相关 POJO `@Verified(basis)` 原据 simulator `PsdkSimulator`，本页可作官方原文依据。

#### A64-1 Events 段 4 项（`events`/up）

| 文档 method | need_reply(页) | SDK `EventMethod` | SDK POJO | 判定 |
|---|---|---|---|---|
| `psdk_ui_resource_upload_result` | 例无标注（SDK 0） | `PSDK_UI_RESOURCE_UPLOAD_RESULT`(0) ✓ | `PsdkUiResourceUploadResultData{psdkIndex, objectKey, size, result}` ✓ | ✓ 一致 |
| `psdk_floating_window_text` | 例无标注（SDK 0） | `PSDK_FLOATING_WINDOW_TEXT`(0) ✓ | `PsdkFloatingWindowTextData{psdkIndex, value}` ✓ | ✓ 一致 |
| `speaker_audio_play_start_progress` | 例无标注（SDK 1） | `SPEAKER_AUDIO_PLAY_START_PROGRESS`(1) ✓ | `SpeakerAudioPlayStartProgressData{result, output:SpeakerOutput}` ✓ | ✓ 一致 |
| `speaker_tts_play_start_progress` | 例无标注（SDK 1） | `SPEAKER_TTS_PLAY_START_PROGRESS`(1) ✓ | `SpeakerTtsPlayStartProgressData{result, output:SpeakerOutput}` ✓ | ✓ 一致 |

- **判定：Event 段 4 项全部已覆盖 ✓**（method 常量 + POJO、字段/类型一致）。
- 页示例未标 `need_reply`；SDK 取 0（psdk 两事件）/1（speaker 两事件），依据为 simulator（**非本页原文**）→ 属既有 `@Inferred` 范畴（A6/A8 已录）。
- `speaker_*_progress.output` 共享 [`SpeakerOutput{psdkIndex, status, md5, progress:SpeakerProgress{percent, stepKey}}`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/speaker/SpeakerOutput.java)；`step_key` 值域：audio `{change_work_mode, download, encoding, play, upload}`、tts `{change_work_mode, play, upload}` → SDK 裸 `String` 可承载两套（**§5-106** 未枚举）。
- **文档自相矛盾（复认 A6）**：`speaker_tts_play_start_progress` 页 example `output.status="success"`，但同页 `status` 枚举仅 `{in_progress, ok}`。

#### A64-2 Services 段 8 项（`services`/down + `services_reply`/up）

| 文档 method | 请求字段 | SDK `ServiceMethod` + Request POJO | 判定 |
|---|---|---|---|
| `psdk_widget_value_set` | `{psdk_index, index, value(int)}` | `PSDK_WIDGET_VALUE_SET` ✓ + `PsdkWidgetValueSetRequest` ✓ | ✓（reply `{result}` → `NoOutputReply`） |
| `psdk_input_box_text_set` | `{psdk_index, value(text ≤128B)}` | `PSDK_INPUT_BOX_TEXT_SET` ✓ + `PsdkInputBoxTextSetRequest` ✓ | ✓ |
| `speaker_audio_play_start` | `{psdk_index, file{name, url, md5, format{pcm}}}` | `SPEAKER_AUDIO_PLAY_START` ✓ + `SpeakerAudioPlayStartRequest` ✓ | ✓ |
| `speaker_tts_play_start` | `{psdk_index, tts{name, text, md5}}` | `SPEAKER_TTS_PLAY_START` ✓ + `SpeakerTtsPlayStartRequest` ✓ | ✓ |
| `speaker_replay` | `{psdk_index}` | `SPEAKER_REPLAY` ✓ + `SpeakerReplayRequest` ✓ | ✓ |
| `speaker_play_stop` | `{psdk_index}` | `SPEAKER_PLAY_STOP` ✓ + `SpeakerPlayStopRequest` ✓ | ✓ |
| `speaker_play_mode_set` | `{psdk_index, play_mode enum_int{0 单次播放,1 循环播放(单曲)}}` | `SPEAKER_PLAY_MODE_SET` ✓ + `SpeakerPlayModeSetRequest` ✓ | ✓ |
| `speaker_play_volume_set` | `{psdk_index, play_volume(0–100)}` | `SPEAKER_PLAY_VOLUME_SET` ✓ + `SpeakerPlayVolumeSetRequest` ✓ | ✓ |

- **判定：Service 段 8/8 已覆盖 ✓**（method + POJO + 字段一致）；8 项 `services_reply` 均为 `{result(int)}` → 复用 `NoOutputReply` ✓。

#### A64-3 Requests 段 1 项（`requests`/up + `requests_reply`/down）

| 文档内容 | SDK | 判定 |
|---|---|---|
| 请求 `storage_config_get` `{module enum_int{0 媒体, 1 psdk ui 资源}}` | `RequestsMethod.STORAGE_CONFIG_GET` ✓ + [`StorageConfigGetRequest`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/config/StorageConfigGetRequest.java)`(module)` ✓ | method ✓；**`module=1` 语义偏差（§5-117）** |
| 回复 `{result, output{bucket, credentials{access_key_id, access_key_secret, expire, security_token}, endpoint, provider enum_string{ali,aws,minio}, region, object_key_prefix}}` | [`StorageConfigGetReply`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/request/config/StorageConfigGetReply.java)`(result, Output{bucket, endpoint, region, provider, objectKeyPrefix, credentials})` + `Credentials{accessKeyId, accessKeySecret, securityToken, expireTime}` | ✓ 结构一致；已知偏差 **§5-52**（`expire` 文档 `int` vs `Long`）、**§5-97**（`expireTime` wire key ≠ `expire`）、**§5-99**（`provider` 未枚举） |

#### A64-4 新登记 / 复认

1. **新登记 §5-117（口径）**：`storage_config_get.module` — 本页 `{0 媒体, 1 psdk ui 资源}`，SDK Javadoc 记 `1=日志` → 核对 `media.html` 后统一。
2. **新登记 §5-118（元数据）**：`speaker_*` 事件 3 类 POJO `@DocUrl` 现指 `dock3/wayline.html`，与 services 侧 `dock3/psdk.html` 不一致 → 归属待核（推测应为 `psdk.html`）。
3. **复认**：§5-52 / §5-97（`expire`）、§5-99（`provider` 未枚举）、§5-106（`speaker.progress.step_key` 未枚举）、A6 文档自相矛盾（tts `status="success"`）。

**新登记 2 项（§5-117 ~ §5-118，非阻断、待开发者裁决）**：见 §5 表与 §0 / §2 / §4。

**📌 溯源更正（A71，2026-10-14）**：本批次「批次性质」称「本页为**已审计** Dock3 `psdk.html` 页的 Dock1 镜像」——经 A71 核实，§8 此前**无** Dock3 `psdk.html` 独立附录，该「已审计」表述**不准确**；Dock3 `psdk.html` 的**首个独立原文记录为 A71**（内容与本 A64 逐字一致，双向确证镜像关系）。

> **A64 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A65. Dock1 PSDK 互联互通页（2026-10-14，AI 直接审计 DJI 官方页原文）

**批次性质**：Dock1「PSDK 互联互通」页原文（页面日期 **2025-03-19**；Topic `thing/product/{gateway_sn}/events`（up）/ `services`（down）；含 **1 event + 1 service**）。用途：**首次以官方原文核对「自定义消息传输」2 个 method**（报告此前**无任何附录覆盖** `custom_data_transmission_from_psdk` / `custom_data_transmission_to_psdk`——此二者由 simulator 建设，未做过官方原文审计）。

#### A65-1 Event 段 1 项（`events`/up）

| 文档 method | need_reply(页) | SDK `EventMethod` | SDK POJO | 判定 |
|---|---|---|---|---|
| `custom_data_transmission_from_psdk` | 例未标注 | `CUSTOM_DATA_TRANSMISSION_FROM_PSDK`(0, `@Inferred`) ✓ | [`CustomDataFromPsdkData{value(String)}`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/psdk/CustomDataFromPsdkData.java) ✓ | ✓ 一致 |

- 字段：`value`（text，长度 <256）✓。
- **`need_reply` 仍为 `@Inferred`（=0）**：本页 example 未标注 `need_reply`，官方原文未给出 → **无法解除** `@Inferred`，保留（待真机验证）。
- `@DocUrl` = `.../dock3/psdk-transmit-custom-data.html` — **与本页（互联互通）归属一致 ✓**（Dock3 对应页）。

#### A65-2 Service 段 1 项（`services`/down + `services_reply`/up）

| 文档内容 | SDK | 判定 |
|---|---|---|
| `custom_data_transmission_to_psdk` 请求 data `{value(text, 长度<256)}`；reply `{result(int, 非0错误)}` | `ServiceMethod.CUSTOM_DATA_TRANSMISSION_TO_PSDK` ✓ + [`CustomDataTransmissionToPsdkRequest{value(String)}`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/psdk/CustomDataTransmissionToPsdkRequest.java) ✓；reply → `NoOutputReply` ✓ | method/POJO ✓；**`@Verified` 依据 / `@DocUrl` 需更正（§5-119）** |

- **关键**：SDK 该类 `@Verified(basis)` 声称「**DJI 文档未显式列出此方法**，依据 hivemind 实测」→ **本页为官方原文，该声明不成立**；`@DocUrl` 现指 `psdk.html` → 应改为 `psdk-transmit-custom-data.html`。字段/类型/枚举无偏差。

#### A65-3 结论

1. **新登记 §5-119（元数据）**：`CustomDataTransmissionToPsdkRequest` 的 `@Verified(basis)` 误称「文档未列出」+ `@DocUrl` 归属更正。
2. **其余全部一致 ✓**，无缺失 method、无字段偏差、无枚举偏差。
3. 附注：同页无 ESDK 变体（`custom_data_transmission_from_esdk` / `..._to_esdk` 不在本页范围内，SDK 已有对应 POJO，另行审计）。

> **A65 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A66. Dock1 ESDK 互联互通页（2026-10-14，AI 直接审计 DJI 官方页原文）

**批次性质**：Dock1「ESDK 互联互通」页原文（页面日期 **2025-03-19**；Topic `thing/product/{gateway_sn}/events`（up）/ `services`（down）；含 **1 event + 1 service**）。用途：**首次以官方原文核对 ESDK 自定义数据传输 2 个 method**（报告此前无任何附录覆盖）。与 **A65（PSDK 互联互通）为对称双胞胎页**（仅 PSDK↔ESDK 替换）。

#### A66-1 Event 段 1 项（`events`/up）

| 文档 method | need_reply(页) | SDK `EventMethod` | SDK POJO | 判定 |
|---|---|---|---|---|
| `custom_data_transmission_from_esdk` | 例未标注 | `CUSTOM_DATA_TRANSMISSION_FROM_ESDK`(0, `@Inferred`) ✓ | [`CustomDataFromEsdkData{value(String)}`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/esdk/CustomDataFromEsdkData.java) ✓ | ✓ 一致 |

- 字段：`value`（text，长度 <256）✓。
- **`need_reply` 仍为 `@Inferred`（=0）**：本页 example 未标注 `need_reply` → 官方原文未给出 → 无法解除，保留待真机验证（与 A65 同）。
- `@DocUrl` = `.../dock3/esdk-transmit-custom-data.html` — **与本页归属一致 ✓**。

#### A66-2 Service 段 1 项（`services`/down + `services_reply`/up）

| 文档内容 | SDK | 判定 |
|---|---|---|
| `custom_data_transmission_to_esdk` 请求 data `{value(text, <256)}`；reply `{result(int, 非0错误)}` | `ServiceMethod.CUSTOM_DATA_TRANSMISSION_TO_ESDK` ✓ + [`CustomDataTransmissionToEsdkRequest{value(String)}`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/esdk/CustomDataTransmissionToEsdkRequest.java) ✓；reply → `NoOutputReply` ✓ | method/POJO ✓；**`@Verified` 依据 / `@DocUrl` 需更正（§5-120）** |

- **关键**：该 SDK 类 `@Verified(basis)` 声称「**DJI Dock3 esdk.html 文档访问受限（cookie 验证）**，依据 hivemind 实测」→ **本页为官方原文，访问受限/未列出的前提不再成立**；`@DocUrl` 现指 `esdk.html` → 应核对为 `esdk-transmit-custom-data.html`。字段/类型/枚举无偏差。

#### A66-3 结论

1. **新登记 §5-120（元数据）**：`CustomDataTransmissionToEsdkRequest` 的 `@Verified(basis)` 误称「文档访问受限」+ `@DocUrl` 归属核对。
2. **其余全部一致 ✓**，无缺失 method、无字段偏差、无枚举偏差。
3. **与 A65 对称总结**：PSDK/ESDK 两组共 4 个「自定义数据传输」method（2 event + 2 service）**现均由官方原文确认**，其 SDK `@Verified(basis)` 中「文档未列出 / 访问受限」表述**均已失效**，待更正为官方文档依据（§5-119 / §5-120）。

> **A66 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A67. Dock1 远程解禁页（2026-10-14，AI 直接审计 DJI 官方页原文）

**批次性质**：Dock1「远程解禁」页原文（页面日期 **2025-03-19**；Topic `thing/product/{gateway_sn}/services`（down）/ `services_reply`（up）；含 **3 services**：`unlock_license_switch` / `unlock_license_update` / `unlock_license_list`）。**判定：本页为已审计 A21（Dock3 `flysafe.html` 解禁证书 3 项）的 Dock1 类目镜像**。**价值：对 A21 缺口（回复 POJO）做二次官方原文确证 + 复核 3 个请求 POJO 字段。**

#### A67-1 `unlock_license_switch`

| 文档 | SDK | 判定 |
|---|---|---|
| 请求 `{license_id(int), enable(bool)}`；reply `{result(int), license_id(int)}`（**扁平**） | `ServiceMethod.UNLOCK_LICENSE_SWITCH` ✓ + [`UnlockLicenseSwitchRequest{int licenseId, boolean enable}`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/flysafe/UnlockLicenseSwitchRequest.java) ✓；**reply ❌ 缺 `UnlockLicenseSwitchReply`** | 请求 ✓；**回复 POJO 缺失（A21 缺口 2，仍未建）** |

#### A67-2 `unlock_license_update`

| 文档 | SDK | 判定 |
|---|---|---|
| 请求 `{file(struct, 可缺省){url(text), fingerprint(text MD5)}}`；reply `{result(int)}` | `ServiceMethod.UNLOCK_LICENSE_UPDATE` ✓ + [`UnlockLicenseUpdateRequest{LicenseFile{url, fingerprint}}`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/flysafe/UnlockLicenseUpdateRequest.java) ✓（`file` 可缺省 → 对象引用可 null ✓）；reply → `NoOutputReply` ✓ | ✓ 一致（含 `file` 可缺省语义） |

#### A67-3 `unlock_license_list`

| 文档 | SDK | 判定 |
|---|---|---|
| 请求 `{device_model_domain enum_int{0 飞行器, 3 机场}}` | `ServiceMethod.UNLOCK_LICENSE_LIST` ✓ + [`UnlockLicenseListRequest{int deviceModelDomain}`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/service/flysafe/UnlockLicenseListRequest.java) ✓ | 请求 ✓ |
| reply `{result(int), device_model_domain, consistence(bool), licenses(array)}`，`licenses[]` = `common_fields{license_id, name, type(0–6), group_id, user_id, device_sn, begin_time, end_time, user_only, enabled}` + 7 类 unlock 之一（`area_unlock{area_ids[]}`/`circle_unlock{radius, latitude, longitude, height}`/`country_unlock{country_number, height}`/`height_unlock{height}`/`polygon_unlock{points[]{latitude, longitude}}`/`power_unlock{}`/`rid_unlock{level{1 欧盟,2 中国}}`） | **reply ❌ 缺 `UnlockLicenseListReply` + `License` + `CommonFields` + `AreaUnlock`/`CircleUnlock`/`CountryUnlock`/`HeightUnlock`/`PolygonUnlock`/`PolygonPoint`/`PowerUnlock`/`RidUnlock`（共 11 类）** | **回复 POJO 缺失（A21 缺口 1，共 12 类含 switch，仍未建）** |

#### A67-4 结论 / 复认

1. **无新增 §5 项**：A21 缺口 1+2 已登记；§5-21（扁平回复 → 新增 `FlatReplyEnvelope`，方案 B）、§5-22（`ReplyEnvelope` 补 `gateway`，方案 A）已决策 → **本页二次确证**（reply data 扁平、example 含 `gateway:""`；`type` 7 值 `{0 授权区,1 自定义圆形,2 国家/地区,3 限高,4 自定义多边形,5 功率,6 RID}`；`power_unlock` 为空 `struct {}`）。
2. **状态核对**：`flysafe` 包现仅 4 文件（3 请求 POJO + `package-info`）；**12 个回复 record 与 `FlatReplyEnvelope` 均未创建** → §4 L317–319 仍为**计划项**（受「原文收齐前不编码」门禁约束）。
3. **文档附注**：`common_fields.type` 值 `"6":" RID 解禁"` 含前导空格（文档笔误）；`country_unlock.country_number` constraint 为空 `{}`（SDK `Integer` 可承载）。
4. 3 个请求 POJO 字段/类型/`@DocUrl`（`dock3/flysafe.html`）**全部一致 ✓**。

> **A67 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A68. Dock1 航线管理页原文投喂（页面日期 2025-11-12）— 逐字复核 A55（重复页，新增差异 0）

**来源**：开发者于本窗口投喂 `航线管理__2025….txt` 官方页原文。**判定：与已登记 A55（Dock1 航线管理页，2025-11-12）为同一页** —— 指令集、字段表、示例、页面日期**逐字一致**。

**A68-0 复核结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| 方法常量存在 ✓ | 15/15 | `EventMethod`/`ServiceMethod`/`RequestsMethod` 枚举齐全（同 A55-1） |
| POJO 已覆盖 ✓ | 13/15 | 例外同 A55（`flight_setup_exception_notify` 无 POJO；`flight_setup_abort` + 4 组 data=null 复用 `NoParameterRequest`） |
| **新增差异** | **0** | **与 A55 逐字一致 → 重复页复核（同 A56 先例）** |

**A68-1 逐字对照要点（与 A55 完全一致）**

| 项 | 值 | 复核 |
|---|---|---|
| Events（5） | `flight_setup_exception_notify` / `device_exit_homing_notify` / `flighttask_progress` / `flighttask_ready` / `return_home_info` | ✓ 同 A55 |
| Services（9） | `flight_setup_abort` / `flighttask_create`（**废弃**） / `flighttask_prepare` / `flighttask_execute` / `flighttask_undo` / `flighttask_pause` / `flighttask_recovery` / `return_home` / `return_home_cancel` | ✓ 同 A55 |
| Requests（1） | `flighttask_resource_get` | ✓ 同 A55 |
| `flight_setup_exception_notify` data | `sn` / `timeout_time`(2–10) / `timestamp` / `flight_type`(1 航线/2 指令飞行)；example 含 `flight_id`、`need_reply=1` | ✓ 同 A55（→ §5-100） |
| `flight_setup_abort` reply 码 | `0` / `326108` / `326109` | ✓ 同 A55（→ §5-101） |
| `return_home` reply | `result` + `output{status}`（8 值 enum_string，无 `partially_done`） | ✓ 同 A55（→ §5-16/§5-51） |
| `simulate_mission` | Dock1 页 **3 字段**（无 `altitude`） | ✓ 同 A55（→ §5-102） |
| `flighttask_prepare` Dock1 字段集 | **无** `rth_mode`/`wayline_precision_type`/`flight_safety_advance_check` | ✓ 同 A55（→ §5-103） |
| `flighttask_progress` | `output{ext/status/progress}` + `result`；`wayline_mission_state`(0–9)、`break_reason`（超大值域）、`current_step`(0–48 + 65533/65534/65535) | ✓ 同 A55 |
| `device_exit_homing_notify.reason` | 文档 `enum_int`(0–10) vs example 字符串 `"0"` | ✓ 同 A55（同 A41/A42） |

**A68-2 复认（无新增 §5 项）**

- §5-100（`flight_setup_exception_notify` 无 POJO + `EventMethod` Javadoc 矛盾）**二次印证**
- §5-101（`flight_setup_abort` reply 码未入 Javadoc）**二次印证**
- §5-102（`simulate_mission.altitude` 机型归属）**二次印证**
- §5-103（Dock1 `flighttask_prepare` 字段子集）**二次印证**
- §5-56（`flighttask_create`（废弃）缺失）**四度印证**
- §5-16 / §5-51（`return_home` reply `{result, output{status}}`）**四度印证**

**A68-3 价值**

1. 将 A55 的来源由「AI 直接审计 DJI 官方页」**升级为「开发者投喂原文逐字复核」** → 确证 A55 无转录偏差。
2. 页面日期 2025-11-12（早于 v1.18 `2026.10.13`），属**现行版**。
3. **A55 已覆盖本窗口需求，A68 不新增附录结论**（除来源升级）。

> **A68 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A69. Dock1 自定义飞行区页原文投喂（页面日期 2025-03-19）— 逐字复核 A63（重复页，新增差异 0）

**来源**：开发者于本窗口投喂 Dock1「自定义飞行区」页原文（含 **2 events + 1 service + 1 request**）。**判定：与已登记 A63 同页，逐字一致**。

**A69-0 复核结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| Event 覆盖 ✓ | 2/2 | `flight_areas_sync_progress`(need_reply=1) / `flight_areas_drone_location`(need_reply=0) |
| Service | 0/1 | `flight_areas_update` **仍缺失**（§5-113，SDK 未实现） |
| Request 覆盖 ✓ | 1/1 | `flight_areas_get`（method + 请求 record ✓；回复形态偏差 §5-114） |
| **新增差异** | **0** | **与 A63 逐字一致 → 重复页复核** |

**A69-1 逐字对照要点（与 A63 完全一致）**

| 项 | 值 | 复核 |
|---|---|---|
| `flight_areas_sync_progress` | `status` enum_string 5 值（fail/switch_fail/synchronized/synchronizing/wait_sync）+ `reason` int 1–13 + `file{name, checksum(SHA256)}`；example `need_reply=1`、`reason:0` | ✓ 同 A63 |
| `flight_areas_drone_location` | `drone_locations`（表 Type=`struct` / example 为**数组**）∪ `{area_id string, area_distance float, is_in_area bool}`；`need_reply=0` | ✓ 同 A63 |
| `flight_areas_update` | 请求 **Data=null**；reply `{result(int, 非 0 错误)}` | ✓ 同 A63 → **SDK ✗ 缺失（§5-113）** |
| `flight_areas_get` | 请求 **Data=null**；reply `{result, output{files: array<{name,url,checksum,size}>}}` | ✓ 同 A63 → **SDK `output{SyncFile file}`（单数、缺 url/size）✗（§5-114 breaking）** |
| 文件名约束 | `geofence_{fileMD5}.json`；文件协议见模板（模板未在本页） | ✓ 同 A63（geofence 文件模型不在本 SDK 范围） |

**A69-2 复认（无新增 §5 项）**

- §5-113（`ServiceMethod` 缺 `flight_areas_update`）**二次印证**
- §5-114（`FlightAreasGetReply.Output` `file` 单数 vs 文档 `files` 数组，且缺 `url`/`size`，**breaking**）**二次印证**
- §5-115（4 类 flight-area POJO `@DocUrl` 归属待核）**二次印证**
- §5-116（`FlightAreasGetRequest` 专用空 record 未复用 `NoParameterRequest`）**二次印证**
- `flight_areas_sync_progress.reason` 值域 1–13 vs example `0`（**文档自身不一致**）**复认**
- `drone_locations` 字段表 `struct` vs example 数组（**文档自身不一致**，SDK 采信 example）**复认**

**A69-3 价值**：A63 来源由「AI 直接审计官方页」升级为「开发者投喂原文逐字复核」，确证无转录偏差。

> **A69 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A70. Dock3 媒体管理页原文投喂（页面日期 2026-08-19）— 逐字复核 A35 / A54（重复页，新增差异 0）

**来源**：开发者于本窗口投喂 Dock3「媒体管理」页原文（Topic `thing/product/{gateway_sn}/events`（up）/ `services`（down）/ `requests`（up）；含 **2 events + 1 service + 1 request**）。**判定：与 A35（`dock3/media.html`，2026-03-19）/ A54（Dock1「媒体管理」，2026-03-19）为同源页，内容逐字一致**；仅**页面日期更新为 2026-08-19**。

**A70-0 复核结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| 方法常量存在 ✓ | 4/4 | `EventMethod`(2) + `ServiceMethod`(1) + `RequestsMethod`(1) 齐全 |
| POJO 覆盖 ✓ | 4/4 | 4 条指令 POJO 全覆盖（含嵌套 record） |
| **新增差异** | **0** | **与 A35/A54 逐字一致 → 重复页复核** |

**A70-1 逐方法比对（与 A54 完全一致）**

| 通道 | method | 文档 data | SDK | 判定 |
|---|---|---|---|---|
| events | `highest_priority_upload_flighttask_media` | `flight_id`；`need_reply=1` | `EventMethod`(1) ✓ + `HighestPriorityUploadFlighttaskMediaData(flightId)` | ✓ |
| events | `file_upload_callback` | `file{ object_key, path, name, ext{ flight_id, drone_model_key, payload_model_key, is_original }, metadata{ gimbal_yaw_degree, absolute_altitude, relative_altitude, create_time, shoot_position{ lat, lng } } }`；`need_reply=1`；example 含 `cloud_to_cloud_id:"DEFAULT"` | `EventMethod`(1) ✓ + `FileUploadCallbackData(file→MediaFile{cloudToCloudId, ext, metadata, name, objectKey, path})` + `MediaFileExt`(4)/`MediaFileMetadata`(5)/`ShootPosition`(2) | ✓（除 `gimbal_yaw_degree`，§5-98） |
| services | `upload_flighttask_media_prioritize` | 请求 `flight_id`；reply `result` | `ServiceMethod` ✓ + `UploadFlighttaskMediaPrioritizeRequest(flightId)`；`NoOutputReply` | ✓ |
| requests | `storage_config_get` | 请求 `module` enum_int `{"0":"媒体"}`；reply `result` + `output{ bucket, credentials{ access_key_id, access_key_secret, expire, security_token }, endpoint, provider, region, object_key_prefix }` | `RequestsMethod` ✓ + `StorageConfigGetRequest(module)`；`StorageConfigGetReply(result, Output{bucket, endpoint, region, provider, objectKeyPrefix, credentials})` + `Credentials(accessKeyId, accessKeySecret, securityToken, expireTime)` | 偏差（`expireTime`→`expire`，§5-97） |

**A70-2 复认（无新增 §5 项）**

- §5-97（`StorageConfigGetReply.Credentials.expireTime` → 文档 `expire`，**breaking**；与 §5-52 同字段）**二次印证**
- §5-98（`MediaFileMetadata.gimbalYawDegree`：文档 Type=`float` vs example 字符串 `"-91.40"`，SDK `String` 对齐 example）**二次印证**
- §5-99（`provider` enum_string{ali,aws,minio} 未建模为枚举）**二次印证**
- §5-117（`storage_config_get.module`）**二次印证 + 新证据**：本页 `module` enum_int **仅 `{"0":"媒体"}`（未定义 `1`）** → 已追加核对说明至 §5-117

**A70-3 文档自身不一致（记录在案，不做 SDK 改动）**

1. `file_upload_callback.metadata.gimbal_yaw_degree`：字段表 Type=`float`，example 为字符串 `"-91.40"`（SDK 采信 example → `String`，同 §5-98）。
2. `file_upload_callback.metadata.create_time`：字段表 Type=`date {"format":"iso8601"}`，example 为 `"2021-05-10 16:04:20"`（**非 ISO8601**）；SDK `String` 可承载。

**A70-4 附注**

- 页面日期 **2026-08-19**（较 A35/A54 的 2026-03-19 **前移**），早于 v1.18（2026.10.13）→ **现行版**；A35/A54 结论**未失效**。
- `UploadFlighttaskMediaPrioritizeRequest.@DocUrl` 现指通用根页 `mqtt-dock.html`（A54 未标为偏差，**保持一致，不另立 §5 项**）。

> **A70 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A71. Dock3 PSDK 功能页原文投喂（页面日期 2026-08-19）— 逐字复核 A64（跨 Dock 镜像，新增差异 0）

**来源**：开发者于本窗口投喂 Dock3「PSDK 功能」页原文（Topic `thing/product/{gateway_sn}/events`（up）/ `services`（down）/ `requests`（up）；含 **4 events + 8 services + 1 request**）。**判定：与 A64（Dock1「PSDK 功能」，2025-03-19）内容逐字一致**（仅换 dock 型号与页面日期）。

**A71-0 复核结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| 方法常量存在 ✓ | 13/13 | Events 4 + Services 8 + Requests 1 齐全 |
| POJO 覆盖 ✓ | 13/13 | 全覆盖（含嵌套 record） |
| **新增差异** | **0** | **与 A64 逐字一致 → 跨 Dock 镜像复核** |

**A71-1 逐项对照（与 A64 完全一致）**

| 段 | method | 要点 | 复核 |
|---|---|---|---|
| events | `speaker_tts_play_start_progress` | result + output{psdk_index, status enum_string{in_progress,ok}, md5, progress{percent 0–100, step_key enum_string{change_work_mode,play,upload}}}；example `status="success"` | ✓ 同 A64（A6 文档自相矛盾复认） |
| events | `speaker_audio_play_start_progress` | 同上，`step_key{change_work_mode,download,encoding,play,upload}` | ✓ |
| events | `psdk_floating_window_text` | psdk_index{0–3} + value | ✓ |
| events | `psdk_ui_resource_upload_result` | psdk_index + object_key + size + result | ✓ |
| services×8 | `speaker_play_volume_set`(psdk_index, play_volume 0–100) / `speaker_play_mode_set`(psdk_index, play_mode enum_int{0单次,1循环}) / `speaker_play_stop`(psdk_index) / `speaker_replay`(psdk_index) / `speaker_tts_play_start`(psdk_index, tts{name,text,md5}) / `speaker_audio_play_start`(psdk_index, file{name,url,md5,format enum_string pcm}) / `psdk_input_box_text_set`(psdk_index, value ≤128B) / `psdk_widget_value_set`(psdk_index, index, value) | 各 reply `{result}` | ✓ 同 A64 |
| requests | `storage_config_get` | 请求 `module` enum_int `{"0":"媒体","1":"psdk ui 资源"}`；回复 `output{bucket, credentials{access_key_id, access_key_secret, expire, security_token}, endpoint, provider enum_string{ali,aws,minio}, region, object_key_prefix}` | ✓ 同 A64（`expireTime`→`expire` §5-97） |

**A71-2 复认（无新增 §5 项）**

- §5-117（`storage_config_get.module`）**二次印证**（同页 `{0 媒体, 1 psdk ui 资源}`）
- §5-118（`speaker_*` 事件 3 类 POJO `@DocUrl` 归属）**二次印证 + 新证据**：本页原文确含 `speaker_audio_play_start_progress`/`speaker_tts_play_start_progress` → 佐证归属应为 `psdk.html`（非 `wayline.html`）
- §5-52 / §5-97（`credentials.expire` 字段名/类型）、§5-99（`provider` 未枚举）、§5-106（`speaker.progress.step_key` 未枚举）**复认**
- A6 文档自相矛盾（`speaker_tts_play_start_progress` example `status="success"` 不在枚举 `{in_progress,ok}`）**复认**

**A71-3 溯源更正（重要）**

- A64 批次性质称「本页为**已审计** Dock3「PSDK 功能（`psdk.html`）」页的 Dock1 镜像」，但 **§8 无对应的独立 Dock3 `psdk.html` 附录**（A1–A70 均非该页）→ 该「已审计」表述**不准确**。
- **A71 为 Dock3 `psdk.html` 的首个独立原文记录**；A64 的 Dock1 镜像结论经 A71 双向确证（已在 A64 追加 📌 溯源更正）。

**A71-4 附注**

- 页面日期 **2026-08-19**（较 A64 的 2025-03-19 **前移**），早于 v1.18（2026.10.13）→ **现行版**。
- 服务示例未含 `gateway`（事件示例含）→ envelope 范畴，非 data 差异。

> **A71 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A72. Dock3 PSDK 互联互通页原文投喂（页面日期 2025-03-19）— 逐字复核 A65（跨 Dock 镜像，新增差异 0）

**来源**：开发者于本窗口投喂 Dock3「PSDK 互联互通」页原文（Topic `thing/product/{gateway_sn}/events`（up）/ `services`（down）；含 **1 event + 1 service**）。**判定：与 A65（Dock1「PSDK 互联互通」，2025-03-19）内容逐字一致**（仅换 dock 型号与页面日期）。

**A72-0 复核结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| 方法常量存在 ✓ | 2/2 | Event 1 + Service 1 齐全 |
| POJO 覆盖 ✓ | 2/2 | 全覆盖 |
| **新增差异** | **0** | **与 A65 逐字一致 → 跨 Dock 镜像复核** |

**A72-1 逐项对照（与 A65 完全一致）**

| 段 | method | 要点 | 复核 |
|---|---|---|---|
| events | `custom_data_transmission_from_psdk` | data `{value(text, 长度<256)}`；example `value="hello world"`；未标注 `need_reply` | ✓ 同 A65（`need_reply=0` 仍 `@Inferred`，无法解除） |
| services | `custom_data_transmission_to_psdk` | 请求 data `{value(text, <256)}`；reply `{result(int, 非0错误)}` → `NoOutputReply` | ✓ 同 A65 |

**A72-2 复认（无新增 §5 项）**

- **§5-119 新证据**：本页为「PSDK 互联互通」的 **Dock3 归属页**（`dock3/psdk-transmit-custom-data.html`）原文 → 佐证 §5-119 目标 `@DocUrl` 正确（事件侧 [`CustomDataFromPsdkData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/psdk/CustomDataFromPsdkData.java) `@DocUrl` 已指向该 URL，服务侧应一致）。
- **无 ESDK 变体**（`custom_data_transmission_from_esdk` / `..._to_esdk` 不在本页范围，见 A66 Dock1 ESDK 互联互通，另行审计）。

**A72-3 溯源**

- **A72 为 Dock3 `psdk-transmit-custom-data.html` 的首个独立原文记录**（A1–A71 中 A65 为 Dock1 变体，无 Dock3 独立附录）。

**A72-4 附注**

- 页面日期 **2025-03-19**（同 A65）→ 早于 v1.18（2026.10.13），**现行版**。
- 服务示例未含 `gateway`（事件示例含）→ envelope 范畴，非 data 差异（同 A71）。

> **A72 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A73. Dock3 ESDK 互联互通页原文投喂（页面日期 2025-03-19）— 逐字复核 A66（跨 Dock 镜像，新增差异 0）

**来源**：开发者于本窗口投喂 Dock3「ESDK 互联互通」页原文（Topic `thing/product/{gateway_sn}/events`（up）/ `services`（down）；含 **1 event + 1 service**）。**判定：与 A66（Dock1「ESDK 互联互通」，2025-03-19）内容逐字一致**（仅换 dock 型号）。

**A73-0 复核结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| 方法常量存在 ✓ | 2/2 | Event 1 + Service 1 齐全 |
| POJO 覆盖 ✓ | 2/2 | 全覆盖 |
| **新增差异** | **0** | **与 A66 逐字一致 → 跨 Dock 镜像复核** |

**A73-1 逐项对照（与 A66 完全一致）**

| 段 | method | 要点 | 复核 |
|---|---|---|---|
| events | `custom_data_transmission_from_esdk` | data `{value(text, 长度<256)}`；example `value="hello world"`；未标注 `need_reply` | ✓ 同 A66（`need_reply=0` 仍 `@Inferred`，无法解除） |
| services | `custom_data_transmission_to_esdk` | 请求 data `{value(text, <256)}`；reply `{result(int, 非0错误)}` → `NoOutputReply` | ✓ 同 A66 |

**A73-2 复认（无新增 §5 项）**

- **§5-120 新证据**：本页为「ESDK 互联互通」的 **Dock3 归属页**（`dock3/esdk-transmit-custom-data.html`）原文 → 佐证 §5-120 拟核对目标 `@DocUrl` 正确（事件侧 [`CustomDataFromEsdkData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/esdk/CustomDataFromEsdkData.java) `@DocUrl` 已指向该 URL，服务侧应一致）。
- **无 PSDK 变体**（`custom_data_transmission_from_psdk` / `..._to_psdk` 见 A65/A72 PSDK 互联互通，另行审计）。
- **与 A72 对称**：PSDK / ESDK 两组「互联互通」页的 Dock3 版（A72 / A73）均已取得官方原文记录。

**A73-3 溯源**

- **A73 为 Dock3 `esdk-transmit-custom-data.html` 的首个独立原文记录**（A1–A72 中 A66 为 Dock1 变体，无 Dock3 独立附录）。

**A73-4 附注**

- 页面日期 **2025-03-19**（同 A66）→ 早于 v1.18（2026.10.13），**现行版**。
- 服务示例未含 `gateway`（事件示例含）→ envelope 范畴，非 data 差异（同 A72）。

> **A73 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A74. Dock1 AirSense 页原文投喂复核（页面日期 2025-03-19）— 逐字复核 A62（重复页，新增差异 0）

**来源**：开发者于本窗口**重复投喂** Dock1「AirSense」页原文（Topic `thing/product/{gateway_sn}/events`（up），唯一 method `airsense_warning`）。**判定：与 A62（Dock1「AirSense」，2025-03-19）内容逐字一致**（同页重新投喂）。

**A74-0 复核结论**

| 结论 | 数量 | 说明 |
|---|---|---|
| 方法常量存在 ✓ | 1/1 | `EventMethod.AIRSENSE_WARNING`（need_reply=1） |
| 字段一致 ✓ | 10/10 | `icao`/`warning_level`/`latitude`/`longitude`/`altitude`/`altitude_type`/`heading`/`relative_altitude`/`vert_trend`/`distance` |
| **新增差异** | **0** | **与 A62 逐字一致 → 重复页复核** |

**A74-1 逐项对照（与 A62 完全一致）**

| 段 | method | 要点 | 复核 |
|---|---|---|---|
| events | `airsense_warning` | `data` 为**数组**（`List<Alert>`）；`warning_level` 完整 5 值 `{0无危险,1等级一,2等级二,3等级三,4等级四}`；`altitude_type` `{0椭球高,1海拔高}`；`vert_trend` `{0不变,1上升,2下降}`；example `need_reply=1` | ✓ 同 A62（[`AirSenseWarningData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/event/alert/AirSenseWarningData.java) 自定义反序列化 ✓） |

**A74-2 复认（无新增 §5 项）**

- **§5-112 未解除（无新证据）**：本页为 `airsense_warning` 的**归属页**（Dock1「AirSense」），但本次投喂**仍只含内容、未含该页 URL** → `@DocUrl`（现误指 `dock1/wayline.html`，A55 确证该页不含 `airsense_warning`）的更正**仍待 URL**（不猜测）。
- **复认 §5-11**（`Alert.altitude`/`relativeAltitude`/`distance` `Double`→`Integer`、类 Javadoc `need_reply` 0→1，已结案）。

**A74-3 溯源**

- **A74 为 Dock1「AirSense」页的首次「重复投喂」复核记录**；A62 为其首个原文记录（来源为「开发者投喂」）。

**A74-4 附注**

- 页面日期 **2025-03-19**（同 A62）→ 早于 v1.18（2026.10.13），**现行版**。
- **投喂目的**：收尾 §5-112（URL）——本次仅投喂内容、未含 URL → **§5-112 未解除**。

> **A74 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A75. Dock1 设备属性页原文投喂复核（页面日期 2025-03-19）— 逐字复核 A50（重复页，新增差异 0）

**来源**：开发者于本窗口以文件投喂 Dock1「设备属性」页原文（长文本 `设备属性__2025....txt`，**410 行**）。**判定：与 A50（Dock1「设备属性」，2025-03-19）同页、内容一致**。

**A75-0 复核结论**

| 结论 | 说明 |
|---|---|
| 页面一致性 | 页头「设备属性 / 2025-03-19 / 5 分 / 3 用户已评分」+ `pushMode`/`accessMode` 说明 + 属性表 + example JSON **与 A50 同页** |
| 顶层属性表 | `mode_code` 0-5 ✓、`flighttask_step_code` `{0-5,255,256}` ✓、`electric_supply_voltage` Type=`int`/V ✓、`drone_authority_info`（pushMode=0）✓ |
| **新增差异** | **0** |

**A75-1 复认（无新增 §5 项）**

- **§5-83**（`electric_supply_voltage` 页 `int`/V vs SDK `Double`）复认 ✓（本文件第 74 行确为 `int`）。
- **§5-84**（`drone_authority_info` 在页、`DockOsd` 无模型字段）复认 ✓（本文件第 128 行）。
- **§5-88 未解除（无新证据）**：本页日期仍为 **2025-03-19** → 只能证明**开发者所持副本未变**，**不能证明 DJI 现行页未被更新** → 仍需开发者主动确认；**内容已收齐（A50/A75），无需再投原文**。

**A75-2 溯源**

- **A75 为 Dock1「设备属性」页的首次「重复投喂」复核记录**；A50 为其首个原文记录（来源为「开发者投喂」）。

**A75-3 附注**

- 文件 410 行（属性表 + example JSON）；**无** `commander_flight_height`/`commander_mode_lost_action`（二者属 M4D/M4TD 页 §5-80，不在 Dock1 页范围）。
- 页面日期 **2025-03-19**（同 A50）→ 早于 v1.18（2026.10.13），**现行版**。

> **A75 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A76. M4D/M4TD 飞行器设备属性页原文投喂复核（页面日期 2026-09-01）— 逐字复核 A49 + **解除 §5-80 原文前提**

**来源**：开发者于本窗口以文件投喂 M4D/M4TD「设备属性」页原文（长文本 `M4D_M4TD 设....txt`，**225 行**）。**判定：与 A49（同页，26303 字节 / 227 行 / 56 顶层属性，2026-10-14 AI 审计）同页**；本次为**开发者投喂原文复核**（来源由「AI 直接审计官方页」升级为「开发者投喂原文」）。

**A76-0 复核结论**

| 结论 | 数量/说明 |
|---|---|
| 顶层属性 | **56**（与 A49 一致） |
| 已覆盖 ✓ | 绝大多数（字段名/类型/pushMode/accessMode 与 SDK 逐项一致） |
| **新增差异** | **0**（页面结构、属性表与 A49 一致） |
| **新增证据** | **1 项**：`commander_mode_lost_action` **值域**（A49 未展开）→ **§5-80 原文前提解除** |

**A76-1 §5-80 原文核实（本窗口关键产出）**

本页第 182–184 行给出三处 commander 属性（均 accessMode=`rw`、pushMode=`1`）：

| 属性 | 类型/约束 | 值域 / 说明 | SDK 现状 |
|---|---|---|---|
| `commander_flight_height` | float，`{max:3000,min:2,step:0.1,unit_name:米/m}` | 相对(机场)起飞点的高度，相对高 ALT | ❌ `PropertySetMethod` 无 → **补常量** |
| `commander_flight_mode` | enum_int | `{0:"智能高度飞行",1:"设定高度飞行"}` | ✓ 已有 [`PropertySetMethod.COMMANDER_FLIGHT_MODE`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/protocol/method/PropertySetMethod.java#L95) |
| `commander_mode_lost_action` | enum_int | **`{0:"继续执行指点飞行任务",1:"退出指点飞行任务，执行普通失控行为"}`** | ❌ `PropertySetMethod` 无 → **补常量 + 值域 0-1** |

→ **§5-80 的「`commander_mode_lost_action` 值域须按原文核对」已解除**：值域 = **{0,1}**。落地时仅需补 **2 个常量**（`COMMANDER_FLIGHT_HEIGHT`、`COMMANDER_MODE_LOST_ACTION`）；`commander_flight_mode` 已存在。

**A76-2 复认 A49 缺口 / Javadoc 项（无新增）**

| 项 | 复认结果 | 本文件证据 |
|---|---|---|
| §5-77 | `best_link_gateway`（pushMode=1）、`flysafe_database_version`（text,64,pushMode=1）在页 | 第 16 行 / 第 78 行 |
| §5-78 | `fts_info` struct 在页（FTS 飞行终止系统，pushMode=0） | 第 221 行 |
| §5-79 | `mode_code` 值域 **0-21**（含 `21:"进离场航线飞行过程中"`） | 第 194 行 |
| §5-81 | `cameras` 20 字段（`wide_*`/`zoom_*`/`ir_metering_*`/存储设置等）在页 | 第 26–76 行 |
| §5-82 | `position_state.»quality` 含 `10:"RTK fixed"` | 第 136 行 |
| A49-2 之一 | `dongle_infos`（第 80 行）/ `offline_map_enable`（第 79 行）确为 pushMode=1 → 「M400 only」注释需补 M4D/M4TD | 第 79–80 行 |

**A76-3 溯源**

- **A76 为 M4D/M4TD「设备属性」页的「开发者投喂原文复核」记录**；A49 为其首个原文记录（来源为「AI 直接审计 DJI 官方页」）。
- **投喂目的**：收尾 §5-80（`commander_mode_lost_action` 值域）→ **已达成**。

**A76-4 附注**

- 页面日期 **2026-09-01**（v1.18 同期）；文件 225 行（A49 记 227 行，属同页）。
- 本页 `mode_code_reason` 值域 **0-23**（第 181 行，与 SDK 一致 ✓）。

> **A76 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A77. Dock3 自定义飞行区页原文投喂复核（页面日期 2025-03-19）— 逐字复核 A63（跨 Dock 镜像，新增差异 0）

**来源**：开发者于本窗口投喂 Dock3「自定义飞行区」页原文（Topic `thing/product/{gateway_sn}/events`（up）/ `services`（down）/ `requests`（up）；含 **2 events + 1 service + 1 request**）。**判定：与 A63（Dock1「自定义飞行区」，2025-03-19）内容逐字一致 → 跨 Dock 镜像复核，新增差异 0**。

**A77-0 复核结论**

| 段 | method | need_reply / 通道 | SDK | 判定 |
|---|---|---|---|---|
| events | `flight_areas_drone_location` | 0 / up | [`EventMethod.FLIGHT_AREAS_DRONE_LOCATION`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/protocol/method/EventMethod.java#L139) ✓ + `FlightAreasDroneLocationData` ✓ | ✓ 一致 |
| events | `flight_areas_sync_progress` | 1 / up | [`EventMethod.FLIGHT_AREAS_SYNC_PROGRESS`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/protocol/method/EventMethod.java#L142) ✓ + `FlightAreasSyncProgressData` ✓ | ✓ 一致 |
| services | `flight_areas_update` | Data=null / down；reply `{result}` | **✗ 缺**（无 `ServiceMethod.FLIGHT_AREAS_UPDATE`） | ✗ 缺失（§5-113） |
| requests | `flight_areas_get` | Data=null / up；reply `{result, output{files[]}}` | [`RequestsMethod.FLIGHT_AREAS_GET`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/protocol/method/RequestsMethod.java#L73) ✓ + `FlightAreasGetRequest(){}` ✓ / `FlightAreasGetReply` **口径偏差** | ✗ §5-114 |

**A77-1 复认（无新增 §5 项）**

- §5-113（`flight_areas_update` 为官方协议、SDK 缺 `ServiceMethod.FLIGHT_AREAS_UPDATE`）✓ 复认。
- §5-114（`FlightAreasGetReply` = `output{ SyncFile file }` 单数，文档为 `files` 数组且含 `url`/`size` → **breaking**）✓ 复认（本页 example 再次确证 `files` 为数组 + `url`/`size`/`checksum`/`name`）。
- §5-116（空请求 record 惯例）✓ 复认。
- 字段逐项：`status` 5 值（`fail`/`switch_fail`/`synchronized`/`synchronizing`/`wait_sync`）、`reason` 1-13、`file{name,checksum}`、`droneLocations[].{area_distance,is_in_area}` 与 A63 **完全一致**。

**A77-2 §5-115 归属证据（本窗口产出）**

- 4 个 flight-area 方法与 POJO 的**归属页**（「自定义飞行区」）在 **Dock1（A63）与 Dock3（A77）两侧均已获原文** → 确证 `@DocUrl` 应指向「自定义飞行区」页（**非** `dock3/wayline.html`；A41 已证 wayline.html 无 flight_areas 内容）。
- **但两页 URL 仍未提供** → **§5-115 未解除**（不猜测 URL）。

**A77-3 溯源**

- **A77 为 Dock3「自定义飞行区」页的首个独立原文记录**；A63 为 Dock1 版首个原文记录（来源「AI 直接审计官方页」）。
- **投喂目的**：收尾 §5-115（URL）→ **未达成**（仍缺 URL）。

**A77-4 附注**

- 页面日期 **2025-03-19**（同 A63）→ 早于 v1.18（2026.10.13）。
- 页面提及「文件名需满足 `geofence_{fileMD5}.json`；文件协议见模板」——模板未在本页给出，SDK 无 geofence 文件模型，**不在本 SDK 范围**（同 A63 结论）。

> **A77 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A78. Dock1 AirSense 页原文投喂复核（页面日期 2025-03-19）— 第 3 次投喂，逐字复核 A62/A74（差异 0）

**来源**：开发者再次投喂 Dock1「AirSense」页原文（投喂序列：**A62 → A74 → A78**）。**判定：与 A62/A74 逐字一致 → 重复页复核，新增差异 0**。

**A78-0 复核结论**

| 结论 | 说明 |
|---|---|
| 与 A62/A74 | **逐字一致**（页头 2025-03-19 / 5 分 / 2 用户已评分 + 10 字段表 + example `need_reply=1`） |
| 方法/字段 | `airsense_warning` 1/1 ✓、10 字段一致 ✓、`warning_level` 5 值 ✓ |
| **新增差异** | **0** |

**A78-1 §5-112 仍未解除（关键）**

- 本页为 `airsense_warning` 的归属页，**内容已完整记录（A62/A74/A78 三次一致）**；**唯一缺口 = 该页 URL**。
- 投喂**内容**不能替代 **URL**（`@DocUrl` 需具体地址）→ **§5-112 曾待 URL**（不猜测）。
- **📌 后续（2026-10-14）**：开发者已提供本页 URL（`.../dock/dock1/airsense.html`）→ **§5-112 已结案**（见 §5-112 / §0）。

**A78-2 溯源**

- A78 为 Dock1「AirSense」页的**第 3 次投喂**记录（A62 首个原文记录、A74 首次复核、A78 二次复核）。

> **A78 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A79. Pilot 上云「RC Pro 行业版 远程控制」页原文投喂复核（页面日期 2025-03-19）— 逐字复核 A14/A16，**§5-74 遗留结案**

**来源**：开发者投喂 Pilot 上云「RC Pro 行业版 远程控制」页原文 + URL：
`https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/rc-pro/remote-control.html`
**同页族第二源（仅 URL，未投喂内容）**：DJI RC Plus 2 行业版 `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/remote-control.html`

**A79-0 页面内容**：Event 2 项（`drc_drone_state_push`、`drc_camera_osd_info_push`，均 `drc/up`）+ Service 1 项（`drc_initial_state_subscribe`，`drc/down` 下行 Data=null + `drc/up` 回包 `{result}`）；`seq` 与 `data` 同级（递增序号）。

**A79-1 逐项核对（对照 A14 / A16）**

| method | SDK | 判定 |
|---|---|---|
| `drc_drone_state_push` | `DRC_DRONE_STATE_PUSH` ✓ + [`DroneStatePushData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/up/DroneStatePushData.java) ✓ | 已覆盖 ✓（**跨页子集口径**：RC Pro 页 Data 表**仅 3 字段** `stealth_state`/`night_lights_state`/`mode_code`{0..17}，**无** `landing_type`/`landing_protection_type`；SDK 为 **5 字段**（A14 Dock 页 5 字段）→ SDK 超集，无缺口。文档 Type=`bool` vs SDK `int`(0/1) → A14 已接受，保持） |
| `drc_camera_osd_info_push` | `DRC_CAMERA_OSD_INFO_PUSH` ✓ + [`CameraOsdInfoPushData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/up/CameraOsdInfoPushData.java) ✓ | 已覆盖 ✓（**§5-46 / §5-47 第三次印证**） |
| `drc_initial_state_subscribe` | `DRC_INITIAL_STATE_SUBSCRIBE` ✓ | 已覆盖 ✓（down Data=null / up 回包 `{result}` → 按 A16 决策**复用 `NoParameterDrcRequest` + `DrcResultReply`**，无需新文件） |

**A79-2 §5-74 遗留结案（关键）**

- Pilot RC DRC 通道 3 项 method **均带 `drc_` 前缀** → 与 Dock 侧**一致**；§5-74 裁决「方案 A：DRC 通道常量逐字对齐官方文档（带 `drc_` 前缀）」的**遗留待核项（Pilot 侧）解除**。

**A79-3 相机 OSD 差异（加档 §5-46 / §5-47）**

- **§5-46**：`ir_lense.thermal_supported_palette_styles`（`array`/`enum_int`）RC 页**再次显式列出**；SDK [`IrLense`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/up/CameraOsdInfoPushData.java#L129-L139) 缺 → 三次印证（A14+A38+A79）。
- **§5-47**：`ir_zoom_factor` 文档 Type=`float` `{"max":20,"min":2}`，SDK `int` → 三次印证。
- `zoom_lense.zoom_factor`：文档 Type=`int` 但示例 `6.999994214380596`（小数）→ SDK 已为 `double`（对齐示例，**正确**）；**文档 Type 失真，记录在案**（A14-3）。
- **`wide_aperture_value`**（enum `{280:F2.8…}`）/`zoom_aperture_value`（`{440:F4.4}`）：与 A14 `ApertureValue` 值域一致 → 共享枚举决策（A14）不变。

**A79-4 结论**

- **新增差异 0**；仅 **§5-74 遗留结案** + **§5-46/§5-47 证据加档**。本页 `@DocUrl` 归属应与 Dock DRC 页一致（无元数据更正需求）。

> **A79 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A80. Pilot 上云「RC Plus 2 行业版 远程控制」页原文投喂复核（页面日期 **2026-10-13 / v1.18**）— 跨场景镜像复核 A15–A18/A25/A38，**§5-74 全量印证**

**来源**：开发者投喂 Pilot 上云「RC Plus 2 行业版 远程控制」页原文（文件《远程控制\_\_2026….txt》，1902 行）+ URL：
`https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/remote-control.html`
**页面日期 2026-10-13 → 属 v1.18 现行版**（区别于 A79 RC Pro 页的 2025-03-19；本页为已更新版本）。

**A80-0 页面结构**：`#Event` 4 项（`drc/up` 推送）+ `#Service` 条目 39 个 = **唯一 38 项**（`drone_emergency_stop` 在「DRC-飞行器急停」与「急停」两组中**重复出现一次**）（`drc/down` 下行 + `drc/up` 回包；`stick_control` 无回包）。**文档瑕疵**：DJI 此处以 `#Service` 作小节标题，但**全部 method 的 Topic 均为 `thing/product/{gateway_sn}/drc/down` 或 `drc/up`**（DRC 通道，非 `services` 通道）——标题表述与实际通道不符（记录在案）。

**A80-1 Event 逐项核对（4 项，对照 `DrcUpMethod`）**

| method | SDK | 判定 |
|---|---|---|
| `drc_camera_osd_info_push` | `DRC_CAMERA_OSD_INFO_PUSH` ✓ + `CameraOsdInfoPushData` ✓ | 已覆盖 ✓（§5-46/§5-47 第 4 次） |
| `drc_drone_state_push` | `DRC_DRONE_STATE_PUSH` ✓ + `DroneStatePushData` ✓ | 已覆盖 ✓（本页 **5 字段** 与 SDK 一致，含 `landing_type`/`landing_protection_type`；A79 RC Pro 页为 3 字段子集） |
| `drc_camera_state_push` | `DRC_CAMERA_STATE_PUSH` ✓ + `CameraStatePushData` ✓ | 已覆盖 ✓（`interval_photo_interval` 示例 `2.5` vs Type=`int` → §4 L363 已裁决 `Double`） |
| `drc_camera_photo_info_push` | `DRC_CAMERA_PHOTO_INFO_PUSH` ✓ + `CameraPhotoInfoPushData` ✓ | 已覆盖 ✓ |

**A80-2 Service 逐项核对（38 项 → 全部已在 §4 登记，新增差异 0）**

- **既有 `DrcMethod` 常量（16 项）**：`heart_beat` / `drc_initial_state_subscribe` / `drone_emergency_stop` / `stick_control` / `drc_camera_dewarping_set` / `drc_camera_mechanical_shutter_set` / `drc_camera_iso_set` / `drc_camera_shutter_set` / `drc_camera_aperture_value_set` / `drc_stealth_state_set` / `drc_night_lights_state_set` / `drc_interval_photo_set` / `drc_video_resolution_set` / `drc_linkage_zoom_set` / `drc_emergency_landing` / `drc_force_landing`
- **§4 已登记新增常量（22 项，A15–A18 / A25 / A38）**：`drc_photo_storage_set` / `drc_video_storage_set` / `drc_camera_mode_switch` / `drc_camera_recording_start` / `drc_camera_recording_stop` / `drc_camera_screen_drag` / `drc_camera_aim` / `drc_camera_focal_length_set` / `drc_gimbal_reset` / `drc_camera_look_at` / `drc_camera_screen_split` / `drc_camera_frame_zoom` / `drc_ir_metering_area_set` / `drc_ir_metering_point_set` / `drc_ir_metering_mode_set` / `drc_camera_point_focus_action` / `drc_camera_focus_value_set` / `drc_camera_focus_mode_set` / `drc_camera_exposure_set` / `drc_camera_exposure_mode_set` / `drc_camera_photo_stop` / `drc_camera_photo_take`（见 §4 L331 [DRC 新增 12+14] 与 L358/L359 POJO 清单）

**A80-3 §5-74 全量印证（关键）**

- 本页 **22 个相机/红外 DRC 方法全部带 `drc_` 前缀**（`drc_camera_aim` / `drc_camera_mode_switch` / `drc_gimbal_reset` / `drc_camera_exposure_mode_set` / `drc_ir_metering_*` …）→ 非仅 A79 的 3 项 → **方案 A（DRC 前缀常量集合，与 `services` 无前缀常量并存）在 Pilot 侧获全量证据**。
- 同一方法的 DRC 版（带 `drc_`）与 `services` 版（无前缀）**data 结构一致** → 支持 §4 L328 已裁决的「常量两套、POJO 字段一致时复用同一 record」。

**A80-4 加档证据**

- **§5-46**：`ir_lense.thermal_supported_palette_styles`（`array`/`enum_int`）本页**第 4 次显式列出**（A14+A38+A79+A80）→ 缺口确证。
- **§5-47**：`ir_zoom_factor` 文档 Type=`float` `{"max":20,"min":2}` → SDK `int`（第 4 次印证）。
- **§5-13（`CameraMode`）**：`drc_camera_state_push.camera_state.camera_mode` = `{0..4}`（`4=定时拍照`）→ 值 4 再次确认。
- **§5-109**：本页 `heart_beat` down/up 的 **Data 表均仅 `{timestamp}`**（`seq` 为信封层字段，与 data 同级）→ 与 SDK `HeartBeatRequest`/`HeartBeatReply` 现态一致，**支持不向 data 补 `seq`**（A61「均含 seq」应理解为信封 `seq`）。
- **`ApertureValue` 值域**：本页 `wide_aperture_value` = `{0:F_AUTO, 50:F0.5, 70:F0.7 … 25600:F256}`（60+ 项）→ 与 A16（line 996）登记值域**完全一致**（共享枚举决策不变）。

**A80-5 文档瑕疵（记录在案，无需改 SDK）**

- `drc_gimbal_reset.payload_index` 的 **Name 栏**含杂乱串 `{{type-subtype-gimbalindex}_aembLbhPpc}`（正文实为 `{type-subtype-gimbalindex}`）→ DJI 文档脏数据。
- `drc_camera_state_push.interval_photo_interval` Type=`int` 但示例 `2.5`（§4 L363 已按 `Double` 裁决）。
- `drc_camera_osd_info_push` 的 `ir_lense` **示例**含 `thermal_current_palette_style`（Data 表未列）→ 同 A14/A38，SDK `IrLense` 已含该字段。
- `drc_camera_screen_split` 的 `drc/up` 回包示例**回显了请求 data**（`{enable, payload_index}`）而非 `{result}` → 疑为文档示例笔误（按其它方法的 `{result}` 惯例）。

**A80-6 结论**

- **新增差异 0**（全部 method 已在 A15–A18/A25/A38 与 §4 登记）；**§5-74 全量印证**；**§5-46/§5-47「第 4 次」、§5-109 加档**。
- **防重复登记**：本页 22 个 `drc_` 前缀方法**不是新缺口**——此前疑为「SDK 缺失 22 项」实为「§4 已登记待统一落地」，A80 予以确认，避免后续 AI 重复登记。

> **A80 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A81. 三页「设备管理」原文投喂复核（Dock1 / RC Pro / RC Plus 2，均 2025-03-19）— **§5-89 结案**，§5-90/§5-91 加档，新发现 §5-121

**来源**：开发者一次性投喂 **3 页「设备管理」** 原文 + URL：
- Dock1：`https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock1/device.html`
- RC Pro 行业版：`https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/rc-pro/device.html`
- RC Plus 2 行业版：`https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/device.html`
页面日期均 **2025-03-19**。

**A81-1 §5-89 结案（关键）**

- 三页均含 `status`(up, `update_topo`) + `status_reply`(down, `update_topo`)。
- **三页 `status_reply` data 均仅 `{result}`（int，非 0 代表错误），皆无 `sub_type`**。
- 故 SDK [`UpdateTopoReplyData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/status/UpdateTopoReplyData.java) 的 `@Inferred(reason="DJI 文档中 status_reply data 含 sub_type（0=上线/1=下线）…")` **依据不成立**；且该 reason 的语义（`0=上线/1=下线`）与 UP data 中 `sub_type` = **网关子设备的产品子类型**（三页一致）**互相矛盾**。
- **裁决**：`UpdateTopoReplyData.subType` 属**无官方依据的推断字段** → **删除**，同时移除 `@Inferred`、修正 Javadoc（现「`sub_type` 取值 0=上线/1=下线」为错误表述）。属 **breaking**（record 构造器 `(result, subType)` → `(result)`），随 `1.18.0.0` 发布，同步改测试。

**A81-2 §5-90 加档**：三页 UP `update_topo` data 的 `sub_type` 均为「网关子设备的产品子类型」（`int`，示例 `sub_type:0`）→ 第 4 次确证 `StatusMethod.UPDATE_TOPO` Javadoc（「data 含 sub_type 区分上线 0/下线 1」）**表述错误**，须更正。

**A81-3 §5-91 加档**：三页 UP `update_topo` data **均无 `base_info`**（Dock1/RC Pro 顶层 = 7 字段，RC Plus 2 = 6 字段）→ 确证 `base_info` 为 **O4 专属**；§4「+ `base_info`」仅适用 O4 页，Dock1/RC 页无需。

**A81-4 新发现（→ §5-121）：RC Plus 2「设备管理」页口径与另两页不一致**

| 维度 | Dock1 / RC Pro | RC Plus 2 |
|---|---|---|
| `update_topo` topic | `sys/product/{gateway_sn}/status` | **`thing/product/{gateway_sn}/status`**（非 `sys`） |
| UP data 顶层字段 | 7（含 `domain`） | **6（缺 `domain`）** |
| `sub_devices[]` 字段 | 8（含 `domain`/`index`） | **6（缺 `domain`/`index`）** |
| 小节标签 | Dock1 `#Status` / RC Pro `#Event` | `#Event` |

- SDK `UpdateTopoData` 顶层 `domain` 为 `Objects.requireNonNull`（必填）→ 若 RC Plus 2 场景确不发 `domain`，反序列化将 **NPE 抛错**；`SubDevice.index` 已可空（Javadoc 已述「RC Plus 2 不上报 domain/index」✓，本页获原文佐证）。
- → 登记 **§5-121**（topic 口径 + 顶层 `domain` 可空性），待开发者裁决。

**A81-5 投喂完整性提示**：RC Plus 2「设备管理」页本次投喂**不完整**（无 Example、无 `status_reply` 段）→ §5-121 定稿需补全该页原文；Dock1/RC Pro 两页完整（各含 2 个 example + reply 段）。

**A81-6 结论**：**§5-89 结案**（裁决删除 `subType`，breaking）；§5-90/§5-91 加档；新增 **§5-121**（待裁决）。

> **A81 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A82. RC Pro 行业版「设备属性」页原文投喂复核（2025-09-03）+ RC Pro「设备管理」页重复复核 — **新增差异 1**，§5-122/§5-123

**来源**：
- RC Pro「设备属性」：`https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/rc-pro/properties.html`（2025-09-03）
- RC Pro「设备管理」：`.../rc-pro/device.html`（2025-03-19）→ **重复 A81 已投喂页，逐字复核一致（新增差异 0）**，进一步确认 §5-89 结案依据（`status_reply` 仅 `{result}`）不变。

**A82-0 页面结构**：`#设备属性列表`，pushMode=0（osd，0.5Hz，topic `thing/product/{device_sn}/osd`）/ pushMode=1（state，变化上报 `.../state`）；accessMode r/rw；rw 走 `.../property/set`。

**A82-1 逐项比对**

| 文档字段 | pushMode | SDK | 判定 |
|---|---|---|---|
| `country` | 0 | `RcOsd.country` | ✓ |
| `capacity_percent` | 0 | **`RcOsd.battery`**（wire `battery`） | **✗ wire 不符**（→ §5-122） |
| `height` | 0 | `RcOsd.height` | ✓ |
| `latitude` / `longitude` | 0 | `RcOsd.latitude` / `longitude` | ✓ |
| `wireless_link`（10 子字段） | 0 | `RcOsd.wirelessLink`（[`WirelessLink`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/nested/WirelessLink.java) 10 字段） | ✓ 全一致 |
| `drc_state` | 0 | `RcOsd.drcState`（`DrcState`） | ✓ |
| `live_capacity`（struct） | 1 | `StateField.LIVE_CAPACITY` | ✓（Javadoc 未注 RC → §5-123） |
| `dongle_infos`（`dongle_type{6,10}`/`esim_*`/`sim_*`） | 1 | `StateField.DONGLE_INFOS` + `DongleType{6,10}` | ✓ |
| `live_status`（`video_quality{0-4}`） | 1 | `StateField.LIVE_STATUS` + `VideoQuality` | ✓ |
| `firmware_version` | 1 | `StateField.FIRMWARE_VERSION` | ✓（Javadoc 未注 RC → §5-123） |
| `cloud_control_auth`（`array<string>`） | 1 | `StateField.CLOUD_CONTROL_AUTH`「Pilot 特有」 | ✓ |
| （无 `mode_code`） | — | `RcOsd.modeCode` | ⚠ 本页未列 → §5-122 |

**A82-2 新发现（→ §5-122 / §5-123）**

1. **`RcOsd.battery` wire 与文档 `capacity_percent` 不符**（电量反序列化丢失；修正属 breaking）。
2. **`RcOsd.modeCode`** 本页 osd 列表**未含 `mode_code`** → 来源待核。
3. **`RcOsd` 页归属不一致**：`@DocUrl` = `dji-rc-plus-2/properties.html`，而类 Javadoc 参考链接 = `remote-controller/properties.html`。
4. **`StateField`** 中 `live_capacity`/`dongle_infos`/`live_status`/`firmware_version` 注释**未注 RC**。

**A82-3 文档瑕疵（记录，不改 SDK）**：`wireless_link.link_workmode` 描述为「**机场**的图传链路模式」（RC 页应为遥控器，疑复制自 Dock 页）；`drc_state` 行未填 accessMode/pushMode 列。

**A82-4 结论**：**新增差异 1（`battery` wire key）**；新 §5-122/§5-123；**需补投喂 RC Plus 2 行业版「设备属性」页（`dji-rc-plus-2/properties.html`，即 `RcOsd.@DocUrl` 目标页）** 以定稿。

> **A82 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A83. RC Plus 2「设备管理」页完整版原文复核（2025-03-19）— **§5-121 结案（撤销）**

**来源**：开发者投喂（本次**标注为「RC Plus 2 设备属性」**，经澄清确认为 **RC Plus 2「设备管理」完整版**，标注为笔误）→ 页面 `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/device.html`，日期 2025-03-19。

**A83-1 内容**：`#Event #设备拓扑更新`，`update_topo`：
- topic = **`sys/product/{gateway_sn}/status`**（up）+ `sys/product/{gateway_sn}/status_reply`（down）；
- UP data 顶层 **7 字段**：`domain`/`type`/`sub_type`/`device_secret`/`nonce`/`thing_version`/`sub_devices`；
- `sub_devices[]` **8 字段**：`sn`/`domain`/`type`/`sub_type`/`index`/`device_secret`/`nonce`/`thing_version`；
- `status_reply` data 仅 **`{result}`**（int）；
- 含 online/offline 两个 Example。

**A83-2 与 A81 记载的差异（更正）**：A81 曾记 RC Plus 2 该页为 topic `thing/product/...` + 顶层 6 字段（缺 `domain`）+ `sub_devices[]` 6 字段（缺 `domain`/`index`）→ 本次完整原文显示**与 Dock1/RC Pro 完全一致**（`sys/product` + 含 `domain` + 含 `index`）→ 判定 A81 所据系**该页旧版或转录差异，非现行口径**。

**A83-3 §5-121 结案（撤销）**：`sys/product` topic 与顶层 `domain` 必填**均与 SDK [`UpdateTopoData`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/command/status/UpdateTopoData.java)（`domain` = `Objects.requireNonNull`）相容** → **无 SDK 改动**；`SubDevice.index` 可空仍正确（本页含 `index`，可空无害）。

**A83-4 附带确认**：本页 `status_reply` 仅 `{result}` → 再次佐证 **§5-89** 结案（删除 `UpdateTopoReplyData.subType`）成立。

**A83-5 仍缺**：RC Plus 2 行业版「设备属性」页（`dji-rc-plus-2/properties.html`，osd/state 属性列表）**仍未获得** → **§5-122** 待其定稿。

**A83-6 结论**：**§5-121 结案（撤销，无 SDK 改动）**；§5-89 再确认；**仍缺 RC Plus 2「设备属性」页**。

> **A83 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A84. RC Plus 2 行业版「设备属性」页原文投喂复核（2025-09-03）— **§5-122-① 裁决**，§5-123 结案

**来源**：开发者投喂 → `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/properties.html`（2025-09-03，5 分 / 1 用户）。

**A84-1 与 RC Pro「设备属性」页（A82）比对：逐字一致，新增差异 0**

| 段 | 字段 | pushMode | SDK | 判定 |
|---|---|---|---|---|
| osd | `country` / `height` / `latitude` / `longitude` | 0 | `RcOsd.*` | ✓ |
| osd | **`capacity_percent`**（剩余电量，int 0-100） | 0 | **`RcOsd.battery`**（wire `battery`） | **✗ wire 不符** |
| osd | `wireless_link`（10 子字段） | 0 | `WirelessLink` | ✓ |
| osd | `drc_state`（enum_int 0/1/2） | （未填） | `RcOsd.drcState` | ✓ |
| state | `live_capacity` / `dongle_infos` / `live_status` / `firmware_version` / `cloud_control_auth` | 1 | `StateField.*`（+`DongleType`/`VideoQuality`） | ✓ |
| — | **`mode_code`** | — | `RcOsd.modeCode` | **✗ 本页无此字段** |

**A84-2 §5-122-① 裁决**：两页（RC Pro + RC Plus 2）osd 均为 **`capacity_percent`**，无一处为 `battery` → SDK [`RcOsd`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/RcOsd.java) 字段 `battery` 的 wire key **无官方依据** → **裁决：`battery` → `capacityPercent`**（对齐文档 `capacity_percent`；语义与现 Javadoc「电池电量百分比（0-100）」一致）→ 属 **breaking**（record 组件名变更），随 `1.18.0.0`。**遗留待确认**：若 simulator/hivemind 实测 wire 为 `battery`，需评估 `@JsonAlias("battery")` 兼容，并在 TDD 覆盖两种 wire。

**A84-3 §5-122-② 仍待确认**：**两页均无 `mode_code`** → `RcOsd.modeCode` 无文档依据。需投喂 `remote-controller/properties.html`（`RcOsd` Javadoc 参考页）佐证，或**裁决删除 `modeCode`**。

**A84-4 §5-122-③ 裁决**：`RcOsd.@DocUrl`（`dji-rc-plus-2/properties.html`）**本页确认为正确目标页**；类 Javadoc 参考链接 `remote-controller/properties.html` 应更正/统一为该页。

**A84-5 §5-123 结案**：RC Plus 2「设备属性」页 state 段与 RC Pro 页**逐字一致**（同样上报 `live_capacity`/`dongle_infos`/`live_status`/`firmware_version`）→ 确证 [`StateField`](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/StateField.java) 4 字段 Javadoc 应补「RC state 亦上报」（`CLOUD_CONTROL_AUTH` 已正确）；枚举/常量不变。

**A84-6 文档瑕疵（记录，不改 SDK）**：`wireless_link.link_workmode` 描述「**机场**的图传链路模式」（RC 页应为遥控器，复制自 Dock 页）；`drc_state` 行未填 accessMode/pushMode 列（同 A82）。

**A84-7 结论**：RC Plus 2 属性页 **= RC Pro 属性页（逐字一致）**；**§5-122-① 裁决（`capacityPercent`，breaking）**、③ 裁决（`@DocUrl` 正确）；**§5-123 结案**；**§5-122-② 仍待确认**（`RcOsd.modeCode` 去留）。

> **A84 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A85. 「负载控制—切换相机模式」页 + RC Pro / RC Plus 2「远程控制」页重复复核 — **新增差异 0**，§5-74 再次印证

**来源**：开发者本轮投喂三份原文 —
- ① **RC Pro 行业版「远程控制」** 页全文（`https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/rc-pro/remote-control.html`，2025-03-19）→ **重复 A79**；
- ② **RC Plus 2 行业版「远程控制」** 页（文件《远程控制\_\_2026….txt》+ URL `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/remote-control.html`）→ **重复 A80**；
- ③ **「负载控制—切换相机模式」段**（文件《负载控制—切换相机模….txt》，875 行）→ **开发者确认：归属 = ② 同一页的锚点段** `.../dji-rc-plus-2/remote-control.html#负载控制—切换相机模式`（Topic 均为 `thing/product/{gateway_sn}/drc/down` + `drc/up`）→ 首次逐项比对。

**A85-1 ③ 20 项逐项核对（全部已在 §4 登记 → 新增差异 0）**

| # | method（本页 wire） | SDK `ServiceMethod`（无前缀，已存在） | DRC 侧 `DrcMethod` | §4 登记位置 | 判定 |
|---|---|---|---|---|---|
| 1 | `drc_camera_mode_switch` | ✓ `CAMERA_MODE_SWITCH` | ❌ | A16/A80（DRC 缺失） | 已登记 ✓ |
| 2 | `drc_camera_recording_start` | ✓ `CAMERA_RECORDING_START` | ❌ | §4（L1088） | 已登记 ✓ |
| 3 | `drc_camera_recording_stop` | ✓ `CAMERA_RECORDING_STOP` | ❌ | §4 | 已登记 ✓ |
| 4 | `drc_camera_screen_drag` | ✓ `CAMERA_SCREEN_DRAG` | ❌ | §4 | 已登记 ✓ |
| 5 | `drc_camera_aim` | ✓ `CAMERA_AIM` | ❌ | A16 | 已登记 ✓ |
| 6 | `drc_camera_focal_length_set` | ✓ `CAMERA_FOCAL_LENGTH_SET` | ❌ | A17 | 已登记 ✓ |
| 7 | `drc_gimbal_reset` | ✓ `GIMBAL_RESET` | ❌ | §4（L1093） | 已登记 ✓ |
| 8 | `drc_camera_look_at` | ✓ `CAMERA_LOOK_AT` | ❌ | A17（L1040） | 已登记 ✓ |
| 9 | `drc_camera_screen_split` | ✓ `CAMERA_SCREEN_SPLIT` | ❌ | §4 | 已登记 ✓ |
| 10 | `drc_camera_frame_zoom` | ✓ `CAMERA_FRAME_ZOOM` | ❌ | A17 | 已登记 ✓ |
| 11 | `drc_ir_metering_area_set` | ✓ `IR_METERING_AREA_SET` | ❌ | §4（L1097） | 已登记 ✓ |
| 12 | `drc_ir_metering_point_set` | ✓ `IR_METERING_POINT_SET` | ❌ | §4（L1099） | 已登记 ✓ |
| 13 | `drc_ir_metering_mode_set` | ✓ `IR_METERING_MODE_SET` | ❌ | §4（L1098） | 已登记 ✓ |
| 14 | `drc_camera_point_focus_action` | ✓ `CAMERA_POINT_FOCUS_ACTION` | ❌ | §4 | 已登记 ✓ |
| 15 | `drc_camera_focus_value_set` | ✓ `CAMERA_FOCUS_VALUE_SET` | ❌ | A17 | 已登记 ✓ |
| 16 | `drc_camera_focus_mode_set` | ✓ `CAMERA_FOCUS_MODE_SET` | ❌ | A17 | 已登记 ✓ |
| 17 | `drc_camera_exposure_set` | ✓ `CAMERA_EXPOSURE_SET` | ❌ | A17（L1034） | 已登记 ✓ |
| 18 | `drc_camera_exposure_mode_set` | ✓ `CAMERA_EXPOSURE_MODE_SET` | ❌ | A17（L1033） | 已登记 ✓ |
| 19 | `drc_camera_photo_stop` | ✓ `CAMERA_PHOTO_STOP` | ❌ | §4 | 已登记 ✓ |
| 20 | `drc_camera_photo_take` | ✓ `CAMERA_PHOTO_TAKE` | ❌ | §4（L1086；回包多 `status`→§5-14） | 已登记 ✓ |

**A85-2 §5-74 再次印证（关键）**：③ 20 个方法 **全部带 `drc_` 前缀**（`drc_camera_*` / `drc_ir_metering_*` / `drc_gimbal_reset`），且 DRC 版 down data **与 services 版一致** → 与 A79（3 项）/ A80（22 项）叠加，**方案 A（DRC 前缀常量集 + services 无前缀常量集并存）的 Pilot 侧证据再次加强（本页 20/20 全前缀）**。

**A85-3 A15–A18 已登记的「DRC 侧缺失」缺口在本页获原文复证**：本页即该批缺口的原文来源之一（`drc_camera_exposure_mode_set` / `drc_camera_exposure_set` / `drc_camera_focal_length_set` / `drc_camera_focus_mode_set` / `drc_camera_focus_value_set` / `drc_camera_frame_zoom` / `drc_camera_look_at` 等）→ **无需新增登记，避免重复**。

**A85-4 文档瑕疵（记录，不改 SDK）**
- `drc_gimbal_reset.payload_index` Name 栏脏串 `{{type-subtype-gimbalindex}_aembLbhPpc}`（同 A80-5 / §4 L1134）；
- `drc_camera_screen_split` 的 `drc/up` 回包 Example **回显请求 data**（`{enable, payload_index}`）而非 `{result}`（同 A80-5）；
- `drc_camera_exposure_set.exposure_value` Data 表 Type = **enum_string** 但枚举键/值均为数字（Example `8`）→ 同 §4 L1068 矛盾记录。

**A85-5 ③ 归属页确认（开发者 2026-10-14）**：「负载控制—切换相机模式」**属于 RC Plus 2 行业版「远程控制」页**（锚点 `.../dji-rc-plus-2/remote-control.html#负载控制—切换相机模式`）→ ③ 与 ② **为同一页**，归属 URL = `https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/remote-control.html` → ③ 20 项方法 `@DocUrl` 归属 = 该页（与 A80 一致），**无独立归属 URL**；原 A85-1「归属 URL 未提供」疑问**解除**。

**A85-6 结论**：①②为**重复页复核（新增差异 0）**；③（= ② 同页锚点段）**20 项全部已在 §4 登记（DRC 侧缺口，随 §5-74 方案 A 落地）→ 新增差异 0**；**§5-74 再次印证**；**无新增 §5 项**。

> **A85 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

### A86. 通用遥控器「设备属性」页原文投喂复核 — **顶层差异 0**，**§5-122-② 证据闭环** + **URL 落实**

**来源**：开发者投喂 DJI **通用遥控器设备属性页** —
`https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/others/rc/properties.html`（2025-07-23，暂无评分）。本页即 `RcOsd` 类 Javadoc 参考链接的**目标页**。

**A86-1 本页 = `RcOsd` Javadoc 参考链接的正确 URL**：`RcOsd` 类 Javadoc 参考链接原写 `.../pilot-to-cloud/mqtt/remote-controller/properties.html`（**路径错误**）→ **正确 URL = `.../pilot-to-cloud/mqtt/others/rc/properties.html`**（本页，开发者提供）→ **§5-122-③ 更正依据确立**。

**A86-2 逐字比对（本页 vs A82/A84 = RC Pro / RC Plus 2「设备属性」页）→ 同源，顶层差异 0**

| 本页字段（osd/state） | pushMode | accessMode | SDK 映射（A82 已核） |
|---|---|---|---|
| `live_capacity`（struct，三层 device/camera/video） | 1 | r | `StateField.LIVE_CAPACITY` ✓ |
| `country` | 0 | r | `RcOsd.country` ✓ |
| `capacity_percent`（剩余电量 int 0-100） | 0 | r | `RcOsd.battery` ⚠ → §5-122-① |
| `height`（double / m） | 0 | r | `RcOsd.height` ✓ |
| `dongle_infos`（array<struct>） | 1 | r | `StateField.DONGLE_INFOS` + `DongleType` ✓ → 子枚举见 A86-6/§5-124 |
| `live_status`（array<struct>，`video_quality{0-4}`） | 1 | r | `StateField.LIVE_STATUS` + `VideoQuality` ✓ |
| `wireless_link` | 0 | r | `RcOsd.wirelessLink`（[WirelessLink](file:///d:/99.Code/dji-cloud-api-sdk/sdk/src/main/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/nested/WirelessLink.java) 10 字段逐字一致）✓ |
| `firmware_version`（text ≤64） | 1 | r | `StateField.FIRMWARE_VERSION` ✓ |
| `latitude` / `longitude`（double） | 0 | r | `RcOsd.latitude` / `RcOsd.longitude` ✓ |
| `cloud_control_auth`（array<string>） | 1 | r | `StateField.CLOUD_CONTROL_AUTH` ✓ |
| **（无 `mode_code`）** | — | — | `RcOsd.modeCode` ⚠ → §5-122-② |

**A86-3 §5-122-② `modeCode` 证据闭环（关键）**：本页 osd/state 列表**亦无 `mode_code`** → 至此 **3 页（RC Pro / RC Plus 2 / 通用 RC）全部无 `mode_code`** → `RcOsd.modeCode` **无任何官方文档依据** → **建议裁决删除 `RcOsd.modeCode`**（breaking，随 `1.18.0.0`；移除后 `RcOsd` 余 7 字段）。

**A86-4 §5-122-③ URL 统一（待裁决）**：`@DocUrl` 现 = `.../dji-rc-plus-2/properties.html`（A84 判为正确）；本页为**机型无关通用页**，URL = `.../others/rc/properties.html` → 建议：**`@DocUrl` 与类 Javadoc 参考链接统一指向通用页**（或保留机型页 `@DocUrl` + Javadoc 链接更正为通用页 URL）→ **待开发者最终裁决**。

**A86-5 §5-122-① 再次印证**：本页 `capacity_percent`（`int` 0-100、`r`、pushMode=0）→ `RcOsd.battery` → `capacityPercent` 裁决**再获一页佐证**（4 页口径一致）。

**A86-6 `dongle_infos` 5 个子枚举未建模（新增，非阻断）**：本页子字段枚举 — `dongle_type`{6,10}（SDK `DongleType` ✓）、`esim_activate_state`{0,1}、`sim_card_state`{0,1}、`sim_slot`{0,1,2}、`telecom_operator`{0,1,2,3}、`sim_type`{0,1,2}（**SDK 后 5 者均无**）→ 登记 **§5-124**（可选强类型建模；A82 已用 `StateField.DONGLE_INFOS` + `DongleType` 捕获字段，**功能不缺失**）。

**A86-7 嵌套结构细化（非缺口）**：本页给出 `live_capacity`（三层：`device_list`→`camera_list`→`video_list{switchable_video_types}`）、`live_status`（`video_id`/`video_type`/`video_quality`/`status`/`error_status`）完整定义 → 与 A82 的 `StateField.LIVE_CAPACITY`/`LIVE_STATUS` 映射结论一致（✓）。

**A86-8 结论**：本页 = A82/A84 **同源通用页 → 顶层差异 0**；**§5-122-② 证据闭环（建议删 `modeCode`）**；**§5-122-③ URL 落实**；新增 **§5-124**（dongle 子枚举，非阻断）。

> **A86 为原文审计记录，未写代码**（遵守「原文收齐前不编码」硬约束）。

---

## 9. 待裁决清单（截至 2026-10-14，供开发者逐条拍板）

> **来源**：§5「待确认项」全表（§5-1~§5-124）中状态仍为**待确认/待裁决**的项；**已结案 / 已裁决 · 仅待落地**者不列入（见文末对照）。
> **用法**：「裁决」列留白供开发者填写；「建议默认」为本报告推荐值，可直接采纳或推翻。
> **共 62 项**，分 7 组。裁决完成后按 TDD 统一落地（`tdd-test-cases.md` → `architecture-design.md` → 代码 → `mvn test`），随 `1.18.0.0` 发布。

> **✅ 全批采纳（2026-10-14）**：开发者裁决「按建议全批」→ **§9.1–§9.7 全部 62 项均按「建议默认」执行**（各表「裁决」列统一视为「采纳」；§5-67/§5-108/§5-122-①（`@JsonAlias`）等「须你定」的子项按建议默认一并采纳）。

### 9.1 纯口径 / 元数据（7 项，少或无代码影响）

| 编号 | 问题 | 建议默认 | 裁决 |
|---|---|---|---|
| §5-43 | 共享 `PathPoint` 的 `@DocUrl`（现指 `dock3/drc.html`）+ 补 `height`=椭球高 | 补 Javadoc；`@DocUrl` 视是否多页共用再定 | |
| §5-44 | Dock1 页「`planned_path_points` 全量更新 / `last_point_type` 显示判定」语义是否入 Javadoc | 补入 Javadoc | |
| §5-50 | `OtaProgressData`/`OtaCreateRequest` 的 `@DocUrl`/`@Verified` 依据 | 改指 O4 `firmware.html` | |
| §5-71 | `ExecutePlacemark.gimbalPitchAngle` 是否保留（该页表未列） | 保留 + `@Inferred` | |
| §5-91 | `base_info` 归属（三页无 → 确证 O4 专属） | 结案（仅 O4 新建） | |
| §5-117 | `storage_config_get.module=1` 权威语义（PSDK UI 资源 vs 日志） | 以 A64「PSDK UI 资源」为准，删「1=日志」 | |
| §5-122-③ | `RcOsd` `@DocUrl` 统一（通用页 vs 机型页） | 统一指通用页 `others/rc/properties.html` | |

### 9.2 Wire key / 字段名 / 类型修正（15 项，breaking）

| 编号 | 问题 | 建议默认 | 裁决 |
|---|---|---|---|
| §5-14 | `drc_camera_photo_take` 回包多 `status` 的出现条件 | 新增专用 Reply + `@Inferred` | |
| §5-47 | `IrLense.irZoomFactor` `int`→`Double` | 改 `Double` | |
| §5-48 | `OtaDevice.fileSize` `Long`→`Integer` | 改 `Integer` | |
| §5-52 | `long`/`Long` 毫秒字段（`start_time`/`end_time`/`expire`，超 int32） | 统一保持 `long`；复核 §5-6 冲突 | |
| §5-59 | `FlighttaskProgressGetRequest.sn` 与 Dock2 页 `target_sn` 错配 | 改名 `targetSn` | |
| §5-66 | `MappingStripPlacemark.margin` `int`→`double` | 改 `double` | |
| §5-83 | `electricSupplyVoltage`（Dock1 `int` vs O4 `double`，SDK `Double`） | 建议保 `Double`（跨页冲突须你定） | |
| §5-92 | `AirportOrganizationBindReply.ErrorInfo.errSn`→文档 `sn` | 改名 `sn` | |
| §5-97 | `StorageConfigGetReply.Credentials.expireTime`→文档 `expire` | 改名 `expire` | |
| §5-98 | `MediaFileMetadata.gimbalYawDegree` 文档 `float` vs 示例字符串 | 保 `String`（对齐示例） | |
| §5-104 | `AirConditionerModeSwitch`/`BatteryStoreModeSwitch` 字段 `mode`→文档 `action` | 改名 `action` | |
| §5-108 | `ConfigReply` 字段集严重不符（→ `appId/appKey/ntpServerHost/ntpServerPort`） | 按文档重建；`result` 去留须定 | |
| §5-114 | `FlightAreasGetReply.Output.file`→`files[]`（含 `url`/`size`） | 改为数组 | |
| §5-122-① | `RcOsd.battery`→文档 `capacity_percent` | 改 `capacityPercent`；`@JsonAlias("battery")` 兼容待定 | |
| §5-122-② | `RcOsd.modeCode` 三页均无 | 删除（breaking） | |

### 9.3 协议缺口补充（21 项，新增字段/POJO，breaking）

| 编号 | 问题 | 建议默认 | 裁决 |
|---|---|---|---|
| §5-46 | `IrLense` 补 `thermalSupportedPaletteStyles`（3 页确证） | 补 | |
| §5-49 | `firmwareUpgradeType` 是否补 `{2,3,4}` 枚举 | 补枚举 | |
| §5-51 | `ota_create` 是否补专属 Reply（与 §5-16 一致性） | 补 | |
| §5-54 | `fileupload_progress` 字段集（表 3 字段 vs 示例 7 字段） | 按示例全字段 + `@Inferred` | |
| §5-55 | `multi_dock_task` 补 `DockInfo` 4 字段 + `RtcmInfo` + `CenterNode.sdrId` + `LeafNode` 3 字段 | 补 | |
| §5-56 | `flighttask_create`（文档标废弃）是否补入 | 补 + `@Deprecated` | |
| §5-57 | `FlighttaskProgressGetRequest` 请求 data 字段 | 按 Dock2/Dock3 页定稿 | |
| §5-58 | `FlighttaskProgressGetReply.output` 由 `Object` 具体化 | 具体化 + 升 `@Verified` | |
| §5-64 | template.kml `Placemark` 补 `isRisky`/`useStraightLine` | 补（`Integer`） | |
| §5-65 | `Mapping2dPlacemark` 补 `quickOrthoMappingEnable`/`quickOrthoMappingPitch`（M4E） | 补 | |
| §5-67 | 航点 `Folder` 是否补 `payloadParam`（表列 vs 示例缺席） | 倾向维持现状（层级归属须你确认） | |
| §5-68 | `ExecuteFolder` 补 `startActionGroup` | 补 | |
| §5-69 | waylines.wpml `ExecutePlacemark` 补 `isRisky`/`useStraightLine` | 补（与 §5-64 一并） | |
| §5-77 | 补 `StateField.BEST_LINK_GATEWAY`/`FLYSAFE_DATABASE_VERSION` + `DroneOsd` 2 字段 | 补 | |
| §5-79 | `DroneModeCode` 补 `21=进离场航线飞行` | 补 | |
| §5-81 | `CameraInfo` 补 20 字段 + `IrMeteringPoint`/`IrMeteringArea` | 补 | |
| §5-84 | `DroneAuthorityInfo` 建模 + `OsdField`/`StateField` 重复定义清理 | 补 + 清理 | |
| §5-93 | `AirportOrganizationGetReply` 补 `output{organizationName}` | 补 | |
| §5-94 | `AirportBindStatusReply` 补 `output{bindStatus[]}` | 补 | |
| §5-100 | `FlightSetupExceptionNotifyData` 建模 + `EventMethod` Javadoc 更正 | 补 | |
| §5-113 | 补 `ServiceMethod.FLIGHT_AREAS_UPDATE` | 补（空请求/回复复用） | |

### 9.4 可选增强（9 项，惯例/便利，可不做）

| 编号 | 问题 | 建议默认 | 裁决 |
|---|---|---|---|
| §5-23 | `PowerUnlock` 空 struct：空 record vs 省略字段 | 空 record 占位 | |
| §5-42 | `last_point_type`/`plan_status` 是否建枚举 | 补枚举 | |
| §5-53 | `module`/`module_list` 是否建枚举 | 维持 `String`（可选枚举） | |
| §5-95 | `url_type`/`video_type` 是否枚举化 | 可选 | |
| §5-99 | `storage_config_get.provider` 是否枚举化 | 可选 | |
| §5-105 | `ActionSwitchRequest` 死代码去留（通用 vs 专用双份） | 删除（职责归 3 专用 POJO） | |
| §5-106 | 12 进度事件 `step_key` 是否枚举化 | 可选 | |
| §5-116 | `FlightAreasGetRequest` 复用 `NoParameterRequest` | 复用（消重） | |
| §5-124 | `dongle_infos` 内层 5 枚举是否强类型建模 | 可选（A82 已由 `StateField` 捕获） | |

### 9.5 新增功能 / 范围（2 项）

| 编号 | 问题 | 建议默认 | 裁决 |
|---|---|---|---|
| §5-60 | 「最低固件版本」是否纳入 SDK（`minimumFirmwareVersion(model)`） | 暂不纳入 | |
| §5-61 | `isCompatible(RC_PRO, drone)` 是否扩展至 Dock 侧机型 | 需你确认搭配后再定 | |

### 9.6 DJI 仓库 ↔ SDK 差集（3 项，是否纳入）

| 编号 | 问题 | 建议默认 | 裁决 |
|---|---|---|---|
| §5-72 | `compressed_sticks_control`（手写层，文档未列） | 暂不纳入 | |
| §5-73 | DJI 仓库独有 DRC（63 下行 + 6 推送） | 暂不纳入 | |
| §5-76 | 非 DRC 差集（services ~35 / requests 7 / status 1 / events 若干） | 暂不纳入 | |

### 9.7 机型「子集」口径（5 项，建议维持超集）

| 编号 | 问题 | 建议默认 | 裁决 |
|---|---|---|---|
| §5-102 | `simulate_mission.altitude`（Dock3 专属？） | 维持超集（null）+ `@Inferred` | |
| §5-103 | Dock1 `flighttask_prepare` 为 Dock3 子集 | 维持超集 | |
| §5-107 | Dock1 `fileupload_start` 为 O4 子集 | 维持超集 | |
| §5-109 | `drone_control` 废弃口径 / `heart_beat.seq` | 不补 `seq`（A80 已倾向） | |
| §5-111 | Dock1 `takeoff_to_point` 为 O4 子集 | 维持超集 | |

### 9.8 对照：已裁决、仅待落地（无需再裁决）

§5-6（并入 §5-52）、§5-16、§5-17、§5-21、§5-22、§5-29、§5-31、§5-35、§5-36、§5-38、§5-39、§5-45、§5-70、§5-74、§5-75（维持）、§5-78、§5-80、§5-82、§5-86、§5-87、§5-89、§5-90、§5-96、§5-101、§5-110、§5-112、§5-115、§5-118、§5-119、§5-120、§5-121、§5-123。

> **§9 为清单汇总（派生自 §5），未写代码**（遵守「原文收齐前不编码」硬约束）。

---

*本报告遵循 AGENTS.md 硬约束：不确定内容记录在案，实现前经开发者确认；协议字段/枚举值以 DJI 官方文档为唯一依据。*
