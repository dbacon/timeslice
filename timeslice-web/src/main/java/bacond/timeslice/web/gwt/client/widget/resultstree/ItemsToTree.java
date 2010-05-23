package bacond.timeslice.web.gwt.client.widget.resultstree;

import java.util.ArrayList;
import java.util.List;

import bacond.timeslice.web.gwt.client.util.Tx;


public class ItemsToTree<PC, V, A>
{
    private final Tx<V, List<PC>> pathExtractor;
    private final Tx<Pair<A, V>, A> valueCombiner;

    public ItemsToTree(NodeIntegrator<PC, V, A> integrator)
    {
        pathExtractor = integrator.createPathExtractor();
        valueCombiner = integrator.createValueCombiner();
    }

    public static <PC,V,A> ItemsToTree<PC, V, A> create(NodeIntegrator<PC, V, A> integrator)
    {
        return new ItemsToTree<PC, V, A>(integrator);
    }

    public Node<PC,V,A> rowsToTree(List<V> lines)
    {
        Node<PC,V,A> root = new Node<PC,V,A>();

        for (V value: lines)
        {
            placeInTreeByPath(root, pathExtractor.apply(value), value);
        }

        return root;
    }

    private Node<PC,V,A> placeInTreeByPath(Node<PC,V,A> parent, List<PC> path, V value)
    {
        if (path.size() > 0)
        {
            PC key = path.get(0);

            Node<PC,V,A> node = parent.children.get(key);
            if (null == node)
            {
                List<PC> newPath = new ArrayList<PC>(parent.path.size() + 1);
                newPath.addAll(parent.path);
                newPath.add(key);
                node = new Node<PC,V,A>(newPath);
                parent.children.put(key, node);
            }

            placeInTreeByPath(node, path.subList(1, path.size()), value);
        }
        else
        {
            parent.value = value;
        }

        if (null != valueCombiner)
        {
            parent.aggregate = valueCombiner.apply(Pair.create(parent.aggregate, value));
        }

        return parent;
    }
}
