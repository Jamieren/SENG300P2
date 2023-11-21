package com.thelocalmarketplace.software;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.AbstractCardReader;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.card.Card.CardSwipeData;
import com.jjjwelectronics.card.CardReaderBronze;
import com.jjjwelectronics.card.CardReaderListener;
import com.jjjwelectronics.card.ICardReader;
import com.thelocalmarketplace.hardware.external.CardIssuer;

import powerutility.NoPowerException;
import powerutility.PowerGrid;

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
 * @author Adrian Brisebois 30170764
 * 
 * Responsible for allowing customer to pay by debit via swipe
 */
public class PayDebitSwipe extends AbstractCardReader implements CardReaderListener{
	
	private Card card = new Card("debit", "1234567890123456", "Bob", "123");
	private CardReaderListener listener;
	private CardIssuer bank = new CardIssuer("bank", 100);
	private double amountDue;
	private CardData data = null;
	private boolean paymentGood = false;
	private Session session;
	
	public boolean payByDebit(){
		amountDue = Session.getInstance().getAmountDue();
		this.plugIn(PowerGrid.instance());
		this.turnOn();
		try {
			data = swipe(card);	
			theDataFromACardHasBeenRead(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			paymentGood = false;
			return paymentGood;
		} catch(NoPowerException e) {
			System.out.println("Card reader has no power");
			paymentGood = false;
			return paymentGood;
		}
		
		return paymentGood;
	}
	
	private boolean amountPaid(String cardNumber, CardData data) {
		long holdNumber = 0;

		holdNumber = bank.authorizeHold(cardNumber, amountDue);

		if(holdNumber != -1) {
			paymentGood = bank.postTransaction(cardNumber, holdNumber, amountDue);
			bank.releaseHold(cardNumber, holdNumber);
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
		Calendar expiry = null;
		String cardNumber = data.getNumber();
		String cardholder = data.getCardholder();
		String cvv = "123";
		expiry = Calendar.getInstance();
		
		bank.addCardData(cardNumber, cardholder, expiry, cvv, 50);
		amountPaid(cardNumber, data);
	}
}




