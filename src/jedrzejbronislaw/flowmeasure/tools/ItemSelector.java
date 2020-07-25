package jedrzejbronislaw.flowmeasure.tools;

import java.util.ArrayList;
import java.util.List;

public class ItemSelector<T> {

	public List<T> select(List<T> list, int numOfItems) {
		List<T> newList;
		int oriListSize = list.size();
		
		if(numOfItems > oriListSize) throw new IllegalArgumentException("numOfItems cannot be grater than list size");
		if(numOfItems < 2) throw new IllegalArgumentException("numOfItems cannot be lower than 2");

		newList = new ArrayList<T>(numOfItems);
		
		newList.add(list.get(0));
		int index;
		float factor = (float)(oriListSize-1)/(numOfItems-1);
		
		for(int i=1; i<=numOfItems-1; i++) {
			index = Math.round(factor*i);
			newList.add(list.get(index));
		}
		
		return newList;
	}
}
