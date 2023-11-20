package com.thelocalmarketplace.software;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.printer.ReceiptPrinterListener;

public class PrintReceipt implements ReceiptPrinterListener {
	private ReceiptPrinterBronze receiptPrinter;	   

	    public void print(String paymentRecord, double amountDue) {
	        try {
	            // Print the payment record
	            for (char c : paymentRecord.toCharArray()) {
	                receiptPrinter.print(c);
	            }

	            // Print a newline to separate the payment record and amount due
	            receiptPrinter.print('\n');

	            // Print the amount due
	            String amountDueString = String.format("Amount Due: $%.2f", amountDue);
	            for (char c : amountDueString.toCharArray()) {
	                receiptPrinter.print(c);
	            }


	        } catch (Exception e) {
	            // Handle exceptions, e.g., out of paper or ink
	            System.err.println("Printing failed: " + e.getMessage());
	        }
	    }

	    // ReceiptPrinterListener methods
	    @Override
	    public void thePrinterIsOutOfInk() {
	        System.out.println("Printer is out of ink. Printing aborted.");
	    }

	    @Override
	    public void thePrinterIsOutOfPaper() {
	        System.out.println("Printer is out of paper. Printing aborted.");
	    }

	    @Override
	    public void inkHasBeenAddedToThePrinter() {
	        System.out.println("Ink has been added to the printer. Ready to print.");
	    }

	    @Override
	    public void paperHasBeenAddedToThePrinter() {
	        System.out.println("Paper has been added to the printer. Ready to print.");
	    }
		@Override
		public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void thePrinterHasLowInk() {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void thePrinterHasLowPaper() {
			// TODO Auto-generated method stub
			
		}
	}


