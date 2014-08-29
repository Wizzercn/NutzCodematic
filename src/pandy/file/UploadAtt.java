package pandy.file;

import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletException;

/**
 * User: jomson
 * Date: 2005-7-11
 * Desp: .
 */
public class UploadAtt {

        private static String newline = "\n";
        private String uploadDirectory = ".";
        private String ContentType = "";
        private String CharacterEncoding = "";
        String filename="";
        int m_totalBytes=0;
        int maxSize=4000000;
        String saveFilename=null;

        public void setSaveFilename(String filename){
            saveFilename=filename;
        }

        public void setMaxSize(int maxSize){this.maxSize=maxSize;}
        public void setUploadDirectory(String s){
            uploadDirectory = s;
        }

        public void setContentType(String s){
            ContentType = s;
            int j;
            if((j = ContentType.indexOf("boundary=")) != -1){
                ContentType = ContentType.substring(j + 9);
                ContentType = "--" + ContentType;
            }
        }

        public void setCharacterEncoding(String s){
            CharacterEncoding = s;
        }
        public int getSize(HttpServletRequest req) throws ServletException ,IOException{
            return req.getContentLength();
        }
        public boolean uploadFile( HttpServletRequest req) throws ServletException, IOException{
            setCharacterEncoding(req.getCharacterEncoding());
            setContentType(req.getContentType());
            m_totalBytes = req.getContentLength();
            if(!(m_totalBytes>maxSize))
            {
                uploadFile(req.getInputStream());
                return true;
            }
            else
            {
                return false;
            }
        }
         public boolean uploadFile( HttpServletRequest req,boolean noLimit) throws ServletException, IOException{
            setCharacterEncoding(req.getCharacterEncoding());
            setContentType(req.getContentType());
            uploadFile(req.getInputStream());
            return true;
        }
        public void uploadFile( ServletInputStream servletinputstream) throws ServletException, IOException{

            String s5 = null;
            this.filename =filename;
            byte Linebyte[] = new byte[4096];
            byte outLinebyte[] = new byte[4096];
            int ai[] = new int[1];
            int ai1[] = new int[1];

            String line;
            while((line = readLine(Linebyte, ai, servletinputstream, CharacterEncoding)) != null)
            {
                int i = line.indexOf("filename=");
                if(i >= 0){
                    line = line.substring(i + 10);
                    if((i = line.indexOf("\"")) > 0)
                    line = line.substring(0, i);
                    break;
                }
            }

            filename = line;

            if(filename != null && !filename.equals("\""))
            {
            filename = getFileName(filename);
            filename=filename.replaceAll(" ","");
            String sContentType = readLine(Linebyte, ai, servletinputstream, CharacterEncoding);
            if(sContentType.indexOf("Content-Type") >= 0)
            readLine(Linebyte, ai, servletinputstream, CharacterEncoding);
            //String filename1=new String(filename.getBytes("gb2312"),"iso8859-1");
            String filename1=filename;
            if(saveFilename!=null)
               filename1= saveFilename;
            File file = new File(uploadDirectory, filename1);
            FileOutputStream fileoutputstream = new FileOutputStream(file);

            while((sContentType = readLine(Linebyte, ai, servletinputstream, CharacterEncoding)) != null){
                if(sContentType.indexOf(ContentType) == 0 && Linebyte[0] == 45)
                    break;

                if(s5 != null){
                    fileoutputstream.write(outLinebyte, 0, ai1[0]);
                    fileoutputstream.flush();
                }
                s5 = readLine(outLinebyte, ai1, servletinputstream, CharacterEncoding);
                if(s5 == null || s5.indexOf(ContentType) == 0 && outLinebyte[0] == 45)
                    break;
                fileoutputstream.write(Linebyte, 0, ai[0]);
                fileoutputstream.flush();
            }

            byte byte0;
            if(newline.length() == 1)
            byte0 = 2;
            else
            byte0 = 1;
            if(s5 != null && outLinebyte[0] != 45 && ai1[0] > newline.length() * byte0)
                fileoutputstream.write(outLinebyte, 0, ai1[0] - newline.length() * byte0);
            if(sContentType != null && Linebyte[0] != 45 && ai[0] > newline.length() * byte0)
                fileoutputstream.write(Linebyte, 0, ai[0] - newline.length() * byte0);
            fileoutputstream.close();
            }

        }

        //=============================================================
        public String getUploadFilename(){
            String s=filename;
            return s;
            }

        //=============================================================

        private String readLine(byte Linebyte[], int ai[],
            ServletInputStream servletinputstream,
            String CharacterEncoding){
            try{
                //readLine(byte[] buffer, int offset, int length)
                //Reads a line from the POST data.
                ai[0] = servletinputstream.readLine(Linebyte, 0, Linebyte.length);
                if(ai[0] == -1)
                return null;
                }catch(IOException _ex){
                return null;
                }
            try{
                if(CharacterEncoding == null){
                return new String(Linebyte, 0, ai[0]);
                }else{
                return new String(Linebyte, 0, ai[0], CharacterEncoding);
                }
            }catch(Exception _ex){
                return null;
            }
        }

        private String getFileName(String s){
            int i = s.lastIndexOf("\\");
            if(i < 0 || i >= s.length() - 1){
                i = s.lastIndexOf("/");
                if(i < 0 || i >= s.length() - 1)
                return s;
            }
            return s.substring(i + 1);
        }

         public int getSize()
            {
                return m_totalBytes;
            }

}
