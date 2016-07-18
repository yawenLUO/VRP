package objects;

import java.util.ArrayList;
import java.util.LinkedList;
import objects.Customer;
import objects.VehicleType;

public class Route
{
	// totalDistance and totalCost calculated by Linker.exe
	private double totalDistance = 0;
	private double totalCost = 0;
	private double totalWeight = 0;
	private VehicleType vehicleType = null;
	// indicate whether this route could add more remaining customer
	private boolean routeFlag = true;
	// the customerSequence doesn't include the depot(origin and terminus)
	private ArrayList<Customer> customerSequence = new ArrayList<Customer>();

	public void addCustomer(Customer customer)
	{
		customerSequence.add(customer);
		totalWeight += customer.get_totalWeight();

	}

	public void set_totalDistance(double totalDistance)
	{
		this.totalDistance = totalDistance;
	}

	public void set_totalCost(double totalCost)
	{
		this.totalCost = totalCost;
	}

	public void setVehicleType(VehicleType vehicleType)
	{
		this.vehicleType = vehicleType;
	}

	public double get_totalCost()
	{
		return totalCost;
	}

	public boolean getFlag()
	{
		return routeFlag;
	}

	public void setFlag(boolean flag)
	{
		routeFlag = flag;
	}

	public double get_totalWeight()
	{
		return totalWeight;
	}

	public double get_totalDistance()
	{
		return totalDistance;
	}

	public VehicleType get_VehicleType()
	{
		return vehicleType;
	}

	public ArrayList<Customer> get_customerSequence()
	{
		return customerSequence;
	}

	public void setCustomerSequence(ArrayList<Customer> newSequence)
	{
		customerSequence = newSequence;
	}
}