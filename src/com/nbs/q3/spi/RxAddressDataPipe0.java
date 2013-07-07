package com.nbs.q3.spi;

public class RxAddressDataPipe0 extends NRFRegister
{
	private static RxAddressDataPipe0 registerInstance = null;
	private RxAddressDataPipe0()
	{
		this.address=0x0A;
		this.registerLength = 5;
		
	}
	public static RxAddressDataPipe0 getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new RxAddressDataPipe0();
		return registerInstance;
	}
}
