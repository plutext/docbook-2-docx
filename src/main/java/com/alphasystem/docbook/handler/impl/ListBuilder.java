package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import org.docbook.model.ListItem;
import org.docx4j.wml.P;

import java.util.List;

import static com.alphasystem.docbook.ApplicationController.getContext;
import static com.alphasystem.util.AppUtil.isInstanceOf;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author sali
 */
public abstract class ListBuilder<T> extends AbstractBuilder<T> {

    private long number;
    private long level;

    protected ListBuilder(Builder parent, T obj) {
        super(parent, obj);
    }

    protected abstract com.alphasystem.openxml.builder.wml.ListItem getItemByName(String styleName);

    protected void parseTitleContent(List<Object> titleContent, List<Object> target) {
        titleContent.forEach(o -> {
            final Builder builder = factory.getBuilder(this, o);
            if (builder == null) {
                logUnhandledContentWarning(o);
            } else {
                final P p = builder.buildParagraph();
                if (p != null) {
                    target.add(p);
                }
                final List content = builder.buildContent();
                if (nonNull(content) && !content.isEmpty()) {
                    target.addAll(content);
                }
            }
        });
    }

    protected void parseStyleAndLevel(String styleName) {
        final Builder parent = getParent();
        if (isInstanceOf(ListItemBuilder.class, parent)) {
            // we have nested list, get the current list item and pass it down to
            ListItemBuilder listItemBuilder = (ListItemBuilder) parent;
            number = listItemBuilder.getNumber();
            level = listItemBuilder.getLevel() + 1;
        } else {
            number = getItemByName(styleName).getNumberId();
            level = 0;
        }
    }

    protected void processListItems(List<ListItem> listItems, List<Object> target) {
        if (isNull(listItems) || listItems.isEmpty()) {
            logger.error("No list item found");
            return;
        }
        final long listNumber = getContext().getListNumber(number, level);
        listItems.forEach(listItem -> {
            final ListItemBuilder builder = (ListItemBuilder) factory.getBuilder(this, listItem);
            builder.setNumber(listNumber);
            builder.setLevel(level);
            target.addAll(builder.buildContent());
        });
    }
}
