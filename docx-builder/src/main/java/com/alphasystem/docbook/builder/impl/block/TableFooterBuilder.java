package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import org.docbook.model.TableFooter;

import java.util.ArrayList;

/**
 * @author sali
 */
public class TableFooterBuilder extends BlockBuilder<TableFooter> {

    public TableFooterBuilder(Builder parent, TableFooter source, int indexInParent) {
        super(parent, source, indexInParent);
    }

    @Override
    protected void initContent() {
        content = new ArrayList<>(source.getRow());
    }
}
