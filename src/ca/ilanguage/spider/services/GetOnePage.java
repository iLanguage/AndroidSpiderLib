package ca.ilanguage.spider.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class GetOnePage extends IntentService {
	public static final String TAG = "GetOnePage";
	public static final String URL = "URL";
	
	public GetOnePage() {
		super("GetOnePage");
	}

	public GetOnePage(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "I am going to spider this URL: " + intent.getStringExtra(GetOnePage.URL));
	}
}