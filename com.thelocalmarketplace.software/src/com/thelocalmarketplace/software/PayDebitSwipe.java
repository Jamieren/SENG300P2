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
 * @author Akashdeep Grewal 30179657
 * @author Amira Wishah 30182579
 * @author Ananya Jain 30196069
 * @author Danny Ly 30127144
 * @author Hillary Nguyen 30161137
 * @author Johnny Tran 30140472 
 * @author Minori Olguin 30035923
 * @author Rhett Bramfield 30170520
 * @author Wyatt Deichert 30174611
 * 
 * Responsible for allowing customer to pay by debit via swipe
 */
public class PayDebitSwipe implements CardReaderListener{
	
	private Card card = new Card("debit", "1234567890123456", "Bob", "123");
	private CardReaderListener cardReader;
	private CardIssuer bank = new CardIssuer("bank", 100);
	private double amountDue;
	private Card.CardSwipeData data = null;
	boolean paymentGood = false;
	
	public boolean payByDebit() {
		amountDue = Session.getInstance().getAmountDue();
		System.out.println("Please swipe your card: ");
		cardSwipe(card);
		if(signatureVerify()){
			theDataFromACardHasBeenRead(data);
		}
		return paymentGood;
	}
	
	private Card.CardSwipeData cardSwipe(Card card) {
		try {
			data = card.swipe();
			System.out.println("Card has been swiped");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data;
	}
	
	private boolean signatureVerify() {
		System.out.println("Please sign here: \n");
		return true;
	}
	
	private boolean amountPaid(String cardNumber) {
		long holdNumber = 0;
		bank.unblock(cardNumber);
		holdNumber = bank.authorizeHold(cardNumber, amountDue);
		System.out.println(cardNumber);
		
		if(holdNumber != -1) {
			System.out.println("Hold authorized. Hold Number: " + holdNumber);
			paymentGood = bank.postTransaction(cardNumber, holdNumber, amountDue);
			bank.releaseHold(cardNumber, holdNumber);
			
			if(paymentGood) {
				System.out.println("Success");
			}else {
				System.out.println("Failed");
			}
		}
		return paymentGood;
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
		String cardNumber = data.getNumber();
		amountPaid(cardNumber);
	}
}




