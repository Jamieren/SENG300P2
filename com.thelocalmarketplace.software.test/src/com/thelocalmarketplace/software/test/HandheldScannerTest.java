package com.thelocalmarketplace.software.test;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.SelfCheckoutStationSoftware;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.TheLocalMarketPlaceDatabase;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

public class HandheldScannerTest {

    private SelfCheckoutStationSoftware sessionSimulation;
    private TheLocalMarketPlaceDatabase testDatabase;
    private Session session;
    private Barcode testBarcode;
    private BarcodedProduct testProduct;

    @Before
    public void setUp() {
        sessionSimulation = new SelfCheckoutStationSoftware();
        testDatabase = new TheLocalMarketPlaceDatabase();
        session = Session.getInstance();

        // Convert string "25" to Barcode
        testBarcode = createBarcodeFromString("25");
        testProduct = new BarcodedProduct(testBarcode, "Milk", 10L, 2.0);

        testDatabase.addBarcodedProductToDatabase(testProduct);
        sessionSimulation.setDataBase(testDatabase);
    }

    private Barcode createBarcodeFromString(String barcodeString) {
        Numeral[] numerals = new Numeral[barcodeString.length()];
        for (int i = 0; i < barcodeString.length(); i++) {
            int digit = Character.getNumericValue(barcodeString.charAt(i));
            numerals[i] = Numeral.values()[digit];
        }
        return new Barcode(numerals);
    }

    @Test
    public void testHandheldScannerAddsItem() {
        String simulatedInput = "25\n";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        sessionSimulation.addItemWithHandheldScanner();

        BarcodedItem addedItem = session.findItem(testBarcode);
        assertNotNull("Item should be added to the session", addedItem);

        // Retrieve the product from the database for comparison
        BarcodedProduct addedProduct = testDatabase.getBarcodedProductFromDatabase(addedItem.getBarcode());
        assertEquals("Added product should match the test product", testProduct, addedProduct);
    }
}