/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uefs.ecomp.analisadorSemantico.model;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author sandr
 */
public class Simbolo {
    private String id;
    private String extends_id;
    private int line;
    private int array_lenght;
    private String scope;
    private String category;
    private String type;
    private TabelaDeSimbolos struct_variables;
    private boolean isArray = false; 
    private boolean read;
    private String value;
    final private ArrayList<Param> parameters;
    
    protected class Param {
        private String type;
        private String id;
        
        public Param(String type, String id){
            this.type = type;
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
            
    public Simbolo(){
        this.parameters = new ArrayList();
        this.struct_variables = new TabelaDeSimbolos();
    }
    
    public void addParam(String type, String id){
        Param p = new Param(type, id);
        
        this.parameters.add(p);
    }

    public boolean isIsArray() {
        return isArray;
    }

    public void setIsArray(boolean isArray) {
        this.isArray = isArray;
    }

    public String getExtends_id() {
        return extends_id;
    }

    public void setExtends_id(String extends_id) {
        this.extends_id = extends_id;
    }

    public TabelaDeSimbolos getStruct_variables() {
        return struct_variables;
    }

    public void setStruct_variables(TabelaDeSimbolos struct_variables) {
        this.struct_variables = struct_variables;
    }

    public int getArray_lenght() {
        return array_lenght;
    }

    public void setArray_lenght(int array_lenght) {
        this.array_lenght = array_lenght;
    }
    
    
    public String getId() {
        return id;
    }

    public int getLine() {
        return line;
    }

    public String getScope() {
        return scope;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public boolean isRead() {
        return read;
    }

    public String getValue() {
        return value;
    }

    public ArrayList<Param> getParameters() {
        return parameters;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Simbolo{" + "id=" + id + ", line=" + line + ", scope=" + scope + ", category=" + category + ", type=" + type + ", read=" + read + ", value=" + value + ", parameters=" + parameters + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.scope);
        hash = 79 * hash + Objects.hashCode(this.parameters);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Simbolo other = (Simbolo) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.scope, other.scope)) {
            return false;
        }
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        return true;
    }
}
