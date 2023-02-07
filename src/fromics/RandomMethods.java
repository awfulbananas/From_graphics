package fromics;

import java.util.ArrayList;
import java.util.List;

public class RandomMethods {
	public static void main(String[] args) {
		findThing();
	}
	
	public static String crossLetters(int size, char a, char b) {
		String s = "";
		String[] aChunk = chunk(size, a, true);
		String[] bChunk = chunk(size, b, false);
		for(int i = 0; i < size; i++) {
			s += aChunk[i] + bChunk[i] + "\n";
		}
		for(int i = 0; i < size; i++) {
			s += bChunk[i] + aChunk[i] + "\n";
		}
		return s;
	}
	
	public static void findThing() {
		int n = 10;
		List<List<Integer>> combos = new ArrayList<>();
	    findCombos(combos, n);
	    System.out.println(combos);
	    System.out.println(combos.size());
	}
	
	public static void findCombos(List<List<Integer>> combos, int n) {
	    if(n > 3) {
	      for(int i = 1; i < Math.floor(n / 2); i++) {
	        List<Integer> ofTwo = new ArrayList<>();
	        ofTwo.add(i);
	        ofTwo.add(n - i);
	        ofTwo.sort(null);
	        if(!combos.contains(ofTwo)) combos.add(ofTwo);
	        List<List<Integer>> cOfI = new ArrayList<>();
	        findCombos(cOfI, n - i);
	        for(List<Integer> combo : cOfI) {
	          combo.add(i);
	          combo.sort(null);
	          if(!combos.contains(combo)) combos.add(combo);
	        }
	      }
	      List<Integer> finalCombo = new ArrayList<>();
	      finalCombo.add(n / 2);
	      finalCombo.add((n / 2) + (n % 2));
	      if(!combos.contains(finalCombo)) combos.add(finalCombo);
	    } else if(n == 2) {
	      List<Integer> combo = new ArrayList<>() ;
	      combo.add(1);
	      combo.add(1);
	      combos.add(combo);
	    } else {
	      List<Integer> combo1 = new ArrayList<>();
	      combo1.add(1);
	      combo1.add(1);
	      combo1.add(1);
	      List<Integer> combo2 = new ArrayList<>();
	      combo2.add(1);
	      combo2.add(2);
	      combos.add(combo1);
	      combos.add(combo2);
	    }
	    
	  }
	
	private static String[] chunk(int size, char c, boolean dir) {
		String[] chunk = new String[size];
		if(dir) {
			for(int i = 0; i < size; i++) {
				chunk[i] = " ".repeat(i) + c + " ".repeat(size - i - 1);
			}
		} else {
			for(int i = 0; i < size; i++) {
				chunk[i] = " ".repeat(size - i - 1) + c + " ".repeat(i);
			}
		}
		return chunk;
	}
}
