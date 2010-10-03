package com.enokinomi.timeslice.web.gwt.client.prorata.tree;

import com.enokinomi.timeslice.web.gwt.client.prorata.core.GroupComponent;

public interface IRuleSource
{
    GroupComponent[] expand(String name);
}
