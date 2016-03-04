package com.alphasystem.xml;

import com.alphasystem.asciidoc.model.AsciiDocumentInfo;
import com.alphasystem.docbook.DocumentContext;
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

    public static <T> T unmarshal(String systemId, Class<T> declaredType) throws IOException,
            JAXBException, SAXException, ParserConfigurationException {
        T document;
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

            document = (T) unmarshallerHandler.getResult();
        }
        return document;
    }

    public static <T> DocumentContext unmarshal2(String systemId, Class<T> declaredType) throws IOException,
            JAXBException, SAXException, ParserConfigurationException {
        DocumentContext documentContext;
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

            AsciiDocumentInfo documentInfo = new AsciiDocumentInfo();
            unmarshallerHandler.getProcessingInstructions().forEach(pi -> populateProcessingInstructions(pi, documentInfo));
            final Object document = unmarshallerHandler.getResult();
            documentContext = new DocumentContext(documentInfo, document);
        }
        return documentContext;
    }

    private static void populateProcessingInstructions(ProcessingInstruction pi, AsciiDocumentInfo documentInfo) {
        final String target = pi.getTarget();
        switch (target) {
            case "asciidoc-toc":
                documentInfo.setToc(true);
                break;
            case "asciidoc-numbered":
                documentInfo.setSectionNumbers(true);
                break;
            default:
                LOGGER.warn("unhandled processing instruction {}", target);
                break;
        }
    }
}
