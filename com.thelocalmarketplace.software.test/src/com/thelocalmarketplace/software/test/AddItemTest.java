package com.thelocalmarketplace.software.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scale.ElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinSlot;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutController;
import com.thelocalmarketplace.software.SelfCheckoutSession;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.PowerGrid;
/**
 * 
 * @author Robin Bowering UCID 30123373
 * @author Kelvin Jamila UCID 30117164
 * @author Nikki Kim UCID 30189188
 * @author Hillary Nguyen UCID 30161137
 * @author Matt Gibson UCID 30117091
 * 
 * Test class for add item
 * 
 */
public class AddItemTest {
	
	Numeral[] testNumeralArray1 = {Numeral.one} ;
	Barcode testBarcode1 = new Barcode(testNumeralArray1);
	BarcodedItem testItem1 = new BarcodedItem(testBarcode1, Mass.ONE_GRAM);
	BarcodedProduct testProduct1 = new BarcodedProduct(testBarcode1, "1 ml of Water", 1, Mass.ONE_GRAM.inGrams().doubleValue());
	
	Numeral[] testNumeralArray2 = {Numeral.two} ;
	Barcode testBarcode2 = new Barcode(testNumeralArray2);
	BarcodedItem testItem2 = new BarcodedItem(testBarcode2, Mass.ONE_GRAM.sum(Mass.ONE_GRAM));
	BarcodedProduct testProduct2 = new BarcodedProduct(testBarcode2, "1 ml of Milk", 2, Mass.ONE_GRAM.sum(Mass.ONE_GRAM).inGrams().
			doubleValue());
	
		

	SelfCheckoutSession session;
	Coin coin;
	Barcode barcode;

		
	@Before 
	public void setup() {
		SelfCheckoutStation hardware = new SelfCheckoutStation();
        hardware.plugIn(PowerGrid.instance());
        hardware.turnOn();
        
        SelfCheckoutController controller = new SelfCheckoutController(hardware);
        
		session = controller.startSession();
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(testBarcode1, testProduct1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(testBarcode2, testProduct2);
	}
	
	
	//test for barcode scanner enabled, but invalid barcode (null)
	@Test(expected = NullPointerException.class)
	public void NullBarcode() {
		session.addItem(null);
	}
	
	
	//test for barcode scanner enabled, and valid barcode is scanned
	@Test
	public void ValidBarcode() {
		session.addItem(testBarcode1);
		assertEquals(BigDecimal.ONE, session.orderTotal);
	}
	
	
	//test for barcode scanner is blocked when weight discrepancy detected
	@Test
	public void ScannerBlockedByWeightDiscrepancy() {
		session.weightDiscrepancyDetected();
		assertTrue(session.scanner.isDisabled());
	}
	
	//test for barcode scanner is blocked when pay with coin
	@Test
	public void ScannerBlockedByPayWithCoin() {
		session.payWithCoin();
		assertTrue(session.scanner.isDisabled());
	}
	
	@After
	public void clearDatabase() {
		ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
	}
	
	//test if expected mass initialized to 0 when null
	@Test
	public void ExpectedMassInitialized() {
		session.expectedMassOnScale = null;
		session.discrepancyCheck();
		 
		assertEquals(BigDecimal.ZERO, session.expectedMassOnScale);
	}
	
	//test for discrepancy check when it exists
	@Test
	public void NoDiscrepancyCheckNoOverload() {
		session.expectedMassOnScale = BigDecimal.valueOf(100);
		
		session.actualMassOnScale = BigDecimal.valueOf(150);
	    
		session.discrepancyCheck();
		assertTrue(session.scanner.isDisabled());
		
	    }
	
	//test for discrepancy check  when it doesn't exist
	//the scanner should not be disabled because there is no discrepancy between expected and actual mass, but it is still called as disabled
	//but it could be cause i'm not reading the session code properly cause i cant read right now for some reason
	@Test
	public void DiscrepancyCheckNoOverload() {
		session.expectedMassOnScale = BigDecimal.valueOf(100);
		
		session.actualMassOnScale = BigDecimal.valueOf(100);
	    
		session.discrepancyCheck();
		assertFalse(session.scanner.isDisabled());
		
	    }
}
