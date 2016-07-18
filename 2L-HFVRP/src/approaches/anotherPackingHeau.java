package approaches;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import javax.swing.text.Position;

import objects.Customer;
import objects.Item;
import objects.Route;
import objects.VehicleType;
import objects.Posi_tion;

/*class ItemComp implements Comparator<Item>
 {
 public int compare(Item i1, Item i2)
 {
 int a1 = i1.get_length() * i1.get_width();
 int a2 = i2.get_length() * i2.get_width();
 if(a1 > a2)
 return 1;
 else if(a1 == a2)
 return 0;
 else
 return -1;
 }
 }
 class PosComp implements Comparator<Position>
 {
 public int compare(Position p1, Position p2)
 {
 if(p1.x > p2.x)
 return 1;
 else if(p1.x == p2.x)
 return 0;
 else
 return -1;
 }
 }*/

public class anotherPackingHeau
{

	static double omega = 1, p = 1;
	static Random random = new Random();
	static int remainArea = 0;

	public static boolean pass_anotherPackingHeau(
			ArrayList<Customer> customers, VehicleType vehicleType)
	{
		for (int i = 0; i < 10; i++)
		{
			if (!pack(customers, vehicleType))
			// change values
			{
				omega = random.nextDouble();
				p = random.nextDouble() + 1.01;
			} else
				return true;
		}
		return pack(customers, vehicleType);

	}

	public static boolean pack(ArrayList<Customer> customers, VehicleType ve)
	{
		double u = 0;
		ArrayList<Item> unpacked = new ArrayList<Item>();
		ArrayList<Item> packed = new ArrayList<Item>();
		for (Customer c : customers)
		{
			for (Item it : c.get_items())
			{
				unpacked.add(it);
				u += it.get_area();
			}
		}
		u = u / ve.get_area();

		for (Item item : unpacked)
		{
			double vi = omega * item.get_area() + (1 - omega)
					* Math.pow(item.get_area(), p) / u;
			item.setUi(vi / item.get_area());
		}

		// 1. sorting
		Collections.sort(unpacked, Item.sort_by_Ui);
		// 2. loop
		int size = unpacked.size();
		double maxRemainArea = 0;
		for (int i = 0; i < size; ++i)
		{
			int k = -1;
			Item it = unpacked.get(i);
			for (int j = 0; j < ve.pos.size(); ++j)
			{
				// 3. inside loop you do GetLB
				if (couldPutHere(it, ve.pos.get(j), ve))
				{
					int res =  getLB(it, ve.pos.get(j), ve);

					if (maxRemainArea < res)
					{
						maxRemainArea = res;
						k = j;
					}
				}
			}
			if (k != -1)
			{
				Posi_tion positon = ve.pos.get(k);
				unpacked.remove(i);
				packed.add(it);
				it.set_item_x(positon.getX());
				it.set_item_y(positon.getY());
				//reset ve.pos;
				
				
				
				int x1 = ve.pos.get(k).getX() + it.get_width();
				int y1 = ve.pos.get(k).getY() + it.get_length();
				int itArea = it.get_length() * it.get_width();

				// Situation 1
				if (ve.pos.size() == 1)
				{
					remainArea = ve.get_area() - itArea;
					ve.pos.remove(0);
					ve.pos.add(new Posi_tion(0, it.get_length()));
					ve.pos.add(new Posi_tion(it.get_width(), 0));
					
				}
				
				//Need to find all possible positions to put new item and put all positions in the arraylist pos.
				
				
				

				// Situation 2
				/*
				if (ve.pos.get(k).getX() == 0)
				{
					if (x1 < ve.pos.get(1).getX())
					{
						ve.remainArea = ve.remainArea - itArea;
						ve.pos.add(new Position(0, ve.pos.get(0).y
								+ it.get_width()));
						ve.pos.add(new Position(it.get_length(),
								veve.pos.sort(new PosComp());.pos.get(0).y));
						ve.pos.remove(0);
						ve.pos.sort(new PosComp());
					} else
					{
						ve.remainArea = ve.remainArea - itArea
								- (x1 - ve.pos.get(1).x)
								* (ve.pos.get(0).y - ve.pos.get(1).y);
						ve.pos.add(new Position(0, ve.pos.get(0).y
								+ it.get_width()));
						ve.pos.add(new Position(it.get_length(),
								ve.pos.get(1).y));
						ve.pos.remove(0);
						ve.pos.sort(new PosComp());
					}
				}
				// Situation 3
				if (ve.pos.get(k).y == 0)
				{
					if (y1 < ve.pos.get(ve.pos.size() - 1).y)
					{
						ve.remainArea = ve.remainArea - itArea;
						ve.pos.add(new Position(ve.pos.get(k).x,
								ve.pos.get(k).y + it.get_width()));
						ve.pos.add(new Position(x1, 0));
						ve.pos.remove(k);
						ve.pos.sort(new PosComp());
					} else
					{
						ve.remainArea = ve.remainArea
								- itArea
								- (ve.pos.get(k).x - ve.pos
										.get(ve.pos.size() - 2).x)
								* (y1 - ve.pos.get(ve.pos.size() - 2).y);
						ve.pos.add(new Position(ve.pos.get(k - 1).x, y1));
						ve.pos.add(new Position(x1, 0));
						ve.pos.remove(k);
						ve.pos.sort(new PosComp());
					}
				}
				// Situation 4
				int ip = ve.pos.indexOf(ve.pos.get(k));
				if (x1 < ve.pos.get(ip + 1).x && y1 < ve.pos.get(ip - 1).y)
				{
					ve.remainArea = ve.remainArea - itArea;
					ve.pos.add(new Position(ve.pos.get(k).x, y1));
					ve.pos.add(new Position(x1, ve.pos.get(k).y));
					ve.pos.remove(k);
					ve.pos.sort(new PosComp());
				} else if (y1 >= ve.pos.get(ip - 1).y)
				{
					ve.remainArea = ve.remainArea - itArea
							- (ve.pos.get(k).x - ve.pos.get(ip - 1).x)
							* (y1 - ve.pos.get(ip - 1).y);
					ve.pos.add(new Position(ve.pos.get(k - 1).x, y1));
					ve.pos.add(new Position(x1, ve.pos.get(k).y));
					ve.pos.remove(k);
					ve.pos.sort(new PosComp());
				} else if (x1 > ve.pos.get(ip + 1).x)
				{
					ve.remainArea = ve.remainArea - itArea
							- (x1 - ve.pos.get(ip + 1).x)
							* (y1 - ve.pos.get(ip + 1).y);
					ve.pos.add(new Position(ve.pos.get(k).x, y1));
					ve.pos.add(new Position(x1, ve.pos.get(k + 1).y));
					ve.pos.remove(k);
					ve.pos.sort(new PosComp());
				} else
					;*/
			}
		}
		if (unpacked.size() != 0)
			return false;
		else
			return true;
	}

	public static int getLB(Item it, Posi_tion p, VehicleType ve)
	{
		// Can't be placed in vehicle
		if ((p.getY() + it.get_length() > ve.get_length())
				|| (p.getX() + it.get_width() > ve.get_width()))
			return 0;
		/*
		 * int index = ve.unpacked.indexOf(it); ve.unpacked.remove(index);
		 * ve.packed.add(it);
		 */

		int y1 = it.get_length() + p.getY();
		int x1 = it.get_width() + p.getX();
		int itArea = it.get_length() * it.get_width();

		// Situation 1
		if (ve.pos.size() == 1)
		{
			return remainArea - itArea;
		}

		// Situation 2
		if (p.getX() == 0)
		{
			if (x1 < ve.pos.get(1).getX())
			{
				return remainArea - itArea;
			} else
			{
				return remainArea - itArea - (x1 - ve.pos.get(1).getX())
						* (ve.pos.get(0).getY() - ve.pos.get(1).getY());
			}
		}
		// Situation 3
		if (p.getY()== 0)
		{
			if (y1 < ve.pos.get(ve.pos.size() - 1).getY())
				return remainArea - itArea;
			else
				return remainArea - itArea
						- (p.getX() - ve.pos.get(ve.pos.size() - 2).getX())
						* (y1 - ve.pos.get(ve.pos.size() - 2).getY());
		}
		// Situation 4
		int ip = ve.pos.indexOf(p);
		if (x1 < ve.pos.get(ip + 1).getX() && y1 < ve.pos.get(ip - 1).getY())
			return remainArea - itArea;
		else if (y1 >= ve.pos.get(ip - 1).getY())
			return remainArea - itArea - (p.getX() - ve.pos.get(ip - 1).getX())
					* (y1 - ve.pos.get(ip - 1).getY());
		else if (x1 > ve.pos.get(ip + 1).getX())
			return remainArea - itArea - (x1 - ve.pos.get(ip + 1).getX())
					* (p.getY() - ve.pos.get(ip + 1).getY());
		else
			return 0;
	}

	public static boolean couldPutHere(Item it, Posi_tion position,
			VehicleType ve)
	{
		if (position.getX() + it.get_width() > ve.get_width())
		{
			return false;
		}
		if (position.getY() + it.get_length() > ve.get_length())
		{
			return false;
		}
		return true;
	}
}
