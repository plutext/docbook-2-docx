package com.alphasystem.docbook.builder.impl.inline;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.InlineBuilder;
import org.docbook.model.Phrase;

/**
 * @author sali
 */
public class PhraseBuilder extends InlineBuilder<Phrase> {

    public PhraseBuilder(Builder parent, Phrase phrase) {
        super(parent, phrase);
    }

    @Override
    protected void initContent() {
        style = source.getRole();
        content = source.getContent();
    }

}
