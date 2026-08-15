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

package ltd.cdmi.dji.cloudapi.sdk.capture;

import java.nio.file.Path;
import java.util.Set;

/**
 * 真机采集配置。
 *
 * <p>通过 {@link CaptureRecorder#enable(CaptureConfig)} 启用采集时传入。
 * 调用方需自行读取系统属性并显式调用 {@code enable}，例如：
 * <pre>{@code
 * if ("true".equalsIgnoreCase(System.getProperty("dji.cloud.capture"))) {
 *     CaptureRecorder.enable(CaptureConfig.defaults());
 * }
 * }</pre>
 *
 * @param enabled            是否启用采集
 * @param captureDir         采集文件输出目录
 * @param maxSamplesPerMethod 每个方法每个机型的最大采集份数（去重上限）
 * @param maskFields         需要脱敏的字段名集合（递归匹配 JSON 全树）
 */
public record CaptureConfig(
        boolean enabled,
        Path captureDir,
        int maxSamplesPerMethod,
        Set<String> maskFields
) {
    /** 默认脱敏字段：SN、License、密钥、令牌等敏感信息 */
    private static final Set<String> DEFAULT_MASK_FIELDS = Set.of(
            "sn",
            "app_license", "app_id", "app_key",
            "access_key_id", "secret_access_key", "security_token", "client_token",
            "nonce", "signature"
    );

    /**
     * 返回默认配置：输出到 {@code dji-capture/} 目录，每方法每机型最多 5 份。
     */
    public static CaptureConfig defaults() {
        return new CaptureConfig(true, Path.of("dji-capture"), 5, DEFAULT_MASK_FIELDS);
    }
}
