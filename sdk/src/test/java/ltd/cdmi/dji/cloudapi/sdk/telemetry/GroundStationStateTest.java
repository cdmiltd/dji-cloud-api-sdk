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

package ltd.cdmi.dji.cloudapi.sdk.telemetry;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link GroundStationState} record 解析测试。
 *
 * <p>验证 O4 地面站 state 的 snake_case ↔ camelCase 映射，以及外设/RTCM/图传拓扑等嵌套结构。
 */
class GroundStationStateTest {

    private static final String SAMPLE = "{"
            + "\"electric_supply_mode\":1,\"is_beidou_version\":1,"
            + "\"peripherals_info\":[{\"device_sn\":\"sn1\",\"name\":\"cam\",\"port\":0,\"type\":1,"
            + "\"ip_addr\":\"192.168.1.10\",\"need_auth\":1,\"main_device\":1}],"
            + "\"peripheral_list\":[{\"sn\":\"sn1\","
            + "\"rid\":{\"supported\":1,\"version\":\"1.0\"},"
            + "\"adsb\":{\"supported\":0,\"version\":\"\"},"
            + "\"dock_control\":{\"supported\":1,\"version\":\"2.0\"}}],"
            + "\"sub_device\":{\"device_sn\":\"sub\",\"device_model_key\":\"3-3-0\","
            + "\"device_online_status\":1,\"device_paired\":1},"
            + "\"rtcm_info\":{\"mount_point\":\"mp\",\"port\":8080,\"host\":\"host\","
            + "\"rtcm_device_type\":1,\"source_type\":3},"
            + "\"wireless_link_topo\":{\"secret_code\":[1,2,3],"
            + "\"center_node\":{\"sdr_id\":1,\"sn\":\"c\",\"model\":\"M4D\",\"device_type\":1},"
            + "\"leaf_nodes\":[{\"sdr_id\":2,\"sn\":\"l\",\"control_source_index\":1,"
            + "\"model\":\"Dock3\",\"device_type\":2}]}}";

    @Test
    @DisplayName("解析顶层字段")
    void testParseTopLevel() {
        GroundStationState state = MessageCodec.fromJson(SAMPLE, GroundStationState.class);

        assertEquals(1, state.electricSupplyMode());
        assertEquals(1, state.isBeidouVersion());
    }

    @Test
    @DisplayName("解析 peripherals_info / peripheral_list")
    void testParsePeripherals() {
        GroundStationState state = MessageCodec.fromJson(SAMPLE, GroundStationState.class);

        assertEquals(1, state.peripheralsInfo().size());
        assertEquals("sn1", state.peripheralsInfo().get(0).deviceSn());
        assertEquals(0, state.peripheralsInfo().get(0).port());
        assertEquals(1, state.peripheralsInfo().get(0).type());
        assertEquals("192.168.1.10", state.peripheralsInfo().get(0).ipAddr());

        assertEquals(1, state.peripheralList().size());
        assertEquals(1, state.peripheralList().get(0).rid().supported());
        assertEquals("2.0", state.peripheralList().get(0).dockControl().version());
    }

    @Test
    @DisplayName("解析 sub_device / rtcm_info")
    void testParseSubDeviceAndRtcm() {
        GroundStationState state = MessageCodec.fromJson(SAMPLE, GroundStationState.class);

        assertEquals("sub", state.subDevice().deviceSn());
        assertEquals("mp", state.rtcmInfo().mountPoint());
        assertEquals(8080, state.rtcmInfo().port());
        assertEquals(1, state.rtcmInfo().rtcmDeviceType());
        assertEquals(3, state.rtcmInfo().sourceType());
    }

    @Test
    @DisplayName("解析 wireless_link_topo（center_node / leaf_nodes）")
    void testParseWirelessLinkTopo() {
        GroundStationState state = MessageCodec.fromJson(SAMPLE, GroundStationState.class);

        assertEquals(3, state.wirelessLinkTopo().secretCode().size());
        assertEquals("M4D", state.wirelessLinkTopo().centerNode().model());
        assertEquals(1, state.wirelessLinkTopo().leafNodes().size());
        assertEquals(1, state.wirelessLinkTopo().leafNodes().get(0).controlSourceIndex());
        assertEquals("Dock3", state.wirelessLinkTopo().leafNodes().get(0).model());
    }

    @Test
    @DisplayName("未推送的 heading 反序列化为 null")
    void testHeadingAbsent() {
        GroundStationState state = MessageCodec.fromJson(SAMPLE, GroundStationState.class);
        assertNull(state.heading());
    }
}
