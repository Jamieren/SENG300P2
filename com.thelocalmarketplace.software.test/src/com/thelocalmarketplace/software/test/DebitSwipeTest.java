package com.thelocalmarketplace.software.test;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.banknote.BanknoteValidator;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.PayDebitSwipe;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.TheLocalMarketPlaceDatabase;
import com.thelocalmarketplace.software.WeightDiscrepancy;

import powerutility.PowerGrid;

public class DebitSwipeTest {
	private Session session;
	private SelfCheckoutStationSoftware simulation;
	private TheLocalMarketPlaceDatabase database;
	private ArrayList<BarcodedItem> orderItems;
	
	@Before
	public void setup() {
		session = Session.getInstance();
		database = new TheLocalMarketPlaceDatabase();
		orderItems = new ArrayList<BarcodedItem>();

		SelfCheckoutStationBronze.resetConfigurationToDefaults();

		SelfCheckoutStationBronze bronzeStation = new SelfCheckoutStationBronze();
		bronzeStation.plugIn(PowerGrid.instance());
		bronzeStation.turnOn();
				
		PayDebitSwipe payment = new PayDebitSwipe();
	}
	
	@Test
	public void validPayment() {
		Card card = new Card("debit", "1234567890123456", "Bob", "123");
		CardIssuer bank = new CardIssuer("bank", 100);
		simulation.payViaDebit();
		Assert.assertTrue(bank.postTransaction(card.number, bank.authorizeHold(card.number, 10), 10));
	}
}
