package pandy.file;

import java.io.*;
/**
 * Created by IntelliJ IDEA.
 * User: pandy
 * Date: 2005-11-25
 * Time: 14:29:23
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil {

    public void fileUtil(String rootpath){

    }
    /**
     * 取得文件内容
     * @return String
     */
    public static String getFileCnt(String filePath){
        StringBuffer content=new StringBuffer();
        try{
            File temFile=new File(filePath);
            FileReader fileReader=new FileReader(temFile);
            BufferedReader bufferReader=new BufferedReader(fileReader);
            String str=null;
            while((str=bufferReader.readLine())!=null)
            {
                content.append(str).append("\n");
            }
            bufferReader.close();
            fileReader.close();
        }catch(Exception e){e.printStackTrace();}
        return content.toString();
    }
    /**
     * 覆盖文件的内容
     */
    public static boolean  setFileCnt(String filePath,String  newContent){
        try
        {
            File file=new File(filePath);
            if(!file.exists()) file.createNewFile();
            PrintWriter out = new PrintWriter(new FileWriter(file));
            out.print(newContent);
            out.close();
            return true;
        }
        catch (IOException e)
        {
            System.out.println("update template false:"+e.toString());
            return false;
        }
    }

    /**
     * 返回标识内的内容
     * @param note
     * @param flag
     */
    public static String getflagnote(String note,String flag)
    {
        String temp="";
        int a=note.indexOf("<!--sysflag:"+flag.trim()+"-->")+flag.trim().length()+15;
        int b=note.indexOf("<!--sysflag:/"+flag.trim()+"-->");
        temp=note.substring(a,b);
        return temp;
    }

    /**
     * 将指定标识的内容替换
     * @param note
     * @param flag
     * @param replacenote
     */
    public static String getreplaceflagnote(String note,String flag,String replacenote)
    {
        String temp=note;
        int a=note.indexOf("<!--sysflag:"+flag.trim()+"-->");
        int b=note.indexOf("<!--sysflag:/"+flag.trim()+"-->")+flag.trim().length()+16;
        temp=note.substring(0,a)+replacenote+note.substring(b);
        return temp;
    }

}

