package pandy.util;


import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;

/**
 * Created by Jomson.
 * Date: 2004-5-22
 * Desc:
 */
public class DateUtil
{

	public static String oraDateFormat = "TO_DATE(?, 'yyyy-MM-dd')";
	public static String oraDateTimeFormat = "TO_DATE(?, 'yyyy-MM-dd HH24:MI:SS')";
	static public SimpleDateFormat MMddYYYY_HHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static public SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
	static public SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 得到当前日期的年月
	 *
	 * @param day
	 * @return
	 */
	public static String getYearMonth(String day)
	{
		if (day == null) return "";
		if (day.length() < 8) return "";
		int n = day.lastIndexOf("-");
		return day.substring(0, n);
	}

	/**
	 * 得到当前日期的年份
	 *
	 * @param day
	 * @return
	 */
	public static int getYear(String day)
	{
		if (day == null) return 0;
		if (day.length() < 8) return 0;
		return Integer.parseInt(day.substring(0, 4));
	}

	/**
	 * 得到当前日期的月
	 *
	 * @param day
	 * @return
	 */
	public static String getMonth(String day)
	{
		if (day == null) return "0";
		if (day.length() < 8) return "0";
		int m = day.indexOf("-", 0);
		int n = day.lastIndexOf("-");
		String temp = day.substring(m + 1, n);
		if (temp.length() == 1) temp = "0" + temp;
		return temp;
	}

	/**
	 * 将util日期转化为字符串格式为yyyy-MM-dd HH:mm:ss
	 *
	 * @param date
	 * @return
	 */
	public static String date2str(java.util.Date date)
	{
		if (date == null) return "";
		try
		{
			return MMddYYYY_HHmmss.format(date);
		}
		catch (Exception e)
		{
			return "";
		}
	}

	/**
	 * 将util日期转化为字符串格式为yyyy-mm-dd
	 *
	 * @param date
	 * @return
	 */
	public static String getDateStr(java.util.Date date)
	{
		if (date == null) return "";
		try
		{
			return yyyyMMdd.format(date);
		}
		catch (Exception e)
		{
			return "";
		}
	}

	/**
	 * 将sql日期转化为字符串格式为yyyy-mm-dd
	 *
	 * @param date
	 * @return
	 */
	static public String date2str(java.sql.Date date)
	{
		if (date == null) return "";
		try
		{
			return yyyyMMdd.format(date);
		}
		catch (Exception e)
		{
			return "";
		}

	}

	/**
	 * 将sql日期转化为字符串格式为yyyy-MM-dd HH:mm:ss
	 *
	 * @param date
	 * @return
	 */
	static public String dateTime2str(java.sql.Date date)
	{
		if (date == null) return "";
		try
		{
			return MMddYYYY_HHmmss.format(date);
		}
		catch (Exception e)
		{
			return "";
		}
	}

	/**
	 * 将sql时间转化为字符串格式为yyyy-MM-dd HH:mm:ss
	 *
	 * @param date
	 * @return
	 */
	static public String dateTime2str(java.sql.Timestamp date)
	{
		if (date == null) return "";
		try
		{
			return MMddYYYY_HHmmss.format(date);
		}
		catch (Exception e)
		{
			return "";
		}
	}

	/**
	 * 得到util日期的年格式为yyyy
	 *
	 * @param date
	 * @return
	 */
	static public String getYYYY(java.util.Date date)
	{
		if (date == null) return "";
		return yyyy.format(date);
	}

	/**
	 * 将util日期转化为字符串格式为yyyy-mm-dd
	 *
	 * @param date
	 * @return
	 */
	static public String getyyyyMMdd(java.util.Date date)
	{
		if (date == null) return "";
		return yyyyMMdd.format(date);
	}

	/**
	 * String YYYY-MM-DD to java.sql.Date object
	 *
	 * @param str
	 * @return
	 */
	public static Date str2date(String str)
	{
		java.sql.Date result = null;
		try
		{
			java.util.Date udate = yyyyMMdd.parse(str);
			result = new Date(udate.getTime());
			return result;
		}
		catch (Exception e)
		{
			System.out.println("DateUtil.str2date(str) Error:e = " + e);
			return null;
		}
	}

	/**
	 * @param handedate
	 * @return
	 */
	public static Timestamp str2dateTime(String handedate)
	{
		Timestamp time = null;
		try
		{
			java.util.Date date = str2utilDate(handedate);
			java.util.Date now = new java.util.Date();
			date.setHours(now.getHours());
			date.setMinutes(now.getMinutes());
			date.setSeconds(now.getSeconds());
			time = new Timestamp(date.getTime());
			return time;
		}
		catch (Exception e)
		{
			System.out.println("DateUtil.str2dateTime(str) Error:e = " + e);
			return null;
		}
	}

	/**
	 * 将字符串日期转化为util日期
	 *
	 * @param str
	 * @return
	 */
	public static java.util.Date str2utilDate(String str)
	{
		try
		{
			java.util.Date udate = yyyyMMdd.parse(str);
			return udate;
		}
		catch (Exception e)
		{
			System.out.println("DateUtil.str2utilDate(str) Error:e = " + e);
			return null;
		}
	}

	/**
	 * 通过字符中日期得到该日期是当前星期中的星期几
	 *
	 * @param curday
	 * @return
	 */
	public static int getCurWeekDayByStr(String curday)
	{
		try
		{
			java.util.Date date = str2utilDate(curday);
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(date);
			int week = rightNow.get(Calendar.DAY_OF_WEEK);
			return week;
		}
		catch (Exception e)
		{
			System.out.println("DateUtil.getYearWeek(str) Error:e = " + e);
			return 1;
		}
	}

	/**
	 * 通过字符串日期得到下个星期一的日期
	 *
	 * @param curday
	 * @return
	 */
	public static String getNexWeekDayByStr(String curday)
	{
		try
		{
			java.util.Date date = str2utilDate(curday);
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(date);
			int week = rightNow.get(Calendar.DAY_OF_WEEK);
			if (week == 7) week = 6;
			if (week == 1) week = 0;
			return getDateStr(new java.util.Date(date.getTime() + (7 - week) * 24 * 3600 * 1000));
		}
		catch (Exception e)
		{
			System.out.println("DateUtil.getYearWeek(str) Error:e = " + e);
			return "";
		}
	}

	/**
	 * 通过字符串日期得到上个星期一的日期
	 *
	 * @param curday
	 * @return
	 */
	public static String getPreWeekDayByStr(String curday)
	{
		try
		{
			java.util.Date date = str2utilDate(curday);
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(date);
			int week = rightNow.get(Calendar.DAY_OF_WEEK);
			if (week == 0) week = 1;
			if (week == 7) week = 8;
			return getDateStr(new java.util.Date(date.getTime() - week * 24 * 3600 * 1000));
		}
		catch (Exception e)
		{
			System.out.println("DateUtil.getYearWeek(str) Error:e = " + e);
			return "";
		}
	}

	/**
	 * 通过字符串日期得到它是这个月中的第几个星期
	 *
	 * @param curday
	 * @return
	 */
	public static int getMonthWeek(String curday)
	{
		try
		{
			java.util.Date date = str2utilDate(curday);
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(date);
			return rightNow.get(Calendar.WEEK_OF_MONTH);
		}
		catch (Exception e)
		{
			System.out.println("DateUtil.getYearWeek(str) Error:e = " + e);
			return 0;
		}
	}

	/**
	 * 通过字符串日期得到它是一年中的第多少个星期
	 *
	 * @param curday
	 * @return
	 */
	public static int getYearWeek(String curday)
	{
		try
		{
			java.util.Date date = str2utilDate(curday);
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(date);
			return rightNow.get(Calendar.WEEK_OF_YEAR);
		}
		catch (Exception e)
		{
			System.out.println("DateUtil.getYearWeek(str) Error:e = " + e);
			return 0;
		}
	}

	/**
	 * 通过字符串日期得到它的年月
	 *
	 * @param curday
	 * @return
	 */
	public static String getCurMonthDayStr(String curday)
	{
		int year = getYear(curday);
		String monthStr = getMonth(curday);
		int month = Integer.parseInt(monthStr);
		monthStr = String.valueOf(month);
		if (monthStr.length() == 1) monthStr = "0" + monthStr;
		return year + "-" + monthStr + "-01";
	}

	/**
	 * 通过字符串日期得到上个月的日期的年月
	 *
	 * @param curday
	 * @return
	 */
	public static String getPreMonthDayStr(String curday)
	{
		int year = getYear(curday);
		String monthStr = getMonth(curday);
		int month = Integer.parseInt(monthStr);
		if (month <= 1)
		{
			month = 12;
			year = year - 1;
		}
		else
			month = month - 1;
		monthStr = String.valueOf(month);
		if (monthStr.length() == 1) monthStr = "0" + monthStr;
		return year + "-" + monthStr + "-01";

	}

	/**
	 * 通过字符串日期得到下个月的日期的年月
	 *
	 * @param curday
	 * @return
	 */
	public static String getNextMonthDayStr(String curday)
	{
		int year = getYear(curday);
		String monthStr = getMonth(curday);
		int month = Integer.parseInt(monthStr);
		if (month >= 12)
		{
			month = 1;
			year = year + 1;
		}
		else
			month = month + 1;
		monthStr = String.valueOf(month);
		if (monthStr.length() == 1) monthStr = "0" + monthStr;
		return year + "-" + monthStr + "-01";
	}

	/**
	 * 通过字符串日期得到上一天的日期
	 *
	 * @param curday
	 * @return
	 */
	public static String getPreDayStr(String curday)
	{
		java.util.Date date = str2utilDate(curday);
		return date2str(new Date(date.getTime() - 24 * 3600 * 1000));

	}

	/**
	 * 通过字符串日期得到下一天的日期
	 *
	 * @param curday
	 * @return
	 */
	public static String getNextDayStr(String curday)
	{
		java.util.Date date = str2utilDate(curday);
		return date2str(new Date(date.getTime() + 24 * 3600 * 1000));
	}

	/**
	 * 通过字符串日期得到当前星期的所有日期
	 *
	 * @param curday
	 * @return
	 */
	public static String[] getallweekdate(String curday)
	{
		String pandy[] = new String[7];
		java.util.Date date = str2utilDate(curday);
		int day = date.getDay();
		String firstday = date2str(new Date(date.getTime() - day * 24 * 3600 * 1000));
		for (int i = 0; i < 7; i++)
		{
			pandy[i] = firstday;
			firstday = getNextDayStr(firstday);
		}
		return pandy;
	}

	/**
	 * 通过开始日期和结束日期得到其之间的所有星期的第一天和最后一天的日期
	 *
	 * @param startdate
	 * @param enddate
	 * @return
	 */
	public static String[][] getweekfl(String startdate, String enddate)
	{
		java.util.Date date1 = str2utilDate(startdate);
		java.util.Date date2 = str2utilDate(enddate);
		long weekcount = (date2.getTime() - date1.getTime()) / (24 * 3600 * 1000 * 7) + 1;
		int weekc = (int) weekcount;
		if (getCurWeekDayByStr(startdate) > getCurWeekDayByStr(enddate))
		{
			weekc++;
		}
		String pandy[][] = new String[weekc][2];
		String tempdate = startdate;
		for (int i = 0; i < weekc; i++)
		{
			pandy[i][0] = getallweekdate(tempdate)[0];
			pandy[i][1] = getallweekdate(tempdate)[6];
			tempdate = getNextDayStr(pandy[i][1]);
		}
		return pandy;
	}

	/**
	 * 通过开始日期和结束日期得到之间所有月的第一天和最后一天的日期
	 *
	 * @param startdate
	 * @param enddate
	 * @return
	 */
	public static Vector getmonthfl(String startdate, String enddate)
	{
		Vector pandy = new Vector();
		String tempdate = getCurMonthDayStr(startdate);
		while (tempdate.compareTo(enddate) <= 0)
		{
			String pan[] = new String[2];
			pan[0] = tempdate;
			pan[1] = getPreDayStr(getNextMonthDayStr(tempdate));
			pandy.add(pan);
			tempdate = getNextMonthDayStr(tempdate);
		}
		return pandy;
	}

	/**
	 * 通过开始日期和结束日期得到它们之间的所有日期并用,分开
	 *
	 * @param startdate
	 * @param enddate
	 * @return
	 */
	public static String getMulday(String startdate, String enddate)
	{
		String pandy = "";
		String tempdate = startdate;
		while (tempdate.compareTo(enddate) <= 0)
		{
			pandy += tempdate + ",";
			tempdate = getNextDayStr(tempdate);
		}
		if (pandy.length() > 0)
			pandy = pandy.substring(0, pandy.length() - 1);
		return pandy;
	}

	/**
	 * 通过开始日期和结束日期得到这们之间所有星期的第一天和最后一天并用,分开
	 *
	 * @param startdate
	 * @param enddate
	 * @return
	 */
	public static String getMulweekday(String startdate, String enddate)
	{
		String pandy = "";
		String tempdate[][] = getweekfl(startdate, enddate);
		for (int i = 0; i < tempdate.length; i++)
		{
			pandy += tempdate[i][0] + "--" + tempdate[i][1] + ",";
		}
		if (pandy.length() > 0)
			pandy = pandy.substring(0, pandy.length() - 1);
		return pandy;
	}

	/**
	 * 通过开始日期和结束日期得到它们之间所有月的第一天和最后一天日期用,分开
	 *
	 * @param startdate
	 * @param enddate
	 * @return
	 */
	public static String getMulmonthday(String startdate, String enddate)
	{
		String pandy = "";
		Vector tempdate = getmonthfl(startdate, enddate);
		String temp[] = new String[2];
		for (int i = 0; i < tempdate.size(); i++)
		{
			temp = (String[]) tempdate.get(i);
			pandy += temp[0] + "--" + temp[1] + ",";

		}
		if (pandy.length() > 0)
			pandy = pandy.substring(0, pandy.length() - 1);
		return pandy;
	}

	/**
	 * 通过开始时间得到count后的日期
	 *
	 * @param startdate
	 * @param count
	 * @return
	 */
	public static String getAllowDay(String startdate, int count)
	{
		String pandy = startdate;
		for (int i = 0; i < count; i++)
		{
			pandy = getNextDayStr(pandy);
		}
		return pandy;
	}

	/**
	 * 通过开始时间得到count星期后的日期
	 *
	 * @param startdate
	 * @param count
	 * @return
	 */
	public static String getAllowWeek(String startdate, int count)
	{
		String pandy = startdate;
		for (int i = 0; i < count * 7; i++)
		{
			pandy = getNextDayStr(pandy);
		}
		return pandy;
	}

	/**
	 * 通过开始时间得到count月后的日期
	 *
	 * @param startdate
	 * @param count
	 * @return
	 */
	public static String getAllowMonth(String startdate, int count)
	{
		String tempdate = getCurMonthDayStr(startdate);
		for (int i = 0; i < count; i++)
		{
			tempdate = getNextMonthDayStr(tempdate);
		}
		return tempdate;
	}
}
