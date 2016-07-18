package approaches;

import ilog.concert.*;
import ilog.cplex.IloCplex;
import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import objects.Customer;
import objects.Invitation;
import objects.Item;
import objects.Route;
import objects.VehicleType;

public class Functions
{

	public static int[] getInitialNumberOfVehicles(ArrayList<Customer> customers,
			ArrayList<VehicleType> vehicleTypes)
	{
		int[] minMaxSizeTypes = new int[vehicleTypes.size()];
		for (int j = 0; j < vehicleTypes.size(); j++)
		{
			for (int i = 0; i < Customer.getScalesArrayLength(customers); i++)
			{
				double value = 0;
				for (Customer customer : customers)
				{
					if (customer.isServed())
						continue;
					if (customer.get_uniqueTypeIndex() != j)
						continue;
					value += customer.scales.get(VehicleType.getMaxSizeType(vehicleTypes)).get(i);
				}
				if (minMaxSizeTypes[j] < (int) Math.ceil(value))
					minMaxSizeTypes[j] = (int) Math.ceil(value);
			}
		}

		IloCplex cplex = null;
		try
		{
			cplex = new IloCplex();
			IloNumVar[][] x = new IloNumVar[customers.size()][vehicleTypes.size()];
			IloIntVar[] y = new IloIntVar[vehicleTypes.size()];

			IloLinearNumExpr objective = cplex.linearNumExpr();
			for (int j = 0; j < vehicleTypes.size(); j++)
			{
				y[j] = cplex.intVar(0, customers.size());
				IloLinearNumExpr yExpr = cplex.linearNumExpr();
				yExpr.addTerm(1, y[j]);
				for (int i = 0; i < customers.size(); i++)
				{
					if (!customers.get(i).isSuitableForType(j))
						continue;
					if (customers.get(i).isServed())
						continue;
					x[i][j] = cplex.numVar(0, 1);
					yExpr.addTerm(-1, x[i][j]);
					objective.addTerm(vehicleTypes.get(j).get_fixedCost(), x[i][j]);
				}
				cplex.addEq(yExpr, 0);
			}

			for (int j = 0; j < vehicleTypes.size(); j++)
			{
				IloLinearNumExpr expr = cplex.linearNumExpr();
				expr.addTerm(1, y[j]);
				cplex.addGe(expr, minMaxSizeTypes[j]);
			}

			for (int i = 0; i < customers.size(); i++)
			{
				if (customers.get(i).isServed())
					continue;
				IloLinearNumExpr areaExpr = cplex.linearNumExpr();
				IloLinearNumExpr weightExpr = cplex.linearNumExpr();
				for (int j = 0; j < vehicleTypes.size(); j++)
				{
					if (!customers.get(i).isSuitableForType(j))
						continue;
					areaExpr.addTerm(vehicleTypes.get(j).get_length()
							* vehicleTypes.get(j).get_width(), x[i][j]);
					weightExpr.addTerm(vehicleTypes.get(j).get_capacity(), x[i][j]);
				}
				int area = 0;
				for (Item item : customers.get(i).get_items())
					area += item.get_length() * item.get_width();
				cplex.addGe(areaExpr, area);
				cplex.addGe(weightExpr, customers.get(i).get_totalWeight());
			}
			cplex.addMinimize(objective);
			cplex.setParam(IloCplex.IntParam.MIPDisplay, 0);
			if (cplex.solve())
			{
				System.out
						.println("---> MIPObjectiveValue:                 " + cplex.getObjValue());
				int[] result = new int[vehicleTypes.size()];
				for (int j = 0; j < vehicleTypes.size(); j++)
					result[j] = (int) (Math.round(cplex.getValue(y[j])));
				cplex.end();
				return result;
			}
			else
				cplex.end();
		}
		catch (IloException e)
		{
			cplex.end();
			e.printStackTrace();
		}
		return null;
	}

	public static boolean initiaRoutes(ArrayList<Route> routes, int[] initialVehicleSet,
			ArrayList<Customer> customers, ArrayList<VehicleType> vehicleTypes, Customer depot)
	{
		int newRouteGenerated = 0;
		for (int i = initialVehicleSet.length - 1; i >= 0; i--)
		{
			int customer_index = customers.size() - 1;

			for (int j = 0; j < initialVehicleSet[i]; j++)
			{
				while (customer_index >= 0)
				{
					if (customers.get(customer_index).isSuitableForType(i))
					{
						Route route = new Route();
						route.setVehicleType(vehicleTypes.get(i));
						double fixedCost = vehicleTypes.get(i).get_fixedCost();
						double variableCost = vehicleTypes.get(i).get_variableCost();
						Customer customer = customers.remove(customer_index);
						route.addCustomer(customer);
						double distance = customer.distance(depot);
						double cost = fixedCost + variableCost * distance * 2;
						route.set_totalDistance(distance * 2);
						route.set_totalCost(cost);

						routes.add(route);
						newRouteGenerated++;
						break;
					}
					customer_index--;
				}
				if (customer_index < 0)
					break;

				customer_index--;
				if (customer_index < 0)
					break;

			}

		}
		if (newRouteGenerated == 0)
			return false;
		else
			return true;

	}
    //check both scale and capacity
	public static boolean checkScale(Route route, Customer customer,
			ArrayList<VehicleType> vehicleTypes)
	{
		
		int index = vehicleTypes.indexOf(route.get_VehicleType());
		if (!customer.isSuitableForType(index))
		{
			return false;
		}
		if (route.get_totalWeight()+customer.get_totalWeight()>route.get_VehicleType().get_capacity())
		{
			return false;
		}
		int arraySize = customer.scales.get(index).size();
		double[] scale_sum = new double[arraySize];
		for (int i = 0; i < scale_sum.length; i++)
		{
			scale_sum[i] = 0;
			for (Iterator iterator = route.get_customerSequence().iterator(); iterator.hasNext();)
			{
				Customer customer2 = (Customer) iterator.next();
				scale_sum[i] += customer2.scales.get(index).get(i);

			}
			scale_sum[i] += customer.scales.get(index).get(i);
			if (scale_sum[i] > 1)
			{
				return false;
			}
		}
		return true;
	}

	public static double calculateDelta(Route route, Customer customer, Customer depot)
	{
		if (route.get_customerSequence().size() == 1)
		{
			Customer customer_inRoute=route.get_customerSequence().get(0);
			double distance = depot.distance(customer_inRoute)+customer_inRoute.distance(customer)+customer.distance(depot);
			double delta = distance - route.get_totalDistance();
			return delta;
		}
		else
		{
			int numNodes = route.get_customerSequence().size() + 2;
			write_linkernInputFile(route, customer, depot);
			double[] result = linkernTour(numNodes);
			double totalDistance = 0;
			for (int i = 0; i < result.length; i++)
			{
				totalDistance += result[i];
			}
			// route need to update total distance
			double delta = totalDistance - route.get_totalDistance();
			return delta;
		}

	}

	// only called when route contain >=2 customers
	// called when calculate delta
	public static void write_linkernInputFile(Route route, Customer customer, Customer depot)
	{
		PrintWriter writer;
		try
		{
			writer = new PrintWriter("linkern_input");
			writer.println("NAME : 2L-HFVRP ");
			writer.println("COMMENT : 2L-HFVRP");
			writer.println("COMMENT : 2L-HFVRP");
			writer.println("TYPE : TSP");
			// existing customers+depot+new customer
			int numNodes = route.get_customerSequence().size() + 2;
			writer.println("DIMENSION : " + numNodes);
			writer.println("EDGE_WEIGHT_TYPE : EUC_2D");
			writer.println("NODE_COORD_SECTION");

			writer.println("1 " + depot.get_x() + " " + depot.get_y());

			for (int i = 2; i < numNodes; i++)
			{
				Customer currentCus = route.get_customerSequence().get(i - 2);
				writer.println(i + " " + currentCus.get_x() + " " + currentCus.get_y());
			}

			writer.println(numNodes + " " + customer.get_x() + " " + customer.get_y());
			writer.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	// only called when route contain >=3 customers
	// called in final step to reset routes
	public static void write_linkernInputFile_2(Route route, Customer depot)
	{
		PrintWriter writer;
		try
		{
			writer = new PrintWriter("linkern_input");
			writer.println("NAME : 2L-HFVRP");
			writer.println("COMMENT : 2L-HFVRP");
			writer.println("COMMENT : 2L-HFVRP");
			writer.println("TYPE : TSP");
			// existing customers+depot+new customer
			int numNodes = route.get_customerSequence().size() + 1;
			writer.println("DIMENSION : " + numNodes);
			writer.println("EDGE_WEIGHT_TYPE : EUC_2D");
			writer.println("NODE_COORD_SECTION");

			writer.println("1 " + depot.get_x() + " " + depot.get_y());

			for (int i = 2; i <= numNodes; i++)
			{
				Customer currentCus = route.get_customerSequence().get(i - 2);
				writer.println(i + " " + currentCus.get_x() + " " + currentCus.get_y());
			}

			writer.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public static boolean pass_packingHeauristic(Route route, Customer customer)
	{
		ArrayList<Item> test = new ArrayList<Item>();
		ArrayList<Item> placed = new ArrayList<Item>();
		int length = route.get_customerSequence().size();
		for (int i = 0; i < length; i++)
		{
			test.addAll(route.get_customerSequence().get(i).get_items());
		}
		test.addAll(customer.get_items());
		if (PackingInGivenBin.placeItems(route.get_VehicleType().get_length(), route
				.get_VehicleType().get_width(), placed, test))
		{
			// for (Item i : placed) System.out.println(i.toString());
			if (PackingInGivenBin.checkForPackingFeasibility(route.get_VehicleType().get_length(),
					route.get_VehicleType().get_width(), placed))
				return true;
		}
		return false;

		// checkForPackingFeasibility checks placed items for overlapping
	}

	public static void sendInvitation(Invitation invitation, Customer customer)
	{
		customer.receiveInvitation(invitation);
	}

	public static void resetRoute(Route route, Customer depot) throws FileNotFoundException
	{
		int numNodes = route.get_customerSequence().size() + 1;

		write_linkernInputFile_2(route, depot);
		linkernTour(numNodes);
		Scanner scanner = new Scanner(new File("linkern_output"));
		ArrayList<Customer> newSequence = new ArrayList<Customer>();
		int length = route.get_customerSequence().size();
		// skip 2 lines
		scanner.nextLine();
		scanner.nextLine();
		for (int i = 0; i < length; i++)
		{
			String line = scanner.nextLine();
			Scanner lineScanner = new Scanner(line);
			int index = Integer.parseInt(lineScanner.next()) - 1;
			Customer tempCustomer = route.get_customerSequence().get(index);
			newSequence.add(tempCustomer);
		}
		route.setCustomerSequence(newSequence);

	}

	public static void printRoutes(ArrayList<Route> routes, Customer depot)
	{
		double totalCost=0;
		for (int i = 0; i < routes.size(); i++)
		{
			System.out.println();
			System.out.println("Route " + i + ":");
			System.out.print("vehicleType is: ");
			routes.get(i).get_VehicleType().print();
			System.out.println("Total Weight: " + routes.get(i).get_totalWeight()
					+ " Total Distance: " + routes.get(i).get_totalDistance() + " Total Cost: "
					+ routes.get(i).get_totalCost());
			totalCost+=routes.get(i).get_totalCost();
			System.out.println();
			// print the customer sequence of this route
			System.out.println("depot " + "(" + depot.get_x() + "," + depot.get_y() + ")  ");
			System.out.println();
			System.out.println("-->");
			ArrayList<Customer> sequence = routes.get(i).get_customerSequence();
			for (int j = 0; j < sequence.size(); j++)
			{
				Customer currentCus = sequence.get(j);
				currentCus.print();
				System.out.println("-->");
				// System.out.print("Cus"+currentCus.get_index()+" Weight:"+currentCus.get_totalWeight()+" ("+currentCus.get_x()+","+currentCus.get_y()+") -> ");
			}
			System.out.println("  depot " + "(" + depot.get_x() + "," + depot.get_y() + ")");
			System.out.println();

			// print all items and the coordinates
			System.out.println("Items and coordinates:");
			for (int j = 0; j < sequence.size(); j++)
			{
				Customer currentCus = sequence.get(j);
				ArrayList<Item> items = currentCus.get_items();
				for (Iterator iterator = items.iterator(); iterator.hasNext();)
				{
					Item item = (Item) iterator.next();
					System.out.println(item.toString());
				}
			}
			System.out.println();
		}
		System.out.println("------------------------------------------------");
		System.out.println("totalCost: "+totalCost);
	}

	public static double[] linkernTour(int numNodes)
	{
		double[] result = new double[numNodes];

		try
		{
			List<String> command = new ArrayList<String>();
			if (System.getProperty("os.name").contains("Windows"))
			{// WINDOWS OS
				command.add("linkern.exe");
			}
			else
			{// LINUX
				command.add("./linkern");
			}
			command.add("-o");
			command.add("linkern_output");
			command.add("linkern_input");

			ProcessBuilder builder = new ProcessBuilder(command);
			builder.redirectErrorStream(true);
			final Process process = builder.start();
			//main thread wait for this process to end and exit, then continue.
			process.waitFor();
			//InputStream is = process.getInputStream();
			//InputStreamReader isr = new InputStreamReader(is);
			//BufferedReader br = new BufferedReader(isr);
			String line;

			//br.close();

			Scanner scanner = new Scanner(new File("linkern_output"));
			// discard the first line
			scanner.nextLine();
			line = null;
			for (int i = 0; i < result.length; i++)
			{
				line = scanner.nextLine();

				double number = Double.parseDouble(line.split("\\s+")[2]);
				result[i] = number;
			}

			scanner.close();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return result;
	}
}
