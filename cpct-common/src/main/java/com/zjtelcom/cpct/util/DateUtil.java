package com.zjtelcom.cpct.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd";
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);


    /**
     * 获取上一个月的月份
     *
     * @return
     */
    public static Integer getLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MONTH, -1);
        SimpleDateFormat dft = new SimpleDateFormat("MM");
        Integer lastMonth = Integer.valueOf(dft.format(cal.getTime()));
        return lastMonth;
    }

    /**
     * 获取上一个月的年份
     *
     * @return
     */
    public static Integer getLastMonthYear() {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MONTH, -1);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy");
        Integer lastYear = Integer.valueOf(dft.format(cal.getTime()));
        return lastYear;
    }

    /**
     * 获取某月的第一天
     *
     * @param month
     * @return
     */
    public static String getFirstDayOfMonth(int month) {

        Calendar cal = Calendar.getInstance();
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }

    /**
     * 获取某月下月的第一天
     *
     * @param month
     * @return
     */
    public static String getFirstDayOfNextMonth(int month) {
        Calendar cal = Calendar.getInstance();
        //设置月份
        cal.set(Calendar.MONTH, month);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }

    /**
     * 获取某月的最后一天
     *
     * @param month
     * @return
     */
    public static String getLastDayOfMonth(int month) {
        Calendar cal = Calendar.getInstance();
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String lastDayOfMonth = sdf.format(cal.getTime());
        return lastDayOfMonth;
    }

    /**
     * @Param nowTime 当前时间
     * @Param beforeTime 对比时间
     * @Return -1 超过10分钟，1没超过
     */
    public static int compareSmsTime(Long beforeTime) {
        final long time = 60 * 1000 * 10;
        Calendar cal = Calendar.getInstance();
        Long nowTime = cal.getTimeInMillis();
        long difference = nowTime - beforeTime;
        if (difference > time) {
            return -1;
        }
        return 1;
    }

    /**
     * @Param nowTime 当前时间
     * @Param beforeTime 对比时间
     * @Return -1 超过10分钟，1没超过
     */
    public static int compareSmsTime(Long nowTime, Long beforeTime) {
        final long time = 60 * 1000 * 10;
        System.out.println("nowTime:" + nowTime);
        System.out.println("beforeTime:" + beforeTime);
        long difference = nowTime - beforeTime;
        System.out.println("difference:" + difference);
        System.out.println("time:" + time);
        if (difference > time) {
            return -1;
        }
        return 1;
    }



    public static String getDateFormatStr(Date date) {
        if (date == null) {
            return new SimpleDateFormat(DEFAULT_FORMAT).format(new Date());
        }
        return new SimpleDateFormat(DEFAULT_FORMAT).format(date);
    }

    /**
     * 日期转字符串
     *
     * @param date   日期
     * @param format 格式
     * @return 格式化后的字符串
     */
    public static String getDateFormatStr(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 字符串转格式化日期
     *
     * @param dateStr 日期字符串
     * @param format  格式
     * @return 日期
     */
    public static Date parseDate(String dateStr, String format) {
        try {
            return new SimpleDateFormat(format).parse(dateStr);
        } catch (final ParseException e) {
            logger.error("", e);
            return null;
        }
    }

    /**
     * @return
     * @Description 获取网络时间
     */
    public static Date getWebsiteDatetime() {
        try {
            URL url = new URL("http://www.baidu.com");// 取得资源对象
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.connect();// 发出连接
            long ld = uc.getDate();// 读取网站日期时间
            Date date = new Date(ld);// 转换为标准时间对象
            return date;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前系统时间
     *
     * @return Long 毫秒值
     */
    public static Date getCurrentTime() {
//        try {
//            return DateUtil.getMilliSecondByStr("2018年7月1日");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
        return new Date();
    }

    public static String getDetailTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String detailTime = sdf.format(new Date());
        return detailTime;
    }

    /**
     * 上月有多少天
     *
     * @return 上月天数
     */
    public static Integer getDaysOfMonth() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.MONTH, -1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前年份
     *
     * @return
     */
    public static Integer getCurrentYear() {
        Calendar cale = null;
        cale = Calendar.getInstance();
        return cale.get(Calendar.YEAR);
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public static Integer getCurrentMonth() {
        Calendar cale = null;
        cale = Calendar.getInstance();
        return cale.get(Calendar.MONTH) + 1;
    }

    /**
     * 日期转星期
     *
     * @param datetime
     * @return
     */
    public static String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 日期转星期
     *
     * @param datetime
     * @return
     */
    public static int dateToWeek(Date datetime) {
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        cal.setTime(datetime);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return w;
    }

    /**
     * 获取当前日期是星期一到星期五的工作日还是星期六到星期日的休息日
     * 如果是工作日的话，返回1，如果是休息日的话返回2
     *
     * @param date
     * @return
     */
    public static Integer getWorkOrWeekDay(String date) {
        String wk = dateToWeek(date);
        if (wk.contains("六") || wk.contains("日")) {
            return 2;
        } else {
            return 1;
        }
    }

    public static Date getPreviousDate(Date startTime, Integer days) {
        Long time = startTime.getTime();
        days = days * 24 * 3600 * 1000;
        time -= days;
        return new Date(time);
    }

    /**
     * 传入date类型返回年份
     *
     * @param date
     * @return
     */
    public static Integer getYearByDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 传入date类型返回月份
     *
     * @param date
     * @return
     */
    public static Integer getMonthByDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 传入date类型返回天
     *
     * @param date
     * @return
     */
    public static Integer getDayByDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取mub个月前的日期
     *
     * @param mub
     * @return
     */
    public static Date getMonthDate(int mub) {
        Date dNow = new Date();
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dNow);//把当前时间赋给日历
        calendar.add(Calendar.MONTH, -mub);  //设置为前3月
        dBefore = calendar.getTime();   //得到前3月的时间
        return dBefore;
    }

    /**
     * 获取当前天
     *
     * @return
     */
    public static Integer getCurrentDay() {
        Calendar cale = null;
        cale = Calendar.getInstance();
        return cale.get(Calendar.DATE);
    }

    /**
     * 通过传入日期返回当前月份有多少天
     *
     * @param date 传入日期
     * @return
     */
    public static Integer getDaysOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 描述:获取下一个月的第一天
     *
     * @return
     */
    public static String getPerFirstDayOfMonth() {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return dft.format(calendar.getTime());
    }

    /**
     * 描述:获取下下一个月的第一天
     *
     * @return
     */
    public static String getNxvFirstDayOfMonth() {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 2);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return dft.format(calendar.getTime());
    }

    /**
     * 获取下个月的最后一天
     *
     * @return
     */
    public static String getPerLastDayOfMonth() {
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return dft.format(calendar.getTime());
    }

    /**
     * 获取某月的第一天
     * @param year
     * @param month
     * @return
     */
    public static String getFisrtDayOfMonth(int year,int month)
    {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR,year);
        //设置月份
        cal.set(Calendar.MONTH, month-1);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }

    /**
     * 获取本月最后一天
     *
     * @return
     */
    public static Date getLastDayOfMonth() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDay = ca.getTime();
        return lastDay;
    }

    /*
     * 将时间转换为时间戳
     */
    public static Long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = simpleDateFormat.parse(s);
        Long ts = date.getTime();
        return ts;
    }
    //将年月日时间转化为时间戳
    public static Long TimeToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
        Date date = simpleDateFormat.parse(s);
        Long ts = date.getTime();
        return ts;
    }

    /**
     * 获取两个日期之间的日期
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 日期集合
     */
    public static List<Date> getBetweenDates(Date start, Date end) {
        List<Date> result = new ArrayList<Date>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.add(Calendar.DAY_OF_YEAR, 1);
        tempStart.setTime(start);
        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        while (tempStart.before(tempEnd)) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    /**
     * 字符转日期
     *
     * @return
     */
    public static Date string2DateTime(String stringDate) {
        if (stringDate != null && !stringDate.equals("")) {
            try {
                Date date = (new SimpleDateFormat("yyyy-MM-dd HH")).parse(stringDate);
                return date;
            } catch (ParseException var3) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 字符转日期
     *
     * @return
     */
    public static Date string2DateTime4Minute(String stringDate) {
        if (stringDate != null && !stringDate.equals("")) {
            try {
                Date date = (new SimpleDateFormat("yyyy-MM-dd")).parse(stringDate);
                return date;
            } catch (ParseException var3) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 字符转日期
     *
     * @return
     */
    public static Date string2DateTime4Day(String stringDate) {
        if (stringDate != null && !stringDate.equals("")) {
            try {
                Date date = (new SimpleDateFormat("yyyy-MM-dd")).parse(stringDate);
                return date;
            } catch (ParseException var3) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 当日开始时间
     *
     * @return
     */
    public static Date getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 当日6:00时间
     *
     * @return
     */
    public static Date getTimeSix() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 6);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 当日结束时间
     *
     * @return
     */
    public static Date getnowEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    /**
     * 获取当前时间前五分钟毫秒数
     *
     * @return
     */
    public static Long getFiveMinAgo() {
        Date beforeDate = new Date(new Date().getTime() - 60000);
        return beforeDate.getTime();
    }

    /**
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return days + " 天 " + hours + " 小时 " + minutes + " 分钟 "
                + seconds + " 秒 ";
    }

    public static String date2String(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String result = sdf.format(date);
        return result;
    }

    public static String date2StringDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = sdf.format(date);
        return result;
    }

    public static String Date2String(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String result = sdf.format(date);
        return result;
    }

    /**
     * 获取上个月第一天
     *
     * @return
     */
    public static String getFirstDayOfLastM() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取前一个月第一天
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.MONTH, -1);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        String firstDay = sdf.format(calendar1.getTime());
        return firstDay;
    }

    /**
     * 获取上个月最后一天
     *
     * @return
     */
    public static String getLastDayOfLastM() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取前一个月第一天
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.DAY_OF_MONTH, 0);
        String lastDay = sdf.format(calendar2.getTime());
        return lastDay;
    }

    /**
     * 获取本月第一天
     *
     * @return
     */
    public static String getCurMFirstDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String first = sdf.format(c.getTime());
        return first;
    }

    /**
     * 获取本月最后一天
     *
     * @return
     */
    public static String getCurMLastDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String last = sdf.format(ca.getTime());
        return last;
    }

    /**
     * 通过传入 2018年3月2日返回毫秒值
     * eg input 2018年3月2日  output  1519920000000
     */
    public static Long getMilliSecondByStr(String str) throws ParseException {
        Date date = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            date = sdf.parse(str);
        } catch (ParseException e) {
            logger.error("[op:DateUtil] fail to getMilliSecondByStr!, Exception: ", e);
        }
        return date.getTime();
    }

    public static String getNowDate(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String v = format.format(new Date());
        return v;
    }


    /**
     * date转数字格式时间
     * @param time
     * @return
     */
    public static String getDatetime(Date time){
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMddHHmmss");
        return  sf.format(time);
    }


    /**
     * 时间戳转数字格式时间
     * @param time
     * @return
     */
    public static String TimeStampToNum(Long time){
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMddHHmmss");
        return  sf.format(time);
    }


    /**
     *以yyyyMMdd格式获取当前时间
     * <p>
     *
     * @return
     */
    public static String getNowTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String timeStamp = sdf.format(new Date());
        return timeStamp;
    }




}
