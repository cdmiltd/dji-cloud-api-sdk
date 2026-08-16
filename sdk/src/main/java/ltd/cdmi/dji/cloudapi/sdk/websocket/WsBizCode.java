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

package ltd.cdmi.dji.cloudapi.sdk.websocket;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Pilot 上云 WebSocket 推送 biz_code 枚举。
 *
 * <p>hivemind 通过 WebSocket 向 Pilot 推送消息，消息信封为
 * {@link WsPushMessage}（{@code {biz_code, version, timestamp, data}}），
 * {@code biz_code} 即本枚举的 {@link #code()}，用于标识消息类型并路由到对应处理器。
 *
 * <p>本枚举当前覆盖 simulator 已对接 hivemind 验证的 8 个 biz_code：
 * <ul>
 *   <li><b>地图元素</b>：{@link #MAP_ELEMENT_CREATE} / {@link #MAP_ELEMENT_UPDATE} /
 *       {@link #MAP_ELEMENT_DELETE} / {@link #MAP_GROUP_REFRESH}</li>
 *   <li><b>态势感知</b>：{@link #DEVICE_OSD} / {@link #DEVICE_ONLINE} /
 *       {@link #DEVICE_OFFLINE} / {@link #DEVICE_UPDATE_TOPO}</li>
 * </ul>
 *
 * <p>注：DJI 官方文档可能还规定其他 biz_code（如航线任务、直播等），
 * 当前未列入本枚举，待后续按文档补全。
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/map-elements/message-push.html">
 * DJI Pilot WebSocket 消息发布</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/pilot-to-cloud/websocket/map-elements/message-push.html")
@Verified(basis = "simulator MapElementWsHandler/SituationAwarenessWsHandler 已对接 hivemind 验证的 8 个 biz_code")
public enum WsBizCode {

    /** 地图元素新增通知 */
    MAP_ELEMENT_CREATE("map_element_create", "地图元素新增"),

    /** 地图元素更新通知 */
    MAP_ELEMENT_UPDATE("map_element_update", "地图元素更新"),

    /** 地图元素删除通知 */
    MAP_ELEMENT_DELETE("map_element_delete", "地图元素删除"),

    /** 图层刷新通知（解析 data.ids[] 并对每个 group_id 调用 HTTP 拉取元素列表） */
    MAP_GROUP_REFRESH("map_group_refresh", "图层刷新"),

    /** 设备 OSD 遥测数据推送（包含位置/姿态/速度等） */
    DEVICE_OSD("device_osd", "设备 OSD 推送"),

    /** 设备上线通知（触发 Pilot 调用 GET /manage/api/v1/.../devices/topologies 刷新拓扑） */
    DEVICE_ONLINE("device_online", "设备上线"),

    /** 设备下线通知（触发 Pilot 调用 GET /manage/api/v1/.../devices/topologies 刷新拓扑） */
    DEVICE_OFFLINE("device_offline", "设备下线"),

    /** 设备拓扑更新通知（触发 Pilot 调用 GET /manage/api/v1/.../devices/topologies 刷新拓扑） */
    DEVICE_UPDATE_TOPO("device_update_topo", "设备拓扑更新");

    private final String code;
    private final String description;

    WsBizCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /** biz_code 字符串值，如 "map_element_create" / "device_osd" */
    public String code() {
        return code;
    }

    /** 中文描述 */
    public String description() {
        return description;
    }

    /**
     * 按 biz_code 字符串反查枚举。
     *
     * @param code biz_code 字符串值（如 "map_element_create"）
     * @return 匹配的枚举；未匹配返回 {@link java.util.Optional#empty()}
     */
    public static java.util.Optional<WsBizCode> fromCode(String code) {
        if (code == null || code.isBlank()) {
            return java.util.Optional.empty();
        }
        for (WsBizCode v : values()) {
            if (v.code.equals(code)) {
                return java.util.Optional.of(v);
            }
        }
        return java.util.Optional.empty();
    }
}
