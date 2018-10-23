package no.ssb.vtl.tools.rest.controllers.inspector;

/*-
 * ========================LICENSE_START=================================
 * Java VTL
 * %%
 * Copyright (C) 2016 - 2017 Hadrien Kohl
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import no.ssb.vtl.connectors.Connector;
import no.ssb.vtl.parser.VTLParser;
import no.ssb.vtl.script.visitors.AssignmentVisitor;

import javax.script.ScriptContext;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Assignment visitor that save all the variables with their position.
 */
public class ContextualAssignmentVisitor extends AssignmentVisitor {

    public ContextualAssignmentVisitor(ScriptContext context, List<Connector> connectors) {
        super(context, connectors);
    }

    @Override
    public Object visitAssignment(VTLParser.AssignmentContext ctx) {
        Object value = super.visitAssignment(ctx);
        ContextualAssignment assignmentSymbol = new ContextualAssignment(ctx.variable(), value);
        bindings.put(assignmentSymbol.getId(), assignmentSymbol);
        return value;
    }

    public static class ContextualAssignment {

        @JsonIgnore
        private final VTLParser.VariableContext context;

        @JsonIgnore
        private final Object value;

        public ContextualAssignment(@NotNull VTLParser.VariableContext context, Object value) {
            this.context = checkNotNull(context);
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        @JsonProperty
        public Integer getStartLine() {
            return context.getStart().getLine();
        }

        @JsonProperty
        public Integer getStopLine() {
            return context.getStop().getLine();
        }

        @JsonProperty
        public Integer getStartColumn() {
            return context.getStart().getCharPositionInLine();
        }

        @JsonProperty
        public Integer getStopColumn() {
            return getStartColumn() + getName().length();
        }

        @JsonProperty
        public String getName() {
            return context.getText();
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("position", getId())
                    .add("value", value)
                    .toString();
        }

        @JsonProperty
        public String getId() {
            return String.format("%s:%d:%d:%d:%d",
                    getName(),
                    getStartLine(),
                    getStopLine(),
                    getStartColumn(),
                    getStopColumn()
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ContextualAssignment)) return false;
            ContextualAssignment that = (ContextualAssignment) o;
            return Objects.equal(getId(), that.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getId());
        }
    }
}
