package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.TheLocalMarketPlaceDatabase;

import powerutility.PowerGrid;

/**
 *
 *
 * Test class for paying with coin
 *
 */
public class PayWithCoinTest {
	Numeral[] testNumeralArray1 = {Numeral.one} ;
	Barcode testBarcode1 = new Barcode(testNumeralArray1);
	BarcodedItem testItem1 = new BarcodedItem(testBarcode1, Mass.ONE_GRAM);
	BarcodedProduct testProduct1 = new BarcodedProduct(testBarcode1, "1 ml of Water", 1, 1);

	Numeral[] testNumeralArray2 = {Numeral.two} ;
	Barcode testBarcode2 = new Barcode(testNumeralArray2);
	BarcodedItem testItem2 = new BarcodedItem(testBarcode2, Mass.ONE_GRAM.sum(Mass.ONE_GRAM));
	BarcodedProduct testProduct2 = new BarcodedProduct(testBarcode2, "1 ml of Milk", 2, 2);
	
	SelfCheckoutStationSoftware software;
	Session session;
	
	Coin quarter, loonie, toonie, dime, nickel;
	
	
	@Before
	public void setup() {
		software = new SelfCheckoutStationSoftware();
		SelfCheckoutStationBronze.configureCurrency(Currency.getInstance("CAD"));
		SelfCheckoutStationBronze.configureBanknoteDenominations(new BigDecimal[] {new BigDecimal("5.0")});
		SelfCheckoutStationBronze.configureBanknoteStorageUnitCapacity(10);
		SelfCheckoutStationBronze.configureCoinDenominations(new BigDecimal[] {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1"), new BigDecimal("2")});
		SelfCheckoutStationBronze.configureCurrency(Currency.getInstance("CAD"));
		SelfCheckoutStationBronze.configureCoinStorageUnitCapacity(10);
		SelfCheckoutStationBronze.configureCoinTrayCapacity(20);
		SelfCheckoutStationBronze.configureCoinDispenserCapacity(20);
		
		software.setDataBase(new TheLocalMarketPlaceDatabase());
		software.initSelfStationBronze();
		software.initSession();
		
		software.getDatabase().addBarcodedProductToDatabase(testProduct1);
		software.getDatabase().addBarcodedProductToDatabase(testProduct2);
		
		session = new Session();
		
		nickel = new Coin(Currency.getInstance("CAD"), new BigDecimal("0.05"));
		dime = new Coin(Currency.getInstance("CAD"), new BigDecimal("0.10"));
		quarter = new Coin(Currency.getInstance("CAD"), new BigDecimal("0.25"));
		loonie = new Coin(Currency.getInstance("CAD"), new BigDecimal("1"));
		toonie = new Coin(Currency.getInstance("CAD"), new BigDecimal("2"));
		
	}


	@Test
	public void exactCoinsInputed() {
		session.addAmountDue(0.75);
		ArrayList<Coin> coinsList = new ArrayList<>();
		coinsList.add(quarter);
		coinsList.add(quarter);
		coinsList.add(quarter);
		software.payWithCoin(coinsList);
		double expected = 0.0;
		double actual = session.getAmountDue();
		double smallValue = 0.0001;
	    assertEquals(expected, actual, smallValue);
		
	}
	
	@Test
	public void moreThanAmountCoinsInputed() {
		session.addAmountDue(0.75);
		ArrayList<Coin> coinsList = new ArrayList<>();
		coinsList.add(quarter);
		coinsList.add(dime);
		coinsList.add(loonie);
		software.payWithCoin(coinsList);
		double expectedChange = (-0.60);
		double actualChange = session.getAmountDue();
		double smallValue = 0.0001;
	    assertEquals(expectedChange, actualChange, smallValue);
	}
	
	@Test
	public void lessAmountCoinsInputed() {
		session.addAmountDue(0.75);
		ArrayList<Coin> coinsList = new ArrayList<>();
		coinsList.add(quarter);
		coinsList.add(dime);
		software.payWithCoin(coinsList);
		double expected = 0.40;
		double actual = session.getAmountDue();
		double smallValue = 0.0001;
	    assertEquals(expected, actual, smallValue);
	}
	
	@Test
	public void zeroCoinsInputed() {
		session.addAmountDue(0.54);
		ArrayList<Coin> coinsList = new ArrayList<>();
		software.payWithCoin(coinsList);
		double expected = 0.54;
		double actual = session.getAmountDue();
		double smallValue = 0.0001;
	    assertEquals(expected, actual, smallValue);
	}
	
	// might put in null pointer test

	
}