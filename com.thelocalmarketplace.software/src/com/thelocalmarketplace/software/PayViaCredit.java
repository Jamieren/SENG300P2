package com.thelocalmarketplace.software;

import java.io.IOException;
import java.util.Scanner;

import com.jjjwelectronics.card.AbstractCardReader;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.Card.CardSwipeData;
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
 * Responsible for allowing customer to pay by credit via swipe
 */

public class PayViaCredit extends AbstractCardReader{
	
	public Card card;
	public Card swipedCard;
	public CardIssuer bank;
	
	public boolean CreditSwipe(TheLocalMarketPlaceDatabase database) {
		Scanner scan = new Scanner(System.in);
		double totalDue = Session.getInstance().getAmountDue();
		System.out.println("Type: ");
		String type = scan.nextLine();
		System.out.println("Card Number: ");
		String cardNum = scan.nextLine();
		System.out.println("Cardholder Name: ");
		String name = scan.nextLine();
		System.out.println("CVV: ");
		String cvv = scan.nextLine();
		
		boolean doesCardExist = database.isCard(cardNum);
		if (doesCardExist == true) {
			swipedCard = database.getCard(cardNum);
			try {
				Card.CardSwipeData data = swipedCard.swipe();
				String company = data.getType();
				bank = database.getBank(company);
;				long holdNumber = bank.authorizeHold(cardNum, totalDue);
				if(holdNumber != -1) {
					boolean paymentGood = bank.postTransaction(cardNum, holdNumber, totalDue);
					bank.releaseHold(cardNum, holdNumber);
					return true;	
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		else {
			System.out.println("The Card does not Exist");
			return false;
		}	
		return false;
	}

}
