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

package ltd.cdmi.dji.cloudapi.sdk.protocol.envelope;

import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.command.request.config.StorageConfigGetReply;
import ltd.cdmi.dji.cloudapi.sdk.command.request.registration.ConfigReply;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link RequestReplyEnvelope} 的组件结构、序列化与反序列化。
 *
 * <p>规格见 tdd-test-cases.md §5.4。
 */
@Tag("spec")
class RequestReplyEnvelopeTest {

    @Test
    @DisplayName("shouldHave5Components_whenRecord：tid/bid/timestamp/method/data，data 为 Object")
    void testComponents() {
        RecordComponent[] components = RequestReplyEnvelope.class.getRecordComponents();
        List<String> names = java.util.Arrays.stream(components)
                .map(RecordComponent::getName).toList();
        assertEquals(5, components.length);
        assertEquals(List.of("tid", "bid", "timestamp", "method", "data"), names);
        assertEquals(long.class, components[2].getType());
        assertEquals(Object.class, components[4].getType());
    }

    @Test
    @DisplayName("shouldAccessComponents_whenInstance")
    void testAccessors() {
        RequestReplyEnvelope e = new RequestReplyEnvelope("t1", "b1", 1L, "config", null);
        assertEquals("t1", e.tid());
        assertEquals("b1", e.bid());
        assertEquals(1L, e.timestamp());
        assertEquals("config", e.method());
        assertNull(e.data());
    }

    @Test
    @DisplayName("shouldRoundTrip_whenJsonFlatData：扁平 data（config 回复）往返")
    void testRoundTripFlatData() {
        String json = "{\"tid\":\"t1\",\"bid\":\"b1\",\"timestamp\":1700000000000,"
                + "\"method\":\"config\",\"data\":{\"result\":0,\"app_id\":\"app-001\","
                + "\"app_license\":\"lic\",\"url\":\"mqtt-host\",\"token\":\"tk\"}}";
        RequestReplyEnvelope env = MessageCodec.fromJson(json, RequestReplyEnvelope.class);
        assertEquals("t1", env.tid());
        assertEquals("config", env.method());
        assertEquals(1700000000000L, env.timestamp());
        assertNotNull(env.data());
        assertTrue(env.data() instanceof Map);
        assertEquals("app-001", ((Map<?, ?>) env.data()).get("app_id"));
    }

    @Test
    @DisplayName("shouldSerialize_whenFlatReplyData：ConfigReply 序列化非 output 包裹")
    void testSerializeFlatReply() {
        RequestReplyEnvelope env = new RequestReplyEnvelope("t", "b", 1L, "config",
                new ConfigReply(0, "app-001", "lic", "mqtt-host", "tk"));
        String json = MessageCodec.toJson(env);
        assertTrue(json.contains("\"tid\":\"t\""));
        assertTrue(json.contains("\"method\":\"config\""));
        assertTrue(json.contains("\"result\":0"));
        assertTrue(json.contains("\"app_id\":\"app-001\""));
        assertTrue(json.contains("\"app_license\":\"lic\""));
        assertFalse(json.contains("\"output\""));
    }

    @Test
    @DisplayName("shouldSerialize_whenNestedOutputReplyData：StorageConfigGetReply 含 {result, output}")
    void testSerializeNestedOutput() {
        StorageConfigGetReply.Output output = new StorageConfigGetReply.Output(
                "bucket", "endpoint", "region", "aliyun", "prefix/", null);
        RequestReplyEnvelope env = new RequestReplyEnvelope("t", "b", 1L,
                "storage_config_get", new StorageConfigGetReply(0, output));
        String json = MessageCodec.toJson(env);
        assertTrue(json.contains("\"result\":0"));
        assertTrue(json.contains("\"output\":{"));
        assertTrue(json.contains("\"bucket\":\"bucket\""));
        assertTrue(json.contains("\"endpoint\":\"endpoint\""));
    }

    @Test
    @DisplayName("shouldDifferFromReplyEnvelope_whenDataIsObject：data 类型 Object vs ReplyData")
    void testDifferFromReplyEnvelope() {
        Class<?> reqReplyData = RequestReplyEnvelope.class.getRecordComponents()[4].getType();
        Class<?> replyData = ReplyEnvelope.class.getRecordComponents()[4].getType();
        assertEquals(Object.class, reqReplyData);
        assertEquals(ReplyEnvelope.ReplyData.class, replyData);
    }

    @Test
    @DisplayName("shouldTolerateUnknownProps_whenDeserialize")
    void testTolerateUnknownProps() {
        String json = "{\"tid\":\"t\",\"bid\":\"b\",\"timestamp\":1,\"method\":\"config\","
                + "\"data\":{\"result\":0},\"extra\":\"x\"}";
        assertDoesNotThrow(() -> MessageCodec.fromJson(json, RequestReplyEnvelope.class));
    }

    @Test
    @DisplayName("shouldOf_whenStaticFactory：of 构造 data 直接持有 Reply record")
    void testOfStaticFactory() {
        ConfigReply reply = new ConfigReply(0, "app", "lic", "url", "token");
        RequestReplyEnvelope env = RequestReplyEnvelope.of("t", "b", "config", reply);
        assertEquals("t", env.tid());
        assertEquals("config", env.method());
        assertSame(reply, env.data());
        assertTrue(env.timestamp() > 0);
    }

    @Test
    @DisplayName("shouldOfSerialize_whenStaticFactory：of 序列化扁平 data 非 output 包裹")
    void testOfSerializeStaticFactory() {
        String json = MessageCodec.toJson(RequestReplyEnvelope.of("t", "b", "config",
                new ConfigReply(0, "app", "lic", "url", "token")));
        assertTrue(json.contains("\"tid\":\"t\""));
        assertTrue(json.contains("\"method\":\"config\""));
        assertTrue(json.contains("\"result\":0"));
        assertTrue(json.contains("\"app_id\":\"app\""));
        assertFalse(json.contains("\"output\""));
    }
}
