package com.nbs.q3.spi;

public enum NRFCommand
{
	ReadRxPayload(0x61),WriteTxPayload(0xA0),FlushTx(0xE1),FlushRx(0xE2),
	ReusePayLoad(0xE3),Activate(0x50),ReadRxPayLoadWidth(0x60),
	WriteAckPayLoadP0(0xA8),WriteAckPayLoadP1(0xA9),WriteAckPayLoadP2(0xAA),
	WriteAckPayLoadP3(0xAB),WriteAckPayLoadP4(0xAC),WriteAckPayLoadP5(0xAD),
	WriteTxPayloadNoAck(0xB0);
	
	private int command;
	private NRFCommand(int command)
	{
		this.command = command;
	}
	public int getCommand()
	{
		return command;
	}

}
