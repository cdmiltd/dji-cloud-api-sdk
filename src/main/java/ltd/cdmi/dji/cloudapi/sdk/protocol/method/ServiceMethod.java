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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API {@code services} 通道的 method 名称枚举。
 *
 * <p>{@code services} 通道用于云对设备的服务调用（云→设备，设备通过 {@code services_reply} 回复）。
 * 这是 DJI Dock 上云的主要控制通道，覆盖航线任务、直播、相机、机场硬件控制等。
 *
 * <p>本枚举按 simulator {@code dji-method-catalog.json} services 已盘点的 89 个 method（去重后）：
 * <ul>
 *   <li><b>三 Dock 共有（62 个）</b>：航线任务/返航/直播/DRC/飞行控制/相机/云台/红外/舱盖/电源/系统等</li>
 *   <li><b>Dock1 独有（3 个）</b>：{@link #FLIGHT_SETUP_ABORT} / {@link #PUTTER_OPEN} / {@link #PUTTER_CLOSE}</li>
 *   <li><b>Dock2/Dock3 共有（5 个）</b>：{@link #FLIGHTTASK_STOP} / {@link #RETURN_SPECIFIC_HOME} /
 *       {@link #ESIM_ACTIVATE} / {@link #ESIM_OPERATOR_SWITCH} / {@link #SIM_SLOT_SWITCH}</li>
 *   <li><b>Dock3 独有（1 个）</b>：{@link #RTK_CALIBRATION}</li>
 *   <li><b>补全（18 个）</b>：OTA / 远程日志 / 空中航线 / PSDK / ESDK services 方法（simulator 已对接 hivemind 验证）</li>
 *   <li><b>回归补全（8 个）</b>：远程解禁 3（Dock3 flysafe.html）+ Pilot 指令飞行 5（poi_mode_* / poi_circle_speed_set / cloud_control_auth_request / cloud_control_release，Pilot-to-Cloud drc.html）</li>
 * </ul>
 *
 * <p>当前共 97 个 method（71 个 catalog 盘点 + 18 个 simulator 已对接补全 + 8 个回归补全）。
 * 所有回归方法均已通过 DJI 文档验证 Data 字段表。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html">
 * DJI Dock 上云 services 通道</a>、
 * <a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/mqtt/dji-rc-plus-2/drc.html">
 * DJI Pilot2 指令飞行 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html")
@Verified(basis = "simulator dji-method-catalog.json services 已对接 hivemind 盘点 89 个 method + DJI 文档验证 8 个回归方法（flysafe 3 + Pilot 5）")
public enum ServiceMethod {

    // ===== 三 Dock 共有（62 个）=====

    /** 航线任务准备 */
    FLIGHTTASK_PREPARE("flighttask_prepare", "航线任务准备"),
    /** 航线任务执行 */
    FLIGHTTASK_EXECUTE("flighttask_execute", "航线任务执行"),
    /** 航线任务暂停 */
    FLIGHTTASK_PAUSE("flighttask_pause", "航线任务暂停"),
    /** 航线任务恢复 */
    FLIGHTTASK_RECOVERY("flighttask_recovery", "航线任务恢复"),
    /** 航线任务撤销 */
    FLIGHTTASK_UNDO("flighttask_undo", "航线任务撤销"),
    /** 返航 */
    RETURN_HOME("return_home", "返航"),
    /** 取消返航 */
    RETURN_HOME_CANCEL("return_home_cancel", "取消返航"),
    /** 开启直播 */
    LIVE_START_PUSH("live_start_push", "开启直播"),
    /** 停止直播 */
    LIVE_STOP_PUSH("live_stop_push", "停止直播"),
    /** 设置直播质量 */
    LIVE_SET_QUALITY("live_set_quality", "设置直播质量"),
    /** 切换直播相机 */
    LIVE_CAMERA_CHANGE("live_camera_change", "切换直播相机"),
    /** 切换直播镜头 */
    LIVE_LENS_CHANGE("live_lens_change", "切换直播镜头"),
    /** 媒体上传优先级 */
    UPLOAD_FLIGHTTASK_MEDIA_PRIORITIZE("upload_flighttask_media_prioritize", "媒体上传优先级"),
    /** 进入 DRC 模式 */
    DRC_MODE_ENTER("drc_mode_enter", "进入 DRC 模式"),
    /** 退出 DRC 模式 */
    DRC_MODE_EXIT("drc_mode_exit", "退出 DRC 模式"),
    /** 飞向目标点 */
    FLY_TO_POINT("fly_to_point", "飞向目标点"),
    /** 停止飞向目标点 */
    FLY_TO_POINT_STOP("fly_to_point_stop", "停止飞向目标点"),
    /** 更新飞向目标点 */
    FLY_TO_POINT_UPDATE("fly_to_point_update", "更新飞向目标点"),
    /** 起飞到目标点 */
    TAKEOFF_TO_POINT("takeoff_to_point", "起飞到目标点"),
    /** 抢夺飞行控制权 */
    FLIGHT_AUTHORITY_GRAB("flight_authority_grab", "抢夺飞行控制权"),
    /** 抢夺负载控制权 */
    PAYLOAD_AUTHORITY_GRAB("payload_authority_grab", "抢夺负载控制权"),
    /** 相机变焦 */
    CAMERA_FRAME_ZOOM("camera_frame_zoom", "相机变焦"),
    /** 切换相机模式 */
    CAMERA_MODE_SWITCH("camera_mode_switch", "切换相机模式"),
    /** 拍照 */
    CAMERA_PHOTO_TAKE("camera_photo_take", "拍照"),
    /** 停止拍照 */
    CAMERA_PHOTO_STOP("camera_photo_stop", "停止拍照"),
    /** 开始录像 */
    CAMERA_RECORDING_START("camera_recording_start", "开始录像"),
    /** 停止录像 */
    CAMERA_RECORDING_STOP("camera_recording_stop", "停止录像"),
    /** 屏幕拖拽 */
    CAMERA_SCREEN_DRAG("camera_screen_drag", "屏幕拖拽"),
    /** 相机瞄准 */
    CAMERA_AIM("camera_aim", "相机瞄准"),
    /** 设置相机焦距 */
    CAMERA_FOCAL_LENGTH_SET("camera_focal_length_set", "设置相机焦距"),
    /** 云台复位 */
    GIMBAL_RESET("gimbal_reset", "云台复位"),
    /** 相机看向目标 */
    CAMERA_LOOK_AT("camera_look_at", "相机看向目标"),
    /** 屏幕分屏 */
    CAMERA_SCREEN_SPLIT("camera_screen_split", "屏幕分屏"),
    /** 设置照片存储 */
    PHOTO_STORAGE_SET("photo_storage_set", "设置照片存储"),
    /** 设置视频存储 */
    VIDEO_STORAGE_SET("video_storage_set", "设置视频存储"),
    /** 设置相机曝光模式 */
    CAMERA_EXPOSURE_MODE_SET("camera_exposure_mode_set", "设置相机曝光模式"),
    /** 设置相机曝光 */
    CAMERA_EXPOSURE_SET("camera_exposure_set", "设置相机曝光"),
    /** 设置相机对焦模式 */
    CAMERA_FOCUS_MODE_SET("camera_focus_mode_set", "设置相机对焦模式"),
    /** 设置相机对焦值 */
    CAMERA_FOCUS_VALUE_SET("camera_focus_value_set", "设置相机对焦值"),
    /** 点对焦动作 */
    CAMERA_POINT_FOCUS_ACTION("camera_point_focus_action", "点对焦动作"),
    /** 设置红外测光模式 */
    IR_METERING_MODE_SET("ir_metering_mode_set", "设置红外测光模式"),
    /** 设置红外测光点 */
    IR_METERING_POINT_SET("ir_metering_point_set", "设置红外测光点"),
    /** 设置红外测光区域 */
    IR_METERING_AREA_SET("ir_metering_area_set", "设置红外测光区域"),
    /** 打开舱盖 */
    COVER_OPEN("cover_open", "打开舱盖"),
    /** 关闭舱盖 */
    COVER_CLOSE("cover_close", "关闭舱盖"),
    /** 强制关闭舱盖 */
    COVER_FORCE_CLOSE("cover_force_close", "强制关闭舱盖"),
    /** 开机（飞行器） */
    DRONE_OPEN("drone_open", "开机（飞行器）"),
    /** 关机（飞行器） */
    DRONE_CLOSE("drone_close", "关机（飞行器）"),
    /** 开启充电 */
    CHARGE_OPEN("charge_open", "开启充电"),
    /** 关闭充电 */
    CHARGE_CLOSE("charge_close", "关闭充电"),
    /** 设备重启 */
    DEVICE_REBOOT("device_reboot", "设备重启"),
    /** 格式化机场存储 */
    DEVICE_FORMAT("device_format", "格式化机场存储"),
    /** 格式化飞行器存储 */
    DRONE_FORMAT("drone_format", "格式化飞行器存储"),
    /** 开启调试模式 */
    DEBUG_MODE_OPEN("debug_mode_open", "开启调试模式"),
    /** 关闭调试模式 */
    DEBUG_MODE_CLOSE("debug_mode_close", "关闭调试模式"),
    /** 开启补光灯 */
    SUPPLEMENT_LIGHT_OPEN("supplement_light_open", "开启补光灯"),
    /** 关闭补光灯 */
    SUPPLEMENT_LIGHT_CLOSE("supplement_light_close", "关闭补光灯"),
    /** 电池保养开关 */
    BATTERY_MAINTENANCE_SWITCH("battery_maintenance_switch", "电池保养开关"),
    /** 电池存储模式开关 */
    BATTERY_STORE_MODE_SWITCH("battery_store_mode_switch", "电池存储模式开关"),
    /** 告警状态开关 */
    ALARM_STATE_SWITCH("alarm_state_switch", "告警状态开关"),
    /** 空调模式切换 */
    AIR_CONDITIONER_MODE_SWITCH("air_conditioner_mode_switch", "空调模式切换"),
    /** SDR 工作模式切换 */
    SDR_WORKMODE_SWITCH("sdr_workmode_switch", "SDR 工作模式切换"),

    // ===== Dock1 独有（3 个）=====

    /** 中断起飞设置（Dock1 独有，Dock2/Dock3 用 flighttask_stop） */
    FLIGHT_SETUP_ABORT("flight_setup_abort", "中断起飞设置"),
    /** 打开推杆（Dock1 独有） */
    PUTTER_OPEN("putter_open", "打开推杆"),
    /** 关闭推杆（Dock1 独有） */
    PUTTER_CLOSE("putter_close", "关闭推杆"),

    // ===== Dock2/Dock3 共有（5 个）=====

    /** 停止航线任务（Dock2/Dock3 共有，Dock1 用 flight_setup_abort） */
    FLIGHTTASK_STOP("flighttask_stop", "停止航线任务"),
    /** 返回指定返航点（Dock2/Dock3 共有） */
    RETURN_SPECIFIC_HOME("return_specific_home", "返回指定返航点"),
    /** 激活 eSIM（Dock2/Dock3 共有） */
    ESIM_ACTIVATE("esim_activate", "激活 eSIM"),
    /** 切换 eSIM 运营商（Dock2/Dock3 共有） */
    ESIM_OPERATOR_SWITCH("esim_operator_switch", "切换 eSIM 运营商"),
    /** 切换 SIM 卡槽（Dock2/Dock3 共有） */
    SIM_SLOT_SWITCH("sim_slot_switch", "切换 SIM 卡槽"),

    // ===== Dock3 独有（1 个）=====

    /** RTK 校准（Dock3 独有） */
    RTK_CALIBRATION("rtk_calibration", "RTK 校准"),

    // ===== 补全：OTA / 远程日志 / 空中航线 / PSDK / ESDK（18 个，simulator 已对接） =====

    /** 固件升级 OTA 创建（simulator 已对接 hivemind 验证） */
    OTA_CREATE("ota_create", "固件升级 OTA 创建"),
    /** 远程日志上传开始（simulator 已对接 hivemind 验证） */
    FILEUPLOAD_START("fileupload_start", "远程日志上传开始"),
    /** 远程日志文件列表（simulator 已对接 hivemind 验证） */
    FILEUPLOAD_LIST("fileupload_list", "远程日志文件列表"),
    /** 远程日志上传状态更新（simulator 已对接 hivemind 验证） */
    FILEUPLOAD_UPDATE("fileupload_update", "远程日志上传状态更新"),
    /** 空中航线下发（simulator 已对接 hivemind 验证） */
    IN_FLIGHT_WAYLINE_DELIVER("in_flight_wayline_deliver", "空中航线下发"),
    /** 空中航线暂停（simulator 已对接 hivemind 验证） */
    IN_FLIGHT_WAYLINE_STOP("in_flight_wayline_stop", "空中航线暂停"),
    /** 空中航线恢复（simulator 已对接 hivemind 验证） */
    IN_FLIGHT_WAYLINE_RECOVER("in_flight_wayline_recover", "空中航线恢复"),
    /** 空中航线取消（simulator 已对接 hivemind 验证，请求 data 为空对象，复用 NoParameterRequest） */
    IN_FLIGHT_WAYLINE_CANCEL("in_flight_wayline_cancel", "空中航线取消"),
    /** PSDK 喊话器音量设置（services 通道，与 DRC 通道的 drc_speaker_play_volume_set 不同） */
    SPEAKER_PLAY_VOLUME_SET("speaker_play_volume_set", "PSDK 喊话器音量设置"),
    /** PSDK 喊话器播放模式设置（services 通道） */
    SPEAKER_PLAY_MODE_SET("speaker_play_mode_set", "PSDK 喊话器播放模式设置"),
    /** PSDK 喊话器停止播放（services 通道） */
    SPEAKER_PLAY_STOP("speaker_play_stop", "PSDK 喊话器停止播放"),
    /** PSDK 喊话器重新播放（services 通道） */
    SPEAKER_REPLAY("speaker_replay", "PSDK 喊话器重新播放"),
    /** PSDK 喊话器 TTS 播放开始（services 通道） */
    SPEAKER_TTS_PLAY_START("speaker_tts_play_start", "PSDK 喊话器 TTS 播放开始"),
    /** PSDK 喊话器音频文件播放开始（services 通道） */
    SPEAKER_AUDIO_PLAY_START("speaker_audio_play_start", "PSDK 喊话器音频播放开始"),
    /** PSDK 输入框文本设置（services 通道） */
    PSDK_INPUT_BOX_TEXT_SET("psdk_input_box_text_set", "PSDK 输入框文本设置"),
    /** PSDK 控件值设置（services 通道） */
    PSDK_WIDGET_VALUE_SET("psdk_widget_value_set", "PSDK 控件值设置"),
    /** 自定义数据传输到 PSDK（services 通道） */
    CUSTOM_DATA_TRANSMISSION_TO_PSDK("custom_data_transmission_to_psdk", "自定义数据传输到 PSDK"),
    /** 自定义数据传输到 ESDK（services 通道） */
    CUSTOM_DATA_TRANSMISSION_TO_ESDK("custom_data_transmission_to_esdk", "自定义数据传输到 ESDK"),

    // ===== 回归补全：远程解禁 + Pilot 指令飞行（8 个，DJI 文档依据）=====

    /** 启用/禁用设备的单个解禁证书 */
    UNLOCK_LICENSE_SWITCH("unlock_license_switch", "启用/禁用解禁证书"),
    /** 更新设备的解禁证书 */
    UNLOCK_LICENSE_UPDATE("unlock_license_update", "更新解禁证书"),
    /** 获取设备的解禁证书列表 */
    UNLOCK_LICENSE_LIST("unlock_license_list", "获取解禁证书列表"),
    /** 进入 POI 环绕模式（Pilot 上云） */
    POI_MODE_ENTER("poi_mode_enter", "进入 POI 环绕模式"),
    /** 退出 POI 环绕模式（Pilot 上云，空 data） */
    POI_MODE_EXIT("poi_mode_exit", "退出 POI 环绕模式"),
    /** POI 环绕速度设置（Pilot 上云） */
    POI_CIRCLE_SPEED_SET("poi_circle_speed_set", "POI 环绕速度设置"),
    /** 请求云控授权（Pilot 上云，data: user_id/user_callsign/control_keys） */
    CLOUD_CONTROL_AUTH_REQUEST("cloud_control_auth_request", "请求云控授权"),
    /** 释放云端控制权（Pilot 上云，data: control_keys 控制权列表） */
    CLOUD_CONTROL_RELEASE("cloud_control_release", "释放云端控制权");

    private final String methodName;
    private final String description;

    ServiceMethod(String methodName, String description) {
        this.methodName = methodName;
        this.description = description;
    }

    /** method 字符串值，如 "flighttask_execute" */
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
    public static Optional<ServiceMethod> fromMethodName(String methodName) {
        if (methodName == null || methodName.isBlank()) {
            return Optional.empty();
        }
        for (ServiceMethod m : values()) {
            if (m.methodName.equals(methodName)) {
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }
}
