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

/**
 * DJI Cloud API services 通道指令 POJO。
 *
 * <p>本包定义 {@code services} 通道（云→设备，设备通过 {@code services_reply} 回复）
 * 的指令请求 data 字段与回复 output 字段的强类型 record。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html">
 * DJI Dock 上云 services 通道</a>
 *
 * <h3>子包结构</h3>
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.wayline} — 航线任务（prepare/execute/pause/recovery/undo/stop/return_specific_home）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.camera} — 相机/负载控制（photo/recording/mode/exposure/focus/gimbal/ir_metering 等 22 个）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.live} — 直播（start_push/stop_push/set_quality/camera_change/lens_change）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.flight} — 飞行控制（fly_to_point/takeoff_to_point/payload_authority_grab）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.drc} — DRC 模式切换（drc_mode_enter + DrcMqttBroker 嵌套 record）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.media} — 媒体管理（upload_flighttask_media_prioritize）</li>
 * </ul>
 *
 * <h3>根目录共享 record</h3>
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.NoParameterRequest} — 27 个无参数指令通用空 Request</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.NoOutputReply} — 无 output 指令通用空 Reply</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.SimulateMission} — 跨包共享嵌套 record（wayline + flight 共用）</li>
 * </ul>
 *
 * <h3>命名规则</h3>
 * <ul>
 *   <li>{@code XxxRequest} = 指令请求 data（如 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.wayline.FlighttaskPrepareRequest}
 *       对应 {@code flighttask_prepare} 的 data）</li>
 *   <li>{@code XxxReply} = 指令回复 output（如 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.wayline.FlighttaskPrepareReply}
 *       对应 {@code flighttask_prepare} 的 output）</li>
 *   <li>嵌套对象用独立 record（如 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.wayline.FlighttaskFile}、
 *       {@link ltd.cdmi.dji.cloudapi.sdk.command.service.wayline.ReadyConditions}），与主 Request 同子包</li>
 * </ul>
 *
 * <h3>Reply 对称性约定</h3>
 * <p>对于 DJI 协议规定「services_reply 仅返回 result=0、无 output 字段」的指令，
 * 统一使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.NoOutputReply}（通用空 record）。
 * 极简类（27 个无参数指令）统一使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.NoParameterRequest}。
 *
 * <h3>调用方使用示例</h3>
 * <pre>{@code
 * // 反序列化 services_reply
 * ReplyEnvelope envelope = MessageCodec.fromJson(json, ReplyEnvelope.class);
 * // 无 output 指令：envelope.data().result() 即足够
 * if (envelope.data().result() == 0) { /* 成功 *​/ }
 * }</pre>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.envelope.ReplyEnvelope
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.ServiceMethod
 */
package ltd.cdmi.dji.cloudapi.sdk.command.service;
