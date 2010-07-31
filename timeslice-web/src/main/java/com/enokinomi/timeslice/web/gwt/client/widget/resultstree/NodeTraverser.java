package bacond.timeslice.web.gwt.client.widget.resultstree;

import java.util.List;
import java.util.Map.Entry;


public abstract class NodeTraverser<PC,T,A>
{
    protected abstract void visit(List<PC> path, T data, A aggregate);

    public void visit(Node<PC, T,A> node)
    {
        visit(node.path, node.value, node.aggregate);

        for (Entry<PC, Node<PC, T, A>> p: node.children.entrySet())
        {
            visit(p.getValue());
        }
    }
}
