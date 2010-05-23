package bacond.timeslice.web.gwt.client.widget.resultstree;

import java.util.List;

import bacond.timeslice.web.gwt.client.util.Tx;

public interface NodeIntegrator<PC,V,A>
{
    Tx<V, List<PC>> createPathExtractor();
    Tx<Pair<A, V>,A> createValueCombiner();
}
