package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import com.alphasystem.openxml.builder.wml.PPrBuilder;
import com.alphasystem.openxml.builder.wml.WmlAdapter;
import org.docbook.model.SimplePara;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;

import java.util.Collections;
import java.util.List;

/**
 * @author sali
 */
public class SimpleParaBuilder extends BlockBuilder<SimplePara> {

    public SimpleParaBuilder(Builder parent, SimplePara simplePara) {
        super(parent, simplePara);
    }

    @Override
    protected void initContent() {
        content = source.getContent();
    }

    @Override
    protected void preProcess() {
        final PPr ppr = ((BlockBuilder) parent).getParaProperties();
        if(ppr != null) {
            paraProperties = new PPrBuilder(ppr, paraProperties).getObject();
        }
    }

    @Override
    protected List<Object> postProcess(List<Object> processedTitleContent, List<Object> processedChildContent) {
        final P p = (P) processedChildContent.get(0);
        if (paraProperties != null) {
            p.setPPr(paraProperties);
        }
        WmlAdapter.addBookMark(p, source.getId());
        return Collections.singletonList(p);
    }

}
