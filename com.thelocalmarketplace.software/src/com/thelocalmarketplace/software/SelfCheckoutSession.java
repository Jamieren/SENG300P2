package com.thelocalmarketplace.software;


import java.math.BigDecimal;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.hardware.SelfCheckoutStation;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scale.ElectronicScale;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScanner;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.coin.*;

/**
 * 
 * @author Robin Bowering UCID 30123373
 * @author Kelvin Jamila UCID 30117164
 * @author Nikki Kim UCID 30189188
 * @author Hillary Nguyen UCID 30161137
 * @author Matt Gibson UCID 30117091
 * 
 * Monolithic class representing and supporting a single session with the Self-Checkout System
 * 
 * Supports:
 *  -session startup
 *  -automatic item addition to order upon barcode scan
 *  -automatic weight management, checked
 *  -finishing session by paying off full balance via coin(s)
 *  
 */
public class SelfCheckoutSession implements CoinSlotObserver, CoinValidatorObserver, CoinStorageUnitObserver, ElectronicScaleListener,
BarcodeScannerListener {
	
	/**
	 * Barcode Scanner of associated self checkout machine, representative of hardware
	 * component
	 */
	public BarcodeScanner scanner;
	/**
	 * Coin Storage Unit of associated self checkout machine, representative of 
	 * hardware component
	 */
	CoinStorageUnit coinStorage;
	/**
	 * Coin Validator of associated self checkout machine, representative of hardware
	 * component
	 */
	CoinValidator validator;
	/**
	 * Coin Slot of associated self checkout machine, representative of hardware
	 * component
	 */
	public CoinSlot coinslot;
	
	/**
	 * Electronic Scale in bagging area of associated self checkout machine, representative of hardware
	 * component
	 */
	ElectronicScale scale;
	
	/**
	 * The controller which set up the current session
	 */
	SelfCheckoutController controller;
	
	/**
	 * The total cost of all items in order
	 */
	public BigDecimal orderTotal = BigDecimal.ZERO;
	
	/**
	 * The amount which has been received against the customer's balance through processPayment()
	 */
	public BigDecimal amountPaid = BigDecimal.ZERO;
	
	/**
	 * State variable, signaling if the session has an ongoing weight discrepancy
	 */
	public boolean weightDiscrepancy = false;
	
	/**
	 * State variable, signaling if the customer has entered the payment phase (and can no longer add items)
	 */
	public boolean payingForOrder = false;

	// Kelvin's Added variables
	/**
	 * Mass as reported by the scale through listener method
	 */
	public BigDecimal actualMassOnScale;
	
	/**
	 * Expected mass based on sum of all items in order's mass
	 */
	public BigDecimal expectedMassOnScale = BigDecimal.ZERO;
	
	/**
	 * Instantiates a Self Checkout Session with all hardware enabled except coinslot
	 * Checks for discrepancy
	 * 
	 * @param a Self Checkout Station with all hardware enabled
	 * @param The self checkout controller which called the constructor
	 */
	public SelfCheckoutSession(SelfCheckoutStation station, SelfCheckoutController instantiator) {
		
		controller = instantiator;
		
		scale = station.baggingArea;
		scale.enable();

		
		scanner = station.scanner;
		scanner.enable();
		
		validator = station.coinValidator;
		validator.enable();
		
		coinslot = station.coinSlot;
		coinslot.disable();
		
		coinStorage = station.coinStorage;
		coinStorage.enable();
		
		discrepancyCheck();
		
	} 
	
	//method to start the session
	public void startSession() {
		return;
	}
	
	/**
	 * Looks up product associated with barcode passed to it and adds it to order
	 * Triggers discrepancyCheck() after updating expected weight
	 * @param barcode
	 */
	public void addItem(Barcode barcode) {
		
		if (payingForOrder) {return;} //barcode scanner should be disabled anyways, but just in case
		
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode); // Gets the database of the barcode
		BigDecimal expectedMass = BigDecimal.valueOf(product.getExpectedWeight()); // Gets expected weight of item
		BigDecimal price = BigDecimal.valueOf(product.getPrice()); // get the price from the database
		
		expectedMassOnScale = expectedMassOnScale.add(expectedMass); //Update the expected weight that should be on the scale

		orderTotal = orderTotal.add(price); // Add product price to the total price of customer cart
		
		discrepancyCheck();
		
		
	}
	
	/**
	 * Sets session to payment state, disabling addition of further items and enabling coinslot
	 * Prints
	 * 
	 * Would be initiated by a customer selecting "Pay with Coin" on GUI, represented by method call here
	 */
	public void payWithCoin() {
		
		if (weightDiscrepancy) {return;}
		
		scanner.disable(); 
		coinslot.enable();
		payingForOrder = true;
		
		System.out.println("Total: $" + orderTotal.toString());
		System.out.print("Insert coin(s): ");
	}     
	
	/**
	 * Updates amount paid, prints balance if one remains, and ends session if payment is sufficient
	 * 
	 * @param currentPayment A confirmed amount of money which has been paid towards the customer's balance
	 */
	public void processPayment(BigDecimal currentPayment) {
		
		amountPaid = amountPaid.add(currentPayment);
		
		if (amountPaid.compareTo(orderTotal) >= 0) {
			
			coinslot.disable();
			System.out.println("Amount received: $" + currentPayment.toString());
			System.out.println("Payment completed, ending session");
			controller.sessionEnded();   
			 
			//GUI scene would reset here
			
		}
		
		else {
			System.out.println("Amount received: $" + currentPayment.toString());
			System.out.println("Remaining balance: $" + orderTotal.subtract(amountPaid));
		}
		
	}
	
	/**
	 * Blocks an open session by disabling scanner and alerts customer/attendant of weight discrepancy
	 * 
	 * If at payment stage, disables coinslot so that payment cannot be completed without correction of
	 * 		bagging area contents
	 */
	public void weightDiscrepancyDetected() {
		
		if (payingForOrder) {coinslot.disable();}
		
		else {scanner.disable();}
		
		weightDiscrepancy = true;
		System.out.println("Customer screen: Weight discrepancy detected.");
		System.out.println("Attendant screen: Weight discrepancy detected.");
	} 
	
	/**
	 * Unblocks a blocked session by reenabling scanner, and notes that the issue is resolved
	 * 
	 * If at payment stage, enables coinslot so that payment can resume
	 */
	public void weightDiscrepancyEnded() {
		
		if (payingForOrder) {
			coinslot.enable();
		}
		else {scanner.enable();}
		
		weightDiscrepancy = false;
		System.out.println("Customer screen: Weight discrepancy resolved.");
		System.out.println("Attendant screen: Weight discrepancy resolved.");
		
	}
	
	/**
	 * Checks if discrepancy has appeared or has been resolved between expected and actual mass on the scale
	 * 
	 * Allows for a margin up to the scale's sensitivity limit
	 * 
	 * If a weight discrepancy is ongoing, checks if it has been resolved and calls weightDiscrepancyEnded() if so
	 * If in an open session, checks for a weight discrepancy and calls weightDiscrepancyDetected() if necessary
	 * 
	 */
	public void discrepancyCheck() {
		
		try {
			actualMassOnScale = scale.getCurrentMassOnTheScale().inGrams();
		} catch (OverloadedDevice sadScale) {
			
			weightDiscrepancyDetected();
			System.out.println("SCALE OVERLOADED. PLEASE REMOVE WEIGHT AND ALERT STAFF");
			return;
		}
		
		BigDecimal difference = actualMassOnScale.subtract(expectedMassOnScale).abs();
		BigDecimal sensitivity = scale.getSensitivityLimit().inGrams();
		
		
		if (!weightDiscrepancy) {
				
			if (difference.compareTo(sensitivity) > 0) {
					weightDiscrepancyDetected();
					return; //so that else block is not required further down
			}
			return;
		}
		
		if (difference.compareTo(sensitivity) <= 0) {
					weightDiscrepancyEnded();
			}		
	}

	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {}

	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {}

	@Override
	public void turnedOn(IComponent<? extends IComponentObserver> component) {}

	@Override
	public void turnedOff(IComponent<? extends IComponentObserver> component) {}

	/**
	 * Calls processPayment with the value of the coin
	 * 
	 * This assumes that once a coin is validated, the system will have a place for it in the coin storage unit and place it there
	 */
	@Override
	public void validCoinDetected(CoinValidator validator, BigDecimal value) {
		if (value != null) {
			processPayment(value);
		}
	}

	@Override
	public void invalidCoinDetected(CoinValidator validator) {}

	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {}

	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {}

	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {}

	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {}

	/**
	 * If a barcode is received from the scanner, passes it to addItem method
	 */
	@Override
	public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
		if (barcode != null) {
			addItem(barcode);
			
		}		
	}

	/**
	 * Runs discrepancyCheck() any time the mass on the scale changes
	 */
	@Override
	public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
		discrepancyCheck();
		
	}

	@Override
	public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {}

	@Override
	public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {}

	@Override
	public void coinsFull(CoinStorageUnit unit) {}

	@Override
	public void coinAdded(CoinStorageUnit unit) {}

	@Override
	public void coinsLoaded(CoinStorageUnit unit) {}

	@Override
	public void coinsUnloaded(CoinStorageUnit unit) {}

	@Override
	public void coinInserted(CoinSlot slot) {}

}