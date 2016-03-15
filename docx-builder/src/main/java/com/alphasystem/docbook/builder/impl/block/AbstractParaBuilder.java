package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import com.alphasystem.openxml.builder.wml.PPrBuilder;
import com.alphasystem.openxml.builder.wml.WmlAdapter;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;

/**
 * @author sali
 */
public abstract class AbstractParaBuilder<T> extends BlockBuilder<T> {

    protected AbstractParaBuilder(Builder parent, T obj) {
        super(parent, obj);
    }

    protected P addParaProperties(final P p) {
        if (paraProperties != null) {
            p.setPPr(paraProperties);
        }
        WmlAdapter.addBookMark(p, getId(source));
        return p;
    }

    @Override
    protected void preProcess() {
        final PPr ppr = ((BlockBuilder) parent).getParaProperties();
        if (ppr != null) {
            paraProperties = new PPrBuilder(ppr, paraProperties).getObject();
        }
    }
}
