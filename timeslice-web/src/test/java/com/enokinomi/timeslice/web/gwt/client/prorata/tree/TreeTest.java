package com.enokinomi.timeslice.web.gwt.client.prorata.tree;

import java.util.Map.Entry;

import org.junit.Test;

import com.enokinomi.timeslice.web.gwt.client.prorata.core.GroupComponent;


public class TreeTest
{
    @Test
    public void test1()
    {
        IRuleSource ruleSource = new MapRuleSource()
        .add("Common", new GroupComponent[] {
                new GroupComponent("Common", "P1", "1"),
                new GroupComponent("Common", "P2", "1"),
                new GroupComponent("Common", "P3", "1"),
                })
        .add("P2", new GroupComponent[] {
                new GroupComponent("P2", "Common", "1"),
                });


        Tree root = new Leaf("Common", 100.)
            .expand(ruleSource, 0.0001);

        TotalTotalingVisitor totaler = root.accept(new TotalTotalingVisitor());
        root.accept(new TreePrinter(System.out));

        System.out.printf("%20s | %s\n", "Name", "Total");
        System.out.printf("%20s + %s\n", "------------", "---------------");
        for (Entry<String, Double> e: totaler.totals.entrySet())
        {
            System.out.printf("%20s | %.10f\n", e.getKey(), e.getValue());
        }
    }
}
