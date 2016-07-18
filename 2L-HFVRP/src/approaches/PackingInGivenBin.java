package approaches;
/*
 * From Sergey
 */
import ilog.concert.IloCumulFunctionExpr;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloIntervalVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cp.IloCP;
import ilog.cp.IloSearchPhase;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.Arrays;

import objects.Area;
import objects.Item;

public class PackingInGivenBin 
{
	final public static int nAdditionalIter=50;
	private static int treeLevel;
	private static int itLevel;

	private static boolean checkForAreaUseless(int height, int width, ArrayList<Item> items)
	{
		for(Item item : items)
		{
			if ((item.get_width()<=width) && (item.get_length()<=height))  return true;
		}
		return false;
	}

	public static boolean checkForPackingFeasibility(int height, int width, ArrayList<Item> items)
	{
		for(int i=0; i<items.size();i++)
		{
			int iw = items.get(i).get_width()+items.get(i).get_item_x(); 
			int ih = items.get(i).get_length()+items.get(i).get_item_y(); 
			for(int j=i+1; j<items.size();j++)
			{
				int jw = items.get(j).get_width()+items.get(j).get_item_x(); 
				int jh = items.get(j).get_length()+items.get(j).get_item_y(); 
				if ((!((iw<=items.get(j).get_item_x())||(jw<=items.get(i).get_item_x()))) && (!((ih<=items.get(j).get_item_y())||(jh<=items.get(i).get_item_y())))) 
				{
					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! conflict");
//					Problem.message=Problem.message+"overlap ";
					return false;
				}
			}
		}
		return true;
	}

	private static boolean checkForTwoAreasIntersection(Area i, Area j)
	{
		int iw = i.get_width()+i.get_area_x(), ih = i.get_length()+i.get_area_y();
		int jw = j.get_width()+j.get_area_x(), jh = j.get_length()+j.get_area_y();
		if ((!((iw<=j.get_area_x())||(jw<=i.get_area_x()))) && (!((ih<=j.get_area_y())||(jh<=i.get_area_y())))) 
			return true;
		else 
			return false;
	}

	private static boolean checkCheckForAreaExistence(Area i, ArrayList<Area> areas)
	{
		for(Area area: areas)
		{
			if ((i.get_width()==area.get_width())&&(i.get_length()==area.get_length())&&(i.get_area_x()==area.get_area_x())&&(i.get_area_y()==area.get_area_y())) return false;
		}
		return true;
	}

	private static ArrayList<Area> findAreas(int H, int W, ArrayList<Item> placed, ArrayList<Item> items)
	{
		ArrayList<Area> FreeAreas = new ArrayList<Area>();
		if (placed.size()==0)
		{
			FreeAreas.add(new Area(H,W,0,0));
		}
		else
		{
			for(int p=0; p<placed.size(); p++)
			{
				int minX = W+1; int ptrt = -1;
				int width = 0; //free space width
				int pw = placed.get(p).get_width()+placed.get(p).get_item_x(); 
				if (W-pw==0) continue;
				int ph = placed.get(p).get_length()+placed.get(p).get_item_y(); 

				for(int t=0; t<placed.size(); t++)
				{
					if (p==t) continue;
					int th = placed.get(t).get_length()+placed.get(t).get_item_y(); 
					if ((placed.get(t).get_item_x()>=pw) && (placed.get(t).get_item_x()-pw<minX) && !((placed.get(t).get_item_y()>=ph)||(th<=placed.get(p).get_item_y())))
					{
						ptrt=t;
						minX = placed.get(t).get_item_x()-pw;
					}
				}
				if (minX==0) continue;
				if (minX>W) width=W-pw;	
				if (minX<W) width=minX;

				int minY = H+1; int ptrs = -1;
				int height = 0; //free space height

				for(int s=0; s<placed.size(); s++)
				{	
					if (p==s) continue;
					int sw = placed.get(s).get_width()+placed.get(s).get_item_x(); 
					if ((placed.get(s).get_item_y()>=placed.get(p).get_item_y()) && (placed.get(s).get_item_y()-placed.get(p).get_item_y()<minY) && !((sw<=pw)||(pw+width<=placed.get(s).get_item_x())))
					{
						ptrs=s;
						minY = placed.get(s).get_item_y()-placed.get(p).get_item_y();
					}
				}

				if (minY!=0)
				{
					if (minY>H) height=H-placed.get(p).get_item_y();
					if (minY<H) height=minY;
					Area newArea = new Area(height,width,placed.get(p).get_item_y(),pw);
					if (checkForAreaUseless(height, width, items)&&checkCheckForAreaExistence(newArea, FreeAreas)) FreeAreas.add(newArea);
				}

				if (placed.get(p).get_item_y()==0) continue;

				int minZ = H+1; int ptrd = -1;
				int depth = 0; //free space height

				for(int d=0; d<placed.size(); d++)
				{	
					if (p==d) continue;
					int dw = placed.get(d).get_width()+placed.get(d).get_item_x(); 
					int dh = placed.get(d).get_length()+placed.get(d).get_item_y(); 
					if ((dh<=placed.get(p).get_item_y()) && (placed.get(p).get_item_y()-dh<minZ) && !((dw<=pw)||(pw+width<=placed.get(d).get_item_x())))
					{
						ptrs=d;
						minZ = placed.get(p).get_item_y()-dh;
					}
				}

				if (minZ==0) continue;
				if (minZ>H) depth=height+placed.get(p).get_item_y();
				if (minZ<H) depth=height+minZ;
				Area newArea = new Area(depth,width,placed.get(p).get_item_y()+height-depth,pw);
				if (checkForAreaUseless(depth, width, items)&&checkCheckForAreaExistence(newArea, FreeAreas)) FreeAreas.add(newArea);
			}

			//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

			//镳钼屦赅 屐屙蝾� 疋屦躞 铗 疣珈妁屙眍泐
			for(int p=0; p<placed.size(); p++)
			{
				int minY = H+1; int ptrt = -1;
				int height = 0; //free space width
				int ph = placed.get(p).get_length()+placed.get(p).get_item_y(); 
				if (H-ph==0) continue;
				int pw = placed.get(p).get_width()+placed.get(p).get_item_x(); 

				for(int t=0; t<placed.size(); t++)
				{
					if (p==t) continue;
					int tw = placed.get(t).get_width()+placed.get(t).get_item_x(); 
					if ((placed.get(t).get_item_y()>=ph) && (placed.get(t).get_item_y()-ph<minY) && !((placed.get(t).get_item_x()>=pw)||(tw<=placed.get(p).get_item_x())))
					{
						ptrt=t;
						minY = placed.get(t).get_item_y()-ph;
					}
				}
				if (minY==0) continue;
				if (minY>H) height=H-ph;	
				if (minY<H) height=minY;

				int minX = W+1; int ptrs = -1;
				int width = 0; //free space height

				for(int s=0; s<placed.size(); s++)
				{	
					if (p==s) continue;
					int sh = placed.get(s).get_length()+placed.get(s).get_item_y(); 
					if ((placed.get(s).get_item_x()>=placed.get(p).get_item_x()) && (placed.get(s).get_item_x()-placed.get(p).get_item_x()<minX) && !((sh<=ph)||(ph+height<=placed.get(s).get_item_y())))
					{
						ptrs=s;
						minX = placed.get(s).get_item_x()-placed.get(p).get_item_x();
					}
				}

				if (minX!=0)
				{
					if (minX>W) width=W-placed.get(p).get_item_x();
					if (minX<W) width=minX;
					Area newArea = new Area(height,width,ph,placed.get(p).get_item_x());
					if (checkForAreaUseless(height, width, items)&&checkCheckForAreaExistence(newArea, FreeAreas)) FreeAreas.add(newArea);
				}

				if (placed.get(p).get_item_x()==0) continue;

				int minZ = W+1; int ptrd = -1;
				int depth = 0; //free space height

				for(int d=0; d<placed.size(); d++)
				{	
					if (p==d) continue;
					int dw = placed.get(d).get_width()+placed.get(d).get_item_x(); 
					int dh = placed.get(d).get_length()+placed.get(d).get_item_y(); 
					if ((dw<=placed.get(p).get_item_x()) && (placed.get(p).get_item_x()-dw<minZ) && !((dh<=ph)||(ph+height<=placed.get(d).get_item_y())))
					{
						ptrs=d;
						minZ = placed.get(p).get_item_x()-dw;
					}
				}

				if (minZ==0) continue;
				if (minZ>W) depth=width+placed.get(p).get_item_x();
				if (minZ<W) depth=width+minZ;
				Area newArea = new Area(height,depth,ph,placed.get(p).get_item_x()+width-depth);
				if (checkForAreaUseless(height, depth, items)&&checkCheckForAreaExistence(newArea, FreeAreas)) FreeAreas.add(newArea);
			}
		}
		return FreeAreas;
	}
	
	public static boolean placeItems(int H, int W, ArrayList<Item> placed, ArrayList<Item> items)
	{
//		items.remove(2);//
		
//		items.clear();
//		items.add(new Item(15,15));
//		for (int i=0; i<4; i++ ) items.add(items.get(0));
		
		int n = items.size();
		
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
				aw[i] = cp.intervalVar(items.get(i).get_width(), "aw" + i);
				aw[i].setStartMin(0);
				aw[i].setStartMax(W-items.get(i).get_width());
				
				ah[i] = cp.intervalVar(items.get(i).get_length(), "ah" + i);
				ah[i].setStartMin(0);
				ah[i].setStartMax(H-items.get(i).get_length());
				
				awExpr = cp.sum(awExpr, cp.pulse(aw[i],items.get(i).get_length()));
				ahExpr = cp.sum(ahExpr, cp.pulse(ah[i],items.get(i).get_width()));
			}
			
			cp.add(cp.le(awExpr, H));
			cp.add(cp.le(ahExpr, W));
			
		    cp.setParameter(IloCP.IntParam.LogPeriod, 1000000);
		    cp.setParameter(IloCP.IntParam.Workers,1);
		    cp.setParameter(IloCP.DoubleParam.RelativeOptimalityTolerance, 0);
		    cp.setParameter(IloCP.DoubleParam.OptimalityTolerance, 0);  
		    cp.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.Restart);
		    cp.setParameter(IloCP.DoubleParam.TimeLimit, 3);
		    
		    Arrays.sort(aw, new IntervalVarComparator_w());
		    Arrays.sort(ah, new IntervalVarComparator_w());
	        ArrayList<IloSearchPhase> phases = new ArrayList<IloSearchPhase>();
		    phases.add(cp.searchPhase(aw));
		    phases.add(cp.searchPhase(ah));
		    
		    if (cp.solve(phases.toArray(new IloSearchPhase[phases.size()])))
	        {
	        	for (int i = 0; i < n; ++i)
	        	{
//	        		items.get(i).rotated=false;
//	        		items.get(i).px=cp.getStart(aw[i]);
//	        		items.get(i).py=cp.getStart(ah[i]);
	        		items.get(i).used=true;
	        		System.out.println(cp.getDomain(aw[i])+"  \t"+cp.getDomain(ah[i]));
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
			e.printStackTrace();
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