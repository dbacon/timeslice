package com.enokinomi.timeslice.web.gwt.client.task.ui_tree;

import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.util.Tx;


public interface NodeIntegrator<PC,V,A>
{
    Tx<V, List<PC>> createPathExtractor();
    Tx<Pair<A, V>,A> createValueCombiner();
}
