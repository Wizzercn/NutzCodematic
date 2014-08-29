package pandy.sys;

import pandy.db.DBOject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2006-2-26
 * Time: 10:53:57
 * To change this template use File | Settings | File Templates.
 */
public class SysTableCtl
{
    public static Vector getListPage(Connection con,String tablename,int curpage,int pagesize,int  colcount)
    {
        Vector pandy=new Vector();
        DBOject obj=new DBOject();
        String sql="SELECT * FROM "+tablename;

        ResultSet rs=obj.getrollresultset(con,sql);
        try
        {
            rs.last();
            int rowcount=rs.getRow();
            rs.beforeFirst();
            rs=obj.listpage(rs,curpage,pagesize);
            Object noteinfo[][] = new Object[pagesize][colcount];
            int counter=0;
            while(rs.next())
            {
                 if(counter>=pagesize)
                {
                    break;
                }
                for (int i = 0; i < colcount; i++)
                {
                    noteinfo[counter][i] = rs.getObject(i + 1);
                }
                counter++;
            }
            Object noteinfo2[][]= new Object[counter][colcount];
            for(int i=0;i<counter;i++)
            for(int j=0;j<colcount;j++)
            {
                noteinfo2[i][j]=noteinfo[i][j];
            }
            pandy.add(String.valueOf(rowcount));
            pandy.add(noteinfo2);
            return pandy;
        } catch (SQLException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally
        {
            obj.freecon(con);
        }
        return pandy;
    }
}
