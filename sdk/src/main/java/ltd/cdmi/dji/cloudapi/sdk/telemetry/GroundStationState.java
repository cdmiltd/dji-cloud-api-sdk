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

package ltd.cdmi.dji.cloudapi.sdk.telemetry;

import java.util.List;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.command.peripheral.PeripheralCapability;
import ltd.cdmi.dji.cloudapi.sdk.command.peripheral.PeripheralInfo;
import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.SubDevice;

/**
 * O4 地面站 state（pushMode=1 状态上报）遥测数据。
 *
 * <p>对应 DJI Cloud API O4 地面站设备属性中 pushMode=1 的字段集合，通过
 * {@code thing/product/{device_sn}/state} 上报。嵌套结构：
 * <ul>
 *   <li>{@code electric_supply_mode} — 供电模式，见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.SupplyPowerMode}</li>
 *   <li>{@code is_beidou_version} — 是否北斗版本（0=非北斗, 1=北斗）</li>
 *   <li>{@code peripherals_info} — 外设接入登记，见 {@link PeripheralInfo}</li>
 *   <li>{@code peripheral_list} — 外设能力集，见 {@link PeripheralCapability}</li>
 *   <li>{@code sub_device} — 子设备状态（复用 {@link SubDevice}）</li>
 *   <li>{@code rtcm_info} — 见 {@link RtcmInfo}</li>
 *   <li>{@code wireless_link_topo} — 见 {@link WirelessLinkTopo}</li>
 *   <li>{@code heading} — 朝向角（文档标注已废弃）</li>
 * </ul>
 *
 * <p>参考：<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html">
 * DJI O4 地面站设备属性</a>
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dock-to-cloud/mqtt/ground-station/o4/properties.html")
@Verified(basis = "DJI O4 地面站设备属性页 pushMode=1 字段集合")
public record GroundStationState(
        /** 供电模式（0=电池, 1=PD, 2=POE），见 SupplyPowerMode */
        Integer electricSupplyMode,

        /** 是否北斗版本（0=非北斗, 1=北斗） */
        Integer isBeidouVersion,

        /** 外设接入登记 */
        List<PeripheralInfo> peripheralsInfo,

        /** 外设能力集 */
        List<PeripheralCapability> peripheralList,

        /** 子设备状态（复用 SubDevice） */
        SubDevice subDevice,

        /** RTCM 信息 */
        RtcmInfo rtcmInfo,

        /** 图传连接拓扑 */
        WirelessLinkTopo wirelessLinkTopo,

        /** 朝向角（文档标注已废弃） */
        @Deprecated
        Double heading
) {
    /**
     * RTCM 信息（rtcm_info struct）。
     *
     * <p>DJI 文档字段：{@code mount_point} / {@code port} / {@code host} /
     * {@code rtcm_device_type}（见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.RtcmDeviceType}）/
     * {@code source_type}（见 {@link ltd.cdmi.dji.cloudapi.sdk.telemetry.enumtype.RtcmSourceType}）。
     */
    @Verified(basis = "DJI O4 地面站设备属性页 rtcm_info struct（mount_point(text)/port(text)/host(text)/rtcm_device_type(int)/source_type(int)）")
    public record RtcmInfo(
            /** 挂载点 */
            String mountPoint,

            /** 端口（文档 Type=text，示例 "1024"） */
            String port,

            /** 主机地址 */
            String host,

            /** RTCM 设备类型（0=遥控器, 1=机场, 2=中继） */
            Integer rtcmDeviceType,

            /** RTCM 源类型（0=未标定, 1=自收敛, 2=手动, 3=网络RTK） */
            Integer sourceType
    ) {
    }

    /**
     * 图传连接拓扑（wireless_link_topo struct）。
     *
     * <p>DJI 文档字段：{@code secret_code}（加密编码，28 项 int 数组）/ {@code center_node} / {@code leaf_nodes}。
     */
    @Verified(basis = "DJI O4 地面站设备属性页 wireless_link_topo struct（secret_code[28]/center_node/leaf_nodes）")
    public record WirelessLinkTopo(
            /** 加密编码（28 项 int） */
            List<Integer> secretCode,

            /** 飞行器对频信息 */
            CenterNode centerNode,

            /** 当前连接的机场或遥控器对频信息 */
            List<LeafNode> leafNodes
    ) {
        /**
         * 中心节点（wireless_link_topo.center_node）。
         *
         * <p>DJI 文档字段：{@code sdr_id}(int) / {@code sn}(text) / {@code model}(int，图传拓扑内部机型编码) /
         * {@code device_type}(int，图传拓扑内部设备类型标识)。
         */
        @Verified(basis = "DJI O4 地面站设备属性页 wireless_link_topo.center_node：sdr_id/model/device_type 均为 int，sn 为 text")
        public record CenterNode(
                /** 扰码信息 */
                Integer sdrId,

                /** 设备 SN */
                String sn,

                /** 机型编码（图传拓扑内部编码，int） */
                Integer model,

                /** 设备类型（图传拓扑内部编码，int） */
                Integer deviceType
        ) {
        }

        /**
         * 叶节点（wireless_link_topo.leaf_nodes 元素）。
         *
         * <p>DJI 文档字段：{@code sdr_id}(int) / {@code sn}(text) / {@code control_source_index}(int，1=A 槽/2=B 槽) /
         * {@code model}(int，图传拓扑内部机型编码) / {@code device_type}(int，图传拓扑内部设备类型标识)。
         */
        @Verified(basis = "DJI O4 地面站设备属性页 wireless_link_topo.leaf_nodes[]：sdr_id/model/device_type 均为 int，sn 为 text，control_source_index 为 int")
        public record LeafNode(
                /** 扰码信息 */
                Integer sdrId,

                /** 设备 SN */
                String sn,

                /** 控制源序号（1=A, 2=B） */
                Integer controlSourceIndex,

                /** 机型编码（图传拓扑内部编码，int） */
                Integer model,

                /** 设备类型（图传拓扑内部编码，int） */
                Integer deviceType
        ) {
        }
    }
}
