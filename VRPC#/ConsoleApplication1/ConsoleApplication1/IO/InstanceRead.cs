using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApplication1.IO
{
    using System.IO;
    using Customer = objects.Customer;
    using Item = objects.Item;
    using VehicleType = objects.VehicleType;

    public class InstanceRead
        {
            private string fileName_prefix;
            private bool GUIMode;
            private string file;

            public InstanceRead(string fileName_prefix)
            {
                this.fileName_prefix = fileName_prefix;
                GUIMode = false;
            }

            

            //JAVA TO C# CONVERTER WARNING: Method 'throws' clauses are not available in .NET:
            //ORIGINAL LINE: public java.util.ArrayList<objects.Customer> readCustomers(java.util.ArrayList<objects.VehicleType> vehicleTypes) throws java.io.FileNotFoundException
            public virtual List<Customer> readCustomers(List<VehicleType> vehicleTypes)
            {
                List<Customer> customers = new List<Customer>();
                // we put "inputs" folder under the root directory of the 2L-HFVRP
                // project.
                StreamReader customerScanner=null;
                if (GUIMode)
                {
                    //customerScanner = new StreamReader(new FileStream(file));
                }
                else
                {
                    string customerFileName = "inputs/" + fileName_prefix + "_input_node.txt";

                    customerScanner = new StreamReader(customerFileName);
                }
                customerScanner.ReadLine();
                int index = 0;
                while (!customerScanner.EndOfStream)
                {
                    string line = customerScanner.ReadLine();
                    string[] lineScanner = line.Split(null);
                    double x = Convert.ToDouble(lineScanner[0]);
                    double y = Convert.ToDouble(lineScanner[1]);
                    double totalWeight = Convert.ToDouble(lineScanner[2]);
                    int numOfItems = Convert.ToInt32(lineScanner[3]);
                    List<Item> items = new List<Item>();
                    if (numOfItems > 0)
                    {
                        for (int i = 0; i < numOfItems; i++)
                        {
                            items.Add(new Item(Convert.ToInt32(lineScanner[4+2*i]), Convert.ToInt32(lineScanner[5+2*i]), i, index));
                        }
                    }
                    Customer customer = new Customer(index, totalWeight, x, y, items, vehicleTypes);
                    customers.Add(customer);
                    index++;
                    //lineScanner.Close();
                }
                customerScanner.Close();
                return customers;
            }

            //JAVA TO C# CONVERTER WARNING: Method 'throws' clauses are not available in .NET:
            //ORIGINAL LINE: public java.util.ArrayList<objects.VehicleType> readVehicles() throws java.io.FileNotFoundException
            public virtual List<VehicleType> readVehicles()
            {
                List<VehicleType> vehicleTypes = new List<VehicleType>();
                string vehicleTypeFileName=null;
                if (GUIMode)
                {
                    //string[] temp = file.Path.Split("node");
                   // vehicleTypeFileName = temp[0] + "vehicle.txt";
                }
                else
                {
                    vehicleTypeFileName = "inputs/" + fileName_prefix + "_input_vehicle.txt";
                }
                StreamReader vehicleScanner = new StreamReader(vehicleTypeFileName);
                vehicleScanner.ReadLine();
                while (!vehicleScanner.EndOfStream)
                {
                    string line = vehicleScanner.ReadLine();
                    string[] lineScanner =line.Split(null);
                    //lineScanner.next(); // in our project the quantity of trucks of each
                                        // type is unlimited, we omit the quantity info
                                        // in the file
                    int capacity = Convert.ToInt32(lineScanner[1].Replace(".0", ""));
                    int width = Convert.ToInt32(lineScanner[2]);
                    int length = Convert.ToInt32(lineScanner[3]);
                    double fixedCost = Convert.ToDouble(lineScanner[4]);
                    double variableCost = Convert.ToDouble(lineScanner[5]);
                    VehicleType vehicleType = new VehicleType(capacity, length, width, fixedCost, variableCost);
                    vehicleTypes.Add(vehicleType);
                    //lineScanner.close();
                }
                vehicleScanner.Close();
                return vehicleTypes;
            }

        }
    }