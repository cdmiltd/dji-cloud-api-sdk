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
 * DJI Cloud API events 通道事件 POJO 定义层。
 *
 * <p>本包及其子包定义 DJI {@code events} 通道 19 个事件 method 的 data 字段强类型 record。
 * 只定义「字段是什么」，不实现「如何处理」。每个事件 POJO 对应一个
 * {@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod} 枚举值。
 *
 * <h3>根包共享 record</h3>
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.PathPoint} — 经纬高坐标点，
 *       被 wayline/flight 子包的多个事件共享（{@code planned_path_points} 数组元素）</li>
 * </ul>
 *
 * <h3>子包结构（按事件类别）</h3>
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.wayline wayline/} — 航线任务事件（4 个：
 *       flighttask_progress/flighttask_ready/in_flight_wayline_progress/return_home_info）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.flight flight/} — 飞行控制事件（4 个：
 *       fly_to_point_progress/takeoff_to_point_progress/poi_circle_status/
 *       camera_photo_take_progress）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.system system/} — 系统事件（2 个：
 *       ota_progress/fileupload_progress）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.media media/} — 媒体事件（2 个：
 *       highest_priority_upload_flighttask_media/file_upload_callback）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.flightarea flightarea/} — 自定义飞行区事件（2 个：
 *       flight_areas_drone_location/flight_areas_sync_progress）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.alert alert/} — 授权/AirSense/HMS 事件（3 个：
 *       cloud_control_auth_notify/airsense_warning/hms）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.speaker speaker/} — 喇叭/音频事件（2 个：
 *       speaker_tts_play_start_progress/speaker_audio_play_start_progress）</li>
 * </ul>
 *
 * <h3>核实状态分布</h3>
 * <ul>
 *   <li><b>@Verified（17 个）</b>：simulator 已对接 hivemind 验证</li>
 *   <li><b>@Inferred（2 个）</b>：return_home_info/in_flight_wayline_progress 待真机验证蛙跳场景字段，
 *       poi_circle_status 待真机验证字段结构</li>
 * </ul>
 *
 * <h3>与现有模块的关系</h3>
 * <ul>
 *   <li>与 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.envelope.EventEnvelope} 组合使用：
 *       事件 data record 作为 {@code EventEnvelope.data}</li>
 *   <li>与 {@link ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod} 对齐：
 *       每个 POJO 对应一个 {@code EventMethod} 枚举值</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt-dock.html">
 * DJI Dock 上云 events 通道</a>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.protocol.method.EventMethod
 */
package ltd.cdmi.dji.cloudapi.sdk.command.event;
