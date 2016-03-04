package com.alphasystem.docbook.model;

import com.alphasystem.asciidoc.model.AsciiDocumentInfo;

/**
 * @author sali
 */
public class DocBookDocumentInfo extends AsciiDocumentInfo {

    public DocBookDocumentInfo() {
        super();
    }

    public DocBookDocumentInfo(AsciiDocumentInfo src) throws IllegalArgumentException {
        super(src);
    }
}
