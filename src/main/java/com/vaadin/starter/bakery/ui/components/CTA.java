package com.vaadin.starter.bakery.ui.components;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Call to Action to be used for published demo
 */
public class CTA {

	public static void show() {
		Notification notification = new Notification();
		notification.getElement().setAttribute("theme", "cta");

		HorizontalLayout ctaContainer = new HorizontalLayout();
		ctaContainer.addClassName("cta-container");
		ctaContainer.setDefaultVerticalComponentAlignment(Alignment.CENTER);

		VerticalLayout textContainer = new VerticalLayout();
		textContainer.setPadding(false);
		textContainer.setMargin(false);
		textContainer.setSpacing(false);

		HorizontalLayout ctaContent = new HorizontalLayout();
		ctaContent.setSizeFull();
		ctaContent.setPadding(false);
		ctaContent.setMargin(false);
		ctaContent.setAlignItems(Alignment.CENTER);

		H1 title = new H1("Get starter");
		title.setClassName("cta-title");

		Span text = new Span("Explore the code and customize the starter to fit your development project!");
		text.setClassName("cta-text");

		textContainer.add(title, text);
		ctaContent.add(VaadinIcon.DOWNLOAD_ALT.create(), textContainer);

		Anchor ctaAnchor = new Anchor();
		ctaAnchor.setHref("https://vaadin.com/start/latest/full-stack-spring");
		ctaAnchor.add(ctaContent);
		ctaAnchor.setSizeFull();

		Icon close = VaadinIcon.CLOSE.create();
		close.setSize("16px");
		close.addClickListener(e -> notification.close());

		ctaContainer.add(ctaAnchor, close);
		ctaContainer.setVerticalComponentAlignment(Alignment.START, close);

		notification.add(ctaContainer);
		notification.setPosition(Notification.Position.TOP_STRETCH);
		notification.open();
	}
}