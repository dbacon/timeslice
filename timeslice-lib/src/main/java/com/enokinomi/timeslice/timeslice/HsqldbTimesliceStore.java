package com.enokinomi.timeslice.timeslice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.Instant;

import com.enokinomi.timeslice.app.core.ITimesliceStore;
import com.enokinomi.timeslice.app.core.StartTag;


public class HsqldbTimesliceStore implements ITimesliceStore
{
    private final Connection conn;

    public HsqldbTimesliceStore(Connection conn)
    {
        this.conn = conn;
    }

    @Override
    public void add(StartTag tag)
    {
        try
        {
            PreparedStatement statement = conn.prepareStatement("insert into ts_tag (whenstamp, who, what) values (?, ?, ?)");
            statement.setString(1, tag.getWhen().toString());
            statement.setString(2, tag.getWho());
            statement.setString(3, tag.getWhat());
            int rows = statement.executeUpdate();
            statement.close();
            if (1 != rows) throw new RuntimeException("add: 'insert' did not result in exactly 1 row.");
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Could not insert: " + e.getMessage());
        }
    }

    @Override
    public void addAll(Collection<? extends StartTag> tags, boolean strict)
    {
        throw new RuntimeException("TODO: need to implement addAll"); // TODO: addAll()
    }

    @Override
    public List<StartTag> query(String owner, Instant starting, Instant ending, int pageSize, int pageIndex)
    {
        // TODO: use page-size and page-index
        try
        {
            ArrayList<StartTag> result = new ArrayList<StartTag>(100);
            PreparedStatement statement = conn.prepareStatement("select limit ? ? whenstamp, who, what from ts_tag where who = ? and whenstamp < ? and whenstamp > ? order by whenstamp desc");
            statement.setInt(1, pageIndex*pageSize);
            statement.setInt(2, pageSize);
            statement.setString(3, owner);
            statement.setString(4, ending.toString());
            statement.setString(5, starting.toString());
            ResultSet rs = statement.executeQuery();
            while (rs.next())
            {
                String whenStr = rs.getString(1);
                String who = rs.getString(2);
                String what = rs.getString(3);

                result.add(new StartTag(who, whenStr, what, null));
            }

            statement.close();

            return result;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("query failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void remove(StartTag tag)
    {
        try
        {
            PreparedStatement statement = conn.prepareStatement("delete from ts_tag where whenstamp = ?");
            statement.setString(1, tag.getWhen().toString());
            int deleted = statement.executeUpdate();
            if (1 != deleted) throw new RuntimeException("delete affected " + deleted + " rows instead of a single row.");
            statement.close();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("deleted failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateText(StartTag tag)
    {
        try
        {
            PreparedStatement statement = conn.prepareStatement("update ts_tag set what = ? where whenstamp = ?");
            statement.setString(1, tag.getWhat());
            statement.setString(2, tag.getWhen().toString());
            int affected = statement.executeUpdate();
            if (1 != affected) throw new RuntimeException("update-text affected " + affected + " rows instead of a single row.");
            statement.close();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("update-text failed: " + e.getMessage(), e);
        }
    }

}
