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

package ltd.cdmi.dji.cloudapi.sdk.command.event.peripheral;

import ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 验证 O4 空域安全事件 {@link RidInfosData} / {@link AdsbInfosData} / {@link Gnss} 的类型安全解析。
 *
 * <p><b>核心口径</b>：
 * <ul>
 *   <li>{@code gnss.time} 为字符串 {@code YYYYMMDD-HHmmss}（非时间戳）</li>
 *   <li>{@code RidTarget.speed}/{@code uprate} 为 {@code Double}</li>
 *   <li>{@code AdsbTarget.height}/{@code speed}/{@code heading}/{@code uprate} 为 {@code Integer}</li>
 * </ul>
 */
class PeripheralEventParseTest {

    private static final String GNSS_JSON = "{\"engine\":1,\"flag\":1,\"time\":\"20261014-105800\","
            + "\"lat\":30.6,\"lon\":104.0,\"alt\":500.0,\"satellite\":12,\"hdop\":0.8}";

    @Test
    @DisplayName("rid_infos — gnss.time 字符串 + RidTarget.speed/uprate Double")
    void testParseRidInfos() {
        String payload = "{\"method\":\"rid_infos\",\"data\":{"
                + "\"capacity_percent\":50,\"power_supply\":1,\"device_sn\":\"DOCK3-1\","
                + "\"gnss\":" + GNSS_JSON + ","
                + "\"rid\":[{\"osid\":\"OS-1\",\"rssi\":-60,\"fre\":1000,\"uatype\":1,\"status\":1,"
                + "\"heading\":90.0,\"speed\":5.5,\"uprate\":1.2,\"lon\":104.0,\"lat\":30.6,"
                + "\"altgeo\":100.0,\"altbaro\":99.0,\"height\":80.0,\"op_lon\":104.0,"
                + "\"op_lat\":30.6,\"op_alt\":500.0,\"uatime\":1700000000}]},"
                + "\"need_reply\":0,\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<RidInfosData> msg = DjiMessage.parse(payload, RidInfosData.class);
        RidInfosData data = msg.data();
        assertEquals(50, data.capacityPercent());
        assertEquals("DOCK3-1", data.deviceSn());
        // gnss.time 必须为字符串（非时间戳）
        assertEquals("20261014-105800", data.gnss().time());
        assertEquals(0.8, data.gnss().hdop());
        assertEquals(1, data.rid().size());
        RidInfosData.RidTarget target = data.rid().get(0);
        assertEquals("OS-1", target.osid());
        assertEquals(5.5, target.speed());
        assertEquals(1.2, target.uprate());
        assertEquals(90.0, target.heading());
    }

    @Test
    @DisplayName("adsb_infos — AdsbTarget.height/speed/heading/uprate Integer")
    void testParseAdsbInfos() {
        String payload = "{\"method\":\"adsb_infos\",\"data\":{"
                + "\"capacity_percent\":30,\"device_sn\":\"DOCK3-1\",\"power_supply\":1,"
                + "\"gnss\":" + GNSS_JSON + ","
                + "\"adsb\":[{\"icao\":\"ABC123\",\"callsign\":\"CA123\",\"height\":1000,"
                + "\"lat\":30.6,\"lon\":104.0,\"speed\":250,\"heading\":90,\"uprate\":2}]},"
                + "\"need_reply\":0,\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<AdsbInfosData> msg = DjiMessage.parse(payload, AdsbInfosData.class);
        AdsbInfosData data = msg.data();
        assertEquals(30, data.capacityPercent());
        assertNotNull(data.gnss());
        assertEquals(1, data.adsb().size());
        AdsbInfosData.AdsbTarget target = data.adsb().get(0);
        assertEquals("ABC123", target.icao());
        assertEquals(1000, target.height());
        assertEquals(250, target.speed());
        assertEquals(90, target.heading());
        assertEquals(2, target.uprate());
    }
}
