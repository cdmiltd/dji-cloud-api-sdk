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

package ltd.cdmi.dji.cloudapi.sdk.model;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link DeviceModels} 单元测试。
 *
 * <p>覆盖 {@link DeviceModels#findByDomainTypeSubType(int, int, int)} 跨枚举反查能力：
 * <ul>
 *   <li>domain=0（飞行器）→ {@link DroneModel}</li>
 *   <li>domain=2（遥控器）→ {@link RcModel}</li>
 *   <li>domain=3（机场）→ {@link DockModel}</li>
 *   <li>未知 domain 或 (type, subType) → {@link Optional#empty()}</li>
 * </ul>
 *
 * <p>测试依据：DJI Cloud API 官方文档设备型号三元组定义，见
 * <a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html">DJI 产品支持</a>。
 */
class DeviceModelsTest {

    /**
     * domain=0（飞行器）应反查到对应的 {@link DroneModel}。
     *
     * <p>Given: domain=0 的三元组 (0, 67, 0) 和 (0, 100, 1)
     * When:  调用 findByDomainTypeSubType
     * Then:  返回 M30 和 M4TD
     */
    @Test
    @DisplayName("domain=0 反查飞行器型号：M30 / M4TD")
    void testFindDroneByAircraftDomain() {
        Optional<DeviceModel> m30 = DeviceModels.findByDomainTypeSubType(0, 67, 0);
        assertTrue(m30.isPresent(), "domain=0, type=67, subType=0 应返回 M30");
        assertEquals(DroneModel.M30.toModel(), m30.get());

        Optional<DeviceModel> m4td = DeviceModels.findByDomainTypeSubType(0, 100, 1);
        assertTrue(m4td.isPresent(), "domain=0, type=100, subType=1 应返回 M4TD");
        assertEquals(DroneModel.M4TD.toModel(), m4td.get());
    }

    /**
     * domain=2（遥控器）应反查到对应的 {@link RcModel}。
     *
     * <p>Given: domain=2 的三元组 (2, 119, 0) 和 (2, 174, 0)
     * When:  调用 findByDomainTypeSubType
     * Then:  返回 RC_PLUS 和 RC_PLUS_2
     */
    @Test
    @DisplayName("domain=2 反查遥控器型号：RC_PLUS / RC_PLUS_2")
    void testFindControllerByControllerDomain() {
        Optional<DeviceModel> rcPlus = DeviceModels.findByDomainTypeSubType(2, 119, 0);
        assertTrue(rcPlus.isPresent(), "domain=2, type=119, subType=0 应返回 RC_PLUS");
        assertEquals(RcModel.RC_PLUS.toModel(), rcPlus.get());

        Optional<DeviceModel> rcPlus2 = DeviceModels.findByDomainTypeSubType(2, 174, 0);
        assertTrue(rcPlus2.isPresent(), "domain=2, type=174, subType=0 应返回 RC_PLUS_2");
        assertEquals(RcModel.RC_PLUS_2.toModel(), rcPlus2.get());
    }

    /**
     * domain=3（机场）应反查到对应的 {@link DockModel}。
     *
     * <p>Given: domain=3 的三元组 (3, 1, 0) 和 (3, 3, 0)
     * When:  调用 findByDomainTypeSubType
     * Then:  返回 DOCK1 和 DOCK3
     */
    @Test
    @DisplayName("domain=3 反查机场型号：DOCK1 / DOCK3")
    void testFindDockByDockDomain() {
        Optional<DeviceModel> dock1 = DeviceModels.findByDomainTypeSubType(3, 1, 0);
        assertTrue(dock1.isPresent(), "domain=3, type=1, subType=0 应返回 DOCK1");
        assertEquals(DockModel.DOCK1.toModel(), dock1.get());

        Optional<DeviceModel> dock3 = DeviceModels.findByDomainTypeSubType(3, 3, 0);
        assertTrue(dock3.isPresent(), "domain=3, type=3, subType=0 应返回 DOCK3");
        assertEquals(DockModel.DOCK3.toModel(), dock3.get());
    }

    /**
     * 未知 domain（DJI 协议未定义的值）应返回 {@link Optional#empty()}。
     *
     * <p>Given: domain=1（协议未定义）、domain=99（非法）、domain=-1（非法）
     * When:  调用 findByDomainTypeSubType
     * Then:  返回 Optional.empty()
     */
    @Test
    @DisplayName("未知 domain 返回 empty：1 / 99 / -1")
    void testReturnEmptyWhenDomainUnknown() {
        assertTrue(DeviceModels.findByDomainTypeSubType(1, 0, 0).isEmpty(),
                "domain=1 未在 DJI 协议中定义，应返回 empty");
        assertTrue(DeviceModels.findByDomainTypeSubType(99, 0, 0).isEmpty(),
                "domain=99 非法，应返回 empty");
        assertTrue(DeviceModels.findByDomainTypeSubType(-1, 0, 0).isEmpty(),
                "domain=-1 非法，应返回 empty");
    }

    /**
     * domain 合法但 (type, subType) 不匹配任何已知型号时，应返回 {@link Optional#empty()}。
     *
     * <p>Given: domain=0 + type=999, domain=3 + type=999
     * When:  调用 findByDomainTypeSubType
     * Then:  返回 Optional.empty()（底层 fromType 抛 IllegalArgumentException 被吞）
     */
    @Test
    @DisplayName("合法 domain + 未知 type+subType 返回 empty：0,999,0 / 3,999,0")
    void testReturnEmptyWhenTypeSubTypeUnknown() {
        assertTrue(DeviceModels.findByDomainTypeSubType(0, 999, 0).isEmpty(),
                "domain=0, type=999 不匹配任何飞行器型号，应返回 empty");
        assertTrue(DeviceModels.findByDomainTypeSubType(3, 999, 0).isEmpty(),
                "domain=3, type=999 不匹配任何机场型号，应返回 empty");
    }

    /**
     * 参数化覆盖全部 21 个型号（14 个 DroneModel + 4 个 RcModel + 3 个 DockModel），
     * 验证每个枚举常量的三元组都能通过 {@link DeviceModels#findByDomainTypeSubType} 反查到。
     *
     * <p>Given: 21 组 (domain, type, subType, expected) 覆盖全部型号
     * When:  调用 findByDomainTypeSubType
     * Then:  每个三元组反查返回对应的 DeviceModel
     */
    @ParameterizedTest
    @MethodSource("allModelsProvider")
    @DisplayName("参数化覆盖全部 21 个型号的三元组反查")
    void testCoverAllModels(int domain, int type, int subType, DeviceModel expected) {
        Optional<DeviceModel> actual = DeviceModels.findByDomainTypeSubType(domain, type, subType);
        assertTrue(actual.isPresent(),
                "domain=" + domain + ", type=" + type + ", subType=" + subType + " 应返回非空");
        assertEquals(expected, actual.get(),
                "domain=" + domain + ", type=" + type + ", subType=" + subType + " 反查结果不匹配");
    }

    /**
     * 提供 21 组 (domain, type, subType, expectedModel) 覆盖全部型号。
     * <ul>
     *   <li>14 个 DroneModel（domain=0）</li>
     *   <li>4 个 RcModel（domain=2）</li>
     *   <li>3 个 DockModel（domain=3）</li>
     * </ul>
     */
    static Stream<Arguments> allModelsProvider() {
        Stream<Arguments> drones = Stream.of(DroneModel.values())
                .map(m -> Arguments.of(m.domain(), m.type(), m.subType(), m.toModel()));
        Stream<Arguments> controllers = Stream.of(RcModel.values())
                .map(m -> Arguments.of(m.domain(), m.type(), m.subType(), m.toModel()));
        Stream<Arguments> docks = Stream.of(DockModel.values())
                .map(m -> Arguments.of(m.domain(), m.type(), m.subType(), m.toModel()));
        return Stream.concat(Stream.concat(drones, controllers), docks);
    }

    // ==================== findByShortName ====================

    /**
     * {@link DeviceModels#findByShortName(String)} 应能通过飞行器简称反查到对应的 {@link DroneModel}。
     *
     * <p>Given: 简称 "M30" / "M4TD" / "  M30  "（含前后空格）
     * When:  调用 findByShortName
     * Then:  返回 M30 / M4TD / M30（空格被 trim）
     */
    @Test
    @DisplayName("findByShortName: 飞行器简称反查 M30 / M4TD / 含空格")
    void testFindDroneByShortName() {
        Optional<DeviceModel> m30 = DeviceModels.findByShortName("M30");
        assertTrue(m30.isPresent(), "简称 \"M30\" 应返回 M30");
        assertEquals(DroneModel.M30.toModel(), m30.get());

        Optional<DeviceModel> m4td = DeviceModels.findByShortName("M4TD");
        assertTrue(m4td.isPresent(), "简称 \"M4TD\" 应返回 M4TD");
        assertEquals(DroneModel.M4TD.toModel(), m4td.get());

        Optional<DeviceModel> m30WithSpaces = DeviceModels.findByShortName("  M30  ");
        assertTrue(m30WithSpaces.isPresent(), "简称 \"  M30  \" 应 trim 后返回 M30");
        assertEquals(DroneModel.M30.toModel(), m30WithSpaces.get());
    }

    /**
     * {@link DeviceModels#findByShortName(String)} 应能通过机场简称反查到对应的 {@link DockModel}。
     *
     * <p>Given: 简称 "Dock1" / "Dock3"
     * When:  调用 findByShortName
     * Then:  返回 DOCK1 / DOCK3
     */
    @Test
    @DisplayName("findByShortName: 机场简称反查 DOCK1 / DOCK3")
    void testFindDockByShortName() {
        Optional<DeviceModel> dock1 = DeviceModels.findByShortName("Dock1");
        assertTrue(dock1.isPresent(), "简称 \"Dock1\" 应返回 DOCK1");
        assertEquals(DockModel.DOCK1.toModel(), dock1.get());

        Optional<DeviceModel> dock3 = DeviceModels.findByShortName("Dock3");
        assertTrue(dock3.isPresent(), "简称 \"Dock3\" 应返回 DOCK3");
        assertEquals(DockModel.DOCK3.toModel(), dock3.get());
    }

    /**
     * {@link DeviceModels#findByShortName(String)} 对未知简称、null、空字符串、纯空格
     * 应返回 {@link Optional#empty()}。
     *
     * <p>Given: "Unknown" / "" / null / "   "
     * When:  调用 findByShortName
     * Then:  返回 Optional.empty()
     */
    @Test
    @DisplayName("findByShortName: 未知简称/空/null/纯空格 返回 empty")
    void testReturnEmptyWhenShortNameUnknown() {
        assertTrue(DeviceModels.findByShortName("Unknown").isEmpty(),
                "未知简称 \"Unknown\" 应返回 empty");
        assertTrue(DeviceModels.findByShortName("").isEmpty(),
                "空字符串应返回 empty");
        assertTrue(DeviceModels.findByShortName(null).isEmpty(),
                "null 应返回 empty");
        assertTrue(DeviceModels.findByShortName("   ").isEmpty(),
                "纯空格应返回 empty");
    }

    /**
     * 参数化覆盖全部 21 个型号的简称反查，验证每个枚举常量的 shortName 都能被反查到。
     *
     * <p>Given: 21 组 (shortName, expectedModel) 覆盖全部型号
     * When:  调用 findByShortName
     * Then:  每个简称反查返回对应的 DeviceModel
     */
    @ParameterizedTest
    @MethodSource("allShortNamesProvider")
    @DisplayName("findByShortName: 参数化覆盖全部 21 个型号的简称反查")
    void testCoverAllShortNames(String shortName, DeviceModel expected) {
        Optional<DeviceModel> actual = DeviceModels.findByShortName(shortName);
        assertTrue(actual.isPresent(),
                "简称 \"" + shortName + "\" 应返回非空");
        assertEquals(expected, actual.get(),
                "简称 \"" + shortName + "\" 反查结果不匹配");
    }

    /**
     * 提供 21 组 (shortName, expectedModel) 覆盖全部型号简称。
     */
    static Stream<Arguments> allShortNamesProvider() {
        Stream<Arguments> drones = Stream.of(DroneModel.values())
                .map(m -> Arguments.of(m.shortName(), m.toModel()));
        Stream<Arguments> controllers = Stream.of(RcModel.values())
                .map(m -> Arguments.of(m.shortName(), m.toModel()));
        Stream<Arguments> docks = Stream.of(DockModel.values())
                .map(m -> Arguments.of(m.shortName(), m.toModel()));
        return Stream.concat(Stream.concat(drones, controllers), docks);
    }
}
