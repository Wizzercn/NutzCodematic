package pandy.util;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
/**
 * Created by IntelliJ IDEA.
 * User: Admin12
 * Date: 2005-11-25
 * Time: 15:25:36
 * To change this template use File | Settings | File Templates.
 */
public class SysUtil {
    public static int getIntSessionValue(HttpSession session,String flag){
        try {
            Object o=session.getAttribute(flag);
            return ((Integer)o).intValue();
        } catch (Exception e) {
            return 0;
        }
    }
    public static void setIntSessionValue(HttpSession session,String flag,int value){
        try {
           session.setAttribute(flag,new Integer(value));
        } catch (Exception e) {
        }
    }

    public static String toGbk(String str){
        try {
            if(str==null) str="";
            String encode=SysProp.getPropValue("db.out.encode");
            if(encode!=null&&encode.startsWith("iso8859"))
                str=new  String(str.getBytes("iso8859-1"),"gbk");
        } catch (UnsupportedEncodingException e) {
        }
        return str;
    }
    public static String toIso(String str) {
        try {
            if (str == null) str = "";
            String encode = SysProp.getPropValue("db.out.encode");
            if (encode != null && encode.startsWith("iso8859"))
                str = new String(str.getBytes("gbk"), "iso8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }
   public static String toGb2312(String str) {
        try {
            if (str == null) str = "";
                str = new String(str.getBytes("iso8859-1"), "gb2312");
        } catch (UnsupportedEncodingException e) {
        }
        return str;
    }
    public static int String2int(String str,int def){
        int intRet = def;
        try
        {
            intRet = Integer.parseInt(str);
        }
        catch(NumberFormatException e) {}
        return intRet;
    }

    public static String getLevelFlag(int level,String flag){
        String temp="";
        for(int i=0;i<level;i++){
            temp= temp+ flag;
        }
        return temp+"©À";
    }
    public static String getLevelFlag2(int level, String flag) {
        String temp = "";
        for (int i = 0; i < level; i++) {
            temp = temp + flag;
        }
        return temp;
    }
    public static String getLevelFlagImg(int level){
        String rootpath=SysProp.getPropValue("sys.application.name")+"/jsp/images/menu/";
        if(level==1)
            return toImage(rootpath+"mfc.gif")+toImage(rootpath+"node.png");
        else{
            StringBuffer result=new StringBuffer();
            for(int i=1;i<level;i++){
                result.append(toImage(rootpath+"line.png")) ;
            }
            result.append(toImage(rootpath+"mfc.gif"));
            result.append(toImage(rootpath+"node.png")) ;
            return result.toString();
        }
    }
    public static String getLevelFlagImgLine(int level){
        String rootpath=SysProp.getPropValue("sys.application.name")+"/jsp/images/menu/";
        if(level==1)
            return toImage(rootpath+"mfc.gif")+toImage(rootpath+"node.png");
        else{
            StringBuffer result=new StringBuffer();
            for(int i=1;i<level;i++){
                result.append(toImage(rootpath+"line.png")) ;
            }
            result.append(toImage(rootpath+"mfc.gif"));
            return result.toString();
        }
    }

    private static String toImage(String url){
        return "<img src='"+url+"' border=0>";
    }
    public static String trimStr(String str,int length) {
        if(str==null||str.equals("null")) return "&nbsp;";
        if(str.length()>length){
            return str=str.substring(0,length)+"...";
        }
        if (str.equals("")) str= "&nbsp;";
        return str;

    }
    public static String getNotNull(String str){
        if(str==null) return "";
        else if(str.equals("null")) return "";
        else return str;
    }

      public static String getStrsplit(String[] ids){
        if(ids==null||ids.length==0) return "()";
        String result="(";
        for(int i=0;i< ids.length;i++){
            if(i==ids.length-1) result=result+"'"+ids[i]+"'";
            else
                result=result+"'"+ids[i]+"',";
        }
        result=result+")";
        return result;
    }
    public static String getIdsplit(String[] ids){
        if(ids==null||ids.length==0) return "()";
        String result="(";
        for(int i=0;i< ids.length;i++){
            if(i==ids.length-1) result=result+ids[i];
            else
                result=result+ids[i]+",";
        }
        result=result+")";
        return result;
    }

	public static String getIdsplit(ArrayList ids){
        if(ids==null||ids.size()==0) return "()";
        String result="(";
        for(int i=0;i< ids.size();i++){
            if(i==ids.size()-1) result=result+(String)ids.get(i);
            else
                result=result+(String)ids.get(i)+",";
        }
        result=result+")";
        return result;
    }
    public static String executeCmd(String cmd)
    {
        StringBuffer stringbuffer = new StringBuffer();
        Process process=null;
        try
        {
            process = Runtime.getRuntime().exec(cmd);
		}catch(Exception e){
            e.printStackTrace();
        } finally{
        }
        return stringbuffer.toString();
    }

    /**
     * replace, replace a string with another string in a string
     * @return string,
     */
    public static String replace(String handleStr, String pointStr, String repStr)
    {
        String str = new String();
        int pos1,pos2;
        try
        {
            if(handleStr.length()>0)
            {
                pos1 = handleStr.indexOf(pointStr);
                pos2 = 0;
                while(pos1 != -1)
                {
                    str += handleStr.substring(pos2,pos1);
                    str += repStr;
                    pos2 = pos1+pointStr.length();
                    pos1 = handleStr.indexOf(pointStr,pos2);
                }
                str += handleStr.substring(pos2);
            }
        }catch(Exception error)
        {
            error.printStackTrace();
        }
        return str;
    }

}

