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
 * 红外测温点（CameraInfo.ir_metering_point）。
 *
 * <p>以镜头左上角为坐标原点，水平方向为 x，竖直方向为 y，取值 0-1。
 * 亦用于测温区域的最高/最低温度点（{@code min_temperature_point}/{@code max_temperature_point}）。
 *
 * <p>DJI 文档字段：
 * <ul>
 *   <li>{@code x} — 测温点坐标 x（0-1）</li>
 *   <li>{@code y} — 测温点坐标 y（0-1）</li>
 *   <li>{@code temperature} — 测温点的温度（°C）</li>
 * </ul>
 *
 * @see CameraInfo#irMeteringPoint()
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m4d-properties.html")
@Verified(basis = "DJI M4D/M4TD 设备属性页 cameras[].ir_metering_point struct（x/y/temperature 均为 double）")
public record IrMeteringPoint(
        /** 测温点坐标 x（0-1） */
        Double x,

        /** 测温点坐标 y（0-1） */
        Double y,

        /** 测温点的温度（°C） */
        Double temperature
) {
}
