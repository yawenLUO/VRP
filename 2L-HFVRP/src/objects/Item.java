package objects;

import java.util.Comparator;

public class Item
{
	private int length, width;
	private boolean couldRotate = false;
	private int item_x, item_y;
	private long area;
	private int index;
	private int index_c;
	public boolean used; // variable used in PackingInGivenBin
	public double Ui;    // variable used in anotherPackingHeau

	public static final Comparator<Item> sort_by_Ui = new Comparator<Item>()
			{
				public int compare(Item r1, Item r2)
				{
					if (r1.Ui < r2.Ui)
						return -1;
					else if (r1.Ui == r2.Ui)
						return 0;
					else
						return 1;
				}
			};
	
	public Item(int length, int width, int index, int index_c)
	{
		this.length = length;
		this.width = width;
		area = length * width;
		this.index = index;
		this.index_c = index_c;
	}

	public int get_length()
	{
		return length;
	}
	
	public int get_index()
	{
		return index;
	}
	public int get_index_c()
	{
		return index_c;
	}
	public void setUi(double Ui)
	{
		this.Ui=Ui;
	}
	
	public int get_width()
	{
		return width;
	}

	public int get_item_x()
	{
		return item_x;
	}

	public int get_item_y()
	{
		return item_y;
	}

	public void set_item_x(int x)
	{
		item_x = x;
	}

	public void set_item_y(int y)
	{
		item_y = y;
	}

	public long get_area()
	{
		return area;
	}

	public boolean couldRotate()
	{
		return couldRotate;
	}

	public void print()
	{
		System.out.println("item " + index + " length: " + length
				+ " width: " + width);
	}

	public String toString()
	{
		return "item " + index_c + "_" + index + " width: " + width + " length: " + length + " x: "
				+ item_x + " y: " + item_y;
	}
}