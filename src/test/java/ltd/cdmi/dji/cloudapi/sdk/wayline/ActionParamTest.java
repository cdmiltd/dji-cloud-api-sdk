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

import static org.junit.jupiter.api.Assertions.assertTrue;

import ltd.cdmi.dji.cloudapi.sdk.wayline.enumtype.GimbalRotateMode;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.CustomDirNameParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.GimbalEvenlyRotateParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.GimbalRotateParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.HoverParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.OrientedShootParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.PanoShotParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.RecordPointCloudParam;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.action.RotateYawParam;

/**
 * 动作参数 record 的 XML 序列化验证。
 *
 * <p>验证各 actionActuatorFuncParam 的字段完整性、字段顺序和 XML 元素名
 * 与 DJI WPML 共用元素文档一致。
 */
class ActionParamTest {

    @Test
    @DisplayName("gimbalRotate: 包含 gimbalHeadingYawBase 且字段顺序正确")
    void testGimbalRotateParam() {
        String xml = WpmlCodec.toXml(new GimbalRotateParam(
            0, "north",
            GimbalRotateMode.ABSOLUTE_ANGLE.code(),
            0, 0.0, 0, 0.0, 1, 30.0, 0, 0.0));

        assertTrue(xml.contains("<wpml:payloadPositionIndex>0</wpml:payloadPositionIndex>"), "应包含 payloadPositionIndex");
        assertTrue(xml.contains("<wpml:gimbalHeadingYawBase>north</wpml:gimbalHeadingYawBase>"), "应包含 gimbalHeadingYawBase");
        assertTrue(xml.contains("<wpml:gimbalRotateMode>absoluteAngle</wpml:gimbalRotateMode>"), "应包含 gimbalRotateMode");
        assertTrue(xml.contains("<wpml:gimbalYawRotateEnable>1</wpml:gimbalYawRotateEnable>"), "应包含 gimbalYawRotateEnable");
        assertTrue(xml.contains("<wpml:gimbalYawRotateAngle>30.0</wpml:gimbalYawRotateAngle>"), "应包含 gimbalYawRotateAngle");

        int payloadIdx = xml.indexOf("payloadPositionIndex");
        int headingBaseIdx = xml.indexOf("gimbalHeadingYawBase");
        int rotateModeIdx = xml.indexOf("gimbalRotateMode");
        assertTrue(payloadIdx < headingBaseIdx, "payloadPositionIndex 应在 gimbalHeadingYawBase 之前");
        assertTrue(headingBaseIdx < rotateModeIdx, "gimbalHeadingYawBase 应在 gimbalRotateMode 之前");
    }

    @Test
    @DisplayName("rotateYaw: 包含 aircraftHeading 和 aircraftPathMode")
    void testRotateYawParam() {
        String xml = WpmlCodec.toXml(new RotateYawParam(90.0, "clockwise"));

        assertTrue(xml.contains("<wpml:aircraftHeading>90.0</wpml:aircraftHeading>"), "应包含 aircraftHeading");
        assertTrue(xml.contains("<wpml:aircraftPathMode>clockwise</wpml:aircraftPathMode>"), "应包含 aircraftPathMode");
    }

    @Test
    @DisplayName("gimbalEvenlyRotate: gimbalPitchRotateAngle 在 payloadPositionIndex 之前")
    void testGimbalEvenlyRotateParam() {
        String xml = WpmlCodec.toXml(new GimbalEvenlyRotateParam(-45.0, 0));

        assertTrue(xml.contains("<wpml:gimbalPitchRotateAngle>-45.0</wpml:gimbalPitchRotateAngle>"), "应包含 gimbalPitchRotateAngle");
        assertTrue(xml.contains("<wpml:payloadPositionIndex>0</wpml:payloadPositionIndex>"), "应包含 payloadPositionIndex");

        int pitchIdx = xml.indexOf("gimbalPitchRotateAngle");
        int payloadIdx = xml.indexOf("payloadPositionIndex");
        assertTrue(pitchIdx < payloadIdx, "gimbalPitchRotateAngle 应在 payloadPositionIndex 之前");
    }

    @Test
    @DisplayName("hover: 包含 hoverTime")
    void testHoverParam() {
        String xml = WpmlCodec.toXml(new HoverParam(5.0));

        assertTrue(xml.contains("<wpml:hoverTime>5.0</wpml:hoverTime>"), "应包含 hoverTime");
    }

    @Test
    @DisplayName("panoShot: 包含全部4个字段")
    void testPanoShotParam() {
        String xml = WpmlCodec.toXml(new PanoShotParam(0, "wide", 1, "panoShot_360"));

        assertTrue(xml.contains("<wpml:payloadPositionIndex>0</wpml:payloadPositionIndex>"), "应包含 payloadPositionIndex");
        assertTrue(xml.contains("<wpml:payloadLensIndex>wide</wpml:payloadLensIndex>"), "应包含 payloadLensIndex");
        assertTrue(xml.contains("<wpml:useGlobalPayloadLensIndex>1</wpml:useGlobalPayloadLensIndex>"), "应包含 useGlobalPayloadLensIndex");
        assertTrue(xml.contains("<wpml:panoShotSubMode>panoShot_360</wpml:panoShotSubMode>"), "应包含 panoShotSubMode");
    }

    @Test
    @DisplayName("recordPointCloud: 包含 payloadPositionIndex 和 recordPointCloudOperate")
    void testRecordPointCloudParam() {
        String xml = WpmlCodec.toXml(new RecordPointCloudParam(0, "startRecord"));

        assertTrue(xml.contains("<wpml:payloadPositionIndex>0</wpml:payloadPositionIndex>"), "应包含 payloadPositionIndex");
        assertTrue(xml.contains("<wpml:recordPointCloudOperate>startRecord</wpml:recordPointCloudOperate>"), "应包含 recordPointCloudOperate");
    }

    @Test
    @DisplayName("customDirName: 包含 directoryName")
    void testCustomDirNameParam() {
        String xml = WpmlCodec.toXml(new CustomDirNameParam(0, "mission_001"));

        assertTrue(xml.contains("<wpml:payloadPositionIndex>0</wpml:payloadPositionIndex>"), "应包含 payloadPositionIndex");
        assertTrue(xml.contains("<wpml:directoryName>mission_001</wpml:directoryName>"), "应包含 directoryName");
    }

    @Test
    @DisplayName("orientedShoot: 包含全部28个字段")
    void testOrientedShootParam() {
        String xml = WpmlCodec.toXml(new OrientedShootParam(
            -45.0, 0.0,
            480, 360, 100, 100,
            24.0, 90.0, 1,
            0, "wide", 1,
            0.0, "uuid-001",
            960, 720,
            100, 0, 52,
            "/path/to/photo.jpg", "abc123md5", 102400, "suffix",
            280, 50, 0.002, 100,
            "normalPhoto"));

        assertTrue(xml.contains("<wpml:gimbalPitchRotateAngle>-45.0</wpml:gimbalPitchRotateAngle>"), "应包含 gimbalPitchRotateAngle");
        assertTrue(xml.contains("<wpml:gimbalYawRotateAngle>0.0</wpml:gimbalYawRotateAngle>"), "应包含 gimbalYawRotateAngle");
        assertTrue(xml.contains("<wpml:focusX>480</wpml:focusX>"), "应包含 focusX");
        assertTrue(xml.contains("<wpml:focusY>360</wpml:focusY>"), "应包含 focusY");
        assertTrue(xml.contains("<wpml:focalLength>24.0</wpml:focalLength>"), "应包含 focalLength");
        assertTrue(xml.contains("<wpml:aircraftHeading>90.0</wpml:aircraftHeading>"), "应包含 aircraftHeading");
        assertTrue(xml.contains("<wpml:accurateFrameValid>1</wpml:accurateFrameValid>"), "应包含 accurateFrameValid");
        assertTrue(xml.contains("<wpml:actionUUID>uuid-001</wpml:actionUUID>"), "应包含 actionUUID");
        assertTrue(xml.contains("<wpml:AFPos>100</wpml:AFPos>"), "应包含 AFPos");
        assertTrue(xml.contains("<wpml:orientedCameraType>52</wpml:orientedCameraType>"), "应包含 orientedCameraType");
        assertTrue(xml.contains("<wpml:orientedPhotoMode>normalPhoto</wpml:orientedPhotoMode>"), "应包含 orientedPhotoMode");
    }
}
