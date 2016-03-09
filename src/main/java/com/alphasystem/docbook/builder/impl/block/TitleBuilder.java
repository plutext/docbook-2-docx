package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import com.alphasystem.openxml.builder.wml.PBuilder;
import com.alphasystem.openxml.builder.wml.WmlAdapter;
import org.docbook.model.Title;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;

import java.util.Collections;
import java.util.List;

import static com.alphasystem.openxml.builder.wml.WmlAdapter.getPStyle;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getPBuilder;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getPPrBuilder;
import static com.alphasystem.util.AppUtil.isInstanceOf;
import static com.alphasystem.util.IdGenerator.nextId;
import static java.lang.String.format;

/**
 * @author sali
 */
public class TitleBuilder extends BlockBuilder<Title> {

    private String titleStyle;
    private PBuilder pBuilder;

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
        final PPr pPr = getPPrBuilder().withPStyle(getPStyle(titleStyle)).getObject();
        String rsidP = nextId();
        pBuilder = getPBuilder().withRsidP(rsidP).withRsidRDefault(rsidP).withRsidR(nextId()).withPPr(pPr);
    }

    @Override
    protected List<Object> postProcess(List<Object> processedTitleContent, List<Object> processedChildContent) {
        pBuilder.addContent(processedChildContent.toArray());
        final P p = pBuilder.getObject();
        final Builder parent = getParent();
        if (parent != null) {
            WmlAdapter.addBookMark(p, getId(parent.getSource()));
        }
        return Collections.singletonList(p);
    }

    private void initTitleStyle() {
        boolean section = false;
        String key = "heading-strong";
        if (isInstanceOf(ArticleBuilder.class, parent)) {
            key = "document-title";
        } else if (isInstanceOf(ExampleBuilder.class, parent)) {
            key = "example-title";
        } else if (isInstanceOf(SideBarBuilder.class, parent)) {
            key = "sidebar-title";
        } else if (isInstanceOf(SectionBuilder.class, parent)) {
            SectionBuilder sectionBuilder = (SectionBuilder) parent;
            final int level = sectionBuilder.getLevel();
            section = ((level >= 1) && (level <= 5));
            key = section ? format("heading-level-%s", level) : "heading-strong";
        } else {
            logger.warn("Unhandled parent in title {}", parent.getClass().getName());
        }
        titleStyle = applicationController.getConfiguration().getString(key);
        final boolean numbered = ApplicationController.getContext().getDocumentInfo().isSectionNumbers();
        if (section && numbered) {
            titleStyle = format("List%s", titleStyle);
        }
    }
}
