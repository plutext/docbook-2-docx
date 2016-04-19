package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import com.alphasystem.docbook.util.ColumnSpecAdapter;
import com.alphasystem.openxml.builder.wml.TrBuilder;
import com.alphasystem.openxml.builder.wml.WmlBuilderFactory;
import org.docbook.model.Row;

import java.util.List;

import static com.alphasystem.util.AppUtil.isInstanceOf;
import static java.util.Collections.singletonList;

/**
 * @author sali
 */
public class RowBuilder extends BlockBuilder<Row> {

    protected ColumnSpecAdapter columnSpecAdapter;

    public RowBuilder(Builder parent, Row source, int indexInParent) {
        super(parent, source, indexInParent);
    }

    @Override
    protected void initContent() {
        content = source.getContent();
    }

    @Override
    protected Builder getChildBuilder(Object o, int index) {
        final Builder childBuilder = super.getChildBuilder(o, index);
        if(isInstanceOf(EntryBuilder.class, childBuilder)) {
            ((EntryBuilder) childBuilder).setColumnSpecAdapter(columnSpecAdapter);
        }
        return childBuilder;
    }

    @Override
    protected List<Object> postProcess(List<Object> processedTitleContent, List<Object> processedChildContent) {
        final TrBuilder trBuilder = WmlBuilderFactory.getTrBuilder();
        processedChildContent.forEach(o -> trBuilder.addContent(o));
        return singletonList(trBuilder.getObject());
    }

    public void setColumnSpecAdapter(ColumnSpecAdapter columnSpecAdapter) {
        this.columnSpecAdapter = columnSpecAdapter;
    }
}
