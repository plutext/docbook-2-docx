package com.alphasystem.docbook.builder.impl.inline;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.InlineBuilder;
import org.docx4j.wml.R;

import java.util.Collections;
import java.util.List;

import static com.alphasystem.openxml.builder.wml.WmlAdapter.getText;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getRBuilder;

/**
 * @author sali
 */
public class TextBuilder extends InlineBuilder<String> {

    public TextBuilder(Builder parent, String text, int indexInParent) {
        super(parent, text, indexInParent);
    }

    @Override
    protected void initContent() {
    }

    @Override
    protected List<R> processContent() {
        return Collections.singletonList(getRBuilder().addContent(getText(source)).getObject());
    }

}
