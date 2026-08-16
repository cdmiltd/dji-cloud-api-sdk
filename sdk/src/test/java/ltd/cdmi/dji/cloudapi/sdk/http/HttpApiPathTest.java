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

package ltd.cdmi.dji.cloudapi.sdk.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link HttpApiPath} 路径常量契约测试。
 *
 * <p>验证所有 public static final String 路径常量：
 * <ul>
 *   <li>不为 null/空</li>
 *   <li>以 {@code /} 开头（DJI HTTP API 路径规范）</li>
 *   <li>总数符合预期（5 个 BASE_PATH + 16 个端点 = 21）</li>
 *   <li>5 个业务域 BASE_PATH 互不相同</li>
 *   <li>完整端点路径以其所属 BASE_PATH 为前缀</li>
 *   <li>路径占位符 {@code {workspace_id}} / {@code {element_id}} /
 *       {@code {group_id}} / {@code {wayline_id}} 出现在预期端点中</li>
 * </ul>
 */
class HttpApiPathTest {

    /** 反射收集 HttpApiPath 所有 public static final String 字段值 */
    private static List<String> allPathConstants() throws IllegalAccessException {
        List<String> values = new ArrayList<>();
        for (Field f : HttpApiPath.class.getDeclaredFields()) {
            int mod = f.getModifiers();
            if (java.lang.reflect.Modifier.isPublic(mod)
                    && java.lang.reflect.Modifier.isStatic(mod)
                    && java.lang.reflect.Modifier.isFinal(mod)
                    && f.getType() == String.class) {
                values.add((String) f.get(null));
            }
        }
        return values;
    }

    // ==================== 基本契约 ====================

    @Test
    @DisplayName("所有路径常量不为 null/空，且以 / 开头")
    void testAllPathsNonBlankAndStartsWithSlash() throws IllegalAccessException {
        for (String path : allPathConstants()) {
            assertNotNull(path, "路径常量不应为 null");
            assertFalse(path.isBlank(), "路径常量不应为空串");
            assertTrue(path.startsWith("/"), "路径应以 / 开头: " + path);
        }
    }

    @Test
    @DisplayName("路径常量总数 = 21（5 BASE_PATH + 16 端点）")
    void testPathCount() throws IllegalAccessException {
        assertEquals(21, allPathConstants().size());
    }

    // ==================== BASE_PATH ====================

    @Test
    @DisplayName("5 个业务域 BASE_PATH 值符合 DJI 文档规定且互不相同")
    void testBasePathsDistinctAndCorrect() {
        assertEquals("/manage/api/v1/workspaces", HttpApiPath.MANAGE_BASE_PATH);
        assertEquals("/map/api/v1/workspaces", HttpApiPath.MAP_BASE_PATH);
        assertEquals("/media/api/v1/workspaces", HttpApiPath.MEDIA_BASE_PATH);
        assertEquals("/storage/api/v1/workspaces", HttpApiPath.STORAGE_BASE_PATH);
        assertEquals("/wayline/api/v1/workspaces", HttpApiPath.WAYLINE_BASE_PATH);
    }

    // ==================== 端点路径前缀校验 ====================

    @Test
    @DisplayName("manage 域端点以 MANAGE_BASE_PATH 为前缀")
    void testManageEndpoints() {
        assertTrue(HttpApiPath.DEVICES_TOPOLOGIES.startsWith(HttpApiPath.MANAGE_BASE_PATH));
        assertTrue(HttpApiPath.DEVICES_TOPOLOGIES.endsWith("/devices/topologies"));
    }

    @Test
    @DisplayName("map 域端点以 MAP_BASE_PATH 为前缀")
    void testMapEndpoints() {
        assertTrue(HttpApiPath.ELEMENT_GROUPS.startsWith(HttpApiPath.MAP_BASE_PATH));
        assertTrue(HttpApiPath.CREATE_ELEMENT.startsWith(HttpApiPath.MAP_BASE_PATH));
        assertTrue(HttpApiPath.UPDATE_ELEMENT.startsWith(HttpApiPath.MAP_BASE_PATH));
        assertTrue(HttpApiPath.DELETE_ELEMENT.startsWith(HttpApiPath.MAP_BASE_PATH));
    }

    @Test
    @DisplayName("media 域端点以 MEDIA_BASE_PATH 为前缀")
    void testMediaEndpoints() {
        assertTrue(HttpApiPath.FAST_UPLOAD.startsWith(HttpApiPath.MEDIA_BASE_PATH));
        assertTrue(HttpApiPath.TINY_FINGERPRINTS.startsWith(HttpApiPath.MEDIA_BASE_PATH));
        assertTrue(HttpApiPath.MEDIA_UPLOAD_CALLBACK.startsWith(HttpApiPath.MEDIA_BASE_PATH));
        assertTrue(HttpApiPath.GROUP_UPLOAD_CALLBACK.startsWith(HttpApiPath.MEDIA_BASE_PATH));
    }

    @Test
    @DisplayName("storage 域端点以 STORAGE_BASE_PATH 为前缀")
    void testStorageEndpoints() {
        assertTrue(HttpApiPath.STS.startsWith(HttpApiPath.STORAGE_BASE_PATH));
        assertTrue(HttpApiPath.STS.endsWith("/sts"));
    }

    @Test
    @DisplayName("wayline 域端点以 WAYLINE_BASE_PATH 为前缀")
    void testWaylineEndpoints() {
        assertTrue(HttpApiPath.WAYLINES.startsWith(HttpApiPath.WAYLINE_BASE_PATH));
        assertTrue(HttpApiPath.WAYLINE_URL.startsWith(HttpApiPath.WAYLINE_BASE_PATH));
        assertTrue(HttpApiPath.WAYLINE_DUPLICATE_NAMES.startsWith(HttpApiPath.WAYLINE_BASE_PATH));
        assertTrue(HttpApiPath.WAYLINE_UPLOAD_CALLBACK.startsWith(HttpApiPath.WAYLINE_BASE_PATH));
        assertTrue(HttpApiPath.ADD_FAVORITES.startsWith(HttpApiPath.WAYLINE_BASE_PATH));
        assertTrue(HttpApiPath.REMOVE_FAVORITES.startsWith(HttpApiPath.WAYLINE_BASE_PATH));
    }

    // ==================== 占位符 ====================

    @Test
    @DisplayName("所有需要 workspace_id 的端点含 {workspace_id} 占位符")
    void testWorkspaceIdPlaceholder() {
        for (String p : new String[]{
                HttpApiPath.DEVICES_TOPOLOGIES,
                HttpApiPath.ELEMENT_GROUPS,
                HttpApiPath.CREATE_ELEMENT,
                HttpApiPath.UPDATE_ELEMENT,
                HttpApiPath.DELETE_ELEMENT,
                HttpApiPath.FAST_UPLOAD,
                HttpApiPath.TINY_FINGERPRINTS,
                HttpApiPath.MEDIA_UPLOAD_CALLBACK,
                HttpApiPath.GROUP_UPLOAD_CALLBACK,
                HttpApiPath.STS,
                HttpApiPath.WAYLINES,
                HttpApiPath.WAYLINE_URL,
                HttpApiPath.WAYLINE_DUPLICATE_NAMES,
                HttpApiPath.WAYLINE_UPLOAD_CALLBACK,
                HttpApiPath.ADD_FAVORITES,
                HttpApiPath.REMOVE_FAVORITES
        }) {
            assertTrue(p.contains("{workspace_id}"), "端点应含 {workspace_id}: " + p);
        }
    }

    @Test
    @DisplayName("map 域创建元素端点含 {group_id} 占位符")
    void testGroupIdPlaceholder() {
        assertTrue(HttpApiPath.CREATE_ELEMENT.contains("{group_id}"));
    }

    @Test
    @DisplayName("map 域更新/删除端点含 {element_id} 占位符")
    void testElementIdPlaceholder() {
        assertTrue(HttpApiPath.UPDATE_ELEMENT.contains("{element_id}"));
        assertTrue(HttpApiPath.DELETE_ELEMENT.contains("{element_id}"));
    }

    @Test
    @DisplayName("wayline 域下载地址端点含 {wayline_id} 占位符")
    void testWaylineIdPlaceholder() {
        assertTrue(HttpApiPath.WAYLINE_URL.contains("{wayline_id}"));
    }

    // ==================== 构造私有 ====================

    @Test
    @DisplayName("HttpApiPath 是工具类，构造函数私有（不可实例化）")
    void testPrivateConstructor() throws NoSuchMethodException {
        // 显式定义了唯一一个无参构造，且为 private
        assertEquals(1, HttpApiPath.class.getDeclaredConstructors().length);
        java.lang.reflect.Constructor<?> c = HttpApiPath.class.getDeclaredConstructor();
        assertFalse(c.canAccess(null), "构造函数应为 private");
    }
}
