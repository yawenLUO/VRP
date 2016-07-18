package objects;

import java.util.Comparator;

public class Invitation
{
	private Route route;
	private double delta;
	private double increaseOfCost;

	public Invitation(Route route, double delta)
	{
		this.route = route;
		this.delta = delta;
		this.increaseOfCost = delta * route.get_VehicleType().get_variableCost();

	}

	public Route getRoute()
	{
		return route;
	}

	public double get_delta()
	{
		return delta;
	}

	public double get_increaseOfCost()
	{
		return increaseOfCost;
	}

	public static final Comparator<Invitation> sortByCostIncrease = new Comparator<Invitation>()
	{
		public int compare(Invitation r1, Invitation r2)
		{
			if (r1.increaseOfCost < r2.increaseOfCost)
				return -1;
			else if (r1.increaseOfCost == r2.increaseOfCost)
				return 0;
			else
				return 1;
		}
	};
}
