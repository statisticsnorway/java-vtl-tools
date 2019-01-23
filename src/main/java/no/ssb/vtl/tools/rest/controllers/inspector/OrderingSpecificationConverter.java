package no.ssb.vtl.tools.rest.controllers.inspector;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import no.ssb.vtl.model.Ordering;
import no.ssb.vtl.model.OrderingSpecification;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OrderingSpecificationConverter implements Converter<String, OrderingSpecification> {
    @Override
    public OrderingSpecification convert(String source) {
        // name1,name2,name3,(ASC|DESC), ...
        String[] params = source.split(",");
        ImmutableMap.Builder<String, Ordering.Direction> specifications = ImmutableMap.builder();

        List<String> columns = new ArrayList<>();
        for (String param : params) {
            if (param.equals("ASC") || param.equals("DESC")) {
                Ordering.Direction direction = Ordering.Direction.valueOf(param);
                for (String column : columns) {
                    specifications.put(column, direction);
                }
                columns.clear();
            } else {
                columns.add(param);
            }
        }

        if (!columns.isEmpty()) {
            throw new IllegalArgumentException("invalid order parameter");
        }

        return new QueryOrderingSpecification(specifications.build(), source);
    }

    private static final class QueryOrderingSpecification implements OrderingSpecification {

        private final ImmutableMap<String, Ordering.Direction> orders;
        private final String source;

        QueryOrderingSpecification(Map<String, Ordering.Direction> orders, String source) {
            this.orders = ImmutableMap.copyOf(orders);
            this.source = source;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("orders", orders)
                    .add("source", source)
                    .toString();
        }

        @Override
        public List<String> columns() {
            return orders.keySet().asList();
        }

        @Override
        public Ordering.Direction getDirection(String column) {
            return orders.get(column);
        }
    }

}
