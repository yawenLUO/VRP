package objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import approaches.PackingInGivenBin;

public class Customer
{
	private int index = -1;
	private double totalWeight;
	private double x, y;
	private ArrayList<Item> items;
	private boolean isServed = false;
	private boolean[] isSuitableForType;
	private int uniqueTypeIndex = -1;
	private int suitableScore = -1;
	private ArrayList<Invitation> invitations = new ArrayList<Invitation>();

	public static final Comparator<Customer> sort_by_suitablescore = new Comparator<Customer>()
	{
		public int compare(Customer r1, Customer r2)
		{
			if (r1.suitableScore < r2.suitableScore)
				return -1;
			else if (r1.suitableScore == r2.suitableScore)
				return 0;
			else
				return 1;
		}
	};

	public ArrayList<ArrayList<Double>> scales = new ArrayList<ArrayList<Double>>();

	public Customer(int index, double totalWeight, double x, double y, ArrayList<Item> items,
			ArrayList<VehicleType> vehicleTypes)
	{
		this.index = index;
		this.totalWeight = totalWeight;
		this.x = x;
		this.y = y;
		this.items = items;

		isSuitableForType = new boolean[vehicleTypes.size()];
		int i = 0, numSuitableTypes = 0;
		for (VehicleType type : vehicleTypes)
		{
			boolean flag = true;
			for (Item item : items)
				if ((item.get_length() > type.get_length())
						|| (item.get_width() > type.get_width()))
				{
					flag = false;
					break;
				}
			
			if (flag) 
			{
				ArrayList<Item> test = new ArrayList<Item>();
				ArrayList<Item> placed = new ArrayList<Item>();
				test.addAll(items);
				if (!PackingInGivenBin.placeItems(type.get_length(), type.get_width(), placed, test)) flag = false;
			}
			isSuitableForType[i] = flag && (totalWeight <= type.get_capacity());
			if (isSuitableForType[i])
			{
				numSuitableTypes++;
				uniqueTypeIndex = i;
			}
			i++;
		}
		if (numSuitableTypes > 1)
			uniqueTypeIndex = -1;

		for (int j = 0; j < isSuitableForType.length; j++)
		{
			if (isSuitableForType(j))
			{
				suitableScore = j;
				break;
			}
		}
	}

	public void receiveInvitation(Invitation invitation)
	{
		invitations.add(invitation);
	}

	public Invitation chooseInvitation()
	{
		Collections.sort(invitations, Invitation.sortByCostIncrease);
		return invitations.get(0);
	}

	public boolean hasInvitations()
	{
		if (invitations.size() == 0)
			return false;
		else
		{
			return true;
		}
	}

	public int get_suitableScore()
	{
		return suitableScore;
	}

	public boolean isSuitableForType(int index)
	{
		return isSuitableForType[index];
	}

	public double distance(Customer anotherCustomer)
	{
		return Math.sqrt(Math.pow(this.x - anotherCustomer.x, 2)
				+ Math.pow(this.y - anotherCustomer.y, 2));
	}

	public void print()
	{
		System.out.println("- customer " + index + " totalWeight " + totalWeight + " coordinates: "
				+ x + "   " + y + " \tis suitable for vehicle types: "
				+ Arrays.toString(isSuitableForType));
		for (Iterator iterator = items.iterator(); iterator.hasNext();)
		{
			Item item = (Item) iterator.next();
			item.print();
		}
		System.out.println();
	}

	public double get_totalWeight()
	{
		return totalWeight;
	}

	public double get_x()
	{
		return x;
	}

	public double get_y()
	{
		return y;
	}

	public boolean isServed()
	{
		return isServed;
	}

	public ArrayList<Item> get_items()
	{
		return items;
	}

	public int get_uniqueTypeIndex()
	{
		return uniqueTypeIndex;
	}

	public int get_index()
	{
		return index;
	}

	public static int getScalesArrayLength(ArrayList<Customer> customers)
	{
		if (customers.isEmpty())
			return 0;
		else
			return customers.get(0).scales.get(0).size();
	}
}
