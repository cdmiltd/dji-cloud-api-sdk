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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link HttpResponseEnvelope} HTTP 响应信封 record 测试。
 *
 * <p>验证泛型 record 的序列化/反序列化与 {@link MessageCodec#parseHttp} 类型安全解析。
 * DJI HTTP API 响应统一信封为 {@code {"code":0, "message":"...", "data":{...}}}。
 */
class HttpResponseEnvelopeTest {

    @Test
    @DisplayName("反序列化：信封 JSON → HttpResponseEnvelope，data 反序列化为 Map")
    @SuppressWarnings("rawtypes")
    void testDeserialize() {
        String json = "{\"code\":0,\"message\":\"success\",\"data\":{\"bucket\":\"b\"}}";
        HttpResponseEnvelope resp = MessageCodec.fromJson(json, HttpResponseEnvelope.class);
        assertEquals(0, resp.code());
        assertEquals("success", resp.message());
        assertNotNull(resp.data());
        assertTrue(resp.data() instanceof Map, "data 应反序列化为 Map");
        assertEquals("b", ((Map<?, ?>) resp.data()).get("bucket"));
    }

    @Test
    @DisplayName("序列化：HttpResponseEnvelope → JSON")
    void testSerialize() {
        HttpResponseEnvelope<String> resp = new HttpResponseEnvelope<>(0, "ok", "payload");
        String json = MessageCodec.toJson(resp);
        assertTrue(json.contains("\"code\":0"));
        assertTrue(json.contains("\"message\":\"ok\""));
        assertTrue(json.contains("\"data\":\"payload\""));
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        HttpResponseEnvelope<String> original = new HttpResponseEnvelope<>(200, "created", "abc");
        String json = MessageCodec.toJson(original);
        HttpResponseEnvelope<String> round = MessageCodec.fromJson(json, HttpResponseEnvelope.class);
        assertEquals(original.code(), round.code());
        assertEquals(original.message(), round.message());
        assertEquals(original.data(), round.data());
    }

    @Test
    @DisplayName("record 访问器：code/message/data")
    void testRecordAccessors() {
        HttpResponseEnvelope<Integer> resp = new HttpResponseEnvelope<>(0, "ok", 42);
        assertEquals(0, resp.code());
        assertEquals("ok", resp.message());
        assertEquals(42, resp.data());
    }

    // ==================== parseHttp 集成 ====================

    @Test
    @DisplayName("parseHttp：成功响应 — data 反序列化为指定 POJO（StsCredentials）")
    void testParseHttpSuccess() {
        String payload = "{\"code\":0,\"message\":\"success\","
                + "\"data\":{\"bucket\":\"bkt\",\"endpoint\":\"ep\",\"region\":\"rg\","
                + "\"provider\":\"aliyun\",\"object_key_prefix\":\"pfx\"}}";
        HttpResponseEnvelope<StsCredentials> resp = MessageCodec.parseHttp(payload, StsCredentials.class);
        assertEquals(0, resp.code());
        assertEquals("success", resp.message());
        assertNotNull(resp.data());
        assertEquals("bkt", resp.data().bucket());
        assertEquals("pfx", resp.data().objectKeyPrefix());
    }

    @Test
    @DisplayName("parseHttp：错误响应 — data 为 null，message 含错误描述")
    void testParseHttpError() {
        String payload = "{\"code\":401,\"message\":\"unauthorized\",\"data\":null}";
        HttpResponseEnvelope<StsCredentials> resp = MessageCodec.parseHttp(payload, StsCredentials.class);
        assertEquals(401, resp.code());
        assertEquals("unauthorized", resp.message());
        assertNull(resp.data());
    }

    @Test
    @DisplayName("parseHttp：JSON 无 data 字段 — data() 返回 null")
    void testParseHttpMissingData() {
        String payload = "{\"code\":0,\"message\":\"success\"}";
        HttpResponseEnvelope<StsCredentials> resp = MessageCodec.parseHttp(payload, StsCredentials.class);
        assertEquals(0, resp.code());
        assertNull(resp.data());
    }
}
