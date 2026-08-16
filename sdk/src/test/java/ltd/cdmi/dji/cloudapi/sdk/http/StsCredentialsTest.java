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

package ltd.cdmi.dji.cloudapi.sdk.http;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link StsCredentials} STS 临时凭证 record 测试。
 *
 * <p>验证 snake_case ↔ camelCase 双向映射（重点 {@code object_key_prefix} ↔ {@code objectKeyPrefix}），
 * 以及 {@code credentials} 子结构以 {@code Object}（Map）持有。
 */
class StsCredentialsTest {

    private static final String JSON = "{"
            + "\"bucket\":\"dji-bucket\","
            + "\"endpoint\":\"oss-cn-hangzhou.aliyuncs.com\","
            + "\"region\":\"cn-hangzhou\","
            + "\"provider\":\"aliyun\","
            + "\"credentials\":{\"access_key_id\":\"STS.xxx\",\"access_key_secret\":\"sec\","
            + "\"security_token\":\"tok\",\"expire\":3600},"
            + "\"object_key_prefix\":\"prefix/flight1\"}";

    @Test
    @DisplayName("反序列化：snake_case JSON → camelCase record（object_key_prefix → objectKeyPrefix）")
    void testDeserialize() {
        StsCredentials sts = MessageCodec.fromJson(JSON, StsCredentials.class);
        assertEquals("dji-bucket", sts.bucket());
        assertEquals("oss-cn-hangzhou.aliyuncs.com", sts.endpoint());
        assertEquals("cn-hangzhou", sts.region());
        assertEquals("aliyun", sts.provider());
        assertEquals("prefix/flight1", sts.objectKeyPrefix());
        assertNotNull(sts.credentials(), "credentials 子结构不应为 null");
        assertTrue(sts.credentials() instanceof Map, "credentials 反序列化为 Map");
        assertEquals("STS.xxx", ((Map<?, ?>) sts.credentials()).get("access_key_id"));
        assertEquals("tok", ((Map<?, ?>) sts.credentials()).get("security_token"));
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（objectKeyPrefix → object_key_prefix）")
    void testSerialize() {
        Map<String, Object> creds = new LinkedHashMap<>();
        creds.put("access_key_id", "AKID");
        creds.put("expire", 3600);
        StsCredentials sts = new StsCredentials("bkt", "ep", "rg", "aws", creds, "prefix/x");
        String json = MessageCodec.toJson(sts);
        assertTrue(json.contains("\"bucket\":\"bkt\""));
        assertTrue(json.contains("\"endpoint\":\"ep\""));
        assertTrue(json.contains("\"region\":\"rg\""));
        assertTrue(json.contains("\"provider\":\"aws\""));
        assertTrue(json.contains("\"object_key_prefix\":\"prefix/x\""));
        assertTrue(json.contains("\"access_key_id\":\"AKID\""));
        assertTrue(json.contains("\"expire\":3600"));
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        Map<String, Object> creds = new LinkedHashMap<>();
        creds.put("access_key_id", "AKID");
        creds.put("expire", 3600);
        StsCredentials original = new StsCredentials("bkt", "ep", "rg", "aliyun", creds, "pfx");
        String json = MessageCodec.toJson(original);
        StsCredentials round = MessageCodec.fromJson(json, StsCredentials.class);
        assertEquals(original.bucket(), round.bucket());
        assertEquals(original.endpoint(), round.endpoint());
        assertEquals(original.region(), round.region());
        assertEquals(original.provider(), round.provider());
        assertEquals(original.objectKeyPrefix(), round.objectKeyPrefix());
        assertEquals(original.credentials(), round.credentials());
    }

    @Test
    @DisplayName("FAIL_ON_UNKNOWN_PROPERTIES=false：未知字段不报错")
    void testUnknownFieldTolerated() {
        String json = "{\"bucket\":\"b\",\"extra_field\":\"ignored\"}";
        StsCredentials sts = MessageCodec.fromJson(json, StsCredentials.class);
        assertEquals("b", sts.bucket());
    }

    @Test
    @DisplayName("record 访问器：bucket/endpoint/region/provider/credentials/objectKeyPrefix")
    void testRecordAccessors() {
        Object creds = Map.of("access_key_id", "AKID");
        StsCredentials sts = new StsCredentials("b", "e", "r", "p", creds, "o");
        assertEquals("b", sts.bucket());
        assertEquals("e", sts.endpoint());
        assertEquals("r", sts.region());
        assertEquals("p", sts.provider());
        assertEquals(creds, sts.credentials());
        assertEquals("o", sts.objectKeyPrefix());
    }
}
