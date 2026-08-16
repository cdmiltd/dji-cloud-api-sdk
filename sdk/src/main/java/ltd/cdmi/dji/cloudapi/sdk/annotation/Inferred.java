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
 * 标注协议元素为推断定义（非 DJI 官方文档明确），待真机验证。
 *
 * <p>模拟器可使用（测试足够），hivemind 需真机验证后才能使用。
 * 对应 AGENTS.md 第 14 条 M-2 诊断日志的编译时体现——凡是未得到官方文档明确、
 * 即使选择了合理推断方案的协议元素，都必须用本注解标记，并附 {@link #reason()} 与
 * {@link #verifyPoint()}，让使用者在真机验证时能注意到这些假设。
 *
 * <p>注：枚举常量在 Java 中隐式为字段，{@link ElementType#FIELD} 已覆盖枚举常量声明，
 * 无需单独的 ENUM_CONSTANT 目标（标准 JDK 21 的 {@link ElementType} 不含该常量）。
 *
 * @see Verified
 * @see DocUrl
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface Inferred {

    /** 推断理由 */
    String reason();

    /** 待验证点 */
    String verifyPoint() default "";
}
