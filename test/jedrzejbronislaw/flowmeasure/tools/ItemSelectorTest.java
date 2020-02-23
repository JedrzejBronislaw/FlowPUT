package jedrzejbronislaw.flowmeasure.tools;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ItemSelectorTest {

	ItemSelector<Integer> selector;
	List<Integer> list;
	List<Integer> smallList;
	
	@Before
	public void before() {
		selector = new ItemSelector<Integer>();
		list = new ArrayList<Integer>();
		smallList = new ArrayList<Integer>();
		
		for(int i=0; i<100; i++) 
			list.add(i);
		
		for(int i=0; i<5; i++) 
			smallList.add(i);
	}

	@Test
	public void test_numOfResultsItems() {
		List<Integer> newList = selector.select(list, 10);
		
		assertEquals(10, newList.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_numOfResultsItems_toMuch_illegalArgument() {
		selector.select(list, 101);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_numOfResultsItems_1_illegalArgument() {
		selector.select(list, 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_numOfResultsItems_0_illegalArgument() {
		selector.select(list, 0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_numOfResultsItems_minus1_illegalArgument() {
		selector.select(list, -1);
	}

	@Test
	public void test_2values_smallList() {
		List<Integer> newList = selector.select(smallList, 2);

		assertEquals(2, newList.size());
		assertEquals(0, newList.get(0).intValue());
		assertEquals(4, newList.get(1).intValue());
	}

	@Test
	public void test_3values_smallList() {
		List<Integer> newList = selector.select(smallList, 3);

		assertEquals(3, newList.size());
		assertEquals(0, newList.get(0).intValue());
		assertEquals(2, newList.get(1).intValue());
		assertEquals(4, newList.get(2).intValue());
	}

	@Test
	public void test_5values_smallList() {
		List<Integer> newList = selector.select(smallList, 5);

		assertEquals(5, newList.size());
		Integer[] expecteds = new Integer[] {0,1,2,3,4};

		assertArrayEquals(expecteds, newList.toArray());
	}

	@Test
	public void test_2values() {
		List<Integer> newList = selector.select(list, 2);

		assertEquals(2, newList.size());
		assertEquals(0, newList.get(0).intValue());
		assertEquals(99, newList.get(1).intValue());
	}
	
	@Test
	public void test_4values() {
		List<Integer> newList = selector.select(list, 4);

		assertEquals(4, newList.size());
		assertEquals(0, newList.get(0).intValue());
		assertEquals(33, newList.get(1).intValue());
		assertEquals(66, newList.get(2).intValue());
		assertEquals(99, newList.get(3).intValue());
	}
	
	@Test
	public void test_valueOrder() {
		List<Integer> newList = selector.select(list, 12);
		int prevValue;
		
		assertEquals(12, newList.size());
		
		prevValue = newList.get(0).intValue();
		for(int i=1; i<newList.size(); i++) {
			assertTrue(prevValue + " [i:" + (i-1) + "] is not lower than " + newList.get(i) + " [i:" + (i) + "]",
					prevValue < newList.get(i));
			prevValue = newList.get(i);
		}
	}

}
