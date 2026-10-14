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

package ltd.cdmi.dji.cloudapi.sdk.command.service.wayline;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.AlternateLandPoint;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 v1.18 新增/改动航线相关 POJO（DockInfo/CenterNode/LeafNode/RtcmInfo/ReturnHomeReply/FlighttaskCreate*）。
 */
class WaylineV118Test {

    @Test
    @DisplayName("RtcmInfo：mount_point(text)/port(text)/rtcm_device_type/source_type wire key")
    void testRtcmInfo() {
        RtcmInfo info = new RtcmInfo("MP", "8080", "host", 1, 0);
        String json = MessageCodec.toJson(info);
        assertTrue(json.contains("\"mount_point\":\"MP\""), json);
        assertTrue(json.contains("\"port\":\"8080\""), json);
        assertTrue(json.contains("\"rtcm_device_type\":1"), json);
        assertTrue(json.contains("\"source_type\":0"), json);
        assertEquals(info, MessageCodec.fromJson(json, RtcmInfo.class));
    }

    @Test
    @DisplayName("CenterNode：sdr_id(int) + sn")
    void testCenterNode() {
        CenterNode node = new CenterNode(933765657, "SN");
        String json = MessageCodec.toJson(node);
        assertTrue(json.contains("\"sdr_id\":933765657"), json);
        assertTrue(json.contains("\"sn\":\"SN\""), json);
        assertEquals(node, MessageCodec.fromJson(json, CenterNode.class));
    }

    @Test
    @DisplayName("LeafNode：sdr_id(int) + sn + control_source_index")
    void testLeafNode() {
        LeafNode node = new LeafNode(920128532, "SN2", 1);
        String json = MessageCodec.toJson(node);
        assertTrue(json.contains("\"sdr_id\":920128532"), json);
        assertTrue(json.contains("\"control_source_index\":1"), json);
        assertEquals(node, MessageCodec.fromJson(json, LeafNode.class));
    }

    @Test
    @DisplayName("DockInfo：补 heading/home_position_is_valid/rtcm_info/alternate_land_point")
    void testDockInfo() {
        DockInfo info = new DockInfo("SN", "dock3", 0, 90.0, 1, 30.0, 120.0, 50.0,
                new RtcmInfo("MP", "8080", "host", 1, 0),
                new AlternateLandPoint(120.1, 30.1, 40.0, 1, 50.0));
        String json = MessageCodec.toJson(info);
        assertTrue(json.contains("\"heading\":90.0"), json);
        assertTrue(json.contains("\"home_position_is_valid\":1"), json);
        assertTrue(json.contains("\"rtcm_info\""), json);
        assertTrue(json.contains("\"alternate_land_point\""), json);
        assertEquals(info, MessageCodec.fromJson(json, DockInfo.class));
    }

    @Test
    @DisplayName("ReturnHomeReply：result + output{status}")
    void testReturnHomeReply() {
        ReturnHomeReply reply = new ReturnHomeReply(0, new ReturnHomeReply.Output("ok"));
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":0"), json);
        assertTrue(json.contains("\"status\":\"ok\""), json);
        assertEquals(reply, MessageCodec.fromJson(json, ReturnHomeReply.class));
        assertThrows(IllegalStateException.class,
                () -> MessageCodec.fromJson("{\"output\":{}}", ReturnHomeReply.class));
    }

    @Test
    @DisplayName("FlighttaskCreateRequest/Reply（@Deprecated）：file{url,sign} / result")
    void testFlighttaskCreate() {
        FlighttaskCreateRequest req = new FlighttaskCreateRequest("ft-1", 0,
                new FlighttaskCreateRequest.File("http://u", "sign"));
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"flight_id\":\"ft-1\""), json);
        assertTrue(json.contains("\"url\":\"http://u\""), json);
        assertTrue(json.contains("\"sign\":\"sign\""), json);
        assertEquals(req, MessageCodec.fromJson(json, FlighttaskCreateRequest.class));

        FlighttaskCreateReply reply = new FlighttaskCreateReply(0);
        assertTrue(MessageCodec.toJson(reply).contains("\"result\":0"));
        assertNull(MessageCodec.fromJson("{}", FlighttaskCreateReply.class).result());
    }
}
