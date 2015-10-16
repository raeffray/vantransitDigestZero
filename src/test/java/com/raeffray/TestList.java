package com.raeffray;

import java.util.ArrayList;
import java.util.List;

public class TestList {

	public void testSplitList() {

		List<String> list = new ArrayList<String>();

		list.add("A");
		list.add("B");
		list.add("C");
		list.add("D");
		 list.add("E");
		 list.add("F");
		 list.add("G");
		 list.add("H");
		 list.add("I");
		 list.add("J");
		 list.add("K");
		 list.add("L");
		 list.add("M");
		 list.add("N");
		 list.add("O");
		 list.add("P");
		 list.add("Q");
		 list.add("R");
		 list.add("S");
		 list.add("T");
		 list.add("U");

		int size = list.size();

		double stepSize = 6;

		// double size = 20;

		double result = size / stepSize;

		double steps = Math.floor(result);

		double leftOver = size - (stepSize * steps);

		int idx = 0;
		for (int i = 0; i <= steps; i++) {
			idx += stepSize;
			int start = 0;
			int end = 0;
			start = (int) (idx - stepSize);
			if (i == steps) {
				end = (int) (start + (leftOver));
				if (start == end) {
					break;
				}
			} else {
				end = (int) (idx);
			}
			List<String> subList = list.subList(start, end);
			System.out.println(start + "," + end);
			System.out.println(subList);

		}

	}
}
