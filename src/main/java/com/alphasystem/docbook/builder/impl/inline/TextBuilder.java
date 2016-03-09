package com.alphasystem.docbook.builder.impl.inline;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.InlineBuilder;
import com.alphasystem.openxml.builder.wml.WmlBuilderFactory;
import org.docx4j.wml.R;

import static com.alphasystem.openxml.builder.wml.WmlAdapter.getText;

/**
 * @author sali
 */
public class TextBuilder extends InlineBuilder<String> {

    public TextBuilder(Builder parent, String text) {
        super(parent, text);
    }

    @Override
    protected void initContent() {

    }

    @Override
    protected R handleChildContent() {
        return WmlBuilderFactory.getRBuilder().withRPr(WmlBuilderFactory.getRPrBuilder().getObject())
                .addContent(getText(source)).getObject();
    }

}
