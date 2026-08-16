package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 媒体文件上传细节（Dock 专属）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code remain_upload} — 待上传数量</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.telemetry.DockOsd#mediaFileDetail()
 */
@Verified(basis = "DJI Cloud API 官方文档机场设备属性 media_file_detail 字段")
public record MediaFileDetail(
        /** 待上传数量 */
        Integer remainUpload
) {
}
