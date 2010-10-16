package com.enokinomi.timeslice.web.prorata.client.tree;

public interface IVisitor<V extends IVisitor<V>>
{
    V visit(Tree t, Branch parent, int currentDepth, int[] siblingCounts, int[] siblingIndexes);
}
