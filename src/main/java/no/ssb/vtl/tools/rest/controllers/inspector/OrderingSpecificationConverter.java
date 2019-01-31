package no.ssb.vtl.tools.rest.controllers.inspector;

/*
 * -
 *  ========================LICENSE_START=================================
 *  Java VTL
 *  %%
 *  Copyright (C) 2019 Hadrien Kohl
 *  %%
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  =========================LICENSE_END==================================
 *
 */

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
