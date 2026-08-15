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

package ltd.cdmi.dji.cloudapi.sdk.protocol.error;

import java.util.Map;
import java.util.Optional;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API 错误码常量与查表。
 *
 * <p>错误码格式为 6 位 ABCDEF：A=来源（3/5=设备端，4/6=Pilot）/ BC=模块 / DEF=具体错误。
 * 另含通用码（0=成功/1=失败）与 HTTP API 注册绑定码（210xxx）。
 *
 * <p>本类全收 DJI Cloud API 错误码文档清单，按模块分组，提供 {@link #describe(int)} 运行时查表获取官方描述。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/error-code.html">
 * DJI Cloud API 错误码</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/error-code.html")
@Verified(basis = "DJI Cloud API 错误码文档：错误码 + 描述对照表")
@Inferred(
    reason = "514xxx 机场操作错误码仅有范围描述无逐个清单（514101-514185），514300-514304 有逐个描述但来源为 deepwiki 非官方文档；4xxxx/6xxxx Pilot 错误码未收录（SDK 关注 Dock 上云）",
    verifyPoint = "514xxx 逐个错误码待 DJI 官方文档确认后补录；4xxxx/6xxxx Pilot 错误码待补；513xxx 描述由 deepwiki 英文翻译为中文，待 DJI 官方文档校对"
)
public final class DjiErrorCode {

    private DjiErrorCode() {
    }

    // ===== 通用错误码 =====
    /** 成功 */
    public static final int SUCCESS = 0;
    /** 失败（通用） */
    public static final int FAIL = 1;

    // ===== HTTP API 注册绑定错误码（210xxx 系列）=====
    /** 组织 ID 与绑定码错误 */
    public static final int BIND_CODE_ERROR = 210229;
    /** 组织不存在 */
    public static final int ORGANIZATION_NOT_EXIST = 210234;
    /** 设备已绑定到其他组织 */
    public static final int DEVICE_BIND_OTHER = 210235;

    // ===== 312xxx 设备操作（升级/电源/连接）=====
    /** 设备升级中，请勿重复操作 */
    public static final int DEVICE_FIRMWARE_UPDATING = 312014;
    /** 机场业务繁忙无法进行设备升级，请等待机场处于空闲中后再试 */
    public static final int DOCK_BUSY_CANNOT_UPDATE = 312015;
    /** 升级失败，机场和飞行器图传链路异常，请重启机场和飞行器后重试 */
    public static final int UPDATE_FAIL_TRANSMISSION_ERROR = 312016;
    /** 飞行器开机失败或未连接，请检查飞行器是否在舱内，是否安装电池，机场和飞行器是否已对频 */
    public static final int AIRCRAFT_POWER_ON_FAILED = 312022;
    /** 推杆闭合失败无法升级飞行器，请检查急停按钮是否被按下，推杆是否有异物卡住 */
    public static final int DRIVING_ROD_CLOSURE_FAILED = 312023;
    /** 升级失败，机场未检测到飞行器 */
    public static final int UPDATE_FAIL_AIRCRAFT_NOT_DETECTED = 312027;
    /** 升级失败，设备升级过程中设备被重启 */
    public static final int UPDATE_FAIL_DEVICE_RESTARTED = 312028;
    /** 设备重启中无法进行设备升级，请等待设备重启完成后重试 */
    public static final int DEVICE_RESTARTING_CANNOT_UPDATE = 312029;
    /** 升级失败，飞行器增强图传开启后无法升级，请关闭飞行器增强图传后重试 */
    public static final int UPDATE_FAIL_ENHANCED_TRANSMISSION_ENABLED = 312030;
    /** 设备电量过低，请充电至20%以上后重试 */
    public static final int BATTERY_TOO_LOW_CHARGE_20 = 312704;

    // ===== 314xxx 飞行任务准备 =====
    /** 设备当前无法支持该操作，建议检查设备当前工作状态 */
    public static final int OPERATION_NOT_SUPPORTED_IN_STATE = 314000;
    /** 飞行任务下发失败，请稍后重试 */
    public static final int FLIGHT_TASK_DISTRIBUTE_FAIL_314001 = 314001;
    /** 飞行任务下发失败，请稍后重试 */
    public static final int FLIGHT_TASK_DISTRIBUTE_FAIL_314002 = 314002;
    /** 航线文件格式不兼容，请检查航线文件是否正确 */
    public static final int ROUTE_FILE_FORMAT_INCOMPATIBLE = 314003;
    /** 飞行任务下发失败，请稍后重试或重启机场后重试 */
    public static final int FLIGHT_TASK_DISTRIBUTE_FAIL_RESTART = 314005;
    /** 飞行器初始化失败，请重启机场后重试 */
    public static final int AIRCRAFT_INIT_FAIL_314006 = 314006;
    /** 机场传输航线至飞行器失败，请重启机场后重试 */
    public static final int DOCK_TRANSFER_ROUTE_FAIL = 314007;
    /** 飞行器起飞前准备超时，请重启机场后重试 */
    public static final int AIRCRAFT_PREP_TIMEOUT_314008 = 314008;
    /** 飞行器初始化失败，请重启机场后重试 */
    public static final int AIRCRAFT_INIT_FAIL_314009 = 314009;
    /** 航线执行失败，请重启机场后重试 */
    public static final int ROUTE_EXECUTION_FAIL_314010 = 314010;
    /** 机场系统异常，无法获取飞行任务执行结果 */
    public static final int DOCK_SYSTEM_ERROR_NO_TASK_RESULT = 314011;
    /** 飞行器起飞前准备失败，无法执行飞行任务，请重启机场后重试 */
    public static final int AIRCRAFT_PREP_FAIL_314012 = 314012;
    /** 飞行任务下发失败，机场无法获取到本次飞行任务的航线，无法执行飞行任务，请稍后重试 */
    public static final int DOCK_CANNOT_GET_ROUTE = 314013;
    /** 机场系统异常，飞行任务执行失败，请稍后重试 */
    public static final int DOCK_SYSTEM_ERROR_TASK_FAIL = 314014;
    /** 机场传输精准复拍航线至飞行器失败，无法执行飞行任务，请稍后重试或重启机场后重试 */
    public static final int AI_SPOT_CHECK_ROUTE_TRANSFER_FAIL = 314015;
    /** 航线文件解析失败，无法执行飞行任务，请检查航线文件 */
    public static final int ROUTE_FILE_PARSE_FAIL_314016 = 314016;
    /** 航线文件解析失败，请检查航线后再试 */
    public static final int ROUTE_FILE_PARSE_FAIL_314017 = 314017;
    /** 飞行器 RTK 定位异常，无法执行飞行任务，请稍后重试或重启机场后重试 */
    public static final int RTK_POSITIONING_ABNORMAL_314018 = 314018;
    /** 飞行器 RTK 收敛失败，无法执行飞行任务，请稍后重试或重启机场后重试 */
    public static final int RTK_CONVERGE_FAIL_314019 = 314019;
    /** 飞行器不在停机坪正中间或飞行器朝向不正确，无法执行飞行任务，请检查飞行器位置和朝向 */
    public static final int AIRCRAFT_POSITION_HEADING_INCORRECT = 314020;
    /** 飞行器 RTK 定位异常，无法执行飞行任务，请稍后重试或重启机场后重试 */
    public static final int RTK_POSITIONING_ABNORMAL_314021 = 314021;
    /** 进离场航线下发失败，请稍后重试或重启机场后重试 */
    public static final int ENTRY_EXIT_ROUTE_DISTRIBUTE_FAIL_314024 = 314024;
    /** RTK收敛超时，用户手动取消任务 */
    public static final int RTK_CONVERGE_TIMEOUT_CANCELLED = 314025;
    /** 任务失败，由于机场网络断开，飞行器已自动返航，请确保机场已连接网络后再试 */
    public static final int NETWORK_DISCONNECT_AUTO_RTH = 314200;

    // ===== 315xxx 通信与系统错误 =====
    /** 机场通信异常，请重启机场后重试 */
    public static final int DOCK_COMM_ABNORMAL_315000 = 315000;
    /** 机场通信异常，请远程开启飞机并等待 1min 后，再次下发任务重试 */
    public static final int DOCK_COMM_ABNORMAL_POWER_ON_AIRCRAFT = 315001;
    /** 机场通信异常，请重启机场后重试 */
    public static final int DOCK_COMM_ABNORMAL_315002 = 315002;
    /** 机场通信异常，请重启机场后重试 */
    public static final int DOCK_COMM_ABNORMAL_315003 = 315003;
    /** 任务失败，请等待两个机场都空闲后，再次下发任务重试 */
    public static final int TASK_FAIL_WAIT_BOTH_DOCKS_IDLE_315004 = 315004;
    /** 机场通信异常，请重启机场后重试 */
    public static final int DOCK_COMM_ABNORMAL_315005 = 315005;
    /** 机场通信异常，请重启机场后重试 */
    public static final int DOCK_COMM_ABNORMAL_315006 = 315006;
    /** 机场通信异常，请将机场升级到最新版本或重启机场后重试 */
    public static final int DOCK_COMM_ABNORMAL_UPGRADE_OR_RESTART = 315007;
    /** 降落机场和起飞机场标定信息不一致，请确认两个机场均链路通畅且使用了相同的网络信息标定 */
    public static final int LANDING_TAKEOFF_DOCK_CALIBRATION_MISMATCH = 315008;
    /** 机场通信异常，请重启机场后重试 */
    public static final int DOCK_COMM_ABNORMAL_315009 = 315009;
    /** 无法停止飞行任务，请稍后重试，如果仍报错请联系大疆售后 */
    public static final int CANNOT_STOP_FLIGHT_TASK_315010 = 315010;
    /** 无法停止飞行任务，请稍后重试，如果仍报错请联系大疆售后 */
    public static final int CANNOT_STOP_FLIGHT_TASK_315011 = 315011;
    /** 无法停止飞行任务，请稍后重试，如果仍报错请联系大疆售后 */
    public static final int CANNOT_STOP_FLIGHT_TASK_315012 = 315012;
    /** 飞行任务下发失败，请稍后重试，如果仍报错请联系大疆售后 */
    public static final int FLIGHT_TASK_DISTRIBUTE_FAIL_315013 = 315013;
    /** 当前任务类型不支持设置返航点 */
    public static final int TASK_TYPE_NOT_SUPPORT_RTH_POINT = 315014;
    /** 返航点设置失败，请稍后重试，如果仍报错请联系大疆售后 */
    public static final int RTH_POINT_SET_FAIL = 315015;
    /** 飞行任务下发失败，请稍后重试，如果仍报错请联系大疆售后 */
    public static final int FLIGHT_TASK_DISTRIBUTE_FAIL_315016 = 315016;
    /** 飞行任务下发失败，请稍后重试，如果仍报错请联系大疆售后 */
    public static final int FLIGHT_TASK_DISTRIBUTE_FAIL_315017 = 315017;
    /** 任务失败，请等待两个机场都空闲后，再次下发任务重试 */
    public static final int TASK_FAIL_WAIT_BOTH_DOCKS_IDLE_315018 = 315018;
    /** 设备部署位置不佳，无法执行蛙跳任务，请选择其它机场再试 */
    public static final int DEPLOYMENT_POSITION_POOR_FOR_LEAPFROG = 315019;
    /** 机场系统异常，请重启机场后重试 */
    public static final int DOCK_SYSTEM_ABNORMAL_315050 = 315050;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315051 = 315051;
    /** 机场位置未收敛，请等待一段时间后重试 */
    public static final int DOCK_POSITION_NOT_CONVERGED = 315052;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315053 = 315053;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315054 = 315054;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315055 = 315055;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315056 = 315056;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315057 = 315057;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315058 = 315058;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315059 = 315059;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315060 = 315060;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315061 = 315061;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315062 = 315062;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315063 = 315063;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315064 = 315064;
    /** 任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后 */
    public static final int TASK_FAIL_RESTART_DOCK_315065 = 315065;

    // ===== 316xxx 飞行器与环境错误 =====
    /** 飞行器参数配置失败，请重启机场后重试 */
    public static final int AIRCRAFT_PARAM_CONFIG_FAIL_316001 = 316001;
    /** 飞行器参数配置失败，请重启机场后重试 */
    public static final int AIRCRAFT_PARAM_CONFIG_FAIL_316002 = 316002;
    /** 飞行器参数配置失败，请重启机场后重试 */
    public static final int AIRCRAFT_PARAM_CONFIG_FAIL_316003 = 316003;
    /** 飞行器参数配置失败，请重启机场后重试 */
    public static final int AIRCRAFT_PARAM_CONFIG_FAIL_316004 = 316004;
    /** 飞行器 RTK 收敛失败，无法执行飞行任务，请重启机场后重试 */
    public static final int RTK_CONVERGE_FAIL_316005 = 316005;
    /** 任务超时，飞行器已丢失或降落时机场未开启舱盖或展开推杆，飞行器无法降落回机场，请尽快至机场部署现场检查飞行器状况 */
    public static final int TASK_TIMEOUT_AIRCRAFT_LOST_OR_COVER_CLOSED = 316006;
    /** 飞行器初始化失败，请重启机场后重试 */
    public static final int AIRCRAFT_INIT_FAIL_316007 = 316007;
    /** 机场获取飞行器控制权失败，无法执行飞行任务，请确认遥控器未锁定控制权 */
    public static final int DOCK_GET_CONTROL_FAIL_REMOTE_LOCKED = 316008;
    /** 飞行器电量低于30%，无法执行飞行任务，请充电后重试（建议电量≥50%） */
    public static final int AIRCRAFT_BATTERY_LOW_30_316009 = 316009;
    /** 机场未检测到飞行器，无法执行飞行任务，请检查舱内是否有飞行器，机场与飞行器是否已对频，或重启机场后重试 */
    public static final int DOCK_NOT_DETECT_AIRCRAFT = 316010;
    /** 飞行器降落位置偏移过大，请检查飞行器是否需要现场摆正 */
    public static final int AIRCRAFT_LANDING_POSITION_OFFSET = 316011;
    /** 飞行器起飞前准备失败，无法执行飞行任务，请重启机场后重试 */
    public static final int AIRCRAFT_PREP_FAIL_316012 = 316012;
    /** 飞行器起飞前准备失败，无法执行飞行任务，请重启机场后重试 */
    public static final int AIRCRAFT_PREP_FAIL_316013 = 316013;
    /** 飞行器起飞前准备失败，无法执行飞行任务，请重启机场后重试 */
    public static final int AIRCRAFT_PREP_FAIL_316014 = 316014;
    /** 飞行器 RTK 收敛位置距离机场过远，无法执行飞行任务，请重启机场后重试 */
    public static final int RTK_CONVERGE_POSITION_TOO_FAR = 316015;
    /** 飞行器降落至机场超时，可能是机场与飞行器断连导致，请通过直播查看飞行器是否降落至舱内 */
    public static final int AIRCRAFT_LANDING_TIMEOUT = 316016;
    /** 获取飞行器媒体数量超时，可能是机场与飞行器断连导致，请通过直播查看飞行器是否降落至舱内 */
    public static final int GET_MEDIA_COUNT_TIMEOUT = 316017;
    /** 飞行任务执行超时，可能是机场与飞行器断连导致，请通过直播查看飞行器是否降落至舱内 */
    public static final int FLIGHT_TASK_EXECUTION_TIMEOUT = 316018;
    /** 机场系统错误，无法执行飞行任务，请稍后重试 */
    public static final int DOCK_SYSTEM_ERROR_CANNOT_FLY = 316019;
    /** 飞行器使用的 RTK 信号源错误，请稍后重试 */
    public static final int RTK_SIGNAL_SOURCE_ERROR = 316020;
    /** 飞行器 RTK 信号源检查超时，请稍后重试 */
    public static final int RTK_SIGNAL_SOURCE_CHECK_TIMEOUT = 316021;
    /** 飞行器无法执行返航指令，请检查飞行器是否已开机，机场与飞行器是否已断连，请确认无以上问题后重试 */
    public static final int CANNOT_RTH_AIRCRAFT_OFF_OR_DISCONNECTED = 316022;
    /** 飞行器无法执行返航指令，飞行器已被 B 控接管，请在 B 控操控飞行器，或关闭 B 控后重试 */
    public static final int CANNOT_RTH_B_CTRL_TAKEOVER = 316023;
    /** 飞行器执行返航指令失败，请检查飞行器是否已起飞，确认飞行器已起飞后请重试 */
    public static final int RTH_FAIL_NOT_TAKEN_OFF = 316024;
    /** 飞行器参数配置失败，请稍后重试或重启机场后重试 */
    public static final int AIRCRAFT_PARAM_CONFIG_FAIL_316025 = 316025;
    /** 机场急停按钮被按下，无法执行飞行任务，请释放急停按钮后重试 */
    public static final int EMERGENCY_STOP_PRESSED_CANNOT_FLY = 316026;
    /** 飞行器参数配置超时，请稍后重试或重启机场后重试 */
    public static final int AIRCRAFT_PARAM_CONFIG_TIMEOUT = 316027;
    /** 机场急停按钮被按下，飞行器将飞往备降点降落，请立即检查飞行器是否已安全降落并将飞行器放回至机场 */
    public static final int EMERGENCY_STOP_ALTERNATE_LANDING = 316029;
    /** 获取电池数据超时，请稍后重试或重启飞行器后重试 */
    public static final int GET_BATTERY_DATA_TIMEOUT = 316032;
    /** 飞行器电池循环次数过高，为保证飞行安全，已自动终止任务，建议更换该电池 */
    public static final int BATTERY_CYCLE_COUNT_TOO_HIGH = 316033;
    /** 无法起飞，飞行器固件版本与机场固件版本不匹配，为保证飞行安全请升级固件后再试 */
    public static final int FIRMWARE_VERSION_MISMATCH_CANNOT_TAKEOFF = 316034;
    /** 进离场航线下发失败，请确保设备固件为最新版本后重新下发任务，如果持续报错，请联系大疆售后 */
    public static final int ENTRY_EXIT_ROUTE_DISTRIBUTE_FAIL_316035 = 316035;
    /** 飞行器因电量过低在舱外降落，请立即检查飞行器是否已安全降落并将飞行器放回至机场 */
    public static final int LOW_BATTERY_LANDING_OUTSIDE_DOCK = 316050;
    /** 飞行任务异常，飞行器在舱外降落，请立即检查飞行器是否已安全降落并将飞行器放回至机场 */
    public static final int TASK_ABNORMAL_LANDING_OUTSIDE_DOCK = 316051;
    /** 飞行任务异常，飞行器将飞往备降点降落，请立即检查飞行器是否已安全降落并将飞行器放回至机场 */
    public static final int TASK_ABNORMAL_ALTERNATE_LANDING = 316052;
    /** 用户已操控飞行器降落，请立即检查飞行器是否已安全降落并将飞行器放回至机场 */
    public static final int USER_CONTROLLED_LANDING = 316053;
    /** 获取相机概要信息失败，请重试 */
    public static final int GET_CAMERA_SUMMARY_FAIL = 316100;
    /** 设置相机为单拍模式失败，请重试 */
    public static final int SET_CAMERA_SINGLE_SHOT_FAIL = 316101;
    /** 关闭相机水印失败，请重试 */
    public static final int CLOSE_CAMERA_WATERMARK_FAIL = 316102;
    /** 设置测光模式到平均测光失败，请重试 */
    public static final int SET_AVERAGE_METERING_FAIL = 316103;
    /** 切换镜头到广角镜头失败，请重试 */
    public static final int SWITCH_WIDE_LENS_FAIL = 316104;
    /** 设置相机存储照片失败，请重试 */
    public static final int SET_PHOTO_STORAGE_FAIL = 316105;
    /** 红外变焦倍数设置失败，请重试 */
    public static final int IR_ZOOM_SET_FAIL = 316106;
    /** 照片尺寸设置4k失败，请重试 */
    public static final int PHOTO_SIZE_4K_SET_FAIL = 316107;
    /** 设置照片存储格式为jpeg格式失败，请重试 */
    public static final int SET_JPEG_FORMAT_FAIL = 316108;
    /** 关闭相机畸变矫正失败，请重试 */
    public static final int CLOSE_DISTORTION_CORRECTION_FAIL = 316109;
    /** 打开相机机械快门失败，请重试 */
    public static final int OPEN_MECHANICAL_SHUTTER_FAIL = 316110;
    /** 设置对焦模式失败，请重试 */
    public static final int SET_FOCUS_MODE_FAIL = 316111;

    // ===== 317xxx 媒体管理 =====
    /** 获取飞行器媒体文件数量失败，请重启机场后重试 */
    public static final int GET_AIRCRAFT_MEDIA_COUNT_FAIL = 317001;
    /** 飞行器存储格式化失败，飞行器未开机、未连接或未检测到相机，请确认无以上问题后重试，或重启飞行器后重试 */
    public static final int AIRCRAFT_STORAGE_FORMAT_FAIL_NOT_DETECTED = 317002;
    /** 飞行器存储格式化失败，请重启飞行器后重试 */
    public static final int AIRCRAFT_STORAGE_FORMAT_FAIL = 317003;
    /** 机场媒体文件格式化失败，请稍后重试或重启机场后重试 */
    public static final int DOCK_MEDIA_FORMAT_FAIL = 317004;
    /** 飞行器结束录像失败，本次飞行任务的媒体文件可能无法上传 */
    public static final int AIRCRAFT_STOP_RECORDING_FAIL = 317005;
    /** 无法格式化，请等待当前飞行器媒体文件下载完成后再试 */
    public static final int CANNOT_FORMAT_DURING_DOWNLOAD = 317006;
    /** 获取媒体文件数量失败，请稍后重试，如本架次任务有媒体文件且持续报错可联系大疆售后 */
    public static final int GET_MEDIA_COUNT_FAIL = 317007;

    // ===== 319xxx 系统操作 =====
    /** 机场作业中或设备异常反馈上传日志中，无法执行飞行任务，请等待当前飞行任务或操作执行完成后重试 */
    public static final int DOCK_BUSY_OR_LOG_UPLOADING = 319001;
    /** 机场系统运行异常，请重启机场后重试 */
    public static final int DOCK_SYSTEM_ABNORMAL_319002 = 319002;
    /** 机场系统运行异常，请重新下发任务 */
    public static final int DOCK_SYSTEM_ABNORMAL_REDISPATCH = 319003;
    /** 飞行任务执行超时，已自动终止本次飞行任务 */
    public static final int FLIGHT_TASK_TIMEOUT_AUTO_TERMINATED = 319004;
    /** 云端与机场通信异常，无法执行飞行任务 */
    public static final int CLOUD_DOCK_COMM_ABNORMAL = 319005;
    /** 取消飞行任务失败，飞行任务已经在执行中 */
    public static final int CANCEL_TASK_FAIL_IN_EXECUTION = 319006;
    /** 修改飞行任务失败，飞行任务已经在执行中 */
    public static final int MODIFY_TASK_FAIL_IN_EXECUTION = 319007;
    /** 机场时间与云端时间不同步，机场无法执行飞行任务 */
    public static final int TIME_SYNC_ERROR = 319008;
    /** 飞行任务下发失败，请稍后重试或重启机场后重试 */
    public static final int FLIGHT_TASK_DISTRIBUTE_FAIL_319009 = 319009;
    /** 机场固件版本过低，无法执行飞行任务，请升级机场固件为最新版本后重试 */
    public static final int DOCK_FIRMWARE_TOO_LOW = 319010;
    /** 机场正在初始化中，无法执行飞行任务，请等待机场初始化完成后重试 */
    public static final int DOCK_INITIALIZING = 319015;
    /** 机场正在执行其他飞行任务，无法执行本次飞行任务 */
    public static final int DOCK_EXECUTING_OTHER_TASK = 319016;
    /** 机场正在处理上次飞行任务媒体文件，无法执行本次飞行任务，请稍后重试 */
    public static final int DOCK_PROCESSING_MEDIA = 319017;
    /** 机场正在自动导出日志中（设备异常反馈），无法执行飞行任务，请稍后重试 */
    public static final int DOCK_EXPORTING_LOG = 319018;
    /** 机场正在拉取日志中（设备异常反馈），无法执行飞行任务，请稍后重试 */
    public static final int DOCK_PULLING_LOG = 319019;
    /** 航线中断失败，请稍后重试 */
    public static final int ROUTE_INTERRUPT_FAIL = 319020;
    /** 退出远程控制失败，请稍后重试 */
    public static final int EXIT_REMOTE_CONTROL_FAIL = 319021;
    /** 指点飞行失败，请稍后重试 */
    public static final int FLY_TO_FAIL_319022 = 319022;
    /** 指点飞行停止失败，请稍后重试 */
    public static final int FLY_TO_STOP_FAIL = 319023;
    /** 一键起飞失败，请稍后重试 */
    public static final int ONE_KEY_TAKEOFF_FAIL = 319024;
    /** 机场未准备完成，无法执行云端下发的飞行任务，请稍后重试 */
    public static final int DOCK_NOT_READY = 319025;
    /** 飞行器电池电量低于用户设置的任务开始执行的电量，请等待充电完成后再执行飞行任务 */
    public static final int BATTERY_LOW_FOR_TASK_START = 319026;
    /** 机场或飞行器剩余存储容量过低，无法执行飞行任务，请等待媒体文件上传，机场和飞行器存储容量释放后再执行飞行任务 */
    public static final int STORAGE_LOW_CANNOT_FLY = 319027;
    /** 正在更新自定义飞行区 */
    public static final int UPDATING_CUSTOM_FLIGHT_AREA = 319028;
    /** 正在更新离线地图 */
    public static final int UPDATING_OFFLINE_MAP = 319029;
    /** 操作失败，无飞行器控制权 */
    public static final int NO_AIRCRAFT_CONTROL = 319030;
    /** 控制权异常，请刷新重试 */
    public static final int CONTROL_ABNORMAL_REFRESH = 319031;
    /** 指点飞行失败，请稍后重试 */
    public static final int FLY_TO_FAIL_319032 = 319032;
    /** 虚拟摇杆操作失败，请稍后重试 */
    public static final int VIRTUAL_STICK_FAIL_319033 = 319033;
    /** 虚拟摇杆操作失败，请稍后重试 */
    public static final int VIRTUAL_STICK_FAIL_319034 = 319034;
    /** 急停失败，请稍后重试 */
    public static final int EMERGENCY_STOP_FAIL = 319035;
    /** 设备远程调试中，请稍后重试 */
    public static final int REMOTE_DEBUGGING = 319036;
    /** 设备本地调试中，请稍后重试 */
    public static final int LOCAL_DEBUGGING = 319037;
    /** 设备正在升级，请稍后重试 */
    public static final int DEVICE_UPGRADING = 319038;
    /** 航线恢复失败，请稍后重试 */
    public static final int ROUTE_RESUME_FAIL = 319042;
    /** 取消返航失败，请稍后重试 */
    public static final int CANCEL_RTH_FAIL = 319043;
    /** 航线任务已结束，无法恢复 */
    public static final int ROUTE_ENDED_CANNOT_RESUME = 319044;
    /** 急停成功，请重新按键操作 */
    public static final int EMERGENCY_STOP_SUCCESS_REPRESS = 319045;
    /** 无法暂停航线，飞行器尚未进入航线或已退出航线 */
    public static final int CANNOT_PAUSE_ROUTE_NOT_IN_ROUTE = 319046;
    /** 机场系统运行异常，请重启机场后重试 */
    public static final int DOCK_SYSTEM_ABNORMAL_319999 = 319999;

    // ===== 321xxx 航线执行 =====
    /** 航线执行异常，请稍后重试或重启机场后重试 */
    public static final int ROUTE_EXECUTION_ABNORMAL = 321000;
    /** 航线文件解析失败，无法执行飞行任务，请检查航线文件 */
    public static final int ROUTE_FILE_PARSE_FAIL_321004 = 321004;
    /** 航线缺少断点信息，机场无法执行飞行任务 */
    public static final int ROUTE_MISSING_BREAKPOINT_INFO = 321005;
    /** 飞行任务已在执行中，请勿重复执行 */
    public static final int TASK_ALREADY_EXECUTING = 321257;
    /** 飞行任务无法终止，请检查飞行器状态 */
    public static final int TASK_CANNOT_TERMINATE = 321258;
    /** 飞行任务未开始执行，无法终止飞行任务 */
    public static final int TASK_NOT_STARTED_CANNOT_TERMINATE = 321259;
    /** 飞行任务未开始执行，无法中断飞行任务 */
    public static final int TASK_NOT_STARTED_CANNOT_INTERRUPT = 321260;
    /** 航线规划高度已超过飞行器限高，机场无法执行飞行任务 */
    public static final int ROUTE_HEIGHT_EXCEEDS_LIMIT = 321513;
    /** 任务失败，起点或终点位于限远区域的缓冲区内或超过了限远距离 */
    public static final int START_END_IN_LIMIT_BUFFER_OR_EXCEED = 321514;
    /** 航线穿过限飞区，机场无法执行飞行任务 */
    public static final int ROUTE_THROUGH_NO_FLY_ZONE = 321515;
    /** 飞行器飞行高度过低，飞行任务执行被终止 */
    public static final int FLIGHT_HEIGHT_TOO_LOW_TERMINATED = 321516;
    /** 飞行器触发避障，飞行任务执行被终止。为保证飞行安全，请勿用当前航线执行断点续飞任务 */
    public static final int OBSTACLE_AVOIDANCE_TERMINATED = 321517;
    /** 飞行器接近限飞区或限远距离自动返航，无法完成航线飞行 */
    public static final int NEAR_NO_FLY_ZONE_AUTO_RTH = 321519;
    /** 飞行器起飞失败，请稍后重试，如果仍报错请联系大疆售后 */
    public static final int TAKEOFF_FAIL_321523 = 321523;
    /** 飞行器起飞前准备失败，可能是飞行器无法定位或档位错误导致，请检查飞行器状态 */
    public static final int PREP_FAIL_CANNOT_LOCATE_OR_GEAR_ERROR = 321524;
    /** 触碰自定义飞行区边界，航线任务已暂停 */
    public static final int TOUCH_CUSTOM_FLIGHT_AREA_BOUNDARY = 321528;
    /** 目标点位于禁飞区域或者障碍物内，无法到达，航线任务已暂停，请重新规划后再试 */
    public static final int TARGET_IN_NO_FLY_OR_OBSTACLE = 321529;
    /** 飞行器飞行航线过程中轨迹规划失败，航线任务已暂停 */
    public static final int TRAJECTORY_PLANNING_FAIL = 321530;
    /** 进离场航线执行失败，请联系大疆售后 */
    public static final int ENTRY_EXIT_ROUTE_EXEC_FAIL_321531 = 321531;
    /** 进离场航线执行失败，请联系大疆售后 */
    public static final int ENTRY_EXIT_ROUTE_EXEC_FAIL_321532 = 321532;
    /** 进离场航线执行失败，请联系大疆售后 */
    public static final int ENTRY_EXIT_ROUTE_EXEC_FAIL_321533 = 321533;
    /** 飞行器卫星定位信号差，无法执行飞行任务，请重启机场后重试 */
    public static final int SATELLITE_SIGNAL_POOR = 321769;
    /** 飞行器挡位错误，无法执行飞行任务，请重启机场后重试 */
    public static final int GEAR_ERROR = 321770;
    /** 飞行器返航点未设置，无法执行飞行任务，请重启机场后重试 */
    public static final int RTH_POINT_NOT_SET = 321771;
    /** 飞行器电量低于30%，无法执行飞行任务，请充电后重试（建议电量≥50%） */
    public static final int BATTERY_LOW_30_321772 = 321772;
    /** 飞行器执行飞行任务过程中低电量返航，无法完成航线飞行 */
    public static final int LOW_BATTERY_RTH_DURING_TASK = 321773;
    /** 飞行器航线飞行过程中失联，无法完成航线飞行 */
    public static final int AIRCRAFT_LOST_CONTACT_DURING_ROUTE = 321775;
    /** 飞行器 RTK 收敛失败，无法执行飞行任务，请重启机场后重试 */
    public static final int RTK_CONVERGE_FAIL_321776 = 321776;
    /** 飞行器未悬停，无法开始执行飞行任务 */
    public static final int AIRCRAFT_NOT_HOVERING = 321777;
    /** 用户使用 B 控操控飞行器起桨，机场无法执行飞行任务 */
    public static final int B_CTRL_STARTING_PROPELLERS = 321778;
    /** 任务过程中遇到大风紧急返航 */
    public static final int STRONG_WIND_EMERGENCY_RTH = 321784;
    /** 任务失败，由于信号受到干扰，导致异常返航 */
    public static final int SIGNAL_INTERFERENCE_ABNORMAL_RTH = 321788;

    // ===== 322xxx 飞行任务中断 =====
    /** 任务失败，机场执行飞行任务过程被手动打断或异常终止 */
    public static final int TASK_INTERRUPTED_MANUAL_OR_ABNORMAL = 322281;
    /** 机场执行飞行任务过程中被中断，飞行器被云端用户或遥控器接管 */
    public static final int TASK_INTERRUPTED_TAKEOVER = 322282;
    /** 机场执行飞行任务过程中被用户触发返航，无法完成航线飞行 */
    public static final int TASK_INTERRUPTED_USER_RTH = 322283;
    /** 航线的断点信息错误，机场无法执行飞行任务 */
    public static final int ROUTE_BREAKPOINT_INFO_ERROR = 322539;
    /** 航线轨迹生成失败，请检查飞行器视觉镜头是否存在脏污或重启飞行器后再试，如果仍报错请联系大疆售后 */
    public static final int TRAJECTORY_GENERATION_FAIL = 322563;

    // ===== 324xxx 远程日志 =====
    /** 日志压缩过程超时，所选日志过多，请减少选择的日志后重试 */
    public static final int LOG_COMPRESS_TIMEOUT_TOO_MANY = 324012;
    /** 设备日志列表获取失败，请稍后重试 */
    public static final int LOG_LIST_GET_FAIL = 324013;
    /** 设备日志列表为空，请刷新页面或重启机场后重试 */
    public static final int LOG_LIST_EMPTY = 324014;
    /** 飞行器已关机或未连接，无法获取日志列表，请确认飞行器在舱内，通过远程调试将飞行器开机后重试 */
    public static final int AIRCRAFT_OFF_CANNOT_GET_LOG = 324015;
    /** 机场存储空间不足，日志压缩失败，请清理机场存储空间或稍后重试 */
    public static final int DOCK_STORAGE_INSUFFICIENT_LOG_COMPRESS = 324016;
    /** 日志压缩失败，无法获取所选飞行器日志，请刷新页面或重启机场后重试 */
    public static final int LOG_COMPRESS_FAIL = 324017;
    /** 日志文件拉取失败，导致本次设备异常反馈上传失败，请稍后重试或重启机场后重试 */
    public static final int LOG_PULL_FAIL = 324018;
    /** 因机场网络异常，日志上传失败，请稍后重试。如果连续多次出现该问题，请联系代理商或大疆售后进行网络排障 */
    public static final int LOG_UPLOAD_FAIL_NETWORK = 324019;
    /** 因机场断电或重启导致日志导出中断，日志导出失败，请稍后重试 */
    public static final int LOG_EXPORT_INTERRUPTED_POWER_OR_RESTART = 324021;
    /** 因机场网络异常、飞行器图传链路异常等原因，媒体文件暂时无法上传或文件已上传但云端读取失败 */
    public static final int MEDIA_UPLOAD_FAIL_OR_CLOUD_READ_FAIL = 324030;

    // ===== 325xxx 命令处理 =====
    /** 云端下发命令不符合格式要求，设备无法执行 */
    public static final int COMMAND_FORMAT_INVALID = 325001;
    /** 指令响应失败，请重试 */
    public static final int COMMAND_RESPONSE_FAIL = 325003;
    /** 设备端命令请求已超时 */
    public static final int DEVICE_COMMAND_REQUEST_TIMEOUT = 325004;

    // ===== 513xxx 直播 =====
    /** 相机不存在或相机类型错误 */
    public static final int CAMERA_NOT_EXIST_OR_WRONG_TYPE = 513002;
    /** 相机正在直播中 */
    public static final int CAMERA_ALREADY_LIVESTREAMING = 513003;
    /** 直播清晰度设置错误 */
    public static final int LIVESTREAM_DEFINITION_ERROR = 513005;
    /** 直播启动失败 */
    public static final int LIVESTREAM_START_FAIL = 513006;
    /** 图传数据异常 */
    public static final int IMAGE_TRANSMISSION_DATA_ABNORMAL = 513008;
    /** 设备无法连接网络 */
    public static final int DEVICE_CANNOT_CONNECT_NETWORK = 513010;
    /** 直播未开启 */
    public static final int LIVESTREAM_NOT_ENABLED = 513011;
    /** 直播中无法切换镜头 */
    public static final int CANNOT_SWITCH_LENS_DURING_LIVESTREAM = 513012;
    /** 视频传输协议不支持 */
    public static final int VIDEO_PROTOCOL_NOT_SUPPORTED = 513013;
    /** 直播参数错误或不完整 */
    public static final int LIVESTREAM_PARAM_ERROR_OR_INCOMPLETE = 513014;
    /** 检测到网络卡顿 */
    public static final int NETWORK_LAG_DETECTED = 513015;
    /** 视频解码失败 */
    public static final int VIDEO_DECODE_FAIL = 513016;
    /** 文件下载期间直播暂停 */
    public static final int LIVESTREAM_PAUSED_DURING_DOWNLOAD = 513017;

    // ===== 查表 =====
    private static final Map<Integer, DjiErrorInfo> CODE_TABLE = Map.ofEntries(
        // 通用
        Map.entry(0, new DjiErrorInfo(0, "成功")),
        Map.entry(1, new DjiErrorInfo(1, "失败（通用）")),
        // HTTP API 注册绑定
        Map.entry(210229, new DjiErrorInfo(210229, "组织 ID 与绑定码错误")),
        Map.entry(210234, new DjiErrorInfo(210234, "组织不存在")),
        Map.entry(210235, new DjiErrorInfo(210235, "设备已绑定到其他组织")),
        // 312xxx 设备操作
        Map.entry(312014, new DjiErrorInfo(312014, "设备升级中，请勿重复操作")),
        Map.entry(312015, new DjiErrorInfo(312015, "机场业务繁忙无法进行设备升级，请等待机场处于空闲中后再试")),
        Map.entry(312016, new DjiErrorInfo(312016, "升级失败，机场和飞行器图传链路异常，请重启机场和飞行器后重试")),
        Map.entry(312022, new DjiErrorInfo(312022, "飞行器开机失败或未连接，请检查飞行器是否在舱内，是否安装电池，机场和飞行器是否已对频")),
        Map.entry(312023, new DjiErrorInfo(312023, "推杆闭合失败无法升级飞行器，请检查急停按钮是否被按下，推杆是否有异物卡住")),
        Map.entry(312027, new DjiErrorInfo(312027, "升级失败，机场未检测到飞行器")),
        Map.entry(312028, new DjiErrorInfo(312028, "升级失败，设备升级过程中设备被重启")),
        Map.entry(312029, new DjiErrorInfo(312029, "设备重启中无法进行设备升级，请等待设备重启完成后重试")),
        Map.entry(312030, new DjiErrorInfo(312030, "升级失败，飞行器增强图传开启后无法升级，请关闭飞行器增强图传后重试")),
        Map.entry(312704, new DjiErrorInfo(312704, "设备电量过低，请充电至20%以上后重试")),
        // 314xxx 飞行任务准备
        Map.entry(314000, new DjiErrorInfo(314000, "设备当前无法支持该操作，建议检查设备当前工作状态")),
        Map.entry(314001, new DjiErrorInfo(314001, "飞行任务下发失败，请稍后重试")),
        Map.entry(314002, new DjiErrorInfo(314002, "飞行任务下发失败，请稍后重试")),
        Map.entry(314003, new DjiErrorInfo(314003, "航线文件格式不兼容，请检查航线文件是否正确")),
        Map.entry(314005, new DjiErrorInfo(314005, "飞行任务下发失败，请稍后重试或重启机场后重试")),
        Map.entry(314006, new DjiErrorInfo(314006, "飞行器初始化失败，请重启机场后重试")),
        Map.entry(314007, new DjiErrorInfo(314007, "机场传输航线至飞行器失败，请重启机场后重试")),
        Map.entry(314008, new DjiErrorInfo(314008, "飞行器起飞前准备超时，请重启机场后重试")),
        Map.entry(314009, new DjiErrorInfo(314009, "飞行器初始化失败，请重启机场后重试")),
        Map.entry(314010, new DjiErrorInfo(314010, "航线执行失败，请重启机场后重试")),
        Map.entry(314011, new DjiErrorInfo(314011, "机场系统异常，无法获取飞行任务执行结果")),
        Map.entry(314012, new DjiErrorInfo(314012, "飞行器起飞前准备失败，无法执行飞行任务，请重启机场后重试")),
        Map.entry(314013, new DjiErrorInfo(314013, "飞行任务下发失败，机场无法获取到本次飞行任务的航线，无法执行飞行任务，请稍后重试")),
        Map.entry(314014, new DjiErrorInfo(314014, "机场系统异常，飞行任务执行失败，请稍后重试")),
        Map.entry(314015, new DjiErrorInfo(314015, "机场传输精准复拍航线至飞行器失败，无法执行飞行任务，请稍后重试或重启机场后重试")),
        Map.entry(314016, new DjiErrorInfo(314016, "航线文件解析失败，无法执行飞行任务，请检查航线文件")),
        Map.entry(314017, new DjiErrorInfo(314017, "航线文件解析失败，请检查航线后再试")),
        Map.entry(314018, new DjiErrorInfo(314018, "飞行器 RTK 定位异常，无法执行飞行任务，请稍后重试或重启机场后重试")),
        Map.entry(314019, new DjiErrorInfo(314019, "飞行器 RTK 收敛失败，无法执行飞行任务，请稍后重试或重启机场后重试")),
        Map.entry(314020, new DjiErrorInfo(314020, "飞行器不在停机坪正中间或飞行器朝向不正确，无法执行飞行任务，请检查飞行器位置和朝向")),
        Map.entry(314021, new DjiErrorInfo(314021, "飞行器 RTK 定位异常，无法执行飞行任务，请稍后重试或重启机场后重试")),
        Map.entry(314024, new DjiErrorInfo(314024, "进离场航线下发失败，请稍后重试或重启机场后重试")),
        Map.entry(314025, new DjiErrorInfo(314025, "RTK收敛超时，用户手动取消任务")),
        Map.entry(314200, new DjiErrorInfo(314200, "任务失败，由于机场网络断开，飞行器已自动返航，请确保机场已连接网络后再试")),
        // 315xxx 通信与系统错误
        Map.entry(315000, new DjiErrorInfo(315000, "机场通信异常，请重启机场后重试")),
        Map.entry(315001, new DjiErrorInfo(315001, "机场通信异常，请远程开启飞机并等待 1min 后，再次下发任务重试")),
        Map.entry(315002, new DjiErrorInfo(315002, "机场通信异常，请重启机场后重试")),
        Map.entry(315003, new DjiErrorInfo(315003, "机场通信异常，请重启机场后重试")),
        Map.entry(315004, new DjiErrorInfo(315004, "任务失败，请等待两个机场都空闲后，再次下发任务重试")),
        Map.entry(315005, new DjiErrorInfo(315005, "机场通信异常，请重启机场后重试")),
        Map.entry(315006, new DjiErrorInfo(315006, "机场通信异常，请重启机场后重试")),
        Map.entry(315007, new DjiErrorInfo(315007, "机场通信异常，请将机场升级到最新版本或重启机场后重试")),
        Map.entry(315008, new DjiErrorInfo(315008, "降落机场和起飞机场标定信息不一致，请确认两个机场均链路通畅且使用了相同的网络信息标定")),
        Map.entry(315009, new DjiErrorInfo(315009, "机场通信异常，请重启机场后重试")),
        Map.entry(315010, new DjiErrorInfo(315010, "无法停止飞行任务，请稍后重试，如果仍报错请联系大疆售后")),
        Map.entry(315011, new DjiErrorInfo(315011, "无法停止飞行任务，请稍后重试，如果仍报错请联系大疆售后")),
        Map.entry(315012, new DjiErrorInfo(315012, "无法停止飞行任务，请稍后重试，如果仍报错请联系大疆售后")),
        Map.entry(315013, new DjiErrorInfo(315013, "飞行任务下发失败，请稍后重试，如果仍报错请联系大疆售后")),
        Map.entry(315014, new DjiErrorInfo(315014, "当前任务类型不支持设置返航点")),
        Map.entry(315015, new DjiErrorInfo(315015, "返航点设置失败，请稍后重试，如果仍报错请联系大疆售后")),
        Map.entry(315016, new DjiErrorInfo(315016, "飞行任务下发失败，请稍后重试，如果仍报错请联系大疆售后")),
        Map.entry(315017, new DjiErrorInfo(315017, "飞行任务下发失败，请稍后重试，如果仍报错请联系大疆售后")),
        Map.entry(315018, new DjiErrorInfo(315018, "任务失败，请等待两个机场都空闲后，再次下发任务重试")),
        Map.entry(315019, new DjiErrorInfo(315019, "设备部署位置不佳，无法执行蛙跳任务，请选择其它机场再试")),
        Map.entry(315050, new DjiErrorInfo(315050, "机场系统异常，请重启机场后重试")),
        Map.entry(315051, new DjiErrorInfo(315051, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        Map.entry(315052, new DjiErrorInfo(315052, "机场位置未收敛，请等待一段时间后重试")),
        Map.entry(315053, new DjiErrorInfo(315053, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        Map.entry(315054, new DjiErrorInfo(315054, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        Map.entry(315055, new DjiErrorInfo(315055, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        Map.entry(315056, new DjiErrorInfo(315056, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        Map.entry(315057, new DjiErrorInfo(315057, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        Map.entry(315058, new DjiErrorInfo(315058, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        Map.entry(315059, new DjiErrorInfo(315059, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        Map.entry(315060, new DjiErrorInfo(315060, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        Map.entry(315061, new DjiErrorInfo(315061, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        Map.entry(315062, new DjiErrorInfo(315062, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        Map.entry(315063, new DjiErrorInfo(315063, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        Map.entry(315064, new DjiErrorInfo(315064, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        Map.entry(315065, new DjiErrorInfo(315065, "任务失败，请重启机场并再次下发任务后重试，如果仍报错请联系大疆售后")),
        // 316xxx 飞行器与环境错误
        Map.entry(316001, new DjiErrorInfo(316001, "飞行器参数配置失败，请重启机场后重试")),
        Map.entry(316002, new DjiErrorInfo(316002, "飞行器参数配置失败，请重启机场后重试")),
        Map.entry(316003, new DjiErrorInfo(316003, "飞行器参数配置失败，请重启机场后重试")),
        Map.entry(316004, new DjiErrorInfo(316004, "飞行器参数配置失败，请重启机场后重试")),
        Map.entry(316005, new DjiErrorInfo(316005, "飞行器 RTK 收敛失败，无法执行飞行任务，请重启机场后重试")),
        Map.entry(316006, new DjiErrorInfo(316006, "任务超时，飞行器已丢失或降落时机场未开启舱盖或展开推杆，飞行器无法降落回机场，请尽快至机场部署现场检查飞行器状况")),
        Map.entry(316007, new DjiErrorInfo(316007, "飞行器初始化失败，请重启机场后重试")),
        Map.entry(316008, new DjiErrorInfo(316008, "机场获取飞行器控制权失败，无法执行飞行任务，请确认遥控器未锁定控制权")),
        Map.entry(316009, new DjiErrorInfo(316009, "飞行器电量低于30%，无法执行飞行任务，请充电后重试（建议电量≥50%）")),
        Map.entry(316010, new DjiErrorInfo(316010, "机场未检测到飞行器，无法执行飞行任务，请检查舱内是否有飞行器，机场与飞行器是否已对频，或重启机场后重试")),
        Map.entry(316011, new DjiErrorInfo(316011, "飞行器降落位置偏移过大，请检查飞行器是否需要现场摆正")),
        Map.entry(316012, new DjiErrorInfo(316012, "飞行器起飞前准备失败，无法执行飞行任务，请重启机场后重试")),
        Map.entry(316013, new DjiErrorInfo(316013, "飞行器起飞前准备失败，无法执行飞行任务，请重启机场后重试")),
        Map.entry(316014, new DjiErrorInfo(316014, "飞行器起飞前准备失败，无法执行飞行任务，请重启机场后重试")),
        Map.entry(316015, new DjiErrorInfo(316015, "飞行器 RTK 收敛位置距离机场过远，无法执行飞行任务，请重启机场后重试")),
        Map.entry(316016, new DjiErrorInfo(316016, "飞行器降落至机场超时，可能是机场与飞行器断连导致，请通过直播查看飞行器是否降落至舱内")),
        Map.entry(316017, new DjiErrorInfo(316017, "获取飞行器媒体数量超时，可能是机场与飞行器断连导致，请通过直播查看飞行器是否降落至舱内")),
        Map.entry(316018, new DjiErrorInfo(316018, "飞行任务执行超时，可能是机场与飞行器断连导致，请通过直播查看飞行器是否降落至舱内")),
        Map.entry(316019, new DjiErrorInfo(316019, "机场系统错误，无法执行飞行任务，请稍后重试")),
        Map.entry(316020, new DjiErrorInfo(316020, "飞行器使用的 RTK 信号源错误，请稍后重试")),
        Map.entry(316021, new DjiErrorInfo(316021, "飞行器 RTK 信号源检查超时，请稍后重试")),
        Map.entry(316022, new DjiErrorInfo(316022, "飞行器无法执行返航指令，请检查飞行器是否已开机，机场与飞行器是否已断连，请确认无以上问题后重试")),
        Map.entry(316023, new DjiErrorInfo(316023, "飞行器无法执行返航指令，飞行器已被 B 控接管，请在 B 控操控飞行器，或关闭 B 控后重试")),
        Map.entry(316024, new DjiErrorInfo(316024, "飞行器执行返航指令失败，请检查飞行器是否已起飞，确认飞行器已起飞后请重试")),
        Map.entry(316025, new DjiErrorInfo(316025, "飞行器参数配置失败，请稍后重试或重启机场后重试")),
        Map.entry(316026, new DjiErrorInfo(316026, "机场急停按钮被按下，无法执行飞行任务，请释放急停按钮后重试")),
        Map.entry(316027, new DjiErrorInfo(316027, "飞行器参数配置超时，请稍后重试或重启机场后重试")),
        Map.entry(316029, new DjiErrorInfo(316029, "机场急停按钮被按下，飞行器将飞往备降点降落，请立即检查飞行器是否已安全降落并将飞行器放回至机场")),
        Map.entry(316032, new DjiErrorInfo(316032, "获取电池数据超时，请稍后重试或重启飞行器后重试")),
        Map.entry(316033, new DjiErrorInfo(316033, "飞行器电池循环次数过高，为保证飞行安全，已自动终止任务，建议更换该电池")),
        Map.entry(316034, new DjiErrorInfo(316034, "无法起飞，飞行器固件版本与机场固件版本不匹配，为保证飞行安全请升级固件后再试")),
        Map.entry(316035, new DjiErrorInfo(316035, "进离场航线下发失败，请确保设备固件为最新版本后重新下发任务，如果持续报错，请联系大疆售后")),
        Map.entry(316050, new DjiErrorInfo(316050, "飞行器因电量过低在舱外降落，请立即检查飞行器是否已安全降落并将飞行器放回至机场")),
        Map.entry(316051, new DjiErrorInfo(316051, "飞行任务异常，飞行器在舱外降落，请立即检查飞行器是否已安全降落并将飞行器放回至机场")),
        Map.entry(316052, new DjiErrorInfo(316052, "飞行任务异常，飞行器将飞往备降点降落，请立即检查飞行器是否已安全降落并将飞行器放回至机场")),
        Map.entry(316053, new DjiErrorInfo(316053, "用户已操控飞行器降落，请立即检查飞行器是否已安全降落并将飞行器放回至机场")),
        Map.entry(316100, new DjiErrorInfo(316100, "获取相机概要信息失败，请重试")),
        Map.entry(316101, new DjiErrorInfo(316101, "设置相机为单拍模式失败，请重试")),
        Map.entry(316102, new DjiErrorInfo(316102, "关闭相机水印失败，请重试")),
        Map.entry(316103, new DjiErrorInfo(316103, "设置测光模式到平均测光失败，请重试")),
        Map.entry(316104, new DjiErrorInfo(316104, "切换镜头到广角镜头失败，请重试")),
        Map.entry(316105, new DjiErrorInfo(316105, "设置相机存储照片失败，请重试")),
        Map.entry(316106, new DjiErrorInfo(316106, "红外变焦倍数设置失败，请重试")),
        Map.entry(316107, new DjiErrorInfo(316107, "照片尺寸设置4k失败，请重试")),
        Map.entry(316108, new DjiErrorInfo(316108, "设置照片存储格式为jpeg格式失败，请重试")),
        Map.entry(316109, new DjiErrorInfo(316109, "关闭相机畸变矫正失败，请重试")),
        Map.entry(316110, new DjiErrorInfo(316110, "打开相机机械快门失败，请重试")),
        Map.entry(316111, new DjiErrorInfo(316111, "设置对焦模式失败，请重试")),
        // 317xxx 媒体管理
        Map.entry(317001, new DjiErrorInfo(317001, "获取飞行器媒体文件数量失败，请重启机场后重试")),
        Map.entry(317002, new DjiErrorInfo(317002, "飞行器存储格式化失败，飞行器未开机、未连接或未检测到相机，请确认无以上问题后重试，或重启飞行器后重试")),
        Map.entry(317003, new DjiErrorInfo(317003, "飞行器存储格式化失败，请重启飞行器后重试")),
        Map.entry(317004, new DjiErrorInfo(317004, "机场媒体文件格式化失败，请稍后重试或重启机场后重试")),
        Map.entry(317005, new DjiErrorInfo(317005, "飞行器结束录像失败，本次飞行任务的媒体文件可能无法上传")),
        Map.entry(317006, new DjiErrorInfo(317006, "无法格式化，请等待当前飞行器媒体文件下载完成后再试")),
        Map.entry(317007, new DjiErrorInfo(317007, "获取媒体文件数量失败，请稍后重试，如本架次任务有媒体文件且持续报错可联系大疆售后")),
        // 319xxx 系统操作
        Map.entry(319001, new DjiErrorInfo(319001, "机场作业中或设备异常反馈上传日志中，无法执行飞行任务，请等待当前飞行任务或操作执行完成后重试")),
        Map.entry(319002, new DjiErrorInfo(319002, "机场系统运行异常，请重启机场后重试")),
        Map.entry(319003, new DjiErrorInfo(319003, "机场系统运行异常，请重新下发任务")),
        Map.entry(319004, new DjiErrorInfo(319004, "飞行任务执行超时，已自动终止本次飞行任务")),
        Map.entry(319005, new DjiErrorInfo(319005, "云端与机场通信异常，无法执行飞行任务")),
        Map.entry(319006, new DjiErrorInfo(319006, "取消飞行任务失败，飞行任务已经在执行中")),
        Map.entry(319007, new DjiErrorInfo(319007, "修改飞行任务失败，飞行任务已经在执行中")),
        Map.entry(319008, new DjiErrorInfo(319008, "机场时间与云端时间不同步，机场无法执行飞行任务")),
        Map.entry(319009, new DjiErrorInfo(319009, "飞行任务下发失败，请稍后重试或重启机场后重试")),
        Map.entry(319010, new DjiErrorInfo(319010, "机场固件版本过低，无法执行飞行任务，请升级机场固件为最新版本后重试")),
        Map.entry(319015, new DjiErrorInfo(319015, "机场正在初始化中，无法执行飞行任务，请等待机场初始化完成后重试")),
        Map.entry(319016, new DjiErrorInfo(319016, "机场正在执行其他飞行任务，无法执行本次飞行任务")),
        Map.entry(319017, new DjiErrorInfo(319017, "机场正在处理上次飞行任务媒体文件，无法执行本次飞行任务，请稍后重试")),
        Map.entry(319018, new DjiErrorInfo(319018, "机场正在自动导出日志中（设备异常反馈），无法执行飞行任务，请稍后重试")),
        Map.entry(319019, new DjiErrorInfo(319019, "机场正在拉取日志中（设备异常反馈），无法执行飞行任务，请稍后重试")),
        Map.entry(319020, new DjiErrorInfo(319020, "航线中断失败，请稍后重试")),
        Map.entry(319021, new DjiErrorInfo(319021, "退出远程控制失败，请稍后重试")),
        Map.entry(319022, new DjiErrorInfo(319022, "指点飞行失败，请稍后重试")),
        Map.entry(319023, new DjiErrorInfo(319023, "指点飞行停止失败，请稍后重试")),
        Map.entry(319024, new DjiErrorInfo(319024, "一键起飞失败，请稍后重试")),
        Map.entry(319025, new DjiErrorInfo(319025, "机场未准备完成，无法执行云端下发的飞行任务，请稍后重试")),
        Map.entry(319026, new DjiErrorInfo(319026, "飞行器电池电量低于用户设置的任务开始执行的电量，请等待充电完成后再执行飞行任务")),
        Map.entry(319027, new DjiErrorInfo(319027, "机场或飞行器剩余存储容量过低，无法执行飞行任务，请等待媒体文件上传，机场和飞行器存储容量释放后再执行飞行任务")),
        Map.entry(319028, new DjiErrorInfo(319028, "正在更新自定义飞行区")),
        Map.entry(319029, new DjiErrorInfo(319029, "正在更新离线地图")),
        Map.entry(319030, new DjiErrorInfo(319030, "操作失败，无飞行器控制权")),
        Map.entry(319031, new DjiErrorInfo(319031, "控制权异常，请刷新重试")),
        Map.entry(319032, new DjiErrorInfo(319032, "指点飞行失败，请稍后重试")),
        Map.entry(319033, new DjiErrorInfo(319033, "虚拟摇杆操作失败，请稍后重试")),
        Map.entry(319034, new DjiErrorInfo(319034, "虚拟摇杆操作失败，请稍后重试")),
        Map.entry(319035, new DjiErrorInfo(319035, "急停失败，请稍后重试")),
        Map.entry(319036, new DjiErrorInfo(319036, "设备远程调试中，请稍后重试")),
        Map.entry(319037, new DjiErrorInfo(319037, "设备本地调试中，请稍后重试")),
        Map.entry(319038, new DjiErrorInfo(319038, "设备正在升级，请稍后重试")),
        Map.entry(319042, new DjiErrorInfo(319042, "航线恢复失败，请稍后重试")),
        Map.entry(319043, new DjiErrorInfo(319043, "取消返航失败，请稍后重试")),
        Map.entry(319044, new DjiErrorInfo(319044, "航线任务已结束，无法恢复")),
        Map.entry(319045, new DjiErrorInfo(319045, "急停成功，请重新按键操作")),
        Map.entry(319046, new DjiErrorInfo(319046, "无法暂停航线，飞行器尚未进入航线或已退出航线")),
        Map.entry(319999, new DjiErrorInfo(319999, "机场系统运行异常，请重启机场后重试")),
        // 321xxx 航线执行
        Map.entry(321000, new DjiErrorInfo(321000, "航线执行异常，请稍后重试或重启机场后重试")),
        Map.entry(321004, new DjiErrorInfo(321004, "航线文件解析失败，无法执行飞行任务，请检查航线文件")),
        Map.entry(321005, new DjiErrorInfo(321005, "航线缺少断点信息，机场无法执行飞行任务")),
        Map.entry(321257, new DjiErrorInfo(321257, "飞行任务已在执行中，请勿重复执行")),
        Map.entry(321258, new DjiErrorInfo(321258, "飞行任务无法终止，请检查飞行器状态")),
        Map.entry(321259, new DjiErrorInfo(321259, "飞行任务未开始执行，无法终止飞行任务")),
        Map.entry(321260, new DjiErrorInfo(321260, "飞行任务未开始执行，无法中断飞行任务")),
        Map.entry(321513, new DjiErrorInfo(321513, "航线规划高度已超过飞行器限高，机场无法执行飞行任务")),
        Map.entry(321514, new DjiErrorInfo(321514, "任务失败，起点或终点位于限远区域的缓冲区内或超过了限远距离")),
        Map.entry(321515, new DjiErrorInfo(321515, "航线穿过限飞区，机场无法执行飞行任务")),
        Map.entry(321516, new DjiErrorInfo(321516, "飞行器飞行高度过低，飞行任务执行被终止")),
        Map.entry(321517, new DjiErrorInfo(321517, "飞行器触发避障，飞行任务执行被终止。为保证飞行安全，请勿用当前航线执行断点续飞任务")),
        Map.entry(321519, new DjiErrorInfo(321519, "飞行器接近限飞区或限远距离自动返航，无法完成航线飞行")),
        Map.entry(321523, new DjiErrorInfo(321523, "飞行器起飞失败，请稍后重试，如果仍报错请联系大疆售后")),
        Map.entry(321524, new DjiErrorInfo(321524, "飞行器起飞前准备失败，可能是飞行器无法定位或档位错误导致，请检查飞行器状态")),
        Map.entry(321528, new DjiErrorInfo(321528, "触碰自定义飞行区边界，航线任务已暂停")),
        Map.entry(321529, new DjiErrorInfo(321529, "目标点位于禁飞区域或者障碍物内，无法到达，航线任务已暂停，请重新规划后再试")),
        Map.entry(321530, new DjiErrorInfo(321530, "飞行器飞行航线过程中轨迹规划失败，航线任务已暂停")),
        Map.entry(321531, new DjiErrorInfo(321531, "进离场航线执行失败，请联系大疆售后")),
        Map.entry(321532, new DjiErrorInfo(321532, "进离场航线执行失败，请联系大疆售后")),
        Map.entry(321533, new DjiErrorInfo(321533, "进离场航线执行失败，请联系大疆售后")),
        Map.entry(321769, new DjiErrorInfo(321769, "飞行器卫星定位信号差，无法执行飞行任务，请重启机场后重试")),
        Map.entry(321770, new DjiErrorInfo(321770, "飞行器挡位错误，无法执行飞行任务，请重启机场后重试")),
        Map.entry(321771, new DjiErrorInfo(321771, "飞行器返航点未设置，无法执行飞行任务，请重启机场后重试")),
        Map.entry(321772, new DjiErrorInfo(321772, "飞行器电量低于30%，无法执行飞行任务，请充电后重试（建议电量≥50%）")),
        Map.entry(321773, new DjiErrorInfo(321773, "飞行器执行飞行任务过程中低电量返航，无法完成航线飞行")),
        Map.entry(321775, new DjiErrorInfo(321775, "飞行器航线飞行过程中失联，无法完成航线飞行")),
        Map.entry(321776, new DjiErrorInfo(321776, "飞行器 RTK 收敛失败，无法执行飞行任务，请重启机场后重试")),
        Map.entry(321777, new DjiErrorInfo(321777, "飞行器未悬停，无法开始执行飞行任务")),
        Map.entry(321778, new DjiErrorInfo(321778, "用户使用 B 控操控飞行器起桨，机场无法执行飞行任务")),
        Map.entry(321784, new DjiErrorInfo(321784, "任务过程中遇到大风紧急返航")),
        Map.entry(321788, new DjiErrorInfo(321788, "任务失败，由于信号受到干扰，导致异常返航")),
        // 322xxx 飞行任务中断
        Map.entry(322281, new DjiErrorInfo(322281, "任务失败，机场执行飞行任务过程被手动打断或异常终止")),
        Map.entry(322282, new DjiErrorInfo(322282, "机场执行飞行任务过程中被中断，飞行器被云端用户或遥控器接管")),
        Map.entry(322283, new DjiErrorInfo(322283, "机场执行飞行任务过程中被用户触发返航，无法完成航线飞行")),
        Map.entry(322539, new DjiErrorInfo(322539, "航线的断点信息错误，机场无法执行飞行任务")),
        Map.entry(322563, new DjiErrorInfo(322563, "航线轨迹生成失败，请检查飞行器视觉镜头是否存在脏污或重启飞行器后再试，如果仍报错请联系大疆售后")),
        // 324xxx 远程日志
        Map.entry(324012, new DjiErrorInfo(324012, "日志压缩过程超时，所选日志过多，请减少选择的日志后重试")),
        Map.entry(324013, new DjiErrorInfo(324013, "设备日志列表获取失败，请稍后重试")),
        Map.entry(324014, new DjiErrorInfo(324014, "设备日志列表为空，请刷新页面或重启机场后重试")),
        Map.entry(324015, new DjiErrorInfo(324015, "飞行器已关机或未连接，无法获取日志列表，请确认飞行器在舱内，通过远程调试将飞行器开机后重试")),
        Map.entry(324016, new DjiErrorInfo(324016, "机场存储空间不足，日志压缩失败，请清理机场存储空间或稍后重试")),
        Map.entry(324017, new DjiErrorInfo(324017, "日志压缩失败，无法获取所选飞行器日志，请刷新页面或重启机场后重试")),
        Map.entry(324018, new DjiErrorInfo(324018, "日志文件拉取失败，导致本次设备异常反馈上传失败，请稍后重试或重启机场后重试")),
        Map.entry(324019, new DjiErrorInfo(324019, "因机场网络异常，日志上传失败，请稍后重试。如果连续多次出现该问题，请联系代理商或大疆售后进行网络排障")),
        Map.entry(324021, new DjiErrorInfo(324021, "因机场断电或重启导致日志导出中断，日志导出失败，请稍后重试")),
        Map.entry(324030, new DjiErrorInfo(324030, "因机场网络异常、飞行器图传链路异常等原因，媒体文件暂时无法上传或文件已上传但云端读取失败")),
        // 325xxx 命令处理
        Map.entry(325001, new DjiErrorInfo(325001, "云端下发命令不符合格式要求，设备无法执行")),
        Map.entry(325003, new DjiErrorInfo(325003, "指令响应失败，请重试")),
        Map.entry(325004, new DjiErrorInfo(325004, "设备端命令请求已超时")),
        // 513xxx 直播
        Map.entry(513002, new DjiErrorInfo(513002, "相机不存在或相机类型错误")),
        Map.entry(513003, new DjiErrorInfo(513003, "相机正在直播中")),
        Map.entry(513005, new DjiErrorInfo(513005, "直播清晰度设置错误")),
        Map.entry(513006, new DjiErrorInfo(513006, "直播启动失败")),
        Map.entry(513008, new DjiErrorInfo(513008, "图传数据异常")),
        Map.entry(513010, new DjiErrorInfo(513010, "设备无法连接网络")),
        Map.entry(513011, new DjiErrorInfo(513011, "直播未开启")),
        Map.entry(513012, new DjiErrorInfo(513012, "直播中无法切换镜头")),
        Map.entry(513013, new DjiErrorInfo(513013, "视频传输协议不支持")),
        Map.entry(513014, new DjiErrorInfo(513014, "直播参数错误或不完整")),
        Map.entry(513015, new DjiErrorInfo(513015, "检测到网络卡顿")),
        Map.entry(513016, new DjiErrorInfo(513016, "视频解码失败")),
        Map.entry(513017, new DjiErrorInfo(513017, "文件下载期间直播暂停"))
    );

    /**
     * 查询错误码的官方描述。
     *
     * @param code 错误码
     * @return 描述条目，未知码返回 {@link Optional#empty()}
     */
    public static Optional<DjiErrorInfo> describe(int code) {
        return Optional.ofNullable(CODE_TABLE.get(code));
    }
}
