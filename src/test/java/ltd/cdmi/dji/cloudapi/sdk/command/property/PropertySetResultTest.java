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

package ltd.cdmi.dji.cloudapi.sdk.command.property;

import ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证 {@link PropertySetResult} 的 Jackson 反序列化、序列化与往返闭环。
 *
 * <p><b>核心证明</b>：property/set_reply 中单个属性结果 {@code {"code":0}}
 * 能反序列化为 record，code=0 表示成功、非 0 表示错误码。
 */
class PropertySetResultTest {

    @Test
    @DisplayName("反序列化：{\"code\":0} → code()==0（成功）")
    void testDeserializeSuccess() {
        PropertySetResult result = MessageCodec.fromJson("{\"code\":0}", PropertySetResult.class);
        assertEquals(0, result.code());
    }

    @Test
    @DisplayName("反序列化：{\"code\":514001} → code()==514001（错误码）")
    void testDeserializeErrorCode() {
        PropertySetResult result = MessageCodec.fromJson("{\"code\":514001}", PropertySetResult.class);
        assertEquals(514001, result.code());
    }

    @Test
    @DisplayName("序列化：PropertySetResult(0) → JSON 含 \"code\":0")
    void testSerialize() {
        PropertySetResult result = new PropertySetResult(0);
        String json = MessageCodec.toJson(result);
        assertTrue(json.contains("\"code\":0"), "JSON 应含 \"code\":0，实际: " + json);
    }

    @Test
    @DisplayName("往返闭环：序列化→反序列化保持 code 不变")
    void testRoundTrip() {
        PropertySetResult original = new PropertySetResult(514001);
        String json = MessageCodec.toJson(original);
        PropertySetResult back = MessageCodec.fromJson(json, PropertySetResult.class);
        assertEquals(original.code(), back.code());
    }
}
