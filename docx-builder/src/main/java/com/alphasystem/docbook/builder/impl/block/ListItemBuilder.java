package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import com.alphasystem.openxml.builder.wml.PPrBuilder;
import org.docbook.model.ListItem;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.NumPr;

import java.util.List;

import static com.alphasystem.openxml.builder.wml.WmlAdapter.getNumPr;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getPPrBuilder;
import static com.alphasystem.util.AppUtil.isInstanceOf;

/**
 * @author sa
 */
public class ListItemBuilder extends BlockBuilder<ListItem> {

    private long number = -1;
    private long level = -1;

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
        NumPr numPr = ((getNumber() < 0) || (getLevel() < 0)) ? null : getNumPr(getNumber(), getLevel());
        paraProperties = new PPrBuilder(pPr, paraProperties).withNumPr(numPr).getObject();
    }

    @Override
    protected List<Object> buildChildContent(Builder builder, int iteration) {
        if (iteration == 1) {
            paraProperties.setNumPr(null);
        }
        return super.buildChildContent(builder, iteration);
    }

    @Override
    protected void postProcessContent(List<Object> processedChildContent, List<Object> result) {
        processedChildContent.forEach(o -> {
            if (isInstanceOf(P.class, o)) {
                ((P) o).setPPr(paraProperties);
            }
        });
        super.postProcessContent(processedChildContent, result);
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
