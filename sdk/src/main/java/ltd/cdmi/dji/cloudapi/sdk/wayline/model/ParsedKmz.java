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

import ltd.cdmi.dji.cloudapi.sdk.wayline.model.execute.ExecuteFolder;

/**
 * KMZ 解析后的 POJO 容器。
 *
 * <p>由 {@link ltd.cdmi.dji.cloudapi.sdk.wayline.WpmlCodec#parseKmz(byte[])} 返回，
 * 包含解析后的 {@link Kml}{@code <}{@link Folder}{@code >}（template.kml）
 * 和 {@link Kml}{@code <}{@link ExecuteFolder}{@code >}（waylines.wpml）POJO。
 *
 * <p>调用方可读取 POJO 字段值后用新 Builder 重建修改（record 不可变，不提供 toBuilder）。
 *
 * @param template template.kml 解析后的 POJO
 * @param waylines waylines.wpml 解析后的 POJO
 */
public record ParsedKmz(
    Kml<Folder> template,
    Kml<ExecuteFolder> waylines
) {}
