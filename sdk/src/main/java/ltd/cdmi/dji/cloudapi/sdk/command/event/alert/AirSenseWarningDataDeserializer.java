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

package ltd.cdmi.dji.cloudapi.sdk.command.event.alert;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * AirSenseWarningData 的自定义反序列化器。
 *
 * <p> airsense_warning 事件的 data 直接是 JSON 数组（非对象包裹），
 * {@code data: [{...}, {...}]}。Jackson 2.17 + record 的默认 BeanDeserializer
 * 无法将 bare-array 反序列化为 record（需对象 {@code {"alerts": [...]}}），
 * {@code @JsonCreator(DELEGATING)} 静态工厂也不生效。
 *
 * <p>本反序列化器将整个 JSON 数组转为 {@code List<Alert>} 再包裹为 record，
 * 调用方 {@code objectMapper.treeToValue(data, AirSenseWarningData.class)} 即可，
 * 与其他事件 POJO 用法完全一致，无需感知 bare-array 结构。
 *
 * <p>SNAKE_CASE 命名策略由调用方的 ObjectMapper 配置决定，本反序列化器通过
 * {@code p.getCodec()} 获取同一 ObjectMapper，确保字段映射一致。
 */
public class AirSenseWarningDataDeserializer extends StdDeserializer<AirSenseWarningData> {

    @SuppressWarnings("unchecked")
    public AirSenseWarningDataDeserializer() {
        super((Class<? super AirSenseWarningData>) (Class<?>) AirSenseWarningData.class);
    }

    @Override
    public AirSenseWarningData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);
        List<AirSenseWarningData.Alert> alerts = mapper.convertValue(node,
                new TypeReference<List<AirSenseWarningData.Alert>>() {});
        return new AirSenseWarningData(alerts);
    }
}
