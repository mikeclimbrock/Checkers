package com.checkers.client;

import java.math.BigDecimal;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.Element;

public class FadeAnimation extends Animation {

 private Element element;
 private double opacityIncrement;
 private double targetOpacity;
 private double baseOpacity;
 private boolean isWinningFade;
 private CheckerBoard checkerBoard;

 public FadeAnimation(Element element, boolean isWinningFade, CheckerBoard checkerBoard) {
  this.element = element;
  this.isWinningFade = isWinningFade;
  this.checkerBoard = checkerBoard;
 }
 
 @Override
 protected void onUpdate(double progress) {
  element.getStyle().setOpacity(baseOpacity + progress * opacityIncrement);
 }
 
 @Override
 protected void onComplete() {
  super.onComplete();
  element.getStyle().setOpacity(targetOpacity);
  CheckerBoard.isFading = false;
  if(isWinningFade){
	  checkerBoard.congratulateWinner();
  }
 }
 
 public void fade(int duration, double targetOpacity) {
  if(targetOpacity > 1.0) {
   targetOpacity = 1.0;
  }
  if(targetOpacity < 0.0) {
   targetOpacity = 0.0;
  }
  this.targetOpacity = targetOpacity;
  String opacityStr = element.getStyle().getOpacity();
  if(opacityStr.equals("")){
	  if(targetOpacity == 1.0)
		  opacityStr = "0.0";
	  else
		  opacityStr = "1.0";
  }
  try {
   baseOpacity = new BigDecimal(opacityStr).doubleValue();
   opacityIncrement = targetOpacity - baseOpacity;
   run(duration);
  } catch(NumberFormatException e) {
   // set opacity directly
   onComplete();
  }
 }

}