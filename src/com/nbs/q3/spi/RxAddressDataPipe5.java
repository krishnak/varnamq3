package com.nbs.q3.spi;

public class RxAddressDataPipe5 extends NRFRegister
{
	private static RxAddressDataPipe5 registerInstance = null;
	private RxAddressDataPipe5()
	{
		this.address=0x0F;
		
	}
	public static RxAddressDataPipe5 getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new RxAddressDataPipe5();
		return registerInstance;
	}
}
