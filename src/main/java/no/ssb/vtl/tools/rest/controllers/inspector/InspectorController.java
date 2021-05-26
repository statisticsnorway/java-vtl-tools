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

//import com.netflix.spectator.api.DefaultRegistry;
//import com.netflix.spectator.api.Spectator;
import no.ssb.vtl.connectors.Connector;
import no.ssb.vtl.model.DataPoint;
import no.ssb.vtl.model.Dataset;
//import no.ssb.vtl.model.OrderingSpecification;
//import no.ssb.vtl.model.VtlOrdering;
import no.ssb.vtl.parser.VTLParser;
import no.ssb.vtl.script.VTLScriptEngine;
//import no.ssb.vtl.script.VtlConfiguration;
import no.ssb.vtl.script.error.ContextualRuntimeException;
import no.ssb.vtl.script.error.VTLCompileException;
import no.ssb.vtl.script.error.VTLScriptException;
//import no.ssb.vtl.script.operations.VtlStream;
import no.ssb.vtl.tools.rest.representations.DatasetRepresentation;
import no.ssb.vtl.tools.rest.representations.PlanRepresentation;
import no.ssb.vtl.tools.rest.representations.ProfileRepresentation;
import org.antlr.v4.runtime.ParserRuleContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.ssb.vtl.tools.rest.controllers.inspector.ContextualAssignmentVisitor.ContextualAssignment;

/**
 * Inspection endpoints.
 */
@CrossOrigin(origins = "*")
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
        try {
            engine.eval(script, bindings);
        } catch (ScriptException vce) {
            // Ignore
        }

        List<Object> assignments = bindings.values().stream()
                .filter(ContextualAssignment.class::isInstance)
                .collect(Collectors.toList());

        return ResponseEntity.ok(assignments);
    }

    /**
     * Profile the symbol
     * TODO: Requires Java VTL 0.1.13
     */
    /*
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/profile/{symbol}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    ResponseEntity<Object> profileSymbol(
            @PathVariable String symbol,
            @RequestParam(required = false) OrderingSpecification order
    ) throws ScriptException, InterruptedException {

        // TODO: This creates duplicates.
        DefaultRegistry registry = new DefaultRegistry();
        Spectator.globalRegistry().add(registry);

        VtlConfiguration config = VtlConfiguration.getConfig();
        config.enableProfiling();
        try {
            Optional<Dataset> optionalDataset = getContextualAssignment(symbol);
            if (optionalDataset.isPresent()) {
                Dataset dataset = optionalDataset.get();

                VtlOrdering ordering = order != null
                        ? new VtlOrdering(order, dataset.getDataStructure())
                        : new VtlOrdering(Collections.emptyMap(), dataset.getDataStructure());

                Stream<DataPoint> stream = dataset.getData(ordering).orElseThrow(() ->
                        new UnsupportedOperationException("Ordering not supported")
                );
                try {
                    if (stream instanceof VtlStream) {
                        VtlStream vtlStream = (VtlStream) stream;
                        Spliterator<DataPoint> spliterator = vtlStream.spliterator();

                        // Consume the stream.
                        spliterator.forEachRemaining(dataPoint -> {});

                        return ResponseEntity.ok(new ProfileRepresentation(vtlStream));
                    } else {
                        throw new UnsupportedOperationException("not a vtl stream");
                    }
                } finally {
                    stream.close();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } finally {
            config.disableProfiling();
        }
    }
*/

    /**
     * Display the execution plan for a symbol.
     * TODO: Requires Java VTL 0.1.13
     */
    /*
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/explain/{symbol}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    ResponseEntity<Object> explainSymbol(
            @PathVariable String symbol,
            @RequestParam(required = false) OrderingSpecification order
    ) throws ScriptException {
        Optional<Dataset> optionalDataset = getContextualAssignment(symbol);
        if (optionalDataset.isPresent()) {
            Dataset dataset = optionalDataset.get();

            VtlOrdering ordering = order != null
                    ? new VtlOrdering(order, dataset.getDataStructure())
                    : new VtlOrdering(Collections.emptyMap(), dataset.getDataStructure());

            Stream<DataPoint> stream = dataset.getData(ordering).orElseThrow(() ->
                    new UnsupportedOperationException("Ordering not supported")
            );
            try {
                if (stream instanceof VtlStream) {
                    VtlStream vtlStream = (VtlStream) stream;
                    System.out.println(vtlStream.printPlan());
                    return ResponseEntity.ok(new PlanRepresentation(vtlStream));
                } else {
                    throw new UnsupportedOperationException("not a vtl stream");
                }
            } finally {
                stream.close();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
*/
    /**
     * Inspect the content of a symbol.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/inspect/{symbol}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    ResponseEntity<Object> inspectSymbol(@PathVariable String symbol) throws ScriptException {
        Optional<Dataset> dataset = getContextualAssignment(symbol);
        if (dataset.isPresent()) {
            return ResponseEntity.ok(DatasetRepresentation.create(symbol, dataset.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private Optional<Dataset> getContextualAssignment(String symbol) {
        for (Object value : bindings.values()) {
            if (value instanceof ContextualAssignment) {
                ContextualAssignment contextualAssignment = (ContextualAssignment) value;
                if (symbol.equals(contextualAssignment.getId())) {
                    Dataset dataset = (Dataset) contextualAssignment.getValue();
                    return Optional.of(dataset);
                }
            }
        }
        return Optional.empty();
    }
}
