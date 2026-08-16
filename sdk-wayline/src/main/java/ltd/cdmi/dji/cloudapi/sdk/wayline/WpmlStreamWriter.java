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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import ltd.cdmi.dji.cloudapi.sdk.wayline.model.WpmlNamespaces;

/**
 * 统一 WPML 命名空间前缀为 {@code wpml:} 的 {@link XMLStreamWriter} 包装器。
 *
 * <p>Woodstox 的命名空间修复机制会为每个 WPML 命名空间元素自动分配
 * {@code wstxns1:}、{@code wstxns2:} 等不同前缀。本包装器拦截
 * {@code writeStartElement}、{@code writeEmptyElement} 和 {@code writeAttribute}
 * 调用，当目标命名空间为 {@link WpmlNamespaces#WPML} 时，显式传递 {@code wpml}
 * 前缀，由 StAX 引擎自动在根元素声明 {@code xmlns:wpml="..."}。
 *
 * <p><b>实现方式：</b>使用 JDK 动态代理。代理自动继承底层 writer 的所有接口
 * （包括 Woodstox 的 {@code org.codehaus.stax2.XMLStreamWriter2}），确保
 * Jackson XML 能识别为 StAX2 兼容实现。仅拦截 6 个命名空间相关方法，
 * 其余方法通过反射委托给底层 writer。
 *
 * <p>JDK 虽提供了 {@code javax.xml.stream.util.StreamWriterDelegate} 基类，
 * 但它仅实现 {@code XMLStreamWriter} 接口，不会暴露 Woodstox 的
 * {@code XMLStreamWriter2} 扩展接口（如缩进功能）。手动实现
 * {@code XMLStreamWriter2} 的 50+ 个方法不现实，因此选择动态代理方案，
 * 可自动继承底层 writer 的所有接口。
 *
 * @see WpmlCodec
 * @see WpmlNamespaces#WPML
 */
final class WpmlStreamWriter {

    /** WPML 命名空间的标准前缀 */
    private static final String WPML_PREFIX = "wpml";

    /** StAX2 扩展接口，类加载时一次性查找并缓存（运行期不变） */
    private static final Class<?> STAX2_CLASS = lookupStax2Class();

    private WpmlStreamWriter() {
    }

    /**
     * 包装 {@link XMLStreamWriter}，统一 WPML 命名空间前缀。
     *
     * <p>代理声明实现 {@code XMLStreamWriter} 和 {@code XMLStreamWriter2}
     * （如底层 writer 支持），确保 Jackson XML 的 StAX2 功能（如缩进）正常工作。
     * 不收集底层 writer 的所有接口，避免引入对类加载器不可见的 Woodstox 内部接口
     * （如 {@code ValidationContext}）。
     *
     * @param delegate 底层 XMLStreamWriter（通常是 Woodstox 实现）
     * @return 包装后的 XMLStreamWriter
     */
    static XMLStreamWriter wrap(XMLStreamWriter delegate) {
        Set<Class<?>> interfaces = new HashSet<>();
        interfaces.add(XMLStreamWriter.class);

        // 检查 delegate 是否实现了 StAX2 的 XMLStreamWriter2 接口
        if (STAX2_CLASS != null && STAX2_CLASS.isInstance(delegate)) {
            interfaces.add(STAX2_CLASS);
        }

        return (XMLStreamWriter) Proxy.newProxyInstance(
            WpmlStreamWriter.class.getClassLoader(),
            interfaces.toArray(new Class<?>[0]),
            new WpmlInvocationHandler(delegate));
    }

    /**
     * 通过反射查找 {@code org.codehaus.stax2.XMLStreamWriter2} 接口。
     *
     * <p>该接口是 StAX2 扩展 API，由 {@code stax2-api} jar 提供，
     * 通过 Woodstox 传递依赖引入。如果 classpath 上不存在，返回 {@code null}。
     */
    private static Class<?> lookupStax2Class() {
        try {
            return Class.forName("org.codehaus.stax2.XMLStreamWriter2");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * 动态代理调用处理器。
     *
     * <p>拦截 6 个命名空间相关方法，将 WPML 命名空间的前缀替换为 {@code wpml}。
     * 其余方法直接委托给底层 writer。
     */
    private static final class WpmlInvocationHandler implements InvocationHandler {

        private final XMLStreamWriter delegate;
        /** 标记根元素是否已写入，用于在根元素上预声明 xmlns:wpml */
        private boolean rootElementWritten;

        WpmlInvocationHandler(XMLStreamWriter delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();

            // 拦截第一个 writeStartElement（根元素）：写入后立即声明 xmlns:wpml，
            // 使后续 WPML 元素复用该前缀，避免每个元素重复声明
            if ("writeStartElement".equals(methodName) && !rootElementWritten) {
                rootElementWritten = true;
                try {
                    // 先执行 writeStartElement
                    if (args != null && args.length > 0) {
                        String namespaceURI = resolveNamespaceURI(methodName, args);
                        if (namespaceURI != null && WpmlNamespaces.WPML.equals(namespaceURI)) {
                            interceptWpmlCall(methodName, args, namespaceURI);
                        } else {
                            method.invoke(delegate, args);
                        }
                    } else {
                        method.invoke(delegate, args);
                    }
                    // 在根元素上声明 wpml 命名空间
                    delegate.writeNamespace(WPML_PREFIX, WpmlNamespaces.WPML);
                    return null;
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }

            if (args != null && args.length > 0) {
                String namespaceURI = resolveNamespaceURI(methodName, args);

                if (namespaceURI != null && WpmlNamespaces.WPML.equals(namespaceURI)) {
                    return interceptWpmlCall(methodName, args, namespaceURI);
                }
            }

            // 非 WPML 方法：直接委托给底层 writer
            try {
                return method.invoke(delegate, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }

        /**
         * 从方法参数中提取命名空间 URI。
         *
         * <pre>
         * writeStartElement(String namespaceURI, String localName)                    → args[0]
         * writeStartElement(String prefix, String localName, String namespaceURI)     → args[2]
         * writeEmptyElement(String namespaceURI, String localName)                    → args[0]
         * writeEmptyElement(String prefix, String localName, String namespaceURI)     → args[2]
         * writeAttribute(String namespaceURI, String localName, String value)         → args[0]
         * writeAttribute(String prefix, String namespaceURI, String localName, String value) → args[1]
         * </pre>
         */
        private String resolveNamespaceURI(String methodName, Object[] args) {
            switch (methodName) {
                case "writeStartElement":
                case "writeEmptyElement":
                    if (args.length == 2 && args[0] instanceof String) {
                        return (String) args[0];
                    }
                    if (args.length == 3 && args[2] instanceof String) {
                        return (String) args[2];
                    }
                    return null;

                case "writeAttribute":
                    if (args.length == 3 && args[0] instanceof String) {
                        return (String) args[0];
                    }
                    if (args.length == 4 && args[1] instanceof String) {
                        return (String) args[1];
                    }
                    return null;

                default:
                    return null;
            }
        }

        /**
         * 拦截 WPML 命名空间调用，显式传递 {@code wpml} 前缀。
         */
        private Object interceptWpmlCall(String methodName, Object[] args, String namespaceURI)
                throws XMLStreamException {
            switch (methodName) {
                case "writeStartElement":
                case "writeEmptyElement":
                    // 统一调用三参数版本：(prefix, localName, namespaceURI)
                    if (args.length == 2) {
                        // (namespaceURI, localName) → (wpml, localName, namespaceURI)
                        if ("writeStartElement".equals(methodName)) {
                            delegate.writeStartElement(WPML_PREFIX, (String) args[1], namespaceURI);
                        } else {
                            delegate.writeEmptyElement(WPML_PREFIX, (String) args[1], namespaceURI);
                        }
                    } else {
                        // (prefix, localName, namespaceURI) → (wpml, localName, namespaceURI)
                        if ("writeStartElement".equals(methodName)) {
                            delegate.writeStartElement(WPML_PREFIX, (String) args[1], namespaceURI);
                        } else {
                            delegate.writeEmptyElement(WPML_PREFIX, (String) args[1], namespaceURI);
                        }
                    }
                    return null;

                case "writeAttribute":
                    if (args.length == 3) {
                        // (namespaceURI, localName, value) → (wpml, namespaceURI, localName, value)
                        delegate.writeAttribute(WPML_PREFIX, namespaceURI,
                            (String) args[1], (String) args[2]);
                    } else {
                        // (prefix, namespaceURI, localName, value) → (wpml, namespaceURI, localName, value)
                        delegate.writeAttribute(WPML_PREFIX, namespaceURI,
                            (String) args[2], (String) args[3]);
                    }
                    return null;

                default:
                    return null;
            }
        }
    }
}
