 package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;

//import java.math.BigDecimal;

//import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;

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
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.WeightDiscrepancy;

import powerutility.PowerGrid;


/* 
 * Software Testing for LThe Local Market Place Software
 * 
 * 
 * */


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
		
		
		SelfCheckoutStationBronze bronzeStation = new SelfCheckoutStationBronze();
		bronzeStation.plugIn(PowerGrid.instance());
		bronzeStation.turnOn();
		
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
	
	// Exception handling needed in code for 
	// adding a null item, should result in a null pointer exception
//	@Test(expected = NullPointerException.class)
//	public void newOrderedNullItemTest() {
//		BarcodedItem testBarcodedItem = null;
//		session.newOrderItem(testBarcodedItem);
//		orderItems.add(testBarcodedItem);
//		session.getOrderItem();
//	} 
	
	//Test to see if a addTotalExcpectedWeight updates when weight is added
	@Test
	public void addTotalExpectedWeightTest() {
		session.addTotalExpectedWeight(testWeight);
		assertEquals(testWeight, session.getTotalExpectedWeight(), marginOfError);
	}
	
	// Exception handling needed in code for 
	// negative weight
//	@Test(expected = .class)
//	public void addTotalExpectedWeightTest() {
//		session.addTotalExpectedWeight(testWeight);
//	}
	
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
	
	// Exception handling needed in code for 
	// negative amount due
//	@Test(expected = .class)
//	public void subAmountDueTest() {
//		session.addAmountDue(3);
//		session.subAmountDue(2);
//	}
	
	//Test to see if setWeightDiscrepancy returns correct boolean value true
//	@Test
//	public void setWeightDiscrepancyTest() {
//		session.setWeightDiscrepancy(null, new BigDecimal(0));
//		session.setWeightDiscrepancy();
//		assertTrue(session.hasWeightDiscrepancy());
//	}
	
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
	
	//Exception handling needed in code for adding null or non-existent products to inventory and database
	//Exception handling needed in code for removing null or non-existent barcoded product from inventory
	
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
	
	//Testing for WeightDiscrepancy class
	
	//
//	@Test
//	public void weightDiscrepancyChoiceYesTest() {
//		testDiscrepancy.WeightDiscrepancyMessage(selfCheckoutStation, testBarcodedProduct);		
//		//User must enter yes for test
//	}
//	
//	@Test
//	public void weightDiscrepancyChoiceNoAddTest() {
//		testDiscrepancy.WeightDiscrepancyMessage(selfCheckoutStation, testBarcodedProduct);		
//		//User must enter no then add for test
//	}
//	
//	@Test
//	public void weightDiscrepancyChoiceNoCancelTest() {
//		testDiscrepancy.WeightDiscrepancyMessage(selfCheckoutStation, testBarcodedProduct);		
//		//User must enter no then cancel for test
//	}
		
	//Testing for SessionSimulation class

	//
	@Test // Testing that promptEnterToContinue runs when user input is \"ENTER"\
	public void promptEnterToContinueTestEnter() throws IOException {
		String simulatedChoice = "1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedChoice.getBytes());
        System.setIn(in);
        
        session.promptToStartSession();
	}
	
	@Test // Testing that promptEnterToContinue runs when user input is \"ENTER"\
	public void scanBarcodedProductTestNullBarcode() {
		String simulatedInput = "\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);
        
        sessionSimulation.scanBarcodedProduct(testBarcode);
	}
	
	@Test // Testing that promptEnterToContinue runs when user input is \"ENTER"\
	public void scanBarcodedProductTestValidBarcode() {
		System.out.println("here");
		String simulatedInput = "\nyes\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

		testDatabase.addBarcodedProductToDatabase(testBarcodedProduct);

        sessionSimulation.scanBarcodedProduct(testBarcode);
        
        
	}
	
	@Test // Testing when user inputs '1' in main
	public void promptMainWhen1Pressed() {
		String simulatedInput = "\n1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);
        
//		SessionSimulation.main(new String[0]);
	}
	
	@Test // Testing when user inputs '2' in main
	public void promptMainWhen2Pressed() {
		String simulatedInput = "\n2\n2.0\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);
        
//		SessionSimulation.main(new String[0]);
	}
	
	@Test // Testing when user inputs '3' in main
	public void promptMainWhen3Pressed() {
		String simulatedInput = "\n3\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);
        
//		sessionSimulation.main(new String[0]);
	}
	
	@Test // Testing when user inputs '4' in main
	public void promptMainWhen4Pressed() {
		String simulatedInput = "\n4\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);
        
//		SessionSimulation.main(new String[0]);
	}
	
	@Test // Testing when user inputs '-1' in main
	public void promptMainWhenMinus1Pressed() {
		String simulatedInput = "\n-1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);
        
//		SessionSimulation.main(new String[0]);
	}
	
	
//	@Test(expected = IOException.class)
//	// Testing that promptEnterToContinue runs when user input is \"ENTER"\
//	public void promptEnterToContinueTestException() {
//		sessionSimulation.promptEnterToContinue();
//	}
//	
	
	

}




