using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApplication1.objects
{
   
        public class VehicleType
        {
            private int capacity, length, width;
            private double fixedCost, variableCost;
            private int area;
            //public List<Posi_tion> pos = new List<Posi_tion>();

            public VehicleType(int capacity, int length, int width, double fixedCost, double variableCost)
            {
                this.capacity = capacity;
                this.length = length;
                this.width = width;
                this.fixedCost = fixedCost;
                this.variableCost = variableCost;
                this.area = length * width;
              //  pos.Add(new Posi_tion());
            }

            public virtual void print()
            {
                Console.WriteLine("capacity: " + capacity + " length: " + length + " width: " + width + " fixedCost: " + fixedCost + " variableCost: " + variableCost);
            }

            public virtual int get_capacity()
            {
                return capacity;
            }

            public virtual int get_length()
            {
                return length;
            }

            public virtual int get_width()
            {
                return width;
            }

            public virtual double get_fixedCost()
            {
                return fixedCost;
            }

            public virtual int get_area()
            {
                return area;
            }

            public virtual double get_variableCost()
            {
                return variableCost;
            }

            public static int getMaxSizeType(List<VehicleType> vehicleTypes)
            {
                if (vehicleTypes.Count == 0)
                {
                    return -1;
                }
                else
                {
                    return vehicleTypes.Count - 1;
                }
            }




        }

    }