package GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import javax.swing.JPanel;

public class myMapPanel extends JPanel
{
	private int[][] coordinates;

	public void setCoordinates(int[][] coor)
	{
		this.coordinates = coor;

	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Draw a rectangle using Rectangle2D class
		//
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.BLUE);
		for (int i = 0; i < coordinates.length; i++)
		{
			double x1 = coordinates[i][0] + 3, y1 = coordinates[i][1] + 3;
			int m = (i + 1) % coordinates.length;
			double x2 = coordinates[m][0] + 3, y2 = coordinates[m][1] + 3;
			g2.draw(new Line2D.Double(x1, y1, x2, y2));
		}
	}
}
