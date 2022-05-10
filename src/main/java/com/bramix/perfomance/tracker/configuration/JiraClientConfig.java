package com.bramix.perfomance.tracker.configuration;

import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class JiraClientConfig {

    @Value("${jira.uri}")
    private String uri;
    @Value("${jira.username}")
    private String username;
    @Value("${jira.token}")
    private String token;

    @Bean
    public SearchRestClient createJiraSearchClient() {
        var authenticationHandler = new BasicHttpAuthenticationHandler(username, token);
        URI serverUri;
        try {
            serverUri = new URI(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unable to parse url + " , e);
        }

        return new AsynchronousJiraRestClientFactory()
                .createWithAuthenticationHandler(serverUri, authenticationHandler)
                .getSearchClient();
    }
}
