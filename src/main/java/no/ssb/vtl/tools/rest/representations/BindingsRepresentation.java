package no.ssb.vtl.tools.rest.representations;

import com.google.common.base.MoreObjects;
import no.ssb.vtl.model.Component;

import java.util.List;

public class BindingsRepresentation {

    private String name;
    private Class type;
    private Component.Role role;
    private List<BindingsRepresentation> children;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("type", type)
                .add("role", role)
                .toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public Component.Role getRole() {
        return role;
    }

    public void setRole(Component.Role role) {
        this.role = role;
    }

    public List<BindingsRepresentation> getChildren() {
        return children;
    }

    public void setChildren(List<BindingsRepresentation> children) {
        this.children = children;
    }
}
