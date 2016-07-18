using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApplication1.objects
{
    


        public class Route
        {
            // totalDistance and totalCost calculated by Linker.exe
            private double totalDistance = 0;
            private double totalCost = 0;
            private double totalWeight = 0;
            private VehicleType vehicleType = null;
            // indicate whether this route could add more remaining customer
            private bool routeFlag = true;
            // the customerSequence doesn't include the depot(origin and terminus)
            private List<Customer> customerSequence = new List<Customer>();

            public virtual void addCustomer(Customer customer)
            {
                customerSequence.Add(customer);
                totalWeight += customer.get_totalWeight();

            }

            public virtual void set_totalDistance(double totalDistance)
            {
                this.totalDistance = totalDistance;
            }

            public virtual void set_totalCost(double totalCost)
            {
                this.totalCost = totalCost;
            }

            //public virtual VehicleType VehicleType
            //{
            //    set
            //    {
            //        this.vehicleType = value;
            //    }
            //}

            public virtual double get_totalCost()
            {
                return totalCost;
            }

            public virtual bool Flag
            {
                get
                {
                    return routeFlag;
                }
                set
                {
                    routeFlag = value;
                }
            }


            public virtual double get_totalWeight()
            {
                return totalWeight;
            }

            public virtual double get_totalDistance()
            {
                return totalDistance;
            }

            public virtual VehicleType get_VehicleType()
            {
                return vehicleType;
            }

            public virtual List<Customer> get_customerSequence()
            {
                return customerSequence;
            }

            public virtual List<Customer> CustomerSequence
            {
                set
                {
                    customerSequence = value;
                }
            }
        }
    }