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
 * DJI WPML template.kml 航线生成工具。
 *
 * <p>提供类型安全的 Builder API，用于构造符合 DJI WPML 规范的
 * {@code template.kml} XML 文件，覆盖航点飞行模板 + 完整 actionGroup 动作组。
 *
 * <p>核心入口：{@link ltd.cdmi.dji.cloudapi.sdk.wayline.WaypointTemplate#builder()}
 *
 * <p>使用示例：
 * <pre>{@code
 * String kml = WaypointTemplate.builder()
 *     .author("John")
 *     .flyToWaylineMode(FlyToWaylineMode.SAFELY)
 *     .finishAction(FinishAction.GO_HOME)
 *     .addWaypoint(w -> w.longitude(113.98).latitude(22.98).height(100))
 *     .toXml();
 * }</pre>
 *
 * <p>子包：
 * <ul>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.wayline.model} — XML 元素 POJO（record + Jackson XML 注解）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.wayline.model.action} — 动作参数 POJO（ActionActuatorFuncParam 密封接口及子类）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.wayline.model.coordinate} — KML 坐标元素（Point/LineString/LinearRing/Polygon）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.wayline.model.execute} — waylines.wpml 执行模型（ExecuteFolder/ExecutePlacemark）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.wayline.model.mapping} — 建图航拍模板 POJO（Mapping2d/3d/Strip）</li>
 *   <li>{@link ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype} — WPML 枚举类型（字符串码）</li>
 * </ul>
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.wayline.WaypointTemplate
 * @see ltd.cdmi.dji.cloudapi.sdk.wayline.WpmlCodec
 */
package ltd.cdmi.dji.cloudapi.sdk.wayline;
