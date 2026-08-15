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

import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionActuatorFunc;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Action;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.ActionActuatorFuncParam;

/**
 * {@link Action} 的 Builder，用于构造单个航点动作。
 *
 * <p>由 {@link ActionGroupBuilder#addAction(java.util.function.Consumer)} 回调创建，
 * 链式配置后通过 {@link #build()} 生成 {@link Action} record。
 *
 * <p>示例：
 * <pre>{@code
 * .addAction(a -> a
 *     .actionId(0)
 *     .actionActuatorFunc(ActionActuatorFunc.TAKE_PHOTO)
 *     .actionActuatorFuncParam(new TakePhotoParam(0, "point1", "wide", 1)))
 * }</pre>
 *
 * @see ActionGroupBuilder
 * @see Action
 */
public final class ActionBuilder {

    private int actionId;
    private ActionActuatorFunc func;
    private ActionActuatorFuncParam param;

    /**
     * 设置动作 ID。
     *
     * @param actionId 动作 ID，范围 [0, 65535]，在动作组内唯一
     * @return this
     * @throws IllegalArgumentException 如果 actionId 超出 [0, 65535]
     */
    public ActionBuilder actionId(int actionId) {
        if (actionId < 0 || actionId > 65535) {
            throw new IllegalArgumentException(
                "actionId 超出范围 [0, 65535]: " + actionId);
        }
        this.actionId = actionId;
        return this;
    }

    /**
     * 设置动作执行函数类型。
     *
     * @param func 动作类型枚举
     * @return this
     */
    public ActionBuilder actionActuatorFunc(ActionActuatorFunc func) {
        this.func = func;
        return this;
    }

    /**
     * 设置动作执行参数。
     *
     * @param param 动作参数（sealed interface 实现，如 TakePhotoParam、GimbalRotateParam 等）
     * @return this
     */
    public ActionBuilder actionActuatorFuncParam(ActionActuatorFuncParam param) {
        this.param = param;
        return this;
    }

    /**
     * 构建 {@link Action} record。
     *
     * @return 动作 record
     */
    Action build() {
        if (func == null) {
            throw new IllegalStateException("actionActuatorFunc 未设置");
        }
        return new Action(actionId, func.code(), param);
    }
}
