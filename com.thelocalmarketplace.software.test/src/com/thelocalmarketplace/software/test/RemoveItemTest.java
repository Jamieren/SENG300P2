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
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
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
	TheLocalMarketPlaceDatabase softwareDatabase;
	SelfCheckoutStationBronze softwareStationBronze;
	
	
	@Before
	public void setup() {
		software = new SelfCheckoutStationSoftware();
		softwareStationBronze.configureCurrency(Currency.getInstance("CAD"));
		softwareStationBronze.configureBanknoteDenominations(new BigDecimal[] {new BigDecimal("5.0")});
		softwareStationBronze.configureBanknoteStorageUnitCapacity(10);
		softwareStationBronze.configureCoinDenominations(new BigDecimal[] {new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1"), new BigDecimal("2")});
		softwareStationBronze.configureCurrency(Currency.getInstance("CAD"));
		softwareStationBronze.configureCoinStorageUnitCapacity(10);
		softwareStationBronze.configureCoinTrayCapacity(20);
		

	
		SelfCheckoutStationBronze bronze = new SelfCheckoutStationBronze();
		
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(testBarcode1, testProduct1);
		
	}
	
	@Test(expected = NullPointerException.class)
	public void productNotInDatabase() {
		software.removeItem(testBarcode2);
		
	}
}
