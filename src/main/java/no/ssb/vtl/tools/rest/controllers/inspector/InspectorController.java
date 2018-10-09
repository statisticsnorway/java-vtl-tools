package no.ssb.vtl.tools.rest.controllers.inspector;

import no.ssb.vtl.connectors.Connector;
import no.ssb.vtl.model.Dataset;
import no.ssb.vtl.parser.VTLParser;
import no.ssb.vtl.script.VTLScriptEngine;
import no.ssb.vtl.script.error.ContextualRuntimeException;
import no.ssb.vtl.script.error.VTLScriptException;
import no.ssb.vtl.tools.rest.representations.DatasetRepresentation;
import org.antlr.v4.runtime.ParserRuleContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.io.Reader;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static no.ssb.vtl.tools.rest.controllers.inspector.ContextualAssignmentVisitor.ContextualAssignment;

/**
 * Inspection endpoints.
 */
@CrossOrigin(origins = "http://localhost:8000/")
@RestController
@RequestMapping(
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class InspectorController {

    private final VTLScriptEngine engine;
    private final Bindings bindings;
    private final List<Connector> connectors;

    public InspectorController(VTLScriptEngine engine, Bindings bindings, List<Connector> connectors) {
        this.engine = engine;
        this.bindings = bindings;
        this.connectors = connectors;
    }

    /**
     * Inspect a script
     * <p>
     * The result of the inspection is a list of all root level assignments
     */
    @RequestMapping(
            path = "/inspect",
            method = RequestMethod.POST,
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    ResponseEntity<Object> inspect(Reader script) throws ScriptException {

        bindings.clear();
        // Wrap the engine so we use the ContextualAssignmentVisitor.
        VTLScriptEngine engine = new VTLScriptEngine(connectors.toArray(new Connector[]{})) {
            @Override
            protected Object run(VTLParser.StartContext start, Consumer<VTLScriptException> errorConsumer, ScriptContext context) throws VTLScriptException {
                ContextualAssignmentVisitor assignmentVisitor = new ContextualAssignmentVisitor(context, connectors);
                Object last = null;
                for (VTLParser.StatementContext statementContext : start.statement()) {
                    try {
                        last = assignmentVisitor.visit(statementContext);
                    } catch (ContextualRuntimeException cre) {
                        ParserRuleContext ctx = cre.getContext();
                        if (cre.getCause() != null) {
                            errorConsumer.accept(new VTLScriptException((Exception) cre.getCause(), ctx));
                        } else {
                            errorConsumer.accept(new VTLScriptException(cre.getMessage(), ctx));
                        }
                    }
                }
                return last;
            }
        };
        engine.eval(script, bindings);

        List<Object> assignments = bindings.values().stream()
                .filter(ContextualAssignment.class::isInstance)
                .collect(Collectors.toList());

        return ResponseEntity.ok(assignments);
    }

    /**
     * Inspect a script
     * <p>
     * The result of the inspection is a list of all root level assignments
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/inspect/{symbol}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    ResponseEntity<Object> inspectSymbol(@PathVariable String symbol) throws ScriptException {
        for (Object value : bindings.values()) {
            if (value instanceof ContextualAssignment) {
                ContextualAssignment contextualAssignment = (ContextualAssignment) value;
                if (symbol.equals(contextualAssignment.getId())) {
                    Dataset dataset = (Dataset) contextualAssignment.getValue();
                    return ResponseEntity.ok(DatasetRepresentation.create(symbol, dataset));
                }
            }
        }
        return ResponseEntity.notFound().build();
    }
}
