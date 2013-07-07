package com.nbs.q3.spi;

public class Jnitest {
	
	private native void print();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Jnitest().print();

	}
	static {
		System.loadLibrary("Jnitest");
		}
}
