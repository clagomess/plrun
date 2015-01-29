import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.UIManager;

public class Main {
	// Variaveis para controle de threads
	public static boolean toreturn = true;
	public static int t_len = 0;
	public static Thread[] arThread;
	public static int start_timestamp = 0;
	public static Thread relogio;
	
	public static void main(String[] args) {
		try{
			UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){
			System.out.print(e.getMessage());
		}
		
		Interface ui = new Interface();
		ui.home();
	}
	
	public static String gettime(){
		GregorianCalendar calendar = new GregorianCalendar();		
		return calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
	}
}
