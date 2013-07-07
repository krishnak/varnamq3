package com.nbs.q3.spi;

public class RxAddressDataPipe4 extends NRFRegister
{
	private static RxAddressDataPipe4 registerInstance = null;
	private RxAddressDataPipe4()
	{
		this.address=0x0E;
		
	}
	public static RxAddressDataPipe4 getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new RxAddressDataPipe4();
		return registerInstance;
	}
}
