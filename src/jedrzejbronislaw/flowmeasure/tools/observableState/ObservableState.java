package jedrzejbronislaw.flowmeasure.tools.observableState;

public interface ObservableState<T extends Enum<?>> {

	public void addStateListener(StateListener<T> listener);
}
