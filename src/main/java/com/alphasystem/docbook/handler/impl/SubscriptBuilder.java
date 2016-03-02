package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import org.docbook.model.Subscript;
import org.docx4j.wml.R;

import static com.alphasystem.docbook.handler.DocumentHandlerHelper.SUBSCRIPT;

/**
 * @author sali
 */
public class SubscriptBuilder extends RunBuilder<Subscript> {

    public SubscriptBuilder(Builder parent, Subscript subscript) {
        super(parent, subscript);
    }

    @Override
    public R build(R r) {
        return buildRun(r, SUBSCRIPT, obj.getContent());
    }
}
