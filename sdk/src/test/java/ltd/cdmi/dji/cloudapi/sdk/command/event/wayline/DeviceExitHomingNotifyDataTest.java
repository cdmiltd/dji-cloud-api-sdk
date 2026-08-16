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

package ltd.cdmi.dji.cloudapi.sdk.command.event.wayline;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link DeviceExitHomingNotifyData} 的 Jackson 反序列化、序列化与往返闭环。
 */
class DeviceExitHomingNotifyDataTest {

    @Test
    @DisplayName("反序列化：snake_case JSON → record（含 sn/action/reason）")
    void testDeserialize() {
        String json = "{\"sn\":\"dock-sn-001\",\"action\":1,\"reason\":0}";
        DeviceExitHomingNotifyData data = MessageCodec.fromJson(json, DeviceExitHomingNotifyData.class);
        assertEquals("dock-sn-001", data.sn());
        assertEquals(1, data.action());
        assertEquals(0, data.reason());
    }

    @Test
    @DisplayName("反序列化：action=0 退出返航场景")
    void testDeserializeExitAction() {
        String json = "{\"sn\":\"dock-sn-002\",\"action\":0,\"reason\":2}";
        DeviceExitHomingNotifyData data = MessageCodec.fromJson(json, DeviceExitHomingNotifyData.class);
        assertEquals("dock-sn-002", data.sn());
        assertEquals(0, data.action());
        assertEquals(2, data.reason());
    }

    @Test
    @DisplayName("序列化：record → snake_case JSON（含 sn/action/reason）")
    void testSerialize() {
        DeviceExitHomingNotifyData data = new DeviceExitHomingNotifyData("dock-sn-001", 1, 0);
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"sn\":\"dock-sn-001\""), "JSON 应含 sn，实际: " + json);
        assertTrue(json.contains("\"action\":1"), "JSON 应含 action，实际: " + json);
        assertTrue(json.contains("\"reason\":0"), "JSON 应含 reason，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        DeviceExitHomingNotifyData original = new DeviceExitHomingNotifyData("dock-sn-001", 1, 0);
        String json = MessageCodec.toJson(original);
        DeviceExitHomingNotifyData back = MessageCodec.fromJson(json, DeviceExitHomingNotifyData.class);
        assertEquals(original, back);
    }
}
