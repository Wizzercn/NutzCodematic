import javax.swing.*;
/**
 * Created by IntelliJ IDEA.
 * User: Admin12
 * Date: 2006-1-12
 * Time: 9:04:26
 * To change this template use File | Settings | File Templates.
 */
public class index
{
	public static void main(String args[])
	{
		/**
		 * 定义二个线程，一个用来显示启动界面，一个用来加载主程序
		 */
        try
        {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception exc)
        {
        }
		Thread start = new Thread(new startup());
		Thread load = new Thread(new loadmain());
		start.start();

		load.start();
	}
}

/**
 * 程序启动界面显示
 */
class startup implements Runnable
{
	public void run()
	{
		new splash();
	}
}

/**
 * 主程序的加载
 */
class loadmain implements Runnable
{
	public void run()
	{
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		new pandy();
	}
}
