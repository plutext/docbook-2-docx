package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import org.docbook.model.VariableList;

import java.util.ArrayList;

/**
 * @author sali
 */
public class VariableListBuilder extends BlockBuilder<VariableList> {

    public VariableListBuilder(Builder parent, VariableList source) {
        super(parent, source);
    }

    @Override
    protected void initContent() {
        titleContent = source.getTitleContent();
        content = new ArrayList<>();
        content.addAll(source.getContent());
        content.addAll(source.getVariableListEntry());
    }
}
