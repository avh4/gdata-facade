package net.avh4.facade.gdata.v3;

import com.google.gdata.client.GoogleService;
import com.google.gdata.util.AuthenticationException;
import org.apache.http.client.utils.URIBuilder;

class UsernamePasswordAuthenticator implements Authenticator {

    private final String username;
    private final String appPassword;

    public UsernamePasswordAuthenticator(String username, String appPassword) {
        this.username = username;
        this.appPassword = appPassword;
    }

    @Override
    public void authenticate(GoogleService service) throws AuthenticationException {
        service.setUserCredentials(username, appPassword);
    }

    @Override
    public URIBuilder prepareRequest(URIBuilder builder) {
        return builder;
    }
}
