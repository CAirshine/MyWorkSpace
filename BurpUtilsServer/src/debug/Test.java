package debug;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

	public static void main(String[] args) {
		
		SimpleDateFormat dataFormat = new SimpleDateFormat("[yyyy-mm-dd HH:MM:ss:sss]");
		System.out.println(dataFormat.format(new Date()));
	}
}
