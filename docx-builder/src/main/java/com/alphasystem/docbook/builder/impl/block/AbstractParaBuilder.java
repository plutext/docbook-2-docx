package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import com.alphasystem.openxml.builder.wml.PBuilder;
import com.alphasystem.openxml.builder.wml.PPrBuilder;
import com.alphasystem.openxml.builder.wml.WmlAdapter;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;

import java.util.Collections;
import java.util.List;

import static com.alphasystem.openxml.builder.wml.WmlAdapter.getListParagraphProperties;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getPBuilder;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getPPrBuilder;
import static com.alphasystem.util.AppUtil.isInstanceOf;
import static com.alphasystem.util.IdGenerator.nextId;

/**
 * @author sali
 */
public abstract class AbstractParaBuilder<T> extends BlockBuilder<T> {

    protected PBuilder pBuilder;

    protected AbstractParaBuilder(Builder parent, T obj) {
        super(parent, obj);
    }

    @Override
    protected void preProcess() {
        String rsidP = nextId();
        String rsidR = nextId();
        pBuilder = getPBuilder().withRsidP(rsidP).withRsidRDefault(rsidR).withRsidR(nextId());
        PPr pPr = pBuilder.getObject().getPPr();
        boolean listType = isInstanceOf(ListItemBuilder.class, parent);
        boolean admonition = hasParent(AdmonitionBuilder.class);
        if (listType) {
            ListItemBuilder listItemBuilder = (ListItemBuilder) parent;
            String style = admonition ? "AdmonitionListParagraph" : null;
            final PPr listPpr = getListParagraphProperties(listItemBuilder.getNumber(), listItemBuilder.getLevel(), style);
            PPrBuilder pPrBuilder = new PPrBuilder(listPpr, pPr);
            pPr = pPrBuilder.getObject();
        } else if (admonition) {
            if (pPr == null) {
                pPr = getPPrBuilder().getObject();
            }
            PPrBuilder pPrBuilder = new PPrBuilder(pPr, null).withPStyle("Admonition");
            pPr = pPrBuilder.getObject();
        }
        pBuilder.withPPr(pPr);
    }

    @Override
    protected List<Object> postProcess(List<Object> processedTitleContent, List<Object> processedChildContent) {
        pBuilder.addContent(processedChildContent.toArray());
        final P p = pBuilder.getObject();
        WmlAdapter.addBookMark(p, getId(source));
        return Collections.singletonList(p);
    }
}
