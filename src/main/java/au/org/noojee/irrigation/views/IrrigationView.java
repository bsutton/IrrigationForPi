package au.org.noojee.irrigation.views;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

import au.org.noojee.irrigation.types.GardenBed;

public class IrrigationView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "Irrigation";

	Grid<GardenBed> grid;
	
	List<GardenBed> gardenBeds = new ArrayList<>();
	
	ListDataProvider<GardenBed> gardenData = new ListDataProvider<>(gardenBeds);
	
	@Override
	public void enter(ViewChangeEvent event) {
		View.super.enter(event);
		
		
		 if (grid == null)
		 {
			 grid = new Grid<>();
			 
			// grid.addColumn(GardenBed::getName);
		 }
		
		
		
	}

}
