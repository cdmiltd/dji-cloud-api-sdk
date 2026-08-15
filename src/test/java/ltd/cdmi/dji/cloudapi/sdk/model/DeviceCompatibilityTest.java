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

import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link DeviceCompatibility} 单元测试。
 *
 * <p>覆盖 DJI 机场-飞行器-遥控器兼容性矩阵：
 * <ul>
 *   <li>DOCK1 ↔ M30 / M30T</li>
 *   <li>DOCK2 ↔ M3D / M3TD / M30 / M30T</li>
 *   <li>DOCK3 ↔ M4D / M4TD</li>
 *   <li>SMART_CONTROLLER_ENTERPRISE ↔ M300_RTK（回归之前的 bug 修复）</li>
 * </ul>
 *
 * <p>测试依据：DJI Cloud API 官方文档产品搭配关系，见
 * <a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html">DJI 产品支持</a>。
 */
class DeviceCompatibilityTest {

    /**
     * 验证 Dock1 兼容的飞行器型号。
     *
     * <p>Given: DockModel.DOCK1
     * When:  对各 DroneModel 调用 isCompatible
     * Then:  M30 / M30T 兼容（true），M3D / M4D 不兼容（false）
     */
    @Test
    void testDock1CompatibleDrones() {
        assertTrue(DeviceCompatibility.isCompatible(DockModel.DOCK1, DroneModel.M30),
                "Dock1 应兼容 M30");
        assertTrue(DeviceCompatibility.isCompatible(DockModel.DOCK1, DroneModel.M30T),
                "Dock1 应兼容 M30T");
        assertFalse(DeviceCompatibility.isCompatible(DockModel.DOCK1, DroneModel.M3D),
                "Dock1 不应兼容 M3D");
        assertFalse(DeviceCompatibility.isCompatible(DockModel.DOCK1, DroneModel.M4D),
                "Dock1 不应兼容 M4D");
    }

    /**
     * 验证 Dock2 兼容的飞行器型号。
     *
     * <p>Given: DockModel.DOCK2
     * When:  对各 DroneModel 调用 isCompatible
     * Then:  M3D / M3TD / M30 / M30T 兼容（true），M4D 不兼容（false）
     */
    @Test
    void testDock2CompatibleDrones() {
        assertTrue(DeviceCompatibility.isCompatible(DockModel.DOCK2, DroneModel.M3D),
                "Dock2 应兼容 M3D");
        assertTrue(DeviceCompatibility.isCompatible(DockModel.DOCK2, DroneModel.M3TD),
                "Dock2 应兼容 M3TD");
        assertTrue(DeviceCompatibility.isCompatible(DockModel.DOCK2, DroneModel.M30),
                "Dock2 应兼容 M30");
        assertTrue(DeviceCompatibility.isCompatible(DockModel.DOCK2, DroneModel.M30T),
                "Dock2 应兼容 M30T");
        assertFalse(DeviceCompatibility.isCompatible(DockModel.DOCK2, DroneModel.M4D),
                "Dock2 不应兼容 M4D");
    }

    /**
     * 验证 Dock3 兼容的飞行器型号。
     *
     * <p>Given: DockModel.DOCK3
     * When:  对各 DroneModel 调用 isCompatible
     * Then:  M4D / M4TD 兼容（true），M30 不兼容（false）
     */
    @Test
    void testDock3CompatibleDrones() {
        assertTrue(DeviceCompatibility.isCompatible(DockModel.DOCK3, DroneModel.M4D),
                "Dock3 应兼容 M4D");
        assertTrue(DeviceCompatibility.isCompatible(DockModel.DOCK3, DroneModel.M4TD),
                "Dock3 应兼容 M4TD");
        assertFalse(DeviceCompatibility.isCompatible(DockModel.DOCK3, DroneModel.M30),
                "Dock3 不应兼容 M30");
    }

    /**
     * 验证智能控（Smart Controller Enterprise）兼容的飞行器型号——回归之前的 bug 修复。
     *
     * <p>Given: ControllerModel.SMART_CONTROLLER_ENTERPRISE
     * When:  对各 DroneModel 调用 isCompatible
     * Then:  M300_RTK 兼容（true），M30 / M350_RTK 不兼容（false）
     */
    @Test
    void testSmartControllerEnterprise() {
        assertTrue(DeviceCompatibility.isCompatible(ControllerModel.SMART_CONTROLLER_ENTERPRISE, DroneModel.M300_RTK),
                "Smart Controller Enterprise 应兼容 M300_RTK");
        assertFalse(DeviceCompatibility.isCompatible(ControllerModel.SMART_CONTROLLER_ENTERPRISE, DroneModel.M30),
                "Smart Controller Enterprise 不应兼容 M30");
        assertFalse(DeviceCompatibility.isCompatible(ControllerModel.SMART_CONTROLLER_ENTERPRISE, DroneModel.M350_RTK),
                "Smart Controller Enterprise 不应兼容 M350_RTK");
    }

    /**
     * 验证未在 switch 中处理的 ControllerModel 的运行时行为。
     *
     * <p>背景：ControllerModel 当前 4 个枚举值均在 {@link DeviceCompatibility#isCompatible(ControllerModel, DroneModel)}
     * 的 switch 表达式中穷举处理，正常途径无法触发不匹配分支。本测试用 {@link Unsafe} 分配一个
     * 未初始化的 ControllerModel 实例并设置 ordinal=99（现有 ordinal 0-3），模拟"运行时枚举新增值
     * 未重新编译 switch"的极端场景，验证 switch 表达式的运行时边界行为。
     *
     * <p><b>实测发现（javap 确认）</b>：JDK 21 将 enum switch 表达式编译为
     * {@code $SwitchMap$ControllerModel[ordinal] iaload + tableswitch}（synthetic int[] 长度 4）。
     * ordinal=99 在 {@code iaload}（数组加载）阶段即越界，抛 {@link ArrayIndexOutOfBoundsException}，
     * 发生在 {@code tableswitch} 之前，且<b>无 try-catch 保护</b>转换为 IllegalStateException。
     * 故本测试断言 {@link ArrayIndexOutOfBoundsException}（实测行为），而非任务描述的 IllegalStateException。
     *
     * <p><b>待用户确认</b>：若期望对未知控器抛 IllegalStateException（语义更友好的保护），
     * 需在 {@link DeviceCompatibility#isCompatible(ControllerModel, DroneModel)} 的 switch 表达式
     * 外加 {@code try { ... } catch (ArrayIndexOutOfBoundsException | NullPointerException e)
     * { throw new IllegalStateException("Unknown controller", e); }}，或在调用方校验。
     * 当前实现依赖 JDK 默认行为，本测试如实反映。
     *
     * <p>Given: 一个 ordinal=99、不等于任何真实枚举常量的伪 ControllerModel 实例
     * When:  调用 DeviceCompatibility.isCompatible(伪实例, M30)
     * Then:  抛 ArrayIndexOutOfBoundsException（synthetic switch table 越界，JDK 21 实测）
     *
     * <p><b>运行注意</b>：获取 {@code sun.misc.Unsafe.theUnsafe} 需反射访问 jdk.unsupported 模块
     * 的私有字段，运行此测试需 JVM 参数
     * {@code --add-opens jdk.unsupported/sun.misc=ALL-UNNAMED}（javac 编译不需此参数）。
     */
    @Test
    void testUnknownControllerThrows() {
        ControllerModel unknown = createUnknownController();
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> DeviceCompatibility.isCompatible(unknown, DroneModel.M30),
                "未在 switch 中处理的 ControllerModel（ordinal=99）应抛 ArrayIndexOutOfBoundsException "
                        + "（synthetic switch table 越界，JDK 21 javap 确认 iaload 无保护）");
    }

    /**
     * 创建一个未初始化的 ControllerModel 实例并设置 ordinal=99。
     *
     * <p>allocateInstance 不调用构造器，对象 name=null、ordinal=0（默认）。
     * 通过 Unsafe 直接写 ordinal 字段偏移设为 99（现有 ordinal 0-3），使 switch 表达式
     * 的 synthetic switch table（{@code $SwitchMap$ControllerModel}，长度 4）在 iaload 时越界。
     */
    @SuppressWarnings("unchecked")
    private static ControllerModel createUnknownController() {
        try {
            Unsafe unsafe = getUnsafe();
            Object fake = unsafe.allocateInstance(ControllerModel.class);
            long ordinalOffset = unsafe.objectFieldOffset(Enum.class.getDeclaredField("ordinal"));
            unsafe.putInt(fake, ordinalOffset, 99);
            return (ControllerModel) fake;
        } catch (Exception e) {
            // 若 Unsafe 获取失败（如 JVM 限制反射访问），转为 AssertionError 让测试明确失败而非静默跳过
            throw new AssertionError("无法创建伪 ControllerModel 实例，可能缺少 JVM 参数 "
                    + "--add-opens jdk.unsupported/sun.misc=ALL-UNNAMED", e);
        }
    }

    /**
     * 获取 sun.misc.Unsafe 单例（反射访问 theUnsafe 静态字段）。
     */
    private static Unsafe getUnsafe() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe) f.get(null);
    }
}
