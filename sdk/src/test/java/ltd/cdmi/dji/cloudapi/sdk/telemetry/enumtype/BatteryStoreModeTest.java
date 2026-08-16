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

package ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype;

import ltd.cdmi.dji.cloudapi.sdk.command.service.debug.BatteryStoreModeSwitchRequest;
import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link BatteryStoreMode} 枚举的 code 反查、描述准确性与 Jackson 双向绑定。
 *
 * <p><b>核心证明</b>：
 * <ol>
 *   <li>DJI Dock3 properties 文档定义的 2 个 battery_store_mode 数值（1=计划模式, 2=待命模式）
 *       能通过 {@link BatteryStoreMode#fromCode(int)} 反查到枚举常量</li>
 *   <li>0 / 负数 / 越界值均抛出 {@link IllegalArgumentException}（确保 0 值不混入，
 *       防止历史 POJO 文档「0=关闭」类错误被引入）</li>
 *   <li>Jackson 通过 {@link com.fasterxml.jackson.annotation.JsonValue} /
 *       {@link com.fasterxml.jackson.annotation.JsonCreator} 实现 int↔enum 双向绑定，
 *       POJO 字段可直接用类型化枚举替代原始 int（见 {@link BatteryStoreModeSwitchRequest}）</li>
 * </ol>
 */
class BatteryStoreModeTest {

    @Test
    @DisplayName("fromCode 反查 DJI 文档定义的 2 个数值（1, 2）")
    void testFromCodeAllValues() {
        assertEquals(BatteryStoreMode.PLANNING, BatteryStoreMode.fromCode(1));
        assertEquals(BatteryStoreMode.STANDBY, BatteryStoreMode.fromCode(2));
    }

    @Test
    @DisplayName("fromCode 越界数值抛出 IllegalArgumentException（含 0 值，文档无此枚举）")
    void testFromCodeUnknownThrows() {
        // DJI 文档明确 battery_store_mode 枚举从 1 开始，0 不属于合法值
        assertThrows(IllegalArgumentException.class, () -> BatteryStoreMode.fromCode(0));
        assertThrows(IllegalArgumentException.class, () -> BatteryStoreMode.fromCode(3));
        assertThrows(IllegalArgumentException.class, () -> BatteryStoreMode.fromCode(-1));
        assertThrows(IllegalArgumentException.class, () -> BatteryStoreMode.fromCode(255));
    }

    @Test
    @DisplayName("枚举总数应为 2（计划模式 + 待命模式）")
    void testTotalCount() {
        assertEquals(2, BatteryStoreMode.values().length);
    }

    @Test
    @DisplayName("code() 与 fromCode 形成双射：1→PLANNING, 2→STANDBY")
    void testCodeRoundTrip() {
        assertEquals(1, BatteryStoreMode.PLANNING.code());
        assertEquals(2, BatteryStoreMode.STANDBY.code());
        // 双向闭环
        assertEquals(BatteryStoreMode.PLANNING, BatteryStoreMode.fromCode(BatteryStoreMode.PLANNING.code()));
        assertEquals(BatteryStoreMode.STANDBY, BatteryStoreMode.fromCode(BatteryStoreMode.STANDBY.code()));
    }

    @Test
    @DisplayName("description 非空且为 DJI 文档原文")
    void testDescription() {
        assertEquals("计划模式", BatteryStoreMode.PLANNING.description());
        assertEquals("待命模式", BatteryStoreMode.STANDBY.description());
        for (BatteryStoreMode mode : BatteryStoreMode.values()) {
            assertTrue(!mode.description().isBlank());
        }
    }

    // ==================== Jackson 双向绑定（@JsonValue + @JsonCreator）====================

    @Test
    @DisplayName("Jackson 序列化：BatteryStoreModeSwitchRequest(PLANNING) → JSON 含 \"mode\":1")
    void testJacksonSerializeEnumAsIntCode() {
        BatteryStoreModeSwitchRequest req = new BatteryStoreModeSwitchRequest(BatteryStoreMode.PLANNING);
        String json = MessageCodec.toJson(req);
        // @JsonValue 使 PLANNING 序列化为 1（int 而非枚举名 "PLANNING"）
        assertTrue(json.contains("\"mode\":1"), "JSON 应包含 \"mode\":1，实际: " + json);
    }

    @Test
    @DisplayName("Jackson 反序列化：JSON {\"mode\":2} → BatteryStoreModeSwitchRequest.mode()=STANDBY")
    void testJacksonDeserializeIntCodeToEnum() {
        BatteryStoreModeSwitchRequest req =
                MessageCodec.fromJson("{\"mode\":2}", BatteryStoreModeSwitchRequest.class);
        // @JsonCreator 使 int 2 反序列化为 STANDBY（而非用枚举名匹配）
        assertEquals(BatteryStoreMode.STANDBY, req.mode());
        assertEquals(2, req.mode().code());
    }

    @Test
    @DisplayName("Jackson 双向闭环：序列化 → 反序列化保持枚举值不变")
    void testJacksonRoundTripPreservesEnum() {
        for (BatteryStoreMode original : BatteryStoreMode.values()) {
            BatteryStoreModeSwitchRequest out = new BatteryStoreModeSwitchRequest(original);
            String json = MessageCodec.toJson(out);
            BatteryStoreModeSwitchRequest back =
                    MessageCodec.fromJson(json, BatteryStoreModeSwitchRequest.class);
            assertEquals(original, back.mode(),
                    "Round-trip 失败: " + original + " → JSON " + json + " → " + back.mode());
        }
    }

    @Test
    @DisplayName("Jackson 反序列化非法 int（0）抛异常：未知枚举值不允许绑定")
    void testJacksonDeserializeInvalidIntThrows() {
        // DJI 文档明确无 0 值，Jackson 反序列化 0 应通过 @JsonCreator 调用 fromCode(0) 抛 IllegalArgumentException
        // MessageCodec.fromJson 将 Jackson 异常包装为 IllegalStateException
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"mode\":0}", BatteryStoreModeSwitchRequest.class));
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"mode\":3}", BatteryStoreModeSwitchRequest.class));
    }
}

