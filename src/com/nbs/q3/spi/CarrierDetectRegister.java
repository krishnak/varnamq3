package com.nbs.q3.spi;

public class CarrierDetectRegister extends NRFRegister
{
	private static CarrierDetectRegister registerInstance = null;
	private CarrierDetectRegister()
	{
		this.address=0x09;
		
	}
	public static CarrierDetectRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new CarrierDetectRegister();
		return registerInstance;
	}
}
