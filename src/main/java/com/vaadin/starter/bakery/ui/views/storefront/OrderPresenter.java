package com.vaadin.starter.bakery.ui.views.storefront;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.starter.bakery.ui.utils.SpringDataVaadinUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.starter.bakery.app.security.CurrentUser;
import com.vaadin.starter.bakery.backend.data.entity.Order;
import com.vaadin.starter.bakery.backend.service.OrderService;
import com.vaadin.starter.bakery.ui.crud.EntityPresenter;
import com.vaadin.starter.bakery.ui.utils.BakeryConst;
import com.vaadin.starter.bakery.ui.views.storefront.beans.OrderCardHeader;
import org.springframework.data.domain.PageRequest;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderPresenter {

	private OrderCardHeaderGenerator headersGenerator;
	private StorefrontView view;

	private final EntityPresenter<Order, StorefrontView> entityPresenter;
	private final CurrentUser currentUser;
	private final OrderService orderService;
	private String filter;
	private boolean showPrevious;
	private GridLazyDataView<Order> dataview;

	@Autowired
	OrderPresenter(OrderService orderService,
			EntityPresenter<Order, StorefrontView> entityPresenter, CurrentUser currentUser) {
		this.orderService = orderService;
		this.entityPresenter = entityPresenter;
		this.currentUser = currentUser;
		headersGenerator = new OrderCardHeaderGenerator();
		headersGenerator.resetHeaderChain(false);
	}

	void init(StorefrontView view) {
		this.entityPresenter.setView(view);
		this.view = view;
		listOrders();
		view.getOpenedOrderEditor().setCurrentUser(currentUser.getUser());
		view.getOpenedOrderEditor().addCancelListener(e -> cancel());
		view.getOpenedOrderEditor().addReviewListener(e -> review());
		view.getOpenedOrderDetails().addSaveListenter(e -> save());
		view.getOpenedOrderDetails().addCancelListener(e -> cancel());
		view.getOpenedOrderDetails().addBackListener(e -> back());
		view.getOpenedOrderDetails().addEditListener(e -> edit());
		view.getOpenedOrderDetails().addCommentListener(e -> addComment(e.getMessage()));
	}

	OrderCardHeader getHeaderByOrderId(Long id) {
		return headersGenerator.get(id);
	}

	public void filterChanged(String filter, boolean showPrevious) {
		this.filter = filter;
		this.showPrevious = showPrevious;
		headersGenerator.resetHeaderChain(showPrevious);
		listOrders();
	}

	void onNavigation(Long id, boolean edit) {
		entityPresenter.loadEntity(id, e -> open(e, edit));
	}

	void createNewOrder() {
		open(entityPresenter.createNew(), true);
	}

	void cancel() {
		entityPresenter.cancel(() -> close(), () -> view.setOpened(true));
	}

	void closeSilently() {
		entityPresenter.close();
		view.setOpened(false);
	}

	void edit() {
		UI.getCurrent().navigate(BakeryConst.PAGE_STOREFRONT_EDIT + "/" + entityPresenter.getEntity().getId());
	}

	void back() {
		view.setDialogElementsVisibility(true);
	}

	void review() {
		// Using collect instead of findFirst to assure all streams are
		// traversed, and every validation updates its view
		List<HasValue<?, ?>> fields = view.validate().collect(Collectors.toList());
		if (fields.isEmpty()) {
			if (entityPresenter.writeEntity()) {
				view.setDialogElementsVisibility(false);
				view.getOpenedOrderDetails().display(entityPresenter.getEntity(), true);
			}
		} else if (fields.get(0) instanceof Focusable) {
			((Focusable<?>) fields.get(0)).focus();
		}
	}

	void save() {
		entityPresenter.save(e -> {
			if (entityPresenter.isNew()) {
				view.showCreatedNotification();
				listOrders();
			} else {
				view.showUpdatedNotification();
				refreshOrder(e);
			}
			close();
		});

	}
	
	void refreshOrder(Order o) {
		dataview.refreshItem(o);
	}
	
	void listOrders() {
		dataview = view.getGrid().setItems(q -> {
			List<Order> orders = orderService.findAnyMatchingAfterDueDate(
					Optional.ofNullable(filter),
					getFilterDate(showPrevious),
					PageRequest.of(
							q.getPage(),
							q.getPageSize(),
							q.getSortOrders().isEmpty() ? BakeryConst.DEFAULT_SORT :
									SpringDataVaadinUtil.toSpringDataSort(q)
					)
			).getContent();
			headersGenerator.ordersRead(orders);
			return orders.stream();
		});
	}

	private Optional<LocalDate> getFilterDate(boolean showPrevious) {
		if (showPrevious) {
			return Optional.empty();
		}

		return Optional.of(LocalDate.now().minusDays(1));
	}


	void addComment(String comment) {
		if (entityPresenter.executeUpdate(e -> orderService.addComment(currentUser.getUser(), e, comment))) {
			// You can only add comments when in view mode, so reopening in that state.
			open(entityPresenter.getEntity(), false);
		}
	}

	private void open(Order order, boolean edit) {
		view.setDialogElementsVisibility(edit);
		view.setOpened(true);

		if (edit) {
			view.getOpenedOrderEditor().read(order, entityPresenter.isNew());
		} else {
			view.getOpenedOrderDetails().display(order, false);
		}
	}

	private void close() {
		view.getOpenedOrderEditor().close();
		view.setOpened(false);
		view.navigateToMainView();
		entityPresenter.close();
	}
}
