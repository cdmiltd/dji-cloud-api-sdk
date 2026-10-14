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

package ltd.cdmi.dji.cloudapi.sdk.command.service.flysafe;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 v1.18 新增解禁证书（flysafe）POJO 的 wire key 与往返闭环。
 */
class FlysafeUnlockTest {

    @Test
    @DisplayName("UnlockLicenseListRequest：device_model_domain")
    void testListRequest() {
        UnlockLicenseListRequest req = new UnlockLicenseListRequest(3);
        String json = MessageCodec.toJson(req);
        assertTrue(json.contains("\"device_model_domain\":3"), json);
        assertEquals(req, MessageCodec.fromJson(json, UnlockLicenseListRequest.class));
    }

    @Test
    @DisplayName("各解禁子结构：字段 wire key")
    void testUnlockSubStructures() {
        assertEquals(new AreaUnlock(List.of(1, 2)), MessageCodec.fromJson("{\"area_ids\":[1,2]}", AreaUnlock.class));
        assertEquals(new CircleUnlock(100, 30.0, 120.0, 50),
                MessageCodec.fromJson("{\"radius\":100,\"latitude\":30.0,\"longitude\":120.0,\"height\":50}", CircleUnlock.class));
        assertEquals(new CountryUnlock(156, 120),
                MessageCodec.fromJson("{\"country_number\":156,\"height\":120}", CountryUnlock.class));
        assertEquals(new HeightUnlock(120), MessageCodec.fromJson("{\"height\":120}", HeightUnlock.class));
        assertEquals(new PolygonUnlock(List.of(new PolygonPoint(30.0, 120.0))),
                MessageCodec.fromJson("{\"points\":[{\"latitude\":30.0,\"longitude\":120.0}]}", PolygonUnlock.class));
        assertEquals(new RidUnlock(2), MessageCodec.fromJson("{\"level\":2}", RidUnlock.class));
        assertNotNull(new PowerUnlock());
        String powerJson = MessageCodec.toJson(new PowerUnlock());
        assertEquals("{}", powerJson.replaceAll("\\s", ""));
    }

    @Test
    @DisplayName("CommonFields：license_id/device_sn/begin_time/user_only 等 wire key")
    void testCommonFields() {
        CommonFields cf = new CommonFields(9, "证书A", 1, 2, "uid", "SN123", 1000, 2000, true, true);
        String json = MessageCodec.toJson(cf);
        assertTrue(json.contains("\"license_id\":9"), json);
        assertTrue(json.contains("\"device_sn\":\"SN123\""), json);
        assertTrue(json.contains("\"begin_time\":1000"), json);
        assertTrue(json.contains("\"user_only\":true"), json);
        assertEquals(cf, MessageCodec.fromJson(json, CommonFields.class));
    }

    @Test
    @DisplayName("License：通用字段 + 类型专属子结构")
    void testLicense() {
        License license = new License(
                new CommonFields(1, "L", 1, 0, "u", "sn", 0, 0, false, true),
                null,
                new CircleUnlock(100, 30.0, 120.0, 50),
                null, null, null, null, null);
        String json = MessageCodec.toJson(license);
        assertTrue(json.contains("\"common_fields\""), json);
        assertTrue(json.contains("\"circle_unlock\""), json);
        assertEquals(license, MessageCodec.fromJson(json, License.class));
    }

    @Test
    @DisplayName("UnlockLicenseListReply：result + device_model_domain + consistence + licenses")
    void testListReply() {
        UnlockLicenseListReply reply = new UnlockLicenseListReply(0, 3, true,
                List.of(new License(new CommonFields(1, "L", 0, 0, "u", "sn", 0, 0, false, true),
                        new AreaUnlock(List.of(1)), null, null, null, null, null, null)));
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":0"), json);
        assertTrue(json.contains("\"device_model_domain\":3"), json);
        assertTrue(json.contains("\"consistence\":true"), json);
        assertTrue(json.contains("\"licenses\""), json);
        assertEquals(reply, MessageCodec.fromJson(json, UnlockLicenseListReply.class));
    }

    @Test
    @DisplayName("UnlockLicenseSwitchReply：result + license_id")
    void testSwitchReply() {
        UnlockLicenseSwitchReply reply = new UnlockLicenseSwitchReply(0, 7);
        String json = MessageCodec.toJson(reply);
        assertTrue(json.contains("\"result\":0"), json);
        assertTrue(json.contains("\"license_id\":7"), json);
        assertEquals(reply, MessageCodec.fromJson(json, UnlockLicenseSwitchReply.class));
    }
}
