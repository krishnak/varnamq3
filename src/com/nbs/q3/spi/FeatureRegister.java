package com.nbs.q3.spi;

public class FeatureRegister extends NRFRegister
{
	private static FeatureRegister registerInstance = null;
	private FeatureRegister()
	{
		this.address=0x1D;
		
	}
	public static FeatureRegister getInstance()
	{
		if(registerInstance == null)
			 registerInstance = new FeatureRegister();
		return registerInstance;
	}
}
