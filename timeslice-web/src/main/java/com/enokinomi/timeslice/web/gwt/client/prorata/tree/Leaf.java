package com.enokinomi.timeslice.web.gwt.client.prorata.tree;

import com.enokinomi.timeslice.web.gwt.client.prorata.core.GroupComponent;


public class Leaf extends Tree
{
    private final String name;
    private final Double value;

    public Leaf(String name, Double value)
    {
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean isLeaf()
    {
        return true;
    };

    public String getName()
    {
        return name;
    }

    public Double getValue()
    {
        return value;
    }

    @Override
    public Double getTotal()
    {
        return getValue();
    }

    @Override
    protected <V extends IVisitor<V>> V accept(V visitor, Branch parent, int currentDepth, int[] siblingCounts, int[] siblingIndexes)
    {
        visitor.visit(this, parent, currentDepth, siblingCounts, siblingIndexes);
        return visitor;
    }

    @Override
    public Tree expand(IRuleSource ruleSource, double precision)
    {
        Tree result = this;

        if (getValue() > precision)
        {
            GroupComponent[] components = ruleSource.expand(name);

            if (null != components && components.length > 0)
            {
                int max = components.length;
                double value = getValue() / max;

                Tree[] children = new Tree[components.length];
                for (int i = 0; i < children.length; ++i)
                {
                    children[i] = new Leaf(components[i].getName(), value).expand(ruleSource, precision);
                }

                result = new Branch(getName(), children);
            }
        }

        return result;
    }
}
