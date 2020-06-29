package jedrzejbronislaw.flowmeasure.states;

public interface State {
	
	public default boolean isOneOf(State... stateTypes) {
		for (int i=0; i<stateTypes.length; i++)
			if (stateTypes[i] == this)
				return true;
		return false;
	}
}
