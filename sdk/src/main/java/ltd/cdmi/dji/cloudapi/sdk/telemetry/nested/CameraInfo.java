package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

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
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DroneOsd#cameras()
 */
@Verified(basis = "DJI Cloud API 官方文档飞行器设备属性 cameras 字段")
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
        LiveviewWorldRegion liveviewWorldRegion
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
