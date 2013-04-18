package net.avh4.facade.gdata.v3;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.util.AuthenticationException;

class ServiceManager {
    private final Authenticator authenticator;
    private final String applicationName;
    private SpreadsheetService spreadsheetService;
    private DocsService documentsService;

    ServiceManager(String applicationName, Authenticator authenticator) {
        this.authenticator = authenticator;
        this.applicationName = applicationName;
    }

    public SpreadsheetService getSpreadsheetService() throws AuthenticationException {
        if (spreadsheetService == null) {
            spreadsheetService = new SpreadsheetService(applicationName);
            authenticator.authenticate(spreadsheetService);
        }
        return spreadsheetService;
    }

    public DocsService getDocumentsService() throws AuthenticationException {
        if (documentsService == null) {
            documentsService = new DocsService(applicationName);
            authenticator.authenticate(documentsService);
        }
        return documentsService;
    }
}
