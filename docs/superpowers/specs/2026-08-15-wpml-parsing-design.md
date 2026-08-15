# WPML 解析设计文档

## 1. 概述

### 设计目标

在 SDK 中提供 WPML 解析能力，支持导入 DJI Pilot 导出的 KMZ 文件，将 `template.kml`/`waylines.wpml` 反序列化为 POJO，供调用方读取字段值后用 Builder 重建修改。

### 功能范围

- 解析粒度：POJO 级别（反序列化为 `Kml<Folder>`/`Kml<ExecuteFolder>`）
- 解析范围：先支持 waypoint 模板（template.kml + waylines.wpml）
- 编辑策略：从 POJO 读取值 + 新 Builder 重建（不添加 toBuilder）
- 多态处理：自定义 `ActionDeserializer` 根据 `actionActuatorFunc` 选择子类

## 2. API 设计

### WpmlCodec 新增方法

```java
// 通用 XML 反序列化
public static <T> T fromXml(String xml, Class<T> type)

// KMZ 解包（返回原始 XML 字符串）
public static KmzContent fromKmz(byte[] kmz)

// 高层解析方法（返回 POJO）
public static Kml<Folder> parseTemplateKml(String xml)
public static Kml<ExecuteFolder> parseWaylinesWpml(String xml)
public static ParsedKmz parseKmz(byte[] kmz)
```

### 容器

```java
public record KmzContent(String templateKml, String waylinesWpml) {}
public record ParsedKmz(Kml<Folder> template, Kml<ExecuteFolder> waylines) {}
```

### 调用方工作流

```java
byte[] kmz = Files.readAllBytes(Path.of("pilot_exported.kmz"));
ParsedKmz parsed = WpmlCodec.parseKmz(kmz);
Kml<Folder> template = parsed.template();
Folder folder = template.document().folder();

// 读取字段值 + 用新 Builder 重建修改
String newKml = WaypointTemplate.builder()
    .autoFlightSpeed(10)
    .droneInfo(...)
    .addWaypoint(...)
    .toXml();
```

## 3. ActionDeserializer 实现细节

### 多态处理逻辑

`ActionActuatorFuncParam` 是 sealed interface（13 个子类），反序列化时需根据 `actionActuatorFunc` 值选择对应子类。

```java
public class ActionDeserializer extends JsonDeserializer<Action> {
    private static final Map<String, Class<? extends ActionActuatorFuncParam>> PARAM_MAP = Map.of(
        "takePhoto",          TakePhotoParam.class,
        "startRecord",        StartRecordParam.class,
        "stopRecord",         StopRecordParam.class,
        "focus",              FocusParam.class,
        "zoom",               ZoomParam.class,
        "customDirName",      CustomDirNameParam.class,
        "gimbalRotate",       GimbalRotateParam.class,
        "rotateYaw",          RotateYawParam.class,
        "hover",              HoverParam.class,
        "gimbalEvenlyRotate", GimbalEvenlyRotateParam.class,
        "orientedShoot",      OrientedShootParam.class,
        "panoShot",           PanoShotParam.class,
        "recordPointCloud",   RecordPointCloudParam.class
    );

    @Override
    public Action deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        Integer actionId = node.has("actionId") ? node.get("actionId").asInt() : null;
        String func = node.get("actionActuatorFunc").asText();
        JsonNode paramNode = node.get("actionActuatorFuncParam");
        ActionActuatorFuncParam param = null;
        if (paramNode != null && !paramNode.isNull()) {
            Class<? extends ActionActuatorFuncParam> paramClass = PARAM_MAP.get(func);
            if (paramClass != null) {
                param = p.getCodec().treeToValue(paramNode, paramClass);
            }
        }
        return new Action(actionId, func, param);
    }
}
```

### Action record 修改

添加 `@JsonDeserialize(using = ActionDeserializer.class)` 注解。序列化行为不变（Jackson 序列化时忽略 `@JsonDeserialize`）。

## 4. WpmlCodec 反序列化实现

### fromXml

```java
public static <T> T fromXml(String xml, Class<T> type) {
    try {
        return MAPPER.readValue(xml, type);
    } catch (Exception e) {
        throw new IllegalStateException("XML 反序列化失败: " + e.getMessage(), e);
    }
}
```

### fromKmz

逐条目扫描 ZIP，匹配 `wpmz/template.kml` 和 `wpmz/waylines.wpml`。

```java
public static KmzContent fromKmz(byte[] kmz) {
    String kml = null, wpml = null;
    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(kmz))) {
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String content = new String(zis.readAllBytes(), StandardCharsets.UTF_8);
            if ("wpmz/template.kml".equals(entry.getName())) kml = content;
            if ("wpmz/waylines.wpml".equals(entry.getName())) wpml = content;
            zis.closeEntry();
        }
    } catch (IOException e) {
        throw new IllegalArgumentException("KMZ 解包失败: " + e.getMessage(), e);
    }
    if (kml == null || wpml == null) {
        throw new IllegalArgumentException("KMZ 缺少 wpmz/template.kml 或 wpmz/waylines.wpml");
    }
    return new KmzContent(kml, wpml);
}
```

### parseTemplateKml / parseWaylinesWpml / parseKmz

```java
public static Kml<Folder> parseTemplateKml(String xml) {
    return fromXml(xml, Kml.class);
}
public static Kml<ExecuteFolder> parseWaylinesWpml(String xml) {
    return fromXml(xml, Kml.class);
}
public static ParsedKmz parseKmz(byte[] kmz) {
    KmzContent content = fromKmz(kmz);
    return new ParsedKmz(parseTemplateKml(content.templateKml()),
                        parseWaylinesWpml(content.waylinesWpml()));
}
```

## 5. 异常处理

| 场景 | 异常类型 | 说明 |
|---|---|---|
| KMZ 不是有效 ZIP | `IllegalArgumentException` | 包装 IOException |
| KMZ 缺少 template.kml/waylines.wpml | `IllegalArgumentException` | 明确提示缺少文件 |
| XML 反序列化失败 | `IllegalStateException` | 包装 Jackson 异常 |

## 6. 编辑策略

record 不可变，**不添加 toBuilder()**。调用方从 POJO 读取值，用新 Builder 重建。

## 7. 风险点

1. **record `-parameters` 编译参数**：如未配置，反序列化可能失败。实现时先检查 pom.xml，如缺则添加
2. **ActionDeserializer 在 XML 中的行为**：`readTree` 方式在 XML 中是否兼容。如不兼容，改用 XmlTokenizer 方式

## 8. TDD 测试策略

测试类：`WpmlParserTest`

| 用例 | 验证点 |
|---|---|
| KMZ 解包 | fromKmz() 返回 KmzContent，XML 字符串正确 |
| KMZ 缺少文件 | fromKmz() 对缺少文件的 KMZ 抛 IllegalArgumentException |
| template.kml 解析 | parseTemplateKml() 返回 Kml<Folder>，字段值正确 |
| waylines.wpml 解析 | parseWaylinesWpml() 返回 Kml<ExecuteFolder>，字段值正确 |
| 多态 Action（takePhoto） | actionActuatorFuncParam 实例为 TakePhotoParam |
| 多态 Action（gimbalRotate） | actionActuatorFuncParam 实例为 GimbalRotateParam |
| parseKmz 端到端 | 解析完整 KMZ，template + waylines POJO 字段正确 |
| 往返一致性 | toXml() → fromXml() → toXml()，输出一致 |
| toKmz → parseKmz 往返 | toKmz() 生成的 KMZ 被 parseKmz() 正确解析 |

## 9. 实现顺序（TDD）

1. 先写 `WpmlParserTest`（全部测试）
2. 创建 `KmzContent` / `ParsedKmz` 容器
3. 创建 `ActionDeserializer`
4. 在 `Action` record 上添加 `@JsonDeserialize`
5. 在 `WpmlCodec` 中添加 `fromXml` / `fromKmz` / `parseTemplateKml` / `parseWaylinesWpml` / `parseKmz`
6. 逐个测试通过
7. 回归现有 77 个 wayline 测试
