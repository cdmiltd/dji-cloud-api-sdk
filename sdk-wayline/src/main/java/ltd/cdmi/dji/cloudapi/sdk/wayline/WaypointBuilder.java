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

import ltd.cdmi.dji.cloudapi.sdk.wayline.model.ActionGroup;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Placemark;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WaypointHeadingParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WaypointTurnParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.coordinate.Point;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingPathMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointTurnMode;

/**
 * {@link Placemark}（航点）的 Builder。
 *
 * <p>由 {@link WaypointTemplate#addWaypoint(java.util.function.Consumer)} 回调创建，
 * 定义航点坐标、高度（相对起飞点）、云台俯仰角、全局参数使用标志以及动作组列表。
 *
 * <p>高度语义：{@code height} 参数均为相对起飞点高度，与 DJI WPML {@code wpml:height} 定义一致。
 *
 * <p>示例：
 * <pre>{@code
 * .addWaypoint(w -> w
 *     .longitude(113.98057)
 *     .latitude(22.987663)
 *     .height(100)
 *     .gimbalPitchAngle(0)
 *     .addActionGroup(ag -> ag ...))
 * }</pre>
 *
 * @see WaypointTemplate
 * @see ActionGroupBuilder
 * @see Placemark
 */
public final class WaypointBuilder {

    private double longitude;
    private double latitude;
    private Double height;
    private Double ellipsoidHeight;
    private Double gimbalPitchAngle;
    private Integer useGlobalHeight = 1;
    private Integer useGlobalSpeed = 1;
    private Integer useGlobalHeadingParam = 1;
    private Integer useGlobalTurnParam = 1;
    private Double waypointSpeed;
    private WaypointHeadingParam waypointHeadingParam;
    private WaypointTurnParam waypointTurnParam;
    private final List<ActionGroup> actionGroups = new ArrayList<>();

    /**
     * 设置经度。
     *
     * @param longitude 经度，范围 [-180, 180]
     * @return this
     * @throws IllegalArgumentException 如果超出 [-180, 180]
     */
    public WaypointBuilder longitude(double longitude) {
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(
                "longitude 超出范围 [-180, 180]: " + longitude);
        }
        this.longitude = longitude;
        return this;
    }

    /**
     * 设置纬度。
     *
     * @param latitude 纬度，范围 [-90, 90]
     * @return this
     * @throws IllegalArgumentException 如果超出 [-90, 90]
     */
    public WaypointBuilder latitude(double latitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(
                "latitude 超出范围 [-90, 90]: " + latitude);
        }
        this.latitude = latitude;
        return this;
    }

    /**
     * 设置相对起飞点高度。
     *
     * @param height 相对起飞点高度（米）
     * @return this
     */
    public WaypointBuilder height(double height) {
        this.height = height;
        return this;
    }

    /**
     * 设置椭球高度（可选）。
     *
     * @param height 椭球高度（米）
     * @return this
     */
    public WaypointBuilder ellipsoidHeight(double height) {
        this.ellipsoidHeight = height;
        return this;
    }

    /**
     * 设置云台俯仰角。
     *
     * @param angle 俯仰角（度），范围 [-90, 90]
     * @return this
     */
    public WaypointBuilder gimbalPitchAngle(double angle) {
        this.gimbalPitchAngle = angle;
        return this;
    }

    /**
     * 设置是否使用全局高度参数，默认 1。
     *
     * @param v 0=使用航点独立高度，1=使用全局高度
     * @return this
     */
    public WaypointBuilder useGlobalHeight(int v) {
        this.useGlobalHeight = v;
        return this;
    }

    /**
     * 设置是否使用全局速度参数，默认 1。
     *
     * @param v 0=使用航点独立速度，1=使用全局速度
     * @return this
     */
    public WaypointBuilder useGlobalSpeed(int v) {
        this.useGlobalSpeed = v;
        return this;
    }

    /**
     * 设置是否使用全局航向参数，默认 1。
     *
     * @param v 0=使用航点独立航向，1=使用全局航向
     * @return this
     */
    public WaypointBuilder useGlobalHeadingParam(int v) {
        this.useGlobalHeadingParam = v;
        return this;
    }

    /**
     * 设置是否使用全局转弯参数，默认 1。
     *
     * @param v 0=使用航点独立转弯模式，1=使用全局转弯模式
     * @return this
     */
    public WaypointBuilder useGlobalTurnParam(int v) {
        this.useGlobalTurnParam = v;
        return this;
    }

    /**
     * 设置航点级速度（自动设置 useGlobalSpeed=0）。
     *
     * @param speed 速度（m/s），范围 [1, 15]
     * @return this
     * @throws IllegalArgumentException 如果超出 [1, 15]
     */
    public WaypointBuilder waypointSpeed(double speed) {
        if (speed < 1 || speed > 15) {
            throw new IllegalArgumentException(
                "waypointSpeed 超出范围 [1, 15]: " + speed);
        }
        this.useGlobalSpeed = 0;
        this.waypointSpeed = speed;
        return this;
    }

    /**
     * 设置航点级航向参数（自动设置 useGlobalHeadingParam=0）。
     *
     * @param mode     航向模式
     * @param angle    航向角（度），范围 [-180, 180]
     * @param poiPoint 兴趣点坐标（"纬度,经度,高度"），可为 null
     * @param pathMode 航向路径模式
     * @return this
     * @throws IllegalArgumentException 如果航向角超出 [-180, 180]
     */
    public WaypointBuilder waypointHeadingParam(WaypointHeadingMode mode, double angle,
                                                 String poiPoint, WaypointHeadingPathMode pathMode) {
        if (angle < -180 || angle > 180) {
            throw new IllegalArgumentException(
                "waypointHeadingAngle 超出范围 [-180, 180]: " + angle);
        }
        this.useGlobalHeadingParam = 0;
        this.waypointHeadingParam = new WaypointHeadingParam(
            mode.code(), angle, poiPoint, pathMode.code());
        return this;
    }

    /**
     * 设置航点级转弯参数（自动设置 useGlobalTurnParam=0）。
     *
     * @param mode        转弯模式
     * @param dampingDist 阻尼距离（米）
     * @return this
     */
    public WaypointBuilder waypointTurnParam(WaypointTurnMode mode, double dampingDist) {
        this.useGlobalTurnParam = 0;
        this.waypointTurnParam = new WaypointTurnParam(mode.code(), dampingDist);
        return this;
    }

    /**
     * 添加动作组到此航点。
     *
     * @param config 动作组配置回调
     * @return this
     */
    public WaypointBuilder addActionGroup(Consumer<ActionGroupBuilder> config) {
        ActionGroupBuilder builder = new ActionGroupBuilder();
        config.accept(builder);
        actionGroups.add(builder.build());
        return this;
    }

    /**
     * 构建 {@link Placemark} record。
     *
     * @param index 航点索引（由 {@link WaypointTemplate} 自动分配）
     * @return 航点 record
     */
    Placemark build(int index) {
        Point point = new Point(formatCoordinates(longitude, latitude));
        List<ActionGroup> groups = actionGroups.isEmpty() ? null : List.copyOf(actionGroups);
        return new Placemark(point, index, ellipsoidHeight, height,
            useGlobalHeight, useGlobalSpeed, useGlobalHeadingParam, useGlobalTurnParam,
            waypointSpeed, waypointHeadingParam, waypointTurnParam,
            gimbalPitchAngle, groups);
    }

    private static String formatCoordinates(double lon, double lat) {
        return String.format("%.6f,%.6f", lon, lat);
    }
}
