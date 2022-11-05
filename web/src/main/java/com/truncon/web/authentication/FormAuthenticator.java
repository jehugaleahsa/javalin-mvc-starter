package com.truncon.web.authentication;

import com.truncon.auditing.AuditContext;
import com.truncon.modeling.repositories.UserRepository;
import com.truncon.models.User;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.CommonProfile;

import javax.inject.Inject;

public final class FormAuthenticator implements Authenticator {
    private final AuditContext auditContext;
    private final UserRepository repository;

    @Inject
    public FormAuthenticator(AuditContext auditContext, UserRepository repository) {
        this.auditContext = auditContext;
        this.repository = repository;
    }

    @Override
    public void validate(Credentials credentials, WebContext webContext, SessionStore sessionStore) {
        if (!(credentials instanceof UsernamePasswordCredentials)) {
            throw new CredentialsException("A username/password credential is required.");
        }
        UsernamePasswordCredentials actualCredentials = (UsernamePasswordCredentials) credentials;
        User user = getUser(actualCredentials);
        if (!isValidCredentials(actualCredentials, user)) {
            throw new BadCredentialsException("The username/password combination is not valid.");
        }
        User unexpectedUser = auditContext.setUser(user);
        if (unexpectedUser != null) {
            throw new IllegalStateException("A user was already present when validating the user credentials.");
        }
        CommonProfile profile = new CommonProfile();
        profile.setId(actualCredentials.getUsername());
        profile.addAttribute("email", actualCredentials.getUsername());
        credentials.setUserProfile(profile);
    }

    private User getUser(UsernamePasswordCredentials credentials) {
        String givenUserName = credentials.getUsername();
        return repository.getUserByEmail(givenUserName).orElse(null);
    }

    private boolean isValidCredentials(UsernamePasswordCredentials credentials, User user) {
        if (user == null || user.isSystemUser() || user.getPassword() == null) {
            return false;
        }
        String givenPassword = credentials.getPassword();
        String hashedPassword = Passwords.hash(givenPassword, user.getSalt());
        return user.getPassword().equals(hashedPassword);
    }
}
