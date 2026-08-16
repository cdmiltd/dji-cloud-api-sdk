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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.CoordinateMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ExecuteHeightMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ExecuteRCLostAction;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ExitOnRCLost;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FinishAction;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.FlyToWaylineMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.GimbalPitchMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.HeightMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.PositioningType;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingPathMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointTurnMode;
import static ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WpmlEnum.codeOf;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Document;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.DroneInfo;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Folder;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.GlobalWaypointHeadingParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.AutoRerouteInfo;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Kml;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.MissionConfig;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.PayloadInfo;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Placemark;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.mapping.WaylineCoordinateSysParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.execute.ExecuteFolder;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.execute.ExecutePlacemark;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WaypointHeadingParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WaypointTurnParam;

/**
 * DJI WPML template.kml 航点飞行模板生成器（入口类）。
 *
 * <p>通过 Builder 模式构造合法的 {@code template.kml} XML 字符串，
 * 覆盖航点飞行模板（{@code templateType=waypoint}）+ 完整 actionGroup 动作组。
 *
 * <p>高度语义：所有 {@code height} 参数为相对起飞点高度，
 * 与 DJI WPML {@code wpml:height} 定义一致。
 *
 * <p>使用示例：
 * <pre>{@code
 * String kml = WaypointTemplate.builder()
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
 *     .gimbalPitchMode(GimbalPitchMode.USE_POINT_SETTING)
 *     .globalHeight(100)
 *     .globalWaypointHeadingMode(WaypointHeadingMode.FOLLOW_WAYLINE)
 *     .globalWaypointHeadingPathMode(WaypointHeadingPathMode.CLOCKWISE)
 *     .globalWaypointTurnMode(WaypointTurnMode.TO_POINT_AND_STOP_WITH_DISCONTINUITY_CURVATURE)
 *     .globalUseStraightLine(0)
 *     .addWaypoint(w -> w.longitude(113.98057).latitude(22.987663).height(100))
 *     .toXml();
 * }</pre>
 *
 * @see WaypointBuilder
 * @see ActionGroupBuilder
 * @see ActionBuilder
 * @see WpmlCodec
 */
public final class WaypointTemplate {

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
    private AutoRerouteInfo autoRerouteInfo;
    private DroneInfo droneInfo;
    private PayloadInfo payloadInfo;

    // ── Folder 模板配置 ──
    private Integer templateId;
    private CoordinateMode coordinateMode;
    private HeightMode heightMode;
    private Double globalShootHeight;
    private PositioningType positioningType;
    private Double autoFlightSpeed;
    private GimbalPitchMode gimbalPitchMode;
    private Double globalHeight;
    private WaypointHeadingMode globalWaypointHeadingMode;
    private Double globalWaypointHeadingAngle;
    private String globalWaypointPoiPoint;
    private WaypointHeadingPathMode globalWaypointHeadingPathMode;
    private WaypointTurnMode globalWaypointTurnMode;
    private Double globalWaypointTurnDampingDist;
    private Integer globalUseStraightLine;
    private Integer waylineId;

    // ── 航点列表 ──
    private final List<Placemark> placemarks = new ArrayList<>();

    private WaypointTemplate() {
    }

    /**
     * 创建 Builder 实例。
     *
     * @return 新的 WaypointTemplate 实例
     */
    public static WaypointTemplate builder() {
        return new WaypointTemplate();
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
    public WaypointTemplate author(String author) {
        this.author = author;
        return this;
    }

    /**
     * 设置创建时间。
     *
     * @param epochMs Unix 时间戳（毫秒）
     * @return this
     */
    public WaypointTemplate createTime(long epochMs) {
        this.createTime = epochMs;
        return this;
    }

    /**
     * 设置更新时间。
     *
     * @param epochMs Unix 时间戳（毫秒）
     * @return this
     */
    public WaypointTemplate updateTime(long epochMs) {
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
    public WaypointTemplate flyToWaylineMode(FlyToWaylineMode mode) {
        this.flyToWaylineMode = mode;
        return this;
    }

    /**
     * 设置航线结束动作。
     *
     * @param action 结束动作
     * @return this
     */
    public WaypointTemplate finishAction(FinishAction action) {
        this.finishAction = action;
        return this;
    }

    /**
     * 设置遥控器断连退出策略。
     *
     * @param exit 退出策略
     * @return this
     */
    public WaypointTemplate exitOnRCLost(ExitOnRCLost exit) {
        this.exitOnRCLost = exit;
        return this;
    }

    /**
     * 设置遥控器断连执行动作。
     *
     * @param action 执行动作
     * @return this
     */
    public WaypointTemplate executeRCLostAction(ExecuteRCLostAction action) {
        this.executeRCLostAction = action;
        return this;
    }

    /**
     * 设置安全起飞高度。
     *
     * @param height 安全起飞高度（米），遥控器 [1.2, 1500]，机场 [8, 1500]
     * @return this
     */
    public WaypointTemplate takeOffSecurityHeight(double height) {
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
    public WaypointTemplate takeOffRefPoint(double latitude, double longitude, double height) {
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
    public WaypointTemplate takeOffRefPointAGLHeight(double height) {
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
    public WaypointTemplate globalTransitionalSpeed(double speed) {
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
    public WaypointTemplate globalRTHHeight(double height) {
        if (height < 2 || height > 1500) {
            throw new IllegalArgumentException(
                "globalRTHHeight 超出范围 [2, 1500]: " + height);
        }
        this.globalRTHHeight = height;
        return this;
    }

    /**
     * 设置自动绕行信息（可选，仅 M3D/M3TD、M4D/M4TD、M4E/M4T 支持）。
     *
     * <p>当航线遇到禁飞区/限飞区时，飞行器根据该配置自动绕行。
     * 未设置时输出 null（其他机型不输出此字段）。
     *
     * @param missionAutoRerouteMode      任务航线绕行模式（0=不开启, 1=开启）
     * @param transitionalAutoRerouteMode 过渡航线绕行模式（0=不开启, 1=开启）
     * @return this
     */
    public WaypointTemplate autoRerouteInfo(int missionAutoRerouteMode, int transitionalAutoRerouteMode) {
        this.autoRerouteInfo = new AutoRerouteInfo(missionAutoRerouteMode, transitionalAutoRerouteMode);
        return this;
    }

    /**
     * 设置无人机机型信息。
     *
     * @param droneEnumValue    无人机主类型枚举值（如 67 = M30）
     * @param droneSubEnumValue 无人机子类型枚举值
     * @return this
     */
    public WaypointTemplate droneInfo(int droneEnumValue, int droneSubEnumValue) {
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
    public WaypointTemplate payloadInfo(int payloadEnumValue, int payloadPositionIndex) {
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
    public WaypointTemplate templateId(int id) {
        if (id < 0 || id > 65535) {
            throw new IllegalArgumentException(
                "templateId 超出范围 [0, 65535]: " + id);
        }
        this.templateId = id;
        return this;
    }

    /**
     * 设置坐标模式。
     *
     * @param mode 坐标模式
     * @return this
     */
    public WaypointTemplate coordinateMode(CoordinateMode mode) {
        this.coordinateMode = mode;
        return this;
    }

    /**
     * 设置高度模式。
     *
     * @param mode 高度模式
     * @return this
     */
    public WaypointTemplate heightMode(HeightMode mode) {
        this.heightMode = mode;
        return this;
    }

    /**
     * 设置全局拍摄高度。
     *
     * @param height 拍摄高度（米）
     * @return this
     */
    public WaypointTemplate globalShootHeight(double height) {
        this.globalShootHeight = height;
        return this;
    }

    /**
     * 设置定位类型。
     *
     * @param type 定位类型
     * @return this
     */
    public WaypointTemplate positioningType(PositioningType type) {
        this.positioningType = type;
        return this;
    }

    /**
     * 设置自动飞行速度。
     *
     * @param speed 飞行速度（m/s），范围 [1, 15]
     * @return this
     * @throws IllegalArgumentException 如果超出 [1, 15]
     */
    public WaypointTemplate autoFlightSpeed(double speed) {
        if (speed < 1 || speed > 15) {
            throw new IllegalArgumentException(
                "autoFlightSpeed 超出范围 [1, 15]: " + speed);
        }
        this.autoFlightSpeed = speed;
        return this;
    }

    /**
     * 设置云台俯仰角模式。
     *
     * @param mode 俯仰角模式
     * @return this
     */
    public WaypointTemplate gimbalPitchMode(GimbalPitchMode mode) {
        this.gimbalPitchMode = mode;
        return this;
    }

    /**
     * 设置全局航点高度（相对起飞点）。
     *
     * @param height 高度（米）
     * @return this
     */
    public WaypointTemplate globalHeight(double height) {
        this.globalHeight = height;
        return this;
    }

    /**
     * 设置全局航点航向模式。
     *
     * @param mode 航向模式
     * @return this
     */
    public WaypointTemplate globalWaypointHeadingMode(WaypointHeadingMode mode) {
        this.globalWaypointHeadingMode = mode;
        return this;
    }

    /**
     * 设置全局航点航向角度。
     *
     * @param angle 航向角（度），范围 [-180, 180]
     * @return this
     * @throws IllegalArgumentException 如果超出 [-180, 180]
     */
    public WaypointTemplate globalWaypointHeadingAngle(double angle) {
        if (angle < -180 || angle > 180) {
            throw new IllegalArgumentException(
                "globalWaypointHeadingAngle 超出范围 [-180, 180]: " + angle);
        }
        this.globalWaypointHeadingAngle = angle;
        return this;
    }

    /**
     * 设置全局航点兴趣点坐标。
     *
     * @param latitude  纬度，范围 [-90, 90]
     * @param longitude 经度，范围 [-180, 180]
     * @param height    高度（米）
     * @return this
     * @throws IllegalArgumentException 如果纬度或经度超出范围
     */
    public WaypointTemplate globalWaypointPoiPoint(double latitude, double longitude, double height) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(
                "latitude 超出范围 [-90, 90]: " + latitude);
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(
                "longitude 超出范围 [-180, 180]: " + longitude);
        }
        this.globalWaypointPoiPoint = String.format("%.6f,%.6f,%.6f", latitude, longitude, height);
        return this;
    }

    /**
     * 设置全局航点航向路径模式。
     *
     * @param mode 航向路径模式
     * @return this
     */
    public WaypointTemplate globalWaypointHeadingPathMode(WaypointHeadingPathMode mode) {
        this.globalWaypointHeadingPathMode = mode;
        return this;
    }

    /**
     * 设置全局航点转弯模式。
     *
     * @param mode 转弯模式
     * @return this
     */
    public WaypointTemplate globalWaypointTurnMode(WaypointTurnMode mode) {
        this.globalWaypointTurnMode = mode;
        return this;
    }

    /**
     * 设置全局航点转弯阻尼距离。
     *
     * <p>仅在 waylines.wpml 中输出，所有航点共用此值。
     * 未设置时默认 0.0。
     *
     * @param dist 阻尼距离（米）
     * @return this
     */
    public WaypointTemplate globalWaypointTurnDampingDist(double dist) {
        this.globalWaypointTurnDampingDist = dist;
        return this;
    }

    /**
     * 设置是否使用直线飞行。
     *
     * @param value 0=曲线飞行，1=直线飞行
     * @return this
     * @throws IllegalArgumentException 如果非 0 或 1
     */
    public WaypointTemplate globalUseStraightLine(int value) {
        if (value != 0 && value != 1) {
            throw new IllegalArgumentException(
                "globalUseStraightLine 只接受 0 或 1: " + value);
        }
        this.globalUseStraightLine = value;
        return this;
    }

    /**
     * 设置执行航线 ID（仅在 waylines.wpml 中输出）。
     *
     * <p>用于多航线场景下的航线标识。未设置时默认 0。
     *
     * @param id 航线 ID
     * @return this
     */
    public WaypointTemplate waylineId(int id) {
        this.waylineId = id;
        return this;
    }

    // ════════════════════════════════════════════
    //  航点
    // ════════════════════════════════════════════

    /**
     * 添加一个航点。
     *
     * <p>航点索引由调用顺序自动分配（从 0 开始递增）。
     *
     * @param config 航点配置回调
     * @return this
     */
    public WaypointTemplate addWaypoint(Consumer<WaypointBuilder> config) {
        WaypointBuilder builder = new WaypointBuilder();
        config.accept(builder);
        placemarks.add(builder.build(placemarks.size()));
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
        Kml kml = buildKml();
        return WpmlCodec.toXml(kml);
    }

    /**
     * 生成 waylines.wpml XML 字符串（飞机直接执行文件）。
     *
     * <p>从 Builder 内存模型转换生成，与 {@link #toXml()} 共享同一状态。
     * 机型通过 {@link #droneInfo(int, int)} / {@link #payloadInfo(int, int)}
     * 直接注入 missionConfig。
     *
     * @return XML 字符串，含 XML 声明和格式化缩进
     * @throws IllegalStateException 如果必需字段缺失（droneInfo/payloadInfo/航点等），
     *         或 useGlobalXxx=0 但未设置对应的航点级参数
     */
    public String toWpml() {
        validateWpmlRequiredFields();
        Kml<ExecuteFolder> kml = buildWpmlKml();
        return WpmlCodec.toXml(kml);
    }

    /**
     * 将 template.kml + waylines.wpml 打包为 DJI KMZ（ZIP）格式。
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

    // ════════════════════════════════════════════
    //  内部构建逻辑
    // ════════════════════════════════════════════

    private Kml<Folder> buildKml() {
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
            autoRerouteInfo,
            droneInfo,
            payloadInfo
        );

        WaylineCoordinateSysParam coordSysParam = new WaylineCoordinateSysParam(
            codeOf(coordinateMode),
            codeOf(heightMode),
            globalShootHeight,
            codeOf(positioningType),
            null,
            null
        );

        GlobalWaypointHeadingParam headingParam = new GlobalWaypointHeadingParam(
            codeOf(globalWaypointHeadingMode),
            globalWaypointHeadingAngle,
            globalWaypointPoiPoint,
            codeOf(globalWaypointHeadingPathMode)
        );

        Folder folder = new Folder(
            "waypoint",
            templateId,
            coordSysParam,
            autoFlightSpeed,
            codeOf(gimbalPitchMode),
            globalHeight,
            headingParam,
            codeOf(globalWaypointTurnMode),
            globalUseStraightLine,
            List.copyOf(placemarks)
        );

        Document<Folder> document = new Document<>(
            author,
            createTime,
            updateTime,
            missionConfig,
            folder
        );

        return new Kml<>(document);
    }

    private void validateRequiredFields() {
        if (placemarks.isEmpty()) {
            throw new IllegalStateException("至少需要一个航点（addWaypoint）");
        }
        if (flyToWaylineMode == null) {
            throw new IllegalStateException("flyToWaylineMode 未设置");
        }
        if (finishAction == null) {
            throw new IllegalStateException("finishAction 未设置");
        }
        if (globalHeight == null) {
            throw new IllegalStateException("globalHeight 未设置");
        }
        if (autoFlightSpeed == null) {
            throw new IllegalStateException("autoFlightSpeed 未设置");
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
        if (globalWaypointHeadingMode == null) {
            throw new IllegalStateException("toWpml 需要 globalWaypointHeadingMode");
        }
        if (globalWaypointTurnMode == null) {
            throw new IllegalStateException("toWpml 需要 globalWaypointTurnMode");
        }
        for (Placemark p : placemarks) {
            if (p.useGlobalSpeed() != null && p.useGlobalSpeed() == 0 && p.waypointSpeed() == null) {
                throw new IllegalStateException(
                    "useGlobalSpeed=0 但未设置 waypointSpeed（航点 #" + p.index() + "）");
            }
            if (p.useGlobalHeadingParam() != null && p.useGlobalHeadingParam() == 0 && p.waypointHeadingParam() == null) {
                throw new IllegalStateException(
                    "useGlobalHeadingParam=0 但未设置 waypointHeadingParam（航点 #" + p.index() + "）");
            }
            if (p.useGlobalTurnParam() != null && p.useGlobalTurnParam() == 0 && p.waypointTurnParam() == null) {
                throw new IllegalStateException(
                    "useGlobalTurnParam=0 但未设置 waypointTurnParam（航点 #" + p.index() + "）");
            }
        }
    }

    private Kml<ExecuteFolder> buildWpmlKml() {
        // missionConfig：移除 takeOffRefPoint / takeOffRefPointAGLHeight（waylines 不需要，
        // 置 null 后 WpmlCodec 的 NON_NULL 策略自动跳过）
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
            autoRerouteInfo,
            droneInfo,
            payloadInfo
        );

        ExecuteHeightMode executeHeightMode =
            ExecuteHeightMode.fromHeightMode(codeOf(heightMode));

        List<ExecutePlacemark> executePlacemarks = new ArrayList<>();
        for (Placemark p : placemarks) {
            executePlacemarks.add(convertToExecutePlacemark(p));
        }

        ExecuteFolder folder = new ExecuteFolder(
            templateId,
            executeHeightMode.code(),
            waylineId != null ? waylineId : 0,
            autoFlightSpeed,
            executePlacemarks
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

    private ExecutePlacemark convertToExecutePlacemark(Placemark p) {
        // 速度：useGlobalSpeed=0 时用航点级，否则用全局
        Double wpSpeed = (p.useGlobalSpeed() != null && p.useGlobalSpeed() == 0)
            ? p.waypointSpeed() : autoFlightSpeed;

        // 航向参数：useGlobalHeadingParam=0 时用航点级，否则用全局
        WaypointHeadingParam headingParam = (p.useGlobalHeadingParam() != null && p.useGlobalHeadingParam() == 0)
            ? p.waypointHeadingParam()
            : new WaypointHeadingParam(
                codeOf(globalWaypointHeadingMode),
                globalWaypointHeadingAngle,
                globalWaypointPoiPoint,
                codeOf(globalWaypointHeadingPathMode)
            );

        // 转弯参数：useGlobalTurnParam=0 时用航点级，否则用全局
        WaypointTurnParam turnParam = (p.useGlobalTurnParam() != null && p.useGlobalTurnParam() == 0)
            ? p.waypointTurnParam()
            : new WaypointTurnParam(
                codeOf(globalWaypointTurnMode),
                globalWaypointTurnDampingDist != null ? globalWaypointTurnDampingDist : 0.0
            );

        return new ExecutePlacemark(
            p.point(),
            p.index(),
            p.height(),
            wpSpeed,
            p.gimbalPitchAngle(),
            headingParam,
            turnParam,
            p.actionGroups()
        );
    }

}
