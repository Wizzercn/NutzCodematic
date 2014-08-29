package pandy.file;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.File;
/**
 * Created by IntelliJ IDEA.
 * User: pandy
 * Date: 2005-11-25
 * Time: 14:26:46
 * To change this template use File | Settings | File Templates.
 */
public class DownHtml extends javax.servlet.GenericServlet{
      public void service(ServletRequest req,ServletResponse rep)
    {
        try{
            HttpServletRequest request=(HttpServletRequest)req;
            String htmlbody = request.getParameter("htmlbody");
            String type=request.getParameter("type");
            if(type==null) type="xls";
            htmlbody=new String(htmlbody.getBytes("iso8859-1"),"gbk");
            htmlbody=htmlbody.replaceAll("border=\"0\"","border=\"1\"");
            htmlbody=htmlbody.replaceAll("border=0","border=1");
            htmlbody=htmlbody.replaceAll("border='0'","border='1'");
            StringBuffer temp=new StringBuffer();
            temp.append("<html>\n");
            temp.append("<meta http-equiv=\"Content-Type\" content=\"APPLICATION/OCTET-STREAM; charset=gbk\">");
            temp.append("<body>\n");
            temp.append(htmlbody+"\n");
            temp.append("</body>\n");
            temp.append("</html>\n");
            String content = temp.toString();
            String filepath="c:\\temp."+type;
            FileUtil.setFileCnt(filepath,content);
            javax.servlet.http.HttpServletResponse response=(HttpServletResponse)rep;
            ServletOutputStream out=response.getOutputStream();
            response.setContentType("APPLICATION/OCTET-STREAM;charset=gbk");
            response.setHeader("Content-disposition","attachment;filename=\"state."+type+"\"" );
            File f=new File(filepath);
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

