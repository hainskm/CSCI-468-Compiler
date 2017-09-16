/*since 21042016-2057 */

grammar Little;

options
{
	language = Java;
}

/* Program */

start				: program EOF ;
//start				: start program | .+?;

program				: PROGRAM id BEGIN pgm_body program_end ;
program_end			: END ;
id					: IDENTIFIER ;
pgm_body			: decl func_declarations ;
decl				: string_decl decl
					| var_decl decl
					|;
// empty				: ;

/* Global String Declaration */
string_decl			: STRING id OP_ASSIGN str OP_SEMIC ;
str					: STRINGLITERAL ;

/* Variable Declaration */
var_decl			: var_type id_list OP_SEMIC ;
var_type			: FLOAT
					| INT ;
any_type			: var_type
					| STRING
					| VOID ;
id_list				: id id_tail ;
id_tail				: OP_COMMA id id_tail
					|;

/* Function Parameter List */
param_decl_list		: param_decl param_decl_tail
					|;
param_decl			: var_type id ;
param_decl_tail		: OP_COMMA param_decl param_decl_tail
					|;

/* Function Declaration */
func_declarations	: func_decl func_declarations
					|;
func_decl			: FUNCTION any_type id OP_LP param_decl_list OP_RP BEGIN func_body END ;
func_body			: decl stmt_list ;

/* Statement List */
stmt_list			: stmt stmt_list
					|;
stmt				: base_stmt
					| if_stmt
					| while_stmt ;
base_stmt			: assign_stmt
					| read_stmt
					| write_stmt
					| return_stmt ;

/* Basic Statement */
assign_stmt			: assign_expr OP_SEMIC ;
assign_expr			: id OP_ASSIGN expr ;
read_stmt			: READ OP_LP id_list OP_RP OP_SEMIC ;
write_stmt			: WRITE OP_LP id_list OP_RP OP_SEMIC ;
return_stmt			: RETURN expr OP_SEMIC ;

/* Expressions */
expr				: expr_prefix factor; // factor expr_postfix; 
expr_prefix			: expr_prefix factor addop // addop factor expr_postfix 
					|;
factor				: factor_prefix postfix_expr ; // prefix_expr factor_postfix;
factor_prefix		: factor_prefix postfix_expr mulop //mulop prefix_expr factor_postfix
					|;
postfix_expr		: primary
					| call_expr ;
call_expr			: id OP_LP expr_list OP_RP ;
expr_list			: expr expr_list_tail
					|;
expr_list_tail		: OP_COMMA expr expr_list_tail
					|;
primary				: OP_LP expr OP_RP
					| id
					| INTLITERAL
					| FLOATLITERAL ;
addop				: OP_PLUS
					| OP_MINUS ;
mulop				: OP_STAR
					| OP_SLASH ;

/* Complex Statements and Condition */
if_stmt			: IF OP_LP cond OP_RP decl stmt_list else_part if_stmt_end ;
if_stmt_end		: ENDIF ;
else_part		: ELSE decl stmt_list
				| ;
cond			: expr comop expr ;
comop			: OP_CLT
				| OP_CGT
				| OP_EQUALS
				| OP_NOT_EQUALS
				| OP_CLTE
				| OP_CGTE ;

/* While Statement */
while_stmt			: WHILE OP_LP cond OP_RP decl stmt_list while_stmt_end ;
while_stmt_end		: ENDWHILE;

// We should only need our print functions in the "{}" appearances to identify tokens.

/*Tokens/Keywords */
PROGRAM			: 'PROGRAM' ;
BEGIN			: 'BEGIN' ;
END				: 'END' ;
FUNCTION		: 'FUNCTION' ;
READ			: 'READ' ;
WRITE			: 'WRITE' ;
IF				: 'IF' ;
ELSE			: 'ELSE' ;
ENDIF			: 'ENDIF' ;
WHILE			: 'WHILE' ;
ENDWHILE		: 'ENDWHILE' ;
CONTINUE		: 'CONTINUE' ;
BREAK			: 'BREAK' ;
RETURN			: 'RETURN' ;
INT				: 'INT' ;
VOID			: 'VOID' ;
STRING			: 'STRING' ;
FLOAT			: 'FLOAT' ;

/*Operators */
OP_ASSIGN		: ':=' ;
OP_PLUS			: '+' ;
OP_MINUS		: '-' ;
OP_STAR			: '*' ;
OP_SLASH		: '/' ;
OP_EQUALS		: '=' ;
OP_NOT_EQUALS	: '!=' ;
OP_CLT			: '<' ;
OP_CGT			: '>' ;
OP_LP			: '(' ;
OP_RP			: ')' ;
OP_SEMIC		: ';' ;
OP_COMMA		: ',' ;
OP_CLTE			: '<=' ;
OP_CGTE			: '>=' ;

INTLITERAL		: [0-9]+ ;
FLOATLITERAL 	: [0-9]* '.' [0-9]+ ;
STRINGLITERAL	: '"' ~'"'* '"' ;
COMMENT			: '--' ~[\r\n]* [\r\n] -> skip ;
WHITESPACE		: (' '|'\t'|'\r'|'\n')+ -> skip ;  //\t is tab, \r is char return ' '\t\r\n
IDENTIFIER		: [a-zA-Z][a-zA-Z0-9]* ;
