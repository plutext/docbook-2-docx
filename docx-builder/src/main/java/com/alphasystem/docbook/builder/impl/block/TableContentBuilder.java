package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import com.alphasystem.docbook.util.ColumnSpecAdapter;

/**
 * @author sali
 */
public abstract class TableContentBuilder<T> extends BlockBuilder<T> {

    protected ColumnSpecAdapter columnSpecAdapter;

    protected TableContentBuilder(Builder parent, T source, int indexInParent) {
        super(parent, source, indexInParent);
    }

    public ColumnSpecAdapter getColumnSpecAdapter() {
        return columnSpecAdapter;
    }

    public void setColumnSpecAdapter(ColumnSpecAdapter columnSpecAdapter) {
        this.columnSpecAdapter = columnSpecAdapter;
    }
}
