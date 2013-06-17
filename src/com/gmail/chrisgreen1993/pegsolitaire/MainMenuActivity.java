package com.gmail.chrisgreen1993.pegsolitaire;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

/**
 * Class for main menu activity
 * 
 * @author Chris Green
 *
 */
public class MainMenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	/**
	 * Opens game activity
	 * 
	 * @param view
	 */
	public void openGameActivity(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		startActivity(intent);
		
	}
	
	

}
