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

package ltd.cdmi.dji.cloudapi.sdk.websocket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link WsBizCode} WebSocket biz_code 枚举测试。
 *
 * <p>验证：
 * <ul>
 *   <li>枚举数量 = 8（4 地图元素 + 4 态势感知）</li>
 *   <li>{@code code()} 返回 biz_code 字符串值，且 8 个 code 互不重复</li>
 *   <li>{@code description()} 非空</li>
 *   <li>{@code fromCode(String)} 双向映射：已知值 / 未知值 / null / 空白</li>
 * </ul>
 */
class WsBizCodeTest {

    @Test
    @DisplayName("枚举数量 = 8（4 地图元素 + 4 态势感知）")
    void testEnumCount() {
        assertEquals(8, WsBizCode.values().length);
    }

    @Test
    @DisplayName("code() 返回 biz_code 字符串值")
    void testCodeValues() {
        assertEquals("map_element_create", WsBizCode.MAP_ELEMENT_CREATE.code());
        assertEquals("map_element_update", WsBizCode.MAP_ELEMENT_UPDATE.code());
        assertEquals("map_element_delete", WsBizCode.MAP_ELEMENT_DELETE.code());
        assertEquals("map_group_refresh", WsBizCode.MAP_GROUP_REFRESH.code());
        assertEquals("device_osd", WsBizCode.DEVICE_OSD.code());
        assertEquals("device_online", WsBizCode.DEVICE_ONLINE.code());
        assertEquals("device_offline", WsBizCode.DEVICE_OFFLINE.code());
        assertEquals("device_update_topo", WsBizCode.DEVICE_UPDATE_TOPO.code());
    }

    @Test
    @DisplayName("code() 唯一性：8 个枚举的 code 互不重复")
    void testCodeUniqueness() {
        Set<String> codes = new HashSet<>();
        for (WsBizCode v : WsBizCode.values()) {
            assertTrue(codes.add(v.code()), "重复的 code: " + v.code());
        }
        assertEquals(8, codes.size());
    }

    @Test
    @DisplayName("description() 非空")
    void testDescriptionNonBlank() {
        for (WsBizCode v : WsBizCode.values()) {
            assertNotNull(v.description());
            assertFalse(v.description().isBlank(), v.name() + " description 不应为空");
        }
    }

    @Test
    @DisplayName("fromCode：已知 biz_code 双向映射（枚举 → code → fromCode → 枚举）")
    void testFromCodeKnown() {
        for (WsBizCode v : WsBizCode.values()) {
            Optional<WsBizCode> got = WsBizCode.fromCode(v.code());
            assertTrue(got.isPresent(), "fromCode(" + v.code() + ") 应返回非空");
            assertEquals(v, got.get());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown_code", "flight_task", "map_element_other", "device_xxx"})
    @DisplayName("fromCode：未知 biz_code 返回 empty")
    void testFromCodeUnknown(String code) {
        assertTrue(WsBizCode.fromCode(code).isEmpty(),
                "未知 biz_code 应返回 empty: " + code);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("fromCode：null / 空串 / 空白 返回 empty")
    void testFromCodeNullAndBlank(String code) {
        assertTrue(WsBizCode.fromCode(code).isEmpty(),
                "null/空串/空白 应返回 empty: [" + code + "]");
    }

    @Test
    @DisplayName("fromCode：大小写敏感（biz_code 全小写，大写不匹配）")
    void testFromCodeCaseSensitive() {
        // DJI biz_code 全小写，大写形式不应匹配
        assertTrue(WsBizCode.fromCode("DEVICE_OSD").isEmpty());
        assertTrue(WsBizCode.fromCode("Map_Element_Create").isEmpty());
    }
}
