package IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import objects.Customer;
import objects.Item;
import objects.VehicleType;

public class InstanceRead
{
	private String fileName_prefix;
	private boolean GUIMode;
	private File file;

	public InstanceRead(String fileName_prefix)
	{
		this.fileName_prefix = fileName_prefix;
		GUIMode = false;
	}

	public InstanceRead(File file)
	{
		this.file = file;
		GUIMode = true;
	}

	public ArrayList<Customer> readCustomers(ArrayList<VehicleType> vehicleTypes)
			throws FileNotFoundException
	{
		ArrayList<Customer> customers = new ArrayList<Customer>();
		// we put "inputs" folder under the root directory of the 2L-HFVRP
		// project.
		Scanner customerScanner;
		if (GUIMode)
		{
			customerScanner = new Scanner(file);
		}
		else
		{
			String customerFileName = "inputs/" + fileName_prefix + "_input_node.txt";

			customerScanner = new Scanner(new File(customerFileName));
		}
		customerScanner.nextLine();
		int index = 0;
		while (customerScanner.hasNextLine())
		{
			String line = customerScanner.nextLine();
			Scanner lineScanner = new Scanner(line);
			double x = Double.parseDouble(lineScanner.next());
			double y = Double.parseDouble(lineScanner.next());
			double totalWeight = Double.parseDouble(lineScanner.next());
			int numOfItems = Integer.parseInt(lineScanner.next());
			ArrayList<Item> items = new ArrayList<Item>();
			if (numOfItems > 0)
			{
				for (int i = 0; i < numOfItems; i++)
				{
					items.add(new Item(Integer.parseInt(lineScanner.next()), Integer
							.parseInt(lineScanner.next()), i, index));
				}
			}
			Customer customer = new Customer(index, totalWeight, x, y, items, vehicleTypes);
			customers.add(customer);
			index++;
			lineScanner.close();
		}
		customerScanner.close();
		return customers;
	}

	public ArrayList<VehicleType> readVehicles() throws FileNotFoundException
	{
		ArrayList<VehicleType> vehicleTypes = new ArrayList<VehicleType>();
		String vehicleTypeFileName;
		if (GUIMode)
		{
			String[] temp = file.getPath().split("node");
			vehicleTypeFileName = temp[0] + "vehicle.txt";
		}
		else
		{
			vehicleTypeFileName = "inputs/" + fileName_prefix + "_input_vehicle.txt";
		}
		Scanner vehicleScanner = new Scanner(new File(vehicleTypeFileName));
		vehicleScanner.nextLine();
		while (vehicleScanner.hasNextLine())
		{
			String line = vehicleScanner.nextLine();
			Scanner lineScanner = new Scanner(line);
			lineScanner.next(); // in our project the quantity of trucks of each
								// type is unlimited, we omit the quantity info
								// in the file
			int capacity = Integer.parseInt(lineScanner.next().replace(".0", ""));
			int width = Integer.parseInt(lineScanner.next());
			int length = Integer.parseInt(lineScanner.next());
			double fixedCost = Double.parseDouble(lineScanner.next());
			double variableCost = Double.parseDouble(lineScanner.next());
			VehicleType vehicleType = new VehicleType(capacity, length, width, fixedCost,
					variableCost);
			vehicleTypes.add(vehicleType);
			lineScanner.close();
		}
		vehicleScanner.close();
		return vehicleTypes;
	}

}
