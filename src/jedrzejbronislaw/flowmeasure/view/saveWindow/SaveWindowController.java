package jedrzejbronislaw.flowmeasure.view.saveWindow;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.DecimalSeparator;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.TimeFormat;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.Unit;
import jedrzejbronislaw.flowmeasure.tools.Injection;
import lombok.Setter;

public class SaveWindowController implements Initializable {

	@FXML private CheckBox    unixTime, fullTime, processTime;
	@FXML private CheckBox    unit_pulses, unit_flow;
	@FXML private RadioButton pulses_first, flow_first;
	@FXML private RadioButton together, separately;
	@FXML private RadioButton comma_separator, dot_separator;
	@FXML private CheckBox    metadata, headers;
	
	@FXML private VBox orderBox;
	@FXML private VBox togetherBox;

	@FXML private CheckBox openBox;
	@FXML private Button saveButton;

	@Setter private BiConsumer<ProcessRepositoryWriterOptions, Boolean> saveAction;
	@Setter private Runnable exitAction;

	
	private ProcessRepositoryWriterOptions getOptions() {
		ProcessRepositoryWriterOptions options = new ProcessRepositoryWriterOptions();
		Set<TimeFormat> timeFormats = options.getTimeFormats();
		List<Unit> units = options.getUnits();

		if(unixTime.isSelected())    timeFormats.add(TimeFormat.UNIX);
		if(fullTime.isSelected())    timeFormats.add(TimeFormat.FULL);
		if(processTime.isSelected()) timeFormats.add(TimeFormat.PROCESS_TIME);
		
		if(unit_pulses.isSelected() && unit_flow.isSelected())
			if(flow_first.isSelected()) {
				units.add(Unit.FLOW);
				units.add(Unit.PULSES);
			} else {
				units.add(Unit.PULSES);
				units.add(Unit.FLOW);
			}
		else if (unit_pulses.isSelected()) units.add(Unit.PULSES);
		else if (unit_flow.isSelected())   units.add(Unit.FLOW);
		
		options.setFlowmeterValuesTogether(together.isSelected());
		options.setDecimalSeparator(comma_separator.isSelected() ? DecimalSeparator.COMMA : DecimalSeparator.POINT);

		options.setSaveMetadata(metadata.isSelected());
		options.setSaveHeaders(headers.isSelected());
		
		return options;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		saveButton.setOnAction(e -> {
			Injection.run(saveAction, getOptions(), openAfterSaving());
			Injection.run(exitAction);
		});

		EventHandler<ActionEvent> timeAction = oneMustBeSelected(unixTime, fullTime, processTime);
		unixTime.setOnAction(timeAction);
		fullTime.setOnAction(timeAction);
		processTime.setOnAction(timeAction);
		
		EventHandler<ActionEvent> unitAction = oneMustBeSelected(unit_flow, unit_pulses);
		unit_flow.setOnAction(unitAction);
		unit_pulses.setOnAction(unitAction);
		
		orderBox   .disableProperty().bind(unit_flow.selectedProperty().and(unit_pulses.selectedProperty()).not());
		togetherBox.disableProperty().bind(unit_flow.selectedProperty().and(unit_pulses.selectedProperty()).not());
	}

	private EventHandler<ActionEvent> oneMustBeSelected(CheckBox... boxes) {
		return event -> {
			CheckBox sourceBox = (CheckBox) event.getSource();
			
			if (Stream.of(boxes).allMatch(box -> !box.isSelected()))
				sourceBox.setSelected(true);
		};
	}

	private boolean openAfterSaving() {
		return openBox.isSelected();
	}
}
