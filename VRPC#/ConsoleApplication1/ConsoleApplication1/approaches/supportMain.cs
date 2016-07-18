using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections;

namespace ConsoleApplication1.approaches
{



    using Customer = objects.Customer;
    using CustomerDelta = objects.CustomerDelta;
    using Invitation = objects.Invitation;
    using Route = objects.Route;
    using VehicleType = objects.VehicleType;

    public class supportMain
    {
        public static void routeTrySendInvitationProcess(Route route, List<Customer> customers, List<VehicleType> vehicleTypes, Customer depot)
        {

            if (route.Flag == true)
            {
                List<CustomerDelta> deltaList = new List<CustomerDelta>();
                // layer-D loop
                for (IEnumerator iterator = customers.GetEnumerator(); ;)
                {
                    if (!iterator.MoveNext())
                        break;
                    Customer customer = (Customer)iterator.Current;
                    if (Functions.checkScale(route, customer, vehicleTypes))
                    {
                        double delta1 = Functions.calculateDelta(route, customer, depot);
                        CustomerDelta customerDelta = new CustomerDelta(customer, delta1);
                        deltaList.Add(customerDelta);
                    }

                }
                deltaList.Sort(CustomerDelta.sortByDelta);
                bool hasChosenCustomer = false;
                Customer chosenCustomer = null;
                double delta = 0;
                for (int j = 0; j < deltaList.Count; j++)
                {
                    chosenCustomer = deltaList[j].Customer;
                    delta = deltaList[j].Delta;
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
                    route.Flag = false;

                }

            }
        }

        public static void customersTryChooseInvitationProcess(List<Customer> customers)
        {
            for (int iterator = customers.Count-1;iterator>=0 ;iterator--)
            {
                
                Customer customer = customers[iterator];
                if (customer.hasInvitations())
                {
                    Invitation invitation = customer.chooseInvitation();
                    Route route = invitation.Route;
                    route.addCustomer(customer);

                    //  Remove the current element from the iterator and the list.
                    customers.RemoveAt(iterator);
                    double distance = route.get_totalDistance() + invitation.get_delta();
                    double cost = route.get_totalCost() + invitation.get_increaseOfCost();
                    route.set_totalDistance(distance);
                    route.set_totalCost(cost);
                }
            }
        }

        //JAVA TO C# CONVERTER WARNING: Method 'throws' clauses are not available in .NET:
        //ORIGINAL LINE: public static void resetAllRoutes(java.util.ArrayList<objects.Route> routes, objects.Customer depot) throws java.io.FileNotFoundException
        public static void resetAllRoutes(List<Route> routes, Customer depot)
        {
            foreach (Route route in routes)
            {

                //need to reset the coordinates of all items in the route
                Functions.pass_packingHeauristic(route, depot);
                if (route.get_customerSequence().Count > 2)
                {
                    Functions.resetRoute(route, depot);
                }
            }
        }
    }
}