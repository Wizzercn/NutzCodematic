package pandy.util;

import java.util.*;
import java.net.URLEncoder;
import java.net.URL;
import java.io.UnsupportedEncodingException;

public class StringUtil
{
	public static boolean blnTextBox = true;

	/**
	 * getGBKFromISO, translate it into Chinese
	 *
	 * @param str
	 * @return String, the chinese string
	 */
	public static String getGBKFromISO(String str)
	{
		try
		{
			byte[] buf = str.getBytes("iso-8859-1");
			str = new String(buf, "gb2312");
			return str;
		}
		catch (java.io.UnsupportedEncodingException e)
		{
			return "";
		}
	}

	public static String getISO(String str)
	{
		try
		{
			byte[] buf = str.getBytes("iso-8859-1");
			str = new String(buf);
			return str;
		}
		catch (java.io.UnsupportedEncodingException e)
		{
			return "";
		}
	}

	/**
	 * explode, separate string into a Vector
	 *
	 * @param handleStr
	 * @param pointStr
	 * @return Vector, include separated string
	 */
	public static Vector explode(String handleStr, String pointStr)
	{
		Vector v = new Vector();
		int pos1, pos2;
		try
		{
			if (handleStr.length() > 0)
			{
				pos1 = handleStr.indexOf(pointStr);
				pos2 = 0;
				while (pos1 != -1)
				{
					v.addElement(handleStr.substring(pos2, pos1));
					pos2 = pos1 + pointStr.length();
					pos1 = handleStr.indexOf(pointStr, pos2);
				}
				v.addElement(handleStr.substring(pos2));
			}
		}
		catch (Exception error)
		{
			error.printStackTrace();
		}
		return v;
	}

	/**
	 * replace, replace a string with another string in a string
	 *
	 * @param handleStr
	 * @param pointStr
	 * @param repStr
	 * @return string,
	 */
	public static String replace(String handleStr, String pointStr, String repStr)
	{
		String str = new String();
		int pos1, pos2;
		try
		{
			if (handleStr.length() > 0)
			{
				pos1 = handleStr.indexOf(pointStr);
				pos2 = 0;
				while (pos1 != -1)
				{
					str += handleStr.substring(pos2, pos1);
					str += repStr;
					pos2 = pos1 + pointStr.length();
					pos1 = handleStr.indexOf(pointStr, pos2);
				}
				str += handleStr.substring(pos2);
			}
		}
		catch (Exception error)
		{
			error.printStackTrace();
		}
		return str;
	}

	public static void setReturn(boolean blnAttrib)
	{
		blnTextBox = blnAttrib;
	}

	/**
	 * htmlspecialchars, Change HTML special char in String
	 *
	 * @param handleStr
	 * @return String
	 */
	public static String htmlSpecialChars(String handleStr)
	{
		return htmlSpecialChars(handleStr, true);
	}

	public static String htmlSpecialChars(String handleStr, boolean seq)
	{
		String str = handleStr;

		if (seq)
		{
			str = replace(str, "&", "&amp;");
			str = replace(str, "\"", "&quot;");
			str = replace(str, "<", "&lt;");
			str = replace(str, ">", "&gt;");
		}
		else
		{
			str = replace(str, "&amp;", "&");
			str = replace(str, "&quot;", "\"");
			str = replace(str, "&lt;", "<");
			str = replace(str, "&gt;", ">");
		}

		if (!blnTextBox)
			if (seq)
				str = replace(str, "\n", "<br>");
			else
				str = replace(str, "<br>", "\n");

		return str;
	}

	/**
	 * htmlspecialchars, replace '\n' with '<br>', used in page
	 *
	 * @param handleStr
	 * @return String
	 */
	public static String returnChar2BR(String handleStr)
	{
		String str = handleStr;
		str = replace(str, "\n", "<br>&nbsp;&nbsp;");
		return str;
	}

	/**
	 * implode, link a vector into a string with a separate string
	 *
	 * @param handler
	 * @param separator
	 * @return String, linked string
	 */
	public static String implode(Vector handler, String separator)
	{
		StringBuffer strbuf = new StringBuffer();
		try
		{
			if (!handler.isEmpty())
			{
				int len = handler.size();
				for (int loopi = 0; loopi < len; loopi++)
				{
					strbuf.append((String) handler.get(loopi));
					if (loopi != len - 1)
						strbuf.append(separator);
				}
			}
		}
		catch (Exception error)
		{
			error.printStackTrace();
		}
		return strbuf.toString();
	}

	/**
	 * Return appointed String from a String Vector
	 * param1: String Vector
	 * param2: appointed Index
	 * param3: include Excel CSV process.
	 */
	public static String getField(Vector vt, int i, boolean isExcel)
	{
		String str = "";
		try
		{
			str = (String) vt.get(i);
			if (str != null && str.length() > 2 && isExcel)
			{
				if (str.substring(0, 1).compareTo("\"") == 0)
				{
					str = str.substring(1, str.length() - 1);
					str = StringUtil.replace(str, "\"\"", "\"");
				}
			}
		}
		catch (ArrayIndexOutOfBoundsException aibe)
		{
			System.out.println("Exceed the length of array, Please check the field number");
			return "";
		}
		return str;
	}

	/**
	 * fill in inschar in string's left or right, in order to let string have appoint length.
	 * param1: father string
	 * param2: need fill in char
	 * param3: 0 is left fill in
	 * 1 is right fill in
	 * param4: total string length after fill in char
	 */
	public static String insStr(String str, String InsChar, int intDirect,
								int Len)
	{
		int intLen = str.length();
		StringBuffer strBuffer = new StringBuffer(str);

		if (intLen < Len)
		{
			int inttmpLen = Len - intLen;
			for (int i = 0; i < inttmpLen; i++)
			{
				if (intDirect == 1)
				{
					str = str.concat(InsChar);
				}
				else if (intDirect == 0)
				{
					strBuffer.insert(0, InsChar);
					str = strBuffer.toString();
				}
			}
		}
		return str;
	}

	public static int searchDiv(String str, String divided)
	{
		String tmpsearchstr = new String();
		tmpsearchstr = str;
		divided = divided.trim();
		int divpos = -1;

		if (tmpsearchstr != "")
		{
			divpos = tmpsearchstr.indexOf(divided);

			return divpos;
		}
		else
			return 0;
	}

	public static String extractStr(String str, String startdiv, String enddiv)
	{
		int startdivlen = startdiv.length();
		str = str.trim();

		int startpos = -1;
		int endpos = -1;

		startdiv = startdiv.trim();
		enddiv = enddiv.trim();
		startpos = searchDiv(str, startdiv);
		if (str != "")
		{
			if (startpos >= 0)
			{
				str = str.substring(startpos + startdivlen);
				str = str.trim();
			}
			endpos = searchDiv(str, enddiv);
			if (endpos == -1)
				return "";
			str = str.substring(0, endpos);
			str = str.trim();
		}
		return str;
	}

	public static String isNull(String str)
	{
		return isNull(str, "&nbsp;");
	}

	public static String isNull(String str, String def)
	{
		if (str == null)
			return def;
		else if (str.length() == 0)
			return def;
		else
			return str;
	}

	public static int StringToInt(String str)
	{
		return StringToInt(str, 0);
	}

	public static int StringToInt(String str, int def)
	{
		int intRet = def;
		try
		{
			intRet = Integer.parseInt(str);
		}
		catch (NumberFormatException e)
		{
		}
		return intRet;
	}

	public static float StringToFloat(String str)
	{
		return StringToFloat(str, 0);
	}

	public static float StringToFloat(String str, float def)
	{
		float fRet = def;
		try
		{
			if (str == null || str.trim().equals(""))
				str = "0";
			fRet = Float.parseFloat(str);
		}
		catch (NumberFormatException e)
		{
		}
		return fRet;
	}

	public static double StringToDouble(String str)
	{
		return StringToDouble(str, (double) 0);
	}

	public static double StringToDouble(String str, double def)
	{
		double dRet = (double) def;
		try
		{
			if (str == null || str.trim().equals(""))
				str = "0";
			dRet = Double.parseDouble(str);
		}
		catch (NumberFormatException e)
		{
		}
		return dRet;
	}

	public static String getSafeString(String str)
	{
		if (str == null)
			return "";
		else
			return str.trim();
	}

	/**
	 * we'll cut it if the length of the specify string longer than specify length
	 *
	 * @param str  in string
	 * @param iLen specify length
	 * @return out string
	 */
	public static String substr(String str, int iLen)
	{
		if (str == null)
			return "";
		if (iLen > 2)
		{
			if (str.length() > iLen - 2)
			{
				str = str.substring(0, iLen - 2) + "..";
			}

		}
		return str;
	}

	/**
	 * filter String into utf-8
	 *
	 * @param str handle string
	 * @return str
	 */
	public static String getJpString(String str)
	{
		if (str == null)
		{
			return null;
		}
		try
		{
			return new String(str.getBytes("ISO-8859-1"), "UTF-8");
		}
		catch (java.io.UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * filter String array into utf-8
	 *
	 * @param str handle string
	 * @return str[]
	 */

	public static String[] getJpString(String[] str)
	{
		if (str == null)
		{
			return null;
		}
		String[] ret = new String[str.length];
		for (int i = 0; i < str.length; i++)
		{
			ret[i] = getJpString(str[i]);
		}
		return ret;
	}

	/**
	 * filter url array into utf-8
	 *
	 * @param str handle string
	 * @return str[]
	 */

	public static String getUrlString(String str)
	{
		if (str == null)
		{
			return null;
		}
		try
		{
			return URLEncoder.encode(str, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 返回一个URL的资料定位符，提高程序的能用性
	 *
	 * @param str
	 * @return
	 */
	public URL srcurl(String str)
	{
		URL url = this.getClass().getResource(str);
		return url;
	}
}