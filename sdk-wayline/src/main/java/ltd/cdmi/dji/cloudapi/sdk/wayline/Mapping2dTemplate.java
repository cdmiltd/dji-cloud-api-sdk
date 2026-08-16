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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.CoordinateMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ExecuteHeightMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ExecuteRCLostAction;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ExitOnRCLost;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FinishAction;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FlyToWaylineMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.GimbalPitchMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.HeightMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.MappingHeadingMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.PositioningType;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ShootType;
import static ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WpmlEnum.codeOf;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Document;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.DroneInfo;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Kml;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.coordinate.LinearRing;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.mapping.Mapping2dPlacemark;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.mapping.MappingFolder;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.mapping.MappingHeadingParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.MissionConfig;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.mapping.Overlap;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.PayloadInfo;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.PayloadParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.coordinate.Polygon;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.mapping.WaylineCoordinateSysParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.execute.ExecuteFolder;

/**
 * DJI WPML template.kml 建图航拍（mapping2d）模板生成器。
 *
 * <p>通过 Builder 模式构造合法的 {@code template.kml} XML 字符串，
 * 覆盖建图航拍模板（{@code templateType=mapping2d}）。
 *
 * <p>高度语义：所有 {@code height} 参数为相对起飞点高度，
 * 与 DJI WPML {@code wpml:height} 定义一致。
 *
 * <p>使用示例：
 * <pre>{@code
 * String kml = Mapping2dTemplate.builder()
 *     .author("John")
 *     .createTime(System.currentTimeMillis())
 *     .flyToWaylineMode(FlyToWaylineMode.SAFELY)
 *     .finishAction(FinishAction.GO_HOME)
 *     .exitOnRCLost(ExitOnRCLost.GO_CONTINUE)
 *     .executeRCLostAction(ExecuteRCLostAction.HOVER)
 *     .takeOffSecurityHeight(20)
 *     .globalTransitionalSpeed(8)
 *     .globalRTHHeight(100)
 *     .droneInfo(67, 0)       // M30
 *     .payloadInfo(52, 0)     // M30 相机
 *     .templateId(0)
 *     .coordinateMode(CoordinateMode.WGS84)
 *     .heightMode(HeightMode.EGM96)
 *     .autoFlightSpeed(7)
 *     .globalShootHeight(100)
 *     .positioningType(PositioningType.GPS)
 *     .payloadParam(0, "wide,ir")
 *     .shootType(ShootType.TIME)
 *     .direction(0)
 *     .overlap(new Overlap(null, null, 80, 70, null, null, null, null))
 *     .height(100)
 *     .polygon("113.98057,22.987663,0 113.990000,22.987663,0 113.990000,22.977663,0 113.98057,22.977663,0")
 *     .mappingHeadingParam(MappingHeadingMode.FOLLOW_WAYLINE, 0)
 *     .toXml();
 * }</pre>
 *
 * @see MappingFolder
 * @see Mapping2dPlacemark
 * @see WpmlCodec
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档建图航拍模板元素定义")
public final class Mapping2dTemplate {

    // ── Document 创建信息 ──
    private String author;
    private Long createTime;
    private Long updateTime;

    // ── MissionConfig 任务配置 ──
    private FlyToWaylineMode flyToWaylineMode;
    private FinishAction finishAction;
    private ExitOnRCLost exitOnRCLost;
    private ExecuteRCLostAction executeRCLostAction;
    private Double takeOffSecurityHeight;
    private String takeOffRefPoint;
    private Double takeOffRefPointAGLHeight;
    private Double globalTransitionalSpeed;
    private Double globalRTHHeight;
    private DroneInfo droneInfo;
    private PayloadInfo payloadInfo;

    // ── Folder 模板配置 ──
    private Integer templateId;
    private Double autoFlightSpeed;
    private CoordinateMode coordinateMode;
    private HeightMode heightMode;
    private Double globalShootHeight;
    private PositioningType positioningType;
    private Integer surfaceFollowModeEnable;
    private Double surfaceRelativeHeight;
    private PayloadParam payloadParam;

    // ── Placemark 测区配置 ──
    private Integer caliFlightEnable;
    private Integer elevationOptimizeEnable;
    private Integer smartObliqueEnable;
    private Integer smartObliqueGimbalPitch;
    private ShootType shootType;
    private Integer direction;
    private Integer margin;
    private Overlap overlap;
    private Double ellipsoidHeight;
    private Double height;
    private Integer facadeWaylineEnable;
    private Polygon polygon;
    private MappingHeadingParam mappingHeadingParam;
    private GimbalPitchMode gimbalPitchMode;
    private Integer gimbalPitchAngle;

    private Mapping2dTemplate() {
    }

    /**
     * 创建 Builder 实例。
     *
     * @return 新的 Mapping2dTemplate 实例
     */
    public static Mapping2dTemplate builder() {
        return new Mapping2dTemplate();
    }

    // ════════════════════════════════════════════
    //  创建信息
    // ════════════════════════════════════════════

    /**
     * 设置文件作者。
     *
     * @param author 作者名称
     * @return this
     */
    public Mapping2dTemplate author(String author) {
        this.author = author;
        return this;
    }

    /**
     * 设置创建时间。
     *
     * @param epochMs Unix 时间戳（毫秒）
     * @return this
     */
    public Mapping2dTemplate createTime(long epochMs) {
        this.createTime = epochMs;
        return this;
    }

    /**
     * 设置更新时间。
     *
     * @param epochMs Unix 时间戳（毫秒）
     * @return this
     */
    public Mapping2dTemplate updateTime(long epochMs) {
        this.updateTime = epochMs;
        return this;
    }

    // ════════════════════════════════════════════
    //  MissionConfig 任务配置
    // ════════════════════════════════════════════

    /**
     * 设置飞行到航线模式。
     *
     * @param mode 飞行模式
     * @return this
     */
    public Mapping2dTemplate flyToWaylineMode(FlyToWaylineMode mode) {
        this.flyToWaylineMode = mode;
        return this;
    }

    /**
     * 设置航线结束动作。
     *
     * @param action 结束动作
     * @return this
     */
    public Mapping2dTemplate finishAction(FinishAction action) {
        this.finishAction = action;
        return this;
    }

    /**
     * 设置遥控器断连退出策略。
     *
     * @param exit 退出策略
     * @return this
     */
    public Mapping2dTemplate exitOnRCLost(ExitOnRCLost exit) {
        this.exitOnRCLost = exit;
        return this;
    }

    /**
     * 设置遥控器断连执行动作。
     *
     * @param action 执行动作
     * @return this
     */
    public Mapping2dTemplate executeRCLostAction(ExecuteRCLostAction action) {
        this.executeRCLostAction = action;
        return this;
    }

    /**
     * 设置安全起飞高度。
     *
     * @param height 安全起飞高度（米），遥控器 [1.2, 1500]，机场 [8, 1500]
     * @return this
     */
    public Mapping2dTemplate takeOffSecurityHeight(double height) {
        this.takeOffSecurityHeight = height;
        return this;
    }

    /**
     * 设置起飞参考点。
     *
     * @param latitude  纬度，范围 [-90, 90]
     * @param longitude 经度，范围 [-180, 180]
     * @param height    高度（米）
     * @return this
     * @throws IllegalArgumentException 如果纬度或经度超出范围
     */
    public Mapping2dTemplate takeOffRefPoint(double latitude, double longitude, double height) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(
                "latitude 超出范围 [-90, 90]: " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(
                "longitude 超出范围 [-180, 180]: " + longitude);
        }
        this.takeOffRefPoint = String.format("%.6f,%.6f,%.1f", latitude, longitude, height);
        return this;
    }

    /**
     * 设置起飞参考点 AGL 高度（离地高度）。
     *
     * @param height AGL 高度（米）
     * @return this
     */
    public Mapping2dTemplate takeOffRefPointAGLHeight(double height) {
        this.takeOffRefPointAGLHeight = height;
        return this;
    }

    /**
     * 设置全局过渡速度。
     *
     * @param speed 过渡速度（m/s），范围 [1, 15]
     * @return this
     * @throws IllegalArgumentException 如果超出 [1, 15]
     */
    public Mapping2dTemplate globalTransitionalSpeed(double speed) {
        if (speed < 1 || speed > 15) {
            throw new IllegalArgumentException(
                "globalTransitionalSpeed 超出范围 [1, 15]: " + speed);
        }
        this.globalTransitionalSpeed = speed;
        return this;
    }

    /**
     * 设置全局返航高度。
     *
     * @param height 返航高度（米），范围 [2, 1500]
     * @return this
     * @throws IllegalArgumentException 如果超出 [2, 1500]
     */
    public Mapping2dTemplate globalRTHHeight(double height) {
        if (height < 2 || height > 1500) {
            throw new IllegalArgumentException(
                "globalRTHHeight 超出范围 [2, 1500]: " + height);
        }
        this.globalRTHHeight = height;
        return this;
    }

    /**
     * 设置无人机机型信息。
     *
     * @param droneEnumValue    无人机主类型枚举值（如 67 = M30）
     * @param droneSubEnumValue 无人机子类型枚举值
     * @return this
     */
    public Mapping2dTemplate droneInfo(int droneEnumValue, int droneSubEnumValue) {
        this.droneInfo = new DroneInfo(droneEnumValue, droneSubEnumValue);
        return this;
    }

    /**
     * 设置负载信息。
     *
     * @param payloadEnumValue    负载类型枚举值（如 52 = M30 相机）
     * @param payloadPositionIndex 负载挂载位置索引
     * @return this
     */
    public Mapping2dTemplate payloadInfo(int payloadEnumValue, int payloadPositionIndex) {
        this.payloadInfo = new PayloadInfo(payloadEnumValue, payloadPositionIndex);
        return this;
    }

    // ════════════════════════════════════════════
    //  Folder 模板配置
    // ════════════════════════════════════════════

    /**
     * 设置模板 ID。
     *
     * @param id 模板 ID，范围 [0, 65535]
     * @return this
     * @throws IllegalArgumentException 如果超出 [0, 65535]
     */
    public Mapping2dTemplate templateId(int id) {
        if (id < 0 || id > 65535) {
            throw new IllegalArgumentException(
                "templateId 超出范围 [0, 65535]: " + id);
        }
        this.templateId = id;
        return this;
    }

    /**
     * 设置自动飞行速度。
     *
     * @param speed 飞行速度（m/s），范围 [1, 15]
     * @return this
     * @throws IllegalArgumentException 如果超出 [1, 15]
     */
    public Mapping2dTemplate autoFlightSpeed(double speed) {
        if (speed < 1 || speed > 15) {
            throw new IllegalArgumentException(
                "autoFlightSpeed 超出范围 [1, 15]: " + speed);
        }
        this.autoFlightSpeed = speed;
        return this;
    }

    /**
     * 设置坐标模式。
     *
     * @param mode 坐标模式
     * @return this
     */
    public Mapping2dTemplate coordinateMode(CoordinateMode mode) {
        this.coordinateMode = mode;
        return this;
    }

    /**
     * 设置高度模式。
     *
     * @param mode 高度模式
     * @return this
     */
    public Mapping2dTemplate heightMode(HeightMode mode) {
        this.heightMode = mode;
        return this;
    }

    /**
     * 设置全局拍摄高度。
     *
     * @param height 拍摄高度（米）
     * @return this
     */
    public Mapping2dTemplate globalShootHeight(double height) {
        this.globalShootHeight = height;
        return this;
    }

    /**
     * 设置定位类型。
     *
     * @param type 定位类型
     * @return this
     */
    public Mapping2dTemplate positioningType(PositioningType type) {
        this.positioningType = type;
        return this;
    }

    /**
     * 设置仿地飞行开关。
     *
     * @param enable 0=关闭，1=开启
     * @return this
     */
    public Mapping2dTemplate surfaceFollowModeEnable(int enable) {
        this.surfaceFollowModeEnable = enable;
        return this;
    }

    /**
     * 设置仿地相对高度。
     *
     * @param height 仿地相对高度（米）
     * @return this
     */
    public Mapping2dTemplate surfaceRelativeHeight(double height) {
        this.surfaceRelativeHeight = height;
        return this;
    }

    /**
     * 设置负载参数（payloadPositionIndex 与 imageFormat）。
     *
     * @param payloadPositionIndex 负载挂载位置索引
     * @param imageFormat          图片格式（如 {@code "wide,ir"}，取值：wide/zoom/ir/narrow_band/visable）
     * @return this
     */
    public Mapping2dTemplate payloadParam(int payloadPositionIndex, String imageFormat) {
        this.payloadParam = new PayloadParam(
            payloadPositionIndex, null, null, null, null, null, null, null, imageFormat);
        return this;
    }

    /**
     * 设置负载参数（完整配置，支持 M300/M350 激光雷达与可见光相机全部字段）。
     *
     * <p>通过 {@link PayloadParamBuilder} 回调集中配置，覆盖激光雷达回波模式、
     * 采样率、扫描模式、真彩上色以及对焦/测光/畸变矫正等 M300 专用参数。
     *
     * @param config 负载参数配置回调
     * @return this
     * @see PayloadParamBuilder
     */
    public Mapping2dTemplate payloadParam(Consumer<PayloadParamBuilder> config) {
        PayloadParamBuilder builder = new PayloadParamBuilder();
        config.accept(builder);
        this.payloadParam = builder.build();
        return this;
    }

    // ════════════════════════════════════════════
    //  Placemark 测区配置
    // ════════════════════════════════════════════

    /**
     * 设置标定飞行开关。
     *
     * @param enable 0=关闭，1=开启
     * @return this
     */
    public Mapping2dTemplate caliFlightEnable(int enable) {
        this.caliFlightEnable = enable;
        return this;
    }

    /**
     * 设置高程优化开关。
     *
     * @param enable 0=关闭，1=开启
     * @return this
     */
    public Mapping2dTemplate elevationOptimizeEnable(int enable) {
        this.elevationOptimizeEnable = enable;
        return this;
    }

    /**
     * 设置智能摆拍开关。
     *
     * @param enable 0=关闭，1=开启
     * @return this
     */
    public Mapping2dTemplate smartObliqueEnable(int enable) {
        this.smartObliqueEnable = enable;
        return this;
    }

    /**
     * 设置智能摆拍云台俯仰角。
     *
     * @param pitch 云台俯仰角（度）
     * @return this
     */
    public Mapping2dTemplate smartObliqueGimbalPitch(int pitch) {
        this.smartObliqueGimbalPitch = pitch;
        return this;
    }

    /**
     * 设置拍照模式。
     *
     * @param type 拍照模式
     * @return this
     */
    public Mapping2dTemplate shootType(ShootType type) {
        this.shootType = type;
        return this;
    }

    /**
     * 设置航线方向角。
     *
     * @param direction 方向角（度），范围 [0, 360]
     * @return this
     * @throws IllegalArgumentException 如果超出 [0, 360]
     */
    public Mapping2dTemplate direction(int direction) {
        if (direction < 0 || direction > 360) {
            throw new IllegalArgumentException(
                "direction 超出范围 [0, 360]: " + direction);
        }
        this.direction = direction;
        return this;
    }

    /**
     * 设置测区外扩距离。
     *
     * @param margin 外扩距离（米）
     * @return this
     */
    public Mapping2dTemplate margin(int margin) {
        this.margin = margin;
        return this;
    }

    /**
     * 设置重叠率参数。
     *
     * @param overlap 重叠率对象
     * @return this
     */
    public Mapping2dTemplate overlap(Overlap overlap) {
        this.overlap = overlap;
        return this;
    }

    /**
     * 设置测区椭球高度。
     *
     * @param height 椭球高度（米）
     * @return this
     */
    public Mapping2dTemplate ellipsoidHeight(double height) {
        this.ellipsoidHeight = height;
        return this;
    }

    /**
     * 设置测区全局高度（相对起飞点）。
     *
     * @param height 高度（米）
     * @return this
     */
    public Mapping2dTemplate height(double height) {
        this.height = height;
        return this;
    }

    /**
     * 设置立面航线开关。
     *
     * @param enable 0=关闭，1=开启
     * @return this
     */
    public Mapping2dTemplate facadeWaylineEnable(int enable) {
        this.facadeWaylineEnable = enable;
        return this;
    }

    /**
     * 设置测区多边形坐标。
     *
     * <p>坐标格式为 {@code "经度,纬度,高度 经度,纬度,高度 ..."}，
     * 例如 {@code "113.98,22.98,0 113.99,22.99,0 113.97,22.97,0"}。
     *
     * @param coordinates 坐标字符串
     * @return this
     */
    public Mapping2dTemplate polygon(String coordinates) {
        this.polygon = new Polygon(
            new Polygon.OuterBoundaryIs(new LinearRing(coordinates)));
        return this;
    }

    /**
     * 设置建图航拍飞行器朝向参数。
     *
     * @param param 朝向参数对象
     * @return this
     */
    public Mapping2dTemplate mappingHeadingParam(MappingHeadingParam param) {
        this.mappingHeadingParam = param;
        return this;
    }

    /**
     * 设置建图航拍飞行器朝向参数（便捷方法）。
     *
     * @param mode  偏航角模式
     * @param angle 偏航角（度）
     * @return this
     */
    public Mapping2dTemplate mappingHeadingParam(MappingHeadingMode mode, int angle) {
        this.mappingHeadingParam = new MappingHeadingParam(codeOf(mode), angle);
        return this;
    }

    /**
     * 设置云台俯仰角模式。
     *
     * @param mode 俯仰角模式
     * @return this
     */
    public Mapping2dTemplate gimbalPitchMode(GimbalPitchMode mode) {
        this.gimbalPitchMode = mode;
        return this;
    }

    /**
     * 设置云台俯仰角度。
     *
     * @param angle 俯仰角度（度）
     * @return this
     */
    public Mapping2dTemplate gimbalPitchAngle(int angle) {
        this.gimbalPitchAngle = angle;
        return this;
    }

    // ════════════════════════════════════════════
    //  输出
    // ════════════════════════════════════════════

    /**
     * 生成 template.kml XML 字符串。
     *
     * @return XML 字符串，含 XML 声明和格式化缩进
     * @throws IllegalStateException 如果必需字段缺失
     */
    public String toXml() {
        Kml<MappingFolder<Mapping2dPlacemark>> kml = buildKml();
        return WpmlCodec.toXml(kml);
    }

    /**
     * 将 template.kml XML 写入文件。
     *
     * @param file 目标文件路径
     * @throws java.io.IOException 如果文件写入失败
     * @throws IllegalStateException 如果必需字段缺失
     */
    public void writeTo(Path file) throws java.io.IOException {
        String xml = toXml();
        Files.writeString(file, xml);
    }

    /**
     * 生成 waylines.wpml XML 字符串。
     *
     * <p>建图模板的 Placemark 是测区配置（非航点），waylines.wpml 中的航点由
     * DJI Pilot 导入后根据测区配置自动计算，因此 Placemark 列表为空。
     *
     * @return XML 字符串，含 XML 声明和格式化缩进
     * @throws IllegalStateException 如果必需字段缺失（droneInfo/payloadInfo 等）
     */
    public String toWpml() {
        validateWpmlRequiredFields();
        Kml<ExecuteFolder> kml = buildWpmlKml();
        return WpmlCodec.toXml(kml);
    }

    /**
     * 将 template.kml + waylines.wpml 打包为 DJI KMZ 格式。
     *
     * <p>便捷方法，等价于 {@code WpmlCodec.toKmz(toXml(), toWpml())}。
     * 调用方可自行将 {@code byte[]} 写入 {@code .kmz} 文件，SDK 不负责文件保存。
     *
     * @return KMZ 字节流
     * @throws IllegalStateException 如果必需字段缺失或打包失败
     */
    public byte[] toKmz() {
        return WpmlCodec.toKmz(toXml(), toWpml());
    }

    // ════════════════════════════════════════════
    //  内部构建逻辑
    // ════════════════════════════════════════════

    private Kml<MappingFolder<Mapping2dPlacemark>> buildKml() {
        validateRequiredFields();

        MissionConfig missionConfig = new MissionConfig(
            codeOf(flyToWaylineMode),
            codeOf(finishAction),
            codeOf(exitOnRCLost),
            codeOf(executeRCLostAction),
            takeOffSecurityHeight,
            takeOffRefPoint,
            takeOffRefPointAGLHeight,
            globalTransitionalSpeed,
            globalRTHHeight,
            null,
            droneInfo,
            payloadInfo
        );

        WaylineCoordinateSysParam coordSysParam = new WaylineCoordinateSysParam(
            codeOf(coordinateMode),
            codeOf(heightMode),
            globalShootHeight,
            codeOf(positioningType),
            surfaceFollowModeEnable,
            surfaceRelativeHeight
        );

        Mapping2dPlacemark placemark = new Mapping2dPlacemark(
            caliFlightEnable,
            elevationOptimizeEnable != null ? elevationOptimizeEnable : 0,
            smartObliqueEnable,
            smartObliqueGimbalPitch,
            codeOf(shootType),
            direction != null ? direction : 0,
            margin != null ? margin : 0,
            overlap,
            ellipsoidHeight != null ? ellipsoidHeight : 0.0,
            height,
            facadeWaylineEnable,
            polygon,
            mappingHeadingParam,
            codeOf(gimbalPitchMode),
            gimbalPitchAngle
        );

        MappingFolder<Mapping2dPlacemark> folder = new MappingFolder<>(
            "mapping2d",
            templateId,
            autoFlightSpeed,
            coordSysParam,
            payloadParam,
            placemark
        );

        Document<MappingFolder<Mapping2dPlacemark>> document = new Document<>(
            author,
            createTime,
            updateTime,
            missionConfig,
            folder
        );

        return new Kml<>(document);
    }

    private void validateRequiredFields() {
        if (flyToWaylineMode == null) {
            throw new IllegalStateException("flyToWaylineMode 未设置");
        }
        if (finishAction == null) {
            throw new IllegalStateException("finishAction 未设置");
        }
        if (autoFlightSpeed == null) {
            throw new IllegalStateException("autoFlightSpeed 未设置");
        }
        if (takeOffSecurityHeight == null) {
            throw new IllegalStateException("takeOffSecurityHeight 未设置");
        }
        if (globalTransitionalSpeed == null) {
            throw new IllegalStateException("globalTransitionalSpeed 未设置");
        }
        if (globalRTHHeight == null) {
            throw new IllegalStateException("globalRTHHeight 未设置");
        }
        if (templateId == null) {
            throw new IllegalStateException("templateId 未设置");
        }
        if (shootType == null) {
            throw new IllegalStateException("shootType 未设置");
        }
        if (direction == null) {
            throw new IllegalStateException("direction 未设置");
        }
        if (overlap == null) {
            throw new IllegalStateException("overlap 未设置");
        }
        if (height == null) {
            throw new IllegalStateException("height 未设置");
        }
        if (polygon == null) {
            throw new IllegalStateException("polygon 未设置");
        }
    }

    private void validateWpmlRequiredFields() {
        validateRequiredFields();
        if (droneInfo == null) {
            throw new IllegalStateException("toWpml 需要 droneInfo（droneInfo() 未设置）");
        }
        if (payloadInfo == null) {
            throw new IllegalStateException("toWpml 需要 payloadInfo（payloadInfo() 未设置）");
        }
    }

    private Kml<ExecuteFolder> buildWpmlKml() {
        MissionConfig wpmlMissionConfig = new MissionConfig(
            codeOf(flyToWaylineMode),
            codeOf(finishAction),
            codeOf(exitOnRCLost),
            codeOf(executeRCLostAction),
            takeOffSecurityHeight,
            null,
            null,
            globalTransitionalSpeed,
            globalRTHHeight,
            null,
            droneInfo,
            payloadInfo
        );

        ExecuteHeightMode executeHeightMode =
            ExecuteHeightMode.fromHeightMode(codeOf(heightMode));

        ExecuteFolder folder = new ExecuteFolder(
            templateId,
            executeHeightMode.code(),
            0,
            autoFlightSpeed,
            List.of()
        );

        Document<ExecuteFolder> document = new Document<>(
            author,
            createTime,
            updateTime,
            wpmlMissionConfig,
            folder
        );

        return new Kml<>(document);
    }

}
