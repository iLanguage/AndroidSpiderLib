package ca.ilanguage.spider.services;

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
		
		// Get the HTML of the given URL
		String html = Spider.getHtml(intent.getStringExtra(GetOnePage.URL));
		
		// Create a file containing the HTML
		String fileLocation = "";
		if (SdCardDao.isWritable()) {
			fileLocation = SdCardDao.writeToFile(getApplicationContext(), "test.html", html);
		} else {
			Log.d(TAG, "Could not write to SD card.");
		}
		
		// Insert a row into the database with the URL and the file location of its HTML.
		insertUrlIntoDatabase(
				intent.getStringExtra(GetOnePage.CONTENT_URI), 
				intent.getStringExtra(GetOnePage.URL_COLUMN_NAME), 
				intent.getStringExtra(GetOnePage.URL),
				intent.getStringExtra(GetOnePage.HTML_FILE_COLUMN_NAME),
				fileLocation,
				intent.getStringExtra(GetOnePage.CREATED_COLUMN_NAME),
				intent.getStringExtra(GetOnePage.MODIFIED_COLUMN_NAME)
		);
	}

	private void insertUrlIntoDatabase(String contentUri, String urlName, String urlValue, String htmlFileName, String htmlValue, String createdDateName, String modifiedDateName) {
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