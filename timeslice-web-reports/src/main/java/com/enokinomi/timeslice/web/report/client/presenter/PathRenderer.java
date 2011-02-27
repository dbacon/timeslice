package com.enokinomi.timeslice.web.report.client.presenter;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.TransformUtils;
import com.enokinomi.timeslice.web.core.client.util.Tx;


public class PathRenderer<PC> implements Tx<List<PC>, String>
{
    private final String separator;
    private final Tx<PC, String> pcStringify;


    public PathRenderer(String separator)
    {
        this(separator, TransformUtils.<PC>stringify());
    }

    PathRenderer(String separator, Tx<PC, String> pcStringify)
    {
        this.separator = separator;
        this.pcStringify = pcStringify;
    }

    @Override
    public String apply(List<PC> path)
    {
        StringBuilder sb = new StringBuilder();
        if (path.size() > 0)
        {
            for (int i = 0; i < path.size() - 1; ++i) sb.append(pcStringify.apply(path.get(i))).append(separator);
            sb.append(pcStringify.apply(path.get(path.size()-1)));
        }
        return sb.toString();
    }
}
