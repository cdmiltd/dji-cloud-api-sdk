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

package ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * 动作执行器函数（actionActuatorFunc）。
 *
 * <p>actionActuatorFunc 表示动作执行的具体功能，出现在 WPML
 * common-element 文档的 {@code Action} 节点配置中。
 *
 * @see ActionTriggerType
 * @see ActionGroupMode
 * @see GimbalRotateMode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html")
@Verified(basis = "DJI WPML common-element 文档 actionActuatorFunc 枚举定义")
public enum ActionActuatorFunc implements WpmlEnum {

    /** 单拍 */
    TAKE_PHOTO("takePhoto", "单拍"),

    /** 开始录像 */
    START_RECORD("startRecord", "开始录像"),

    /** 结束录像 */
    STOP_RECORD("stopRecord", "结束录像"),

    /** 对焦 */
    FOCUS("focus", "对焦"),

    /** 变焦 */
    ZOOM("zoom", "变焦"),

    /** 创建新文件夹 */
    CUSTOM_DIR_NAME("customDirName", "创建新文件夹"),

    /** 旋转云台 */
    GIMBAL_ROTATE("gimbalRotate", "旋转云台"),

    /** 飞行器偏航 */
    ROTATE_YAW("rotateYaw", "飞行器偏航"),

    /** 悬停等待 */
    HOVER("hover", "悬停等待"),

    /** 航段间均匀转动云台 pitch 角 */
    GIMBAL_EVENLY_ROTATE("gimbalEvenlyRotate", "航段间均匀转动云台pitch角"),

    /** 定向拍照动作 */
    ORIENTED_SHOOT("orientedShoot", "定向拍照动作"),

    /** 精准复拍（已弃用，建议使用 orientedShoot） */
    ACCURATE_SHOOT("accurateShoot", "精准复拍（已弃用）"),

    /** 全景拍照动作 */
    PANO_SHOT("panoShot", "全景拍照动作"),

    /** 点云录制操作 */
    RECORD_POINT_CLOUD("recordPointCloud", "点云录制操作"),

    /** 喊话器（M4D/M4TD） */
    MEGAPHONE("megaphone", "喊话器"),

    /** 探照灯（M4D/M4TD） */
    SEARCHLIGHT("searchlight", "探照灯");

    /** code → 枚举值 的不可变查找表 */
    private static final Map<String, ActionActuatorFunc> BY_CODE =
            Stream.of(values())
                    .collect(Collectors.toUnmodifiableMap(ActionActuatorFunc::code, Function.identity()));

    private final String code;
    private final String description;

    ActionActuatorFunc(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 返回动作执行器函数字符串码。
     *
     * @return 字符串码，如 {@code "takePhoto"} 表示单拍
     */
    public String code() {
        return code;
    }

    /**
     * 返回动作执行器函数的中文描述。
     *
     * @return 描述文本，如 {@code "单拍"}
     */
    public String description() {
        return description;
    }

    /**
     * 根据字符串码查找对应的枚举值。
     *
     * @param code 字符串码，如 {@code "takePhoto"}
     * @return 对应的 {@link ActionActuatorFunc} 枚举值
     * @throws IllegalArgumentException 如果字符串码不存在于已知枚举中
     */
    public static ActionActuatorFunc fromCode(String code) {
        ActionActuatorFunc func = BY_CODE.get(code);
        if (func == null) {
            throw new IllegalArgumentException("未知的 actionActuatorFunc: " + code);
        }
        return func;
    }
}
