package jedrzejbronislaw.flowmeasure.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.Unit;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.TimeFormat;
import jedrzejbronislaw.flowmeasure.tools.Injection;

public class SaveWindowController implements Initializable {

	@FXML
	private CheckBox unixTime, fullTime, processTime;

	@FXML
	private CheckBox unit_pulses, unit_flow;
	
	@FXML
	private RadioButton pulses_first, flow_first;

	@FXML
	private RadioButton together, separately;
	
	@FXML
	private RadioButton comma_separator, dot_separator;
	
	@FXML
	private Button saveButton;

	private Consumer<ProcessRepositoryWriterOptions> saveAction;
	private Runnable exitAction;

	public SaveWindowController setSaveAction(Consumer<ProcessRepositoryWriterOptions> saveAction) {
		this.saveAction = saveAction;
		return this;
	}
	public SaveWindowController setExitAction(Runnable exitAction) {
		this.exitAction = exitAction;
		return this;
	}

	
	private ProcessRepositoryWriterOptions getOptions() {
		ProcessRepositoryWriterOptions options = new ProcessRepositoryWriterOptions();

		if(unixTime.isSelected())
			options.getTimeFormats().add(TimeFormat.Unix);
		if(fullTime.isSelected())
			options.getTimeFormats().add(TimeFormat.Full);
		if(processTime.isSelected())
			options.getTimeFormats().add(TimeFormat.ProcessTime);
		
		if(unit_pulses.isSelected() && unit_flow.isSelected()) {
			if(flow_first.isSelected()) {
				options.getUnits().add(Unit.Flow);
				options.getUnits().add(Unit.Pulses);
			} else {
				options.getUnits().add(Unit.Pulses);
				options.getUnits().add(Unit.Flow);
			}
		} else
			if (unit_pulses.isSelected())
			options.getUnits().add(Unit.Pulses);
		else
			if (unit_flow.isSelected())
			options.getUnits().add(Unit.Flow);
		
		options.setFlowmeterValuesTogether(together.isSelected());

		options.setCommaSeparator(comma_separator.isSelected());
		
		return options;
	}

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		saveButton.setOnAction(e -> {
			Injection.run(saveAction, getOptions());
			Injection.run(exitAction);
		});

		EventHandler<ActionEvent> timeAction = event -> {
			if(!unixTime.isSelected() && !fullTime.isSelected() && !processTime.isSelected()) 
				((CheckBox)event.getSource()).setSelected(true);
		};
		
		unixTime.setOnAction(timeAction);
		fullTime.setOnAction(timeAction);
		processTime.setOnAction(timeAction);
		

		EventHandler<ActionEvent> unitAction = event -> {
			if(!unit_flow.isSelected() && !unit_pulses.isSelected()) 
				((CheckBox)event.getSource()).setSelected(true);
		};
		
		unit_flow.setOnAction(unitAction);
		unit_pulses.setOnAction(unitAction);
		
		
	}

}
