package Fekete;
/*
 * From Sergey
 */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import objects.Customer;
import objects.Item;
import objects.VehicleType;

public class lbFekete
{

	public static void scaleGeneration(BigDecimal start, BigDecimal step,
			ArrayList<Customer> customers, ArrayList<VehicleType> vehicleTypes)
	{
		int ptr = -1;
		for (VehicleType type : vehicleTypes)
		{
			ptr++;
			for (Customer customer : customers)
			{
				customer.scales.add(new ArrayList<Double>());
				if (!customer.isSuitableForType(ptr))
					continue;
				BigDecimal stop = new BigDecimal("0.5");
				double value;
				for (BigDecimal r = start; r.compareTo(stop) <= 0; r = r.add(step))
				{
					value = 0;
					for (Item item : customer.get_items())
						value += scale1(item.get_length(), item.get_width(), 1, r,
								type.get_length(), type.get_width()).doubleValue();
					customer.scales.get(ptr).add(value);

					value = 0;
					for (Item item : customer.get_items())
						value += scale2(item.get_length(), item.get_width(), 1, r,
								type.get_length(), type.get_width()).doubleValue();
					customer.scales.get(ptr).add(value);

					value = 0;
					for (Item item : customer.get_items())
						value += scale3(item.get_length(), item.get_width(), 1, r,
								type.get_length(), type.get_width()).doubleValue();
					customer.scales.get(ptr).add(value);

					value = 0;
					for (Item item : customer.get_items())
						value += scale4(item.get_length(), item.get_width(), 1, r,
								type.get_length(), type.get_width()).doubleValue();
					customer.scales.get(ptr).add(value);

					value = 0;
					for (Item item : customer.get_items())
						value += scale5(item.get_length(), item.get_width(), r, type.get_length(),
								type.get_width()).doubleValue();
					customer.scales.get(ptr).add(value);

					value = 0;
					for (Item item : customer.get_items())
						value += scale6(item.get_length(), item.get_width(), r, type.get_length(),
								type.get_width()).doubleValue();
					customer.scales.get(ptr).add(value);

					for (BigDecimal q = start; q.compareTo(stop) <= 0; q = q.add(step))
					{
						value = 0;
						for (Item item : customer.get_items())
							value += scale7(item.get_length(), item.get_width(), r, q,
									type.get_length(), type.get_width()).doubleValue();
						customer.scales.get(ptr).add(value);
					}
				}
			}
		}
	}

	private static BigDecimal scale1(int h, int w, int k, BigDecimal r, int L, int W)
	{
		return uFunction(new BigDecimal(h).divide(new BigDecimal(L), 8, BigDecimal.ROUND_HALF_UP))
				.multiply(
						UFunction(new BigDecimal(w).divide(new BigDecimal(W), 8,
								BigDecimal.ROUND_HALF_UP), r));
	}

	private static BigDecimal scale2(int h, int w, int k, BigDecimal r, int L, int W)
	{
		return uFunction(new BigDecimal(w).divide(new BigDecimal(W), 8, BigDecimal.ROUND_HALF_UP))
				.multiply(
						UFunction(new BigDecimal(h).divide(new BigDecimal(L), 8,
								BigDecimal.ROUND_HALF_UP), r));
	}

	private static BigDecimal scale3(int h, int w, int k, BigDecimal r, int L, int W)
	{
		return uFunction(new BigDecimal(h).divide(new BigDecimal(L), 8, BigDecimal.ROUND_HALF_UP))
				.multiply(
						fFunction(new BigDecimal(w).divide(new BigDecimal(W), 8,
								BigDecimal.ROUND_HALF_UP), r));
	}

	private static BigDecimal scale4(int h, int w, int k, BigDecimal r, int L, int W)
	{
		return uFunction(new BigDecimal(w).divide(new BigDecimal(W), 8, BigDecimal.ROUND_HALF_UP))
				.multiply(
						fFunction(new BigDecimal(h).divide(new BigDecimal(L), 8,
								BigDecimal.ROUND_HALF_UP), r));
	}

	private static BigDecimal scale5(int h, int w, BigDecimal r, int L, int W)
	{
		return (new BigDecimal(h).divide(new BigDecimal(L), 8, BigDecimal.ROUND_HALF_UP))
				.multiply(UFunction(
						new BigDecimal(w).divide(new BigDecimal(W), 8, BigDecimal.ROUND_HALF_UP), r));
	}

	private static BigDecimal scale6(int h, int w, BigDecimal r, int L, int W)
	{
		return (new BigDecimal(w).divide(new BigDecimal(W), 8, BigDecimal.ROUND_HALF_UP))
				.multiply(UFunction(
						new BigDecimal(h).divide(new BigDecimal(L), 8, BigDecimal.ROUND_HALF_UP), r));
	}

	private static BigDecimal scale7(int h, int w, BigDecimal r, BigDecimal q, int L, int W)
	{
		return fFunction(new BigDecimal(h).divide(new BigDecimal(L), 8, BigDecimal.ROUND_HALF_UP),
				r).multiply(
				fFunction(new BigDecimal(w).divide(new BigDecimal(W), 8, BigDecimal.ROUND_HALF_UP),
						q));
	}

	private static BigDecimal uFunction(BigDecimal xVar)
	{
		if (frac(xVar.multiply(new BigDecimal(2))).compareTo(new BigDecimal(0)) == 0)
			return xVar;
		else
			return xVar.multiply(new BigDecimal(2)).setScale(0, RoundingMode.FLOOR);
	}

	private static BigDecimal UFunction(BigDecimal xVar, BigDecimal param)
	{
		if (xVar.compareTo((new BigDecimal(1)).subtract(param)) > 0)
			return (new BigDecimal(1));
		if ((param.compareTo(xVar) <= 0)
				&& (xVar.compareTo((new BigDecimal(1)).subtract(param)) <= 0))
			return xVar;
		if (xVar.compareTo(param) < 0)
			return (new BigDecimal(0));
		return (new BigDecimal(0));
	}

	private static BigDecimal fFunction(BigDecimal xVar, BigDecimal param)
	{
		if (xVar.compareTo(new BigDecimal("0.5")) > 0)
			return (new BigDecimal(1)).subtract(((new BigDecimal(1)).subtract(xVar).divide(param,
					8, BigDecimal.ROUND_HALF_UP)).setScale(0, RoundingMode.FLOOR).divide(
					(new BigDecimal(1)).divide(param, 8, BigDecimal.ROUND_HALF_UP).setScale(0,
							RoundingMode.FLOOR), 8, BigDecimal.ROUND_HALF_UP));
		if ((param.compareTo(xVar) <= 0) && (xVar.compareTo(new BigDecimal("0.5")) <= 0))
			return (new BigDecimal(1)).divide(
					(new BigDecimal(1)).divide(param, 8, BigDecimal.ROUND_HALF_UP).setScale(0,
							RoundingMode.FLOOR), 8, BigDecimal.ROUND_HALF_UP);
		if (xVar.compareTo(param) < 0)
			return (new BigDecimal(0));
		return (new BigDecimal(0));
	}

	private static BigDecimal frac(BigDecimal xVar)
	{
		return xVar.subtract(xVar.setScale(0, RoundingMode.FLOOR));
	}
}
