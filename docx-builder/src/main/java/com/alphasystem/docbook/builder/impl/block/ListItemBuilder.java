package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import org.docbook.model.ListItem;

/**
 * @author sa
 */
public class ListItemBuilder extends BlockBuilder<ListItem> {

    private long number;
    private long level;

    public ListItemBuilder(Builder parent, ListItem listitem) {
        super(parent, listitem);
    }

    @Override
    protected void initContent() {
        content = source.getContent();
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
