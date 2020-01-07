package NetworkToolkit;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;

public class BackgroundPingThread implements Runnable {

	private String m_URL;
	private ArrayList<Float> m_PingTimes;
	
	private float m_LastPing;
	private float m_FastestPing;
	private float m_SlowestPing;
	private int m_CurrentPingID;
	
	@Override
	public void run() {
		start();
	}
	
	public BackgroundPingThread(HostConfigDataSet candidate) {
		m_URL = candidate.getDownloadFile();
		m_PingTimes = new ArrayList<Float>();	
	}
	
	public void start() {
		m_LastPing 	  = 0;
		m_FastestPing = 10000;
		m_SlowestPing = 0;
		m_CurrentPingID = 0;
		
		int timeoutMillis = 2000;
		
		SocketAddress a = null;
		
		try {
			String niceurlname = new URL(m_URL).getHost();
			Socket s = null;
			a = new InetSocketAddress(niceurlname, 80);
			
			long start = 0;
			
			for(int i = 0; i < 50; i++) {
				s = new Socket();
				start = System.nanoTime();
				
			    s.connect(a, timeoutMillis);
			    long stop = System.nanoTime();
				float timerequired = (stop - start)/1000f/1000f;

				BigDecimal bd = new BigDecimal(Float.toString(timerequired));
			    bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
			    timerequired = bd.floatValue();

				m_LastPing = timerequired;
				
				if(m_FastestPing > timerequired) {
					m_FastestPing = timerequired;
				}
				
				if(m_SlowestPing < timerequired) {
					m_SlowestPing = timerequired;
				}
				
				m_CurrentPingID++;
				
				s.close();
				m_PingTimes.add(timerequired);
				System.out.println("Time required to Ping " + niceurlname +" => " + timerequired + " ms.");
			}
		} catch (Exception e) {
			System.out.println("Error pinging " + m_URL);
		}		
	}
	
	public float getFastestPing() {
		return m_FastestPing;
	}
	
	public float getSlowestPing() {
		return m_SlowestPing;
	}
	
	public float getLastPing() {
		return m_LastPing;
	}
	
	public ArrayList<Float> getPingTimes() {
		return m_PingTimes;
	}
	
	public int getCurrentPingID() {
		return m_CurrentPingID;
	}
	
	public int getCurrentPingTestStatus() { // For Progressbar
		return  100 / 50 * m_CurrentPingID;
	}
}
