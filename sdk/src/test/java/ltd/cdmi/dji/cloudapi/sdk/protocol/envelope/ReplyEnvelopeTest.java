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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link ReplyEnvelope} 的组件结构、gateway 字段与静态工厂方法 {@link ReplyEnvelope#ok}。
 *
 * <p>规格见 tdd-test-cases.md §5.2 静态工厂方法部分。
 */
@Tag("spec")
class ReplyEnvelopeTest {

    @Test
    @DisplayName("shouldHave6Components_whenRecord：tid/bid/timestamp/method/data/gateway（gateway 末位可空）")
    void testComponents() {
        RecordComponent[] components = ReplyEnvelope.class.getRecordComponents();
        List<String> names = java.util.Arrays.stream(components)
                .map(RecordComponent::getName).toList();
        assertEquals(6, components.length);
        assertEquals(List.of("tid", "bid", "timestamp", "method", "data", "gateway"), names);
        assertEquals(long.class, components[2].getType());
        assertEquals(ReplyEnvelope.ReplyData.class, components[4].getType());
        assertEquals(String.class, components[5].getType());
    }

    @Test
    @DisplayName("shouldAuxCtor5Args_whenOmitted：5 参兼容构造器将 gateway 置 null")
    void testAuxConstructorNullsGateway() {
        ReplyEnvelope env = new ReplyEnvelope("t", "b", 1L, "config",
                new ReplyEnvelope.ReplyData(0, null));
        assertNull(env.gateway(), "5 参兼容构造器 gateway 应为 null");
        String json = MessageCodec.toJson(env);
        assertFalse(json.contains("\"gateway\""), "gateway 为 null 时不应序列化，实际: " + json);
    }

    @Test
    @DisplayName("shouldCarryGateway_whenCanonicalCtor：6 参规范构造器承载 gateway 并往返")
    void testGatewayRoundTrip() {
        ReplyEnvelope env = new ReplyEnvelope("t", "b", 1L, "config",
                new ReplyEnvelope.ReplyData(0, Map.of("k", "v")), "GATEWAY_SN");
        assertEquals("GATEWAY_SN", env.gateway());
        String json = MessageCodec.toJson(env);
        assertTrue(json.contains("\"gateway\":\"GATEWAY_SN\""), "JSON 应含 gateway，实际: " + json);
        ReplyEnvelope back = MessageCodec.fromJson(json, ReplyEnvelope.class);
        assertEquals("GATEWAY_SN", back.gateway());
        assertEquals(env, back);
    }

    @Test
    @DisplayName("shouldOkNoOutput_whenStaticFactory：ok(tid,bid,method) 构造无 output 成功回复")
    void testOkNoOutput() {
        ReplyEnvelope env = ReplyEnvelope.ok("t", "b", "fly_to_point");
        assertEquals("t", env.tid());
        assertEquals("b", env.bid());
        assertEquals("fly_to_point", env.method());
        assertTrue(env.timestamp() > 0);
        assertEquals(0, env.data().result());
        assertNull(env.data().output());
        assertNull(env.gateway());
    }

    @Test
    @DisplayName("shouldOkWithOutput_whenStaticFactory：ok(tid,bid,method,output) 构造有 output 成功回复")
    void testOkWithOutput() {
        Map<String, String> output = Map.of("k", "v");
        ReplyEnvelope env = ReplyEnvelope.ok("t", "b", "config", output);
        assertEquals(0, env.data().result());
        assertEquals(output, env.data().output());
    }

    @Test
    @DisplayName("shouldOkSerialize_whenStaticFactory：ok 序列化后 result=0，无 output 时不含 output 字段")
    void testOkSerialize() {
        String json = MessageCodec.toJson(ReplyEnvelope.ok("t", "b", "fly_to_point"));
        assertTrue(json.contains("\"result\":0"));
        assertFalse(json.contains("\"output\""));
    }
}
