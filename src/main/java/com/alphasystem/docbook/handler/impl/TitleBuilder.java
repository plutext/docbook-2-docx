package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.ApplicationController;
import com.alphasystem.docbook.handler.Builder;
import com.alphasystem.openxml.builder.wml.PBuilder;
import org.docbook.model.Phrase;
import org.docbook.model.Title;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;

import static com.alphasystem.openxml.builder.wml.WmlAdapter.getPStyle;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getPBuilder;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getPPrBuilder;
import static com.alphasystem.util.AppUtil.isInstanceOf;
import static com.alphasystem.util.IdGenerator.nextId;
import static java.lang.String.format;

/**
 * @author sali
 */
public class TitleBuilder extends AbstractBuilder<Title> {

    private String titleStyle;

    public TitleBuilder(Builder parent, Title title) {
        super(parent, title);
        initTitleStyle();
    }

    public void initTitleStyle() {
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

    @Override
    public P buildParagraph() {
        final PPr pPr = getPPrBuilder().withPStyle(getPStyle(titleStyle)).getObject();
        String rsidP = nextId();
        final PBuilder pBuilder = getPBuilder().withRsidP(rsidP).withRsidRDefault(rsidP).withRsidR(nextId()).withPPr(pPr);

        for (Object o : obj.getContent()) {
            if (isInstanceOf(String.class, o)) {
                final Builder builder = factory.getBuilder(this, o);
                pBuilder.addContent(builder.build(null));
            } else if (isInstanceOf(Phrase.class, o)) {
                pBuilder.addContent(factory.getBuilder(this, o).build(null));
            } else {
                logUnhandledContentWarning(o);
            }
        }
        return pBuilder.getObject();
    }

}
