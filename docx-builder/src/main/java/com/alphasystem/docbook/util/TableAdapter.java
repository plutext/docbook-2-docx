package com.alphasystem.docbook.util;

import com.alphasystem.docbook.model.ColumnInfo;
import com.alphasystem.openxml.builder.wml.TblBuilder;
import com.alphasystem.openxml.builder.wml.TblGridBuilder;
import com.alphasystem.openxml.builder.wml.TblPrBuilder;
import com.alphasystem.openxml.builder.wml.TcPrBuilder;
import org.docx4j.wml.*;

import java.math.BigDecimal;
import java.util.List;

import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.*;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.docx4j.sharedtypes.STOnOff.ONE;

/**
 * @author sali
 */
public final class TableAdapter {

    private static final String TYPE_PCT = "pct";

    private TableAdapter() {
    }

    public static Tbl getTable(ColumnSpecAdapter columnSpecAdapter, String tableStyle, TblPr tableProperties) {
        TblBuilder tblBuilder = getTblBuilder();

        TblGridBuilder tblGridBuilder = getTblGridBuilder();
        columnSpecAdapter.getColumnInfos().forEach(columnInfo -> {
            final TblGridCol tblGridCol = getTblGridColBuilder().withW((long) columnInfo.getGridWidth()).getObject();
            tblGridBuilder.addGridCol(tblGridCol);
        });

        TblWidth tblWidth = getTblWidthBuilder().withType(TYPE_PCT).withW(columnSpecAdapter.getTotalTableWidth().toString()).getObject();

        CTTblLook cTTblLook = getCTTblLookBuilder().withFirstRow(ONE).withLastRow(ONE).withFirstColumn(ONE)
                .withLastColumn(ONE).withNoVBand(ONE).withNoHBand(ONE).getObject();

        tableStyle = isBlank(tableStyle) ? "TableGrid" : tableStyle;
        TblPr tblPr = getTblPrBuilder().withTblStyle(tableStyle).withTblW(tblWidth).withTblLook(cTTblLook).getObject();
        TblPrBuilder tblPrBuilder = new TblPrBuilder(tblPr, tableProperties);

        return tblBuilder.withTblGrid(tblGridBuilder.getObject()).withTblPr(tblPrBuilder.getObject()).getObject();
    }

    public static TcPr getColumnProperties(ColumnSpecAdapter columnSpecAdapter, Integer columnIndex, Integer gridSpanValue,
                                           TcPr columnProperties) throws ArrayIndexOutOfBoundsException {
        List<ColumnInfo> columnInfos = columnSpecAdapter.getColumnInfos();
        checkColumnIndex(columnInfos, columnIndex);
        final ColumnInfo columnInfo = columnInfos.get(columnIndex);
        BigDecimal columnWidth = new BigDecimal(columnInfo.getColumnWidth());
        long gs = 1;
        if (gridSpanValue != null && gridSpanValue > 1) {
            // sanity check, make sure we are not going out of bound
            checkColumnIndex(columnInfos, columnIndex + gridSpanValue - 1);
            // iterate through width and get the total width for the grid span
            for (int i = columnIndex + 1; i < gridSpanValue; i++) {
                final ColumnInfo columnInfo1 = columnInfos.get(i);
                columnWidth = columnWidth.add(new BigDecimal(columnInfo1.getColumnWidth()));
            }
            gs = gridSpanValue;
        }

        TblWidth tblWidth = getTblWidthBuilder().withType(TYPE_PCT).withW(columnWidth.longValue()).getObject();
        TcPrBuilder tcPrBuilder = getTcPrBuilder().withGridSpan(gs).withTcW(tblWidth);
        return new TcPrBuilder(tcPrBuilder.getObject(), columnProperties).getObject();
    }

    // private methods

    /**
     * Checks whether <code>columnIndex</code> is within range.
     *
     * @param columnInfos column data
     * @param columnIndex index of column
     * @throws ArrayIndexOutOfBoundsException if <code>columnIndex</code> is out of bound.
     */
    private static void checkColumnIndex(List<ColumnInfo> columnInfos, int columnIndex)
            throws ArrayIndexOutOfBoundsException {
        int numOfColumns = columnInfos.size();
        if (columnIndex < 0 || columnIndex >= numOfColumns) {
            throw new ArrayIndexOutOfBoundsException(
                    format("Invalid columnIndex {%s}, expected values are between %s and %s",
                            columnIndex, 0, (numOfColumns - 1)));
        }
    }
}
