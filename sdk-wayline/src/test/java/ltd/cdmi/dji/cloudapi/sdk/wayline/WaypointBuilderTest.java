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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;

import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionActuatorFunc;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.ActionTriggerType;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointHeadingPathMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.WaypointTurnMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Placemark;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.TakePhotoParam;

/**
 * {@link WaypointBuilder} 单元测试。
 *
 * <p>验证坐标格式化、高度/云台俯仰角设置、useGlobalXxx 默认值及自动设置逻辑、
 * waypointSpeed / waypointHeadingParam / waypointTurnParam 的范围校验以及
 * build(int) 生成 {@link Placemark} record 的字段正确性。
 */
@DisplayName("WaypointBuilder 单元测试")
class WaypointBuilderTest {

    @Test
    @DisplayName("构建 Placemark：基本字段正确设置")
    void shouldBuildPlacemarkWithBasicFields() {
        Placemark p = new WaypointBuilder()
            .longitude(113.98057)
            .latitude(22.987663)
            .height(100)
            .gimbalPitchAngle(-30)
            .ellipsoidHeight(50)
            .build(3);

        assertEquals(3, p.index(), "index 应为 3");
        assertEquals(100.0, p.height(), "height 应为 100");
        assertEquals(50.0, p.ellipsoidHeight(), "ellipsoidHeight 应为 50");
        assertEquals(-30.0, p.gimbalPitchAngle(), "gimbalPitchAngle 应为 -30");
    }

    @Test
    @DisplayName("坐标格式化为 6 位小数 '经度,纬度'")
    void shouldFormatCoordinatesToSixDecimals() {
        Placemark p = new WaypointBuilder()
            .longitude(113.98057)
            .latitude(22.987663)
            .height(100)
            .build(0);

        assertEquals("113.980570,22.987663", p.point().coordinates(),
            "坐标应格式化为 '经度,纬度' 并保留 6 位小数");
    }

    @Test
    @DisplayName("默认 useGlobalXxx 标志均为 1")
    void shouldDefaultUseGlobalFlagsToOne() {
        Placemark p = new WaypointBuilder()
            .longitude(113.98)
            .latitude(22.98)
            .height(100)
            .build(0);

        assertEquals(1, p.useGlobalHeight(), "useGlobalHeight 默认应为 1");
        assertEquals(1, p.useGlobalSpeed(), "useGlobalSpeed 默认应为 1");
        assertEquals(1, p.useGlobalHeadingParam(), "useGlobalHeadingParam 默认应为 1");
        assertEquals(1, p.useGlobalTurnParam(), "useGlobalTurnParam 默认应为 1");
    }

    @Test
    @DisplayName("waypointSpeed 自动设置 useGlobalSpeed=0")
    void shouldAutoSetUseGlobalSpeedToZeroWhenSpeedSet() {
        Placemark p = new WaypointBuilder()
            .longitude(113.98)
            .latitude(22.98)
            .height(100)
            .waypointSpeed(7)
            .build(0);

        assertEquals(0, p.useGlobalSpeed(), "设置 waypointSpeed 后 useGlobalSpeed 应为 0");
        assertEquals(7.0, p.waypointSpeed(), "waypointSpeed 应为 7");
    }

    @Test
    @DisplayName("waypointHeadingParam 自动设置 useGlobalHeadingParam=0")
    void shouldAutoSetUseGlobalHeadingParamToZero() {
        Placemark p = new WaypointBuilder()
            .longitude(113.98)
            .latitude(22.98)
            .height(100)
            .waypointHeadingParam(WaypointHeadingMode.FOLLOW_WAYLINE, 0, null,
                WaypointHeadingPathMode.CLOCKWISE)
            .build(0);

        assertEquals(0, p.useGlobalHeadingParam(),
            "设置 waypointHeadingParam 后 useGlobalHeadingParam 应为 0");
        assertNotNull(p.waypointHeadingParam(), "waypointHeadingParam 不应为 null");
        assertEquals("followWayline", p.waypointHeadingParam().waypointHeadingMode(),
            "waypointHeadingMode 应为 followWayline");
        assertEquals(0.0, p.waypointHeadingParam().waypointHeadingAngle(),
            "waypointHeadingAngle 应为 0");
        assertNull(p.waypointHeadingParam().waypointPoiPoint(),
            "waypointPoiPoint 应为 null");
        assertEquals("clockwise", p.waypointHeadingParam().waypointHeadingPathMode(),
            "waypointHeadingPathMode 应为 clockwise");
    }

    @Test
    @DisplayName("waypointTurnParam 自动设置 useGlobalTurnParam=0")
    void shouldAutoSetUseGlobalTurnParamToZero() {
        Placemark p = new WaypointBuilder()
            .longitude(113.98)
            .latitude(22.98)
            .height(100)
            .waypointTurnParam(WaypointTurnMode.COORDINATE_TURN, 2.5)
            .build(0);

        assertEquals(0, p.useGlobalTurnParam(),
            "设置 waypointTurnParam 后 useGlobalTurnParam 应为 0");
        assertNotNull(p.waypointTurnParam(), "waypointTurnParam 不应为 null");
        assertEquals("coordinateTurn", p.waypointTurnParam().waypointTurnMode(),
            "waypointTurnMode 应为 coordinateTurn");
        assertEquals(2.5, p.waypointTurnParam().waypointTurnDampingDist(),
            "waypointTurnDampingDist 应为 2.5");
    }

    @Test
    @DisplayName("三个 waypoint 级参数同时设置时所有 useGlobal 标志均置 0")
    void shouldSetAllUseGlobalFlagsToZeroWhenAllPerWaypointParamsSet() {
        Placemark p = new WaypointBuilder()
            .longitude(113.98)
            .latitude(22.98)
            .height(100)
            .waypointSpeed(5)
            .waypointHeadingParam(WaypointHeadingMode.FIXED, 90, null,
                WaypointHeadingPathMode.COUNTER_CLOCKWISE)
            .waypointTurnParam(WaypointTurnMode.COORDINATE_TURN, 1.0)
            .build(0);

        assertEquals(0, p.useGlobalSpeed(), "useGlobalSpeed 应为 0");
        assertEquals(0, p.useGlobalHeadingParam(), "useGlobalHeadingParam 应为 0");
        assertEquals(0, p.useGlobalTurnParam(), "useGlobalTurnParam 应为 0");
        assertEquals(1, p.useGlobalHeight(), "useGlobalHeight 应保持默认 1");
    }

    @Test
    @DisplayName("useGlobalHeight 可手动设置为 0")
    void shouldAllowManualUseGlobalHeightOverride() {
        Placemark p = new WaypointBuilder()
            .longitude(113.98)
            .latitude(22.98)
            .height(100)
            .useGlobalHeight(0)
            .build(0);

        assertEquals(0, p.useGlobalHeight(), "useGlobalHeight 应为手动设置的 0");
    }

    @Test
    @DisplayName("无动作组时 actionGroups 为 null")
    void shouldReturnNullActionGroupsWhenNoneAdded() {
        Placemark p = new WaypointBuilder()
            .longitude(113.98)
            .latitude(22.98)
            .height(100)
            .build(0);

        assertNull(p.actionGroups(), "无动作组时 actionGroups 应为 null");
    }

    @Test
    @DisplayName("addActionGroup 添加动作组到列表")
    void shouldAddActionGroupsToList() {
        Placemark p = new WaypointBuilder()
            .longitude(113.98)
            .latitude(22.98)
            .height(100)
            .addActionGroup(ag -> ag
                .actionGroupId(0)
                .actionGroupStartIndex(0)
                .actionGroupEndIndex(0)
                .actionTriggerType(ActionTriggerType.REACH_POINT)
                .addAction(a -> a
                    .actionId(0)
                    .actionActuatorFunc(ActionActuatorFunc.TAKE_PHOTO)
                    .actionActuatorFuncParam(new TakePhotoParam(0, "p1", "wide", 1))))
            .build(0);

        assertNotNull(p.actionGroups(), "actionGroups 不应为 null");
        assertEquals(1, p.actionGroups().size(), "应包含 1 个动作组");
        assertEquals(0, p.actionGroups().get(0).actionGroupId(), "actionGroupId 应为 0");
    }

    @Test
    @DisplayName("经度超出 [-180,180] 抛出 IllegalArgumentException")
    void shouldRejectLongitudeOutOfRange() {
        assertThrows(IllegalArgumentException.class, () ->
            new WaypointBuilder().longitude(-180.1));
        assertThrows(IllegalArgumentException.class, () ->
            new WaypointBuilder().longitude(180.1));
    }

    @Test
    @DisplayName("经度边界值 -180 和 180 合法")
    void shouldAcceptBoundaryLongitude() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            new WaypointBuilder().longitude(-180).latitude(0).height(100).build(0));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            new WaypointBuilder().longitude(180).latitude(0).height(100).build(0));
    }

    @Test
    @DisplayName("纬度超出 [-90,90] 抛出 IllegalArgumentException")
    void shouldRejectLatitudeOutOfRange() {
        assertThrows(IllegalArgumentException.class, () ->
            new WaypointBuilder().latitude(-90.1));
        assertThrows(IllegalArgumentException.class, () ->
            new WaypointBuilder().latitude(90.1));
    }

    @Test
    @DisplayName("纬度边界值 -90 和 90 合法")
    void shouldAcceptBoundaryLatitude() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            new WaypointBuilder().longitude(0).latitude(-90).height(100).build(0));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            new WaypointBuilder().longitude(0).latitude(90).height(100).build(0));
    }

    @Test
    @DisplayName("waypointSpeed 超出 [1,15] 抛出 IllegalArgumentException")
    void shouldRejectWaypointSpeedOutOfRange() {
        assertThrows(IllegalArgumentException.class, () ->
            new WaypointBuilder().waypointSpeed(0.9));
        assertThrows(IllegalArgumentException.class, () ->
            new WaypointBuilder().waypointSpeed(15.1));
    }

    @Test
    @DisplayName("waypointSpeed 边界值 1 和 15 合法")
    void shouldAcceptBoundaryWaypointSpeed() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            new WaypointBuilder().longitude(0).latitude(0).height(100).waypointSpeed(1).build(0));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            new WaypointBuilder().longitude(0).latitude(0).height(100).waypointSpeed(15).build(0));
    }

    @Test
    @DisplayName("waypointHeadingParam 航向角超出 [-180,180] 抛出 IllegalArgumentException")
    void shouldRejectHeadingAngleOutOfRange() {
        assertThrows(IllegalArgumentException.class, () ->
            new WaypointBuilder().waypointHeadingParam(
                WaypointHeadingMode.FOLLOW_WAYLINE, -180.1, null, WaypointHeadingPathMode.CLOCKWISE));
        assertThrows(IllegalArgumentException.class, () ->
            new WaypointBuilder().waypointHeadingParam(
                WaypointHeadingMode.FOLLOW_WAYLINE, 180.1, null, WaypointHeadingPathMode.CLOCKWISE));
    }

    @Test
    @DisplayName("waypointHeadingParam 航向角边界值 -180 和 180 合法")
    void shouldAcceptBoundaryHeadingAngle() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            new WaypointBuilder().longitude(0).latitude(0).height(100)
                .waypointHeadingParam(WaypointHeadingMode.FIXED, -180, null,
                    WaypointHeadingPathMode.CLOCKWISE).build(0));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            new WaypointBuilder().longitude(0).latitude(0).height(100)
                .waypointHeadingParam(WaypointHeadingMode.FIXED, 180, null,
                    WaypointHeadingPathMode.CLOCKWISE).build(0));
    }

    @Test
    @DisplayName("waypointHeadingParam 支持 POI 兴趣点参数")
    void shouldSupportPoiPointInHeadingParam() {
        Placemark p = new WaypointBuilder()
            .longitude(113.98)
            .latitude(22.98)
            .height(100)
            .waypointHeadingParam(WaypointHeadingMode.TOWARD_POI, 0,
                "22.980000,113.980000,100", WaypointHeadingPathMode.CLOCKWISE)
            .build(0);

        assertEquals("towardPOI", p.waypointHeadingParam().waypointHeadingMode(),
            "waypointHeadingMode 应为 towardPOI");
        assertEquals("22.980000,113.980000,100",
            p.waypointHeadingParam().waypointPoiPoint(), "waypointPoiPoint 应正确设置");
    }

    @Test
    @DisplayName("链式调用返回 this")
    void shouldReturnSameBuilderInstance() {
        WaypointBuilder builder = new WaypointBuilder();
        assertSame(builder, builder.longitude(113.98));
        assertSame(builder, builder.latitude(22.98));
        assertSame(builder, builder.height(100));
        assertSame(builder, builder.ellipsoidHeight(50));
        assertSame(builder, builder.gimbalPitchAngle(0));
        assertSame(builder, builder.useGlobalHeight(0));
        assertSame(builder, builder.useGlobalSpeed(0));
        assertSame(builder, builder.useGlobalHeadingParam(0));
        assertSame(builder, builder.useGlobalTurnParam(0));
        assertSame(builder, builder.waypointSpeed(5));
        assertSame(builder, builder.waypointHeadingParam(
            WaypointHeadingMode.FOLLOW_WAYLINE, 0, null, WaypointHeadingPathMode.CLOCKWISE));
        assertSame(builder, builder.waypointTurnParam(WaypointTurnMode.COORDINATE_TURN, 1.0));
    }
}
