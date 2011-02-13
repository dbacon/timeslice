package com.enokinomi.timeslice.web.session.server.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoDao;
import com.enokinomi.timeslice.lib.userinfo.impl.Sha1V1Scheme;
import com.enokinomi.timeslice.web.core.client.util.NeedsSetupException;
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
    private final IUserInfoDao userInfoDao;

    private final String aclFilename;


    @Inject
    SessionTracker(SessionDataProvider sessionDataProvider, @Named("acl") String aclFilename, IUserInfoDao userInfoDao)
    {
        this.sessionDataProvider = sessionDataProvider;
        this.aclFilename = aclFilename;
        this.userInfoDao = userInfoDao;
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
        boolean authenticated = userInfoDao.authenticate(username, password);

        if (authenticated)
        {
            log.info("Authenticated from database");
        }
        else
        {
            String aclFileName = aclFilename;
            if (null == aclFileName)
            {
                log.warn(mkMsg(username, "No ACL filename"));

                // TODO: this is the only line needed after ACL-file support is removed
                //       but we don't do it unless there's no ACL-file.
                //       While there still is, users should be added there, and will
                //       be auto-migrated to db.

                checkFor1stTimeSetup();
            }
            else
            {
                String realPw = null;
                try
                {
                    realPw = new AclFile(aclFileName).lookupPassword(username);
                }
                catch (Exception e)
                {
                    checkFor1stTimeSetup();
                }

                if (null == realPw)
                {
                    log.info(mkMsg(username, "No password-entry for '" + username + "' in ACL file '" + aclFileName + "'."));
                }
                else
                {
                    if (!realPw.equals(password))
                    {
                        log.info(mkMsg(username, "Password mis-match for  '" + username + "' in ACL file '" + aclFileName + "'."));
                    }
                    else
                    {
                        log.info(mkMsg(username, "Authentication match for  '" + username + "' in ACL file '" + aclFileName + "'"));
                        authenticated = true;

                        // Auto-add this id to the user db,
                        //  so going forward, it will be authenticated from the db.
                        //  then the file can be removed, and this support can be removed in future releases.
                        userInfoDao.createUser(username, password, Sha1V1Scheme.class.getCanonicalName());
                        log.info("Auto-created user '" + username + "' in database for next time");
                    }
                }
            }
        }

        if (authenticated)
        {
            log.info("Generating session and token.");

            SessionData sd = sessionDataProvider.createSessionForAuthenticatedUser(username);
            validSessions.put(sd.getUuid(), sd);

            return sd.getUuid();
        }
        else
        {
            log.info("No session and token generated.");
            return null;
        }
    }

    private void checkFor1stTimeSetup()
    {
        // TODO: get already asked from user settings.
        boolean alreadyAsked = false;

        if (userInfoDao.userCount() == 0 && !alreadyAsked)
        {
            throw new NeedsSetupException("No user accounts detected");
        }
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
