package com.enokinomi.timeslice.web.gwt.client.prorata.tree;

import java.io.PrintStream;
import java.util.Arrays;

public class TreePrinter implements IVisitor<TreePrinter>
{
    private final PrintStream out;

    public TreePrinter(PrintStream out)
    {
        this.out = out;
    }

    @Override
    public TreePrinter visit(Tree t, Branch parent,
            int currentDepth, int[] siblingCounts, int[] siblingIndexes)
    {
        char[] fill = new char[currentDepth*2];
        Arrays.fill(fill, ' ');
        String prefix = new String(fill);

        out.println(prefix + t.getValue());

        return this;
    }
}
