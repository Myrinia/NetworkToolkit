package NetworkToolkit;

public class HostConfigDataSet {
	private String m_FileToDownload;
	private String m_HostName;
	private boolean m_SpeedTest;
	private boolean m_PingTest;
	private boolean m_TraceTest;

	public HostConfigDataSet() {
		m_HostName = "";
		m_FileToDownload = "";
		m_SpeedTest = true;
		m_PingTest = true;
		m_TraceTest = true;
	}
	
	public HostConfigDataSet(String file, String name, boolean speed, boolean ping, boolean trace) {
		m_HostName = name;
		m_FileToDownload = file;
		m_SpeedTest = speed;
		m_PingTest = ping;
		m_TraceTest = trace;
	}
	
	void setFileToDownload(String s) {
		m_FileToDownload = s;
	}
	
	void setHostName(String s) {
		m_HostName = s;
	}
	
	void setSpeedTest(boolean b) {
		m_SpeedTest = b;
	}
	
	void setPingTest(boolean b) {
		m_PingTest = b;
	}
	
	void setTraceTest(boolean b) {
		m_TraceTest = b;
	}
	
	String getHostName() {
		return m_HostName;
	}
	
	String getDownloadFile() {
		return m_FileToDownload;
	}

	boolean getDoSpeedTest() {
		return m_SpeedTest;
	}
	
	boolean getDoPingTest() {
		return m_PingTest;
	}
	
	boolean getDoTraceTest() {
		return m_TraceTest;
	}
	
}
