package ca.ilanguage.spider.services;

import java.util.HashMap;

import ca.ilanguage.spider.util.SdCardDao;
import ca.ilanguage.spider.util.Spider;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class GetOnePage extends IntentService {
	public static final String TAG = "GetOnePage";
	public static final String URL = "URL";
	public static final String CONTENT_URI = "contentUri";
	public static final String URL_COLUMN_NAME = "url";
	public static final String HTML_FILE_COLUMN_NAME = "html";
	public static final String CREATED_COLUMN_NAME = "created";
	public static final String MODIFIED_COLUMN_NAME = "modified";

	public GetOnePage() {
		super("GetOnePage");
	}

	public GetOnePage(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "I am going to spider this URL: " + intent.getStringExtra(GetOnePage.URL));

		// Create a Spider to spider the HTML of the given URL
		// Spider spider = new Spider(intent.getStringExtra(GetOnePage.URL));
		Spider spider = new Spider("https://docs.google.com/spreadsheet/viewform?formkey=dGhEUVU3VHpHdHdYR0t5VGtnT2U2U0E6MQ#gid=0"); // Hardcoded "Math Test" survey

		String fileLocation = "";
		if (SdCardDao.isWritable()) {
			// Save the linked CSS file(s) and modify the HTML to point to the new location(s)
			HashMap<String, String> cssLinks = spider.getAndReplaceCss();
			for (String newLink : cssLinks.keySet()) {
				SdCardDao.downloadFromUrl(cssLinks.get(newLink), getApplicationContext().getExternalFilesDir(null).toString(), newLink);
			}
			
			// TODO Save the CSS file(s) imported in the <style> tag and modify the HTML to point to the new location(s)
			
			// TODO Save the files referenced by the CSS in the <style> tag and modify the HTML to point to the new location(s)
			
			// Save the modified HTML
			fileLocation = SdCardDao.writeToFile(spider.getHtml(), getApplicationContext().getExternalFilesDir(null).toString(), "index.html");
		} else {
			Log.d(TAG, "Could not write to SD card.");
		}

		// Insert a row into the database with the URL and the file location of
		// its HTML.
		insertUrlIntoDatabase(intent.getStringExtra(GetOnePage.CONTENT_URI),
				intent.getStringExtra(GetOnePage.URL_COLUMN_NAME),
				intent.getStringExtra(GetOnePage.URL),
				intent.getStringExtra(GetOnePage.HTML_FILE_COLUMN_NAME),
				fileLocation,
				intent.getStringExtra(GetOnePage.CREATED_COLUMN_NAME),
				intent.getStringExtra(GetOnePage.MODIFIED_COLUMN_NAME));
	}

	private void insertUrlIntoDatabase(String contentUri, String urlName,
			String urlValue, String htmlFileName, String htmlValue,
			String createdDateName, String modifiedDateName) {
		// Put together all the values for the new row
		ContentValues values = new ContentValues();
		values.put(urlName, urlValue);
		values.put(htmlFileName, htmlValue);
		values.put(createdDateName, System.currentTimeMillis());
		values.put(modifiedDateName, System.currentTimeMillis());

		// Insert the new row into the database
		this.getContentResolver().insert(Uri.parse(contentUri), values);
	}
}