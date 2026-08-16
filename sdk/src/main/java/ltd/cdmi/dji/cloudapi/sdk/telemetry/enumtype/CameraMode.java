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

package ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI 相机工作模式（camera_mode）。
 *
 * <p>camera_mode 标识相机当前工作模式，出现在 M3D/M30 properties 的 cameras 数组元素中，
 * 也出现在 camera_photo_take_progress 事件的 output.ext 字段。
 *
 * <p><strong>注意值域</strong>：M3D 文档定义 4 个值（0-3）；M30 文档额外含 -1=不支持的模式。
 * 本枚举以 M3D properties 文档为准，仅定义 4 个正常值。M30 的 -1 为异常值，开发者可通过
 * {@link #fromCode(int)} 抛 {@link IllegalArgumentException} 捕获。
 *
 * <p><b>Jackson 绑定</b>：通过 {@link JsonValue}（序列化：枚举 → int code）与
 * {@link JsonCreator}（反序列化：int code → 枚举）实现 DJI 协议 int 值与枚举类型的双向绑定，
 * 可作为 record 字段类型直接使用（如
 * {@link ltd.cdmi.dji.cloudapi.sdk.command.event.flight.CameraPhotoTakeProgressData.Ext#cameraMode}）。
 * 未知值（如 M30 的 -1）反序列化时会抛 {@link IllegalArgumentException}，由 Jackson 包装为
 * {@link com.fasterxml.jackson.databind.JsonMappingException}，调用方可捕获或预处理 JSON。
 *
 * <p>关联 POJO：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.camera.CameraModeSwitchRequest#cameraMode} — services 指令字段（已升级为本枚举类型）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.flight.CameraPhotoTakeProgressData.Ext#cameraMode} — events 字段（已升级为本枚举类型）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m3d-properties.html">
 * DJI M3D/M3TD 设备属性 camera_mode 枚举定义</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/aircraft/m3d-properties.html")
@Verified(basis = "DJI M3D properties.html camera_mode 枚举定义（0-3）")
public enum CameraMode {

    PHOTO(0, "拍照"),
    VIDEO(1, "录像"),
    HYPER_LIGHT(2, "智能低光"),
    PANORAMA(3, "全景拍照");

    private static final Map<Integer, CameraMode> BY_CODE =
            Stream.of(values()).collect(Collectors.toUnmodifiableMap(CameraMode::code, Function.identity()));

    private final int code;
    private final String description;

    CameraMode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public int code() { return code; }
    public String description() { return description; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CameraMode fromCode(int code) {
        CameraMode m = BY_CODE.get(code);
        if (m == null) {
            throw new IllegalArgumentException("未知的 camera_mode: " + code);
        }
        return m;
    }
}
