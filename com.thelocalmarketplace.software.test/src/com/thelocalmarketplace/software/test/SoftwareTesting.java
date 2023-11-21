package com.thelocalmarketplace.software.test;

/*SENG 300 Project Iteration 2

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
*/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

//import java.math.BigDecimal;

//import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.InputMismatchException;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.banknote.BanknoteValidator;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.TheLocalMarketPlaceDatabase;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.AddOwnBag;
import com.thelocalmarketplace.software.Bag;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.WeightDiscrepancy;

import powerutility.PowerGrid;


/*SENG 300 Project Iteration 2

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
*/


public class SoftwareTesting {
	
	Session session;
	
	SelfCheckoutStationSoftware sessionSimulation = new SelfCheckoutStationSoftware();
	ArrayList<BarcodedItem> orderItems;
	Numeral[] testCode = {Numeral.one,Numeral.two,Numeral.three,Numeral.four};
	Barcode testBarcode = new Barcode(testCode);

	Currency currency = Currency.getInstance("CAD");
	BigDecimal[] banknoteDenominations = new BigDecimal[] {new BigDecimal("5.0"), new BigDecimal("10.0"), new BigDecimal("20.0")};
	
	TheLocalMarketPlaceDatabase testDatabase;
	WeightDiscrepancy testDiscrepancy;
	
	Mass testMass = new Mass(2L);
	double marginOfError = 0.1;
	long testPrice = 10L;
	double testWeight = 5.0;
	String testProductDescription = "Product description";
	BarcodedProduct testBarcodedProduct = new BarcodedProduct(testBarcode, testProductDescription, testPrice, testWeight);

	
	boolean isActive = false;
	
	@Before
	public void setup() {
		session = Session.getInstance();
		testDatabase = new TheLocalMarketPlaceDatabase();
		orderItems = new ArrayList<BarcodedItem>();

		SelfCheckoutStationBronze.resetConfigurationToDefaults();

		BanknoteValidator banknoteValidator = new BanknoteValidator(currency, banknoteDenominations);
		banknoteValidator.connect(PowerGrid.instance());

		banknoteValidator.hasPower();
		banknoteValidator.activate();
		banknoteValidator.enable();
		
		PowerGrid.engageUninterruptiblePowerSource();

		SelfCheckoutStationBronze bronzeStation = new SelfCheckoutStationBronze();
		bronzeStation.plugIn(PowerGrid.instance());
		bronzeStation.turnOn();
		
		Bag testBag = new Bag(testMass);
		
		testDiscrepancy  = new WeightDiscrepancy();

	}
	

	//Testing for Session Class
	

	//Tests if the session returns false for isActive() method after deactivate() method is called
	@Test
	public void sessionDeactiveTest() {
		session.deactivate();
		assertFalse(session.isActive());
	}
	
	//Tests if the session returns true for isActive() method after activate() method is called
	@Test
	public void sessionIsActiveTest() {
		isActive = true;
		session.activate();
		assertTrue(session.isActive());
	}
	
	//Tests if the session returns true for isActive() method after activate() method is called
	@Test
	public void sessionIsNotActiveTest() {
		isActive = false;
		session.activate();
		assertTrue(session.isActive());
	}
	
	
	//Test to see if a new item is added to orderItems when newOrderItem is called
	@Test
	public void newOrderedItemTest() {
		BarcodedItem testBarcodedItem = new BarcodedItem(testBarcode, new Mass(2L));
		session.newOrderItem(testBarcodedItem);
		orderItems.add(testBarcodedItem);
		assertEquals(orderItems, session.getOrderItem());
	}
	
	//Test to see if a addTotalExcpectedWeight updates when weight is added
	@Test
	public void addTotalExpectedWeightTest() {
		session.addTotalExpectedWeight(testWeight);
		assertEquals(testWeight, session.getTotalExpectedWeight(), marginOfError);
	}
	
	//Test to see if a addAmountDue updates when amount is added
	@Test
	public void addAmountDueTest() {
		double testAmount = session.getAmountDue() + 1;
		session.addAmountDue(1);
		assertEquals(testAmount, session.getAmountDue(), marginOfError);
	}
	
	//Test to see if a addAmountDue updates when amount is subtracted
	@Test
	public void subAmountDueTest() {
		session.addAmountDue(3);
		session.subAmountDue(2);
		assertEquals(1, session.getAmountDue(), marginOfError);
	}

	
	//Test to see if setWeightDiscrepancy returns correct boolean value true
	@Test
	public void setWeightDiscrepancyTest() {
		testDiscrepancy.setDiscrepancy(true);
		assertTrue(testDiscrepancy.getDiscrepancy());
	}
	
	@Test
	public void setDiscrepancyWeightTest() {
		testDiscrepancy.setWeight(new BigDecimal(testWeight));
		assertEquals(new BigDecimal(testWeight), testDiscrepancy.getDiscrepancyWeight());
	}
	
	@Test
	public void setDiscrepancyBarcodedProductTest() {
		testDiscrepancy.setBarcodedProduct(testBarcodedProduct);
		assertEquals(testBarcodedProduct, testDiscrepancy.getDiscrepancyProduct());
	}
	
	@Test
	public void setDiscrepancyTrueTest() {
		testDiscrepancy.setDiscrepancy(true);
		assertTrue(testDiscrepancy.getDiscrepancy());
	}
	
	@Test
	public void setDiscrepancyFalseTest() {
		testDiscrepancy.setDiscrepancy(false);
		assertFalse(testDiscrepancy.getDiscrepancy());
	}
	
	//Testing for LocalMarketPlaceDatabase class
	
	//Test that addBarcodedProductToDatabase adds a barcoded product to the database
	@Test
	public void addBarcodedProductToDatabaseTest() {
		testDatabase.addBarcodedProductToDatabase(testBarcodedProduct);
		testDatabase.getBarcodedProductFromDatabase(testBarcode);
		assertEquals(testBarcodedProduct,testDatabase.getBarcodedProductFromDatabase(testBarcode));
	}
	
	//Test that addBarcodedProductToInventory adds a barcoded product to inventory
	@Test
	public void addBarcodedProductToInventoryTest() {
		int increaseInventory = 1;
		testDatabase.addBarcodedProductToDatabase(testBarcodedProduct);
		testDatabase.addBarcodedProductToInventory(testBarcodedProduct, increaseInventory);		
		testDatabase.addBarcodedProductToInventory(testBarcodedProduct, increaseInventory);
		assertEquals(increaseInventory,testDatabase.getInventoryOfBarcodedProduct(testBarcodedProduct));
	}
	
	//Test that removeBarcodedProductFromInventory removes a barcoded product to inventory
	@Test
	public void removeBarcodedProductFromInventoryTest() {
		int increaseInventory = 2;
		int decreaseInventory = 1;
		testDatabase.addBarcodedProductToDatabase(testBarcodedProduct);
		testDatabase.addBarcodedProductToInventory(testBarcodedProduct, increaseInventory);		
		testDatabase.removeBarcodedProductFromInventory(testBarcodedProduct, decreaseInventory);
		assertEquals(decreaseInventory,testDatabase.getInventoryOfBarcodedProduct(testBarcodedProduct));
	}
		
	//Testing for Software class
	
	@Test
	public void promptStartSession() throws IOException {
		String simulatedChoice = "1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedChoice.getBytes());
        System.setIn(in);
        session.promptToStartSession();
        assertTrue(session.isActive());
	}

	@Test (expected = InputMismatchException.class)
	public void promptStartSessionCharPressed() throws IOException {
		String simulatedInput = "s\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);
        session.promptToStartSession();
	}
	

}




