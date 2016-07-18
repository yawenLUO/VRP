using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApplication1.Fekete
{
   
    
        /*
         * From Sergey
         */
        using Customer = objects.Customer;
        using Item = objects.Item;
        using VehicleType = objects.VehicleType;

        public class lbFekete
        {

            public static void scaleGeneration(decimal start, decimal step, List<Customer> customers, List<VehicleType> vehicleTypes)
            {
                int ptr = -1;
                foreach (VehicleType type in vehicleTypes)
                {
                    ptr++;
                    foreach (Customer customer in customers)
                    {
                        customer.scales.Add(new List<double?>());
                        if (!customer.isSuitableForType(ptr))
                        {
                            continue;
                        }
                        decimal stop = new decimal("0.5");
                        double value;
                        for (decimal r = start; r.CompareTo(stop) <= 0; r = r + step)
                        {
                            value = 0;
                            foreach (Item item in customer.get_items())
                            {
                                value += (double)scale1(item.get_length(), item.get_width(), 1, r, type.get_length(), type.get_width());
                            }
                            customer.scales[ptr].Add(value);

                            value = 0;
                            foreach (Item item in customer.get_items())
                            {
                                value += (double)scale2(item.get_length(), item.get_width(), 1, r, type.get_length(), type.get_width());
                            }
                            customer.scales[ptr].Add(value);

                            value = 0;
                            foreach (Item item in customer.get_items())
                            {
                                value += (double)scale3(item.get_length(), item.get_width(), 1, r, type.get_length(), type.get_width());
                            }
                            customer.scales[ptr].Add(value);

                            value = 0;
                            foreach (Item item in customer.get_items())
                            {
                                value += (double)scale4(item.get_length(), item.get_width(), 1, r, type.get_length(), type.get_width());
                            }
                            customer.scales[ptr].Add(value);

                            value = 0;
                            foreach (Item item in customer.get_items())
                            {
                                value += (double)scale5(item.get_length(), item.get_width(), r, type.get_length(), type.get_width());
                            }
                            customer.scales[ptr].Add(value);

                            value = 0;
                            foreach (Item item in customer.get_items())
                            {
                                value += (double)scale6(item.get_length(), item.get_width(), r, type.get_length(), type.get_width());
                            }
                            customer.scales[ptr].Add(value);

                            for (decimal q = start; q.CompareTo(stop) <= 0; q = q + step)
                            {
                                value = 0;
                                foreach (Item item in customer.get_items())
                                {
                                    value += (double)scale7(item.get_length(), item.get_width(), r, q, type.get_length(), type.get_width());
                                }
                                customer.scales[ptr].Add(value);
                            }
                        }
                    }
                }
            }

            private static decimal scale1(int h, int w, int k, decimal r, int L, int W)
            {
                return uFunction((new decimal(h)).divide(new decimal(L), 8, decimal.ROUND_HALF_UP)) * UFunction((new decimal(w)).divide(new decimal(W), 8, decimal.ROUND_HALF_UP), r);
            }

            private static decimal scale2(int h, int w, int k, decimal r, int L, int W)
            {
                return uFunction((new decimal(w)).divide(new decimal(W), 8, decimal.ROUND_HALF_UP)) * UFunction((new decimal(h)).divide(new decimal(L), 8, decimal.ROUND_HALF_UP), r);
            }

            private static decimal scale3(int h, int w, int k, decimal r, int L, int W)
            {
                return uFunction((new decimal(h)).divide(new decimal(L), 8, decimal.ROUND_HALF_UP)) * fFunction((new decimal(w)).divide(new decimal(W), 8, decimal.ROUND_HALF_UP), r);
            }

            private static decimal scale4(int h, int w, int k, decimal r, int L, int W)
            {
                return uFunction((new decimal(w)).divide(new decimal(W), 8, decimal.ROUND_HALF_UP)) * fFunction((new decimal(h)).divide(new decimal(L), 8, decimal.ROUND_HALF_UP), r);
            }

            private static decimal scale5(int h, int w, decimal r, int L, int W)
            {
                return ((new decimal(h)).divide(new decimal(L), 8, decimal.ROUND_HALF_UP)).multiply(UFunction((new decimal(w)).divide(new decimal(W), 8, decimal.ROUND_HALF_UP), r));
            }

            private static decimal scale6(int h, int w, decimal r, int L, int W)
            {
                return ((new decimal(w)).divide(new decimal(W), 8, decimal.ROUND_HALF_UP)).multiply(UFunction((new decimal(h)).divide(new decimal(L), 8, decimal.ROUND_HALF_UP), r));
            }

            private static decimal scale7(int h, int w, decimal r, decimal q, int L, int W)
            {
                return fFunction((new decimal(h)).divide(new decimal(L), 8, decimal.ROUND_HALF_UP), r) * fFunction((new decimal(w)).divide(new decimal(W), 8, decimal.ROUND_HALF_UP), q);
            }

            private static decimal uFunction(decimal xVar)
            {
                if (frac(xVar * (new decimal(2))).CompareTo(new decimal(0)) == 0)
                {
                    return xVar;
                }
                else
                {
                    return xVar * (new decimal(2)).setScale(0, RoundingMode.FLOOR);
                }
            }

            private static decimal UFunction(decimal xVar, decimal param)
            {
                if (xVar.CompareTo((new decimal(1)) - param) > 0)
                {
                    return (new decimal(1));
                }
                if ((param.CompareTo(xVar) <= 0) && (xVar.CompareTo((new decimal(1)) - param) <= 0))
                {
                    return xVar;
                }
                if (xVar.CompareTo(param) < 0)
                {
                    return (new decimal(0));
                }
                return (new decimal(0));
            }

            private static decimal fFunction(decimal xVar, decimal param)
            {
                if (xVar.CompareTo(new decimal("0.5")) > 0)
                {
                    return (new decimal(1)) - (((new decimal(1)) - xVar.divide(param, 8, decimal.ROUND_HALF_UP)).setScale(0, RoundingMode.FLOOR).divide((new decimal(1)).divide(param, 8, decimal.ROUND_HALF_UP).setScale(0, RoundingMode.FLOOR), 8, decimal.ROUND_HALF_UP));
                }
                if ((param.CompareTo(xVar) <= 0) && (xVar.CompareTo(new decimal("0.5")) <= 0))
                {
                    return (new decimal(1)).divide((new decimal(1)).divide(param, 8, decimal.ROUND_HALF_UP).setScale(0, RoundingMode.FLOOR), 8, decimal.ROUND_HALF_UP);
                }
                if (xVar.CompareTo(param) < 0)
                {
                    return (new decimal(0));
                }
                return (new decimal(0));
            }

            private static decimal frac(decimal xVar)
            {
                return xVar - xVar.setScale(0, RoundingMode.FLOOR);
            }
        }
    }