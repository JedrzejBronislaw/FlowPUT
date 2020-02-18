package jedrzejbronislaw.flowmeasure.tools.observableState;

public interface ObservableState<T extends Enum> {

	public void addStateListiner(StateListener<T> listener);

}
