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
import ltd.cdmi.dji.cloudapi.sdk.command.request.config.StorageConfigGetReply.Credentials;
import ltd.cdmi.dji.cloudapi.sdk.command.request.config.StorageConfigGetReply.Output;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link StorageConfigGetReply} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：storage_config_get 回复 data（result + output{bucket,endpoint,...,credentials}）
 * 能反序列化为嵌套 record；缺失 result 时构造器抛 NPE。
 */
class StorageConfigGetReplyTest {

    private static final String SAMPLE_JSON =
            "{\"result\":0,\"output\":{\"bucket\":\"cdmi-bucket\",\"endpoint\":\"oss.example.com\","
            + "\"region\":\"cn-east\",\"provider\":\"aliyun\",\"object_key_prefix\":\"media/\","
            + "\"credentials\":{\"access_key_id\":\"AKID\",\"access_key_secret\":\"SK\","
            + "\"security_token\":\"STOKEN\",\"expire_time\":1700000000}}}";

    @Test
    @DisplayName("反序列化：完整 JSON → 嵌套 Output + Credentials")
    void testDeserialize() {
        StorageConfigGetReply reply = MessageCodec.fromJson(SAMPLE_JSON, StorageConfigGetReply.class);
        assertEquals(0, reply.result());
        Output output = reply.output();
        assertEquals("cdmi-bucket", output.bucket());
        assertEquals("oss.example.com", output.endpoint());
        assertEquals("cn-east", output.region());
        assertEquals("aliyun", output.provider());
        assertEquals("media/", output.objectKeyPrefix());
        Credentials cred = output.credentials();
        assertEquals("AKID", cred.accessKeyId());
        assertEquals("SK", cred.accessKeySecret());
        assertEquals("STOKEN", cred.securityToken());
        assertEquals(1700000000L, cred.expireTime());
    }

    @Test
    @DisplayName("序列化：含嵌套 Output/Credentials 的 record → JSON")
    void testSerialize() {
        Credentials cred = new Credentials("AKID", "SK", "STOKEN", 1700000000L);
        Output output = new Output("cdmi-bucket", "oss.example.com", "cn-east",
                "aliyun", "media/", cred);
        StorageConfigGetReply reply = new StorageConfigGetReply(0, output);
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":0"), "JSON 应含 result:0，实际: " + json);
        assertTrue(json.contains("\"bucket\":\"cdmi-bucket\""), "JSON 应含 bucket，实际: " + json);
        assertTrue(json.contains("\"access_key_id\":\"AKID\""), "JSON 应含 access_key_id，实际: " + json);
        assertTrue(json.contains("\"expire_time\":1700000000"), "JSON 应含 expire_time，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持嵌套结构不变")
    void testRoundTrip() {
        Credentials cred = new Credentials("AKID", "SK", "STOKEN", 1700000000L);
        Output output = new Output("b", "e", "r", "p", "pre/", cred);
        StorageConfigGetReply original = new StorageConfigGetReply(0, output);
        String json = MessageCodec.toJson(original);
        StorageConfigGetReply back = MessageCodec.fromJson(json, StorageConfigGetReply.class);
        assertEquals(0, back.result());
        assertEquals("b", back.output().bucket());
        assertEquals("AKID", back.output().credentials().accessKeyId());
        assertEquals(1700000000L, back.output().credentials().expireTime());
    }

    @Test
    @DisplayName("缺失 result 字段：反序列化后构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingResultThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"output\":{}}", StorageConfigGetReply.class));
    }
}
