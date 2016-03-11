package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import org.docbook.model.FormalPara;

import java.util.Collections;

/**
 * @author sali
 */
public class FormalParaBuilder extends BlockBuilder<FormalPara> {

    public FormalParaBuilder(Builder parent, FormalPara obj) {
        super(parent, obj);
    }

    @Override
    protected void initContent() {
        titleContent = source.getTitleContent();
        content = Collections.singletonList(source.getPara());
    }
}
