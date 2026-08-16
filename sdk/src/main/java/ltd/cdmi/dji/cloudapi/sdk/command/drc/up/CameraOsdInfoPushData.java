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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.up;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * {@code drc_camera_osd_info_push} 推送数据：摄像头 OSD 信息（payload_index + 各镜头参数 + 测距 + 直播视图）。
 *
 * <p>字段集依据 simulator
 * <a href="file:///d:/99.Code/hivemind-simulator/src/main/java/ltd/cdmi/hivemind/simulator/device/DeviceSimulator.java#L431-L494">
 * DeviceSimulator.buildDrcCameraOsdInfo()</a> 已对接 hivemind 验证。
 *
 * <p>注：本推送为摄像头 OSD（含镜头曝光/焦距/测距/红外参数），与 services 通道的
 * {@code DeviceOsdPushData}（飞行器位置/姿态 OSD）不同。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html">
 * DJI Dock3 DRC drc_camera_osd_info_push</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/drc/dock-drc.html")
@Verified(basis = "simulator DeviceSimulator.buildDrcCameraOsdInfo 已对接 hivemind 验证")
public record CameraOsdInfoPushData(
        /** 相机挂载索引（如 {@code 165-0-7}） */
        String payloadIndex,
        /** 广角镜头参数 */
        WideLense wideLense,
        /** 变焦镜头参数 */
        ZoomLense zoomLense,
        /** 激光测距信息 */
        MeasureTarget measureTarget,
        /** 红外镜头参数 */
        IrLense irLense,
        /** 直播视图区域 */
        Liveview liveview
) {
    /**
     * 广角镜头参数。
     *
     * @param wideExposureMode  曝光模式
     * @param wideIso           ISO
     * @param wideShutterSpeed  快门速度
     * @param wideExposureValue 曝光值
     * @param wideApertureValue 光圈值
     */
    public record WideLense(
            int wideExposureMode,
            int wideIso,
            int wideShutterSpeed,
            int wideExposureValue,
            int wideApertureValue
    ) {}

    /**
     * 变焦镜头参数。
     *
     * @param zoomExposureMode              曝光模式
     * @param zoomIso                       ISO
     * @param zoomShutterSpeed              快门速度
     * @param zoomExposureValue             曝光值
     * @param zoomFocusMode                 对焦模式
     * @param zoomFocusValue                当前对焦值
     * @param zoomMaxFocusValue             最大对焦值
     * @param zoomMinFocusValue             最小对焦值
     * @param zoomCalibrateFarthestFocusValue 校准最远对焦值
     * @param zoomCalibrateNearestFocusValue  校准最近对焦值
     * @param zoomFocusState                对焦状态
     * @param zoomFactor                    变焦倍数
     * @param zoomApertureValue             光圈值
     */
    public record ZoomLense(
            int zoomExposureMode,
            int zoomIso,
            int zoomShutterSpeed,
            int zoomExposureValue,
            int zoomFocusMode,
            int zoomFocusValue,
            int zoomMaxFocusValue,
            int zoomMinFocusValue,
            int zoomCalibrateFarthestFocusValue,
            int zoomCalibrateNearestFocusValue,
            int zoomFocusState,
            double zoomFactor,
            int zoomApertureValue
    ) {}

    /**
     * 激光测距信息。
     *
     * @param measureTargetLongitude   测距目标经度
     * @param measureTargetLatitude    测距目标纬度
     * @param measureTargetAltitude    测距目标海拔高度
     * @param measureTargetDistance    测距目标距离（米）
     * @param measureTargetErrorState  测距错误状态（0=正常, 1=异常）
     */
    public record MeasureTarget(
            double measureTargetLongitude,
            double measureTargetLatitude,
            double measureTargetAltitude,
            double measureTargetDistance,
            int measureTargetErrorState
    ) {}

    /**
     * 红外镜头参数。
     *
     * @param screenSplitEnable            屏幕分屏启用
     * @param irZoomFactor                 红外变焦倍数
     * @param thermalCurrentPaletteStyle   热成像调色板样式
     * @param thermalGainMode              热成像增益模式
     * @param thermalIsothermState         等温线状态
     * @param thermalIsothermUpperLimit     等温线上限温度
     * @param thermalIsothermLowerLimit     等温线下限温度
     * @param thermalGlobalTemperatureMin  全局最低温度
     * @param thermalGlobalTemperatureMax  全局最高温度
     */
    public record IrLense(
            boolean screenSplitEnable,
            int irZoomFactor,
            int thermalCurrentPaletteStyle,
            int thermalGainMode,
            int thermalIsothermState,
            int thermalIsothermUpperLimit,
            int thermalIsothermLowerLimit,
            double thermalGlobalTemperatureMin,
            double thermalGlobalTemperatureMax
    ) {}

    /**
     * 直播视图区域。
     *
     * @param liveviewWorldRegion 直播视图世界坐标区域
     */
    public record Liveview(
            LiveviewWorldRegion liveviewWorldRegion
    ) {
        /**
         * 直播视图世界坐标区域。
         *
         * @param left   左边界（0-1）
         * @param top    上边界（0-1）
         * @param right  右边界（0-1）
         * @param bottom 下边界（0-1）
         */
        public record LiveviewWorldRegion(
                double left,
                double top,
                double right,
                double bottom
        ) {}
    }
}
