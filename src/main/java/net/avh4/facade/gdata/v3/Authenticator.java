package net.avh4.facade.gdata.v3;

import com.google.gdata.client.GoogleService;
import com.google.gdata.util.AuthenticationException;
import org.apache.http.client.utils.URIBuilder;

interface Authenticator {
    void authenticate(GoogleService service) throws AuthenticationException;

    URIBuilder prepareRequest(URIBuilder builder);
}
