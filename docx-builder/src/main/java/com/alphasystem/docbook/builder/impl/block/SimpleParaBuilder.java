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

import static com.alphasystem.openxml.builder.wml.WmlAdapter.getListParagraphProperties;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getPPrBuilder;
import static com.alphasystem.util.AppUtil.isInstanceOf;

/**
 * @author sali
 */
public class SimpleParaBuilder extends BlockBuilder<SimplePara> {

    protected PPr pPr;

    public SimpleParaBuilder(Builder parent, SimplePara simplePara) {
        super(parent, simplePara);
    }

    @Override
    protected void initContent() {
        content = source.getContent();
    }

    @Override
    protected void preProcess() {
        boolean listType = isInstanceOf(ListItemBuilder.class, parent);
        boolean admonition = hasParent(AdmonitionBuilder.class);
        if (listType) {
            ListItemBuilder listItemBuilder = (ListItemBuilder) parent;
            String style = admonition ? "AdmonitionListParagraph" : "ListParagraph";
            final boolean applyNumber = listItemBuilder.isApplyNumber();
            final PPr listPpr = getListParagraphProperties(listItemBuilder.getNumber(), listItemBuilder.getLevel(), style, applyNumber);
            PPrBuilder pPrBuilder = new PPrBuilder(listPpr, pPr);
            pPr = pPrBuilder.getObject();
            if(applyNumber){
                listItemBuilder.setApplyNumber(false);
            }
        } else if (admonition) {
            if (pPr == null) {
                pPr = getPPrBuilder().getObject();
            }
            PPrBuilder pPrBuilder = new PPrBuilder(pPr, null).withPStyle("Admonition");
            pPr = pPrBuilder.getObject();
        }
    }

    @Override
    protected List<Object> postProcess(List<Object> processedTitleContent, List<Object> processedChildContent) {
        final P p = (P) processedChildContent.get(0);
        if (pPr != null) {
            p.setPPr(pPr);
        }
        WmlAdapter.addBookMark(p, source.getId());
        return Collections.singletonList(p);
    }

}
