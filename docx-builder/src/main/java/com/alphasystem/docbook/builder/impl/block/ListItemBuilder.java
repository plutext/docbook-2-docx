package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import com.alphasystem.openxml.builder.wml.PPrBuilder;
import org.docbook.model.ListItem;
import org.docx4j.wml.PPr;

import java.util.List;

import static com.alphasystem.openxml.builder.wml.WmlAdapter.getNumPr;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getPPrBuilder;

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
        listParaProperties = getPPrBuilder().withPStyle(configurationUtils.getDefaultListStyle()).getObject();
    }

    @Override
    protected void preProcess() {
        PPr pPr = listParaProperties;
        BlockBuilder currentParent = (BlockBuilder) parent;
        while (currentParent != null) {
            final PPr listParaProperties = currentParent.getListParaProperties();
            if (listParaProperties != null) {
                pPr = listParaProperties;
                break;
            }
            currentParent = (BlockBuilder) currentParent.getParent();
        }
        paraProperties = new PPrBuilder(pPr, paraProperties).withNumPr(getNumPr(getNumber(), getLevel())).getObject();
    }

    @Override
    protected List<Object> buildChildContent(Builder builder, int iteration) {
        if(iteration == 1){
            paraProperties.setNumPr(null);
        }
        return super.buildChildContent(builder, iteration);
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
