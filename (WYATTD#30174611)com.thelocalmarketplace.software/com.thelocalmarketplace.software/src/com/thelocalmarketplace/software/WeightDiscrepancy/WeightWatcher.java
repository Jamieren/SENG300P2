package com.thelocalmarketplace.software.WeightDiscrepancy;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
/***
 * @author de Jaraiz, Diego / 30176017 / Rapdoo
 * @author DEICHERT Wyatt / 30174611 / wyattdeich
 * @author Lumumba Anne / 30171346 / AnneLumumba
 * @author MANALASTAS Rico / 30164386 / RicoM101
 * @author Non, Althea Anne Louise / 30172442 / altheanon
 */

/* 
 * Get it, WEIGHT WATCHER, hahahaha, i'm quite the comedian.
 * Partially implemented listener for the WEIGHT DISCREPANCY functionality. Constructor takes in a WeightDiscrepancy object, 
 * which is used to track the current discrepancy status of the SelfCheckout's scale.
 * 
 * WeightWatcher must be a registered listener for an ElectronicScale in order to take advantage of WeightDiscrepancy use case.
 * Once WeightWater is registered, it should keep tabs on the status of the scale.
 * 
 * When an ElectronicScale object announces that theMassOnTheScaleHasBeenChanged (occurs when an item is added/removed), 
 * it will call discrepancyWatcher to check the mass on the scale.
 * 
 */
public class WeightWatcher implements ElectronicScaleListener  {
	WeightDiscrepancy discrepancyWatcher;
	
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		//stubby

	}

	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
		//stubby

	}

	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		//stubby

	}

	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		//stubby
	}

	@Override
	public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
		discrepancyWatcher.checkWeight(mass);
	}

	@Override
	public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
		//stubby

	}

	@Override
	public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
		//stubby

	}
	
	public WeightWatcher(WeightDiscrepancy discrepancyObj) {
		discrepancyWatcher = discrepancyObj;
	}

}
