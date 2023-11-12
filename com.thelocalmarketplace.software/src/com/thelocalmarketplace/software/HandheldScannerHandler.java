package com.thelocalmarketplace.software;

import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

public class HandheldScannerHandler {

    private SelfCheckoutSession currentSession;

    public HandheldScannerHandler(SelfCheckoutSession session) {
        this.currentSession = session;
    }

    public void scanBarcode(Barcode barcode) {
        BarcodedProduct product = findBarcodedProduct(barcode);
        if (product != null) {
            addProductToSession(product);
        } else {
            System.out.println("Barcoded product not found for barcode: " + barcode);
        }
    }

    private BarcodedProduct findBarcodedProduct(Barcode barcode) {
        // Accessing the barcoded product database directly
        return ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
    }

    private void addProductToSession(BarcodedProduct product) {
        if (currentSession != null) {
            currentSession.addItem(product.getBarcode());
            System.out.println("Barcoded product added to session: " + product.getDescription());
        } else {
            System.out.println("No active session to add barcoded product.");
        }
    }
}
