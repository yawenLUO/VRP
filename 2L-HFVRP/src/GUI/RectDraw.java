package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class RectDraw extends JPanel
{
	private int length = 0, width = 0;

	public void setSize(int length, int width)
	{
		this.length = length;
		this.width = width;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Draw a rectangle using Rectangle2D class
		//
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.BLUE);
		//
		// Draw the blue rectangle
		//
		if (length != 0)
		{
			if (length == 10)
				g2.draw(new Rectangle2D.Double(50, 1, 180, 180));
			else if (length == 15)
				g2.draw(new Rectangle2D.Double(50, 1, 190, 190));
			else if (length == 25)
				g2.draw(new Rectangle2D.Double(50, 1, 198, 198));
			else if (length == 20)
			{
				if (length > width)
				{
					if (width == 10)
						g2.draw(new Rectangle2D.Double(50, 1, 195, 160));
					else
						g2.draw(new Rectangle2D.Double(50, 1, 195, 172));
				}
				else
					g2.draw(new Rectangle2D.Double(50, 1, 195, 190));
			}
			else if (length == 30)
			{
				if (length > width)
					g2.draw(new Rectangle2D.Double(20, 1, 225, 190));
				else
					g2.draw(new Rectangle2D.Double(20, 1, 225, 199));
			}
			else
			{
				g2.draw(new Rectangle2D.Double(1, 1, 250, 180));
			}
		}
	}
}
