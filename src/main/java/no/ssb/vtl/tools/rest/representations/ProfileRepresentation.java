package no.ssb.vtl.tools.rest.representations;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import no.ssb.vtl.script.operations.VtlStream;

import java.util.List;

public class ProfileRepresentation {

    @JsonIgnore
    protected final VtlStream stream;

    @JsonProperty
    private Integer id;

    @JsonProperty
    private List<ProfileRepresentation> parents;
    @JsonProperty
    private Class operationType;
    @JsonProperty
    private Integer operationId;
    @JsonProperty
    private Class streamType;
    @JsonProperty
    private Integer streamId;
    @JsonProperty
    private String order;
    @JsonProperty
    private String actualOrdering;
    @JsonProperty
    private String filter;
    public ProfileRepresentation(VtlStream stream) {
        this.stream = stream;
        this.id = stream.hashCode();

        this.operationType = stream.getOperation().getClass();
        this.operationId = stream.getOperation().hashCode();

        this.streamType = stream.getClass();
        this.streamId = stream.hashCode();

        this.order = stream.getOrdering().toString();
        this.actualOrdering = stream.getActualOrdering().toString();
        this.filter = stream.getFiltering().toString();

        this.parents = stream.getParents().stream()
                .filter(parentStream -> parentStream instanceof VtlStream)
                .map(parentStream -> new ProfileRepresentation((VtlStream) parentStream))
                .collect(ImmutableList.toImmutableList());

    }

    public String getActualOrdering() {
        return actualOrdering;
    }

    public List<ProfileRepresentation> getParents() {
        return parents;
    }

    public Class getOperationType() {
        return operationType;
    }

    public Integer getOperationId() {
        return operationId;
    }

    public Class getStreamType() {
        return streamType;
    }

    public Integer getStreamId() {
        return streamId;
    }

    public String getOrder() {
        return order;
    }

    public String getFilter() {
        return filter;
    }

    @JsonProperty
    public Long getTimeCount() {
        return stream.getStatistics().getTime().count();
    }

    @JsonProperty
    public Long getTimeTotal() {
        return stream.getStatistics().getTime().totalTime();
    }

    @JsonProperty
    public Long getFilterTimeCount() {
        return stream.getStatistics().getFilterTime().count();
    }

    @JsonProperty
    public Long getFilterTimeTotal() {
        return stream.getStatistics().getFilterTime().totalTime();
    }

    @JsonProperty
    public Long getSortTimeCount() {
        return stream.getStatistics().getSortTime().count();
    }

    @JsonProperty
    public Long getSortTimeTotal() {
        return stream.getStatistics().getSortTime().totalTime();
    }

    @JsonProperty
    public Long getRows() {
        return stream.getStatistics().getRows().count();
    }

    @JsonProperty
    public Long getCells() {
        return stream.getStatistics().getCells().count();
    }


}
