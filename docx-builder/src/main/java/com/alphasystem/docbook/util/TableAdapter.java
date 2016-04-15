package com.alphasystem.docbook.util;

import com.alphasystem.docbook.model.ColumnInfo;
import com.alphasystem.openxml.builder.wml.TblBuilder;
import com.alphasystem.openxml.builder.wml.TblGridBuilder;
import com.alphasystem.openxml.builder.wml.TblPrBuilder;
import com.alphasystem.openxml.builder.wml.TcPrBuilder;
import org.docbook.model.ColumnSpec;
import org.docx4j.wml.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import static com.alphasystem.openxml.builder.wml.WmlBuilderFactory.*;
import static java.lang.String.format;
import static java.math.RoundingMode.HALF_UP;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.docx4j.sharedtypes.STOnOff.ONE;
import static org.docx4j.sharedtypes.STOnOff.ZERO;

/**
 * @author sali
 */
public final class TableAdapter {

    private static final String TYPE_PCT = "pct";
    private static final BigDecimal TOTAL_GRID_COL_WIDTH = new BigDecimal(9576);
    private static final BigDecimal TOTAL_TABLE_WIDTH = new BigDecimal(5000);
    private static final BigDecimal PERCENT = new BigDecimal(100.0);
    private static final MathContext ROUNDING = new MathContext(2, HALF_UP);

    private final List<ColumnInfo> columnInfos;
    private final BigDecimal totalTableWidth;

    public TableAdapter(List<ColumnSpec> columnSpecs) {
        this(PERCENT.doubleValue(), columnSpecs);
    }

    public TableAdapter(double tableWidthInPercent, List<ColumnSpec> columnSpecs) {
        BigDecimal _w = new BigDecimal((tableWidthInPercent <= 0.0) ? PERCENT.doubleValue() : tableWidthInPercent);
        BigDecimal totalGridWidth = TOTAL_GRID_COL_WIDTH.multiply(_w).divide(PERCENT);
        totalTableWidth = TOTAL_TABLE_WIDTH.multiply(_w).divide(PERCENT);
        columnInfos = new ArrayList<>(columnSpecs.size());
        final int length = columnSpecs.size();
        BigDecimal[] widths = new BigDecimal[length];
        BigDecimal totalWidth = new BigDecimal(0.0);
        for (int i = 0; i < length; i++) {
            final ColumnSpec columnSpec = columnSpecs.get(i);
            String columnWidth = columnSpec.getColumnWidth();
            if (columnWidth.endsWith("*")) {
                columnWidth = columnWidth.substring(0, columnWidth.length() - 1);
            }
            final BigDecimal width = new BigDecimal(Double.parseDouble(columnWidth), ROUNDING);
            widths[i] = width;
            totalWidth = totalWidth.add(width, ROUNDING);
        }

        for (int i = 0; i < length; i++) {
            final ColumnSpec columnSpec = columnSpecs.get(i);
            BigDecimal columnWidthInPercent = widths[i].multiply(PERCENT).divide(totalWidth, ROUNDING);
            final double columnWidth = totalTableWidth.multiply(columnWidthInPercent).divide(PERCENT).doubleValue();
            final double gridWidth = totalGridWidth.multiply(columnWidthInPercent).divide(PERCENT).doubleValue();
            columnInfos.add(new ColumnInfo(i, columnSpec.getColumnName(), columnWidth, gridWidth));
        }
    }

    public Tbl getTable(String tableStyle, TblPr tableProperties) {
        TblBuilder tblBuilder = getTblBuilder();

        TblGridBuilder tblGridBuilder = getTblGridBuilder();
        columnInfos.forEach(columnInfo -> tblGridBuilder.addGridCol(getTblGridColBuilder().withW((long) columnInfo.getGridWidth()).getObject()));

        TblWidth tblWidth = getTblWidthBuilder().withType(TYPE_PCT).withW(totalTableWidth.toString()).getObject();

        CTTblLook cTTblLook = getCTTblLookBuilder().withFirstRow(ONE).withLastRow(ZERO).withFirstColumn(ONE)
                .withLastColumn(ZERO).withNoVBand(ONE).withNoHBand(ZERO).getObject();

        tableStyle = isBlank(tableStyle) ? "TableGrid" : tableStyle;
        TblPr tblPr = getTblPrBuilder().withTblStyle(tableStyle).withTblW(tblWidth).withTblLook(cTTblLook).getObject();
        TblPrBuilder tblPrBuilder = new TblPrBuilder(tblPr, tableProperties);

        return tblBuilder.withTblGrid(tblGridBuilder.getObject()).withTblPr(tblPrBuilder.getObject()).getObject();
    }

    public TcPr getColumnProperties(Integer columnIndex, Integer gridSpanValue, TcPr columnProperties) throws ArrayIndexOutOfBoundsException {
        checkColumnIndex(columnIndex);
        final ColumnInfo columnInfo = columnInfos.get(columnIndex);
        BigDecimal columnWidth = new BigDecimal(columnInfo.getColumnWidth());
        long gs = 1;
        if (gridSpanValue != null && gridSpanValue > 1) {
            // sanity check, make sure we are not going out of bound
            checkColumnIndex(columnIndex + gridSpanValue - 1);
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

    public List<ColumnInfo> getColumnInfos() {
        return columnInfos;
    }

    // private methods

    /**
     * Checks whether <code>columnIndex</code> is within range.
     *
     * @param columnIndex index of column
     * @throws ArrayIndexOutOfBoundsException if <code>columnIndex</code> is out of bound.
     */
    private void checkColumnIndex(int columnIndex)
            throws ArrayIndexOutOfBoundsException {
        int numOfColumns = columnInfos.size();
        if (columnIndex < 0 || columnIndex >= numOfColumns) {
            throw new ArrayIndexOutOfBoundsException(
                    format("Invalid columnIndex {%s}, expected values are between %s and %s",
                            columnIndex, 0, (numOfColumns - 1)));
        }
    }
}
