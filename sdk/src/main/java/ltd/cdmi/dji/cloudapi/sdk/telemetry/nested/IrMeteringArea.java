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

package ltd.cdmi.dji.cloudapi.sdk.telemetry.nested;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 红外测温区域（CameraInfo.ir_metering_area）。
 *
 * <p>以镜头左上角为坐标原点，水平方向为 x，竖直方向为 y，坐标与宽高取值 0-1。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code x} — 测温区域左上角点坐标 x（0-1）</li>
 *   <li>{@code y} — 测温区域左上角点坐标 y（0-1）</li>
 *   <li>{@code width} — 测温区域宽度（0-1）</li>
 *   <li>{@code height} — 测温区域高度（0-1）</li>
 *   <li>{@code aver_temperature} — 测温区域平均温度（°C）</li>
 *   <li>{@code min_temperature_point} — 测温区域最低温度点（struct，同 {@link IrMeteringPoint}）</li>
 *   <li>{@code max_temperature_point} — 测温区域最高温度点（struct，同 {@link IrMeteringPoint}）</li>
 * </ul>
 *
 * @see CameraInfo#irMeteringArea()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m4d-properties.html")
@Verified(basis = "DJI M4D/M4TD 设备属性页 cameras[].ir_metering_area struct（x/y/width/height/aver_temperature 为 double；min/max_temperature_point 为含 x/y/temperature 的 struct，与 IrMeteringPoint 同构）")
public record IrMeteringArea(
        /** 测温区域左上角点坐标 x（0-1） */
        Double x,

        /** 测温区域左上角点坐标 y（0-1） */
        Double y,

        /** 测温区域宽度（0-1） */
        Double width,

        /** 测温区域高度（0-1） */
        Double height,

        /** 测温区域平均温度（°C） */
        Double averTemperature,

        /** 测温区域最低温度点 */
        IrMeteringPoint minTemperaturePoint,

        /** 测温区域最高温度点 */
        IrMeteringPoint maxTemperaturePoint
) {
}
