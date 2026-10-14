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

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link RcOsd} 的电量字段 wire key（capacity_percent）与旧 key（battery）兼容。
 */
class RcOsdTest {

    @Test
    @DisplayName("反序列化：wire key capacity_percent 绑定到 capacityPercent")
    void testDeserializeCapacityPercent() {
        RcOsd osd = MessageCodec.fromJson("{\"capacity_percent\":88}", RcOsd.class);
        assertEquals(88, osd.capacityPercent());
    }

    @Test
    @DisplayName("反序列化：旧 wire key battery 通过 @JsonAlias 兼容绑定到 capacityPercent")
    void testDeserializeLegacyBatteryAlias() {
        RcOsd osd = MessageCodec.fromJson("{\"battery\":75}", RcOsd.class);
        assertEquals(75, osd.capacityPercent());
    }

    @Test
    @DisplayName("序列化：capacityPercent → JSON 含 capacity_percent 字段")
    void testSerialize() {
        RcOsd osd = new RcOsd(null, null, 66, null, null, null, null);
        String json = MessageCodec.toJson(osd);
        assertTrue(json.contains("\"capacity_percent\":66"), "JSON 应含 capacity_percent，实际: " + json);
    }

    @Test
    @DisplayName("结构：RcOsd 不再含 modeCode 字段")
    void testNoModeCodeComponent() {
        List<String> names = Arrays.stream(RcOsd.class.getRecordComponents())
                .map(RecordComponent::getName).toList();
        assertTrue(!names.contains("modeCode"), "RcOsd 不应含 modeCode 字段，实际: " + names);
    }
}
