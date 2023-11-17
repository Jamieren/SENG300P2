package com.thelocalmarketplace.software.PayViaCoin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.software.WeightDiscrepancy.WeightDiscrepancy;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import powerutility.PowerGrid;
/***
 * @author de Jaraiz, Diego / 30176017 / Rapdoo
 * @author DEICHERT Wyatt / 30174611 / wyattdeich
 * @author Lumumba Anne / 30171346 / AnneLumumba
 * @author MANALASTAS Rico / 30164386 / RicoM101
 * @author Non, Althea Anne Louise / 30172442 / altheanon
 */
// test comment

public class PayCoin {
	
	private Currency currency;
	private List<BigDecimal> coinDenominations = new ArrayList<BigDecimal>();
    private CoinValidator validate; // Instantiate CoinValidator
    private BigDecimal price; // Temporal value of price
    private WeightDiscrepancy discrepancyStatus;
    private boolean coinValid = false;
    private PowerGrid grid = PowerGrid.instance();


    
 
    public PayCoin(Currency currency, WeightDiscrepancy discrepancy, CoinValidator validate){
    	discrepancyStatus = discrepancy; // instantiate machine discrepancy object for method.
    	this.currency = currency;
    	this.validate = validate;
    	this.coinDenominations.add(new BigDecimal(0.05));
    	this.coinDenominations.add(new BigDecimal(0.10));
    	this.coinDenominations.add(new BigDecimal(0.25));
    	this.coinDenominations.add(new BigDecimal(1.00));
    	this.coinDenominations.add(new BigDecimal(2.00));
    
    }
    

    public BigDecimal pay(Coin coin, BigDecimal price) throws DisabledException, CashOverloadException {
        coinValid = true;
    	PowerGrid.engageUninterruptiblePowerSource();
    	validate.connect(grid);
    	validate.activate();
        validate.receive(coin); //Coin goes to designated sink hole and listeners are called
        BigDecimal coinValue = coin.getValue(); // Get the value of the coin

        if (!discrepancyStatus.isBlocked()) { // if there is an ongoing weight discrepancy, no paying
                if (coinValue.compareTo(price) >= 0) { // More coins than or equal to the price
                    BigDecimal change = coinValue.subtract(price);
                    System.out.println("Thank You for Your Purchase! Here is Your Change: " + change + ".");
                    return change;
                } else {
                    BigDecimal remaining = price.subtract(coinValue);
                    System.out.println("There are " + remaining + " remaining.");
                    this.price = remaining; //Price updates
                }
            
            } else {
            throw new InvalidArgumentSimulationException("Ongoing weight discrepancy");
        }
        
        double zero = 0.00;
        return new BigDecimal(zero); // Return 0.0 if no change is given
    }
    
    public BigDecimal returnChange() throws NoCashAvailableException{
    	BigDecimal amountDue;
    	if //changeInMachine < amountDue{
    		//throw new NoCashAvailableException;
    		//dispense what left?
    		throw new NoCashAvailableException("insufficient change available in machine")
    		
    	else
    		while(coinTray.hasSpace()) {
    			// return coins
    		}
    	
    }
    
    	
}


