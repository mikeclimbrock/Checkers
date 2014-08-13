package com.checkers.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.touch.client.Point;

public class Player {

	private String name;
	private int numPieces;
	private boolean isTurn;

	private static int NUM_PIECES_START = 12;

	public Player(String name){
		this.name = name;
		this.numPieces = NUM_PIECES_START;
		this.isTurn = false;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getNumPieces() {
		return numPieces;
	}

	public void setNumPieces(int numPieces) {
		this.numPieces = numPieces;
	}
	
	public boolean isTurn() {
		return isTurn;
	}

	public void setTurn(boolean isTurn) {
		this.isTurn = isTurn;
	}
	
	private HashMap<Point, ArrayList<Point>> getRoutes(Point from, ArrayList<Point> possibleMoves){
		HashMap<Point, ArrayList<Point>> routes = new HashMap<Point, ArrayList<Point>>();
		
		if(possibleMoves.size() > 0){
			if(routes.containsKey(from)){
				ArrayList<Point> value = routes.get(from);
				value.addAll(possibleMoves);
				routes.put(from, value);
			}
			else{
				ArrayList<Point> value = new ArrayList<Point>();
				value.addAll(possibleMoves);
				routes.put(from, value);
			}
		}
		
		return routes;
	}
	
	private ArrayList<Point> getAdjacentMoves(int row, int col, int direction, List<List<BoardSpace>> internalBoard, Moves moves){
		ArrayList<Point> possibleAdjacentMoves = new ArrayList<Point>();
		
		possibleAdjacentMoves.addAll(moves.checkAdjacentMoves(row, col, direction, internalBoard));
		
		if(internalBoard.get(row).get(col).getPiece().isKing()){
			possibleAdjacentMoves.addAll(moves.checkAdjacentMoves(row, col, direction * -1, internalBoard));			
		}
		
		return possibleAdjacentMoves;
	}
	
	public ArrayList<Point> getJumpMoves(int row, int col, int direction, List<List<BoardSpace>> internalBoard, Moves moves, Player opponent){
		ArrayList<Point> possibleJumpMoves = new ArrayList<Point>();
		
		possibleJumpMoves.addAll(moves.checkJumpMoves(row, col, direction, internalBoard, opponent));
		
		if(internalBoard.get(row).get(col).getPiece().isKing()){
			possibleJumpMoves.addAll(moves.checkJumpMoves(row, col,  direction * -1, internalBoard, opponent));				
		}
		
		return possibleJumpMoves;
	}
	
	public HashMap<Point, ArrayList<Point>> getAdjacentRoutes(Point currCoord, int direction, List<List<BoardSpace>> internalBoard, Moves moves){
		HashMap<Point, ArrayList<Point>> adjacentRoutes = new HashMap<Point, ArrayList<Point>>();
		ArrayList<Point> possibleAdjacentMoves = new ArrayList<Point>();
		
		possibleAdjacentMoves = getAdjacentMoves((int)currCoord.getX(), (int)currCoord.getY(), direction, internalBoard, moves);
		
		adjacentRoutes = getRoutes(currCoord, possibleAdjacentMoves);
		
		return adjacentRoutes;
	}
	
	public HashMap<Point, ArrayList<Point>> getJumpRoutes(Point currCoord, int direction, List<List<BoardSpace>> internalBoard, Moves moves, Player opponent){
		HashMap<Point, ArrayList<Point>> jumpRoutes = new HashMap<Point, ArrayList<Point>>();
		ArrayList<Point> possibleJumpMoves = new ArrayList<Point>();
		
		possibleJumpMoves = getJumpMoves((int)currCoord.getX(), (int)currCoord.getY(), direction, internalBoard, moves, opponent);
		
		jumpRoutes = getRoutes(currCoord, possibleJumpMoves);
		
		return jumpRoutes;
	}
}