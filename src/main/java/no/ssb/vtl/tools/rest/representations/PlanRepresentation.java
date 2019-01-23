package no.ssb.vtl.tools.rest.representations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import no.ssb.vtl.script.operations.VtlStream;

import java.util.List;

public class PlanRepresentation {

    @JsonIgnore
    protected final VtlStream stream;

    @JsonProperty
    private Integer id;

    @JsonProperty
    private List<PlanRepresentation> parents;
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
    private String filter;

    public PlanRepresentation(VtlStream stream) {
        this.stream = stream;
        this.id = stream.hashCode();

        this.operationType = stream.getOperation().getClass();
        this.operationId = stream.getOperation().hashCode();

        this.streamType = stream.getClass();
        this.streamId = stream.hashCode();

        this.order = stream.getOrdering().toString();
        this.filter = stream.getFiltering().toString();

        this.parents = stream.getParents().stream()
                .filter(parentStream -> parentStream instanceof VtlStream)
                .map(parentStream -> new PlanRepresentation((VtlStream) parentStream))
                .collect(ImmutableList.toImmutableList());

    }

    public List<PlanRepresentation> getParents() {
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
}
