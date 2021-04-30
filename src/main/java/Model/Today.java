package Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Today {
	public String today;
	public String dayweek;
	public int minute;
	public int todayweek;
	public Today() {
		Calendar time = Calendar.getInstance();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		today = format.format(time.getTime());
		int todayweek = time.get(Calendar.DAY_OF_WEEK);
		this.dayweek = getDayWeek(todayweek);
		String[] times = today.split(" ");
		minute = changeTime(times[1]);
		System.out.println(minute);
		today=times[0];
	}
	public int changeTime(String time) {
		String[] times = time.split(":");
		int minute = Integer.parseInt(times[1]);
		int hour = Integer.parseInt(times[0]);
		return hour * 60 + minute;
	}
	public String getDayWeek(int dayofweek) {
		if(dayofweek == 1) return "sunday";
		else if(dayofweek == 2) return "monday";
		else if(dayofweek == 3) return "tuesday";
		else if(dayofweek == 4) return "wednesday";
		else if(dayofweek == 5) return "thursday";
		else if(dayofweek == 6) return "friday";
		else return "saturday";
	}
	public String getNextDate(String date) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		Date d = format.parse(date);
		
		c.setTime(d);
		c.add(Calendar.DATE, 1);
		date = format.format(c.getTime());
		today = date;
		return today;
	}
	
}
