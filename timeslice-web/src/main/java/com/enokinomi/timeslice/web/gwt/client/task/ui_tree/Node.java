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

    public Node()
    {
        this(Collections.<PathComponent>emptyList());
    }

    public Node(List<PathComponent> path)
    {
        this.path = Collections.unmodifiableList(new ArrayList<PathComponent>(path));
        this.aggregate = null;
        this.value = null;
    }
}
