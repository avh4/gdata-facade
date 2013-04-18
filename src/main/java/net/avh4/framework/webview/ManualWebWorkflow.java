package net.avh4.framework.webview;

import javax.swing.*;

public class ManualWebWorkflow implements WebWorkflow {
    @Override
    public WebCallbackResponse execute(String approvalPageUrl) {
        System.out.println(approvalPageUrl);

        final String queryString = JOptionPane.showInputDialog(null, "Enter OAUTH response query");
        return new WebCallbackResponse() {
            @Override
            public String getQueryString() {
                return queryString;
            }
        };
    }
}
