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

package ltd.cdmi.dji.cloudapi.sdk.command.event.system;

import ltd.cdmi.dji.cloudapi.sdk.codec.DjiMessage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证 {@link RtkCalibrationData} 的类型安全解析（O4 cmd rtk_calibration 事件）。
 *
 * <p><b>核心口径</b>：其结构 {@code output.ext.devices[] + version} 比通用
 * {@link ServiceProgressData} 更丰富，故独立建模。
 */
class RtkCalibrationDataTest {

    @Test
    @DisplayName("rtk_calibration 事件 — output{status, progress, ext{devices[], version}}")
    void testParseRtkCalibrationEvent() {
        String payload = "{\"method\":\"rtk_calibration\",\"data\":{\"result\":0,"
                + "\"output\":{\"status\":\"ok\","
                + "\"progress\":{\"percent\":100,\"current_step\":1},"
                + "\"ext\":{\"devices\":[{\"sn\":\"SN-1\",\"type\":1,\"module\":\"3\","
                + "\"result\":0,\"status\":\"ok\"}],\"version\":1}}},"
                + "\"need_reply\":0,\"tid\":\"t1\",\"bid\":\"b1\"}";
        DjiMessage<RtkCalibrationData> msg = DjiMessage.parse(payload, RtkCalibrationData.class);
        RtkCalibrationData data = msg.data();
        assertEquals(0, data.result());
        assertEquals("ok", data.output().status());
        assertEquals(100, data.output().progress().percent());
        assertEquals(1, data.output().progress().currentStep());
        assertEquals(1, data.output().ext().version());
        RtkCalibrationData.Device device = data.output().ext().devices().get(0);
        assertEquals("SN-1", device.sn());
        assertEquals(1, device.type());
        assertEquals("3", device.module());
        assertEquals(0, device.result());
        assertEquals("ok", device.status());
    }
}
