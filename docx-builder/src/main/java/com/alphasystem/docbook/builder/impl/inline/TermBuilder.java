package com.alphasystem.docbook.builder.impl.inline;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.InlineBuilder;
import org.docbook.model.Term;

/**
 * @author sali
 */
public class TermBuilder extends InlineBuilder<Term> {

    public TermBuilder(Builder parent, Term term) {
        super(parent, term);
    }

    @Override
    protected void initContent() {
        style = "Strong";
        content = source.getContent();
    }
}
