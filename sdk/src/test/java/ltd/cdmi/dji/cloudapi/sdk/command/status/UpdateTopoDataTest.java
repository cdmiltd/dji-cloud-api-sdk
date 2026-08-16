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

package ltd.cdmi.dji.cloudapi.sdk.command.status;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.command.status.UpdateTopoData.SubDevice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link UpdateTopoData} 的 Jackson 反序列化、序列化、往返闭环与必填字段校验。
 *
 * <p><b>核心证明</b>：update_topo 状态上报 data（domain/type/sub_type/device_secret/nonce/
 * sub_devices/thing_version）能反序列化为嵌套 record；sub_devices 为空列表表示下线；
 * SubDevice 的 domain/index 允许 null（RC Plus 2 场景）。
 */
class UpdateTopoDataTest {

    private static final String SAMPLE_JSON =
            "{\"domain\":\"3\",\"type\":1,\"sub_type\":0,\"device_secret\":\"SECRET\","
            + "\"nonce\":\"NONCE001\","
            + "\"sub_devices\":[{\"sn\":\"DRONE_SN\",\"domain\":\"0\",\"type\":89,\"sub_type\":0,"
            + "\"index\":\"A\",\"device_secret\":\"DSECRET\",\"nonce\":\"DNONCE\","
            + "\"thing_version\":\"4.5.0\"}],"
            + "\"thing_version\":\"6.0.0\"}";

    @Test
    @DisplayName("反序列化：完整 JSON → 嵌套 SubDevice 列表，全部字段正确绑定")
    void testDeserialize() {
        UpdateTopoData data = MessageCodec.fromJson(SAMPLE_JSON, UpdateTopoData.class);
        assertEquals("3", data.domain());
        assertEquals(1, data.type());
        assertEquals(0, data.subType());
        assertEquals("SECRET", data.deviceSecret());
        assertEquals("NONCE001", data.nonce());
        assertEquals("6.0.0", data.thingVersion());
        List<SubDevice> subs = data.subDevices();
        assertEquals(1, subs.size());
        SubDevice sub = subs.get(0);
        assertEquals("DRONE_SN", sub.sn());
        assertEquals("0", sub.domain());
        assertEquals(89, sub.type());
        assertEquals(0, sub.subType());
        assertEquals("A", sub.index());
        assertEquals("DSECRET", sub.deviceSecret());
        assertEquals("DNONCE", sub.nonce());
        assertEquals("4.5.0", sub.thingVersion());
    }

    @Test
    @DisplayName("序列化：含嵌套 sub_devices 的 record → JSON（snake_case）")
    void testSerialize() {
        SubDevice sub = new SubDevice("DRONE_SN", "0", 89, 0, "A", "DSECRET", "DNONCE", "4.5.0");
        UpdateTopoData data = new UpdateTopoData("3", 1, 0, "SECRET", "NONCE001",
                List.of(sub), "6.0.0");
        String json = MessageCodec.toJson(data);
        assertTrue(json.contains("\"domain\":\"3\""), "JSON 应含 domain，实际: " + json);
        assertTrue(json.contains("\"device_secret\":\"SECRET\""), "JSON 应含 device_secret，实际: " + json);
        assertTrue(json.contains("\"sub_devices\":"), "JSON 应含 sub_devices，实际: " + json);
        assertTrue(json.contains("\"thing_version\":\"4.5.0\""), "JSON 应含 thing_version，实际: " + json);
        assertTrue(json.contains("\"index\":\"A\""), "JSON 应含 index，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持嵌套结构不变")
    void testRoundTrip() {
        SubDevice sub = new SubDevice("DRONE_SN", "0", 89, 0, "A", "DSECRET", "DNONCE", "4.5.0");
        UpdateTopoData original = new UpdateTopoData("3", 1, 0, "SECRET", "NONCE001",
                List.of(sub), "6.0.0");
        String json = MessageCodec.toJson(original);
        UpdateTopoData back = MessageCodec.fromJson(json, UpdateTopoData.class);
        assertEquals("3", back.domain());
        assertEquals(1, back.type());
        assertEquals("6.0.0", back.thingVersion());
        SubDevice bd = back.subDevices().get(0);
        assertEquals("DRONE_SN", bd.sn());
        assertEquals("0", bd.domain());
        assertEquals("A", bd.index());
        assertEquals("4.5.0", bd.thingVersion());
    }

    @Test
    @DisplayName("下线场景：sub_devices 为空列表")
    void testOfflineEmptySubDevices() {
        String json = "{\"domain\":\"3\",\"type\":1,\"sub_type\":0,\"device_secret\":\"S\","
                + "\"nonce\":\"N\",\"sub_devices\":[],\"thing_version\":\"6.0.0\"}";
        UpdateTopoData data = MessageCodec.fromJson(json, UpdateTopoData.class);
        assertTrue(data.subDevices().isEmpty(), "下线场景 sub_devices 应为空列表");
    }

    @Test
    @DisplayName("可空字段：SubDevice 的 domain/index 省略时为 null（RC Plus 2 场景）")
    void testSubDeviceNullableDomainIndex() {
        String json = "{\"domain\":\"3\",\"type\":1,\"sub_type\":0,\"device_secret\":\"S\","
                + "\"nonce\":\"N\","
                + "\"sub_devices\":[{\"sn\":\"SN\",\"type\":89,\"sub_type\":0,"
                + "\"device_secret\":\"DS\",\"nonce\":\"DN\",\"thing_version\":\"4.5.0\"}],"
                + "\"thing_version\":\"6.0.0\"}";
        UpdateTopoData data = MessageCodec.fromJson(json, UpdateTopoData.class);
        SubDevice sub = data.subDevices().get(0);
        assertEquals("SN", sub.sn());
        assertNull(sub.domain(), "RC Plus 2 子设备不上报 domain，应为 null");
        assertNull(sub.index(), "RC Plus 2 子设备不上报 index，应为 null");
    }

    @Test
    @DisplayName("缺失 domain 字段：构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingDomainThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"type\":1,\"sub_type\":0,\"device_secret\":\"S\","
                        + "\"nonce\":\"N\",\"thing_version\":\"6.0.0\"}",
                        UpdateTopoData.class));
    }

    @Test
    @DisplayName("缺失 thing_version 字段：构造器抛 NPE（包装为 IllegalStateException）")
    void testMissingThingVersionThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"domain\":\"3\",\"type\":1,\"sub_type\":0,\"device_secret\":\"S\","
                        + "\"nonce\":\"N\"}",
                        UpdateTopoData.class));
    }

    @Test
    @DisplayName("SubDevice 缺失 sn 字段：构造器抛 NPE（包装为 IllegalStateException）")
    void testSubDeviceMissingSnThrowsNpe() {
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson(
                        "{\"domain\":\"3\",\"type\":1,\"sub_type\":0,\"device_secret\":\"S\","
                        + "\"nonce\":\"N\",\"sub_devices\":[{\"type\":89,\"sub_type\":0,"
                        + "\"device_secret\":\"DS\",\"nonce\":\"DN\",\"thing_version\":\"4.5.0\"}],"
                        + "\"thing_version\":\"6.0.0\"}",
                        UpdateTopoData.class));
    }
}
