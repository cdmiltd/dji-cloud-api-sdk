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

package ltd.cdmi.dji.cloudapi.sdk.websocket.data;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link WsEmptyData} 空 data 承接 record 测试。
 *
 * <p>验证 device_online/offline/update_topo 三个 biz_code 的空对象 {@code {}} data
 * 可正确反序列化为空 record 实例（非 null）。
 */
class WsEmptyDataTest {

    @Test
    @DisplayName("反序列化：空对象 {} → 空 record 实例")
    void testDeserialize() {
        WsEmptyData data = MessageCodec.fromJson("{}", WsEmptyData.class);
        assertNotNull(data);
    }

    @Test
    @DisplayName("序列化：空 record → 空对象 {}")
    void testSerialize() {
        WsEmptyData data = new WsEmptyData();
        String json = MessageCodec.toJson(data);
        assertEquals("{}", json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持不变")
    void testRoundTrip() {
        WsEmptyData original = new WsEmptyData();
        String json = MessageCodec.toJson(original);
        WsEmptyData round = MessageCodec.fromJson(json, WsEmptyData.class);
        assertEquals(original, round);
    }

    @Test
    @DisplayName("FAIL_ON_UNKNOWN_PROPERTIES=false：含未知字段的 data 反序列化为空 record")
    void testUnknownFieldTolerated() {
        // 即使 data 含字段，WsEmptyData 无对应字段也忽略，仍得到空 record 实例
        WsEmptyData data = MessageCodec.fromJson("{\"foo\":\"bar\"}", WsEmptyData.class);
        assertNotNull(data);
    }

    @Test
    @DisplayName("parseWs 承接 device_online/offline/update_topo 的空 data")
    void testParseWsForEmptyDataBizCodes() {
        for (String biz : new String[]{"device_online", "device_offline", "device_update_topo"}) {
            String payload = "{\"biz_code\":\"" + biz + "\",\"version\":\"1.0\",\"timestamp\":0,\"data\":{}}";
            var msg = MessageCodec.parseWs(payload, WsEmptyData.class);
            assertEquals(biz, msg.bizCode());
            assertNotNull(msg.data(), "空对象 data 应反序列化为空 record 实例（非 null）");
        }
    }
}
