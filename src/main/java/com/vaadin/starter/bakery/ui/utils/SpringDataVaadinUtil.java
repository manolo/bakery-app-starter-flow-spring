package com.vaadin.starter.bakery.ui.utils;

import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;

public class SpringDataVaadinUtil {

    public static Sort toSpringDataSort(Query<?, Void> q) {
        Sort springDataSort = Sort.by(q.getSortOrders().stream()
                .map(so -> so.getDirection() == SortDirection.ASCENDING ? Order.asc(so.getSorted())
                        : Order.desc(so.getSorted()))
                .collect(Collectors.toList()));
        return springDataSort;
    }

}