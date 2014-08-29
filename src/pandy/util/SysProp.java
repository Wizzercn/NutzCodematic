package pandy.util;

import pandy.file.FileUtil;
import pandy.com.PageValue;
import pandy.com.Globa;

import java.util.Properties;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
/**
 * Created by IntelliJ IDEA.
 * User: Admin12
 * Date: 2005-11-25
 * Time: 15:26:55
 * To change this template use File | Settings | File Templates.
 */
public class SysProp {
    static String filename="sysconfig.ini";
    static String templateFix="sys.page.template.path";
    static String flagFix="sys.page.template.flag";
    private static Properties sysPropties;
    private static Properties pageProptie=new Properties();
    static SysProp sysProp=null;

    private  static String getTemplateStr(int pageStyle){
        return FileUtil.getFileCnt(Globa.appRootDir+"//web-inf//classes//conf//"+SysProp.getPropValue(templateFix+ "_"+pageStyle));
    }
    private static String getFlagValue(String templateStr,String flag){
        String startFlag= "<!--flag:" + flag + "-->";
        String endFlag="<!--flag:/"+flag+"-->";
        int start=templateStr.indexOf(startFlag);
        int end=templateStr.indexOf(endFlag);
        if(start!=-1&&end!=-1)
        return templateStr.substring(start+startFlag.length(),end);
        else return "";
    }
    public static int getPropertyNameSize(String propNameFix){
        int size=0;
        Enumeration eum = sysPropties.propertyNames();
        while (eum.hasMoreElements()) {
            String name = (String)eum.nextElement();
            if(name.startsWith(propNameFix)){
                size++;
            }
        }
        return size;
    }
    /**
     * 初始化页面模板
     */
    private static void  loadTemplate(){
        int tempSize= getPropertyNameSize(templateFix);
        for(int i=0;i<tempSize;i++){
            int pageStyle= i +1;
            String strTemplate= getTemplateStr(pageStyle);
            int flagSize= getPropertyNameSize(flagFix);
            for(int j=0;j< flagSize;j++){
                int flagInt=j+1;
                String flag= getPropValue(flagFix+ "_"+ flagInt);
                String addKey = flag + "_" + String.valueOf(pageStyle);
                System.out.println("add template to page properties : " + addKey);
                pageProptie.put(addKey,getFlagValue(strTemplate,flag));
            }
        }
    }

   static boolean isLoadTemplate=false;
    /**
     * 取摸班中内容
     * @param flag
     * @param pageValue
     * @return
     */
   public static String getTemplate(String flag, PageValue pageValue){
       if(!isLoadTemplate){
           loadTemplate();
           isLoadTemplate=true;
       }
       String result = getPageValue(flag + "_" + pageValue.pageStyle);
       if(pageValue.tempvalues!=null) {
           Hashtable insert= pageValue.tempvalues;
           Enumeration keys=insert.keys();
           while(keys.hasMoreElements()){
               String key=(String) keys.nextElement();
               String value=(String)insert.get(key);
               result=result.replaceAll("<!--insert:"+key+"-->",value);
           }
       }
       if(result==null) result="";
       return result.trim();
   }
    public static Properties getProp() {
        if (sysPropties == null) {
            sysPropties = sysProp.getInstance().getProp();
        }
        return sysPropties;
    }

    public static String getPageValue(String propName) {
        return pageProptie.getProperty(propName);
    }
    public static String getPropValue(String propName){
        if(sysPropties==null)
            sysPropties=sysProp.getInstance().getProp(filename);
        return  sysPropties.getProperty(propName);
    }
    public static int getIntPropValue(String propName) {
        if (sysPropties == null)
            sysPropties = sysProp.getInstance().getProp(filename);
        int result=0;
        try {
            result=Integer.parseInt(sysPropties.getProperty(propName));
        } catch (NumberFormatException e) {
        }
        return result;
    }

    public static boolean getBlnPropValue(String propName){
        String temp=getPropValue(propName);
        if(temp!=null&&temp.equals("true"))
            return true;
        else return false;
    }
    public static String getPropValue(String propName,String fileName){
        Properties  prop=sysProp.getInstance().getProp(fileName);
        return  prop.getProperty(propName);
    }
    protected static SysProp getInstance()
	{
		if(sysProp==null)
			sysProp = new SysProp();
		return sysProp;
	}
    public static Properties getPropbyFileName(String filename){
        Properties props;
        props = new Properties();
        try {
            InputStream is = new FileInputStream(new File(filename));
            props.load(is);
            is.close();
            System.out.println("load "+filename+" parameters... " );
        }catch (Exception e) {
            System.err.println("Can't read the properties file. Make sure " + filename + " is in the CLASSPATH");
            return null;
        }
        Enumeration enu=props.propertyNames();
        while(enu.hasMoreElements()){
            String name=(String) enu.nextElement();
            props.setProperty(name,SysUtil.toGb2312(props.getProperty(name)));
        }
        return props;
    }

    public static Properties getPropbyResourceName(String filename){
        Properties props;
        InputStream is =new SysProp().getClass().getClassLoader().getResourceAsStream(filename);
        props = new Properties();
        try {
            props.load(is);
            is.close();
            System.out.println("load "+filename+" parameters... " );
        }catch (Exception e) {
            System.err.println("Can't read the properties file. Make sure " + filename + " is in the CLASSPATH");
            return null;
        }
        Enumeration enu=props.propertyNames();
        while(enu.hasMoreElements()){
            String name=(String) enu.nextElement();
            props.setProperty(name,SysUtil.toGb2312(props.getProperty(name)));
        }
        return props;
    }
    private  Properties getProp(String filename){
        Properties props;
        InputStream is = getClass().getClassLoader().getResourceAsStream("conf\\"+filename);
        props = new Properties();
        try {
            props.load(is);
            is.close();
            System.out.println("load system parameters... " );
        }catch (Exception e) {
            System.err.println("Can't read the properties file. Make sure " + filename + " is in the CLASSPATH");
            return null;
        }
        Enumeration enu=props.propertyNames();
        while(enu.hasMoreElements()){
            String name=(String) enu.nextElement();
            props.setProperty(name,SysUtil.toGb2312(props.getProperty(name)));
        }
        return props;
    }




}
