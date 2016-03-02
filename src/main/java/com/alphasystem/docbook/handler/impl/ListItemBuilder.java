package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import com.alphasystem.openxml.builder.wml.PBuilder;
import com.alphasystem.openxml.builder.wml.WmlBuilderFactory;
import org.docbook.model.ListItem;
import org.docbook.model.SimplePara;
import org.docx4j.wml.PPr;

import java.util.ArrayList;
import java.util.List;

import static com.alphasystem.openxml.builder.wml.WmlAdapter.getListParagraphProperties;
import static com.alphasystem.util.AppUtil.isInstanceOf;

/**
 * @author sa
 */
public class ListItemBuilder extends AbstractBuilder<ListItem> {

    private long number;
    private long level;

    public ListItemBuilder(Builder parent, ListItem listitem) {
        super(parent, listitem);
    }

    @Override
    public List<Object> buildContent() {
        List<Object> result = new ArrayList<>();
        final List<Object> content = obj.getContent();
        for (Object o : content) {
            final Builder builder = factory.getBuilder(this, o);
            if (builder == null) {
                logUnhandledContentWarning(o);
            } else if (isInstanceOf(SimplePara.class, o)) {
                final PPr pPr = getListParagraphProperties(number, level);
                final PBuilder pBuilder = WmlBuilderFactory.getPBuilder().withPPr(pPr);
                pBuilder.addContent(builder.buildRuns().toArray());
                result.add(pBuilder.getObject());
            } else {
                result.addAll(builder.buildContent());
            }
        }
        return result;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }
}
