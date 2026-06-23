package Test;

import model.TodaysDateTime;

public class TodaysDateTimeTest {
	public static void main(String[] args) {
		TodaysDateTime time = new TodaysDateTime();
		System.out.println(time.getNowUtc());
	}
}
