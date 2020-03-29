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
    private int array_dimensions = 0;
    private int array_lenght = 1;
    private String scope;
    private String category;
    private String type;
    private String struct_id;
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
    }
    
    public void addParam(String type, String id){
        Param p = new Param(type, id);
        
        this.parameters.add(p);
    }

    public String getExtends_id() {
        return extends_id;
    }

    public void setExtends_id(String extends_id) {
        this.extends_id = extends_id;
    }

    public int getArray_dimensions() {
        return array_dimensions;
    }

    public void setArray_dimensions() {
        this.array_dimensions++;
    }

    public String getStruct_id() {
        return struct_id;
    }

    public void setStruct_id(String struct_id) {
        this.struct_id = struct_id;
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

    public int getArray_lenght() {
        return array_lenght;
    }

    public void setArray_lenght(int array_lenght) {
        this.array_lenght *= array_lenght;
    }

    @Override
    public String toString() {
        return "Simbolo{" + "id=" + id + ", extends_id=" + extends_id + ", line=" + line + ", array_dimensions=" + array_dimensions + ", array_lenght=" + array_lenght + ", scope=" + scope + ", category=" + category + ", type=" + type + ", struct_id=" + struct_id + ", read=" + read + ", value=" + value + ", parameters=" + parameters + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.id);
        hash = 79 * hash + Objects.hashCode(this.scope);
        hash = 79 * hash + Objects.hashCode(this.parameters);
        hash = 79 * hash + Objects.hashCode(this.type);
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
        if (Objects.equals(this.id, other.id)
                && Objects.equals(this.scope, other.scope)
                && Objects.equals(this.parameters, other.parameters)) {
            if(this.struct_id != null && other.struct_id != null){
                if(!Objects.equals(this.struct_id, this.struct_id)){
                    return true;
                }
            }
            
            return true;
        }
        return false;
    }
}
