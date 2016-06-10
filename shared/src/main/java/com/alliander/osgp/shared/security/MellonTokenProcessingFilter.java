package com.alliander.osgp.shared.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

public class MellonTokenProcessingFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MellonTokenProcessingFilter.class);

    private final boolean useMellonForUserIdentity;
    private final String httpHeaderForUsername;
    private final CustomAuthenticationManager authenticationManager;

    public MellonTokenProcessingFilter(final boolean useMellonForUserIdentity, final String httpHeaderForUsername,
            final CustomAuthenticationManager authenticationManager) {
        this.useMellonForUserIdentity = useMellonForUserIdentity;
        this.httpHeaderForUsername = httpHeaderForUsername;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        if (!this.useMellonForUserIdentity) {
            LOGGER.debug("Skipping Mellon token processing, since Mellon is not configured to be used.");
            chain.doFilter(request, response);
            return;
        }

        final String username;
        if (request instanceof HttpServletRequest) {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            username = httpServletRequest.getHeader(this.httpHeaderForUsername);
        } else {
            username = null;
        }

        LOGGER.info("Validating login for user {} based on input via Mellon", username);

        // TODO use the authenticationManager to stop further authentication
        // filters from being triggered.

        chain.doFilter(request, response);
    }

}
