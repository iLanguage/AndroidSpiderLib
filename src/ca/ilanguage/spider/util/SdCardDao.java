package ca.ilanguage.spider.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class SdCardDao {
	private static final String TAG = "SdCardDao";
	
	/**
	 * Determines whether the SD Card is available for writing, or not.
	 * 
	 * Code based on: http://developer.android.com/guide/topics/data/data-storage.html.
	 */
	public static Boolean isWritable() {
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageWriteable = false;
		}
		
		return mExternalStorageWriteable;
	}

	/**
	 * Writes the given String to a file with the given path and given file name.
	 * 
	 * @return The absolute path of the new file.
	 */
	public static String writeToFile(String fileContents, String path, String filename) {
		// Make sure the directory has been created for this path
		(new File(path)).mkdirs();
		
		// File handle for the new file
		File file = new File(path, filename);

		// Store the string in the file
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(fileContents);
        } catch (IOException e) {
        	Log.e(TAG, e.getMessage());
        } finally {
        	if (writer != null) {
            	try {
            		writer.close( );
            	} catch (Exception e) {
            		// Do nothing
            	}
            }
        }
        
        return file.getAbsolutePath();
	}
	

	/**
	 * Copies a file from the given URL to a file with the given path and given file name.
	 * 
	 * Code based on:
	 * http://www.helloandroid.com/tutorials/how-download-fileimage-url-your-device
	 */
	public static String downloadFromUrl(String urlString, String path, String filename) {
		FileOutputStream fos = null;
		try {
			// Make sure the directory has been created for this path
			(new File(path)).mkdirs();
			
			// File handle for the new file
			File file = new File(path, filename);

			// Open a connection to that URL
			java.net.URL url = new java.net.URL(urlString);
			URLConnection ucon = url.openConnection();

			//Define InputStreams to read from the URLConnection.
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			// Read bytes to the Buffer until there is nothing more to read(-1)
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			// Convert the Bytes read to a String and store them in the file
			fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			
			return file.getAbsolutePath();
		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					// Do nothing
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Get the contents of the given file as a single String.
	 */
	public static String readFromFile(Context ctx, String filename) {
		File file = new File(ctx.getExternalFilesDir(null), filename);
		
	    StringBuilder fileContents = new StringBuilder();
	    
	    BufferedReader input = null;
	    try {
    		input =  new BufferedReader(new FileReader(file));
    		
			String line = null;
			while ((line = input.readLine()) != null) {
				fileContents.append(line);
				fileContents.append(System.getProperty("line.separator"));
			}
    	} catch (IOException e) {
    		Log.e(TAG, e.getMessage());
	    } finally {
	    	if (input != null) {
	    		try {
	    			input.close();
	    		} catch (Exception e) {
	    			// Do nothing
	    		}
	    	}
	    }
	    
	    return fileContents.toString();
	}
}
