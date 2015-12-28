package ru.tomsk.pear.tetris;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Tetris extends Activity {
	private final static int NEWGAME_ID = 1;
	private final static int EXIT_ID = 2;
	private final static int ABOUT_ID = 3;
	private final static int CONTROLS_ID = 4;
	Game _game;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _game = new Game(this);
        setContentView(_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	populateMenu(menu);
    	return (super.onCreateOptionsMenu(menu));
    }
        
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	return (applyMenuItem(item) || super.onOptionsItemSelected(item));
    }
    
    @Override
    public void onOptionsMenuClosed(Menu menu) {
    	_game.resumeGame();
    }
    
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
    	_game.pauseGame();
    	return super.onMenuOpened(featureId, menu);
    }
    
    public void populateMenu(Menu menu) {
    	menu.add(Menu.NONE, NEWGAME_ID, Menu.NONE, "New game");
    	menu.add(Menu.NONE, CONTROLS_ID, Menu.NONE, "Controls");
    	menu.add(Menu.NONE, ABOUT_ID, Menu.NONE, "About");
    	menu.add(Menu.NONE, EXIT_ID, Menu.NONE, "Exit");
    }
    
    public boolean applyMenuItem(MenuItem item) {
    	switch (item.getItemId()) {
    		case NEWGAME_ID:
    			_game.newGame();
    			return true;
    		case EXIT_ID:
    			this.finish();
    		case ABOUT_ID:
    			_game.pauseGame();
    			new AlertDialog.Builder(this)
    			  .setTitle("About")
    			  .setMessage("This instance of Tetris was made by Alexander Bykov, whose thirst for knowledge made him to step into the way of game making :)\n 23 february 2011.")
    			  .setNeutralButton("Close", new DialogInterface.OnClickListener() {
    			     public void onClick(DialogInterface dlg, int sumthin) {
    			       _game.resumeGame();
    			     }
    			  }).show();    			
    			return true;
    		case CONTROLS_ID:
    			_game.pauseGame();
    			new AlertDialog.Builder(this)
    			  .setTitle("How to play")
    			  .setMessage("Move shape aside -- slide aside the screen\n\nQuick fall of shape -- slide down the screen\n\nRotate shape -- tap the screen\n\nHave a nice play ^_^")
    			  .setNeutralButton("Close", new DialogInterface.OnClickListener() {
    			     public void onClick(DialogInterface dlg, int sumthin) {
    			       _game.resumeGame();
    			     }
    			  }).show();
    			
    			return true;
    			
    	}
    	return false;
    }
    	
		
   
}