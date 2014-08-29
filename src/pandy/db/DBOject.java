package pandy.db;

import pandy.com.Globa;

import java.sql.*;
import java.io.BufferedWriter;
import java.io.BufferedReader;

public class DBOject
{
    private ResultSet rs=null;
    private Statement stmt=null;
    private PreparedStatement psmt=null;
    private CallableStatement cst=null;

    public void setAutoCommit(Connection con,boolean auto) {
        try {
            con.setAutoCommit(auto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void rollback(Connection con)  {
        try {
            con.rollback();
        } catch (SQLException e) {
        }
    }

    public void commitCon(Connection con) throws SQLException{
        con.commit();
        con.setAutoCommit(true);
    }
    public boolean setClobBody(Connection con,String sql,String colnumname,String body) {
        try {
            Statement stmt = con.createStatement();
            ResultSet  rs = stmt.executeQuery(sql);
            if(rs != null){
                if(rs.next()){
                    oracle.sql.CLOB clob = (oracle.sql.CLOB)rs.getClob(colnumname);
                    BufferedWriter out = new BufferedWriter(clob.getCharacterOutputStream());
                    out.write(body);
                    out.close();
                }
            }
            rs.close();
            stmt.close();
            return true;
        } catch (Exception e) {
            if (Globa.sysInfo) Globa.logger.info(e.toString());
            if (Globa.sysDebug) e.printStackTrace();
            return false;
        }
    }
    /*
     *获取CLOB字段值
    **/
    public static String getClobBody(ResultSet rs,String colnumName){
        String result="";
        try {
            String str_Clob = "";
            StringBuffer strBuffer_CLob = new StringBuffer();
            strBuffer_CLob.append("");
            oracle.sql.CLOB clob = (oracle.sql.CLOB)rs.getClob(colnumName);
            BufferedReader in = new BufferedReader(clob.getCharacterStream());
            while((str_Clob=in.readLine())!=null){
                strBuffer_CLob.append(str_Clob+"\n");
            }
            in.close();
            result=strBuffer_CLob.toString();
        } catch (Exception e) {
            if (Globa.sysInfo) Globa.logger.info(e.toString());
            if (Globa.sysDebug) e.printStackTrace();
        }
        return result;
    }
    public static long getNextRowId(Connection con,String tableName,String colName){
        long result=1;
        DBOject dbobj=new DBOject();
        try {
            String sql="select max("+colName+") from "+tableName;
            ResultSet rs=dbobj.getresultset(con,sql);
            if(rs.next())
                result= rs.getLong(1)+1;
            return result;
        } catch (Exception e) {
            if (Globa.sysInfo) Globa.logger.info(e.toString());
            if (Globa.sysDebug) e.printStackTrace();
            return result;
        } finally {
            dbobj.freecon(con);
        }
    }

    public ResultSet getresultset(Connection con,String sql)
    {
        try
        {
            stmt=con.createStatement();
            rs=stmt.executeQuery(sql);
            return rs;
        }
        catch(Exception e)
        {
            System.out.println(e);
            return null;
        }
    }

    public ResultSet getrollresultset(Connection con,String sql)
    {
        try
        {
            stmt=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            rs=stmt.executeQuery(sql);
            return rs;
        }
        catch(Exception e)
        {
             System.out.println(e);
             return null;
        }
    }
    public int executeupdate(Connection con,String sql)
    {
        try
        {
            stmt=con.createStatement();
            int p=stmt.executeUpdate(sql);
            return   p;
        }
        catch(Exception e)
        {
             e.printStackTrace(); 
            return 0;
        }
    }
    public int deleteById(Connection con,String tablename,int idvalue)
    {
        try
        {
            stmt=con.createStatement();
            int p=stmt.executeUpdate("delete from "+tablename+" where id="+idvalue);
            return   p;
        }
        catch(Exception e)
        {
            return 0;
        }
    }
    public int deleteByName(Connection con,String tablename,String rowname,String name)
    {
        try
        {
            stmt=con.createStatement();
            int p=stmt.executeUpdate("delete from "+tablename+" where "+rowname+"='"+name+"'");
            return   p;
        }
        catch(Exception e)
        {
            return 0;
        }
    }
    public ResultSet listpage(ResultSet rs,int intCurPage,int intPageSize)
    {
         try {
             int min=(intCurPage-1)*intPageSize;
             if(intCurPage!=1&&min>0) rs.absolute(min) ;
             return rs;
         } catch (Exception e) {
             return null;
         }
    }
     /** 分页时指针指向*/
    public ResultSet fingerTo(ResultSet rs,int intCurPage,int intPageSize) {
         try {
             int min=(intCurPage-1)*intPageSize;
             if(intCurPage!=1&&min>0) rs.absolute(min) ;
             return rs;
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
     }

    /**分页时检查纪录集 */
    public ResultSet checkRs (ResultSet rs,int rsCount,int pageSize) throws SQLException
    {
        if(rs==null) {
            return null;
        }
        if(rs.isAfterLast()||rsCount>=pageSize ){
            return null;
        }
        return rs;
    }

    public int executeUpdate()  throws SQLException {
        try {
            int result = 0;
            if(cst!=null)
               result =  cst.executeUpdate();
            else
                result = psmt.executeUpdate();
            return result;
        } catch (SQLException e) {
            if(cst!=null)
                Globa.logger.info("ERROR Excuteupdate:[" + cst.toString() + "] " + e.toString());
            else if(psmt!=null)
                Globa.logger.info("ERROR Excuteupdate:[" + psmt.toString() + "] " + e.toString());
            throw e;
        }

    }
    public void prepareCallStatement(Connection con,String sql){
        try {
            cst= con.prepareCall(sql);
        } catch (SQLException e) {
            if (Globa.sysInfo) Globa.logger.info("ERROR prepareCallStatement:[" + sql + "] " + e.toString());
            if (Globa.sysDebug) e.printStackTrace();
        }
    }
    /**
     * 预编译语句
     * @param sql
     */

    public void prepareStatement(Connection con,String sql)
    {
        try {
            psmt=con.prepareStatement(sql);
        } catch (Exception e) {
            if (Globa.sysInfo) Globa.logger.info("ERROR prepareStatement:[" + sql + "] " + e.toString());
            if (Globa.sysDebug) e.printStackTrace();
        }
    }
    /**
     * 设定返回值
     * @param i
     * @param objectType
     * @throws SQLException
     */
    public void registerOutParameter(int i,int objectType) throws SQLException{
        cst.registerOutParameter(i,objectType);
    }

    /**
     * 预编译sql语句中的String参数
     * @param i  位置
     * @param s  参数值
     * @throws SQLException
     */
    public void setString(int i, String s)
            throws SQLException {
        if(cst!=null)
            cst.setString(i, s);
        else
            psmt.setString(i, s);
    }

    /**
     * 预编译sql语句中的int参数
     * @param i  位置
     * @param j  参数值
     * @throws SQLException
     */
    public void setInt(int i, int j)
            throws SQLException {
        if(cst!=null)
            cst.setInt(i, j);
        else
        psmt.setInt(i, j);
    }

    /**
     * 预编译sql语句中的boolean参数
     * @param i  位置
     * @param flag  参数值
     * @throws SQLException
     */
    public void setBoolean(int i, boolean flag)
            throws SQLException {
         if(cst!=null)
            cst.setBoolean(i, flag);
        else
        psmt.setBoolean(i, flag);
    }

    /**
     * 预编译sql语句中的Date参数
     * @param i  位置
     * @param  date :java.sql.Date 参数值
     * @throws SQLException
     */
    public void setDate(int i, Date date)
            throws SQLException {
        if(cst!=null)
            cst.setDate(i, date);
        else
        psmt.setDate(i, date);
    }

    /**
     * 预编译sql语句中的long参数
     * @param i  位置
     * @param l  long参数值
     * @throws SQLException
     */
    public void setLong(int i, long l)
            throws SQLException {
        if(cst!=null)
            cst.setLong(i, l);
        else
        psmt.setLong(i, l);
    }

    /**
     * 预编译sql语句中的float参数
     * @param i  位置
     * @param f  float参数值
     * @throws SQLException
     */
    public void setFloat(int i, float f)
            throws SQLException {
         if(cst!=null)
            cst.setFloat(i, f);
        else
        psmt.setFloat(i, f);
    }

    /**
     * 预编译sql语句中的byte[]参数
     * @param i  位置
     * @param abyte0  参数值
     * @throws SQLException
     */
    public void setBytes(int i, byte abyte0[])
            throws SQLException {
        if(cst!=null)
            cst.setBytes(i, abyte0);
        else
        psmt.setBytes(i, abyte0);
    }

    /**
     * 关闭预编译的Statament
     */
    public void clearParameters()
    {
        try {
            if (psmt != null) {
                psmt.clearParameters();
                psmt.close();
            }if(cst!=null){
                cst.clearParameters();
                cst.close();
            }
        } catch (Exception e) {
        }
    }
       
    public void freecon(Connection con)
    {
        try
        {
            if(stmt!=null)
            {
                stmt.close();
            }
            if(rs!=null)
            {
                rs.close();  
            }
          if(psmt!=null||cst!=null)
            clearParameters();
         }
        catch(Exception e)
        {
               System.out.println(e);
        }
    }
    public int[] exectmoresql(Connection con,String[] sql)
    {
        setAutoCommit(con,false);
        int returnvalue[]=new int[sql.length];
        try {
            stmt=con.createStatement();
            for(int i=0;i<sql.length;i++)
            {
                stmt.addBatch(sql[i]);
            }
            returnvalue = stmt.executeBatch();
            commitCon(con);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            rollback(con);
        } finally {
        }
        return returnvalue;
    }
}
