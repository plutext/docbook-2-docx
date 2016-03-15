package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import org.docbook.model.SimplePara;
import org.docx4j.wml.P;

import java.util.List;

/**
 * @author sali
 */
public class SimpleParaBuilder extends AbstractParaBuilder<SimplePara> {

    public SimpleParaBuilder(Builder parent, SimplePara simplePara) {
        super(parent, simplePara);
    }

    @Override
    protected void initContent() {
        content = source.getContent();
    }

    @Override
    protected void postProcessContent(List<Object> processedChildContent, List<Object> result) {
        result.add(addParaProperties((P) processedChildContent.get(0)));
    }

}
