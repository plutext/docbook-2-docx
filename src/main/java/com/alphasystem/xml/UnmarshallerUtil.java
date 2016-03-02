package com.alphasystem.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Paths.get;

/**
 * @author sali
 */
public class UnmarshallerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnmarshallerUtil.class);

    public static <T> DocumentInfo<T> unmarshal(String systemId, Class<T> declaredType) throws IOException,
            JAXBException, SAXException, ParserConfigurationException {
        DocumentInfo documentInfo;
        try (InputStream inputStream = newInputStream(get(systemId))) {
            JAXBContext jc = JAXBContext.newInstance(declaredType);
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            MyUnmarshallerHandlerWrapper unmarshallerHandler = new MyUnmarshallerHandlerWrapper(
                    unmarshaller.getUnmarshallerHandler());

            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);

            XMLReader xmlReader = spf.newSAXParser().getXMLReader();
            xmlReader.setContentHandler(unmarshallerHandler);
            xmlReader.parse(new InputSource(inputStream));

            documentInfo = new DocumentInfo();
            documentInfo.setDocument(unmarshallerHandler.getResult());
            unmarshallerHandler.getProcessingInstructions().forEach(pi -> populateProcessingInstructions(pi, documentInfo));
        }
        return documentInfo;
    }

    private static void populateProcessingInstructions(ProcessingInstruction pi, DocumentInfo documentInfo) {
        final String target = pi.getTarget();
        switch (target) {
            case "asciidoc-toc":
                documentInfo.setToc(true);
                break;
            case "asciidoc-numbered":
                documentInfo.setNumbered(true);
                break;
            default:
                LOGGER.warn("unhandled processing instruction {}", target);
                break;
        }
    }
}
