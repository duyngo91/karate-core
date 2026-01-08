package core.platform.web.element.table;

import com.intuit.karate.driver.Driver;
import com.intuit.karate.driver.Element;
import core.platform.web.ChromeCustom;
import core.platform.web.common.Common;
import core.platform.web.element.CheckBox;
import core.platform.web.element.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Table extends WebElement {
    private static final Logger logger = LoggerFactory.getLogger(Table.class);
    private String xpathHeader;
    private String xpathRow;
    private final List<String> skipHeaders = new ArrayList<>();
    private static final String COLUMN_INDEX = "[<col>]";
    private String xpathCell;
    private static final String REPLACE_COLUMN = "<col>";
    private static final String REPLACE_ROW = "<row>";
    private static final String TEXT_AREA = "<textarea";
    private static final String INPUT = "<input";
    private static final String TYPE_TEXT = "type=\"text\"";
    private static final String TYPE_CHECKBOX = "type=\"checkbox\"";
    private static final String DIV = "<div";
    private static final String SELECT = "<select";
    private static final String MAT_SELECT = "<mat-select";
    private static final String NO_DATA = "Không có dữ liệu";
    private static final String MESSAGE = "message";


    public Table(Driver driver, String xpathHeader) {
        super(driver, xpathHeader);
        this.xpathHeader = xpathHeader;
        this.xpathCell = "//td";
    }

    public Table(Driver driver, String xpathHeader, String xpathRow) {
        this(driver, xpathHeader);
        this.xpathRow = xpathRow;
        this.xpathCell = "//td";
    }
    public Element style(String style) {
        return getDriver().tableManager.getService(style).createTable(getDriver(), xpathHeader, xpathRow);
    }

    public Table(Driver driver, Element element) {
        super(driver, element);
    }

    public List<String> getHeaders() {
        List<String> headers = new ArrayList<>();
        waitFor();
        String xpathHeaderWithIndex = getXpathHeader() + COLUMN_INDEX;
        int totalHeaders = count();

        for (int index = 0; index < totalHeaders; index++) {
            int col = index + 1;
            String txt = new WebElement(driver, xpathHeaderWithIndex.replace(REPLACE_COLUMN, String.valueOf(col))).getText();
            headers.add(txt.trim());
        }
        return headers;
    }

    public List<String> getHeadersByScroll(String xpathOfScroll, int x){
        delay(500);
        getDriver().scrollToLeftInfoElement(xpathOfScroll);
        List<String> rs = getHeaders();
        while(!getDriver().isScrollToEndOfRight(xpathOfScroll)){
            getDriver().scrollElementBy(xpathOfScroll, x, 0);
            delay(500);
            List<String> current = getHeaders();
            rs.addAll(current);
            rs = rs.stream().distinct().collect(Collectors.toList());
        }

        return rs;
    }

    public List<Map<String, String>> getDataByScroll(String xpathOfScroll, int x){
        waitFor();
        int totalRows = countRows();
        List<String> headers = removeAccentAndCamel(getHeadersByScroll(xpathOfScroll,x));
        if (isEmpty()) return List.of(Map.of(MESSAGE, NO_DATA));
        // Lấy tất cả dữ liệu từ bảng
        List<Map<String, String>> tableData = new ArrayList<>();
        for (int row = 0; row < totalRows; row++) {
            Map<String, String> rowData = getDataOneRowByScroll(headers, row, xpathOfScroll, x).get(0);
            tableData.add(rowData);
        }
        return tableData;
    }


    private List<String> getHeadersWithRemoveAccentAndCamel() {
        return removeAccentAndCamel(getHeaders());
    }

    private List<String> removeAccentAndCamel(List<String> list) {
        return list.stream().map(Common::removeAccentAndCamel).collect(Collectors.toList());
    }
    public int countRows() {
        return new WebElement(driver, getXpathRow()).count();
    }

    public boolean isEmpty() {
        WebElement firstCell = new WebElement(driver, getXpathRow() + getXpathCell() + "[1]");
        return firstCell.count() == 0 || firstCell.getText().contains(NO_DATA);
    }

    public Table skips(String... headerNames) {
        if (headerNames != null) {
            for (String header : headerNames) {
                if (header != null) {
                    skipHeaders.add(header.trim());
                }
            }
        }
        return this;
    }

    public List<Map<String, String>> getDataOnSecondsRow() {
        return getDataOnRow(1);
    }
    public List<Map<String, String>> getDataOnFirstRow() {
        return getDataOnRow(0);
    }

    public List<Map<String, String>> getDataOnFirstRowByScroll(String xpathOfScroll, int x) {
        return getDataOneRowByScroll(getHeadersByScroll(xpathOfScroll, x), 0, xpathOfScroll, x);
    }

    public List<Map<String, String>> getDataOnRow(int index) {
        waitFor();
        if (isEmpty()) return List.of(Map.of(MESSAGE, NO_DATA));
        List<Map<String, String>> tableData = new ArrayList<>();
        tableData.add(getDataOneRow(getHeadersWithRemoveAccentAndCamel(), index));
        return tableData;
    }

    public List<Map<String, String>> getData() {
        waitFor();
        int totalRows = countRows();
        List<String> headers = getHeadersWithRemoveAccentAndCamel();
        if (isEmpty()) return List.of(Map.of(MESSAGE, NO_DATA));

        // Lấy tất cả dữ liệu từ bảng
        List<Map<String, String>> tableData = new ArrayList<>();
        for (int row = 0; row < totalRows; row++) {
            Map<String, String> rowData = getDataOneRow(headers, row);
            tableData.add(rowData);
        }
        return tableData;
    }

    private String getCellValue(WebElement cell) {
        String cellHTML = cell.outerHTML();

        // Kiểm tra textarea
        if (cellHTML.contains(TEXT_AREA)) {
            return new WebElement(driver, cell.getLocator() + "//textarea").getValue();
        }

        // Kiểm tra text input
        if (cellHTML.contains(INPUT) && cellHTML.contains(TYPE_TEXT)) {
            return new WebElement(driver, cell.getLocator() + "//input").getValue();
        }

        // Kiểm tra checkbox
        if (cellHTML.contains(INPUT) && cellHTML.contains(TYPE_CHECKBOX)) {
            return String.valueOf(new CheckBox(driver, cell.getLocator() + "//input").isChecked());
        }

        // Kiểm tra select
        if (cellHTML.contains(SELECT)) {
            return ((ChromeCustom) driver).getSelectedText(cell.getLocator() + "//select").toString().trim();
        }

        // Kiểm tra mat-select
        if (cellHTML.contains(MAT_SELECT)) {
            return ((ChromeCustom) driver).textContent(cell.getLocator() + "//mat-select").trim();
        }

        // Kiểm tra section clamp
        if (cellHTML.contains(DIV) && cellHTML.contains("classroot=\"div.section_clamp\"")) {
            return new WebElement(driver, cell.getLocator() + "//div[@classroot='div.section_clamp']").getText().trim();
        }

        // Kiểm tra div thông thường
        if (cellHTML.contains(DIV) && !cellHTML.contains("classroot=\"div.section_clamp\"")) {
            return new WebElement(driver, cell.getLocator() + "/div").getText().trim();
        }

        // Trường hợp mặc định
        return cell.getText().trim();
    }

    private WebElement getCell(int row, int col) {
        return new WebElement(driver, (getXpathRow() + "[<row>]"+ getXpathCell() + COLUMN_INDEX)
                .replace(REPLACE_ROW, String.valueOf(row))
                .replace(REPLACE_COLUMN, String.valueOf(col)));
    }

    private Map<String, String> getDataOneRow(List<String> headers, int row) {
        Map<String, String> rowData = new LinkedHashMap<>();
        for (int col = 0; col < headers.size(); col++) {
            String headerName = headers.get(col);
            if (skipHeaders.stream().noneMatch(s -> s.equalsIgnoreCase(headerName))) {
                logger.info("[DebugTable]read data from row : {},column : {}", row + 1, col + 1);
                WebElement cell = getCell(row + 1, col + 1);
                String cellValue = getCellValue(cell);
                rowData.put(headerName, cellValue);
            }
        }
        return rowData;
    }

    public List<Map<String, String>> getDataOneRowByScroll(List<String> headers, int row, String xpathOfScroll, int x) {
        delay(500);
        getDriver().scrollToLeftInfoElement(xpathOfScroll);
        List<String> currentHeaders = getHeadersWithRemoveAccentAndCamel();
        Map<String, String> rowData = new LinkedHashMap<>();
        for (String h : headers){
            if (skipHeaders.stream().noneMatch(s -> s.equalsIgnoreCase(h))) {
                int col = currentHeaders.indexOf(h);
                while (col < 0){
                    // Không Tìm thấy header nên scroll qua phải để tìm
                    getDriver().scrollElementBy(xpathOfScroll, x, 0);
                    delay(500);
                    currentHeaders = getHeadersWithRemoveAccentAndCamel();
                    col = currentHeaders.indexOf(h);
                }
                logger.info("[DebugTable]read data from row : {},column : {}", row + 1, col + 1);
                WebElement cell = getCell(row + 1, col + 1);
                String cellValue = getCellValue(cell);
                rowData.put(h, cellValue);
            }
        }
        return List.of(rowData);
    }

    private String getXpathHeader() {
        return xpathHeader;
    }

    private String getXpathRow() {
        return xpathRow;
    }


    public String getXpathCell() {
        return xpathCell;
    }

    public void setXpathCell(String xpathCell) {
        this.xpathCell = xpathCell;
    }

    public ChromeCustom getDriver() {
        return (ChromeCustom) driver;
    }

}
