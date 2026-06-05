package model;

import java.time.LocalDateTime;

import beans.DateTimeBean;

public class TodaysDateTime {
	//method
	public DateTimeBean getDayTime() {
		String date = "",time = "";
		LocalDateTime localDateTime = LocalDateTime.now();
		try {
			date = 
					Integer.toString(localDateTime.getYear()) + "-" + 
					Integer.toString(localDateTime.getMonthValue()) + "-" + 
					Integer.toString(localDateTime.getDayOfMonth());
			time =
					Integer.toString(localDateTime.getHour()) + ":" +
					Integer.toString(localDateTime.getMinute()) + ":" +
					Integer.toString(localDateTime.getSecond());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new DateTimeBean(date, time);
				
	}
}
