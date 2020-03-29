package br.uefs.ecomp.analisadorSemantico.model;

import br.uefs.ecomp.AnalisadorLexico.model.Token;
import java.util.Iterator;

/**
 *
 * @author sandr
 */
public class TokensReader {
    Iterator<Token> arq;
    ErrorList erroList;
    AnaliseSemantica semantics;
    TabelaDeSimbolos tabela_variables;
    TabelaDeSimbolos tabela_constants;
    TabelaDeSimbolos tabela_local_varibles;
    TabelaDeSimbolos tabela_structs;
    TabelaDeSimbolos tabela_functions;
    ScanLexema scan;
    Token token;
    
    private enum _scope {
        GLOBAL, LOCAL, STRUCT;
    }
    
    private enum types {
        INT, REAL, BOOLEAN, STRING;
    }
    
    private enum category {
        FUNCTION, STRUCT, VARIABLE, CONSTANT
    }
    
    public TokensReader(Iterator<Token> arq){
        this.arq = arq;
        scan = new ScanLexema();
        this.erroList = new ErrorList();
        this.token = this.arq.next();
        this.semantics = new AnaliseSemantica();
        this.tabela_variables = new TabelaDeSimbolos();
        this.tabela_local_varibles = new TabelaDeSimbolos();
        this.tabela_constants = new TabelaDeSimbolos();
        this.tabela_structs = new TabelaDeSimbolos();
        this.tabela_functions = new TabelaDeSimbolos();
    }
    
    private void setErro(int line, String expected, String found){
        Error erro = new Error(line, expected, found); 
        this.erroList.addErro(erro);
    }
    
    private void next(){
        if(this.arq.hasNext()){
            this.token = this.arq.next();
        } else {
            for (Object lista : this.tabela_variables.pegaTodos()) {
                System.out.print(lista.toString() + "\n");
            }
            
            for (Object lista : this.tabela_local_varibles.pegaTodos()) {
                System.out.print(lista.toString() + "\n");
            }
            
            for (Object lista : this.tabela_constants.pegaTodos()) {
                System.out.print(lista.toString() + "\n");
            }
            stateZero();
        }
    }
    
    public ErrorList stateZero() {
        if(this.arq.hasNext()){
            global_values(_scope.GLOBAL.toString());
            
            while(this.arq.hasNext()){
                if(this.token.getLexema().equals("function")){
                    next();
                    if(scan.isType(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
                        next();
                    } 

                    if(this.token.getCodigo().equals("IDE")){
                        next();
                    }

                    function_procedure();
                    _return();

                    if(this.token.getLexema().equals("}")){
                        next();
                     }
                }

                if(this.token.getLexema().equals("procedure")){
                    next();
                    if(this.token.getCodigo().equals("IDE") || this.token.getLexema().equals("start")){
                        next();
                    }

                    function_procedure();

                    if(this.token.getLexema().equals("}")){
                       next();
                    }
                }
            } 
        }
        
        return this.erroList;
    }
    
    private void global_values(String scope){
        Simbolo symbol = new Simbolo();
        symbol.setScope(scope);
        
        if(this.token.getLexema().equals("var")){
            next();
            if(this.token.getLexema().equals("{")){
                next();
            } 
            
            var_values_declaration(scope, symbol);
            
            if(this.token.getLexema().equals("}")){
                next();
            }
            
            if(this.token.getLexema().equals("const")){
                next();
            } 
            
            if(this.token.getLexema().equals("{")){
                next();
            } 

            Simbolo const_s = new Simbolo();
            const_s.setScope(scope);
            const_values_declaration(scope, const_s);
            
            if(this.token.getLexema().equals("}")){
                next();
            } 
        }
        
        if(this.token.getLexema().equals("const")){
            next();
            if(this.token.getLexema().equals("{")){
                next();
            }
            
            const_values_declaration(scope, symbol);
            
            if(this.token.getLexema().equals("}")){
                next();
            }
            
            if(this.token.getLexema().equals("var")){
                next();
            } 
            
            if(this.token.getLexema().equals("{")){
                next();
            } 

            Simbolo var_s = new Simbolo();
            var_s.setScope(scope);
            var_values_declaration(scope, var_s);
            
            if(this.token.getLexema().equals("}")){
                next();
            } 
        }
    }
    
    private void var_values_declaration(String scope, Simbolo symbol){
        symbol.setCategory(category.VARIABLE.toString());

        if(scan.isType(this.token.getLexema())){
            symbol.setType(this.token.getLexema());
            next();
            var_values_atribuition(symbol);
            
            Simbolo s_more_var = new Simbolo();
            s_more_var.setScope(scope);
            s_more_var.setCategory(symbol.getCategory());
            s_more_var.setType(symbol.getType());
            if(symbol.getStruct_id() != null){
                s_more_var.setStruct_id(symbol.getStruct_id());
            }
            
            var_more_atribuition(s_more_var);
            if(this.token.getLexema().equals(";")){
                next();
            }
            
            Simbolo s_var = new Simbolo();
            s_var.setScope(scope);
            var_values_declaration(scope, s_var);
        }
        
        if(this.token.getLexema().equals("typedef")){
            symbol.setCategory(category.STRUCT.toString());
            
            next();
            if(this.token.getLexema().equals("struct")){
                next();
            } 
            
            IDE_struct(scope, symbol);
            
            Simbolo s_typedef = new Simbolo();
            s_typedef.setScope(scope);
            s_typedef.setCategory(symbol.getCategory());
            var_values_declaration(scope, s_typedef);
        }
        
        if(this.token.getLexema().equals("struct")){
            symbol.setCategory(category.STRUCT.toString());
            next();
            
            IDE_struct(scope, symbol);
            
            Simbolo s_struct = new Simbolo();
            s_struct.setScope(scope);
            s_struct.setCategory(symbol.getCategory());
            var_values_declaration(scope, s_struct);
        }
    }
    
    private void IDE_struct(String scope, Simbolo symbol){
        
        if(this.token.getCodigo().equals("IDE")){
            symbol.setId(this.token.getLexema());
            symbol.setLine(this.token.getLine());
            next();
        }
        
        IDE_struct_2(scope, symbol);
    }
    
    private void IDE_struct_2(String scope, Simbolo symbol_struct){
        
        if(this.token.getLexema().equals("extends")){
            next();
            if(this.token.getCodigo().equals("IDE")){
                symbol_struct.setExtends_id(this.token.getLexema());
                next();
            }
        }
        
        if(this.token.getLexema().equals("{")){
            next();
        }
        
        if(this.token.getLexema().equals("var")){
            next();
        } 
        
        if(this.token.getLexema().equals("{")){
            next();
        } 
        
        Simbolo struct_var_symbol = new Simbolo();
        struct_var_symbol.setScope(scope);
        struct_var_symbol.setStruct_id(symbol_struct.getId());
        var_values_declaration(scope, struct_var_symbol);
        
        if(this.token.getLexema().equals("}")){
            next();
        } 

        if(this.token.getLexema().equals("}")){
            next();
        }
        
        if(!this.tabela_structs.contem(symbol_struct)){
            this.tabela_structs.addElement(symbol_struct);
        } else {
            System.out.print("Struct já declarada!" + "\n" + symbol_struct.toString() + "\n");
        }
        
        Simbolo new_symbol = new Simbolo();
        var_values_declaration(scope, new_symbol);
    }
    
    private void var_values_atribuition(Simbolo symbol){
        
        if(this.token.getCodigo().equals("IDE")){
            symbol.setId(this.token.getLexema());
            symbol.setLine(this.token.getLine());
            next();
        } 
        array_verification(symbol);
    }
    
    private void array_verification(Simbolo symbol){
        
        if(this.token.getLexema().equals("[")){
            symbol.setArray_dimensions();
            next();
            if(this.token.getCodigo().equals("NRO")){
                if(this.semantics.checkTypes("int", this.token)){
                    symbol.setArray_lenght(Integer.parseInt(this.token.getLexema()));
                } else {
                    System.out.print("Valor invalido declaração de array");
                }
                next();
            } 
            
            if(this.token.getLexema().equals("]")){
                next();
            }
            
            array_verification(symbol);
        }
 
        if(symbol.getCategory().equals("VARIABLE")){
            if(symbol.getScope().equals("GLOBAL")){
                if(!this.tabela_variables.contem(symbol)){
                    this.tabela_variables.addElement(symbol);
                } else {
                    System.out.print("Variavel já declarada!");
                }
            } else {
                if(!this.tabela_local_varibles.contem(symbol)){
                    this.tabela_local_varibles.addElement(symbol);
                } else {
                    System.out.print("Variavel já declarada!");
                }
            }
        }
    }
    
    private void var_more_atribuition(Simbolo symbol){
        
        if(this.token.getLexema().equals(",")){
            next();
            var_values_atribuition(symbol);
            
            Simbolo s_more_var = new Simbolo();
            s_more_var.setScope(symbol.getScope());
            s_more_var.setCategory(symbol.getCategory());
            s_more_var.setType(symbol.getType());
            if(symbol.getStruct_id() != null){
                s_more_var.setStruct_id(symbol.getStruct_id());
            }
            
            var_more_atribuition(s_more_var);
        }
    }
    
    private void const_values_declaration(String scope, Simbolo symbol){
        symbol.setCategory(category.CONSTANT.toString());
        symbol.setScope(scope);
        
        if(scan.isType(this.token.getLexema())){
            symbol.setType(this.token.getLexema());
            next();
            const_values_atribuition(symbol);
            
            Simbolo more_const = new Simbolo();
            more_const.setCategory(symbol.getCategory());
            more_const.setScope(scope);
            more_const.setType(symbol.getType());
            
            const_more_atribuition(more_const);
            
            if(this.token.getLexema().equals(";")){
                next();
            }
            
            Simbolo new_const = new Simbolo();
            const_values_declaration(scope, new_const);
        } 
    }
    
    private void const_values_atribuition(Simbolo symbol){
        
        if(this.token.getCodigo().equals("IDE")){
            symbol.setId(this.token.getLexema());
            symbol.setLine(this.token.getLine());
            next();
        }
        
        if(this.token.getLexema().equals("=")){
            next();
        }
        
        value_const(symbol);
    }
    
    private void value_const(Simbolo symbol){
        
        if(this.token.getCodigo().equals("NRO") || this.token.getCodigo().equals("CDC")
                || scan.isBooleans(this.token.getLexema())){
            if(this.semantics.checkTypes(symbol.getType(), this.token)){
                symbol.setValue(this.token.getLexema());
            } else {
                System.out.print("Valores incompativeis" + this.token.getLexema() + symbol.getType() + "\n");
            }
            next();
        } else 
            if(this.token.getLexema().equals("-")){
                next();
                if(this.token.getCodigo().equals("NRO")){
                    if(!this.semantics.checkTypes(symbol.getType(), this.token)){
                        symbol.setValue(this.token.getLexema());
                    } else {
                        System.out.print("Valores incompativeis" + this.token.getLexema() + symbol.getType() + "\n");
                    }
                    next();
                }
        }
        
        if(!this.tabela_constants.contem(symbol)){
            this.tabela_constants.addElement(symbol);
        } else {
            System.out.print("Constante já declarada! " + symbol.toString() + "\n");
        }
    }
    
    private void const_more_atribuition(Simbolo symbol){
        
        if(this.token.getLexema().equals(",")){
            next();
            const_values_atribuition(symbol);
            
            Simbolo more_const = new Simbolo();
            more_const.setCategory(symbol.getCategory());
            more_const.setType(symbol.getType());
            more_const.setScope(symbol.getScope());
            
            const_more_atribuition(more_const);
        }
    }
    
    private void function_procedure (){
        
        if(this.token.getLexema().equals("(")){
            next();
        }
        
        params_list();
        
        if(this.token.getLexema().equals(")")){
            next();
        }
        
        if(this.token.getLexema().equals("{")){
            next();
        }
        
        if(this.token.getLexema().equals("var")) {
            next();
        }
        
        var_fuctions_procedures(_scope.LOCAL.toString());
        
        if(scan.isCommands(this.token.getLexema()) || scan.isModifiers(this.token.getLexema())
                || this.token.getCodigo().equals("IDE")){
            commands();
        }
    }
    
    private void params_list () {
        param();
    }
    
    private void param(){
        
        if(scan.isType(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
            next();
            if(this.token.getCodigo().equals("IDE")){
                next();
            }

            more_params();
        }
    }
    
    private void more_params(){
        
        if(this.token.getLexema().equals(",")){
            next();
            param();
        } 
    }
            
    
    private void var_fuctions_procedures (String scope){
        
        if(this.token.getLexema().equals("{")){
            
            next();
        } 
        
        Simbolo symbol = new Simbolo();
        symbol.setScope(scope);
        var_values_declaration(scope, symbol);
        
        if(this.token.getLexema().equals("}")){
            next();
        } 
    }
    
    private void commands () {
        
        if(this.token.getLexema().equals("if")){
            next();
            ifStatement();
        }
        
        if(this.token.getLexema().equals("while")){
            next();
            whileStatement();
        }
        
        if(this.token.getLexema().equals("read")){
            next();
            readStatement();
        }
        
        if(this.token.getLexema().equals("print")){
            next();
            printStatement();
        }
        
        if(token.getLexema().equals("!") || scan.isUnaryOp(this.token.getLexema())){
            unary_operation();
            if(token.getLexema().equals(";")){
                next();
            }
        }
        
        if(this.token.getCodigo().equals("IDE")){
            next();
            if(this.token.getLexema().equals("(")){
                call_procedure_function();
                if(this.token.getLexema().equals(";")){
                    next();
                }
            } else {
                paths();
                if(scan.isUnaryOp(this.token.getLexema())){
                    next();
                    if(this.token.getLexema().equals(";")){
                        next();
                    } 
                } else {
                    if(this.token.getLexema().equals("=")){
                        next();
                        assing();
                    } 
                } 
            }
        }
    }
    
    private void assing(){
        
        if(scan.isModifiers(this.token.getLexema()) || this.token.getLexema().equals("-") 
                || this.token.getCodigo().equals("NRO") || scan.isBooleans(this.token.getLexema())
                || scan.isUnaryOp(this.token.getLexema()) || this.token.getLexema().equals("!")){
            expression();
        } else if(this.token.getCodigo().equals("IDE")){
                expression();
                if(this.token.getLexema().equals("(")){
                    call_procedure_function();
                }
        } else if(this.token.getCodigo().equals("CDC")){
                next();
        }
        
        if(this.token.getLexema().equals(";")){
            next();
        } 
        
        commands();
    }
    
    private void unary_operation(){
        
        if(scan.isUnaryOp(this.token.getLexema())){
            next();
            if(scan.isModifiers(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
                variable();
            }
        } else 
            if(this.token.getLexema().equals("!")){
                next();
                if(scan.isModifiers(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
                    variable();
                }
        } else 
            if(scan.isModifiers(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
                variable();
                if(scan.isUnaryOp(this.token.getLexema())){
                    next();
                }
        } else {
            
            final_value();
        }
    }
    
    private void variable(){
        if(scan.isModifiers(this.token.getLexema())){
            call_variable();
        }

        if(this.token.getCodigo().equals("IDE")){
            next();
            paths();
        }
    }
    
    private void final_value(){
        if(this.token.getLexema().equals("-")){
            next();
            if(this.token.getCodigo().equals("NRO")){
                next();
            } 
        } else 
            if(this.token.getCodigo().equals("NRO")){
                next();
        } else
            if(scan.isBooleans(token.getLexema())){
                next();
        } 
    }
    
    private void commands_exp(){
        
        logical_exp();
    }
    
    private void expression() {
        
        aritmetic_exp();
        if(scan.isRelationalOpStronger(this.token.getLexema()) || scan.isRelationalOpWeaker(this.token.getLexema())){
            opt_relational_exp();
            if(scan.isLogicalOp(this.token.getLexema())){
                logical_exp();
            }
        }
    }
    
    private void logical_exp(){
        
        relational_exp();
        opt_logical_exp();
    }
    
    private void relational_exp(){
        
        if(this.token.getLexema().equals("(")){
            next();
            logical_exp();
            if(this.token.getLexema().equals(")")){
                next();
            } 
        } else {
            aritmetic_exp();
            opt_relational_exp();
        }
    }
    
    private void aritmetic_exp(){
        
        if(this.token.getLexema().equals("(")){
            next();
            relational_exp();
            if(this.token.getLexema().equals(")")){
                next();
            } 
        } else {
            operation();
            op_sum();
        }
    }
    
    private void operation(){
        
        op_unary();
        op_times_div();
    }
    
    private void op_unary(){
        
        if(this.token.getLexema().equals("(")){
            next();
            aritmetic_exp();
            if(this.token.getLexema().equals(")")){
                next();
            } 
        } else {
            unary_operation();
        }
        
    }
    
    private void op_times_div(){
        
        if(scan.isTimesDiv(this.token.getLexema())){
            next();
            op_unary();
            op_times_div();
        }
    }
    
    private void op_sum(){
        
        if(scan.isPlusMinus(this.token.getLexema())){
            next();
            operation();
            op_sum();
        }
    }
    
    private void inequal_exp(){
        
        if(scan.isRelationalOpStronger(this.token.getLexema())){
            next();
            aritmetic_exp();
            equal_exp();
        }
    }
    
    private void equal_exp(){
        
        if(scan.isRelationalOpWeaker(this.token.getLexema())){
            next();
            aritmetic_exp();
            inequal_exp();
            equal_exp();
        }
    }
    
    private void opt_relational_exp(){
        
        if(scan.isRelationalOpStronger(this.token.getLexema())){
            next();
            aritmetic_exp();
            inequal_exp();
            equal_exp();
        } else if(scan.isRelationalOpWeaker(this.token.getLexema())){
            next();
            aritmetic_exp();
            inequal_exp();
        }
    }
    
    private void opt_logical_exp(){
        
        if(scan.isLogicalOp(this.token.getLexema())){
            next();
            logical_exp();
        }
    }
    
    private void call_procedure_function(){
        
        if(this.token.getLexema().equals("(")){
            next();
        }
        
        realParams();
        
        if(this.token.getLexema().equals(")")){
            next();
        }
        
        commands();
    }
    
    private void realParams(){
        realParam();
    }
    
    private void realParam(){
        
        if(this.token.getCodigo().equals("NRO") || this.token.getCodigo().equals("CDC") 
                || scan.isBooleans(this.token.getLexema()) || scan.isModifiers(this.token.getLexema())
                || this.token.getLexema().equals("-") || this.token.getCodigo().equals("IDE")){
            if(scan.isModifiers(this.token.getLexema())){
                call_variable();
                if(this.token.getLexema().equals("IDE")){
                    next();
                }
            } else 
                if(this.token.getLexema().equals("-")){
                    next();
                    if(this.token.getCodigo().equals("NRO")){
                        next();
                    }
                }
            else {
                next();
            }
            
            more_real_params();
        } 
    }
    
    private void more_real_params(){
        
        if(this.token.getLexema().equals(",")){
            next();
            if(this.token.getCodigo().equals("NRO") || this.token.getCodigo().equals("CDC") 
                || scan.isBooleans(this.token.getLexema()) || scan.isModifiers(this.token.getLexema())
                || this.token.getLexema().equals("-") || this.token.getCodigo().equals("IDE")){
                realParam();
            } 
            more_real_params();
        }
    }
    
    private void printStatement(){
        
        if(this.token.getLexema().equals("(")){
            next();
        } 
        
        print_params();
        
        if(this.token.getLexema().equals(")")){
            next();
        }
        
        if(this.token.getLexema().equals(";")){
            next();
        }
    }
    
    private void print_params(){
        
        print_param();
        more_print_param();
    }
    
    private void print_param(){
        
        if(this.token.getCodigo().equals("CDC")){
            next();
        } else 
            if(scan.isModifiers(this.token.getLexema())){
                call_variable();
        } else 
            if(this.token.getCodigo().equals("IDE")){
                next();
                paths();
        }
    }
    
    private void more_print_param(){
        
        if(this.token.getLexema().equals(",")){
            next();
            print_params();
        }
    }
    
    private void readStatement(){
        
        if(this.token.getLexema().equals("(")){
            next();
        }
        
        read_params();
        
        if(this.token.getLexema().equals(")")){
            next();
        }
        
        if(this.token.getLexema().equals(";")){
            next();
        } 
    }
    
    private void read_params(){
        
        if(scan.isModifiers(this.token.getLexema())){
            call_variable();
            more_read_params();
        } else 
            if(this.token.getCodigo().equals("IDE")){
                next();
                paths();
                more_read_params();
        } 
    }
    
    private void more_read_params(){
        
        if(this.token.getLexema().equals(",")){
            next();
            read_params();
        }
    }
    
    private void whileStatement(){
        
        if(this.token.getLexema().equals("(")){
            next();
        } 
        
        commands_exp();
        
        if(this.token.getLexema().equals(")")){
            next();
        }
        
        if(this.token.getLexema().equals("{")){
            next();
        }
        
        commands();
        
        if(this.token.getLexema().equals("}")){
            next();
        } 
    }
    
    private void ifStatement(){
        
        if(this.token.getLexema().equals("(")){
            next();
        } 
        
        commands_exp();
        
        if(this.token.getLexema().equals(")")){
            next();
        }
        
        if(this.token.getLexema().equals("then")){
            next();
        }
        
        if(this.token.getLexema().equals("{")){
            next();
        }
        
        commands();
        
        if(this.token.getLexema().equals("}")){
            next();
        }
        
        elseStatement();
    }
    
    private void elseStatement(){
        
        if(this.token.getLexema().equals("else")){
            next();
            if(this.token.getLexema().equals("{")){
                next();
            } 
            
            commands();
            
            if(this.token.getLexema().equals("}")){
                next();
            } 
        }
    }
    
    private void _return () {
        
        if(this.token.getLexema().equals("return")){
            next();
            expression();
        } 
        
        if(token.getLexema().equals(";")){
            next();
        }
    }
    
    private void call_variable (){
        
        if(scan.isModifiers(this.token.getLexema())){
            next();
            if(this.token.getLexema().equals(".")){
                next();
            } 
            
            if(this.token.getCodigo().equals("IDE")){
                next();
            }
            
            paths();
        }
    }
    
    private void paths (){
        
        if(this.token.getLexema().equals(".")){
            next();
            struct();
        }
        
        if(this.token.getLexema().equals("[")){
            next();
            matriz();
        }
    }
    
    private void struct(){
        
        if(this.token.getCodigo().equals("IDE")){
            next();
            if(this.token.getLexema().equals(".") || this.token.getLexema().equals("[")){
                paths();
            }
        }
    }
    
    private void matriz(){
        
        if(this.token.getCodigo().equals("NRO") || this.token.getCodigo().equals("IDE")){
            next();
        }
        
        if(this.token.getLexema().equals("]")){
            next();
        }
    }
}
