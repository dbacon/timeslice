package bacond.timeslice.web.gwt.server.rpc;

import java.util.LinkedHashMap;
import java.util.Map;

import bacond.timeslice.web.gwt.client.beans.NotAuthenticException;
import bacond.timeslicer.app.core.AclFile;
import bacond.timeslicer.timeslice.TimesliceApp;

public class SessionTracker
{
    private static final long serialVersionUID = 1L;
    
    protected Map<String, SessionData> validSessions = new LinkedHashMap<String, SessionData>();

    public void logout(String authenticationToken)
    {
        validSessions.remove(authenticationToken);
    }

    public String authenticate(TimesliceApp timesliceApp, String username, String password)
    {
        System.out.println("authenticate(" + username + ").");
    
        // TODO: implement hashing the pw, lookup in db, applying authorization.
        // for now, just make sure it matches what's in their acl.
        String aclFileName = timesliceApp.getAclFileName();
        if (null == aclFileName) return null;
    
        String realPw = new AclFile(aclFileName).lookupPassword(username);
        if (null == realPw) return null;
    
        if (!realPw.equals(password)) return null;
    
        SessionData sd = new SessionData(username);
        validSessions.put(sd.getUuid(), sd);
    
        return sd.getUuid();
    }

    protected SessionData checkToken(String authenticationToken)
    {
        if (!validSessions.containsKey(authenticationToken))
        {
            throw new NotAuthenticException("Invalid token.");
        }
    
        SessionData sessionData = validSessions.get(authenticationToken);
        if (sessionData.getExpiresAt().isBeforeNow())
        {
            SessionData expiredSession = validSessions.remove(authenticationToken);
            throw new NotAuthenticException("Expired token: " + expiredSession.expiresAt.toString());
        }
    
        // great, have a nice time.
        return sessionData;
    }

}