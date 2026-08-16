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

import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WpmlNamespaces;

/**
 * {@link WpmlStreamWriter} 单元测试。
 *
 * <p>验证 {@link WpmlStreamWriter#wrap(XMLStreamWriter)} 返回的代理 writer：
 * <ul>
 *   <li>WPML 命名空间元素使用 {@code wpml:} 前缀</li>
 *   <li>WPML 命名空间属性使用 {@code wpml:} 前缀</li>
 *   <li>根元素声明 {@code xmlns:wpml}</li>
 *   <li>非 WPML 命名空间元素（如 KML）不使用 {@code wpml:} 前缀</li>
 *   <li>{@code writeEmptyElement} 也正确使用 {@code wpml:} 前缀</li>
 * </ul>
 */
@DisplayName("WpmlStreamWriter 单元测试")
class WpmlStreamWriterTest {

    /** WPML 命名空间 URI */
    private static final String WPML_NS = WpmlNamespaces.WPML;
    /** KML 命名空间 URI */
    private static final String KML_NS = WpmlNamespaces.KML;

    /** 创建启用命名空间修复的标准 XMLOutputFactory */
    private static XMLOutputFactory newFactory() {
        XMLOutputFactory factory = XMLOutputFactory.newFactory();
        factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        return factory;
    }

    @Test
    @DisplayName("wrap 返回非 null 的 XMLStreamWriter")
    void shouldReturnNonNullWriter() throws XMLStreamException {
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = WpmlStreamWriter.wrap(newFactory().createXMLStreamWriter(sw));

        assertNotNull(writer, "wrap 不应返回 null");
    }

    @Test
    @DisplayName("WPML 命名空间元素使用 wpml: 前缀")
    void shouldUseWpmlPrefixForWpmlElements() throws XMLStreamException {
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = WpmlStreamWriter.wrap(newFactory().createXMLStreamWriter(sw));

        writer.writeStartElement(KML_NS, "kml");
        writer.writeStartElement(WPML_NS, "action");
        writer.writeCharacters("content");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.flush();

        String xml = sw.toString();
        assertTrue(xml.contains("<wpml:action"), "WPML 元素应使用 <wpml:action 前缀");
        assertTrue(xml.contains("</wpml:action>"), "WPML 结束标签应使用 wpml: 前缀");
    }

    @Test
    @DisplayName("WPML 命名空间属性使用 wpml: 前缀")
    void shouldUseWpmlPrefixForWpmlAttributes() throws XMLStreamException {
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = WpmlStreamWriter.wrap(newFactory().createXMLStreamWriter(sw));

        writer.writeStartElement(KML_NS, "kml");
        writer.writeStartElement(WPML_NS, "action");
        writer.writeAttribute(WPML_NS, "actionId", "0");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.flush();

        String xml = sw.toString();
        assertTrue(xml.contains("wpml:actionId=\"0\""), "WPML 属性应使用 wpml: 前缀");
    }

    @Test
    @DisplayName("根元素声明 xmlns:wpml 命名空间")
    void shouldDeclareWpmlNamespaceOnRootElement() throws XMLStreamException {
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = WpmlStreamWriter.wrap(newFactory().createXMLStreamWriter(sw));

        writer.writeStartElement(KML_NS, "kml");
        writer.writeEndElement();
        writer.flush();

        String xml = sw.toString();
        assertTrue(xml.contains("xmlns:wpml=\"" + WPML_NS + "\""),
            "根元素应声明 xmlns:wpml 命名空间");
    }

    @Test
    @DisplayName("非 WPML 命名空间元素不使用 wpml: 前缀")
    void shouldNotUseWpmlPrefixForNonWpmlElements() throws XMLStreamException {
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = WpmlStreamWriter.wrap(newFactory().createXMLStreamWriter(sw));

        writer.writeStartElement(KML_NS, "kml");
        writer.writeStartElement(KML_NS, "Document");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.flush();

        String xml = sw.toString();
        assertFalse(xml.contains("<wpml:kml"), "KML 根元素不应使用 wpml: 前缀");
        assertFalse(xml.contains("<wpml:Document"), "KML 子元素不应使用 wpml: 前缀");
    }

    @Test
    @DisplayName("writeEmptyElement 对 WPML 命名空间使用 wpml: 前缀")
    void shouldUseWpmlPrefixForEmptyElement() throws XMLStreamException {
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = WpmlStreamWriter.wrap(newFactory().createXMLStreamWriter(sw));

        writer.writeStartElement(KML_NS, "kml");
        writer.writeEmptyElement(WPML_NS, "action");
        writer.writeEndElement();
        writer.flush();

        String xml = sw.toString();
        assertTrue(xml.contains("<wpml:action"), "WPML 空元素应使用 wpml: 前缀");
    }

    @Test
    @DisplayName("三参数 writeStartElement(prefix, localName, namespaceURI) 也统一为 wpml: 前缀")
    void shouldOverridePrefixToWpmlForThreeArgWriteStartElement() throws XMLStreamException {
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = WpmlStreamWriter.wrap(newFactory().createXMLStreamWriter(sw));

        writer.writeStartElement(KML_NS, "kml");
        // 即使传入其他前缀，WPML 命名空间元素也应统一为 wpml:
        writer.writeStartElement("wstxns1", "action", WPML_NS);
        writer.writeEndElement();
        writer.writeEndElement();
        writer.flush();

        String xml = sw.toString();
        assertTrue(xml.contains("<wpml:action"), "三参数版本也应统一为 wpml: 前缀");
        assertFalse(xml.contains("wstxns1:action"), "不应出现 wstxns1 前缀");
    }

    @Test
    @DisplayName("四参数 writeAttribute(prefix, namespaceURI, localName, value) 也统一为 wpml: 前缀")
    void shouldOverridePrefixToWpmlForFourArgWriteAttribute() throws XMLStreamException {
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = WpmlStreamWriter.wrap(newFactory().createXMLStreamWriter(sw));

        writer.writeStartElement(KML_NS, "kml");
        writer.writeStartElement(WPML_NS, "action");
        writer.writeAttribute("wstxns1", WPML_NS, "actionId", "0");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.flush();

        String xml = sw.toString();
        assertTrue(xml.contains("wpml:actionId=\"0\""), "四参数版本也应统一为 wpml: 前缀");
        assertFalse(xml.contains("wstxns1:actionId"), "不应出现 wstxns1 前缀属性");
    }

    @Test
    @DisplayName("根元素为 WPML 命名空间时也正确使用 wpml: 前缀")
    void shouldUseWpmlPrefixWhenRootIsWpmlNamespace() throws XMLStreamException {
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = WpmlStreamWriter.wrap(newFactory().createXMLStreamWriter(sw));

        writer.writeStartElement(WPML_NS, "missionConfig");
        writer.writeEndElement();
        writer.flush();

        String xml = sw.toString();
        assertTrue(xml.contains("<wpml:missionConfig"), "WPML 根元素应使用 wpml: 前缀");
        assertTrue(xml.contains("xmlns:wpml"), "应声明 xmlns:wpml");
    }

    @Test
    @DisplayName("嵌套 WPML 元素均使用 wpml: 前缀")
    void shouldUseWpmlPrefixForNestedWpmlElements() throws XMLStreamException {
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = WpmlStreamWriter.wrap(newFactory().createXMLStreamWriter(sw));

        writer.writeStartElement(KML_NS, "kml");
        writer.writeStartElement(WPML_NS, "Document");
        writer.writeStartElement(WPML_NS, "Folder");
        writer.writeStartElement(WPML_NS, "Placemark");
        writer.writeCharacters("wp");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndElement();
        writer.flush();

        String xml = sw.toString();
        assertTrue(xml.contains("<wpml:Document"), "第一层 WPML 元素应使用 wpml: 前缀");
        assertTrue(xml.contains("<wpml:Folder"), "第二层 WPML 元素应使用 wpml: 前缀");
        assertTrue(xml.contains("<wpml:Placemark"), "第三层 WPML 元素应使用 wpml: 前缀");
    }

    @Test
    @DisplayName("WPML 和非 WPML 元素混合时各自使用正确前缀")
    void shouldUseCorrectPrefixForMixedNamespaces() throws XMLStreamException {
        StringWriter sw = new StringWriter();
        XMLStreamWriter writer = WpmlStreamWriter.wrap(newFactory().createXMLStreamWriter(sw));

        writer.writeStartElement(KML_NS, "kml");
        writer.writeStartElement(KML_NS, "Document");
        writer.writeStartElement(WPML_NS, "missionConfig");
        writer.writeCharacters("cfg");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndElement();
        writer.flush();

        String xml = sw.toString();
        assertFalse(xml.contains("<wpml:kml"), "KML kml 元素不应使用 wpml: 前缀");
        assertFalse(xml.contains("<wpml:Document"), "KML Document 元素不应使用 wpml: 前缀");
        assertTrue(xml.contains("<wpml:missionConfig"), "WPML missionConfig 应使用 wpml: 前缀");
    }
}
