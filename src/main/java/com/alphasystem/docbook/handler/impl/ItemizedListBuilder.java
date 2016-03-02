package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import com.alphasystem.openxml.builder.wml.ListItem;
import com.alphasystem.openxml.builder.wml.UnorderedList;
import org.docbook.model.ItemizedList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sali
 */
public class ItemizedListBuilder extends ListBuilder<ItemizedList> {

    public ItemizedListBuilder(Builder parent, ItemizedList obj) {
        super(parent, obj);
    }

    @Override
    protected ListItem getItemByName(String styleName) {
        return UnorderedList.getByStyleName(styleName);
    }

    @Override
    public List<Object> buildContent() {
        List<Object> result = new ArrayList<>();

        parseTitleContent(obj.getTitleContent(), result);
        parseContent(obj.getContent(), result);
        parseStyleAndLevel(obj.getMark());
        processListItems(obj.getListitem(), result);

        return result;
    }
}
