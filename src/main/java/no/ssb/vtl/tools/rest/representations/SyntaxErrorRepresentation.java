package no.ssb.vtl.tools.rest.representations;

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
import no.ssb.vtl.tools.rest.controllers.ValidatorController;

/**
 * Json representation of syntax errors.
 *
 * @see ValidatorController
 */
public class SyntaxErrorRepresentation {

    private final Integer startLine;
    private final Integer stopLine;
    private final Integer startColumn;
    private final Integer stopColumn;
    private final String message;
    private final ThrowableRepresentation exception;

    public SyntaxErrorRepresentation(Integer startLine, Integer stopLine, Integer startColumn, Integer stopColumn, String message, Exception exception) {
        this.startLine = startLine;
        this.stopLine = stopLine;
        this.startColumn = startColumn;
        this.stopColumn = stopColumn;
        this.message = message;

        ThrowableRepresentation throwableRepresentation = null;
        if (exception != null)
            throwableRepresentation = new ThrowableRepresentation(exception);
        this.exception = throwableRepresentation;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public Integer getStopLine() {
        return stopLine;
    }

    public Integer getStartColumn() {
        return startColumn;
    }

    public Integer getStopColumn() {
        return stopColumn;
    }

    public String getMessage() {
        return message;
    }

    @JsonIgnore
    public ThrowableRepresentation getException() {
        return exception;
    }
}
