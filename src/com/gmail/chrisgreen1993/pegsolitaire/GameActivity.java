package com.gmail.chrisgreen1993.pegsolitaire;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Game activity class, contains event handling and lays out board
 * 
 * @author Chris Green
 *
 */
public class GameActivity extends Activity {
	
	private Drawable defaultSquare;
	
	private TableLayout gameTableLayout; 
	private PegLayout[][] squares = new PegLayout[7][7];
	private TableRow[] row  = new TableRow[7];
	private PegView[][] pieces = new PegView[7][7];
	private TextView textviewScore;
	
	private int score = 32;
	
	/**
	 * Gets layout from XML and lays out board
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		defaultSquare = getResources().getDrawable(R.drawable.square);
		
		gameTableLayout = (TableLayout) findViewById(R.id.game_table_layout);
		
		int height = dpToPixels(45);
		int width = dpToPixels(45);
		
		/*
		 * Loops through rows and columns creating TableRows and the views indside them,
		 * Sets rows and columns of PegLayouts and PegViews
		 */
		for (int r = 0; r < 7; r++) {
			row[r] = new TableRow(this);
			for (int c = 0; c < 7; c++) {
				if (!((r == 0 || r == 1 || r == 5 || r == 6) && 
					(c == 0 || c == 1 || c == 5 || c == 6))) {
					squares[r][c] = new PegLayout(this, r, c);
					squares[r][c].setBackgroundDrawable(defaultSquare);
					squares[r][c].setOnDragListener(new SquareDragListener());
					if (!((r == 3) && (c == 3))) { 
						pieces[r][c] = new PegView(this, r, c);
						pieces[r][c].setImageResource(R.drawable.peg);
						pieces[r][c].setLayoutParams(new LayoutParams(height,width));
						pieces[r][c].setOnTouchListener(new PegTouchListener());
						squares[r][c].addView(pieces[r][c]);
					}
					row[r].addView(squares[r][c]);
					TableRow.LayoutParams params = (TableRow.LayoutParams)squares[r][c].getLayoutParams();
					params.column = c;
					params.height = height;
					params.width = width;
					squares[r][c].setLayoutParams(params);
				} 
			}
			gameTableLayout.addView(row[r], new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		
		textviewScore = (TextView) findViewById(R.id.textview_score);
		textviewScore.setText(Integer.toString(getScore()));
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	/**
	 * Can't set height and width as device independent pixels, so have to convert
	 * 
	 * @param dps
	 * @return pixels
	 */
	public int dpToPixels(int dps) {
		float scale = getBaseContext().getResources().getDisplayMetrics().density;
		int pixels = (int) (dps * scale + 0.5f);
		return pixels;
	}
	
	/**
	 * Getter for array of PegLayouts which make up the board
	 * 
	 * @return squares
	 */
	public PegLayout[][] getSquares() {
		return squares;
		
	}
	
	/**
	 * Setter for score
	 * 
	 * @param s score to be set to
	 */
	public void setScore(int s) {
		score = s;
	}
	
	/**
	 * Getter for score
	 * 
	 * @return score
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Updates score TextView and opens dialog if 1 Peg remaining
	 * 
	 */
	public void updateTextViewScore() {
		
		int score = getScore();
		if (score == 1) {
			openDialog();
		}
		
		textviewScore.setText(Integer.toString(getScore()));
		
	}
	
	/**
	 * Called when finishGame button is pressed
	 * 
	 * @param view
	 */
	public void finishGame(View view) {
		openDialog();
		
	}

	/**
	 * Sets up the dialog box and related onClickListener which is opened when the game is finished
	 * 
	 */
	public void openDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Game Over");
		builder.setMessage("You had " + getScore() + " pegs left!");
	
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
		        GameActivity.this.finish();
		    }
		});
		
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * Touch listener for dragging of PegViews
	 * Calls startDrag() which is used in the DragListener
	 * Pegs are dragged into PegLayouts which are assigned to DragListeners
	 * 
	 * @author chris
	 *
	 */
	private final class PegTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == event.ACTION_DOWN) {
				//ClipData data = ClipData.newPlainText("", "");
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
				v.startDrag(null, shadowBuilder, v, 0);
				v.setVisibility(View.INVISIBLE);
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * DragListener for PegLayouts in board, waits until something has been dragged over it 
	 * 
	 * @author chris
	 *
	 */
	class SquareDragListener implements OnDragListener {
		
		Drawable defaultSquare = getResources().getDrawable(R.drawable.square);
		Drawable hoverSquare = getResources().getDrawable(R.drawable.square_hover);
		
		@Override
		public boolean onDrag(View v, DragEvent event) {
			PegView view;
			PegLayout oldSquare;
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				v.setBackgroundDrawable(hoverSquare);
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				v.setBackgroundDrawable(defaultSquare);
				break;
			case DragEvent.ACTION_DROP:
				/*
				 * When Peg is dropped move method is called and score is updated
				 */
				view = (PegView) event.getLocalState();
				PegLayout newSquare = (PegLayout) v;
				oldSquare = (PegLayout) view.getParent();
				if (view.move(oldSquare, newSquare, getSquares())) {
					int score = getScore();
					score--;
					setScore(score);
					updateTextViewScore();
				}
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				view = (PegView) event.getLocalState();
				view.setVisibility(View.VISIBLE);
				v.setBackgroundDrawable(defaultSquare);
			default:
				break;
			}
			return true;
		}
		
	}
}

