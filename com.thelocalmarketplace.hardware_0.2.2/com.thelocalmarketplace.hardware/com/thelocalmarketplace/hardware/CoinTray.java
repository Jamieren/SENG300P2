package com.thelocalmarketplace.hardware;

import java.util.ArrayList;
import java.util.List;

import com.tdc.AbstractComponent;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponentObserver;
import com.tdc.Sink;
import com.tdc.coin.Coin;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

/**
 * Simulates the tray where dispensed coins go for the user to collect them. It
 * is a passive device requiring no power, being gravity-fed.
 */
public class CoinTray extends AbstractComponent<IComponentObserver> implements Sink<Coin> {
	private Coin[] coins;
	private int nextIndex = 0;

	/**
	 * Creates a coin tray.
	 * 
	 * @param capacity
	 *            The maximum number of coins that this tray can hold without
	 *            overflowing.
	 * @throws SimulationException
	 *             If the capacity is &le;0.
	 */
	public CoinTray(int capacity) {
		if(capacity <= 0)
			throw new InvalidArgumentSimulationException("capacity must be positive.");

		coins = new Coin[capacity];
	}

	/**
	 * Causes the indicated coin to be added to the tray. Causes no events.
	 * 
	 * @param coin
	 *            The coin to add.
	 * @throws SimulationException
	 *             If coin is null.
	 * @throws CashOverloadException
	 *             If the tray overflows.
	 */
	public synchronized void receive(Coin coin) throws CashOverloadException, DisabledException {
		if(coin == null)
			throw new NullPointerSimulationException("coin");

		if(nextIndex < coins.length)
			coins[nextIndex++] = coin;
		else
			throw new CashOverloadException("The tray has overflowed.");
	}

	/**
	 * Simulates the act of physically removing coins from the try by a user.
	 * 
	 * @return The list of coins collected. May not be null. May be empty.
	 */
	public synchronized List<Coin> collectCoins() {
		List<Coin> result = new ArrayList<>();

		for(int i = 0; i < coins.length; i++) {
			Coin coin = coins[i];

			if(coin != null) {
				result.add(coin);
				coins[i] = null;
			}
		}

		return result;
	}

	/**
	 * Returns whether this coin receptacle has enough space to accept at least one
	 * more coin: always true. Causes no events.
	 */
	@Override
	public synchronized boolean hasSpace() {
		return true;
	}

	@Override
	public synchronized void connect(PowerGrid grid) {
		throw new UnsupportedOperationException("This is not an electrical device");
	}

	@Override
	public synchronized void disconnect() {
		throw new UnsupportedOperationException("This is not an electrical device");
	}

	@Override
	public synchronized void activate() {
		throw new UnsupportedOperationException("This is not an electrical device");
	}

	@Override
	public synchronized void disactivate() {
		throw new UnsupportedOperationException("This is not an electrical device");
	}
}
