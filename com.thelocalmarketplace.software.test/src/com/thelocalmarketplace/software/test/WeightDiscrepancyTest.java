package com.thelocalmarketplace.software.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scale.ElectronicScale;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.Barcode;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinSlot;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutController;
import com.thelocalmarketplace.software.SelfCheckoutSession;

import powerutility.PowerGrid;

/**
 * 
 * @author Robin Bowering UCID 30123373
 * @author Kelvin Jamila UCID 30117164
 * @author Nikki Kim UCID 30189188
 * @author Hillary Nguyen UCID 30161137
 * @author Matt Gibson UCID 30117091
 * 
 * Test class for weight discrepancy
 * 
 */
public class WeightDiscrepancyTest {
	

	Numeral[] testNumeralArray1 = {Numeral.one} ;
	Barcode testBarcode1 = new Barcode(testNumeralArray1);
	BarcodedItem testItem1 = new BarcodedItem(testBarcode1, Mass.ONE_GRAM);
	BarcodedProduct testProduct1 = new BarcodedProduct(testBarcode1, "1 ml of Water", 1, 1);
	
	Numeral[] testNumeralArray2 = {Numeral.two} ;
	Barcode testBarcode2 = new Barcode(testNumeralArray2);
	BarcodedItem testItem2 = new BarcodedItem(testBarcode2, Mass.ONE_GRAM.sum(Mass.ONE_GRAM));
	BarcodedProduct testProduct2 = new BarcodedProduct(testBarcode2, "1 ml of Milk", 2, 2);
	
	Mass oneHundredKilograms = new Mass(100001);
	
	Numeral[] testNumeralArray3 = {Numeral.three} ;
	Barcode testBarcode3 = new Barcode(testNumeralArray3);
	BarcodedItem testItem3 = new BarcodedItem(testBarcode2, oneHundredKilograms);
	BarcodedProduct testProduct3 = new BarcodedProduct(testBarcode2, "brick", 1, 100001);
		
	
	SelfCheckoutStation hardware;
	SelfCheckoutSession session;
	Coin coin;
	
	
	
	@Before 
	public void setup() {
		
		hardware = new SelfCheckoutStation();
        hardware.plugIn(PowerGrid.instance());
        hardware.turnOn();
        
        SelfCheckoutController controller = new SelfCheckoutController(hardware);
        
		session = controller.startSession();
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(testBarcode1, testProduct1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(testBarcode2, testProduct2);
		

	}
	
	@Test(expected = DisabledException.class)
	public void TestCoinSlotBlocked() throws DisabledException, CashOverloadException {
		session.payingForOrder = true;
		session.coinslot.receive(coin);
		session.weightDiscrepancyDetected();
	}
	
	@Test
	public void TestScannerBlocked() {
		session.payingForOrder = false;
		session.weightDiscrepancyDetected();
		assertEquals(true, session.scanner.isDisabled());
	}
	
	@Test
	public void triggerDiscrepancyUnexpectedItem() {
		hardware.baggingArea.addAnItem(testItem1);
		
		assertTrue(session.weightDiscrepancy);
	}
	
	
	@Test
	public void resolveDiscrepancyUnexpectedItem() {
		hardware.baggingArea.addAnItem(testItem1);
		hardware.baggingArea.removeAnItem(testItem1);
		assertFalse(session.weightDiscrepancy);
	}
	
	@Test
	public void triggerDiscrepancyItemRemoved() {
		session.expectedMassOnScale = BigDecimal.valueOf(1);
		hardware.baggingArea.addAnItem(testItem1);
		hardware.baggingArea.removeAnItem(testItem1);
		
		assertTrue(session.weightDiscrepancy);
	}
	
	@Test
	public void scaleOverloadTriggersDiscrepancy() {
		session.expectedMassOnScale = BigDecimal.valueOf(100001);
		hardware.baggingArea.addAnItem(testItem3);
		
		assertTrue(session.weightDiscrepancy);
	}
	
	@After
	public void clearDatabase() {
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
	}
	
}
