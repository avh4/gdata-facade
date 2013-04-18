package net.avh4.facade.gdata.v3;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Category;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.DocumentEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.util.ServiceException;
import org.apache.http.client.utils.URIBuilder;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class GoogleAppsApi {

    private final ServiceManager services;
    private final Authenticator authenticator;

    public GoogleAppsApi(String applicationName, final String username, final String appPassword) {
        authenticator = new UsernamePasswordAuthenticator(username, appPassword);
        this.services = new ServiceManager(applicationName, authenticator);
    }

    public GoogleAppsApi(String applicationName, final String clientKey, String clientSecret, String username) {
        authenticator = new ThreeLegOauthAuthenticator(clientKey, clientSecret, username);
        this.services = new ServiceManager(applicationName, authenticator);
    }

    public Spreadsheet createSpreadsheet(String spreadsheetName) {
        DocumentEntry entry = new DocumentEntry();
        entry.setTitle(new PlainTextConstruct(spreadsheetName));
        String categoryTerm = "http://schemas.google.com/docs/2007#spreadsheet";
        entry.getCategories().clear();
        entry.getCategories().add(new Category(categoryTerm));
        try {
            DocsService documentsService = services.getDocumentsService();
            final URL feedUrl = new URL("https://docs.google.com/feeds/default/private/full");
            final DocumentEntry newDocument = documentsService.insert(feedUrl, entry);
            return findSpreadsheetByEtag(newDocument.getEtag());
        } catch (IOException | ServiceException e) {
            throw new RuntimeException("Failed to create spreadsheet: " + spreadsheetName, e);
        }
    }

    public List<Spreadsheet> findSpreadsheetsByName(String spreadsheetName) throws IOException, ServiceException {
        URL feedUrl;
        try {
            URIBuilder builder;
            builder = new URIBuilder("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
            builder.addParameter("title", spreadsheetName);

            builder.addParameter("xoauth_requestor_id", "gruen0aermel@gmail.com");
            feedUrl = builder.build().toURL();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        final SpreadsheetService spreadsheetService = services.getSpreadsheetService();
        SpreadsheetFeed resultFeed = spreadsheetService.getFeed(feedUrl, SpreadsheetFeed.class);

        List<SpreadsheetEntry> spreadsheets = resultFeed.getEntries();
        return Lists.transform(spreadsheets, new Function<SpreadsheetEntry, Spreadsheet>() {
            @Nullable
            @Override
            public Spreadsheet apply(@Nullable SpreadsheetEntry spreadsheetEntry) {
                return new Spreadsheet(spreadsheetService, spreadsheetEntry);
            }
        });
    }

    public Spreadsheet findSpreadsheetByEtag(String etag) throws ServiceException, IOException {
        URL feedUrl;
        try {
            URIBuilder builder;
            builder = new URIBuilder("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
            builder.addParameter("etag", etag);
            authenticator.prepareRequest(builder);
            feedUrl = builder.build().toURL();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        final SpreadsheetService spreadsheetService = services.getSpreadsheetService();
        SpreadsheetFeed resultFeed = spreadsheetService.getFeed(feedUrl, SpreadsheetFeed.class);

        if (resultFeed.getEntries().size() > 1) {
            new Exception("Found more than one document for etag: " + etag).printStackTrace();
        }
        return new Spreadsheet(spreadsheetService, resultFeed.getEntries().get(0));
    }
}
