package com.enokinomi.timeslice.lib.ordering2;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enokinomi.timeslice.lib.assign.ConnectionFactory;
import com.enokinomi.timeslice.lib.assign.MockSchemaManager;
import com.enokinomi.timeslice.lib.commondatautil.BaseHsqldbOps;
import com.enokinomi.timeslice.lib.commondatautil.ConnectionContext;
import com.enokinomi.timeslice.lib.commondatautil.SchemaDuty;


public class IOrderingTestImplTest
{
    private final class PermGtor implements IPermutationGenerator
    {
        @Override
        public Set<List<String>> generatePermutations(List<String> prefix, Set<String> leftovers)
        {
            Set<List<String>> result = new LinkedHashSet<List<String>>();

            for (String nextElem: leftovers)
            {
                List<String> nextListPrefix = new ArrayList<String>(prefix.size() + 1);
                nextListPrefix.addAll(prefix);
                nextListPrefix.add(nextElem);

                if (1 == leftovers.size())
                {
                    result.add(nextListPrefix);
                    System.out.println("adding: " + nextListPrefix);
                }
                else
                {
                    Set<String> nextLeftovers = new LinkedHashSet<String>(leftovers);
                    nextLeftovers.remove(nextElem);

                    result.addAll(generatePermutations(nextListPrefix, nextLeftovers));
                }
            }

            return result;
        }
    }

    private final class InitialBuilder
    {
        private final IOrderingStore orderingStore;
        private final String orderName;

        public InitialBuilder(IOrderingStore orderingStore, String orderName)
        {
            this.orderingStore = orderingStore;
            this.orderName = orderName;
        }

        public InitialBuilder addPartialOrdering(String setName, String smaller, List<String> larger)
        {
            orderingStore.addPartialOrdering(setName, smaller, larger);
            return this;
        }

        public PermutationMembersSpecifiedBuilder requestOrderingOnAllPermutationsOf(final String ... members)
        {
            return new PermutationMembersSpecifiedBuilder(new PermGtor(), members);
        }

        private final class PermutationMembersSpecifiedBuilder
        {
            private final String[] members;
            private final IPermutationGenerator permGtor;

            private PermutationMembersSpecifiedBuilder(IPermutationGenerator permGtor, String[] members)
            {
                this.permGtor = permGtor;
                this.members = members;
            }

            public void assertEachResultEquals(String ... expected)
            {
                for(List<String> perm: generatePermutations(members))
                {
                    List<String> result = orderingStore.requestOrdering(orderName, perm);
                    Assert.assertEquals(Arrays.asList(expected), result);
                }
            }

            public void assertEachResultEquals(String[] expected, String ... unorderedRest)
            {
                LinkedHashSet<String> theRest = new LinkedHashSet<String>(Arrays.asList(unorderedRest));
                for(List<String> perm: generatePermutations(members))
                {
                    List<String> result = orderingStore.requestOrdering(orderName, perm);
                    Assert.assertEquals(Arrays.asList(expected), result.subList(0, expected.length));
                    Assert.assertEquals(theRest, new LinkedHashSet<String>(result.subList(expected.length, result.size())));
                }
            }

            public void assertEachResultEqualsRespectiveInput()
            {
                for(List<String> perm: generatePermutations(members))
                {
                    List<String> result = orderingStore.requestOrdering(orderName, perm);
                    Assert.assertEquals(perm, result);
                }
            }

            private Set<List<String>> generatePermutations(String ... elements)
            {
                return permGtor.generatePermutations(Collections.<String>emptyList(), new LinkedHashSet<String>(Arrays.asList(elements)));
            }

        }
    }


    private Connection conn;

    @Before
    public void setup() throws Exception
    {
        String dbDir = "target/test-generated-data/test-ordering-3-db";
        FileUtils.deleteDirectory(new File(dbDir));

        ConnectionFactory connFactory = new ConnectionFactory();
        conn = connFactory.createConnection(dbDir + "/test-1");

        SchemaDuty sd = new SchemaDuty("timeslice-3.ddl");
        sd.createSchema(conn);
    }

    @After
    public void teardown() throws Exception
    {
        conn.close();
        conn = null;

        String dbDir = "target/test-generated-data/test-ordering-3-db";
        FileUtils.deleteDirectory(new File(dbDir));
    }

    private IOrderingStore createImplUnderTest(List<String> existingData)
    {
        return new OrderingStore(new ConnectionContext(conn), new OrderingWorks(new BaseLowLevelOrderingWorks(new BaseHsqldbOps(new MockSchemaManager(3)))));
    }

    protected InitialBuilder on(final IOrderingStore impl, final String orderName)
    {
        return new InitialBuilder(impl, orderName);
    }

    @Test
    public void testPermGen()
    {
        Set<List<String>> perms = new PermGtor().generatePermutations(Collections.<String>emptyList(), new LinkedHashSet<String>(Arrays.asList("grape", "balloon", "car")));

        LinkedHashSet<List<String>> expected = new LinkedHashSet<List<String>>();
        expected.add(Arrays.asList("balloon", "grape", "car"));
        expected.add(Arrays.asList("grape", "balloon", "car"));
        expected.add(Arrays.asList("car", "grape", "balloon"));
        expected.add(Arrays.asList("grape", "car", "balloon"));
        expected.add(Arrays.asList("car", "balloon", "grape"));
        expected.add(Arrays.asList("balloon", "car", "grape"));

        assertEquals(expected, perms);
    }

    @Test
    public void hello_initiallyEmpty_orderRequestEmpty()
    {
        on(createImplUnderTest(Arrays.<String>asList()), "order1")
            .requestOrderingOnAllPermutationsOf()
            .assertEachResultEquals();
    }

    @Test
    public void hello_initiallyEmpty_orderRequestNonEmpty()
    {
        on(createImplUnderTest(Arrays.<String>asList()), "order1")
            .requestOrderingOnAllPermutationsOf("apple", "kiwi", "strawberry")
            .assertEachResultEqualsRespectiveInput();
    }

    @Test
    public void hello_initiallyNonEmpty_orderRequestEmpty()
    {
        String thisSet = "order1";

        on(createImplUnderTest(Arrays.<String>asList()), thisSet)
            .addPartialOrdering(thisSet, "grape", Arrays.asList("orange", "apple"))
            .requestOrderingOnAllPermutationsOf()
            .assertEachResultEquals();
    }

    @Test
    public void hello_initiallyNonEmpty_orderRequestNonEmpty_disjunct()
    {
        String thisSet = "order1";

        on(createImplUnderTest(Arrays.<String>asList()), thisSet)
            .addPartialOrdering(thisSet, "grape", Arrays.asList("orange", "apple"))
            .requestOrderingOnAllPermutationsOf("banana", "kiwi", "strawberry")
            .assertEachResultEqualsRespectiveInput();
    }

    @Test
    public void hello_initiallyNonEmpty_orderRequestNonEmpty_supersetOneElement()
    {
        String thisSet = "order1";

        on(createImplUnderTest(Arrays.<String>asList()), thisSet)
            .addPartialOrdering(thisSet, "grape", Arrays.asList("orange", "apple"))
            .requestOrderingOnAllPermutationsOf("grape", "apple", "orange", "banana")
            .assertEachResultEquals("grape", "orange", "apple", "banana");
    }

    @Test
    public void hello_initiallyNonEmpty_orderRequestNonEmpty_supersetMultiElement()
    {
        String thisSet = "order1";

        on(createImplUnderTest(Arrays.<String>asList()), thisSet)
            .addPartialOrdering(thisSet, "grape", Arrays.asList("orange", "apple"))
            .requestOrderingOnAllPermutationsOf("grape", "apple", "orange", "banana", "melon")
            .assertEachResultEquals(new String[] { "grape", "orange", "apple" }, "banana", "melon");
    }

    @Test
    public void hello_initiallyNonEmpty_orderRequestNonEmpty_subset()
    {
        String thisSet = "order1";

        on(createImplUnderTest(Arrays.<String>asList()), thisSet)
            .addPartialOrdering(thisSet, "grape", Arrays.asList("orange", "apple"))
            .requestOrderingOnAllPermutationsOf("orange", "grape")
            .assertEachResultEquals("grape", "orange");
    }

    @Test
    public void hello_merge_insertFront()
    {
        String thisSet = "order1";

        on(createImplUnderTest(Arrays.<String>asList()), thisSet)
            .addPartialOrdering(thisSet, "grape", Arrays.asList("orange", "apple"))
            .addPartialOrdering(thisSet, null, Arrays.asList("kiwi"))
            .requestOrderingOnAllPermutationsOf("kiwi", "grape", "orange", "apple", "pineapple", "strawberry")
            .assertEachResultEquals(new String[] { "kiwi", "grape", "orange", "apple" }, "pineapple", "strawberry");
    }

    @Test
    public void hello_merge_insertAfterFirst()
    {
        String thisSet = "order1";

        on(createImplUnderTest(Arrays.<String>asList()), thisSet)
            .addPartialOrdering(thisSet, "grape", Arrays.asList("orange", "apple"))
            .addPartialOrdering(thisSet, "grape", Arrays.asList("kiwi"))
            .requestOrderingOnAllPermutationsOf("kiwi", "grape", "orange", "apple", "pineapple", "strawberry")
            .assertEachResultEquals(new String[] { "grape", "kiwi", "orange", "apple" }, "pineapple", "strawberry");
    }

    @Test
    public void hello_merge_insertAfterMiddle()
    {
        String thisSet = "order1";

        on(createImplUnderTest(Arrays.<String>asList()), thisSet)
            .addPartialOrdering(thisSet, "grape", Arrays.asList("orange", "apple"))
            .addPartialOrdering(thisSet, "orange", Arrays.asList("kiwi"))
            .requestOrderingOnAllPermutationsOf("kiwi", "grape", "orange", "apple", "pineapple", "strawberry")
            .assertEachResultEquals(new String[] { "grape", "orange", "kiwi", "apple" }, "pineapple", "strawberry");
    }

    @Test
    public void hello_merge_insertAfterLast()
    {
        String thisSet = "order1";

        on(createImplUnderTest(Arrays.<String>asList()), thisSet)
            .addPartialOrdering(thisSet, "grape", Arrays.asList("orange", "apple"))
            .addPartialOrdering(thisSet, "apple", Arrays.asList("kiwi"))
            .requestOrderingOnAllPermutationsOf("kiwi", "grape", "orange", "apple", "pineapple", "strawberry")
            .assertEachResultEquals(new String[] { "grape", "orange", "apple", "kiwi" }, "pineapple", "strawberry");
    }

    @Test
    public void hello_merge_insertAfterMiddle_containsExisting_requestOrderingSubWithExtra()
    {
        String thisSet = "order1";

        on(createImplUnderTest(Arrays.<String>asList()), thisSet)
            .addPartialOrdering(thisSet, "grape", Arrays.asList("orange", "apple", "peanut", "walnut", "cashew"))
            .addPartialOrdering(thisSet, "orange", Arrays.asList("kiwi", "walnut"))
            .requestOrderingOnAllPermutationsOf("grape", "orange", "kiwi", "walnut", "apple", "pineapple", "strawberry")
            .assertEachResultEquals(new String[] { "grape", "orange", "kiwi", "walnut", "apple"}, "pineapple", "strawberry");
    }

}
