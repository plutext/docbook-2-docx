package com.alphasystem.docbook.builder.impl.inline;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.InlineBuilder;
import org.docbook.model.Literal;

import static com.alphasystem.docbook.builder.DocumentBuilderHelper.LITERAL;

/**
 * @author sali
 */
public class LiteralBuilder extends InlineBuilder<Literal> {

    public LiteralBuilder(Builder parent, Literal literal) {
        super(parent, literal);
    }

    @Override
    protected void initContent() {
        style = LITERAL;
        content = source.getContent();
    }

}
