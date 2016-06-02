package com.n22.test;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DecimalTest {

	public static void main(String[] args) {
		System.out.println(3.001);
		System.out.println("------------");
		
		DecimalFormat df = new DecimalFormat("0.0");
		df.setRoundingMode(RoundingMode.HALF_UP);
		System.out.println(df.format(0.05));
	}
}
