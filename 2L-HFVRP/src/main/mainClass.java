package main;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import approaches.*;
import objects.Customer;
import objects.CustomerDelta;
import objects.Invitation;
import objects.Item;
import objects.Route;
import objects.VehicleType;
import Fekete.lbFekete;
import IO.InstanceRead;

public class mainClass
{
	private static ArrayList<Customer> customers;
	private static ArrayList<Route> routes = new ArrayList<Route>();
	private static ArrayList<VehicleType> vehicleTypes;

	public static void main(String[] args) throws FileNotFoundException
	{
		String fileName_prefix = args[0];
		InstanceRead instanceReader = new InstanceRead(fileName_prefix);
		vehicleTypes = instanceReader.readVehicles();

		customers = instanceReader.readCustomers(vehicleTypes);
		// No.0 customer is depot, should be removed from the list and store
		// separately.
		Customer depot = customers.remove(0);
		Collections.sort(customers, Customer.sort_by_suitablescore);

		for (VehicleType vehicleType : vehicleTypes)
			vehicleType.print();
		for (Customer customer : customers)
			customer.print();
//corresponding to Run button pressed part
		lbFekete.scaleGeneration(new BigDecimal("0.1"), new BigDecimal("0.1"), customers, vehicleTypes);

		int routeIndex_begin = 0;
		boolean isFeasible = true;
		// need a loop here to repeat the process for remaining customers until
		// none left or declare infeasible.
		// layer-A loop
		while (customers.size() > 0)
		{
			int[] initialVehicleSet = Functions.getInitialNumberOfVehicles(customers, vehicleTypes);

			// allocate one customer to one truck according to
			// initialVehicleSet[]
			isFeasible = Functions.initiaRoutes(routes, initialVehicleSet, customers, vehicleTypes,
					depot);
			if (!isFeasible)
				break;
			boolean currentRoutesFlag = true;
			// layer-B loop
			while (customers.size() > 0 && currentRoutesFlag)
			{
				// layer-C loop
				for (int i = routeIndex_begin; i < routes.size(); i++)
				{
					Route route = routes.get(i);
					supportMain
							.routeTrySendInvitationProcess(route, customers, vehicleTypes, depot);

				}
				
					
				supportMain.customersTryChooseInvitationProcess( customers);

				

				currentRoutesFlag = false;
				for (int i = routeIndex_begin; i < routes.size(); i++)
				{
					if (routes.get(i).getFlag() == true)
					{
						currentRoutesFlag = true;
						break;
					}
				}

			}
			routeIndex_begin = routes.size();
		}
		if (!isFeasible)
			System.out
					.println("We think some items from one or more customers are too big to be packed in any type of the current vehicle sets");
		else
		{
			supportMain.resetAllRoutes(routes, depot);
			Functions.printRoutes(routes, depot);
		}

	}
}
