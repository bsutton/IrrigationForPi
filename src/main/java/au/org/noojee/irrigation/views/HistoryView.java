package au.org.noojee.irrigation.views;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.dao.HistoryDao;
import au.org.noojee.irrigation.entities.GardenBed;
import au.org.noojee.irrigation.entities.GardenFeature;
import au.org.noojee.irrigation.entities.History;
import au.org.noojee.irrigation.util.Formatters;

public class HistoryView extends VerticalLayout implements SmartView
{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "History";

	@Override
	public void enter(ViewChangeEvent event)
	{
		SmartView.super.enter(event);
		this.removeAllComponents();

		HorizontalLayout headingLayout = new HorizontalLayout();
		this.addComponent(headingLayout);
		headingLayout.setWidth("100%");

		Label gardenHeading = new Label("Feature");
		headingLayout.addComponent(gardenHeading);
		gardenHeading.addStyleName(ValoTheme.LABEL_H3);
		gardenHeading.addStyleName(ValoTheme.LABEL_BOLD);
		//gardenHeading.setStyleName("i4p-heading");
		Responsive.makeResponsive(gardenHeading);


		headingLayout.setExpandRatio(gardenHeading, 1.0f);

		Label startDateHeading = new Label("Date/Time");
		headingLayout.addComponent(startDateHeading);
		startDateHeading.setWidth("40mm");
		startDateHeading.addStyleName(ValoTheme.LABEL_H3);
		startDateHeading.addStyleName(ValoTheme.LABEL_BOLD);
		Responsive.makeResponsive(startDateHeading);


		Label durationHeading = new Label("Duration");
		headingLayout.addComponent(durationHeading);
		durationHeading.setWidth("20mm");
		durationHeading.addStyleName(ValoTheme.LABEL_H3);
		durationHeading.addStyleName(ValoTheme.LABEL_BOLD);
		Responsive.makeResponsive(durationHeading);


		Responsive.makeResponsive(gardenHeading, startDateHeading, durationHeading);

		List<History> histories;

		HistoryDao daoHistory = new HistoryDao();

		histories = daoHistory.getAll();

		for (History history : histories)
		{
			HorizontalLayout historyHorizontal = new HorizontalLayout();
			this.addComponent(historyHorizontal);
			historyHorizontal.setWidth("100%");

			Label featureIcon;
			if (history.getGardenFeature() instanceof GardenBed)
				featureIcon = new Label(VaadinIcons.DROP.getHtml());
			else
				featureIcon = new Label(VaadinIcons.LIGHTBULB.getHtml());
			featureIcon.setContentMode(ContentMode.HTML);
			historyHorizontal.addComponent(featureIcon);

			GardenFeature gardenFeature = history.getGardenFeature();
			Label gardenLabel = new Label(gardenFeature.getName());
			historyHorizontal.addComponent(gardenLabel);
			historyHorizontal.setExpandRatio(gardenLabel, 1.0f);

			gardenLabel.setStyleName("i4p-label");

			// gardenLabel.setWidth(SWITCH_WIDTH, Unit.MM);

			// Label
			LocalDateTime startDate = history.getStart();
			Label startDateLabel = new Label(Formatters.format(startDate));
			historyHorizontal.addComponent(startDateLabel);
			startDateLabel.setWidth("40mm");
			startDateLabel.setStyleName("i4p-label");

			Duration duration = history.getDuration();
			Label durationLabel = new Label(Formatters.format(duration));
			historyHorizontal.addComponent(durationLabel);
			durationLabel.setWidth("20mm");
			durationLabel.setStyleName("i4p-label");

			Responsive.makeResponsive(gardenLabel, startDateLabel, durationLabel);

		}
	}

	@Override
	public String getName()
	{
		return NAME;
	}

}
