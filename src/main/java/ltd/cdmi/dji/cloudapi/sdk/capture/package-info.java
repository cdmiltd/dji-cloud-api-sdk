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
 * 真机消息采集模块——将真机收发的 MQTT 消息自动分类存储，供 SDK 维护者验证协议。
 *
 * <h2>用途</h2>
 * <p>SDK 维护者无法拥有所有机型（Dock1/2/3 × M30/M3D/M4D × 各种负载），
 * 但用户有真机。通过开启采集开关，用户可将真机收发的消息自动采集并提交到
 * GitHub Issue，帮助 SDK 维护者将 {@code @Inferred} 项验证为 {@code @Verified}。
 *
 * <h2>核心类</h2>
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.capture.CaptureConfig} — 采集配置</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.capture.CaptureRecorder} — 采集记录器（静态工具类）</li>
 * </ul>
 *
 * <h2>采集文件结构</h2>
 * <pre>
 * dji-capture/
 * ├── Dock3-M4D/                 ← 网关型号-飞行器型号
 * │   ├── inbound/               ← 真机→平台（验证 Request POJO）
 * │   │   ├── fly_to_point_20260815T103000_001.json
 * │   │   └── cover_open_20260815T103002_002.json
 * │   └── outbound/              ← 平台→真机（验证 Reply POJO）
 * │       └── fly_to_point_reply_20260815T103001_003.json
 * ├── Dock2-M3D/
 * │   └── ...
 * └── SN-7UUXN1Q00A008W/         ← 未注册设备（用 SN 兜底）
 *     └── ...
 * </pre>
 *
 * <h2>采集文件格式</h2>
 * <pre>{@code
 * {
 *   "_capture": {
 *     "timestamp": "2026-08-15T10:30:00",
 *     "topic": "thing/product/7UUXN1Q00A008W/services",
 *     "direction": "inbound",
 *     "gateway": "Dock3",
 *     "aircraft": "M4D",
 *     "method": "fly_to_point"
 *   },
 *   "tid": "abc123",
 *   "method": "fly_to_point",
 *   "data": {
 *     "fly_to_id": "FT001",
 *     "target_height": 50.0,
 *     "sn": "***"              ← 已脱敏
 *   }
 * }
 * }</pre>
 */
package ltd.cdmi.dji.cloudapi.sdk.capture;
