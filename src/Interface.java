import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Interface {
	public static String lastfolder = "";
	private String[] arScripts = new String[0];
	
	public static void alert(String msg){
		JOptionPane.showMessageDialog(null, msg);
		System.out.println(msg);
	}
	
	private JFrame getJanela(int w, int h){
		JFrame janela = new JFrame();
		janela.setTitle("Plrun");
		janela.setSize(w,h);
		janela.setResizable(false);
		janela.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		janela.setLocationRelativeTo(null);
		
		return janela;
	}
	
	public void home(){
		JPanel panel;
		
		JFrame janela = this.getJanela(510, 700);
		janela.addWindowListener( new WindowAdapter( ){
			public void windowClosing(WindowEvent w){	
				System.exit(0);
			}
		});
		janela.setLayout(new FlowLayout(FlowLayout.LEFT,10,10));
		panel = new JPanel();
		panel.setLayout(new GridLayout(2,1,5,5));
		panel.setPreferredSize(new Dimension(480, 80));
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Parametros:"));
		
		final JTextField bUser = new JTextField("");
		final JTextField bSid = new JTextField(""); 
		final JPasswordField bPass = new JPasswordField("");
		JButton bTestar = new JButton("Testar Configurações");
		JButton bRun = new JButton("Rodar Script(s)");
		JButton bLog = new JButton("Limpar Log");
		JButton bScripts = new JButton("Adicionar");
		final JTextArea tlog = new JTextArea();
		
		panel.add(new JLabel("User"));
		panel.add(bUser);
		panel.add(new JLabel("Pass"));
		panel.add(bPass);
		panel.add(new JLabel("SID"));
		panel.add(bSid);
		panel.add(new JLabel("Selecionar PL/SQL"));
		panel.add(bScripts);
		janela.add(panel);
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.setPreferredSize(new Dimension(480, 40));
		panel.add(bTestar);
		panel.add(bLog);		
		janela.add(panel);
		
		final JPanel panelpl = new JPanel();
		panelpl.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		panelpl.setPreferredSize(new Dimension(480, 200));
		panelpl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "PL/SQL Selecionados:"));
		janela.add(panelpl);
		
		bScripts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				JFileChooser fScripts = new JFileChooser(Interface.lastfolder);
				fScripts.setFileFilter(new FileNameExtensionFilter("PL/SQL", "sql"));
				fScripts.setMultiSelectionEnabled(true);
				fScripts.showOpenDialog(null);
				
				int heighPanel = fScripts.getSelectedFiles().length * 20;
				
				if(fScripts.getSelectedFiles().length > 0){
					JPanel gridfile = new JPanel();
					gridfile.setLayout(new GridLayout(fScripts.getSelectedFiles().length,1));
					gridfile.setPreferredSize(new Dimension(460, heighPanel));
					
					Interface.lastfolder = fScripts.getSelectedFiles()[0].getAbsolutePath();
					
					arScripts = new String[fScripts.getSelectedFiles().length];
					
					for(int i = 0; i < fScripts.getSelectedFiles().length; i++){
						gridfile.add(new JLabel((i + 1) + " - " + fScripts.getSelectedFiles()[i].getName()));
						arScripts[i] = fScripts.getSelectedFiles()[i].getPath();
					}
					
					JScrollPane sGridefile = new JScrollPane(gridfile);
					sGridefile.setPreferredSize(new Dimension(470, 180));
					sGridefile.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					sGridefile.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					sGridefile.setBorder(BorderFactory.createEmptyBorder());
					
					panelpl.removeAll();
					panelpl.add(sGridefile);
					panelpl.revalidate();
					panelpl.repaint();
				}
			}
		});
		
		bTestar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread(new Plrun(bUser.getText(), new String(bPass.getPassword()), bSid.getText(), null, true, tlog, null, 0));
				t.start();
			}
		});
		
		bLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tlog.setText("");
			}
		});
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "Progresso:"));
		panel.setPreferredSize(new Dimension(480, 250));
		final JLabel lStatus = new JLabel("Parado");
		lStatus.setPreferredSize(new Dimension(470, 20));
		panel.add(lStatus);
		
		final JProgressBar progresso = new JProgressBar(0, 100);
		progresso.setPreferredSize(new Dimension(455, 20));
		panel.add(progresso);
		
		tlog.setFont(new Font("consolas", Font.PLAIN, 10));
		JScrollPane slog = new JScrollPane(tlog);
		slog.setPreferredSize(new Dimension(455, 170));
		slog.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		slog.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(slog);
		janela.add(panel);
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel.setPreferredSize(new Dimension(480, 30));
		panel.add(bRun);
		janela.add(panel);
		progresso.setStringPainted(false);
		
		bRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {if(arScripts.length == 0){
					JOptionPane.showMessageDialog(null, "Favor, Adicione PL/SQL");
				}else{
					int confirm = JOptionPane.showConfirmDialog(null, "Deseja realmente rodar os Scripts selecionados?");
					if(confirm == 0){
						Main.toreturn = true;
						Main.start_timestamp = (int) (System.currentTimeMillis() / 1000);
						Main.t_len = arScripts.length;
						Main.arThread = new Thread[Main.t_len];
						Main.relogio = new Thread(new Relogio(lStatus));
						
						progresso.setValue(0);
						lStatus.setText("Rodando os script: Fique de olho no LOG!");
						
						for(int i = 0; i < arScripts.length; i++){							
							Main.arThread[i] = new Thread(new Plrun(bUser.getText(), new String(bPass.getPassword()), bSid.getText(), arScripts[i], false, tlog, progresso, i));
						}
						
						Main.arThread[0].start();
						Main.relogio.start();
					}
				}
			}
		});
		
		janela.setVisible(true);
	}
}
