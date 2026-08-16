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

package ltd.cdmi.dji.cloudapi.sdk.wayline;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;

import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionActuatorFunc;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionGroupMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionTriggerType;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Action;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.ActionGroup;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.HoverParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.TakePhotoParam;

/**
 * {@link ActionGroupBuilder} 单元测试。
 *
 * <p>验证 actionGroupId / startIndex / endIndex 范围校验、actionTriggerParam 校验、
 * 默认 actionGroupMode、addAction 累积以及 build() 生成 {@link ActionGroup} record
 * 的字段正确性与各种异常场景。
 */
@DisplayName("ActionGroupBuilder 单元测试")
class ActionGroupBuilderTest {

    @Test
    @DisplayName("构建 ActionGroup：所有字段正确设置")
    void shouldBuildActionGroupWithAllFields() {
        ActionGroup group = new ActionGroupBuilder()
            .actionGroupId(3)
            .actionGroupStartIndex(0)
            .actionGroupEndIndex(2)
            .actionTriggerType(ActionTriggerType.REACH_POINT)
            .actionTriggerParam(5.0)
            .addAction(a -> a.actionId(0)
                .actionActuatorFunc(ActionActuatorFunc.TAKE_PHOTO)
                .actionActuatorFuncParam(new TakePhotoParam(0, "p1", "wide", 1)))
            .build();

        assertEquals(3, group.actionGroupId(), "actionGroupId 应为 3");
        assertEquals(0, group.actionGroupStartIndex(), "actionGroupStartIndex 应为 0");
        assertEquals(2, group.actionGroupEndIndex(), "actionGroupEndIndex 应为 2");
        assertEquals("sequence", group.actionGroupMode(), "默认 actionGroupMode 应为 sequence");
        assertEquals("reachPoint", group.actionTrigger().actionTriggerType(),
            "actionTriggerType 应为 reachPoint");
        assertEquals(5.0, group.actionTrigger().actionTriggerParam(),
            "actionTriggerParam 应为 5.0");
        assertEquals(1, group.actions().size(), "应包含 1 个 action");
    }

    @Test
    @DisplayName("默认 actionGroupMode 为 SEQUENCE")
    void shouldDefaultActionGroupModeToSequence() {
        ActionGroup group = new ActionGroupBuilder()
            .actionGroupId(0)
            .actionGroupStartIndex(0)
            .actionGroupEndIndex(0)
            .actionTriggerType(ActionTriggerType.REACH_POINT)
            .addAction(a -> a.actionId(0).actionActuatorFunc(ActionActuatorFunc.HOVER))
            .build();

        assertEquals("sequence", group.actionGroupMode(), "默认应为 sequence");
    }

    @Test
    @DisplayName("actionGroupMode 可设置为 PARALLEL")
    void shouldSetActionGroupModeToParallel() {
        ActionGroup group = new ActionGroupBuilder()
            .actionGroupId(0)
            .actionGroupStartIndex(0)
            .actionGroupEndIndex(0)
            .actionGroupMode(ActionGroupMode.PARALLEL)
            .actionTriggerType(ActionTriggerType.REACH_POINT)
            .addAction(a -> a.actionId(0).actionActuatorFunc(ActionActuatorFunc.HOVER))
            .build();

        assertEquals("parallel", group.actionGroupMode(), "actionGroupMode 应为 parallel");
    }

    @Test
    @DisplayName("addAction 累积多个 action")
    void shouldAccumulateMultipleActions() {
        ActionGroup group = new ActionGroupBuilder()
            .actionGroupId(0)
            .actionGroupStartIndex(0)
            .actionGroupEndIndex(0)
            .actionTriggerType(ActionTriggerType.REACH_POINT)
            .addAction(a -> a.actionId(0).actionActuatorFunc(ActionActuatorFunc.TAKE_PHOTO)
                .actionActuatorFuncParam(new TakePhotoParam(0, "p1", "wide", 1)))
            .addAction(a -> a.actionId(1).actionActuatorFunc(ActionActuatorFunc.HOVER)
                .actionActuatorFuncParam(new HoverParam(2.0)))
            .build();

        assertEquals(2, group.actions().size(), "应包含 2 个 action");
        assertEquals(0, group.actions().get(0).actionId(), "第一个 action 的 actionId 应为 0");
        assertEquals(1, group.actions().get(1).actionId(), "第二个 action 的 actionId 应为 1");
    }

    @Test
    @DisplayName("actionTriggerParam 为 null 时 trigger.param 也为 null")
    void shouldAllowNullTriggerParam() {
        ActionGroup group = new ActionGroupBuilder()
            .actionGroupId(0)
            .actionGroupStartIndex(0)
            .actionGroupEndIndex(0)
            .actionTriggerType(ActionTriggerType.REACH_POINT)
            .addAction(a -> a.actionId(0).actionActuatorFunc(ActionActuatorFunc.HOVER))
            .build();

        assertNull(group.actionTrigger().actionTriggerParam(),
            "未设置 actionTriggerParam 时应为 null");
    }

    @Test
    @DisplayName("actionGroupId 边界值 0 和 65535 合法")
    void shouldAcceptBoundaryActionGroupId() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> new ActionGroupBuilder().actionGroupId(0));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> new ActionGroupBuilder().actionGroupId(65535));
    }

    @Test
    @DisplayName("actionGroupId 超出 [0,65535] 抛出 IllegalArgumentException")
    void shouldRejectActionGroupIdOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new ActionGroupBuilder().actionGroupId(-1));
        assertThrows(IllegalArgumentException.class, () -> new ActionGroupBuilder().actionGroupId(65536));
    }

    @Test
    @DisplayName("actionGroupStartIndex 超出 [0,65535] 抛出 IllegalArgumentException")
    void shouldRejectStartIndexOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new ActionGroupBuilder().actionGroupStartIndex(-1));
        assertThrows(IllegalArgumentException.class, () -> new ActionGroupBuilder().actionGroupStartIndex(65536));
    }

    @Test
    @DisplayName("actionGroupEndIndex 超出 [0,65535] 抛出 IllegalArgumentException")
    void shouldRejectEndIndexOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new ActionGroupBuilder().actionGroupEndIndex(-1));
        assertThrows(IllegalArgumentException.class, () -> new ActionGroupBuilder().actionGroupEndIndex(65536));
    }

    @Test
    @DisplayName("actionTriggerParam <= 0 抛出 IllegalArgumentException")
    void shouldRejectNonPositiveTriggerParam() {
        assertThrows(IllegalArgumentException.class, () -> new ActionGroupBuilder().actionTriggerParam(0));
        assertThrows(IllegalArgumentException.class, () -> new ActionGroupBuilder().actionTriggerParam(-1.0));
    }

    @Test
    @DisplayName("未设置 actionTriggerType 时 build() 抛出 IllegalStateException")
    void shouldThrowWhenTriggerTypeNotSet() {
        assertThrows(IllegalStateException.class, () -> new ActionGroupBuilder()
            .actionGroupId(0)
            .actionGroupStartIndex(0)
            .actionGroupEndIndex(0)
            .addAction(a -> a.actionId(0).actionActuatorFunc(ActionActuatorFunc.HOVER))
            .build());
    }

    @Test
    @DisplayName("endIndex < startIndex 时 build() 抛出 IllegalStateException")
    void shouldThrowWhenEndIndexLessThanStartIndex() {
        assertThrows(IllegalStateException.class, () -> new ActionGroupBuilder()
            .actionGroupId(0)
            .actionGroupStartIndex(2)
            .actionGroupEndIndex(1)
            .actionTriggerType(ActionTriggerType.REACH_POINT)
            .addAction(a -> a.actionId(0).actionActuatorFunc(ActionActuatorFunc.HOVER))
            .build());
    }

    @Test
    @DisplayName("无 action 时 build() 抛出 IllegalStateException")
    void shouldThrowWhenNoActions() {
        assertThrows(IllegalStateException.class, () -> new ActionGroupBuilder()
            .actionGroupId(0)
            .actionGroupStartIndex(0)
            .actionGroupEndIndex(0)
            .actionTriggerType(ActionTriggerType.REACH_POINT)
            .build());
    }

    @Test
    @DisplayName("链式调用返回 this")
    void shouldReturnSameBuilderInstance() {
        ActionGroupBuilder builder = new ActionGroupBuilder();
        assertSame(builder, builder.actionGroupId(0));
        assertSame(builder, builder.actionGroupStartIndex(0));
        assertSame(builder, builder.actionGroupEndIndex(0));
        assertSame(builder, builder.actionGroupMode(ActionGroupMode.SEQUENCE));
        assertSame(builder, builder.actionTriggerType(ActionTriggerType.REACH_POINT));
        assertSame(builder, builder.actionTriggerParam(1.0));
    }

    @Test
    @DisplayName("build() 返回的 actions 列表不可变")
    void shouldReturnImmutableActionsList() {
        ActionGroup group = new ActionGroupBuilder()
            .actionGroupId(0)
            .actionGroupStartIndex(0)
            .actionGroupEndIndex(0)
            .actionTriggerType(ActionTriggerType.REACH_POINT)
            .addAction(a -> a.actionId(0).actionActuatorFunc(ActionActuatorFunc.HOVER))
            .build();

        assertThrows(UnsupportedOperationException.class, () -> group.actions().add(
            new Action(1, "hover", null)));
    }

    @Test
    @DisplayName("使用 multipleTiming 触发类型并设置 actionTriggerParam")
    void shouldBuildWithMultipleTimingTrigger() {
        ActionGroup group = new ActionGroupBuilder()
            .actionGroupId(0)
            .actionGroupStartIndex(0)
            .actionGroupEndIndex(5)
            .actionTriggerType(ActionTriggerType.MULTIPLE_TIMING)
            .actionTriggerParam(10.0)
            .addAction(a -> a.actionId(0).actionActuatorFunc(ActionActuatorFunc.TAKE_PHOTO)
                .actionActuatorFuncParam(new TakePhotoParam(0, "p1", "wide", 1)))
            .build();

        assertEquals("multipleTiming", group.actionTrigger().actionTriggerType(),
            "actionTriggerType 应为 multipleTiming");
        assertEquals(10.0, group.actionTrigger().actionTriggerParam(),
            "actionTriggerParam 应为 10.0");
    }
}
