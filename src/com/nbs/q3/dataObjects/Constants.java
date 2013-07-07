package com.nbs.q3.dataObjects;

public interface Constants
{
	public static final int BUFFER_SIZE = 1024;
	public static final int NUM_DATA_PACKETS = 10;
	public static final byte DEFAULT_BUTTON_BYTE = 0x7F;
	public static final int DEFAULT_WINDOW_SIZE=5;
	public static final Byte[] DEFAULT_AXIS_DATA= new Byte[]{new Byte((byte)0),new Byte((byte)0),new Byte((byte)0)};
	public static final int DIRECTION_NORTH=1;
	public static final int DIRECTION_SOUTH=-1;
	public static final int DIRECTION_EAST=2;
	public static final int DIRECTION_WEST=-2;
	
	public static final int MAXY=33; //rotate remote southwards
	public static final int MINY=-33; //rotate remote north
	public static final int MAXX=34; //tilt down east
	public static final int MINX=-34; //tilt down west
}
