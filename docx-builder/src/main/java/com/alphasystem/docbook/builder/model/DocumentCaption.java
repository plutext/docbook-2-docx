package com.alphasystem.docbook.builder.model;

import com.alphasystem.asciidoc.model.AsciiDocumentInfo;
import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.openxml.builder.wml.HeadingList;
import org.docbook.model.Example;

import static com.alphasystem.docbook.util.ConfigurationUtils.getInstance;
import static java.lang.String.format;

/**
 * @author sali
 */
public abstract class DocumentCaption extends HeadingList<DocumentCaption> {

    public static final DocumentCaption EXAMPLE = new DocumentCaption(getInstance().getString(format("%s.title", Example.class.getName()))) {

        @Override
        public String getValue(int i) {
            final AsciiDocumentInfo documentInfo = ApplicationController.getContext().getDocumentInfo();
            return format("%s %%%s.", documentInfo.getExampleCaption(), i);
        }

        @Override
        public String getName() {
            return "EXAMPLE";
        }


    };

    DocumentCaption(String styleName) {
        super(styleName);
    }

    @Override
    public long getLeftIndent(int i) {
        return 432;
    }

}
