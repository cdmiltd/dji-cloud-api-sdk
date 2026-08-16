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

package ltd.cdmi.dji.cloudapi.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注协议元素对应的 DJI 官方文档 URL。
 *
 * <p>用于协议溯源，让 SDK 使用者能快速查阅官方文档原文。可标注在类型、字段、
 * 方法及枚举常量上，配合 {@link Verified} / {@link Inferred} 表明该协议元素的核实状态。
 *
 * <p>注：枚举常量在 Java 中隐式为字段，{@link ElementType#FIELD} 已覆盖枚举常量声明，
 * 无需单独的 ENUM_CONSTANT 目标（标准 JDK 21 的 {@link ElementType} 不含该常量）。
 *
 * @see Verified
 * @see Inferred
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface DocUrl {

    /** DJI 官方文档 URL */
    String value();
}
