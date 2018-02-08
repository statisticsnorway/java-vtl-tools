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

import com.fasterxml.jackson.annotation.JsonProperty;
import no.ssb.vtl.model.Component;

/**
 * Json representation of a {@link no.ssb.vtl.model.Component}.
 */
public class ComponentRepresentation {

    @JsonProperty
    private String name;

    @JsonProperty
    private Component.Role role;

    private Class<?> type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public Class<?> getType() {
        return type;
    }

    @JsonProperty
    public void setType(Class<?> type) {
        this.type = type;
    }

//    @JsonProperty
//    public Class<?> getClassType() {
//        return type;
//    }

    public Component.Role getRole() {
        return role;
    }

    public void setRole(Component.Role role) {
        this.role = role;
    }

}
