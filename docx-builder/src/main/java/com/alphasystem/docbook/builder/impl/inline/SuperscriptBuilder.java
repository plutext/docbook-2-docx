package com.alphasystem.docbook.builder.impl.inline;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.InlineBuilder;
import org.docbook.model.Superscript;

import static com.alphasystem.docbook.builder.DocumentBuilderHelper.SUPERSCRIPT;

/**
 * @author sali
 */
public class SuperscriptBuilder extends InlineBuilder<Superscript> {

    public SuperscriptBuilder(Builder parent, Superscript superscript) {
        super(parent, superscript);
    }

    @Override
    protected void initContent() {
        style = SUPERSCRIPT;
        content = source.getContent();
    }
}
