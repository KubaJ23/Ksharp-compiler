package KSCompiler;

public record ParameterDeclaration(String dataType, String name) {
    @Override
    public String toString() {
        return "%s %s".formatted(dataType, name);
    }
}
