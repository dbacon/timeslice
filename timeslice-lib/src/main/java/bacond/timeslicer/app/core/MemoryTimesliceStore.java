package bacond.timeslicer.app.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;

public class MemoryTimesliceStore implements ITimesliceStore
{
    private Instant starting;
    private Instant ending;
    private boolean enabled = false;
    private String firstTagText = "[blank]";

    private final Map<Instant, StartTag> store = new LinkedHashMap<Instant, StartTag>();

    public MemoryTimesliceStore(Instant starting, Instant ending, String firstTagText)
    {
        this.starting = starting;
        this.ending = ending;
        this.firstTagText = firstTagText;
    }

    @Override
    public Instant getStarting()
    {
        return starting;
    }

    public void setStarting(Instant starting)
    {
        this.starting = starting;
    }

    @Override
    public Instant getEnding()
    {
        return ending;
    }

    public void setEnding(Instant ending)
    {
        this.ending = ending;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public String getFirstTagText()
    {
        return firstTagText;
    }

    public void setFirstTagText(String firstTagText)
    {
        this.firstTagText = firstTagText;
    }

    @Override
    public boolean enable()
    {
        ensureDisabled();
        this.enabled = true;
        return true;
    }

    @Override
    public boolean disable()
    {
        ensureEnabled();
        this.enabled = false;
        return true;
    }

    private void ensureEnabled()
    {
        if (!enabled) throw new RuntimeException("Not enabled.");
    }

    private void ensureDisabled()
    {
        if (enabled) throw new RuntimeException("Not disabled.");
    }

    @Override
    public void add(StartTag tag)
    {
        ensureEnabled();

        if (tag.getWhen().isAfter(getStarting()) && tag.getWhen().isBefore(getEnding()))
        {
            store.put(tag.getWhen(), tag);
        }
        else
        {
            throw new RuntimeException("Invalid tag for this store.");
        }
    }

    @Override
    public void addAll(Collection<? extends StartTag> tags, boolean strict)
    {
        ensureEnabled();

        Map<Instant, StartTag> p = new LinkedHashMap<Instant, StartTag>();
        Instant from = getStarting();
        Instant thru = getEnding();
        for(StartTag tag: tags)
        {
            if (tag.getWhen().isAfter(from) && tag.getWhen().isBefore(thru))
            {
                p.put(tag.getWhen(), tag);
            }
            else
            {
                if (strict) throw new RuntimeException("Invalid tag for this store.");
            }
        }

        store.putAll(p);
    }

    @Override
    public List<StartTag> query(String who, Instant starting, Instant ending, int pageSize, int pageIndex)
    {
        ensureEnabled();
        if (pageSize < 0) throw new RuntimeException("Page-size must not be negative.");
        if (pageIndex < 0) throw new RuntimeException("Page-index must not be negative.");

        ArrayList<StartTag> result = new ArrayList<StartTag>();

        for(StartTag tag: store.values())
        {
            if (tag.getWhen().isAfter(starting) && tag.getWhen().isBefore(ending))
            {
                result.add(tag);
            }
        }

        Collections.sort(result, ByWhen.get());
        Collections.reverse(result);

        int si = pageIndex*pageSize;
        int ei = (pageIndex+1)*pageSize;

        si = Math.max(0, si);
        si = Math.min(result.size(), si);
        ei = Math.max(0, si);
        ei = Math.max(result.size(), ei);

        return result.subList(si, ei);
    }

    @Override
    public void remove(StartTag tag)
    {
        ensureEnabled();

        store.remove(tag.getWhen());
    }

    @Override
    public void updateText(StartTag tag)
    {
        ensureEnabled();
        if (!store.containsKey(tag.getWhen())) throw new RuntimeException("Specified tag not found.");

        store.put(tag.getWhen(), tag);
    }

}
