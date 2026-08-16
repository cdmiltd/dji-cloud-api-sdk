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
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamResult;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WpmlNamespaces;

/**
 * {@link WpmlOutputFactory} 单元测试。
 *
 * <p>验证 createXMLStreamWriter 系列方法返回被 {@link WpmlStreamWriter} 包装的 writer
 * （输出中使用 {@code wpml:} 前缀），createXMLEventWriter 系列方法直接委托给底层工厂，
 * 以及 setProperty / getProperty / isPropertySupported 的委托行为。
 */
@DisplayName("WpmlOutputFactory 单元测试")
class WpmlOutputFactoryTest {

    /** 创建启用命名空间修复的标准 XMLOutputFactory */
    private static XMLOutputFactory newDelegate() {
        XMLOutputFactory factory = XMLOutputFactory.newFactory();
        factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        return factory;
    }

    @Test
    @DisplayName("createXMLStreamWriter(Writer) 返回的 writer 使用 wpml: 前缀")
    void shouldWrapStreamWriterForWriter() throws XMLStreamException {
        WpmlOutputFactory factory = new WpmlOutputFactory(newDelegate());
        StringWriter sw = new StringWriter();

        XMLStreamWriter writer = factory.createXMLStreamWriter(sw);
        writeWpmlSample(writer);

        String xml = sw.toString();
        assertTrue(xml.contains("wpml:action"), "WPML 元素应使用 wpml: 前缀");
        assertTrue(xml.contains("xmlns:wpml"), "应声明 xmlns:wpml");
    }

    @Test
    @DisplayName("createXMLStreamWriter(OutputStream) 返回的 writer 使用 wpml: 前缀")
    void shouldWrapStreamWriterForOutputStream() throws XMLStreamException {
        WpmlOutputFactory factory = new WpmlOutputFactory(newDelegate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLStreamWriter writer = factory.createXMLStreamWriter(baos);
        writeWpmlSample(writer);

        String xml = baos.toString();
        assertTrue(xml.contains("wpml:action"), "WPML 元素应使用 wpml: 前缀");
    }

    @Test
    @DisplayName("createXMLStreamWriter(OutputStream, encoding) 返回的 writer 使用 wpml: 前缀")
    void shouldWrapStreamWriterForOutputStreamWithEncoding() throws XMLStreamException {
        WpmlOutputFactory factory = new WpmlOutputFactory(newDelegate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLStreamWriter writer = factory.createXMLStreamWriter(baos, "UTF-8");
        writeWpmlSample(writer);

        String xml = baos.toString();
        assertTrue(xml.contains("wpml:action"), "WPML 元素应使用 wpml: 前缀");
    }

    @Test
    @DisplayName("createXMLStreamWriter(Result) 返回的 writer 使用 wpml: 前缀")
    void shouldWrapStreamWriterForResult() throws XMLStreamException {
        WpmlOutputFactory factory = new WpmlOutputFactory(newDelegate());
        StringWriter sw = new StringWriter();

        XMLStreamWriter writer = factory.createXMLStreamWriter(new StreamResult(sw));
        writeWpmlSample(writer);

        String xml = sw.toString();
        assertTrue(xml.contains("wpml:action"), "WPML 元素应使用 wpml: 前缀");
    }

    @Test
    @DisplayName("createXMLEventWriter(Writer) 直接委托给底层工厂")
    void shouldDelegateEventWriterForWriter() throws XMLStreamException {
        XMLOutputFactory delegate = newDelegate();
        WpmlOutputFactory factory = new WpmlOutputFactory(delegate);
        StringWriter sw = new StringWriter();

        XMLEventWriter eventWriter = factory.createXMLEventWriter(sw);
        assertNotNull(eventWriter, "createXMLEventWriter 不应返回 null");
    }

    @Test
    @DisplayName("createXMLEventWriter(OutputStream) 直接委托给底层工厂")
    void shouldDelegateEventWriterForOutputStream() throws XMLStreamException {
        XMLOutputFactory delegate = newDelegate();
        WpmlOutputFactory factory = new WpmlOutputFactory(delegate);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLEventWriter eventWriter = factory.createXMLEventWriter(baos);
        assertNotNull(eventWriter, "createXMLEventWriter 不应返回 null");
    }

    @Test
    @DisplayName("createXMLEventWriter(OutputStream, encoding) 直接委托给底层工厂")
    void shouldDelegateEventWriterForOutputStreamWithEncoding() throws XMLStreamException {
        XMLOutputFactory delegate = newDelegate();
        WpmlOutputFactory factory = new WpmlOutputFactory(delegate);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XMLEventWriter eventWriter = factory.createXMLEventWriter(baos, "UTF-8");
        assertNotNull(eventWriter, "createXMLEventWriter 不应返回 null");
    }

    @Test
    @DisplayName("createXMLEventWriter(Result) 直接委托给底层工厂")
    void shouldDelegateEventWriterForResult() throws XMLStreamException {
        XMLOutputFactory delegate = newDelegate();
        WpmlOutputFactory factory = new WpmlOutputFactory(delegate);
        StringWriter sw = new StringWriter();

        XMLEventWriter eventWriter = factory.createXMLEventWriter(new StreamResult(sw));
        assertNotNull(eventWriter, "createXMLEventWriter 不应返回 null");
    }

    @Test
    @DisplayName("setProperty / getProperty 委托给底层工厂")
    void shouldDelegatePropertyAccess() {
        XMLOutputFactory delegate = XMLOutputFactory.newFactory();
        WpmlOutputFactory factory = new WpmlOutputFactory(delegate);

        factory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
        Object value = factory.getProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES);

        assertTrue((Boolean) value, "IS_REPAIRING_NAMESPACES 应为 true");
    }

    @Test
    @DisplayName("isPropertySupported 委托给底层工厂")
    void shouldDelegateIsPropertySupported() {
        XMLOutputFactory delegate = XMLOutputFactory.newFactory();
        WpmlOutputFactory factory = new WpmlOutputFactory(delegate);

        assertTrue(factory.isPropertySupported(XMLOutputFactory.IS_REPAIRING_NAMESPACES),
            "IS_REPAIRING_NAMESPACES 应被支持");
        assertFalse(factory.isPropertySupported("non.existent.property"),
            "不存在的属性应返回 false");
    }

    /** 写入一段包含 WPML 命名空间元素的 XML 用于验证 wpml: 前缀 */
    private static void writeWpmlSample(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement(WpmlNamespaces.KML, "kml");
        writer.writeStartElement(WpmlNamespaces.WPML, "action");
        writer.writeAttribute(WpmlNamespaces.WPML, "actionId", "0");
        writer.writeEndElement();
        writer.writeEndElement();
        writer.flush();
    }
}
