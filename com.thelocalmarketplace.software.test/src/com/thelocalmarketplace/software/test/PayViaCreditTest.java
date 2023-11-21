package com.thelocalmarketplace.software.test;

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

import static org.junit.Assert.*;

import org.junit.Test;

import com.thelocalmarketplace.software.PayViaCredit;
import com.thelocalmarketplace.software.TheLocalMarketPlaceDatabase;

public class PayViaCreditTest {

	@Test
	public void CardExistsReturnsTrue() {
		TheLocalMarketPlaceDatabase database = new TheLocalMarketPlaceDatabase();
		PayViaCredit pay = new PayViaCredit();
				assertTrue(pay.CreditSwipe(database));
	}
	
	@Test
	public void CardDoesnrExistReturnsFalse() {
		TheLocalMarketPlaceDatabase database = new TheLocalMarketPlaceDatabase();
		PayViaCredit pay = new PayViaCredit();
				assertFalse(pay.CreditSwipe(database));
				
	}
}
