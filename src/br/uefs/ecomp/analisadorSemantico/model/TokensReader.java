package br.uefs.ecomp.analisadorSemantico.model;

import br.uefs.ecomp.AnalisadorLexico.model.Token;
import br.uefs.ecomp.analisadorSemantico.model.Simbolo.Param;
import java.util.Iterator;

/**
 *
 * @author sandr
 */
public class TokensReader {
    String type;
    boolean isExpression = false;
    Iterator<Token> arq;
    ErrorList erroList;
    ErrorList semanticErrors;
    AnaliseSemantica semantics;
    TabelaDeSimbolos tabela_variables;
    TabelaDeSimbolos tabela_constants;
    TabelaDeSimbolos tabela_local_variables;
    TabelaDeSimbolos tabela_structs;
    TabelaDeSimbolos tabela_functions;
    TabelaDeSimbolos tabela_call_functions;
    ScanLexema scan;
    Token token;
    
    private enum _scope {
        GLOBAL, LOCAL;
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
        this.semanticErrors = new ErrorList();
        this.token = this.arq.next();
        this.semantics = new AnaliseSemantica();
        this.tabela_variables = new TabelaDeSimbolos();
        this.tabela_local_variables = new TabelaDeSimbolos();
        this.tabela_constants = new TabelaDeSimbolos();
        this.tabela_structs = new TabelaDeSimbolos();
        this.tabela_functions = new TabelaDeSimbolos();
        this.tabela_call_functions = new TabelaDeSimbolos();
    }

    public ErrorList getSemanticErrors() {
        return semanticErrors;
    }
    
    private void setSemanticError(String erro){
        this.semanticErrors.addErro(erro);
    }
    
    private void next(){
        if(this.arq.hasNext()){
            this.token = this.arq.next();
        } else {
            stateZero();
        }
    }
    
    private void checkFunctionsCalls(){
        for (Simbolo call : this.tabela_call_functions.pegaTodos()){
            Object[] call_param = call.getParameters().toArray();
            Simbolo findFunction = this.tabela_functions.findById(call);
            
            if(findFunction != null){
                Object[] p = findFunction.getParameters().toArray();
                for(int i = 0; i < p.length; i++){
                    Param param = (Simbolo.Param) p[i];
                    Param c_param = (Simbolo.Param) call_param[i];
                    
                    if(!param.getType().equals(c_param.getType())){
                        setSemanticError("Função ou procedimento " + call.getId() + " chamada na linha "
                            + call.getLine() + " não existe!");
                    }
                }
            }
        }
    }
    
    private void checkForProcedureStart(){
        boolean hasStart = false;
        
        for (Simbolo function : this.tabela_functions.pegaTodos()){
            if(function.getType() == null && function.getId().equals("start")){
                hasStart = !hasStart;
            }
        }
        
        if(!hasStart){
            setSemanticError("Programa não contém o procedimento start()");
        }
    }
    
    public ErrorList stateZero() {
        if(this.arq.hasNext()){
            global_values(_scope.GLOBAL.toString());
            
            while(this.arq.hasNext()){
                if(this.token.getLexema().equals("function")){
                    Simbolo function = new Simbolo();
                    Simbolo type_struct = new Simbolo();
                    function.setCategory(category.FUNCTION.toString());
                    function.setScope(_scope.GLOBAL.toString());
                    next();
                    
                    if(scan.isType(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
                        if(this.token.getCodigo().equals("IDE")){
                            type_struct.setCategory(category.STRUCT.toString());
                            type_struct.setId(this.token.getLexema());
                            type_struct.setScope(_scope.GLOBAL.toString());
                            type_struct.setLine(this.token.getLine());
                        }
                        function.setType(this.token.getLexema());
                        next();
                    }

                    if(this.token.getCodigo().equals("IDE")){
                        function.setId(this.token.getLexema());
                        function.setLine(this.token.getLine());
                        
                        if(this.tabela_structs.findById(type_struct) == null){
                            setSemanticError("Struct " + type_struct.getId() + " do retorno da função " + function.getId()
                                     + " na linha " + type_struct.getLine() +" não declarada!");
                        }
                        
                        next();
                    }

                    function_procedure(function);
                    _return(function);
                    
                    this.tabela_local_variables.removeAll();
                    this.tabela_structs.removeSymbolsByScope("LOCAL");

                    if(this.token.getLexema().equals("}")){
                        next();
                    }
                }
                

                if(this.token.getLexema().equals("procedure")){
                    Simbolo procedure = new Simbolo();
                    procedure.setCategory(category.FUNCTION.toString());
                    procedure.setScope(_scope.GLOBAL.toString());
                    next();
                    if(this.token.getCodigo().equals("IDE") || this.token.getLexema().equals("start")){
                        procedure.setId(this.token.getLexema());
                        procedure.setLine(this.token.getLine());
                        next();
                    }

                    function_procedure(procedure);
                    
                    this.tabela_local_variables.removeAll();
                    this.tabela_structs.removeSymbolsByScope("LOCAL");

                    if(this.token.getLexema().equals("}")){
                       next();
                    }
                }
            } 
            
            checkFunctionsCalls();
            checkForProcedureStart();
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
                Simbolo _extends = new Simbolo();
                _extends.setId(this.token.getLexema());
                _extends.setLine(this.token.getLine());
                _extends.setScope(_scope.LOCAL.toString());
                _extends.setCategory(category.STRUCT.toString());
                if(!this.tabela_structs.contem(_extends)){
                    _extends.setScope(_scope.GLOBAL.toString());
                    if(!this.tabela_structs.contem(_extends)){
                        setSemanticError("Struct não encontrada para extends " +  _extends.getId() + " na linha "
                        + _extends.getLine());
                    }
                }
                
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
            setSemanticError("Struct " + symbol_struct.getId() + " na linha " + symbol_struct.getLine() + " já declarada");
        }
        
        Simbolo new_symbol = new Simbolo();
        new_symbol.setScope(scope);
        var_values_declaration(scope, new_symbol);
    }
    
    private void var_values_atribuition(Simbolo symbol){
        
        if(this.token.getCodigo().equals("IDE")){
            symbol.setId(this.token.getLexema());
            symbol.setLine(this.token.getLine());
            next();
        }
        array_verification(symbol);
        
        if(symbol.getCategory().equals("VARIABLE")){
            if(symbol.getScope().equals("GLOBAL")){
                if(!this.tabela_variables.contem(symbol)){
                    this.tabela_variables.addElement(symbol);
                } else {
                    setSemanticError("Variavel " + symbol.getId()+ " na linha " + symbol.getLine() + " já declarada!");
                }
            } else {
                if(!this.tabela_local_variables.contem(symbol)){
                    this.tabela_local_variables.addElement(symbol);
                } else {
                    setSemanticError("Variavel " + symbol.getId()+ " na linha " + symbol.getLine() + " já declarada!");
                }
            }
        }
    }
    
    private void array_verification(Simbolo symbol){
        
        if(this.token.getLexema().equals("[")){
            symbol.setArray_dimensions();
            next();
            if(this.token.getCodigo().equals("NRO")){
                if(this.semantics.checkTypes("int", this.token)){
                    symbol.setArray_lenght(Integer.parseInt(this.token.getLexema()));
                } else {
                    setSemanticError("Valor inválido para tamanho de vetores na linha "+ symbol.getLine());
                }
                next();
            }
            
            if(this.token.getLexema().equals("]")){
                next();
            }
            
            array_verification(symbol);
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
                setSemanticError("Não é possível atribuir valor " + this.token.getLexema() + " na linha "
                    + symbol.getLine() + " para a constante " 
                    + symbol.getId() + " de tipo " + symbol.getType());
            }
            next();
        } else 
            if(this.token.getLexema().equals("-")){
                next();
                if(this.token.getCodigo().equals("NRO")){
                    if(!this.semantics.checkTypes(symbol.getType(), this.token)){
                        symbol.setValue(this.token.getLexema());
                    } else {
                        setSemanticError("Não é possível atribuir valor " + this.token.getLexema() + " na linha "
                        + symbol.getLine() + " para a constante " 
                        + symbol.getId() + " de tipo " + symbol.getType());
                    }
                    next();
                }
        }
        
        if(!this.tabela_constants.contem(symbol)){
            this.tabela_constants.addElement(symbol);
        } else {
            setSemanticError("Constante " + symbol.getId()+ " na linha " + symbol.getLine() + " já declarada!");
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
    
    private void function_procedure (Simbolo function){
        
        if(this.token.getLexema().equals("(")){
            next();
        }
        
        params_list(function);
        
        if(this.token.getLexema().equals(")")){
            next();
        }
        
        if(!this.tabela_functions.contem(function)){
            this.tabela_functions.addElement(function);
        } else {
            setSemanticError("Função ou procedimento " + function.getId()+ " na linha " + function.getLine() + " já declarada!");
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
    
    private void params_list (Simbolo symbol) {
        param(symbol);
    }
    
    private void param(Simbolo symbol){
        Simbolo param = new Simbolo();
        
        if(scan.isType(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
            if(scan.isType(this.token.getLexema())){
                param.setType(this.token.getLexema());
                param.setCategory(category.VARIABLE.toString());
                param.setLine(this.token.getLine());
            } else {
                param.setId(this.token.getLexema());
                param.setScope(_scope.GLOBAL.toString());
                param.setLine(this.token.getLine());
                if(!this.tabela_structs.contem(param)){
                    setSemanticError("Tipo inválido para struct com nome: " + param.getId() +
                                " na linha " + param.getLine());
                }
            }
            
            String type = this.token.getLexema();
            next();
            if(this.token.getCodigo().equals("IDE")){
                param.setScope(_scope.LOCAL.toString());
                param.setId(this.token.getLexema());
                this.tabela_local_variables.addElement(param);
                symbol.addParam(type, this.token.getLexema());
                next();
            }

            more_params(symbol);
        }
    }
    
    private void more_params(Simbolo symbol){
        
        if(this.token.getLexema().equals(",")){
            next();
            param(symbol);
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
            Simbolo call = new Simbolo();
            call.setId(this.token.getLexema());
            call.setLine(this.token.getLine());
            next();
            if(this.token.getLexema().equals("(")){
                call.setCategory(category.FUNCTION.toString());
                call.setScope(_scope.GLOBAL.toString());
                call_procedure_function(call);
                if(this.token.getLexema().equals(";")){
                    next();
                }
            } else {
                call.setCategory(category.VARIABLE.toString());
                call.setScope(_scope.LOCAL.toString());
                
                paths(call);
                
                if(!this.tabela_local_variables.contem(call)){
                    call.setScope(_scope.GLOBAL.toString());
                    if(!this.tabela_variables.contem(call)){
                        if(this.tabela_constants.contem(call)){
                            setSemanticError("Não pode atribuir valor a constante " +  call.getId() 
                            + " na linha " + call.getLine());
                        } else {
                            setSemanticError("Variavel " +  call.getId() + " na linha " + call.getLine()
                                + " não declarada!");
                        }
                    } else {
                        call.setType(this.tabela_variables.findById(call).getType());
                    }
                    
                } else {
                    call.setType(this.tabela_local_variables.findById(call).getType());
                }
                
                if(scan.isUnaryOp(this.token.getLexema())){
                    next();
                    if(this.token.getLexema().equals(";")){
                        next();
                    }
                } else {
                    if(this.token.getLexema().equals("=")){
                        next();
                        assing(call);
                    }
                } 
            }
        }
    }
    
    private void assing(Simbolo symbol){
        
        if(scan.isModifiers(this.token.getLexema()) || this.token.getLexema().equals("-") 
                || this.token.getCodigo().equals("NRO") || scan.isBooleans(this.token.getLexema())
                || scan.isUnaryOp(this.token.getLexema()) || this.token.getLexema().equals("!")){
            expression(symbol);
        } else if(this.token.getCodigo().equals("IDE")){
                expression(symbol);
        } else if(this.token.getCodigo().equals("CDC")){
                if(symbol.getType() != null){
                    if(!symbol.getType().equals("string")){
                        setSemanticError("Tipo incompatível atribuição de valor do tipo string a uma variavel de tipo "
                                + symbol.getType() + " na linha " +  symbol.getLine());
                    }
                }
                next();
        }
        
        if(this.token.getLexema().equals(";")){
            next();
        }
        
        commands();
    }
    
    private void unary_operation(){
        Simbolo variable = new Simbolo();
        String operation_type = "undefined";
        int line = this.token.getLine();
        
        if(scan.isUnaryOp(this.token.getLexema())){
            this.type = "int";
            operation_type = "increment";
            next();
            if(scan.isModifiers(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
                variable(variable);
            }
        } else 
            if(this.token.getLexema().equals("!")){
                this.type = "boolean";
                operation_type = "negation";
                next();
                if(scan.isModifiers(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
                    variable(variable);
                }
        } else 
            if(scan.isModifiers(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
                variable(variable);
                if(scan.isUnaryOp(this.token.getLexema())){
                    this.type = "int";
                    operation_type = "increment";
                    next();
                }
        } else {
            
            final_value(operation_type, line);
        }
    }
    
    private void variable(Simbolo symbol){
        
        if(scan.isModifiers(this.token.getLexema())){
            call_variable(symbol);
        }

        if(this.token.getCodigo().equals("IDE")){
            symbol.setId(this.token.getLexema());
            symbol.setLine(this.token.getLine());
            symbol.setCategory(category.VARIABLE.toString());
            symbol.setScope(_scope.LOCAL.toString());
            
            next();
            if(this.token.getLexema().equals("(")){
                symbol.setCategory(category.FUNCTION.toString());
                symbol.setScope(_scope.GLOBAL.toString());
                    call_procedure_function(symbol);
            } else {
                if(this.tabela_local_variables.contem(symbol)){
                    symbol.setType(this.tabela_local_variables.findById(symbol).getType());
                    //this.type = symbol.getType();
                } else {
                    if(symbol.getStruct_id() == null){
                        if(this.tabela_constants.contem(symbol)){
                            symbol.setType(this.tabela_constants.findById(symbol).getType());
                            //this.type = symbol.getType();
                        } else 
                            if(!this.tabela_variables.contem(symbol)){
                                setSemanticError("Variavel, constante ou atributo de struct " 
                                        + symbol.getId()+ " na linha " + symbol.getLine() + " não foi declarada!");
                            } else {
                                symbol.setType(this.tabela_variables.findById(symbol).getType());
                                //this.type = symbol.getType();
                            }
                    } else {
                        if(!this.tabela_variables.contem(symbol)){
                            setSemanticError("Variavel, constante ou atributo de struct " 
                                    + symbol.getId()+ " na linha " + symbol.getLine() + " não foi declarada!");
                        } else {
                            symbol.setType(this.tabela_variables.findById(symbol).getType());
                            //this.type = symbol.getType();
                        }
                    }
                }
            }
            paths(symbol);
        }
    }
    
    private void final_value(String operation, int line){
        String t = this.type;
        
        if(this.token.getLexema().equals("-")){
            next();
            if(this.token.getCodigo().equals("NRO")){
                if(this.semantics.checkTypes("int", token)){
                    t = "int";
                } else {
                    t = "real";
                }
                
                if(!this.isExpression){
                    this.type = t;
                }
                
                if(operation.equals("negation")){
                    setSemanticError("Erro operação invalida para tipo real/inteiro na linha " + line);
                }
                next();
            }
        } else 
            if(this.token.getCodigo().equals("NRO")){
                if(this.semantics.checkTypes("int", token)){
                    t = "int";
                } else {
                    t = "real";
                }
                
                if(!this.isExpression){
                    this.type = t;
                }
                
                if(operation.equals("negation")){
                    setSemanticError("Erro operação invalida para tipo real/inteiro na linha " + line);
                }
                next();
        } else
            if(scan.isBooleans(token.getLexema())){
                this.type = "boolean";
                if(operation.equals("increment")){
                    setSemanticError("Erro operação invalida para tipo booleano na linha " + line);
                }
                next();
        }
    }
    
    private void commands_exp(){
        this.type = "undefined";
        this.isExpression = false;
        int line = this.token.getLine();
        
        logical_exp();        
        
        if(!this.type.equals("boolean")){
            setSemanticError("Tipo incompátivel para expressão em comandos if e while na linha " + line);
        }
    }
    
    private void expression(Simbolo symbol) {
        this.type = "undefined";
        int line = this.token.getLine();
        this.isExpression = false;
        
        aritmetic_exp();
        if(scan.isRelationalOpStronger(this.token.getLexema()) || scan.isRelationalOpWeaker(this.token.getLexema())){
            opt_relational_exp();
            if(scan.isLogicalOp(this.token.getLexema())){
                logical_exp();
            }
        }
        
        if(symbol.getType() != null){
            if(this.type.equals("int")){
                if(symbol.getType().equals("boolean")){
                    setSemanticError("Valor inválido para atribuição de variável ou retorno "
                            + "função de tipo " + symbol.getType() + " na linha " + line);
                }
            } else 
                if(!symbol.getType().equals(this.type)){
                setSemanticError("Valor inválido para atribuição de variável ou retorno "
                        + "função de tipo " + symbol.getType() + " na linha " + line);
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
            this.isExpression = true;
            if(!this.type.equals("int") && !this.type.equals("real")){
                this.type = "int";
            }
            next();
            op_unary();
            op_times_div();
        }
    }
    
    private void op_sum(){
        
        if(scan.isPlusMinus(this.token.getLexema())){
            this.isExpression = true;
            if(!this.type.equals("int") && !this.type.equals("real")){
                this.type = "int";
            }
            next();
            operation();
            op_sum();
        }
    }
    
    private void inequal_exp(){
        
        if(scan.isRelationalOpStronger(this.token.getLexema())){
            this.isExpression = true;
            this.type = "boolean";
            next();
            aritmetic_exp();
            equal_exp();
        }
    }
    
    private void equal_exp(){
        
        if(scan.isRelationalOpWeaker(this.token.getLexema())){
            this.type = "boolean";
            this.isExpression = true;
            next();
            aritmetic_exp();
            inequal_exp();
            equal_exp();
        }
    }
    
    private void opt_relational_exp(){
        
        if(scan.isRelationalOpStronger(this.token.getLexema())){
            this.isExpression = true;
            this.type = "boolean";
            next();
            aritmetic_exp();
            inequal_exp();
            equal_exp();
        } else if(scan.isRelationalOpWeaker(this.token.getLexema())){
            this.isExpression = true;
            this.type = "boolean";
            next();
            aritmetic_exp();
            inequal_exp();
        }
    }
    
    private void opt_logical_exp(){
        
        if(scan.isLogicalOp(this.token.getLexema())){
            this.isExpression = true;
            this.type = "boolean";
            next();
            logical_exp();
        }
    }
    
    private void call_procedure_function(Simbolo symbol){
        
        if(this.token.getLexema().equals("(")){
            next();
        }
        
        realParams(symbol);
        
        if(this.token.getLexema().equals(")")){
            next();
        }
        
        commands();
    }
    
    private void realParams(Simbolo symbol){
        realParam(symbol);
    }
    
    private void realParam(Simbolo symbol){
        
        if(this.token.getCodigo().equals("NRO") || this.token.getCodigo().equals("CDC") 
                || scan.isBooleans(this.token.getLexema()) || scan.isModifiers(this.token.getLexema())
                || this.token.getLexema().equals("-") || this.token.getCodigo().equals("IDE")){
            if(scan.isModifiers(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
                Simbolo param = new Simbolo();
                variable(param);
                symbol.addParam(param.getType(), param.getId());
            } else 
                if(this.token.getLexema().equals("-")){
                    next();
                    if(this.token.getCodigo().equals("NRO")){
                        if(this.semantics.checkTypes("int", token)){
                            symbol.addParam("int", null);
                        } else {
                            symbol.addParam("real", null);
                        }
                        next();
                    }
            } else 
                if(this.token.getCodigo().equals("CDC")){
                    symbol.addParam("string", null);
                    next();
            } else
                if(scan.isBooleans(this.token.getLexema())){
                    symbol.addParam("boolean", null);
                    next();
            } else {
                if(this.semantics.checkTypes("int", token)){
                    symbol.addParam("int", null);
                } else {
                    symbol.addParam("real", null);
                }
                next();
            }
            
            more_real_params(symbol);
            
            this.tabela_call_functions.addElement(symbol);
        } 
    }
    
    private void more_real_params(Simbolo symbol){
        
        if(this.token.getLexema().equals(",")){
            next();
            if(this.token.getCodigo().equals("NRO") || this.token.getCodigo().equals("CDC") 
                || scan.isBooleans(this.token.getLexema()) || scan.isModifiers(this.token.getLexema())
                || this.token.getLexema().equals("-") || this.token.getCodigo().equals("IDE")){
                realParam(symbol);
            }
            more_real_params(symbol);
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
        Simbolo print_param = new Simbolo();
        
        if(this.token.getCodigo().equals("CDC")){
            next();
        } else
            if(scan.isModifiers(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
                variable(print_param);
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
        Simbolo read_param = new Simbolo();
        
        if(scan.isModifiers(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
            variable(read_param);
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
    
    private void _return (Simbolo symbol) {
        
        if(this.token.getLexema().equals("return")){
            next();
            if(token.getCodigo().equals("CDC")){
                if(!symbol.getType().equals("string")){
                    setSemanticError("Retorno inválido para função de tipo " + symbol.getType() 
                            + " na linha " + symbol.getLine());
                }
                next();
            } else {
                expression(symbol);
            }
        }
        
        if(token.getLexema().equals(";")){
            next();
        }
    }
    
    private void call_variable (Simbolo symbol){
        symbol.setScope(_scope.LOCAL.toString());
        symbol.setCategory(category.VARIABLE.toString());
        
        if(scan.isModifiers(this.token.getLexema())){
            if(this.token.getLexema().equals("global")){
                symbol.setScope(_scope.GLOBAL.toString());
            }
            next();
            if(this.token.getLexema().equals(".")){
                next();
            }
            
            if(this.token.getCodigo().equals("IDE")){
                symbol.setId(this.token.getLexema());
                symbol.setLine(this.token.getLine());
                next();
            }
            
            paths(symbol);
            
            if(symbol.getScope().equals("LOCAL")){
                if(!this.tabela_local_variables.contem(symbol)){
                    setSemanticError("Variável " + symbol.getId()+ " na linha " + symbol.getLine() + " não foi declarada!");
                } else {
                    symbol.setType(this.tabela_local_variables.findById(symbol).getType());
                    if(!this.isExpression){
                        this.type = symbol.getType();
                    }
                }
            }
            
            if(symbol.getScope().equals("GLOBAL")){
                if(symbol.getStruct_id() == null){
                    if(!this.tabela_constants.contem(symbol)){
                        setSemanticError("Constante " + symbol.getId()+ " na linha " + symbol.getLine() + " não foi declarada!");
                    } else {
                        symbol.setType(this.tabela_constants.findById(symbol).getType());
                        if(!this.isExpression){
                            this.type = symbol.getType();
                        }
                    }
                } else {
                    if(!this.tabela_variables.contem(symbol)){
                        setSemanticError("Variável " + symbol.getId()+ " na linha " + symbol.getLine() + " não foi declarada!");
                    } else {
                        symbol.setType(this.tabela_variables.findById(symbol).getType());
                        if(!this.isExpression){
                            this.type = symbol.getType();
                        }
                    }
                }
            }
        }
    }
    
    private void paths (Simbolo symbol){
        
        if(this.token.getLexema().equals(".")){
            next();
            struct(symbol);
        }
        
        if(this.token.getLexema().equals("[")){
            next();
            symbol.setArray_dimensions();
            matriz();
        }
    }
    
    private void struct(Simbolo symbol){
        
        if(this.token.getCodigo().equals("IDE")){
            symbol.setStruct_id(symbol.getId());
            symbol.setId(this.token.getLexema());
            next();
            
            if(this.token.getLexema().equals(".") || this.token.getLexema().equals("[")){
                paths(symbol);
            }
        }
    }
    
    private void matriz(){
        
        if(this.token.getCodigo().equals("NRO") || this.token.getCodigo().equals("IDE")){
            if(this.token.getCodigo().equals("IDE")){
                Simbolo indice_var = new Simbolo();
                indice_var.setId(this.token.getLexema());
                indice_var.setScope(_scope.LOCAL.toString());
                indice_var.setLine(this.token.getLine());
                
                if(this.tabela_local_variables.findById(indice_var) == null){
                    indice_var.setScope(_scope.GLOBAL.toString());
                    if(this.tabela_variables.findById(indice_var) == null){
                        setSemanticError("Variável " + indice_var.getId()+ " na linha " + indice_var.getLine() + " não foi declarada!");
                    } else {
                        indice_var = this.tabela_local_variables.findById(indice_var);
                    }
                } else {
                    indice_var = this.tabela_local_variables.findById(indice_var);
                }
                
                if(!indice_var.getType().equals("int")){
                    setSemanticError("Tipo incompativel para indice de vetores na linha "+ indice_var.getLine());
                }
            }
            next();
        }
        
        if(this.token.getLexema().equals("]")){
            next();
        }
    }
}
