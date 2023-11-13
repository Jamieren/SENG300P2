package com.thelocalmarketplace.software.test;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;

import powerutility.PowerGrid;

public class RemoveItemTest {
	SelfCheckoutStationSoftware software;
	SelfCheckoutStationBronze hardware;
	Numeral[] testNumeralArray1 = {Numeral.one} ;
	Barcode testBarcode1 = new Barcode(testNumeralArray1);
	BarcodedItem testItem1 = new BarcodedItem(testBarcode1, Mass.ONE_GRAM);
	BarcodedProduct testProduct1 = new BarcodedProduct(testBarcode1, "1 ml of Water", 1, Mass.ONE_GRAM.inGrams().doubleValue());

	Numeral[] testNumeralArray2 = {Numeral.two} ;
	Barcode testBarcode2 = new Barcode(testNumeralArray2);
	BarcodedItem testItem2 = new BarcodedItem(testBarcode2, Mass.ONE_GRAM.sum(Mass.ONE_GRAM));
	BarcodedProduct testProduct2 = new BarcodedProduct(testBarcode2, "1 ml of Milk", 2, Mass.ONE_GRAM.sum(Mass.ONE_GRAM).inGrams().doubleValue());
	
	@Before
	public void setup() {
		SelfCheckoutStationSoftware software = new SelfCheckoutStationSoftware();
		SelfCheckoutStationBronze hardware = new SelfCheckoutStationBronze();
        hardware.plugIn(PowerGrid.instance());
        hardware.turnOn();


		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(testBarcode1, testProduct1);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(testBarcode2, testProduct2);
	}
	@Test
	public void removeNoItemInOrder() {
		
	}
}
