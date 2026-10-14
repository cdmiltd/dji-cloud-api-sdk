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

package ltd.cdmi.dji.cloudapi.sdk.command.service.debug;

import ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证 O4 cmd 新增服务 {@link LowPowerSwitchRequest} / {@link LowPowerSwitchReply} 的类型安全解析。
 */
class LowPowerSwitchParseTest {

    @Test
    @DisplayName("low_power_switch 请求 — action 字段")
    void testParseLowPowerSwitchRequest() {
        String payload = "{\"method\":\"low_power_switch\",\"data\":{\"action\":1},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<LowPowerSwitchRequest> msg = DjiMessage.parse(payload, LowPowerSwitchRequest.class);
        assertEquals(1, msg.data().action());
    }

    @Test
    @DisplayName("low_power_switch 回复 — result + output.status（text）")
    void testParseLowPowerSwitchReply() {
        String payload = "{\"method\":\"low_power_switch\","
                + "\"data\":{\"result\":0,\"output\":{\"status\":\"ok\"}},"
                + "\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<LowPowerSwitchReply> msg = DjiMessage.parse(payload, LowPowerSwitchReply.class);
        assertEquals(0, msg.data().result());
        assertEquals("ok", msg.data().output().status());
    }
}
