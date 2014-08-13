package com.checkers.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Checkers implements EntryPoint {

	public static String PLAYER_ONE_NAME = "Human";
	public static String PLAYER_TWO_NAME = "Computer";
	private Player player1;
	private Player player2;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		player1 = new Player(PLAYER_ONE_NAME);
		player2 = new Player(PLAYER_TWO_NAME);
		
		final CheckerBoard board = new CheckerBoard(player1, player2);
		
		RootPanel.get("board").add(board.getBoard());
		
		Button newGameButton = Button.wrap(Document.get().getElementById("newGameButton"));
		newGameButton.addClickHandler(new ClickHandler() {
	    	@Override
	    	public void onClick(ClickEvent event) {
	    		RootPanel.get("board").remove(0);
	    		player1 = new Player(Checkers.PLAYER_ONE_NAME);
	    		player2 = new Player(Checkers.PLAYER_TWO_NAME);
	    		
	    		CheckerBoard board = new CheckerBoard(player1, 
	    											  player2, 
	    											  Integer.parseInt(RootPanel.get(Constants.COMPUTER_WINS_HTML_ELEMENT).getElement().getInnerHTML()),
	    											  Integer.parseInt(RootPanel.get(Constants.PLAYER_WINS_HTML_ELEMENT).getElement().getInnerHTML()),
	    											  Integer.parseInt(RootPanel.get(Constants.TIE_WINS_HTML_ELEMENT).getElement().getInnerHTML()));
	    		
	    		RootPanel.get("board").add(board.getBoard());
	    	}
	    });
		
		Button resetButton = Button.wrap(Document.get().getElementById("resetButton"));
		resetButton.addClickHandler(new ClickHandler() {
	    	@Override
	    	public void onClick(ClickEvent event) {
	    		RootPanel.get("board").remove(0);
	    		player1 = new Player(Checkers.PLAYER_ONE_NAME);
	    		player2 = new Player(Checkers.PLAYER_TWO_NAME);
	    		
	    		CheckerBoard board = new CheckerBoard(player1,player2, 0, 0, 0);
	    		RootPanel.get("board").add(board.getBoard());
	    	}
	    });
		
		Button undoButton = Button.wrap(Document.get().getElementById("undoButton"));
		undoButton.addClickHandler(new ClickHandler() {
	    	@Override
	    	public void onClick(ClickEvent event) {
	    		if(null != board.getPreviousBoard()){
	    			board.reInitInternalBoard(board.getPreviousBoard());
	    			board.reInitPieces();
	    		}
	    	}
	    });
	}
}