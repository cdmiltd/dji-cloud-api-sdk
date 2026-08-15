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

package ltd.cdmi.dji.cloudapi.sdk.flow;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;
import ltd.cdmi.dji.cloudapi.sdk.flow.OnlineFlow.SubDevice;
import ltd.cdmi.dji.cloudapi.sdk.model.DeviceDomain;
import ltd.cdmi.dji.cloudapi.sdk.model.DeviceModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link OnlineFlow} 的常量定义、上线/下线报文构造与 envelope 结构。
 *
 * <p><b>核心证明</b>：
 * <ul>
 *   <li>METHOD 常量为 {@code "update_topo"}，与 status 通道一致</li>
 *   <li>{@code buildUpdateTopoPayload} 生成的 JSON 遵循 DJI envelope：
 *       tid/bid/method/timestamp/data 五字段齐全</li>
 *   <li>data 内层字段类型对齐 DJI 文档：domain 为 string，type/sub_type 为 int</li>
 *   <li>空子设备列表表示下线，非空列表表示上线（含飞行器子设备）</li>
 *   <li>tid/bid 每次调用生成不同 UUID（事务隔离）</li>
 *   <li>类与字段挂 {@link DocUrl} / {@link Verified} 注解</li>
 * </ul>
 */
class OnlineFlowTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** 网关设备型号（Dock3，domain=3） */
    private static final DeviceModel DOCK_MODEL = new DeviceModel(
            DeviceDomain.DOCK.value(), 3, 0, "大疆机场3", "Dock3", "1581F4BJD23456789"
    );

    /** 子设备型号（M3D 飞行器，domain=0） */
    private static final DeviceModel DRONE_MODEL = new DeviceModel(
            DeviceDomain.AIRCRAFT.value(), 89, 0, "Mavic 3D", "M3D", "1581F4BJD234567890123"
    );

    // =====================================================================
    // 常量与注解
    // =====================================================================

    @Test
    @DisplayName("METHOD 常量为 \"update_topo\"")
    void testMethodConstant() {
        assertEquals("update_topo", OnlineFlow.METHOD);
    }

    @Test
    @DisplayName("类挂 @DocUrl 与 @Verified 注解")
    void testClassAnnotations() {
        assertNotNull(OnlineFlow.class.getAnnotation(DocUrl.class),
                "OnlineFlow 应有 @DocUrl");
        assertNotNull(OnlineFlow.class.getAnnotation(Verified.class),
                "OnlineFlow 应有 @Verified");
        DocUrl docUrl = OnlineFlow.class.getAnnotation(DocUrl.class);
        assertTrue(docUrl.value().startsWith("https://"),
                "@DocUrl 应为 https URL，实际: " + docUrl.value());
        Verified verified = OnlineFlow.class.getAnnotation(Verified.class);
        assertFalse(verified.basis().isBlank(), "@Verified basis 不应为空");
    }

    // =====================================================================
    // buildUpdateTopoPayload：envelope 结构
    // =====================================================================

    @Test
    @DisplayName("上线报文 envelope 含 tid/bid/method/timestamp/data 五字段")
    void testEnvelopeStructure() throws Exception {
        String json = OnlineFlow.buildUpdateTopoPayload(
                "DOCK_SN", DOCK_MODEL, "secret", "nonce", "3.0.0.0", List.of()
        );
        JsonNode root = MAPPER.readTree(json);

        assertTrue(root.has("tid"), "应含 tid 字段");
        assertTrue(root.has("bid"), "应含 bid 字段");
        assertTrue(root.has("method"), "应含 method 字段");
        assertTrue(root.has("timestamp"), "应含 timestamp 字段");
        assertTrue(root.has("data"), "应含 data 字段");

        assertEquals("update_topo", root.get("method").asText());
        assertFalse(root.get("tid").asText().isBlank(), "tid 不应为空");
        assertFalse(root.get("bid").asText().isBlank(), "bid 不应为空");
    }

    @Test
    @DisplayName("method 字段等于 METHOD 常量")
    void testMethodInPayload() {
        String json = OnlineFlow.buildUpdateTopoPayload(
                "DOCK_SN", DOCK_MODEL, "s", "n", "v", List.of()
        );
        assertEquals(OnlineFlow.METHOD, MessageCodec.extractMethod(json));
    }

    @Test
    @DisplayName("tid/bid 为 UUID 格式，可经 MessageCodec 反查")
    void testTidBidExtractable() {
        String json = OnlineFlow.buildUpdateTopoPayload(
                "DOCK_SN", DOCK_MODEL, "s", "n", "v", List.of()
        );
        String tid = MessageCodec.extractTid(json);
        String bid = MessageCodec.extractBid(json);
        assertNotNull(tid, "tid 应可提取");
        assertNotNull(bid, "bid 应可提取");
        // UUID 格式：8-4-4-4-12 共 36 字符（含连字符）
        assertEquals(36, tid.length(), "tid 应为 UUID 格式");
        assertEquals(36, bid.length(), "bid 应为 UUID 格式");
    }

    @Test
    @DisplayName("timestamp 接近当前系统时间（500ms 容差）")
    void testTimestampCloseToNow() throws Exception {
        long before = System.currentTimeMillis();
        String json = OnlineFlow.buildUpdateTopoPayload(
                "DOCK_SN", DOCK_MODEL, "s", "n", "v", List.of()
        );
        long after = System.currentTimeMillis();
        JsonNode root = MAPPER.readTree(json);
        long ts = root.get("timestamp").asLong();
        assertTrue(ts >= before && ts <= after,
                "timestamp 应在 [before, after] 区间内，实际: " + ts + ", before=" + before + ", after=" + after);
    }

    @Test
    @DisplayName("两次调用生成不同的 tid/bid（UUID 事务隔离）")
    void testDifferentTidBidPerCall() {
        String json1 = OnlineFlow.buildUpdateTopoPayload(
                "DOCK_SN", DOCK_MODEL, "s", "n", "v", List.of()
        );
        String json2 = OnlineFlow.buildUpdateTopoPayload(
                "DOCK_SN", DOCK_MODEL, "s", "n", "v", List.of()
        );
        assertNotEquals(MessageCodec.extractTid(json1), MessageCodec.extractTid(json2),
                "两次调用的 tid 应不同");
        assertNotEquals(MessageCodec.extractBid(json1), MessageCodec.extractBid(json2),
                "两次调用的 bid 应不同");
    }

    // =====================================================================
    // buildUpdateTopoPayload：data 内层字段
    // =====================================================================

    @Test
    @DisplayName("data 内层含 domain(string)/type(int)/sub_type(int)/device_secret/nonce/thing_version/sub_devices")
    void testDataFields() throws Exception {
        String json = OnlineFlow.buildUpdateTopoPayload(
                "DOCK_SN", DOCK_MODEL, "GATEWAY_SECRET", "GATEWAY_NONCE", "3.0.0.0", List.of()
        );
        JsonNode data = MAPPER.readTree(json).get("data");

        // DJI 文档：domain 为 string，type/sub_type 为 int
        assertTrue(data.get("domain").isTextual(), "domain 应为 string 类型");
        assertEquals("3", data.get("domain").asText(), "Dock domain=3 应转为字符串 \"3\"");
        assertTrue(data.get("type").isInt(), "type 应为 int 类型");
        assertEquals(3, data.get("type").asInt());
        assertTrue(data.get("sub_type").isInt(), "sub_type 应为 int 类型");
        assertEquals(0, data.get("sub_type").asInt());

        assertEquals("GATEWAY_SECRET", data.get("device_secret").asText());
        assertEquals("GATEWAY_NONCE", data.get("nonce").asText());
        assertEquals("3.0.0.0", data.get("thing_version").asText());
        assertTrue(data.get("sub_devices").isArray(), "sub_devices 应为数组");
    }

    @Test
    @DisplayName("下线场景：sub_devices 为空数组")
    void testOfflineEmptySubDevices() throws Exception {
        String json = OnlineFlow.buildUpdateTopoPayload(
                "DOCK_SN", DOCK_MODEL, "s", "n", "v", List.of()
        );
        JsonNode subDevices = MAPPER.readTree(json).get("data").get("sub_devices");
        assertTrue(subDevices.isArray());
        assertEquals(0, subDevices.size(), "下线场景 sub_devices 应为空数组");
    }

    @Test
    @DisplayName("下线场景：sub_devices 传 null 等价于空列表")
    void testOfflineNullSubDevices() throws Exception {
        String json = OnlineFlow.buildUpdateTopoPayload(
                "DOCK_SN", DOCK_MODEL, "s", "n", "v", null
        );
        JsonNode subDevices = MAPPER.readTree(json).get("data").get("sub_devices");
        assertTrue(subDevices.isArray());
        assertEquals(0, subDevices.size(), "null sub_devices 应序列化为空数组");
    }

    // =====================================================================
    // buildUpdateTopoPayload：上线场景（含子设备）
    // =====================================================================

    @Test
    @DisplayName("上线场景：sub_devices 含 1 个飞行器子设备，全部字段正确序列化")
    void testOnlineWithSubDevice() throws Exception {
        SubDevice drone = new SubDevice(
                "DRONE_SN", DRONE_MODEL, "A", "DRONE_SECRET", "DRONE_NONCE", "3.0.0.0"
        );
        String json = OnlineFlow.buildUpdateTopoPayload(
                "DOCK_SN", DOCK_MODEL, "G_SECRET", "G_NONCE", "3.0.0.0", List.of(drone)
        );
        JsonNode subDevices = MAPPER.readTree(json).get("data").get("sub_devices");
        assertEquals(1, subDevices.size(), "应含 1 个子设备");

        JsonNode sub = subDevices.get(0);
        assertEquals("DRONE_SN", sub.get("sn").asText());
        // 子设备 domain 也应为 string
        assertTrue(sub.get("domain").isTextual(), "子设备 domain 应为 string");
        assertEquals("0", sub.get("domain").asText(), "飞行器 domain=0 应转为字符串 \"0\"");
        assertTrue(sub.get("type").isInt(), "子设备 type 应为 int");
        assertEquals(89, sub.get("type").asInt());
        assertTrue(sub.get("sub_type").isInt(), "子设备 sub_type 应为 int");
        assertEquals(0, sub.get("sub_type").asInt());
        assertEquals("A", sub.get("index").asText());
        assertEquals("DRONE_SECRET", sub.get("device_secret").asText());
        assertEquals("DRONE_NONCE", sub.get("nonce").asText());
        assertEquals("3.0.0.0", sub.get("thing_version").asText());
    }

    @Test
    @DisplayName("上线场景：多个子设备全部序列化")
    void testMultipleSubDevices() throws Exception {
        SubDevice d1 = new SubDevice("SN1", DRONE_MODEL, "A", "S1", "N1", "v1");
        SubDevice d2 = new SubDevice("SN2", DRONE_MODEL, "B", "S2", "N2", "v2");
        String json = OnlineFlow.buildUpdateTopoPayload(
                "DOCK_SN", DOCK_MODEL, "s", "n", "v", List.of(d1, d2)
        );
        JsonNode subDevices = MAPPER.readTree(json).get("data").get("sub_devices");
        assertEquals(2, subDevices.size());
        assertEquals("SN1", subDevices.get(0).get("sn").asText());
        assertEquals("SN2", subDevices.get(1).get("sn").asText());
        assertEquals("A", subDevices.get(0).get("index").asText());
        assertEquals("B", subDevices.get(1).get("index").asText());
    }

    // =====================================================================
    // SubDevice record
    // =====================================================================

    @Test
    @DisplayName("SubDevice record 访问器：6 字段全部正确暴露")
    void testSubDeviceAccessors() {
        SubDevice sd = new SubDevice(
                "SN", DRONE_MODEL, "A", "SECRET", "NONCE", "3.0.0.0"
        );
        assertEquals("SN", sd.sn());
        assertEquals(DRONE_MODEL, sd.model());
        assertEquals("A", sd.index());
        assertEquals("SECRET", sd.deviceSecret());
        assertEquals("NONCE", sd.nonce());
        assertEquals("3.0.0.0", sd.thingVersion());
    }

    @Test
    @DisplayName("SubDevice equals/hashCode 遵循 record 语义")
    void testSubDeviceEquals() {
        SubDevice a = new SubDevice("SN", DRONE_MODEL, "A", "S", "N", "v");
        SubDevice b = new SubDevice("SN", DRONE_MODEL, "A", "S", "N", "v");
        SubDevice c = new SubDevice("SN", DRONE_MODEL, "B", "S", "N", "v");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c, "index 不同应不相等");
    }

    // =====================================================================
    // 网关 SN 与 topic 关系
    // =====================================================================

    @Test
    @DisplayName("网关 SN 不进入 payload body（属于 topic 而非 data 字段）")
    void testGatewaySnNotInPayload() throws Exception {
        String json = OnlineFlow.buildUpdateTopoPayload(
                "GATEWAY_SN_IN_TOPIC", DOCK_MODEL, "s", "n", "v", List.of()
        );
        JsonNode root = MAPPER.readTree(json);
        JsonNode data = root.get("data");
        // 网关 SN 不应出现在顶层或 data 内层
        assertFalse(root.toString().contains("GATEWAY_SN_IN_TOPIC"),
                "网关 SN 应只在 topic 中，不应出现在 payload body: " + json);
    }
}
