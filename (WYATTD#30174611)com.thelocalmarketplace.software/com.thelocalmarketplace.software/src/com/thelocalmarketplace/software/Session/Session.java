package com.thelocalmarketplace.software.Session;
/***
 * @author de Jaraiz, Diego / 30176017 / Rapdoo
 * @author DEICHERT Wyatt / 30174611 / wyattdeich
 * @author Lumumba Anne / 30171346 / AnneLumumba
 * @author MANALASTAS Rico / 30164386 / RicoM101
 * @author Non, Althea Anne Louise / 30172442 / altheanon
 */

public class Session {

	private boolean ongoingSession = false;
	
	/** 	
	 * Default constructor.
	 */	
	public Session() {}

	/**
	 * This method sets ongoingSession= true, simulating that a new session
	 * has started.
	 */
	public void startSession() {
		
		ongoingSession = true;

	}

	/**
	 * This method returns ongoingSession as true or false.
	 */
	public boolean sessionActive() {
		
		return ongoingSession;
	}
	
	
	/**
	 * This method sets ongoingSession to false, ending the session.
	 */
	public void endSession() {
		
		ongoingSession = false;
		
	}
}
