package com.enokinomi.timeslice.web.report.client.presenter;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.Pair;
import com.enokinomi.timeslice.web.core.client.util.Tx;


public interface NodeIntegrator<PC,V,A>
{
    Tx<V, List<PC>> createPathExtractor();
    Tx<Pair<A, V>,A> createValueCombiner();
}
