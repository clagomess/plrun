import javax.swing.JLabel;


public class Relogio implements Runnable {
	private JLabel status;
	
	public Relogio(JLabel status){
		this.status = status;
	}
	
	@Override
	public void run() {
		while(Main.start_timestamp != 0){
			try {
				status.setText(Relogio.get());
				
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String get(){
		int stime = (int) ((System.currentTimeMillis() / 1000) - Main.start_timestamp);
		
		int h = (int) Math.floor(stime / 3600);
		String m = String.format("%02d", (int) Math.floor((stime - (h * 3600)) / 60));
		String s = String.format("%02d", (int) Math.floor(stime % 60));
		
		return h + ":" + m + ":" + s;
	}
}
