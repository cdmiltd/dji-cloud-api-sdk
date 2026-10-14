package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 飞行器相机信息（Drone OSD cameras 数组元素）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code remain_photo_num} — 剩余拍照张数</li>
 *   <li>{@code remain_record_duration} — 剩余录像时间（秒）</li>
 *   <li>{@code record_time} — 视频录制时长（秒）</li>
 *   <li>{@code payload_index} — 负载编号，格式为 {type-subtype-gimbalindex}</li>
 *   <li>{@code camera_mode} — 相机模式（0=拍照, 1=录像）</li>
 *   <li>{@code photo_state} — 拍照状态（0=空闲, 1=拍照中）</li>
 *   <li>{@code screen_split_enable} — 分屏是否使能</li>
 *   <li>{@code recording_state} — 录像状态（0=空闲, 1=录像中）</li>
 *   <li>{@code zoom_factor} — 变焦倍数（2-200）</li>
 *   <li>{@code ir_zoom_factor} — 红外变焦倍数（2-20）</li>
 *   <li>{@code liveview_world_region} — 视场角在 liveview 中的区域</li>
 * </ul>
 *
 * <p>v1.18.0.0 起按 M4D/M4TD 设备属性页补全 cameras[] 元素字段：
 * {@code photo_storage_settings}/{@code video_storage_settings}（存储设置集合）、
 * {@code wide_*}（广角镜头 4 项）、{@code zoom_*}（变焦镜头 8 项）、
 * {@code ir_metering_mode}/{@code ir_metering_point}/{@code ir_metering_area}（红外测温 3 项）。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DroneOsd#cameras()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m4d-properties.html")
@Verified(basis = "DJI Cloud API 官方文档 M4D/M4TD 设备属性 cameras[] 元素字段（31 字段，含 20 项扩增）")
public record CameraInfo(
        /** 剩余拍照张数 */
        Integer remainPhotoNum,

        /** 剩余录像时间（秒） */
        Integer remainRecordDuration,

        /** 视频录制时长（秒） */
        Integer recordTime,

        /** 负载编号，格式为 {type-subtype-gimbalindex} */
        String payloadIndex,

        /** 相机模式（0=拍照, 1=录像） */
        Integer cameraMode,

        /** 拍照状态（0=空闲, 1=拍照中） */
        Integer photoState,

        /** 分屏是否使能 */
        Boolean screenSplitEnable,

        /** 录像状态（0=空闲, 1=录像中） */
        Integer recordingState,

        /** 变焦倍数（2-200） */
        Double zoomFactor,

        /** 红外变焦倍数（2-20） */
        Double irZoomFactor,

        /** 视场角在 liveview 中的区域 */
        LiveviewWorldRegion liveviewWorldRegion,

        /** 照片存储设置集合，取值范围 {current, vision, ir} */
        List<String> photoStorageSettings,

        /** 视频存储设置集合，取值范围 {current, vision, ir} */
        List<String> videoStorageSettings,

        /** 广角镜头曝光模式（1=自动, 2=快门优先曝光, 3=光圈优先曝光, 4=手动曝光） */
        Integer wideExposureMode,

        /** 广角镜头感光度（enum_int，0=Auto ... 255=FIXED） */
        Integer wideIso,

        /** 广角镜头快门速度（enum_int，0=1/8000s ... 65534=Auto） */
        Integer wideShutterSpeed,

        /** 广角镜头曝光值（enum_int，1=-5.0EV ... 255=FIXED） */
        Integer wideExposureValue,

        /** 变焦镜头曝光模式（1=自动, 2=快门优先曝光, 3=光圈优先曝光, 4=手动曝光） */
        Integer zoomExposureMode,

        /** 变焦镜头感光度（enum_int，0=Auto ... 255=FIXED） */
        Integer zoomIso,

        /** 变焦镜头快门速度（enum_int，0=1/8000s ... 65534=Auto） */
        Integer zoomShutterSpeed,

        /** 变焦镜头曝光值（enum_int，1=-5.0EV ... 255=FIXED） */
        Integer zoomExposureValue,

        /** 变焦镜头对焦模式（0=MF, 1=AFS, 2=AFC） */
        Integer zoomFocusMode,

        /** 变焦镜头对焦值 */
        Integer zoomFocusValue,

        /** 变焦镜头最大对焦值 */
        Integer zoomMaxFocusValue,

        /** 变焦镜头最小对焦值 */
        Integer zoomMinFocusValue,

        /** 变焦镜头标定的最远对焦值 */
        Integer zoomCalibrateFarthestFocusValue,

        /** 变焦镜头标定的最近对焦值 */
        Integer zoomCalibrateNearestFocusValue,

        /** 变焦镜头对焦状态（0=空闲, 1=对焦中, 2=对焦成功, 3=对焦失败） */
        Integer zoomFocusState,

        /** 红外测温模式（0=关闭测温, 1=点测温, 2=区域测温） */
        Integer irMeteringMode,

        /** 红外测温点 */
        IrMeteringPoint irMeteringPoint,

        /** 红外测温区域 */
        IrMeteringArea irMeteringArea
) {
    /**
     * 视场角（FOV）在 liveview 中的区域。
     *
     * <p>变焦相机的视场角相对于广角相机或红外相机的视场角，在 liveview 中会有所不同。
     * 坐标原点为镜头左上角。
     *
     * <p>DJI 文档字段：
     * <ul>
     *   <li>{@code left} — 左上角的 x 轴起始点（0-1）</li>
     *   <li>{@code top} — 左上角的 y 轴起始点（0-1）</li>
     *   <li>{@code right} — 右下角的 x 轴起始点（0-1）</li>
     *   <li>{@code bottom} — 右下角的 y 轴起始点（0-1）</li>
     * </ul>
     */
    public record LiveviewWorldRegion(
            /** 左上角的 x 轴起始点（0-1） */
            Double left,

            /** 左上角的 y 轴起始点（0-1） */
            Double top,

            /** 右下角的 x 轴起始点（0-1） */
            Double right,

            /** 右下角的 y 轴起始点（0-1） */
            Double bottom
    ) {
    }
}
