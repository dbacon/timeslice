package com.enokinomi.timeslice.lib.commondatautil.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.enokinomi.timeslice.lib.util.ITransformThrowable;

public interface IBaseHsqldbOps
{

    boolean versionIsAtLeast(Connection conn, int minversion);

    void require(Connection conn, int minversion);

    <T> T doSomeSqlSingleResult(Connection conn, String sql, Object[] params, ITransformThrowable<ResultSet, T, SQLException> rowConverter);

    /**
     *
     * @param <T>
     * @param conn
     * @param sql
     * @param params
     * @param rowConverter - pass null if statement is update/insert, pass a Transform if it's a query w/ a result-set.
     * @return
     */
    <T> List<T> doSomeSql(Connection conn, String sql, Object[] params, ITransformThrowable<ResultSet, T, SQLException> rowConverter, Integer expectedAffectedRowCount);

}
