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

package ltd.cdmi.dji.cloudapi.sdk.wayline;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.stream.XMLOutputFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import ltd.cdmi.dji.cloudapi.sdk.annotation.DocUrl;
import ltd.cdmi.dji.cloudapi.sdk.annotation.Verified;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Folder;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.Kml;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.KmzContent;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.ParsedKmz;
import ltd.cdmi.dji.cloudapi.sdk.wayline.model.execute.ExecuteFolder;

/**
 * WPML template.kml 的 XML 序列化工具。
 *
 * <p>基于 Jackson {@link XmlMapper}，将 {@link ltd.cdmi.dji.cloudapi.sdk.wayline.model.Kml}
 * POJO 树序列化为符合 DJI WPML 规范的 XML 字符串。
 *
 * <p>对标 {@link ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec} 的设计模式：
 * <ul>
 *   <li>静态 {@link XmlMapper} 单例，配置完成后线程安全</li>
 *   <li>方法均为静态方法，可被直接调用</li>
 *   <li>异常包装为 {@link IllegalStateException}</li>
 * </ul>
 *
 * <p>XmlMapper 配置：
 * <ul>
 *   <li>{@link SerializationFeature#INDENT_OUTPUT} — 格式化输出（缩进）</li>
 *   <li>{@link ToXmlGenerator.Feature#WRITE_XML_DECLARATION} — 输出 XML 声明
 *       {@code <?xml version="1.0" encoding="UTF-8"?>}</li>
 *   <li>{@link JsonInclude.Include#NON_NULL} — 排除 null 字段</li>
 * </ul>
 *
 * <p>命名空间处理：POJO 上的 {@code @JacksonXmlProperty(namespace = ...)} 注解
 * 声明元素所属命名空间。通过 {@link WpmlOutputFactory} + {@link WpmlStreamWriter}
 * 在流式写入时拦截 WPML 命名空间元素，显式传递 {@code wpml} 前缀，
 * 由 StAX 引擎的命名空间修复机制自动在根元素声明
 * {@code xmlns:wpml="http://www.dji.com/wpmz/1.0.2"}。
 *
 * @see ltd.cdmi.dji.cloudapi.sdk.wayline.model.WpmlNamespaces
 * @see ltd.cdmi.dji.cloudapi.sdk.codec.MessageCodec
 */
@DocUrl("https://developer.dji.com/doc/cloud-api-tutorial/cn/api-reference/dji-wpml/template-kml.html")
@Verified(basis = "DJI WPML template.kml 文档 XML 格式规范")
public final class WpmlCodec {

    /** 内部持有的 XmlMapper 实例（配置后线程安全） */
    private static final XmlMapper MAPPER = createMapper();

    /** template.kml 反序列化目标类型（Kml<Folder>），不可变，类加载时缓存 */
    private static final JavaType TEMPLATE_KML_TYPE =
        MAPPER.getTypeFactory().constructParametricType(Kml.class, Folder.class);

    /** waylines.wpml 反序列化目标类型（Kml<ExecuteFolder>），不可变，类加载时缓存 */
    private static final JavaType WAYLINES_WPML_TYPE =
        MAPPER.getTypeFactory().constructParametricType(Kml.class, ExecuteFolder.class);

    private WpmlCodec() {
    }

    private static XmlMapper createMapper() {
        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 获取底层 StAX 输出工厂，启用命名空间修复
        XMLOutputFactory outputFactory = mapper.getFactory().getXMLOutputFactory();
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);

        // 用 WpmlOutputFactory 包装，统一 WPML 命名空间前缀为 wpml:
        ((XmlFactory) mapper.getFactory()).setXMLOutputFactory(new WpmlOutputFactory(outputFactory));

        return mapper;
    }

    /**
     * 序列化为 XML 字符串。
     *
     * @param obj 待序列化对象（通常是 {@link ltd.cdmi.dji.cloudapi.sdk.wayline.model.Kml}）
     * @return XML 字符串，含 XML 声明和格式化缩进
     * @throws IllegalStateException 如果 XML 序列化失败
     */
    public static String toXml(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new IllegalStateException("XML 序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 template.kml 和 waylines.wpml 打包为 DJI KMZ（ZIP）格式。
     *
     * <p>KMZ 内部结构符合 DJI WPML 规范：
     * <pre>
     * *.kmz
     * └── wpmz
     *     ├── template.kml
     *     └── waylines.wpml
     * </pre>
     *
     * <p>调用方可自行将 {@code byte[]} 写入 {@code .kmz} 文件，SDK 不负责文件保存。
     *
     * @param templateKml template.kml XML 字符串（由 {@code toXml()} 生成）
     * @param waylinesWpml waylines.wpml XML 字符串（由 {@code toWpml()} 生成）
     * @return KMZ 字节流
     * @throws IllegalStateException 如果 ZIP 打包失败
     */
    public static byte[] toKmz(String templateKml, String waylinesWpml) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            addZipEntry(zos, "wpmz/template.kml", templateKml);
            addZipEntry(zos, "wpmz/waylines.wpml", waylinesWpml);
        } catch (IOException e) {
            throw new IllegalStateException("KMZ 打包失败: " + e.getMessage(), e);
        }
        return baos.toByteArray();
    }

    private static void addZipEntry(ZipOutputStream zos, String path, String content) throws IOException {
        zos.putNextEntry(new ZipEntry(path));
        zos.write(content.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    // ==================== 反序列化（解析） ====================

    /**
     * 将 XML 字符串反序列化为 POJO。
     *
     * @param xml  XML 字符串
     * @param type 目标类型
     * @param <T>  目标类型
     * @return 反序列化后的 POJO
     * @throws IllegalStateException 如果 XML 反序列化失败
     */
    public static <T> T fromXml(String xml, Class<T> type) {
        try {
            return MAPPER.readValue(xml, type);
        } catch (Exception e) {
            throw new IllegalStateException("XML 反序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解包 KMZ（ZIP），返回 {@code wpmz/template.kml} 和 {@code wpmz/waylines.wpml} 的原始 XML 字符串。
     *
     * @param kmz KMZ 字节流
     * @return 包含两个 XML 字符串的容器
     * @throws IllegalArgumentException 如果 KMZ 不是有效 ZIP 或缺少必需文件
     */
    public static KmzContent fromKmz(byte[] kmz) {
        String kml = null;
        String wpml = null;
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(kmz))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String content = new String(zis.readAllBytes(), StandardCharsets.UTF_8);
                if ("wpmz/template.kml".equals(entry.getName())) {
                    kml = content;
                }
                if ("wpmz/waylines.wpml".equals(entry.getName())) {
                    wpml = content;
                }
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

    /**
     * 解析 template.kml XML 字符串为 {@link Kml}{@code <}{@link Folder}{@code >} POJO。
     *
     * @param xml template.kml XML 字符串
     * @return 解析后的 POJO
     * @throws IllegalStateException 如果反序列化失败
     */
    public static Kml<Folder> parseTemplateKml(String xml) {
        try {
            return MAPPER.readValue(xml, TEMPLATE_KML_TYPE);
        } catch (Exception e) {
            throw new IllegalStateException("template.kml 反序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析 waylines.wpml XML 字符串为 {@link Kml}{@code <}{@link ExecuteFolder}{@code >} POJO。
     *
     * @param xml waylines.wpml XML 字符串
     * @return 解析后的 POJO
     * @throws IllegalStateException 如果反序列化失败
     */
    public static Kml<ExecuteFolder> parseWaylinesWpml(String xml) {
        try {
            return MAPPER.readValue(xml, WAYLINES_WPML_TYPE);
        } catch (Exception e) {
            throw new IllegalStateException("waylines.wpml 反序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析 KMZ 字节流为 {@link ParsedKmz} POJO 容器。
     *
     * <p>等价于先 {@link #fromKmz(byte[])} 解包，再分别
     * {@link #parseTemplateKml(String)} / {@link #parseWaylinesWpml(String)} 反序列化。
     *
     * @param kmz KMZ 字节流
     * @return 包含 template 和 waylines POJO 的容器
     * @throws IllegalArgumentException 如果 KMZ 解包失败
     * @throws IllegalStateException 如果 XML 反序列化失败
     */
    public static ParsedKmz parseKmz(byte[] kmz) {
        KmzContent content = fromKmz(kmz);
        return new ParsedKmz(
            parseTemplateKml(content.templateKml()),
            parseWaylinesWpml(content.waylinesWpml())
        );
    }
}
