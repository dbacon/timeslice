package com.enokinomi.timeslice.web.gwt.client.prorata.tree;

import com.enokinomi.timeslice.web.gwt.client.prorata.core.GroupComponent;


public class Leaf extends Tree
{
    private final Double value;

    public Leaf(String name, Double weight, Double value)
    {
        super(name, weight);

        this.value = value;
    }

    @Override
    public boolean isLeaf()
    {
        return true;
    };

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

        if (Math.abs(getValue()) > precision)
        {
            GroupComponent[] components = ruleSource.expand(getName());

            if (null != components && components.length > 0)
            {
                Tree[] children = new Tree[components.length];

                Double totalWeight = 0.;
                for (int i = 0; i < children.length; ++i)
                {
                    totalWeight += components[i].getWeight();
                }

                for (int i = 0; i < children.length; ++i)
                {
                    double scale = components[i].getWeight() / totalWeight;
                    children[i] = new Leaf(
                            components[i].getName(),
                            components[i].getWeight(),
                            getValue() * scale)
                        .expand(ruleSource, precision);
                }

                result = new Branch(getName(), getWeight(), children);
            }
        }

        return result;
    }
}
