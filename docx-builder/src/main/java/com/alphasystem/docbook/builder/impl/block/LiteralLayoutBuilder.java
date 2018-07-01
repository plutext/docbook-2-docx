package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import org.docbook.model.Para;

/**
 * @author sali
 */
public class LiteralLayoutBuilder extends AbstractParaBuilder<org.docbook.model.LiteralLayout> {

    public LiteralLayoutBuilder(Builder parent, org.docbook.model.LiteralLayout obj, int indexInParent) {
        super(parent, obj, indexInParent);
        this.role = source.getRole();
    }

    @Override
    protected void initContent() {
        content = source.getContent();
    }

}
