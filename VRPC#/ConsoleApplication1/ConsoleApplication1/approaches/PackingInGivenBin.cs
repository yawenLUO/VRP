using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ConsoleApplication1.approaches
{
   
    
        /*
         * From Sergey
         */
        using IloCumulFunctionExpr = ilog.concert.IloCumulFunctionExpr;
        using IloException = ilog.concert.IloException;
        using IloIntVar = ilog.concert.IloIntVar;
        using IloIntervalVar = ilog.concert.IloIntervalVar;
        using IloLinearNumExpr = ilog.concert.IloLinearNumExpr;
        using IloNumVar = ilog.concert.IloNumVar;
        using IloCP = ilog.cp.IloCP;
        using IloSearchPhase = ilog.cp.IloSearchPhase;
        using IloCplex = ilog.cplex.IloCplex;


        using Area = objects.Area;
        using Item = objects.Item;

        public class PackingInGivenBin
        {
            public const int nAdditionalIter = 50;
            private static int treeLevel;
            private static int itLevel;

            private static bool checkForAreaUseless(int height, int width, List<Item> items)
            {
                foreach (Item item in items)
                {
                    if ((item.get_width() <= width) && (item.get_length() <= height))
                    {
                        return true;
                    }
                }
                return false;
            }

            public static bool checkForPackingFeasibility(int height, int width, List<Item> items)
            {
                for (int i = 0; i < items.Count; i++)
                {
                    int iw = items[i].get_width() + items[i].get_item_x();
                    int ih = items[i].get_length() + items[i].get_item_y();
                    for (int j = i + 1; j < items.Count; j++)
                    {
                        int jw = items[j].get_width() + items[j].get_item_x();
                        int jh = items[j].get_length() + items[j].get_item_y();
                        if ((!((iw <= items[j].get_item_x()) || (jw <= items[i].get_item_x()))) && (!((ih <= items[j].get_item_y()) || (jh <= items[i].get_item_y()))))
                        {
                            Console.WriteLine("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! conflict");
                            //					Problem.message=Problem.message+"overlap ";
                            return false;
                        }
                    }
                }
                return true;
            }

            private static bool checkForTwoAreasIntersection(Area i, Area j)
            {
                int iw = i.get_width() + i.get_area_x(), ih = i.get_length() + i.get_area_y();
                int jw = j.get_width() + j.get_area_x(), jh = j.get_length() + j.get_area_y();
                if ((!((iw <= j.get_area_x()) || (jw <= i.get_area_x()))) && (!((ih <= j.get_area_y()) || (jh <= i.get_area_y()))))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }

            private static bool checkCheckForAreaExistence(Area i, List<Area> areas)
            {
                foreach (Area area in areas)
                {
                    if ((i.get_width() == area.get_width()) && (i.get_length() == area.get_length()) && (i.get_area_x() == area.get_area_x()) && (i.get_area_y() == area.get_area_y()))
                    {
                        return false;
                    }
                }
                return true;
            }

            private static List<Area> findAreas(int H, int W, List<Item> placed, List<Item> items)
            {
                List<Area> FreeAreas = new List<Area>();
                if (placed.Count == 0)
                {
                    FreeAreas.Add(new Area(H, W, 0, 0));
                }
                else
                {
                    for (int p = 0; p < placed.Count; p++)
                    {
                        int minX = W + 1;
                        int ptrt = -1;
                        int width = 0; //free space width
                        int pw = placed[p].get_width() + placed[p].get_item_x();
                        if (W - pw == 0)
                        {
                            continue;
                        }
                        int ph = placed[p].get_length() + placed[p].get_item_y();

                        for (int t = 0; t < placed.Count; t++)
                        {
                            if (p == t)
                            {
                                continue;
                            }
                            int th = placed[t].get_length() + placed[t].get_item_y();
                            if ((placed[t].get_item_x() >= pw) && (placed[t].get_item_x() - pw < minX) && !((placed[t].get_item_y() >= ph) || (th <= placed[p].get_item_y())))
                            {
                                ptrt = t;
                                minX = placed[t].get_item_x() - pw;
                            }
                        }
                        if (minX == 0)
                        {
                            continue;
                        }
                        if (minX > W)
                        {
                            width = W - pw;
                        }
                        if (minX < W)
                        {
                            width = minX;
                        }

                        int minY = H + 1;
                        int ptrs = -1;
                        int height = 0; //free space height

                        for (int s = 0; s < placed.Count; s++)
                        {
                            if (p == s)
                            {
                                continue;
                            }
                            int sw = placed[s].get_width() + placed[s].get_item_x();
                            if ((placed[s].get_item_y() >= placed[p].get_item_y()) && (placed[s].get_item_y() - placed[p].get_item_y() < minY) && !((sw <= pw) || (pw + width <= placed[s].get_item_x())))
                            {
                                ptrs = s;
                                minY = placed[s].get_item_y() - placed[p].get_item_y();
                            }
                        }

                        if (minY != 0)
                        {
                            if (minY > H)
                            {
                                height = H - placed[p].get_item_y();
                            }
                            if (minY < H)
                            {
                                height = minY;
                            }
                            Area newAreaa = new Area(height, width, placed[p].get_item_y(), pw);
                            if (checkForAreaUseless(height, width, items) && checkCheckForAreaExistence(newAreaa, FreeAreas))
                            {
                                FreeAreas.Add(newAreaa);
                            }
                        }

                        if (placed[p].get_item_y() == 0)
                        {
                            continue;
                        }

                        int minZ = H + 1;
                        int ptrd = -1;
                        int depth = 0; //free space height

                        for (int d = 0; d < placed.Count; d++)
                        {
                            if (p == d)
                            {
                                continue;
                            }
                            int dw = placed[d].get_width() + placed[d].get_item_x();
                            int dh = placed[d].get_length() + placed[d].get_item_y();
                            if ((dh <= placed[p].get_item_y()) && (placed[p].get_item_y() - dh < minZ) && !((dw <= pw) || (pw + width <= placed[d].get_item_x())))
                            {
                                ptrs = d;
                                minZ = placed[p].get_item_y() - dh;
                            }
                        }

                        if (minZ == 0)
                        {
                            continue;
                        }
                        if (minZ > H)
                        {
                            depth = height + placed[p].get_item_y();
                        }
                        if (minZ < H)
                        {
                            depth = height + minZ;
                        }
                        Area newArea = new Area(depth, width, placed[p].get_item_y() + height - depth, pw);
                        if (checkForAreaUseless(depth, width, items) && checkCheckForAreaExistence(newArea, FreeAreas))
                        {
                            FreeAreas.Add(newArea);
                        }
                    }

                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

                    //é•³é’¼å±¦èµ… î‘"å±�å±™è�¾ï¿½ ç–‹å±¦èºž é"— ç–£ç�ˆå¦�å±™çœ�æ³�
                    for (int p = 0; p < placed.Count; p++)
                    {
                        int minY = H + 1;
                        int ptrt = -1;
                        int height = 0; //free space width
                        int ph = placed[p].get_length() + placed[p].get_item_y();
                        if (H - ph == 0)
                        {
                            continue;
                        }
                        int pw = placed[p].get_width() + placed[p].get_item_x();

                        for (int t = 0; t < placed.Count; t++)
                        {
                            if (p == t)
                            {
                                continue;
                            }
                            int tw = placed[t].get_width() + placed[t].get_item_x();
                            if ((placed[t].get_item_y() >= ph) && (placed[t].get_item_y() - ph < minY) && !((placed[t].get_item_x() >= pw) || (tw <= placed[p].get_item_x())))
                            {
                                ptrt = t;
                                minY = placed[t].get_item_y() - ph;
                            }
                        }
                        if (minY == 0)
                        {
                            continue;
                        }
                        if (minY > H)
                        {
                            height = H - ph;
                        }
                        if (minY < H)
                        {
                            height = minY;
                        }

                        int minX = W + 1;
                        int ptrs = -1;
                        int width = 0; //free space height

                        for (int s = 0; s < placed.Count; s++)
                        {
                            if (p == s)
                            {
                                continue;
                            }
                            int sh = placed[s].get_length() + placed[s].get_item_y();
                            if ((placed[s].get_item_x() >= placed[p].get_item_x()) && (placed[s].get_item_x() - placed[p].get_item_x() < minX) && !((sh <= ph) || (ph + height <= placed[s].get_item_y())))
                            {
                                ptrs = s;
                                minX = placed[s].get_item_x() - placed[p].get_item_x();
                            }
                        }

                        if (minX != 0)
                        {
                            if (minX > W)
                            {
                                width = W - placed[p].get_item_x();
                            }
                            if (minX < W)
                            {
                                width = minX;
                            }
                            Area newArea1 = new Area(height, width, ph, placed[p].get_item_x());
                            if (checkForAreaUseless(height, width, items) && checkCheckForAreaExistence(newArea1, FreeAreas))
                            {
                                FreeAreas.Add(newArea1);
                            }
                        }

                        if (placed[p].get_item_x() == 0)
                        {
                            continue;
                        }

                        int minZ = W + 1;
                        int ptrd = -1;
                        int depth = 0; //free space height

                        for (int d = 0; d < placed.Count; d++)
                        {
                            if (p == d)
                            {
                                continue;
                            }
                            int dw = placed[d].get_width() + placed[d].get_item_x();
                            int dh = placed[d].get_length() + placed[d].get_item_y();
                            if ((dw <= placed[p].get_item_x()) && (placed[p].get_item_x() - dw < minZ) && !((dh <= ph) || (ph + height <= placed[d].get_item_y())))
                            {
                                ptrs = d;
                                minZ = placed[p].get_item_x() - dw;
                            }
                        }

                        if (minZ == 0)
                        {
                            continue;
                        }
                        if (minZ > W)
                        {
                            depth = width + placed[p].get_item_x();
                        }
                        if (minZ < W)
                        {
                            depth = width + minZ;
                        }
                        Area newArea = new Area(height, depth, ph, placed[p].get_item_x() + width - depth);
                        if (checkForAreaUseless(height, depth, items) && checkCheckForAreaExistence(newArea, FreeAreas))
                        {
                            FreeAreas.Add(newArea);
                        }
                    }
                }
                return FreeAreas;
            }

            public static bool placeItems(int H, int W, List<Item> placed, List<Item> items)
            {
                //		items.remove(2);//

                //		items.clear();
                //		items.add(new Item(15,15));
                //		for (int i=0; i<4; i++ ) items.add(items.get(0));

                int n = items.Count;

                IloIntervalVar[] aw = new IloIntervalVar[n];
                IloIntervalVar[] ah = new IloIntervalVar[n];

                IloCP cp = null;
                try
                {
                    cp = new IloCP();
                    IloCumulFunctionExpr awExpr = cp.cumulFunctionExpr();
                    IloCumulFunctionExpr ahExpr = cp.cumulFunctionExpr();

                    for (int i = 0; i < n; ++i)
                    {
                        aw[i] = cp.intervalVar(items[i].get_width(), "aw" + i);
                        aw[i].StartMin = 0;
                        aw[i].StartMax = W - items[i].get_width();

                        ah[i] = cp.intervalVar(items[i].get_length(), "ah" + i);
                        ah[i].StartMin = 0;
                        ah[i].StartMax = H - items[i].get_length();

                        awExpr = cp.sum(awExpr, cp.pulse(aw[i], items[i].get_length()));
                        ahExpr = cp.sum(ahExpr, cp.pulse(ah[i], items[i].get_width()));
                    }

                    cp.add(cp.le(awExpr, H));
                    cp.add(cp.le(ahExpr, W));

                    cp.setParameter(IloCP.IntParam.LogPeriod, 1000000);
                    cp.setParameter(IloCP.IntParam.Workers, 1);
                    cp.setParameter(IloCP.DoubleParam.RelativeOptimalityTolerance, 0);
                    cp.setParameter(IloCP.DoubleParam.OptimalityTolerance, 0);
                    cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.Restart);
                    cp.setParameter(IloCP.DoubleParam.TimeLimit, 3);

                    Array.Sort(aw, new IntervalVarComparator_w());
                    Array.Sort(ah, new IntervalVarComparator_w());
                    List<IloSearchPhase> phases = new List<IloSearchPhase>();
                    phases.Add(cp.searchPhase(aw));
                    phases.Add(cp.searchPhase(ah));

                    if (cp.solve(phases.ToArray()))
                    {
                        for (int i = 0; i < n; ++i)
                        {
                            //	        		items.get(i).rotated=false;
                            //	        		items.get(i).px=cp.getStart(aw[i]);
                            //	        		items.get(i).py=cp.getStart(ah[i]);
                            items[i].used = true;
                            Console.WriteLine(cp.getDomain(aw[i]) + "  \t" + cp.getDomain(ah[i]));
                        }
                        cp.end();
                        return true;
                    }
                    else
                    {
                        cp.end();
                        return false;
                    }
                }
                catch (IloException e)
                {
                    Console.WriteLine(e.ToString());
                    Console.Write(e.StackTrace);
                    cp.end();
                }
                return false;
            }

            //	public static boolean placeItems(int H, int W, ArrayList<Item> placed, ArrayList<Item> items)
            //	{
            //		int it=1;
            //		ArrayList<Area> areas = findAreas(H, W, placed, items);
            //		if ((areas.size()==0)&&(items.size()>0)) return false;
            //		treeLevel++;
            //
            //		int n = items.size();
            //		int m = areas.size();
            //		IloNumVar[][] assignmentOR = new IloNumVar[n][m];
            //		IloNumVar[] wArea = new IloNumVar[m];
            //		IloNumVar[] hArea = new IloNumVar[m];
            //		IloCplex cplex = null;
            //		ArrayList<Item> itemsCopy = new ArrayList<Item>();
            //		for (Item item : items) itemsCopy.add(item);
            //		ArrayList<Item> placedCopy = new ArrayList<Item>();
            //		for (Item item : placed) placedCopy.add(item);
            //
            //		try 
            //		{
            //			cplex = new IloCplex();	
            //			IloLinearNumExpr objective = cplex.linearNumExpr(); 
            //			for(int i=0; i<n; i++) 
            //			{
            //				IloLinearNumExpr itemUsedOneTime = cplex.linearNumExpr(); 
            //				for(int j=0; j<m; j++)
            //				{
            //					if ((items.get(i).get_width()<=areas.get(j).get_width()) && (items.get(i).get_length()<=areas.get(j).get_length())) 
            //					{
            //						assignmentOR[i][j] = cplex.boolVar();
            //						itemUsedOneTime.addTerm(1, assignmentOR[i][j]);
            //						objective.addTerm((double)items.get(i).get_area()/(areas.get(j).get_width()*areas.get(j).get_length()),assignmentOR[i][j]);
            //					}
            //				}
            //				cplex.addLe(itemUsedOneTime, 1);
            //			}
            //
            //			for(int j=0; j<m; j++)
            //			{
            //				IloLinearNumExpr oneAreaOccupied = cplex.linearNumExpr(); 				
            //				for(int i=0; i<n; i++) if (assignmentOR[i][j]!=null) oneAreaOccupied.addTerm(1, assignmentOR[i][j]);	
            //				cplex.addLe(oneAreaOccupied, 1);
            //
            //				for(int k=j+1; k<m; k++) 
            //				{
            ////					if (j==k) continue;
            //					if (!checkForTwoAreasIntersection(areas.get(j), areas.get(k))) continue;
            //
            //					if (areas.get(j).get_area_y()>areas.get(k).get_area_y())
            //					{
            //						if (areas.get(j).get_area_x()<areas.get(k).get_area_x())
            //						{
            //							if (wArea[j]==null) wArea[j] = cplex.numVar(0, areas.get(j).get_width());
            //							if (hArea[k]==null) hArea[k] = cplex.numVar(0, areas.get(k).get_length());
            //
            //							IloLinearNumExpr atleft = cplex.linearNumExpr();
            //							IloNumVar lIndicator = cplex.boolVar();
            //							atleft.addTerm(1, wArea[j]);
            //							atleft.addTerm(W, lIndicator);
            //							cplex.addLe(atleft, W+areas.get(k).get_area_x()-areas.get(j).get_area_x());
            //
            //							IloLinearNumExpr atbottom = cplex.linearNumExpr();
            //							IloNumVar bIndicator = cplex.boolVar();
            //							atbottom.addTerm(1, hArea[k]);
            //							atbottom.addTerm(H, bIndicator);
            //							cplex.addLe(atbottom, H+areas.get(j).get_area_y()-areas.get(k).get_area_y());
            //
            //							IloLinearNumExpr noOverlap = cplex.linearNumExpr();
            //							noOverlap.addTerm(1, lIndicator);
            //							noOverlap.addTerm(1, bIndicator);
            //							cplex.addGe(noOverlap, 1);
            //						}
            //						if (areas.get(j).get_area_x()==areas.get(k).get_area_x())
            //						{
            //							if (hArea[k]==null) hArea[k] = cplex.numVar(0, areas.get(k).get_length());
            //
            //							IloLinearNumExpr atbottom = cplex.linearNumExpr();
            //							IloNumVar bIndicator = cplex.boolVar();
            //							atbottom.addTerm(1, hArea[k]);
            //							atbottom.addTerm(H, bIndicator);
            //							cplex.addLe(atbottom, H+areas.get(j).get_area_y()-areas.get(k).get_area_y());
            //
            //							IloLinearNumExpr noOverlap = cplex.linearNumExpr();
            //							noOverlap.addTerm(1, bIndicator);
            //							cplex.addGe(noOverlap, 1);
            //						}
            //						if (areas.get(j).get_area_x()>areas.get(k).get_area_x())
            //						{
            //							if (wArea[k]==null) wArea[k] = cplex.numVar(0, areas.get(k).get_width());
            //							if (hArea[k]==null) hArea[k] = cplex.numVar(0, areas.get(k).get_length());
            //
            //							IloLinearNumExpr atleft = cplex.linearNumExpr();
            //							IloNumVar lIndicator = cplex.boolVar();
            //							atleft.addTerm(1, wArea[k]);
            //							atleft.addTerm(W, lIndicator);
            //							cplex.addLe(atleft, W+areas.get(j).get_area_x()-areas.get(k).get_area_x());
            //
            //							IloLinearNumExpr atbottom = cplex.linearNumExpr();
            //							IloNumVar bIndicator = cplex.boolVar();
            //							atbottom.addTerm(1, hArea[k]);
            //							atbottom.addTerm(H, bIndicator);
            //							cplex.addLe(atbottom, H+areas.get(j).get_area_y()-areas.get(k).get_area_y());
            //
            //							IloLinearNumExpr noOverlap = cplex.linearNumExpr();
            //							noOverlap.addTerm(1, lIndicator);
            //							noOverlap.addTerm(1, bIndicator);
            //							cplex.addGe(noOverlap, 1);
            //						}
            //					}
            //					if (areas.get(j).get_area_y()==areas.get(k).get_area_y())
            //					{
            //						if (areas.get(k).get_area_x()<areas.get(j).get_area_x())
            //						{
            //							if (wArea[k]==null) wArea[k] = cplex.numVar(0, areas.get(k).get_width());
            //
            //							IloLinearNumExpr atleft = cplex.linearNumExpr();
            //							IloNumVar lIndicator = cplex.boolVar();
            //							atleft.addTerm(1, wArea[k]);
            //							atleft.addTerm(W, lIndicator);
            //							cplex.addLe(atleft, W+areas.get(j).get_area_x()-areas.get(k).get_area_x());
            //
            //							IloLinearNumExpr noOverlap = cplex.linearNumExpr();
            //							noOverlap.addTerm(1, lIndicator);
            //							cplex.addGe(noOverlap, 1);
            //						}
            //						if (areas.get(k).get_area_x()>areas.get(j).get_area_x())
            //						{
            //							if (wArea[j]==null) wArea[j] = cplex.numVar(0, areas.get(j).get_width());
            //
            //							IloLinearNumExpr atleft = cplex.linearNumExpr();
            //							IloNumVar lIndicator = cplex.boolVar();
            //							atleft.addTerm(1, wArea[j]);
            //							atleft.addTerm(W, lIndicator);
            //							cplex.addLe(atleft, W+areas.get(k).get_area_x()-areas.get(j).get_area_x());
            //
            //							IloLinearNumExpr noOverlap = cplex.linearNumExpr();
            //							noOverlap.addTerm(1, lIndicator);
            //							cplex.addGe(noOverlap, 1);
            //						}
            //					}
            //					if (areas.get(k).get_area_y()>areas.get(j).get_area_y())
            //					{
            //						if (areas.get(k).get_area_x()<areas.get(j).get_area_x())
            //						{
            //							if (wArea[k]==null) wArea[k] = cplex.numVar(0, areas.get(k).get_width());
            //							if (hArea[j]==null) hArea[j] = cplex.numVar(0, areas.get(j).get_length());
            //
            //							IloLinearNumExpr atleft = cplex.linearNumExpr();
            //							IloNumVar lIndicator = cplex.boolVar();
            //							atleft.addTerm(1, wArea[k]);
            //							atleft.addTerm(W, lIndicator);
            //							cplex.addLe(atleft, W+areas.get(j).get_area_x()-areas.get(k).get_area_x());
            //
            //							IloLinearNumExpr atbottom = cplex.linearNumExpr();
            //							IloNumVar bIndicator = cplex.boolVar();
            //							atbottom.addTerm(1, hArea[j]);
            //							atbottom.addTerm(H, bIndicator);
            //							cplex.addLe(atbottom, H+areas.get(k).get_area_y()-areas.get(j).get_area_y());
            //
            //							IloLinearNumExpr noOverlap = cplex.linearNumExpr();
            //							noOverlap.addTerm(1, lIndicator);
            //							noOverlap.addTerm(1, bIndicator);
            //							cplex.addGe(noOverlap, 1);
            //						}
            //						if (areas.get(k).get_area_x()==areas.get(j).get_area_x())
            //						{
            //							if (hArea[j]==null) hArea[j] = cplex.numVar(0, areas.get(j).get_length());
            //
            //							IloLinearNumExpr atbottom = cplex.linearNumExpr();
            //							IloNumVar bIndicator = cplex.boolVar();
            //							atbottom.addTerm(1, hArea[j]);
            //							atbottom.addTerm(H, bIndicator);
            //							cplex.addLe(atbottom, H+areas.get(k).get_area_y()-areas.get(j).get_area_y());
            //
            //							IloLinearNumExpr noOverlap = cplex.linearNumExpr();
            //							noOverlap.addTerm(1, bIndicator);
            //							cplex.addGe(noOverlap, 1);
            //						}
            //						if (areas.get(k).get_area_x()>areas.get(j).get_area_x())
            //						{
            //							if (wArea[j]==null) wArea[j] = cplex.numVar(0, areas.get(j).get_width());
            //							if (hArea[j]==null) hArea[j] = cplex.numVar(0, areas.get(j).get_length());
            //
            //							IloLinearNumExpr atleft = cplex.linearNumExpr();
            //							IloNumVar lIndicator = cplex.boolVar();
            //							atleft.addTerm(1, wArea[j]);
            //							atleft.addTerm(W, lIndicator);
            //							cplex.addLe(atleft, W+areas.get(k).get_area_x()-areas.get(j).get_area_x());
            //
            //							IloLinearNumExpr atbottom = cplex.linearNumExpr();
            //							IloNumVar bIndicator = cplex.boolVar();
            //							atbottom.addTerm(1, hArea[j]);
            //							atbottom.addTerm(H, bIndicator);
            //							cplex.addLe(atbottom, H+areas.get(k).get_area_y()-areas.get(j).get_area_y());
            //
            //							IloLinearNumExpr noOverlap = cplex.linearNumExpr();
            //							noOverlap.addTerm(1, lIndicator);
            //							noOverlap.addTerm(1, bIndicator);
            //							cplex.addGe(noOverlap, 1);
            //						}
            //					}
            //				}
            //			}
            //
            //			for(int j=0; j<m; j++)
            //			{
            //				if (wArea[j]!=null)
            //				{
            //					IloLinearNumExpr usedWidth = cplex.linearNumExpr(); 
            //					for(int i=0; i<n; i++) if (assignmentOR[i][j]!=null) usedWidth.addTerm(items.get(i).get_width(), assignmentOR[i][j]);
            //					cplex.addLe(usedWidth, wArea[j]);
            //				}
            //
            //				if (hArea[j]!=null)
            //				{
            //					IloLinearNumExpr usedHeight = cplex.linearNumExpr(); 
            //					for(int i=0; i<n; i++) if (assignmentOR[i][j]!=null) usedHeight.addTerm(items.get(i).get_length(), assignmentOR[i][j]);
            //					cplex.addLe(usedHeight, hArea[j]);
            //				}
            //			}
            //
            //			//			cplex.setParam(IloCplex.IntParam.Threads, 48);
            //			//			cplex.setParam(IloCplex.IntParam.ParallelMode, -1);
            //			cplex.setParam(IloCplex.IntParam.MIPDisplay, 0);
            //			//			cplex.setParam(IloCplex.IntParam.TimeLimit, timeLimit-(System.currentTimeMillis()-StartingTime)/1000);
            //			//			System.out.println("---> number:                 "+s2);
            //			cplex.addMaximize(objective);
            //
            //			boolean result = false;
            //			while (true)
            //			{
            //				itLevel=it;
            //				int nPlaced=0;
            //				IloLinearNumExpr cut = cplex.linearNumExpr(); 
            //				if (cplex.solve())
            //				{
            //					// --------------------------------------output
            ////					System.out.println("\n Solution ------------------ level "+ treeLevel+"\tattempt "+itLevel);
            ////					System.out.println("---> MIPObjectiveValue:                 "+cplex.getObjValue());
            //					//					System.out.println("---> getMIPRelativeGap:                 "+cplex.getMIPRelativeGap());
            //					//					System.out.println("---> getCplexStatus:                    "+cplex.getCplexStatus());
            //
            //					for(int i=0; i<items.size(); i++)
            //					{
            //						String s = "item "+items.get(i).toString();
            //						items.get(i).used=false;
            //						for(int j=0; j<m; j++)
            //						{
            //							if ((assignmentOR[i][j]!=null) && (Math.round(cplex.getValue(assignmentOR[i][j]))==1))
            //							{
            //								s=s+" oriented"+"\tx "+areas.get(j).get_area_x()+"\ty "+areas.get(j).get_area_y()+"\tw "+items.get(i).get_width()+"\tl "+items.get(i).get_length();
            //								items.get(i).set_item_x(areas.get(j).get_area_x());
            //								items.get(i).set_item_y(areas.get(j).get_area_y());
            //								items.get(i).used=true;
            //								cut.addTerm(1, assignmentOR[i][j]);
            //								nPlaced++;
            //								break;
            //							}
            //						}
            //						if  (items.get(i).used)
            //						{
            ////							System.out.println(s);
            //							placed.add(items.get(i));
            //						}
            //
            //					}
            //					for(int i=0; i<items.size(); i++)
            //					{
            //						if (items.get(i).used)
            //						{
            //							items.remove(i);
            //							i--;
            //						}
            //					}
            //				}
            //				else 
            //				{
            //					cplex.end();
            //					return false;
            //				}
            //				if (nPlaced==0)
            //				{
            //					result=false;
            //					break;
            //				}
            //				if (items.size()!=0) result=placeItems(H, W, placed,items);
            //				else 
            //				{
            //					result=true;
            //					break;
            //				}
            //				if ((!result) && ((itemsCopy.size()>1)))
            //				{
            //					if ((treeLevel<=2) && (it<=1))
            //					{
            //						it++;
            //						cplex.addLe(cut, nPlaced-1);
            //						items.clear();
            //						placed.clear();
            //						for (Item item : itemsCopy) items.add(item);
            //						for (Item item : placedCopy) placed.add(item);
            //					}
            //					else break;
            //				}
            //				else break;
            //			}
            //			cplex.end();
            //			treeLevel--;
            //			return result;
            //		}
            //		catch (IloException e) 
            //		{
            //			cplex.end();
            //			e.printStackTrace();
            //		}
            //		return false;
            //	}
        }
    }