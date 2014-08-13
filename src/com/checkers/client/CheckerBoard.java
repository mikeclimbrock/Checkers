package com.checkers.client;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

public class CheckerBoard extends Grid{

	private List<List<BoardSpace>> internalBoard = new ArrayList<List<BoardSpace>>();
	private List<List<BoardSpace>> previousInternalBoard;
	private List<FadeObject> fadeObjects;
	private List<Point> computerPieces; 
	private List<Point> humanPieces;

	private FadeAnimation fadeAnimation;
	private Moves moves;
	private Grid board;
	
	private Player player1;
	private Player player2;
	
	private int currX;
	private int currY;
	private int lastX;
	private int lastY;
	private int staleMateIndicator = 0;
	private int tieIndicator = 0;
	private int computerTotalWins = 0;

	private int playerTotalWins = 0;
	private int tieTotalWins = 0;
	
	private boolean computerJumpMade = false;
	private boolean comboJumpTimer = false;
	
	public static boolean isFading = false;
	

	public CheckerBoard(Player player1, Player player2){
		this(player1, player2, 0, 0, 0);
	}
	
	public CheckerBoard(Player player1, Player player2, int cw, int pw, int tw){
		this.player1 = player1;
		this.player2 = player2;
		moves = new Moves();
		fadeObjects = new ArrayList<FadeObject>();
		resetLastSelectedPieceCell();
		initInternalBoard();
		initBoard();
		initPieces();
		
		fadeTimer.scheduleRepeating(500);
		player1.setTurn(true);
		
		this.computerTotalWins = cw;
		this.playerTotalWins = pw;
		this.tieTotalWins = tw;
		
		RootPanel.get(Constants.PLAYER_WINS_HTML_ELEMENT).getElement().setInnerHTML(Integer.toString(playerTotalWins));
		RootPanel.get(Constants.COMPUTER_WINS_HTML_ELEMENT).getElement().setInnerHTML(Integer.toString(computerTotalWins));
		RootPanel.get(Constants.TIE_WINS_HTML_ELEMENT).getElement().setInnerHTML(Integer.toString(tieTotalWins));
	}
	
	final Timer timer = new Timer(){
        @Override
        public void run() {
            if(comboJumpTimer) {
            	setComboJumpTimerRunning(false);
            	reInitBoard();
            	moves.clearPossibleJumps();
            	if(!isGameOver() && !isStalemateGame()){
            		player1.setTurn(false);
            		internalBoard.get(currX).get(currY).getPiece().setSelected(false);
                	resetLastSelectedPieceCell();
            		player2.setTurn(true);
            		computerTimer.schedule(Constants.COMPUTER_WAIT_INTERVAL);
            	}
            	else{
            		//congratulateWinner();
            	}
            } else {
               timer.cancel();
            }
        }
    };
    
    final Timer computerTimer = new Timer(){
        @Override
        public void run() {
        	if(isGameOver() || isStalemateGame()){
        		computerTimer.cancel();
	    		//congratulateWinner();
	    	}
        	else if(player2.isTurn()){
        		heuristicMove();
        	}
        }
    };
    
    final Timer fadeTimer = new Timer(){
        @Override
        public void run() {
        	while(fadeObjects.size() > 0 && !isFading){
        		isFading = true;
        		boolean isWinningFade = false;
        		if((isGameOver() || isStalemateGame()) && fadeObjects.size() == 1){
        			isWinningFade = true;
        		}
        		fadeAnimation = new FadeAnimation(board.getWidget(fadeObjects.get(0).getX(), fadeObjects.get(0).getY()).getElement(), isWinningFade, getCheckerBoard());
        		fadeAnimation.fade(Constants.FADE_DURATION, fadeObjects.get(0).getOpacity());
        		fadeObjects.remove(0);
        	}
        }
    };
    
	public void initInternalBoard(){
		for(int row = 0; row < Constants.BOARD_SIZE; row++){
			List<BoardSpace> boardRow = new ArrayList<BoardSpace>(Constants.BOARD_SIZE);
			for(int col = 0; col < Constants.BOARD_SIZE; col++){
				String style = Constants.RED;
				
				if((row + col) % 2 != 0){
					style = Constants.BLACK;
				}
				BoardSpace space = new BoardSpace(style);
				boardRow.add(space);
			}
			internalBoard.add(boardRow);
		}
	}
	
	public void reInitInternalBoard(List<List<BoardSpace>> previousInternalBoard){
		internalBoard = new ArrayList<List<BoardSpace>>();
	
		internalBoard.addAll(previousInternalBoard);
	}
	
	public void reInitBoard(){
		for(int row = 0; row < Constants.BOARD_SIZE; row++){
			for(int col = 0; col < Constants.BOARD_SIZE; col++){
				board.getCellFormatter().setStyleName(row, col, internalBoard.get(row).get(col).getStyle());
			}
		}
	}
	
	public void reInitPieces(){
		computerPieces = new ArrayList<Point>();
		humanPieces = new ArrayList<Point>();
		
		for(int row = 0; row < Constants.BOARD_SIZE; row++){
			for(int col = 0; col < Constants.BOARD_SIZE; col++){
				BoardSpace boardSpace = internalBoard.get(row).get(col);
				
				if(null != boardSpace.getPiece()){
					if(boardSpace.getPiece().getOwner().getName().equalsIgnoreCase(Checkers.PLAYER_ONE_NAME)){
						Piece piece = new Piece(Constants.PLAYER_PIECE, player1, false);
						boardSpace.setPiece(piece);
						boardSpace.setOccupied(true);
						board.setWidget(row, col, new Image(piece.getImgName()));
						
						//keep list of human piece coordinates
						humanPieces.add(new Point(row, col));
					}
					else{
						Piece piece = new Piece(Constants.COMPUTER_PIECE, player2, false);
						boardSpace.setPiece(piece);
						boardSpace.setOccupied(true);
						board.setWidget(row, col, new Image(piece.getImgName()));
						
						//keep list of computer piece coordinates
						computerPieces.add(new Point(row, col));
					}
				}
				else{
					board.setWidget(row, col, null);
				}
			}
		}
		
		player1.setNumPieces(humanPieces.size());
		player2.setNumPieces(computerPieces.size());
		RootPanel.get(Constants.PLAYER_PIECES_AMOUNT).getElement().setInnerHTML(Integer.toString(player1.getNumPieces()));
		RootPanel.get(Constants.COMPUTER_PIECES_AMOUNT).getElement().setInnerHTML(Integer.toString(player2.getNumPieces()));
	}
	
	public void initPieces(){
		computerPieces = new ArrayList<Point>();
		humanPieces = new ArrayList<Point>();
		
		for(int row = 0; row < Constants.BOARD_SIZE; row++){
			for(int col = 0; col < Constants.BOARD_SIZE; col++){
				BoardSpace boardSpace = internalBoard.get(row).get(col);
				if((row + col) % 2 != 0 && row < 3){
					Piece piece = new Piece(Constants.COMPUTER_PIECE, player2, false);
					boardSpace.setPiece(piece);
					boardSpace.setOccupied(true);
					board.setWidget(row, col, new Image(piece.getImgName()));
					
					//keep list of computer piece coordinates
					computerPieces.add(new Point(row, col));
				}
				else if((row + col) % 2 != 0 && row > 4){
				//else if(row == 5 && col == 0){
					Piece piece = new Piece(Constants.PLAYER_PIECE, player1, false);
					boardSpace.setPiece(piece);
					boardSpace.setOccupied(true);
					board.setWidget(row, col, new Image(piece.getImgName()));
					
					//keep list of human piece coordinates
					humanPieces.add(new Point(row, col));
				}
			}
		}
		RootPanel.get(Constants.PLAYER_PIECES_AMOUNT).getElement().setInnerHTML(Integer.toString(player1.getNumPieces()));
		RootPanel.get(Constants.COMPUTER_PIECES_AMOUNT).getElement().setInnerHTML(Integer.toString(player2.getNumPieces()));
		//player1.setNumPieces(1);
		/*internalBoard.get(1).get(0).setPiece(null);
		internalBoard.get(1).get(0).setOccupied(false);
		board.setWidget(1, 0, null);*/
		
		/*internalBoard.get(1).get(0).setPiece(null);
		internalBoard.get(1).get(0).setOccupied(false);
		board.setWidget(1, 0, null);
		
		Piece piece = new Piece(Constants.COMPUTER_PIECE, player2, false, true);
		internalBoard.get(4).get(1).setPiece(piece);
		internalBoard.get(4).get(1).setOccupied(true);
		board.setWidget(4, 1, new Image(piece.getImgName()));
		
		computerPieces.remove(new Point(1, 0));
		computerPieces.add(new Point(4, 1));*/
		/*
		internalBoard.get(1).get(4).setPiece(null);
		internalBoard.get(1).get(4).setOccupied(false);
		board.setWidget(1, 4, null);*/
		
		/*internalBoard.get(6).get(3).setPiece(null);
		internalBoard.get(6).get(3).setOccupied(false);
		board.setWidget(6, 3, null);
		
		Piece piece = new Piece(PLAYER_PIECE, player1, false, true);
		internalBoard.get(3).get(2).setPiece(piece);
		internalBoard.get(3).get(2).setOccupied(true);
		board.setWidget(3, 2, new Image(piece.getImgName()));
		
		humanPieces.remove(new Point(6, 3));
		humanPieces.add(new Point(3, 2));*/
		
	}

	public void initBoard(){
		board = new Grid(Constants.BOARD_SIZE, Constants.BOARD_SIZE);
		board.setCellPadding(0);
		board.setCellSpacing(0);
		reInitBoard();
				
		// mouse event handler
		board.addClickHandler(new ClickHandler() {
		    @Override
		    public void onClick(ClickEvent event) {
		    	//if(isGameOver() || isStalemateGame()){
		    		//congratulateWinner();
		    	//}
		    	if(player1.isTurn()){
		    		computerTimer.schedule(Constants.COMPUTER_WAIT_INTERVAL);
			    	Cell cell = ((HTMLTable)event.getSource()).getCellForEvent(event);
			        
			        //get selected space
			        BoardSpace selectedSpace = internalBoard.get(cell.getRowIndex()).get(cell.getCellIndex());
			        currX = cell.getRowIndex();
			        currY = cell.getCellIndex();
			        
			        //if alternate piece was selected (user changed their mind on which piece to move)
			        if(selectedSpace.isOccupied() && 
			        		selectedSpace.getPiece().getOwner().getName().equalsIgnoreCase(player1.getName()) && 
			        		isNewSpaceSelected() &&
			        		!isComboJumpTimerRunning()){
			        	
			        	//reset currently selected piece
			        	if(lastX != -1 && lastY != -1){
			        		BoardSpace origSelectedSpace = internalBoard.get(lastX).get(lastY);
			        		origSelectedSpace.getPiece().setSelected(false);
			        	}
			        	setLastSelectedPieceCell(currX, currY);
			        	reInitBoard();
			        }
			        
			        //deselect piece if already selected
			        if(selectedSpace.isOccupied() && selectedSpace.getPiece().isSelected() && !isComboJumpTimerRunning()){
			        	selectedSpace.getPiece().setSelected(false);
			        	setLastSelectedPieceCell(currX, currY);
			        	reInitBoard();
			        }
			        
			        //make move if valid space was selected
			        else if(!selectedSpace.isOccupied() &&
			        		board.getCellFormatter().getStyleName(currX, currY).equalsIgnoreCase(Constants.YELLOW)){
			        	
			        	timer.cancel();
				    	
			        	//save board for undo move
			        	savePreviousBoard(internalBoard);
			        	
			        	setComboJumpTimerRunning(false);
				    	staleMateIndicator++;
				    	
			        	//remove old piece
				    	boolean alreadyKing = internalBoard.get(lastX).get(lastY).getPiece().isKing();
				    	boolean isNewKing = false;
			            internalBoard.get(lastX).get(lastY).setOccupied(false);
			            internalBoard.get(lastX).get(lastY).setPiece(null);
			        	
			            //fade out old piece
			            fadeObjects.add(new FadeObject(lastX, lastY, Constants.FADE_OUT_OPACITY));
			            humanPieces.remove(new Point(lastX, lastY));
			            
			            //set new piece
			        	boolean isKing = false;
			        	String playerPiece = Constants.PLAYER_PIECE;
			        	if(currX == 0 || alreadyKing){
			        		isKing = true;
			        		playerPiece = Constants.PLAYER_KING_PIECE;
			        		if(!alreadyKing)
			        			isNewKing = true;
			        	}
			            selectedSpace.setOccupied(true);
			            selectedSpace.setPiece(new Piece(playerPiece, player1, false, isKing));
			            
			            //fade in new piece
			            board.setWidget(currX, currY, new Image(playerPiece));
			            board.getWidget(currX, currY).addStyleName(Constants.INVISIBLE);
			            fadeObjects.add(new FadeObject(currX, currY, Constants.FADE_IN_OPACITY));
			        	humanPieces.add(new Point(currX, currY));
		        	
			        	//remove any jumped pieces
			        	Point moveTo = new Point(currX, currY);
			        	boolean hasJumpedPieceBeenRemoved = false;
			        	
			        	Iterator<Map.Entry<Point, ArrayList<Point>>> it = moves.getPossibleJumps().entrySet().iterator();
			            while (it.hasNext() && !hasJumpedPieceBeenRemoved) {
			                Map.Entry<Point, ArrayList<Point>> entry = (Map.Entry<Point, ArrayList<Point>>)it.next();
			                
			                if(entry.getValue().contains(moveTo)){
			                	
			                	//remove jumped piece
			        			Point jumpedPiece = entry.getKey();
					            internalBoard.get((int)jumpedPiece.getX()).get((int)jumpedPiece.getY()).setOccupied(false);
					            internalBoard.get((int)jumpedPiece.getX()).get((int)jumpedPiece.getY()).setPiece(null);
					        	
					            //fade out old piece
					            fadeObjects.add(new FadeObject((int)jumpedPiece.getX(), (int)jumpedPiece.getY(), Constants.FADE_OUT_OPACITY));

					            //reduce number of opponent pieces
					        	computerPieces.remove(jumpedPiece);
				        		player2.setNumPieces(player2.getNumPieces() - 1);
				        		RootPanel.get(Constants.COMPUTER_PIECES_AMOUNT).getElement().setInnerHTML(Integer.toString(player2.getNumPieces()));
				        		
				        		//reset jumps
				        		moves.clearPossibleJumps();
				        		
				        		//check for combo jumps
				        		highlightPossibleJumpMoves(currX, currY, isNewKing);
				        		
				        		if(!moves.getPossibleJumps().isEmpty()){
				        			//highlight selected space
					        		selectedSpace.getPiece().setSelected(true);	
					        		board.getCellFormatter().setStyleName(currX, currY, Constants.YELLOW);
				        			board.getCellFormatter().setStyleName(lastX, lastY, internalBoard.get(lastX).get(lastY).getStyle());
				        			setLastSelectedPieceCell(currX, currY);
				        			setComboJumpTimerRunning(true);
				        			
				        			//allow time for user to make another jump
				        			timer.schedule(Constants.TIME_TO_MAKE_COMBO_JUMP);
				        		}
				        		
				        		hasJumpedPieceBeenRemoved = true;
				        		staleMateIndicator = 0;
				        	}
			            }
			        	
			        	if(!isComboJumpTimerRunning()){
			        		selectedSpace.getPiece().setSelected(false);
			        		resetLastSelectedPieceCell();
			        		
			        		//repaint board to remove yellow highlights
			        		reInitBoard();
			        		
			        		if(!isGameOver()){
			        			player1.setTurn(false);
			        			player2.setTurn(true);
			        		}
			        	}
			        }
			        
			        //find possible moves
			        else if (!isComboJumpTimerRunning()){
			        	if(selectedSpace.isOccupied() && selectedSpace.getPiece().getOwner().getName().equalsIgnoreCase(player1.getName())){
			        		//highlight selected space
			        		selectedSpace.getPiece().setSelected(true);	
			        		board.getCellFormatter().setStyleName(currX, currY, Constants.YELLOW);
			        		moves.clearPossibleJumps();	
			        		//highlight all moves
			        		highlightPossibleMoves(currX, currY);
			        		setLastSelectedPieceCell(currX, currY);
			        	}
			        }
		    	}
		    }
		    
		    public boolean highlightPossibleMoves(int row, int col){
				List<Point> highlightMoves = new ArrayList<Point>();
				
				highlightMoves.addAll(moves.checkAdjacentMoves(row, col, Constants.DIRECTION_SOUTH_TO_NORTH, internalBoard));
				highlightMoves.addAll(moves.checkJumpMoves(row, col, Constants.DIRECTION_SOUTH_TO_NORTH, internalBoard, player2));
				
				if(internalBoard.get(row).get(col).getPiece().isKing()){
					highlightMoves.addAll(moves.checkAdjacentMoves(row, col, Constants.DIRECTION_NORTH_TO_SOUTH, internalBoard));
					highlightMoves.addAll(moves.checkJumpMoves(row, col, Constants.DIRECTION_NORTH_TO_SOUTH, internalBoard, player2));
				}
				
				for(int i = 0; i < highlightMoves.size(); i ++){
					board.getCellFormatter().setStyleName((int)highlightMoves.get(i).getX(), (int)highlightMoves.get(i).getY(), Constants.YELLOW);
				}
				
				//no possible moves, change turn
				if(highlightMoves.size() == 0){
					return false;
				}
				
				return true;
			}
		    
		    public void highlightPossibleJumpMoves(int row, int col, boolean isNewKing){
		    	List<Point> highlightMoves = new ArrayList<Point>();
		    	
		    	highlightMoves.addAll(moves.checkJumpMoves(row, col, Constants.DIRECTION_SOUTH_TO_NORTH, internalBoard, player2));
				
				if(internalBoard.get(row).get(col).getPiece().isKing() && !isNewKing){
					highlightMoves.addAll(moves.checkJumpMoves(row, col, Constants.DIRECTION_NORTH_TO_SOUTH, internalBoard, player2));
				}
				
				for(int i = 0; i < highlightMoves.size(); i ++){
					board.getCellFormatter().setStyleName((int)highlightMoves.get(i).getX(), (int)highlightMoves.get(i).getY(), Constants.YELLOW);
				}
			}
		});
	}
	
	public void heuristicMove(){
		Map<Point, ArrayList<Point>> adjacentRoutes = new HashMap<Point, ArrayList<Point>>();
		Map<Point, ArrayList<Point>> jumpRoutes = new HashMap<Point, ArrayList<Point>>();
		Point moveFrom = null;
		Point moveTo = null;
		
		for(int i = 0; i < computerPieces.size(); i++){
			int row = (int)computerPieces.get(i).getX();
			int col = (int)computerPieces.get(i).getY();
			Point currCoord = new Point(row, col);
			
			adjacentRoutes.putAll(player2.getAdjacentRoutes(currCoord, Constants.DIRECTION_NORTH_TO_SOUTH, internalBoard, moves));
			jumpRoutes.putAll(player2.getJumpRoutes(currCoord, Constants.DIRECTION_NORTH_TO_SOUTH, internalBoard, moves, player1));
		}
		
		if(!jumpRoutes.isEmpty()){
			int size = jumpRoutes.entrySet().size();
			int item = Random.nextInt(size);
			int i = 0;
			for(Map.Entry<Point, ArrayList<Point>> obj : jumpRoutes.entrySet())
			{
			    if (i == item)
			    	moveFrom = obj.getKey();
			    i = i + 1;
			}
			moveTo = jumpRoutes.get(moveFrom).get(0);
		}
		else if(!adjacentRoutes.isEmpty()){
			List<Point> blockMoves = new ArrayList<Point>();
			blockMoves = getPotentialHumanJumps();
			boolean moveDetermined = false;
			
			Iterator<Map.Entry<Point, ArrayList<Point>>> it = adjacentRoutes.entrySet().iterator();
			
			while (it.hasNext() && !moveDetermined) {
				Map.Entry<Point, ArrayList<Point>> entry = (Map.Entry<Point, ArrayList<Point>>)it.next();
				
				ArrayList<Point> adjMoves = entry.getValue();
				
				for(int i = 0; i < adjMoves.size(); i++){
					if(blockMoves.contains(adjMoves.get(i)) && !moveDetermined){
						moveFrom = entry.getKey();
						moveTo = adjMoves.get(i);
						moveDetermined = true;
					}
				}
			}
            
			if(!moveDetermined){
				int size = adjacentRoutes.entrySet().size();
				int item = Random.nextInt(size);
				int i = 0;
				for(Map.Entry<Point, ArrayList<Point>> obj : adjacentRoutes.entrySet()){
				    if (i == item)
				    	moveFrom = obj.getKey();
				    i = i + 1;
				}
				moveTo = adjacentRoutes.get(moveFrom).get(0);
			}
		}
		else{
			tieIndicator++;
			
			if(isTieGame()){
				congratulateWinner();
			}
		}
		
		adjacentRoutes.clear();
		jumpRoutes.clear();
		
		if(null != moveTo){
			computerPieces.remove(moveFrom);
			computerPieces.add(moveTo);
			boolean isNewKing = makeMove(moveFrom, moveTo);
			while(computerJumpMade && !isNewKing){
				jumpRoutes.putAll(player2.getJumpRoutes(moveTo, Constants.DIRECTION_NORTH_TO_SOUTH, internalBoard, moves, player1));
				
				if(!jumpRoutes.isEmpty()){
					Iterator<Map.Entry<Point, ArrayList<Point>>> it = jumpRoutes.entrySet().iterator();
		            //while (it.hasNext()) {
		            Map.Entry<Point, ArrayList<Point>> entry = (Map.Entry<Point, ArrayList<Point>>)it.next();
		            moveFrom = entry.getKey();
					moveTo = jumpRoutes.get(moveFrom).get(0);
					computerPieces.remove(moveFrom);
					computerPieces.add(moveTo);
					
					makeMove(moveFrom, moveTo);
					jumpRoutes.clear();
				}
				else{
					computerJumpMade = false;
				}
			}
			staleMateIndicator = 0;
			tieIndicator = 0;
		}
		
		if(!isGameOver() && !isStalemateGame()){
			player2.setTurn(false);
			if(checkHumanMovesExist()){
				player1.setTurn(true);
			}
			/*else if(isTieGame()){
				congratulateWinner();
			}*/
			else{
				player2.setTurn(true);
				computerTimer.schedule(Constants.COMPUTER_WAIT_INTERVAL);
			}
		}
		//else{
    		//congratulateWinner();
    	//}
	}
	
	public boolean makeMove(Point moveFrom, Point moveTo){
		staleMateIndicator++;
		computerJumpMade = false;
		
		//remove old piece
		boolean alreadyKing = internalBoard.get((int)moveFrom.getX()).get((int)moveFrom.getY()).getPiece().isKing();
        internalBoard.get((int)moveFrom.getX()).get((int)moveFrom.getY()).setOccupied(false);
        internalBoard.get((int)moveFrom.getX()).get((int)moveFrom.getY()).setPiece(null);
        
        fadeObjects.add(new FadeObject((int)moveFrom.getX(), (int)moveFrom.getY(), Constants.FADE_OUT_OPACITY));
    	
    	//set new piece
    	boolean isKing = false;
    	boolean isNewKing = false;
    	String playerPiece = Constants.COMPUTER_PIECE;
    	if((int)moveTo.getX() == 7 || alreadyKing){
    		isKing = true;
    		playerPiece = Constants.COMPUTER_KING_PIECE;
    		if(!alreadyKing)
    			isNewKing = true;
    	}
    	internalBoard.get((int)moveTo.getX()).get((int)moveTo.getY()).setOccupied(true);
    	internalBoard.get((int)moveTo.getX()).get((int)moveTo.getY()).setPiece(new Piece(playerPiece, player2, false, isKing));
    	
    	//fade in new piece
        board.setWidget((int)moveTo.getX(), (int)moveTo.getY(), new Image(playerPiece));
        board.getWidget((int)moveTo.getX(), (int)moveTo.getY()).addStyleName(Constants.INVISIBLE);

        fadeObjects.add(new FadeObject((int)moveTo.getX(), (int)moveTo.getY(), Constants.FADE_IN_OPACITY));
    	
    	//remove any jumped piece
    	boolean hasJumpedPieceBeenRemoved = false;
    	Iterator<Map.Entry<Point, ArrayList<Point>>> it = moves.getPossibleJumps().entrySet().iterator();
        while (it.hasNext() && !hasJumpedPieceBeenRemoved) {
            Map.Entry<Point, ArrayList<Point>> entry = (Map.Entry<Point, ArrayList<Point>>)it.next();
            
            if(entry.getValue().contains(moveTo)){
    	
	    		int jumpedX = (int)entry.getKey().getX();
	    		int jumpedY = (int)entry.getKey().getY();
	    		
	    		//remove jumped piece
	            internalBoard.get(jumpedX).get(jumpedY).setOccupied(false);
	            internalBoard.get(jumpedX).get(jumpedY).setPiece(null);
	            
	            fadeObjects.add(new FadeObject(jumpedX, jumpedY, Constants.FADE_OUT_OPACITY));
	        	
	        	humanPieces.remove(entry.getKey());
	        	player1.setNumPieces(player1.getNumPieces() - 1);
	    		RootPanel.get(Constants.PLAYER_PIECES_AMOUNT).getElement().setInnerHTML(Integer.toString(player1.getNumPieces()));
	        	hasJumpedPieceBeenRemoved = true;
	        	staleMateIndicator = 0;
	        	
	        	computerJumpMade = true;
	    	}
        }
    	
    	moves.clearPossibleJumps();
    	
    	return isNewKing;
	}

	public boolean checkHumanMovesExist(){
		Map<Point, ArrayList<Point>> adjacentRoutes = new HashMap<Point, ArrayList<Point>>();
		Map<Point, ArrayList<Point>> jumpRoutes = new HashMap<Point, ArrayList<Point>>();
		
		for(int i = 0; i < humanPieces.size(); i++){	
			int row = (int)humanPieces.get(i).getX();
			int col = (int)humanPieces.get(i).getY();
			Point currCoord = new Point(row, col);
			
			adjacentRoutes.putAll(player1.getAdjacentRoutes(currCoord, Constants.DIRECTION_SOUTH_TO_NORTH, internalBoard, moves));
			jumpRoutes.putAll(player1.getJumpRoutes(currCoord, Constants.DIRECTION_SOUTH_TO_NORTH, internalBoard, moves, player2));
		}
		
		moves.clearPossibleJumps();
		if(jumpRoutes.isEmpty() && adjacentRoutes.isEmpty()){
			tieIndicator++;
			
			if(isTieGame()){
				congratulateWinner();
			}
			return false;
		}
		
		return true;
	}
	
	public List<Point> getPotentialHumanJumps(){
		List<Point> jumpDestinations = new ArrayList<Point>();
		Map<Point, ArrayList<Point>> jumpRoutes = new HashMap<Point, ArrayList<Point>>();
		
		for(int i = 0; i < humanPieces.size(); i++){	
			int row = (int)humanPieces.get(i).getX();
			int col = (int)humanPieces.get(i).getY();
			Point currCoord = new Point(row, col);
			
			jumpRoutes.putAll(player1.getJumpRoutes(currCoord, Constants.DIRECTION_SOUTH_TO_NORTH, internalBoard, moves, player2));
		}
		
		moves.clearPossibleJumps();
		
		Iterator<Map.Entry<Point, ArrayList<Point>>> it = jumpRoutes.entrySet().iterator();
        while (it.hasNext()) {
        	Map.Entry<Point, ArrayList<Point>> entry = (Map.Entry<Point, ArrayList<Point>>)it.next();
        	jumpDestinations.addAll(entry.getValue());
        }
		
		return jumpDestinations;
	}

	public Grid getBoard(){
		return this.board;
	}
	
	public CheckerBoard getCheckerBoard(){
		return this;
	}
	
	public boolean isNewSpaceSelected(){
		return (currX != lastX) || (currY != lastY);
	}
	
	public void setLastSelectedPieceCell(int row, int col){
		lastX = row;
		lastY = col;
	}
	
	public void resetLastSelectedPieceCell(){
		lastX = -1;
		lastY = -1;
	}
	
	public boolean isComboJumpTimerRunning() {
		return comboJumpTimer;
	}

	public void setComboJumpTimerRunning(boolean runJumpTimer) {
		this.comboJumpTimer = runJumpTimer;
	}
	
	public boolean isGameOver(){
		return player1.getNumPieces() == 0 || player2.getNumPieces() == 0;
	}
	
	public boolean isTieGame(){
		return tieIndicator == 2;
	}
	
	public boolean isStalemateGame(){
		return staleMateIndicator == 20;
	}
	
	public int getComputerTotalWins() {
		return computerTotalWins;
	}

	public void setComputerTotalWins(int computerTotalWins) {
		this.computerTotalWins = computerTotalWins;
	}

	public int getPlayerTotalWins() {
		return playerTotalWins;
	}

	public void setPlayerTotalWins(int playerTotalWins) {
		this.playerTotalWins = playerTotalWins;
	}

	public int getTieTotalWins() {
		return tieTotalWins;
	}

	public void setTieTotalWins(int tieTotalWins) {
		this.tieTotalWins = tieTotalWins;
	}
	
	private void savePreviousBoard(List<List<BoardSpace>> internalBoard){
		previousInternalBoard = new ArrayList<List<BoardSpace>>();
	
		for(int i = 0; i < internalBoard.size(); i++){
			List<BoardSpace> boardSpaces = new ArrayList<BoardSpace>();
			for(int j = 0; j < internalBoard.get(i).size(); j++){
				Piece piece = null;
				if(null != internalBoard.get(i).get(j).getPiece())
					piece = new Piece(internalBoard.get(i).get(j).getPiece().getImgName(), internalBoard.get(i).get(j).getPiece().getOwner(), internalBoard.get(i).get(j).getPiece().isSelected(), internalBoard.get(i).get(j).getPiece().isKing());
				BoardSpace boardSpace = new BoardSpace(internalBoard.get(i).get(j).getStyle(), internalBoard.get(i).get(j).isOccupied(), piece);
				
				boardSpaces.add(boardSpace);
			}
			previousInternalBoard.add(boardSpaces);
		}
	}
	
	public List<List<BoardSpace>> getPreviousBoard(){
		return previousInternalBoard;
	}
	
	public void congratulateWinner(){
		if(player1.getNumPieces() == 0){
			Window.alert(Constants.COMPUTER_WIN_MSG);
			computerTotalWins++;
			RootPanel.get(Constants.COMPUTER_WINS_HTML_ELEMENT).getElement().setInnerHTML(Integer.toString(computerTotalWins));
		}
		else if(player2.getNumPieces() == 0){
			Window.alert(Constants.PLAYER_WIN_MSG);
			playerTotalWins++;
			RootPanel.get(Constants.PLAYER_WINS_HTML_ELEMENT).getElement().setInnerHTML(Integer.toString(playerTotalWins));
		}
		else{
			Window.alert(Constants.TIE_WIN_MSG);
			tieTotalWins++;
			RootPanel.get(Constants.TIE_WINS_HTML_ELEMENT).getElement().setInnerHTML(Integer.toString(tieTotalWins));
		}

		RootPanel.get("board").remove(0);
		player1 = new Player(Checkers.PLAYER_ONE_NAME);
		player2 = new Player(Checkers.PLAYER_TWO_NAME);
		
		CheckerBoard board = new CheckerBoard(player1, player2, computerTotalWins, playerTotalWins, tieTotalWins);
		
		RootPanel.get("board").add(board.getBoard());
	}
}