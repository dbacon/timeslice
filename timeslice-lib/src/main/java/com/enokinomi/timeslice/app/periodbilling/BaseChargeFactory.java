package bacond.timeslicer.app.periodbilling;

import java.util.Arrays;
import java.util.List;


public class BaseChargeFactory implements IChargeFactory
{
    private final String name;

    public BaseChargeFactory(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public List<Charge> createCharges(long millis)
    {
        return Arrays.asList(new Charge(name, millis));
    }
}
