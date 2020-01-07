package NetworkToolkit;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class VersionManagement {

	private final static String NETWORK_TOOLKIT_VERSION = "0.1.1";
	
	private final JFrame f = new JFrame();
	
	public VersionManagement() {
		
	}
	
	public boolean isLatestVersion() {
		String latestVersion = getLatestVersionString();
		
		if( ! NETWORK_TOOLKIT_VERSION.equals(latestVersion)) {
			System.out.println("Diese Version ist nicht aktuell, bitte lade zuerst die aktuellste Version herunter");
			System.out.println("Deine Version: " + NETWORK_TOOLKIT_VERSION);
			System.out.println("Aktuellste Version: " + latestVersion);
			showUpdateFrame(latestVersion);
			return false;
		}
		
		return true;
	}

	private void showUpdateFrame(final String latestVersion) {

		f.setVisible(true);
		f.setResizable(false);
		f.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/com/sun/java/swing/plaf/windows/icons/Computer.gif")));
		f.setTitle("NetworkToolkit by Myrinia@LTEForum.at");
		f.getContentPane().setBackground(new Color(255, 255, 255));
		f.setBounds(100, 100, 500, 300);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().setLayout(null);
		
		JTextArea lblWantToUpgrade = new JTextArea();
		lblWantToUpgrade.setText("Bitte aktualisieren Sie diese Version des NetworkToolkits. \nAktuell verwenden Sie "+NETWORK_TOOLKIT_VERSION+"\nDie neueste Version ist: "+latestVersion);
		
		lblWantToUpgrade.setBounds(10,10,450,100);
		
		
		ActionListener updateListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateApplication();
			}

			private void updateApplication() {
				// Download Latest
				
				try {
					URL url = new URL("https://github.com/Myrinia/NetworkToolkit/releases/download/"+ latestVersion + "/NetworkToolkit_V"+ latestVersion +".zip");
					try (BufferedInputStream in = new BufferedInputStream(url.openStream());
							
							  FileOutputStream fileOutputStream = new FileOutputStream("Latest.zip")) {
							    byte dataBuffer[] = new byte[1024];
							    int bytesRead;
							    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
							    	fileOutputStream.write(dataBuffer, 0, bytesRead);
							    }
							    
							    fileOutputStream.flush();
							    fileOutputStream.close();
							    
							    ZipFileHandler z = new ZipFileHandler();
							    z.unzip("Latest.zip",latestVersion);
							    
							    File s = new File("Latest.zip");
							    s.delete();
							    
							    JFrame ok = new JFrame();
							    ok.setVisible(true);
							    ok.setResizable(false);
							    
								JTextArea lblAllOK = new JTextArea();
								lblAllOK.setText("Update erfolgreich, bitte überprüfen Sie den Ordner: "+ latestVersion +".");
								lblAllOK.setBounds(10,10,450,100);
								
								ok.add(lblAllOK);
								ok.setBounds(100, 100, 500, 300);
								ok.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
								ok.getContentPane().setLayout(null);
								
								f.setVisible(false);
								
								ActionListener closeListener = new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										System.exit(0);
									};
								};
								
								JButton closeApplication = new JButton("Beenden.");
								closeApplication.addActionListener(closeListener);
								closeApplication.setBounds(250,150,150,50);
								ok.add(closeApplication);
								ok.add(lblAllOK);
								
							} catch (IOException e) {
								System.out.println("Download Error");
								e.printStackTrace();
							}
				} catch (MalformedURLException e) {
					System.out.println("Update failed!");
				}
			};
		};
		
		JButton doUpdate = new JButton("Jetzt updaten.");
		doUpdate.setBounds(10, 150, 150, 50);
		doUpdate.addActionListener(updateListener);
		
		ActionListener closeListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			};
		};
		
		JButton doAbortUpdate = new JButton("Jetzt nicht.");
		doAbortUpdate.addActionListener(closeListener);
		doAbortUpdate.setBounds(250,150,150,50);
		
		f.add(doAbortUpdate);
		f.add(doUpdate);
		f.add(lblWantToUpgrade);
	}

	private String getLatestVersionString() {
	    try {
        	URL url = new URL("https://raw.githubusercontent.com/Myrinia/NetworkToolkit/master/version");
        
        	BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString(); 
        }catch(Exception e) {
        	return NETWORK_TOOLKIT_VERSION;
        }
	}
}
