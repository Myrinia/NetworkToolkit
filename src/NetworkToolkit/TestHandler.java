package NetworkToolkit;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class TestHandler {

	private JLabel m_CurrentActionHeadlineLabel;
	private JLabel m_CurrentActionLabel;
	private JLabel m_CurrentHostLabel;
	private JLabel m_CurrentInformations;
	private JLabel m_CurrentInformations2;
	
	public static boolean m_GlobalTestStatusRunning;
	private JProgressBar m_TotalProgressBar;
	private JProgressBar m_CurrentProgressBar;

	private BackgroundTestHandler m_BackgroundTestHandler;
	private Thread m_BackgroundTestHandlerThread;

	public TestHandler() {
		m_GlobalTestStatusRunning = false;
		m_BackgroundTestHandler = new BackgroundTestHandler();
	}

	public void initialize() {
		JLabel lblAktuelleAktionHeadline = new JLabel("Aktuelle Aktion");
		lblAktuelleAktionHeadline.setBounds(200, 10, 150, 25);
		Main.m_StatusPanel.add(lblAktuelleAktionHeadline);
		
		m_CurrentActionHeadlineLabel = new JLabel("Warte auf Start");
		m_CurrentActionHeadlineLabel.setBounds(360, 10, 150, 25);
		Main.m_StatusPanel.add(m_CurrentActionHeadlineLabel);
		
		m_TotalProgressBar = new JProgressBar();
		m_TotalProgressBar.setBounds(300, 35, 240, 35);
		Main.m_StatusPanel.add(m_TotalProgressBar);
		
		JLabel lblTeststatus = new JLabel("Teststatus:");
		lblTeststatus.setBounds(200, 45, 72, 25);
		Main.m_StatusPanel.add(lblTeststatus);

		JLabel lblAktuellerHost = new JLabel("Aktueller Host");
		lblAktuellerHost.setBounds(200, 70, 150, 25);
		Main.m_StatusPanel.add(lblAktuellerHost);
		
		m_CurrentHostLabel = new JLabel("Warte auf Start");
		m_CurrentHostLabel.setBounds(360, 70, 150, 25);
		Main.m_StatusPanel.add(m_CurrentHostLabel);
		
		JLabel lblAktuelleAktion1 = new JLabel("Aktuelle Aktion");
		lblAktuelleAktion1.setBounds(200, 95, 150, 25);
		Main.m_StatusPanel.add(lblAktuelleAktion1);
		
		m_CurrentActionLabel = new JLabel("Warte auf Start");
		m_CurrentActionLabel.setBounds(360, 95, 150, 25);
		Main.m_StatusPanel.add(m_CurrentActionLabel);

		JLabel lblAktuelleAktionStatus = new JLabel("Aktuelle Aktion");
		lblAktuelleAktionStatus.setBounds(200, 125, 150, 25);
		Main.m_StatusPanel.add(lblAktuelleAktionStatus);
		
		m_CurrentProgressBar = new JProgressBar();
		m_CurrentProgressBar.setBounds(300, 120, 240, 35);
		Main.m_StatusPanel.add(m_CurrentProgressBar);
		
		JLabel lblAktuelleInfos = new JLabel("Informationen 1:");
		lblAktuelleInfos.setBounds(200, 150, 150, 25);
		Main.m_StatusPanel.add(lblAktuelleInfos);
		
		m_CurrentInformations = new JLabel("Warte auf Start");
		m_CurrentInformations.setBounds(300, 150, 200, 25);
		Main.m_StatusPanel.add(m_CurrentInformations);
		
		JLabel lblAktuelleInfos2 = new JLabel("Informationen 2:");
		lblAktuelleInfos2.setBounds(200, 175, 150, 25);
		Main.m_StatusPanel.add(lblAktuelleInfos2);
		
		m_CurrentInformations2 = new JLabel();
		m_CurrentInformations2.setText("Warte auf Start");
		m_CurrentInformations2.setBounds(300, 175, 245, 25);
		Main.m_StatusPanel.add(m_CurrentInformations2);
		
		m_BackgroundTestHandler.setHostNamePanel(m_CurrentHostLabel);
		m_BackgroundTestHandler.setActionPanel(m_CurrentActionLabel);
		m_BackgroundTestHandler.setTotalProgressBar(m_TotalProgressBar);
		m_BackgroundTestHandler.setCurrentProgressBar(m_CurrentProgressBar);
		m_BackgroundTestHandler.setInformationPanel(m_CurrentInformations);
		m_BackgroundTestHandler.setInformationPanel2(m_CurrentInformations2);
	}
	
	public void stopTest() {
		if(m_GlobalTestStatusRunning) {
			m_GlobalTestStatusRunning = false;
			Main.btnAbortTest.setVisible(false);
			Main.btnTestStarten.setVisible(true);
			m_CurrentActionHeadlineLabel.setText("Test beendet!");
			m_BackgroundTestHandler.stopTest();
		}
	}

	public void startTest() {
		if(!m_GlobalTestStatusRunning) {
			System.out.println("TestHandler.java - startTest");
			m_GlobalTestStatusRunning = true;
			Main.btnAbortTest.setVisible(true);
			Main.btnTestStarten.setVisible(false);
			m_CurrentActionHeadlineLabel.setText("Test gestartet!");
			
			m_BackgroundTestHandler.setCandidateList(Main.m_ConfigHandler.getHostList());
			m_BackgroundTestHandlerThread = new Thread(m_BackgroundTestHandler);
			m_BackgroundTestHandlerThread.start();
		}
	}
}


