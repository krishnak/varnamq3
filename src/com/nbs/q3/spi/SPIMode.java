package com.nbs.q3.spi;

public enum SPIMode 
{
	mode0((byte)0x00),mode1((byte)0x01),mode2((byte)0x02),mode3((byte)0x03);
	
	private byte spiMode;
	private SPIMode(byte mode)
	{
		spiMode = mode;
	}
	
	public byte getSpiMode()
	{
		return spiMode; 
	}
}
