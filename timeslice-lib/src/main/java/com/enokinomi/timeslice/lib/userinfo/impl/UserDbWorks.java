package com.enokinomi.timeslice.lib.userinfo.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.enokinomi.timeslice.lib.commondatautil.api.IBaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.userinfo.api.IUserDbWorks;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.google.inject.Inject;

public class UserDbWorks implements IUserDbWorks
{
    private static final int RequiredVersion = 6;

    private final IBaseHsqldbOps baseStore;

    @Inject
    UserDbWorks(IBaseHsqldbOps baseStore)
    {
        this.baseStore = baseStore;
    }

    public static class AccountData
    {
        private final int id;
        private final int active;
        private final String username;
        private final String hashscheme;
        private final String hashsalt;
        private final String hashvalue;
        private final Timestamp lastchanged;

        public AccountData(int id, int active, String username, String hashscheme, String hashsalt, String hashvalue, Timestamp lastchanged)
        {
            this.id = id;
            this.active = active;
            this.username = username;
            this.hashscheme = hashscheme;
            this.hashsalt = hashsalt;
            this.hashvalue = hashvalue;
            this.lastchanged = lastchanged;
        }

        public int getId()
        {
            return id;
        }

        public int getActive()
        {
            return active;
        }

        public String getUsername()
        {
            return username;
        }

        public String getHashscheme()
        {
            return hashscheme;
        }

        public String getHashsalt()
        {
            return hashsalt;
        }

        public String getHashvalue()
        {
            return hashvalue;
        }

        public Timestamp getLastchanged()
        {
            return lastchanged;
        }

    }

    @Override
    public IConnectionWork<AccountData> getUserAccountData(final String user, String password)
    {
        return new IConnectionWork<AccountData>()
        {
            @Override
            public AccountData performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, RequiredVersion))
                {
                    return baseStore.<AccountData>doSomeSqlSingleResult(
                            conn,
                            "select id,active,username,hashscheme,hashsalt,hashvalue,lastchanged from ts_user " +
                            "where username = ? and active = 1 ",
                            new Object[] {
                                    user
                            },
                            new ITransformThrowable<ResultSet, AccountData, SQLException>()
                            {
                                @Override
                                public AccountData apply(ResultSet r) throws SQLException
                                {
                                    int id = r.getInt("id");
                                    int active = r.getInt("active");
                                    String username = r.getString("username");
                                    String hashscheme = r.getString("hashscheme");
                                    String hashsalt = r.getString("hashsalt");
                                    String hashvalue = r.getString("hashvalue");
                                    Timestamp lastchanged = r.getTimestamp("lastchanged");

                                    return new AccountData(
                                            id,
                                            active,
                                            username,
                                            hashscheme,
                                            hashsalt,
                                            hashvalue,
                                            lastchanged
                                            );
                                }
                            });
                }
                else
                {
                    return null;
                }
            }
        };
    }

    @Override
    public IConnectionWork<Void> createUser(final String user, final String hashscheme, final String hashsalt, final String hashvalue)
    {
        final int active = 1;
        final Timestamp lastchanged = new Timestamp(new Date().getTime());

        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, RequiredVersion))
                {
                    baseStore.doSomeSql(
                            conn,
                            "insert into ts_user (active,username,hashscheme,hashsalt,hashvalue,lastchanged) " +
                            "values (?,?,?,?,?,?)",
                            new Object[]
                            {
                                    active,
                                    user,
                                    hashscheme,
                                    hashsalt,
                                    hashvalue,
                                    lastchanged,
                            },
                            null,
                            null
                            );

                    return null; // Void
                }
                else
                {
                    return null; // Void
                }
            }
        };
    }

    @Override
    public IConnectionWork<Integer> userCount()
    {
        return new IConnectionWork<Integer>()
        {
            @Override
            public Integer performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, RequiredVersion))
                {

                    return baseStore.<Integer>doSomeSqlSingleResult(
                            conn,
                            "select count(*) from ts_user where active=1",
                            new Object[] {
                            },
                            new ITransformThrowable<ResultSet, Integer, SQLException>()
                            {
                                @Override
                                public Integer apply(ResultSet r) throws SQLException
                                {
                                    return r.getInt(1);
                                }
                            });
                }
                else
                {
                    return 0;
                }
            }
        };
    }
}
