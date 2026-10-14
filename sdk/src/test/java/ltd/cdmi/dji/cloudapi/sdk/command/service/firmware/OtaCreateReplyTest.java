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

package ltd.cdmi.dji.cloudapi.sdk.command.service.firmware;

import ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.FirmwareUpgradeType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 {@link OtaCreateReply} 的类型安全解析与 {@link FirmwareUpgradeType} 枚举绑定。
 */
class OtaCreateReplyTest {

    @Test
    @DisplayName("ota_create 回复 — {result, output{status}}")
    void testParseOtaCreateReply() {
        String payload = "{\"method\":\"ota_create\","
                + "\"data\":{\"result\":0,\"output\":{\"status\":\"in_progress\"}},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<OtaCreateReply> msg = DjiMessage.parse(payload, OtaCreateReply.class);
        assertEquals(0, msg.data().result());
        assertEquals("in_progress", msg.data().output().status());
    }

    @Test
    @DisplayName("firmware_upgrade_type enum_int 反序列化为枚举")
    void testFirmwareUpgradeTypeBinding() {
        String payload = "{\"method\":\"ota_create\",\"data\":{\"devices\":["
                + "{\"sn\":\"SN-1\",\"product_version\":\"v1\",\"firmware_upgrade_type\":4}]},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<OtaCreateRequest> msg = DjiMessage.parse(payload, OtaCreateRequest.class);
        assertEquals(FirmwareUpgradeType.PSDK, msg.data().devices().get(0).firmwareUpgradeType());
    }

    @Test
    @DisplayName("未知 firmware_upgrade_type 值抛 IllegalArgumentException")
    void testFirmwareUpgradeTypeUnknownThrows() {
        assertThrows(IllegalArgumentException.class, () -> FirmwareUpgradeType.fromCode(99));
    }
}
