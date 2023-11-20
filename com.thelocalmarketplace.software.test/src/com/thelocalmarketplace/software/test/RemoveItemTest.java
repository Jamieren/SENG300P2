package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.TheLocalMarketPlaceDatabase;

public class RemoveItemTest {
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
		
	}
	
	@Test
	public void removeItemBeforeScanned() {
		software.removeItem(testBarcode2);
		assertTrue(software.getSession().getOrderItem().isEmpty());
	}
	@Test
	public void sessionContainItemRemovingItem() {
		// Item was scanned added to bagging area and session item list
		software.getSelfStationBronze().baggingArea.addAnItem(testItem1);
		software.getSession().newOrderItem(testItem1);
		//Get weight
		BarcodedProduct product = software.getDatabase().getBarcodedProductFromDatabase(testBarcode1);
		//Adjust weight
		software.getSession().addTotalExpectedWeight(product.getExpectedWeight());
		//Remove Item From Session
		software.removeItem(testBarcode1);
		assertTrue(software.getSession().getTotalExpectedWeight() == 0);
	}
	@Test
	public void itemNotinSessionAndRemoveItem() {
		software.getSelfStationBronze().baggingArea.addAnItem(testItem1);
		software.getSession().newOrderItem(testItem1);
		software.removeItem(testBarcode2);
	}
}
