package approaches;

import ilog.concert.IloIntervalVar;

import java.util.Comparator;

public class IntervalVarComparator_w implements Comparator<IloIntervalVar>
{
	public int compare(IloIntervalVar arg0, IloIntervalVar arg1) 
	{
		if (arg1.getSizeMax() < arg0.getSizeMax()) return -1;
		if (arg1.getSizeMax() > arg0.getSizeMax()) return 1;
		return 0;
	}
}

