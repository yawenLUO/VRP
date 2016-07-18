package approaches;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import objects.Customer;
import objects.CustomerDelta;
import objects.Invitation;
import objects.Route;
import objects.VehicleType;

public class supportMain
{
	public static void routeTrySendInvitationProcess(Route route, ArrayList<Customer> customers,
			ArrayList<VehicleType> vehicleTypes, Customer depot)
	{

		if (route.getFlag() == true)
		{
			ArrayList<CustomerDelta> deltaList = new ArrayList<CustomerDelta>();
			// layer-D loop
			for (Iterator iterator = customers.iterator(); iterator.hasNext();)
			{
				Customer customer = (Customer) iterator.next();
				if (Functions.checkScale(route, customer, vehicleTypes))
				{
					double delta = Functions.calculateDelta(route, customer, depot);
					CustomerDelta customerDelta = new CustomerDelta(customer, delta);
					deltaList.add(customerDelta);
				}

			}
			Collections.sort(deltaList, CustomerDelta.sortByDelta);
			boolean hasChosenCustomer = false;
			Customer chosenCustomer = null;
			double delta = 0;
			for (int j = 0; j < deltaList.size(); j++)
			{
				chosenCustomer = deltaList.get(j).getCustomer();
				delta = deltaList.get(j).getDelta();
				if (Functions.pass_packingHeauristic(route, chosenCustomer))
				{
					hasChosenCustomer = true;
					break;
				}
			}
			if (hasChosenCustomer)
			{
				Functions.sendInvitation(new Invitation(route, delta), chosenCustomer);
			}
			else
			{
				route.setFlag(false);

			}

		}
	}

	public static void customersTryChooseInvitationProcess(ArrayList<Customer> customers)
	{
		for (Iterator iterator = customers.iterator(); iterator.hasNext();)
		{
			Customer customer = (Customer) iterator.next();
			if (customer.hasInvitations())
			{
				Invitation invitation = customer.chooseInvitation();
				Route route = invitation.getRoute();
				route.addCustomer(customer);
				
				//  Remove the current element from the iterator and the list.
				iterator.remove();
				double distance = route.get_totalDistance() + invitation.get_delta();
				double cost = route.get_totalCost() + invitation.get_increaseOfCost();
				route.set_totalDistance(distance);
				route.set_totalCost(cost);
			}
		}
	}

	public static void resetAllRoutes(ArrayList<Route> routes, Customer depot)
			throws FileNotFoundException
	{
		for (Route route : routes)
		{
			
			//need to reset the coordinates of all items in the route
			Functions.pass_packingHeauristic(route, depot);
			if (route.get_customerSequence().size() > 2)
				Functions.resetRoute(route, depot);
		}
	}
}
