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
import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.flow.RegistrationStep.ChannelType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证设备注册流程定义：{@link DockRegistrationFlow}、{@link PilotRegistrationFlow}
 * 与步骤载体 {@link RegistrationStep}。
 *
 * <p><b>核心证明</b>：
 * <ul>
 *   <li>机场与 Pilot 两种网关均定义 5 步注册序列，前 4 步走 REQUESTS 通道、
 *       第 5 步 update_topo 走 STATUS 通道</li>
 *   <li>每一步常量均挂 {@link DocUrl} 与 {@link Verified}，确保协议溯源可查</li>
 *   <li>Pilot 的 update_topo 额外挂 {@link Inferred}，标记 RC Plus 2 子设备字段为待真机验证</li>
 *   <li>{@link RegistrationStep} record 字段访问与 {@link ChannelType} 枚举完整性正确</li>
 * </ul>
 */
class RegistrationFlowTest {

    // =====================================================================
    // DockRegistrationFlow
    // =====================================================================

    @Nested
    @DisplayName("DockRegistrationFlow：机场上云 5 步注册流程")
    class DockFlow {

        @Test
        @DisplayName("steps() 返回 5 步且 totalSteps() == 5")
        void testStepsCount() {
            List<RegistrationStep> steps = DockRegistrationFlow.steps();
            assertEquals(5, steps.size());
            assertEquals(5, DockRegistrationFlow.totalSteps());
        }

        @Test
        @DisplayName("步骤按执行顺序排列：config → airport_bind_status → airport_organization_get → airport_organization_bind → update_topo")
        void testStepOrder() {
            List<RegistrationStep> steps = DockRegistrationFlow.steps();
            assertEquals("config", steps.get(0).methodName());
            assertEquals("airport_bind_status", steps.get(1).methodName());
            assertEquals("airport_organization_get", steps.get(2).methodName());
            assertEquals("airport_organization_bind", steps.get(3).methodName());
            assertEquals("update_topo", steps.get(4).methodName());
        }

        @Test
        @DisplayName("CONFIG 步骤：method=config，REQUESTS 通道，超时 3s 重试 3 次间隔 3s")
        void testConfigStep() {
            RegistrationStep s = DockRegistrationFlow.CONFIG;
            assertEquals("config", s.methodName());
            assertEquals(ChannelType.REQUESTS, s.channelType());
            assertEquals(3, s.timeoutSeconds());
            assertEquals(3, s.retryCount());
            assertEquals(3, s.retryIntervalSeconds());
        }

        @Test
        @DisplayName("AIRPORT_BIND_STATUS 步骤：REQUESTS 通道，超时 3s 不重试")
        void testBindStatusStep() {
            RegistrationStep s = DockRegistrationFlow.AIRPORT_BIND_STATUS;
            assertEquals("airport_bind_status", s.methodName());
            assertEquals(ChannelType.REQUESTS, s.channelType());
            assertEquals(3, s.timeoutSeconds());
            assertEquals(0, s.retryCount());
            assertEquals(0, s.retryIntervalSeconds());
        }

        @Test
        @DisplayName("AIRPORT_ORGANIZATION_GET 步骤：REQUESTS 通道，超时 3s 不重试")
        void testOrgGetStep() {
            RegistrationStep s = DockRegistrationFlow.AIRPORT_ORGANIZATION_GET;
            assertEquals("airport_organization_get", s.methodName());
            assertEquals(ChannelType.REQUESTS, s.channelType());
            assertEquals(3, s.timeoutSeconds());
            assertEquals(0, s.retryCount());
        }

        @Test
        @DisplayName("AIRPORT_ORGANIZATION_BIND 步骤：REQUESTS 通道，超时 3s 不重试")
        void testOrgBindStep() {
            RegistrationStep s = DockRegistrationFlow.AIRPORT_ORGANIZATION_BIND;
            assertEquals("airport_organization_bind", s.methodName());
            assertEquals(ChannelType.REQUESTS, s.channelType());
            assertEquals(3, s.timeoutSeconds());
            assertEquals(0, s.retryCount());
        }

        @Test
        @DisplayName("UPDATE_TOPO 步骤：method=update_topo，STATUS 通道（非 REQUESTS）")
        void testUpdateTopoStep() {
            RegistrationStep s = DockRegistrationFlow.UPDATE_TOPO;
            assertEquals("update_topo", s.methodName());
            assertEquals(ChannelType.STATUS, s.channelType());
            assertEquals(3, s.timeoutSeconds());
            assertEquals(0, s.retryCount());
        }

        @Test
        @DisplayName("前 4 步走 REQUESTS 通道，第 5 步走 STATUS 通道")
        void testChannelDistribution() {
            List<RegistrationStep> steps = DockRegistrationFlow.steps();
            for (int i = 0; i < 4; i++) {
                assertEquals(ChannelType.REQUESTS, steps.get(i).channelType(),
                        "步骤 " + i + " (" + steps.get(i).methodName() + ") 应走 REQUESTS 通道");
            }
            assertEquals(ChannelType.STATUS, steps.get(4).channelType(),
                    "update_topo 应走 STATUS 通道");
        }

        @Test
        @DisplayName("所有步骤 methodName 与 description 非空")
        void testNonBlankFields() {
            for (RegistrationStep s : DockRegistrationFlow.steps()) {
                assertFalse(s.methodName().isBlank(), "methodName 不应为空");
                assertFalse(s.description().isBlank(), "description 不应为空");
            }
        }

        @Test
        @DisplayName("所有步骤 methodName 全局唯一")
        void testMethodNameUnique() {
            Set<String> names = new HashSet<>();
            for (RegistrationStep s : DockRegistrationFlow.steps()) {
                assertTrue(names.add(s.methodName()), "methodName 重复: " + s.methodName());
            }
        }

        @Test
        @DisplayName("每个步骤常量均挂 @DocUrl 与 @Verified 注解（协议溯源可查）")
        void testStepAnnotations() throws NoSuchFieldException {
            assertStepHasDocUrlAndVerified(DockRegistrationFlow.class, "CONFIG");
            assertStepHasDocUrlAndVerified(DockRegistrationFlow.class, "AIRPORT_BIND_STATUS");
            assertStepHasDocUrlAndVerified(DockRegistrationFlow.class, "AIRPORT_ORGANIZATION_GET");
            assertStepHasDocUrlAndVerified(DockRegistrationFlow.class, "AIRPORT_ORGANIZATION_BIND");
            assertStepHasDocUrlAndVerified(DockRegistrationFlow.class, "UPDATE_TOPO");
        }

        @Test
        @DisplayName("DockRegistrationFlow 类未挂类级别注解（注解集中在步骤常量上）")
        void testClassHasNoAnnotations() {
            // DockRegistrationFlow 类本身无 @DocUrl/@Verified，注解集中在各步骤常量上
            // 这是源码设计：机场注册流程的协议溯源分散到每个步骤
            assertEquals(0, DockRegistrationFlow.class.getAnnotations().length,
                    "DockRegistrationFlow 类不应有注解（注解在步骤常量上）");
        }
    }

    // =====================================================================
    // PilotRegistrationFlow
    // =====================================================================

    @Nested
    @DisplayName("PilotRegistrationFlow：Pilot 上云 5 步注册流程")
    class PilotFlow {

        @Test
        @DisplayName("steps() 返回 5 步且 totalSteps() == 5")
        void testStepsCount() {
            List<RegistrationStep> steps = PilotRegistrationFlow.steps();
            assertEquals(5, steps.size());
            assertEquals(5, PilotRegistrationFlow.totalSteps());
        }

        @Test
        @DisplayName("步骤序列与机场一致：config → airport_bind_status → airport_organization_get → airport_organization_bind → update_topo")
        void testStepOrder() {
            List<RegistrationStep> steps = PilotRegistrationFlow.steps();
            assertEquals("config", steps.get(0).methodName());
            assertEquals("airport_bind_status", steps.get(1).methodName());
            assertEquals("airport_organization_get", steps.get(2).methodName());
            assertEquals("airport_organization_bind", steps.get(3).methodName());
            assertEquals("update_topo", steps.get(4).methodName());
        }

        @Test
        @DisplayName("CONFIG 步骤参数与机场一致：REQUESTS 通道，超时 3s 重试 3 次间隔 3s")
        void testConfigStep() {
            RegistrationStep s = PilotRegistrationFlow.CONFIG;
            assertEquals("config", s.methodName());
            assertEquals(ChannelType.REQUESTS, s.channelType());
            assertEquals(3, s.timeoutSeconds());
            assertEquals(3, s.retryCount());
            assertEquals(3, s.retryIntervalSeconds());
        }

        @Test
        @DisplayName("UPDATE_TOPO 步骤：method=update_topo，STATUS 通道")
        void testUpdateTopoStep() {
            RegistrationStep s = PilotRegistrationFlow.UPDATE_TOPO;
            assertEquals("update_topo", s.methodName());
            assertEquals(ChannelType.STATUS, s.channelType());
        }

        @Test
        @DisplayName("前 4 步走 REQUESTS 通道，第 5 步走 STATUS 通道")
        void testChannelDistribution() {
            List<RegistrationStep> steps = PilotRegistrationFlow.steps();
            for (int i = 0; i < 4; i++) {
                assertEquals(ChannelType.REQUESTS, steps.get(i).channelType(),
                        "步骤 " + i + " (" + steps.get(i).methodName() + ") 应走 REQUESTS 通道");
            }
            assertEquals(ChannelType.STATUS, steps.get(4).channelType());
        }

        @Test
        @DisplayName("所有步骤 methodName 与 description 非空")
        void testNonBlankFields() {
            for (RegistrationStep s : PilotRegistrationFlow.steps()) {
                assertFalse(s.methodName().isBlank(), "methodName 不应为空");
                assertFalse(s.description().isBlank(), "description 不应为空");
            }
        }

        @Test
        @DisplayName("每个步骤常量均挂 @DocUrl 与 @Verified 注解")
        void testStepAnnotations() throws NoSuchFieldException {
            assertStepHasDocUrlAndVerified(PilotRegistrationFlow.class, "CONFIG");
            assertStepHasDocUrlAndVerified(PilotRegistrationFlow.class, "AIRPORT_BIND_STATUS");
            assertStepHasDocUrlAndVerified(PilotRegistrationFlow.class, "AIRPORT_ORGANIZATION_GET");
            assertStepHasDocUrlAndVerified(PilotRegistrationFlow.class, "AIRPORT_ORGANIZATION_BIND");
            assertStepHasDocUrlAndVerified(PilotRegistrationFlow.class, "UPDATE_TOPO");
        }

        @Test
        @DisplayName("UPDATE_TOPO 额外挂 @Inferred 注解（RC Plus 2 子设备字段待真机验证）")
        void testUpdateTopoInferredAnnotation() throws NoSuchFieldException {
            Field f = PilotRegistrationFlow.class.getDeclaredField("UPDATE_TOPO");
            assertNotNull(f.getAnnotation(Inferred.class),
                    "PilotRegistrationFlow.UPDATE_TOPO 应有 @Inferred 注解标记待验证项");
            Inferred inferred = f.getAnnotation(Inferred.class);
            assertFalse(inferred.reason().isBlank(), "@Inferred 的 reason 不应为空");
            assertFalse(inferred.verifyPoint().isBlank(), "@Inferred 的 verifyPoint 不应为空");
        }

        @Test
        @DisplayName("类级别挂 @DocUrl 与 @Verified 注解")
        void testClassAnnotations() {
            assertNotNull(PilotRegistrationFlow.class.getAnnotation(DocUrl.class),
                    "PilotRegistrationFlow 应有 @DocUrl");
            assertNotNull(PilotRegistrationFlow.class.getAnnotation(Verified.class),
                    "PilotRegistrationFlow 应有 @Verified");
        }
    }

    // =====================================================================
    // Dock 与 Pilot 流程对比
    // =====================================================================

    @Nested
    @DisplayName("Dock 与 Pilot 流程对比")
    class DockVsPilot {

        @Test
        @DisplayName("两流程的 methodName 序列完全一致")
        void testSameMethodNameSequence() {
            List<RegistrationStep> dockSteps = DockRegistrationFlow.steps();
            List<RegistrationStep> pilotSteps = PilotRegistrationFlow.steps();
            assertEquals(dockSteps.size(), pilotSteps.size());
            for (int i = 0; i < dockSteps.size(); i++) {
                assertEquals(dockSteps.get(i).methodName(), pilotSteps.get(i).methodName(),
                        "第 " + i + " 步 methodName 应一致");
            }
        }

        @Test
        @DisplayName("两流程的通道分布一致：前 4 REQUESTS + 第 5 STATUS")
        void testSameChannelDistribution() {
            for (int i = 0; i < 4; i++) {
                assertEquals(DockRegistrationFlow.steps().get(i).channelType(),
                        PilotRegistrationFlow.steps().get(i).channelType());
            }
            assertEquals(DockRegistrationFlow.UPDATE_TOPO.channelType(),
                    PilotRegistrationFlow.UPDATE_TOPO.channelType());
        }
    }

    // =====================================================================
    // RegistrationStep
    // =====================================================================

    @Nested
    @DisplayName("RegistrationStep：步骤载体 record 与 ChannelType 枚举")
    class StepRecord {

        @Test
        @DisplayName("ChannelType 枚举含 3 个值：REQUESTS、REQUESTS_REPLY、STATUS")
        void testChannelTypeValues() {
            ChannelType[] values = ChannelType.values();
            assertEquals(3, values.length);
            List<String> names = new ArrayList<>();
            for (ChannelType c : values) {
                names.add(c.name());
            }
            assertTrue(names.contains("REQUESTS"), "应包含 REQUESTS");
            assertTrue(names.contains("REQUESTS_REPLY"), "应包含 REQUESTS_REPLY");
            assertTrue(names.contains("STATUS"), "应包含 STATUS");
        }

        @Test
        @DisplayName("ChannelType.valueOf 按名称反查")
        void testChannelTypeValueOf() {
            assertEquals(ChannelType.REQUESTS, ChannelType.valueOf("REQUESTS"));
            assertEquals(ChannelType.REQUESTS_REPLY, ChannelType.valueOf("REQUESTS_REPLY"));
            assertEquals(ChannelType.STATUS, ChannelType.valueOf("STATUS"));
        }

        @Test
        @DisplayName("record 访问器：6 字段全部正确暴露")
        void testRecordAccessors() {
            RegistrationStep step = new RegistrationStep(
                    "test_method",
                    "测试步骤",
                    ChannelType.STATUS,
                    10, 2, 5
            );
            assertEquals("test_method", step.methodName());
            assertEquals("测试步骤", step.description());
            assertEquals(ChannelType.STATUS, step.channelType());
            assertEquals(10, step.timeoutSeconds());
            assertEquals(2, step.retryCount());
            assertEquals(5, step.retryIntervalSeconds());
        }

        @Test
        @DisplayName("description 允许为空字符串（无强制约束）")
        void testEmptyDescriptionAllowed() {
            RegistrationStep step = new RegistrationStep(
                    "m", "", ChannelType.REQUESTS, 1, 0, 0
            );
            assertEquals("", step.description());
        }

        @Test
        @DisplayName("retryCount=0 表示不重试，retryIntervalSeconds=0 表示无间隔")
        void testNoRetrySemantics() {
            RegistrationStep step = new RegistrationStep(
                    "m", "d", ChannelType.REQUESTS, 3, 0, 0
            );
            assertEquals(0, step.retryCount());
            assertEquals(0, step.retryIntervalSeconds());
        }

        @Test
        @DisplayName("equals/hashCode 遵循 record 语义：相同字段值相等")
        void testRecordEquals() {
            RegistrationStep a = new RegistrationStep("m", "d", ChannelType.REQUESTS, 3, 0, 0);
            RegistrationStep b = new RegistrationStep("m", "d", ChannelType.REQUESTS, 3, 0, 0);
            RegistrationStep c = new RegistrationStep("m", "d", ChannelType.STATUS, 3, 0, 0);
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
            assertFalse(a.equals(c), "通道不同应不相等");
        }
    }

    // =====================================================================
    // 辅助方法
    // =====================================================================

    /**
     * 断言指定流程类的指定步骤常量同时挂有 {@link DocUrl} 与 {@link Verified} 注解。
     */
    private static void assertStepHasDocUrlAndVerified(Class<?> flowClass, String fieldName)
            throws NoSuchFieldException {
        Field f = flowClass.getDeclaredField(fieldName);
        assertNotNull(f.getAnnotation(DocUrl.class),
                flowClass.getSimpleName() + "." + fieldName + " 应有 @DocUrl");
        assertNotNull(f.getAnnotation(Verified.class),
                flowClass.getSimpleName() + "." + fieldName + " 应有 @Verified");
        DocUrl docUrl = f.getAnnotation(DocUrl.class);
        Verified verified = f.getAnnotation(Verified.class);
        assertTrue(docUrl.value().startsWith("https://"),
                "@DocUrl 应为 https URL，实际: " + docUrl.value());
        assertFalse(verified.basis().isBlank(),
                "@Verified 的 basis 不应为空");
    }
}
