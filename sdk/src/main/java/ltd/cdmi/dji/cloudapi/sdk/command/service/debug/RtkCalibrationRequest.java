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

package ltd.cdmi.dji.cloudapi.sdk.command.service.debug;

import java.util.List;
import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * rtk_calibration services 请求 data。
 *
 * <p>对应 DJI Cloud API {@code rtk_calibration} 服务（services 通道，云→设备）的 data。
 * 用于一键 RTK 标定，Job 类指令（异步进度经 events 通道同名 method 上报，见
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.event.system.RtkCalibrationData}）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod#RTK_CALIBRATION}
 *
 * <p>结构依据：DJI O4 地面站 cmd 文档 services 段 Data 表——
 * {@code devices[]{ sn, type(enum_int 1=手动标定), module(enum_string 3=机场, 6=中继),
 * data{ longitude, latitude, height(椭球高) } }}。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/cmd.html">
 * DJI O4 地面站 远程调试（cmd）</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/cmd.html")
@Verified(basis = "DJI O4 地面站 cmd.html rtk_calibration services 请求 Data 表（devices[]{sn,type,module,data}）")
public record RtkCalibrationRequest(
    /** 待标定设备列表 */
    List<Device> devices
) {
    public RtkCalibrationRequest {
        Objects.requireNonNull(devices, "devices 必填，DJI JSON data 不能为空");
    }

    /**
     * 单个待标定设备。
     *
     * @param sn     设备序列号
     * @param type   标定类型（enum_int：1=手动标定）
     * @param module 设备模块（enum_string：3=机场, 6=中继）
     * @param data   标定数据（经纬高）
     */
    public record Device(
        String sn,
        Integer type,
        String module,
        CalibrationData data
    ) {}

    /**
     * rtk_calibration 请求 data.devices[].data 标定坐标。
     *
     * <p>文档仅给出字段名（{@code longitude}/{@code latitude}/{@code height}），
     * 未逐项标注类型 → 标 @Inferred。
     */
    @Inferred(
        reason = "DJI O4 cmd 文档 data{ longitude, latitude, height } 未逐项标注字段类型，按经纬度/椭球高惯例承载 Double",
        verifyPoint = "真机核对 longitude/latitude/height 的实际类型（float/double）"
    )
    public record CalibrationData(
        /** 经度 */
        Double longitude,
        /** 纬度 */
        Double latitude,
        /** 椭球高（米） */
        Double height
    ) {}
}
