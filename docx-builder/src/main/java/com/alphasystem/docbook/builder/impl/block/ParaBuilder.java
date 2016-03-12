package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import org.docbook.model.Para;

/**
 * @author sali
 */
public class ParaBuilder extends BlockBuilder<Para> {

    public ParaBuilder(Builder parent, Para obj) {
        super(parent, obj);
    }

    @Override
    protected void initContent() {
        content = source.getContent();
    }

}
