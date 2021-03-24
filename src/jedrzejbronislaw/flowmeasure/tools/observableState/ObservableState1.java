package jedrzejbronislaw.flowmeasure.tools.observableState;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

public class ObservableState1<T extends Enum<?>> implements ObservableState<T>, StateChanger<T> {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Getter
	private T state;
	private List<StateListener<T>> stateListeners = new ArrayList<>();

	public ObservableState1(T initState) {
		setState(initState);
	}
	
	public boolean is(T state) {
		return this.state == state;
	}
	
	@Override
	public void setState(T state) {
		log.info("-> New {}: {}", state.getClass().getSimpleName(), state);
		
		this.state = state;
		stateListeners.forEach(listener -> listener.onChangeState(state));
	}
	
	@Override
	public void addStateListener(StateListener<T> listener) {
		stateListeners.add(listener);
		listener.onChangeState(state);
	}
}
