package com.enokinomi.timeslice.web.session.server.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.enokinomi.timeslice.web.core.client.util.NotAuthenticException;
import com.enokinomi.timeslice.web.session.server.core.ISessionTracker;
import com.enokinomi.timeslice.web.session.server.core.SessionData;
import com.google.inject.Inject;
import com.google.inject.name.Named;


class SessionTracker implements ISessionTracker
{
    private static final Logger log = Logger.getLogger(SessionTracker.class);

    private static final long serialVersionUID = 1L;

    protected Map<String, SessionData> validSessions = new LinkedHashMap<String, SessionData>();

    private final SessionDataProvider sessionDataProvider;

    private final String aclFilename;

    @Inject
    SessionTracker(SessionDataProvider sessionDataProvider, @Named("acl") String aclFilename)
    {
        this.sessionDataProvider = sessionDataProvider;
        this.aclFilename = aclFilename;
    }

    @Override
    public synchronized void logout(String authenticationToken)
    {
        validSessions.remove(authenticationToken);
    }

    private String mkMsg(String username, String msg)
    {
        return "authenticate(" + username + ") - " + this.toString() + ": " + msg;
    }

    @Override
    public synchronized String authenticate(String username, String password)
    {
        // TODO: implement hashing the pw, lookup in db, applying authorization.
        // for now, just make sure it matches what's in their acl.
        String aclFileName = aclFilename;
        if (null == aclFileName)
        {
            log.warn(mkMsg(username, "No ACL filename"));
            return null;
        }

        String realPw = new AclFile(aclFileName).lookupPassword(username);
        if (null == realPw)
        {
            log.info(mkMsg(username, "No password-entry for '" + username + "' in ACL file '" + aclFileName + "'."));
            return null;
        }

        if (!realPw.equals(password))
        {
            log.info(mkMsg(username, "Password mis-match for  '" + username + "' in ACL file '" + aclFileName + "'."));
            return null;
        }

        log.info(mkMsg(username, "Authentication match for  '" + username + "' in ACL file '" + aclFileName + "', generating session and token."));

        SessionData sd = sessionDataProvider.createSessionForAuthenticatedUser(username);
        validSessions.put(sd.getUuid(), sd);

        return sd.getUuid();
    }

    @Override
    public synchronized SessionData checkToken(String authenticationToken) throws NotAuthenticException
    {
        if (!validSessions.containsKey(authenticationToken))
        {
            throw new NotAuthenticException("Invalid token.");
        }

        SessionData sessionData = validSessions.get(authenticationToken);
        if (sessionData.getExpiresAt().isBeforeNow())
        {
            validSessions.remove(authenticationToken);
            throw new NotAuthenticException("Expired token.");
        }

        // great, have a nice time.
        return sessionData;
    }

}
