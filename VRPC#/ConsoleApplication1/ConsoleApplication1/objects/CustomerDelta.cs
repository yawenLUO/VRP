using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApplication1.objects
{
    

        public class CustomerDelta
        {
            private Customer customer;
            internal double delta;

            public CustomerDelta(Customer customer, double delta)
            {
                this.customer = customer;
                this.delta = delta;
            }

            public static readonly IComparer<CustomerDelta> sortByDelta = new ComparatorAnonymousInnerClassHelper();

            private class ComparatorAnonymousInnerClassHelper : IComparer<CustomerDelta>
            {
                public ComparatorAnonymousInnerClassHelper()
                {
                }

                public virtual int Compare(CustomerDelta r1, CustomerDelta r2)
                {
                    if (r1.delta < r2.delta)
                    {
                        return -1;
                    }
                    else if (r1.delta == r2.delta)
                    {
                        return 0;
                    }
                    else
                    {
                        return 1;
                    }
                }
            }

            public virtual Customer Customer
            {
                get
                {
                    return customer;
                }
            }

            public virtual double Delta
            {
                get
                {
                    return delta;
                }
            }
        }
    }