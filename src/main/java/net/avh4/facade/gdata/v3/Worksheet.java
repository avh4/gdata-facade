package net.avh4.facade.gdata.v3;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class Worksheet {

    private final WorksheetEntry worksheetEntry;
    private final SpreadsheetService service;

    public Worksheet(SpreadsheetService service, WorksheetEntry worksheetEntry) {
        this.worksheetEntry = worksheetEntry;
        this.service = service;
    }

    public void addRow(Map<String, String> attributes) throws IOException, ServiceException {
        ListEntry row = new ListEntry();
        for (Map.Entry<String, String> attribute : attributes.entrySet()) {
            row.getCustomElements().setValueLocal(attribute.getKey(), attribute.getValue());
        }
        service.insert(worksheetEntry.getListFeedUrl(), row);
    }

    public void setCell(int row, int column, String value) throws IOException, ServiceException {
        URL cellFeedUrl = worksheetEntry.getCellFeedUrl();
        CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);

        CellEntry cellEntry = new CellEntry(row, column, value);
        cellFeed.insert(cellEntry);
    }
}
