package com.alphasystem.docbook.builder.test;

import com.alphasystem.docbook.model.ColumnInfo;
import com.alphasystem.docbook.util.ColumnSpecAdapter;
import org.docbook.model.ColumnSpec;
import org.docbook.model.ObjectFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.math.RoundingMode.HALF_UP;
import static org.testng.Assert.assertEquals;
import static org.testng.Reporter.log;

/**
 * @author sali
 */
public class ColumnSpecAdapterTest {

    private static final BigDecimal TOTAL_GRID_COL_WIDTH = new BigDecimal(9576);
    private static final BigDecimal TOTAL_TABLE_WIDTH = new BigDecimal(5000);
    private static final BigDecimal PERCENT = new BigDecimal(100.0);
    private static final MathContext ROUNDING = new MathContext(2, HALF_UP);
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private static ColumnSpec createColumnSpec(String width) {
        return createColumnSpec(null, width);
    }

    private static ColumnSpec createColumnSpec(String name, String width) {
        return OBJECT_FACTORY.createColumnSpec().withColumnName(name).withColumnWidth(width);
    }

    private static void printColumnInfos(List<ColumnInfo> columnInfos) {
        log("#####################################################", true);
        columnInfos.forEach(ColumnSpecAdapterTest::printColumnInfo);
        log("#####################################################", true);
    }

    private static void printColumnInfo(ColumnInfo columnInfo) {
        log("************************************************", true);
        log(format("Column Number: %s", columnInfo.getColumnNumber()), true);
        log(format("Column Name: %s", columnInfo.getColumnName()), true);
        log(format("Column Width: %s", columnInfo.getColumnWidth()), true);
        log(format("Column Width%%: %s", getPercentage(columnInfo.getColumnWidth(), TOTAL_TABLE_WIDTH)), true);
        log(format("Grid Width: %s", columnInfo.getGridWidth()), true);
        log(format("Grid Width%%: %s", getPercentage(columnInfo.getGridWidth(), TOTAL_GRID_COL_WIDTH)), true);
        log("************************************************", true);
    }

    private static void verifyWidth(double width, BigDecimal totalWidth, double percent) {
        assertEquals(new BigDecimal(percent), getPercentage(width, totalWidth));
    }

    private static BigDecimal getPercentage(double width, BigDecimal totalWidth) {
        return (new BigDecimal(width).multiply(PERCENT)).divide(totalWidth, ROUNDING);
    }

    private static void verifyWidth(ColumnInfo columnInfo, double percent) {
        verifyWidth(columnInfo.getColumnWidth(), TOTAL_TABLE_WIDTH, percent);
        verifyWidth(columnInfo.getGridWidth(), TOTAL_GRID_COL_WIDTH, percent);
    }

    @Test
    public void test1() {
        List<ColumnSpec> columnSpecs = new ArrayList<>();

        columnSpecs.add(createColumnSpec("15*"));
        columnSpecs.add(createColumnSpec("85*"));

        ColumnSpecAdapter columnSpecAdapter = new ColumnSpecAdapter(columnSpecs);
        final List<ColumnInfo> columnInfos = columnSpecAdapter.getColumnInfos();
        printColumnInfos(columnInfos);
        verifyWidth(columnInfos.get(0), 15);
        verifyWidth(columnInfos.get(1), 85);
    }

    @Test
    public void test2() {
        List<ColumnSpec> columnSpecs = new ArrayList<>();

        columnSpecs.add(createColumnSpec("col_1", "192*"));
        columnSpecs.add(createColumnSpec("col_2", "192*"));

        ColumnSpecAdapter columnSpecAdapter = new ColumnSpecAdapter(columnSpecs);
        final List<ColumnInfo> columnInfos = columnSpecAdapter.getColumnInfos();
        printColumnInfos(columnInfos);
        verifyWidth(columnInfos.get(0), 50);
        verifyWidth(columnInfos.get(1), 50);
    }

    @Test
    public void test3() {
        List<ColumnSpec> columnSpecs = new ArrayList<>();

        columnSpecs.add(createColumnSpec("col_1", "126*"));
        columnSpecs.add(createColumnSpec("col_2", "126*"));
        columnSpecs.add(createColumnSpec("col_3", "126*"));

        ColumnSpecAdapter columnSpecAdapter = new ColumnSpecAdapter(columnSpecs);
        final List<ColumnInfo> columnInfos = columnSpecAdapter.getColumnInfos();
        printColumnInfos(columnInfos);
        verifyWidth(columnInfos.get(0), 33);
        verifyWidth(columnInfos.get(1), 33);
        verifyWidth(columnInfos.get(2), 33);
    }

    @Test
    public void test4() {
        List<ColumnSpec> columnSpecs = new ArrayList<>();

        columnSpecs.add(createColumnSpec("col_1", "22*"));
        columnSpecs.add(createColumnSpec("col_2", "11*"));
        columnSpecs.add(createColumnSpec("col_3", "11*"));
        columnSpecs.add(createColumnSpec("col_4", "55*"));

        ColumnSpecAdapter columnSpecAdapter = new ColumnSpecAdapter(columnSpecs);
        final List<ColumnInfo> columnInfos = columnSpecAdapter.getColumnInfos();
        printColumnInfos(columnInfos);
        verifyWidth(columnInfos.get(0), 22);
        verifyWidth(columnInfos.get(1), 11);
        verifyWidth(columnInfos.get(2), 11);
        verifyWidth(columnInfos.get(3), 56);
    }
}
