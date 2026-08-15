// Copyright (C) 2026 CDMI.LTD
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package ltd.cdmi.dji.cloudapi.sdk.protocol.method;

import java.util.Optional;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API {@code events} 通道的 method 名称枚举。
 *
 * <p>{@code events} 通道用于设备事件上行（设备→云，云通过 {@code events_reply} 回复）。
 * 消息含 {@code need_reply} 字段：1 表示需平台回复，0 表示单向通知。
 *
 * <p>本枚举覆盖 27 个 events 专属 method（不含与 services 同名的进度上报 method）：
 * <ul>
 *   <li><b>simulator 已对接验证 22 个（@Verified）</b>：航线/飞行/相机/升级/日志/媒体/飞行区/授权/AirSense/HMS/喇叭/PSDK/ESDK 等</li>
 *   <li><b>DJI 文档列出待验证 4 个（@Inferred）</b>：return_home_info/flighttask_ready/in_flight_wayline_progress/poi_circle_status</li>
 *   <li><b>补全 8 个（simulator 已对接 7 个 + Abandoned 1 个）</b>：device_exit_homing_notify/obstacle_avoidance_notify/joystick_invalid_notify/psdk_floating_window_text/psdk_ui_resource_upload_result/custom_data_transmission_from_psdk/custom_data_transmission_from_esdk/drc_status_notify(Abandoned)</li>
 * </ul>
 *
 * <p><b>services 指令进度上报</b>：DJI 协议中，services 指令（如 drone_open/cover_open）的
 * 异步进度上报使用与指令同名的 method 发送到 events 通道。这些同名 method 不在本枚举重复定义，
 * 参见 {@link ServiceMethod}。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html">
 * DJI Dock 上云 events 通道</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator FlightAreaSimulator/MediaUploadSimulator/FlightCommandSimulator/OtaSimulator/RemoteLogSimulator/AuthFlowHandler/AirSenseSimulator/HmsSimulator/PsdkSimulator/EsdkSimulator 已对接 hivemind 验证 22 个 method（15 原有 + 7 补全）")
@Inferred(
        reason = "4 个原有 method（return_home_info/flighttask_ready/in_flight_wayline_progress/poi_circle_status）DJI 文档列出但 simulator 未实现验证；1 个补全 method（drc_status_notify）DJI 英文文档标注 Abandoned，协议不再维护，推荐使用 drc_state 设备属性或 DRC-heart beat",
        verifyPoint = "4 个 @Inferred method 待真机验证或 simulator 补全后改标 @Verified；drc_status_notify 若真机仍上报需补全 POJO（data: result + drc_state，DrcState 枚举已存在）"
)
public enum EventMethod {

    // ===== 航线任务相关 =====

    /** 航线任务进度上报（need_reply=1，data 含 ext/status/trigger_time/flight_id） */
    FLIGHTTASK_PROGRESS("flighttask_progress", "航线任务进度上报"),

    /** 任务就绪通知（need_reply=0，data 含 flight_ids 数组） */
    @Inferred(
            reason = "DJI 文档列出但 simulator 未实现验证",
            verifyPoint = "待真机验证或 simulator 补全后改标 @Verified"
    )
    FLIGHTTASK_READY("flighttask_ready", "任务就绪通知"),

    /** 空中下发航线状态上报（need_reply=1，in_flight_wayline_progress） */
    @Inferred(
            reason = "DJI 文档列出但 simulator 未实现验证",
            verifyPoint = "待真机验证或 simulator 补全后改标 @Verified"
    )
    IN_FLIGHT_WAYLINE_PROGRESS("in_flight_wayline_progress", "空中下发航线状态上报"),

    // ===== 飞行控制相关 =====

    /** flyto 执行结果通知（need_reply=1，data 含 fly_to_id/status/remaining_distance） */
    FLY_TO_POINT_PROGRESS("fly_to_point_progress", "flyto 执行结果通知"),

    /** 一键起飞结果通知（need_reply=1，data 含 flight_id/status/track_id） */
    TAKEOFF_TO_POINT_PROGRESS("takeoff_to_point_progress", "一键起飞结果通知"),

    /** 返航信息（need_reply=0，data 含 planned_path_points/last_point_type/flight_id） */
    @Inferred(
            reason = "DJI 文档列出但 simulator 未实现验证",
            verifyPoint = "待真机验证或 simulator 补全后改标 @Verified"
    )
    RETURN_HOME_INFO("return_home_info", "返航信息"),

    /** POI 环绕状态信息通知（need_reply=1，data 含 status/reason/circle_radius/circle_speed） */
    @Inferred(
            reason = "DJI 文档列出但 simulator 未实现验证",
            verifyPoint = "待真机验证或 simulator 补全后改标 @Verified"
    )
    POI_CIRCLE_STATUS("poi_circle_status", "POI 环绕状态信息通知"),

    // ===== 相机/拍照相关 =====

    /** 拍照进度上报（need_reply=1，data 含 status/current_step/percent/camera_mode） */
    CAMERA_PHOTO_TAKE_PROGRESS("camera_photo_take_progress", "拍照进度上报"),

    // ===== 系统/升级/日志相关 =====

    /** 固件升级进度上报（need_reply=1，data 含 status/current_step/percent） */
    OTA_PROGRESS("ota_progress", "固件升级进度上报"),

    /** 日志文件上传进度（need_reply=1，data 含 status/percent） */
    FILEUPLOAD_PROGRESS("fileupload_progress", "日志文件上传进度"),

    // ===== 媒体管理相关 =====

    /** 媒体上传优先级上报（need_reply=1，航线任务完成后触发，等待 events_reply） */
    HIGHEST_PRIORITY_UPLOAD_FLIGHTTASK_MEDIA("highest_priority_upload_flighttask_media", "媒体上传优先级上报"),

    /** 文件上传结果回调（need_reply=1，逐个文件上传完成后触发，等待 events_reply） */
    FILE_UPLOAD_CALLBACK("file_upload_callback", "文件上传结果回调"),

    // ===== 自定义飞行区相关 =====

    /** 飞行器位置告警推送（need_reply=0，单向通知，data 含 drone_locations 数组） */
    FLIGHT_AREAS_DRONE_LOCATION("flight_areas_drone_location", "飞行器位置告警推送"),

    /** 文件同步进度上报（need_reply=1，data 含 status/reason/file） */
    FLIGHT_AREAS_SYNC_PROGRESS("flight_areas_sync_progress", "文件同步进度上报"),

    // ===== 授权/AirSense/HMS 相关 =====

    /** 请求授权结果通知（need_reply=0，data 含 result/output.status） */
    CLOUD_CONTROL_AUTH_NOTIFY("cloud_control_auth_notify", "请求授权结果通知"),

    /** AirSense 告警（need_reply=0，data 含 icao/latitude/longitude/altitude/heading） */
    AIRSENSE_WARNING("airsense_warning", "AirSense 告警"),

    /** HMS 告警（need_reply=0，data 含 level/code/module/key 等） */
    HMS("hms", "HMS 告警"),

    // ===== 喇叭/音频相关（PSDK） =====

    /** TTS 播放进度（need_reply=1，data 含 result/output.status/percent） */
    SPEAKER_TTS_PLAY_START_PROGRESS("speaker_tts_play_start_progress", "TTS 播放进度"),

    /** 音频播放进度（need_reply=1，data 含 result/output.status/percent） */
    SPEAKER_AUDIO_PLAY_START_PROGRESS("speaker_audio_play_start_progress", "音频播放进度"),

    // ===== 补全：返航/避障/摇杆/PSDK/ESDK events（8 个，simulator 已对接 7 个 + 已废弃 1 个）=====

    /** 设备返航退出状态通知（need_reply=1，三 Dock 通用，data 含 sn/action/reason；reason 类型 @Inferred） */
    DEVICE_EXIT_HOMING_NOTIFY("device_exit_homing_notify", "设备返航退出状态通知"),

    /** 避障记录上报（need_reply=1，仅 Dock3，data 含 wayline_uuid/flight_id/obstacles/is_final_report） */
    OBSTACLE_AVOIDANCE_NOTIFY("obstacle_avoidance_notify", "避障记录上报"),

    /** 飞行控制无效原因通知（need_reply=1，三 Dock 共有，data 含 reason 枚举 0-4） */
    JOYSTICK_INVALID_NOTIFY("joystick_invalid_notify", "飞行控制无效原因通知"),

    /** PSDK 浮窗文本推送（need_reply=0，data 直接平铺 psdk_index + value） */
    PSDK_FLOATING_WINDOW_TEXT("psdk_floating_window_text", "PSDK 浮窗文本推送"),

    /** PSDK UI 资源包上传结果（need_reply=0，data 直接平铺 psdk_index/object_key/size/result） */
    PSDK_UI_RESOURCE_UPLOAD_RESULT("psdk_ui_resource_upload_result", "PSDK UI 资源包上传结果"),

    /** PSDK 自定义消息接收（need_reply=0 @Inferred，data 含 value 文本 < 256 字节） */
    CUSTOM_DATA_TRANSMISSION_FROM_PSDK("custom_data_transmission_from_psdk", "PSDK 自定义消息接收"),

    /** ESDK 自定义消息接收（need_reply=0 @Inferred，data 含 value 文本 < 256 字节） */
    CUSTOM_DATA_TRANSMISSION_FROM_ESDK("custom_data_transmission_from_esdk", "ESDK 自定义消息接收"),

    /** DRC 链路状态通知（Abandoned，DJI 英文文档标注协议不再维护，推荐使用 drc_state 设备属性或 DRC-heart beat） */
    @Inferred(
        reason = "drc_status_notify DJI 英文文档标注 Abandoned，协议不再维护；中文文档未标注废弃但英文版明确说明。data 结构已有定义（result + drc_state），但按废弃处理不创建 POJO",
        verifyPoint = "若真机仍上报此事件，可按 DJI 文档 data 结构（result:int + drc_state:enum_int{0,1,2}）补全 POJO，DrcState 枚举已存在可复用"
    )
    DRC_STATUS_NOTIFY("drc_status_notify", "DRC 链路状态通知（Abandoned）");

    private final String methodName;
    private final String description;

    EventMethod(String methodName, String description) {
        this.methodName = methodName;
        this.description = description;
    }

    /** method 字符串值，如 "flight_areas_drone_location" */
    public String methodName() {
        return methodName;
    }

    /** 中文描述 */
    public String description() {
        return description;
    }

    /**
     * 按 method 字符串反查枚举。
     *
     * <p>method 名随固件升级可能扩展，未知 method 是正常情况，故返回 {@link Optional}
     * 而非抛异常（差异化错误处理策略见 SDK design doc §3.5）。
     *
     * @param methodName method 字符串值
     * @return 匹配的枚举；未匹配返回 {@link Optional#empty()}
     */
    public static Optional<EventMethod> fromMethodName(String methodName) {
        if (methodName == null || methodName.isBlank()) {
            return Optional.empty();
        }
        for (EventMethod m : values()) {
            if (m.methodName.equals(methodName)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }
}
