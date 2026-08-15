# DJI Cloud API SDK — TDD 测试用例文档

- **关联设计**：[2026-08-14-tdd-test-suite-design.md](./superpowers/specs/2026-08-14-tdd-test-suite-design.md)
- **状态**：第一批（spec 标签包）详细版
- **图例**：`green` = 期望通过；`@Disabled 待修复：<原因>` = 期望失败登记
- **基准**：`doc:<URL>` = DJI 官方文档；`char` = 特征化（当前实现行为）；`meta` = 注解元数据
- **断言库**：AssertJ `assertThat(...)`；参数化用 `@ParameterizedTest + @MethodSource`

---

## 总则

1. spec 包测试类标 `@Tag("spec")`；characterization 包标 `@Tag("characterization")`。
2. 枚举值映射用 `@ParameterizedTest + @MethodSource`，`*Provider` 提供 `Arguments.of(枚举, code, desc)`。
3. 每个枚举必测：`fromXxx` 已知值、未知值（抛 `IllegalArgumentException` 或返回 `Optional.empty()`）、`null`、code 唯一性、枚举数量。
4. JSON 往返用 `MessageCodec`（snake_case 不匹配项标红）。
5. `@Inferred` 项：验证注解存在 + `reason()` 非空 + `verifyPoint()`，不做协议规范断言。

---

# 第一批：spec 标签包

## 1. annotation 包

### 1.1 DocUrlTest (`@Tag("spec")`)

#### shouldHaveRuntimeRetention_whenInspected
- 输入：`DocUrl.class.getAnnotation(Retention.class).value()`
- 预期：`RetentionPolicy.RUNTIME`
- 断言：`assertThat(DocUrl.class.getAnnotation(Retention.class).value()).isEqualTo(RetentionPolicy.RUNTIME)`
- 基准：meta | spec | green

#### shouldTargetTypeFieldMethod_whenInspected
- 输入：`DocUrl.class.getAnnotation(Target.class).value()`
- 预期：含 `ElementType.TYPE, FIELD, METHOD`
- 断言：`assertThat(DocUrl.class.getAnnotation(Target.class).value()).contains(TYPE, FIELD, METHOD)`
- 基准：meta | spec | green

#### shouldHaveValueAttribute_whenDeclared
- 输入：`DocUrl.class.getDeclaredMethod("value")`
- 预期：存在，返回类型 `String.class`
- 断言：`assertThat(DocUrl.class.getDeclaredMethod("value").getReturnType()).isEqualTo(String.class)`
- 基准：meta | spec | green

#### shouldReturnValue_whenReflectiveRead
- 输入：`TopicChannel.class.getAnnotation(DocUrl.class).value()`
- 预期：`"https://developer.dji.com/doc/cloud-api-tutorial/cn/overview/connection.html"`
- 断言：`assertThat(TopicChannel.class.getAnnotation(DocUrl.class).value()).endsWith("connection.html")`
- 基准：meta | spec | green

### 1.2 VerifiedTest (`@Tag("spec")`)

#### shouldHaveRuntimeRetention_whenInspected
- 输入：`Verified.class` 的 `@Retention`
- 预期：`RUNTIME`
- 断言：`assertThat(Verified.class.getAnnotation(Retention.class).value()).isEqualTo(RetentionPolicy.RUNTIME)`
- 基准：meta | spec | green

#### shouldTargetTypeFieldMethod_whenInspected
- 输入：`Verified.class` 的 `@Target`
- 预期：含 `TYPE, FIELD, METHOD`
- 断言：`assertThat(Verified.class.getAnnotation(Target.class).value()).contains(TYPE, FIELD, METHOD)`
- 基准：meta | spec | green

#### shouldHaveBasisAttributeWithEmptyDefault
- 输入：反射 `basis()` 的 `defaultValue`
- 预期：`""`
- 断言：`assertThat(Verified.class.getMethod("basis").getDefaultValue()).isEqualTo("")`
- 基准：meta | spec | green

#### shouldReturnBasis_whenReflectiveRead
- 输入：`Gear.class.getAnnotation(Verified.class).basis()`
- 预期：含 `"gear 枚举定义"`
- 断言：`assertThat(Gear.class.getAnnotation(Verified.class).basis()).contains("gear")`
- 基准：meta | spec | green

#### shouldAllProtocolClassesBeVerifiedOrInferred
- 输入：反射扫描 `ltd.cdmi.dji.cloudapi.sdk` 下所有枚举/record/final 工具类（排除 `annotation` 包自身）
- 预期：每个类有 `@Verified` 或 `@Inferred`
- 断言：`assertThat(classes).allMatch(c -> c.isAnnotationPresent(Verified.class) || c.isAnnotationPresent(Inferred.class))`
- 基准：char | spec | green

### 1.3 InferredTest (`@Tag("spec")`)

#### shouldHaveRuntimeRetention_whenInspected
- 断言：`assertThat(Inferred.class.getAnnotation(Retention.class).value()).isEqualTo(RetentionPolicy.RUNTIME)`
- 基准：meta | spec | green

#### shouldTargetTypeFieldMethod_whenInspected
- 断言：`assertThat(Inferred.class.getAnnotation(Target.class).value()).contains(TYPE, FIELD, METHOD)`
- 基准：meta | spec | green

#### shouldHaveReasonAttributeRequired
- 输入：反射 `reason()` 的 `defaultValue`
- 预期：无默认值（`AnnotationTypeMismatchException` 或默认为 `null`，表明必填）
- 断言：`assertThat(Inferred.class.getMethod("reason").getDefaultValue()).isNull()`
- 基准：meta | spec | green

#### shouldHaveVerifyPointAttributeWithEmptyDefault
- 输入：反射 `verifyPoint()` 的 `defaultValue`
- 预期：`""`
- 断言：`assertThat(Inferred.class.getMethod("verifyPoint").getDefaultValue()).isEqualTo("")`
- 基准：meta | spec | green

#### shouldMarkInferred_whenDrcHeartBeat
- 输入：`DrcMethod.HEART_BEAT` 字段（`DrcMethod.class.getField("HEART_BEAT")`）的 `@Inferred`
- 预期：注解存在，`reason()` 非空，`verifyPoint()` 非空
- 断言：
  ```java
  Inferred inf = DrcMethod.class.getField("HEART_BEAT").getAnnotation(Inferred.class);
  assertThat(inf).isNotNull();
  assertThat(inf.reason()).isNotBlank();
  assertThat(inf.verifyPoint()).isNotBlank();
  ```
- 基准：meta | spec | green

#### shouldMarkInferred_whenServiceCloudControlAuth
- 输入：`ServiceMethod.CLOUD_CONTROL_AUTH_REQUEST` 字段的 `@Inferred`
- 预期：存在，`reason()` 非空
- 断言：
  ```java
  Inferred inf = ServiceMethod.class.getField("CLOUD_CONTROL_AUTH_REQUEST").getAnnotation(Inferred.class);
  assertThat(inf).isNotNull();
  assertThat(inf.reason()).isNotBlank();
  ```
- 基准：meta | spec | green

#### shouldMarkInferred_whenPilotUpdateTopo
- 输入：`PilotRegistrationFlow.class.getField("UPDATE_TOPO")` 的 `@Inferred`
- 预期：存在，`reason()` 非空
- 断言：
  ```java
  Inferred inf = PilotRegistrationFlow.class.getField("UPDATE_TOPO").getAnnotation(Inferred.class);
  assertThat(inf).isNotNull();
  assertThat(inf.reason()).isNotBlank();
  ```
- 基准：meta | spec | green

---

## 2. codec 包

### 2.1 MessageCodecTest (`@Tag("spec")`)

#### shouldSerializeToJson_whenObjectGiven
- 输入：`MessageCodec.toJson(Map.of("method", "config"))`
- 预期：JSON 含 `"method":"config"`
- 断言：`assertThat(MessageCodec.toJson(Map.of("method", "config"))).contains("\"method\":\"config\"")`
- 基准：char | spec | green

#### shouldSerializeRecord_whenEnvelopeGiven
- 输入：`new RequestEnvelope("t1", "b1", 1700000000000L, "config", null)`
- 预期：JSON 含 tid/bid/timestamp/method
- 断言：
  ```java
  String json = MessageCodec.toJson(new RequestEnvelope("t1", "b1", 1700000000000L, "config", null));
  assertThat(json).contains("\"tid\":\"t1\"", "\"bid\":\"b1\"", "\"method\":\"config\"", "\"timestamp\":1700000000000");
  ```
- 基准：doc:connection.html | spec | green

#### shouldDeserializeFromJson_whenClassGiven
- 输入：`"{\"tid\":\"t1\",\"bid\":\"b1\",\"timestamp\":1700000000000,\"method\":\"config\",\"data\":null}"` → `RequestEnvelope.class`
- 预期：`tid="t1"`, `method="config"`, `timestamp=1700000000000L`（信封字段为单词，无 snake_case 问题）
- 断言：
  ```java
  RequestEnvelope env = MessageCodec.fromJson(json, RequestEnvelope.class);
  assertThat(env.tid()).isEqualTo("t1");
  assertThat(env.method()).isEqualTo("config");
  assertThat(env.timestamp()).isEqualTo(1700000000000L);
  ```
- 基准：doc:connection.html | spec | green

#### shouldDeserializeFromJson_whenTypeReferenceGiven
- 输入：`"[{\"tid\":\"t1\",\"bid\":\"b1\",\"timestamp\":1,\"method\":\"m\",\"data\":null}]"` → `new TypeReference<List<RequestEnvelope>>(){}`
- 预期：返回长度 1 的 List
- 断言：
  ```java
  List<RequestEnvelope> list = MessageCodec.fromJson(json, new TypeReference<List<RequestEnvelope>>() {});
  assertThat(list).hasSize(1);
  assertThat(list.get(0).tid()).isEqualTo("t1");
  ```
- 基准：char | spec | green

#### shouldThrowIllegalState_whenInvalidJsonDeserialize
- 输入：`"{"` → `RequestEnvelope.class`
- 预期：抛 `IllegalStateException`，message 含 `"反序列化失败"`
- 断言：
  ```java
  assertThatThrownBy(() -> MessageCodec.fromJson("{", RequestEnvelope.class))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("反序列化失败");
  ```
- 基准：char | spec | green

#### shouldThrowIllegalState_whenNonSerializable
- 输入：传入不可序列化对象（如含循环引用的对象）→ `toJson`
- 预期：抛 `IllegalStateException`，message 含 `"序列化失败"`
- 断言：`assertThatThrownBy(() -> MessageCodec.toJson(cyclicRef)).isInstanceOf(IllegalStateException.class).hasMessageContaining("序列化失败")`
- 基准：char | spec | green

#### shouldExtractMethod_whenPresent
- 输入：`"{\"method\":\"update_topo\"}"`
- 预期：`"update_topo"`
- 断言：`assertThat(MessageCodec.extractMethod("{\"method\":\"update_topo\"}")).isEqualTo("update_topo")`
- 基准：doc:connection.html | spec | green

#### shouldReturnNull_whenMethodAbsent
- 输入：`"{\"tid\":\"x\"}"`
- 预期：`null`
- 断言：`assertThat(MessageCodec.extractMethod("{\"tid\":\"x\"}")).isNull()`
- 基准：char | spec | green

#### shouldReturnNull_whenMethodJsonInvalid
- 输入：`"not json"`
- 预期：`null`
- 断言：`assertThat(MessageCodec.extractMethod("not json")).isNull()`
- 基准：char | spec | green

#### shouldExtractTid_whenPresent
- 输入：`"{\"tid\":\"uuid-1\"}"`
- 预期：`"uuid-1"`
- 断言：`assertThat(MessageCodec.extractTid("{\"tid\":\"uuid-1\"}")).isEqualTo("uuid-1")`
- 基准：doc:connection.html | spec | green

#### shouldExtractBid_whenPresent
- 输入：`"{\"bid\":\"uuid-2\"}"`
- 预期：`"uuid-2"`
- 断言：`assertThat(MessageCodec.extractBid("{\"bid\":\"uuid-2\"}")).isEqualTo("uuid-2")`
- 基准：doc:connection.html | spec | green

#### shouldExtractResult_whenDataResultPresent
- 输入：`"{\"data\":{\"result\":0}}"`
- 预期：`0`
- 断言：`assertThat(MessageCodec.extractResult("{\"data\":{\"result\":0}}")).isEqualTo(0)`
- 基准：doc:connection.html | spec | green

#### shouldExtractResult_whenNonZero
- 输入：`"{\"data\":{\"result\":210229}}"`
- 预期：`210229`
- 断言：`assertThat(MessageCodec.extractResult("{\"data\":{\"result\":210229}}")).isEqualTo(210229)`
- 基准：doc:error.html | spec | green

#### shouldReturnMinusOne_whenResultAbsent
- 输入：`"{\"data\":{}}"`
- 预期：`-1`（哨兵值）
- 断言：`assertThat(MessageCodec.extractResult("{\"data\":{}}")).isEqualTo(-1)`
- 基准：char + codec/package-info | spec | green

#### shouldReturnMinusOne_whenDataAbsent
- 输入：`"{\"tid\":\"x\"}"`
- 预期：`-1`
- 断言：`assertThat(MessageCodec.extractResult("{\"tid\":\"x\"}")).isEqualTo(-1)`
- 基准：char | spec | green

#### shouldReturnMinusOne_whenJsonInvalid
- 输入：`"bad"`
- 预期：`-1`
- 断言：`assertThat(MessageCodec.extractResult("bad")).isEqualTo(-1)`
- 基准：char | spec | green

#### shouldExtractData_whenPresent
- 输入：`"{\"data\":{\"k\":\"v\"}}"`
- 预期：非 null，`Map` 含 `k=v`
- 断言：
  ```java
  Object data = MessageCodec.extractData("{\"data\":{\"k\":\"v\"}}");
  assertThat(data).isInstanceOf(Map.class);
  assertThat(((Map<?,?>) data)).containsEntry("k", "v");
  ```
- 基准：doc:connection.html | spec | green

#### shouldReturnNull_whenDataAbsent
- 输入：`"{\"tid\":\"x\"}"`
- 预期：`null`
- 断言：`assertThat(MessageCodec.extractData("{\"tid\":\"x\"}")).isNull()`
- 基准：char | spec | green

#### shouldReturnNull_whenDataJsonInvalid
- 输入：`"bad"`
- 预期：`null`
- 断言：`assertThat(MessageCodec.extractData("bad")).isNull()`
- 基准：char | spec | green

#### shouldVerifyExtractResultSentinelDocumented
- 输入：读 `codec/package-info.java`（通过反射 `Package` 或读源文件）
- 预期：文档含 `"-1"` 哨兵说明
- 断言：`assertThat(packageDocContent).contains("-1")`（读取 package-info 资源）
- 基准：char | spec | green

#### shouldDeserializeSnakeCaseOsd_whenToCamelCaseRecord
- 输入：
  ```java
  String json = "{\"mode_code\":0,\"latitude\":22.0,\"gear\":1}";
  DroneOsd osd = MessageCodec.fromJson(json, DroneOsd.class);
  ```
- 预期：`osd.modeCode()` = 0，`osd.latitude()` = 22.0，`osd.gear()` = 1（snake_case JSON → camelCase record，命名策略 SNAKE_CASE 双向匹配）
- 断言：
  ```java
  assertThat(osd.modeCode()).isEqualTo(0);
  assertThat(osd.latitude()).isEqualTo(22.0);
  assertThat(osd.gear()).isEqualTo(1);
  ```
- 基准：doc:aircraft/properties.html | spec | green

#### shouldRoundTripOsd_whenSerializeThenDeserialize
- 输入：`new DroneOsd(0, 22.0, 113.0, null,...)` → `toJson` → `fromJson`
- 预期：往返后字段一致（序列化输出 snake_case `mode_code`，反序列化按 snake_case 读回，往返一致）
- 断言：`assertThat(roundtrip.modeCode()).isEqualTo(original.modeCode())`
- 基准：char | spec | green

#### shouldSerializeOsdToSnakeCase_whenPropertyNamingStrategyConfigured
- 输入：`new DroneOsd(0, 22.0, 113.0, null,...)` → `toJson`
- 预期：JSON 含 `"mode_code"`（snake_case 输出，符合 DJI 协议）
- 断言：`assertThat(json).contains("\"mode_code\"")`
- 基准：doc:aircraft/properties.html | spec | green

### 2.2 TopicResolverTest (`@Tag("spec")`)

#### shouldResolveOsChannel_whenTopicGiven
- 输入：`TopicResolver.resolve("thing/product/1UUXN1Q00A001W/osd", "x")`
- 预期：channel=OSD, deviceSn=`1UUXN1Q00A001W`, method=`x`, direction=UP
- 断言：
  ```java
  TopicInfo r = TopicResolver.resolve("thing/product/1UUXN1Q00A001W/osd", "x");
  assertThat(r.channel()).isEqualTo(TopicChannel.OSD);
  assertThat(r.deviceSn()).isEqualTo("1UUXN1Q00A001W");
  assertThat(r.method()).isEqualTo("x");
  assertThat(r.direction()).isEqualTo(TopicDirection.UP);
  ```
- 基准：doc:connection.html | spec | green

#### shouldResolveAll14Channels_whenParameterized
- 输入：`@MethodSource("channelTopicProvider")` 提供 14 组 `(topicSuffix, expectedChannel, expectedDirection)`
  例：`("osd", OSD, UP)`, `("services", SERVICES, DOWN)`, `("drc/up", DRC_UP, UP)`, `("property/set", PROPERTY_SET, DOWN)` 等
- 预期：`resolve("thing/product/SN/"+suffix, "m").channel()` = expectedChannel，direction 匹配
- 断言：
  ```java
  assertThat(r.channel()).isEqualTo(expectedChannel);
  assertThat(r.direction()).isEqualTo(expectedDirection);
  ```
- 基准：doc:connection.html | spec | green

#### shouldExtractDeviceSn_whenThingProductTopic
- 输入：`"thing/product/1UUXN1Q00A001W/osd"`
- 预期：sn=`1UUXN1Q00A001W`
- 断言：`assertThat(resolve(...).deviceSn()).isEqualTo("1UUXN1Q00A001W")`
- 基准：doc:connection.html | spec | green

#### shouldExtractDeviceSn_whenSysProductTopic
- 输入：`"sys/product/SN/status"`
- 预期：sn=`SN`
- 断言：`assertThat(resolve("sys/product/SN/status","m").deviceSn()).isEqualTo("SN")`
- 基准：doc:connection.html | spec | green

#### shouldReturnNullSn_whenInvalidTopic
- 输入：`"foo/bar/x/y"`
- 预期：sn=null, channel=null
- 断言：
  ```java
  TopicInfo r = TopicResolver.resolve("foo/bar/x/y", "m");
  assertThat(r.deviceSn()).isNull();
  assertThat(r.channel()).isNull();
  ```
- 基准：char | spec | green

#### shouldReturnNullSn_whenTopicNull
- 输入：`resolve(null, "m")`
- 预期：sn=null, channel=null, direction=null
- 断言：`assertThat(r.deviceSn()).isNull(); assertThat(r.channel()).isNull();`
- 基准：char | spec | green

#### shouldResolveMultiSegmentSuffix_whenDrcUp
- 输入：`"thing/product/SN/drc/up"`
- 预期：channel=DRC_UP
- 断言：`assertThat(resolve("thing/product/SN/drc/up","m").channel()).isEqualTo(TopicChannel.DRC_UP)`
- 基准：doc:connection.html | spec | green

#### shouldResolveMultiSegmentSuffix_whenPropertySet
- 输入：`"thing/product/SN/property/set"`
- 预期：channel=PROPERTY_SET
- 断言：`assertThat(resolve("thing/product/SN/property/set","m").channel()).isEqualTo(TopicChannel.PROPERTY_SET)`
- 基准：doc:connection.html | spec | green

#### shouldReturnNullChannel_whenUnknownSuffix
- 输入：`"thing/product/SN/unknown"`
- 预期：channel=null, direction=null
- 断言：`assertThat(resolve("thing/product/SN/unknown","m").channel()).isNull()`
- 基准：char | spec | green

#### shouldKeepMethodAsIs
- 输入：`resolve("thing/product/SN/osd", "flighttask_execute")`
- 预期：method=`flighttask_execute`
- 断言：`assertThat(r.method()).isEqualTo("flighttask_execute")`
- 基准：char | spec | green

#### shouldSetDirectionFromChannel_whenResolved
- 输入：OSD topic 与 SERVICES topic
- 预期：OSD→UP, SERVICES→DOWN
- 断言：`assertThat(resolve(osdTopic,"m").direction()).isEqualTo(UP); assertThat(resolve(servicesTopic,"m").direction()).isEqualTo(DOWN)`
- 基准：doc:connection.html | spec | green

#### shouldHandleShortTopic_whenLessThan4Segments
- 输入：`"thing/product/SN"`（3 段）
- 预期：channel=null（parts.length<4）
- 断言：`assertThat(resolve("thing/product/SN","m").channel()).isNull()`
- 基准：char | spec | green

---

## 3. protocol.topic 包

### 3.1 TopicDirectionTest (`@Tag("spec")`)

#### shouldHaveTwoDirections_whenValues
- 输入：`TopicDirection.values()`
- 预期：长度 2，含 `UP, DOWN`
- 断言：`assertThat(TopicDirection.values()).containsExactly(UP, DOWN)`
- 基准：doc:connection.html | spec | green

#### shouldReturnDescription_whenUp
- 输入：`UP.description()`
- 预期：含 `"上行"`
- 断言：`assertThat(UP.description()).contains("上行")`
- 基准：doc:connection.html | spec | green

#### shouldReturnDescription_whenDown
- 输入：`DOWN.description()`
- 预期：含 `"下行"`
- 断言：`assertThat(DOWN.description()).contains("下行")`
- 基准：doc:connection.html | spec | green

### 3.2 TopicChannelTest (`@Tag("spec")`)

#### shouldHave14Channels_whenValues
- 输入：`TopicChannel.values()`
- 预期：长度 14
- 断言：`assertThat(TopicChannel.values()).hasSize(14)`
- 基准：doc:connection.html | spec | green

#### shouldReturnSuffixAndDirection_whenParameterized
- 输入：`@MethodSource("channelProvider")` 提供 14 组 `(channel, suffix, direction)`：
  `(OSD,"osd",UP)`, `(STATE,"state",UP)`, `(SERVICES,"services",DOWN)`, `(SERVICES_REPLY,"services_reply",UP)`,
  `(EVENTS,"events",UP)`, `(EVENTS_REPLY,"events_reply",DOWN)`, `(REQUESTS,"requests",UP)`, `(REQUESTS_REPLY,"requests_reply",DOWN)`,
  `(STATUS,"status",UP)`, `(STATUS_REPLY,"status_reply",DOWN)`, `(DRC_UP,"drc/up",UP)`, `(DRC_DOWN,"drc/down",DOWN)`,
  `(PROPERTY_SET,"property/set",DOWN)`, `(PROPERTY_SET_REPLY,"property/set_reply",UP)`
- 预期：`channel.suffix()`=suffix，`channel.direction()`=direction
- 断言：
  ```java
  assertThat(channel.suffix()).isEqualTo(suffix);
  assertThat(channel.direction()).isEqualTo(direction);
  ```
- 基准：doc:connection.html | spec | green

#### shouldHaveUniqueSuffixes_whenAllChannels
- 输入：所有 `suffix()`
- 预期：互不重复
- 断言：
  ```java
  List<String> suffixes = stream(values()).map(TopicChannel::suffix).toList();
  assertThat(suffixes).doesNotHaveDuplicates();
  ```
- 基准：char | spec | green

#### shouldReturnNonBlankDescription_whenAnyChannel
- 输入：`@EnumSource(TopicChannel.class)` 遍历
- 预期：`description()` 非空
- 断言：`assertThat(channel.description()).isNotBlank()`
- 基准：char | spec | green

#### shouldReplyChannelHaveOppositeDirection_whenParameterized
- 输入：`(SERVICES, SERVICES_REPLY)`, `(EVENTS, EVENTS_REPLY)`, `(REQUESTS, REQUESTS_REPLY)`, `(STATUS, STATUS_REPLY)`, `(PROPERTY_SET, PROPERTY_SET_REPLY)`
- 预期：reply 通道方向与原通道相反
- 断言：`assertThat(reply.direction()).isNotEqualTo(original.direction())`
- 基准：doc:connection.html | spec | green

### 3.3 TopicTemplateTest (`@Tag("spec")`)

#### shouldReturnThingProductPrefix
- 输入：`THING_PRODUCT_PREFIX`
- 预期：`"thing/product/%s/"`
- 断言：`assertThat(TopicTemplate.THING_PRODUCT_PREFIX).isEqualTo("thing/product/%s/")`
- 基准：doc:connection.html | spec | green

#### shouldReturnSysProductPrefix
- 输入：`SYS_PRODUCT_PREFIX`
- 预期：`"sys/product/%s/"`
- 断言：`assertThat(TopicTemplate.SYS_PRODUCT_PREFIX).isEqualTo("sys/product/%s/")`
- 基准：doc:connection.html | spec | green

#### shouldBuildThingProduct_whenSuffixGiven
- 输入：`thingProduct("osd")`
- 预期：`"thing/product/%s/osd"`
- 断言：`assertThat(TopicTemplate.thingProduct("osd")).isEqualTo("thing/product/%s/osd")`
- 基准：doc:connection.html | spec | green

#### shouldBuildSysProduct_whenSuffixGiven
- 输入：`sysProduct("status")`
- 预期：`"sys/product/%s/status"`
- 断言：`assertThat(TopicTemplate.sysProduct("status")).isEqualTo("sys/product/%s/status")`
- 基准：doc:connection.html | spec | green

#### shouldOsdTemplateUseThingProduct
- 输入：`OSD` 常量
- 预期：`"thing/product/%s/osd"`
- 断言：`assertThat(TopicTemplate.OSD).isEqualTo("thing/product/%s/osd")`
- 基准：doc:connection.html | spec | green

#### shouldStateTemplateUseThingProduct
- 输入：`STATE` 常量
- 预期：`"thing/product/%s/state"`
- 断言：`assertThat(TopicTemplate.STATE).isEqualTo("thing/product/%s/state")`
- 基准：doc:connection.html | spec | green

#### shouldStatusTemplateUseSysProduct
- 输入：`STATUS` 常量
- 预期：`"sys/product/%s/status"`
- 断言：`assertThat(TopicTemplate.STATUS).isEqualTo("sys/product/%s/status")`
- 基准：doc:connection.html | spec | green

#### shouldStatusReplyTemplateUseSysProduct
- 输入：`STATUS_REPLY` 常量
- 预期：`"sys/product/%s/status_reply"`
- 断言：`assertThat(TopicTemplate.STATUS_REPLY).isEqualTo("sys/product/%s/status_reply")`
- 基准：doc:connection.html | spec | green

#### should12ThingChannelsUseThingProduct_whenParameterized
- 输入：`@MethodSource` 提供 12 个 thing 通道常量（OSD/STATE/SERVICES/SERVICES_REPLY/EVENTS/EVENTS_REPLY/REQUESTS/REQUESTS_REPLY/DRC_UP/DRC_DOWN/PROPERTY_SET/PROPERTY_SET_REPLY）
- 预期：均以 `"thing/product/%s/"` 开头
- 断言：`assertThat(constant).startsWith("thing/product/%s/")`
- 基准：doc:connection.html | spec | green

### 3.4 TopicBuilderTest (`@Tag("spec")`)

#### shouldBuildThingProduct_whenBuildSnChannel
- 输入：`TopicBuilder.build("1UUXN1Q00A001W", TopicChannel.OSD)`
- 预期：`"thing/product/1UUXN1Q00A001W/osd"`
- 断言：`assertThat(TopicBuilder.build("1UUXN1Q00A001W", OSD)).isEqualTo("thing/product/1UUXN1Q00A001W/osd")`
- 基准：char | spec | green

#### shouldBeDeprecated_whenBuildSnChannelOverload
- 输入：反射 `TopicBuilder.class.getMethod("build", String.class, TopicChannel.class)` 的 `@Deprecated`
- 预期：注解存在
- 断言：`assertThat(method.isAnnotationPresent(Deprecated.class)).isTrue()`
- 基准：char | spec | green

#### shouldBuildSysProduct_whenBuildWithSysPrefix
- 输入：`TopicBuilder.buildWithSysPrefix("SN", TopicChannel.STATUS)`
- 预期：`"sys/product/SN/status"`
- 断言：`assertThat(TopicBuilder.buildWithSysPrefix("SN", STATUS)).isEqualTo("sys/product/SN/status")`
- 基准：doc:connection.html | spec | green

#### shouldBuildSysProduct_whenUseSysPrefixTrue
- 输入：`TopicBuilder.build("SN", TopicChannel.STATUS, true)`
- 预期：`"sys/product/SN/status"`
- 断言：`assertThat(TopicBuilder.build("SN", STATUS, true)).isEqualTo("sys/product/SN/status")`
- 基准：doc:connection.html | spec | green

#### shouldBuildThingProduct_whenUseSysPrefixFalse
- 输入：`TopicBuilder.build("SN", TopicChannel.OSD, false)`
- 预期：`"thing/product/SN/osd"`
- 断言：`assertThat(TopicBuilder.build("SN", OSD, false)).isEqualTo("thing/product/SN/osd")`
- 基准：doc:connection.html | spec | green

#### shouldHandleMultiSegmentSuffix_whenDrcUp
- 输入：`TopicBuilder.build("SN", TopicChannel.DRC_UP)`
- 预期：`"thing/product/SN/drc/up"`
- 断言：`assertThat(TopicBuilder.build("SN", DRC_UP)).isEqualTo("thing/product/SN/drc/up")`
- 基准：char | spec | green

---

## 4. protocol.method 包

> 通用模式：每个 MethodXxx 测试类用 `@ParameterizedTest + @EnumSource(MethodXxx.class)` 遍历全部枚举值，
> 断言 `methodName()` 与 `description()` 非空；另用 `@MethodSource` 提供代表性精确值断言 methodName 字符串。

### 4.1 StatusMethodTest (`@Tag("spec")`)

#### shouldHave1Method_whenValues
- 断言：`assertThat(StatusMethod.values()).hasSize(1)`
- 基准：doc:dock3/topology.html | spec | green

#### shouldReturnMethodNameAndDescription_whenParameterized
- 输入：`@EnumSource(StatusMethod.class)`
- 预期：`methodName()` 与 `description()` 非空
- 断言：`assertThat(m.methodName()).isNotBlank(); assertThat(m.description()).isNotBlank()`
- 基准：doc:dock3/topology.html | spec | green

#### shouldReturnUpdateTopo_whenMethodName
- 输入：`UPDATE_TOPO.methodName()`
- 预期：`"update_topo"`
- 断言：`assertThat(UPDATE_TOPO.methodName()).isEqualTo("update_topo")`
- 基准：doc:dock3/topology.html | spec | green

#### shouldFind_whenKnownMethod
- 输入：`fromMethodName("update_topo")`
- 预期：`Optional.of(UPDATE_TOPO)`
- 断言：`assertThat(StatusMethod.fromMethodName("update_topo")).contains(UPDATE_TOPO)`
- 基准：char | spec | green

#### shouldReturnEmpty_whenUnknownMethod
- 输入：`fromMethodName("nope")`
- 预期：`Optional.empty()`
- 断言：`assertThat(StatusMethod.fromMethodName("nope")).isEmpty()`
- 基准：char | spec | green

#### shouldReturnEmpty_whenNull
- 输入：`fromMethodName(null)`
- 预期：`Optional.empty()`
- 断言：`assertThat(StatusMethod.fromMethodName(null)).isEmpty()`
- 基准：char | spec | green

#### shouldHaveUniqueMethodNames_whenAllValues
- 输入：所有 `methodName()`
- 预期：互不重复
- 断言：`assertThat(methodNames).doesNotHaveDuplicates()`
- 基准：char | spec | green

### 4.2 RequestsMethodTest (`@Tag("spec")`)

#### shouldHave7Methods_whenValues
- 断言：`assertThat(RequestsMethod.values()).hasSize(7)`
- 基准：doc:dock3/requests.html | spec | green

#### shouldReturnMethodNames_whenParameterized
- 输入：`@MethodSource` 提供精确映射：`CONFIG→"config"`, `AIRPORT_BIND_STATUS→"airport_bind_status"`, `AIRPORT_ORGANIZATION_GET→"airport_organization_get"`, `AIRPORT_ORGANIZATION_BIND→"airport_organization_bind"`
- 预期：`methodName()` 匹配
- 断言：`assertThat(m.methodName()).isEqualTo(expectedName)`
- 基准：doc:dock3/requests.html | spec | green

#### shouldReturnNonBlankDescription_whenEnumSource
- 输入：`@EnumSource(RequestsMethod.class)`
- 断言：`assertThat(m.description()).isNotBlank()`
- 基准：doc:dock3/requests.html | spec | green

#### shouldFind_whenKnownMethod
- 输入：`fromMethodName("config")`
- 预期：`Optional.of(CONFIG)`
- 断言：`assertThat(RequestsMethod.fromMethodName("config")).contains(CONFIG)`
- 基准：char | spec | green

#### shouldReturnEmpty_whenUnknown
- 断言：`assertThat(RequestsMethod.fromMethodName("x")).isEmpty()`
- 基准：char | spec | green

#### shouldReturnEmpty_whenNull
- 断言：`assertThat(RequestsMethod.fromMethodName(null)).isEmpty()`
- 基准：char | spec | green

#### shouldHaveUniqueMethodNames
- 断言：`assertThat(methodNames).doesNotHaveDuplicates()`
- 基准：char | spec | green

### 4.3 EventMethodTest (`@Tag("spec")`)

#### shouldHave20Methods_whenValues
- 断言：`assertThat(EventMethod.values()).hasSize(20)`
- 基准：doc:dock3/events.html | spec | green

#### shouldReturnNonBlankMethodNameAndDescription_whenEnumSource
- 输入：`@EnumSource(EventMethod.class)`
- 断言：`assertThat(m.methodName()).isNotBlank(); assertThat(m.description()).isNotBlank()`
- 基准：doc:dock3/events.html | spec | green

#### shouldReturnRepresentativeMethodNames_whenParameterized
- 输入：`@MethodSource` 提供精确映射：`FLIGHT_AREAS_DRONE_LOCATION→"flight_areas_drone_location"`, `FLIGHT_AREAS_SYNC_PROGRESS→"flight_areas_sync_progress"`, `HIGHEST_PRIORITY_UPLOAD_FLIGHTTASK_MEDIA→"highest_priority_upload_flighttask_media"`, `FILE_UPLOAD_CALLBACK→"file_upload_callback"`
- 预期：`methodName()` 匹配
- 断言：`assertThat(m.methodName()).isEqualTo(expectedName)`
- 基准：doc:dock3/events.html | spec | green

#### shouldFind_whenKnownMethod
- 输入：`fromMethodName("flight_areas_drone_location")`
- 预期：`Optional.of(FLIGHT_AREAS_DRONE_LOCATION)`
- 断言：`assertThat(EventMethod.fromMethodName("flight_areas_drone_location")).contains(FLIGHT_AREAS_DRONE_LOCATION)`
- 基准：char | spec | green

#### shouldReturnEmpty_whenUnknown
- 断言：`assertThat(EventMethod.fromMethodName("x")).isEmpty()`
- 基准：char | spec | green

#### shouldReturnEmpty_whenNull
- 断言：`assertThat(EventMethod.fromMethodName(null)).isEmpty()`
- 基准：char | spec | green

#### shouldHaveUniqueMethodNames
- 断言：`assertThat(methodNames).doesNotHaveDuplicates()`
- 基准：char | spec | green

### 4.4 DrcMethodTest (`@Tag("spec")`)

#### shouldHave19Methods_whenValues
- 断言：`assertThat(DrcMethod.values()).hasSize(19)`
- 基准：doc:dock3/remote-control.html | spec | green

#### shouldReturnNonBlankMethodNameAndDescription_whenEnumSource
- 输入：`@EnumSource(DrcMethod.class)`
- 断言：`assertThat(m.methodName()).isNotBlank(); assertThat(m.description()).isNotBlank()`
- 基准：doc:dock3/remote-control.html | spec | green

#### shouldFind_whenKnownMethod
- 输入：`fromMethodName("heart_beat")`
- 预期：`Optional.of(HEART_BEAT)`
- 断言：`assertThat(DrcMethod.fromMethodName("heart_beat")).contains(DrcMethod.HEART_BEAT)`
- 基准：char | spec | green

#### shouldReturnEmpty_whenUnknown
- 断言：`assertThat(DrcMethod.fromMethodName("x")).isEmpty()`
- 基准：char | spec | green

#### shouldReturnEmpty_whenNull
- 断言：`assertThat(DrcMethod.fromMethodName(null)).isEmpty()`
- 基准：char | spec | green

#### shouldHaveUniqueMethodNames
- 断言：`assertThat(methodNames).doesNotHaveDuplicates()`
- 基准：char | spec | green

#### shouldHeartBeatBeInferred
- 输入：`DrcMethod.class.getField("HEART_BEAT").getAnnotation(Inferred.class)`
- 预期：存在，`reason()` 非空，`verifyPoint()` 非空
- 断言：
  ```java
  Inferred inf = DrcMethod.class.getField("HEART_BEAT").getAnnotation(Inferred.class);
  assertThat(inf).isNotNull();
  assertThat(inf.reason()).isNotBlank();
  assertThat(inf.verifyPoint()).isNotBlank();
  ```
- 基准：meta | spec | green

### 4.5 ServiceMethodTest (`@Tag("spec")`)

#### shouldHave69Methods_whenValues
- 断言：`assertThat(ServiceMethod.values()).hasSize(69)`
- 基准：doc:dock3/services.html | spec | green

#### shouldReturnNonBlankMethodNameAndDescription_whenEnumSource
- 输入：`@EnumSource(ServiceMethod.class)`
- 断言：`assertThat(m.methodName()).isNotBlank(); assertThat(m.description()).isNotBlank()`
- 基准：doc:dock3/services.html | spec | green

#### shouldReturnRepresentativeMethodNames_whenParameterized
- 输入：`@MethodSource` 提供代表性值：`FLIGHTTASK_PREPARE→"flighttask_prepare"`, `FLIGHTTASK_EXECUTE→"flighttask_execute"`, `LIVE_START_PUSH→"live_start_push"`, `DRONE_OPEN→"drone_open"`, `RTK_CALIBRATION→"rtk_calibration"`
- 预期：`methodName()` 匹配
- 断言：`assertThat(m.methodName()).isEqualTo(expectedName)`
- 基准：doc:dock3/services.html | spec | green

#### shouldFind_whenKnownMethod
- 输入：`fromMethodName("flighttask_prepare")`
- 预期：`Optional.of(FLIGHTTASK_PREPARE)`
- 断言：`assertThat(ServiceMethod.fromMethodName("flighttask_prepare")).contains(ServiceMethod.FLIGHTTASK_PREPARE)`
- 基准：char | spec | green

#### shouldReturnEmpty_whenUnknown
- 断言：`assertThat(ServiceMethod.fromMethodName("x")).isEmpty()`
- 基准：char | spec | green

#### shouldReturnEmpty_whenNull
- 断言：`assertThat(ServiceMethod.fromMethodName(null)).isEmpty()`
- 基准：char | spec | green

#### shouldHaveUniqueMethodNames
- 断言：`assertThat(methodNames).doesNotHaveDuplicates()`
- 基准：char | spec | green

#### shouldClassLevelInferredMarkCloudControlAuthPending
- 输入：`ServiceMethod.class.getAnnotation(Inferred.class)`
- 预期：存在，`reason()` 含 `cloud_control_auth_request`，`verifyPoint()` 非空（类级 @Inferred 标注 `cloud_control_auth_request` 在 simulator catalog 未列出，待 DJI 文档确认）
- 断言：
  ```java
  Inferred inf = ServiceMethod.class.getAnnotation(Inferred.class);
  assertThat(inf).isNotNull();
  assertThat(inf.reason()).contains("cloud_control_auth_request");
  assertThat(inf.verifyPoint()).isNotBlank();
  ```
- 基准：meta | spec | green

---

### 4.6 http 包

> 通用模式：`HttpApiPath` 测试路径常量非空 + 前缀正确；`StsCredentials` 测试 record 组件与 @Inferred 注解。
> 注：http 包非 protocol.method 子包，归入 §4 仅为本次新增时的就近编排，后续可独立成章。

#### 4.6.1 HttpApiPathTest (`@Tag("spec")`)

#### shouldHave21Constants_whenDeclared
- 输入：反射 `HttpApiPath.class.getDeclaredFields()`
- 预期：21 个 `public static final String` 字段
- 断言：`assertThat(fields).filteredOn(f -> Modifier.isPublic(f.getModifiers()) && Modifier.isStatic(f.getModifiers())).hasSize(21)`
- 基准：doc:pilot-to-cloud/http | spec | green

#### shouldAllConstantsBeNonBlank_whenInspected
- 输入：反射读取所有常量值
- 断言：`assertThat(values).allMatch(v -> !((String) v).isBlank())`
- 基准：char | spec | green

#### shouldManageBasePathStartWithSlash_whenInspected
- 输入：`MANAGE_BASE_PATH`
- 预期：`"/manage/api/v1/workspaces"`
- 断言：`assertThat(MANAGE_BASE_PATH).isEqualTo("/manage/api/v1/workspaces")`
- 基准：doc:pilot-to-cloud/http/manage | spec | green

#### shouldDevicesTopologiesIncludeWorkspaceId_whenBuilt
- 输入：`DEVICES_TOPOLOGIES`
- 预期：包含 `"/{workspace_id}/devices/topologies"`
- 断言：`assertThat(DEVICES_TOPOLOGIES).endsWith("/{workspace_id}/devices/topologies")`
- 基准：doc:pilot-to-cloud/http/manage/device-topology | spec | green

#### shouldNotBeInstantiable_whenPrivateConstructor
- 输入：反射 `HttpApiPath.class.getDeclaredConstructor()` 调用 `setAccessible(true)` 后 `newInstance()`
- 预期：抛 `UnsupportedOperationException`（或按工具类约定 `IllegalAccessException`）
- 断言：`assertThatThrownBy(() -> ...).isInstanceOf(UnsupportedOperationException.class)`
- 基准：char | spec | green

#### 4.6.2 StsCredentialsTest (`@Tag("spec")`)

#### shouldHave6Components_whenRecord
- 输入：`StsCredentials.class.getRecordComponents()`
- 预期：组件名 `bucket, endpoint, region, provider, credentials, objectKeyPrefix`
- 断言：`assertThat(components).extracting(RecordComponent::getName).containsExactly("bucket", "endpoint", "region", "provider", "credentials", "objectKeyPrefix")`
- 基准：doc:pilot-to-cloud/http/storage/sts | spec | green

#### shouldBeAnnotatedWithInferred_whenClassLevel
- 输入：`StsCredentials.class.getAnnotation(Inferred.class)`
- 预期：存在，`reason()` 非空，`verifyPoint()` 含 `credentials`（子结构字段名待真机确认）
- 断言：
  ```java
  Inferred inf = StsCredentials.class.getAnnotation(Inferred.class);
  assertThat(inf).isNotNull();
  assertThat(inf.reason()).isNotBlank();
  assertThat(inf.verifyPoint()).contains("credentials");
  ```
- 基准：meta | spec | green

#### shouldBeAnnotatedWithDocUrl_whenClassLevel
- 输入：`StsCredentials.class.getAnnotation(DocUrl.class)`
- 预期：存在，`value()` 指向 generate-upload-credentials 文档
- 断言：`assertThat(StsCredentials.class.getAnnotation(DocUrl.class).value()).contains("generate-upload-credentials")`
- 基准：meta | spec | green

---

### 4.7 websocket 包

> 通用模式：`WsBizCode` 测试枚举数量与 fromMethodName 行为；`WsPushMessage` 测试 record 组件。
> 注：websocket 包非 protocol.method 子包，归入 §4 仅为本次新增时的就近编排，后续可独立成章。

#### 4.7.1 WsBizCodeTest (`@Tag("spec")`)

#### shouldHave8Codes_whenValues
- 断言：`assertThat(WsBizCode.values()).hasSize(8)`
- 基准：doc:pilot-to-cloud/websocket | spec | green

#### shouldReturnCodeAndDescription_whenParameterized
- 输入：`@EnumSource(WsBizCode.class)`
- 断言：`assertThat(v.code()).isNotBlank(); assertThat(v.description()).isNotBlank()`
- 基准：doc:pilot-to-cloud/websocket | spec | green

#### shouldReturnRepresentativeCodes_whenParameterized
- 输入：`@MethodSource` 提供精确映射：`MAP_ELEMENT_CREATE→"map_element_create"`, `DEVICE_OSD→"device_osd"`, `DEVICE_UPDATE_TOPO→"device_update_topo"`
- 预期：`code()` 匹配
- 断言：`assertThat(v.code()).isEqualTo(expectedCode)`
- 基准：doc:pilot-to-cloud/websocket | spec | green

#### shouldFind_whenKnownCode
- 输入：`fromMethodName("map_element_create")`
- 预期：`Optional.of(MAP_ELEMENT_CREATE)`
- 断言：`assertThat(WsBizCode.fromMethodName("map_element_create")).contains(WsBizCode.MAP_ELEMENT_CREATE)`
- 基准：char | spec | green

#### shouldReturnEmpty_whenUnknownCode
- 断言：`assertThat(WsBizCode.fromMethodName("x")).isEmpty()`
- 基准：char | spec | green

#### shouldReturnEmpty_whenNull
- 断言：`assertThat(WsBizCode.fromMethodName(null)).isEmpty()`
- 基准：char | spec | green

#### shouldHaveUniqueCodes_whenAllValues
- 断言：`assertThat(codes).doesNotHaveDuplicates()`
- 基准：char | spec | green

#### 4.7.2 WsPushMessageTest (`@Tag("spec")`)

#### shouldHave4Components_whenRecord
- 输入：`WsPushMessage.class.getRecordComponents()`
- 预期：组件名 `bizCode, version, timestamp, data`，`timestamp` 类型 `long`
- 断言：`assertThat(components).extracting(RecordComponent::getName).containsExactly("bizCode", "version", "timestamp", "data")`
- 基准：doc:pilot-to-cloud/websocket/message-push | spec | green

#### shouldBeAnnotatedWithVerified_whenClassLevel
- 输入：`WsPushMessage.class.getAnnotation(Verified.class)`
- 预期：存在，`basis()` 非空（标注 simulator WsMessageHandler 已对接 hivemind 验证）
- 断言：
  ```java
  Verified v = WsPushMessage.class.getAnnotation(Verified.class);
  assertThat(v).isNotNull();
  assertThat(v.basis()).isNotBlank();
  ```
- 基准：meta | spec | green

#### shouldBeAnnotatedWithDocUrl_whenClassLevel
- 输入：`WsPushMessage.class.getAnnotation(DocUrl.class)`
- 预期：存在，`value()` 指向 message-push 文档
- 断言：`assertThat(WsPushMessage.class.getAnnotation(DocUrl.class).value()).contains("message-push")`
- 基准：meta | spec | green

#### shouldRoundTrip_whenJsonSnakeCase
- 输入：JSON `{"biz_code":"device_osd","version":"1.0.0","timestamp":1234567890,"data":{}}`
- 预期：`bizCode()`=`"device_osd"`，`timestamp()`=1234567890
- 断言：`assertThat(msg.bizCode()).isEqualTo("device_osd"); assertThat(msg.timestamp()).isEqualTo(1234567890L)`
- 基准：doc:pilot-to-cloud/websocket/message-push | spec | green

---

## 5. protocol.envelope 包

### 5.1 RequestEnvelopeTest (`@Tag("spec")`)

#### shouldHave5Components_whenRecord
- 输入：`RequestEnvelope.class.getRecordComponents()`
- 预期：组件名 `tid, bid, timestamp, method, data`，`timestamp` 类型 `long`，`data` 类型 `Object`
- 断言：
  ```java
  List<String> names = stream(components).map(RecordComponent::getName).toList();
  assertThat(names).containsExactly("tid", "bid", "timestamp", "method", "data");
  assertThat(componentType("timestamp")).isEqualTo(long.class);
  ```
- 基准：doc:connection.html | spec | green

#### shouldAccessComponents_whenInstance
- 输入：`new RequestEnvelope("t1", "b1", 1L, "config", null)`
- 预期：各访问器返回对应值
- 断言：
  ```java
  RequestEnvelope e = new RequestEnvelope("t1", "b1", 1L, "config", null);
  assertThat(e.tid()).isEqualTo("t1");
  assertThat(e.bid()).isEqualTo("b1");
  assertThat(e.timestamp()).isEqualTo(1L);
  assertThat(e.method()).isEqualTo("config");
  assertThat(e.data()).isNull();
  ```
- 基准：char | spec | green

#### shouldRoundTrip_whenJsonSingleWords
- 输入：`"{\"tid\":\"t1\",\"bid\":\"b1\",\"timestamp\":1700000000000,\"method\":\"config\",\"data\":null}"` → `fromJson` → `toJson`
- 预期：往返后 tid/method/timestamp 一致（字段为单词，无 snake_case 问题）
- 断言：
  ```java
  RequestEnvelope env = MessageCodec.fromJson(json, RequestEnvelope.class);
  assertThat(env.tid()).isEqualTo("t1");
  assertThat(env.method()).isEqualTo("config");
  assertThat(env.timestamp()).isEqualTo(1700000000000L);
  ```
- 基准：doc:connection.html | spec | green

#### shouldSerializeEnvelope_whenToJson
- 输入：`new RequestEnvelope("t1","b1",1L,"config",Map.of("k","v"))` → `toJson`
- 预期：JSON 含 tid/bid/timestamp/method/data
- 断言：`assertThat(json).contains("\"tid\":\"t1\"", "\"method\":\"config\"", "\"data\":{")`
- 基准：doc:connection.html | spec | green

#### shouldTolerateUnknownProps_whenDeserialize
- 输入：JSON 含额外字段 `"extra":"x"`
- 预期：不抛异常（FAIL_ON_UNKNOWN=false）
- 断言：`assertThatCode(() -> MessageCodec.fromJson(jsonWithExtra, RequestEnvelope.class)).doesNotThrowAnyException()`
- 基准：char | spec | green

### 5.2 ReplyEnvelopeTest (`@Tag("spec")`)

#### shouldHave5Components_whenRecord
- 输入：`ReplyEnvelope.class.getRecordComponents()`
- 预期：`tid, bid, timestamp, method, data`，`data` 类型 `ReplyEnvelope.ReplyData`
- 断言：`assertThat(names).containsExactly("tid", "bid", "timestamp", "method", "data")`
- 基准：doc:connection.html | spec | green

#### shouldReplyDataHaveResultAndOutput
- 输入：`ReplyEnvelope.ReplyData.class.getRecordComponents()`
- 预期：`result`(int), `output`(Object)
- 断言：`assertThat(names).containsExactly("result", "output")`
- 基准：doc:connection.html | spec | green

#### shouldRoundTrip_whenJsonNestedData
- 输入：`"{\"tid\":\"t\",\"bid\":\"b\",\"timestamp\":1,\"method\":\"m\",\"data\":{\"result\":0,\"output\":{\"k\":\"v\"}}}"` → `fromJson`
- 预期：`data.result()`=0，`data.output()` 非 null
- 断言：
  ```java
  ReplyEnvelope env = MessageCodec.fromJson(json, ReplyEnvelope.class);
  assertThat(env.data().result()).isEqualTo(0);
  assertThat(env.data().output()).isNotNull();
  ```
- 基准：doc:connection.html | spec | green

#### shouldSerializeReply_whenToJson
- 输入：`new ReplyEnvelope("t","b",1L,"m",new ReplyData(0,Map.of("k","v")))` → `toJson`
- 预期：JSON `data.result`=0
- 断言：`assertThat(json).contains("\"result\":0", "\"output\":{")`
- 基准：doc:connection.html | spec | green

#### shouldResultZeroMeanSuccess
- 输入：`new ReplyData(0, null)`
- 预期：`result()`=0（对应 `DjiErrorCode.SUCCESS`）
- 断言：`assertThat(new ReplyData(0,null).result()).isEqualTo(DjiErrorCode.SUCCESS)`
- 基准：doc:error.html | spec | green

### 5.3 EventEnvelopeTest (`@Tag("spec")`)

#### shouldHave5Components_whenRecord
- 输入：`EventEnvelope.class.getRecordComponents()`
- 预期：`tid, bid, timestamp, method, data`
- 断言：`assertThat(names).containsExactly("tid", "bid", "timestamp", "method", "data")`
- 基准：doc:dock3/events.html | spec | green

#### shouldRoundTrip_whenJsonSingleWords
- 输入：`"{\"tid\":\"t\",\"bid\":\"b\",\"timestamp\":1,\"method\":\"file_upload_progress\",\"data\":{\"progress\":50}}"` → `fromJson`
- 预期：method=`file_upload_progress`，data 非 null
- 断言：
  ```java
  EventEnvelope env = MessageCodec.fromJson(json, EventEnvelope.class);
  assertThat(env.method()).isEqualTo("file_upload_progress");
  assertThat(env.data()).isNotNull();
  ```
- 基准：doc:dock3/events.html | spec | green

#### shouldSerializeEvent_whenToJson
- 输入：`new EventEnvelope("t","b",1L,"file_upload_progress",Map.of("progress",50))` → `toJson`
- 预期：JSON 含各字段
- 断言：`assertThat(json).contains("\"method\":\"file_upload_progress\"", "\"progress\":50")`
- 基准：doc:dock3/events.html | spec | green

---

## 6. protocol.error 包

### 6.1 DjiErrorCodeTest (`@Tag("spec")`)

#### shouldSuccessBeZero
- 输入：`DjiErrorCode.SUCCESS`
- 预期：`0`
- 断言：`assertThat(DjiErrorCode.SUCCESS).isEqualTo(0)`
- 基准：doc:error.html | spec | green

#### shouldFailBeOne
- 输入：`DjiErrorCode.FAIL`
- 预期：`1`
- 断言：`assertThat(DjiErrorCode.FAIL).isEqualTo(1)`
- 基准：doc:error.html | spec | green

#### shouldBindCodeErrorBe210229
- 输入：`DjiErrorCode.BIND_CODE_ERROR`
- 预期：`210229`
- 断言：`assertThat(DjiErrorCode.BIND_CODE_ERROR).isEqualTo(210229)`
- 基准：doc:error.html | spec | green

#### shouldOrganizationNotExistBe210234
- 输入：`DjiErrorCode.ORGANIZATION_NOT_EXIST`
- 预期：`210234`
- 断言：`assertThat(DjiErrorCode.ORGANIZATION_NOT_EXIST).isEqualTo(210234)`
- 基准：doc:error.html | spec | green

#### shouldDeviceBindOtherBe210235
- 输入：`DjiErrorCode.DEVICE_BIND_OTHER`
- 预期：`210235`
- 断言：`assertThat(DjiErrorCode.DEVICE_BIND_OTHER).isEqualTo(210235)`
- 基准：doc:error.html | spec | green

#### shouldHave233Constants_whenCountPublicStaticFinalIntFields
- 输入：`DjiErrorCode.class.getDeclaredFields()` 过滤 `public static final int` 类型
- 预期：233 个（通用 2 + HTTP API 注册绑定 3 + MQTT services_reply 215 + 直播 13）
- 断言：`assertThat(count).isEqualTo(233)`
- 基准：doc:error.html | spec | green

#### shouldNotBeInstantiable_whenPrivateConstructor
- 输入：`DjiErrorCode.class.getDeclaredConstructor()` 的修饰符
- 预期：构造器为 private
- 断言：`assertThat(DjiErrorCode.class.getDeclaredConstructor().getModifiers() & Modifier.PRIVATE).isNotZero()`
- 基准：char | spec | green

#### shouldDescribeReturnInfo_whenKnownCode
- 输入：`DjiErrorCode.describe(314001)`
- 预期：`Optional` 非空，`code()`=314001，`description()` 含「飞行任务下发失败」
- 断言：`assertThat(info).isPresent(); assertThat(info.get().code()).isEqualTo(314001); assertThat(info.get().description()).contains("飞行任务下发失败")`
- 基准：doc:error.html | spec | green

#### shouldDescribeReturnEmpty_whenUnknownCode
- 输入：`DjiErrorCode.describe(999999)`
- 预期：`Optional.empty()`
- 断言：`assertThat(DjiErrorCode.describe(999999)).isEmpty()`
- 基准：char | spec | green

#### shouldCodeTableSizeMatchConstantCount
- 输入：反射读取 `CODE_TABLE` 字段，调用 `size()`
- 预期：233（与 `public static final int` 常量数一致）
- 断言：`assertThat(tableSize).isEqualTo(233)`
- 基准：char | spec | green

### 6.2 DjiErrorInfoTest (`@Tag("spec")`)

#### shouldHave2Components_whenRecord
- 输入：`DjiErrorInfo.class.getRecordComponents()`
- 预期：组件名 `code, description`，类型 `int, String`
- 断言：`assertThat(names).containsExactly("code", "description")`
- 基准：doc:error.html | spec | green

#### shouldRoundTrip_whenJsonSnakeCase
- 输入：`"{\"code\":314001,\"description\":\"飞行任务下发失败\"}"` → `fromJson(DjiErrorInfo.class)`
- 预期：`code()`=314001，`description()` 含「飞行任务下发失败」
- 断言：`assertThat(info.code()).isEqualTo(314001); assertThat(info.description()).contains("飞行任务下发失败")`
- 基准：doc:error.html | spec | green

### 6.3 ErrorInfoTest (`@Tag("spec")`)

#### shouldHave2Components_whenRecord
- 输入：`ErrorInfo.class.getRecordComponents()`
- 预期：组件名 `sn, err_code`，`err_code` 类型 `int`
- 断言：`assertThat(names).containsExactly("sn", "err_code")`
- 基准：doc:error.html | spec | green

#### shouldRoundTrip_whenJsonSnakeCase
- 输入：`"{\"sn\":\"SN\",\"err_code\":210229}"` → `fromJson(ErrorInfo.class)`
- 预期：`sn()`=`SN`，`err_code()`=`210229`（record 组件名本身为 `err_code`，与 JSON 字段名一致，不受 #1 影响）
- 断言：
  ```java
  ErrorInfo info = MessageCodec.fromJson("{\"sn\":\"SN\",\"err_code\":210229}", ErrorInfo.class);
  assertThat(info.sn()).isEqualTo("SN");
  assertThat(info.err_code()).isEqualTo(210229);
  ```
- 基准：doc:error.html | spec | green

#### shouldAccessComponents_whenInstance
- 输入：`new ErrorInfo("SN", 0)`
- 预期：sn=SN, err_code=0
- 断言：`assertThat(new ErrorInfo("SN",0).sn()).isEqualTo("SN"); assertThat(new ErrorInfo("SN",0).err_code()).isEqualTo(0)`
- 基准：char | spec | green

#### shouldSerialize_whenToJson
- 输入：`new ErrorInfo("SN", 0)` → `toJson`
- 预期：JSON 含 `sn`/`err_code`
- 断言：`assertThat(MessageCodec.toJson(new ErrorInfo("SN",0))).contains("\"sn\":\"SN\"", "\"err_code\":0")`
- 基准：doc:error.html | spec | green

---

## 7. telemetry.enumtype 包

> 通用模式（22 个枚举共用）：
> - `shouldHaveNValues_whenValues`：断言 `values().length`
> - `shouldReturnCodeAndDescription_whenEnumSource`：`@EnumSource` 遍历，断言 `code()` 与 `description()` 非空
> - `shouldFindFromCode_whenKnown`：`@MethodSource` 提供 `(code, expectedEnum)`，断言 `fromCode(code)=expected`
> - `shouldThrow_whenUnknownCode`：断言抛 `IllegalArgumentException`，message 含关键词
> - `shouldHaveUniqueCodes`：所有 `code()` 互不重复
> - `shouldHaveConsecutiveCodes`：排序后连续（0..N-1）；**不连续值域枚举**（如 `DongleType` 6/10、`PositionQuality` 1-5/10、`NetworkType` 1-2）需标注"不连续"并验证空隙值抛异常
> - **Jackson 绑定**（仅 `CameraMode`/`BatteryStoreMode`/`ModeCodeReason`）：`@JsonValue` 序列化 int code、`@JsonCreator` 反序列化 int→枚举、双向闭环、非法 int 抛异常
>
> **已实现状态**（12/22）：
> - ✅ `BatteryStoreModeTest`（Jackson 绑定验证）
> - ✅ `ModeCodeReasonTest`
> - ✅ `CameraModeTest`（Jackson 绑定验证）
> - ✅ `DongleTypeTest`（不连续值 6/10）
> - ✅ `ThermalGainModeTest`
> - ✅ `RainfallTest`
> - ✅ `VideoQualityTest`
> - ✅ `NetworkTypeTest`（值域从 1 开始，无 0）
> - ✅ `NetworkQualityTest`
> - ✅ `PositionQualityTest`（不连续值 1-5/10）
> - ✅ `SourceTypeTest`
> - ✅ `HomePositionIsValidTest`
> - ⬜ `GearTest`（v1 规划，待实现）
> - ⬜ `DroneModeCodeTest`（v1 规划，待实现）
> - ⬜ `DockModeCodeTest`（v1 规划，待实现）
> - ⬜ `PositionStateTest`（v1 规划，待实现）
> - ⬜ `DroneChargeStateTest`（v1 规划，待实现）
> - ⬜ `AirConditionerStateTest`（待实现）
> - ⬜ `CoverStateTest`（待实现）
> - ⬜ `DrcStateTest`（待实现）
> - ⬜ `FlighttaskStepCodeTest`（待实现）
> - ⬜ `RcLostActionTest`（待实现）

### 7.1 GearTest (`@Tag("spec")`)

#### shouldHave10Gears_whenValues
- 断言：`assertThat(Gear.values()).hasSize(10)`
- 基准：doc:aircraft/properties.html | spec | green

#### shouldReturnCodeAndDescription_whenEnumSource
- 输入：`@EnumSource(Gear.class)`
- 断言：`assertThat(g.code()).isGreaterThanOrEqualTo(0); assertThat(g.description()).isNotBlank()`
- 基准：doc:aircraft/properties.html | spec | green

#### shouldReturnCodeAndDescription_whenParameterized
- 输入：`@MethodSource` 提供精确映射：`A→(0,"A档（姿态）")`, `P→(1,"P档（定位）")`, `NAV→(2,...)`, `FPV→(3,...)`, `FARM→(4,...)`, `S→(5,...)`, `F→(6,...)`, `M→(7,...)`, `G→(8,...)`, `T→(9,...)`
- 预期：`code()` 与 `description()` 匹配
- 断言：`assertThat(g.code()).isEqualTo(code); assertThat(g.description()).isEqualTo(desc)`
- 基准：doc:aircraft/properties.html | spec | green

#### shouldFindFromCode_whenKnown
- 输入：`@MethodSource` 提供 `(0,A),(1,P),...,(9,T)`
- 预期：`fromCode(code)` 返回对应枚举
- 断言：`assertThat(Gear.fromCode(code)).isEqualTo(expected)`
- 基准：doc:aircraft/properties.html | spec | green

#### shouldThrow_whenUnknownCode
- 输入：`Gear.fromCode(10)`
- 预期：抛 `IllegalArgumentException`，message 含 `"档位"`
- 断言：`assertThatThrownBy(() -> Gear.fromCode(10)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("档位")`
- 基准：char | spec | green

#### shouldThrow_whenNegativeCode
- 输入：`Gear.fromCode(-1)`
- 预期：抛 `IllegalArgumentException`
- 断言：`assertThatThrownBy(() -> Gear.fromCode(-1)).isInstanceOf(IllegalArgumentException.class)`
- 基准：char | spec | green

#### shouldHaveUniqueCodes_whenAllValues
- 断言：`assertThat(codes).doesNotHaveDuplicates()`
- 基准：char | spec | green

#### shouldHaveConsecutiveCodes_whenSorted
- 断言：排序后为 `0,1,...,9`
- 基准：char | spec | green

### 7.2 DroneModeCodeTest (`@Tag("spec")`)

#### shouldHave21Modes_whenValues
- 断言：`assertThat(DroneModeCode.values()).hasSize(21)`
- 基准：doc:aircraft/properties.html | spec | green

#### shouldReturnCodeAndDescription_whenEnumSource
- 输入：`@EnumSource(DroneModeCode.class)`
- 断言：`assertThat(m.code()).isBetween(0,20); assertThat(m.description()).isNotBlank()`
- 基准：doc:aircraft/properties.html | spec | green

#### shouldFindFromCode_whenKnown
- 输入：`@MethodSource` 提供 `(0,STANDBY),(1,TAKEOFF_PREPARATION),...,(20,POI_ORBIT)`（实现时按源码补全全部 21 项）
- 预期：`fromCode(code)` 返回对应枚举
- 断言：`assertThat(DroneModeCode.fromCode(code)).isEqualTo(expected)`
- 基准：doc:aircraft/properties.html | spec | green

#### shouldFindStandbyFromCodeZero
- 输入：`DroneModeCode.fromCode(0)`
- 预期：`STANDBY`
- 断言：`assertThat(DroneModeCode.fromCode(0)).isEqualTo(DroneModeCode.STANDBY)`
- 基准：doc:aircraft/properties.html | spec | green

#### shouldThrow_whenUnknownCode
- 输入：`DroneModeCode.fromCode(21)`
- 预期：抛 `IllegalArgumentException`，message 含 `"模式码"`
- 断言：`assertThatThrownBy(() -> DroneModeCode.fromCode(21)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("模式码")`
- 基准：char | spec | green

#### shouldHaveUniqueCodes
- 断言：`assertThat(codes).doesNotHaveDuplicates()`
- 基准：char | spec | green

#### shouldHaveConsecutiveCodes_whenSorted
- 断言：排序后为 `0,1,...,20`
- 基准：char | spec | green

#### shouldNotContainDockModes_whenDescriptionsInspected
- 输入：所有 `description()`
- 预期：不含机场模式描述（"空闲中"/"远程调试"等机场专属词），验证与 DockModeCode 已拆分（防历史缺陷回归）
- 断言：`assertThat(descriptions).noneMatch(d -> d.contains("空闲中"))`
- 基准：doc:aircraft/properties.html | spec | green

### 7.3 DockModeCodeTest (`@Tag("spec")`)

#### shouldHave6Modes_whenValues
- 断言：`assertThat(DockModeCode.values()).hasSize(6)`
- 基准：doc:dock2/properties.html | spec | green

#### shouldReturnCodeAndDescription_whenEnumSource
- 输入：`@EnumSource(DockModeCode.class)`
- 断言：`assertThat(m.code()).isBetween(0,5); assertThat(m.description()).isNotBlank()`
- 基准：doc:dock2/properties.html | spec | green

#### shouldReturnCodeAndDescription_whenParameterized
- 输入：`@MethodSource`：`IDLE→(0,"空闲中")`, `LOCAL_DEBUG→(1,...)`, `REMOTE_DEBUG→(2,...)`, `FIRMWARE_UPGRADING→(3,...)`, `WORKING→(4,...)`, `CALIBRATION_PENDING→(5,...)`
- 断言：`assertThat(m.code()).isEqualTo(code); assertThat(m.description()).isEqualTo(desc)`
- 基准：doc:dock2/properties.html | spec | green

#### shouldFindFromCode_whenKnown
- 输入：`@MethodSource` 提供 `(0,IDLE),...,(5,CALIBRATION_PENDING)`
- 预期：`fromCode` 返回对应
- 断言：`assertThat(DockModeCode.fromCode(code)).isEqualTo(expected)`
- 基准：doc:dock2/properties.html | spec | green

#### shouldThrow_whenUnknownCode
- 输入：`DockModeCode.fromCode(6)`
- 预期：抛 `IllegalArgumentException`，message 含 `"机场模式码"`
- 断言：`assertThatThrownBy(() -> DockModeCode.fromCode(6)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("机场模式码")`
- 基准：char | spec | green

#### shouldHaveUniqueCodes
- 断言：`assertThat(codes).doesNotHaveDuplicates()`
- 基准：char | spec | green

### 7.4 PositionStateTest (`@Tag("spec")`)

#### shouldHave4States_whenValues
- 断言：`assertThat(PositionState.values()).hasSize(4)`
- 基准：doc:dock2/properties.html | spec | green

#### shouldReturnCodeAndDescription_whenParameterized
- 输入：`@MethodSource`：`NOT_STARTED→(0,"未开始")`, `CONVERGING→(1,"收敛中")`, `CONVERGED→(2,"收敛成功")`, `CONVERGE_FAILED→(3,"收敛失败")`
- 断言：`assertThat(s.code()).isEqualTo(code); assertThat(s.description()).isEqualTo(desc)`
- 基准：doc:dock2/properties.html | spec | green

#### shouldFindFromCode_whenKnown
- 输入：`@MethodSource` 提供 `(0,NOT_STARTED),(1,CONVERGING),(2,CONVERGED),(3,CONVERGE_FAILED)`
- 预期：`fromCode` 返回对应
- 断言：`assertThat(PositionState.fromCode(code)).isEqualTo(expected)`
- 基准：doc:dock2/properties.html | spec | green

#### shouldThrow_whenUnknownCode
- 输入：`PositionState.fromCode(4)`
- 预期：抛 `IllegalArgumentException`，message 含 `"RTK 定位状态"`
- 断言：`assertThatThrownBy(() -> PositionState.fromCode(4)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("RTK 定位状态")`
- 基准：char | spec | green

#### shouldHaveUniqueCodes
- 断言：`assertThat(codes).doesNotHaveDuplicates()`
- 基准：char | spec | green

#### shouldHaveFromCodeConsistentWithOthers
- 输入：`PositionState.class.getMethod("fromCode", int.class)`
- 预期：存在（验证 #2 已修复，与 Gear/DroneModeCode/DockModeCode 一致）
- 断言：`assertThat(PositionState.class.getMethod("fromCode", int.class)).isNotNull()`
- 基准：char | spec | green

### 7.5 DroneChargeStateTest (`@Tag("spec")`)

#### shouldHave2States_whenValues
- 断言：`assertThat(DroneChargeState.values()).hasSize(2)`
- 基准：doc:dock2/properties.html | spec | green

#### shouldReturnCodeAndDescription_whenParameterized
- 输入：`@MethodSource`：`IDLE→(0,"空闲")`, `CHARGING→(1,"充电中")`
- 断言：`assertThat(s.code()).isEqualTo(code); assertThat(s.description()).isEqualTo(desc)`
- 基准：doc:dock2/properties.html | spec | green

#### shouldFindFromCode_whenKnown
- 输入：`@MethodSource` 提供 `(0,IDLE),(1,CHARGING)`
- 预期：`fromCode` 返回对应
- 断言：`assertThat(DroneChargeState.fromCode(code)).isEqualTo(expected)`
- 基准：doc:dock2/properties.html | spec | green

#### shouldThrow_whenUnknownCode
- 输入：`DroneChargeState.fromCode(2)`
- 预期：抛 `IllegalArgumentException`，message 含 `"充电状态"`
- 断言：`assertThatThrownBy(() -> DroneChargeState.fromCode(2)).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("充电状态")`
- 基准：char | spec | green

#### shouldHaveUniqueCodes
- 断言：`assertThat(codes).doesNotHaveDuplicates()`
- 基准：char | spec | green

#### shouldHaveFromCodeConsistentWithOthers
- 输入：`DroneChargeState.class.getMethod("fromCode", int.class)`
- 预期：存在（验证 #2 已修复）
- 断言：`assertThat(DroneChargeState.class.getMethod("fromCode", int.class)).isNotNull()`
- 基准：char | spec | green

### 7.6 BatteryStoreModeTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[BatteryStoreModeTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/BatteryStoreModeTest.java)
> 覆盖：2 值反查（1=计划模式, 2=待命模式）、0/3 越界异常、Jackson 序列化/反序列化（`@JsonValue`+`@JsonCreator`）、双向闭环

### 7.7 ModeCodeReasonTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[ModeCodeReasonTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/ModeCodeReasonTest.java)
> 覆盖：reason code 反查、越界异常

### 7.8 CameraModeTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[CameraModeTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/CameraModeTest.java)
> 覆盖：4 值反查（0=拍照, 1=录像, 2=智能低光, 3=全景拍照）、-1/4/5 越界异常（M30 的 -1 不混入）、Jackson 绑定（`@JsonValue`+`@JsonCreator`）、4 值双向闭环、CameraModeSwitchRequest POJO 绑定

### 7.9 DongleTypeTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[DongleTypeTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/DongleTypeTest.java)
> 覆盖：**不连续值** 6/10 反查（0/1/5/7/9/11 越界，含早期推测的 0/1 错误值验证）

### 7.10 ThermalGainModeTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[ThermalGainModeTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/ThermalGainModeTest.java)
> 覆盖：3 值反查（0=自动, 1=低增益, 2=高增益）、-1/3 越界、description 含测温范围信息

### 7.11 RainfallTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[RainfallTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/RainfallTest.java)
> 覆盖：4 值反查（0=无雨, 1=小雨, 2=中雨, 3=大雨）、-1/4 越界

### 7.12 VideoQualityTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[VideoQualityTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/VideoQualityTest.java)
> 覆盖：5 值反查（0=自适应, 1=流畅, 2=标清, 3=高清, 4=超清）、-1/5 越界

### 7.13 NetworkTypeTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[NetworkTypeTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/NetworkTypeTest.java)
> 覆盖：2 值反查（**值域从 1 开始**：1=4G, 2=以太网）、**0 越界验证**（值域无 0）

### 7.14 NetworkQualityTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[NetworkQualityTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/NetworkQualityTest.java)
> 覆盖：6 值反查（0=无信号, 1=差, 2=较差, 3=一般, 4=较好, 5=好）、-1/6 越界

### 7.15 PositionQualityTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[PositionQualityTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/PositionQualityTest.java)
> 覆盖：**不连续值** 1-5/10 反查（**空隙 6-9 与边界 0/11 越界验证**）

### 7.16 SourceTypeTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[SourceTypeTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/SourceTypeTest.java)
> 覆盖：4 值反查（0=未标定, 1=自收敛标定, 2=手动标定, 3=网络RTK标定）、-1/4 越界

### 7.17 HomePositionIsValidTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[HomePositionIsValidTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/telemetry/enumtype/HomePositionIsValidTest.java)
> 覆盖：4 值反查（0=都无效, 1=都有效, 2=航向有效经纬度无效, 3=经纬度有效航向无效）、description 含航向/经纬度有效性组合语义

### 7.18 AirConditionerStateTest (`@Tag("spec")`) — ⬜ 待实现

- 基准：doc:dock3/properties.html | spec | green
- 模式：通用 5 项（fromCode 反查、越界异常、总数、双射闭环、description 非空）

### 7.19 CoverStateTest (`@Tag("spec")`) — ⬜ 待实现

- 基准：doc:dock3/properties.html | spec | green
- 模式：通用 5 项

### 7.20 DrcStateTest (`@Tag("spec")`) — ⬜ 待实现

- 基准：doc:dock3/properties.html | spec | green
- 模式：通用 5 项

### 7.21 FlighttaskStepCodeTest (`@Tag("spec")`) — ⬜ 待实现

- 基准：doc:dock3/properties.html | spec | green
- 模式：通用 5 项（注意 step_code 值域可能不连续，需查 DJI 文档确认）

### 7.22 RcLostActionTest (`@Tag("spec")`) — ⬜ 待实现

- 基准：doc:aircraft/properties.html | spec | green
- 模式：通用 5 项

---

## 第一批汇总

- **测试类数**：42（annotation 3 + codec 2 + protocol.topic 4 + protocol.method 5 + protocol.envelope 3 + protocol.error 3 + telemetry.enumtype 22）
- **telemetry.enumtype 已实现**：12/22（CameraMode/DongleType/ThermalGainMode/BatteryStoreMode/ModeCodeReason + Rainfall/VideoQuality/NetworkType/NetworkQuality/PositionQuality/SourceType/HomePositionIsValid）
- **telemetry.enumtype 待实现**：10/22（Gear/DroneModeCode/DockModeCode/PositionState/DroneChargeState + AirConditionerState/CoverState/DrcState/FlighttaskStepCode/RcLostAction）
- **`@Disabled` 标红项**：2（均为 MessageCodec #1 snake_case 不匹配：`shouldDeserializeSnakeCaseOsd_whenToCamelCaseRecord`、`shouldRoundTripOsd_whenSerializeThenDeserialize`）
- **已修复项验证**：
  - #2：`PositionState`/`DroneChargeState` 的 `fromCode` 一致性（`shouldHaveFromCodeConsistentWithOthers`）
  - #4：`TopicBuilder.build(String,TopicChannel)` 的 `@Deprecated`（`shouldBeDeprecated_whenBuildSnChannelOverload`）
  - #5：`codec/package-info` 哨兵文档（`shouldVerifyExtractResultSentinelDocumented`）
- **历史缺陷防回归**：`Gear` 10 档（0-9）、`DroneModeCode`/`DockModeCode` 拆分验证（`shouldNotContainDockModes`）
- **修正项**：TopicChannel 实为 14 个（含 `STATE`）；DrcMethod=47、ServiceMethod=32（修正初版估计 33/35）
- **待用户确认**：本批详细格式与覆盖。确认后编写第二批（characterization 包：model/flow/telemetry record，15 类）。

---

# 第二批：characterization 标签包

> 本批锁定当前实现行为（无逐字段文档依据的项），标签 `@Tag("characterization")`。
> 对已知缺陷 #3（DeviceCompatibility switch 未覆盖 SMART_CONTROLLER_ENTERPRISE）以 `@Disabled` 标红。

## 8. model 包

### 8.1 DeviceDomainTest (`@Tag("characterization")`)

#### shouldHave3Domains_whenValues
- 断言：`assertThat(DeviceDomain.values()).hasSize(3)`
- 基准：doc:product-support.html | characterization | green

#### shouldReturnValues_whenParameterized
- 输入：`@MethodSource`：`AIRCRAFT→(0,"飞行器")`, `CONTROLLER→(2,"遥控器")`, `DOCK→(3,"机场")`
- 预期：`value()`/`description()` 匹配
- 断言：`assertThat(d.value()).isEqualTo(v); assertThat(d.description()).isEqualTo(desc)`
- 基准：doc:product-support.html | characterization | green

#### shouldNotHaveDomain1_whenValuesInspected
- 输入：所有 `value()`
- 预期：不含 `1`（DJI domain=1 未定义，保留空缺）
- 断言：`assertThat(values).doesNotContain(1)`
- 基准：doc:product-support.html | characterization | green

#### shouldHaveUniqueValues
- 断言：`assertThat(values).doesNotHaveDuplicates()`
- 基准：char | characterization | green

### 8.2 DeviceModelTest (`@Tag("characterization")`)

#### shouldHave6Components_whenRecord
- 输入：`DeviceModel.class.getRecordComponents()`
- 预期：`domain(int), type(int), subType(int), displayName(String), shortName(String), defaultSn(String)`
- 断言：`assertThat(names).containsExactly("domain","type","subType","displayName","shortName","defaultSn")`
- 基准：doc:product-support.html | characterization | green

#### shouldReturnModelKey_whenFormat
- 输入：`new DeviceModel(3,3,0,"大疆机场3","Dock3","SN").modelKey()`
- 预期：`"3-3-0"`
- 断言：`assertThat(new DeviceModel(3,3,0,"x","y","z").modelKey()).isEqualTo("3-3-0")`
- 基准：doc:product-support.html | characterization | green

#### shouldReturnIsDockTrue_whenDomain3
- 输入：`new DeviceModel(3,1,0,...).isDock()`
- 预期：`true`
- 断言：`assertThat(new DeviceModel(3,1,0,null,null,null).isDock()).isTrue()`
- 基准：doc:product-support.html | characterization | green

#### shouldReturnIsControllerTrue_whenDomain2
- 断言：`assertThat(new DeviceModel(2,119,0,null,null,null).isController()).isTrue()`
- 基准：char | characterization | green

#### shouldReturnIsAircraftTrue_whenDomain0
- 断言：`assertThat(new DeviceModel(0,67,0,null,null,null).isAircraft()).isTrue()`
- 基准：char | characterization | green

#### shouldReturnFalse_whenDomainNotMatch
- 输入：domain=0 的 `isDock()`
- 预期：`false`
- 断言：`assertThat(new DeviceModel(0,67,0,null,null,null).isDock()).isFalse()`
- 基准：char | characterization | green

### 8.3 DroneModelTest (`@Tag("characterization")`)

#### shouldHave14Models_whenValues
- 断言：`assertThat(DroneModel.values()).hasSize(14)`
- 基准：doc:product-support.html | characterization | green

#### shouldImplementDeviceModelProvider
- 输入：`DroneModel.class.getInterfaces()`
- 预期：含 `DeviceModelProvider.class`
- 断言：`assertThat(DroneModel.class.getInterfaces()).contains(DeviceModelProvider.class)`
- 基准：char | characterization | green

#### shouldReturnToModel_whenParameterized
- 输入：`@EnumSource(DroneModel.class)`
- 预期：`toModel()` 非 null，`domain()`=0，`modelKey()` 非 null
- 断言：
  ```java
  DeviceModel m = drone.toModel();
  assertThat(m).isNotNull();
  assertThat(m.domain()).isEqualTo(0);
  assertThat(m.modelKey()).isNotBlank();
  ```
- 基准：doc:product-support.html | characterization | green

#### shouldReturnRepresentativeTriples_whenParameterized
- 输入：`@MethodSource`：`M30→toModel()=("0-67-0")`, `M30T→"0-67-1"`, `M3D→"0-91-0"`, `M4D→"0-100-0"`, `M350_RTK→"0-89-0"`, `M300_RTK→"0-60-0"`, `MAVIC_3E→"0-77-0"`, `M400→"0-103-0"`, `M4E→"0-99-0"`, `M4T→"0-99-1"`
- 预期：`toModel().modelKey()` 匹配
- 断言：`assertThat(drone.toModel().modelKey()).isEqualTo(expectedKey)`
- 基准：doc:product-support.html | characterization | green

#### shouldAllHaveDomainZero
- 输入：`@EnumSource(DroneModel.class)`
- 预期：`toModel().domain()`=0
- 断言：`assertThat(drone.toModel().domain()).isEqualTo(0)`
- 基准：char | characterization | green

#### shouldHaveUniqueModelKeys
- 输入：所有 `toModel().modelKey()`
- 预期：互不重复
- 断言：`assertThat(keys).doesNotHaveDuplicates()`
- 基准：char | characterization | green

#### shouldHaveNonBlankDefaultSn
- 输入：`@EnumSource(DroneModel.class)`
- 预期：`toModel().defaultSn()` 非空，长度 20（飞行器 SN）
- 断言：`assertThat(drone.toModel().defaultSn()).hasSize(20)`
- 基准：char | characterization | green

### 8.4 DockModelTest (`@Tag("characterization")`)

#### shouldHave3Models_whenValues
- 断言：`assertThat(DockModel.values()).hasSize(3)`
- 基准：doc:product-support.html | characterization | green

#### shouldImplementDeviceModelProvider
- 断言：`assertThat(DockModel.class.getInterfaces()).contains(DeviceModelProvider.class)`
- 基准：char | characterization | green

#### shouldReturnTriples_whenParameterized
- 输入：`@MethodSource`：`DOCK1→"3-1-0"`, `DOCK2→"3-2-0"`, `DOCK3→"3-3-0"`
- 预期：`toModel().modelKey()` 匹配，`domain()`=3
- 断言：`assertThat(dock.toModel().modelKey()).isEqualTo(key); assertThat(dock.toModel().domain()).isEqualTo(3)`
- 基准：doc:product-support.html | characterization | green

#### shouldHaveDefaultSnLength15
- 输入：`@EnumSource(DockModel.class)`
- 预期：`defaultSn()` 长度 15（机场 SN）
- 断言：`assertThat(dock.toModel().defaultSn()).hasSize(15)`
- 基准：char | characterization | green

### 8.5 ControllerModelTest (`@Tag("characterization")`)

#### shouldHave4Models_whenValues
- 断言：`assertThat(ControllerModel.values()).hasSize(4)`
- 基准：doc:product-support.html | characterization | green

#### shouldImplementDeviceModelProvider
- 断言：`assertThat(ControllerModel.class.getInterfaces()).contains(DeviceModelProvider.class)`
- 基准：char | characterization | green

#### shouldReturnTriples_whenParameterized
- 输入：`@MethodSource`：`SMART_CONTROLLER_ENTERPRISE→"2-56-0"`, `RC_PLUS→"2-119-0"`, `RC_PLUS_2→"2-174-0"`, `RC_PRO→"2-144-0"`
- 预期：`toModel().modelKey()` 匹配，`domain()`=2
- 断言：`assertThat(c.toModel().modelKey()).isEqualTo(key); assertThat(c.toModel().domain()).isEqualTo(2)`
- 基准：doc:product-support.html | characterization | green

### 8.6 DeviceCompatibilityTest (`@Tag("characterization")`)

#### shouldDock1BeCompatibleWithM30M30T
- 输入：`isCompatible(DOCK1, M30)` / `isCompatible(DOCK1, M30T)`
- 预期：`true`
- 断言：
  ```java
  assertThat(DeviceCompatibility.isCompatible(DockModel.DOCK1, DroneModel.M30)).isTrue();
  assertThat(DeviceCompatibility.isCompatible(DockModel.DOCK1, DroneModel.M30T)).isTrue();
  ```
- 基准：doc:product-support.html | characterization | green

#### shouldDock1BeIncompatibleWithM3D
- 输入：`isCompatible(DOCK1, M3D)`
- 预期：`false`
- 断言：`assertThat(DeviceCompatibility.isCompatible(DockModel.DOCK1, DroneModel.M3D)).isFalse()`
- 基准：doc:product-support.html | characterization | green

#### shouldDock2BeCompatibleWithM3D_M3TD_M30_M30T
- 输入：`@MethodSource` 提供 `(DOCK2, M3D),(DOCK2,M3TD),(DOCK2,M30),(DOCK2,M30T)`
- 预期：`true`
- 断言：`assertThat(DeviceCompatibility.isCompatible(dock, drone)).isTrue()`
- 基准：doc:product-support.html | characterization | green

#### shouldDock3BeCompatibleWithM4D_M4TD
- 输入：`(DOCK3, M4D), (DOCK3, M4TD)`
- 预期：`true`
- 断言：`assertThat(DeviceCompatibility.isCompatible(DOCK3, M4D)).isTrue(); assertThat(...M4TD).isTrue()`
- 基准：doc:product-support.html | characterization | green

#### shouldDockCompatibilityMatrix_whenAllCombosParameterized
- 输入：`@MethodSource` 提供 3×14=42 组 `(dock, drone, expected)` 全矩阵
- 预期：按兼容规则
- 断言：`assertThat(DeviceCompatibility.isCompatible(dock, drone)).isEqualTo(expected)`
- 基准：doc:product-support.html | characterization | green

#### shouldRcPlusBeCompatibleWithM350_M300_M30_M30T
- 输入：`@MethodSource`：`(RC_PLUS, M350_RTK),(RC_PLUS,M300_RTK),(RC_PLUS,M30),(RC_PLUS,M30T)`
- 预期：`true`
- 断言：`assertThat(DeviceCompatibility.isCompatible(controller, drone)).isTrue()`
- 基准：doc:product-support.html | characterization | green

#### shouldRcPlus2BeCompatibleWithM400_M4E_M4T
- 输入：`(RC_PLUS_2, M400),(RC_PLUS_2,M4E),(RC_PLUS_2,M4T)`
- 预期：`true`
- 断言：`assertThat(DeviceCompatibility.isCompatible(RC_PLUS_2, drone)).isTrue()`
- 基准：doc:product-support.html | characterization | green

#### shouldRcProBeCompatibleWithMavic3E_3T
- 输入：`(RC_PRO, MAVIC_3E),(RC_PRO, MAVIC_3T)`
- 预期：`true`
- 断言：`assertThat(DeviceCompatibility.isCompatible(RC_PRO, drone)).isTrue()`
- 基准：doc:product-support.html | characterization | green

#### shouldControllerCompatibilityMatrix_whenAllCombosParameterized
- 输入：`@MethodSource` 提供 4×14=56 组 `(controller, drone, expected)` 全矩阵（含 SMART_CONTROLLER_ENTERPRISE）
- 预期：RC_PLUS/RC_PLUS_2/RC_PRO/SMART_CONTROLLER_ENTERPRISE 均按兼容规则返回，无异常
- 断言：`assertThat(isCompatible(c, d)).isEqualTo(expected)`
- 基准：doc:product-support.html | characterization | green

#### shouldSmartControllerEnterpriseBeCompatibleWithM300Rtk
- 输入：`isCompatible(SMART_CONTROLLER_ENTERPRISE, M300_RTK)`
- 预期：`true`（DJI 带屏遥控器行业版搭配 Matrice 300 RTK，依据 ControllerModel Javadoc）
- 断言：`assertThat(DeviceCompatibility.isCompatible(ControllerModel.SMART_CONTROLLER_ENTERPRISE, DroneModel.M300_RTK)).isTrue()`
- 基准：doc:product-support.html | characterization | green

#### shouldSmartControllerEnterpriseBeIncompatibleWithOthers
- 输入：`@MethodSource`：`(SMART_CONTROLLER_ENTERPRISE, M30),(SMART_CONTROLLER_ENTERPRISE,M4D),(SMART_CONTROLLER_ENTERPRISE,MAVIC_3E)` 等（非 M300_RTK 的代表性型号）
- 预期：`false`
- 断言：`assertThat(DeviceCompatibility.isCompatible(ControllerModel.SMART_CONTROLLER_ENTERPRISE, drone)).isFalse()`
- 基准：doc:product-support.html | characterization | green

---

## 9. flow 包

### 9.1 RegistrationStepTest (`@Tag("characterization")`)

#### shouldHave6Components_whenRecord
- 输入：`RegistrationStep.class.getRecordComponents()`
- 预期：`methodName, description, channelType, timeoutSeconds, retryCount, retryIntervalSeconds`
- 断言：`assertThat(names).containsExactly("methodName","description","channelType","timeoutSeconds","retryCount","retryIntervalSeconds")`
- 基准：char | characterization | green

#### shouldChannelTypeHave3Values
- 输入：`RegistrationStep.ChannelType.values()`
- 预期：`REQUESTS, REQUESTS_REPLY, STATUS`
- 断言：`assertThat(RegistrationStep.ChannelType.values()).containsExactly(REQUESTS, REQUESTS_REPLY, STATUS)`
- 基准：doc:connection.html | characterization | green

#### shouldAccessComponents_whenInstance
- 输入：`new RegistrationStep("config","desc",REQUESTS,3,3,3)`
- 预期：各访问器返回对应值
- 断言：
  ```java
  RegistrationStep s = new RegistrationStep("config","desc",REQUESTS,3,3,3);
  assertThat(s.methodName()).isEqualTo("config");
  assertThat(s.channelType()).isEqualTo(REQUESTS);
  assertThat(s.timeoutSeconds()).isEqualTo(3);
  assertThat(s.retryCount()).isEqualTo(3);
  ```
- 基准：char | characterization | green

### 9.2 OnlineFlowTest (`@Tag("characterization")`)

#### shouldReturnUpdateTopoMethod
- 输入：`OnlineFlow.METHOD`
- 预期：`"update_topo"`
- 断言：`assertThat(OnlineFlow.METHOD).isEqualTo("update_topo")`
- 基准：doc:dock1/device.html | characterization | green

#### shouldBuildPayload_whenSubDevicesGiven
- 输入：
  ```java
  String json = OnlineFlow.buildUpdateTopoPayload(
      "gateway-sn", new DeviceModel(3,3,0,"Dock3","Dock3","SN"),
      "secret", "nonce", "3.0.0.0",
      List.of(new OnlineFlow.SubDevice("drone-sn", new DeviceModel(0,67,0,"M30","M30","DSN"), "A", "ds", "dn", "3.0.0.0")));
  ```
- 预期：JSON 含 `method:"update_topo"`，`data.domain:"3"`(string)，`data.type:3`(int)，`data.sub_type:0`，`data.sub_devices` 长度 1，子设备 `sn:"drone-sn"`，`domain:"0"`(string)，`index:"A"`
- 断言：
  ```java
  assertThat(json).contains("\"method\":\"update_topo\"");
  assertThat(MessageCodec.extractMethod(json)).isEqualTo("update_topo");
  // 解析 data 验证类型
  Object data = MessageCodec.extractData(json);
  assertThat(((Map<?,?>)data).get("domain")).isEqualTo("3");
  assertThat(((Map<?,?>)data).get("type")).isEqualTo(3);
  assertThat(((Map<?,?>)data).get("sub_devices")).isInstanceOf(List.class);
  ```
- 基准：doc:dock1/device.html | characterization | green

#### shouldGatewaySnNotAppearInPayload
- 输入：同上，gatewaySn=`"gateway-sn"`
- 预期：payload JSON 不含 `"gateway-sn"`（SN 属于 topic，非 payload）
- 断言：`assertThat(json).doesNotContain("gateway-sn")`
- 基准：doc:dock1/device.html | characterization | green

#### shouldReturnEmptySubDevices_whenEmptyList
- 输入：`buildUpdateTopoPayload(..., List.of())`
- 预期：`data.sub_devices` 为空数组 `[]`
- 断言：`assertThat(json).contains("\"sub_devices\":[]")`
- 基准：char | characterization | green

#### shouldReturnEmptySubDevices_whenNullList
- 输入：`buildUpdateTopoPayload(..., null)`
- 预期：`data.sub_devices` 为 `[]`
- 断言：`assertThat(json).contains("\"sub_devices\":[]")`
- 基准：char | characterization | green

#### shouldGenerateUuidTidBid
- 输入：`buildUpdateTopoPayload(...)`
- 预期：`tid`/`bid` 为 UUID 格式字符串（非 null）
- 断言：
  ```java
  assertThat(MessageCodec.extractTid(json)).isNotBlank();
  assertThat(MessageCodec.extractBid(json)).isNotBlank();
  ```
- 基准：doc:connection.html | characterization | green

#### shouldDomainBeStringAndTypeBeInt
- 输入：`buildUpdateTopoPayload(..., new DeviceModel(3,3,0,...), ...)`
- 预期：`data.domain` 序列化为字符串 `"3"`，`data.type`/`data.sub_type` 为数字
- 断言：
  ```java
  Map<?,?> data = (Map<?,?>) MessageCodec.extractData(json);
  assertThat(data.get("domain")).isInstanceOf(String.class);
  assertThat(data.get("type")).isInstanceOf(Integer.class);
  assertThat(data.get("sub_type")).isInstanceOf(Integer.class);
  ```
- 基准：doc:dock1/device.html | characterization | green

### 9.3 DockRegistrationFlowTest (`@Tag("characterization")`)

#### shouldHave5Steps_whenSteps
- 输入：`DockRegistrationFlow.steps()`
- 预期：长度 5
- 断言：`assertThat(DockRegistrationFlow.steps()).hasSize(5)`
- 基准：doc:pilot-access.html | characterization | green

#### shouldTotalStepsBe5
- 断言：`assertThat(DockRegistrationFlow.totalSteps()).isEqualTo(5)`
- 基准：char | characterization | green

#### shouldStepsInOrder_whenMethodNameInspected
- 输入：`steps().map(RegistrationStep::methodName)`
- 预期：`["config","airport_bind_status","airport_organization_get","airport_organization_bind","update_topo"]`
- 断言：
  ```java
  assertThat(DockRegistrationFlow.steps())
      .map(RegistrationStep::methodName)
      .containsExactly("config","airport_bind_status","airport_organization_get","airport_organization_bind","update_topo");
  ```
- 基准：doc:organization.html | characterization | green

#### shouldFirst4StepsUseRequestsChannel
- 输入：`steps().subList(0,4)` 的 `channelType()`
- 预期：均为 `REQUESTS`
- 断言：`assertThat(DockRegistrationFlow.steps()).subList(0,4).map(RegistrationStep::channelType).containsOnly(REQUESTS)`
- 基准：doc:organization.html | characterization | green

#### shouldUpdateTopoStepUseStatusChannel
- 输入：`UPDATE_TOPO.channelType()`
- 预期：`STATUS`
- 断言：`assertThat(DockRegistrationFlow.UPDATE_TOPO.channelType()).isEqualTo(STATUS)`
- 基准：doc:dock1/device.html | characterization | green

#### shouldConfigStepHaveRetry3
- 输入：`CONFIG.retryCount()` / `CONFIG.retryIntervalSeconds()`
- 预期：均为 3
- 断言：`assertThat(DockRegistrationFlow.CONFIG.retryCount()).isEqualTo(3); assertThat(...CONFIG.retryIntervalSeconds()).isEqualTo(3)`
- 基准：char | characterization | green

#### shouldNonConfigStepsHaveNoRetry
- 输入：`steps()` 中除 CONFIG 外的 `retryCount()`
- 预期：均为 0
- 断言：`assertThat(steps).filteredOn(s -> s != CONFIG).map(RegistrationStep::retryCount).containsOnly(0)`
- 基准：char | characterization | green

#### shouldAllStepsBeVerifiedOrInferred
- 输入：反射 5 个 step 字段的 `@Verified`/`@Inferred`
- 预期：每个字段有 `@Verified`
- 断言：`assertThat(fields).allMatch(f -> f.isAnnotationPresent(Verified.class))`
- 基准：meta | characterization | green

### 9.4 PilotRegistrationFlowTest (`@Tag("characterization")`)

#### shouldHave5Steps_whenSteps
- 断言：`assertThat(PilotRegistrationFlow.steps()).hasSize(5)`
- 基准：doc:pilot-access.html | characterization | green

#### shouldStepsInOrder_whenMethodNameInspected
- 输入：`steps().map(RegistrationStep::methodName)`
- 预期：`["config","airport_bind_status","airport_organization_get","airport_organization_bind","update_topo"]`
- 断言：`assertThat(PilotRegistrationFlow.steps()).map(RegistrationStep::methodName).containsExactly("config","airport_bind_status","airport_organization_get","airport_organization_bind","update_topo")`
- 基准：doc:pilot-access.html | characterization | green

#### shouldUpdateTopoStepUseStatusChannel
- 断言：`assertThat(PilotRegistrationFlow.UPDATE_TOPO.channelType()).isEqualTo(STATUS)`
- 基准：doc:dock1/device.html | characterization | green

#### shouldUpdateTopoBeInferred
- 输入：`PilotRegistrationFlow.class.getField("UPDATE_TOPO").getAnnotation(Inferred.class)`
- 预期：存在，`reason()` 非空，`verifyPoint()` 非空
- 断言：
  ```java
  Inferred inf = PilotRegistrationFlow.class.getField("UPDATE_TOPO").getAnnotation(Inferred.class);
  assertThat(inf).isNotNull();
  assertThat(inf.reason()).isNotBlank();
  assertThat(inf.verifyPoint()).isNotBlank();
  ```
- 基准：meta | characterization | green

#### shouldHaveSameMethodNamesAsDockFlow
- 输入：`PilotRegistrationFlow.steps()` vs `DockRegistrationFlow.steps()` 的 methodName
- 预期：完全一致（Pilot 与机场注册步骤 method 名相同）
- 断言：
  ```java
  List<String> pilot = PilotRegistrationFlow.steps().map(RegistrationStep::methodName).toList();
  List<String> dock = DockRegistrationFlow.steps().map(RegistrationStep::methodName).toList();
  assertThat(pilot).isEqualTo(dock);
  ```
- 基准：doc:pilot-access.html | characterization | green

---

## 10. telemetry 包

### 10.1 OsdFieldTest (`@Tag("characterization")`)

#### shouldHave64Fields_whenValues
- 断言：`assertThat(OsdField.values()).hasSize(64)`
- 基准：doc:dock2/properties.html | characterization | green

#### shouldReturnFieldNameAndDescription_whenEnumSource
- 输入：`@EnumSource(OsdField.class)`
- 预期：`fieldName()` 与 `description()` 非空
- 断言：`assertThat(f.fieldName()).isNotBlank(); assertThat(f.description()).isNotBlank()`
- 基准：doc:dock2/properties.html | characterization | green

#### shouldFieldNameBeSnakeCase_whenEnumSource
- 输入：`@EnumSource(OsdField.class)`
- 预期：`fieldName()` 匹配 `^[a-z][a-z0-9_]*$`（snake_case）
- 断言：`assertThat(f.fieldName()).matches("^[a-z][a-z0-9_]*$")`
- 基准：char | characterization | green

#### shouldHaveUniqueFieldNames
- 输入：所有 `fieldName()`
- 预期：互不重复
- 断言：`assertThat(fieldNames).doesNotHaveDuplicates()`
- 基准：char | characterization | green

#### shouldFindFromFieldName_whenKnown
- 输入：`@MethodSource` 提供代表性值（如 `"mode_code"→MODE_CODE`，`"latitude"→LATITUDE`，`"job_number"→JOB_NUMBER`，实现时补全）
- 预期：`fromFieldName(name)` 返回对应枚举
- 断言：`assertThat(OsdField.fromFieldName(name)).isEqualTo(expected)`
- 基准：char | characterization | green

#### shouldThrow_whenUnknownFieldName
- 输入：`OsdField.fromFieldName("not_a_field")`
- 预期：抛 `IllegalArgumentException`
- 断言：`assertThatThrownBy(() -> OsdField.fromFieldName("not_a_field")).isInstanceOf(IllegalArgumentException.class)`
- 基准：char | characterization | green

#### shouldThrow_whenNullFieldName
- 输入：`OsdField.fromFieldName(null)`
- 预期：抛 `IllegalArgumentException`（与 Javadoc `@throws` 声明一致；源码在查找表查询前对 null 做显式校验，避免 `Collectors.toUnmodifiableMap` 底层 `MapN.get(null)` 触发 NPE）
- 断言：`assertThatThrownBy(() -> OsdField.fromFieldName(null)).isInstanceOf(IllegalArgumentException.class)`
- 基准：char | characterization | green

### 10.2 StateFieldTest (`@Tag("characterization")`)

#### shouldHave33Fields_whenValues
- 断言：`assertThat(StateField.values()).hasSize(33)`
- 基准：doc:dock2/properties.html | characterization | green

#### shouldReturnFieldNameAndDescription_whenEnumSource
- 输入：`@EnumSource(StateField.class)`
- 断言：`assertThat(f.fieldName()).isNotBlank(); assertThat(f.description()).isNotBlank()`
- 基准：doc:dock2/properties.html | characterization | green

#### shouldFieldNameBeSnakeCase_whenEnumSource
- 断言：`assertThat(f.fieldName()).matches("^[a-z][a-z0-9_]*$")`
- 基准：char | characterization | green

#### shouldHaveUniqueFieldNames
- 断言：`assertThat(fieldNames).doesNotHaveDuplicates()`
- 基准：char | characterization | green

#### shouldFindFromFieldName_whenKnown
- 输入：`@MethodSource` 提供代表性值（如 `"current_status"→...`，实现时按源码补全）
- 预期：`fromFieldName(name)` 返回对应枚举
- 断言：`assertThat(StateField.fromFieldName(name)).isEqualTo(expected)`
- 基准：char | characterization | green

#### shouldThrow_whenUnknownFieldName
- 断言：`assertThatThrownBy(() -> StateField.fromFieldName("nope")).isInstanceOf(IllegalArgumentException.class)`
- 基准：char | characterization | green

#### shouldThrow_whenNullFieldName
- 输入：`StateField.fromFieldName(null)`
- 预期：抛 `IllegalArgumentException`（与 Javadoc `@throws` 声明一致；源码在查找表查询前对 null 做显式校验，避免 `Collectors.toUnmodifiableMap` 底层 `MapN.get(null)` 触发 NPE）
- 断言：`assertThatThrownBy(() -> StateField.fromFieldName(null)).isInstanceOf(IllegalArgumentException.class)`
- 基准：char | characterization | green

### 10.3 DroneOsdTest (`@Tag("characterization")`)

#### shouldHave33Components_whenRecord
- 输入：`DroneOsd.class.getRecordComponents()`
- 预期：组件数 33
- 断言：`assertThat(DroneOsd.class.getRecordComponents()).hasSize(33)`
- 基准：doc:aircraft/properties.html | characterization | green

#### shouldComponentsBeCamelCase_whenInspected
- 输入：所有组件名
- 预期：匹配 `^[a-z][a-zA-Z0-9]*$`（camelCase）
- 断言：`assertThat(names).allMatch(n -> n.matches("^[a-z][a-zA-Z0-9]*$"))`
- 基准：char | characterization | green

#### shouldContainKeyFields_whenInspected
- 输入：组件名列表
- 预期：含 `modeCode, latitude, longitude, height, elevation, gear, battery, positionState, firmwareVersion`
- 断言：`assertThat(names).contains("modeCode","latitude","longitude","height","elevation","gear","battery","positionState","firmwareVersion")`
- 基准：doc:aircraft/properties.html | characterization | green

#### shouldAllowNullFields_whenInstance
- 输入：`new DroneOsd(null,null,...,null)`（全部 null）
- 预期：不抛异常，各访问器返回 null（包装类型允许 null）
- 断言：
  ```java
  DroneOsd osd = new DroneOsd(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
  assertThat(osd.modeCode()).isNull();
  assertThat(osd.latitude()).isNull();
  ```
- 基准：char | characterization | green

#### shouldDeserializeSnakeCaseJson_whenAllFieldsNull
- 输入：`"{\"mode_code\":0,\"latitude\":22.0,\"gear\":1}"` → `fromJson(DroneOsd.class)`
- 预期（特征化，当前行为）：`modeCode()`=null（因 #1 snake_case 不匹配）
- 断言：`assertThat(osd.modeCode()).isNull()`（锁定当前 bug 行为，修复后此测试需更新为 `isEqualTo(0)`）
- 基准：char | characterization | green

### 10.4 ControllerOsdTest (`@Tag("characterization")`)

#### shouldHave5Components_whenRecord
- 输入：`ControllerOsd.class.getRecordComponents()`
- 预期：`modeCode, latitude, longitude, battery, country`
- 断言：`assertThat(names).containsExactly("modeCode","latitude","longitude","battery","country")`
- 基准：doc:remote-controller/properties.html | characterization | green

#### shouldComponentsBeCamelCase
- 断言：`assertThat(names).allMatch(n -> n.matches("^[a-z][a-zA-Z0-9]*$"))`
- 基准：char | characterization | green

#### shouldAccessComponents_whenInstance
- 输入：`new ControllerOsd(0,22.0,113.0,80,"CN")`
- 预期：各访问器返回对应值
- 断言：
  ```java
  ControllerOsd osd = new ControllerOsd(0,22.0,113.0,80,"CN");
  assertThat(osd.modeCode()).isEqualTo(0);
  assertThat(osd.battery()).isEqualTo(80);
  assertThat(osd.country()).isEqualTo("CN");
  ```
- 基准：char | characterization | green

#### shouldDeserializeSnakeCaseJson_whenAllFieldsNull
- 输入：`"{\"mode_code\":0,\"latitude\":22.0}"` → `fromJson(ControllerOsd.class)`
- 预期（特征化，当前行为）：`modeCode()`=null（因 #1）
- 断言：`assertThat(osd.modeCode()).isNull()`
- 基准：char | characterization | green

### 10.5 DockOsdTest (`@Tag("characterization")`)

#### shouldHave37Components_whenRecord
- 输入：`DockOsd.class.getRecordComponents()`
- 预期：组件数 37
- 断言：`assertThat(DockOsd.class.getRecordComponents()).hasSize(37)`
- 基准：doc:dock2/properties.html | characterization | green

#### shouldComponentsBeCamelCase
- 输入：所有组件名
- 预期：匹配 `^[a-z][a-zA-Z0-9]*$`
- 断言：`assertThat(names).allMatch(n -> n.matches("^[a-z][a-zA-Z0-9]*$"))`
- 基准：char | characterization | green

#### shouldContainKeyFields_whenInspected
- 输入：组件名列表
- 预期：含 `modeCode, latitude, longitude, droneChargeState, networkState, droneInDock`（代表性，实现时按源码调整）
- 断言：`assertThat(names).contains("modeCode","latitude","longitude")`
- 基准：doc:dock2/properties.html | characterization | green

#### shouldAllowNullFields_whenInstance
- 输入：全 null 构造（按组件数填 null）
- 预期：不抛异常
- 断言：`assertThatCode(() -> new DockOsd(null,...)).doesNotThrowAnyException()`
- 基准：char | characterization | green

#### shouldDeserializeSnakeCaseJson_whenAllFieldsNull
- 输入：`"{\"mode_code\":1,\"latitude\":22.0}"` → `fromJson(DockOsd.class)`
- 预期（特征化，当前行为）：`modeCode()`=null（因 #1）
- 断言：`assertThat(osd.modeCode()).isNull()`
- 基准：char | characterization | green

---

## 11. command.service 包

> 设计依据：[command 设计文档](superpowers/specs/2026-08-14-command-pojo-design.md)
> 测试模式：每指令 5 类用例（组件数 / snake_case 反序列化 / snake_case 序列化 / 注解存在性 / 往返一致性）
> 空 Reply 专项：组件数 0 + Javadoc 含「无 output」标注
>
> **已实现状态**（2/~107）：
> - ✅ `ServiceSupplementParseTest`（96 个 ServiceMethod 枚举总数验证 + fromMethodName 反查）
> - ✅ `ServiceParamParseTest`（services 通道参数解析）
> - ✅ `CameraModeSwitchRequestTest`（[测试文件](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/command/service/camera/CameraModeSwitchRequestTest.java)，camera_mode:3 反序列化 + Jackson 双向闭环 + 缺失字段 NPE）
> - ⬜ 其余 ~100 个 service POJO 待实现（camera 21 + flight 10 + live 7 + wayline 22 + debug 6 + drc 3 + esdk 2 + esim 4 + firmware 2 + flysafe 4 + log 5 + media 2 + pilot 3 + psdk 15 + 根 14）

### 11.1 LiveStartPushRequestTest (`@Tag("spec")`)

#### shouldHave4Components_whenRecord
- 输入：`LiveStartPushRequest.class.getRecordComponents()`
- 预期：`videoId, url, urlType, videoQuality`
- 断言：`assertThat(names).containsExactly("videoId","url","urlType","videoQuality")`
- 基准：doc:dock.html | spec | green

#### shouldDeserializeSnakeCase_whenJsonGiven
- 输入：`{"video_id":"v1","url":"rtmp://x","url_type":1,"video_quality":4}`
- 预期：`videoId()="v1"`, `urlType()=1`
- 断言：`assertThat(req.videoId()).isEqualTo("v1"); assertThat(req.urlType()).isEqualTo(1)`
- 基准：doc:dock.html | spec | green

#### shouldSerializeToSnakeCase_whenRecordGiven
- 输入：`new LiveStartPushRequest("v1","rtmp://x",1,4)`
- 预期：JSON 含 `"video_id":"v1"`,`"url_type":1`
- 断言：`assertThat(json).contains("\"video_id\":\"v1\"", "\"url_type\":1")`
- 基准：doc:dock.html | spec | green

#### shouldBeAnnotatedWithVerified_whenClassLevel
- 输入：`LiveStartPushRequest.class.getAnnotation(Verified.class)`
- 预期：存在，`basis()` 含 "LiveStreamSimulator"
- 断言：`assertThat(v.basis()).contains("LiveStreamSimulator")`
- 基准：meta | spec | green

#### shouldRoundTrip_whenSerializeThenDeserialize
- 输入：`new LiveStartPushRequest("v1","rtmp://x",1,4)` → `toJson` → `fromJson`
- 预期：字段值一致
- 断言：`assertThat(roundTrip.videoId()).isEqualTo("v1")`
- 基准：char | spec | green

#### shouldThrowWhenRequiredFieldMissing_whenVideoIdNull
- 输入：`{"url":"x","url_type":1,"video_quality":4}`（缺 video_id）
- 预期：反序列化抛 `NullPointerException`，message 含「videoId 必填」
- 断言：`assertThatThrownBy(() -> MessageCodec.fromJson(json, LiveStartPushRequest.class)).isInstanceOf(NullPointerException.class).hasMessageContaining("videoId 必填")`
- 基准：char | spec | green

### 11.2 LiveStartPushReplyTest (`@Tag("spec")`)（空 Reply 模板）

#### shouldHaveZeroComponents_whenEmptyReply
- 输入：`LiveStartPushReply.class.getRecordComponents()`
- 预期：空数组
- 断言：`assertThat(components).isEmpty()`
- 基准：char | spec | green

#### shouldBeAnnotatedWithVerified_whenClassLevel
- 输入：`LiveStartPushReply.class.getAnnotation(Verified.class)`
- 预期：basis 含「无 output」
- 断言：`assertThat(v.basis()).contains("无 output")`
- 基准：meta | spec | green

### 11.3 FlighttaskPrepareRequestTest (`@Tag("spec")`)（多嵌套字段模板）

#### shouldHave14Components_whenRecord
- 输入：`FlighttaskPrepareRequest.class.getRecordComponents()`
- 预期：14 个组件，含 `flightId, taskType, file, readyConditions, breakPoint, simulateMission` 等
- 断言：`assertThat(components).hasSize(14); assertThat(names).contains("flightId","file","simulateMission")`
- 基准：doc:dock.html | spec | green

#### shouldDeserializeSnakeCase_whenNestedJsonGiven
- 输入：`{"flight_id":"f1","file":{"url":"u","fingerprint":"fp"},"simulate_mission":{"is_enable":1,"latitude":22.0,"longitude":113.0,"altitude":50.0}}`
- 预期：`flightId()="f1"`, `file().url()="u"`, `simulateMission().latitude()=22.0`
- 断言：`assertThat(req.flightId()).isEqualTo("f1"); assertThat(req.file().url()).isEqualTo("u"); assertThat(req.simulateMission().latitude()).isEqualTo(22.0)`
- 基准：doc:dock.html | spec | green

#### shouldSerializeToSnakeCase_whenNestedRecordGiven
- 输入：含嵌套 record 的 FlighttaskPrepareRequest
- 预期：JSON 含 `"flight_id":"f1"`,`"file":{"url":"u"}`,`"simulate_mission":{"is_enable":1}`
- 断言：`assertThat(json).contains("\"flight_id\":\"f1\"", "\"simulate_mission\":{")`
- 基准：doc:dock.html | spec | green

#### shouldBeAnnotatedWithVerified_whenClassLevel
- 输入：类级 `@Verified`
- 预期：basis 含 "WaylineTaskSimulator.handlePrepare"
- 断言：`assertThat(v.basis()).contains("WaylineTaskSimulator")`
- 基准：meta | spec | green

#### shouldHaveInferredOnWaylinePrecisionType_whenFieldLevel
- 输入：`FlighttaskPrepareRequest.class.getRecordComponents()` 中 `waylinePrecisionType` 字段的 `@Inferred`
- 预期：存在 `@Inferred`，reason 含「字段含义」
- 断言：`assertThat(inferred.reason()).contains("字段含义")`
- 基准：meta | spec | green

#### shouldThrowWhenRequiredFieldMissing_whenFlightIdNull
- 输入：`{"task_type":0}`（缺 flight_id）
- 预期：反序列化抛 `NullPointerException`，message 含「flightId 必填」
- 断言：`assertThatThrownBy(() -> MessageCodec.fromJson(json, FlighttaskPrepareRequest.class)).isInstanceOf(NullPointerException.class).hasMessageContaining("flightId 必填")`
- 基准：char | spec | green

### 11.4 FlighttaskPrepareReplyTest (`@Tag("spec")`)（空 Reply）

#### shouldHaveZeroComponents_whenEmptyReply
- 输入：`FlighttaskPrepareReply.class.getRecordComponents()`
- 预期：空数组
- 断言：`assertThat(components).isEmpty()`
- 基准：char | spec | green

#### shouldBeAnnotatedWithVerified_whenClassLevel
- 输入：类级 `@Verified`
- 预期：basis 含「无 output」
- 断言：`assertThat(v.basis()).contains("无 output")`
- 基准：meta | spec | green

### 11.5 FlighttaskExecuteRequestTest (`@Tag("spec")`)（multi_dock_task 复杂嵌套模板）

#### shouldHave2Components_whenRecord
- 输入：`FlighttaskExecuteRequest.class.getRecordComponents()`
- 预期：`flightId, multiDockTask`
- 断言：`assertThat(names).containsExactly("flightId","multiDockTask")`
- 基准：doc:dock.html | spec | green

#### shouldDeserializeSnakeCase_whenMultiDockTaskNested
- 输入：`{"flight_id":"f1","multi_dock_task":{"wireless_link_topo":{"secret_code":"sc","center_node":{"sn":"sn1"},"leaf_nodes":[]},"dock_infos":[{"sn":"d1","dock_type":"1","index":0,"latitude":22.0,"longitude":113.0,"height":50.0}]}}`
- 预期：`flightId()="f1"`, `multiDockTask().dockInfos().get(0).sn()="d1"`
- 断言：`assertThat(req.flightId()).isEqualTo("f1"); assertThat(req.multiDockTask().dockInfos().get(0).sn()).isEqualTo("d1")`
- 基准：doc:dock.html | spec | green

#### shouldSerializeToSnakeCase_whenMultiDockTaskGiven
- 输入：含 MultiDockTask 的 FlighttaskExecuteRequest
- 预期：JSON 含 `"flight_id":"f1"`,`"multi_dock_task":{"wireless_link_topo":{`
- 断言：`assertThat(json).contains("\"flight_id\":\"f1\"", "\"multi_dock_task\":{")`
- 基准：doc:dock.html | spec | green

#### shouldBeAnnotatedWithVerified_whenClassLevel
- 输入：类级 `@Verified`
- 预期：basis 含 "handleExecute"
- 断言：`assertThat(v.basis()).contains("handleExecute")`
- 基准：meta | spec | green

#### shouldThrowWhenRequiredFieldMissing_whenFlightIdNull
- 输入：`{"multi_dock_task":null}`（缺 flight_id）
- 预期：反序列化抛 `NullPointerException`，message 含「flightId 必填」
- 断言：`assertThatThrownBy(() -> MessageCodec.fromJson(json, FlighttaskExecuteRequest.class)).isInstanceOf(NullPointerException.class).hasMessageContaining("flightId 必填")`
- 基准：char | spec | green

### 11.6 FlighttaskExecuteReplyTest (`@Tag("spec")`)（空 Reply）

#### shouldHaveZeroComponents_whenEmptyReply
- 输入：`FlighttaskExecuteReply.class.getRecordComponents()`
- 预期：空数组
- 断言：`assertThat(components).isEmpty()`
- 基准：char | spec | green

#### shouldBeAnnotatedWithVerified_whenClassLevel
- 输入：类级 `@Verified`
- 预期：basis 含「无 output」与「flight_task_progress」
- 断言：`assertThat(v.basis()).contains("无 output", "flight_task_progress")`
- 基准：meta | spec | green

### 11.7 DroneOpenRequestTest (`@Tag("spec")`)（无参数模板）

#### shouldHaveZeroComponents_whenEmptyRequest
- 输入：`DroneOpenRequest.class.getRecordComponents()`
- 预期：空数组（无参数指令）
- 断言：`assertThat(components).isEmpty()`
- 基准：char | spec | green

#### shouldBeAnnotatedWithVerified_whenClassLevel
- 输入：类级 `@Verified`
- 预期：basis 含 "RemoteDebugSimulator" 与「无请求参数」
- 断言：`assertThat(v.basis()).contains("RemoteDebugSimulator", "无请求参数")`
- 基准：meta | spec | green

### 11.8 DroneOpenReplyTest (`@Tag("spec")`)（@Inferred 待确认模板）

#### shouldHaveZeroComponents_whenEmptyReply
- 输入：`DroneOpenReply.class.getRecordComponents()`
- 预期：空数组
- 断言：`assertThat(components).isEmpty()`
- 基准：char | spec | green

#### shouldBeAnnotatedWithInferred_whenClassLevel
- 输入：`DroneOpenReply.class.getAnnotation(Inferred.class)`
- 预期：存在，reason 含「output 结构」与「未明确」
- 断言：`assertThat(i.reason()).contains("output 结构", "未明确")`
- 基准：meta | spec | green

### 11.9 TakeoffToPointRequestTest (`@Tag("spec")`)（异步双阶段多字段模板）

#### shouldHave14Components_whenRecord
- 输入：`TakeoffToPointRequest.class.getRecordComponents()`
- 预期：14 个组件，含 `flightId, maxSpeed, targetLatitude, targetLongitude, targetHeight, securityTakeoffHeight, simulateMission` 等
- 断言：`assertThat(components).hasSize(14); assertThat(names).contains("flightId","targetLatitude","simulateMission")`
- 基准：doc:dock.html | spec | green

#### shouldDeserializeSnakeCase_whenJsonGiven
- 输入：`{"flight_id":"f1","max_speed":10,"target_latitude":22.0,"target_longitude":113.0,"target_height":50.0,"security_takeoff_height":30.0}`
- 预期：`flightId()="f1"`, `maxSpeed()=10`, `targetLatitude()=22.0`, `securityTakeoffHeight()=30.0`
- 断言：`assertThat(req.flightId()).isEqualTo("f1"); assertThat(req.maxSpeed()).isEqualTo(10); assertThat(req.securityTakeoffHeight()).isEqualTo(30.0)`
- 基准：doc:dock.html | spec | green

#### shouldSerializeToSnakeCase_whenRecordGiven
- 输入：`new TakeoffToPointRequest("f1",10,22.0,113.0,50.0,...)`
- 预期：JSON 含 `"flight_id":"f1"`,`"max_speed":10`,`"target_latitude":22.0`,`"security_takeoff_height":30.0`
- 断言：`assertThat(json).contains("\"flight_id\":\"f1\"", "\"max_speed\":10", "\"security_takeoff_height\":30.0")`
- 基准：doc:dock.html | spec | green

#### shouldBeAnnotatedWithVerified_whenClassLevel
- 输入：类级 `@Verified`
- 预期：basis 含 "FlightCommandSimulator.handleTakeoffToPoint"
- 断言：`assertThat(v.basis()).contains("FlightCommandSimulator")`
- 基准：meta | spec | green

#### shouldThrowWhenRequiredFieldMissing_whenFlightIdNull
- 输入：`{"max_speed":10,"target_latitude":22.0,"target_longitude":113.0,"target_height":50.0}`（缺 flight_id）
- 预期：反序列化抛 `NullPointerException`，message 含「flightId 必填」
- 断言：`assertThatThrownBy(() -> MessageCodec.fromJson(json, TakeoffToPointRequest.class)).isInstanceOf(NullPointerException.class).hasMessageContaining("flightId 必填")`
- 基准：char | spec | green

#### shouldThrowWhenRequiredFieldMissing_whenTargetLatitudeNull
- 输入：`{"flight_id":"f1","target_longitude":113.0,"target_height":50.0}`（缺 target_latitude）
- 预期：反序列化抛 `NullPointerException`，message 含「targetLatitude 必填」
- 断言：`assertThatThrownBy(() -> MessageCodec.fromJson(json, TakeoffToPointRequest.class)).isInstanceOf(NullPointerException.class).hasMessageContaining("targetLatitude 必填")`
- 基准：char | spec | green

### 11.10 TakeoffToPointReplyTest (`@Tag("spec")`)（空 Reply）

#### shouldHaveZeroComponents_whenEmptyReply
- 输入：`TakeoffToPointReply.class.getRecordComponents()`
- 预期：空数组
- 断言：`assertThat(components).isEmpty()`
- 基准：char | spec | green

#### shouldBeAnnotatedWithVerified_whenClassLevel
- 输入：类级 `@Verified`
- 预期：basis 含「无 output」与「takeoff_to_point_progress」
- 断言：`assertThat(v.basis()).contains("无 output", "takeoff_to_point_progress")`
- 基准：meta | spec | green

---

## 12. command.drc 包

> 测试模式：DRC 上行推送 11 个 POJO 反序列化 + DRC 下行控制 5 子包（camera/flight/light/safety/speaker）POJO + `DrcUpMethod` 枚举双向映射
>
> **已实现状态**（2/~41）：
> - ✅ `DrcUpPushDataParseTest`（11 个 DRC 上行推送 POJO 反序列化验证）
> - ✅ `DrcUpMethodTest`（`DrcUpMethod` 枚举 method 字符串↔枚举双向映射）
> - ⬜ DRC 下行控制 5 子包 POJO 待实现（camera 5 + flight 5 + light 5 + safety 1 + speaker 6）

### 12.1 DrcUpPushDataParseTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[DrcUpPushDataParseTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/up/DrcUpPushDataParseTest.java)
> 覆盖：11 个 DRC 上行推送 POJO（OsdInfoPushData/HsiInfoPushData/DroneStatePushData/CameraOsdInfoPushData/CameraStatePushData/CameraPhotoInfoPushData/DelayInfoPushData/PsdkStateInfoData/PsdkUiResourceData/PsdkFloatingWindowTextData/SpeakerPlayProgressData）反序列化

### 12.2 DrcUpMethodTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[DrcUpMethodTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/command/drc/up/DrcUpMethodTest.java)
> 覆盖：`DrcUpMethod` 枚举 method 字符串↔枚举双向映射

### 12.3-12.4 DRC 下行控制 POJO — ⬜ 待实现

- 基准：doc:drc.html | spec | green
- 测试模式：与 command.service 一致（组件数 / snake_case 反序列化 / 序列化 / 注解 / 往返）
- 子包：camera（5）/ flight（5：DroneControlRequest/StickControlRequest/HeartBeatRequest/Reply）/ light（5）/ speaker（6）

---

## 13. command.event 包

> 测试模式：events 通道 POJO 反序列化（含 system/flight/wayline/media/alert/speaker/psdk/esdk/flightarea 子包）+ 进度上报通用结构 + `EventMethod` 枚举
>
> **已实现状态**（2/~46）：
> - ✅ `EventSupplementParseTest`（events 通道补充解析）
> - ✅ `AirSenseWarningDataTest`（[测试文件](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/command/event/alert/AirSenseWarningDataTest.java)，AirSenseWarningData 反序列化 + 自定义 Deserializer）
> - ⬜ 其余 ~44 个 event POJO 待实现

### 13.1 EventSupplementParseTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[EventSupplementParseTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/command/event/EventSupplementParseTest.java)
> 覆盖：events 通道补充解析

### 13.2 AirSenseWarningDataTest (`@Tag("spec")`) — ✅ 已实现

> **已实现**：[AirSenseWarningDataTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/command/event/alert/AirSenseWarningDataTest.java)
> 覆盖：AirSenseWarningData 反序列化 + 自定义 AirSenseWarningDataDeserializer

### 13.3-13.10 其余 event POJO — ⬜ 待实现

- 基准：doc:dock3/events.html | spec | green
- 子包：system（4：OtaProgressData/FileuploadProgressData/ServiceProgressData）/ flight（10：CameraPhotoTakeProgressData/FlyToPointProgressData/TakeoffToPointProgressData/PhotoProgressData/PoiCircleStatusData/ObstacleAvoidanceNotifyData/JoystickInvalidNotifyData）/ wayline（6：FlighttaskProgressData/FlighttaskReadyData/InFlightWaylineProgressData/ReturnHomeInfoData/DeviceExitHomingNotifyData）/ media（3）/ alert（5）/ speaker（4）/ psdk（4）/ esdk（2）/ flightarea（3）
- **CameraPhotoTakeProgressDataTest** 已实现（[测试文件](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/command/event/flight/CameraPhotoTakeProgressDataTest.java)，ext.cameraMode 反序列化 + 嵌套结构）

---

## 14. command.property 包

> 测试模式：`property/set` 通道 `PropertySetRequest`/`PropertySetReply`/`PropertySetResult` + `PropertySetMethod` 枚举 15 个可设置属性
>
> **已实现状态**（0/4）：全部待实现

### 14.1-14.4 PropertySet 相关 — ⬜ 待实现

- 基准：doc:dock3/property-set.html | spec | green
- 测试模式：`PropertySetMethod` 枚举 15 个可设置属性 method 字符串↔枚举双向映射、`PropertySetRequest` 批量属性设置反序列化、`PropertySetReply`/`PropertySetResult` 回复解析

---

## 15. command.request 包

> 测试模式：requests 通道 POJO（config/flightarea/registration/wayline 子包）+ `RequestsMethod` 枚举
>
> **已实现状态**（0/~18）：全部待实现

### 15.1-15.4 Requests 相关 — ⬜ 待实现

- 基准：doc:dock3/requests.html | spec | green
- 子包：config（3：StorageConfigGetRequest/Reply）/ flightarea（3：FlightAreasGetRequest/Reply）/ registration（8：ConfigRequest/Reply/AirportBindStatusRequest/Reply/AirportOrganizationGetRequest/Reply/AirportOrganizationBindRequest/Reply）/ wayline（4：FlighttaskProgressGetRequest/Reply/FlighttaskResourceGetRequest/Reply）
- 测试模式：`RequestsMethod` 枚举双向映射 + 各 POJO snake_case 反序列化

---

## 16. command.status 包

> 测试模式：`update_topo` 通道 `UpdateTopoData`/`UpdateTopoReplyData` + `StatusMethod` 枚举
>
> **已实现状态**（0/3）：全部待实现

### 16.1-16.3 Status 相关 — ⬜ 待实现

- 基准：doc:dock3/status.html | spec | green
- 测试模式：`StatusMethod` 枚举双向映射 + `UpdateTopoData`/`UpdateTopoReplyData` 反序列化

---

## 17. http 包

> 测试模式：`HttpApiPath` 路径常量 + `StsCredentials` record + `HttpResponseEnvelope` 信封
>
> **已实现状态**（0/3）：全部待实现

### 17.1-17.3 HTTP 相关 — ⬜ 待实现

- 基准：doc:server-api.html | spec | green
- 测试模式：`HttpApiPath` 路径常量验证、`StsCredentials` record 组件 + JSON 往返、`HttpResponseEnvelope` 信封结构

---

## 18. websocket 包

> 测试模式：`WsBizCode` 枚举 + `WsPushMessage` 信封 + `DeviceOsdPushData`/`MapElementPushData` 等推送 POJO
>
> **已实现状态**（0/8）：全部待实现

### 18.1-18.4 WebSocket 相关 — ⬜ 待实现

- 基准：doc:server-api/websocket.html | spec | green
- 测试模式：`WsBizCode` 枚举 biz_code 字符串↔枚举双向映射、`WsPushMessage` 信封字段提取、推送 POJO 反序列化

---

## 19. capture 包

> 测试模式：`CaptureRecorder` 录制行为 + `CaptureConfig` 配置项
>
> **已实现状态**（1/3）：
> - ✅ `CaptureRecorderTest`（[测试文件](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/capture/CaptureRecorderTest.java)，录制行为特征化）

### 19.1 CaptureRecorderTest (`@Tag("characterization")`) — ✅ 已实现

> **已实现**：[CaptureRecorderTest.java](../src/test/java/ltd/cdmi/dji/cloudapi/sdk/capture/CaptureRecorderTest.java)
> 覆盖：录制行为特征化锁定

---

## 第三批汇总（command 子包 + http + websocket + capture）

- **新增章节**：§12 command.drc + §13 command.event + §14 command.property + §15 command.request + §16 command.status + §17 http + §18 websocket + §19 capture
- **§11 command.service 测试类数**：10 规划 + 3 已实现（ServiceSupplementParseTest/ServiceParamParseTest/CameraModeSwitchRequestTest）
- **§12 command.drc 已实现**：2（DrcUpPushDataParseTest/DrcUpMethodTest），覆盖 11 个上行推送 POJO
- **§13 command.event 已实现**：2+1（EventSupplementParseTest/AirSenseWarningDataTest/CameraPhotoTakeProgressDataTest）
- **§14-§16 command 子包**：全部待实现（property 4 + request ~18 + status 3）
- **§17-§19 http/websocket/capture**：capture 已实现 1（CaptureRecorderTest），http/websocket 全部待实现
- **command.service 覆盖指令**：5 个示范指令（flighttask_prepare/execute + live_start_push + drone_open + takeoff_to_point）
- **@Inferred 待验证项**：3 个（DroneOpenReply output 结构、LeafNode 字段、WirelessLinkTopo.secretCode 类型、SimulateMission.altitude 字段一致性）
- **空 Reply 对称性**：4 个空 Reply（FlighttaskPrepare/Execute/LiveStartPush/TakeoffToPoint）+ 1 个 @Inferred 空 Reply（DroneOpen）
- **嵌套 record 覆盖**：10 个嵌套 record（FlighttaskFile/ReadyConditions/ExecutableConditions/BreakPoint/SimulateMission/MultiDockTask/WirelessLinkTopo/CenterNode/LeafNode/DockInfo）

---

## 第二批汇总

- **测试类数**：15（model 6 + flow 4 + telemetry 5）
- **`@Disabled` 标红项**：1（DeviceCompatibility #3：SMART_CONTROLLER_ENTERPRISE switch 未覆盖）
- **特征化锁定 #1 bug 行为**：DroneOsd/ControllerOsd/DockOsd 各有 `shouldDeserializeSnakeCaseJson_whenAllFieldsNull`，锁定"snake_case 反序列化字段为 null"的当前行为（修复后需更新为正确映射断言）
- **DeviceModelProvider 契约**：由 DroneModel/DockModel/ControllerModel 3 个测试类的 `shouldImplementDeviceModelProvider` + `shouldReturnToModel_*` 覆盖
- **全矩阵参数化**：DeviceCompatibility 的 dock(3×14=42) 与 controller(4×14=56) 兼容矩阵全覆盖

---

## 总汇总（第一批 + 第二批 + 第三批）

| 维度 | 数量 |
|---|---|
| 测试类规划总数 | 49+（v1 spec 34 + characterization 15 + v2 新增 §12-§19 规划） |
| **已实现测试类** | **21**（telemetry.enumtype 12 + command.drc 2 + command.event 3 + command.service 3 + capture 1） |
| `@Disabled` 标红项 | 0（#1 MessageCodec 已修复、#3 DeviceCompatibility 已修复） |
| 已修复项验证 | #1（MessageCodec SNAKE_CASE）、#2（PositionState/DroneChargeState fromCode）、#3（DeviceCompatibility SMART_CONTROLLER_ENTERPRISE）、#4（TopicBuilder @Deprecated）、#5（codec package-info 哨兵）、#6（OsdField/StateField fromFieldName） |
| 历史缺陷防回归 | Gear 10 档、DroneModeCode/DockModeCode 拆分 |
| 特征化锁定 #1 行为 | 3 个 OSD record 测试（修复后改为正确映射断言） |
| command.service 包新增 | 10 个测试类（5 Request + 5 Reply，含 10 嵌套 record 覆盖） |
| **v2 新增章节** | §12-§19（command.drc/event/property/request/status + http + websocket + capture） |
| **v2 源文件覆盖** | 285+ 个编译单元（v1 的 40 类扩展 + command/capture/http/websocket 新增包） |

- **覆盖源类**：285+ 个编译单元（v2 扩展后，16 顶层包）
- **待实现优先级**：P3 为 v1 规划的 10 个待实现测试类（Gear/DroneModeCode/DockModeCode/PositionState/DroneChargeState + AirConditionerState/CoverState/DrcState/FlighttaskStepCode/RcLostAction），P4 为 §12-§19 新增包的测试类
