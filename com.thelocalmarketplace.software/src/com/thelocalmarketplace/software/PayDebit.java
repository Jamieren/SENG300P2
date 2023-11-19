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
public class PayDebit{
	
	private CardReaderListener cardReader;
	private CardIssuer bank;
	private double amountDue;
	
	public void payByDebit() {
		amountDue = Session.getInstance().getAmountDue();
		System.out.println("Please swipe your card: ");
		String signature = signaturePrompt();
		
		
	}
	
	private String signaturePrompt() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please sign here: ");
		scanner.close();
		return scanner.nextLine();
	}

}



