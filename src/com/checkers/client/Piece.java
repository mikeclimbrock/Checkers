package com.checkers.client;

public class Piece {

	private String imgName;
	private Player owner;
	private boolean selected;
	private boolean isKing;


	public Piece(String imgName, Player player, boolean selected){
		this(imgName, player, selected, false);
	}
	
	public Piece(String imgName, Player player, boolean selected, boolean isKing){
		this.imgName = imgName;
		this.owner = player;
		this.selected = selected;
		this.isKing = isKing;
	}
	
	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}
	
	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player player) {
		this.owner = player;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isKing() {
		return isKing;
	}

	public void setKing(boolean isKing) {
		this.isKing = isKing;
	}
}