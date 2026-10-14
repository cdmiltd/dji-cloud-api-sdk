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

package ltd.cdmi.dji.cloudapi.sdk.command.service.flysafe;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 单张解禁证书（{@code unlock_license_list.licenses[]} 元素）。
 *
 * <p>结构 = {@link CommonFields} + 7 种解禁类型之一的子结构（{@code area_unlock} / {@code circle_unlock} /
 * {@code country_unlock} / {@code height_unlock} / {@code polygon_unlock} / {@code power_unlock} /
 * {@code rid_unlock}）。当前解禁类型由 {@link CommonFields#type()} 标识。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/flysafe.html">
 * DJI Dock3 远程解禁 services</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/flysafe.html")
@Verified(basis = "DJI Dock3 flysafe.html unlock_license_list licenses[] 元素 = common_fields + 7 种解禁类型子结构之一")
public record License(
        /** 通用字段 */
        CommonFields commonFields,
        /** 区域解禁（type=0） */
        AreaUnlock areaUnlock,
        /** 圆形解禁（type=1） */
        CircleUnlock circleUnlock,
        /** 国家解禁（type=2） */
        CountryUnlock countryUnlock,
        /** 高度解禁（type=3） */
        HeightUnlock heightUnlock,
        /** 多边形解禁（type=4） */
        PolygonUnlock polygonUnlock,
        /** 动力解禁（type=5） */
        PowerUnlock powerUnlock,
        /** RID 解禁（type=6） */
        RidUnlock ridUnlock
) {}
