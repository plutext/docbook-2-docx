package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import org.docbook.model.TableHeader;

import java.util.ArrayList;

/**
 * @author sali
 */
public class TableHeaderBuilder extends BlockBuilder<TableHeader> {

    public TableHeaderBuilder(Builder parent, TableHeader source, int indexInParent) {
        super(parent, source, indexInParent);
    }

    @Override
    protected void initContent() {
        content = new ArrayList<>(source.getRow());
    }
}
