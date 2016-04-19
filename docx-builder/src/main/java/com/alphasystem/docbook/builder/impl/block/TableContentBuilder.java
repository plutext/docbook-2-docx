package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import com.alphasystem.docbook.util.ColumnSpecAdapter;

import static com.alphasystem.util.AppUtil.isInstanceOf;

/**
 * @author sali
 */
public abstract class TableContentBuilder<T> extends BlockBuilder<T> {

    protected ColumnSpecAdapter columnSpecAdapter;

    protected TableContentBuilder(Builder parent, T source, int indexInParent) {
        super(parent, source, indexInParent);
    }

    public void setColumnSpecAdapter(ColumnSpecAdapter columnSpecAdapter) {
        this.columnSpecAdapter = columnSpecAdapter;
    }

    @Override
    protected Builder getChildBuilder(Object o, int index) {
        final Builder childBuilder = super.getChildBuilder(o, index);
        if(isInstanceOf(RowBuilder.class, childBuilder)) {
            ((RowBuilder) childBuilder).setColumnSpecAdapter(columnSpecAdapter);
        }
        return childBuilder;
    }
}
