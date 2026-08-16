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
 * services 通道飞行控制类指令 POJO。
 *
 * <p>本包含飞向目标点/更新目标点/一键起飞/抢占负载权限等指令的 Request record。
 * Reply 统一使用 {@link ltd.cdmi.dji.cloudapi.sdk.command.service.NoOutputReply}。
 *
 * <p>{@link ltd.cdmi.dji.cloudapi.sdk.command.service.SimulateMission} 为跨包共享 record，
 * 保留在 command/service 根目录。
 */
package ltd.cdmi.dji.cloudapi.sdk.command.service.flight;
