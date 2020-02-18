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
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.Columns;
import jedrzejbronislaw.flowmeasure.model.processRepositoryWriter.ProcessRepositoryWriterOptions.TimeFormat;

public class SaveWindowController implements Initializable {

	@FXML
	private CheckBox unixTime, fullTime, processTime;

	@FXML
	private CheckBox col_pulses, col_flow;
	
	@FXML
	private RadioButton pulses_first, flow_first;

	@FXML
	private RadioButton together, separately;
	
	@FXML
	private RadioButton comma_separator, dot_separator;
//	enum State {
//		unavailable, available, ongoing
//	}

	
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
		
		if(col_pulses.isSelected() && col_flow.isSelected()) {
			if(flow_first.isSelected()) {
				options.getColumns().add(Columns.Flow);
				options.getColumns().add(Columns.Pulses);
			} else {
				options.getColumns().add(Columns.Pulses);
				options.getColumns().add(Columns.Flow);
			}
		} else
			if (col_pulses.isSelected())
			options.getColumns().add(Columns.Pulses);
		else
			if (col_flow.isSelected())
			options.getColumns().add(Columns.Flow);
		
		options.setFlowmeterValuesTogether(together.isSelected());

		options.setCommaSeparator(comma_separator.isSelected());
		
		return options;
	}

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		saveButton.setOnAction(e -> {
			if (saveAction != null)
				saveAction.accept(getOptions());
			if (exitAction != null)
				exitAction.run();
			
		});

		EventHandler<ActionEvent> timeAction = event -> {
			if(!unixTime.isSelected() && !fullTime.isSelected() && !processTime.isSelected()) 
				((CheckBox)event.getSource()).setSelected(true);
		};
		
		unixTime.setOnAction(timeAction);
		fullTime.setOnAction(timeAction);
		processTime.setOnAction(timeAction);
		

		EventHandler<ActionEvent> columnAction = event -> {
			if(!col_flow.isSelected() && !col_pulses.isSelected()) 
				((CheckBox)event.getSource()).setSelected(true);
		};
		
		col_flow.setOnAction(columnAction);
		col_pulses.setOnAction(columnAction);
		
		
	}

}
