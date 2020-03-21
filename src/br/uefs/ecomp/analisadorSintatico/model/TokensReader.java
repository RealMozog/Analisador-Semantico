package br.uefs.ecomp.analisadorSintatico.model;

import br.uefs.ecomp.AnalisadorLexico.model.Token;
import java.util.Iterator;

/**
 *
 * @author sandr
 */
public class TokensReader {
    Iterator<Token> arq;
    ErrorList erroList;
    ScanLexema scan;
    Token token;
    
    public TokensReader(Iterator<Token> arq){
        this.arq = arq;
        scan = new ScanLexema();
        this.erroList = new ErrorList();
        this.token = this.arq.next();
    }
    
    private void setErro(int line, String expected, String found){
        Error erro = new Error(line, expected, found); 
        this.erroList.addErro(erro);
    }
    
    private void next(){
        if(this.arq.hasNext()){
            this.token = this.arq.next();
        } else {
            stateZero();
        }
    }
    
    public ErrorList stateZero() {
        if(this.arq.hasNext()){
            global_values();
            
            while(this.arq.hasNext()){
                if(this.token.getLexema().equals("function")){
                    next();
                    if(scan.isType(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
                        next();
                    } else {
                        setErro(this.token.getLine(), "Type or typedef", this.token.getLexema());
                        while(!this.token.getCodigo().equals("IDE")){
                            next();
                            if(!this.arq.hasNext()){
                                break;
                            }
                        }
                    }

                    if(this.token.getCodigo().equals("IDE")){
                        next();
                    } else {
                        setErro(this.token.getLine(), "Identifier", this.token.getLexema());
                        panicState("(");
                    }

                    function_procedure();
                    _return();

                    if(this.token.getLexema().equals("}")){
                        next();
                     } else {
                        setErro(this.token.getLine(), "Delimitator:\"}", this.token.getLexema());
                        while(arq.hasNext()){
                            next();
                        }
                     }
                }

                if(this.token.getLexema().equals("procedure")){
                    next();
                    if(this.token.getCodigo().equals("IDE") || this.token.getLexema().equals("start")){
                        next();
                    } else {
                        setErro(this.token.getLine(), "Identifier or reserverd word: \"start", this.token.getLexema());
                        panicState("(");
                    }

                    function_procedure();

                    if(this.token.getLexema().equals("}")){
                       next();
                    } else {
                        setErro(this.token.getLine(), "Delimitator:\"}", this.token.getLexema());
                        while(arq.hasNext()){
                            next();
                        }
                    }
                }
            } 
        }
        
        return this.erroList;
    }
    
    private void global_values(){
        
        if(this.token.getLexema().equals("var")){
            next();
            if(this.token.getLexema().equals("{")){
                next();
            } else {
                setErro(this.token.getLine(), "Delimitator:\"{", this.token.getLexema());
                while(!scan.isType(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                        && !this.token.getLexema().equals("typedef") && !this.token.getLexema().equals("struct")
                        && !this.token.getLexema().equals("}")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            var_values_declaration();
            
            if(this.token.getLexema().equals("}")){
                next();
            } else {
                setErro(this.token.getLine(), "Delimitator:\"}", this.token.getLexema());
                panicState("const");
            }
            
            if(this.token.getLexema().equals("const")){
                next();
            } else {
                setErro(this.token.getLine(), "Reserved word: \"const", this.token.getLexema());
                panicState("{");
            }
            
            if(this.token.getLexema().equals("{")){
                next();
            } else {
                setErro(this.token.getLine(), "Delimitator:\"{", this.token.getLexema());
                while(!scan.isType(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                        && !this.token.getLexema().equals("}")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }

            const_values_declaration();
            
            if(this.token.getLexema().equals("}")){
                next();
            } else {
                setErro(this.token.getLine(), "Delimitator:\"}", this.token.getLexema());
                while(!this.token.getLexema().equals("function") && !this.token.getLexema().equals("procedure")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
        }
        
        if(this.token.getLexema().equals("const")){
            next();
            if(this.token.getLexema().equals("{")){
                next();
            } else {
                setErro(this.token.getLine(), "Delimitator:\"{", this.token.getLexema());
                while(!scan.isType(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                        && !this.token.getLexema().equals("}")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            const_values_declaration();
            
            if(this.token.getLexema().equals("}")){
                next();
            } else {
                setErro(this.token.getLine(), "Delimitator:\"}", this.token.getLexema());
                panicState("var");
            }
            
            if(this.token.getLexema().equals("var")){
                next();
            } else {
                setErro(this.token.getLine(), "reserved word: \"var", this.token.getLexema());
                panicState("{");
            }
            
            if(this.token.getLexema().equals("{")){
                next();
            } else {
                setErro(this.token.getLine(), "Delimitator:\"{", this.token.getLexema());
                while(!scan.isType(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                        && !this.token.getLexema().equals("typedef") && !this.token.getLexema().equals("struct")
                        && !this.token.getLexema().equals("}")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }

            var_values_declaration();
            
            if(this.token.getLexema().equals("}")){
                next();
            } else {
                setErro(this.token.getLine(), "Delimitator:\"}", this.token.getLexema());
                while(!this.token.getLexema().equals("function") && !this.token.getLexema().equals("procedure")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
        }
    }
    
    private void var_values_declaration(){
        
        if(scan.isType(this.token.getLexema())){
            next();
            var_values_atribuition();
            var_more_atribuition();
            if(this.token.getLexema().equals(";")){
                next();
            } else {
                setErro(this.token.getLine(), "Delimitator:\";", this.token.getLexema());
                while(!scan.isType(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                        && !this.token.getLexema().equals("typedef") && !this.token.getLexema().equals("struct")
                        && !this.token.getLexema().equals("}")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            var_values_declaration();
        }
        
        if(this.token.getLexema().equals("typedef")){
            next();
            if(this.token.getLexema().equals("struct")){
                next();
            } else {
                setErro(this.token.getLine(), "Reserved word: \"struct", this.token.getLexema());
                while(!this.token.getCodigo().equals("IDE")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            IDE_struct();
            
            var_values_declaration();
        }
        
        if(this.token.getLexema().equals("struct")){
            next();
            
            IDE_struct();
            
            var_values_declaration();
        }
    }
    
    private void IDE_struct(){
        
        if(this.token.getCodigo().equals("IDE")){
            next();
        } else {
            setErro(this.token.getLine(), "Identifier", this.token.getLexema());
            panicState("{");
        }
        
        IDE_struct_2();
    }
    
    private void IDE_struct_2(){
        
        if(this.token.getLexema().equals("extends")){
            next();
            if(this.token.getCodigo().equals("IDE")){
                next();
            } else {
                setErro(this.token.getLine(), "Identifier", this.token.getLexema());
                panicState("{");
            }
        }
        
        if(this.token.getLexema().equals("{")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator:\"{", this.token.getLexema());
            panicState("var");
        }
        
        if(this.token.getLexema().equals("var")){
            next();
        } else {
            setErro(this.token.getLine(), "Reserved word: \"var", this.token.getLexema());
            panicState("{");
        }
        
        if(this.token.getLexema().equals("{")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator:\"{", this.token.getLexema());
            while(!scan.isType(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("typedef") && !this.token.getLexema().equals("struct")
                    && !this.token.getLexema().equals("}")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        var_values_declaration();
        
        if(this.token.getLexema().equals("}")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator:\"}", this.token.getLexema());
            panicState("}");
        }

        if(this.token.getLexema().equals("}")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator:\"}", this.token.getLexema());
            while(!scan.isType(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("typedef") && !this.token.getLexema().equals("struct")
                    && !this.token.getLexema().equals("}")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        var_values_declaration();
    }
    
    private void var_values_atribuition(){
        
        if(this.token.getCodigo().equals("IDE")){
            next();
        } else {
            setErro(this.token.getLine(), "Identifier", this.token.getLexema());
            while(!scan.isType(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("typedef") && !this.token.getLexema().equals("struct")
                    && !this.token.getLexema().equals("}") && !this.token.getLexema().equals("[")
                    && !this.token.getLexema().equals(";")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        array_verification();
    }
    
    private void array_verification(){
        
        if(this.token.getLexema().equals("[")){
            next();
            if(this.token.getCodigo().equals("NRO")){
                next();
            } else {
                setErro(this.token.getLine(), "Number", this.token.getLexema());
                panicState("]");
            }
            
            array_verification();
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
            } else {
                setErro(this.token.getLine(), "Delimitator: \";", this.token.getLexema());
                while(!scan.isType(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                        && !this.token.getLexema().equals("}")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            const_values_declaration();
        } 
    }
    
    private void const_values_atribuition(){
        
        if(this.token.getCodigo().equals("IDE")){
            next();
        } else {
            setErro(this.token.getLine(), "Identifier", this.token.getLexema());
            panicState("=");
        }
        
        if(this.token.getLexema().equals("=")){
            next();
        } else {
            setErro(this.token.getLine(), "Operator: \"=", this.token.getLexema());
            while(!this.token.getCodigo().equals("NRO") && !this.token.getCodigo().equals("CDC")
                    && scan.isBooleans(this.token.getLexema())){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
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
                } else {
                    setErro(this.token.getLine(), "Number", this.token.getLexema());
                    panicState(";");
                }
        } else {
            setErro(this.token.getLine(), "Number, string, boolean", this.token.getLexema());
            panicState(";");
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
        } else {
            setErro(this.token.getLine(), "Delimitator:\"(", this.token.getLexema());
            while(!scan.isType(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();                
            }
        }
        
        params_list();
        
        if(this.token.getLexema().equals(")")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator:\")", this.token.getLexema());
            panicState("{");
        }
        
        if(this.token.getLexema().equals("{")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator:\"{", this.token.getLexema());
            panicState("var");
        }
        
        if(this.token.getLexema().equals("var")) {
            next();
        } else {
            setErro(this.token.getLine(), "Reserved word: \"var", this.token.getLexema());
            panicState("{");
        }
        
        var_fuctions_procedures();
        
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
            } else {
                setErro(this.token.getLine(), "Identifier", this.token.getLexema());
                while(!this.token.getLexema().equals(",") && !this.token.getLexema().equals(")")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
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
            
    
    private void var_fuctions_procedures (){
        
        if(this.token.getLexema().equals("{")){
            
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \"{", this.token.getLexema());
            while(!scan.isType(this.token.getLexema()) && !this.token.getLexema().equals("struct")
                    && !this.token.getLexema().equals("typedef") && !this.token.getLexema().equals("}")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        var_values_declaration();
        
        if(this.token.getLexema().equals("}")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \"}", this.token.getLexema());
            while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("return") && !this.token.getLexema().equals("}")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
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
            } else {
                setErro(this.token.getLine(), "Delimitator: \";", this.token.getLexema());
                while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                        && !this.token.getLexema().equals("return") && !this.token.getLexema().equals("}")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
        }
        
        if(this.token.getCodigo().equals("IDE")){
            next();
            if(this.token.getLexema().equals("(")){
                call_procedure_function();
                if(this.token.getLexema().equals(";")){
                    next();
                } else {
                    setErro(this.token.getLine(), "Delimitator: \";", this.token.getLexema());
                    while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                            && !this.token.getLexema().equals("return") && !this.token.getLexema().equals("}")){
                        if(!this.arq.hasNext()){
                            break;
                        }
                        next();
                    }
                }
            } else {
                paths();
                if(scan.isUnaryOp(this.token.getLexema())){
                    next();
                    if(this.token.getLexema().equals(";")){
                        next();
                    } else {
                        setErro(this.token.getLine(), "Delimitator: \";", this.token.getLexema());
                        while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                                && !this.token.getLexema().equals("return") && !this.token.getLexema().equals("}")
                                && !scan.isModifiers(this.token.getLexema())){
                            if(!this.arq.hasNext()){
                                break;
                            }
                            next();
                        }
                    }
                } else {
                    if(this.token.getLexema().equals("=")){
                        next();
                        assing();
                    } else {
                        setErro(this.token.getLine(), "Attribuition Operator: \"=", this.token.getLexema());
                        while(!this.token.getCodigo().equals("NRO") && !scan.isModifiers(this.token.getLexema())
                                && this.token.getCodigo().equals("IDE") && !this.token.getLexema().equals("-")
                                && this.token.getCodigo().equals("CDC")){
                            if(!this.arq.hasNext()){
                                break;
                            }
                            next();
                        }
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
        } else {
            setErro(this.token.getLine(), "Number, string, boolean, variable, function, procedure"
                    + ", unary operation", this.token.getLexema());
            panicState(";");
        }
        
        if(this.token.getLexema().equals(";")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \";", this.token.getLexema());
            while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("return") && !this.token.getLexema().equals("}")
                    && !scan.isModifiers(this.token.getLexema())){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        commands();
    }
    
    private void unary_operation(){
        
        if(scan.isUnaryOp(this.token.getLexema())){
            next();
            if(scan.isModifiers(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
                variable();
            } else {
                setErro(this.token.getLine(), "Variable", this.token.getLexema());
                while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                        && !this.token.getLexema().equals("return") && !this.token.getLexema().equals("}")
                        && !scan.isModifiers(this.token.getLexema()) && !this.token.getLexema().equals(";")
                        && !scan.isNoLogicalOperators(this.token.getLexema())){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
        } else 
            if(this.token.getLexema().equals("!")){
                next();
                if(scan.isModifiers(this.token.getLexema()) || this.token.getCodigo().equals("IDE")){
                    variable();
                } else {
                    setErro(this.token.getLine(), "Variable", this.token.getLexema());
                    while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                            && !this.token.getLexema().equals("return") && !this.token.getLexema().equals("}")
                            && !scan.isModifiers(this.token.getLexema()) && !this.token.getLexema().equals(";")
                            && !scan.isNoLogicalOperators(this.token.getLexema())){
                        if(!this.arq.hasNext()){
                            break;
                        }
                        next();
                    }
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
            } else {
                setErro(this.token.getLine(), "Number", this.token.getLexema());
                while(!scan.isOperators(this.token.getLexema()) && !this.token.getLexema().equals(";")
                        && !this.token.getLexema().equals(")") && !scan.isUnaryOp(this.token.getLexema())){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
        } else 
            if(this.token.getCodigo().equals("NRO")){
                next();
        } else
            if(scan.isBooleans(token.getLexema())){
                next();
        } else {
            
            setErro(this.token.getLine(), "number, boolean", this.token.getLexema());
            
            while(!scan.isOperators(this.token.getLexema()) && !this.token.getLexema().equals(";")
                    && !this.token.getLexema().equals(")") && !this.token.getLexema().equals(".")
                    && !this.token.getLexema().equals("[") && !scan.isUnaryOp(this.token.getLexema())){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
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
            } else {
                setErro(this.token.getLine(), "Delimitator: \")", this.token.getLexema());
                while(!scan.isLogicalOp(this.token.getLexema()) && !this.token.getLexema().equals(")")
                        && !this.token.getLexema().equals(";")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
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
            } else {
                setErro(this.token.getLine(), "Delimitator: \")", this.token.getLexema());
                while(!scan.isRelationalOpStronger(this.token.getLexema()) && !scan.isRelationalOpWeaker(this.token.getLexema())){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
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
            } else {
                setErro(this.token.getLine(), "Delimitator: \")", this.token.getLexema());
                while(!scan.isRelationalOpStronger(this.token.getLexema()) && !scan.isRelationalOpWeaker(this.token.getLexema())
                        && !scan.isPlusMinus(this.token.getLexema()) && !scan.isTimesDiv(this.token.getLexema())){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
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
        } else {
            
            setErro(this.token.getLine(), "Relational Operators", this.token.getLexema());
            while(!this.token.getLexema().equals("(") && !scan.isUnaryOp(this.token.getLexema())
                    && !scan.isModifiers(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("-") && !scan.isBooleans(this.token.getLexema())
                    && !this.token.getCodigo().equals("NRO")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
            
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
        } else {
            setErro(this.token.getLine(), "Delimitator: \"(", this.token.getLexema());
            while(!this.token.getCodigo().equals("NRO") && !this.token.getCodigo().equals("CDC")
                    && !scan.isBooleans(this.token.getLexema()) && !scan.isModifiers(this.token.getLexema())
                    && !this.token.getLexema().equals("-")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        realParams();
        
        if(this.token.getLexema().equals(")")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \")", this.token.getLexema());
            panicState(";");
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
                } else {
                    setErro(this.token.getLine(), "Identifier", this.token.getLexema());
                    panicState(",");
                }
            } else 
                if(this.token.getLexema().equals("-")){
                    next();
                    if(this.token.getCodigo().equals("NRO")){
                        next();
                    } else {
                        setErro(this.token.getLine(), "Number", this.token.getLexema());
                        panicState(",");
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
            } else {
                setErro(this.token.getLine(), "Number, string, boolean, variable", this.token.getLexema());
                panicState(",");
            }
            more_real_params();
        }
    }
    
    private void printStatement(){
        
        if(this.token.getLexema().equals("(")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \"(", this.token.getLexema());
            while(!scan.isModifiers(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getCodigo().equals("CDC")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        print_params();
        
        if(this.token.getLexema().equals(")")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \")", this.token.getLexema());
            panicState(";");
        }
        
        if(this.token.getLexema().equals(";")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \";", this.token.getLexema());
            while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("return") && !this.token.getLexema().equals("}")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
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
        } else {
            setErro(this.token.getLine(), "Varible, string", this.token.getLexema());
            panicState(")");
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
        } else {
            setErro(this.token.getLine(), "Delimitator: \"(", this.token.getLexema());
            while(!scan.isModifiers(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        read_params();
        
        if(this.token.getLexema().equals(")")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \")", this.token.getLexema());
            panicState(";");
        }
        
        if(this.token.getLexema().equals(";")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \";", this.token.getLexema());
            while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("return") && !this.token.getLexema().equals("}")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
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
        } else {
            setErro(this.token.getLine(), "Variable", this.token.getLexema());
            panicState(")");     
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
        } else {
            setErro(this.token.getLine(), "Delimitator: \"(", this.token.getLexema());
            while(!scan.isModifiers(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("-") && !this.token.getCodigo().equals("NRO")
                    && !scan.isBooleans(this.token.getLexema()) && !scan.isUnaryOp(this.token.getLexema())
                    && !this.token.getLexema().equals("!")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        commands_exp();
        
        if(this.token.getLexema().equals(")")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \")", this.token.getLexema());
            panicState("{");
        }
        
        if(this.token.getLexema().equals("{")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \"{", this.token.getLexema());
            while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("}") && !scan.isModifiers(this.token.getLexema())){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        commands();
        
        if(this.token.getLexema().equals("}")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \"}", this.token.getLexema());
            while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("}") && !scan.isModifiers(this.token.getLexema())
                    && !this.token.getLexema().equals("return")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
    }
    
    private void ifStatement(){
        
        if(this.token.getLexema().equals("(")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \"(", this.token.getLexema());
            while(!scan.isModifiers(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("-") && !this.token.getCodigo().equals("NRO")
                    && !scan.isBooleans(this.token.getLexema()) && !scan.isUnaryOp(this.token.getLexema())
                    && !this.token.getLexema().equals("!")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        commands_exp();
        
        if(this.token.getLexema().equals(")")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \")", this.token.getLexema());
            panicState("then");
        }
        
        if(this.token.getLexema().equals("then")){
            next();
        } else {
            setErro(this.token.getLine(), "Reserved word: \"then", this.token.getLexema());
            panicState("{");
        }
        
        if(this.token.getLexema().equals("{")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \"{", this.token.getLexema());
            while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("}") && !scan.isModifiers(this.token.getLexema())){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        commands();
        
        if(this.token.getLexema().equals("}")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \"}", this.token.getLexema());
            while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("}") && !scan.isModifiers(this.token.getLexema())
                    && !this.token.getLexema().equals("return") && !this.token.getLexema().equals("else")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        elseStatement();
    }
    
    private void elseStatement(){
        
        if(this.token.getLexema().equals("else")){
            next();
            if(this.token.getLexema().equals("{")){
                next();
            } else {
                setErro(this.token.getLine(), "Delimitator: \"{", this.token.getLexema());
                while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                        && !this.token.getLexema().equals("}") && !scan.isModifiers(this.token.getLexema())
                        && !this.token.getLexema().equals("return")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            commands();
            
            if(this.token.getLexema().equals("}")){
                next();
            } else {
                setErro(this.token.getLine(), "Delimitator: \"}", this.token.getLexema());
                while(!scan.isCommands(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                        && !this.token.getLexema().equals("}") && !scan.isModifiers(this.token.getLexema())
                        && !this.token.getLexema().equals("return")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
        }
    }
    
    private void _return () {
        
        if(this.token.getLexema().equals("return")){
            next();
            expression();
        } else {
            setErro(this.token.getLine(), "Delimitator: \";", this.token.getLexema());
            while(!scan.isModifiers(this.token.getLexema()) && !this.token.getCodigo().equals("IDE")
                    && !this.token.getLexema().equals("-") && !this.token.getCodigo().equals("NRO")
                    && !scan.isBooleans(this.token.getLexema()) && !scan.isUnaryOp(this.token.getLexema())
                    && !this.token.getLexema().equals("!")){
                if(!this.arq.hasNext()){
                    break;
                }
                next();
            }
        }
        
        if(token.getLexema().equals(";")){
            next();
        } else {
            setErro(this.token.getLine(), "Reserved word: \"return", this.token.getLexema());
            panicState("}");
        }
    }
    
    private void call_variable (){
        
        if(scan.isModifiers(this.token.getLexema())){
            next();
            if(this.token.getLexema().equals(".")){
                next();
            } else {
                setErro(this.token.getLine(), "Delimitator: \".", this.token.getLexema());
                while(!this.token.getCodigo().equals("IDE")){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
            }
            
            if(this.token.getCodigo().equals("IDE")){
                next();
            } else {
               setErro(this.token.getLine(), "Identifier", this.token.getLexema());
               while(!scan.isOperators(this.token.getLexema()) && !this.token.getLexema().equals(";")
                        && !this.token.getLexema().equals(")") && !this.token.getLexema().equals(".")
                        && !this.token.getLexema().equals("[") && !this.token.getLexema().equals("]")
                        && !this.token.getLexema().equals(")") && !scan.isUnaryOp(this.token.getLexema())){
                    if(!this.arq.hasNext()){
                        break;
                    }
                    next();
                }
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
        } else {
            setErro(this.token.getLine(), "Identifier", this.token.getLexema());
            panicState("=");
        }
    }
    
    private void matriz(){
        
        if(this.token.getCodigo().equals("NRO") || this.token.getCodigo().equals("IDE")){
            next();
        } else {
            setErro(this.token.getLine(), "Number, indetifier", this.token.getLexema());
            panicState("]");
        }
        
        if(this.token.getLexema().equals("]")){
            next();
        } else {
            setErro(this.token.getLine(), "Delimitator: \"]", this.token.getLexema());
            panicState("=");
        }
    }
    
    private void panicState (String sinalizer) {
        while(!this.token.getLexema().equals(sinalizer)){
            if(!this.arq.hasNext()){
                break;
            }
            next();
        }
    }
}
