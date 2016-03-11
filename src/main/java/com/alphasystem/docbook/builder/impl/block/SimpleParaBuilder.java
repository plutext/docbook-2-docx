package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import org.docbook.model.SimplePara;

/**
 * @author sali
 */
public class SimpleParaBuilder extends AbstractParaBuilder<SimplePara> {

    public SimpleParaBuilder(Builder parent, SimplePara simplePara) {
        super(parent, simplePara);
    }

    @Override
    protected void initContent() {
        content = source.getContent();
    }

}