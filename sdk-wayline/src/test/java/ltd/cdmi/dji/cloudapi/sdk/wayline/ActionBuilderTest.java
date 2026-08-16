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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionActuatorFunc;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Action;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.HoverParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.TakePhotoParam;

/**
 * {@link ActionBuilder} 单元测试。
 *
 * <p>验证 actionId 范围校验、actionActuatorFunc / actionActuatorFuncParam 设置、
 * 链式调用以及 build() 生成 {@link Action} record 的字段正确性。
 */
@DisplayName("ActionBuilder 单元测试")
class ActionBuilderTest {

    @Test
    @DisplayName("构建 Action：所有字段正确设置")
    void shouldBuildActionWithAllFields() {
        TakePhotoParam param = new TakePhotoParam(0, "point1", "wide", 1);
        Action action = new ActionBuilder()
            .actionId(5)
            .actionActuatorFunc(ActionActuatorFunc.TAKE_PHOTO)
            .actionActuatorFuncParam(param)
            .build();

        assertEquals(5, action.actionId(), "actionId 应为 5");
        assertEquals("takePhoto", action.actionActuatorFunc(),
            "actionActuatorFunc 应为枚举的 code 值");
        assertSame(param, action.actionActuatorFuncParam(),
            "actionActuatorFuncParam 应为传入的对象");
    }

    @Test
    @DisplayName("actionActuatorFunc 转换为枚举 code 字符串")
    void shouldConvertFuncEnumToCode() {
        Action action = new ActionBuilder()
            .actionId(0)
            .actionActuatorFunc(ActionActuatorFunc.GIMBAL_ROTATE)
            .actionActuatorFuncParam(new HoverParam(3.0))
            .build();

        assertEquals("gimbalRotate", action.actionActuatorFunc(),
            "actionActuatorFunc 应为 gimbalRotate");
    }

    @Test
    @DisplayName("actionActuatorFuncParam 可为 null")
    void shouldAllowNullParam() {
        Action action = new ActionBuilder()
            .actionId(0)
            .actionActuatorFunc(ActionActuatorFunc.HOVER)
            .build();

        assertNull(action.actionActuatorFuncParam(), "param 默认应为 null");
    }

    @Test
    @DisplayName("actionId 边界值 0 和 65535 合法")
    void shouldAcceptBoundaryActionId() {
        assertDoesNotThrow(() -> new ActionBuilder().actionId(0)
            .actionActuatorFunc(ActionActuatorFunc.HOVER).build());
        assertDoesNotThrow(() -> new ActionBuilder().actionId(65535)
            .actionActuatorFunc(ActionActuatorFunc.HOVER).build());
    }

    @Test
    @DisplayName("actionId 为负数抛出 IllegalArgumentException")
    void shouldRejectNegativeActionId() {
        assertThrows(IllegalArgumentException.class, () -> new ActionBuilder().actionId(-1));
    }

    @Test
    @DisplayName("actionId 超过 65535 抛出 IllegalArgumentException")
    void shouldRejectActionIdAbove65535() {
        assertThrows(IllegalArgumentException.class, () -> new ActionBuilder().actionId(65536));
    }

    @Test
    @DisplayName("未设置 actionActuatorFunc 时 build() 抛出 IllegalStateException")
    void shouldThrowWhenFuncNotSet() {
        assertThrows(IllegalStateException.class, () -> new ActionBuilder().actionId(0).build());
    }

    @Test
    @DisplayName("链式调用返回 this（同一 Builder 实例）")
    void shouldReturnSameBuilderInstance() {
        ActionBuilder builder = new ActionBuilder();
        assertSame(builder, builder.actionId(1), "actionId 应返回 this");
        assertSame(builder, builder.actionActuatorFunc(ActionActuatorFunc.HOVER), "actionActuatorFunc 应返回 this");
        assertSame(builder, builder.actionActuatorFuncParam(new HoverParam(1.0)),
            "actionActuatorFuncParam 应返回 this");
    }

    @Test
    @DisplayName("默认 actionId 为 0")
    void shouldDefaultActionIdToZero() {
        Action action = new ActionBuilder()
            .actionActuatorFunc(ActionActuatorFunc.HOVER)
            .build();

        assertEquals(0, action.actionId(), "默认 actionId 应为 0");
    }
}
