package com.nbs.q3.gpio;

public class InPin extends GpioPin {

	public InPin(int pinNumber) {
		super(pinNumber, Direction.IN);
	}
}
