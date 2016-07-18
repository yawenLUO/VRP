package GUI;

import java.awt.Font;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class RunningDialog extends JDialog
{
	
	private JButton exitButton = new JButton();
	private JLabel label=new JLabel("Running... ... , please wait!!");
	public RunningDialog(JFrame frame)
	{
		super(frame);
		setModal(true);
		setLayout(null);
		exitButton.setText("Exit !");
		exitButton.setSize(60,40);
		exitButton.setLocation(110,100);
		label.setLocation(20, 0);
		label.setSize(280, 50);
		label.setFont(new Font("Tahoma", 0, 18));
		exitButton.setFont(new Font("Tahoma", 0, 14));
		setLocation(500, 200);
		setSize(300, 200);
		setVisible(false);
		
		add(label);
		add(exitButton);
//		runningDialog.add(new JButton());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//		setModal(true);
		exitButton.addMouseListener(new java.awt.event.MouseAdapter()
		{
			public void mouseClicked(java.awt.event.MouseEvent evt)
			{

				exitButtonMouseClicked(evt);
			}
		});
		
	}
	private void exitButtonMouseClicked(MouseEvent evt)
	{
		System.exit(1);
	}
}
