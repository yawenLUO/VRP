using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApplication1.objects
{
    

        public class Invitation
        {
            private Route route;
            private double delta;
            private double increaseOfCost;

            public Invitation(Route route, double delta)
            {
                this.route = route;
                this.delta = delta;
                this.increaseOfCost = delta * route.get_VehicleType().get_variableCost();

            }

            public virtual Route Route
            {
                get
                {
                    return route;
                }
            }

            public virtual double get_delta()
            {
                return delta;
            }

            public virtual double get_increaseOfCost()
            {
                return increaseOfCost;
            }

            public static readonly IComparer<Invitation> sortByCostIncrease = new ComparatorAnonymousInnerClassHelper();

            private class ComparatorAnonymousInnerClassHelper : IComparer<Invitation>
            {
                public ComparatorAnonymousInnerClassHelper()
                {
                }

                public virtual int Compare(Invitation r1, Invitation r2)
                {
                    if (r1.increaseOfCost < r2.increaseOfCost)
                    {
                        return -1;
                    }
                    else if (r1.increaseOfCost == r2.increaseOfCost)
                    {
                        return 0;
                    }
                    else
                    {
                        return 1;
                    }
                }
            }
        }
    }