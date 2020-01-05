package NetworkToolkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

public class StatisticSaver {

	
	private String m_StatisticFolderName;
	
	public StatisticSaver()
	{
		
	}
	
	public void onTestStart()
	{
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new Date());
		setFolderName(timeStamp);
	}
	
	public void setFolderName(String name)
	{
		m_StatisticFolderName = "data/statistics/"+name+"/";
		CreateStatisticFolderIfNotExist();
	}
	
	private void CreateStatisticFolderIfNotExist()
	{
		new File(m_StatisticFolderName).mkdirs();
	}
	
	private boolean writeFile(String filename,String content)
	{
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter( new FileWriter( m_StatisticFolderName + filename ));
		    writer.write( content);

		}
		catch ( Exception e)
		{
			return false;
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        {
		        	writer.close( );
		        }
		    }
		    catch ( Exception e)
		    {
		    	return false;
		    }
		}
		
		return true;
	}

	public void onTestFinish() {
		
	}

	public void saveStatisticFile(String filename, JSONObject json) {
		saveStatisticFile(filename,json.toString());
	}
	
	public void saveStatisticFile(String filename, String content)
	{
		if(writeFile(filename,content))
		{
			System.out.println("Statistic File: " + filename + " saved!");
		}else
		{
			System.out.println("Error saving Statistic " + filename + " !");
		}
	}
	
}
