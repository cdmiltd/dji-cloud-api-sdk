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

import java.io.OutputStream;
import java.io.Writer;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

/**
 * 包装 {@link XMLOutputFactory}，在创建 {@link XMLStreamWriter} 时
 * 注入 {@link WpmlStreamWriter} 以统一 WPML 命名空间前缀。
 *
 * <p>所有方法均委托给底层工厂，仅 {@code createXMLStreamWriter} 系列
 * 方法的返回值被 {@link WpmlStreamWriter} 包装。
 *
 * @see WpmlStreamWriter
 * @see WpmlCodec
 */
final class WpmlOutputFactory extends XMLOutputFactory {

    private final XMLOutputFactory delegate;

    WpmlOutputFactory(XMLOutputFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(Writer writer) throws XMLStreamException {
        return WpmlStreamWriter.wrap(delegate.createXMLStreamWriter(writer));
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(OutputStream stream) throws XMLStreamException {
        return WpmlStreamWriter.wrap(delegate.createXMLStreamWriter(stream));
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return WpmlStreamWriter.wrap(delegate.createXMLStreamWriter(stream, encoding));
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        return WpmlStreamWriter.wrap(delegate.createXMLStreamWriter(result));
    }

    @Override
    public XMLEventWriter createXMLEventWriter(Writer writer) throws XMLStreamException {
        return delegate.createXMLEventWriter(writer);
    }

    @Override
    public XMLEventWriter createXMLEventWriter(OutputStream stream) throws XMLStreamException {
        return delegate.createXMLEventWriter(stream);
    }

    @Override
    public XMLEventWriter createXMLEventWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return delegate.createXMLEventWriter(stream, encoding);
    }

    @Override
    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return delegate.createXMLEventWriter(result);
    }

    @Override
    public void setProperty(String name, Object value) throws IllegalArgumentException {
        delegate.setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return delegate.getProperty(name);
    }

    @Override
    public boolean isPropertySupported(String name) {
        return delegate.isPropertySupported(name);
    }
}
