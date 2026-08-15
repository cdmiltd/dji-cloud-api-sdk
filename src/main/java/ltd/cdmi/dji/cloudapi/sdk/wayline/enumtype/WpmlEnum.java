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

package ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype;

/**
 * WPML 枚举公共接口。
 *
 * <p>所有 WPML template.kml / common-element 文档定义的枚举类型均实现此接口，
 * 统一暴露 {@link #code()} 和 {@link #description()} 方法，
 * 供 {@link ltd.cdmi.dji.cloudapi.sdk.wayline.WaypointTemplate} 等模板类统一调用。
 *
 * <p>WPML 枚举值为字符串（如 {@code "safely"}、{@code "goHome"}），
 * 与 MQTT 协议的整数枚举不同。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.wayline.WaypointTemplate
 */
public interface WpmlEnum {

    /**
     * 返回 WPML 规范定义的字符串码。
     *
     * @return 字符串码，如 {@code "safely"}
     */
    String code();

    /**
     * 返回枚举值的中文描述。
     *
     * @return 描述文本
     */
    String description();

    /**
     * 将可空枚举转为字符串码，{@code null} 安全。
     *
     * <p>供模板类（{@link ltd.cdmi.dji.cloudapi.sdk.wayline.WaypointTemplate} 等）
     * 在构造 POJO 时统一调用，避免每个模板重复定义相同逻辑。
     *
     * @param e 可空的 {@link WpmlEnum} 实例
     * @return 字符串码；如果 {@code e} 为 {@code null} 则返回 {@code null}
     */
    static String codeOf(WpmlEnum e) {
        return e == null ? null : e.code();
    }
}
