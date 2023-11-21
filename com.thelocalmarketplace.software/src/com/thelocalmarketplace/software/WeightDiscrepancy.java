package com.thelocalmarketplace.software;

/*SENG 300 Project Iteration 2

@author Akashdeep Grewal 30179657
@author Amira Wishah 30182579
@author Ananya Jain 30196069
@author Danny Ly 30127144
@author Hillary Nguyen 30161137
@author Johnny Tran 30140472 
@author Minori Olguin 30035923
@author Rhett Bramfield 30170520
@author Wyatt Deichert 30174611
@author Zhenhui Ren 30139966
@author Adrian Brisebois 30170764
*/

import com.thelocalmarketplace.hardware.BarcodedProduct;

import java.math.BigDecimal;
/*
 * 
 * */

public class WeightDiscrepancy {

	private BarcodedProduct product;
	private BigDecimal weight;
	private boolean discrepancy;
	
	public WeightDiscrepancy () {
		discrepancy = false;
	}
	
	public void setBarcodedProduct(BarcodedProduct product) {
		this.product = product;
	}
	
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	
    public void setDiscrepancy(Boolean discrepancy) {
    	this.discrepancy = discrepancy;
    }
	
	public BarcodedProduct getDiscrepancyProduct() {
		return product;
	}

	public BigDecimal getDiscrepancyWeight() {
		return weight;
	}
    
    public boolean getDiscrepancy() {
    	return discrepancy;
    }

}
	

