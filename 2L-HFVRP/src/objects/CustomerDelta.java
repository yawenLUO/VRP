package objects;

import java.util.Comparator;

public class CustomerDelta
{
	private Customer customer;
	double delta;

	public CustomerDelta(Customer customer, double delta)
	{
		this.customer = customer;
		this.delta = delta;
	}

	public static final Comparator<CustomerDelta> sortByDelta = new Comparator<CustomerDelta>()
	{
		public int compare(CustomerDelta r1, CustomerDelta r2)
		{
			if (r1.delta < r2.delta)
				return -1;
			else if (r1.delta == r2.delta)
				return 0;
			else
				return 1;
		}
	};

	public Customer getCustomer()
	{
		return customer;
	}

	public double getDelta()
	{
		return delta;
	}
}
