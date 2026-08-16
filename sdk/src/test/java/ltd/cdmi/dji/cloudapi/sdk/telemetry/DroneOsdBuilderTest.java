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

import ltd.cdmi.dji.cloudapi.sdk.telemetry.nested.Battery;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link DroneOsd.Builder} 合并辅助测试。
 *
 * <p>验证三个核心能力：
 * <ol>
 *   <li>{@code builder()} 从零构造完整 DroneOsd</li>
 *   <li>{@code toBuilder()} 保留原 record 全部字段值</li>
 *   <li>{@code mergeNonNullFrom(source)} 仅覆盖 source 中非 null 字段（高频遥测合并语义）</li>
 * </ol>
 *
 * <p>使用场景：调用方收到无人机自报 OSD（高频字段非 null），需要覆盖式合并到缓存中的现有 OSD。
 */
@DisplayName("DroneOsd.Builder 合并辅助")
class DroneOsdBuilderTest {

    @Test
    @DisplayName("builder() 从零构造完整 DroneOsd")
    void builder_应构造包含所有设置字段的Record() {
        Battery battery = new Battery(85, 1200, null, null, null);
        DroneOsd osd = DroneOsd.builder()
                .modeCode(2)
                .latitude(30.67)
                .longitude(104.07)
                .elevation(120.0)
                .horizontalSpeed(15.5)
                .battery(battery)
                .build();

        assertEquals(2, osd.modeCode());
        assertEquals(30.67, osd.latitude());
        assertEquals(104.07, osd.longitude());
        assertEquals(120.0, osd.elevation());
        assertEquals(15.5, osd.horizontalSpeed());
        assertEquals(battery, osd.battery());
        // 未设置字段应为 null
        assertNull(osd.height());
        assertNull(osd.verticalSpeed());
    }

    @Test
    @DisplayName("toBuilder() 保留原 record 全部字段值")
    void toBuilder_应保留原Record全部字段值() {
        Battery battery = new Battery(90, 1500, null, null, null);
        DroneOsd original = DroneOsd.builder()
                .modeCode(1)
                .latitude(30.0)
                .longitude(104.0)
                .height(200.0)
                .elevation(100.0)
                .battery(battery)
                .build();

        DroneOsd rebuilt = original.toBuilder().build();

        assertEquals(original, rebuilt);
        assertEquals(1, rebuilt.modeCode());
        assertEquals(30.0, rebuilt.latitude());
        assertEquals(200.0, rebuilt.height());
        assertEquals(battery, rebuilt.battery());
    }

    @Test
    @DisplayName("mergeNonNullFrom 仅覆盖 source 中非 null 字段")
    void mergeNonNullFrom_应仅覆盖SourceNonNull字段() {
        // 现有 OSD（缓存中的完整状态）
        DroneOsd existing = DroneOsd.builder()
                .modeCode(1)
                .latitude(30.0)
                .longitude(104.0)
                .height(200.0)
                .elevation(100.0)
                .horizontalSpeed(10.0)
                .build();

        // partial OSD（高频遥测字段，其他字段为 null）
        DroneOsd partial = DroneOsd.builder()
                .horizontalSpeed(15.5)  // 更新速度
                .elevation(110.0)       // 更新高度
                .build();

        // 合并：partial 中非 null 字段覆盖 existing
        DroneOsd merged = existing.toBuilder()
                .mergeNonNullFrom(partial)
                .build();

        // 被覆盖的字段
        assertEquals(15.5, merged.horizontalSpeed());
        assertEquals(110.0, merged.elevation());
        // 未被覆盖的字段保留原值
        assertEquals(1, merged.modeCode());
        assertEquals(30.0, merged.latitude());
        assertEquals(104.0, merged.longitude());
        assertEquals(200.0, merged.height());
    }

    @Test
    @DisplayName("mergeNonNullFrom(null) 不改变任何字段")
    void mergeNonNullFrom_null_不应改变任何字段() {
        DroneOsd existing = DroneOsd.builder()
                .modeCode(1)
                .latitude(30.0)
                .build();

        DroneOsd merged = existing.toBuilder()
                .mergeNonNullFrom(null)
                .build();

        assertEquals(existing, merged);
    }

    @Test
    @DisplayName("多次 mergeNonNullFrom 实现多源合并")
    void mergeNonNullFrom_多次调用应实现多源合并() {
        DroneOsd existing = DroneOsd.builder()
                .modeCode(1)
                .latitude(30.0)
                .build();

        DroneOsd source1 = DroneOsd.builder()
                .longitude(104.0)
                .build();

        DroneOsd source2 = DroneOsd.builder()
                .horizontalSpeed(15.0)
                .build();

        DroneOsd merged = existing.toBuilder()
                .mergeNonNullFrom(source1)
                .mergeNonNullFrom(source2)
                .build();

        assertEquals(1, merged.modeCode());      // existing
        assertEquals(30.0, merged.latitude());  // existing
        assertEquals(104.0, merged.longitude()); // source1
        assertEquals(15.0, merged.horizontalSpeed()); // source2
    }

    @Test
    @DisplayName("builder() 链式构造支持嵌套 record")
    void builder_应支持嵌套Record构造() {
        DroneOsd osd = DroneOsd.builder()
                .modeCode(0)
                .battery(new Battery(100, 1500, null, null, null))
                .build();

        assertNotNull(osd.battery());
        assertEquals(100, osd.battery().capacityPercent());
        assertEquals(1500, osd.battery().remainFlightTime());
    }
}
