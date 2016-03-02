package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import org.docbook.model.Literal;
import org.docx4j.wml.R;

import static com.alphasystem.docbook.handler.DocumentHandlerHelper.LITERAL;

/**
 * @author sali
 */
public class LiteralBuilder extends RunBuilder<Literal> {

    public LiteralBuilder(Builder parent, Literal literal) {
        super(parent, literal);
    }

    @Override
    public R build(R r) {
        return buildRun(r, LITERAL, obj.getContent());
    }
}
