package com.rworld.invasion;

import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.rworld.core.GameActivity;
import com.rworld.core.GameState;
import com.rworld.core.OnLoadGameStateSoundListener;
import com.rworld.core.graphics.Surface;
import com.rworld.core.states.MenuState;
import com.rworld.core.states.StyleProperty;
import com.rworld.core.states.TimerState;
import com.rworld.core.states.menu.MenuControl;
import com.rworld.core.states.menu.OnLoadMenuStateListener;
import com.rworld.core.states.menu.controls.ChoiceItem;
import com.rworld.core.states.menu.controls.MenuButtonControl;
import com.rworld.core.states.menu.controls.MenuChoiceControl;
import com.rworld.core.states.menu.controls.MenuLabelControl;
import com.rworld.core.states.menu.controls.MenuPanelControl;
import com.rworld.core.states.menu.controls.OnTouchListener;
import com.rworld.core.states.menu.effects.HideInEffect;
import com.rworld.core.states.menu.effects.HideOutEffect;
import com.rworld.core.states.menu.effects.SlideLeftInEffect;
import com.rworld.core.states.menu.effects.SlideRightInEffect;
import com.rworld.core.states.menu.effects.SlideLeftOutEffect;
import com.rworld.core.states.menu.effects.SlideRightOutEffect;
import com.rworld.invasion.R;
import com.rworld.invasion.database.EntityTable;
import com.rworld.invasion.database.WaveTable;

public class MainActivity extends GameActivity {

	public static final int STATE_ALL_TO_MENU = 0;
	public static final int STATE_MENU_TO_LOADLEVEL = 0;
	public static final int STATE_MENU_TO_GALLERY = 1;
	public static final int STATE_MENU_TO_OPTIONS = 2;
	public static final int STATE_MENU_TO_QUIT = 3;
	public static final int STATE_PLAY_TO_NEXTLEVEL = 1;
	public static final int STATE_PLAY_TO_HIGHSCORE = 2;
	
    public static final int CREDITS_DIALOG = 0;
    public static final int PROGRESS_DIALOG = 1;
    public static final int HIGHSCORE_DIALOG = 2;
    
    public static int SoundClickId;
    public static int SoundShotId;
    public static int SoundBangId;
    
    public static EntityTable EntityTable = null;
    public static WaveTable WaveTable = null;
    public static ArrayList<HighScore> HighScores = new ArrayList<HighScore>();
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	if(MainActivity.EntityTable == null) {
    		MainActivity.EntityTable = new EntityTable(this);
    	}
    	if(MainActivity.WaveTable == null) {
    		MainActivity.WaveTable = new WaveTable(this);
    	}
    	
    	GameActivity.GraphicLevel = getPreferences(0).getInt("GraphicLevel", GameActivity.GRAPHIC_MED);
    	GameActivity.IsMusicOn = getPreferences(0).getBoolean("IsMusicOn", true);
    	GameActivity.IsSoundOn = getPreferences(0).getBoolean("IsSoundOn", true);
    }
    
    @Override
	protected void onPause() {
    	SharedPreferences.Editor ed = getPreferences(0).edit();
    	ed.putInt("GraphicLevel", GameActivity.GraphicLevel);
    	ed.putBoolean("IsMusicOn", GameActivity.IsMusicOn);
    	ed.putBoolean("IsSoundOn", GameActivity.IsSoundOn);
    	ed.putBoolean("IsAutoShootOn", _playState.isAutoShootOn);
    	ed.putInt("TypeofControl", _playState.typeofControl);
    	ed.putBoolean("IsNewGame", _playState.isNewGame);
    	ed.putInt("Score", _playState.score);
    	ed.putInt("Level", _playState.level);
        ed.commit();
		super.onPause();
	}
    
    @Override
	protected void loadStates() {
    	_splashState.background.set("screens/splash.png", 256, 256);
    	_splashState.timer = 2.0f;
    	_splashState.next = _menuState;
    	setInitialState(_splashState);
  
    	_menuState.music.filePath = "musics/cantina.mp3";
    	_menuState.font.set("fonts/font16x16.png", 16, 16);
    	_menuState.background.set("screens/back1_1.png", 256, 256);
    	_menuState.decoration.set("screens/panels.png", 256, 256);
    	_menuState.addCase(MainActivity.STATE_MENU_TO_LOADLEVEL, _loadLevelState);
    	_menuState.addCase(MainActivity.STATE_MENU_TO_GALLERY, _galleryState);
    	_menuState.addCase(MainActivity.STATE_MENU_TO_OPTIONS, _optionsState);
    	_menuState.addCase(MainActivity.STATE_MENU_TO_QUIT, GameState.FinalState);
    	_menuState.onLoadGameStateSoundListener = new OnLoadGameStateSoundListener() {
			public void onLoadSounds(GameState gameState) {
				_menuStateOnLoadSounds(gameState);
			}
    	};
    	_menuState.onLoadMenuStateListener = new OnLoadMenuStateListener() {
    		public void onLoadControls(MenuState menuState) {
    			_menuStateOnLoadControls(menuState);
    		}
    	};
    	
    	_loadLevelState.background.set("screens/loading.png", 256, 256);
    	_loadLevelState.timer = 1.0f;
    	_loadLevelState.next = _playState;	
    	
    	_playState.music.filePath = "musics/theme.mp3";
    	_playState.isAutoShootOn = getPreferences(0).getBoolean("IsAutoShootOn", false);
    	_playState.typeofControl = getPreferences(0).getInt("TypeofControl", PlayState.CONTROL_TILT);
    	_playState.score = getPreferences(0).getInt("Score", 0);
    	_playState.level = getPreferences(0).getInt("Level", 0);
    	_playState.isNewGame = getPreferences(0).getBoolean("IsNewGame", true);
    	_playState.addCase(MainActivity.STATE_ALL_TO_MENU, _menuState);
    	_playState.addCase(MainActivity.STATE_PLAY_TO_NEXTLEVEL, _loadLevelState);
    	_playState.addCase(MainActivity.STATE_PLAY_TO_HIGHSCORE, _highScoresState);
    	_playState.onLoadGameStateSoundListener = new OnLoadGameStateSoundListener() {
			public void onLoadSounds(GameState gameState) {
				_playStateOnLoadSounds(gameState);
			}
    	};
    	
    	//_highScoreState.music.filePath = "musics/finish.mp3";
    	_highScoresState.font.set("fonts/font16x16.png", 16, 16);
    	_highScoresState.background.set("screens/back1_1.png", 256, 256);
    	_highScoresState.decoration.set("screens/panels.png", 256, 256);
    	_highScoresState.addCase(MainActivity.STATE_ALL_TO_MENU, _menuState);
    	_highScoresState.onLoadGameStateSoundListener = new OnLoadGameStateSoundListener() {
			public void onLoadSounds(GameState gameState) {
				_highScoresStateOnLoadSounds(gameState);
			}
    	};
    	_highScoresState.onLoadMenuStateListener = new OnLoadMenuStateListener() {
    		public void onLoadControls(MenuState menuState) {
    			_highScoresStateOnLoadControls(menuState);
    		}
    	};
    	
    	_galleryState.font.set("fonts/font16x16.png", 16, 16);
    	_galleryState.background.set("screens/back2.png", 256, 256);
    	_galleryState.decoration.set("screens/panels.png", 256, 256);
    	_galleryState.addCase(MainActivity.STATE_ALL_TO_MENU, _menuState);
    	_galleryState.onLoadGameStateSoundListener = new OnLoadGameStateSoundListener() {
			public void onLoadSounds(GameState gameState) {
				_galleryStateOnLoadSounds(gameState);
			}
    	};
    	_galleryState.onLoadMenuStateListener = new OnLoadMenuStateListener() {
    		public void onLoadControls(MenuState menuState) {
    			_galleryStateOnLoadControls(menuState);
    		}
    	};
    	
    	_optionsState.font.set("fonts/font16x16.png", 16, 16);
    	_optionsState.background.set("screens/back1_1.png", 256, 256);
    	_optionsState.decoration.set("screens/panels.png", 256, 256);
    	_optionsState.addCase(MainActivity.STATE_ALL_TO_MENU, _menuState);
    	_optionsState.onLoadGameStateSoundListener = new OnLoadGameStateSoundListener() {
			public void onLoadSounds(GameState gameState) {
				_optionsStateOnLoadSounds(gameState);
			}
    	};
    	_optionsState.onLoadMenuStateListener = new OnLoadMenuStateListener() {
    		public void onLoadControls(MenuState menuState) {
    			_optionsStateOnLoadControls(menuState);
    		}
    	};
    }
    
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == MainActivity.CREDITS_DIALOG) {
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle(getString(R.string.app_name));
			dialog.setMessage(getString(R.string.credits_text));
			dialog.setButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
		    return dialog;
		}
		else if(id == MainActivity.PROGRESS_DIALOG) {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle(getString(R.string.app_name));
			dialog.setMessage(getString(R.string.please_wait_label));
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			return dialog;
		}
		else if(id == MainActivity.HIGHSCORE_DIALOG) {
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle(getString(R.string.app_name));
			dialog.setMessage(getString(R.string.enter_your_name_label));
			final EditText name = new EditText(this);
			name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			name.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
			dialog.setView(name);
			dialog.setButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					setCustomDialofResult(name.getText().toString());
					dialog.dismiss();
				}
			});
		    return dialog;
		}
		else {
			return super.onCreateDialog(id);
		}
	}
	
	private void _menuStateOnLoadSounds(GameState gameState) {
		try {
			MainActivity.SoundClickId = gameState.musicManager.loadSound(gameState.activity.getAssets().openFd("sounds/click.ogg"));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load sound '" + "sounds/click.ogg" + "'", e);
		}
	}
	
	private void _menuStateOnLoadControls(MenuState menuState) {
		MenuLabelControl title = new MenuLabelControl();
		title.layout.set(50, 40, 750, 50);
		title.backgroundSurface = new Surface(15, 31, 240, 17);
    	title.inEffect = new SlideRightInEffect(0.2f);
    	title.outEffect = new SlideRightOutEffect(0.2f);
    	title.text = getString(R.string.app_name);
    	menuState.addControl(title);
    	MenuButtonControl quit = new MenuButtonControl();
    	quit.layout.set(544, 386, 256, 50);
    	quit.backgroundSurface = new Surface(174, 207, 81, 17);
    	quit.inEffect = new SlideRightInEffect(0.2f);
    	quit.outEffect = new SlideRightOutEffect(0.2f);
    	quit.text = getString(R.string.quit_label);
    	quit.style.align = StyleProperty.ALIGN_CENTER;
    	quit.style.touchedColor = new float[]{0, 0, 1, 1};
    	quit.isBack = true;
    	quit.onTouchListener = new OnTouchListener() {
    		public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				menuControl.menuState.playSound(MainActivity.SoundClickId);
				menuControl.menuState.breakCase(MainActivity.STATE_MENU_TO_QUIT);
			}
    	};
    	menuState.addControl(quit);
    	MenuPanelControl panel = new MenuPanelControl();
    	panel.layout.set(50, 110, 356, 256);
    	panel.backgroundSurface = new Surface(15, 60, 114, 135);
    	panel.inEffect = new SlideLeftInEffect(0.2f);
    	panel.outEffect = new SlideLeftOutEffect(0.2f);
    	menuState.addControl(panel);
    	if(!_playState.isNewGame) {
    		MenuButtonControl playAndContinue = new MenuButtonControl();
    		playAndContinue.text = getString(R.string.continue_game_label);
    		playAndContinue.style.touchedColor = new float[]{0, 0, 1, 1};
    		playAndContinue.onTouchListener = new OnTouchListener() {
	    		public void onTouchPress(MenuControl menuControl) {
				}
				public void onTouchRelease(MenuControl menuControl) {
					menuControl.menuState.playSound(MainActivity.SoundClickId);
					menuControl.menuState.breakCase(MainActivity.STATE_MENU_TO_LOADLEVEL);
				}
	    	};
	    	panel.addControl(playAndContinue);
    	}
    	MenuButtonControl play = new MenuButtonControl();
    	play.text = getString(R.string.new_game_label);
    	play.style.touchedColor = new float[]{0, 0, 1, 1};
    	play.onTouchListener = new OnTouchListener() {
    		public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				_playState.isNewGame = true;
				menuControl.menuState.playSound(MainActivity.SoundClickId);
				menuControl.menuState.breakCase(MainActivity.STATE_MENU_TO_LOADLEVEL);
			}
    	};
    	panel.addControl(play);
    	MenuButtonControl gallery = new MenuButtonControl();
    	gallery.text = getString(R.string.gallery_label);
    	gallery.style.touchedColor = new float[]{0, 0, 1, 1};
    	gallery.onTouchListener = new OnTouchListener() {
    		public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				menuControl.menuState.playSound(MainActivity.SoundClickId);
				menuControl.menuState.breakCase(MainActivity.STATE_MENU_TO_GALLERY);
			}
    	};
    	panel.addControl(gallery);
    	MenuButtonControl options = new MenuButtonControl();
    	options.text = getString(R.string.options_label);
    	options.style.touchedColor = new float[]{0, 0, 1, 1};
    	options.onTouchListener = new OnTouchListener() {
    		public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				menuControl.menuState.playSound(MainActivity.SoundClickId);
				menuControl.menuState.breakCase(MainActivity.STATE_MENU_TO_OPTIONS);
			}
    	};
    	panel.addControl(options);
    	MenuButtonControl credits = new MenuButtonControl();
    	credits.text = getString(R.string.credits_label);
    	credits.style.touchedColor = new float[]{0, 0, 1, 1};
    	credits.onTouchListener = new OnTouchListener() {
    		public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				menuControl.menuState.playSound(MainActivity.SoundClickId);
				menuControl.menuState.activity.showCustomDialog(MainActivity.CREDITS_DIALOG);
			}
    	};
    	panel.addControl(credits);
	}
	
	private void _playStateOnLoadSounds(GameState gameState) {
		try {
			MainActivity.SoundShotId = gameState.musicManager.loadSound(gameState.activity.getAssets().openFd("sounds/shot.ogg"));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load sound '" + "sounds/shot.ogg" + "'", e);
		}
		try {
			MainActivity.SoundBangId = gameState.musicManager.loadSound(gameState.activity.getAssets().openFd("sounds/bang.ogg"));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load sound '" + "sounds/bang.ogg" + "'", e);
		}
	}
	
	private void _highScoresStateOnLoadSounds(GameState gameState) {
		try {
			MainActivity.SoundClickId = gameState.musicManager.loadSound(gameState.activity.getAssets().openFd("sounds/click.ogg"));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load sound '" + "sounds/click.ogg" + "'", e);
		}
	}
	
	private void _highScoresStateOnLoadControls(MenuState menuState) {
		if((MainActivity.HighScores.size() == 0) || (_playState.score > MainActivity.HighScores.get(0).score)) {
			menuState.activity.showCustomDialog(MainActivity.HIGHSCORE_DIALOG);
			final String name = (String) menuState.activity.getCustomDialogResult();
			MainActivity.HighScores.add(0, new HighScore(name, _playState.score));
		}
		MenuLabelControl title = new MenuLabelControl();
		title.layout.set(50, 40, 750, 50);
		title.backgroundSurface = new Surface(15, 31, 240, 17);
    	title.inEffect = new SlideRightInEffect(0.2f);
    	title.outEffect = new SlideRightOutEffect(0.2f);
    	title.text = getString(R.string.high_scores_label);
    	menuState.addControl(title);
		MenuButtonControl back = new MenuButtonControl();
    	back.layout.set(544, 386, 256, 50);
    	back.backgroundSurface = new Surface(174, 207, 81, 17);
    	back.inEffect = new SlideRightInEffect(0.2f);
    	back.outEffect = new SlideRightOutEffect(0.2f);
    	back.text = getString(R.string.back_label);
    	back.style.align = StyleProperty.ALIGN_CENTER;
    	back.style.touchedColor = new float[]{0, 0, 1, 1};
    	back.isBack = true;
    	back.onTouchListener = new OnTouchListener() {
    		public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				menuControl.menuState.playSound(MainActivity.SoundClickId);
				menuControl.menuState.breakCase(MainActivity.STATE_ALL_TO_MENU);
			}
    	};
    	menuState.addControl(back);
    	MenuPanelControl leftPanel = new MenuPanelControl();
    	leftPanel.layout.set(50, 110, 506, 256);
    	leftPanel.backgroundSurface = new Surface(15, 60, 114, 135);
    	leftPanel.inEffect = new SlideLeftInEffect(0.2f);
    	leftPanel.outEffect = new SlideLeftOutEffect(0.2f);
    	menuState.addControl(leftPanel);
    	MenuPanelControl rightPanel = new MenuPanelControl();
    	rightPanel.layout.set(576, 110, 206, 256);
    	rightPanel.backgroundSurface = new Surface(15, 60, 114, 135);
    	rightPanel.inEffect = new SlideRightInEffect(0.2f);
    	rightPanel.outEffect = new SlideRightOutEffect(0.2f);
    	menuState.addControl(rightPanel);
    	for(int i = 0; i < 5; i++) {
    		MenuLabelControl nameLabel = new MenuLabelControl();
    		nameLabel.text = (i < MainActivity.HighScores.size()) ? MainActivity.HighScores.get(i).name : "?";
        	leftPanel.addControl(nameLabel);
        	MenuLabelControl scoreLabel = new MenuLabelControl();
        	scoreLabel.style.align = StyleProperty.ALIGN_RIGHT;
        	scoreLabel.text = (i < MainActivity.HighScores.size()) ? "" + MainActivity.HighScores.get(i).score : "?";
        	rightPanel.addControl(scoreLabel);
		}
	}
	
	private void _galleryStateOnLoadSounds(GameState gameState) {
		try {
			MainActivity.SoundClickId = gameState.musicManager.loadSound(gameState.activity.getAssets().openFd("sounds/click.ogg"));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load sound '" + "sounds/click.ogg" + "'", e);
		}
	}
	
	private void _galleryStateOnLoadControls(MenuState menuState) {
		MenuLabelControl title = new MenuLabelControl();
    	title.layout.set(50, 40, 750, 50);
    	title.backgroundSurface = new Surface(15, 31, 240, 17);
    	title.inEffect = new SlideRightInEffect(0.2f);
    	title.outEffect = new SlideRightOutEffect(0.2f);
    	title.text = getString(R.string.app_name);
    	menuState.addControl(title);
    	MenuButtonControl back = new MenuButtonControl();
    	back.layout.set(544, 386, 256, 50);
    	back.backgroundSurface = new Surface(174, 207, 81, 17);
    	back.inEffect = new SlideRightInEffect(0.2f);
    	back.outEffect = new SlideRightOutEffect(0.2f);
    	back.text = getString(R.string.back_label);
    	back.style.align = StyleProperty.ALIGN_CENTER;
    	back.style.touchedColor = new float[]{0, 0, 1, 1};
    	back.isBack = true;
    	back.onTouchListener = new OnTouchListener() {
    		public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				menuControl.menuState.playSound(MainActivity.SoundClickId);
				menuControl.menuState.breakCase(MainActivity.STATE_ALL_TO_MENU);
			}
    	};
    	menuState.addControl(back);
    	MenuPanelControl panel = new MenuPanelControl();
    	panel.layout.set(426, 110, 356, 256);
    	panel.backgroundSurface = new Surface(15, 60, 114, 135);
    	panel.inEffect = new SlideRightInEffect(0.2f);
    	panel.outEffect = new SlideRightOutEffect(0.2f);
    	panel.autoPack = false;
    	menuState.addControl(panel);
    	MenuLabelControl description = new MenuLabelControl();
    	description.text = "";
    	description.style.fontScale = 1.0f;
    	panel.addControl(description);
    	GalleryControl gallery = new GalleryControl();
    	gallery.layout.set(10, 110, 410, 256);
    	gallery.inEffect = new HideInEffect(0.2f);
    	gallery.outEffect = new HideOutEffect(0.2f);
    	gallery.labelControl = description;
    	menuState.addControl(gallery);
	}
	
	private void _optionsStateOnLoadSounds(GameState gameState) {
		try {
			MainActivity.SoundClickId = gameState.musicManager.loadSound(gameState.activity.getAssets().openFd("sounds/click.ogg"));
		} catch (IOException e) {
			Log.e("Invasion3D", "couldn't load sound '" + "sounds/click.ogg" + "'", e);
		}
	}
	
	private void _optionsStateOnLoadControls(MenuState menuState) {
		MenuLabelControl title = new MenuLabelControl();
		title.layout.set(50, 40, 750, 50);
    	title.backgroundSurface = new Surface(15, 31, 240, 17);
    	title.inEffect = new SlideRightInEffect(0.2f);
    	title.outEffect = new SlideRightOutEffect(0.2f);
    	title.text = getString(R.string.app_name);
    	menuState.addControl(title);
    	MenuButtonControl save = new MenuButtonControl();
    	save.layout.set(544, 386, 256, 50);
    	save.backgroundSurface = new Surface(174, 207, 81, 17);
    	save.inEffect = new SlideRightInEffect(0.2f);
    	save.outEffect = new SlideRightOutEffect(0.2f);
    	save.text = getString(R.string.save_label);
    	save.style.align = StyleProperty.ALIGN_CENTER;
    	save.style.touchedColor = new float[]{0, 0, 1, 1};
    	save.isBack = true;
    	save.onTouchListener = new OnTouchListener() {
			public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				menuControl.menuState.playSound(MainActivity.SoundClickId);
				menuControl.menuState.breakCase(MainActivity.STATE_ALL_TO_MENU);
			}
    	};
    	menuState.addControl(save);
    	MenuPanelControl leftPanel = new MenuPanelControl();
    	leftPanel.layout.set(50, 110, 506, 256);
    	leftPanel.backgroundSurface = new Surface(15, 60, 114, 135);
    	leftPanel.inEffect = new SlideLeftInEffect(0.2f);
    	leftPanel.outEffect = new SlideLeftOutEffect(0.2f);
    	menuState.addControl(leftPanel);
    	MenuPanelControl rightPanel = new MenuPanelControl();
    	rightPanel.layout.set(576, 110, 206, 256);
    	rightPanel.backgroundSurface = new Surface(15, 60, 114, 135);
    	rightPanel.inEffect = new SlideRightInEffect(0.2f);
    	rightPanel.outEffect = new SlideRightOutEffect(0.2f);
    	menuState.addControl(rightPanel);
    	MenuLabelControl graphicLabel = new MenuLabelControl();
    	graphicLabel.text = getString(R.string.graphic_label);
    	leftPanel.addControl(graphicLabel);
    	MenuChoiceControl graphic = new MenuChoiceControl();
    	graphic.style.color = new float[]{0.5f, 0.5f, 1, 1};
    	graphic.style.touchedColor = new float[]{0, 0, 1, 1};
    	graphic.style.align = StyleProperty.ALIGN_CENTER;
    	graphic.addItem(new ChoiceItem(getString(R.string.low_label), 0));
    	graphic.addItem(new ChoiceItem(getString(R.string.medium_label), 1));
    	graphic.addItem(new ChoiceItem(getString(R.string.high_label), 2));
    	graphic.selectedIndex = GameActivity.GraphicLevel;
    	graphic.onTouchListener = new OnTouchListener() {
			public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				GameActivity.GraphicLevel = ((MenuChoiceControl)menuControl).selectedIndex;
			}
    	};
    	rightPanel.addControl(graphic);
    	MenuLabelControl musicLabel = new MenuLabelControl();
    	musicLabel.text = getString(R.string.music_label);
    	leftPanel.addControl(musicLabel);
    	MenuChoiceControl music = new MenuChoiceControl();
    	music.style.color = new float[]{0.5f, 0.5f, 1, 1};
    	music.style.touchedColor = new float[]{0, 0, 1, 1};
    	music.style.align = StyleProperty.ALIGN_CENTER;
    	music.addItem(new ChoiceItem(getString(R.string.off_label), 0));
    	music.addItem(new ChoiceItem(getString(R.string.on_label), 1));
    	music.selectedIndex = GameActivity.IsMusicOn ? 1 : 0;
    	music.onTouchListener = new OnTouchListener() {
			public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				GameActivity.IsMusicOn = (((MenuChoiceControl)menuControl).selectedIndex == 1);
			}
    	};
    	rightPanel.addControl(music);
    	MenuLabelControl soundLabel = new MenuLabelControl();
    	soundLabel.text = getString(R.string.sound_label);
    	leftPanel.addControl(soundLabel);
    	MenuChoiceControl sound = new MenuChoiceControl();
    	sound.style.color = new float[]{0.5f, 0.5f, 1, 1};
    	sound.style.touchedColor = new float[]{0, 0, 1, 1};
    	sound.style.align = StyleProperty.ALIGN_CENTER;
    	sound.addItem(new ChoiceItem(getString(R.string.off_label), 0));
    	sound.addItem(new ChoiceItem(getString(R.string.on_label), 1));
    	sound.selectedIndex = GameActivity.IsSoundOn ? 1 : 0;
    	sound.onTouchListener = new OnTouchListener() {
			public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				GameActivity.IsSoundOn = (((MenuChoiceControl)menuControl).selectedIndex == 1);
			}
    	};
    	rightPanel.addControl(sound);
    	MenuLabelControl isAutoShootLabel = new MenuLabelControl();
    	isAutoShootLabel.text = getString(R.string.auto_shoot_label);
    	leftPanel.addControl(isAutoShootLabel);
    	MenuChoiceControl autoShoot = new MenuChoiceControl();
    	autoShoot.style.color = new float[]{0.5f, 0.5f, 1, 1};
    	autoShoot.style.touchedColor = new float[]{0, 0, 1, 1};
    	autoShoot.style.align = StyleProperty.ALIGN_CENTER;
    	autoShoot.addItem(new ChoiceItem(getString(R.string.off_label), 0));
    	autoShoot.addItem(new ChoiceItem(getString(R.string.on_label), 1));
    	autoShoot.selectedIndex = _playState.isAutoShootOn ? 1 : 0;
    	autoShoot.onTouchListener = new OnTouchListener() {
			public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				_playState.isAutoShootOn = (((MenuChoiceControl)menuControl).selectedIndex == 1);
			}
    	};
    	rightPanel.addControl(autoShoot);
    	MenuLabelControl typeofControlLabel = new MenuLabelControl();
    	typeofControlLabel.text = getString(R.string.control_label);
    	leftPanel.addControl(typeofControlLabel);
    	MenuChoiceControl typeofControl = new MenuChoiceControl();
    	typeofControl.style.color = new float[]{0.5f, 0.5f, 1, 1};
    	typeofControl.style.touchedColor = new float[]{0, 0, 1, 1};
    	typeofControl.style.align = StyleProperty.ALIGN_CENTER;
    	typeofControl.addItem(new ChoiceItem(getString(R.string.tilt_label), 0));
    	typeofControl.addItem(new ChoiceItem(getString(R.string.touch_label), 1));
    	typeofControl.selectedIndex = _playState.typeofControl;
    	typeofControl.onTouchListener = new OnTouchListener() {
			public void onTouchPress(MenuControl menuControl) {
			}
			public void onTouchRelease(MenuControl menuControl) {
				_playState.typeofControl = ((MenuChoiceControl)menuControl).selectedIndex;
			}
    	};
    	rightPanel.addControl(typeofControl);
	}
	
	private TimerState _splashState  = new TimerState();
    private MenuState _menuState = new MenuState();
    private TimerState _loadLevelState  = new TimerState();
    private PlayState _playState = new PlayState();
    private MenuState _highScoresState  = new MenuState();
    private MenuState _galleryState = new MenuState();
    private MenuState _optionsState = new MenuState();
}