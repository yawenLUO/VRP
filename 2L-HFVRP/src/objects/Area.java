package objects;

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

	public int get_length()
	{
		return length;
	}

	public int get_width()
	{
		return width;
	}

	public int get_area_x()
	{
		return x;
	}

	public int get_area_y()
	{
		return y;
	}

}