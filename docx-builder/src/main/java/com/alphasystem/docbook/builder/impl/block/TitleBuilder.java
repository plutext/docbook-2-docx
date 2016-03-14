package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import com.alphasystem.openxml.builder.wml.WmlAdapter;
import org.docbook.model.Title;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;

import java.util.Collections;
import java.util.List;

import static com.alphasystem.openxml.builder.wml.WmlAdapter.getPStyle;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getPPrBuilder;

/**
 * @author sali
 */
public class TitleBuilder extends BlockBuilder<Title> {

    private String titleStyle;
    private PPr pPr;

    public TitleBuilder(Builder parent, Title title) {
        super(parent, title);
        initTitleStyle();
    }

    @Override
    protected void initContent() {
        content = source.getContent();
    }

    @Override
    protected void preProcess() {
        pPr = getPPrBuilder().withPStyle(getPStyle(titleStyle)).getObject();
    }

    @Override
    protected List<Object> postProcess(List<Object> processedTitleContent, List<Object> processedChildContent) {
        final P p = (P) processedChildContent.get(0);
        if (pPr != null) {
            p.setPPr(pPr);
        }
        if (parent != null) {
            WmlAdapter.addBookMark(p, getId(parent.getSource()));
        }
        return Collections.singletonList(p);
    }

    private void initTitleStyle() {
        final boolean numbered = ApplicationController.getContext().getDocumentInfo().isSectionNumbers();
        titleStyle = configurationUtils.getTitleStyle(parent, numbered);
    }
}
