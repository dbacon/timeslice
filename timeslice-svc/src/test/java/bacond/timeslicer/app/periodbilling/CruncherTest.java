package bacond.timeslicer.app.periodbilling;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import org.junit.Test;

import bacond.timeslicer.app.periodbilling.api.BillableTotal;
import bacond.timeslicer.app.periodbilling.api.BillableTotalList;
import bacond.timeslicer.app.periodbilling.api.Charge;
import bacond.timeslicer.app.periodbilling.api.ChargeBroker;
import bacond.timeslicer.app.periodbilling.api.DefaultChargeFactoryResolver;
import bacond.timeslicer.app.periodbilling.api.IChargeFactoryResolver;


public class CruncherTest
{
	public static final BigDecimal MillisecondsPerHour = new BigDecimal("3600000");

	public static Double millisToHours(long millis)
	{
		return new BigDecimal(millis).divide(MillisecondsPerHour, MathContext.DECIMAL64).doubleValue();
	}

	@Test
	public void test_0() throws Exception
	{
		IChargeFactoryResolver chargeFactoryResolver = DefaultChargeFactoryResolver.create();

		BillableTotalList billableTasks = new BillableTotalList();
		billableTasks.addBillableTotal(new BillableTotal("project 1", 40000, "dept 1"));
		billableTasks.addBillableTotal(new BillableTotal("project 2", 40000, "dept 2"));
		billableTasks.addBillableTotal(new BillableTotal("project 3", 40000, "dept 3"));
		billableTasks.addBillableTotal(new BillableTotal("project 1", 40000, "dept 1"));
		List<Charge> charges = new ChargeBroker(chargeFactoryResolver)
			.crunch(billableTasks);

		System.out.println("Charges:");
		for (Charge charge: charges)
		{
			System.out.printf("  %-30s : % 3.13f\n", charge.getChargeableName(), millisToHours(charge.getMillis()));
		}
		System.out.println("Done.");
	}

	@Test
	public void test_1() throws Exception
	{
		IChargeFactoryResolver chargeFactoryResolver = DefaultChargeFactoryResolver.create();

		BillableTotalList billableTasks = new BillableTotalList();
		billableTasks.addBillableTotal(new BillableTotal("project 1", 40000, "dept 1"));
		billableTasks.addBillableTotal(new BillableTotal("project 2", 40000, "dept 2"));
		billableTasks.addBillableTotal(new BillableTotal("project 3", 40000, "dept 3"));
		billableTasks.addBillableTotal(new BillableTotal("task common", 40000, "meta:1,dept 1:1,dept 2"));
		List<Charge> charges = new ChargeBroker(chargeFactoryResolver)
			.crunch(billableTasks);

		System.out.println("Charges:");
		for (Charge charge: charges)
		{
			System.out.printf("  %-30s : % 3.13f\n", charge.getChargeableName(), millisToHours(charge.getMillis()));
		}
		System.out.println("Done.");
	}
}
