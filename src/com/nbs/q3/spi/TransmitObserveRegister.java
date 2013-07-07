package com.nbs.q3.spi;

public class TransmitObserveRegister extends NRFRegister
{
	private static TransmitObserveRegister registerInstance = null;
	private TransmitObserveRegister()
	{
		this.address=0x08;
		
	}
	public static TransmitObserveRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new TransmitObserveRegister();
		return registerInstance;
	}
}
