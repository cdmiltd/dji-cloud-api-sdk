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

package ltd.cdmi.dji.cloudapi.sdk.examples;

import java.lang.reflect.Field;

import ltd.cdmi.dji.cloudapi.sdk.annotation.Inferred;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.command.service.flight.TakeoffToPointRequest;
import ltd.cdmi.dji.cloudapi.sdk.protocol.error.DjiErrorCode;
import ltd.cdmi.dji.cloudapi.sdk.protocol.topic.TopicResolver;

/**
 * 示例 8：诊断工具。
 *
 * <p>演示 DjiErrorCode 错误码查表、TopicResolver topic 路由解析、
 * 以及 @Verified / @Inferred 协议注解反射扫描。
 *
 * <p>关键点：
 * <ul>
 *   <li>未知错误码 describe 返回 Optional.empty，不抛异常</li>
 *   <li>TopicResolver 与 TopicBuilder 互为逆操作：从 topic 解析 SN + 通道 + 方向</li>
 *   <li>@Verified 记录核实依据，@Inferred 标记待真机验证的推断项（字段级扫描）</li>
 * </ul>
 */
public class DiagnosticsExample {

    public static void main(String[] args) {
        // 1. 错误码查表（未知码返回 Optional.empty）
        System.out.println("===== 错误码查表 =====");
        DjiErrorCode.describe(314001).ifPresent(info ->
                System.out.println("314001 → " + info.description()));
        DjiErrorCode.describe(312014).ifPresent(info ->
                System.out.println("312014 → " + info.description()));
        DjiErrorCode.describe(999999).ifPresentOrElse(
                info -> System.out.println("999999 → " + info.description()),
                () -> System.out.println("999999 → 未知错误码（Optional.empty）"));

        // 2. Topic 路由解析
        System.out.println("\n===== Topic 路由解析 =====");
        String topic = "thing/product/7UUXN1Q00A008W/services";
        TopicResolver.TopicInfo info = TopicResolver.resolve(topic, "fly_to_point");
        System.out.println("topic = " + topic);
        System.out.println("  channel = " + info.channel());
        System.out.println("  deviceSn = " + info.deviceSn());
        System.out.println("  method = " + info.method());
        System.out.println("  direction = " + info.direction());

        // 3. 协议注解扫描：类级 @Verified + 字段级 @Inferred（遍历全部字段，无硬编码字段名）
        System.out.println("\n===== 协议注解扫描 =====");
        Verified verified = TakeoffToPointRequest.class.getAnnotation(Verified.class);
        if (verified != null) {
            System.out.println("TakeoffToPointRequest 类级 @Verified:");
            System.out.println("  basis = " + verified.basis());
        }
        for (Field field : TakeoffToPointRequest.class.getDeclaredFields()) {
            Inferred inf = field.getAnnotation(Inferred.class);
            if (inf != null) {
                System.out.println("TakeoffToPointRequest." + field.getName() + " @Inferred:");
                System.out.println("  reason = " + inf.reason());
                System.out.println("  verifyPoint = " + inf.verifyPoint());
            }
        }
    }
}
