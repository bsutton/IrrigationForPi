package au.org.noojee.irrigation.views;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.server.Responsive;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import au.org.noojee.irrigation.entities.GardenFeature;

public class TimerDialog extends Window
{
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger();
	private GardenFeature gardenFeature;
	
	private boolean wasCancelled = false;
	
	TimerNotification timerNotifications;
	
	TimerDialog(String title, GardenFeature gardenFeature, TimerNotification timerNotifications)
	{
		this.timerNotifications = timerNotifications;
		
		this.setWidth("50%");
		this.setClosable(false);
		this.setResizable(false);
		this.setModal(true);
		
		this.gardenFeature = gardenFeature;
		
		VerticalLayout subContent = new VerticalLayout();
		this.setContent(subContent);

		// Put some components in it
		Label heading = new Label(title);
		subContent.addComponent(heading);
		heading.setStyleName("i4p-label");
		Responsive.makeResponsive(heading);
		
		// re-add for quick test option.
		
//		Button time1 = new Button("20 Seconds");
//		time1.setWidth("100%");
//		subContent.addComponent(time1);
//		time1.setData(Duration.ofSeconds(20));
//		time1.addClickListener(l -> startTimer(l));

		Button time15 = new Button("15 Minutes");
		time15.setWidth("100%");
		subContent.addComponent(time15);
		time15.setData(Duration.ofMinutes(15));
		time15.addClickListener(l -> startTimer(l));
		
		Button time20 = new Button("20 Minutes");
		time20.setWidth("100%");
		subContent.addComponent(time20);
		time20.setData(Duration.ofMinutes(20));
		time20.addClickListener(l -> startTimer(l));

		Button time30 = new Button("30 Minutes");
		time30.setWidth("100%");
		subContent.addComponent(time30);
		time30.setData(Duration.ofMinutes(30));
		time30.addClickListener(l -> startTimer(l));

		Button time45 = new Button("45 Minutes");
		time45.setWidth("100%");
		subContent.addComponent(time45);
		time45.setData(Duration.ofMinutes(45));
		time45.addClickListener(l -> startTimer(l));

		Button time60 = new Button("1 Hour");
		time60.setWidth("100%");
		subContent.addComponent(time60);
		time60.setData(Duration.ofMinutes(60));
		time60.addClickListener(l -> startTimer(l));

		Button time90 = new Button("90 Minutes");
		time90.setWidth("100%");
		subContent.addComponent(time90);
		time90.setData(Duration.ofMinutes(90));
		time90.addClickListener(l -> startTimer(l));

		Button time120 = new Button("2 Hours");
		time120.setWidth("100%");
		subContent.addComponent(time120);
		time120.setData(Duration.ofMinutes(120));
		time120.addClickListener(l -> startTimer(l));

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setWidth("100%");
		
		subContent.addComponent(buttons);
		Button cancel = new Button("Cancel");
		cancel.setStyleName(ValoTheme.BUTTON_PRIMARY);
		cancel.setWidth("100%");
		buttons.addComponent(cancel);
		cancel.addClickListener(l -> {this.close(); wasCancelled = true;});
		

		// Center it in the browser window
		this.center();
	}

	private void startTimer(ClickEvent l)
	{
		Duration delay = (Duration)l.getButton().getData();
		gardenFeature.runForTime(delay);
		
		timerNotifications.timerStarted(this.gardenFeature, delay);
		
		this.close();
	}

	// Open it in the UI
	void show(UI ui)
	{
		ui.addWindow(this);
	}

	public boolean wasCancelled()
	{
		return wasCancelled;
	}

}