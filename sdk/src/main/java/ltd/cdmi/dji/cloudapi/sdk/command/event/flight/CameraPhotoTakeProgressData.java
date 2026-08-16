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

package ltd.cdmi.dji.cloudapi.sdk.command.event.flight;

import java.util.Objects;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.CameraMode;

/**
 * camera_photo_take_progress 事件 data。
 *
 * <p>对应 DJI Cloud API {@code camera_photo_take_progress} 事件（events 通道）的 data。
 * 用于拍照进度上报，{@code need_reply=1} 需平台回复，含执行结果与 output（status/progress/ext）。
 *
 * <p>对应枚举：{@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod#CAMERA_PHOTO_TAKE_PROGRESS}
 *
 * <p>字段依据：simulator {@code FlightCommandSimulator.triggerCameraPhotoTakeProgress}（L525-L547）
 * 已对接 hivemind 验证。
 *
 * <p>嵌套 record（单用，定义在本类体内）：
 * <ul>
 *   <li>{@link Output} — 输出对象（status/progress/ext）</li>
 *   <li>{@link Output.Progress} — 步进进度（current_step/percent）</li>
 *   <li>{@link Output.Ext} — 扩展信息（camera_mode，类型化枚举 {@link CameraMode}）</li>
 * </ul>
 *
 * <p><b>类型化字段</b>：{@code ext.camera_mode} 字段使用类型化枚举 {@link CameraMode}，
 * 通过 Jackson {@code @JsonValue}/{@code @JsonCreator}（见枚举类）实现 DJI 协议 int 值
 * 与枚举的双向绑定。未知值（如 M30 文档的 -1=不支持的模式）反序列化时会抛
 * {@link IllegalArgumentException}，由 Jackson 包装为 {@code JsonMappingException}。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html")
@Verified(basis = "simulator FlightCommandSimulator.triggerCameraPhotoTakeProgress L525-L547 已对接 hivemind 验证")
public record CameraPhotoTakeProgressData(
    Integer result,
    Output output
) {
    public CameraPhotoTakeProgressData {
        Objects.requireNonNull(result, "result 必填，DJI JSON 缺失 result 字段");
    }

    /** camera_photo_take_progress 事件 output 字段。 */
    public record Output(
        String status,
        Progress progress,
        Ext ext
    ) {}

    /** output.progress 字段，步进进度。 */
    public record Progress(
        Integer currentStep,
        Integer percent
    ) {}

    /** output.ext 字段，扩展信息。 */
    public record Ext(
        /** 当前相机模式（类型化枚举，Jackson 自动绑定 int↔enum，见 {@link CameraMode}） */
        CameraMode cameraMode
    ) {}
}
