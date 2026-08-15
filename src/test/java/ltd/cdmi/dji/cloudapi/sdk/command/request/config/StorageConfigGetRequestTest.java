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

package ltd.cdmi.dji.cloudapi.sdk.command.request.config;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link StorageConfigGetRequest} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：storage_config_get 请求 data（含 module=0 媒体/1 日志）
 * 能反序列化为 record；缺失 module 时构造器抛 NPE（包装为 IllegalStateException）。
 */
class StorageConfigGetRequestTest {

    @Test
    @DisplayName("反序列化：{\"module\":0} → module()==0（媒体）")
    void testDeserializeMedia() {
        StorageConfigGetRequest req = MessageCodec.fromJson("{\"module\":0}", StorageConfigGetRequest.class);
        assertEquals(0, req.module());
    }

    @Test
    @DisplayName("反序列化：{\"module\":1} → module()==1（日志）")
    void testDeserializeLog() {
        StorageConfigGetRequest req = MessageCodec.fromJson("{\"module\":1}", StorageConfigGetRequest.class);
        assertEquals(1, req.module());
    }

    @Test
    @DisplayName("序列化：StorageConfigGetRequest(0) → JSON 含 \"module\":0")
    void testSerialize() {
        StorageConfigGetRequest req = new StorageConfigGetRequest(0);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"module\":0"), "JSON 应含 \"module\":0，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持 module 不变")
    void testRoundTrip() {
        StorageConfigGetRequest original = new StorageConfigGetRequest(1);
        String json = MessageCodec.toJson(original);
        StorageConfigGetRequest back = MessageCodec.fromJson(json, StorageConfigGetRequest.class);
        assertEquals(original.module(), back.module());
    }

    @Test
    @DisplayName("缺失 module 字段：反序列化后构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingModuleThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{}", StorageConfigGetRequest.class));
    }
}
