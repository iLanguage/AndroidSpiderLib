package ca.ilanguage.spider.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class GetAllDaughters extends IntentService {
	public static final String TAG = "GetAllDaughters";
	public static final String URL = "URL";

	public GetAllDaughters() {
		super("GetAllDaughters");
	}
	
	public GetAllDaughters(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "I am going to spider this URL and all its daughters: " + intent.getStringExtra(GetAllDaughters.URL));
	}

}
