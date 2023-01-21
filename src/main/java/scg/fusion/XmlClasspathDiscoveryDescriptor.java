package scg.fusion;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import org.jdom2.input.SAXBuilder;
import scg.fusion.exceptions.FusionRuntimeException;
import scg.fusion.exceptions.IllegalContractException;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.nonNull;

public final class XmlClasspathDiscoveryDescriptor {

    private static final String descriptorFileName = "fusion-discovery.xml";
    private static final String rootElementName = "discover";
    private static final String childElementName = "found";

    private XmlClasspathDiscoveryDescriptor() {
        throw new UnsupportedOperationException();
    }

    static void accept(ComponentDiscovery discovery) {

        Thread thread = Thread.currentThread();

        ClassLoader classLoader = thread.getContextClassLoader();

        InputStream descriptorStream = classLoader.getResourceAsStream(descriptorFileName);

        if (nonNull(descriptorStream)) try {

            SAXBuilder builder = new SAXBuilder();

            Element root = builder.build(descriptorStream).getRootElement();

            if (!rootElementName.equals(root.getName())) {
                throw new IllegalContractException("Cannot find discovery descriptor root element ['%s']", rootElementName);
            }

            for (Element child : root.getChildren(childElementName)) {

                String componentType = child.getValue();

                discovery.found(Class.forName(componentType));

            }

            return;

        } catch (IOException | JDOMException | ClassNotFoundException cause) {
            throw new FusionRuntimeException(cause) {
                @Override
                public String getMessage() {
                    return "An error has occurred during discovery descriptor parsing";
                }
            };
        } finally {
            try {
                descriptorStream.close();
            } catch (IOException cause) {
                throw new RuntimeException(cause);
            }
        }

        throw new IllegalContractException("Discovery descriptor file [%s] not found", descriptorFileName);

    }
}
