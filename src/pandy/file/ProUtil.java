package pandy.file;
import java.util.*;
import java.io.*;
public class ProUtil 
{
	private static ProUtil instance;
	private static Properties pro=new Properties();
	/**
	 *获得唯一实例.
	 */
	public static ProUtil getInstance()throws Exception
	{
		if(instance==null)
		{
			instance=new ProUtil(getRelativePath());
		}
		return instance;
	}
	/**
	 *私有化构造函数,导入对应文件的属性.
	 *relativePath为相对classpath的路径值.
	 */
	private ProUtil(String relativePath)throws Exception
	{
		loadProperties(getFile(relativePath));
	}
	/**
	 *获得相对路径为relativePath值的File变量.
	 */
	public File getFile(String relativePath)throws Exception
	{
		String absolutePath=null;
		try 
		{
			absolutePath=getClass().getClassLoader().getResource(relativePath).getPath();//获得绝对路径
		}
		catch(Exception e)
		{
			throw new Exception("属性文件路径错误!请将文件放置于classpath路径下:"+relativePath+e);
		}
		File f=new File(absolutePath);
		if(!f.exists())
		{
			throw new Exception("绝对路径为:"+absolutePath+" 的文件不存在.注意路径值不能包含有中文字符.");
		}
		if(!f.canWrite())
		{
			throw new Exception("文件属性设置错误,请将文件属性设置为可读.");
		}
		return f;
	}
	/**
	 *从属性文件中导入属性值.
	 */
	public void loadProperties(File f)throws Exception
	{
		FileInputStream fin=null;
		try
		{
			fin=new FileInputStream(f); 
			pro.load(fin);//将文件中的属性值导入static变量中
			fin.close();
		}
		catch(Exception e)
		{
			throw new Exception("打开属性文件错误:"+e);
		}
		finally
		{
			if(fin!=null)
			{
				try
				{
					fin.close();
				}
				catch(Exception e)
				{
					throw new Exception("导入属性时,关闭文件错误:"+e);
				}
			}
		}
	}
	/**
	 *返回属性pro.
	 */
	public static Properties getPro()throws Exception
	{
		/**
		 *若实例instance为空,则重新实例化.
		 */
		getInstance();
		return pro;
	}
	public static String getValue(String key,String defaultValue)throws Exception
	{
		getInstance();
		return pro.getProperty(key,defaultValue); 
	}
	public static String getValue(String key)throws Exception
	{
		getInstance();
		return pro.getProperty(key); 
	}

	/**
	 *设定属性文件路径值,并返回其值.
	 */
	public static String getRelativePath()
	{
		String relativePath="file.properties";
		return relativePath;
	}
}
