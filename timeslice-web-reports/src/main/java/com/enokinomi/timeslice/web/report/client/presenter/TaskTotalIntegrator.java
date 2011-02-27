package com.enokinomi.timeslice.web.report.client.presenter;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.Pair;
import com.enokinomi.timeslice.web.core.client.util.Split;
import com.enokinomi.timeslice.web.core.client.util.TransformUtils;
import com.enokinomi.timeslice.web.core.client.util.Tx;
import com.enokinomi.timeslice.web.report.client.core.TaskTotal;


public class TaskTotalIntegrator implements NodeIntegrator<String, TaskTotal, TaskTotal>
{
    private String separator;

    public TaskTotalIntegrator(String separator)
    {
        this.separator = separator;
    }

    public void setSeparator(String separator)
    {
        this.separator = separator;
    }

    public String getSeparator()
    {
        return separator;
    }

    @Override
    public Tx<TaskTotal, List<String>> createPathExtractor()
    {
        return TransformUtils.comp(
                new Tx<TaskTotal, String>() { @Override public String apply(TaskTotal t) { return t.getWhat(); } },
                new Split(getSeparator()));
    }

    @Override
    public Tx<Pair<TaskTotal, TaskTotal>, TaskTotal> createValueCombiner()
    {
        return new Tx<Pair<TaskTotal,TaskTotal>, TaskTotal>()
        {
            @Override
            public TaskTotal apply(Pair<TaskTotal, TaskTotal> r)
            {
                if (null == r.first) return r.second;
                if (null == r.second) return null;
                return new TaskTotal(
                        r.first.getWho(),
                        r.first.getHours() + r.second.getHours(),
                        r.first.getPercentage() + r.second.getPercentage(),
                        r.first.getWhat());
            }
        };
    }
}
