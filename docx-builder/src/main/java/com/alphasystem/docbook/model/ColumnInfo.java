package com.alphasystem.docbook.model;

import java.io.Serializable;

import static com.alphasystem.util.AppUtil.isInstanceOf;
import static java.lang.String.format;
import static java.util.Objects.hash;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author sali
 */
public final class ColumnInfo implements Serializable, Comparable<ColumnInfo> {

    private int columnNumber;
    private String columnName;
    private double columnWidth;
    private double gridWidth;

    public ColumnInfo() {
        this(0);
    }

    public ColumnInfo(int columnNumber) {
        this(columnNumber, null);
    }

    public ColumnInfo(int columnNumber, String columnName) {
        this(columnNumber, columnName, 0.0, 0.0);
    }

    public ColumnInfo(int columnNumber, String columnName, double columnWidth, double gridWidth) {
        setColumnNumber(columnNumber);
        setColumnName(columnName);
        setColumnWidth(columnWidth);
        setGridWidth(gridWidth);
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = isBlank(columnName) ? format("col_%s", getColumnNumber() + 1) : columnName;
    }

    public double getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(double columnWidth) {
        this.columnWidth = columnWidth;
    }

    public double getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(double gridWidth) {
        this.gridWidth = gridWidth;
    }

    @Override
    public int compareTo(ColumnInfo o) {
        int result = 1;
        if (o != null) {
            result = getColumnName().compareTo(o.getColumnName());
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (isInstanceOf(ColumnInfo.class, obj)) {
            ColumnInfo other = (ColumnInfo) obj;
            result = getColumnName().equals(other.getColumnName());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return hash(getColumnName());
    }
}
