package jedrzejbronislaw.flowmeasure.states;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class StateComparator {

	@NonNull
	@Getter
	private State state;
	
	public boolean isOneOf(State... stateTypes) {
		for (int i=0; i<stateTypes.length; i++)
			if (stateTypes[i] == state)
				return true;
		return false;
	}
}
