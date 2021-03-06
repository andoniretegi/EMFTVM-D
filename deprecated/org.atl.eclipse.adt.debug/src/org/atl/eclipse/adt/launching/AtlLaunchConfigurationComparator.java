/*
 * Created on 29 avr. 2004
 */
package org.atl.eclipse.adt.launching;

import java.util.Comparator;
import java.util.List;

/**
 * @author allilaire
 */
public class AtlLaunchConfigurationComparator implements Comparator {

	/*
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		List list1 = (List)o1;
		List list2 = (List)o2;
		
		if (list1.size() == list2.size()) {
			for (int i = 0; i < list1.size(); i++) {
				String memento1 = (String)list1.get(i);
				String memento2 = (String)list2.get(i);
				if (!equalsIgnoreWhitespace(memento1, memento2)) {
					return -1;
				}
			}
			return 0;
		}
		return -1;
	}
	
	protected boolean equalsIgnoreWhitespace(String one, String two) {
		int i1 = 0;
		int i2 = 0;
		int l1 = one.length();
		int l2 = two.length();
		char ch1 = ' ';
		char ch2 = ' ';
		while (i1 < l1 && i2 < l2) {
			while (i1 < l1 && Character.isWhitespace(ch1 = one.charAt(i1))) {
				i1++;
			}
			while (i2 < l2 && Character.isWhitespace(ch2 = two.charAt(i2))) {
				i2++;
			}
			if (i1 == l1 && i2 == l2) {
				return true;
			}
			if (ch1 != ch2) {
				return false;
			}			
			i1++;
			i2++;
		}
		return true;
	}
}
