package com.alliander.osgp.shared.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.alliander.osgp.shared.usermanagement.PlatformDomain;

/**
 * Simple authentication class.
 * 
 * @author CGI
 * 
 */
public final class CustomAuthentication implements Authentication {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 1L;
    private transient String userName;
    private transient String organisationIdentification;
    private transient List<String> applications;
    private transient boolean authenticated;
    private transient Object details;
    private static final transient String ROLE = "ROLE_USER";
    private transient String token;
    private List<PlatformDomain> domains;

    public String getToken() {
        return this.token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    @Override
    public String getName() {
        return this.userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public void setOrganisationIdentification(final String organisationIdentification) {
        this.organisationIdentification = organisationIdentification;
    }

    public List<String> getApplications() {
        return this.applications;
    }

    public void setApplications(final List<String> applications) {
        this.applications = applications;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        final Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        if (this.authenticated) {
            authorities.add(new SimpleGrantedAuthority(ROLE));
        }

        return authorities;
    }

    @Override
    public Object getCredentials() {
        return this.userName + " is web-api-user-management authenticated user";
    }

    @Override
    public Object getDetails() {
        return this.details;
    }

    public void setDetails(final Object details) {
        this.details = details;
    }

    @Override
    public Object getPrincipal() {
        return this.userName;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(final boolean arg0) {
        this.authenticated = arg0;
    }

    /**
     * @return the domains
     */
    public List<PlatformDomain> getDomains() {
        return this.domains;
    }

    /**
     * @param domains
     *            the domains to set
     */
    public void setDomains(final List<PlatformDomain> domains) {
        this.domains = domains;
    }
}
