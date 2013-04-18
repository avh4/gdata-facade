package net.avh4.facade.gdata.v3;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class Spreadsheet {

    private final SpreadsheetService service;
    private final SpreadsheetEntry spreadsheetEntry;

    protected Spreadsheet(SpreadsheetService spreadsheetService, SpreadsheetEntry spreadsheetEntry) {
        service = spreadsheetService;
        this.spreadsheetEntry = spreadsheetEntry;
    }

    public Worksheet getWorksheet(int worksheetIndex) throws IOException, ServiceException {
        URL worksheetFeedUrl = spreadsheetEntry.getWorksheetFeedUrl();
        WorksheetFeed worksheetFeed = service.getFeed(worksheetFeedUrl, WorksheetFeed.class);

        List<WorksheetEntry> worksheetEntries = worksheetFeed.getEntries();
        WorksheetEntry worksheetEntry = worksheetEntries.get(worksheetIndex);

        return new Worksheet(service, worksheetEntry);
    }
}
