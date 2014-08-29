package pandy.sys;

import java.util.Properties;
import java.util.Vector;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2006-2-24
 * Time: 16:54:56
 * To change this template use File | Settings | File Templates.
 */
public class SysproCtl
{
    public static Vector getsysprolist()
    {
        File dir = new File("properties/");
        String[] children = dir.list();
        Vector rs=new Vector();
        if (children != null)
        {
            for (int i = 0; i < children.length; i++)
            {
                // Get filename of file or directory
                String filename = children[i];
                if (filename.endsWith(".properties"))
                {
                    SysproInfo prop=new SysproInfo();
                    Properties dbProps=new Properties();
                    try
                    {   File file = new File("properties/"+filename.substring(0, filename.length() - 11)+".properties");
                        InputStream is=new FileInputStream(file.getAbsolutePath());
                        dbProps.load(is);
                        prop=new SysproInfo(dbProps);
                        prop.setDbname(filename.substring(0, filename.length() - 11));
                        rs.add(prop);
                    }
                    catch(Exception e)
                    {
                        System.err.println("不能读取属性文件。"+"请确保db.properties在classpath指定的路径中");
                        return null;
                    }


                }
            }
        }
        return rs;
    }
}
