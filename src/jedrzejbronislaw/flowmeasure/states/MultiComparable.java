package jedrzejbronislaw.flowmeasure.states;

public interface MultiComparable {
	
	public default boolean isOneOf(MultiComparable... stateTypes) {
		for (int i=0; i<stateTypes.length; i++)
			if (stateTypes[i] == this)
				return true;
		return false;
	}
}
