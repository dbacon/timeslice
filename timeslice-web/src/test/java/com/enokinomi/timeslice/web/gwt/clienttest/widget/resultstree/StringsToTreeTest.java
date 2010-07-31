package com.enokinomi.timeslice.web.gwt.clienttest.widget.resultstree;


import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.enokinomi.timeslice.web.gwt.client.util.Split;
import com.enokinomi.timeslice.web.gwt.client.util.TransformUtils;
import com.enokinomi.timeslice.web.gwt.client.util.Tx;
import com.enokinomi.timeslice.web.gwt.client.widget.resultstree.ItemsToTree;
import com.enokinomi.timeslice.web.gwt.client.widget.resultstree.NodeIntegrator;
import com.enokinomi.timeslice.web.gwt.client.widget.resultstree.NodeTraverser;
import com.enokinomi.timeslice.web.gwt.client.widget.resultstree.Pair;
import com.enokinomi.timeslice.web.gwt.client.widget.resultstree.PathRenderer;


public class StringsToTreeTest
{
    private static class Row
    {
        public int count;
        public String name;

        public Row(String name, int count)
        {
            this.name = name;
            this.count = count;
        }
    }

    public class TotalIntegrator implements NodeIntegrator<String, Row, Row>
    {
        private final String pathSeparator;

        public TotalIntegrator(String pathSeparator)
        {
            this.pathSeparator = pathSeparator;
        }

        @Override
        public Tx<Row, List<String>> createPathExtractor()
        {
            return TransformUtils.comp(
                    new Tx<Row, String>() { @Override public String apply(Row r) { return r.name; } },
                    new Split(pathSeparator));
        }

        @Override
        public Tx<Pair<Row, Row>, Row> createValueCombiner()
        {
            return new Tx<Pair<Row,Row>, Row>()
            {
                @Override
                public Row apply(Pair<Row, Row> r)
                {
                    if (null == r.first) return r.second;
                    if (null == r.second) return null;
                    return new Row(r.first.name, r.first.count + r.second.count);
                }
            };
        }
    }

    public static class PrintNestedTableRowFullPath extends NodeTraverser<String, Row, Row>
    {
        @Override
        protected void visit(List<String> path, Row item, Row aggregate)
        {
            System.out.printf("%3s %3s : %s\n",
                    item == null ? "" : ("" + item.count),
                            aggregate.count,
                            new PathRenderer<String>("/").apply(path)
            );
        }
    }

    public static class PrintNestedTableRow extends NodeTraverser<String, Row, Row>
    {
        @Override
        protected void visit(List<String> path, Row item, Row aggregate)
        {
            char[] buf = new char[path.size()*2];
            Arrays.fill(buf, ' ');

            System.out.printf("%3s %3s : %s%s\n",
                    item == null ? "" : ("" + item.count),
                            aggregate.count,
                            new String(buf),
                            path.size() == 0 ? "" : path.get(path.size() - 1)
            );
        }
    }

    @Test
    public void test0()
    {
        new PrintNestedTableRow()
            .visit(ItemsToTree.create(new TotalIntegrator("/"))
                    .rowsToTree(
                        Arrays.asList(
                            new Row("break/coffee", 3),
                            new Row("work/blah", 3),
                            new Row("work/blah/meeting", 3),
                            new Row("work/blah/code", 3),
                            new Row("break", 3),
                            new Row("a/b/d", 3),
                            new Row("a/c", 3),
                            new Row("bc", 3),
                            new Row("a/g", 3))));
    }

}
