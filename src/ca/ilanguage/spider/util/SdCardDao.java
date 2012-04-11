package ca.ilanguage.spider.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
	 * Uses the given context to save the given file contents to the SD card with the given file name.
	 * 
	 * @return The absolute path of the new file.
	 */
	public static String writeToFile(Context ctx, String filename, String fileContents) {
		// Create the directory if it does not exist
		ctx.getExternalFilesDir(null).mkdirs();
		
		File file = new File(ctx.getExternalFilesDir(null), filename);

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
