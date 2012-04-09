package ca.ilanguage.spider.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class GetOnePage extends IntentService {
	
	public GetOnePage() {
		super("GetOnePage");
	}

	public GetOnePage(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		Log.d("Spider", "Services running");
	}

}
