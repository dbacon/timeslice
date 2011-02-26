package com.enokinomi.timeslice.web.prorata.client.tree;

import org.junit.Test;

import com.enokinomi.timeslice.web.prorata.client.core.GroupComponent;


public class TreeTest
{
    @Test
    public void testExpand()
    {
        MapRuleSource ruleSource = new MapRuleSource();
        ruleSource
                .add("TV", new GroupComponent[] {
                        new GroupComponent("TV", "tv1", 1.),
                        new GroupComponent("TV", "tv2", 1.),
                        })
                .add("tv1", new GroupComponent[] {
                        new GroupComponent("tv1", "tv1a", 1.),
                    })
                .add("tv1a", new GroupComponent[] {
                        new GroupComponent("tv1a", "tv1ai", 1.),
                    })
                ;

        Tree root = new Leaf("TV", 1., 100.);
        Tree expanded = root.expand(ruleSource, 0.0001);
        expanded.accept(new TreePrinter(System.out));
    }
}
