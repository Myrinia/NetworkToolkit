package NetworkToolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import javax.swing.Timer;


public class BackgroundDownloadThread implements Runnable {

	private String m_DownloadUrl;
	private String m_DownloadHostName;
	
	private int m_MaxRunTime;
	private int m_CurrentRunTime;
	private double m_TargetFileSize;
	public static boolean m_TestRunning;
	private double m_BytesLoaded;
	Timer m_CountdownTimer;
	
	public void run()
	{
		startDownload();
	}
	
	private void startDownload() {
		URL url;
		try {
			url = new URL(m_DownloadUrl);
			setTargetFileSize(url);
			m_TestRunning = true;
			m_CountdownTimer.start();
			try (BufferedInputStream in = new BufferedInputStream(url.openStream());
			  FileOutputStream fileOutputStream = new FileOutputStream("TestFile")) {
			    byte dataBuffer[] = new byte[524288];
			    int bytesRead;
			    while (m_TestRunning && (bytesRead = in.read(dataBuffer, 0, 524288)) != -1) {
			    	m_BytesLoaded += bytesRead;
			    	Main.m_StatisticHandler.addHostSpeed(m_DownloadHostName, m_CurrentRunTime , m_BytesLoaded);
			        fileOutputStream.write(dataBuffer, 0, bytesRead);
			    }
			} catch (IOException e) {
				System.out.println("Download Error");
				m_CountdownTimer.stop();
			    // handle exception
			}
			m_CountdownTimer.stop();
			deleteTestfile();
		} catch (MalformedURLException e1) {
			m_CountdownTimer.stop();
			m_TestRunning = false;
		}
	}

	private void deleteTestfile() {
		try
        { 
            if(Files.exists(Paths.get("TestFile")))
            {
            	Files.delete(Paths.get("TestFile"));
            	System.out.println("TestFile removed!");
            }else
            {
            	System.out.println("TestFile Does not exist!");
            }
            
        } 
        catch(NoSuchFileException e) 
        { 
            System.out.println("No such file/directory exists"); 
        } 
        catch(DirectoryNotEmptyException e) 
        { 
            System.out.println("Directory is not empty."); 
        } 
        catch(IOException e) 
        { 
            System.out.println("Invalid permissions."); 
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
		double Timer_PCT = ((float)100) / (m_MaxRunTime*1000) * m_CurrentRunTime;

		if( DL_PCT > Timer_PCT)
			return DL_PCT;
		return Timer_PCT;
	}
	
	public BackgroundDownloadThread()
	{
		
	}
	
	public BackgroundDownloadThread(HostConfigDataSet set, int runtime)
	{
		this.m_DownloadUrl = set.getDownloadFile();
		this.m_DownloadHostName = set.getHostName();
		this.m_MaxRunTime = runtime;
		m_BytesLoaded = 1;
		m_TargetFileSize = 1;
		m_TestRunning = false;
		
		ActionListener countDown=new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_CurrentRunTime += 10;
				if(m_CurrentRunTime == m_MaxRunTime*1000)
				{
					m_TestRunning  = false;
					m_CountdownTimer.stop();
				}
			}
		};

		m_CountdownTimer = new Timer(10,countDown);
	}
	
	public String getBytesLoaded()
	{
		return humanReadableByteCountSI((long)m_BytesLoaded);
	}
	
	public String getBytesLoadedBin()
	{
		return humanReadableByteCountBin((long)m_BytesLoaded);
	}
	
	private String humanReadableByteCountSI(long bytes) {
	    String s = bytes < 0 ? "-" : "";
	    long b = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
	    return b < 1000L ? bytes + " B"
	            : b < 999_950L ? String.format("%s%.1f kB", s, b / 1e3)
	            : (b /= 1000) < 999_950L ? String.format("%s%.1f MB", s, b / 1e3)
	            : (b /= 1000) < 999_950L ? String.format("%s%.1f GB", s, b / 1e3)
	            : (b /= 1000) < 999_950L ? String.format("%s%.1f TB", s, b / 1e3)
	            : (b /= 1000) < 999_950L ? String.format("%s%.1f PB", s, b / 1e3)
	            : String.format("%s%.1f EB", s, b / 1e6);
	}
	
	private String humanReadableByteCountBin(long bytes) {
	    long b = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
	    return b < 1024L ? bytes + " B"
	            : b <= 0xfffccccccccccccL >> 40 ? String.format("%.1f KiB", bytes / 0x1p10)
	            : b <= 0xfffccccccccccccL >> 30 ? String.format("%.1f MiB", bytes / 0x1p20)
	            : b <= 0xfffccccccccccccL >> 20 ? String.format("%.1f GiB", bytes / 0x1p30)
	            : b <= 0xfffccccccccccccL >> 10 ? String.format("%.1f TiB", bytes / 0x1p40)
	            : b <= 0xfffccccccccccccL ? String.format("%.1f PiB", (bytes >> 10) / 0x1p40)
	            : String.format("%.1f EiB", (bytes >> 20) / 0x1p40);
	}

	public float getMBitperSecond()
	{
		float seconds = m_CurrentRunTime/1000;
		
		return (float)Math.round((float) ((m_BytesLoaded*8/1024.f/1024.f)/seconds)*100) /100.f;
		
	}

	public float getMBperSecond()
	{
		float seconds = m_CurrentRunTime/1000;
		
		return (float)Math.round((float) ((m_BytesLoaded/1000.f/1000.f)/seconds)*100.f) /100.f;
		
	}
	public String getTotalFileSize() {
		return humanReadableByteCountSI((long)m_TargetFileSize);
	}
	public String getTotalFileSizeBin() {
		return humanReadableByteCountBin((long)m_TargetFileSize);
	}
}
