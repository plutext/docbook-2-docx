package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import com.alphasystem.openxml.builder.wml.TcBuilder;
import org.docbook.model.BasicVerticalAlign;
import org.docbook.model.Entry;
import org.docbook.model.VerticalAlign;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.TcPr;

import java.util.List;

import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getTcBuilder;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getTcPrBuilder;
import static com.alphasystem.util.AppUtil.isInstanceOf;
import static java.util.Collections.singletonList;

/**
 * @author sali
 */
public class EntryBuilder extends BlockBuilder<Entry> {

    public EntryBuilder(Builder parent, Entry entry, int indexInParent) {
        super(parent, entry, indexInParent);
    }

    @Override
    protected void initContent() {
        content = source.getContent();
    }

    @Override
    protected List<Object> postProcess(List<Object> processedTitleContent, List<Object> processedChildContent) {
        TcPr tcPr = getTcPrBuilder().withVAlign(getVerticalAlign()).getObject();
        TcBuilder tcBuilder = getTcBuilder().withTcPr(tcPr);
        processedChildContent.forEach(o -> tcBuilder.addContent(o));
        return singletonList(tcBuilder.getObject());
    }

    private STVerticalJc getVerticalAlign() {
        final Builder parent = getParent().getParent();
        if (!isInstanceOf(TableBodyBuilder.class, parent) || !isInstanceOf(TableHeaderBuilder.class, parent) ||
                !isInstanceOf(TableFooterBuilder.class, parent)) {
            // EntryBuilder immediate parent should be RowBuilder and RowBuilder parent must be either of TableBodyBuilder,
            // TableHeaderBuilder, or TableFooterBuilder, if not raise exception
            throw new RuntimeException(String.format("Found different parent \"%s\".", parent.getClass().getName()));
        }
        final Object parentSource = parent.getSource();
        final VerticalAlign parentAlign = (VerticalAlign) invokeMethod(parentSource, "getVAlign");
        BasicVerticalAlign vAlign = source.getValign();

        if (vAlign == null && parentAlign != null) {
            try {
                vAlign = BasicVerticalAlign.fromValue(parentAlign.value());
            } catch (Exception e) {
                // ignore, if the parent value is BASELINE, then there is no corresponding value for BasicVerticalAlign
            }
        }
        STVerticalJc val = null;
        if (vAlign != null) {
            switch (vAlign) {
                case BOTTOM:
                    val = STVerticalJc.BOTTOM;
                    break;
                case MIDDLE:
                    val = STVerticalJc.CENTER;
                    break;
                case TOP:
                    val = STVerticalJc.TOP;
                    break;
            }
        }
        return val;
    }
}
