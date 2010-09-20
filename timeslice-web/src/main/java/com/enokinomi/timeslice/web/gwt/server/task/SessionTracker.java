package com.enokinomi.timeslice.web.gwt.server.task;

import java.util.LinkedHashMap;
import java.util.Map;

import com.enokinomi.timeslice.app.core.AclFile;
import com.enokinomi.timeslice.web.gwt.client.core.NotAuthenticException;
import com.google.inject.Inject;
import com.google.inject.name.Named;


public class SessionTracker
{
    private static final long serialVersionUID = 1L;

    protected Map<String, SessionData> validSessions = new LinkedHashMap<String, SessionData>();

    private final SessionDataProvider sessionDataProvider;

    private final String aclFilename;

    @Inject
    public SessionTracker(SessionDataProvider sessionDataProvider, @Named("acl") String aclFilename)
    {
        this.sessionDataProvider = sessionDataProvider;
        this.aclFilename = aclFilename;
    }

    public synchronized void logout(String authenticationToken)
    {
        validSessions.remove(authenticationToken);
    }

    public synchronized String authenticate(String username, String password)
    {
        System.out.println("authenticate(" + username + ") - " + this.toString());

        // TODO: implement hashing the pw, lookup in db, applying authorization.
        // for now, just make sure it matches what's in their acl.
        String aclFileName = aclFilename;
        if (null == aclFileName)
        {
            System.out.println("  No ACL filename");
            return null;
        }

        String realPw = new AclFile(aclFileName).lookupPassword(username);
        if (null == realPw)
        {
            System.out.println("  No password-entry for '" + username + "' in ACL file '" + aclFileName + "'.");
            return null;
        }

        if (!realPw.equals(password))
        {
            System.out.println("  Password mis-match for  '" + username + "' in ACL file '" + aclFileName + "'.");
            return null;
        }

        System.out.println("  Authentication match for  '" + username + "' in ACL file '" + aclFileName + "', generating session and token.");

        SessionData sd = sessionDataProvider.createSessionForAuthenticatedUser(username);
        validSessions.put(sd.getUuid(), sd);

        return sd.getUuid();
    }

    public synchronized SessionData checkToken(String authenticationToken) throws NotAuthenticException
    {
        if (!validSessions.containsKey(authenticationToken))
        {
            throw new NotAuthenticException("Invalid token.");
        }

        SessionData sessionData = validSessions.get(authenticationToken);
        if (sessionData.getExpiresAt().isBeforeNow())
        {
            SessionData expiredSession = validSessions.remove(authenticationToken);
            throw new NotAuthenticException("Expired token: " + expiredSession.getExpiresAt().toString());
        }

        // great, have a nice time.
        return sessionData;
    }

}
