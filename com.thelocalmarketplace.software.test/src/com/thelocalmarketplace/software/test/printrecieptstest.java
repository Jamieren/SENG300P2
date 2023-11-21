package com.thelocalmarketplace.software.test;

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
import com.thelocalmarketplace.software.PrintReceipt;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.Session;

public class printrecieptstest {
	

    SelfCheckoutStationSoftware software;
	PrintReceipt printReceipt;
	Session session;
	
	Numeral[] testNumeralArray1 = {Numeral.one} ;
	Barcode testBarcode1 = new Barcode(testNumeralArray1);
	BarcodedItem testItem1 = new BarcodedItem(testBarcode1, Mass.ONE_GRAM);
	BarcodedProduct testProduct1 = new BarcodedProduct(testBarcode1, "1 ml of Water", 1, 1);

	@Before
	public void setup() {
		software = new SelfCheckoutStationSoftware();
		SelfCheckoutStationBronze.configureCurrency(Currency.getInstance("CAD"));
		SelfCheckoutStationBronze.configureBanknoteDenominations(new BigDecimal[] {new BigDecimal("5.0")});
        
        software.initDatabase();
		software.initSelfStationBronze();
		software.initSession();
		
		software.getDatabase().addBarcodedProductToDatabase(testProduct1);

	}
	 @Test
	    public void testPrintReceipt() {
	        // Set up test data
		 
	        String paymentRecord = "Payment Record Details";
	        double amountDue = session.getAmountDue();

	        // Call the method to be tested
			printReceipt.print(paymentRecord,amountDue);

	 }
	 
}
