using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections;
using Microsoft.Build.Utilities;
using Microsoft.Build.Framework;




namespace ConsoleApplication1.approaches
{

    using ilog.concert;
    using IloCplex = ilog.cplex.IloCplex;
    using Customer = objects.Customer;
    using Invitation = objects.Invitation;
    using Item = objects.Item;
    using Route = objects.Route;
    using VehicleType = objects.VehicleType;
    using System.IO;

    public class Functions
    {

        public static int[] getInitialNumberOfVehicles(List<Customer> customers, List<VehicleType> vehicleTypes)
        {
            int[] minMaxSizeTypes = new int[vehicleTypes.Count];
            for (int j = 0; j < vehicleTypes.Count; j++)
            {
                for (int i = 0; i < Customer.getScalesArrayLength(customers); i++)
                {
                    double value = 0;
                    foreach (Customer customer in customers)
                    {
                        if (customer.Served)
                        {
                            continue;
                        }
                        if (customer.get_uniqueTypeIndex() != j)
                        {
                            continue;
                        }
                        value += (double)customer.scales[VehicleType.getMaxSizeType(vehicleTypes)][i];
                    }
                    if (minMaxSizeTypes[j] < (int)Math.Ceiling(value))
                    {
                        minMaxSizeTypes[j] = (int)Math.Ceiling(value);
                    }
                }
            }

            IloCplex cplex = null;
            try
            {
                cplex = new IloCplex();
                //JAVA TO C# CONVERTER NOTE: The following call to the 'RectangularArrays' helper class reproduces the rectangular array initialization that is automatic in Java:
                //ORIGINAL LINE: IloNumVar[][] x = new IloNumVar[customers.Count][vehicleTypes.Count];
                IloNumVar[][] x = RectangularArrays.ReturnRectangularIloNumVarArray(customers.Count, vehicleTypes.Count);
                IloIntVar[] y = new IloIntVar[vehicleTypes.Count];

                IloLinearNumExpr objective = cplex.linearNumExpr();
                for (int j = 0; j < vehicleTypes.Count; j++)
                {
                    y[j] = cplex.intVar(0, customers.Count);
                    IloLinearNumExpr yExpr = cplex.linearNumExpr();
                    yExpr.addTerm(1, y[j]);
                    for (int i = 0; i < customers.Count; i++)
                    {
                        if (!customers[i].isSuitableForType(j))
                        {
                            continue;
                        }
                        if (customers[i].Served)
                        {
                            continue;
                        }
                        x[i][j] = cplex.numVar(0, 1);
                        yExpr.addTerm(-1, x[i][j]);
                        objective.addTerm(vehicleTypes[j].get_fixedCost(), x[i][j]);
                    }
                    cplex.addEq(yExpr, 0);
                }

                for (int j = 0; j < vehicleTypes.Count; j++)
                {
                    IloLinearNumExpr expr = cplex.linearNumExpr();
                    expr.addTerm(1, y[j]);
                    cplex.addGe(expr, minMaxSizeTypes[j]);
                }

                for (int i = 0; i < customers.Count; i++)
                {
                    if (customers[i].Served)
                    {
                        continue;
                    }
                    IloLinearNumExpr areaExpr = cplex.linearNumExpr();
                    IloLinearNumExpr weightExpr = cplex.linearNumExpr();
                    for (int j = 0; j < vehicleTypes.Count; j++)
                    {
                        if (!customers[i].isSuitableForType(j))
                        {
                            continue;
                        }
                        areaExpr.addTerm(vehicleTypes[j].get_length() * vehicleTypes[j].get_width(), x[i][j]);
                        weightExpr.addTerm(vehicleTypes[j].get_capacity(), x[i][j]);
                    }
                    int area = 0;
                    foreach (Item item in customers[i].get_items())
                    {
                        area += item.get_length() * item.get_width();
                    }
                    cplex.addGe(areaExpr, area);
                    cplex.addGe(weightExpr, customers[i].get_totalWeight());
                }
                cplex.addMinimize(objective);
                cplex.setParam(IloCplex.IntParam.MIPDisplay, 0);
                if (cplex.solve())
                {
                    Console.WriteLine("---> MIPObjectiveValue:                 " + cplex.ObjValue);
                    int[] result = new int[vehicleTypes.Count];
                    for (int j = 0; j < vehicleTypes.Count; j++)
                    {
                        result[j] = (int)(Math.Round(cplex.getValue(y[j])));
                    }
                    cplex.end();
                    return result;
                }
                else
                {
                    cplex.end();
                }
            }
            catch (IloException e)
            {
                cplex.end();
                Console.WriteLine(e.ToString());
                Console.Write(e.StackTrace);
            }
            return null;
        }

        public static bool initiaRoutes(List<Route> routes, int[] initialVehicleSet, List<Customer> customers, List<VehicleType> vehicleTypes, Customer depot)
        {
            int newRouteGenerated = 0;
            for (int i = initialVehicleSet.Length - 1; i >= 0; i--)
            {
                int customer_index = customers.Count - 1;

                for (int j = 0; j < initialVehicleSet[i]; j++)
                {
                    while (customer_index >= 0)
                    {
                        if (customers[customer_index].isSuitableForType(i))
                        {
                            Route route = new Route();
                            route.VehicleType = vehicleTypes[i];
                            double fixedCost = vehicleTypes[i].get_fixedCost();
                            double variableCost = vehicleTypes[i].get_variableCost();
                            Customer customer = customers[customer_index];
                            customers.RemoveAt(customer_index);
                            route.addCustomer(customer);
                            double distance = customer.distance(depot);
                            double cost = fixedCost + variableCost * distance * 2;
                            route.set_totalDistance(distance * 2);
                            route.set_totalCost(cost);

                            routes.Add(route);
                            newRouteGenerated++;
                            break;
                        }
                        customer_index--;
                    }
                    if (customer_index < 0)
                    {
                        break;
                    }

                    customer_index--;
                    if (customer_index < 0)
                    {
                        break;
                    }

                }

            }
            if (newRouteGenerated == 0)
            {
                return false;
            }
            else
            {
                return true;
            }

        }
        //check both scale and capacity
        public static bool checkScale(Route route, Customer customer, List<VehicleType> vehicleTypes)
        {

            int index = vehicleTypes.IndexOf(route.get_VehicleType());
            if (!customer.isSuitableForType(index))
            {
                return false;
            }
            if (route.get_totalWeight() + customer.get_totalWeight() > route.get_VehicleType().get_capacity())
            {
                return false;
            }
            int arraySize = customer.scales[index].Count;
            double[] scale_sum = new double[arraySize];
            for (int i = 0; i < scale_sum.Length; i++)
            {
                scale_sum[i] = 0;
                for (IEnumerator iterator = route.get_customerSequence().GetEnumerator(); ;)
                {
                    if (!iterator.MoveNext())
                        break;
                    Customer customer2 = (Customer)iterator.Current;
                    scale_sum[i] += (double)customer2.scales[index][i];

                }
                scale_sum[i] += (double)customer.scales[index][i];
                if (scale_sum[i] > 1)
                {
                    return false;
                }
            }
            return true;
        }

        public static double calculateDelta(Route route, Customer customer, Customer depot)
        {
            if (route.get_customerSequence().Count == 1)
            {
                Customer customer_inRoute = route.get_customerSequence()[0];
                double distance = depot.distance(customer_inRoute) + customer_inRoute.distance(customer) + customer.distance(depot);
                double delta = distance - route.get_totalDistance();
                return delta;
            }
            else
            {
                int numNodes = route.get_customerSequence().Count + 2;
                write_linkernInputFile(route, customer, depot);
                double[] result = linkernTour(numNodes);
                double totalDistance = 0;
                for (int i = 0; i < result.Length; i++)
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
            StreamWriter writer;
            try
            {
                writer = new StreamWriter("linkern_input");
                writer.WriteLine("NAME : 2L-HFVRP ");
                writer.WriteLine("COMMENT : 2L-HFVRP");
                writer.WriteLine("COMMENT : 2L-HFVRP");
                writer.WriteLine("TYPE : TSP");
                // existing customers+depot+new customer
                int numNodes = route.get_customerSequence().Count + 2;
                writer.WriteLine("DIMENSION : " + numNodes);
                writer.WriteLine("EDGE_WEIGHT_TYPE : EUC_2D");
                writer.WriteLine("NODE_COORD_SECTION");

                writer.WriteLine("1 " + depot.get_x() + " " + depot.get_y());

                for (int i = 2; i < numNodes; i++)
                {
                    Customer currentCus = route.get_customerSequence()[i - 2];
                    writer.WriteLine(i + " " + currentCus.get_x() + " " + currentCus.get_y());
                }

                writer.WriteLine(numNodes + " " + customer.get_x() + " " + customer.get_y());
                writer.Close();
            }
            catch (FileNotFoundException e)
            {
                Console.WriteLine(e.ToString());
                Console.Write(e.StackTrace);
            }
        }

        // only called when route contain >=3 customers
        // called in final step to reset routes
        public static void write_linkernInputFile_2(Route route, Customer depot)
        {
            StreamWriter writer;
            try
            {
                writer = new StreamWriter("linkern_input");
                writer.WriteLine("NAME : 2L-HFVRP");
                writer.WriteLine("COMMENT : 2L-HFVRP");
                writer.WriteLine("COMMENT : 2L-HFVRP");
                writer.WriteLine("TYPE : TSP");
                // existing customers+depot+new customer
                int numNodes = route.get_customerSequence().Count + 1;
                writer.WriteLine("DIMENSION : " + numNodes);
                writer.WriteLine("EDGE_WEIGHT_TYPE : EUC_2D");
                writer.WriteLine("NODE_COORD_SECTION");

                writer.WriteLine("1 " + depot.get_x() + " " + depot.get_y());

                for (int i = 2; i <= numNodes; i++)
                {
                    Customer currentCus = route.get_customerSequence()[i - 2];
                    writer.WriteLine(i + " " + currentCus.get_x() + " " + currentCus.get_y());
                }

                writer.Close();
            }
            catch (FileNotFoundException e)
            {
                Console.WriteLine(e.ToString());
                Console.Write(e.StackTrace);
            }
        }

        public static bool pass_packingHeauristic(Route route, Customer customer)
        {
            List<Item> test = new List<Item>();
            List<Item> placed = new List<Item>();
            int length = route.get_customerSequence().Count;
            for (int i = 0; i < length; i++)
            {
                test.AddRange(route.get_customerSequence()[i].get_items());
            }
            test.AddRange(customer.get_items());
            if (PackingInGivenBin.placeItems(route.get_VehicleType().get_length(), route.get_VehicleType().get_width(), placed, test))
            {
                // for (Item i : placed) System.out.println(i.toString());
                if (PackingInGivenBin.checkForPackingFeasibility(route.get_VehicleType().get_length(), route.get_VehicleType().get_width(), placed))
                {
                    return true;
                }
            }
            return false;

            // checkForPackingFeasibility checks placed items for overlapping
        }

        public static void sendInvitation(Invitation invitation, Customer customer)
        {
            customer.receiveInvitation(invitation);
        }

        //JAVA TO C# CONVERTER WARNING: Method 'throws' clauses are not available in .NET:
        //ORIGINAL LINE: public static void resetRoute(objects.Route route, objects.Customer depot) throws java.io.FileNotFoundException
        public static void resetRoute(Route route, Customer depot)
        {
            int numNodes = route.get_customerSequence().Count + 1;

            write_linkernInputFile_2(route, depot);
            linkernTour(numNodes);
            StreamReader scanner = new StreamReader("linkern_output");
            List<Customer> newSequence = new List<Customer>();
            int length = route.get_customerSequence().Count;
            // skip 2 lines
            scanner.ReadLine();
            scanner.ReadLine();
            for (int i = 0; i < length; i++)
            {
                string line = scanner.ReadLine();
                String[] lineScanner = line.Split(null);
                int index = Convert.ToInt32(lineScanner[0]) - 1;
                Customer tempCustomer = route.get_customerSequence()[index];
                newSequence.Add(tempCustomer);
            }
            route.CustomerSequence = newSequence;

        }

        public static void printRoutes(List<Route> routes, Customer depot)
        {
            double totalCost = 0;
            for (int i = 0; i < routes.Count; i++)
            {
                Console.WriteLine();
                Console.WriteLine("Route " + i + ":");
                Console.Write("vehicleType is: ");
                routes[i].get_VehicleType().print();
                Console.WriteLine("Total Weight: " + routes[i].get_totalWeight() + " Total Distance: " + routes[i].get_totalDistance() + " Total Cost: " + routes[i].get_totalCost());
                totalCost += routes[i].get_totalCost();
                Console.WriteLine();
                // print the customer sequence of this route
                Console.WriteLine("depot " + "(" + depot.get_x() + "," + depot.get_y() + ")  ");
                Console.WriteLine();
                Console.WriteLine("-->");
                List<Customer> sequence = routes[i].get_customerSequence();
                for (int j = 0; j < sequence.Count; j++)
                {
                    Customer currentCus = sequence[j];
                    currentCus.print();
                    Console.WriteLine("-->");
                    // System.out.print("Cus"+currentCus.get_index()+" Weight:"+currentCus.get_totalWeight()+" ("+currentCus.get_x()+","+currentCus.get_y()+") -> ");
                }
                Console.WriteLine("  depot " + "(" + depot.get_x() + "," + depot.get_y() + ")");
                Console.WriteLine();

                // print all items and the coordinates
                Console.WriteLine("Items and coordinates:");
                for (int j = 0; j < sequence.Count; j++)
                {
                    Customer currentCus = sequence[j];
                    List<Item> items = currentCus.get_items();
                    for (IEnumerator iterator = items.GetEnumerator(); ;)
                    {
                        if (!iterator.MoveNext())
                            break;
                        Item item = (Item)iterator.Current;
                        Console.WriteLine(item.ToString());
                    }
                }
                Console.WriteLine();
            }
            Console.WriteLine("------------------------------------------------");
            Console.WriteLine("totalCost: " + totalCost);
        }

        public static double[] linkernTour(int numNodes)
        {
            double[] result = new double[numNodes];

            try
            {
                IList<string> command = new List<string>();
                
                    command.Add("linkern.exe");
               
                command.Add("-o");
                command.Add("linkern_output");
                command.Add("linkern_input");

                CommandLineBuilder builder = new CommandLineBuilder(command);
                builder.redirectErrorStream(true);
                //JAVA TO C# CONVERTER WARNING: The original Java variable was marked 'final':
                //ORIGINAL LINE: final Process process = builder.start();
                Process process = builder.start();
                //main thread wait for this process to end and exit, then continue.
                process.waitFor();
                //InputStream is = process.getInputStream();
                //InputStreamReader isr = new InputStreamReader(is);
                //BufferedReader br = new BufferedReader(isr);
                string line;

                //br.close();

                StreamReader scanner = new StreamReader("linkern_output");
                // discard the first line
                scanner.ReadLine();
                line = null;
                for (int i = 0; i < result.Length; i++)
                {
                    line = scanner.ReadLine();

                    double number = Convert.ToDouble(line.Split("\\s+", true)[2]);
                    result[i] = number;
                }

                scanner.Close();

            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.ToString());
                Console.Write(ex.StackTrace);
            }

            return result;
        }
    }
}

//-------------------------------------------------------------------------------------------
//	Copyright © 2007 - 2013 Tangible Software Solutions Inc.
//	This class can be used by anyone provided that the copyright notice remains intact.
//
//	This class is used to convert some aspects of the Java String class.
//-------------------------------------------------------------------------------------------
internal static class StringHelperClass
{
    //------------------------------------------------------------------------------------
    //	This method is used to replace calls to the 2-arg Java String.startsWith method.
    //------------------------------------------------------------------------------------
    internal static bool StartsWith(this string self, string prefix, int toffset)
    {
        return self.IndexOf(prefix, toffset) == toffset;
    }

    //------------------------------------------------------------------------------
    //	This method is used to replace most calls to the Java String.split method.
    //------------------------------------------------------------------------------
    internal static string[] Split(this string self, string regexDelimiter, bool trimTrailingEmptyStrings)
    {
        string[] splitArray = System.Text.RegularExpressions.Regex.Split(self, regexDelimiter);

        if (trimTrailingEmptyStrings)
        {
            if (splitArray.Length > 1)
            {
                for (int i = splitArray.Length; i > 0; i--)
                {
                    if (splitArray[i - 1].Length > 0)
                    {
                        if (i < splitArray.Length)
                            System.Array.Resize(ref splitArray, i);

                        break;
                    }
                }
            }
        }

        return splitArray;
    }

    //-----------------------------------------------------------------------------
    //	These methods are used to replace calls to some Java String constructors.
    //-----------------------------------------------------------------------------
    internal static string NewString(sbyte[] bytes)
    {
        return NewString(bytes, 0, bytes.Length);
    }
    internal static string NewString(sbyte[] bytes, int index, int count)
    {
        return System.Text.Encoding.UTF8.GetString((byte[])(object)bytes, index, count);
    }
    internal static string NewString(sbyte[] bytes, string encoding)
    {
        return NewString(bytes, 0, bytes.Length, encoding);
    }
    internal static string NewString(sbyte[] bytes, int index, int count, string encoding)
    {
        return System.Text.Encoding.GetEncoding(encoding).GetString((byte[])(object)bytes, index, count);
    }

    //--------------------------------------------------------------------------------
    //	These methods are used to replace calls to the Java String.getBytes methods.
    //--------------------------------------------------------------------------------
    internal static sbyte[] GetBytes(this string self)
    {
        return GetSBytesForEncoding(System.Text.Encoding.UTF8, self);
    }
    internal static sbyte[] GetBytes(this string self, string encoding)
    {
        return GetSBytesForEncoding(System.Text.Encoding.GetEncoding(encoding), self);
    }
    private static sbyte[] GetSBytesForEncoding(System.Text.Encoding encoding, string s)
    {
        sbyte[] sbytes = new sbyte[encoding.GetByteCount(s)];
        encoding.GetBytes(s, 0, s.Length, (byte[])(object)sbytes, 0);
        return sbytes;
    }
}

//----------------------------------------------------------------------------------------
//	Copyright © 2007 - 2013 Tangible Software Solutions Inc.
//	This class can be used by anyone provided that the copyright notice remains intact.
//
//	This class provides the logic to simulate Java rectangular arrays, which are jagged
//	arrays with inner arrays of the same length. A size of -1 indicates unknown length.
//----------------------------------------------------------------------------------------
internal static partial class RectangularArrays
{
    internal static IloNumVar[][] ReturnRectangularIloNumVarArray(int Size1, int Size2)
    {
        IloNumVar[][] Array;
        if (Size1 > -1)
        {
            Array = new IloNumVar[Size1][];
            if (Size2 > -1)
            {
                for (int Array1 = 0; Array1 < Size1; Array1++)
                {
                    Array[Array1] = new IloNumVar[Size2];
                }
            }
        }
        else
            Array = null;

        return Array;
    }
}