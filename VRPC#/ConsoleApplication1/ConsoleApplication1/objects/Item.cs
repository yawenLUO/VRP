using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApplication1.objects
{
   

        public class Item
        {
            private int length, width;
            private bool couldRotate_Renamed = false;
            private int item_x, item_y;
            private long area;
            private int index;
            private int index_c;
            public bool used; // variable used in PackingInGivenBin
            public double Ui_Renamed; // variable used in anotherPackingHeau

            public static readonly IComparer<Item> sort_by_Ui = new ComparatorAnonymousInnerClassHelper();

            private class ComparatorAnonymousInnerClassHelper : IComparer<Item>
            {
                public ComparatorAnonymousInnerClassHelper()
                {
                }

                public virtual int Compare(Item r1, Item r2)
                {
                    if (r1.Ui_Renamed < r2.Ui_Renamed)
                    {
                        return -1;
                    }
                    else if (r1.Ui_Renamed == r2.Ui_Renamed)
                    {
                        return 0;
                    }
                    else
                    {
                        return 1;
                    }
                }
            }

            public Item(int length, int width, int index, int index_c)
            {
                this.length = length;
                this.width = width;
                area = length * width;
                this.index = index;
                this.index_c = index_c;
            }

            public virtual int get_length()
            {
                return length;
            }

            public virtual int get_index()
            {
                return index;
            }
            public virtual int get_index_c()
            {
                return index_c;
            }
            public virtual double Ui
            {
                set
                {
                    this.Ui_Renamed = value;
                }
            }

            public virtual int get_width()
            {
                return width;
            }

            public virtual int get_item_x()
            {
                return item_x;
            }

            public virtual int get_item_y()
            {
                return item_y;
            }

            public virtual void set_item_x(int x)
            {
                item_x = x;
            }

            public virtual void set_item_y(int y)
            {
                item_y = y;
            }

            public virtual long get_area()
            {
                return area;
            }

            public virtual bool couldRotate()
            {
                return couldRotate_Renamed;
            }

            public virtual void print()
            {
                Console.WriteLine("item " + index + " length: " + length + " width: " + width);
            }

            public override string ToString()
            {
                return "item " + index_c + "_" + index + " width: " + width + " length: " + length + " x: " + item_x + " y: " + item_y;
            }
        }
    }