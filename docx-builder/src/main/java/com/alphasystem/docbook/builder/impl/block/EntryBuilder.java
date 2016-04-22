package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import com.alphasystem.docbook.model.ColumnInfo;
import com.alphasystem.docbook.util.ColumnSpecAdapter;
import com.alphasystem.openxml.builder.wml.PPrBuilder;
import com.alphasystem.openxml.builder.wml.TcBuilder;
import com.alphasystem.openxml.builder.wml.WmlBuilderFactory;
import org.docbook.model.Align;
import org.docbook.model.BasicVerticalAlign;
import org.docbook.model.Entry;
import org.docbook.model.VerticalAlign;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.TcPr;

import java.util.List;

import static com.alphasystem.docbook.util.TableAdapter.getColumnProperties;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getTcBuilder;
import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.getTcPrBuilder;
import static com.alphasystem.util.AppUtil.isInstanceOf;
import static java.lang.String.format;
import static java.util.Collections.singletonList;

/**
 * @author sali
 */
public class EntryBuilder extends BlockBuilder<Entry> {

    protected ColumnSpecAdapter columnSpecAdapter;

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
        // TODO: row span
        int gridSpan = 1;
        int columnIndex = indexInParent;
        final String nameStart = source.getNameStart();
        final String nameEnd = source.getNameEnd();
        if (nameStart != null && nameEnd != null) {
            final ColumnInfo startColumn = columnSpecAdapter.getColumnInfo(nameStart);
            if (startColumn == null) {
                throw new RuntimeException(format("No column info found with name \"%s\".", nameStart));
            }
            final ColumnInfo endColumn = columnSpecAdapter.getColumnInfo(nameEnd);
            if (endColumn == null) {
                throw new RuntimeException(format("No column info found with name \"%s\".", nameEnd));
            }
            final int startColumnColumnNumber = startColumn.getColumnNumber();
            final int endColumnColumnNumber = endColumn.getColumnNumber();
            gridSpan = endColumnColumnNumber - startColumnColumnNumber + 1;
            if (gridSpan < 1) {
                throw new RuntimeException(format("Invalid start (%s) and end (%s) column indices for columns \"%s\" and \"%s\" respectively.",
                        startColumnColumnNumber, endColumnColumnNumber, nameStart, nameEnd));
            }
        }
        ((RowBuilder)getParent()).updateNextColumnIndex(gridSpan);
        tcPr = getColumnProperties(columnSpecAdapter, columnIndex, gridSpan, tcPr);
        TcBuilder tcBuilder = getTcBuilder().withTcPr(tcPr);
        JcEnumeration align = getAlign();
        processedChildContent.forEach(o -> {
            if (isInstanceOf(P.class, o)) {
                P p = (P) o;
                if (align != null) {
                    PPrBuilder pPrBuilder = WmlBuilderFactory.getPPrBuilder().withJc(align);
                    pPrBuilder = new PPrBuilder(p.getPPr(), pPrBuilder.getObject());
                    p.setPPr(pPrBuilder.getObject());
                }
            }
            tcBuilder.addContent(o);
        });
        return singletonList(tcBuilder.getObject());
    }

    private STVerticalJc getVerticalAlign() {
        final Builder parent = getParent().getParent();
        if (!isInstanceOf(TableContentBuilder.class, parent)) {
            // EntryBuilder immediate parent should be RowBuilder and RowBuilder parent must be either of TableBodyBuilder,
            // TableHeaderBuilder, or TableFooterBuilder, if not raise exception
            throw new RuntimeException(format("Found different parent \"%s\".", parent.getClass().getName()));
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

    private JcEnumeration getAlign() {
        JcEnumeration jcEnumeration = null;
        Align align = source.getAlign();
        if (align != null) {
            switch (align) {
                case LEFT:
                    jcEnumeration = JcEnumeration.LEFT;
                    break;
                case CENTER:
                    jcEnumeration = JcEnumeration.CENTER;
                    break;
                case RIGHT:
                    jcEnumeration = JcEnumeration.RIGHT;
                    break;
                case JUSTIFY:
                    jcEnumeration = JcEnumeration.BOTH;
                    break;
            }
        }
        return jcEnumeration;
    }

    public void setColumnSpecAdapter(ColumnSpecAdapter columnSpecAdapter) {
        this.columnSpecAdapter = columnSpecAdapter;
    }
}
