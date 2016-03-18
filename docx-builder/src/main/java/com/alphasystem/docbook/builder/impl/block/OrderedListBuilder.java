package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.openxml.builder.wml.ListItem;
import org.docbook.model.OrderedList;

import java.util.ArrayList;

/**
 * @author sali
 */
public class OrderedListBuilder extends ListBuilder<OrderedList> {

    public OrderedListBuilder(Builder parent, OrderedList orderedlist) {
        super(parent, orderedlist);
    }

    @Override
    protected void initContent() {
        titleContent = source.getTitleContent();
        content = new ArrayList<>();
        content.addAll(source.getContent());
        content.addAll(source.getListItem());
    }

    @Override
    protected ListItem getItemByName(String styleName) {
        return com.alphasystem.openxml.builder.wml.OrderedList.getByStyleName(styleName);
    }

    @Override
    protected void preProcess() {
        parseStyleAndLevel(source.getNumeration());
    }

}
