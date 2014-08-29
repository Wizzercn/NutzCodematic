/**
 * DBUtil.java
 * @version 1.0.1
 */

package pandy.db;

import java.sql.*;
import java.util.*;
import java.math.*;
import java.text.*;
import pandy.util.*;

public class DBUtil
{
	/**
	 * isEqual, compare 2 strings
	 * @param String str1
	 * @param String str2
	 * @return boolean
	 */
	static public boolean isEqual(String str1, String str2)
	{
		if(str1 == null && str2 == null) return true;
		if((str1 == null && str2 != null) || (str1 != null && str2 == null)) return false;
		if(str1.toUpperCase().trim().compareTo(str2.toUpperCase().trim())==0) return true;
		return false;
	}

	/**
	 * getRSInt, get fields value from a object
	 * @param Object object
	 * @return int
	 */
	static public int getRSInt(Object object)
	{
		if(object == null) return 0;
		if(object instanceof Long) return ((Long)object).intValue();
		if(object instanceof Integer) return ((Integer)object).intValue();
		if(object instanceof BigDecimal) return ((BigDecimal)object).intValue();
		if(object instanceof Boolean) return (((Boolean)object).booleanValue()==true) ? 1 : 0;
		if(object instanceof Double) return ((Double)object).intValue();
		System.out.println("end of getRSInt, wrong object: " + object.getClass().getName()+"OBJ is"+object.toString());
		return 0;
	}

	/**
	 * getRSString, get fields value from a object
	 * @param Object object
	 * @return String
	 */
	static public String getRSString(Object obj)
	{
		return getRSString(obj, null);
	}

	static public String getRSString(Object obj, String def)
	{
		if(obj==null) return def;
		try{
			if(obj instanceof String){
				return (String)obj;
				//String tmp = (String)obj;
				//return new String(tmp.getBytes("gb2312"));
			}

			System.out.println("end of getRSString, wrong object: " + obj.getClass().getName());
		}catch(Exception e){e.printStackTrace();}
		return def;
	}

	static public String getRSClob(Object obj) throws java.sql.SQLException
	{
		return getRSClob(obj, null);
	}

	static public String getRSClob(Object obj , String def) throws java.sql.SQLException
	{
		if (obj == null) return def;
		if (obj instanceof java.sql.Clob) return ((java.sql.Clob)obj).getSubString((long)1,(int)((java.sql.Clob)obj).length());
		System.out.println("end of getRSClob, wrong object: " + obj.getClass().getName());
		return def;
	}

	/**
	 * getRSDouble, get fields value from a object
	 * @param Object object
	 * @return double
	 */
	static public double getRSDouble(Object object)
	{
		if(object==null) return (double)0;
		if(object instanceof BigDecimal) return ((BigDecimal)object).doubleValue();
		if(object instanceof Double) return ((Double)object).doubleValue();
		System.out.println("end of getRSDouble, wrong object: " + object.getClass().getName());
		return (double)0;
	}

	/**
	 * getRSBigDecimal, get fields value from a object
	 * @param Object object
	 * @return double
	 */
	static public double getRSBigDecimal(Object object)
	{
		if(object==null) return (double)0;
		return ((BigDecimal)object).doubleValue();
	}

	/**
	 * getRSDate, get fields value from a object
	 * @param Object object
	 * @return java.util.Date
	 */
	static public java.util.Date getRSDate(Object object)
	{
		if(object==null) return null;
		if(object instanceof java.util.Date) return (java.util.Date)object;
		System.out.println("end of getRSDate, wrong object: " + object.getClass().getName());
		return new java.util.Date(((Timestamp)object).getTime());
	}

	/**
	 * getObject, get fields value from a object
	 * @param int sqlType, the field type in sql statement
	 * @param Object object, field object
	 * @return Object
	 */
	static public Object getObject(int sqlType, Object object)
	{
		if(object==null) return null;
		switch(sqlType)
		{
			case 93:			// datetime
				return new java.util.Date(((Timestamp)object).getTime());
			case 4:				// int
			case 5:				// small int as int
			case -6:			// tiny int as int
				return (Integer)object;
			case 12:			// string
			case -1:			// mysql longtext
			case -9:			// nvarchar as string
			case -10:			// ntext as string
				return ((String)object).trim();
			case -7:			// bit as boolean
				return (Boolean)object;
			case 2:				// money
				if(object instanceof Double) return (Double)object;
				return new Double(((BigDecimal)object).doubleValue());
			default:
				System.out.println("unknown type, sqlType: " + sqlType);
		}
		return null;
	}
	/**
	 * getObjectEX, get fields value from a object
	 * @param int sqlType, the field type in sql statement
	 * @param Object object, field object
	 * @return Object
	 */
	static public Object getObjectEX(int sqlType, Object object)
	{
		/**
		 * zhou add for customize report use, because Number(in oracle) is always BigDecimal in Jave
		 * but getObject make it into Double, this cause id ==> double, I make BigDecimal is BigDecimal
		 */
		if(object==null) return null;
		switch(sqlType)
		{
			case 93:			// datetime
				return new java.util.Date(((Timestamp)object).getTime());
			case 4:				// int
			case 5:				// small int as int
			case -6:			// tiny int as int
				return (Integer)object;
			case 12:			// string
			case -1:			// mysql longtext
			case -9:			// nvarchar as string
			case -10:			// ntext as string
				return ((String)object).trim();
			case -7:			// bit as boolean
				return (Boolean)object;
			case 2:				// money
				return (BigDecimal)object;
			default:
				System.out.println("unknown type, sqlType: " + sqlType);
		}
		return null;
	}

	static public String getDBSaveString(int nDBType, String strField)
	{
		return getDBSaveString(nDBType, strField, null);
	}

	static public String getDBSaveString(int nDBType, String strField, String def)
	{
		switch(nDBType)
		{
			case 1:
				return getOracleSaveString(strField, def);
			case 2:
				return getMssqlSaveString(strField, def);
			case 3:
				return getMysqlSaveString(strField, def);
			default:
				System.out.println("Unknown database type");
				return def;
		}
	}

	static public String getOracleSaveString(String strField, String def)
	{
		if(strField == null) return (def==null)?"\'N/A\'":def;
		String ret = strField;
		try{
		if(strField.indexOf('\'') != -1)
			ret = replaceCharacterWithString('\'', "\'\'", ret);
		if(strField.indexOf("&") != -1)
			ret = StringUtil.replace(ret, "&", "'||'&'||'");

		if((ret.indexOf("|")==1) && ret.length()>4)
			ret = ret.substring(4);
		if((ret.lastIndexOf("|")==(ret.length()-2)) && (ret.length()>2))
			ret = ret.substring(0,(ret.length()-4));

		//ret = new String(ret.getBytes("iso-8859-1"), "gb2312");
		}catch(Exception e){e.printStackTrace(); ret = ((def==null)?"\'N/A\'":def);}
		return "'" + ret + "'";
	}

	static public String getMssqlSaveString(String strField, String def)
	{
		if(strField == null) return (def==null)?"\'\'":def;
		if(strField.indexOf('\'') != -1)	return "'" + replaceCharacterWithString('\'', "\'\'", strField) + "'";
		return "'" + strField.trim() + "'";
	}

	static public String getMysqlSaveString(String strField, String def)
	{
		if(strField == null) return (def==null)?"\'\'":def;
		if(strField.indexOf('\'') != -1)	return "'" + replaceCharacterWithString('\'', "\'\'", strField) + "'";
		return "'" + strField + "'";
	}

	static public String getDBSaveStringNoQuote(int nDBType, String strField)
	{
		return getDBSaveStringNoQuote(nDBType, strField, null);
	}

	static public String getDBSaveStringNoQuote(int nDBType, String strField, String def)
	{
		switch(nDBType)
		{
			case 1:
				return getOracleSaveStringNoQuote(strField, def);
			case 2:
				return getMssqlSaveStringNoQuote(strField, def);
			case 3:
				return getMysqlSaveStringNoQuote(strField, def);
			default:
				System.out.println("Unknown database type");
				return def;
		}
	}

	static public String getOracleSaveStringNoQuote(String strField, String def)
	{
		if(strField == null) return (def==null)?"N/A":def;
		String ret = strField;
		try{
		if(strField.indexOf('\'') != -1)
			ret = replaceCharacterWithString('\'', "\'\'", ret);
		if(strField.indexOf("&") != -1)
			ret = StringUtil.replace(ret, "&", "'||'&'||'");

		if((ret.indexOf("|")==1) && ret.length()>4)
			ret = ret.substring(4);
		if((ret.lastIndexOf("|")==(ret.length()-2)) && (ret.length()>2))
			ret = ret.substring(0,(ret.length()-4));

		//ret = new String(ret.getBytes("iso-8859-1"), "gb2312");
		}catch(Exception e){e.printStackTrace(); ret = ((def==null)?"N/A":def);}

		return ret;
	}

	static public String getMssqlSaveStringNoQuote(String strField, String def)
	{
		if (strField == null) return def;
		if(strField.indexOf('\'') != -1)	return replaceCharacterWithString('\'', "\'\'", strField);
		return strField;
	}

	static public String getMysqlSaveStringNoQuote(String strField, String def)
	{
		if (strField == null) return def;
		if(strField.indexOf('\'') != -1)	return replaceCharacterWithString('\'', "\'\'", strField);
		return strField;
	}

	static public String getDBSaveDate(int nDBType, java.util.Date dateField)
	{
		if(dateField == null) return "NULL";
		switch(nDBType)
		{
			case 1:
				return getOracleSaveDate(dateField);
			case 2:
				return getMssqlSaveDate(dateField);
			case 3:
				return getMysqlSaveDate(dateField);
			default:
				return "unknown database type";
		}
	}

	static public SimpleDateFormat YYYY_MM_dd_HHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static public String getOracleSaveDate(java.util.Date dateField)
	{
		return "TO_DATE('" + YYYY_MM_dd_HHmmss.format(dateField) + "', 'yyyy-MM-dd HH24:MI:SS')";
	}

	static public String getMssqlSaveDate(java.util.Date dateField)
	{
		return "'" + new java.sql.Date(dateField.getTime()) + "'";
	}

	static public String getMysqlSaveDate(java.util.Date dateField)
	{
		return "'" + YYYY_MM_dd_HHmmss.format(dateField) + "'";
	}


	static public SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	static public String getSaveDataStringToDate(java.util.Date date, boolean mssql)
	{
		if(date==null) return "NULL";
		if(mssql) return "'" + new java.sql.Date(date.getTime()) + "'";
		return "TO_DATE('" + yyyy_MM_dd.format(date) + "', 'yyyy-mm-dd')";
	}

	static public SimpleDateFormat YYYYMMdd_HHmmss = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	static public String getSaveDataStringToSecond(java.util.Date date, boolean mssql)
	{
		if(date==null) return "NULL";
		if(mssql) return "'" + YYYYMMdd_HHmmss.format(date) + "'";
		return "TO_DATE('" + YYYYMMdd_HHmmss.format(date) + "', 'yyyy.MM.dd HH24:MI:SS')";
	}
	static public String get_SQL_DateString_YYYYMMdd_HHmmss(java.util.Date date)
	{
		if(date==null) return "NULL";
		return "'" + YYYYMMdd_HHmmss.format(date) + "'";
	}
	static public String get_Oracle_DateString_YYYYMMdd_HHmmss(java.util.Date date)
	{
		if(date==null) return "NULL";
		String dateString = "'" + YYYYMMdd_HHmmss.format(date) + "'";
		return "TO_DATE(" + dateString + ", 'YYYY.MM.DD HH24:MI:SS')";
	}

	static public String getSaveObject(Object object)
	{
		if(object==null) return "NULL";
		if(object instanceof java.util.Date)
			return getSaveDataStringToSecond((java.util.Date)object, false);
		if(object instanceof Integer)
			return String.valueOf(((Integer)object).intValue());
		if(object instanceof Double)
			return String.valueOf(((Double)object).doubleValue());
		if(object instanceof String)
			return getSaveString((String)object);
		if(object instanceof Boolean)
			return (((Boolean)object).booleanValue()==true) ? "1" : "0";
		System.out.println("unknow object w/h type: " + object.getClass().getName());
		return "";
	}

	static public String getSaveDateString(java.util.Date date)
	{
		if(date==null) return "NULL";
		return "'" + new java.sql.Date(date.getTime()) + "'";
	}

	static public String getSaveDateStringNoQuote(java.util.Date date)
	{
		if(date==null) return "NULL";
		return "" + new java.sql.Date(date.getTime());
	}

	static public String getSaveString(String str)
	{
		if(str==null) return "NULL";
		if(str.indexOf('\'') != -1)
			return "'" + replaceCharacterWithString('\'', "\'\'", str) + "'";
		return "'" + str + "'";
	}
	static public String getSaveStringNoQuote(String str)
	{
		if(str==null) return "";
		return str;
	}
	static public String getSaveInt(int val)
	{
		if(val == -1) return "";
		return String.valueOf(val);
	}

	static public String getSaveDate(java.util.Date val)
	{
		if(val == null) return "";
		return YYYYMMdd_HHmmss.format(val);
	}

	static public String getSaveStringNoNull(String str)
	{
		if(str==null) return "";
		if(str.indexOf('\'') != -1)
			return replaceCharacterWithString('\'', "\'\'", str);
		return str;
	}
	static public String getSaveStringNoBackSlash(String str)
	{
		if(str==null) return "";
		if(str.indexOf('\\') != -1)
			return replaceCharacterWithString('\\', " ", str);
		return str;
	}

	static public String getSaveStringEncodeBar(String str)
	{
		if(str==null) return "";
		if(str.indexOf('|') != -1)
			return replaceCharacterWithString('|', "\\|", str);
		return str;
	}

	static public String getSaveStringEncodeBackSlash(String str)
	{
		if(str==null) return "";
		if(str.indexOf('\\') != -1)
			return replaceCharacterWithString('\\', "\\\\", str);
		return str;
	}

	static public String getSaveStringNoBar(String str)
	{
		if(str==null) return "";
		if(str.indexOf('|') != -1)
			return replaceCharacterWithString('|', " ", str);
		return str;
	}
	static public String getSaveStringNoCarrage(String str)
	{
		if(str==null) return "";
		if(str.indexOf('\r') != -1)
			return replaceCharacterWithString('\r', " ", str);
		return str;
	}
	static public String getSaveStringNoNewLine(String str)
	{
		if(str==null) return "";
		if(str.indexOf('\n') != -1)
			return replaceCharacterWithString('\n', " ", str);
		return str;
	}

	static public String replaceCharacterWithString(char character, String replacement, String source)
	{
		StringBuffer myStringBuffer = new StringBuffer(source);
		int          length         = myStringBuffer.length();
		int          replacementLen = replacement.length();

		for(int indexOf = 0; indexOf < length; indexOf++)
		{
			if(myStringBuffer.charAt(indexOf) == character)
			{
				myStringBuffer.replace(indexOf, indexOf + 1, replacement);
				length = myStringBuffer.length();
				indexOf += replacementLen-1;
			}
		}
		return myStringBuffer.toString();
	}

	public static String escapeDBString(String str)
	{
		if (str == null) return "";
		int len=str.length();
		String tmpstr="";
		char char_arr[]=str.toCharArray();
		String chrar="";
		for(int i=0;i<len;i++)
		{
			chrar = String.valueOf(char_arr[i]);
			if(chrar.equals("'")==true)
				tmpstr=tmpstr+"''";
			else
				tmpstr=tmpstr + char_arr[i];
		}

		return tmpstr;
	}

	static public String toHexString(byte[] bytes)
	{
		StringBuffer strBuffer = new StringBuffer(50);
		for(int i=0; i<bytes.length; i++)
			strBuffer.append(toHexString(bytes[i]));
		return strBuffer.toString();
	}

	static public String toHexString(int iValue)
	{
		String str = Integer.toHexString(iValue);
		if(str.length()==1)
			return "0" + str;
		/**
		 * if iValue >= 128, it will return ffffff80, we need to cut off first 6 digits
		 * only get last two digits
		 */
		else if(str.length()==8)
			return str.substring(6,8);
		return str;
	}

	public static Hashtable getRsPage(Hashtable rs,int pageNum,int pageSize)
	{
		if (rs == null) return null;
		if (rs.size() <1) return null;
		if ( pageNum < 1 || pageSize <= 0)
			return null;

		Hashtable rsPage = new Hashtable();
		Enumeration	rsValue = rs.elements();

		for(int i=0; i< pageNum*pageSize && rsValue.hasMoreElements() ;i++)
		{
			if (i>= (pageNum - 1) * pageSize)
			{
				rsPage.put( new Integer(i), rsValue.nextElement());
			}
		}
		return rsPage;
	}

	static public String getUpperField(String field)
	{
		return "UPPER(" + field + ")";
	}

	public static void main(String args[])
	{
		byte[] bytes = {0, 0, 0, 0, 0, 0, 1, (byte)164};
		System.out.println("str:<" + toHexString(bytes));
	}
}