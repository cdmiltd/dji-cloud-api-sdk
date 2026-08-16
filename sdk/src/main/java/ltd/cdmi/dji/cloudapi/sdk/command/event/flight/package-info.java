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
 * events 通道飞行控制类事件 POJO。
 *
 * <p>本包含 6 个飞行控制相关事件的 data record：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.flight.FlyToPointProgressData FlyToPointProgressData}
 *       — fly_to_point_progress flyto 执行结果通知（@Verified，含 planned_path_points）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.flight.TakeoffToPointProgressData TakeoffToPointProgressData}
 *       — takeoff_to_point_progress 一键起飞结果通知（@Verified，含 flight_id/track_id）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.flight.PoiCircleStatusData PoiCircleStatusData}
 *       — poi_circle_status POI 环绕状态通知（@Inferred，simulator 方法名 poi_status_notify
 *       与 EventMethod 枚举 poi_circle_status 待真机核实）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.flight.CameraPhotoTakeProgressData CameraPhotoTakeProgressData}
 *       — camera_photo_take_progress 拍照进度上报（@Verified，含嵌套 Output/Progress/Ext）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.flight.ObstacleAvoidanceNotifyData ObstacleAvoidanceNotifyData}
 *       — obstacle_avoidance_notify 避障记录上报（@Verified，仅 Dock3，含嵌套 ObstacleInfo）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.command.event.flight.JoystickInvalidNotifyData JoystickInvalidNotifyData}
 *       — joystick_invalid_notify 飞行控制无效原因通知（@Verified，三 Dock 共有，reason 枚举 0-4）</li>
 * </ul>
 *
 * <p>跨包共享 record：{@link ltd.cdmi.dji.cloudapi.sdk.command.event.PathPoint}（flyto/takeoff 轨迹点），
 * 定义在 event 根包。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/drc.html">
 * DJI Dock3 DRC events</a>
 */
package ltd.cdmi.dji.cloudapi.sdk.command.event.flight;
