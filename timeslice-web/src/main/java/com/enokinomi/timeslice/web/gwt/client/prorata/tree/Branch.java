package com.enokinomi.timeslice.web.gwt.client.prorata.tree;



public class Branch extends Tree
{
    private final Tree[] children;

    public Branch(String name, Double weight, Tree[] children)
    {
        super(name, weight);
        this.children = new Tree[children.length];
        for (int i = 0; i < this.children.length; ++i) this.children[i] = children[i];
    }

    @Override
    public boolean isLeaf()
    {
        if (null == children) return true;
        if (0 == children.length) return true;
        return false;
    };

    @Override
    public Double getValue()
    {
        return 0.;
    }

    @Override
    public Double getTotal()
    {
        Double total = 0.;
        for (Tree child: children)
        {
            total += child.getTotal();
        }
        return total;
    }

    @Override
    protected <V extends IVisitor<V>> V accept(V visitor, Branch parent, int currentDepth, int[] siblingCounts, int[] siblingIndexes)
    {
        visitor = visitor.visit(this, parent, currentDepth, siblingCounts, siblingIndexes);
        int[] newSiblingCounts = new int[siblingCounts.length + 1];
        int[] newSiblingIndexes = new int[siblingIndexes.length + 1];
        for (int i = 0; i < siblingCounts.length; ++i)
        {
            newSiblingCounts[i] = siblingCounts[i];
            newSiblingIndexes[i] = siblingIndexes[i];
        }

        newSiblingCounts[newSiblingCounts.length - 1] = children.length;

        for (int i = 0; i < children.length; ++i)
        {
            newSiblingIndexes[newSiblingIndexes.length - 1] = i;

            visitor = children[i].accept(visitor, this, 1 + currentDepth, newSiblingCounts, newSiblingIndexes);
        }

        return visitor;
    }

    @Override
    public Tree expand(IRuleSource ruleSource, double precision)
    {
        for (int i = 0; i < children.length; ++i)
        {
            children[i].expand(ruleSource, precision);
        }

        return this;
    }
}
