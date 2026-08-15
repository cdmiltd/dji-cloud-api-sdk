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

package ltd.cdmi.dji.cloudapi.sdk.wayline.model;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import static java.util.Map.entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.AccurateShootParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.ActionActuatorFuncParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.CustomDirNameParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.FocusParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.GimbalEvenlyRotateParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.GimbalRotateParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.HoverParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.MegaphoneParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.OrientedShootParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.PanoShotParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.RecordPointCloudParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.RotateYawParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.SearchlightParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.StartRecordParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.StopRecordParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.TakePhotoParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.ZoomParam;

/**
 * {@link Action} 的自定义反序列化器。
 *
 * <p>{@link ActionActuatorFuncParam} 是密封接口（16 个子类），反序列化时需根据
 * {@code actionActuatorFunc} 值选择对应子类。本反序列化器先读取 XML 树，
 * 提取 {@code actionActuatorFunc} 值后用 {@code treeToValue} 转为具体子类实例。
 *
 * <p>遇到未知的 {@code actionActuatorFunc} 值时，记录 WARNING 日志并保留
 * {@code actionActuatorFunc} 字符串，但 {@code actionActuatorFuncParam} 置空，
 * 确保解析不中断（适用于 DJI Pilot 导出的 KMZ 含 SDK 尚未支持的动作类型）。
 *
 * <p>序列化行为不受影响（Jackson 序列化时忽略 {@code @JsonDeserialize}）。
 *
 * @see Action
 * @see ActionActuatorFuncParam
 */
public class ActionDeserializer extends JsonDeserializer<Action> {

    private static final Logger LOG = Logger.getLogger(ActionDeserializer.class.getName());

    /** actionActuatorFunc 值 → 子类映射（16 个） */
    private static final Map<String, Class<? extends ActionActuatorFuncParam>> PARAM_MAP = Map.ofEntries(
        entry("takePhoto",          TakePhotoParam.class),
        entry("startRecord",        StartRecordParam.class),
        entry("stopRecord",         StopRecordParam.class),
        entry("focus",              FocusParam.class),
        entry("zoom",               ZoomParam.class),
        entry("customDirName",      CustomDirNameParam.class),
        entry("gimbalRotate",       GimbalRotateParam.class),
        entry("rotateYaw",          RotateYawParam.class),
        entry("hover",              HoverParam.class),
        entry("gimbalEvenlyRotate", GimbalEvenlyRotateParam.class),
        entry("orientedShoot",      OrientedShootParam.class),
        entry("accurateShoot",      AccurateShootParam.class),
        entry("panoShot",           PanoShotParam.class),
        entry("recordPointCloud",   RecordPointCloudParam.class),
        entry("megaphone",          MegaphoneParam.class),
        entry("searchlight",        SearchlightParam.class)
    );

    @Override
    public Action deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        Integer actionId = node.has("actionId") ? node.get("actionId").asInt() : null;
        String func = node.has("actionActuatorFunc") ? node.get("actionActuatorFunc").asText() : null;
        JsonNode paramNode = node.get("actionActuatorFuncParam");

        ActionActuatorFuncParam param = null;
        if (paramNode != null && !paramNode.isNull() && func != null) {
            Class<? extends ActionActuatorFuncParam> paramClass = PARAM_MAP.get(func);
            if (paramClass != null) {
                param = p.getCodec().treeToValue(paramNode, paramClass);
            } else {
                LOG.warning("未知的 actionActuatorFunc: " + func
                    + "，actionActuatorFuncParam 将被忽略。"
                    + "请向 SDK 反馈以补充对该动作类型的支持。");
            }
        }

        return new Action(actionId, func, param);
    }
}
