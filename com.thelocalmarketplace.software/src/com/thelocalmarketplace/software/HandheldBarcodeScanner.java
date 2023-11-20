package com.thelocalmarketplace.software;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import java.math.BigDecimal;
import java.util.Scanner;

public class HandheldBarcodeScanner {

    private Scanner inputScanner;

    public HandheldBarcodeScanner(Scanner scanner) {
        this.inputScanner = new Scanner(System.in);
    }

    public Barcode scanBarcode() {
        System.out.print("Please scan the barcode using the handheld scanner: ");
        String barcodeString = inputScanner.nextLine();
        BigDecimal barcodeNumber = new BigDecimal(barcodeString);
        return convertToBarcode(barcodeNumber);
    }

    private Barcode convertToBarcode(BigDecimal barcodeNumber) {
        String barcodeString = barcodeNumber.toPlainString();
        Numeral[] barcodeNumeral = new Numeral[barcodeString.length()];
        int i = 0;
        for (char c : barcodeString.toCharArray()) {
            barcodeNumeral[i] = Numeral.valueOf(Byte.valueOf(String.valueOf(c)));
            i++;
        }
        return new Barcode(barcodeNumeral);
    }

    // Make sure to close the scanner when it's no longer needed
    public void close() {
        if (inputScanner != null) {
            inputScanner.close();
        }
    }
}