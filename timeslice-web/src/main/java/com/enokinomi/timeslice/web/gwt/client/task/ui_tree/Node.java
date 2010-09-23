package com.enokinomi.timeslice.web.gwt.client.task.ui_tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Node<PathComponent, Value, Aggregate>
{
    public final List<PathComponent> path;
    public Value value;
    public Aggregate aggregate;
    public Map<PathComponent, Node<PathComponent, Value,Aggregate>> children = new LinkedHashMap<PathComponent, Node<PathComponent, Value,Aggregate>>();

    /**
     * Only to be used to construct a root node.
     *
     * @param emptyAggregate
     * @param emptyValue
     */
    public Node(Aggregate emptyAggregate)
    {
        this(Collections.<PathComponent>emptyList(), emptyAggregate, null);
    }

    /**
     * Only to be used for non-root nodes.
     *
     * @param path
     */
    public Node(List<PathComponent> path)
    {
        this(path, null, null);
    }

    private Node(List<PathComponent> path, Aggregate aggregate, Value value)
    {
        this.path = Collections.unmodifiableList(new ArrayList<PathComponent>(path));
        this.aggregate = aggregate;
        this.value = value;
    }
}
