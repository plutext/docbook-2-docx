package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.model.Admonition;
import org.docbook.model.Caution;

/**
 * @author sali
 */
public class CautionBuilder extends AdmonitionBuilder<Caution> {

    public CautionBuilder(Builder parent, Caution caution) {
        super(parent, caution, Admonition.CAUTION);
    }

    @Override
    protected void initContent() {
        titleContent = source.getTitleContent();
        content = source.getContent();
    }
}
