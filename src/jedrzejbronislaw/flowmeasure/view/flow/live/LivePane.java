package jedrzejbronislaw.flowmeasure.view.flow.live;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.application.Components;
import jedrzejbronislaw.flowmeasure.components.flowConverter.FlowConverters;
import jedrzejbronislaw.flowmeasure.settings.Consts;
import jedrzejbronislaw.flowmeasure.settings.FlowmeterNameProperty;
import jedrzejbronislaw.flowmeasure.settings.Settings;
import jedrzejbronislaw.flowmeasure.tools.MyFXMLLoader2;
import jedrzejbronislaw.flowmeasure.view.ViewMediator;
import jedrzejbronislaw.flowmeasure.view.flow.live.flowPreview.FlowPreview;

public class LivePane extends VBox implements Initializable {

	@FXML private VBox flowBox;
	@FXML private Button resetButton;
	
	private ViewMediator viewMediator;
	private FlowConverters flowConverters;
	private Settings settings;
	
	private List<FlowPreview> flowsPreviews = new ArrayList<>(Consts.FLOWMETERS_NUMBER);

	
	public LivePane() {
		MyFXMLLoader2.create("LivePane.fxml", this);
		
		Components.getComponentsLoader().addLoadMethod(() -> {
			viewMediator   = Components.getViewMediator();
			flowConverters = Components.getFlowConverters();
			settings       = Components.getSettings();
			init();
		});
	}

	
	public void addFlowPreview(Node node) {
		flowBox.getChildren().add(node);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
	
	private void init() {
		resetButton.setOnAction(e -> this.resetVolumes());
		
		for (int i=0; i<Consts.FLOWMETERS_NUMBER; i++)
			addFlowPreview(createFlowPreview(i));
		
		settings.addChangeListener(this::updateFlowmeterNames);
	}

	private void updateFlowmeterNames() {
		for (int i=0; i<flowsPreviews.size(); i++)
			flowsPreviews.get(i).setName(settings.getString(new FlowmeterNameProperty(i)));
	}

	private Node createFlowPreview(int number) {
		FlowPreview flowPreview = new FlowPreview(flowConverters.get(number));
		
		flowsPreviews.add(flowPreview);
		viewMediator.setFlowPreviewer(number, flowPreview::addPulses);
		
		return flowPreview;
	}
	
	private void resetVolumes() {
		flowsPreviews.forEach(flow -> flow.resetSecPulse());
	}
}
