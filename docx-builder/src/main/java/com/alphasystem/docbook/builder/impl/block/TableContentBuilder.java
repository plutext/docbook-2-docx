package com.alphasystem.docbook.builder.impl.block;

import com.alphasystem.docbook.builder.Builder;
import com.alphasystem.docbook.builder.impl.BlockBuilder;
import org.docbook.model.Entry;
import org.docbook.model.ObjectFactory;
import org.docbook.model.Row;

import java.util.List;

import static com.alphasystem.util.AppUtil.isInstanceOf;
import static java.lang.String.format;

/**
 * @author sali
 */
public abstract class TableContentBuilder<T> extends BlockBuilder<T> {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    protected TableContentBuilder(Builder parent, T source, int indexInParent) {
        super(parent, source, indexInParent);
    }

    @Override
    protected void preProcess() {
        super.preProcess();
        Row nextRow = null;
        boolean hasNextRow = false;
        boolean hasMoreRows = false;
        final int size = content.size();
        for (int i = 0; i < size; i++) {
            final Row currentRow = (Row) content.get(i);
            final int nextIndex = i + 1;
            hasNextRow = nextIndex < size;
            if (hasNextRow) {
                nextRow = (Row) content.get(nextIndex);
            } else {
                nextRow = OBJECT_FACTORY.createRow();
            }
            final List<Object> rowContent = currentRow.getContent();
            for (int j = 0; j < rowContent.size(); j++) {
                final Object o = rowContent.get(j);
                if (isInstanceOf(Entry.class, o)) {
                    final Entry entry = (Entry) o;
                    final String moreRows = entry.getMoreRows();
                    hasMoreRows = moreRows != null;
                    if (hasMoreRows) {
                        final String nextMoreRows = getNextMoreRows(moreRows);
                        if (nextMoreRows != null) {
                            Entry nextEntry = OBJECT_FACTORY.createEntry().withMoreRows(nextMoreRows)
                                    .withNameStart(entry.getNameStart()).withNameEnd(entry.getNameEnd());
                            nextRow.getContent().add(j, nextEntry);
                        }
                    } // end of if "hasMoreRows"
                } // end of Entry check
            } // end of inner for loop
        } // end of outer for loop
        if (hasMoreRows && !hasNextRow) {
            content.add(nextRow);
        }
    }

    private static String getNextMoreRows(String moreRows) {
        // System.out.print(format("More Rows before \"%s\", ", moreRows));
        if (moreRows.endsWith("*")) {
            moreRows = moreRows.substring(0, moreRows.length() - 1);
        }
        final int i = Integer.parseInt(moreRows) - 1;
        if (i < 0) {
            // System.out.println("None");
            return null;
        }
        moreRows = format("%s*", i);
        // System.out.println(format("More rows after \"%s\"", moreRows));
        return moreRows;
    }

}
