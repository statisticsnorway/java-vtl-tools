package no.ssb.vtl.tools.rest.controllers.inspector;

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
