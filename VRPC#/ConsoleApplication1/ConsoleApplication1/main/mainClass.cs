using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;




namespace main
{

    using ConsoleApplication1.approaches;
    using Customer = ConsoleApplication1.objects.Customer;
    using CustomerDelta = ConsoleApplication1.objects.CustomerDelta;
    using Invitation = ConsoleApplication1.objects.Invitation;
    using Item = ConsoleApplication1.objects.Item;
    using Route = ConsoleApplication1.objects.Route;
    using VehicleType = ConsoleApplication1.objects.VehicleType;
    using lbFekete = ConsoleApplication1.Fekete.lbFekete;
    using InstanceRead = ConsoleApplication1.IO.InstanceRead;

    public class mainClass
    {
        private static List<Customer> customers;
        private static List<Route> routes = new List<Route>();
        private static List<VehicleType> vehicleTypes;

        //JAVA TO C# CONVERTER WARNING: Method 'throws' clauses are not available in .NET:
        //ORIGINAL LINE: public static void main(String[] args) throws java.io.FileNotFoundException
        public static void Main(string[] args)
        {
            string fileName_prefix = args[0];
            InstanceRead instanceReader = new InstanceRead(fileName_prefix);
            vehicleTypes = instanceReader.readVehicles();

            customers = instanceReader.readCustomers(vehicleTypes);
            // No.0 customer is depot, should be removed from the list and store
            // separately.
            Customer depot = customers[0];
            customers.RemoveAt(0);
            customers.Sort(Customer.sort_by_suitablescore);

            foreach (VehicleType vehicleType in vehicleTypes)
            {
                vehicleType.print();
            }
            foreach (Customer customer in customers)
            {
                customer.print();
            }
            //corresponding to Run button pressed part
            lbFekete.scaleGeneration(new decimal("0.1"), new decimal("0.1"), customers, vehicleTypes);

            int routeIndex_begin = 0;
            bool isFeasible = true;
            // need a loop here to repeat the process for remaining customers until
            // none left or declare infeasible.
            // layer-A loop
            while (customers.Count > 0)
            {
                int[] initialVehicleSet = Functions.getInitialNumberOfVehicles(customers, vehicleTypes);

                // allocate one customer to one truck according to
                // initialVehicleSet[]
                isFeasible = Functions.initiaRoutes(routes, initialVehicleSet, customers, vehicleTypes, depot);
                if (!isFeasible)
                {
                    break;
                }
                bool currentRoutesFlag = true;
                // layer-B loop
                while (customers.Count > 0 && currentRoutesFlag)
                {
                    // layer-C loop
                    for (int i = routeIndex_begin; i < routes.Count; i++)
                    {
                        Route route = routes[i];
                        supportMain.routeTrySendInvitationProcess(route, customers, vehicleTypes, depot);

                    }


                    supportMain.customersTryChooseInvitationProcess(customers);



                    currentRoutesFlag = false;
                    for (int i = routeIndex_begin; i < routes.Count; i++)
                    {
                        if (routes[i].Flag == true)
                        {
                            currentRoutesFlag = true;
                            break;
                        }
                    }

                }
                routeIndex_begin = routes.Count;
            }
            if (!isFeasible)
            {
                Console.WriteLine("We think some items from one or more customers are too big to be packed in any type of the current vehicle sets");
            }
            else
            {
                supportMain.resetAllRoutes(routes, depot);
                Functions.printRoutes(routes, depot);
            }

        }
    }
}