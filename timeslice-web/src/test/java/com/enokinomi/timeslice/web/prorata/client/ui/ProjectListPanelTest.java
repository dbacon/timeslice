package com.enokinomi.timeslice.web.prorata.client.ui;

import static com.enokinomi.timeslice.web.prorata.client.ui.ProjectListPanel.TreeSupport.Segment.Bar;
import static com.enokinomi.timeslice.web.prorata.client.ui.ProjectListPanel.TreeSupport.Segment.End;
import static com.enokinomi.timeslice.web.prorata.client.ui.ProjectListPanel.TreeSupport.Segment.Mid;
import static com.enokinomi.timeslice.web.prorata.client.ui.ProjectListPanel.TreeSupport.Segment.Spc;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.enokinomi.timeslice.web.prorata.client.tree.Branch;
import com.enokinomi.timeslice.web.prorata.client.tree.Leaf;
import com.enokinomi.timeslice.web.prorata.client.tree.Tree;
import com.enokinomi.timeslice.web.prorata.client.ui.ProjectListPanel.RowMaker;
import com.enokinomi.timeslice.web.prorata.client.ui.ProjectListPanel.RowMaker.RowListener;
import com.enokinomi.timeslice.web.prorata.client.ui.ProjectListPanel.TreeSupport;
import com.enokinomi.timeslice.web.prorata.client.ui.ProjectListPanel.TreeSupport.Segment;


public class ProjectListPanelTest
{
    public static String rep(String pattern, int count)
    {
        if (count < 0) throw new RuntimeException("Cannot repeat negative times.");
        if (count == 1) return pattern;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; ++i)
        {
            sb.append(pattern);
        }
        return sb.toString();
    }

    public String prefixToString(List<Segment> segments)
    {
        StringBuilder sb = new StringBuilder();
        for (Segment segment: segments)
        {
            switch (segment)
            {
            case Bar:   sb.append("\u2502"); break;
            case End:   sb.append("\u2514"); break;
            case Mid:   sb.append("\u251c"); break;
            case Spc: sb.append(" "); break;
            }
        }
        return sb.toString();
    }

    @Test
    public void testRowMaker()
    {
        final List<List<String>> l = new ArrayList<List<String>>();

        RowMaker rowMaker = new RowMaker(100, 0, new RowListener()
        {
            @Override
            public void treeToRow(double total, int rowIndex, Tree t, Branch parent, int currentDepth, int[] siblingCounts, int[] siblingIndexs)
            {
                List<Segment> prefix = TreeSupport.drawPrefix(siblingCounts, siblingIndexs);
                String prefixString = prefixToString(prefix);

                List<String> r = new ArrayList<String>();
                r.add(Integer.toString(rowIndex));
                r.add(Integer.toString(currentDepth));
                r.add(prefixString);
                r.add(t.getName());
                l.add(r);

                System.out.println(prefixString + "#" + t.getName());
            }
        });

        Tree root =
                new Branch("root", 1., new Tree[] {
                        new Branch("A", 1., new Tree[] {
                                new Leaf  ("1", 1., 1.),
                                new Branch("2", 1., new Tree[] {
                                        new Leaf  ("a", 1., 1.),
                                        new Branch("b", 1., new Tree[] {
                                                new Leaf("i", 1., 1.),
                                                new Leaf("ii", 1., 1.),
                                        }),
                                        new Leaf("c", 1., 1.)
                                        }),
                                new Leaf  ("3", 1., 1.)
                                }),
                        new Branch("B", 1., new Tree[] {
                                new Leaf("1", 1., 1.),
                                new Leaf("2", 1., 1.)
                                })
                    });

        root.accept(rowMaker);

        ArrayList<List<String>> rows = new ArrayList<List<String>>();
        int i = 0;
        rows.add(Arrays.asList(Integer.toString(i++), "0", prefixToString(Arrays.<Segment>asList()), "root"));
        rows.add(Arrays.asList(Integer.toString(i++), "1", prefixToString(Arrays.asList(Spc, Mid)), "A"));
        rows.add(Arrays.asList(Integer.toString(i++), "2", prefixToString(Arrays.asList(Spc, Bar, Mid)), "1"));
        rows.add(Arrays.asList(Integer.toString(i++), "2", prefixToString(Arrays.asList(Spc, Bar, Mid)), "2"));
        rows.add(Arrays.asList(Integer.toString(i++), "3", prefixToString(Arrays.asList(Spc, Bar, Bar, Mid)), "a"));
        rows.add(Arrays.asList(Integer.toString(i++), "3", prefixToString(Arrays.asList(Spc, Bar, Bar, Mid)), "b"));
        rows.add(Arrays.asList(Integer.toString(i++), "4", prefixToString(Arrays.asList(Spc, Bar, Bar, Bar, Mid)), "i"));
        rows.add(Arrays.asList(Integer.toString(i++), "4", prefixToString(Arrays.asList(Spc, Bar, Bar, Bar, End)), "ii"));
        rows.add(Arrays.asList(Integer.toString(i++), "3", prefixToString(Arrays.asList(Spc, Bar, Bar, End)), "c"));
        rows.add(Arrays.asList(Integer.toString(i++), "2", prefixToString(Arrays.asList(Spc, Bar, End)), "3"));
        rows.add(Arrays.asList(Integer.toString(i++), "1", prefixToString(Arrays.asList(Spc, End)), "B"));
        rows.add(Arrays.asList(Integer.toString(i++), "2", prefixToString(Arrays.asList(Spc, Spc, Mid)), "1"));
        rows.add(Arrays.asList(Integer.toString(i++), "2", prefixToString(Arrays.asList(Spc, Spc, End)), "2"));

        assertEquals(rows, l);
    }

}
