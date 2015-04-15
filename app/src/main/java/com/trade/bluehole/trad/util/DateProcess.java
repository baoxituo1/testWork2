package com.trade.bluehole.trad.util;



import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateProcess {

    public static String getFormatDate(String formatStr){
        DateFormat df = new SimpleDateFormat(formatStr);
		String date = df.format(Calendar.getInstance().getTime());
		return date;
    }

    public static String getUploadFileDate(){
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String date = df.format(Calendar.getInstance().getTime());
		return date; 
    }
	/**
	 * 得到当前系统时间yyyy-MM-dd hh:mm:ss字符串
	 * 
	 * @return
	 */
	public static String getSysTime() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = df.format(Calendar.getInstance().getTime());
		return date;
	}

	/**
	 * 得到当前系统时间yyyy-MM-dd hh:mm:ss字符串
	 * 
	 * @return Date
	 */
	public static Date getSysTimeDate() {
		return Calendar.getInstance().getTime();
	}
	/**
	 * 得到当前系统时间yyyy-MM-dd hh:mm:ss.S字符串
	 * 
	 * @return
	 */
	public static String getSysMilTime() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		String date = df.format(Calendar.getInstance().getTime());
		return date;
	}

	/**
	 * 得到当前系统时间yyyy-MM-dd字符串
	 * 
	 * @return
	 */
	public static String getSysDate() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(Calendar.getInstance().getTime());
		return date;
	}
	
	/**
	 * 根据格式得到系统日期
	 * @param format
	 * @return
	 */
	public static String getSysDate(String format) {
		DateFormat df = new SimpleDateFormat(format);
		String date = df.format(Calendar.getInstance().getTime());
		return date;
	}

	/**
	 * 得到当前系统时间yyyyMMdd字符串
	 * 
	 * @return
	 */
	public static String getDate() {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String date = df.format(Calendar.getInstance().getTime());
		return date;
	}

	public static String getNum() {
		DateFormat df = new SimpleDateFormat("HH:mm:ss.S");
		String date = df.format(Calendar.getInstance().getTime());
		return date;
	}

	/**
	 * 计算当前日期前几天或是后几天
	 */
	public static String afterNDay(int n) {
		Calendar c = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		c.setTime(new Date());
		c.add(Calendar.DATE, n);
		Date d2 = c.getTime();
		String s = df.format(d2);

		return s;
	}

	/**
	 * 计算当前日期前几月或是后几月
	 */
	public static String afterMon(int n) {
		Calendar c = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy-MM");
		c.setTime(new Date());
		c.add(Calendar.MONTH, n);
		Date d2 = c.getTime();
		String s = df.format(d2);

		return s;
	}

    /**
     * 计算某一天的下一天
     * @param d
     * @return
     */
	public static Date afterDay(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, 1);
		return c.getTime();
	}

	/**
	 * 计算两天的时间差
	 * @param beforeTime
	 * @param endTime
	 * @return
	 */
	public static String timeDiffrence(String beforeTime,String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;
		try {
			d = sdf.parse(beforeTime);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		Date d1 = null;
		try {
			d1 = sdf.parse(endTime);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		long timethis = calendar.getTimeInMillis();
		calendar.setTime(d1);
		long timeend = calendar.getTimeInMillis();
		long theday = (timeend - timethis) / (1000 * 60 * 60 * 24);
		return theday+"";
	}


	/**
	 * 计算两天的天数差
	 * @param beforeTime
	 * @param endTime
	 * @return
	 */
	public static long timeDiffrenceDate(String beforeTime,String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		try {
			d = sdf.parse(beforeTime);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		Date d1 = null;
		try {
			d1 = sdf.parse(endTime);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		long timethis = calendar.getTimeInMillis();
		calendar.setTime(d1);
		long timeend = calendar.getTimeInMillis();
		long theday = (timeend - timethis) / (1000 * 60 * 60 * 24);
		return theday;
	}

	/**
	 * 计算两天的时间差
	 * @param beforeTime
	 * @param endTime
	 * @return
	 */
	public static long timeDiffrenceInt(String beforeTime,String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;
		try {
			d = sdf.parse(beforeTime);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		Date d1 = null;
		try {
			d1 = sdf.parse(endTime);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		long timethis = calendar.getTimeInMillis();
		calendar.setTime(d1);
		long timeend = calendar.getTimeInMillis();
		long theday = (timeend - timethis) / (1000 * 60 * 60 * 24);
		return theday;
	}
	/**
	 * 计算两天的时间差 秒
	 * @param beforeTime
	 * @param endTime
	 * @return
	 */
	public static long timeDiffrenceSec(String beforeTime,String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;
		try {
			d = sdf.parse(beforeTime);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		Date d1 = null;
		try {
			d1 = sdf.parse(endTime);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		long timethis = calendar.getTimeInMillis();
		calendar.setTime(d1);
		long timeend = calendar.getTimeInMillis();
		long theday = (timeend - timethis) / (1000);
		return theday;
	}

    public static int compareTwoDate(String beforeTime,String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;
		try {
			d = sdf.parse(beforeTime);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		Date d1 = null;
		try {
			d1 = sdf.parse(endTime);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		long timethis = calendar.getTimeInMillis();
		calendar.setTime(d1);
		long timeend = calendar.getTimeInMillis();
		long theday = (timeend - timethis) / (1000);
		if(theday > 0){
            return -1;
        }else if(theday < 0){
            return 1;
        }else{
            return 0;
        }
	}

    /**
     * 按照 固定格式转化字符串类型为日期类型
     * @param date   字符串日期
     * @param formate   日期格式
     * @return
     */
    public static Date StringToDate(String date,String formate){
        SimpleDateFormat sdf = new SimpleDateFormat(formate);
		Date d = null;
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {

			e.printStackTrace();
		}
        return d;
    }
    
    /**
     * 按照 固定格式转化字符串类型为时间戳
     * @param date   字符串日期
     * @param
     * @return
     */
    public static Timestamp StringToTimeStamp(String date) throws ParseException{
        Timestamp d = null;
		d = Timestamp.valueOf(date);
        return d;
    }
    
    /**
     * 转换成日期字符串
     * @param time
     * @return
     */
    public static String longToDateString(long time){
    	Date date = new Date(time);
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = null;
		str = sdf.format(date);
		return str;
    }

   /* 
     * 比较两个年份的大小，
     * @param firstTime
     * @param secondTime
     * @return
     
    public static int compareTwoYear(String firstTime,String secondTime){
        if(StringTool.isEmpty(firstTime) || StringTool.isEmpty(secondTime)){
            return -2;
        }
        String firstYear = firstTime.substring(0,4);
        String secondYear = secondTime.substring(0,4);
        if(Integer.parseInt(firstYear) == Integer.parseInt(secondYear)){
            return 0;
        }else if(Integer.parseInt(firstYear) > Integer.parseInt(secondYear)){
            return 1;
        }else if(Integer.parseInt(firstYear) < Integer.parseInt(secondYear)){
            return -1;
        }else{
            return -2;
        }
    }*/
    public static String getCurrentOrderDate(){
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String date = df.format(Calendar.getInstance().getTime());
		return date; 
    }
    public static void main(String[] arg){
    	long var = DateProcess.timeDiffrenceSec("2011-07-05 12:00:00", "2011-07-05 12:02:11");
    	System.out.println(var);
    }

}

