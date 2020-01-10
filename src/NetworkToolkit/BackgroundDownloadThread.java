package NetworkToolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.Timer;


public class BackgroundDownloadThread implements Runnable {

	private String m_DownloadUrl;
	private String m_DownloadHostName;
	
	private int m_MaxRunTime;
	private int m_CurrentRunTime;
	private double m_TargetFileSize;
	public static boolean m_TestRunning;
	private long m_BytesLoaded;
	Timer m_CountdownTimer;
	
	public void run() {
		startDownload();
	}
	
	private void startDownload() {
		
		try {
			URL url = new URL(m_DownloadUrl);
			setTargetFileSize(url);
			m_TestRunning = true;
			m_CountdownTimer.start();
			try {
				BufferedInputStream in = new BufferedInputStream(url.openStream());
				byte dataBuffer[] = new byte[524288];
			    int bytesRead;
			    
			    while (m_TestRunning && (bytesRead = in.read(dataBuffer, 0, 524288)) != -1) {
			    	m_BytesLoaded += bytesRead;
			    	Main.m_StatisticHandler.addHostSpeed(m_DownloadHostName, m_CurrentRunTime , m_BytesLoaded);
			    }
			    
			} catch (IOException e) {
				System.out.println("Download Error");
				m_CountdownTimer.stop();
			    // handle exception
			}

			m_CountdownTimer.stop();
			
		} catch (MalformedURLException e1) {
			m_CountdownTimer.stop();
			m_TestRunning = false;
		}
	}

	private void setTargetFileSize(URL url) {
		  HttpURLConnection conn = null;
		  try {
		    conn = (HttpURLConnection) url.openConnection();
		    conn.setRequestMethod("HEAD");
		    m_TargetFileSize = conn.getContentLengthLong();
		  } catch (IOException e) {
		    throw new RuntimeException(e);
		  } finally {
		    if (conn != null) {
		      conn.disconnect();
		    }
		  }
	}

	public double getDownloadProgress()
	{	
		// Return the timer that is closer to the real time
		double DL_PCT =  (m_BytesLoaded/m_TargetFileSize)*100 ;
		double Timer_PCT = (100.f / (m_MaxRunTime*1000)) * m_CurrentRunTime;

		if( DL_PCT > Timer_PCT) {
			return DL_PCT;
		}
		
		return Timer_PCT;
	}
	
	public BackgroundDownloadThread()
	{
		
	}
	
	public BackgroundDownloadThread(HostConfigDataSet set, int runtime) {
		this.m_DownloadUrl = set.getDownloadFile();
		this.m_DownloadHostName = set.getHostName();
		this.m_MaxRunTime = runtime;
		m_BytesLoaded = 1;
		m_TargetFileSize = 1;
		m_TestRunning = false;
		
		ActionListener countDown=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_CurrentRunTime += 10;
				if(m_CurrentRunTime == m_MaxRunTime*1000) {
					m_TestRunning  = false;
					m_CountdownTimer.stop();
				}
			}
		};

		m_CountdownTimer = new Timer(10,countDown);
	}
	
	public String getBytesLoaded() {
		return BitByteManager.humanReadableByteCountSI((long)m_BytesLoaded);
	}
	
	public String getBytesLoadedBin() {
		return BitByteManager.humanReadableByteCountBin((long)m_BytesLoaded);
	}

	public float getMBitperSecond() {
		float seconds = m_CurrentRunTime/1000;
		
		return (float)Math.round((float) ((m_BytesLoaded*8/1024.f/1024.f)/seconds)*100) /100.f;
	}

	public float getMBperSecond() {
		float seconds = m_CurrentRunTime/1000;
		
		return (float)Math.round((float) ((m_BytesLoaded/1000.f/1000.f)/seconds)*100.f) /100.f;
	}
	
	public String getTotalFileSize() {
		return BitByteManager.humanReadableByteCountSI((long)m_TargetFileSize);
	}
	
	public String getTotalFileSizeBin() {
		return BitByteManager.humanReadableByteCountBin((long)m_TargetFileSize);
	}
}
