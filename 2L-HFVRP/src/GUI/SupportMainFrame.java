package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JPanel;
import Fekete.lbFekete;
import approaches.Functions;
import approaches.supportMain;
import objects.Customer;
import objects.Item;
import objects.Route;
import objects.VehicleType;

public class SupportMainFrame
{
	public static int handle_customerSB(StringBuffer customerSB, ArrayList<Customer> customers)
	{
		int quantityOfItems = 0;
		customerSB.append("<html>");
		for (Customer customer : customers)
		{
			quantityOfItems += customer.get_items().size();
			customerSB.append("- customer " + customer.get_index() + " totalWeight: "
					+ customer.get_totalWeight() + ", coordinates: (" + customer.get_x() + " ,  "
					+ customer.get_y() + ") ");
			for (Item item : customer.get_items())
			{
				customerSB.append("item " + item.get_index() + " couldRotate: "
						+ item.couldRotate() + ", length: " + item.get_length() + " width: "
						+ item.get_width() + ", ");
			}
			customerSB.append("<br>");
		}
		customerSB.append("</html>");
		return quantityOfItems;
	}

	public static int[] findCoordinatesLimits(Customer depot, ArrayList<Customer> customers)
	{
		int[] result = new int[4];
		int minX = (int) depot.get_x();
		int maxX = minX;
		int minY = (int) depot.get_y();
		int maxY = minY;
		for (Iterator iterator = customers.iterator(); iterator.hasNext();)
		{
			Customer customer = (Customer) iterator.next();
			int x = (int) customer.get_x();
			int y = (int) customer.get_y();
			if (x < minX)
			{
				minX = x;
			}
			if (x > maxX)
			{
				maxX = x;
			}
			if (y < minY)
			{
				minY = y;
			}
			if (y > maxY)
			{
				maxY = y;
			}
		}
		result[0] = minX;
		result[1] = minY;
		result[2] = maxX;
		result[3] = maxY;
		return result;
	}

	public static int[] addPointforDepot(Customer depot, JPanel MapjPanel1,
			ArrayList<CustomerLabel> customerLabels, int[] result)
	{
		JLabel label = new JLabel();
		Dimension dim = MapjPanel1.getPreferredSize();

		int width = dim.width;
		int height = dim.height;
		int cusX = (int) depot.get_x(), cusY = (int) depot.get_y();
		int minX = result[0], minY = result[1], maxX = result[2], maxY = result[3];
		int X_range = maxX - minX, Y_range = maxY - minY;
		double xunit = 0, yunit = 0;
		if (X_range == 0)
			xunit = 0;
		if (X_range != 0)
			xunit = width * 0.95 / X_range;
		if (Y_range == 0)
			yunit = 0;
		if (Y_range != 0)
			yunit = height * 0.95 / Y_range;

		label.setSize(12, 12);
//		label.setOpaque(true);
//		label.setBackground(new Color(51,51,0));
		label.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2,
				new java.awt.Color(0,0,0)));
		int x = 0, y = 0;
		if (X_range == 0)
			x = width / 2;
		if (X_range != 0)
			x = (int) (width * 0.025 + (cusX - minX) * xunit);
		if (Y_range == 0)
			y = height / 2;
		if (Y_range != 0)
			y = (int) (height * 0.025 + (cusY - minY) * yunit);
		label.setLocation(x, y);
		label.setToolTipText("depot (" + cusX + "," + cusY + ")");
		MapjPanel1.add(label);
		customerLabels.add(new CustomerLabel(label, depot));
		int[] a = new int[2];
		a[0] = x;
		a[1] = y;
		return a;
	}

	public static int[] addPointforCus(Customer cus, JPanel MapjPanel1,
			ArrayList<CustomerLabel> customerLabels, int[] result)
	{
		JLabel label = new JLabel();
		Dimension dim = MapjPanel1.getPreferredSize();

		int width = dim.width;
		int height = dim.height;
		int cusX = (int) cus.get_x(), cusY = (int) cus.get_y();
		int minX = result[0], minY = result[1], maxX = result[2], maxY = result[3];
		int X_range = maxX - minX, Y_range = maxY - minY;
		double xunit = 0, yunit = 0;
		if (X_range == 0)
			xunit = 0;
		if (X_range != 0)
			xunit = width * 0.95 / X_range;
		if (Y_range == 0)
			yunit = 0;
		if (Y_range != 0)
			yunit = height * 0.95 / Y_range;

		label.setSize(6, 6);
		label.setOpaque(true);
		label.setBackground(new Color(0, 0, 255));
		int x = 0, y = 0;
		if (X_range == 0)
			x = width / 2;
		if (X_range != 0)
			x = (int) (width * 0.025 + (cusX - minX) * xunit);
		if (Y_range == 0)
			y = height / 2;
		if (Y_range != 0)
			y = (int) (height * 0.025 + (cusY - minY) * yunit);
		label.setLocation(x, y);
		label.setToolTipText("customer" + cus.get_index() + " (" + cusX + "," + cusY + ")");
		MapjPanel1.add(label);
		customerLabels.add(new CustomerLabel(label, cus));
		int[] a = new int[2];
		a[0] = x;
		a[1] = y;
		return a;
	}

	public static void calculate(ArrayList<Customer> customers, ArrayList<Route> routes,
			ArrayList<VehicleType> vehicleTypes, Customer depot)
	{
		lbFekete.scaleGeneration(new BigDecimal("0.1"), new BigDecimal("0.1"), customers,
				vehicleTypes);

		int routeIndex_begin = 0;
		boolean isFeasible = true;
		// need a loop here to repeat the process for remaining customers until
		// none left or declare infeasible.
		// layer-A loop
		while (customers.size() > 0)
		{
			int[] initialVehicleSet = Functions.getInitialNumberOfVehicles(customers, vehicleTypes);

			// allocate one customer to one truck according to
			// initialVehicleSet[]
			isFeasible = Functions.initiaRoutes(routes, initialVehicleSet, customers, vehicleTypes,
					depot);
			if (!isFeasible)
				break;
			boolean currentRoutesFlag = true;
			// layer-B loop
			while (customers.size() > 0 && currentRoutesFlag)
			{
				// layer-C loop
				for (int i = routeIndex_begin; i < routes.size(); i++)
				{
					Route route = routes.get(i);
					supportMain
							.routeTrySendInvitationProcess(route, customers, vehicleTypes, depot);

				}

				supportMain.customersTryChooseInvitationProcess(customers);

				currentRoutesFlag = false;
				for (int i = routeIndex_begin; i < routes.size(); i++)
				{
					if (routes.get(i).getFlag() == true)
					{
						currentRoutesFlag = true;
						break;
					}
				}

			}
			routeIndex_begin = routes.size();
		}
		if (!isFeasible)
			System.out
					.println("We think some items from one or more customers are too big to be packed in any type of the current vehicle sets");
		else
		{
			try
			{
				supportMain.resetAllRoutes(routes, depot);
			}
			catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Functions.printRoutes(routes, depot);
		}
	}
}
