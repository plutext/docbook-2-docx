package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import com.alphasystem.docbook.handler.BuilderFactory;
import org.docbook.model.Superscript;
import org.docx4j.wml.R;

import static com.alphasystem.docbook.handler.DocumentHandlerHelper.SUPERSCRIPT;

/**
 * @author sali
 */
public class SuperscriptBuilder extends RunBuilder<Superscript> {

    public SuperscriptBuilder(Builder parent, Superscript superscript) {
        super(parent, superscript);
    }

    @Override
    public R build(R r) {
        return buildRun(r, SUPERSCRIPT, obj.getContent());
    }
}
