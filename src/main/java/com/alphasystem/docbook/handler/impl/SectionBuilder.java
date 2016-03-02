package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import org.docbook.model.Section;
import org.docbook.model.Title;
import org.docx4j.wml.P;

import java.util.ArrayList;
import java.util.List;

import static com.alphasystem.docbook.handler.DocumentHandlerHelper.addBookMark;
import static com.alphasystem.openxml.builder.wml.WmlAdapter.getEmptyPara;
import static com.alphasystem.util.AppUtil.isInstanceOf;

/**
 * @author sali
 */
public class SectionBuilder extends AbstractBuilder<Section> {

    private int level;

    public SectionBuilder(Builder parent, Section section) {
        super(parent, section);
        initLevel();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public List<Object> buildContent() {
        List<Object> content = new ArrayList<>();

        final List<Object> childContent = obj.getContent();

        for (Object o : childContent) {
            if (isInstanceOf(Title.class, o)) {
                final Builder titleBuilder = factory.getBuilder(this, o);
                final P p = titleBuilder.buildParagraph();
                addBookMark(p, obj.getId());
                content.add(p);
            } else {
                final Builder builder = factory.getBuilder(this, o);
                if (builder == null) {
                    logUnhandledContentWarning(o);
                } else {
                    final List<P> paragraphs = builder.buildContent();
                    if (!paragraphs.isEmpty()) {
                        content.addAll(paragraphs);
                    }
                    final P p = builder.buildParagraph();
                    if (p != null) {
                        content.add(p);
                    }
                }
            }
        }

        if (!content.isEmpty()) {
            content.add(getEmptyPara());
        }
        return content;
    }

    public void initLevel() {
        if (isInstanceOf(ArticleBuilder.class, parent)) {
            // must be top level section
            level = 1;
        } else if (isInstanceOf(SectionBuilder.class, parent)) {
            // must be one of sub level section, increase the level of parent by 1
            SectionBuilder parentSection = (SectionBuilder) parent;
            level = parentSection.getLevel() + 1;
        } else {
            logger.warn("unhandled parent of section {}", parent.getClass().getName());
            level = -1;
        }
    }

    public int getLevel() {
        return level;
    }
}
