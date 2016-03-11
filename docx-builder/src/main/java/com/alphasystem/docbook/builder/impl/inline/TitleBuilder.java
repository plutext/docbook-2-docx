package com.alphasystem.docbook.builder.impl.inline;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.InlineBuilder;
import org.docbook.model.Title;

/**
 * @author sali
 */
public class TitleBuilder extends InlineBuilder<Title> {

    public TitleBuilder(Builder parent, Title title) {
        super(parent, title);
    }

    @Override
    protected void initContent() {
        content = source.getContent();
    }

}
