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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionGroupMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionTriggerType;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Action;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.ActionGroup;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.ActionTrigger;

/**
 * {@link ActionGroup} 的 Builder，用于构造航点动作组。
 *
 * <p>由 {@link WaypointBuilder#addActionGroup(java.util.function.Consumer)} 回调创建，
 * 定义动作组 ID、起止航点索引、执行模式、触发器以及动作列表。
 *
 * <p>示例：
 * <pre>{@code
 * .addActionGroup(ag -> ag
 *     .actionGroupId(0)
 *     .actionGroupStartIndex(0)
 *     .actionGroupEndIndex(1)
 *     .actionTriggerType(ActionTriggerType.REACH_POINT)
 *     .addAction(a -> a.actionId(0).actionActuatorFunc(ActionActuatorFunc.TAKE_PHOTO)
 *         .actionActuatorFuncParam(new TakePhotoParam(0, "p1", "wide", 1))))
 * }</pre>
 *
 * @see WaypointBuilder
 * @see ActionBuilder
 * @see ActionGroup
 */
public final class ActionGroupBuilder {

    private int actionGroupId;
    private int actionGroupStartIndex;
    private int actionGroupEndIndex;
    private ActionGroupMode actionGroupMode = ActionGroupMode.SEQUENCE;
    private ActionTriggerType actionTriggerType;
    private Double actionTriggerParam;
    private final List<Action> actions = new ArrayList<>();

    /**
     * 设置动作组 ID。
     *
     * @param actionGroupId 动作组 ID，范围 [0, 65535]
     * @return this
     * @throws IllegalArgumentException 如果超出 [0, 65535]
     */
    public ActionGroupBuilder actionGroupId(int actionGroupId) {
        if (actionGroupId < 0 || actionGroupId > 65535) {
            throw new IllegalArgumentException(
                "actionGroupId 超出范围 [0, 65535]: " + actionGroupId);
        }
        this.actionGroupId = actionGroupId;
        return this;
    }

    /**
     * 设置动作组起始航点索引。
     *
     * @param index 起始索引，范围 [0, 65535]
     * @return this
     * @throws IllegalArgumentException 如果超出 [0, 65535]
     */
    public ActionGroupBuilder actionGroupStartIndex(int index) {
        if (index < 0 || index > 65535) {
            throw new IllegalArgumentException(
                "actionGroupStartIndex 超出范围 [0, 65535]: " + index);
        }
        this.actionGroupStartIndex = index;
        return this;
    }

    /**
     * 设置动作组结束航点索引。
     *
     * @param index 结束索引，必须 >= actionGroupStartIndex
     * @return this
     * @throws IllegalArgumentException 如果超出 [0, 65535] 或小于 startIndex
     */
    public ActionGroupBuilder actionGroupEndIndex(int index) {
        if (index < 0 || index > 65535) {
            throw new IllegalArgumentException(
                "actionGroupEndIndex 超出范围 [0, 65535]: " + index);
        }
        this.actionGroupEndIndex = index;
        return this;
    }

    /**
     * 设置动作组执行模式，默认 {@link ActionGroupMode#SEQUENCE}。
     *
     * @param mode 执行模式
     * @return this
     */
    public ActionGroupBuilder actionGroupMode(ActionGroupMode mode) {
        this.actionGroupMode = mode;
        return this;
    }

    /**
     * 设置动作触发类型。
     *
     * @param type 触发类型枚举
     * @return this
     */
    public ActionGroupBuilder actionTriggerType(ActionTriggerType type) {
        this.actionTriggerType = type;
        return this;
    }

    /**
     * 设置动作触发参数。
     *
     * <p>触发类型为 {@code multipleTiming} 时为时间间隔（秒），
     * {@code multipleDistance} 时为距离间隔（米），须 > 0。
     *
     * @param param 触发参数
     * @return this
     * @throws IllegalArgumentException 如果 param <= 0
     */
    public ActionGroupBuilder actionTriggerParam(double param) {
        if (param <= 0) {
            throw new IllegalArgumentException(
                "actionTriggerParam 必须 > 0: " + param);
        }
        this.actionTriggerParam = param;
        return this;
    }

    /**
     * 添加一个动作到动作组。
     *
     * @param config 动作配置回调
     * @return this
     */
    public ActionGroupBuilder addAction(Consumer<ActionBuilder> config) {
        ActionBuilder builder = new ActionBuilder();
        config.accept(builder);
        actions.add(builder.build());
        return this;
    }

    /**
     * 构建 {@link ActionGroup} record。
     *
     * @return 动作组 record
     */
    ActionGroup build() {
        if (actionTriggerType == null) {
            throw new IllegalStateException("actionTriggerType 未设置");
        }
        if (actionGroupEndIndex < actionGroupStartIndex) {
            throw new IllegalStateException(
                "actionGroupEndIndex (" + actionGroupEndIndex
                + ") < actionGroupStartIndex (" + actionGroupStartIndex + ")");
        }
        if (actions.isEmpty()) {
            throw new IllegalStateException("动作组至少需要一个 action");
        }
        ActionTrigger trigger = new ActionTrigger(
            actionTriggerType.code(), actionTriggerParam);
        return new ActionGroup(actionGroupId, actionGroupStartIndex,
            actionGroupEndIndex, actionGroupMode.code(), trigger,
            List.copyOf(actions));
    }
}
