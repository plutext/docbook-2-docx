package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import org.docbook.model.Table;
import org.docbook.model.TableGroup;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * @author sali
 */
public class TableBuilder extends AbstractTableBuilder<Table> {

    public TableBuilder(Builder parent, Table source, int indexInParent) {
        super(parent, source, indexInParent);
    }

    @Override
    protected void initContent() {
        final List<TableGroup> tableGroup = source.getTableGroup();
        if (tableGroup != null && !tableGroup.isEmpty()) {
            initializeTableAdapter(tableGroup.get(0), source.getFrame(), source.getTableStyle());
        }
        titleContent = singletonList(source.getTitle());
        // TODO:
        content = emptyList();
    }
}
