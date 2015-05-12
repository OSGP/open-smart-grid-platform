package com.alliander.osgp.shared.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.alliander.osgp.shared.usermanagement.AuthenticationClient;
import com.alliander.osgp.shared.usermanagement.AuthenticationClientException;
import com.alliander.osgp.shared.usermanagement.AuthenticationResponse;
import com.alliander.osgp.shared.usermanagement.LoginRequest;
import com.alliander.osgp.shared.usermanagement.LoginResponse;

/**
 * Authentication manager class that offers login and authentication token
 * validation for web applications.
 *
 * The web application must create an instance of CustomAuthenticationManager
 * using an AuthenticationClient instance and a member of enumeration
 * com.alliander.osp.usermanagementweb.domain.Application as application String.
 * The web application shall use the authenticate(Authentication) function to
 * login. Upon successful authentication an CustomAuthentication instance will
 * be returned. The web application shall use the CustomAuthentication instance
 * as argument for the function validateToken(CustomAuthentication) to validate
 * the authentication token. After each validation, a new authentication token
 * is set in the CustomAuthentication instance.
 *
 * @author CGI
 *
 */
public final class CustomAuthenticationManager implements AuthenticationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationManager.class);

    private static final String NULL_AUTHENTICATION = "null authentication";
    private static final String NULL_BLANK_USERNAME_CREDENTIALS = "null/blank username/credential";
    private static final String LOGIN_ATTEMPT_FAILED = "login attempt failed";
    private static final String LOGIN_RESPONSE_IS_NULL = "login response is null";
    private static final String LOGIN_RESPONSE_IS_NOT_OK = "login response is not OK";
    private static final String AUTHENTICATION_RESPONSE_IS_NULL = "authentication response is null";
    private static final String AUTHENTICATION_RESPONSE_IS_NOT_OK = "authentication response is not OK";

    private static final String OK = "OK";

    private final AuthenticationClient authenticationClient;
    private final String application;

    /**
     * Construct an instance using
     * com.alliander.osgp.shared.usermanagement.AuthenticationClient and
     * com.alliander.osgp.usermanagementweb.domain.Application as application
     * String
     *
     * @param authenticationClient
     *            The AuthenticationClient instance.
     * @param application
     *            The Application as String.
     */
    public CustomAuthenticationManager(final AuthenticationClient authenticationClient, final String application) {
        this.authenticationClient = authenticationClient;
        this.application = application;
    }

    /**
     * The login function. Use an Authentication instance with the principal set
     * to the user name and the credentials set to the password. Authentication
     * will be granted if the user is permitted for an/this application, the
     * user name is registered and the password matches.
     *
     * @param authentication
     *            An Authentication instance containing user name and password.
     *
     * @return An CustomAuthentication instance containing user name, users
     *         organisation identification, platform domains, user role, user
     *         applications and an authentication token.
     */
    @Override
    public Authentication authenticate(final Authentication authentication) {

        // Check if user has authentication instance.
        this.checkAuthenticationInstance(authentication);

        // Get user name and password.
        final String username = authentication.getName();
        final String password = (String) authentication.getCredentials();

        // Check user name and password.
        this.checkUsernameAndPasswordForEmptiness(username, password);

        // Prepare LoginRequest and LoginResponse.
        final LoginRequest loginRequest = new LoginRequest(username, password, this.application);
        LoginResponse loginResponse = null;

        // Try to login.
        try {

            loginResponse = this.authenticationClient.login(loginRequest);
        } catch (final Exception e) {
            LOGGER.debug(LOGIN_ATTEMPT_FAILED, e);
            throw new BadCredentialsException(LOGIN_ATTEMPT_FAILED, e);
        }

        // Check the response.
        this.checkLoginResponse(loginResponse);

        // Create the CustomAuthentication instance.
        return this.createCustomAuthenticationInstance(username, loginResponse);
    }

    private void checkAuthenticationInstance(final Authentication authentication) {

        // Check if user has authentication instance.
        if (authentication == null) {
            LOGGER.debug(NULL_AUTHENTICATION);
            throw new BadCredentialsException(NULL_AUTHENTICATION);
        }
    }

    private void checkUsernameAndPasswordForEmptiness(final String username, final String password) {

        // Check user name and password.
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            LOGGER.debug(NULL_BLANK_USERNAME_CREDENTIALS);
            throw new BadCredentialsException(NULL_BLANK_USERNAME_CREDENTIALS);
        }
    }

    private void checkLoginResponse(final LoginResponse loginResponse) {

        // Check if the response equals null.
        if (loginResponse == null) {
            LOGGER.debug(LOGIN_RESPONSE_IS_NULL);
            throw new BadCredentialsException(LOGIN_RESPONSE_IS_NULL);
        }

        // Check if the response is OK.
        if (!loginResponse.getFeedbackMessage().equals(OK)) {
            LOGGER.debug(LOGIN_RESPONSE_IS_NOT_OK);
            throw new BadCredentialsException(LOGIN_RESPONSE_IS_NOT_OK);
        }
    }

    private CustomAuthentication createCustomAuthenticationInstance(final String username,
            final LoginResponse loginResponse) {

        // Create the instance.
        final CustomAuthentication customAuthentication = new CustomAuthentication();
        customAuthentication.setAuthenticated(true);
        customAuthentication.setUserName(username);
        customAuthentication.setOrganisationIdentification(loginResponse.getOrganisationIdentification());
        customAuthentication.setDomains(loginResponse.getDomains());
        customAuthentication.getAuthorities().clear();
        final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(loginResponse.getRole());
        customAuthentication.getAuthorities().add(grantedAuthority);
        customAuthentication.setApplications(loginResponse.getApplications());
        customAuthentication.setToken(loginResponse.getToken());

        return customAuthentication;
    }

    /**
     * Check the validity of an authentication token.
     *
     * @param authentication
     *            The CustomAuthentication instance obtained by successful
     *            authentication.
     *
     * @throws AuthenticationClientException
     *             In case the organisationIdentification or token are an empty
     *             string, the token is not valid, the response is null, the
     *             HTTP status code is not equal to 200 OK or if the response
     *             body is empty.
     */
    public void validateToken(final CustomAuthentication authentication) throws AuthenticationClientException {

        // Check if user has authentication instance.
        this.checkAuthenticationInstance(authentication);

        // Set authenticated to false.
        authentication.setAuthenticated(false);

        final String organisationIdentification = authentication.getOrganisationIdentification();
        final String token = authentication.getToken();

        final AuthenticationResponse authenticationResponse = this.authenticationClient.authenticate(
                organisationIdentification, token);

        // Check the response.
        this.checkAuthenticationResponse(authenticationResponse);

        // Set authenticated to true, set the new token and user name.
        authentication.setToken(authenticationResponse.getToken());
        authentication.setUserName(authenticationResponse.getUserName());
        authentication.setAuthenticated(true);
    }

    private void checkAuthenticationResponse(final AuthenticationResponse authenticationResponse)
            throws AuthenticationClientException {

        // Check if the response equals null.
        if (authenticationResponse == null) {
            LOGGER.debug(AUTHENTICATION_RESPONSE_IS_NULL);
            throw new AuthenticationClientException(AUTHENTICATION_RESPONSE_IS_NULL);
        }

        // Check if the response is OK.
        if (!authenticationResponse.getFeedbackMessage().equals(OK)) {
            LOGGER.debug(AUTHENTICATION_RESPONSE_IS_NOT_OK);
            throw new AuthenticationClientException(AUTHENTICATION_RESPONSE_IS_NOT_OK);
        }
    }
}
