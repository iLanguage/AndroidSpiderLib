package ca.ilanguage.spider.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

// Code based on: http://www.javaworld.com/javaworld/jw-11-2004/jw-1101-spider.html
public class Spider {
	private static final String TAG = "Spider";
	
	/**
	 * Get the HTML at the given URL.
	 */
	public static String getHtml(String urlString) {
		BufferedReader reader = null;
		StringBuffer buf = new StringBuffer();
		try {
			URL url = new URL(urlString);
			InputStream response = url.openStream();
			reader = new BufferedReader(new InputStreamReader(response));
			for (String line; (line = reader.readLine()) != null;) {
				buf.append(line);
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					// Do nothing
				}
			}
		}

		return buf.toString();
	}
}