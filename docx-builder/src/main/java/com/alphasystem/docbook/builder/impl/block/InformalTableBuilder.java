package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import org.docbook.model.InformalTable;
import org.docbook.model.TableGroup;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author sali
 */
public class InformalTableBuilder extends AbstractTableBuilder<InformalTable> {

    public InformalTableBuilder(Builder parent, InformalTable informalTable, int indexInParent) {
        super(parent, informalTable, indexInParent);
    }

    @Override
    protected void initContent() {
        final List<TableGroup> tableGroup = source.getTableGroup();
        if (tableGroup != null && !tableGroup.isEmpty()) {
            initializeTableAdapter(tableGroup.get(0), source.getFrame(), source.getTableStyle());
        }
        content = emptyList();
    }

}
