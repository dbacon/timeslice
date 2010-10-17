package com.enokinomi.timeslice.lib.userinfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.BaseHsqldbStore;
import com.enokinomi.timeslice.lib.util.ITransformThrowable;
import com.google.inject.Inject;


public class UserInfoDao implements IUserInfoDao
{
    private static final int RequiredVersion = 4;

    private final BaseHsqldbStore baseStore;

    @Inject
    UserInfoDao(BaseHsqldbStore baseStore)
    {
        this.baseStore = baseStore;
    }

    public static class ConfEntry
    {
        private final String name;
        private final String type;
        private final String value;

        ConfEntry(String name, String type, String value)
        {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public String getName()
        {
            return name;
        }

        public String getType()
        {
            return type;
        }

        public String getValue()
        {
            return value;
        }
    }

    @Override
    public TsSettings loadUserSettings(String username, String prefix)
    {
        if (baseStore.versionIsAtLeast(RequiredVersion))
        {
            List<ConfEntry> confEntries = baseStore.doSomeSql(
                    "select name, type, value from ts_conf where username = ? and name like ?",
                    new Object[] { username,  prefix + "%" },
                    new ITransformThrowable<ResultSet, ConfEntry, SQLException>()
                    {
                        @Override
                        public ConfEntry apply(ResultSet r) throws SQLException
                        {
                            return new ConfEntry(r.getString("name"), r.getString("type"), r.getString("value"));
                        }
                    });

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

    @Override
    public void saveUserSettings(String username, TsSettings settings)
    {
        if (baseStore.versionIsAtLeast(RequiredVersion))
        {
            for (String key: settings.getKeys())
            {
                baseStore.doSomeSql(
                    "delete from ts_conf where username = ? and name = ?",
                    new Object[]
                    {
                        username,
                        key,
                    },
                    null,
                    0);

                List<String> values = settings.getRawValuesForKey(key);

                for(String value: values)
                {
                    baseStore.doSomeSql(
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
    }
}
