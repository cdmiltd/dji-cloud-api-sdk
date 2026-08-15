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
 * services 通道相机/负载控制类指令 POJO。
 *
 * <p>本包含相机拍照/录像/模式/曝光/对焦/云台复位/红外测光/屏幕拖拽/瞄准等指令的 Request record。
 * Reply 统一使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.NoOutputReply}。
 *
 * <p>所有指令均含 payloadIndex 字段（DJI JSON payload_index），用于标识负载位置。
 */
package ltd.cdmi.dji.cloudapi.sdk.command.service.camera;
