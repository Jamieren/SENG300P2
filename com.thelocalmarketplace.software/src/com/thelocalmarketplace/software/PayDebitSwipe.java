package com.thelocalmarketplace.software;

import java.io.IOException;
import java.util.Scanner;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.card.Card.CardSwipeData;
import com.jjjwelectronics.card.CardReaderListener;
import com.thelocalmarketplace.hardware.external.CardIssuer;

/**
SENG 300 Project Iteration 2

@author Akashdeep Grewal 30179657
@author Amira Wishah 30182579
@author Ananya Jain 30196069
@author Danny Ly 30127144
@author Hillary Nguyen 30161137
@author Johnny Tran 30140472 
@author Minori Olguin 30035923
@author Rhett Bramfield 30170520
@author Wyatt Deichert 30174611
@author Zhenhui Ren 30139966
@author Adrian Brisebois 30170764
 * 
 * Responsible for allowing customer to pay by debit via swipe
 */

public class PayDebitSwipe implements CardReaderListener{
	
	private Card card;
	private CardReaderListener cardReader;
	private CardIssuer bank;
	private double amountDue;
	
	public boolean payByDebit() {
		amountDue = Session.getInstance().getAmountDue();
		System.out.println("Please swipe your card: ");
		cardSwipe(card);
		String cardNumber = this.card.number;
		long holdNumber = bank.authorizeHold(cardNumber, amountDue);
		
		if(holdNumber != -1) {
			boolean paymentGood = bank.postTransaction(cardNumber, holdNumber, amountDue);
			bank.releaseHold(cardNumber, holdNumber);
			
			return paymentGood;
		}
		return false;
	}
	
	private void cardSwipe(Card card) {
		try {
			Card.CardSwipeData data = card.swipe();
			System.out.println("Card has been swiped");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String signaturePrompt() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please sign here: ");
		return scanner.nextLine();
	}

	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void aCardHasBeenSwiped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void theDataFromACardHasBeenRead(CardData data) {
		// TODO Auto-generated method stub
		
	}

}




