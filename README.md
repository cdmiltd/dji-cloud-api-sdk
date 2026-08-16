# DJI Cloud API SDK

## 简介

DJI Cloud API SDK 是 DJI 上云协议的 Java 类型安全 POJO 库，覆盖两个独立场景：机场上云（Dock ↔ 平台，MQTT 5 通道 services/drc/events/requests/status）与 Pilot 上云（Pilot ↔ 平台，HTTP + WebSocket），共 188 个方法、250 个 POJO。

**为什么用这个 SDK？**

- **编译期类型安全**——DJI JSON 字段名映射为 Java record 字段，拼写错误编译即报，不再运行时静默失败
- **协议知识在 JAR 不在人脑**——188 个方法全部编码为类型安全的 record，IDE 自动补全即可发现全部能力，无需逐字阅读 DJI 文档
- **专人跟踪协议变更**——DJI 更新协议时由 SDK 维护者同步更新，消费方升级 JAR 即获取最新定义，无需自己盯文档
- **100% 接口覆盖不随人员流动退化**——核心成员离职不会导致代码无人敢改、平台兼容性永久降级；协议知识固化在代码中，团队能力不依赖个人记忆
- **协议权威解释，持续验证完善**——字段必填/选填、类型、回复结构等歧义由 SDK 一锤定音，消除团队内解读争议；当前已通过 simulator 对接 hivemind 平台验证（`@Verified` 标注），剩余推断项（`@Inferred`）标注待真机抓包确认，详见下方「验证状态说明」

作为模拟器与 hivemind 平台的共享"单一真相源"，本 SDK 消除两个系统间协议定义的重复维护，升级一处即双方同步。

## 版本说明

本 SDK 跟随大疆官网 Cloud API 协议版本，当前最新协议版本为 **1.16.1**。

版本号格式：`1.16.1.X`

- 前三段（`1.16.1`）对齐 DJI Cloud API 协议版本，协议升级时同步递增
- 末段（`X`）为 SDK 问题修复版本号，协议不变情况下随 SDK 缺陷修复递增

## 特性

- **全通道覆盖**：services(69) / drc(19) / events(20) / requests(7) / status(1) = 116 个 method POJO
- **类型安全**：每个 method 有对应的 Request/Reply/Data record，字段 camelCase 自动映射 DJI snake_case JSON
- **文档追踪**：`@DocUrl` 标注 DJI 文档链接，`@Verified`/`@Inferred` 标注验证状态
- **零 MQTT 耦合**：纯 POJO 定义层，不依赖 MQTT 客户端
- **Jackson SNAKE_CASE**：`MessageCodec` 统一配置，snake_case ↔ camelCase 双向映射

## 快速开始

### Maven 依赖

自 v1.16.1.0 起拆分为两个模块，按需引入：

**仅需协议定义**（services/drc/events/requests/status 指令 POJO + 遥测 + 设备型号）：

```xml
<dependency>
    <groupId>ltd.cdmi</groupId>
    <artifactId>dji-cloud-api-sdk</artifactId>
    <version>1.16.1.0</version>
</dependency>
```

**需要航线文件生成/解析**（WPML 模板/Builder/Codec，额外引入）：

```xml
<dependency>
    <groupId>ltd.cdmi</groupId>
    <artifactId>dji-cloud-api-sdk</artifactId>
    <version>1.16.1.0</version>
</dependency>
<dependency>
    <groupId>ltd.cdmi</groupId>
    <artifactId>dji-cloud-api-sdk-wayline</artifactId>
    <version>1.16.1.0</version>
</dependency>
```

### 直接下载 JAR

非 Maven 项目可直接下载预构建 JAR（包含源码包）：

| 平台 | 下载地址 |
|---|---|
| GitHub | [releases/latest](https://github.com/cdmiltd/dji-cloud-api-sdk/releases/latest) |
| Gitee | [releases](https://gitee.com/alpeai/dji-cloud-api-sdk/releases) |

> JAR 文件名格式：`dji-cloud-api-sdk-{version}.jar`（协议定义层）、`dji-cloud-api-sdk-wayline-{version}.jar`（航线工具层）

### 通用调用模式

三个信封类提供对称入口，分别对应三类协议通道，调用方式完全一致（先提取消息类型 → switch 路由 → parse 反序列化为类型安全 POJO）：

| 协议通道 | 提取消息类型 | 反序列化入口 | 适用场景 |
|---|---|---|---|
| MQTT | `DjiMessage.extractMethod(payload)` | `DjiMessage.parse(payload, Class)` | 机场上云（services/drc/events/requests/status 5 通道） |
| WebSocket | `WsPushMessage.extractBizCode(payload)` | `WsPushMessage.parse(payload, Class)` | Pilot 上云推送（平台 → Pilot） |
| HTTP | — | `HttpResponseEnvelope.parse(body, Class)` | Pilot 上云请求（Pilot → 平台） |

**机场上云（MQTT）示例**：5 个 MQTT 通道信封结构一致 `{method, tid, bid?, data}`，用 `switch + parse` 模式，每个 case 1 行 `parse`，其余是类型安全业务代码：

```java
String method = DjiMessage.extractMethod(payload);
switch (method) {
    case "fly_to_point" -> {
        var msg = DjiMessage.parse(payload, FlyToPointRequest.class);
        msg.data().flyToId();          // 编译期类型安全，无需 cast
        String reply = MessageCodec.toJson(new NoOutputReply());
        sendReply(msg.tid(), reply);   // 发到 thing/product/{sn}/services_reply
    }
    default -> log.warn("未处理: {}", method);
}
```

**事件通道**同样使用 `parse`，回复用 `events_reply`：

```java
var msg = DjiMessage.parse(payload, FlighttaskProgressData.class);
msg.data().output().status();          // 编译期类型安全
sendEventReply(msg.tid(), 0);           // tid 与原始 event 一致
```

## 场景一：机场上云（Dock ↔ 平台，MQTT 5 通道）

机场上云场景中，Dock 机场通过 MQTT 与云平台交互，覆盖 services/drc/events/requests/status 全部 5 个通道。SDK 不绑定 MQTT 客户端实现，调用方自行接入任意 MQTT 客户端，按 `extractMethod + parse` 模式处理消息。以下按 DJI Cloud API 业务功能划分，列出每个功能涉及的 SDK 类与调用方式，全部基于 MQTT 协议。

### 1. 设备注册与上线

**通道**：requests（注册四步）+ status（上线拓扑）

```java
// 注册流程：config → airport_bind_status → airport_organization_get → airport_organization_bind
case "config" -> {
    var msg = DjiMessage.parse(payload, ConfigRequest.class);
    msg.data().appId();
    var reply = new ConfigReply(0, msg.data().appId(), "mqtt-host", 2, ...);
    sendReply(msg.tid(), MessageCodec.toJson(reply));
}
case "airport_bind_status" -> {
    var msg = DjiMessage.parse(payload, AirportBindStatusRequest.class);
    sendReply(msg.tid(), MessageCodec.toJson(new AirportBindStatusReply(0, 1)));
}
case "airport_organization_get" -> {
    var msg = DjiMessage.parse(payload, AirportOrganizationGetRequest.class);
    sendReply(msg.tid(), MessageCodec.toJson(new AirportOrganizationGetReply(0, ...)));
}
case "airport_organization_bind" -> {
    var msg = DjiMessage.parse(payload, AirportOrganizationBindRequest.class);
    sendReply(msg.tid(), MessageCodec.toJson(new AirportOrganizationBindReply(0)));
}

// 上线拓扑：设备主动上报 update_topo（status 通道）
var topo = DjiMessage.parse(payload, UpdateTopoData.class);
topo.data().subDevice();  // 子设备列表（含 domain/type/sub_type）
```

**涉及 SDK 类**：`ConfigRequest/Reply`、`AirportBindStatusRequest/Reply`、`AirportOrganizationGetRequest/Reply`、`AirportOrganizationBindRequest/Reply`、`UpdateTopoData`、`UpdateTopoReplyData`

### 2. 航线任务

**通道**：services（下发）+ events（进度）+ requests（查询）

```java
case "flighttask_prepare" -> {
    var msg = DjiMessage.parse(payload, FlighttaskPrepareRequest.class);
    msg.data().flightId();              // 航线 ID
    msg.data().executableConditions();   // 执行条件
    sendReply(msg.tid(), MessageCodec.toJson(new FlighttaskPrepareReply(0, "output-url")));
}
case "flighttask_execute" -> {
    var msg = DjiMessage.parse(payload, FlighttaskExecuteRequest.class);
    sendReply(msg.tid(), MessageCodec.toJson(new FlighttaskExecuteReply(0)));
}
case "flighttask_stop" -> {
    var msg = DjiMessage.parse(payload, FlighttaskStopRequest.class);
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}
case "flighttask_undo" -> {
    var msg = DjiMessage.parse(payload, FlighttaskUndoRequest.class);
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}

// 航线进度事件（events 通道）
case "flighttask_progress" -> {
    var msg = DjiMessage.parse(payload, FlighttaskProgressData.class);
    msg.data().output().status();       // executing / success / failed
    msg.data().ext().currentWaypointIndex();
    sendEventReply(msg.tid(), 0);
}
case "flighttask_ready" -> {
    var msg = DjiMessage.parse(payload, FlighttaskReadyData.class);
    sendEventReply(msg.tid(), 0);
}

// 查询进度（requests 通道）
case "flighttask_progress_get" -> {
    var msg = DjiMessage.parse(payload, FlighttaskProgressGetRequest.class);
    sendReply(msg.tid(), MessageCodec.toJson(new FlighttaskProgressGetReply(0, ...)));
}
```

**涉及 SDK 类**：`FlighttaskPrepareRequest/Reply`、`FlighttaskExecuteRequest/Reply`、`FlighttaskUndoRequest`、`FlighttaskStopRequest`、`ReturnSpecificHomeRequest`、`FlighttaskProgressData`、`FlighttaskReadyData`、`InFlightWaylineProgressData`、`ReturnHomeInfoData`、`FlighttaskProgressGetRequest/Reply`

### 3. 指令飞行

**通道**：services + events

```java
case "fly_to_point" -> {
    var msg = DjiMessage.parse(payload, FlyToPointRequest.class);
    msg.data().flyToId();
    msg.data().points().get(0).height();   // DJI 协议用 points 数组
    msg.data().maxSpeed();
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
    // 异步：延迟发 fly_to_point_progress 事件
}
case "fly_to_point_update" -> {
    var msg = DjiMessage.parse(payload, FlyToPointUpdateRequest.class);
    msg.data().maxSpeed();              // 更新最大速度
    msg.data().points().get(0).latitude();  // 更新目标点纬度
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}
case "takeoff_to_point" -> {
    var msg = DjiMessage.parse(payload, TakeoffToPointRequest.class);
    // services_reply 仅含 result=0，无 output 字段
    // track_id 是设备内部状态（simulator 生成），不下发回平台
    sendReply(msg.tid(), MessageCodec.toJson(new TakeoffToPointReply()));
}
case "flight_authority_grab" -> {
    var msg = DjiMessage.parse(payload, PayloadAuthorityGrabRequest.class);
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}

// 飞行进度事件
case "fly_to_point_progress" -> {
    var msg = DjiMessage.parse(payload, FlyToPointProgressData.class);
    msg.data().status();                // in_progress / success / failed
    sendEventReply(msg.tid(), 0);
}
case "takeoff_to_point_progress" -> {
    var msg = DjiMessage.parse(payload, TakeoffToPointProgressData.class);
    msg.data().status();
    sendEventReply(msg.tid(), 0);
}
```

**涉及 SDK 类**：`FlyToPointRequest`、`FlyToPointUpdateRequest`、`TakeoffToPointRequest/Reply`、`PayloadAuthorityGrabRequest`、`FlyToPointProgressData`、`TakeoffToPointProgressData`、`CameraPhotoTakeProgressData`

### 4. 远程控制（DRC 通道）

**通道**：services（进入 DRC）+ drc（摇杆/降落/相机/灯/喊话器）

```java
// 进入 DRC 模式（services 通道）
case "drc_mode_enter" -> {
    var msg = DjiMessage.parse(payload, DrcModeEnterRequest.class);
    // DJI 协议：mqtt_broker 是平台下发给设备的 DRC 专用连接信息（Request 字段）
    // services_reply output 仅含 result=0，无 output 字段
    // 设备解析 Request 中的 mqtt_broker 后建立专用连接，不通过 Reply 回传 broker
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}

// DRC 通道消息（topic: thing/product/{sn}/drc/up）
// 注意：DRC 回复格式与 services_reply 不同，data 直接是 {result}
case "stick_control" -> {
    var msg = DjiMessage.parse(payload, StickControlRequest.class);
    msg.data().roll();      // 横滚
    msg.data().pitch();     // 俯仰
    msg.data().throttle();  // 油门
    msg.data().yaw();       // 偏航
    // 无回包（stick_control 不需要回复）
}
case "heart_beat" -> {
    var msg = DjiMessage.parse(payload, HeartBeatRequest.class);
    msg.data().timestamp();
    // 回包：HeartBeatReply（回显 timestamp）
    sendDrcReply(MessageCodec.toJson(new HeartBeatReply(msg.data().timestamp())));
}
case "drc_force_landing" -> {
    // 15 个安全类 DRC 指令使用通用 DrcResultReply
    sendDrcReply(MessageCodec.toJson(new DrcResultReply(0)));
}
```

**涉及 SDK 类**：`DrcModeEnterRequest`、`DrcMqttBroker`、`StickControlRequest`、`DroneControlRequest`、`HeartBeatRequest/Reply`、`DrcResultReply`（15 个安全类指令共用）、DRC 相机/灯/喊话器 POJO（见下方）

### 5. 相机与负载管理

**通道**：services（21 个专用 POJO + 无参方法用 NoParameterRequest）

```java
case "camera_photo_take" -> {
    var msg = DjiMessage.parse(payload, CameraPhotoTakeRequest.class);
    msg.data().payloadIndex();          // 负载索引
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}
case "camera_aim" -> {
    var msg = DjiMessage.parse(payload, CameraAimRequest.class);
    msg.data().pitch();                // 云台俯仰角
    msg.data().yaw();                  // 云台偏航角
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}
case "camera_focal_length_set" -> {
    var msg = DjiMessage.parse(payload, CameraFocalLengthSetRequest.class);
    msg.data().focalLength();
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}
case "camera_exposure_mode_set" -> {
    var msg = DjiMessage.parse(payload, CameraExposureModeSetRequest.class);
    msg.data().exposureMode();
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}
// cover_open / cover_close / putter_open / putter_close 等无参数方法：
case "cover_open" -> {
    var msg = DjiMessage.parse(payload, NoParameterRequest.class);
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}
```

**涉及 SDK 类**（21 个专用）：`CameraPhotoTakeRequest`、`CameraPhotoStopRequest`、`CameraAimRequest`、`CameraFocalLengthSetRequest`、`CameraExposureModeSetRequest`、`CameraExposureSetRequest`、`CameraFocusModeSetRequest`、`CameraFocusValueSetRequest`、`CameraFrameZoomRequest`、`CameraLookAtRequest`、`CameraModeSwitchRequest`、`CameraPointFocusActionRequest`、`CameraRecordingStartRequest`、`CameraRecordingStopRequest`、`CameraScreenDragRequest`、`CameraScreenSplitRequest`、`GimbalResetRequest`、`IrMeteringAreaSetRequest`、`IrMeteringModeSetRequest`、`IrMeteringPointSetRequest`、`PhotoStorageSetRequest`、`VideoStorageSetRequest`

### 6. 直播管理

**通道**：services

```java
case "live_start_push" -> {
    var msg = DjiMessage.parse(payload, LiveStartPushRequest.class);
    msg.data().url();                  // RTMP/RTSP 推流地址
    msg.data().videoIndex();           // 视频流索引
    sendReply(msg.tid(), MessageCodec.toJson(new LiveStartPushReply(0)));
}
case "live_stop_push" -> {
    var msg = DjiMessage.parse(payload, LiveStopPushRequest.class);
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}
case "live_set_quality" -> {
    var msg = DjiMessage.parse(payload, LiveSetQualityRequest.class);
    msg.data().quality();              // 0=smooth, 1=SD, 2=HD, 3=superHD
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}
case "live_lens_change" -> {
    var msg = DjiMessage.parse(payload, LiveLensChangeRequest.class);
    msg.data().lens();                 // 镜头类型
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}
case "live_camera_change" -> {
    var msg = DjiMessage.parse(payload, LiveCameraChangeRequest.class);
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}
```

**涉及 SDK 类**：`LiveStartPushRequest/Reply`、`LiveStopPushRequest`、`LiveSetQualityRequest`、`LiveLensChangeRequest`、`LiveCameraChangeRequest`

### 7. 媒体管理

**通道**：services（优先级）+ requests（STS 凭证）+ events（上传回调）

```java
// 调整媒体上传优先级（services 通道）
case "upload_flighttask_media_prioritize" -> {
    var msg = DjiMessage.parse(payload, UploadFlighttaskMediaPrioritizeRequest.class);
    msg.data().flightId();             // 优先上传的飞行任务 ID
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}

// 获取 STS 临时凭证（requests 通道）
case "storage_config_get" -> {
    var msg = DjiMessage.parse(payload, StorageConfigGetRequest.class);
    var reply = new StorageConfigGetReply(0, "bucket", "endpoint", "object-key-prefix/", credentials);
    sendReply(msg.tid(), MessageCodec.toJson(reply));
}

// 媒体上传回调（events 通道）
case "file_upload_callback" -> {
    var msg = DjiMessage.parse(payload, FileUploadCallbackData.class);
    msg.data().file().objectKey();     // S3 对象键
    sendEventReply(msg.tid(), 0);
}
case "highest_priority_upload_flighttask_media" -> {
    var msg = DjiMessage.parse(payload, HighestPriorityUploadFlighttaskMediaData.class);
    msg.data().flightId();
    sendEventReply(msg.tid(), 0);
}
```

**涉及 SDK 类**：`UploadFlighttaskMediaPrioritizeRequest`、`StorageConfigGetRequest/Reply`、`FileUploadCallbackData`、`HighestPriorityUploadFlighttaskMediaData`

### 8. 自定义飞行区

**通道**：requests（查询）+ events（位置/进度）

```java
case "flight_areas_get" -> {
    var msg = DjiMessage.parse(payload, FlightAreasGetRequest.class);
    sendReply(msg.tid(), MessageCodec.toJson(new FlightAreasGetReply(0, List.of())));
}
case "flight_areas_drone_location" -> {
    var msg = DjiMessage.parse(payload, FlightAreasDroneLocationData.class);
    msg.data().areaId();               // 命中的飞行区 ID
    sendEventReply(msg.tid(), 0);
}
case "flight_areas_sync_progress" -> {
    var msg = DjiMessage.parse(payload, FlightAreasSyncProgressData.class);
    msg.data().progress();
    sendEventReply(msg.tid(), 0);
}
```

**涉及 SDK 类**：`FlightAreasGetRequest/Reply`、`FlightAreasDroneLocationData`、`FlightAreasSyncProgressData`

### 9. 设备状态与遥测

**通道**：status（OSD 定时上报）+ state（属性变化上报）

```java
// 机场 OSD（topic: thing/product/{sn}/osd）
DockOsd dockOsd = MessageCodec.fromJson(payload, DockOsd.class);
dockOsd.modeCode();      // 0=待机, 1=作业中
dockOsd.networkState();   // 网络状态

// 飞行器 OSD
DroneOsd droneOsd = MessageCodec.fromJson(payload, DroneOsd.class);
droneOsd.modeCode();      // 0=待机, 9=返航, 10=降落
droneOsd.latitude();      // 纬度
droneOsd.height();        // 高度（相对起飞点）
```

**涉及 SDK 类**：`DockOsd`、`DroneOsd`、`State`（telemetry 包，不在 command 包中）

### 10. HMS 告警

**通道**：events

```java
case "hms" -> {
    var msg = DjiMessage.parse(payload, HmsData.class);
    msg.data().list().forEach(hms -> {
        hms.code();        // HMS 错误码（参见 DjiErrorCode）
        hms.level();       // 1=warning, 2=error, 3=critical
        hms.module();      // 模块标识
    });
    sendEventReply(msg.tid(), 0);
}

// AirSense 避障告警（data 是数组，SDK 自动反序列化）
case "airsense_warning" -> {
    var msg = DjiMessage.parse(payload, AirSenseWarningData.class);
    msg.data().alerts().forEach(alert -> {
        alert.icaoAddress();  // ADS-B 目标地址
        alert.relativeLatitude();
    });
    sendEventReply(msg.tid(), 0);
}
```

**涉及 SDK 类**：`HmsData`、`AirSenseWarningData`（+ `AirSenseWarningDataDeserializer`）、`CloudControlAuthNotifyData`、`DjiErrorCode`（错误码查询）

### 11. OTA 升级与日志管理

**通道**：events（进度）+ services（无参方法）

```java
case "ota_progress" -> {
    var msg = DjiMessage.parse(payload, OtaProgressData.class);
    msg.data().progress();            // 0-100
    sendEventReply(msg.tid(), 0);
}
case "fileupload_progress" -> {
    var msg = DjiMessage.parse(payload, FileUploadProgressData.class);
    msg.data().progress();
    sendEventReply(msg.tid(), 0);
}
case "ota_create" -> {
    var msg = DjiMessage.parse(payload, NoParameterRequest.class);
    sendReply(msg.tid(), MessageCodec.toJson(new NoOutputReply()));
}
```

**涉及 SDK 类**：`OtaProgressData`、`FileUploadProgressData`、`NoParameterRequest`（无参方法共用）

### 12. 喊话器与补光灯（DRC 子类）

**通道**：drc

```java
// 喊话器
case "drc_speaker_tts_set" -> {
    var msg = DjiMessage.parse(payload, DrcSpeakerTtsSetRequest.class);
    msg.data().text();               // 播报文本
    sendDrcReply(MessageCodec.toJson(new DrcResultReply(0)));
}
case "drc_speaker_play_volume_set" -> {
    var msg = DjiMessage.parse(payload, DrcSpeakerPlayVolumeSetRequest.class);
    msg.data().volume();
    sendDrcReply(MessageCodec.toJson(new DrcResultReply(0)));
}

// 补光灯
case "drc_light_brightness_set" -> {
    var msg = DjiMessage.parse(payload, DrcLightBrightnessSetRequest.class);
    msg.data().brightness();
    sendDrcReply(MessageCodec.toJson(new DrcResultReply(0)));
}
case "drc_light_mode_set" -> {
    var msg = DjiMessage.parse(payload, DrcLightModeSetRequest.class);
    msg.data().mode();
    sendDrcReply(MessageCodec.toJson(new DrcResultReply(0)));
}

// 喊话器播放进度（events 通道）
case "speaker_tts_play_start_progress" -> {
    var msg = DjiMessage.parse(payload, SpeakerTtsPlayStartProgressData.class);
    msg.data().output().status();
    sendEventReply(msg.tid(), 0);
}
```

**涉及 SDK 类**：喊话器（`DrcSpeakerTtsSetRequest`、`DrcSpeakerPlayModeSetRequest`、`DrcSpeakerPlayVolumeSetRequest`、`DrcSpeakerPlayStopRequest`、`DrcSpeakerReplayRequest`）、补光灯（`DrcLightBrightnessSetRequest`、`DrcLightCalibrationRequest`、`DrcLightFineTuningSetRequest`、`DrcLightModeSetRequest`）、事件（`SpeakerTtsPlayStartProgressData`、`SpeakerAudioPlayStartProgressData`、`SpeakerOutput`）

## 场景二：Pilot 上云（Pilot ↔ 平台，HTTP + WebSocket）

Pilot 上云与机场上云是两个独立场景：机场通过 MQTT 5 通道与平台交互（见上方「场景一」），Pilot 遥控器通过 HTTP 调用平台 API、通过 WebSocket 接收平台推送。SDK 对 Pilot 上云提供路径常量（`HttpApiPath`）、HTTP 响应信封（`HttpResponseEnvelope<T>`）、STS 凭证（`StsCredentials`）、推送 biz_code 枚举（`WsBizCode`）和推送信封（`WsPushMessage<T>`），不绑定具体 HTTP/WS 客户端实现。本场景内部按协议通道分为 HTTP 调用与 WebSocket 推送两节。

### HTTP 调用（Pilot → 平台）

用 `HttpApiPath` 常量拼接路径（替换 `{workspace_id}` / `{wayline_id}` 等占位符），用任意 HTTP 客户端发请求。

```java
// 航线管理：获取航线文件下载地址
String path = HttpApiPath.WAYLINE_URL
        .replace("{workspace_id}", workspaceId)
        .replace("{wayline_id}", waylineId);
HttpResponse resp = httpClient.get(server + path);
```

**STS 凭证**：上传媒体/航线文件前，先调 `HttpApiPath.STS` 获取临时凭证，再用凭证向对象存储上传文件。DJI HTTP API 响应统一信封为 `{"code":0,"message":"...","data":{...}}`，用 `HttpResponseEnvelope.parse` 一步完成信封解析 + `data` 反序列化为类型安全 POJO——与 MQTT 通道的 `parse` 和 WebSocket 通道的 `parse` 对称。**不要**用 `fromJson` 直接反序列化响应体（业务数据在 `data` 内层，`fromJson` 会把 `code`/`message`/`data` 当顶层字段，导致 POJO 字段全 null）。

```java
String path = HttpApiPath.STS.replace("{workspace_id}", workspaceId);
HttpResponse resp = httpClient.post(server + path, body);
var envelope = HttpResponseEnvelope.parse(resp.body(), StsCredentials.class);
if (envelope.code() == 0) {
    envelope.data().bucket();           // 对象存储桶名
    envelope.data().objectKeyPrefix();  // 上传 key 前缀（拼到文件名前）
    envelope.data().credentials();     // 临时凭证子结构（按服务商 SDK 解析，SDK 不固化字段名）
} else {
    log.error("STS 获取失败: {} - {}", envelope.code(), envelope.message());
}
```

### WebSocket 推送（平台 → Pilot）

用 `WsPushMessage.extractBizCode` 提取消息类型，再按 `WsBizCode` 路由，每个 case 调 `WsPushMessage.parse` 一步完成信封解析 + `data` 反序列化为类型安全 POJO——与 MQTT 通道的 `extractMethod` + `parse` 模式完全对称。

```java
void onWsMessage(String payload) {
    String bizCode = WsPushMessage.extractBizCode(payload);
    switch (WsBizCode.fromCode(bizCode).orElse(null)) {
        case DEVICE_OSD -> {
            var msg = WsPushMessage.parse(payload, DeviceOsdPushData.class);
            msg.data().host().latitude();   // 类型安全，无 cast，无 instanceof
        }
        case DEVICE_ONLINE, DEVICE_OFFLINE, DEVICE_UPDATE_TOPO -> {
            var msg = WsPushMessage.parse(payload, WsEmptyData.class);
            // 触发 HTTP 拓扑刷新
            String path = HttpApiPath.DEVICES_TOPOLOGIES.replace("{workspace_id}", workspaceId);
            httpClient.get(server + path);
        }
        case MAP_ELEMENT_CREATE, MAP_ELEMENT_UPDATE, MAP_ELEMENT_DELETE -> {
            var msg = WsPushMessage.parse(payload, MapElementPushData.class);
            // msg.data() 类型安全访问地图元素
        }
        case MAP_GROUP_REFRESH -> {
            var msg = WsPushMessage.parse(payload, MapGroupRefreshData.class);
            // msg.data().ids() → 按 group_id 调 HTTP 拉取元素列表
        }
        case null -> log.warn("未知 biz_code: {}", bizCode);
    }
}
```

> SDK 为高频 biz_code 提供类型安全 POJO（`websocket.data` 包）：`DeviceOsdPushData`、`MapElementPushData`、`MapGroupRefreshData`、`WsEmptyData`。调用方也可传自定义 POJO，由 Jackson SNAKE_CASE 策略自动映射。

### 涉及 SDK 类

`HttpApiPath`（5 业务域 14 个端点）、`HttpResponseEnvelope<T>`、`StsCredentials`、`WsBizCode`（8 个：地图元素 4 + 态势感知 4）、`WsPushMessage<T>`、`HttpResponseEnvelope.parse`/`WsPushMessage.parse`/`WsPushMessage.extractBizCode`、推送 data POJO（`DeviceOsdPushData`、`MapElementPushData`、`MapGroupRefreshData`、`WsEmptyData`）

### HTTP 端点按业务域

| 业务域 | 路径前缀 | 端点常量 |
|---|---|---|
| 设备拓扑 | `/manage/api/v1/workspaces` | `DEVICES_TOPOLOGIES` |
| 地图元素 | `/map/api/v1/workspaces` | `ELEMENT_GROUPS`、`CREATE_ELEMENT`、`UPDATE_ELEMENT`、`DELETE_ELEMENT` |
| 媒体管理 | `/media/api/v1/workspaces` | `FAST_UPLOAD`、`TINY_FINGERPRINTS`、`MEDIA_UPLOAD_CALLBACK`、`GROUP_UPLOAD_CALLBACK` |
| 存储服务 | `/storage/api/v1/workspaces` | `STS` |
| 航线管理 | `/wayline/api/v1/workspaces` | `WAYLINES`、`WAYLINE_URL`、`WAYLINE_DUPLICATE_NAMES`、`WAYLINE_UPLOAD_CALLBACK`、`ADD_FAVORITES`、`REMOVE_FAVORITES` |

## 航线模板（WPML 生成与解析）

WPML（Waypoint Mission Language）是 DJI 机场/Pilot 使用的航线文件格式，本质是一组 XML 文件打包成的 `.kmz`（ZIP）。本模块是**本地文件工具**，不涉及 MQTT/HTTP/WS 协议，生成的 `.kmz` 可通过「场景一 → 2. 航线任务」上传到云平台再推送给机场执行。

### 文件结构

```
*.kmz
└── wpmz
    ├── template.kml    # 模板文件（人读/可编辑，由本 SDK 生成）
    └── waylines.wpml   # 执行文件（机读，由 template.kml 派生）
```

- `template.kml`：包含航点/测区/动作配置，可被 DJI Pilot 2 打开编辑
- `waylines.wpml`：包含展开后的航点执行参数，飞机直接执行；可由 SDK 根据机型从 `template.kml` 派生，也可由 DJI Cloud/Pilot 导入 KMZ 后自动计算

### 四种模板

| 模板类 | templateType | 测区/航点 | 典型场景 |
|---|---|---|---|
| `WaypointTemplate` | `waypoint` | 航点列表 | 航点飞行（巡检/拍照/录像） |
| `Mapping2dTemplate` | `mapping2d` | `Polygon` 测区 | 二维正射建图 |
| `Mapping3dTemplate` | `mapping3d` | `Polygon` 测区 | 三维倾斜摄影（五航线） |
| `MappingStripTemplate` | `mappingStrip` | `LineString` 航带 | 航带飞行（带状测区） |

四个模板均提供 `toXml()`（生成 `template.kml`）、`toWpml()`（派生 `waylines.wpml`）、`toKmz()`（打包 KMZ）三种输出方式。**高度语义**：所有 `height` 参数均为相对起飞点高度，与 DJI WPML `wpml:height` 定义一致。

### 航点飞行模板

```java
String kml = WaypointTemplate.builder()
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
    .gimbalPitchMode(GimbalPitchMode.USE_POINT_SETTING)
    .globalHeight(100)
    .globalWaypointHeadingMode(WaypointHeadingMode.FOLLOW_WAYLINE)
    .globalWaypointHeadingPathMode(WaypointHeadingPathMode.CLOCKWISE)
    .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
    .globalUseStraightLine(0)
    .addWaypoint(w -> w.longitude(113.98057).latitude(22.987663).height(100)
        .addActionGroup(ag -> ag
            .actionGroupId(0)
            .actionGroupStartIndex(0)
            .actionGroupEndIndex(0)
            .actionTriggerType(ActionTriggerType.REACH_POINT)
            .addAction(a -> a.actionId(0)
                .actionActuatorFunc(ActionActuatorFunc.TAKE_PHOTO)
                .actionActuatorFuncParam(new TakePhotoParam(0, "point1", "wide", 1)))))
    .addWaypoint(w -> w.longitude(113.99000).latitude(22.987663).height(100))
    .toXml();

// 派生 waylines.wpml（需 droneInfo/payloadInfo，且所有航点 useGlobalXxx=1）
String wpml = WaypointTemplate.builder()
    /* ... 同上 ... */
    .toWpml();

// 打包为 .kmz（等价于 WpmlCodec.toKmz(kml, wpml)）
byte[] kmz = WaypointTemplate.builder()
    /* ... 同上 ... */
    .toKmz();
```

> `toWpml()` 当前要求所有航点 `useGlobalSpeed/useGlobalHeadingParam/useGlobalTurnParam=1`（即使用全局参数），若存在航点级参数覆盖会抛 `UnsupportedOperationException`。

### 建图航拍模板

三个 Mapping 模板的 `Placemark` 描述的是**测区/航带**而非航点列表，实际的航点序列由 DJI Cloud/Pilot 导入 KMZ 后根据测区参数（重叠率、航向、高度等）自动计算。因此 `toWpml()` 生成的 `waylines.wpml` 中 `ExecuteFolder.placemarks` 为空，是一个等待 DJI Pilot 填充的「骨架」。

```java
// 二维正射建图
String kml = Mapping2dTemplate.builder()
    .author("John").createTime(System.currentTimeMillis())
    .flyToWaylineMode(FlyToWaylineMode.SAFELY)
    .finishAction(FinishAction.GO_HOME)
    .exitOnRCLost(ExitOnRCLost.GO_CONTINUE)
    .executeRCLostAction(ExecuteRCLostAction.HOVER)
    .takeOffSecurityHeight(20).globalTransitionalSpeed(8).globalRTHHeight(100)
    .droneInfo(67, 0).payloadInfo(52, 0)
    .templateId(0)
    .coordinateMode(CoordinateMode.WGS84).heightMode(HeightMode.EGM96)
    .autoFlightSpeed(7).globalShootHeight(100)
    .positioningType(PositioningType.GPS)
    .payloadParam(0, "wide,ir")             // payloadPositionIndex, imageFormat
    .shootType(ShootType.TIME).direction(0)
    .overlap(new Overlap(null, null, 80, 70, null, null, null, null))
    .height(100)
    .polygon("113.98057,22.987663,0 113.990000,22.987663,0 113.990000,22.977663,0 113.98057,22.977663,0")
    .mappingHeadingParam(MappingHeadingMode.FOLLOW_WAYLINE, 0)
    .toXml();
```

- `Mapping3dTemplate`：在 `Mapping2d` 基础上增加 `inclinedGimbalPitch`（倾斜云台俯仰角）和 `inclinedFlightSpeed`（倾斜航线速度），自动生成 5 条航线（1 正射 + 4 倾斜）
- `MappingStripTemplate`：用 `lineString(...)` 替代 `polygon(...)` 描述带状测区，支持 `cuttingDistance`/`leftExtend`/`rightExtend`/`singleLineEnable` 等航带专属参数

### 动作（Action）

`WaypointTemplate.addWaypoint(w -> w.addActionGroup(...))` 内可挂载动作组，动作组通过 `ActionActuatorFunc` 指定执行功能。SDK 内置 16 种动作，每种动作对应一个 `ActionActuatorFuncParam` 子类：

| ActionActuatorFunc | 参数 POJO | 说明 |
|---|---|---|
| `TAKE_PHOTO` | `TakePhotoParam` | 单拍 |
| `START_RECORD` / `STOP_RECORD` | `StartRecordParam` / `StopRecordParam` | 录像开始/结束 |
| `FOCUS` / `ZOOM` | `FocusParam` / `ZoomParam` | 对焦/变焦 |
| `CUSTOM_DIR_NAME` | `CustomDirNameParam` | 创建新文件夹 |
| `GIMBAL_ROTATE` / `GIMBAL_EVENLY_ROTATE` | `GimbalRotateParam` / `GimbalEvenlyRotateParam` | 旋转云台/航段均匀转动 |
| `ROTATE_YAW` / `HOVER` | `RotateYawParam` / `HoverParam` | 飞行器偏航/悬停 |
| `ORIENTED_SHOOT` / `ACCURATE_SHOOT` | `OrientedShootParam` / `AccurateShootParam` | 定向拍照/精准复拍 |
| `PANO_SHOT` / `RECORD_POINT_CLOUD` | `PanoShotParam` / `RecordPointCloudParam` | 全景拍照/点云录制 |
| `MEGAPHONE` / `SEARCHLIGHT` | `MegaphoneParam` / `SearchlightParam` | 喊话器/探照灯（M4D/M4TD） |

### KMZ 打包与解析

`WpmlCodec` 提供 XML 序列化、KMZ 打包/解包、POJO 反序列化三类静态方法：

```java
// 1. 打包：XML 字符串 → KMZ 字节流
byte[] kmz = WpmlCodec.toKmz(kml, wpml);

// 2. 解包：KMZ 字节流 → 原始 XML 字符串（KmzContent record）
KmzContent content = WpmlCodec.fromKmz(kmz);
String kml = content.templateKml();
String wpml = content.waylinesWpml();

// 3. 解析：XML 字符串 → POJO（支持 16 种动作的多态反序列化）
Kml<Folder> template = WpmlCodec.parseTemplateKml(kml);
Kml<ExecuteFolder> waylines = WpmlCodec.parseWaylinesWpml(wpml);

// 4. 一站式：KMZ 字节流 → POJO 容器（ParsedKmz record）
ParsedKmz parsed = WpmlCodec.parseKmz(kmz);
Kml<Folder> t = parsed.template();
Kml<ExecuteFolder> w = parsed.waylines();
```

> POJO 均为 `record`（不可变）。编辑策略：读取字段值 → 新建 Builder 重建对象，不提供 `toBuilder()`。

### 涉及 SDK 类

- 模板入口：`WaypointTemplate`、`Mapping2dTemplate`、`Mapping3dTemplate`、`MappingStripTemplate`
- Builder 链：`WaypointBuilder`、`ActionGroupBuilder`、`ActionBuilder`、`PayloadParamBuilder`
- 编解码：`WpmlCodec`（静态工具类）、`WpmlStreamWriter`/`WpmlOutputFactory`（命名空间处理）
- 动作参数：`ActionActuatorFunc`（枚举）、`ActionActuatorFuncParam`（密封接口）及 16 个实现类（`model/action/` 包）
- 模型：`Kml<T>`、`Document<T>`、`Folder`、`Placemark`、`MissionConfig`、`DroneInfo`、`PayloadInfo`、`PayloadParam` 等（`model/` 包）
- 执行模型：`ExecuteFolder`、`ExecutePlacemark`、`WaypointHeadingParam`、`WaypointTurnParam`、`ExecuteHeightMode`（`model/execute/` 包）
- 解析容器：`KmzContent`、`ParsedKmz`
- 枚举：`enumtype/` 包下共 22 个枚举（`HeightMode`、`ActionActuatorFunc`、`FinishAction` 等），均实现 `WpmlEnum` 接口

## 真机消息采集

`CaptureRecorder` 是一个通用的真机消息采集工具，按网关+飞行器+方向+方法自动分类存储真机收发的 MQTT 消息。**主要用于开发者自身调试**——记录真机实际发送的请求和回复，排查协议对接问题。如果愿意将采集到的数据提交到 GitHub Issue，可顺带帮助 SDK 维护者将 `@Inferred` 项验证为 `@Verified`，但这完全可选。

### 开启采集

```java
// 方式 1：代码直接开启
CaptureRecorder.enable(CaptureConfig.defaults());
CaptureRecorder.registerDevice("7UUXN1Q00A008W", DockModel.DOCK3, DroneModel.M4D);

// 方式 2：通过系统属性由调用方自行开启（SDK 不自动扫描）
// 启动时调用方自行检查：
if ("true".equalsIgnoreCase(System.getProperty("dji.cloud.capture"))) {
    CaptureRecorder.enable(CaptureConfig.defaults());
}
```

### 在消息处理点插入采集调用

```java
// 接收真机消息时
void onMessage(String topic, String payload) {
    CaptureRecorder.capture(topic, "inbound", payload);  // 一行采集
    // ... 正常处理 ...
}

// 发送回复时
void onReply(String topic, String replyJson) {
    CaptureRecorder.capture(topic, "outbound", replyJson);
    // ... 正常发送 ...
}
```

未启用时 `capture()` 立即返回，零开销。

### 采集文件自动分类

#### 目录结构

```
dji-capture/                                           ← 采集根目录（CaptureConfig.captureDir）
├── Dock3-M4D/                                         ← 第一级：网关型号-飞行器型号
│   ├── inbound/                                       ← 第二级：方向
│   │   ├── fly_to_point_20260815T103000_001.json      ← 第三级：采集文件
│   │   ├── cover_open_20260815T103002_002.json
│   │   └── flighttask_progress_20260815T103005_005.json
│   └── outbound/
│       ├── fly_to_point_reply_20260815T103001_003.json
│       └── cover_open_reply_20260815T103003_004.json
├── Dock2-M3D/
│   ├── inbound/
│   │   └── ...
│   └── outbound/
│       └── ...
├── Dock1-M30T/
│   └── ...
└── SN-7UUXN1Q00A008W/                                 ← 未注册设备：用 SN- 前缀兜底
    └── inbound/
        └── ...
```

**各级目录含义**：

| 级别 | 目录名 | 来源 | 示例 |
|---|---|---|---|
| 第一级 | `{Gateway}-{Aircraft}` | `registerDevice()` 注册的网关+飞行器简称 | `Dock3-M4D` |
| 第一级（未注册） | `SN-{SN}` | 从 topic 提取的 SN，未注册时兜底 | `SN-7UUXN1Q00A008W` |
| 第一级（无 SN） | `unknown` | topic 中提取不到 SN 时兜底 | `unknown` |
| 第二级 | `inbound` / `outbound` | `capture()` 调用时传入的 `direction` 参数 | `inbound` |
| 第三级 | 文件 | 自动命名（见下方） | `fly_to_point_20260815T103000_001.json` |

### 文件命名规则

```
{method}_{timestamp}_{sequence}.json
```

| 组成 | 格式 | 说明 |
|---|---|---|
| `{method}` | DJI 方法名 | 从 payload 的 `method` 字段提取，如 `fly_to_point`、`cover_open` |
| `{timestamp}` | `yyyyMMdd'T'HHmmss` | 采集时刻，如 `20260815T103000` 表示 2026-08-15 10:30:00 |
| `{sequence}` | 3 位序号 | 全局递增，防止同一秒多文件冲突，如 `001`、`002` |

### 文件内容结构

每个采集文件是一个 JSON 文档，由 `_capture` 元数据头 + 原始消息字段组成：

```json
{
  "_capture": {                           ← 采集元数据（SDK 自动添加）
    "timestamp": "2026-08-15T10:30:00",  ← ISO 8601 采集时间
    "topic": "thing/product/7UUXN1Q00A008W/services",  ← MQTT topic（含 SN）
    "direction": "inbound",              ← 方向：inbound（真机→平台）/ outbound（平台→真机）
    "gateway": "Dock3",                  ← 网关型号
    "aircraft": "M4D",                   ← 飞行器型号
    "method": "fly_to_point"             ← DJI 方法名
  },
  "tid": "t1",                           ← 以下是原始消息字段（已脱敏）
  "method": "fly_to_point",
  "data": {
    "fly_to_id": "FT001",
    "target_height": 50.0,
    "sn": "***"                           ← 敏感字段自动替换为 ***
  }
}
```

**`_capture` 元数据字段**：

| 字段 | 说明 |
|---|---|
| `timestamp` | 采集时刻（ISO 8601），与文件名中的 timestamp 一致但精度更高 |
| `topic` | 原始 MQTT topic，可用于追溯设备 SN 和消息通道 |
| `direction` | `inbound` = 真机发给平台（验证 Request/Data POJO），`outbound` = 平台发给真机（验证 Reply POJO） |
| `gateway` | 网关型号简称，来自 `registerDevice()` 注册 |
| `aircraft` | 飞行器型号简称，来自 `registerDevice()` 注册 |
| `method` | DJI 方法名，与原始消息的 `method` 字段一致 |

**脱敏字段**：默认对以下字段递归替换为 `"***"`（包括嵌套在 `data` 内部的）：

| 类别 | 字段名 |
|---|---|
| 设备标识 | `sn` |
| 认证凭据 | `app_license`, `app_id`, `app_key` |
| STS 凭证 | `access_key_id`, `secret_access_key`, `security_token` |
| 其他 | `client_token`, `nonce`, `signature` |

可通过 `CaptureConfig` 自定义脱敏字段集。

### 去重机制

每个「网关+飞行器+方向+方法」组合最多采集 `maxSamplesPerMethod` 份（默认 5 份）。同一组合的第 6 次及后续消息不再采集，避免高频消息导致文件爆炸。

### 提交验证数据（可选）

如果采集到的真机数据对您自己的调试有帮助，它对 SDK 维护者同样有价值。将 `dji-capture/` 目录打包提交到 GitHub Issue 并注明机型组合，SDK 维护者可据此将 `@Inferred` 转为 `@Verified`。这完全自愿，不影响采集器的任何功能。

## 诊断工具

SDK 提供以下诊断类，供调用者在运行时排查协议对接问题、校验设备配置、追溯协议来源。

### 错误码查表：`DjiErrorCode`

DJI 协议错误码为 6 位数字（如 `314001`），裸数字无法理解含义。`DjiErrorCode` 收录全部 DJI 官方错误码文档，提供运行时查表：

```java
// services_reply 返回 result=314001，查表获取官方描述
var msg = DjiMessage.parse(payload, FlighttaskPrepareReply.class);
int result = msg.data().result();

DjiErrorCode.describe(result).ifPresentOrElse(
    info -> log.error("任务失败: {} ({})", info.description(), info.code()),
    () -> log.error("未知错误码: {}", result)
);
// 输出: 任务失败: 飞行任务下发失败，请稍后重试 (314001)
```

**诊断意义**：将裸数字错误码翻译为人类可读描述，避免查文档。未知码返回 `Optional.empty()`，可识别 SDK 未收录的新错误码。

也可直接使用静态常量做判断：

```java
if (result == DjiErrorCode.DEVICE_FIRMWARE_UPDATING) {
    log.warn("设备升级中，请勿重复操作");
}
```

### Topic 路由解析：`TopicResolver`

从 MQTT topic 字符串中解析出设备 SN、通道类型、消息方向，用于日志分类、消息路由、问题定位：

```java
void onMessage(String topic, String payload) {
    String method = DjiMessage.extractMethod(payload);
    TopicResolver.TopicInfo info = TopicResolver.resolve(topic, method);

    log.info("[{}] {} {} → {}",
        info.direction(),   // DOWN（云→设备）/ UP（设备→云）
        info.channel(),     // SERVICES / EVENTS / DRC_UP / ...
        info.deviceSn(),    // 7UUXN1Q00A008W
        info.method());     // fly_to_point

    // 按通道分发处理
    switch (info.channel()) {
        case SERVICES -> handleServices(info.deviceSn(), info.method(), payload);
        case EVENTS   -> handleEvents(info.deviceSn(), info.method(), payload);
        case DRC_UP   -> handleDrcUp(info.deviceSn(), info.method(), payload);
        case null     -> log.warn("未知通道: {}", topic);
    }
}
```

**诊断意义**：无需手写字符串分割逻辑，一行解析出路由四要素（通道/SN/method/方向）。无法识别的通道返回 `null`，可用于检测异常 topic。

### 设备兼容性校验：`DeviceCompatibility`

在配置设备组合前校验机场-飞行器或遥控器-飞行器是否兼容，避免下发不支持的指令组合：

```java
// 校验 Dock3 + M4D 是否兼容（true）
boolean ok = DeviceCompatibility.isCompatible(DockModel.DOCK3, DroneModel.M4D);

// 校验 Dock1 + M4D 是否兼容（false，Dock1 不支持 M4D）
boolean ok = DeviceCompatibility.isCompatible(DockModel.DOCK1, DroneModel.M4D);
if (!ok) {
    log.error("Dock1 不支持 M4D，请更换为 M30/M30T");
}

// 校验遥控器与飞行器
boolean ok = DeviceCompatibility.isCompatible(RcModel.RC_PLUS_2, DroneModel.M4E);
```

**诊断意义**：在设备注册或任务下发前提前拦截不兼容组合，避免运行时协议错误。兼容矩阵源自 DJI 官方产品支持文档。

### 协议溯源：`@Verified` / `@Inferred` / `@DocUrl` 注解

三个注解均为 `RUNTIME` 保留策略，可通过反射在运行时检查任何 POJO 或枚举的验证状态和文档来源：

```java
// 检查某个 POJO 的验证状态
var clazz = FlighttaskPrepareRequest.class;
boolean verified = clazz.isAnnotationPresent(Verified.class);
boolean inferred = clazz.isAnnotationPresent(Inferred.class);

if (verified) {
    String basis = clazz.getAnnotation(Verified.class).basis();
    log.info("{} 已验证: {}", clazz.getSimpleName(), basis);
}
if (inferred) {
    String reason = clazz.getAnnotation(Inferred.class).reason();
    String verifyPoint = clazz.getAnnotation(Inferred.class).verifyPoint();
    log.warn("{} 推断项: {} (待验证: {})", clazz.getSimpleName(), reason, verifyPoint);
}

// 获取 DJI 文档链接
if (clazz.isAnnotationPresent(DocUrl.class)) {
    String url = clazz.getAnnotation(DocUrl.class).value();
    log.info("文档: {}", url);
}
```

**诊断意义**：调用方可构建启动时诊断报告，列出所有 `@Inferred` 项及其待验证点，提示运维关注未真机验证的协议部分。也可在遇到异常时快速跳转到 DJI 官方文档原文。

```java
// 启动时扫描所有 @Inferred 项，输出诊断报告
Reflections reflections = new Reflections("ltd.cdmi.dji.cloudapi.sdk");
Set<Class<?>> inferredClasses = reflections.getTypesAnnotatedWith(Inferred.class);
inferredClasses.forEach(clazz -> {
    var ann = clazz.getAnnotation(Inferred.class);
    System.out.printf("[INFERRED] %s: %s%n", clazz.getSimpleName(), ann.reason());
});
```

### 信封类诊断能力

三个信封类除了类型安全解析，还提供协议级诊断方法：

| 方法 | 用途 | 诊断场景 |
|---|---|---|
| `DjiMessage.extractMethod(payload)` | 从 MQTT 消息提取 method 名 | 路由分发前预判消息类型 |
| `WsPushMessage.extractBizCode(payload)` | 从 WebSocket 消息提取 biz_code | WS 推送路由分发 |
| `HttpResponseEnvelope.parse(body, class)` | 解析 HTTP 响应信封 + data | 排查 HTTP API code≠0 错误 |
| `WsPushMessage.parse(payload, class)` | 解析 WS 推送信封 + data | 排查 WS 推送解析失败 |

```java
// HTTP API 返回错误时，一步提取 code + message
var envelope = HttpResponseEnvelope.parse(resp.body(), StsCredentials.class);
if (envelope.code() != 0) {
    log.error("HTTP API 错误: code={}, message={}", envelope.code(), envelope.message());
    // envelope.data() 此时为 null（DJI 信封 code≠0 时不携带 data）
}
```

## 模块概览

| 模块 | 职责 | 核心类 |
|---|---|---|
| `protocol/` | 协议层：envelope/topic/method/error | `RequestEnvelope`/`ReplyEnvelope`/`EventEnvelope`, `TopicBuilder`, `TopicResolver`, `ServiceMethod`/`EventMethod`/`DrcMethod`, `DjiErrorCode` |
| `command/` | 指令 POJO：5 通道 116 方法 | `service/`(69), `drc/`(19), `event/`(20), `request/`(7), `status/`(1) |
| `codec/` | JSON 编解码 + 类型安全信封解析 | `MessageCodec`, `DjiMessage` |
| `model/` | 设备型号/兼容性 | `DeviceModel`, `RcModel`, `DeviceCompatibility` |
| `telemetry/` | OSD/State 遥测 | `DockOsd`, `DroneOsd`, `State` |
| `flow/` | 业务流程 | `DockRegistrationFlow`, `PilotRegistrationFlow` |
| `annotation/` | 文档追踪注解 | `@DocUrl`, `@Verified`, `@Inferred` |
| `http/` | HTTP 路径常量+响应信封+STS | `HttpApiPath`, `HttpResponseEnvelope<T>`, `StsCredentials` |
| `websocket/` | WebSocket biz_code+推送 | `WsBizCode`, `WsPushMessage<T>` |
| `websocket/data/` | 推送 data POJO | `DeviceOsdPushData`, `MapElementPushData`, `MapGroupRefreshData`, `WsEmptyData` |
| `capture/` | 真机采集验证 | `CaptureRecorder`, `CaptureConfig` |

## 验证状态说明

- **`@Verified`**：simulator 已对接 hivemind 验证
- **`@Inferred`**：基于 DJI 文档推断或 simulator 行为推断，待真机验证
- 详见各 POJO 文件注解

当前 `@Inferred` 待验证项（共 20+ 处，下列为关键设计决策项，完整清单见各源文件 `@Inferred` 注解）：

**关键设计决策（3 处）**：
- `DrcMethod.HEART_BEAT` — heart_beat 协议格式与发起方未在文档明确
- `ServiceMethod.CLOUD_CONTROL_AUTH_REQUEST` — 授权值 `["flight"]` 未在文档明确
- `PilotRegistrationFlow.UPDATE_TOPO` — RC Plus 2 子设备不上报 domain/index 字段未核实

**POJO 字段级推断（17+ 处，散落于各 record）**：
- `StsCredentials` — credentials 子结构字段名未在 DJI 文档直接核实
- `UpdateTopoReplyData` — sub_type 字段推断
- `DrcMethod`（类级）+ `EventMethod`（5 个 method）+ `ServiceMethod`（类级）— 部分方法枚举值归类待文档核实
- `DjiErrorCode`（类级）— 514xxx/4xxxx/6xxxx 错误码未完整收录
- `NoOutputReply` — drone_open output 结构推断
- `ReturnHomeInfoData` — multiDockHomeInfo 蛙跳结构推断
- `PoiCircleStatusData` — 方法名待文档验证
- `PhotoProgressData` — 最小骨架结构推断
- `SimulateMission` — altitude 字段单位语义
- `DrcSpeakerTtsSetRequest` — text 字段是否必填待确认
- `FlighttaskPrepareRequest`（类级 + 字段级 wayline_precision_type）— 部分字段待文档核实
- `LeafNode`、`WirelessLinkTopo` — 拓扑子结构推断
- `HeartBeatRequest`/`HeartBeatReply` — DRC 心跳字段结构推断
- `DroneControlRequest` — 已废弃指令，字段约束放宽
- `FlighttaskProgressGetReply` — output 结构待文档验证
- `AirportOrganizationGetReply` — output 字段结构推断
- `ConfigReply` — config_type/config_scope 结构推断

**完整 `@Inferred` 清单可通过 `grep -rn "@Inferred" src/main/java` 获取**。

## 文档索引

- [架构设计文档](docs/architecture-design.md) — 模块设计、技术选型、目录结构
- [Command POJO 设计](docs/superpowers/specs/2026-08-14-command-pojo-design.md) — POJO 组织策略、错误处理
- [DJI Cloud API 官方文档](https://developer.dji.com/doc/cloud-api-tutorial/cn/) — 协议真相源

## 交流沟通

<p>
  <img src="assets/friendCode.png" width="200" alt="微信二维码" />
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="assets/group.png" width="200" alt="技术交流群" />
</p>

- **左侧**：扫码添加好友
- **右侧**：扫码加入技术交流群

## License

Apache License 2.0 (详见 [LICENSE](LICENSE))
