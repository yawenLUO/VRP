using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections;

namespace ConsoleApplication1.objects
{




    using PackingInGivenBin = approaches.PackingInGivenBin;

    public class Customer
    {
        private int index = -1;
        private double totalWeight;
        private double x, y;
        private List<Item> items;
        private bool isServed = false;
        private bool[] isSuitableForType_Renamed;
        private int uniqueTypeIndex = -1;
        private int suitableScore = -1;
        private List<Invitation> invitations = new List<Invitation>();

        public static readonly IComparer<Customer> sort_by_suitablescore = new ComparatorAnonymousInnerClassHelper();

        private class ComparatorAnonymousInnerClassHelper : IComparer<Customer>
        {
            public ComparatorAnonymousInnerClassHelper()
            {
            }

            public virtual int Compare(Customer r1, Customer r2)
            {
                if (r1.suitableScore < r2.suitableScore)
                {
                    return -1;
                }
                else if (r1.suitableScore == r2.suitableScore)
                {
                    return 0;
                }
                else
                {
                    return 1;
                }
            }
        }

        public List<List<double?>> scales = new List<List<double?>>();

        public Customer(int index, double totalWeight, double x, double y, List<Item> items, List<VehicleType> vehicleTypes)
        {
            this.index = index;
            this.totalWeight = totalWeight;
            this.x = x;
            this.y = y;
            this.items = items;

            isSuitableForType_Renamed = new bool[vehicleTypes.Count];
            int i = 0, numSuitableTypes = 0;
            foreach (VehicleType type in vehicleTypes)
            {
                bool flag = true;
                foreach (Item item in items)
                {
                    if ((item.get_length() > type.get_length()) || (item.get_width() > type.get_width()))
                    {
                        flag = false;
                        break;
                    }
                }

                if (flag)
                {
                    List<Item> test = new List<Item>();
                    List<Item> placed = new List<Item>();
                    test.AddRange(items);
                    if (!PackingInGivenBin.placeItems(type.get_length(), type.get_width(), placed, test))
                    {
                        flag = false;
                    }
                }
                isSuitableForType_Renamed[i] = flag && (totalWeight <= type.get_capacity());
                if (isSuitableForType_Renamed[i])
                {
                    numSuitableTypes++;
                    uniqueTypeIndex = i;
                }
                i++;
            }
            if (numSuitableTypes > 1)
            {
                uniqueTypeIndex = -1;
            }

            for (int j = 0; j < isSuitableForType_Renamed.Length; j++)
            {
                if (isSuitableForType(j))
                {
                    suitableScore = j;
                    break;
                }
            }
        }

        public virtual void receiveInvitation(Invitation invitation)
        {
            invitations.Add(invitation);
        }

        public virtual Invitation chooseInvitation()
        {
            invitations.Sort(Invitation.sortByCostIncrease);
            return invitations[0];
        }

        public virtual bool hasInvitations()
        {
            if (invitations.Count == 0)
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        public virtual int get_suitableScore()
        {
            return suitableScore;
        }

        public virtual bool isSuitableForType(int index)
        {
            return isSuitableForType_Renamed[index];
        }

        public virtual double distance(Customer anotherCustomer)
        {
            return Math.Sqrt(Math.Pow(this.x - anotherCustomer.x, 2) + Math.Pow(this.y - anotherCustomer.y, 2));
        }

        public virtual void print()
        {
            Console.WriteLine("- customer " + index + " totalWeight " + totalWeight + " coordinates: " + x + "   " + y + " \tis suitable for vehicle types: " + isSuitableForType_Renamed.ToString());
            for (IEnumerator iterator = items.GetEnumerator(); ;)
            {
                if (!iterator.MoveNext())
                    break;
                Item item = (Item)iterator.Current;
                item.print();
            }
            Console.WriteLine();
        }

        public virtual double get_totalWeight()
        {
            return totalWeight;
        }

        public virtual double get_x()
        {
            return x;
        }

        public virtual double get_y()
        {
            return y;
        }

        public virtual bool Served
        {
            get
            {
                return isServed;
            }
        }

        public virtual List<Item> get_items()
        {
            return items;
        }

        public virtual int get_uniqueTypeIndex()
        {
            return uniqueTypeIndex;
        }

        public virtual int get_index()
        {
            return index;
        }

        public static int getScalesArrayLength(List<Customer> customers)
        {
            if (customers.Count == 0)
            {
                return 0;
            }
            else
            {
                return customers[0].scales[0].Count;
            }
        }
    }
}