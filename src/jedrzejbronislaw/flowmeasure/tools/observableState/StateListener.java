package jedrzejbronislaw.flowmeasure.tools.observableState;

public interface StateListener<T extends Enum> {
	
	void onChangeState(T state);

	
}
