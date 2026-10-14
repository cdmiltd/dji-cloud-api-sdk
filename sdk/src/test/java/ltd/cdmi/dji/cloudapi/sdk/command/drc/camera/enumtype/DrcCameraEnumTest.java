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

package ltd.cdmi.dji.cloudapi.sdk.command.drc.camera.enumtype;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 DRC 相机枚举（int 值 + string 值）的 fromCode/fromValue 反查与 Jackson 往返。
 */
class DrcCameraEnumTest {

    @Test
    @DisplayName("int 枚举：fromCode 反查 + JSON round-trip")
    void testIntEnums() {
        assertEquals(0, VideoResolution.FHD_1080P.code());
        assertEquals(VideoResolution.UHD_4K, VideoResolution.fromCode(1));
        assertEquals(VideoResolution.UHD_4K, MessageCodec.fromJson("1", VideoResolution.class));

        assertEquals(PhotoResolution.R4K, PhotoResolution.fromCode(0));
        assertEquals(PhotoResolution.R8K, PhotoResolution.fromCode(4));

        assertEquals(DewarpingState.ON, DewarpingState.fromCode(1));
        assertEquals(SwitchState.OPEN, SwitchState.fromCode(1));
        assertEquals(NightMode.AUTO, NightMode.fromCode(2));
        assertEquals(FocusMode.AFC, FocusMode.fromCode(2));
        assertEquals(ExposureMode.MANUAL, ExposureMode.fromCode(4));
        assertEquals(IrMeteringMode.AREA, IrMeteringMode.fromCode(2));
        assertEquals(GimbalResetMode.PITCH_DOWN, GimbalResetMode.fromCode(3));

        assertEquals(IsoValue.ISO_FIXED, IsoValue.fromCode(255));
        assertEquals(ApertureValue.F256, ApertureValue.fromCode(25600));
        assertEquals(ShutterValue.AUTO, ShutterValue.fromCode(60));
        assertEquals(ExposureValue.FIXED, ExposureValue.fromCode(255));
    }

    @Test
    @DisplayName("int 枚举序列化 @JsonValue 输出数值")
    void testIntEnumSerialize() {
        assertEquals("1", MessageCodec.toJson(DewarpingState.ON));
        assertEquals("255", MessageCodec.toJson(IsoValue.ISO_FIXED));
    }

    @Test
    @DisplayName("int 枚举：未知值抛 IllegalArgumentException")
    void testIntEnumUnknown() {
        assertThrows(IllegalArgumentException.class, () -> DewarpingState.fromCode(9));
        assertThrows(IllegalArgumentException.class, () -> PhotoResolution.fromCode(2));
        assertThrows(IllegalArgumentException.class, () -> ShutterValue.fromCode(-1));
    }

    @Test
    @DisplayName("string 枚举：fromValue 反查 + JSON round-trip")
    void testStringEnums() {
        assertEquals("wide", CameraType.WIDE.value());
        assertEquals(CameraType.ZOOM, CameraType.fromValue("zoom"));
        assertEquals(CameraType.IR, MessageCodec.fromJson("\"ir\"", CameraType.class));
        assertEquals("\"ir\"", MessageCodec.toJson(CameraType.IR));

        assertEquals("0.7", IntervalPhoto.S0_7.value());
        assertEquals(IntervalPhoto.S60, IntervalPhoto.fromValue("60"));
        assertThrows(IllegalArgumentException.class, () -> CameraType.fromValue("mystery"));
    }
}
