package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import org.docbook.model.ListItem;

import java.util.List;

import static com.alphasystem.docbook.ApplicationController.getContext;
import static java.util.Objects.isNull;

/**
 * @author sali
 */
public abstract class ListBuilder<T> extends BlockBuilder<T> {

    protected long number;
    protected long level;

    protected ListBuilder(Builder parent, T obj) {
        super(parent, obj);
    }

    protected abstract com.alphasystem.openxml.builder.wml.ListItem getItemByName(String styleName);

    protected void parseStyleAndLevel(String styleName) {
        final Builder parent = getParent();
        if (hasParent(ListItemBuilder.class)) {
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
