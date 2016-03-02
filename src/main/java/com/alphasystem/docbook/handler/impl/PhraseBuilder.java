package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import org.docbook.model.Phrase;
import org.docx4j.wml.R;

/**
 * @author sali
 */
public class PhraseBuilder extends RunBuilder<Phrase> {

    public PhraseBuilder(Builder parent, Phrase phrase) {
        super(parent, phrase);
    }

    @Override
    public R build(R r) {
        return buildRun(r, obj.getRole(), obj.getContent());
    }
}
