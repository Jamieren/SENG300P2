package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.*;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;

public class HandleBulkyTest {
	SelfCheckoutStationSoftware software;
	
	Numeral[] testNumeralArray1 = {Numeral.one} ;
	Barcode testBarcode1 = new Barcode(testNumeralArray1);
	BarcodedItem testItem1 = new BarcodedItem(testBarcode1, Mass.ONE_GRAM);
	BarcodedProduct testProduct1 = new BarcodedProduct(testBarcode1, "1 ml of Water", 1, 1);

	@Before
	public void setUp() {
		software = new SelfCheckoutStationSoftware();
		SelfCheckoutStationBronze.configureCurrency(Currency.getInstance("CAD"));
		SelfCheckoutStationBronze.configureBanknoteDenominations(new BigDecimal[] {new BigDecimal("5.0")});
		SelfCheckoutStationBronze.configureBanknoteStorageUnitCapacity(10);
		SelfCheckoutStationBronze.configureCoinDenominations(new BigDecimal[] {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1"), new BigDecimal("2")});
		SelfCheckoutStationBronze.configureCurrency(Currency.getInstance("CAD"));
		SelfCheckoutStationBronze.configureCoinStorageUnitCapacity(10);
		SelfCheckoutStationBronze.configureCoinTrayCapacity(20);
		SelfCheckoutStationBronze.configureCoinDispenserCapacity(20);
		
		software.getDatabase();
		software.initSelfStationBronze();
		software.initSession();
		
	}
	
	@Test
	public void addsItemToOrder() {
		// add test item to order
		software.getSession().newOrderItem(testItem1);
		software.getSession().addTotalExpectedWeight(testProduct1.getExpectedWeight());
		// handle the no bag request for the test product
		software.handleBulkyItem(testProduct1);
		// ensure that the system has removed the items weight from the expected in the bagging area.
		assertTrue(software.getSession().getTotalExpectedWeight() == 0);
	}
	
	@Test
	public void correctlyAdjustsWhenWeightZero() {
		// ensure weight on scale = 0
		software.getSession().addTotalExpectedWeight(-software.getSession().getTotalExpectedWeight());
		System.out.println(software.getSession().getTotalExpectedWeight());
		// "handle" the bulky item
		software.handleBulkyItem(testProduct1);
		// ensure the expected weight remains at 0
		System.out.println(software.getSession().getTotalExpectedWeight());
		assertTrue(software.getSession().getTotalExpectedWeight() == 0);
	}
	
	@Test
	public void correctlyAdjustsWhenWeight() {
		software.getSession().addTotalExpectedWeight(10);
		software.handleBulkyItem(testProduct1);
		assertTrue(software.getSession().getTotalExpectedWeight() == 9);
	}
}
