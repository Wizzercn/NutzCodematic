package pandy.db;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Driver;
import java.util.*;


public class DBConnectionManager
{
	static private DBConnectionManager instance;
	static private int clients;
    static String dbname="db";
    public static String user="";
	private Vector drivers = new Vector();
	private PrintWriter log;
	private Hashtable pools = new Hashtable();

	/**
	 * 返回唯一实例。如果是第一次调用此方法，则创建实例
	 * * * @return DBConnectionManager 唯一实例
	 */
	static synchronized public DBConnectionManager getInstance()
	{
		if (instance == null)
		{
			instance = new DBConnectionManager();
		}
		clients++;
		return instance;
	}

    /**
     * 根据新的工程文件建立驱动程序
     */
    static synchronized public  DBConnectionManager getnewInstance(String newdbname)
    {
        dbname=newdbname;
        instance = new DBConnectionManager();
        clients=0;
        return instance;
    }


	//构建私有函数以防止其他对象调用本类实例
	private DBConnectionManager()
	{
		init();
	}

	/**
	 * 将连接对象返回给由名字指定的连接池
	 * * * @param name 在属性文件中定义的连接池名字
	 * * * @param con 连接对象
	 */
	public void freeConnection(String name, Connection con)
	{
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null)
		{
			pool.freeConnection(con);
		}
	}

	/**
	 * 获得一个可用（空闲的）连接。如果没有可用连接，且已有连接数小于最大连接数限制，则创建并返回新连接
	 * * * @param name 在属性文件中定义的连接池名字
	 * * * @param Connecton 可用连接或null
	 */
	public Connection getConnection(String name)
	{
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null)
		{
			return pool.getConnection();
		}
		return null;
	}

	/**
	 * 获得一个可用（空闲的）连接。如果没有可用连接，且已有连接数小于最大连接数限制，则创建并返回新连接。否则，在指定的时间内等待其他线程释放连接
	 * * * @param name 在属性文件中定义的连接池名字
	 * * * @param time 以毫秒计的等待时间
	 * * * @return Connection 可用连接或null
	 */
	public Connection getConnection(String name, long Time)
	{
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null)
		{
			return pool.getConnection(Time);
		}
		return null;
	}

	//闭关所有连接，撤消驱动程序的注册
	public synchronized void release()
	{
		//等待直至最后一个祈望客户程序调用
		if (--clients != 0)
		{
			return;
		}
		Enumeration allPools = pools.elements();
		while (allPools.hasMoreElements())
		{
			DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
			pool.release();
		}
		Enumeration allDrivers = drivers.elements();
		while (allDrivers.hasMoreElements())
		{
			Driver driver = (Driver) allDrivers.nextElement();
			try
			{
				DriverManager.deregisterDriver(driver);
				System.out.println("撤消JDBC驱动程的注册" + driver.getClass().getName() + "的注册");
			}
			catch (Exception e)
			{
				System.out.println("无法撤消下列JDBC驱动程序的注册" + driver.getClass().getName());
			}
		}
	}

	/**
	 * 根据指定属性创建连接池实例
	 * * * @param progs 连接池属性
	 */
	private void createPools(Properties props)
	{

		Enumeration propNames=props.propertyNames();
		while(propNames.hasMoreElements())
		{
			String name=(String)propNames.nextElement();
			if(name.endsWith(".url"))
			{
				String poolName=name.substring(0,name.lastIndexOf("."));
				String URL=props.getProperty(poolName+".url");
				if(URL==null)
				{
					log("没有为连接池"+poolName+"指定URL");
					continue;
				}
				user=props.getProperty(poolName+".user");
				String password=props.getProperty(poolName+".password");
				String maxconn=props.getProperty(poolName+".maxconn","0");
				int max;
				try
				{
					max=Integer.valueOf(maxconn).intValue();
				}
				catch(NumberFormatException e)
				{
					log("错误的最大连接数限制："+maxconn+"。连接池："+poolName);
					max=0;
				}
				DBConnectionPool pool=new DBConnectionPool(poolName,URL,user,password,max);
				pools.put(poolName,pool);
			}
		}


	}

	//读取属性完成初始化
	private void init()
	{
		Properties dbProps=new Properties();
		try
		{   File file = new File("properties/"+dbname+".properties");
            InputStream is=new FileInputStream(file.getAbsolutePath());
			dbProps.load(is);
		}
		catch(Exception e)
		{
			System.err.println("不能读取属性文件。"+"请确保db.properties在classpath指定的路径中");
			return;
		}
		String logFile=dbProps.getProperty("logfile","log.txt");
		try
		{
			log=new PrintWriter(new FileWriter(logFile,true),true);
		}
		catch(IOException e)
		{
			System.err.println("无法打开日志文件："+logFile);
			log=new PrintWriter(System.err);
		}
        String user=dbProps.getProperty("sqlserverbase.user");
		loadDrivers(dbProps);
		createPools(dbProps);
	}

	/**
	 * 加载和注册所有JDBC驱动程序
	 * * * @param progs 连接池属性
	 */
	private void loadDrivers(Properties Props)
	{
		String driverClasses=Props.getProperty("driver");
		StringTokenizer st=new StringTokenizer(driverClasses);
		while(st.hasMoreElements())
		{
			String driverClassName=st.nextToken().trim();
			try
			{
                Driver Driver =(Driver)	Class.forName(driverClassName).newInstance();
                DriverManager.registerDriver(Driver);
                drivers.addElement(Driver);
				log("成功注册JDBC驱动程序"+driverClassName);
			}
			catch(Exception e)
			{
				log("无法注册JDBC驱动程序："+driverClassName+",错误："+e);
			}
		}

	}

	//将文本信息写入日志文件
	private void log(String msg)
	{
		System.out.println(new Date() + ":" + msg);
	}

	//将文本信息与异常写入日志文件
	private void log(Throwable e, String msg)
	{
		System.out.println(new Date() + ":错误原因：" + e + ",错误信息：" + msg);
	}

	// 此内部类定义了一个连接池。它能够根据要求创建新连接，直到预定的最大连接数为止。在返回连接给客户程序之前，它能够验证连接的有效性
	class DBConnectionPool
	{
		/**
		 * 创建新的连接池
		 *
		 * @param name 连接池名字
		 * @param URL 数据库的JDBC　URL
		 * @param user 数据库账号，或null
		 * @param password 密码，或null
		 * @param maxConn 此连接池允许建立的最大连接数
		 */
		private int checkedOut;
		private Stack freeConnections = new Stack();
		private int maxConn;
		private int Conncount = 0;
		private String name;
		private String password;
		private String URL;
		private String user;
		private String databaseUrl;

		public DBConnectionPool(String name, String url, String user, String password, int maxConn)
		{
			this.name = name;
			this.URL = url;
			this.user = user;
			this.password = password;
			this.maxConn = maxConn;
			for (int i = 0; i < maxConn; i++)
			{
				Connection con = newConnection();
				checkedOut++;
				freeConnection(con);
			}
		}

		/**
		 * 将不再使用的连接返回给连接池
		 * * * @param con 客户程序释放的连接
		 */
		public synchronized void freeConnection(Connection con)
		{
			freeConnections.push(con);
			checkedOut--;
			notifyAll();
			System.out.println("成功释放一个连接！连接池情况：大小：" + Conncount + ",空闲：" + freeConnections.size() + "，现用：" + checkedOut);
		}

		//从连接池获得一个可用连接。如没有空闲的连接且当前连接数小于最大连接数限制，则创建新连接。如原来登记为可用的连接不再有效，则从向量中删除，然后递归调用自己以尝试新的可用连接。
		public synchronized Connection getConnection()
		{
			Connection con = null;
			if (freeConnections.size() > 0)
			{
				//获取向量中的第一个可用连接
				con = (Connection) freeConnections.pop();
				try
				{
					if (con.isClosed())
					{
						log("从连接池" + name + "删除一个无效连接");
						//递归调用自己，尝试再次获取可用连接
						con = getConnection();
					}
				}
				catch (Exception e)
				{
					log("从连接池" + name + "删除一个无效连接");
					//递归调用自己，尝试再次获取可用连接
					con = getConnection();

				}
				log("从连接池" + name + "成功获取一个连接");
			}
			else if (maxConn == 0 || checkedOut < maxConn)
			{
				con = newConnection();
			}
			else
			{
				try
				{
					log("max using connect: [max:" + maxConn + "/all:" + Conncount + "/free:" + freeConnections.size() + "],wait ...");
					wait(1000 * 10);
					return getConnection();
				}
				catch (InterruptedException ie)
				{
				}
			}
			if (con != null)
			{
				checkedOut++;
			}
			return con;
		}

		//从连接池获取可用连接。可以指定客户程序能够等待的最长时间
		//@param timeout　以毫秒计的等待时间限制
		public synchronized Connection getConnection(long timeout)
		{
			long startTime = new Date().getTime();
			Connection con;
			while ((con = getConnection()) == null)
			{
				try
				{
					wait(timeout);
				}
				catch (InterruptedException e)
				{
				}
				if ((new Date().getTime() - startTime) >= timeout)
				{
					return null;
				}
			}
			return con;
		}

		//关闭所有连接
		public synchronized void release()
		{
			Enumeration allConnections = freeConnections.elements();
			while (allConnections.hasMoreElements())
			{
				Connection con = (Connection) allConnections.nextElement();
				try
				{
					con.close();
					log("关闭连接池" + name + "中的一个连接");
				}
				catch (Exception e)
				{
					log(e, "无法关闭连接池" + name + "中的一个连接");
				}
			}
			freeConnections.removeAllElements();
		}

		//创建新的连接
		private Connection newConnection()
		{
			Connection con = null;
			try
			{
				if (user == null)
				{
					con = DriverManager.getConnection(URL);
				}
				else
				{
					con = DriverManager.getConnection(URL, user, password);
				}
				log("连接池" + name + "创建一个新的连接");
				Conncount++;
			}
			catch (Exception e)
			{
				log(e, "无法创建下列URL的连接" + URL + "用户名：" + user + "密码：" + password);
				return null;
			}
			return con;
		}
	}
}

				
		
	
	
