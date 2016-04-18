package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import org.docbook.model.Row;

/**
 * @author sali
 */
public class RowBuilder extends BlockBuilder<Row> {

    public RowBuilder(Builder parent, Row source, int indexInParent) {
        super(parent, source, indexInParent);
    }

    @Override
    protected void initContent() {
        content = source.getContent();
    }
}
