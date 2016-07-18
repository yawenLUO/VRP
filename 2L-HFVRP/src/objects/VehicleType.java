package objects;

import java.util.ArrayList;

public class VehicleType
{
	private int capacity, length, width;
	private double fixedCost, variableCost;
	private int area;
	public ArrayList<Posi_tion> pos=new ArrayList<Posi_tion>();

	public VehicleType(int capacity, int length, int width, double fixedCost, double variableCost)
	{
		this.capacity = capacity;
		this.length = length;
		this.width = width;
		this.fixedCost = fixedCost;
		this.variableCost = variableCost;
		this.area = length*width;
		pos.add(new Posi_tion());
	}

	public void print()
	{
		System.out.println("capacity: " + capacity + " length: " + length + " width: " + width
				+ " fixedCost: " + fixedCost + " variableCost: " + variableCost);
	}

	public int get_capacity()
	{
		return capacity;
	}

	public int get_length()
	{
		return length;
	}

	public int get_width()
	{
		return width;
	}

	public double get_fixedCost()
	{
		return fixedCost;
	}
	
	public int get_area()
	{
		return area;
	}

	public double get_variableCost()
	{
		return variableCost;
	}

	public static int getMaxSizeType(ArrayList<VehicleType> vehicleTypes)
	{
		if (vehicleTypes.isEmpty())
			return -1;
		else
			return vehicleTypes.size() - 1;
	}

	
	

}

