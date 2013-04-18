package net.avh4.facade.gdata.v3;

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.authn.oauth.GoogleOAuthHelper;
import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.util.AuthenticationException;
import net.avh4.framework.webview.ManualWebWorkflow;
import net.avh4.framework.webview.WebCallbackResponse;
import net.avh4.framework.webview.WebWorkflow;
import org.apache.http.client.utils.URIBuilder;

class ThreeLegOauthAuthenticator implements Authenticator {
    private final String clientId;
    private final String clientSecret;
    private final String username;
    private final String scope;
    private final WebWorkflow webWorkflow;

    public ThreeLegOauthAuthenticator(String clientId, String clientSecret, String username) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.username = username;
        scope = "https://spreadsheets.google.com/feeds/";
        webWorkflow = new ManualWebWorkflow();
    }

    @Override
    public void authenticate(GoogleService service) throws AuthenticationException {
        GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
        oauthParameters.setOAuthConsumerKey(clientId);
        oauthParameters.setOAuthConsumerSecret(clientSecret);
        oauthParameters.setOAuthCallback("http://localhost/oauth-callback");
        oauthParameters.setScope(scope);

        GoogleOAuthHelper oauthHelper = new GoogleOAuthHelper(new OAuthHmacSha1Signer());
        try {
            oauthHelper.getUnauthorizedRequestToken(oauthParameters);

            String approvalPageUrl = oauthHelper.createUserAuthorizationUrl(oauthParameters);
            WebCallbackResponse response = webWorkflow.execute(approvalPageUrl);

            oauthHelper.getOAuthParametersFromCallback(response.getQueryString(), oauthParameters);

            String accessToken = oauthParameters.getOAuthToken();
            System.out.println("OAuth Access Token: " + accessToken);
            String accessTokenSecret = oauthParameters.getOAuthTokenSecret();
            System.out.println("OAuth Access Token's Secret: " + accessTokenSecret);

//            service.useSsl();
            service.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
        } catch (OAuthException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public URIBuilder prepareRequest(URIBuilder builder) {
        builder.addParameter("xoauth_requestor_id", username);
        return builder;
    }
}
