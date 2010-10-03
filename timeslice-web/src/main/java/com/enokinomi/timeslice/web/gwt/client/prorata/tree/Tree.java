package com.enokinomi.timeslice.web.gwt.client.prorata.tree;


public abstract class Tree
{
    private final String name;
    private final Double weight;

    public Tree(String name, Double weight)
    {
        this.name = name;
        this.weight = weight;
    }

    public String getName()
    {
        return name;
    }

    public Double getWeight()
    {
        return weight;
    }

    public abstract Double getValue();
    public abstract Double getTotal();
    public abstract boolean isLeaf();

    public abstract Tree expand(IRuleSource ruleSource, double precision);

    protected abstract <V extends IVisitor<V>> V accept(V visitor, Branch parent, int depth, int[] siblingCounts, int[] siblingIndexes);

    public <V extends IVisitor<V>> V accept(V visitor)
    {
        return accept(visitor, null, 0, new int[] { 1 }, new int[] { 0 });
    }
}
