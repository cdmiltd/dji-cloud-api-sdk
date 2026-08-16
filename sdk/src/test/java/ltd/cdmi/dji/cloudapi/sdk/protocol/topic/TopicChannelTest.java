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

package ltd.cdmi.dji.cloudapi.sdk.protocol.topic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 验证 {@link TopicChannel} 枚举的后缀映射与反查。
 */
class TopicChannelTest {

    @Test
    @DisplayName("枚举总数应为 14（覆盖全部 MQTT 通道）")
    void testTotalCount() {
        assertEquals(14, TopicChannel.values().length);
    }

    @Test
    @DisplayName("fromSuffix 反查：覆盖全部 14 个通道后缀")
    void testFromSuffix() {
        for (TopicChannel expected : TopicChannel.values()) {
            assertEquals(expected, TopicChannel.fromSuffix(expected.suffix()),
                    "suffix=" + expected.suffix() + " 反查失败");
        }
    }

    @Test
    @DisplayName("fromSuffix 单段后缀反查")
    void testFromSuffixSingleSegment() {
        assertEquals(TopicChannel.OSD, TopicChannel.fromSuffix("osd"));
        assertEquals(TopicChannel.STATE, TopicChannel.fromSuffix("state"));
        assertEquals(TopicChannel.SERVICES, TopicChannel.fromSuffix("services"));
        assertEquals(TopicChannel.SERVICES_REPLY, TopicChannel.fromSuffix("services_reply"));
        assertEquals(TopicChannel.EVENTS, TopicChannel.fromSuffix("events"));
        assertEquals(TopicChannel.EVENTS_REPLY, TopicChannel.fromSuffix("events_reply"));
        assertEquals(TopicChannel.REQUESTS, TopicChannel.fromSuffix("requests"));
        assertEquals(TopicChannel.REQUESTS_REPLY, TopicChannel.fromSuffix("requests_reply"));
        assertEquals(TopicChannel.STATUS, TopicChannel.fromSuffix("status"));
        assertEquals(TopicChannel.STATUS_REPLY, TopicChannel.fromSuffix("status_reply"));
    }

    @Test
    @DisplayName("fromSuffix 多段后缀反查（含斜杠）")
    void testFromSuffixMultiSegment() {
        assertEquals(TopicChannel.DRC_UP, TopicChannel.fromSuffix("drc/up"));
        assertEquals(TopicChannel.DRC_DOWN, TopicChannel.fromSuffix("drc/down"));
        assertEquals(TopicChannel.PROPERTY_SET, TopicChannel.fromSuffix("property/set"));
        assertEquals(TopicChannel.PROPERTY_SET_REPLY, TopicChannel.fromSuffix("property/set_reply"));
    }

    @Test
    @DisplayName("fromSuffix 未知后缀抛 IllegalArgumentException")
    void testFromSuffixUnknown() {
        assertThrows(IllegalArgumentException.class, () -> TopicChannel.fromSuffix("unknown"));
        assertThrows(IllegalArgumentException.class, () -> TopicChannel.fromSuffix("drc"));
        assertThrows(IllegalArgumentException.class, () -> TopicChannel.fromSuffix("property"));
    }
}
