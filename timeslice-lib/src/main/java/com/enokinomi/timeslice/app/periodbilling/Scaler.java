/**
 *
 */
package bacond.timeslicer.app.periodbilling;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import bacond.lib.util.ITransform;

public class Scaler implements ITransform<List<Charge>, List<Charge>>
{
    private final long millis;

    private Scaler(long millis)
    {
        this.millis = millis;
    }

    @Override
    public List<Charge> apply(List<Charge> charges)
    {
        long total = 0;

        for (Charge charge: charges)
        {
            total = charge.getMillis();
        }

        BigDecimal millisB = BigDecimal.valueOf(millis);
        BigDecimal totalB = BigDecimal.valueOf(total);

        List<Charge> result = new ArrayList<Charge>(charges.size());
        for (Charge charge: charges)
        {
            result.add(
                    new Charge(
                            charge.getChargeableName(),
                            BigDecimal.valueOf(charge.getMillis())
                                .multiply(millisB)
                                .divide(totalB, MathContext.DECIMAL64)
                                .setScale(0, RoundingMode.HALF_UP)
                                .longValueExact()
                            ));
        }

        return result;
    }
}
