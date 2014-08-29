package pandy.db;
import java.sql.Connection;
public class DBConnector
{
    public static DBConnectionManager connMgr=null;
    public static String user="";
    public  static Connection getconecttion()
    {
        Connection con=null;
        try
        {
            connMgr=DBConnectionManager.getInstance();
            con=connMgr.getConnection("sqlserverbase");
            user=DBConnectionManager.user;
            return con;
        }
        catch(Exception e)
        {
            return null;
        }
    }
    public  static Connection newgetconecttion(String dbname)
    {
        Connection con=null;
        try
        {
            if(connMgr!=null)
            {
                 connMgr.release();
            }
            connMgr=DBConnectionManager.getnewInstance(dbname);
            con=connMgr.getConnection("sqlserverbase");
            user=DBConnectionManager.user;
            return con;
        }
        catch(Exception e)
        {
            return null;
        }
    }
    public static void freecon(Connection con)
    {
        try
        {
            connMgr.freeConnection("sqlserverbase",con);
         }
        catch(Exception e)
        {
               System.out.println(e);
        }
    }
}
