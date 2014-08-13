package com.checkers.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.touch.client.Point;

public class Moves {
	
	Map<Point, ArrayList<Point>> possibleJumps;
	
	public Moves(){
		possibleJumps = new HashMap<Point, ArrayList<Point>>();
	}

	public List<Point> checkAdjacentMoves(int row, int col, int direction, List<List<BoardSpace>> internalBoard){
		List<Point> adjacentMoves = new ArrayList<Point>();
		
		if(direction == -1){
			if(row - 1 >= 0 && col - 1 >= 0){
				BoardSpace possibleSpace = internalBoard.get(row - 1).get(col - 1);
				if(!possibleSpace.isOccupied()){
					adjacentMoves.add(new Point(row - 1, col - 1));
				}
			}
			if(row - 1 >= 0 && col + 1 < 8){
				BoardSpace possibleSpace = internalBoard.get(row - 1).get(col + 1);
				if(!possibleSpace.isOccupied()){
					adjacentMoves.add(new Point(row - 1, col + 1));			
				}
			}
		}
		else{
			if(row + 1 < 8 && col - 1 >= 0){
				BoardSpace possibleSpace = internalBoard.get(row + 1).get(col - 1);
				if(!possibleSpace.isOccupied()){
					adjacentMoves.add(new Point(row + 1, col - 1));
				}
			}
			if(row + 1 < 8 && col + 1 < 8){
				BoardSpace possibleSpace = internalBoard.get(row + 1).get(col + 1);
				if(!possibleSpace.isOccupied()){
					adjacentMoves.add(new Point(row + 1, col + 1));		
				}
			}
		}
		
		return adjacentMoves;
	}
	
	public List<Point> checkJumpMoves(int row, int col, int direction, List<List<BoardSpace>> internalBoard, Player player){
		List<Point> jumpMoves = new ArrayList<Point>();
		
		if(direction == -1){
			if(row - 1 >= 0 && col - 1 >= 0){
				BoardSpace possibleOpponentSpace = internalBoard.get(row - 1).get(col - 1);
				if(possibleOpponentSpace.isOccupied() && possibleOpponentSpace.getPiece().getOwner().getName().equalsIgnoreCase(player.getName())){
					if(row - 2 >= 0 && col - 2 >= 0){
						BoardSpace possibleSpace = internalBoard.get(row - 2).get(col - 2);
						if(!possibleSpace.isOccupied()){
							Point jumpTo = new Point(row - 2, col - 2);
							Point jumpOver = new Point(row - 1, col - 1);
							if(possibleJumps.containsKey(jumpOver)){
								ArrayList<Point> value = possibleJumps.get(jumpOver);
								value.add(jumpTo);
								possibleJumps.put(jumpOver, value);
							}
							else{
								ArrayList<Point> value = new ArrayList<Point>();
								value.add(jumpTo);
								possibleJumps.put(jumpOver, value);
							}
							jumpMoves.add(jumpTo);
							//checkJumpMoves(row - 2, col - 2, DIRECTION_SOUTH_TO_NORTH);
						}
					}
				}
			}
			if(row - 1 >= 0 && col + 1 < 8){
				BoardSpace possibleOpponentSpace = internalBoard.get(row - 1).get(col + 1);
				if(possibleOpponentSpace.isOccupied() && possibleOpponentSpace.getPiece().getOwner().getName().equalsIgnoreCase(player.getName())){
					if(row - 2 >= 0 && col + 2 < 8){
						BoardSpace possibleSpace = internalBoard.get(row - 2).get(col + 2);
						if(!possibleSpace.isOccupied()){
							Point jumpTo = new Point(row - 2, col + 2);
							Point jumpOver = new Point(row - 1, col + 1);
							if(possibleJumps.containsKey(jumpOver)){
								ArrayList<Point> value = possibleJumps.get(jumpOver);
								value.add(jumpTo);
								possibleJumps.put(jumpOver, value);
							}
							else{
								ArrayList<Point> value = new ArrayList<Point>();
								value.add(jumpTo);
								possibleJumps.put(jumpOver, value);
							}
							jumpMoves.add(jumpTo);
							//checkJumpMoves(row - 2, col + 2, DIRECTION_SOUTH_TO_NORTH);
						}
					}
				}
			}
		}
		else{
			if(row + 1 < 8 && col - 1 >= 0){
				BoardSpace possibleOpponentSpace = internalBoard.get(row + 1).get(col - 1);
				if(possibleOpponentSpace.isOccupied() && possibleOpponentSpace.getPiece().getOwner().getName().equalsIgnoreCase(player.getName())){
					if(row + 2 < 8 && col - 2 >= 0){
						BoardSpace possibleSpace = internalBoard.get(row + 2).get(col - 2);
						if(!possibleSpace.isOccupied()){
							Point jumpTo = new Point(row + 2, col - 2);
							Point jumpOver = new Point(row + 1, col - 1);
							if(possibleJumps.containsKey(jumpOver)){
								ArrayList<Point> value = possibleJumps.get(jumpOver);
								value.add(jumpTo);
								possibleJumps.put(jumpOver, value);
							}
							else{
								ArrayList<Point> value = new ArrayList<Point>();
								value.add(jumpTo);
								possibleJumps.put(jumpOver, value);
							}
							jumpMoves.add(jumpTo);
							//checkJumpMoves(row + 2, col - 2, DIRECTION_NORTH_TO_SOUTH);
						}
					}
				}
			}
			if(row + 1 < 8 && col + 1 < 8){
				BoardSpace possibleOpponentSpace = internalBoard.get(row + 1).get(col + 1);
				if(possibleOpponentSpace.isOccupied() && possibleOpponentSpace.getPiece().getOwner().getName().equalsIgnoreCase(player.getName())){
					if(row + 2 < 8 && col + 2 < 8){
						BoardSpace possibleSpace = internalBoard.get(row + 2).get(col + 2);
						if(!possibleSpace.isOccupied()){
							Point jumpTo = new Point(row + 2, col + 2);
							Point jumpOver = new Point(row + 1, col + 1);
							
							if(possibleJumps.containsKey(jumpOver)){
								ArrayList<Point> value = possibleJumps.get(jumpOver);
								value.add(jumpTo);
								possibleJumps.put(jumpOver, value);
							}
							else{
								ArrayList<Point> value = new ArrayList<Point>();
								value.add(jumpTo);
								possibleJumps.put(jumpOver, value);
							}
							
							jumpMoves.add(jumpTo);	
							//checkJumpMoves(row + 2, col + 2, DIRECTION_NORTH_TO_SOUTH);
						}
					}
				}
			}
		}
		
		//setPossibleJumps(possibleJumps);
		
		return jumpMoves;
	}
	

	public Map<Point, ArrayList<Point>> getPossibleJumps() {
		return possibleJumps;
	}

	public void setPossibleJumps(Map<Point, ArrayList<Point>> possibleJumps) {
		this.possibleJumps = possibleJumps;
	}
	
	public void clearPossibleJumps(){
		this.possibleJumps.clear();
	}
}