package pandy.file;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.File;
/**
 * Created by IntelliJ IDEA.
 * User: pandy
 * Date: 2005-11-25
 * Time: 14:41:29
 * To change this template use File | Settings | File Templates.
 */
public class DownFile  extends javax.servlet.GenericServlet{
    public void service(ServletRequest req,ServletResponse rep)
	{
		try{
			HttpServletRequest request=(HttpServletRequest)req;
			javax.servlet.http.HttpServletResponse response=(HttpServletResponse)rep;
			ServletOutputStream out=response.getOutputStream();
            String pathAndName=request.getParameter("path");
            String showname=request.getParameter("showname");
            showname=java.net.URLEncoder.encode(showname,"utf-8");
            String filename=showname+pathAndName.substring(pathAndName.lastIndexOf("."));
            response.setContentType("APPLICATION/OCTET-STREAM;charset=gbk");
            response.setHeader("Content-disposition","filename=\""+filename+"\"" );
            File f=new File(pathAndName);
        	BufferedInputStream bufferedInput =new BufferedInputStream(new FileInputStream(f));
			byte[] buffer=new byte[1024];
			int i;
			while((i=bufferedInput.read(buffer))!=-1){
				out.write(buffer,0,i);
			}
			bufferedInput.close();
			out.flush();
			out.close();
		}
		catch(Exception e)
		{
            e.printStackTrace();
			System.out.println("servlet Error:"+e.getMessage());
		}
    }
}

