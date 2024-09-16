package KSCompiler;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class SymbolTableEntry {
    private Boolean isFunction;
    private Boolean isDeclared;
    private Boolean isInitialised;
    private String dataType;
    private String identifier;
    private String scope;
    private List<ParameterDeclaration> inputParameters;

    public SymbolTableEntry(Boolean isFunction, Boolean isDeclared, Boolean isInitialised, String dataType, String identifier, String scope, List<ParameterDeclaration> inputParameters) {
        this.isFunction = isFunction;
        this.isDeclared = isDeclared;
        this.isInitialised = isInitialised;
        this.dataType = dataType;
        this.identifier = identifier;
        this.scope = scope;
        this.inputParameters = inputParameters;
    }

    public SymbolTableEntry setDataType(String dataType) {
        this.dataType = dataType;
        return this;
    }

    public SymbolTableEntry setDeclared(Boolean declared) {
        isDeclared = declared;
        return this;
    }
    public SymbolTableEntry setInitialised(Boolean intialised) {
        isInitialised = intialised;
        return this;
    }

    public SymbolTableEntry setInputParameters(List<ParameterDeclaration> inputParameters) {
        this.inputParameters = inputParameters;
        return this;
    }
}
