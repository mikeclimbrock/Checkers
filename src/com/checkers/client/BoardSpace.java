package com.checkers.client;

public class BoardSpace {

	private String style;
	private boolean occupied;
	private Piece piece;

	
	public BoardSpace(String style){
		this(style, false, null);
	}
	
	public BoardSpace(String style, boolean occupied, Piece piece){
		this.style = style;
		this.occupied = occupied;
		this.piece = piece;
	}
	
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}
	
	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}
}