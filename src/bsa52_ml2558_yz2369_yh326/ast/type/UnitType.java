package bsa52_ml2558_yz2369_yh326.ast.type;

public class UnitType extends NodeType {
    @Override
    public String toString() {
        return "unit";
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UnitType;
    }
}
