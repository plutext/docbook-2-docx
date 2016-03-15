package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.openxml.builder.wml.ListItem;
import com.alphasystem.openxml.builder.wml.UnorderedList;
import org.docbook.model.ItemizedList;

import java.util.List;

/**
 * @author sali
 */
public class ItemizedListBuilder extends ListBuilder<ItemizedList> {

    public ItemizedListBuilder(Builder parent, ItemizedList obj) {
        super(parent, obj);
    }

    @Override
    protected void initContent() {
        titleContent = source.getTitleContent();
        content = source.getContent();
    }

    @Override
    protected ListItem getItemByName(String styleName) {
        return UnorderedList.getByStyleName(styleName);
    }

    @Override
    protected void preProcess() {
        parseStyleAndLevel(source.getMark());
    }

    @Override
    protected List<Object> postProcess(List<Object> processedTitleContent, List<Object> processedChildContent) {
        final List<Object> result = super.postProcess(processedTitleContent, processedChildContent);
        processListItems(source.getListItem(), result);
        return result;
    }

}
