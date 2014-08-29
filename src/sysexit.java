import java.awt.*;
import javax.swing.*;
/*
 * Created by JFormDesigner on Sun Jul 30 13:07:26 CST 2006
 */



/**
 * @author pandy pandy
 */
public class sysexit extends JDialog {
	public sysexit(Frame owner) {
		super(owner);
		initComponents();
	}

	public sysexit(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		pack();
		setLocationRelativeTo(getOwner());
	}

}
