package com.enokinomi.timeslice.web.gwt.clienttest.task.ui_tree;


import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.enokinomi.timeslice.web.gwt.client.task.ui_tree.ItemsToTree;
import com.enokinomi.timeslice.web.gwt.client.task.ui_tree.NodeIntegrator;
import com.enokinomi.timeslice.web.gwt.client.task.ui_tree.NodeTraverser;
import com.enokinomi.timeslice.web.gwt.client.task.ui_tree.Pair;
import com.enokinomi.timeslice.web.gwt.client.task.ui_tree.PathRenderer;
import com.enokinomi.timeslice.web.gwt.client.util.Split;
import com.enokinomi.timeslice.web.gwt.client.util.TransformUtils;
import com.enokinomi.timeslice.web.gwt.client.util.Tx;


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
        private final PrintStream ps;

        public PrintNestedTableRowFullPath(PrintStream ps)
        {
            this.ps = ps;
        }

        @Override
        protected void visit(List<String> path, Row item, Row aggregate)
        {
            ps.printf("%3s %3s : %s\n",
                    item == null ? "" : ("" + item.count),
                            aggregate.count,
                            new PathRenderer<String>("/").apply(path)
            );
        }
    }

    public static class PrintNestedTableRow extends NodeTraverser<String, Row, Row>
    {
        private final PrintStream ps;

        public PrintNestedTableRow(PrintStream ps)
        {
            this.ps = ps;
        }

        @Override
        protected void visit(List<String> path, Row item, Row aggregate)
        {
            char[] buf = new char[path.size()*2];
            Arrays.fill(buf, ' ');

            ps.printf("%3s %3s : %s%s\n",
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
        new PrintNestedTableRow(System.out)
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
                            new Row("a/g", 3)),
                        new Row("", 0)));
    }

}
