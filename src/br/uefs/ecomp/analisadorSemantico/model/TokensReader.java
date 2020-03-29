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
    
    private enum category {
        FUNCTION, STRUCT, VARIABLE, CONSTANT
    }
    
    public TokensReader(Iterator<Token> arq){
        this.arq = arq;
        scan = new ScanLexema();
        this.erroList = new ErrorList();
        this.token = this.arq.next();
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
            for (Object lista : this.tabela_variables.lista()) {
                System.out.print(lista.toString());
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

            const_values_declaration();
            
            if(this.token.getLexema().equals("}")){
                next();
            } 
        }
        
        if(this.token.getLexema().equals("const")){
            next();
            if(this.token.getLexema().equals("{")){
                next();
            }
            
            const_values_declaration();
            
            if(this.token.getLexema().equals("}")){
                next();
            }
            
            if(this.token.getLexema().equals("var")){
                next();
            } 
            
            if(this.token.getLexema().equals("{")){
                next();
            } 

            var_values_declaration(scope, symbol);
            
            if(this.token.getLexema().equals("}")){
                next();
            } 
        }
    }
    
    private void var_values_declaration(String scope, Simbolo symbol){
        
        if(symbol.getCategory() == null){
            symbol.setCategory(category.VARIABLE.toString());
        }
        if(!scope.equals("STRUCT")){
            symbol.setScope(scope);
        }

        if(scan.isType(this.token.getLexema())){
            if(scope.equals("STRUCT")){
                Simbolo var_struct = new Simbolo();
            }
            symbol.setType(this.token.getLexema());
            next();
            var_values_atribuition(symbol);
            var_more_atribuition();
            if(this.token.getLexema().equals(";")){
                next();
            }
            
            Simbolo s_var = new Simbolo();
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
            var_values_declaration(scope, s_typedef);
        }
        
        if(this.token.getLexema().equals("struct")){
            next();
            
            IDE_struct(scope, symbol);
            
            Simbolo s_struct = new Simbolo();
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
    
    private void IDE_struct_2(String scope, Simbolo symbol){
        
        if(this.token.getLexema().equals("extends")){
            next();
            if(this.token.getCodigo().equals("IDE")){
                symbol.setExtends_id(this.token.getLexema());
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
        
        
        var_values_declaration(_scope.STRUCT.toString(), symbol);
        
        if(this.token.getLexema().equals("}")){
            next();
        } 

        if(this.token.getLexema().equals("}")){
            next();
        }
        
        var_values_declaration(scope);
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
            next();
            if(this.token.getCodigo().equals("NRO")){
                symbol.setIsArray(true);
                // symbol.setArray_lenght(Integer.parseInt(this.token.getLexema()));
                next();
            } 
            
            array_verification(symbol);
        }
        
        if(symbol.getCategory().equals("VARIABLE")){
            if(symbol.getScope().equals("GLOBAL")){
                this.tabela_variables.addElement(symbol);
            } else {
                this.tabela_local_varibles.addElement(symbol);
            }
        }
        
        if(symbol.getCategory().equals("CONSTANT")){
            this.tabela_constants.addElement(symbol);
        }
    }
    
    private void var_more_atribuition(){
        
        if(this.token.getLexema().equals(",")){
            next();
            var_values_atribuition();
            var_more_atribuition();
        }
    }
    
    private void const_values_declaration(){
        
        if(scan.isType(this.token.getLexema())){
            next();
            const_values_atribuition();
            const_more_atribuition();
            if(this.token.getLexema().equals(";")){
                next();
            }
            
            const_values_declaration();
        } 
    }
    
    private void const_values_atribuition(){
        
        if(this.token.getCodigo().equals("IDE")){
            next();
        }
        
        if(this.token.getLexema().equals("=")){
            next();
        }
        
        value_const();
    }
    
    private void value_const(){
        
        if(this.token.getCodigo().equals("NRO") || this.token.getCodigo().equals("CDC")
                || scan.isBooleans(this.token.getLexema())){
            next();
        } else 
            if(this.token.getLexema().equals("-")){
                next();
                if(this.token.getCodigo().equals("NRO")){
                    next();
                }
        }
    }
    
    private void const_more_atribuition(){
        
        if(this.token.getLexema().equals(",")){
            next();
            const_values_atribuition();
            const_more_atribuition();
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
        
        var_fuctions_procedures(scope.LOCAL.toString());
        
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
        
        var_values_declaration(scope);
        
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