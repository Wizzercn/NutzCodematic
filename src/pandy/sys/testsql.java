import java.sql.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2006-9-20
 * Time: 14:02:16
 * To change this template use File | Settings | File Templates.
 */
public class testsql
{
    public static void main(String args[])
    {
        Connection conn = null;
        ResultSet rs = null;
        Statement stmt = null;
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            conn = DriverManager.getConnection("jdbc:oracle:thin:@l92.168.0.100:1521:hfmh", "portal", "hitsportal");
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String sql = "SELECT * FROM portal_docissue";
            rs = stmt.executeQuery(sql);

            while (rs.next())
            {
                Thread.sleep(10000);
                System.out.println(new Date() + ":" + rs.getString("title"));
            }
            rs.close();
            stmt.close();
            conn.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
