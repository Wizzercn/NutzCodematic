import java.awt.*;
import java.util.*;
import javax.swing.*;

/*
 * Created by JFormDesigner on Sun Jul 30 11:03:11 CST 2006
 */



/**
 * @author pandy pandy
 */
public class splash extends JFrame {
	public splash() {
		initComponents();
        splash.setVisible(true);
		try
		{
			Thread.currentThread().sleep(5000);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		splash.dispose();
    }

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("sys");
		splash = new JWindow();
		label1 = new JLabel();
		load = new JTextField();
		textField1 = new JTextField();

		//======== splash ========
		{
			splash.setEnabled(false);
			splash.setFocusable(false);
			Container splashContentPane = splash.getContentPane();
			splashContentPane.setLayout(new BorderLayout());

			//---- label1 ----
			label1.setIcon(new ImageIcon(getClass().getResource("/images/ico.jpg")));
			label1.setHorizontalAlignment(SwingConstants.CENTER);
			label1.setPreferredSize(new Dimension(540, 300));
			label1.setMinimumSize(new Dimension(540, 250));
			label1.setMaximumSize(new Dimension(540, 250));
			splashContentPane.add(label1, BorderLayout.CENTER);

			//---- load ----
			load.setEditable(false);
			load.setEnabled(false);
			load.setText("\u6b63\u5728\u52a0\u8f7d\u7a0b\u5e8f\u8bf7\u7a0d\u5019 \u2192 ");
			load.setBackground(Color.black);
			load.setForeground(Color.white);
			splashContentPane.add(load, BorderLayout.SOUTH);

			//---- textField1 ----
			textField1.setBackground(new Color(51, 51, 51));
			textField1.setText("Nutz Codematic                                                                                                                                    Wizzer.cn");
			textField1.setForeground(Color.white);
			textField1.setEnabled(false);
			splashContentPane.add(textField1, BorderLayout.NORTH);
			splash.setSize(540, 270);
			splash.setLocationRelativeTo(null);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - pandy pandy
	private JWindow splash;
	private JLabel label1;
	private JTextField load;
	private JTextField textField1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
