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

package ltd.cdmi.dji.cloudapi.sdk.command.service.fts;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * FTS 服务端类型（{@code fts_config.fts_server_type}，enum_int {0,1}）。
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/dock/dock3/fts.html")
@Verified(basis = "DJI Dock3 FTS services 文档 fts_server_type enum_int {0:DJI FTS 服务器, 1:自定义 FTS 服务器}")
public enum FtsServerType {

    DJI_FTS(0, "DJI FTS 服务器"),
    CUSTOM(1, "自定义 FTS 服务器");

    private final int code;
    private final String description;

    FtsServerType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() { return code; }
    public String description() { return description; }

    public static FtsServerType fromCode(int code) {
        for (FtsServerType v : values()) {
            if (v.code == code) {
                return v;
            }
        }
        throw new IllegalArgumentException("未知的 fts_server_type: " + code);
    }
}
