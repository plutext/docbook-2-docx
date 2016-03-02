package com.alphasystem.docbook.handler.impl;

import com.alphasystem.docbook.handler.Builder;
import com.alphasystem.openxml.builder.wml.ListItem;
import org.docbook.model.OrderedList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sali
 */
public class OrderedListBuilder extends ListBuilder<OrderedList> {

    public OrderedListBuilder(Builder parent, OrderedList orderedlist) {
        super(parent, orderedlist);
    }

    @Override
    protected ListItem getItemByName(String styleName) {
        return com.alphasystem.openxml.builder.wml.OrderedList.getByStyleName(styleName);
    }

    @Override
    public List<Object> buildContent() {
        List<Object> result = new ArrayList<>();

        parseTitleContent(obj.getTitleContent(), result);
        parseContent(obj.getContent(), result);
        parseStyleAndLevel(obj.getNumeration());
        processListItems(obj.getListitem(), result);

        return result;
    }
}
