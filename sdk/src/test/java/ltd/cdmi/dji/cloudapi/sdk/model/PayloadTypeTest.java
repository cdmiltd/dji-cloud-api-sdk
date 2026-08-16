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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PayloadType 协议层能力测试。
 *
 * <p>验证范围：
 * <ul>
 *   <li>{@code byAircraft(DroneModel)} — 飞行器→内置主相机反查（基于 compatibleAircraft）</li>
 *   <li>{@code auxiliaryCameraOf(DroneModel)} — 飞行器→辅助影像反查（M30 系列=39-0-7, M3D/M4D 系列=176-0-0）</li>
 *   <li>{@code AUXILIARY_CAMERA_LEGACY/AUXILIARY_CAMERA_NEW} 枚举值与 cameraIndex()</li>
 *   <li>{@code isThermal()} 热成像判断</li>
 * </ul>
 *
 * <p>事实依据：DJI Cloud API 官方文档"相机枚举值"表
 * （<a href="https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/product-support.html">产品支持</a>）。
 */
class PayloadTypeTest {

    // ============ byAircraft: 飞行器→内置主相机 ============

    @Test
    void byAircraft_M4D应返回M4D_CAMERA() {
        assertEquals(PayloadType.M4D_CAMERA, PayloadType.byAircraft(DroneModel.M4D));
        assertEquals("98-0-0", PayloadType.byAircraft(DroneModel.M4D).cameraIndex());
    }

    @Test
    void byAircraft_M4TD应返回M4TD_CAMERA() {
        assertEquals(PayloadType.M4TD_CAMERA, PayloadType.byAircraft(DroneModel.M4TD));
    }

    @Test
    void byAircraft_M30应返回M30_CAMERA() {
        assertEquals(PayloadType.M30_CAMERA, PayloadType.byAircraft(DroneModel.M30));
    }

    @Test
    void byAircraft_M30T应返回M30T_CAMERA() {
        assertEquals(PayloadType.M30T_CAMERA, PayloadType.byAircraft(DroneModel.M30T));
    }

    @Test
    void byAircraft_M3D应返回M3D_CAMERA() {
        assertEquals(PayloadType.M3D_CAMERA, PayloadType.byAircraft(DroneModel.M3D));
    }

    @Test
    void byAircraft_MAVIC_3E应返回MAVIC_3E_CAMERA() {
        assertEquals(PayloadType.MAVIC_3E_CAMERA, PayloadType.byAircraft(DroneModel.MAVIC_3E));
    }

    @Test
    void byAircraft_M400应返回null_因M400搭载通用云台非内置主相机() {
        // M400 搭载 H30/H30T 通用云台负载（compatibleAircraft=null），byAircraft 返回 null
        assertNull(PayloadType.byAircraft(DroneModel.M400));
    }

    @Test
    void byAircraft_M350_RTK应返回null_因M350搭载通用云台() {
        assertNull(PayloadType.byAircraft(DroneModel.M350_RTK));
    }

    @Test
    void byAircraft_null应返回null() {
        assertNull(PayloadType.byAircraft(null));
    }

    // ============ auxiliaryCameraOf: 飞行器→辅助影像 ============

    @Test
    void auxiliaryCameraOf_M30应返回AUXILIARY_CAMERA_LEGACY_39_0_7() {
        // DJI 官方文档：Matrice 30 FPV = 39-0-7（旧机型辅助影像）
        assertEquals(PayloadType.AUXILIARY_CAMERA_LEGACY, PayloadType.auxiliaryCameraOf(DroneModel.M30));
        assertEquals("39-0-7", PayloadType.auxiliaryCameraOf(DroneModel.M30).cameraIndex());
    }

    @Test
    void auxiliaryCameraOf_M30T应返回AUXILIARY_CAMERA_LEGACY() {
        assertEquals(PayloadType.AUXILIARY_CAMERA_LEGACY, PayloadType.auxiliaryCameraOf(DroneModel.M30T));
    }

    @Test
    void auxiliaryCameraOf_M3D应返回AUXILIARY_CAMERA_NEW_176_0_0() {
        // DJI 官方文档：Matrice 3D 辅助影像 = 176-0-0（新机型辅助影像）
        assertEquals(PayloadType.AUXILIARY_CAMERA_NEW, PayloadType.auxiliaryCameraOf(DroneModel.M3D));
        assertEquals("176-0-0", PayloadType.auxiliaryCameraOf(DroneModel.M3D).cameraIndex());
    }

    @Test
    void auxiliaryCameraOf_M3TD应返回AUXILIARY_CAMERA_NEW() {
        assertEquals(PayloadType.AUXILIARY_CAMERA_NEW, PayloadType.auxiliaryCameraOf(DroneModel.M3TD));
    }

    @Test
    void auxiliaryCameraOf_M4D应返回AUXILIARY_CAMERA_NEW() {
        assertEquals(PayloadType.AUXILIARY_CAMERA_NEW, PayloadType.auxiliaryCameraOf(DroneModel.M4D));
    }

    @Test
    void auxiliaryCameraOf_M4TD应返回AUXILIARY_CAMERA_NEW() {
        assertEquals(PayloadType.AUXILIARY_CAMERA_NEW, PayloadType.auxiliaryCameraOf(DroneModel.M4TD));
    }

    @Test
    void auxiliaryCameraOf_M400应返回AUXILIARY_CAMERA_LEGACY() {
        // DJI 官方文档：Matrice 400 FPV = 39-0-7
        assertEquals(PayloadType.AUXILIARY_CAMERA_LEGACY, PayloadType.auxiliaryCameraOf(DroneModel.M400));
    }

    @Test
    void auxiliaryCameraOf_M350_RTK应返回AUXILIARY_CAMERA_LEGACY() {
        assertEquals(PayloadType.AUXILIARY_CAMERA_LEGACY, PayloadType.auxiliaryCameraOf(DroneModel.M350_RTK));
    }

    @Test
    void auxiliaryCameraOf_MAVIC_3E应返回null_因Mavic3E无辅助影像() {
        // DJI 官方文档相机枚举值表中 Mavic 3 行业系列无辅助影像条目
        assertNull(PayloadType.auxiliaryCameraOf(DroneModel.MAVIC_3E));
    }

    @Test
    void auxiliaryCameraOf_null应返回null() {
        assertNull(PayloadType.auxiliaryCameraOf(null));
    }

    // ============ 枚举值与 cameraIndex ============

    @Test
    void AUXILIARY_CAMERA_LEGACY_cameraIndex应为39_0_7() {
        assertEquals("39-0-7", PayloadType.AUXILIARY_CAMERA_LEGACY.cameraIndex());
    }

    @Test
    void AUXILIARY_CAMERA_NEW_cameraIndex应为176_0_0() {
        assertEquals("176-0-0", PayloadType.AUXILIARY_CAMERA_NEW.cameraIndex());
    }

    @Test
    void DOCK_CAMERA_cameraIndex应为165_0_7() {
        assertEquals("165-0-7", PayloadType.DOCK_CAMERA.cameraIndex());
    }

    // ============ isThermal（回归保护） ============

    @Test
    void isThermal_热成像相机应返回true() {
        assertTrue(PayloadType.M30T_CAMERA.isThermal());
        assertTrue(PayloadType.M4TD_CAMERA.isThermal());
        assertTrue(PayloadType.M3TD_CAMERA.isThermal());
        assertTrue(PayloadType.H20T.isThermal());
        assertTrue(PayloadType.H30T.isThermal());
    }

    @Test
    void isThermal_非热成像相机应返回false() {
        assertFalse(PayloadType.M30_CAMERA.isThermal());
        assertFalse(PayloadType.M4D_CAMERA.isThermal());
        assertFalse(PayloadType.H20.isThermal());
        assertFalse(PayloadType.H30.isThermal());
        assertFalse(PayloadType.AUXILIARY_CAMERA_LEGACY.isThermal());
        assertFalse(PayloadType.AUXILIARY_CAMERA_NEW.isThermal());
    }
}
