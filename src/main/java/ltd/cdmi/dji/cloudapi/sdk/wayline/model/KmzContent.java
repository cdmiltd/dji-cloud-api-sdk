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

package ltd.cdmi.dji.cloudapi.sdk.wayline.model;

/**
 * KMZ 解包后的 XML 字符串容器。
 *
 * <p>由 {@link ltd.cdmi.dji.cloudapi.sdk.wayline.WpmlCodec#fromKmz(byte[])} 返回，
 * 包含 KMZ 内 {@code wpmz/template.kml} 和 {@code wpmz/waylines.wpml} 的原始 XML 字符串。
 * 调用方可直接查看/编辑 XML，或用
 * {@link ltd.cdmi.dji.cloudapi.sdk.wayline.WpmlCodec#parseTemplateKml(String)} /
 * {@link ltd.cdmi.dji.cloudapi.sdk.wayline.WpmlCodec#parseWaylinesWpml(String)}
 * 进一步反序列化为 POJO。
 *
 * @param templateKml  template.kml XML 字符串
 * @param waylinesWpml waylines.wpml XML 字符串
 */
public record KmzContent(
    String templateKml,
    String waylinesWpml
) {}
