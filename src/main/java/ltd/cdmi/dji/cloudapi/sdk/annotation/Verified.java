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
 * 标注协议元素已通过 DJI 官方文档核实。
 *
 * <p>模拟器和 hivemind 均可安全使用。建议配合 {@link DocUrl} 标注核实依据的文档链接，
 * 通过 {@link #basis()} 进一步说明核实依据。
 *
 * <p>注：枚举常量在 Java 中隐式为字段，{@link ElementType#FIELD} 已覆盖枚举常量声明，
 * 无需单独的 ENUM_CONSTANT 目标（标准 JDK 21 的 {@link ElementType} 不含该常量）。
 *
 * @see DocUrl
 * @see Inferred
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface Verified {

    /** 核实依据说明 */
    String basis() default "";
}
