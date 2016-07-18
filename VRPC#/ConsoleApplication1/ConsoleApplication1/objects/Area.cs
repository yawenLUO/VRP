using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApplication1.objects
{

        public class Area
        {
            private int length;
            private int width;
            private int x;
            private int y;

            public Area(int l, int w, int y, int x)
            {
                this.length = l;
                this.width = w;
                this.x = x;
                this.y = y;
            }

            public virtual int get_length()
            {
                return length;
            }

            public virtual int get_width()
            {
                return width;
            }

            public virtual int get_area_x()
            {
                return x;
            }

            public virtual int get_area_y()
            {
                return y;
            }

        }
    }