package NetworkToolkit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileHandler {
	
	public ZipFileHandler() {
		
	}
	
	public void unzip(String fileZip,String newPath) {
		try {
	        int BUFFER = 2048;
	        File file = new File(fileZip);

	        @SuppressWarnings("resource")
			ZipFile zip = new ZipFile(file);

	        new File(newPath).mkdir();
	        Enumeration<?> zipFileEntries = zip.entries();

	        // Process each entry
	        while (zipFileEntries.hasMoreElements()) {
	            // grab a zip file entry
	            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
	            String currentEntry = entry.getName();
	            
	            File destFile = new File(newPath, currentEntry);
	            //destFile = new File(newPath, destFile.getName());
	            File destinationParent = destFile.getParentFile();

	            // create the parent directory structure if needed
	            destinationParent.mkdirs();

	            if (!entry.isDirectory()) {
	                BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
	                int currentByte;
	                // establish buffer for writing file
	                byte data[] = new byte[BUFFER];

	                // write the current file to disk
	                FileOutputStream fos = new FileOutputStream(destFile);
	                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

	                // read and write until last byte is encountered
	                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
	                    dest.write(data, 0, currentByte);
	                }
	                dest.flush();
	                dest.close();
	                is.close();
	            }
	        }
	    } catch (Exception e) {
	        System.out.println("ERROR: "+e.getMessage());
	    }
	}
}
