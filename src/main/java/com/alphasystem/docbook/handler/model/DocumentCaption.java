package com.alphasystem.docbook.handler.model;

import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.openxml.builder.wml.AbstractListItem;
import com.alphasystem.xml.DocumentInfo;
import org.docx4j.wml.NumberFormat;

import static com.alphasystem.docbook.ApplicationController.getInstance;
import static java.lang.String.format;
import static org.docx4j.wml.NumberFormat.DECIMAL;

/**
 * @author sali
 */
public abstract class DocumentCaption extends AbstractListItem<DocumentCaption> {

    public static final DocumentCaption EXAMPLE = new DocumentCaption(12,
            getInstance().getConfiguration().getString("example-title"), DECIMAL) {

        @Override
        public String getValue(int i) {
            final DocumentInfo documentInfo = ApplicationController.getContext().getDocumentInfo();
            return format("%s %%%s.", documentInfo.getExampleCaption(), i);
        }

        @Override
        public DocumentCaption getNext() {
            return null;
        }
    };

    DocumentCaption(int numberId, String styleName, NumberFormat numberFormat) {
        super(numberId, styleName, numberFormat);
    }

    @Override
    public boolean linkStyle() {
        return true;
    }

    @Override
    public long getLeftIndent(int i) {
        return 432;
    }

    @Override
    public String getMultiLevelType() {
        return "multilevel";
    }
}
