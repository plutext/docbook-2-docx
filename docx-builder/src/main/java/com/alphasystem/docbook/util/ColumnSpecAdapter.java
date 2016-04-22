package com.alphasystem.docbook.util;

import com.alphasystem.docbook.model.ColumnInfo;
import org.docbook.model.ColumnSpec;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

/**
 * @author sali
 */
public final class ColumnSpecAdapter {

    private static final BigDecimal TOTAL_GRID_COL_WIDTH = new BigDecimal(9576);
    private static final BigDecimal TOTAL_TABLE_WIDTH = new BigDecimal(5000);
    private static final BigDecimal PERCENT = new BigDecimal(100.0);
    private static final MathContext ROUNDING = new MathContext(2, HALF_UP);

    private final List<ColumnInfo> columnInfos;
    private final BigDecimal totalTableWidth;

    public ColumnSpecAdapter(List<ColumnSpec> columnSpecs) {
        this(PERCENT.doubleValue(), columnSpecs);
    }

    public ColumnSpecAdapter(double tableWidthInPercent, List<ColumnSpec> columnSpecs) {
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

    public List<ColumnInfo> getColumnInfos() {
        return columnInfos;
    }

    public BigDecimal getTotalTableWidth() {
        return totalTableWidth;
    }

    public ColumnInfo getColumnInfo(String name) {
        final int i = columnInfos.indexOf(new ColumnInfo(name));
        return columnInfos.get(i);
    }
}
