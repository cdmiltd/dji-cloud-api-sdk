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

import java.util.Set;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;

/**
 * DJI Cloud API 负载类型枚举。
 *
 * <p>负载标识格式为 {type}-{subtype}-{gimbalindex}（注意：不含 domain，与设备 model_key 不同）。
 * 该标识用于直播推流的 camera_index、相机指令的 payload_index 等场景。
 *
 * <p>枚举值分为三类：
 * <ul>
 *   <li><b>飞行器主相机</b>：与飞行器一一对应（如 M30_CAMERA 对应 DroneModel.M30），
 *       通过 {@link #compatibleAircraft()} 关联，可用 {@link #byAircraft(DroneModel)} 反查</li>
 *   <li><b>通用云台负载</b>：可挂载于 M300/M350/M400 等多款飞行器（如 H20、H20T、H30 等），
 *       {@code compatibleAircraft} 为 null（具体搭载关系由应用层决策，如 DefaultCameraResolver）</li>
 *   <li><b>辅助影像</b>：FPV/辅助影像相机，分新旧两代：
 *       <ul>
 *         <li>{@link #AUXILIARY_CAMERA_LEGACY}（39-0-7）：M300 RTK / M350 RTK / M30 / M30T / M400 的辅助影像</li>
 *         <li>{@link #AUXILIARY_CAMERA_NEW}（176-0-0）：M3D / M3TD / M4D / M4TD 的辅助影像</li>
 *       </ul>
 *       通过 {@link #applicableAircraft()} 关联，可用 {@link #auxiliaryCameraOf(DroneModel)} 反查</li>
 *   <li><b>机场相机</b>：所有机场共用 type=165，通过 camera_position 区分舱内/舱外</li>
 * </ul>
 *
 * <p>注意：本枚举仅定义"负载是什么"（标识与属性），不定义"通用云台机型的默认配置"——
 * 后者属于应用层配置决策（如模拟器的 DefaultCameraResolver）。
 *
 * @see DroneModel
 * @see <a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html">DJI 产品支持</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html")
@Verified(basis = "DJI Cloud API 官方文档产品支持页相机枚举值表，type/subtype/gimbalindex 三元组对齐官方文档")
public enum PayloadType {

    // ==================== 飞行器主相机（与飞行器一一对应） ====================

    /** Matrice 30 主相机 */
    M30_CAMERA  (52,  0, 0, DroneModel.M30),
    /** Matrice 30T 主相机（含热成像） */
    M30T_CAMERA (53,  0, 0, DroneModel.M30T),
    /** Matrice 3D 主相机 */
    M3D_CAMERA  (80,  0, 0, DroneModel.M3D),
    /** Matrice 3TD 主相机（含热成像） */
    M3TD_CAMERA (81,  0, 0, DroneModel.M3TD),
    /** Matrice 4D 主相机 */
    M4D_CAMERA  (98,  0, 0, DroneModel.M4D),
    /** Matrice 4TD 主相机（含热成像） */
    M4TD_CAMERA (99,  0, 0, DroneModel.M4TD),
    /** Mavic 3E 主相机 */
    MAVIC_3E_CAMERA (66, 0, 0, DroneModel.MAVIC_3E),
    /** Mavic 3T 主相机（含热成像） */
    MAVIC_3T_CAMERA (67, 0, 0, DroneModel.MAVIC_3T),
    /** Matrice 4E 主相机 */
    M4E_CAMERA  (88, 0, 0, DroneModel.M4E),
    /** Matrice 4T 主相机（含热成像） */
    M4T_CAMERA  (89, 0, 0, DroneModel.M4T),

    // ==================== 通用云台负载（可挂载于 M300/M350/M400 等多款飞行器） ====================

    /** Z30 变焦云台 */
    Z30  (20, 0, 0, null),
    /** XT2 热成像云台（含热成像） */
    XT2  (26, 0, 0, null),
    /** XTS 热成像云台（含热成像） */
    XTS  (41, 0, 0, null),
    /** H20 通用云台 */
    H20  (42, 0, 0, null),
    /** H20T 通用云台（含热成像） */
    H20T (43, 0, 0, null),
    /** H20N 通用云台（含热成像） */
    H20N (61, 0, 0, null),
    /** H30 通用云台 */
    H30  (82, 0, 0, null),
    /** H30T 通用云台（含热成像） */
    H30T (83, 0, 0, null),

    // ==================== 辅助影像（FPV） ====================

    /**
     * 旧机型辅助影像（gimbalindex=7）。
     * <p>适用机型：Matrice 300 RTK / Matrice 350 RTK / Matrice 30 / Matrice 30T / Matrice 400。
     * <p>DJI 官方文档"相机枚举值"表中称此类为"飞行器 FPV"。
     */
    AUXILIARY_CAMERA_LEGACY(39, 0, 7, null, Set.of(
            DroneModel.M300_RTK, DroneModel.M350_RTK,
            DroneModel.M30, DroneModel.M30T,
            DroneModel.M400
    )),

    /**
     * 新机型辅助影像（gimbalindex=0）。
     * <p>适用机型：Matrice 3D / Matrice 3TD / Matrice 4D / Matrice 4TD。
     * <p>DJI 官方文档"相机枚举值"表中称此类为"辅助影像"（与旧机型的 FPV 区分）。
     */
    AUXILIARY_CAMERA_NEW(176, 0, 0, null, Set.of(
            DroneModel.M3D, DroneModel.M3TD,
            DroneModel.M4D, DroneModel.M4TD
    )),

    // ==================== 机场相机 ====================

    /** 机场相机（所有机场共用 type=165，通过 camera_position 区分舱内/舱外） */
    DOCK_CAMERA(165, 0, 7, null);

    private final int type;
    private final int subType;
    private final int gimbalIndex;
    /** 兼容的飞行器型号（飞行器主相机一一对应，通用云台负载/机场相机为 null） */
    private final DroneModel compatibleAircraft;
    /** 辅助影像适用的飞行器型号集合（仅辅助影像枚举非 null，其他为 null） */
    private final Set<DroneModel> applicableAircraft;

    PayloadType(int type, int subType, int gimbalIndex, DroneModel compatibleAircraft) {
        this(type, subType, gimbalIndex, compatibleAircraft, null);
    }

    PayloadType(int type, int subType, int gimbalIndex, DroneModel compatibleAircraft,
                Set<DroneModel> applicableAircraft) {
        this.type = type;
        this.subType = subType;
        this.gimbalIndex = gimbalIndex;
        this.compatibleAircraft = compatibleAircraft;
        this.applicableAircraft = applicableAircraft;
    }

    /** 负载类型 */
    public int type() { return type; }

    /** 负载子类型 */
    public int subType() { return subType; }

    /** 云台索引 */
    public int gimbalIndex() { return gimbalIndex; }

    /** 兼容的飞行器型号（通用云台负载返回 null） */
    public DroneModel compatibleAircraft() { return compatibleAircraft; }

    /**
     * 辅助影像适用的飞行器型号集合。
     * <p>仅 {@link #AUXILIARY_CAMERA_LEGACY} / {@link #AUXILIARY_CAMERA_NEW} 返回非 null，
     * 其他枚举返回 null。
     */
    public Set<DroneModel> applicableAircraft() { return applicableAircraft; }

    /**
     * 负载标识: {type}-{subtype}-{gimbalindex}，如 "98-0-0" 表示 M4D Camera。
     * <p>用于直播推流的 camera_index、相机指令的 payload_index 等场景。</p>
     */
    public String cameraIndex() {
        return type + "-" + subType + "-" + gimbalIndex;
    }

    /**
     * 按飞行器型号反查内置主相机。
     * <p>基于 {@link #compatibleAircraft()} 匹配，仅返回与飞行器一一对应的内置主相机
     * （如 M4D → M4D_CAMERA）。通用云台负载机型（M300/M350/M400）返回 null，
     * 其默认搭载的云台负载由应用层决策（参见 simulator 的 DefaultCameraResolver）。
     *
     * @param aircraft 飞行器型号（null 返回 null）
     * @return 内置主相机 PayloadType，无对应时返回 null
     */
    public static PayloadType byAircraft(DroneModel aircraft) {
        if (aircraft == null) return null;
        for (PayloadType p : values()) {
            if (p.compatibleAircraft == aircraft) {
                return p;
            }
        }
        return null;
    }

    /**
     * 按飞行器型号反查辅助影像相机。
     * <p>基于 {@link #applicableAircraft()} 匹配：
     * <ul>
     *   <li>M300 RTK / M350 RTK / M30 / M30T / M400 → {@link #AUXILIARY_CAMERA_LEGACY}（39-0-7）</li>
     *   <li>M3D / M3TD / M4D / M4TD → {@link #AUXILIARY_CAMERA_NEW}（176-0-0）</li>
     *   <li>其他机型（如 Mavic 3 行业系列无辅助影像）→ null</li>
     * </ul>
     *
     * @param aircraft 飞行器型号（null 返回 null）
     * @return 辅助影像 PayloadType，无对应时返回 null
     */
    public static PayloadType auxiliaryCameraOf(DroneModel aircraft) {
        if (aircraft == null) return null;
        for (PayloadType p : values()) {
            if (p.applicableAircraft != null && p.applicableAircraft.contains(aircraft)) {
                return p;
            }
        }
        return null;
    }

    /**
     * 是否为热成像负载。
     * <p>热成像相机：H20T / H20N / H30T / XT2 / XTS（通用云台），
     * M30T_CAMERA / MAVIC_3T_CAMERA / M3TD_CAMERA / M4TD_CAMERA / M4T_CAMERA（飞行器内置热成像）。</p>
     * <p>核实依据：DJI 产品支持文档相机枚举值中含热成像功能的相机型号。</p>
     */
    public boolean isThermal() {
        return this == H20T || this == H20N || this == H30T
                || this == XT2 || this == XTS
                || this == M30T_CAMERA || this == MAVIC_3T_CAMERA
                || this == M3TD_CAMERA || this == M4TD_CAMERA || this == M4T_CAMERA;
    }
}
