package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import com.alphasystem.docbook.handler.BuilderFactory;
import org.docx4j.wml.R;

import static com.alphasystem.openxml.builder.wml.WmlAdapter.getText;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getRBuilder;

/**
 * @author sali
 */
public class TextBuilder extends RunBuilder<String> {

    public TextBuilder(Builder parent, String text) {
        super(parent, text);
    }

    @Override
    public R build(R r) {
        return getRBuilder(r).addContent(getText(obj)).getObject();
    }
}
