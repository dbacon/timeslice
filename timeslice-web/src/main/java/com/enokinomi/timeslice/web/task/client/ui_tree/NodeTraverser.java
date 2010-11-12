package com.enokinomi.timeslice.web.task.client.ui_tree;

import java.util.List;
import java.util.Map.Entry;


public abstract class NodeTraverser<PC,T,A>
{
    protected abstract void visit(List<PC> path, T data, A aggregate);

    protected NodeTraverser()
    {
    }

    public void visit(Node<PC, T,A> node)
    {
        visit(node.path, node.value, node.aggregate);

        for (Entry<PC, Node<PC, T, A>> p: node.children.entrySet())
        {
            visit(p.getValue());
        }
    }
}
