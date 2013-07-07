package com.nbs.q3.spi;

public enum RxDataPipes
{
	pipe0(0),pipe1(1),pipe2(2),pipe3(3),pipe4(4),pipe5(5);
	private int pipeNumber;
	private RxDataPipes(int pipe)
	{
		pipeNumber = pipe;
	}
	public int getDataPipe()
	{
		return pipeNumber;
	}
	
}
