package KSCompiler;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class SymbolTable {
    private List<SymbolTableEntry> symbolTable = new ArrayList<>();

    public SymbolTableEntry add(SymbolTableEntry newEntry) {
        if (getEntry(newEntry.getIdentifier(), newEntry.getScope()) != null)
            return null;

        symbolTable.add(newEntry);
        return newEntry;
    }

    public SymbolTableEntry getEntry(String identifier, String scope) {
        for (SymbolTableEntry entry : symbolTable) {
            if (entry.getIdentifier().equals(identifier) && scope.startsWith(entry.getScope())) {
                return entry;
            }
        }
        return null;
    }

    public void print() {
        System.out.printf("%-20s%-20s%-20s%-20s\n".formatted("Identifier", "Scope", "Datatype", "Parameters"));
        symbolTable.forEach((entry) -> {
            System.out.printf("%-20s%-20s%-20s%-20s\n".formatted(entry.getIdentifier(), entry.getScope().equals("") ? "Global" : entry.getScope(), entry.getDataType(), entry.getInputParameters()));
        });
    }
}
