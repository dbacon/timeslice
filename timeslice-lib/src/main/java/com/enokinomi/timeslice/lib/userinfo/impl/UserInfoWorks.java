package com.enokinomi.timeslice.lib.userinfo.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.api.IBaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.userinfo.api.ConfEntry;
import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoWorks;
import com.enokinomi.timeslice.lib.userinfo.api.TsSettings;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.google.inject.Inject;

public class UserInfoWorks implements IUserInfoWorks
{
    private static final int RequiredVersion = 4;

    private final IBaseHsqldbOps baseStore;

    @Inject
    UserInfoWorks(IBaseHsqldbOps baseStore)
    {
        this.baseStore = baseStore;
    }

    @Override
    public IConnectionWork<TsSettings> workLoadUserSettings(final String username, final String prefix)
    {
        return new IConnectionWork<TsSettings>()
        {
            @Override
            public TsSettings performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, RequiredVersion))
                {
                    List<ConfEntry> confEntries = baseStore.doSomeSql(
                            conn,
                            "select name, type, value from ts_conf where username = ? and name like ?",
                            new Object[] { username,  prefix + "%" },
                            new ITransformThrowable<ResultSet, ConfEntry, SQLException>()
                            {
                                @Override
                                public ConfEntry apply(ResultSet r) throws SQLException
                                {
                                    return new ConfEntry(r.getString("name"), r.getString("type"), r.getString("value"));
                                }
                            },
                            null);

                    TsSettings result = new TsSettings();

                    for(ConfEntry confEntry: confEntries)
                    {
                        result.addConfValue(confEntry.getName(), confEntry.getType(), confEntry.getValue());
                    }

                    return result;
                }
                else
                {
                    return new TsSettings();
                }
            }
        };
    }

    @Override
    public IConnectionWork<Void> workSaveUserSettings(final String username, final TsSettings settings)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, RequiredVersion))
                {
                    for (String key: settings.getKeys())
                    {
                        baseStore.doSomeSql(
                                conn,
                                "delete from ts_conf where username = ? and name = ?",
                                new Object[]
                                           {
                                        username,
                                        key,
                                           },
                                           null,
                                           null);

                        List<String> values = settings.getRawValuesForKey(key);

                        for(String value: values)
                        {
                            baseStore.doSomeSql(
                                    conn,
                                    "insert into ts_conf (username, name, type, value) values (?, ?, ?, ?)",
                                    new Object[]
                                               {
                                            username,
                                            key,
                                            "unused",
                                            value
                                               },
                                               null,
                                               1);
                        }
                    }
                }

                return null; // Void
            }
        };
    }

    @Override
    public IConnectionWork<Void> addSetting(final String user, final String name, final String value)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, RequiredVersion))
                {
                    baseStore.doSomeSql(
                            conn,
                            "insert into ts_conf (username, name, type, value) values (?, ?, ?, ?)",
                            new Object[]
                            {
                                    user,
                                    name,
                                    "unused",
                                    value
                            },
                            null,
                            1);
                }

                return null; // Void
            }
        };
    }

    @Override
    public IConnectionWork<Void> editSetting(final String user, final String name, final String oldValue, final String newValue)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, RequiredVersion))
                {
                    baseStore.doSomeSql(
                            conn,
                            "update ts_conf set value = ? where username = ? and name = ? and value = ?",
                            new Object[]
                            {
                                    newValue,
                                    user,
                                    name,
                                    oldValue
                            },
                            null,
                            null); // we might match more
                }

                return null; // Void
            }
        };
    }

    @Override
    public IConnectionWork<Void> deleteSetting(final String user, final String name, final String value)
    {
        return new IConnectionWork<Void>()
        {
            @Override
            public Void performWithConnection(Connection conn)
            {
                if (baseStore.versionIsAtLeast(conn, RequiredVersion))
                {
                    baseStore.doSomeSql(
                            conn,
                            "delete from ts_conf where username = ? and name = ? and value = ?",
                            new Object[]
                            {
                                    user,
                                    name,
                                    value
                            },
                            null,
                            null); // we can't distinguish
                }

                return null; // Void
            }
        };
    }
}
