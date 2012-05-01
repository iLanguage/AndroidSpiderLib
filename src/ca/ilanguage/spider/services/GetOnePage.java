package ca.ilanguage.spider.services;

import java.io.IOException;
import java.util.HashMap;

import ca.ilanguage.spider.bean.SpiderResult;
import ca.ilanguage.spider.util.SdCardDao;
import ca.ilanguage.spider.util.Spider;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class GetOnePage extends IntentService {
	public static final String TAG = "GetOnePage";
	public static final String URL = "URL";
	public static final String FILE_PREFIX = "prefix";
	public static final String CONTENT_URI = "contentUri";
	public static final String URL_COLUMN_NAME = "url";
	public static final String HTML_FILE_COLUMN_NAME = "html";
	public static final String TITLE_COLUMN_NAME = "title";
	public static final String CREATED_COLUMN_NAME = "created";
	public static final String MODIFIED_COLUMN_NAME = "modified";
	public static final String MESSENGER = "messenger";

	public GetOnePage() {
		super("GetOnePage");
	}

	public GetOnePage(String name) {
		super(name);
	} 

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "I am going to spider this URL: " + intent.getStringExtra(GetOnePage.URL));

		Spider spider = null;
		String fileLocation = "";
		String rowId = "";
		
		try {
			// Create a Spider to spider the HTML of the given URL
			spider = new Spider(intent.getStringExtra(GetOnePage.URL));
	
			if (SdCardDao.isWritable()) {
				// Save the linked CSS file(s) and modify the HTML to point to the new location(s)
				HashMap<String, String> cssLinks = spider.getAndReplaceCss(intent.getStringExtra(FILE_PREFIX) + "link", ".css");
				for (String newLink : cssLinks.keySet()) {
					SdCardDao.downloadFromUrl(cssLinks.get(newLink), getApplicationContext().getExternalFilesDir(null).toString(), newLink);
				}
				
				// Save the CSS file(s) imported in the <style> tag and modify the HTML to point to the new location(s)
				HashMap<String, String> importLinks = spider.getAndReplaceImports(intent.getStringExtra(FILE_PREFIX) + "import", ".css");
				for (String newLink : importLinks.keySet()) {
					SdCardDao.downloadFromUrl(importLinks.get(newLink), getApplicationContext().getExternalFilesDir(null).toString(), newLink);
				}
				
				// Save the files referenced by the CSS in the <style> tag and modify the HTML to point to the new location(s)
				HashMap<String, String> fileLinks = spider.getAndReplaceUrls(intent.getStringExtra(FILE_PREFIX) + "url", "");
				for (String newLink : fileLinks.keySet()) {
					SdCardDao.downloadFromUrl(fileLinks.get(newLink), getApplicationContext().getExternalFilesDir(null).toString(), newLink);
				}
				
				// Save the modified HTML
				fileLocation = SdCardDao.writeToFile(spider.getHtml(), getApplicationContext().getExternalFilesDir(null).toString(), intent.getStringExtra(FILE_PREFIX) + "index.html");
			} else {
				Log.d(TAG, "Could not write to SD card.");
			}
			
			// Get the title of the HTML page
			String title = spider.getTitle();
	
			// Insert a row into the database with the URL and the file location of
			// its HTML.
			rowId = insertUrlIntoDatabase(intent.getStringExtra(GetOnePage.CONTENT_URI),
					intent.getStringExtra(GetOnePage.URL_COLUMN_NAME),
					intent.getStringExtra(GetOnePage.URL),
					intent.getStringExtra(GetOnePage.HTML_FILE_COLUMN_NAME),
					fileLocation,
					intent.getStringExtra(GetOnePage.TITLE_COLUMN_NAME),
					title,
					intent.getStringExtra(GetOnePage.CREATED_COLUMN_NAME),
					intent.getStringExtra(GetOnePage.MODIFIED_COLUMN_NAME));
		} catch (IOException e) {
			Log.e(TAG, "Error parsing URL: " + intent.getStringExtra(URL));
		}
		
		// Send a Message back to the caller, if they wanted one
		// Code based on: http://www.vogella.com/articles/AndroidServices/article.html#tutorial_intentservice
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Messenger messenger = (Messenger) extras.get(MESSENGER);
			// If the user gave us a Messenger
			if (messenger != null) {
				Message msg = Message.obtain();
				msg.obj = new SpiderResult((spider != null) && spider.isSpiderInitialized(), intent.getStringExtra(URL), fileLocation, rowId);
				try {
					messenger.send(msg);
				} catch (android.os.RemoteException e) {
					Log.e(getClass().getName(), "Exception sending message", e);
				}
			}
		}
	}

	private String insertUrlIntoDatabase(String contentUri, String urlName,
			String urlValue, String htmlFileName, String htmlValue,
			String titleName, String titleValue, String createdDateName, 
			String modifiedDateName) {
		// Put together all the values for the new row
		ContentValues values = new ContentValues();
		values.put(urlName, urlValue);
		values.put(htmlFileName, htmlValue);
		values.put(titleName, titleValue);
		values.put(createdDateName, System.currentTimeMillis());
		values.put(modifiedDateName, System.currentTimeMillis());

		// Insert the new row into the database and get its row URI
		String uri = this.getContentResolver().insert(Uri.parse(contentUri), values).toString();
		
		// Extract the row ID from the row URI
		String rowId = "";
		if (uri != null) {
			rowId = uri.substring(uri.lastIndexOf("/") + 1);
		}
		
		return rowId;
	}
}