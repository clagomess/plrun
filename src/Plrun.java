import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;


public class Plrun implements Runnable {
	private String user;
	private String pass;
	private String sid;
	private String file;
	private int idx;
	private JProgressBar progresso;
	
	boolean modo_teste = false;
	JTextArea log;
	
	public Plrun(String user, String pass, String sid, String file, boolean modo_teste, JTextArea log, JProgressBar progresso, int idx) {
		super();
		this.user = user;
		this.pass = pass;
		this.sid = sid;
		this.file = file;
		this.modo_teste = modo_teste;
		this.log = log;
		this.idx = idx;
		this.progresso = progresso;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@SuppressWarnings("resource")
	private void check_terminator(){
		String terminador = "\r\n\r\n\r\n";
		terminador += "-- ESSA SELECT E APENAS UM WORKAROUND PARA TERMINAR O PROCESSO DO SQLPLUS\r\n";
		terminador += "SELECT 'PLRUN-FIM' AS MENSAGEM_DE_TERMINO FROM DUAL;";
		
		try {			
			BufferedReader sql;
			sql = new BufferedReader(new FileReader(this.getFile()));
			
			Boolean has_terminator = false;
			String linha = "";
			
			while ((linha = sql.readLine()) != null) {
				if(linha.contains("PLRUN-FIM")){
					has_terminator = true;
					break;
				}
			}
			
			if(!has_terminator){
				FileWriter fwrite = new FileWriter(new File(this.getFile()), true);
				fwrite.write(terminador);
				fwrite.flush();
				fwrite.close();
				
				String sqlfile = "";
				sql = new BufferedReader(new FileReader(this.getFile()));
				while ((linha = sql.readLine()) != null) {
					sqlfile += linha + "\r\n";
				}
				
				sqlfile = sqlfile.replace("END;", "END;\r\n/\r\n");
				sqlfile = sqlfile.replace("ï»¿", "");
				
				FileWriter fwrite2 = new FileWriter(new File(this.getFile()));
				fwrite2.write(sqlfile);
				fwrite2.flush();
				fwrite2.close();
			}
			
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			String line = "";
			ProcessBuilder pb = null;
			
			if(Main.toreturn){
				if(modo_teste){
					pb = new ProcessBuilder("sqlplus", this.getUser() + "/" + this.getPass() + "@" + this.getSid(), "@PLRUN-teste.sql");
				}else{
					check_terminator();
					
					log.append("##################################\n");
					log.append("#### Hora Inicio: " + Main.gettime() + "\n");
					log.append("#### " + (this.idx + 1) + "/" + Main.t_len + " -> " + this.getFile() + "\n");
					
					pb = new ProcessBuilder("sqlplus", this.getUser() + "/" + this.getPass() + "@" + this.getSid(), "@" + this.getFile());
				}
				
				Process p = pb.start();
				
				BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
				
				while ((line = bri.readLine()) != null) {
					if(line.length() > 0){
						//System.out.println(line);
						log.append(line + "\n");
					}
					
					if(line.contains("PLRUN-FIM") || line.contains("PLRUN-teste.sql")){
						bri.close();
						break;
					}
					
					if(line.contains("ORA-")){
						bri.close();
						Main.toreturn = false;
						break;
					}
				}
				
				log.append("OK\n");
				log.append("#### Hora Fim: " + Main.gettime() + "\n");
				log.append("##################################\n");
				progresso.setValue((int) Math.ceil(((this.idx+1) * 100) / Main.t_len));
				
				if((this.idx + 1) <  Main.arThread.length){
					Main.arThread[(this.idx + 1)].start();
				}else{
					Main.start_timestamp = 0;
					if(Main.toreturn){
						JOptionPane.showMessageDialog(null, "Operação Realizada Com Sucesso!");
					}
					progresso.setValue(0);
				}
				
				if(!Main.toreturn){
					Main.start_timestamp = 0;
					JOptionPane.showMessageDialog(null, "Deu Bode no script: " + this.getFile());
					progresso.setValue(0);
				}
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
}
