package pandy.com;

import pandy.util.SysProp;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.sql.Connection;
/**
 * Created by IntelliJ IDEA.
 * User: Admin12
 * Date: 2005-11-25
 * Time: 15:30:57
 * To change this template use File | Settings | File Templates.
 */
public class Globa {
	static{

	}
    public static boolean sysDebug=SysProp.getBlnPropValue("sysDebug");
    public static boolean sysInfo=SysProp.getBlnPropValue("sysInfo");
    public static Logger logger=Logger.getLogger("wzsoa2.0");
    public static ArrayList onlineUser=new ArrayList();
    public static String appRootDir="";
    public static String appRootName="";

    public static String getPageTemplate(String flagName, PageValue pageValue){
        return SysProp.getTemplate(flagName, pageValue);
    }
    public static String getPageTemplate(String flagName, int pageStyle) {
        return SysProp.getTemplate(flagName,new PageValue(pageStyle));
    }

    public static String getErrorMsg(int errorType){
        String msg= SysProp.getPropValue("error."+ errorType) ;
        if(msg==null) msg="unkown ErrorType";
        return msg;
    }

    public static void main(String[] args) {
    }

}

