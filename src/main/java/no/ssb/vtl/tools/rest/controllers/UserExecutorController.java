package no.ssb.vtl.tools.rest.controllers;

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

import com.google.common.collect.Maps;
import no.ssb.vtl.model.DataPoint;
import no.ssb.vtl.model.DataStructure;
import no.ssb.vtl.model.Dataset;
import no.ssb.vtl.model.VTLObject;
import no.ssb.vtl.script.VTLScriptEngine;
import no.ssb.vtl.script.operations.VtlStream;
import no.ssb.vtl.tools.rest.representations.StructureRepresentation;
import no.ssb.vtl.tools.rest.representations.ThrowableRepresentation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.script.Bindings;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

@CrossOrigin(origins = "http://localhost:8000/")
@RequestMapping(
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.ALL_VALUE
)
@RestController
public class UserExecutorController {

    private final VTLScriptEngine engine;
    private final Bindings bindings;

    public UserExecutorController(VTLScriptEngine engine, Bindings bindings) {
        this.engine = checkNotNull(engine);
        this.bindings = checkNotNull(bindings);
    }

    @ExceptionHandler
    @ResponseStatus()
    public Object handleError(Throwable t) {
        return new ThrowableRepresentation(t);
    }

    @RequestMapping(
            path = "/execute",
            method = RequestMethod.POST
    )
    public Collection<String> execute(Reader script) throws IOException, ScriptException {
        engine.eval(script, bindings);
        return bindings.keySet();
    }

    @RequestMapping(
            path = "/dataset/{id}/structure",
            method = RequestMethod.GET
    )
    public ResponseEntity<StructureRepresentation> getStructure(@PathVariable String id) {
        Dataset dataset = (Dataset) bindings.get(id);

        if (dataset == null)
            return ResponseEntity.notFound().build();

        StructureRepresentation structureRepresentation = new StructureRepresentation();
        structureRepresentation.setDataStructure(dataset.getDataStructure());
        return ResponseEntity.ok(structureRepresentation);
    }

    @RequestMapping(
            path = "/dataset/{id}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (bindings.containsKey(id)) {
            bindings.remove(id);
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(
            path = "/dataset/{id}/data",
            method = RequestMethod.GET
    )
    public Iterable<Map<String, Object>> getData(@PathVariable String id) {
        final Dataset dataset = (Dataset) bindings.get(id);
        DataStructure structure = dataset.getDataStructure();
        Map<String, Object> map = Maps.newLinkedHashMap();
        Stream<DataPoint> stream = dataset.getData();
        return () -> {
            return stream.map(dataPoints -> {
                map.clear();
                Iterator<VTLObject> iterator = dataPoints.iterator();
                for (String name : structure.keySet()) {
                    map.put(name, iterator.next().get());
                }
                return map;
            }).iterator();
        };
    }
}
