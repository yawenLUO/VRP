package GUI;

import javax.swing.JLabel;
import objects.Customer;

public class CustomerLabel
{
	private JLabel label;
	private Customer customer;
	public CustomerLabel(JLabel label,Customer customer)
	{
		this.label=label;
		this.customer=customer;
	}
	
	public JLabel getLabel()
	{
		return label;
	}
	
	public Customer getCustomer()
	{
		return customer;
	}
}
