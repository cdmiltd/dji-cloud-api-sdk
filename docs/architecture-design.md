# DJI Cloud API SDK — 架构设计文档

- **状态**：已交付
- **日期**：2026-08-14
- **版本**：v1.0（对齐 DJI Cloud API 文档 v1.16.1）
- **关联文档**：
  - [TDD 测试套件设计](superpowers/specs/2026-08-14-tdd-test-suite-design.md)
  - [TDD 测试用例文档](tdd-test-cases.md)
  - [DJI Cloud API 文档对比分析报告](../dji-cloud-api-doc-comparison/dji-cloud-api-doc-comparison.html)

---

## 1. 项目概述

### 1.1 背景与定位

`dji-cloud-api-sdk` 是从 DJI Dock 模拟器抽取的 **协议定义层**，作为模拟器与 hivemind 平台的共享
"单一真相源"（Single Source of Truth）。项目将 DJI Cloud API 的协议常量、数据结构和流程定义
从业务代码中剥离，以纯 Java 类型（record + enum + 静态工具类）表达，消除两个系统间协议定义的重复维护。

### 1.2 设计目标

| 目标 | 说明 |
|---|---|
| **协议单一真相源** | 模拟器与平台引用同一套协议定义，避免不一致 |
| **文档可追溯** | 每个协议元素标注 DJI 官方文档 URL 与核实状态（`@DocUrl`/`@Verified`/`@Inferred`） |
| **零运行时依赖** | 仅依赖 Jackson（JSON 编解码），不引入 Spring 等框架 |
| **类型安全** | 用枚举替代魔法字符串/数字，编译期捕获协议常量错误 |
| **不可变** | 全部 record + final 类，线程安全，无状态变更风险 |

### 1.3 非目标

- **不实现业务逻辑** — 本 SDK 只定义"协议是什么"，不实现"怎么用"
- **不管理运行时状态** — 无连接管理、无重试、无超时控制（由调用方实现）
- **不耦合 MQTT 客户端** — 仅定义 topic 模板与消息结构，不绑定具体 MQTT 库
- **不耦合 HTTP/WS 客户端** — `http/` 仅定义 API 路径常量与 STS 凭证结构，`websocket/` 仅定义 `biz_code` 枚举与推送消息 record；不绑定 Spring `RestTemplate`/`WebClient`、Tyrus/Jetty 等 HTTP/WS 客户端实现（由调用方集成）
- **不实现指令 POJO 全集** — `command/` 子包已落地 97 个 services + 42 个 drc/down（含 11 个 v1.16 AI + 1 个 Pilot + 11 个 DRC 状态/相机参数）+ 12 个 drc/up（含 1 个 v1.16 AI）+ 29 个 event + 7 个 request + 1 个 status 指令 POJO，共 188 个方法、250 个 record（见 [command 设计文档](superpowers/specs/2026-08-14-command-pojo-design.md)）

---

## 2. 架构总览

### 2.0 多模块结构

自 v1.16.1.0 起，项目拆分为两个 Maven 模块，分离"协议定义层"与"航线工具层"：

```
dji-cloud-api-sdk/                        ← Git 根目录
├── pom.xml                               ← 父 pom（packaging=pom）
├── sdk/                                  ← 子模块 1：协议定义层
│   ├── pom.xml                              artifactId=dji-cloud-api-sdk
│   └── src/main/java/.../cloudapi/sdk/
│       ├── annotation/                      文档追溯注解
│       ├── codec/                           JSON 编解码
│       ├── protocol/                        协议定义（topic/method/envelope/error）
│       ├── command/                         指令 POJO（service/drc/event/property/request/status）
│       ├── model/                           设备型号
│       ├── telemetry/                       遥测数据（OsdField/StateField/DockOsd/DroneOsd/RcOsd）
│       ├── wayline/model/ + enumtype/       WPML 协议 POJO + 枚举（纯 record/enum，零业务逻辑）
│       ├── flow/                            注册流程
│       ├── http/                            HTTP API
│       ├── websocket/                       WebSocket 推送
│       └── capture/                         录制配置
│
└── sdk-wayline/                          ← 子模块 2：航线工具层
    ├── pom.xml                              artifactId=dji-cloud-api-sdk-wayline
    └── src/main/java/.../cloudapi/sdk/wayline/
        ├── WaypointTemplate.java            航点航线模板（Builder 模式，含校验/IO）
        ├── Mapping2d/3d/StripTemplate.java  建图航线模板
        ├── WaypointBuilder.java             航点构造器
        ├── ActionGroupBuilder.java          动作组构造器
        ├── ActionBuilder.java               动作构造器
        ├── PayloadParamBuilder.java         负载参数构造器
        ├── WpmlCodec.java                   WPML 编解码（XML 序列化 + ZIP/KMZ 打包）
        ├── WpmlStreamWriter.java            XML 流写入器（命名空间修复）
        └── WpmlOutputFactory.java           XML 输出工厂
```

**依赖关系**：`sdk-wayline` → `sdk`（sdk-wayline 依赖 sdk 的 WPML POJO + 枚举）

**消费方式**：
- 只需协议定义：`ltd.cdmi:dji-cloud-api-sdk`
- 需要航线文件生成/解析：`ltd.cdmi:dji-cloud-api-sdk` + `ltd.cdmi:dji-cloud-api-sdk-wayline`

### 2.1 包结构

```
ltd.cdmi.dji.cloudapi.sdk
├── annotation/              ← 文档追溯注解（3 类）
│   ├── DocUrl.java             标注 DJI 官方文档 URL
│   ├── Verified.java           标注已通过文档核实
│   └── Inferred.java           标注为推断定义，待真机验证
│
├── codec/                   ← JSON 编解码层（2 类 + package-info）
│   ├── MessageCodec.java       ObjectMapper 静态封装（toJson/fromJson）
│   ├── DjiMessage.java         MQTT 信封 record，含 parse()/extract* 解析方法（泛型 T）
│   └── package-info.java       错误处理风格说明（-1 哨兵）
│
├── protocol/                ← 协议定义层
│   ├── topic/                  ← MQTT Topic 体系（5 类）
│   │   ├── TopicChannel.java      14 通道枚举（suffix + direction）
│   │   ├── TopicDirection.java    UP/DOWN 方向枚举
│   │   ├── TopicTemplate.java     thing/product vs sys/product 模板
│   │   ├── TopicBuilder.java      topic 构造工具
│   │   └── TopicResolver.java     topic 解析工具（→ TopicInfo record）
│   │
│   ├── method/                 ← Method 名称枚举（7 类 + package-info）【已落地骨架】
│   │   ├── StatusMethod.java      1 个（update_topo）
│   │   ├── RequestsMethod.java    7 个（config/airport_bind_status/airport_organization_get/airport_organization_bind/storage_config_get/flighttask_progress_get/flight_areas_get）
│   │   ├── EventMethod.java       29 个（flighttask_progress/fly_to_point_progress/ota_progress/file_upload_callback/hms/...，含 5 个原有 @Inferred + 1 个补全 @Inferred drc_status_notify）
│   │   ├── DrcMethod.java         42 个（drc/down 远程控制指令，19 个 simulator catalog + 11 个 v1.16 AI + 1 个 Pilot + 11 个 DRC 状态/相机参数；@Inferred 标注 HEART_BEAT + 8 个 DRC 相机参数）
│   │   ├── DrcUpMethod.java       12 个（drc/up 上行推送方法，10 个 simulator 已对接 + 1 个 Pilot @Inferred + 1 个 v1.16 AI）
│   │   ├── ServiceMethod.java    97 个（services 通道方法，71 catalog + 18 补全 OTA/日志/空中航线/PSDK/ESDK + 8 回归补全 flysafe/Pilot；@Verified 全部已验证）
│   │   ├── PropertySetMethod.java 18 个（property/set 通道可设置属性，Dock3 4 + M3D 9 + M3D 红外 5；含 v1.16.1 新增 remaining_power_for_return_home）
│   │   └── package-info.java      错误处理风格说明（Optional）
│   │
│   ├── envelope/               ← 消息信封结构（3 类）
│   │   ├── RequestEnvelope.java   请求信封（tid/bid/timestamp/method/data）
│   │   ├── ReplyEnvelope.java     回复信封（data=ReplyData{result,output}）
│   │   └── EventEnvelope.java     事件信封（结构同 RequestEnvelope）
│   │
│   └── error/                  ← 错误码（3 类）
│       ├── DjiErrorCode.java      233 个错误码常量 + Map 查表 + describe() 方法
│       ├── DjiErrorInfo.java      错误码描述条目 record（code + description）
│       └── ErrorInfo.java         逐设备错误信息 record
│
├── http/                    ← HTTP API 层（2 类）【已落地】
│   ├── HttpApiPath.java         21 个路径常量（6 BASE_PATH + 15 具体端点，覆盖 manage/map/media/storage/wayline 五类）
│   └── StsCredentials.java      STS 上传凭证 record（@Inferred 标注 credentials 子结构字段名待真机确认）
│
├── websocket/               ← WebSocket 推送层（2 类）【已落地】
│   ├── WsBizCode.java           8 个 biz_code 枚举（4 地图元素 + 4 设备拓扑推送）
│   └── WsPushMessage.java      推送消息信封 record（biz_code/version/timestamp/data）
│
├── command/                 ← 指令 POJO 层（147 类 + 33 package-info）【已落地 92 services + 19 drc + 28 event + 7 request + 1 status 指令】
│   ├── service/                 ← services 通道指令 POJO（92 个指令，12 个子包 + 根目录共享 record）
│   │   ├── NoParameterRequest.java         通用空 Request（32 个无参数指令共用）
│   │   ├── NoOutputReply.java              通用空 Reply（无 output 指令共用，@Inferred）
│   │   ├── SimulateMission.java            跨包共享嵌套 record（wayline + flight 共用，@Inferred altitude 字段）
│   │   ├── package-info.java       子包结构说明 + 命名规则 + Reply 对称性约定
│   │   ├── wayline/                ← 航线任务（16 类 + 1 package-info）
│   │   │   ├── FlighttaskPrepareRequest/Reply   14 字段 + 空 Reply（含 4 嵌套 record）
│   │   │   ├── FlighttaskExecuteRequest/Reply   multiDockTask 蛙跳嵌套 + 空 Reply（含 5 嵌套 record）
│   │   │   ├── FlighttaskUndoRequest            flightIds(List<String>)
│   │   │   ├── FlighttaskStopRequest            flightId, reason
│   │   │   └── ReturnSpecificHomeRequest        homeDockSn
│   │   ├── camera/                ← 相机/负载控制（22 类 + 1 package-info）
│   │   │   ├── CameraPhotoTake/StopRequest          payloadIndex
│   │   │   ├── CameraRecordingStart/StopRequest     payloadIndex
│   │   │   ├── CameraModeSwitchRequest              payloadIndex, cameraMode
│   │   │   ├── CameraFocalLengthSetRequest         payloadIndex, cameraType, zoomFactor
│   │   │   ├── GimbalResetRequest                   payloadIndex, resetMode
│   │   │   ├── CameraScreenSplitRequest             payloadIndex, enable
│   │   │   ├── CameraExposureModeSet/SetRequest     payloadIndex, cameraType, exposureMode/Value
│   │   │   ├── CameraFocusModeSet/ValueSetRequest  payloadIndex, cameraType, focusMode/Value
│   │   │   ├── Photo/VideoStorageSetRequest         payloadIndex, photo/videoStorageSettings(List)
│   │   │   ├── IrMeteringMode/PointSetRequest       payloadIndex, mode / x, y
│   │   │   ├── CameraScreenDragRequest              payloadIndex, locked, pitchSpeed, yawSpeed
│   │   │   ├── CameraPointFocusActionRequest        payloadIndex, cameraType, x, y
│   │   │   ├── CameraAimRequest                     payloadIndex, cameraType, locked, x, y
│   │   │   ├── CameraLookAtRequest                  payloadIndex, locked, lat, lng, height
│   │   │   ├── IrMeteringAreaSetRequest             payloadIndex, x, y, width, height
│   │   │   └── CameraFrameZoomRequest               payloadIndex, cameraType, locked, x, y, width, height
│   │   ├── live/                   ← 直播（6 类 + 1 package-info）
│   │   │   ├── LiveStartPushRequest/Reply       4 必填字段 + 空 Reply
│   │   │   ├── LiveStopPushRequest              videoId
│   │   │   ├── LiveSetQualityRequest            videoId, videoQuality
│   │   │   ├── LiveCameraChangeRequest          videoId, cameraPosition
│   │   │   └── LiveLensChangeRequest            videoType, videoId（RC Plus/RC Pro 必填，Dock 不适用）
│   │   ├── flight/                 ← 飞行控制（6 类 + 1 package-info）
│   │   │   ├── FlyToPointTarget               latitude, longitude, height（points 数组元素）
│   │   │   ├── FlyToPointRequest               flyToId, maxSpeed(可选), points: List<FlyToPointTarget>
│   │   │   ├── FlyToPointUpdateRequest         maxSpeed(可选), points: List<FlyToPointTarget>
│   │   │   ├── TakeoffToPointRequest/Reply     14 字段双阶段 + 空 Reply（含 SimulateMission 跨包引用）
│   │   │   └── PayloadAuthorityGrabRequest     payloadIndex
│   │   ├── drc/                    ← DRC 模式切换（2 类 + 1 package-info）
│   │   │   ├── DrcModeEnterRequest             mqttBroker(DrcMqttBroker), hsiFrequency, osdFrequency
│   │   │   └── DrcMqttBroker                   address, clientId, username, enableTls, expireTime
│   │   ├── media/                  ← 媒体管理（1 类 + 1 package-info）
│   │   │   └── UploadFlighttaskMediaPrioritizeRequest  flightId
│   │   ├── firmware/              ← OTA 固件升级（1 类 + 1 package-info）【补全】
│   │   │   └── OtaCreateRequest              devices[]{sn,productVersion,firmwareUpgradeType,fileUrl?,md5?,fileSize?,fileName?}
│   │   ├── log/                    ← 远程日志上传（4 类 + 1 package-info）【补全】
│   │   │   ├── FileUploadStartRequest        params{files[]{objectKey,size,type},boot{@class,args[]}}（@Inferred params 包装层）
│   │   │   ├── FileUploadUpdateRequest       fileId, status
│   │   │   ├── FileUploadListRequest         fileFilter(模块过滤)
│   │   │   └── FileUploadListReply           groups[]{module,files[]{objectKey,size,name,...}}
│   │   ├── wayline/psdk/          ← 空中航线（InFlightWaylineDeliver/Stop/RecoverRequest，见 wayline 子包）
│   │   ├── psdk/                   ← PSDK 控制（8 类 + 1 package-info）【补全】
│   │   │   ├── SpeakerPlayVolumeSetRequest    psdkIndex, playVolume
│   │   │   ├── SpeakerPlayModeSetRequest      psdkIndex, playMode
│   │   │   ├── SpeakerPlayStopRequest          psdkIndex
│   │   │   ├── SpeakerReplayRequest            psdkIndex
│   │   │   ├── SpeakerTtsPlayStartRequest     psdkIndex, volume, type, language, speed, text
│   │   │   ├── SpeakerAudioPlayStartRequest    psdkIndex, md5, audioType
│   │   │   ├── PsdkInputBoxTextSetRequest      psdkIndex, value
│   │   │   └── PsdkWidgetValueSetRequest       psdkIndex, widgetValue
│   │   ├── esdk/                   ← ESDK 控制（1 类 + 1 package-info）【补全】
│   │   │   └── CustomDataTransmissionToEsdkRequest  value
│   │   ├── esim/                   ← eSIM 管理（3 类 + 1 package-info）【补全】
│   │   │   ├── EsimActivateRequest            eid, esimInfos[]{operator,iccid,imsi}（@Inferred）
│   │   │   ├── EsimOperatorSwitchRequest      imei, deviceType, esimOperator（@Verified DJI 文档确认）
│   │   │   └── SimSlotSwitchRequest           slotType（@Inferred）
│   │   └── debug/                  ← 设备控制（5 类 + 1 package-info）【补全】
│   │       ├── RtkCalibrationRequest          caliType（@Inferred，仅 Dock3）
│   │       ├── BatteryStoreModeSwitchRequest  mode（@Inferred）
│   │       ├── AlarmStateSwitchRequest         action（@Inferred）
│   │       ├── AirConditionerModeSwitchRequest  mode（@Inferred）
│   │       └── SdrWorkmodeSwitchRequest        linkWorkmode（@Inferred）
│   ├── drc/                      ← drc 通道指令 POJO（42 个 drc/down 指令 + 12 个 drc/up 推送 POJO，7 个子包 + 根 DrcResultReply）【已落地】
│   │   ├── DrcResultReply.java         通用 {result} 回复（26 个 drc/down 指令共用，区别于 services_reply 的 {result,output}）
│   │   ├── package-info.java           DRC 消息格式说明 + 回复行为分类
│   │   ├── up/                    ← drc/up 上行推送 POJO（12 个，对应 DrcUpMethod 枚举）
│   │   ├── safety/                ← 飞行安全（3 个空请求指令，package-info 文档化，回复用 DrcResultReply）
│   │   ├── flight/                ← 飞行控制（4 类 + 1 package-info）
│   │   │   ├── StickControlRequest          roll, pitch, throttle, yaw（无回包）
│   │   │   ├── DroneControlRequest          seq, x, y, h, w（无回包，已废弃 @Inferred）
│   │   │   └── HeartBeatRequest/Reply       timestamp（回显，@Inferred 发起方未明确）
│   │   ├── camera/                ← 相机高级控制 Dock3（4 类 + 1 package-info）
│   │   │   ├── DrcCameraNightModeSetRequest         payloadIndex, mode
│   │   │   ├── DrcCameraDenoiseLevelSetRequest       payloadIndex, level
│   │   │   ├── DrcCameraNightVisionEnableRequest     payloadIndex, enable
│   │   │   └── DrcInfraredFillLightEnableRequest     payloadIndex, enable
│   │   ├── light/                 ← 探照灯控制 Dock3（4 类 + 1 package-info）
│   │   │   ├── DrcLightBrightnessSetRequest    psdkIndex, brightness
│   │   │   ├── DrcLightModeSetRequest           psdkIndex, mode
│   │   │   ├── DrcLightFineTuningSetRequest     psdkIndex, position, value
│   │   │   └── DrcLightCalibrationRequest       psdkIndex
│   │   ├── speaker/               ← 喊话器控制 Dock3（5 类 + 1 package-info）
│   │   │   ├── DrcSpeakerPlayModeSetRequest      psdkIndex, playMode
│   │   │   ├── DrcSpeakerTtsSetRequest            psdkIndex, volume, type, language, speed（@Inferred text 字段待确认）
│   │   │   ├── DrcSpeakerPlayVolumeSetRequest     psdkIndex, playVolume
│   │   │   ├── DrcSpeakerPlayStopRequest          psdkIndex
│   │   │   └── DrcSpeakerReplayRequest            psdkIndex
│   │   └── ai/                    ← AI 目标识别 Dock3 v1.16（11 个 drc/down + 1 个 drc/up 推送，7 个 POJO + 6 个枚举 + 1 package-info）
│   │       ├── AiModelSelectRequest            index
│   │       ├── AiIdentifySetRequest            on（AiSwitchState）
│   │       ├── AiIdentifyScoreModeSetRequest   scoreMode（AiScoreMode）
│   │       ├── AiIdentifyScoreSetRequest        score
│   │       ├── AiIdentifyFilterSetRequest       filters（List<Integer>，128 偏移规则）
│   │       ├── AiSpotlightZoomSetRequest        on（AiSwitchState）
│   │       ├── AiSpotlightZoomTrackRequest      targetIndex
│   │       ├── AiSpotlightZoomSelectRequest     centerX, centerY, width, height（归一化坐标 ×10000）
│   │       ├── AiInfoPushData                  drc/up 状态推送（含 6 个子 record，AiWaylineState 标 @Inferred）
│   │       ├── AiSwitchState                   0=OFF, 1=ON
│   │       ├── AiScoreMode                     0=INVALID, 1=COUNT, 2=SEARCH_RESCUE, 3=CUSTOM
│   │       ├── AiTrackState                    0=IDLE, 1=WAITING_SELECT, 2=WAITING_CONFIRM, 3=TRACKING
│   │       ├── AiTrackStateReason              0-15 正常原因 + 160-168 退出原因（共 25 个）
│   │       ├── AiImageSource                   1=WIDE, 2=ZOOM, 3=IR, 7=VISIBLE_LIGHT（enum_list 多选）
│   │       └── AiDigitalEffect                 0=WHITE_HOT, 1=BLACK_HOT, 2=RED_HOT（enum_list 多选）
│   ├── event/                    ← events 通道事件 POJO（28 个事件，9 个子包 + 根 PathPoint 共享 record）【已落地】
│   │   ├── PathPoint.java              共享轨迹点（latitude/longitude/height，wayline/flight 子包共用）
│   │   ├── package-info.java           event 子包总说明
│   │   ├── wayline/              ← 航线任务事件（5 类 + 1 package-info）
│   │   │   ├── FlighttaskProgressData       result, output{ext, progress, status}（含嵌套 BreakPoint）
│   │   │   ├── FlighttaskReadyData           flightIds
│   │   │   ├── InFlightWaylineProgressData   inFlightWaylineId, progress{percent}, status, result, wayPointIndex
│   │   │   ├── ReturnHomeInfoData            plannedPathPoints, lastPointType, flightId, homeDockSn?, multiDockHomeInfo?（@Inferred 蛙跳）
│   │   │   └── DeviceExitHomingNotifyData    sn, action, reason（@Inferred reason 类型 enum_int vs 字符串待验证）
│   │   ├── flight/               ← 指令飞行事件（7 类 + 1 package-info）
│   │   │   ├── FlyToPointProgressData        flyToId, status, result, wayPointIndex, remainingDistance/Time, plannedPathPoints
│   │   │   ├── TakeoffToPointProgressData    status, result, flightId, trackId, wayPointIndex, remainingDistance/Time, plannedPathPoints
│   │   │   ├── PoiCircleStatusData           status, reason, circleRadius, circleSpeed, maxCircleSpeed（@Inferred 方法名待验证）
│   │   │   ├── CameraPhotoTakeProgressData   result, output{status, progress, ext}
│   │   │   ├── PhotoProgressData             result, output（@Inferred 最小骨架，simulator 未实现）
│   │   │   ├── ObstacleAvoidanceNotifyData   waylineUuid, flightId, obstacles[]{id,type,timestamp,lat,lng,height,waylineId,waypointIndex}, isFinalReport（仅 Dock3）
│   │   │   └── JoystickInvalidNotifyData     reason（0-4 枚举，三 Dock 共有）
│   │   ├── system/               ← 系统/升级/日志事件（2 类 + 1 package-info）
│   │   │   ├── OtaProgressData               result, output{status, progress{percent, currentStep}}
│   │   │   └── FileUploadProgressData         result, output{status, ext{files[]}}（4 层嵌套：FileItem/FileProgress）
│   │   ├── media/                ← 媒体管理事件（2 类 + 1 package-info）
│   │   │   ├── HighestPriorityUploadFlighttaskMediaData  flightId
│   │   │   └── FileUploadCallbackData         file{ext, metadata, name, objectKey, path}（4 层嵌套）
│   │   ├── flightarea/           ← 自定义飞行区事件（2 类 + 1 package-info）
│   │   │   ├── FlightAreasDroneLocationData  droneLocations[]{areaDistance, areaId, isInArea}
│   │   │   └── FlightAreasSyncProgressData   status, reason, file?{name, checksum}
│   │   ├── alert/                ← 告警/授权事件（3 类 + 1 package-info）
│   │   │   ├── CloudControlAuthNotifyData     result, output{status}
│   │   │   ├── AirSenseWarningData            List<Alert>（data 直接是数组，特殊结构）
│   │   │   └── HmsData                        list[]{code, level, module, inTheSky, deviceType, imminent, args}
│   │   ├── psdk/                 ← PSDK 事件（3 类 + 1 package-info）【新增】
│   │   │   ├── PsdkFloatingWindowTextData       psdkIndex, value（data 直接平铺，非 output 包裹）
│   │   │   ├── PsdkUiResourceUploadResultData    psdkIndex, objectKey, size, result
│   │   │   └── CustomDataFromPsdkData            value（@Inferred need_reply 待验证）
│   │   ├── esdk/                 ← ESDK 事件（1 类 + 1 package-info）【新增】
│   │   │   └── CustomDataFromEsdkData            value（@Inferred need_reply 待验证）
│   │   └── speaker/              ← 喊话器/音频播放事件（3 类 + 1 package-info）
│   │       ├── SpeakerOutput                 共享：psdkIndex, status, md5, progress{percent, stepKey}
│   │       ├── SpeakerTtsPlayStartProgressData    result, output（引用 SpeakerOutput）
│   │       └── SpeakerAudioPlayStartProgressData  result, output（引用 SpeakerOutput）
│   ├── request/                 ← requests 通道指令 POJO（7 个方法，4 个子包）【已落地】
│   │   ├── package-info.java           request 子包总说明
│   │   ├── registration/         ← 机场注册流程（4 个方法 × Request + Reply = 8 类 + 1 package-info）
│   │   │   ├── ConfigRequest             config_type/config_scope
│   │   │   ├── ConfigReply               result/appId/appLicense/url/token（@Inferred data 非包裹 output）
│   │   │   ├── AirportBindStatusRequest  devices[]{sn}
│   │   │   ├── AirportBindStatusReply    result
│   │   │   ├── AirportOrganizationGetRequest  deviceBindingCode/organizationId
│   │   │   ├── AirportOrganizationGetReply     result（@Inferred output 待验证）
│   │   │   ├── AirportOrganizationBindRequest  bindDevices[]{sn, deviceModelKey, deviceCallsign, organizationId, deviceBindingCode}
│   │   │   └── AirportOrganizationBindReply    result, output{errInfos[]{errCode, errSn}}
│   │   ├── config/               ← 存储配置查询（1 个方法 × Request + Reply = 2 类 + 1 package-info）
│   │   │   ├── StorageConfigGetRequest   module（0=媒体/1=日志）
│   │   │   └── StorageConfigGetReply      result, output{bucket, endpoint, region, provider, objectKeyPrefix, credentials{...}}
│   │   ├── wayline/              ← 蛙跳任务进度查询（1 个方法 × Request + Reply = 2 类 + 1 package-info）
│   │   │   ├── FlighttaskProgressGetRequest   flightId, sn?（目标机场 SN）
│   │   │   └── FlighttaskProgressGetReply      result, output（@Inferred 结构待验证）
│   │   └── flightarea/           ← 自定义飞行区查询（1 个方法 × Request + Reply = 2 类 + 1 package-info）
│   │       ├── FlightAreasGetRequest    空 record（无参数）
│   │       └── FlightAreasGetReply      result, output{file{name, checksum}}
│   ├── status/                  ← status 通道指令 POJO（1 个方法 = 2 类 + 1 package-info）【已落地】
│   │   ├── package-info.java           status 子包总说明
│   │   ├── UpdateTopoData             domain(String)/type/subType/deviceSecret/nonce/subDevices[]{sn,...}/thingVersion
│   │   └── UpdateTopoReplyData        result, subType?（0=上线/1=下线，@Inferred 待验证）
│   └── package-info.java         command 子包总说明（与 envelope/method 对齐）
│
├── model/                   ← 设备型号层（7 类）
│   ├── DeviceDomain.java        domain 枚举（0=飞行器/2=遥控器/3=机场）
│   ├── DeviceModel.java         型号三元组 record（domain/type/subType + 展示信息）
│   ├── DeviceModelProvider.java 接口（enum → DeviceModel 转换契约 + default 委托方法）
│   ├── DroneModel.java          14 种飞行器型号枚举 + fromType/fromModelKey 反查
│   ├── DockModel.java           3 种机场型号枚举 + fromType/fromModelKey 反查
│   ├── RcModel.java             4 种遥控器型号枚举 + fromType/fromModelKey 反查
│   └── DeviceCompatibility.java 兼容性矩阵（dock↔drone / controller↔drone）
│
├── telemetry/               ← 遥测数据层
│   ├── OsdField.java            65 个 OSD 字段名枚举（pushMode=0，周期推送）
│   ├── StateField.java          35 个 State 字段名枚举（pushMode=1，变化推送）
│   ├── DroneOsd.java            飞行器 OSD record（33 组件）
│   ├── DockOsd.java             机场 OSD record（37 组件）
│   ├── RcOsd.java       遥控器 OSD record（5 组件）
│   └── enumtype/                ← 遥测枚举类型（7 类 + package-info）
│       ├── Gear.java               飞行器档位（10 档，0-9）
│       ├── DroneModeCode.java      飞行器模式码（0-20）
│       ├── DockModeCode.java       机场模式码（0-5）
│       ├── ModeCodeReason.java     飞行器模式码触发原因（0-23，对应 mode_code_reason 字段）
│       ├── BatteryStoreMode.java   机场电池存储模式（1-2，1=计划模式/2=待命模式，无 0 值）
│       ├── PositionState.java      RTK 定位收敛状态（0-3）
│       ├── DroneChargeState.java   飞行器充电状态（0-1）
│       └── package-info.java       错误处理风格说明（抛异常）
│
└── flow/                    ← 注册流程层（3 类）
    ├── RegistrationStep.java    流程步骤 record（method/channel/timeout/retry）
    ├── DockRegistrationFlow.java   机场上云 5 步注册流程
    └── PilotRegistrationFlow.java  Pilot 上云 5 步注册流程
```

**编译单元统计**：101 个类/接口 + 12 个 package-info = 113 个编译单元，分布于 20 个包。
（含 http 子包 2 类、websocket 子包 2 类、protocol/method 子包 5 类 + 1 package-info、
command 子包 1 package-info + command/service 子包 3 共享类 + 1 package-info +
6 个子包共 52 类 + 6 package-info）

### 2.2 分层模型

```
┌─────────────────────────────────────────────────────┐
│                   annotation                         │  ← 横切层：文档追溯注解
│              (@DocUrl / @Verified / @Inferred)       │
├─────────────────────────────────────────────────────┤
│                                                      │
│  ┌──────────┐   ┌──────────────────────────────┐   │
│  │  codec   │   │         protocol              │   │  ← 协议核心层（MQTT）
│  │ (JSON)   │   │  (topic / method / envelope   │   │
│  │          │   │   / error)                    │   │
│  └────┬─────┘   └──────────┬───────────────────┘   │
│       │                     │                        │
│       └─────────┬───────────┘                        │
│                 │                                     │
│  ┌──────────────▼──────────────────────────┐        │
│  │   http / websocket                       │        │  ← 协议核心层（HTTP + WS）
│  │   (API 路径常量 / STS 凭证 / biz_code)   │        │
│  └──────────────┬──────────────────────────┘        │
│                 │                                     │
│  ┌──────────────▼──────────────┐                    │
│  │     model / telemetry       │                    │  ← 数据模型层
│  │  (设备型号 / OSD / 枚举)     │                    │
│  └──────────────┬──────────────┘                    │
│                 │                                     │
│  ┌──────────────▼──────────────┐                    │
│  │           flow              │                    │  ← 流程编排层
│  │    (注册 / 上线流程)         │                    │
│  └─────────────────────────────┘                    │
│                                                      │
└─────────────────────────────────────────────────────┘
```

### 2.3 依赖规则

| 规则 | 说明 |
|---|---|
| **annotation 无依赖** | `@DocUrl`/`@Verified`/`@Inferred` 是纯注解，不依赖任何其他包 |
| **codec → protocol.topic** | `DjiMessage.parse()` 返回 `DjiMessage`（codec 内部），不直接引用 topic 包 |
| **protocol.topic 自包含** | `TopicResolver` 引用同包 `TopicChannel`/`TopicDirection`，无跨包依赖 |
| **protocol 无跨包依赖** | `protocol.*` 子包间无相互依赖，各自独立 |
| **http 无依赖** | `HttpApiPath` 纯常量；`StsCredentials` 是 record，仅依赖 `annotation`（标注用） |
| **websocket 无依赖** | `WsBizCode`/`WsPushMessage` 仅依赖 `annotation`（标注用），不依赖 MQTT 包 |
| **model 无依赖** | `model` 包仅依赖自身 + `annotation`（标注用） |
| **telemetry → telemetry.enumtype** | OSD record 的字段值引用 enumtype 枚举（Javadoc 引用，非代码依赖） |
| **所有包 → annotation** | 任何协议元素均可标注 `@DocUrl`/`@Verified`/`@Inferred` |

**禁止方向**：`annotation` ← 任何包（注解层不可反向依赖业务层）；`protocol` ← `flow`（协议层不可依赖流程层）。

### 2.4 协议覆盖范围与分包规划

#### 当前覆盖范围

DJI Cloud API 包含三种传输协议，本 SDK 当前 **仅覆盖 MQTT**：

| 协议 | 通信方向 | 主体 | 用途 | SDK 覆盖 |
|---|---|---|---|---|
| **MQTT** | 设备 ↔ 云 双向 | Dock/Pilot ↔ 云平台 | 遥测推送、服务调用、事件通知、设备拓扑、属性设置、DRC 指令 | **全覆盖** |
| **HTTP** | 云平台 → DJI Server REST | 云平台 → DJI REST API | 航线管理、媒体管理、设备管理、飞行任务、直播管理 | 未覆盖 |
| **WebSocket** | 云平台 ↔ 设备 DRC 隧道 | 云平台 ↔ 设备 | 低延迟远程控制（虚拟摇杆、云台控制） | 未覆盖 |

pom.xml 定位 SDK "供模拟器与 hivemind 平台共同引用"——模拟器只需 MQTT，hivemind 平台作为云端还需 HTTP/WebSocket。当前 MQTT-only 状态满足模拟器需求，hivemind 的 HTTP/WebSocket 需求待后续补充。

#### 包名问题：`protocol` → `mqtt`

当前 `protocol` 包名暗示"所有协议"，但实际内容 100% 是 MQTT 特有概念（topic/method/envelope/error），存在 **包名与内容不匹配** 的命名问题。

按"概念核心"原则分析各包归属：

| 包 | 概念核心 | 协议绑定 | 归属 |
|---|---|---|---|
| `protocol.topic` | MQTT topic 结构 | MQTT 特有 | → `mqtt.topic` |
| `protocol.method` | MQTT method 字段 | MQTT 特有 | → `mqtt.method` |
| `protocol.envelope` | MQTT 消息信封 | MQTT 特有 | → `mqtt.envelope` |
| `protocol.error` | MQTT 错误码 | MQTT 特有 | → `mqtt.error` |
| `flow` | 注册流程（步骤绑定 requests/status 通道） | MQTT 特有 | → `mqtt.flow` |
| `telemetry` | 遥测数据结构（字段定义是设备属性规范） | 协议无关 | 保持顶层 |
| `model` | 设备型号身份 | 协议无关 | 保持顶层 |
| `codec` | JSON 编解码 | 协议无关 | 保持顶层 |
| `annotation` | 文档追溯 | 协议无关 | 保持顶层 |

#### 当前实际分包结构

```
sdk/
├── annotation/              ← 共享
├── codec/                   ← 共享
├── model/                   ← 共享
├── telemetry/               ← 共享（数据结构不绑定传输协议）
│   ├── OsdField.java
│   ├── StateField.java
│   ├── DroneOsd.java
│   ├── DockOsd.java
│   ├── RcOsd.java
│   └── enumtype/
├── protocol/                ← MQTT 协议层（原命名保留，迁移至 mqtt/ 待后续重构）
│   ├── topic/
│   ├── method/
│   ├── envelope/
│   └── error/
├── flow/                    ← 注册流程（原计划迁入 mqtt.flow，目前保持顶层）
├── http/                    ← 已落地（HTTP REST API 路径常量与 STS 凭证 POJO）
└── websocket/               ← 已落地（WebSocket biz_code 枚举与推送信封 POJO）
```

**当前状态**：
- `http/` 与 `websocket/` 已实施（不再属于"未来扩展"），分别承载 HTTP REST API 路径常量（`HttpApiPath`、`StsCredentials`）与 WebSocket 推送 POJO（`WsBizCode`、`WsPushMessage<T>`、`DeviceOsdPushData` 等）。
- `protocol` → `mqtt` 重命名 **未执行**：当前 `protocol/` 包名保留，迁移至 `mqtt/` 属于"未来可选重构"（涉及 18 个类移动 + 全量 import 更新，需评估对调用方的影响后再决策）。
- `flow/` 保持顶层（与 `protocol/` 平级），未迁入 `mqtt.flow`。

---

## 3. 核心设计决策

### 3.1 协议定义层抽取：从模拟器到独立 SDK

**决策**：将 DJI Dock 模拟器中的协议常量、数据结构和流程定义抽取为独立 Maven 模块。

**理由**：
- 模拟器与 hivemind 平台都需要 DJI Cloud API 协议定义，各自维护导致不一致
- 协议定义是 **纯数据**（常量、结构、名称），不含业务逻辑，天然适合独立模块
- 独立模块可被其他系统（如测试工具、协议校验器）复用

### 3.2 record + enum 而非类层次

**决策**：用 Java 21 `record` 表达协议数据结构，用 `enum` 表达协议常量，不使用继承类层次。

**理由**：
- DJI 协议是 **数据导向** 的（JSON 消息），不是行为导向的
- `record` 提供不可变性、自动 `equals/hashCode/toString`，且结构一目了然
- `enum` 提供编译期类型安全，避免魔法字符串/数字
- 无继承层次 = 无多态复杂性，协议定义"所见即所得"

**体现**：
- `DroneOsd`/`DockOsd`/`RcOsd` 是 record，不是某个 `Osd` 抽象基类的子类
- `DroneModeCode`/`DockModeCode` 是独立枚举，不是某个 `ModeCode` 枚举的子类

### 3.3 静态工具类而非 Spring Bean

**决策**：`MessageCodec`、`TopicBuilder`、`DeviceCompatibility` 等均为 `final` 类 + 静态方法，不注册为 Spring Bean。

**理由**：
- 协议定义是 **无状态的**，不需要依赖注入、生命周期管理或配置注入
- 静态方法调用更直接，无代理开销
- 不引入 Spring 依赖，SDK 可被非 Spring 应用使用
- 符合"恰如其分"原则：无状态工具不需要框架管理

### 3.4 注解驱动的文档追溯体系

**决策**：设计三个注解（`@DocUrl`/`@Verified`/`@Inferred`）建立代码与官方文档的可追溯链。

| 注解 | 语义 | 属性 |
|---|---|---|
| `@DocUrl` | 标注 DJI 官方文档 URL | `value()` — 文档链接 |
| `@Verified` | 标注已通过文档核实 | `basis()` — 核实依据说明（默认 `""`） |
| `@Inferred` | 标注为推断定义，待真机验证 | `reason()` — 推断理由（必填）；`verifyPoint()` — 验证点 |

**理由**：
- DJI Cloud API 文档迭代频繁（v1.8.0 → v1.16.1），需要明确哪些协议元素已核实、哪些是推断
- `@Inferred` 让使用者在真机验证时能注意到假设性定义，避免盲目信任
- `RUNTIME` 保留策略支持反射扫描，可用于自动化协议校验

**当前 `@Inferred` 项**（3 处）：
- `DrcMethod.HEART_BEAT` — heart_beat 协议格式与发起方未在文档明确
- `ServiceMethod.CLOUD_CONTROL_AUTH_REQUEST` — 授权值 `["flight"]` 未在文档明确
- `PilotRegistrationFlow.UPDATE_TOPO` — RC Plus 2 子设备不上报 domain/index 字段未核实

### 3.5 差异化错误处理策略

**决策**：不同场景采用不同的错误处理风格，而非统一为一种模式。

| 场景 | 风格 | 理由 | package-info |
|---|---|---|---|
| `fromCode(int)` | 抛 `IllegalArgumentException` | 枚举值范围由文档明确定义，未知 code = 协议异常，应快速失败 | `telemetry.enumtype` |
| `fromMethodName(String)` | 返回 `Optional` | method 名随固件升级可能扩展，未知 method 是正常情况，调用者需灵活处理 | `protocol.method` |
| `fromFieldName(String)` | 抛 `IllegalArgumentException` | 字段名由文档定义，未知字段 = 协议异常 | `telemetry`（OsdField/StateField） |
| `extractResult(String)` | 返回 `-1` 哨兵 | result 是整数，`-1` 非合法 DJI 值，区分"解析失败"与"合法错误码" | `codec` |

**理由**：符合"场景驱动设计"原则——简单配置简单处理，复杂配置集中处理。不同枚举的未知值语义不同，不应强制统一。

### 3.6 DroneModeCode / DockModeCode 拆分

**决策**：将历史合并的 `ModeCode` 枚举拆分为 `DroneModeCode`（0-20）与 `DockModeCode`（0-5）两个独立枚举。

**理由**：
- 飞行器模式码与机场模式码是 **完全不同的两套枚举**，值范围不重叠，语义无关
- 合并定义会导致类型不安全（可传入机场模式码给飞行器字段）
- 拆分后各自 `fromCode` 独立，清晰表达"这是机场的 mode_code"vs"这是飞行器的 mode_code"

### 3.7 TopicBuilder 前缀歧义处理

**决策**：将 `TopicBuilder.build(sn, channel)` 标 `@Deprecated`，新增 `build(sn, channel, useSysPrefix)` 与 `buildWithSysPrefix(sn, channel)`。

**理由**：
- `status`/`status_reply` 通道在机场上云用 `sys/product` 前缀，在 Pilot 上云用 `thing/product` 前缀
- 前缀取决于 **网关类型**，无法仅凭 channel 自动判断
- `@Deprecated` 提醒调用者注意前缀选择，避免误用

---

## 4. 分包详细设计

### 4.1 annotation — 文档追溯注解

**职责**：提供协议元素与 DJI 官方文档的可追溯标注能力。

| 类 | 类型 | 说明 |
|---|---|---|
| `DocUrl` | `@interface` | 标注文档 URL，`@Target({TYPE, FIELD, METHOD})`，`@Retention(RUNTIME)` |
| `Verified` | `@interface` | 标注已核实，`basis()` 默认 `""` |
| `Inferred` | `@interface` | 标注待验证，`reason()` 必填，`verifyPoint()` 默认 `""` |

**设计要点**：
- 三个注解均 `@Retention(RUNTIME)`，支持反射扫描
- `@Target` 含 `FIELD`，覆盖枚举常量（Java 中枚举常量隐式为 static field）
- `@Inferred.reason()` 无默认值（必填），确保推断项必附理由

### 4.2 codec — JSON 编解码

**职责**：封装 Jackson `ObjectMapper`，提供 JSON 编解码基础设施（`MessageCodec`）与 DJI MQTT 信封解析（`DjiMessage`）。

| 类 | 说明 |
|---|---|
| `MessageCodec` | 静态封装 `ObjectMapper`，提供 `toJson`/`fromJson`/`readTree`/`treeToValue` |
| `DjiMessage<T>` | MQTT 信封 record，提供 `parse`/`extractMethod`/`extractTid`/`extractBid`/`extractResult`/`extractData`，泛型 `T` 由调用方通过 `Class<T>` 指定 |

**已知缺陷**：无（已修复 #1 MessageCodec snake_case、#3 DeviceCompatibility SMART_CONTROLLER_ENTERPRISE，见 §6.2 修复记录）。

### 4.3 protocol.topic — MQTT Topic 体系

**职责**：定义 DJI Cloud API 的 MQTT topic 通道、方向、模板与构造工具。

| 类 | 说明 |
|---|---|
| `TopicChannel` | 14 通道枚举，每个含 `suffix()`/`direction()`/`description()` |
| `TopicDirection` | `UP`（设备→云）/ `DOWN`（云→设备） |
| `TopicTemplate` | `thing/product/%s/{suffix}` 与 `sys/product/%s/{suffix}` 模板常量 |
| `TopicBuilder` | topic 构造工具，`build(sn,ch,useSysPrefix)` 选择前缀 |
| `TopicResolver` | topic 解析工具，`resolve(topic,method)` 返回 `TopicInfo` record（channel/SN/direction） |

**14 通道一览**：

| 通道 | suffix | 方向 | 用途 |
|---|---|---|---|
| OSD | `osd` | UP | 遥测周期推送 |
| STATE | `state` | UP | 状态变化推送 |
| SERVICES | `services` | DOWN | 服务调用 |
| SERVICES_REPLY | `services_reply` | UP | 服务回复 |
| EVENTS | `events` | UP | 事件推送 |
| EVENTS_REPLY | `events_reply` | DOWN | 事件回复 |
| REQUESTS | `requests` | UP | 设备请求 |
| REQUESTS_REPLY | `requests_reply` | DOWN | 请求回复 |
| STATUS | `status` | UP | 设备拓扑（机场用 sys/product） |
| STATUS_REPLY | `status_reply` | DOWN | 拓扑回复 |
| DRC_UP | `drc/up` | UP | DRC 上行 |
| DRC_DOWN | `drc/down` | DOWN | DRC 下行 |
| PROPERTY_SET | `property/set` | DOWN | 属性设置 |
| PROPERTY_SET_REPLY | `property/set_reply` | UP | 属性设置回复 |

### 4.4 protocol.method — Method 名称枚举

**职责**：定义各通道的 method 字符串常量，提供 `fromMethodName` 反查。

| 枚举 | 通道 | 数量 | 代表值 |
|---|---|---|---|
| `StatusMethod` | status | 1 | `update_topo` |
| `RequestsMethod` | requests | 7 | `config`/`airport_bind_status`/`storage_config_get`/`flighttask_progress_get`/`flight_areas_get` |
| `EventMethod` | events | 29 | `flighttask_progress`/`fly_to_point_progress`/`ota_progress`/`file_upload_callback`/`hms`/`device_exit_homing_notify`/`obstacle_avoidance_notify`/`joystick_invalid_notify`/`psdk_floating_window_text`/...（含 5 个原有 @Inferred + 1 个补全 @Inferred drc_status_notify） |
| `DrcMethod` | drc/down | 42 | `heart_beat`/`stick_control`/`drone_control`/`drc_force_landing`/`drc_ai_model_select`/`drc_live_lens_change`/`drc_initial_state_subscribe`/`drc_camera_iso_set`/...（19 simulator + 11 v1.16 AI + 1 Pilot + 11 DRC 状态/相机参数） |
| `DrcUpMethod` | drc/up | 12 | `osd_info_push`/`hsi_info_push`/`delay_info_push`/`drc_drone_state_push`/`drc_camera_state_push`/`drc_camera_osd_info_push`/`drc_psdk_state_info`/`drc_psdk_floating_window_text`/`drc_speaker_play_progress`/`drc_psdk_ui_resource`/`drc_camera_photo_info_push`/`drc_ai_info_push` |
| `ServiceMethod` | services | 97 | `flighttask_execute`/`live_start_push`/`drone_open`/`takeoff_to_point`/`ota_create`/`fileupload_start`/`in_flight_wayline_deliver`/...（71 catalog + 18 补全 + 8 回归补全 flysafe/Pilot） |

**统一接口**：所有 method 枚举提供 `methodName()`/`description()`/`fromMethodName(String) → Optional`。

### 4.5 protocol.envelope — 消息信封

**职责**：定义 DJI Cloud API 消息的通用信封结构。

| 类 | 用途 | data 类型 |
|---|---|---|
| `RequestEnvelope` | 请求消息（services/requests/property_set） | `Object`（由 method 决定） |
| `ReplyEnvelope` | 回复消息（services_reply/requests_reply/property_set_reply） | `ReplyData{result, output}` |
| `EventEnvelope` | 事件消息（events） | `Object`（由 method 决定） |

**信封通用字段**：`tid`（事务ID）/ `bid`（批次ID）/ `timestamp`（毫秒）/ `method` / `data`。

### 4.6 protocol.error — 错误码

**职责**：定义 DJI Cloud API 协议层错误码常量、错误码描述查表与逐设备错误结构。

| 类 | 说明 |
|---|---|
| `DjiErrorCode` | 233 个错误码常量（通用 2 + HTTP API 注册绑定 3 + MQTT services_reply 215 + 直播 13），按 13 个模块分组，提供 `describe(int)` 运行时查表。类级 `@Inferred` 标注 514xxx（仅范围无逐个清单）、4xxxx/6xxxx Pilot 错误码未收录 |
| `DjiErrorInfo` | record `{code, description}`，错误码描述条目，由 `describe()` 返回 |
| `ErrorInfo` | record `{sn, err_code}`，用于 `output.err_infos` 逐设备错误 |

**D2 查表策略**：`DjiErrorCode.describe(int code)` 返回 `Optional<DjiErrorInfo>`，调用方可运行时获取官方描述（如 `describe(314001)` 返回「飞行任务下发失败，请稍后重试」）。`CODE_TABLE` 用 `Map.ofEntries` 初始化，包含所有 233 个错误码。

### 4.7 model — 设备型号

**职责**：定义 DJI 设备的型号三元组（domain/type/subType）与兼容性矩阵。

| 类 | 说明 |
|---|---|
| `DeviceDomain` | domain 枚举：`AIRCRAFT(0)`/`CONTROLLER(2)`/`DOCK(3)` |
| `DeviceModel` | record：三元组 + `displayName`/`shortName`/`defaultSn` + `modelKey()`/`isDock()`/`isController()`/`isAircraft()` |
| `DeviceModelProvider` | 接口：`toModel() → DeviceModel` + default 委托方法（`domain()`/`type()`/`subType()`/`displayName()`/`shortName()`/`defaultSn()`/`modelKey()`/`isDock()`/`isController()`/`isAircraft()`） |
| `DroneModel` | 14 种飞行器 + `fromType(type, subType)` / `fromModelKey("0-67-0")` 反查 |
| `DockModel` | 3 种机场 + `fromType(type, subType)` / `fromModelKey("3-3-0")` 反查 |
| `RcModel` | 4 种遥控器 + `fromType(type, subType)` / `fromModelKey("2-119-0")` 反查 |
| `DeviceCompatibility` | 兼容矩阵：`isCompatible(DockModel, DroneModel)` + `isCompatible(RcModel, DroneModel)` |

**已知缺陷**：无（已修复 #3 switch 未覆盖 SMART_CONTROLLER_ENTERPRISE，见 §6.2 修复记录）。

### 4.8 telemetry — 遥测数据

**职责**：定义 DJI Cloud API 的 OSD/State 字段名枚举与遥测数据结构。

| 类 | 说明 |
|---|---|
| `OsdField` | 65 个 OSD 字段名（pushMode=0，周期推送），含 `fromFieldName` |
| `StateField` | 35 个 State 字段名（pushMode=1，变化推送），含 `fromFieldName` |
| `DroneOsd` | 飞行器 OSD record（33 组件，camelCase） |
| `DockOsd` | 机场 OSD record（37 组件，camelCase） |
| `RcOsd` | 遥控器 OSD record（5 组件：modeCode/latitude/longitude/battery/country） |

**设计要点**：
- OSD/State record 使用包装类型（Integer/Double/Long/Object）允许 `null`，因不同机型上报字段集不同
- 嵌套结构（battery/positionState/obstacleAvoidance 等）用 `Object` 表示，具体结构见 DJI 文档
- `height` 为绝对高度（椭球面），`elevation` 为相对起飞点高度

### 4.9 telemetry.enumtype — 遥测枚举类型

**职责**：定义 OSD/State 字段中使用的枚举值。

| 枚举 | 字段 | 值范围 | 数量 |
|---|---|---|---|
| `Gear` | `gear` | 0-9 | 10（A/P/NAV/FPV/FARM/S/F/M/G/T） |
| `DroneModeCode` | `mode_code`（飞行器） | 0-20 | 21 |
| `DockModeCode` | `mode_code`（机场） | 0-5 | 6 |
| `ModeCodeReason` | `mode_code_reason`（飞行器） | 0-23 | 24（无意义/低电量/低电压/严重低电压/RC按键/App请求/RC失联/外部触发/进入限飞区/距Home太近/距Home太远/航点起飞/Home上方继续降落/0.7m二段下降/突破限低/周边航班/高度控制失败/智能低电量/AP控制/硬件异常/防触地结束/返航取消/返航遇障/大风） |
| `BatteryStoreMode` | `battery_store_mode`（机场） | 1-2 | 2（计划模式/待命模式，注意无 0 值） |
| `PositionState` | `position_state.is_fixed` | 0-3 | 4 |
| `DroneChargeState` | `drone_charge_state.state` | 0-1 | 2 |

**统一接口**：所有枚举提供 `code()`/`description()`/`fromCode(int)`（抛 `IllegalArgumentException`）+ `BY_CODE` 不可变查找表。

**Jackson 绑定（仅 `BatteryStoreMode`）**：`BatteryStoreMode` 是首个用作 POJO 字段类型的遥测枚举，在 `code()` 上加 `@JsonValue`、在 `fromCode(int)` 上加 `@JsonCreator(mode = DELEGATING)`，实现 DJI 协议 int 值与枚举的双向绑定。`BatteryStoreModeSwitchRequest.mode` 字段因此可使用类型化枚举替代原始 `int`，编译期捕获非法值。其他枚举（如 `DroneModeCode`）仅作为 Javadoc 引用，因对应字段在 OSD record 中需用 `Integer` 兼容不同机型字段集差异。

### 4.10 flow — 注册/上线流程

**职责**：定义设备注册与上线的流程步骤序列和报文构造。

| 类 | 说明 |
|---|---|
| `RegistrationStep` | record：`methodName`/`description`/`channelType`/`timeoutSeconds`/`retryCount`/`retryIntervalSeconds`，含 `ChannelType` 枚举（REQUESTS/REQUESTS_REPLY/STATUS） |
| `DockRegistrationFlow` | 机场上云 5 步：config → airport_bind_status → airport_organization_get → airport_organization_bind → update_topo |
| `PilotRegistrationFlow` | Pilot 上云 5 步：与机场一致，但 update_topo 用 `thing/product` 前缀（机场用 `sys/product`） |

**设计要点**：
- 流程步骤以 `public static final RegistrationStep` 常量定义，`steps()` 返回不可变列表
- 超时/重试参数为模拟器实现策略，非 DJI 协议规定
- update_topo 报文构造由调用方负责，SDK 仅提供 `UpdateTopoData` record + `StatusMethod.UPDATE_TOPO` 枚举

---

## 5. 协议集成接口

### 5.1 MQTT Topic 体系

DJI Cloud API 基于 MQTT 实现，topic 形如 `{prefix}/product/{sn}/{suffix}`：

```
thing/product/{sn}/osd              ← 飞行器/机场遥测上行
thing/product/{sn}/state            ← 状态变化上行
thing/product/{sn}/services         ← 服务调用下行
thing/product/{sn}/services_reply   ← 服务回复上行
thing/product/{sn}/events           ← 事件上行
thing/product/{sn}/events_reply     ← 事件回复下行
thing/product/{sn}/requests         ← 设备请求上行
thing/product/{sn}/requests_reply   ← 请求回复下行
thing/product/{sn}/drc/up           ← DRC 上行
thing/product/{sn}/drc/down         ← DRC 下行
thing/product/{sn}/property/set     ← 属性设置下行
thing/product/{sn}/property/set_reply ← 属性设置回复上行
sys/product/{sn}/status             ← 设备拓扑上行（机场上云）
sys/product/{sn}/status_reply       ← 拓扑回复下行
```

**前缀规则**：`status`/`status_reply` 在机场上云用 `sys/product`，在 Pilot 上云用 `thing/product`，其余通道统一 `thing/product`。

### 5.2 消息信封结构

**请求/事件消息**：
```json
{
  "tid": "uuid",
  "bid": "uuid",
  "timestamp": 1700000000000,
  "method": "flighttask_execute",
  "data": { ... }
}
```

**回复消息**：
```json
{
  "tid": "uuid",
  "bid": "uuid",
  "timestamp": 1700000000000,
  "method": "flighttask_execute",
  "data": {
    "result": 0,
    "output": { ... }
  }
}
```

### 5.3 设备注册流程

**5 步序列**（机场上云与 Pilot 上云一致）：

| 步骤 | method | 通道 | 超时 | 重试 | 说明 |
|---|---|---|---|---|---|
| 1 | `config` | requests | 3s | 3 次/3s | 获取 License 校验参数 |
| 2 | `airport_bind_status` | requests | 3s | 0 | 查询设备绑定状态 |
| 3 | `airport_organization_get` | requests | 3s | 0 | 查询组织信息（校验绑定码） |
| 4 | `airport_organization_bind` | requests | 3s | 0 | 绑定到组织 |
| 5 | `update_topo` | status | 3s | 0 | 上线通知（机场 sys/product，Pilot thing/product） |

---

## 6. 约束与已知问题

### 6.1 硬约束

| 约束 | 来源 | 说明 |
|---|---|---|
| 不确定内容不改代码 | 项目记忆 | 发现不确定项时记录之，不修改代码 |
| 文档变更需跟踪对比 | 项目记忆 | DJI 文档更新时需与代码实现对比，按严重度标记差异 |

### 6.2 已知缺陷

| # | 位置 | 问题 | 严重度 | 状态 |
|---|---|---|---|---|
| 1 | `MessageCodec` | `ObjectMapper` 未配 `PropertyNamingStrategy.SNAKE_CASE`，snake_case JSON → camelCase record 反序列化字段全 null | 关键 | ✅ 已修复（2026-08-14）：配置 `PropertyNamingStrategies.SNAKE_CASE`，序列化输出 snake_case 符合 DJI 协议 |
| 3 | `DeviceCompatibility` | `isCompatible(RcModel, DroneModel)` switch 未覆盖 `SMART_CONTROLLER_ENTERPRISE` | 重要 | ✅ 已修复（2026-08-14）：补 `case SMART_CONTROLLER_ENTERPRISE -> Set.of(M300_RTK)`，依据 RcModel Javadoc「搭配 Matrice 300 RTK」 |

### 6.3 待验证项（`@Inferred`）

| 项 | 推断理由 | 验证点 |
|---|---|---|
| `DrcMethod.HEART_BEAT` | 文档未明确协议格式与发起方 | 真机 heart_beat 的发起方与回包格式 |
| `ServiceMethod.CLOUD_CONTROL_AUTH_REQUEST` | 授权值 `["flight"]` 未在文档明确 | 真机授权成功后的实际取值 |
| `PilotRegistrationFlow.UPDATE_TOPO` | RC Plus 2 子设备不上报 domain/index 未核实 | 真机抓包确认 sub_devices 字段 |

---

## 7. 技术栈与依赖

### 7.1 技术栈

| 技术 | 版本 | 用途 |
|---|---|---|
| Java | 21 | record / sealed / switch expression / text block |
| Jackson | 2.17.0 | JSON 编解码（`jackson-databind` + `jackson-datatype-jsr310`） |
| JUnit 5 | 5.10.2 | 测试框架（test scope） |
| Maven | — | 构建工具 |

### 7.2 依赖原则

- **无 Spring 依赖** — SDK 是纯协议定义层，不引入框架
- **仅 compile 依赖 Jackson** — JSON 编解码的唯一运行时依赖
- **test scope 依赖 JUnit 5** — 后续新增 AssertJ（见 TDD 设计文档）

### 7.3 构建配置

```xml
<properties>
    <maven.compiler.release>21</maven.compiler.release>
    <jackson.version>2.17.0</jackson.version>
    <junit.version>5.10.2</junit.version>
</properties>
```

版本号统一在 `<properties>` 管理，符合"无硬编码值"偏好。

---

## 8. 文档索引

| 文档 | 路径 | 说明 |
|---|---|---|
| 架构设计（本文档） | `docs/architecture-design.md` | 项目整体架构与设计决策 |
| TDD 测试套件设计 | `docs/superpowers/specs/2026-08-14-tdd-test-suite-design.md` | 测试架构、策略与缺陷标记 |
| TDD 测试用例文档 | `docs/tdd-test-cases.md` | 39 个测试类的详细用例（spec 24 + characterization 15） |
| DJI 文档对比报告 | `dji-cloud-api-doc-comparison/dji-cloud-api-doc-comparison.html` | 官方文档 v1.8.0→v1.16.1 与 SDK 代码的差异分析 |
