package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;

/**
 * @author sali
 */
public abstract class TableContentBuilder<T> extends BlockBuilder<T> {

    protected TableContentBuilder(Builder parent, T source, int indexInParent) {
        super(parent, source, indexInParent);
    }

}
