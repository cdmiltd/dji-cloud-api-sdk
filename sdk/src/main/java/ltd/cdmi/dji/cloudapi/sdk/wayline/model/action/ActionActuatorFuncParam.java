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

package ltd.cdmi.dji.cloudapi.sdk.wayline.model.action;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Action;

/**
 * WPML {@code <wpml:actionActuatorFuncParam>} 密封接口。
 *
 * <p>对应 WPML 共用元素中 {@code actionActuatorFuncParam} 的多态参数容器。
 * 根据 {@link Action#actionActuatorFunc()} 的取值，由对应的 action param record
 * 实现具体参数字段。每个实现 record 标注
 * {@code @JacksonXmlRootElement(localName = "actionActuatorFuncParam", namespace = WpmlNamespaces.WPML)}
 * 以在 XML 序列化时生成正确的元素名。
 *
 * <p>所有实现均已按 DJI WPML 共用元素文档验证：
 * <ul>
 *   <li>{@link TakePhotoParam} — takePhoto（拍照）</li>
 *   <li>{@link StartRecordParam} — startRecord（开始录像）</li>
 *   <li>{@link StopRecordParam} — stopRecord（停止录像）</li>
 *   <li>{@link FocusParam} — focus（对焦）</li>
 *   <li>{@link ZoomParam} — zoom（变焦）</li>
 *   <li>{@link CustomDirNameParam} — customDirName（自定义目录名）</li>
 *   <li>{@link GimbalRotateParam} — gimbalRotate（云台旋转）</li>
 *   <li>{@link RotateYawParam} — rotateYaw（偏航旋转）</li>
 *   <li>{@link HoverParam} — hover（悬停）</li>
 *   <li>{@link GimbalEvenlyRotateParam} — gimbalEvenlyRotate（云台均匀旋转）</li>
 *   <li>{@link AccurateShootParam} — accurateShoot（精准复拍，已暂停维护）</li>
 *   <li>{@link OrientedShootParam} — orientedShoot（定向拍摄）</li>
 *   <li>{@link PanoShotParam} — panoShot（全景拍摄）</li>
 *   <li>{@link RecordPointCloudParam} — recordPointCloud（录制点云）</li>
 *   <li>{@link MegaphoneParam} — megaphone（喊话器，M4D/M4TD）</li>
 *   <li>{@link SearchlightParam} — searchlight（探照灯，M4D/M4TD）</li>
 * </ul>
 *
 * @see Action
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML 共用元素文档 actionActuatorFuncParam 各动作参数定义")
public sealed interface ActionActuatorFuncParam
    permits TakePhotoParam, StartRecordParam, StopRecordParam, FocusParam,
            ZoomParam, CustomDirNameParam, GimbalRotateParam, RotateYawParam,
            HoverParam, GimbalEvenlyRotateParam, AccurateShootParam,
            OrientedShootParam, PanoShotParam, RecordPointCloudParam,
            MegaphoneParam, SearchlightParam {}
