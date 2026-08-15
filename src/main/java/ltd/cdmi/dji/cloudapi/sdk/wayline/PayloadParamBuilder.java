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

import java.util.Set;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FocusMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.MeteringMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ReturnMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ScanningMode;
import static ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WpmlEnum.codeOf;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.PayloadParam;

/**
 * {@link PayloadParam} 的 Builder。
 *
 * <p>由建图类模板（{@link Mapping2dTemplate}/{@link Mapping3dTemplate}/{@link MappingStripTemplate}）
 * 的 {@code payloadParam(Consumer<PayloadParamBuilder>)} 回调创建，集中配置负载参数。
 *
 * <p>覆盖两类负载场景：
 * <ul>
 *   <li>可见光相机（如 Zenmuse P1，M300/M350）：{@code focusMode}、{@code meteringMode}、
 *       {@code dewarpingEnable}、{@code imageFormat}</li>
 *   <li>激光雷达（如 Zenmuse L1/L2，M300/M350）：{@code returnMode}、{@code samplingRate}、
 *       {@code scanningMode}、{@code modelColoringEnable}</li>
 * </ul>
 *
 * <p>{@code payloadPositionIndex} 为必需字段，其余按负载类型选填。
 * {@code samplingRate} 仅接受 DJI 文档定义的固定值域。
 *
 * <p>示例（激光雷达）：
 * <pre>{@code
 * .payloadParam(p -> p
 *     .payloadPositionIndex(0)
 *     .returnMode(ReturnMode.TRIPLE_RETURN)
 *     .samplingRate(240000)
 *     .scanningMode(ScanningMode.NON_REPETITIVE)
 *     .modelColoringEnable(1)
 *     .imageFormat("wide,ir"))
 * }</pre>
 *
 * @see PayloadParam
 * @see ReturnMode
 * @see ScanningMode
 * @see FocusMode
 * @see MeteringMode
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/common-element.html#wpml-payloadparam")
@Verified(basis = "DJI WPML 共用元素文档 payloadParam 元素定义")
public final class PayloadParamBuilder {

    /** DJI 文档定义的合法采样率值域（Hz），仅 M300 RTK / M350 RTK 激光雷达负载。 */
    private static final Set<Integer> VALID_SAMPLING_RATES =
            Set.of(60000, 80000, 120000, 160000, 180000, 240000);

    private int payloadPositionIndex;
    private boolean payloadPositionIndexSet = false;
    private FocusMode focusMode;
    private MeteringMode meteringMode;
    private Integer dewarpingEnable;
    private ReturnMode returnMode;
    private Integer samplingRate;
    private ScanningMode scanningMode;
    private Integer modelColoringEnable;
    private String imageFormat;

    PayloadParamBuilder() {
    }

    /**
     * 设置负载挂载位置索引（必需）。
     *
     * @param index 负载挂载位置索引（gimbalindex）
     * @return this
     */
    public PayloadParamBuilder payloadPositionIndex(int index) {
        this.payloadPositionIndex = index;
        this.payloadPositionIndexSet = true;
        return this;
    }

    /**
     * 设置负载对焦模式（M300/M350 可见光相机）。
     *
     * @param mode 对焦模式
     * @return this
     */
    public PayloadParamBuilder focusMode(FocusMode mode) {
        this.focusMode = mode;
        return this;
    }

    /**
     * 设置负载测光模式（M300/M350 可见光相机）。
     *
     * @param mode 测光模式
     * @return this
     */
    public PayloadParamBuilder meteringMode(MeteringMode mode) {
        this.meteringMode = mode;
        return this;
    }

    /**
     * 设置是否开启畸变矫正（M300/M350 可见光相机）。
     *
     * @param enable 0=不开启，1=开启
     * @return this
     */
    public PayloadParamBuilder dewarpingEnable(int enable) {
        this.dewarpingEnable = enable;
        return this;
    }

    /**
     * 设置激光雷达回波模式（M300/M350 激光雷达负载）。
     *
     * @param mode 回波模式
     * @return this
     */
    public PayloadParamBuilder returnMode(ReturnMode mode) {
        this.returnMode = mode;
        return this;
    }

    /**
     * 设置激光雷达采样率（M300/M350 激光雷达负载）。
     *
     * @param rateHz 采样率（Hz），合法值：60000/80000/120000/160000/180000/240000
     * @return this
     * @throws IllegalArgumentException 如果采样率不在合法值域内
     */
    public PayloadParamBuilder samplingRate(int rateHz) {
        if (!VALID_SAMPLING_RATES.contains(rateHz)) {
            throw new IllegalArgumentException(
                "samplingRate 不在合法值域 " + VALID_SAMPLING_RATES + " 内: " + rateHz);
        }
        this.samplingRate = rateHz;
        return this;
    }

    /**
     * 设置激光雷达扫描模式（M300/M350 激光雷达负载）。
     *
     * @param mode 扫描模式
     * @return this
     */
    public PayloadParamBuilder scanningMode(ScanningMode mode) {
        this.scanningMode = mode;
        return this;
    }

    /**
     * 设置激光雷达真彩上色（M300/M350 激光雷达负载）。
     *
     * @param enable 0=不上色，1=真彩上色
     * @return this
     */
    public PayloadParamBuilder modelColoringEnable(int enable) {
        this.modelColoringEnable = enable;
        return this;
    }

    /**
     * 设置图片格式。
     *
     * @param format 图片格式（如 {@code "wide,ir"}，取值：wide/zoom/ir/narrow_band/visable）
     * @return this
     */
    public PayloadParamBuilder imageFormat(String format) {
        this.imageFormat = format;
        return this;
    }

    /**
     * 构建 {@link PayloadParam} 实例。
     *
     * @return PayloadParam 实例
     * @throws IllegalStateException 如果 {@code payloadPositionIndex} 未设置
     */
    PayloadParam build() {
        if (!payloadPositionIndexSet) {
            throw new IllegalStateException("payloadPositionIndex 未设置");
        }
        return new PayloadParam(
            payloadPositionIndex,
            codeOf(focusMode),
            codeOf(meteringMode),
            dewarpingEnable,
            codeOf(returnMode),
            samplingRate,
            codeOf(scanningMode),
            modelColoringEnable,
            imageFormat
        );
    }

}
