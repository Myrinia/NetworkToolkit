package NetworkToolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class BackgroundTestHandler implements Runnable{

	private JLabel m_CurrentActionLabel;
	private JLabel m_CurrentHostLabel;
	private JLabel m_CurrentInformations1;
	private JLabel m_CurrentInformations2;
	private JProgressBar m_CurrentProgressBar;
	private JProgressBar m_TotalProgressBar;
	private ArrayList<HostConfigDataSet> m_Candidates;
	private Timer m_RefreshTimer;
	private boolean m_TestRunning;
	
	public void run() {
		startTest();
	}
	
	public BackgroundTestHandler()
	{
		m_TestRunning = false;
	}

	public void setHostNamePanel(JLabel HostLabel) {
		m_CurrentHostLabel = HostLabel;
	}

	public void setActionPanel(JLabel ActionLabel) {
		m_CurrentActionLabel = ActionLabel;
	}

	public void setCurrentProgressBar(JProgressBar ProgressBar) {
		m_CurrentProgressBar = ProgressBar;
	}
	public void setTotalProgressBar(JProgressBar ProgressBar) {
		m_TotalProgressBar = ProgressBar;
	}
	
	private void setAction(String string) {
		System.out.println("setAction: " + string);
		m_CurrentActionLabel.setText(string);
		m_CurrentActionLabel.revalidate();
		m_CurrentActionLabel.repaint();
	}

	private void setHost(String hostName) {
		System.out.println("SetHost: " + hostName);
		m_CurrentHostLabel.setText(hostName);
		m_CurrentHostLabel.revalidate();
		m_CurrentHostLabel.repaint();
	}

	public void startTest() {
		m_TestRunning = true;
		Main.m_StatisticHandler.startTest();
		
		int totalteststodo = 0; 
		// Get TotalTestsToDo 
		for(HostConfigDataSet candidate : m_Candidates)
		{
			if(candidate.getDoSpeedTest())
			{
				totalteststodo++;
			}
			if(candidate.getDoPingTest())
			{
				totalteststodo++;
			}
			if(candidate.getDoTraceTest())
			{
				totalteststodo++;
			}
		}
		
		int currenttest = 0;
		for(HostConfigDataSet candidate : m_Candidates)
		{
			
			
			if(candidate.getDoSpeedTest())
			{
				setTotalBarStatus( totalteststodo,currenttest );
				currenttest++;
				DoSpeedTest(candidate);
			}
			if(candidate.getDoPingTest())
			{
				setTotalBarStatus( totalteststodo,currenttest );
				currenttest++;
				DoPingTest(candidate);
			}
			if(candidate.getDoTraceTest())
			{
				setTotalBarStatus( totalteststodo,currenttest );
				currenttest++;
				DoTraceTest(candidate);
			}
		}
		
		setHost("Test beendet!");
		setAction("Test beendet!");
		setProgressBarStatus(100);
		setTotalBarStatus( totalteststodo,currenttest );
		setInformation1("Test Beendet!");
		setInformation2("Test Beendet!");
		
		Main.m_StatisticHandler.finishTest();
		Main.btnAbortTest.doClick();
	}

	private void DoTraceTest(final HostConfigDataSet candidate) {
		if(!m_TestRunning)
			return;
		
		setAction("TraceTest [ ! slow Test ! ]");
		setHost(candidate.getHostName());
		
		setInformation1("Traceroute to " + candidate.getHostName());
		final TraceRoute t = new TraceRoute();
		t.setHost(candidate.getDownloadFile());
		
		ActionListener refresher=new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setInformation2(t.getLatestLine());
			}
		};
		
		m_RefreshTimer = new Timer(10,refresher);
		
		m_RefreshTimer.start();
		Main.m_StatisticHandler.addHostTraceroute(candidate.getHostName(), t.getRoute());
		m_RefreshTimer.stop();
	}

	private void DoPingTest(final HostConfigDataSet candidate) {
		if(!m_TestRunning)
			return;
	
		setAction("PingTest");
		setHost(candidate.getHostName());
		
		final BackgroundPingThread d  = new BackgroundPingThread(candidate);
		Thread t = new Thread(d);
		
		t.start();
		
		ActionListener refresher=new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setInformation1("Ping: " + candidate.getHostName() + " for " + d.getLastPing() + " ms.");
				setInformation2(" FastesPing: "+d.getFastestPing() +  " / SlowestPing: " + d.getSlowestPing() +" [Jitter:"+ (d.getSlowestPing() - d.getFastestPing()) +"]");
				setProgressBarStatus(d.getCurrentPingTestStatus());
			}
		};
		
		m_RefreshTimer = new Timer(10,refresher);
		
		
		try {
			m_RefreshTimer.start();
			t.join();
			m_RefreshTimer.stop();
			setProgressBarStatus(100);
			Main.m_StatisticHandler.addHostPingTimes(candidate.getHostName(), d.getPingTimes());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	private void DoSpeedTest(HostConfigDataSet candidate) {
		if(!m_TestRunning)
			return;
		
		setAction("SpeedTest");
		setHost(candidate.getHostName());
		
		final BackgroundDownloadThread d = new BackgroundDownloadThread(candidate,Integer.valueOf(Main.m_ConfigHandler.getString("dltime")));
		Thread t = new Thread(d);
		t.start();
		
		ActionListener refresher=new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setProgressBarStatus((int)d.getDownloadProgress());
				setInformation1(d.getBytesLoaded() + " / " + d.getTotalFileSize() +" ("+ d.getMBperSecond() +" MB/s)");
				setInformation2(d.getBytesLoadedBin()  + "  /  " + d.getTotalFileSizeBin() +" ("+ d.getMBitperSecond() +" Mbit/s)");
			}
		};

		m_RefreshTimer = new Timer(10,refresher);
		m_RefreshTimer.start();
		try {
			t.join();
			setProgressBarStatus(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		m_RefreshTimer.stop();
		
	}

	public void setCandidateList(final ArrayList<HostConfigDataSet> hostList) {
		m_Candidates = hostList;
	}
		
	private void setProgressBarStatus(int value)
	{
		m_CurrentProgressBar.setValue(value);
	}
	private void setTotalBarStatus(int totalTests, int currenttest)
	{
		if(totalTests == 0)
			totalTests = 1;
		
		if(currenttest == 0)
			currenttest = 1;
		m_TotalProgressBar.setValue( 100/totalTests*currenttest );
	}
	
	private void setInformation1(String infos)
	{
		m_CurrentInformations1.setText(infos);
	}
	private void setInformation2(String infos)
	{
		m_CurrentInformations2.setText(infos);
	}
	
	public void setInformationPanel(JLabel Informations) {
		m_CurrentInformations1 = Informations;
	}

	public void setInformationPanel2(JLabel m_CurrentInformations22) {
		m_CurrentInformations2 = m_CurrentInformations22;
	}
	public void stopTest() {
		m_TestRunning = false;
		BackgroundDownloadThread.m_TestRunning = false;
	}
	public boolean isTestRunning()
	{
		return m_TestRunning;
	}




}
