package com.thelocalmarketplace.software;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.CardIssuer;
/* 
 * Simple database for the Local Market Place 
 * */

public class TheLocalMarketPlaceDatabase {
	
//	private static TheLocalMarketPlaceDatabase instance = null;

	private final Map<Barcode, BarcodedProduct> BARCODED_PRODUCT_DATABASE;
	private final Map<Product, Integer> INVENTORY;
	private final Map<String, Card> CARDS;
	private final Map<String, CardIssuer> BANKS;
	

	public TheLocalMarketPlaceDatabase() {
		BARCODED_PRODUCT_DATABASE = new HashMap<>();
		INVENTORY = new HashMap<>();
		CARDS = new HashMap<>();
		BANKS = new HashMap<>();
		populateDatabase();
	}
	 
	private void populateDatabase() {

		Barcode milkBarcode = new Barcode(new Numeral[] {Numeral.one, Numeral.two, Numeral.three, Numeral.four, Numeral.five});
		Barcode juiceBarcode = new Barcode(new Numeral[] {Numeral.two, Numeral.three, Numeral.four, Numeral.five, Numeral.one});
		Barcode breadBarcode = new Barcode( new Numeral[] {Numeral.three, Numeral.four, Numeral.five, Numeral.one, Numeral.two});
		Barcode eggsBarcode = new Barcode(new Numeral[] {Numeral.four, Numeral.five, Numeral.one, Numeral.two, Numeral.three});
		Barcode canOfBeansBarcode = new Barcode(new Numeral[] {Numeral.five, Numeral.one, Numeral.two, Numeral.three, Numeral.four});

		final BarcodedProduct milk = new BarcodedProduct(milkBarcode, "MooMilk 2% 4L", 5L, 4128.00);
		final BarcodedProduct juice = new BarcodedProduct(juiceBarcode, "Orange Juice Pulp Free 2.63L", 6L, 2630.00);
		final BarcodedProduct bread = new BarcodedProduct(breadBarcode, "Whole Wheat Sliced Bread", 2L, 675.00);
		final BarcodedProduct eggs = new BarcodedProduct(eggsBarcode, "Large Eggs, 12 Count", 3L, 699.00);
		final BarcodedProduct canOfBeans = new BarcodedProduct(canOfBeansBarcode, "Dark Red Kidney Beans, 540mL", 1L, 423.00);
			
		addBarcodedProductToDatabase(milk);
		addBarcodedProductToDatabase(juice);
		addBarcodedProductToDatabase(bread);
		addBarcodedProductToDatabase(eggs);
		addBarcodedProductToDatabase(canOfBeans);

		addBarcodedProductToInventory(milk, 25);
		addBarcodedProductToInventory(juice, 12);
		addBarcodedProductToInventory(bread, 35);
		addBarcodedProductToInventory(eggs, 44);
		addBarcodedProductToInventory(canOfBeans, 75);
		
		CARDS.put("2648264926081648", new Card("Visa", "2648264926081648", "John Smith", "123"));
		CardIssuer visa = new CardIssuer("Visa", 5);
		Calendar c = Calendar.getInstance();
		c.set(2026, 5, 20);
		visa.addCardData("2648264926081648", "John Smith", c , "123", 1000.00);
		BANKS.put("Visa",visa);
		
	}

//	public static TheLocalMarketPlaceDatabase getInstance() {
//		if (instance == null) {
//			instance = new TheLocalMarketPlaceDatabase();
//		}
//		return instance;
//	}

	public void addBarcodedProductToDatabase(BarcodedProduct barcodedProduct) {
		if(barcodedProduct != null) {
			BARCODED_PRODUCT_DATABASE.put(barcodedProduct.getBarcode(), barcodedProduct);
		}
	}
	
	public BarcodedProduct getBarcodedProductFromDatabase(Barcode barcode) {
		return BARCODED_PRODUCT_DATABASE.get(barcode);
	}

	public void addBarcodedProductToInventory(BarcodedProduct barcodedProduct, int amount) {
		if(barcodedProduct != null) {
			INVENTORY.put(barcodedProduct, amount);
		}	
	}

	public void removeBarcodedProductFromInventory(BarcodedProduct barcodedProduct, int amountRemoved) {
		INVENTORY.put(barcodedProduct, INVENTORY.get(barcodedProduct) - amountRemoved);
	}

	public int getInventoryOfBarcodedProduct(BarcodedProduct barcodedProduct) {
		return INVENTORY.get(barcodedProduct);
	}
	
	public Card getCard(String cardNum) {
		return CARDS.get(cardNum);
	}
	
	public CardIssuer getBank(String bankName) {
		return BANKS.get(bankName);
	}
	
	public boolean isCard(String cardNum) {
		return CARDS.containsKey(cardNum);
	}

}
