package jedrzejbronislaw.flowmeasure.tools.observableState;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class ObservableState1<T extends Enum<?>> implements ObservableState<T>, StateChanger<T> {
	
	@Getter
	private T state;
	private List<StateListener<T>> stateListeners = new ArrayList<>();

	public ObservableState1(T initState) {
		state = initState;
	}
	
	public boolean is(T state) {
		return this.state == state;
	}
	
	@Override
	public void setState(T state) {
		this.state = state;
		stateListeners.forEach(listener -> listener.onChangeState(state));
	}
	
	@Override
	public void addStateListener(StateListener<T> listener) {
		stateListeners.add(listener);
		listener.onChangeState(state);
	}
}
