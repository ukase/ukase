/*
 * Copyright (c) 2016 Konstantin Lepa <konstantin+ukase@lepabox.net>
 *
 * This file is part of Ukase.
 *
 *  Ukase is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.ukase.toolkit.xlsx;

import com.github.ukase.toolkit.xlsx.translators.Translator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.newtable.TableCellBox;
import org.xhtmlrenderer.newtable.TableRowBox;
import org.xhtmlrenderer.render.BlockBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.github.ukase.toolkit.xlsx.XlsxUtil.*;

public class RenderingTable implements Runnable {
    private static final int CALCULATED_KOEF = 36;
    private static final String TAG_TD = "td";
    private static final String TAG_TR = "tr";
    private static final String TAG_CAPTION = "caption";
    private static final String TAG_TH = "th";
    private static final String ATTR_COL_SPAN = "colspan";
    private static final String ATTR_ROW_SPAN = "rowspan";
    private static final String NAMESPACE_XLSX = "urn:ukase:xlsx";
    private static final String ATTR_DATA_TYPE = "data-type";
    private static final String ATTR_NUMBER_FORMAT = "format-number";
    private static final String ATTR_DATE_FORMAT = "format-date";
    private static final short FORMAT_TEXT_DEFAULT = 49;
    private static final short FORMAT_DATE_DEFAULT = 14;
    private static final short FORMAT_NUMBER_DEFAULT = 3;

    private final Workbook wb;
    private final Sheet sheet;
    private final BlockBox box;
    private final Element table;
    private final Collection<Translator> translators;
    private final List<CellMerge> mergedCells;
    private final ConcurrentMap<Integer, Integer> cellSizes;
    private final ConcurrentMap<CellStyleKey, XSSFCellStyle> cachedStyles;
    private final short numberFormat;
    private final short textFormat;
    private final short dateFormat;

    RenderingTable(Workbook wb, Element table, BlockBox box, Collection<Translator> translators) {
        this.wb = wb;
        this.box = getBlockBoxFor(table, box);
        this.table = table;
        this.translators = translators;
        this.mergedCells = new ArrayList<>();
        this.cellSizes = new ConcurrentHashMap<>();
        this.cachedStyles = new ConcurrentHashMap<>();
        this.sheet = prepareSheet();
        DataFormat cellFormat = wb.createDataFormat();
        this.numberFormat = getNumberFormat(table, cellFormat);
        this.dateFormat = getDateFormat(table, cellFormat);
        this.textFormat = FORMAT_TEXT_DEFAULT;
    }

    @Override
    public void run() {
        new ElementList(table.getElementsByTagName(TAG_TR))
                .forEach(this::processRows);
        cellSizes.forEach(sheet::setColumnWidth);
    }

    private Sheet prepareSheet() {
        NodeList captionList = table.getElementsByTagName(TAG_CAPTION);
        if (captionList.getLength() > 0) {
            return wb.createSheet(captionList.item(0).getTextContent());
        }
        return wb.createSheet();
    }

    private void processRows(Element tr) {
        Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
        TableRowBox rowBox = (TableRowBox) getBlockBoxFor(tr, box);

        new ElementList(tr.getChildNodes()).stream()
                .filter(this::isTableCellTag)
                .forEach(td -> processCell(row, td, rowBox));
    }

    private boolean isTableCellTag(Element tag) {
        String tagName = tag.getTagName();
        return TAG_TH.equals(tagName) || TAG_TD.equals(tagName);
    }

    private void processCell(Row row, Element td, TableRowBox rowBox) {
        TableCellBox cellBox = (TableCellBox)getBlockBoxFor(td, rowBox);
        String type = td.getAttributeNS(NAMESPACE_XLSX, ATTR_DATA_TYPE);
        CellType cellType = CellType.fromString(type);
        CellStyle style = prepareCellStyle(cellBox.getStyle(), getFormat(cellType));

        mergedCells.stream()
                .filter(merge -> merge.isApplicable(row))
                .forEach(merge -> merge.fillRow(row));

        int cellNumber = row.getPhysicalNumberOfCells();
        Cell cell = createCell(cellType, row, td, cellNumber);
        cell.setCellStyle(style);

        mergeCells(row, td, cellNumber, style);
        calculateColumnWidth(cellNumber, cellBox.getStyle());
    }

    private Cell createCell(CellType type, Row row, Element td, int cellNumber) {
        Cell cell = row.createCell(cellNumber, type.getXssfType());
        String textContent = td.getTextContent().trim();

        switch (type) {
            case NUMERIC:
                setNumericValue(cell, textContent);
                break;
            default:
                cell.setCellValue(textContent);
        }
        return cell;
    }

    private void setNumericValue(Cell cell, String textContent) {
        if (textContent.isEmpty()) {
            return;
        }
        try {
            double numberValue = Double.parseDouble(textContent);
            cell.setCellValue(numberValue);
        } catch (NumberFormatException e) {
            cell.setCellType(CellType.DEFAULT.getXssfType());
            cell.setCellValue(textContent);
        }
    }

    private short getFormat(CellType type) {
        switch (type) {
            case NUMERIC:
                return numberFormat;
            case STRING:
                return textFormat;
            case DATE:
                return dateFormat;
        }
        return 0;
    }

    private void mergeCells(Row row, Element td, int cellNumber, CellStyle style) {
        int columns = intValue(td.getAttribute(ATTR_COL_SPAN), 1);
        int rows = intValue(td.getAttribute(ATTR_ROW_SPAN), 1);
        if (columns == 1 && rows == 1) {
            return;
        }

        CellMerge merge = new CellMerge(cellNumber, columns, row.getRowNum(), rows, style);
        merge.apply(sheet);
        merge.fillRow(row);
        mergedCells.add(merge);
    }

    private CellStyle prepareCellStyle(CalculatedStyle style, short format) {
        CellStyleKey key = new CellStyleKey();

        key.setFormat(format);
        translators.forEach(translator -> translator.translateCssToXlsx(style, key));

        return cachedStyles.computeIfAbsent(key, this::getNewStyle);
    }

    private XSSFCellStyle getNewStyle(CellStyleKey key) {
        XSSFCellStyle style = (XSSFCellStyle)wb.createCellStyle();
        key.applyToStyle(style, wb::createFont);
        return style;
    }

    private void calculateColumnWidth(int cellNumber, CalculatedStyle style) {
        if (style.isAutoWidth()) {
            return;
        }
        int width = Math.round(style.valueByName(CSSName.WIDTH).asFloat() * CALCULATED_KOEF);
        if (width > 0) {
            cellSizes.compute(cellNumber,
                    (num, w) -> greaterInt(w, width));
        }
    }

    private BlockBox getBlockBoxFor(Element element, BlockBox source) {
        List boxes = source.getElementBoxes(element);
        for (Object box: boxes) {
            if (box instanceof BlockBox) {
                return (BlockBox) box;
            }
        }
        throw new IllegalStateException("Cannot found styles for element " + element);
    }

    private short getDateFormat(Element table, DataFormat cellFormat) {
        String dFormat = table.getAttributeNS(NAMESPACE_XLSX, ATTR_DATE_FORMAT);
        if (dFormat == null || dFormat.trim().isEmpty()) {
            return FORMAT_DATE_DEFAULT;
        }
        return cellFormat.getFormat(dFormat);
    }

    private short getNumberFormat(Element table, DataFormat cellFormat) {
        String nFormat = table.getAttributeNS(NAMESPACE_XLSX, ATTR_NUMBER_FORMAT);
        if (nFormat == null || nFormat.trim().isEmpty()) {
            return FORMAT_NUMBER_DEFAULT;
        }
        return cellFormat.getFormat(nFormat);
    }
}
