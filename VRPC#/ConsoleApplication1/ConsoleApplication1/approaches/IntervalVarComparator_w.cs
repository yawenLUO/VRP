using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApplication1.approaches
{

    using IloIntervalVar = ilog.concert.IloIntervalVar;

    public class IntervalVarComparator_w : IComparer<IloIntervalVar>
    {
        public virtual int Compare(IloIntervalVar arg0, IloIntervalVar arg1)
        {
            if (arg1.SizeMax < arg0.SizeMax)
            {
                return -1;
            }
            if (arg1.SizeMax > arg0.SizeMax)
            {
                return 1;
            }
            return 0;
        }
    }

}