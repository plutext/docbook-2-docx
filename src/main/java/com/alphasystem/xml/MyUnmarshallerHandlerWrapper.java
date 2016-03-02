package com.alphasystem.xml;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshallerHandler;
import java.util.ArrayList;
import java.util.List;


public class MyUnmarshallerHandlerWrapper implements UnmarshallerHandler {

    private List<ProcessingInstruction> processingInstructions;

    private UnmarshallerHandler handle;

    public MyUnmarshallerHandlerWrapper(UnmarshallerHandler handle) {
        this.handle = handle;
        processingInstructions = new ArrayList<>();
    }

    public List<ProcessingInstruction> getProcessingInstructions() {
        return processingInstructions;
    }

    @Override
    public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
        handle.characters(arg0, arg1, arg2);
    }

    @Override
    public void endDocument() throws SAXException {
        handle.endDocument();
    }

    @Override
    public void endElement(String arg0, String arg1, String arg2)
            throws SAXException {
        handle.endElement(arg0, arg1, arg2);
    }

    @Override
    public void endPrefixMapping(String arg0) throws SAXException {
        handle.endPrefixMapping(arg0);
    }

    @Override
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
            throws SAXException {
        handle.ignorableWhitespace(arg0, arg1, arg2);
    }

    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        processingInstructions.add(new ProcessingInstruction(target, data));
    }

    @Override
    public void setDocumentLocator(Locator arg0) {
        handle.setDocumentLocator(arg0);
    }

    @Override
    public void skippedEntity(String arg0) throws SAXException {
        handle.skippedEntity(arg0);
    }

    @Override
    public void startDocument() throws SAXException {
        handle.startDocument();
    }

    @Override
    public void startElement(String arg0, String arg1, String arg2,
                             Attributes arg3) throws SAXException {
        handle.startElement(arg0, arg1, arg2, arg3);
    }

    @Override
    public void startPrefixMapping(String arg0, String arg1)
            throws SAXException {
        handle.startPrefixMapping(arg0, arg1);
    }

    @Override
    public Object getResult() throws JAXBException, IllegalStateException {
        return handle.getResult();
    }

}
