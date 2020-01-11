package NetworkToolkit;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Dimension;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JTextField;

import java.awt.Toolkit;

import javax.swing.border.BevelBorder;

public class Main {

	private JFrame MainFrame;
	
	static ConfigHandler m_ConfigHandler;
	final private TestHandler m_BackgroundTestHandler = new TestHandler();
	final static JPanel m_StatusPanel = new JPanel();
	final JPanel m_ConfigPanel = new JPanel();
	
	final static StatisticHandler m_StatisticHandler = new StatisticHandler();
	
	final static JButton btnTestStarten = new JButton("Test Starten");
	final static JButton btnKonfiguration = new JButton("Konfigurieren");
	final static JButton btnAbortTest = new JButton("Test Abbrechen");
	final static JButton btnShowStatistics = new JButton("Zeige Statistik");
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VersionManagement vmgr = new VersionManagement();
					
					if(vmgr.isLatestVersion()) {
						Main window = new Main();
						window.MainFrame.setVisible(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	private void initVariables() {
		m_ConfigHandler = new ConfigHandler();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		initVariables();
		setupAppFrame();
		loadStatusPanel();
		loadConfigPanel();
	}

	private void setupAppFrame() {
		MainFrame = new JFrame();
		MainFrame.setResizable(false);
		MainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/com/sun/java/swing/plaf/windows/icons/Computer.gif")));
		MainFrame.setTitle("NetworkToolkit by Myrinia@LTEForum.at");
		MainFrame.getContentPane().setBackground(new Color(255, 255, 255));
		MainFrame.setBounds(100, 100, 575, 470);
		MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MainFrame.getContentPane().setLayout(null);
		
		btnKonfiguration.setBackground(new Color(255, 255, 255));
		btnTestStarten.setBackground(new Color(255, 255, 255));
		btnAbortTest.setBackground(new Color(255, 255, 255));
		btnShowStatistics.setBackground(new Color(255, 255, 255));
		
		btnTestStarten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnTestStarten.setVisible(false);
				btnAbortTest.setVisible(true);
				btnShowStatistics.setVisible(false);
				m_ConfigPanel.setVisible(false);
				m_StatusPanel.setVisible(true);
				m_BackgroundTestHandler.startTest();
			}
		});
		
		btnKonfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				m_ConfigPanel.setVisible(true);
				m_StatusPanel.setVisible(false);
				m_BackgroundTestHandler.stopTest();
			}
		});
		
		btnAbortTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				btnTestStarten.setVisible(true);
				btnShowStatistics.setVisible(true);
				btnAbortTest.setVisible(false);
				m_BackgroundTestHandler.stopTest();
			}
		});
		
		btnShowStatistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					new StatisticViewer().startUsingFileSelection();
				} catch (Exception e) {
					// e.printStackTrace();
					System.out.println("Error viewing Statistic File: " + e.getMessage());
				}
			}
		});
		
		btnTestStarten.setBounds(10, 3, 180, 60);
		btnKonfiguration.setBounds(195, 3, 180, 60);
		btnAbortTest.setBounds(380, 3, 180, 60);
		btnShowStatistics.setBounds(380, 3, 180, 60);
		
		MainFrame.getContentPane().add(btnTestStarten);
		MainFrame.getContentPane().add(btnKonfiguration);
		MainFrame.getContentPane().add(btnAbortTest);
		MainFrame.getContentPane().add(btnShowStatistics);
		
		btnAbortTest.setVisible(false);
		m_StatusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(173, 216, 230), new Color(173, 216, 230), new Color(173, 216, 230), new Color(173, 216, 230)));
		
		m_StatusPanel.setLayout(null);
		m_StatusPanel.setBackground(new Color(230, 230, 250));
		m_StatusPanel.setBounds(10, 65, 550, 370);
		MainFrame.getContentPane().add(m_StatusPanel);
		
		m_ConfigPanel.setLayout(null);
		m_ConfigPanel.setBackground(new Color(255, 255, 255));
		m_ConfigPanel.setVisible(false);
		m_ConfigPanel.setBounds(10, 65, 550, 370);
		
		MainFrame.getContentPane().add(m_ConfigPanel);
	}

	private void loadConfigPanel() {
		m_ConfigPanel.removeAll();
		
		final JSlider DLTimeSlider;
		final JLabel lblDLTime;
		final JPanel PNLHosts;
		final JTextField textFieldHostName;
		final JTextField textFieldDLURL;
		
		final JLabel lblISPName;
		final JLabel lblISPDown;
		final JLabel lblISPUp;
		final JComboBox<String> comboBoxConnectionType;
		final JLabel lblISPDownMbitBez;
		final JLabel lblISPUpMbitBez;
		final JLabel lblConnectionType;
		final JTextField textFieldISPName;
		final JTextField textFieldISPDown;
		final JTextField textFieldISPUp;
		
		
		lblDLTime = new JLabel("");
		lblDLTime.setBounds(290, 0, 50, 25);
		m_ConfigPanel.add(lblDLTime);
		
		DLTimeSlider = new JSlider(JSlider.HORIZONTAL);
		DLTimeSlider.setMinimum(1);
		DLTimeSlider.setMaximum(90);
		DLTimeSlider.createStandardLabels(1);
		DLTimeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				lblDLTime.setText(DLTimeSlider.getValue() + " Sek.");
				m_ConfigHandler.setConfigVariable("dltime", String.valueOf(DLTimeSlider.getValue()));
			}
		});
		
		DLTimeSlider.setValue(m_ConfigHandler.getInt("dltime"));
		lblDLTime.setText(DLTimeSlider.getValue() + " Sek.");
		DLTimeSlider.setPaintTicks(true);
		DLTimeSlider.setSnapToTicks(true);
		DLTimeSlider.setPaintLabels(true);
		DLTimeSlider.setBounds(140, 2, 150, 27);
		DLTimeSlider.setBackground(new Color(255,255,255));
		m_ConfigPanel.add(DLTimeSlider);

		JLabel lblDownloadTimePerFile = new JLabel("Max. Zeit Pro Download");
		lblDownloadTimePerFile.setBounds(5, 0, 150, 25);
		m_ConfigPanel.add(lblDownloadTimePerFile);

		PNLHosts = new JPanel();
		PNLHosts.setBackground(new Color(210, 105, 30));
		PNLHosts.setForeground(new Color(224, 255, 255));
		PNLHosts.setBounds(0, 130, 450, 200);
		m_ConfigPanel.add(PNLHosts);

		final JPanel pnlSetISPData = new JPanel();
		pnlSetISPData.setBounds(300, 30, 250, 98);
		pnlSetISPData.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"ISP Daten"));
		pnlSetISPData.setBackground(new Color(192, 192, 192));
		pnlSetISPData.setForeground(new Color(0, 0, 0));
		pnlSetISPData.setLayout(null);
		
		lblISPName = new JLabel("Anbieter:");
		lblISPName.setBounds(10, 15, 100, 20);
		pnlSetISPData.add(lblISPName);
		
		lblISPDown = new JLabel("Download:");
		lblISPDown.setBounds(10, 35, 70, 20);
		pnlSetISPData.add(lblISPDown);
		
		lblISPDownMbitBez = new JLabel("Mbit/s");
		lblISPDownMbitBez.setBounds(155, 35, 50, 20);
		pnlSetISPData.add(lblISPDownMbitBez);
		
		lblISPUp = new JLabel("Upload:");
		lblISPUp.setBounds(10, 55, 50, 20);
		pnlSetISPData.add(lblISPUp);
		
		lblISPUpMbitBez = new JLabel("Mbit/s");
		lblISPUpMbitBez.setBounds(155, 55, 50, 20);
		pnlSetISPData.add(lblISPUpMbitBez);
		
		textFieldISPName = new JTextField();
		textFieldISPName.setBounds(75, 15, 150, 20);
		textFieldISPName.setText(m_ConfigHandler.getString("ISPName"));
		pnlSetISPData.add(textFieldISPName);
		
		textFieldISPDown = new JTextField();
		textFieldISPDown.setBounds(75, 35, 70, 20);
		textFieldISPDown.setText(m_ConfigHandler.getString("ISPDown"));
		pnlSetISPData.add(textFieldISPDown);
		
		textFieldISPUp = new JTextField();
		textFieldISPUp.setBounds(75, 55, 70, 20);
		textFieldISPUp.setText(m_ConfigHandler.getString("ISPUp"));
		pnlSetISPData.add(textFieldISPUp);
		
		lblConnectionType = new JLabel("Type:");
		lblConnectionType.setBounds(10, 75, 50, 20);
		pnlSetISPData.add(lblConnectionType);
		
		String connectiontype[]= { "LTE","DSL","Kabel","5G","Glasfaser","Satellit","Other" };
		
		int selectedindex = 0;
		comboBoxConnectionType = new JComboBox<String>();
		
		for(String s : connectiontype) {
			comboBoxConnectionType.addItem(s);
			if(s.equals(m_ConfigHandler.getString("ISPConnectionType"))) {
				comboBoxConnectionType.setSelectedIndex(selectedindex);
			}
			selectedindex++;
		}
		
		comboBoxConnectionType.setBounds(75,75,100,20);
		pnlSetISPData.add(comboBoxConnectionType);
		
		m_ConfigPanel.add(pnlSetISPData);
		
		final JPanel pnlAddHost = new JPanel();
		pnlAddHost.setBounds(0, 30, 295, 95);
		pnlAddHost.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"Host hinzufügen"));
		pnlAddHost.setVisible(true);
		pnlAddHost.setBackground(new Color(192, 192, 192));
		pnlAddHost.setForeground(new Color(0, 0, 0));
		pnlAddHost.setLayout(null);
		
		textFieldHostName = new JTextField();
		textFieldHostName.setBounds(60, 15, 225, 20);
		pnlAddHost.add(textFieldHostName);
		textFieldHostName.setColumns(10);
		
		textFieldDLURL = new JTextField();
		textFieldDLURL.setBounds(60, 35, 225, 20);
		pnlAddHost.add(textFieldDLURL);
		textFieldDLURL.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Name");
		lblNewLabel.setBounds(10, 15, 50, 20);
		pnlAddHost.add(lblNewLabel);
		
		JLabel lblDlUrl = new JLabel("DL URL");
		lblDlUrl.setBounds(10, 35, 50, 20);
		pnlAddHost.add(lblDlUrl);
		
		JButton btnNewButton = new JButton("Host hinzufügen");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				m_ConfigHandler.addHost(textFieldHostName.getText(), textFieldDLURL.getText());
				textFieldDLURL.setText("");
				textFieldHostName.setText("");
				PNLHosts.removeAll();
				m_ConfigHandler.saveConfig();
				LoadHostsIntoConfigPanel(PNLHosts);
			}
		});
		btnNewButton.setBounds(10, 66, 275, 25);
		pnlAddHost.add(btnNewButton);
		
		JButton btnKonfigurationSpeichern = new JButton("Speichern");
		btnKonfigurationSpeichern.setBounds(450, 250, 100, 35);
		m_ConfigPanel.add(btnKonfigurationSpeichern);
		
		JButton btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				m_BackgroundTestHandler.stopTest();
				m_ConfigPanel.setVisible(false);
				m_StatusPanel.setVisible(true);
				loadStatusPanel();		
			}
		});
		btnAbbrechen.setBounds(450, 215, 100, 35);
		m_ConfigPanel.add(btnAbbrechen);
		btnKonfigurationSpeichern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				m_BackgroundTestHandler.stopTest();
				m_ConfigHandler.setISPData(textFieldISPName.getText(), textFieldISPDown.getText(), textFieldISPUp.getText(),comboBoxConnectionType.getSelectedItem().toString());
				m_ConfigHandler.saveConfig();
				m_ConfigPanel.setVisible(false);
				m_StatusPanel.setVisible(true);
				loadStatusPanel();
			}
		});
		
		JButton btnDisableSpeedTests = new JButton("Deaktiviere Speedtests");
		JButton btnActivateSpeedTests = new JButton("Aktiviere Speedtests");
		JButton btnDisablePingTests = new JButton("Deaktiviere Pingtests");
		JButton btnActivatePingTests = new JButton("Aktiviere Pingtests");
		JButton btnDisableTraceTests = new JButton("Deaktiviere Tracetests");
		JButton btnActivateTraceTests = new JButton("Aktiviere Tracetests");
		
		btnDisableSpeedTests.setBounds(0  , 330, 170, 20);
		btnActivateSpeedTests.setBounds(0 , 350, 170, 20);
		btnDisablePingTests.setBounds(170 , 330, 170, 20);
		btnActivatePingTests.setBounds(170, 350, 170, 20);
		btnDisableTraceTests.setBounds(340 , 330, 170, 20);
		btnActivateTraceTests.setBounds(340, 350, 170, 20);
		
		
		
		btnDisableSpeedTests.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_BackgroundTestHandler.stopTest();
				m_ConfigHandler.setISPData(textFieldISPName.getText(), textFieldISPDown.getText(), textFieldISPUp.getText(),comboBoxConnectionType.getSelectedItem().toString());
				m_ConfigHandler.setTestsState(ConfigHandler.HOST_STATE_SPEED, false);
				m_ConfigHandler.saveConfig();
				PNLHosts.removeAll();
				LoadHostsIntoConfigPanel(PNLHosts);
			}
		});
		btnActivateSpeedTests.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_BackgroundTestHandler.stopTest();
				m_ConfigHandler.setISPData(textFieldISPName.getText(), textFieldISPDown.getText(), textFieldISPUp.getText(),comboBoxConnectionType.getSelectedItem().toString());
				m_ConfigHandler.setTestsState(ConfigHandler.HOST_STATE_SPEED, true);
				m_ConfigHandler.saveConfig();
				PNLHosts.removeAll();
				LoadHostsIntoConfigPanel(PNLHosts);
			}
		});
		btnDisablePingTests.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_BackgroundTestHandler.stopTest();
				m_ConfigHandler.setISPData(textFieldISPName.getText(), textFieldISPDown.getText(), textFieldISPUp.getText(),comboBoxConnectionType.getSelectedItem().toString());
				m_ConfigHandler.setTestsState(ConfigHandler.HOST_STATE_PING, false);
				m_ConfigHandler.saveConfig();
				PNLHosts.removeAll();
				LoadHostsIntoConfigPanel(PNLHosts);
			}
		});
		btnActivatePingTests.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_BackgroundTestHandler.stopTest();
				m_ConfigHandler.setISPData(textFieldISPName.getText(), textFieldISPDown.getText(), textFieldISPUp.getText(),comboBoxConnectionType.getSelectedItem().toString());
				m_ConfigHandler.setTestsState(ConfigHandler.HOST_STATE_PING, true);
				m_ConfigHandler.saveConfig();
				PNLHosts.removeAll();
				LoadHostsIntoConfigPanel(PNLHosts);
			}
		});
		btnDisableTraceTests.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_BackgroundTestHandler.stopTest();
				m_ConfigHandler.setISPData(textFieldISPName.getText(), textFieldISPDown.getText(), textFieldISPUp.getText(),comboBoxConnectionType.getSelectedItem().toString());
				m_ConfigHandler.setTestsState(ConfigHandler.HOST_STATE_TRACE, false);
				m_ConfigHandler.saveConfig();
				PNLHosts.removeAll();
				LoadHostsIntoConfigPanel(PNLHosts);
			}
		});
		btnActivateTraceTests.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_BackgroundTestHandler.stopTest();
				m_ConfigHandler.setISPData(textFieldISPName.getText(), textFieldISPDown.getText(), textFieldISPUp.getText(),comboBoxConnectionType.getSelectedItem().toString());
				m_ConfigHandler.setTestsState(ConfigHandler.HOST_STATE_TRACE, true);
				m_ConfigHandler.saveConfig();
				PNLHosts.removeAll();
				LoadHostsIntoConfigPanel(PNLHosts);
			}
		});
		
		m_ConfigPanel.add(btnDisableSpeedTests);
		m_ConfigPanel.add(btnActivateSpeedTests);
		m_ConfigPanel.add(btnDisablePingTests);
		m_ConfigPanel.add(btnActivatePingTests);
		m_ConfigPanel.add(btnDisableTraceTests);
		m_ConfigPanel.add(btnActivateTraceTests);
		
		m_ConfigPanel.add(pnlAddHost);
		LoadHostsIntoConfigPanel(PNLHosts);
	}

	private void loadStatusPanel() {
		m_StatusPanel.removeAll();
		m_BackgroundTestHandler.initialize();
		// Check for activated, disabled and total host count
		int activeSpeed = 0;
		int activePing = 0;
		int activeTrace = 0;
		
		int total = 0;
		
		ArrayList<HostConfigDataSet> set = m_ConfigHandler.getHostList();
		
		for(HostConfigDataSet entry : set) {
			if(entry.getDoSpeedTest()) { // Activated ?
				activeSpeed+=1;
			}
			if(entry.getDoPingTest()) {
				activePing+=1;
			}
			
			if(entry.getDoTraceTest()) {
				activeTrace+= 1;
			}
			total +=1;
		}

		JLabel lblAvailHost = new JLabel("Verfügbare Hosts:");
		lblAvailHost.setBackground(Color.WHITE);
		lblAvailHost.setForeground(new Color(0, 128, 0));
		lblAvailHost.setBounds(5, 5, 135, 25);
		m_StatusPanel.add(lblAvailHost);
		
		JLabel lblAvailHostCount = new JLabel(String.valueOf(total));
		lblAvailHostCount.setForeground(new Color(0, 0, 0));
		lblAvailHostCount.setBounds(150, 5, 40, 25);
		m_StatusPanel.add(lblAvailHostCount);

		JLabel lblActiveSpeed = new JLabel("Aktivierte Speedtests:");
		lblActiveSpeed.setForeground(new Color(255, 165, 0));
		lblActiveSpeed.setBounds(5, 25, 135, 25);
		m_StatusPanel.add(lblActiveSpeed);
		
		JLabel lblActiveSpeedCount = new JLabel(String.valueOf(activeSpeed));
		lblActiveSpeedCount.setForeground(new Color(0, 0, 0));
		lblActiveSpeedCount.setBounds(150, 25, 40, 25);
		m_StatusPanel.add(lblActiveSpeedCount);

		JLabel lblActivePing = new JLabel("Aktivierte Pingtests:");
		lblActivePing.setForeground(new Color(100, 149, 237));
		lblActivePing.setBounds(5, 45, 135, 25);
		m_StatusPanel.add(lblActivePing);
		
		JLabel lblActivePingCount = new JLabel(String.valueOf(activePing));
		lblActivePingCount.setForeground(new Color(0, 0, 0));
		lblActivePingCount.setBounds(150, 45, 40, 25);
		m_StatusPanel.add(lblActivePingCount);

		JLabel lblActiveTrace = new JLabel("Aktivierte Tracetests:");
		lblActiveTrace.setForeground(new Color(128, 0, 0));
		lblActiveTrace.setBounds(5, 65, 135, 25);
		m_StatusPanel.add(lblActiveTrace);
		
		JLabel lblActiveTraceCount = new JLabel(String.valueOf(activeTrace));
		lblActiveTraceCount.setForeground(new Color(0, 0, 0));
		lblActiveTraceCount.setBounds(150, 65, 40, 25);
		m_StatusPanel.add(lblActiveTraceCount);
				
		m_StatusPanel.revalidate();
		m_StatusPanel.updateUI();
	}

	private void LoadHostsIntoConfigPanel(final JPanel PNLHosts) {
		PNLHosts.removeAll();
		PNLHosts.setLayout(null);
		
		int currentFrameHeight = 30*m_ConfigHandler.getHostList().size();
		
		PNLHosts.setPreferredSize(new Dimension(340,155));
		
		JPanel hostPanel = new JPanel();
		hostPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"Was soll getestet werden ?"));
		
		hostPanel.setLayout(null);
		hostPanel.setPreferredSize(new Dimension(340,currentFrameHeight));
		JLabel lblHost = new JLabel("Host");
		lblHost.setBounds(10, 10, 51, 20);
		hostPanel.add(lblHost);
		
		JLabel lblSpeedtest = new JLabel("SpeedTest");
		lblSpeedtest.setBounds(170, 10, 80, 20);
		hostPanel.add(lblSpeedtest);
		
		JLabel lblPingtest = new JLabel("PingTest");
		lblPingtest.setBounds(240, 10, 80, 20);
		hostPanel.add(lblPingtest);
		
		JLabel lblTracert = new JLabel("Tracert");
		lblTracert.setBounds(310, 10, 80, 20);
		hostPanel.add(lblTracert);
		
		JScrollPane scrollPane = new JScrollPane(hostPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(0, 0, 450, 200);
        
		PNLHosts.add(scrollPane);
		
		// Iterate through our map
		
		ArrayList<HostConfigDataSet> hostlist = m_ConfigHandler.getHostList();
		
		int margintop = 20;

		for(final HostConfigDataSet entry : hostlist) {
			JLabel lblName = new JLabel();
			JCheckBox boxSpeed = new JCheckBox();
			JCheckBox boxPing = new JCheckBox();
			JCheckBox boxTrace = new JCheckBox();
			JButton deleteme = new JButton("Löschen");
			
			lblName.setText(entry.getHostName());;
			boxSpeed.setToolTipText("Testfile:"+entry.getDownloadFile());
			
			boxSpeed.setSelected(entry.getDoSpeedTest());
			boxPing.setSelected(entry.getDoPingTest());
			boxTrace.setSelected(entry.getDoTraceTest());
			
			deleteme.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					m_ConfigHandler.removeHost(entry.getHostName());
					LoadHostsIntoConfigPanel(PNLHosts);
				}
			});
			
			boxSpeed.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			        boolean state = false;
			        if(e.getStateChange() == 1) {
			        	state = true;
			        }
			        m_ConfigHandler.setHostState(entry.getHostName(),state, ConfigHandler.HOST_STATE_SPEED);
			    }
			});
			
			boxPing.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			        boolean state = false;
			        if(e.getStateChange() == 1) {
			        	state = true;
			        }
			        m_ConfigHandler.setHostState(entry.getHostName(),state, ConfigHandler.HOST_STATE_PING);
			    }
			});
			
			boxTrace.addItemListener(new ItemListener() {
			    public void itemStateChanged(ItemEvent e) {
			        boolean state = false;
			        if(e.getStateChange() == 1) {
			        	state = true;
			        }
			        m_ConfigHandler.setHostState(entry.getHostName(),state, ConfigHandler.HOST_STATE_TRACE);
			    }
			});
			margintop += 25;
			lblName.setBounds(   5, margintop, 150, 25);
			boxSpeed.setBounds(190, margintop,  20, 25);
			boxPing.setBounds( 255, margintop,  20, 25);
			boxTrace.setBounds(320, margintop,  20, 25);
			deleteme.setBounds(345, margintop,  85, 25);
			
			hostPanel.add(lblName);
			hostPanel.add(boxSpeed);
			hostPanel.add(boxPing);
			hostPanel.add(boxTrace);
			hostPanel.add(deleteme);
			
			hostPanel.revalidate();
			hostPanel.updateUI();
		}
		
	}
}
