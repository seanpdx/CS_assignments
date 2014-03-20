// Sean Bradford
// Lab 4 Programming Languages 11/14/2010
// Coded to Dubstep, heavy metal, and instrumental banjos.
import java.util.Scanner;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.*;

public class Syntax_Analyzer {

    public static boolean program(Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, Vector symbol_table) {
        // make sure program is boolean.
        int spaces = 0;
        // Push Rule 0
        prog_stack.push("<program>");

        boolean test;
        String[] current_token = new String[10];

        // mapping of current[]
        //
        // current[1]
        // current[2] is last value computed, saved, ect.
        //
        //current[8] is first dim int
        //current[9] is the 2nd dim int, it will be -1 if there is no 2nd dim
        //


        current_token[0] = (String) token_id.remove(0); // cast to string
        //String current_token;
        // Rule for program states that a declaration or statement comes next.
        while (!token_lex.isEmpty()) // while there are still lexemes.
        {
            current_token[8] = "0"; // reset each pass.
            current_token[9] = "-1";
            //current_token[0] = (String) token_id.remove(0); // cast to string


            test = declaration(current_token, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

            if (test == false) {
                // check to see if it is a statement, if neither, syntax error.
                test = statement(current_token, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

            }

            if (test == false) {
                // Syntax error. Return.
                return false;
            }
            // pop next value off vector and do it again.

        }
        // legitimate Program, return true.
        return true;
    }

    public static boolean declaration(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) {
        // testing for declaration.  In Rule 2.
        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //   for (int i = 0; i < spaces; i++) {
        //      space_num = space_num + " ";
        //  }
        step = space_num + "<declaration>";
        prog_stack.add(step);
        // Check for type.
        boolean test = type(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
        if (test == false) {
            // bubble failure up.
            String temp;
            temp = (String) prog_stack.pop(); // because not a declaration
            return false;
        }

        if (current[0].equals("IDENT")) // check to see if it is an Identifer.
        {
            //System.out.println("  " + token_lex.firstElement());
            //token_lex.remove(0);
            String temp1 = (String) token_lex.remove(0);
            // For Lab 3
            ((Record) symbol_table.lastElement()).ChangeName(temp1);

            prog_stack.add(space_num + "  " + temp1);
            //token_id.remove(0);
            token_number.remove(0);
            token_val.remove(0);
            current[0] = (String) token_id.remove(0);

            return true; // passes declaration test.

        }

        String temp;
        temp = (String) prog_stack.pop(); // because not a declaration
        return false; // not a declaration.
    }

    public static boolean type(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) {
        // In Rule 3.
        spaces = spaces + 2;
        String step = "";
        String space_num = "";
        //  for (int i = 0; i < spaces; i++) {
        //       space_num = space_num + " ";
        //   }
        step = space_num + "<type>";
        prog_stack.add(step);

        // Going to be simple type or array type.
        boolean test = simple_type(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
        if (test == false) { // see if it is array.
            test = array_type(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
        }
        if (test == false) {
            // pop off type, because it is not true.
            String temp;
            temp = (String) prog_stack.pop(); // because not a type.
            return false;
        } else {
            // System.out.println("  "+token_lex.firstElement()); // Only terminal generatora should print.
            //  token_lex.remove(0);
            //  token_id.remove(0);
            //  token_number.remove(0);
            //  token_val.remove(0);
            return true;
        }
    }

    public static boolean simple_type(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 4
        spaces = spaces + 2;
        String step = "";
        String space_num = "";
        //   for (int i = 0; i < spaces; i++) {
        //        space_num = space_num + " ";
        //    }
        step = space_num + "<simple_type>";
        prog_stack.add(step);

        // either a int, float, or char.
        if (current[0].equals("INT") | current[0].equals("FLOAT") | current[0].equals("CHAR")) {
            //System.out.println("  " + token_lex.firstElement());

            // For lab 3- set type of record =  one of the above.
            Record rec = new Record();
            rec.ChangeType(current[0]);
            rec.ChangeKind("variable");
            //also set the memory.
            if (current[0].equals("INT") | current[0].equals("FLOAT")) {
                // 4 bytes- from 000 to 003 next one would start at 004-007
                if (symbol_table.isEmpty()) {
                    rec.SetAddress(0, 3);
                } else {
                    int tempadd = ((Record) symbol_table.lastElement()).getEndAddress() + 1;
                    rec.SetAddress(tempadd, tempadd + 3);
                }




            }

            if (current[0].equals("CHAR")) {
                if (symbol_table.isEmpty()) {
                    rec.SetAddress(0, 1);
                } else {
                    int tempadd = ((Record) symbol_table.lastElement()).getEndAddress() + 1;
                    rec.SetAddress(tempadd, tempadd + 1);
                }


            }
            symbol_table.add(rec);
            prog_stack.add(space_num + "  " + token_lex.remove(0));
            //token_lex.remove(0);
            //token_id.remove(0);
            token_number.remove(0);
            token_val.remove(0);
            current[0] = (String) token_id.remove(0);
            return true;
        }
        String temp;
        temp = (String) prog_stack.pop(); // because not a simple type.
        return false; // Not a simple type.
    }

    public static boolean array_type(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 5
        // Simply the declaration of an array?
        spaces = spaces + 2;
        String space_num = "";
        // for (int i = 0; i < spaces; i++) {
        //     space_num = space_num + " ";
        // }
        String temp1 = "";
        String step = space_num + "<array_type>";
        prog_stack.add(step);
        boolean test;
        if (current[0].equals("ARRAY")) {
            temp1 = space_num + "  ARRAY";
            prog_stack.add(temp1);
            // go into simple type.
            if (!token_id.isEmpty()) {
                current[0] = (String) token_id.remove(0);
                token_number.remove(0);
                token_val.remove(0);
                token_lex.remove(0);
            }

            test = simple_type(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
            // left parenthese
            if (current[0].equals("LBRACK") && test == true) {
                // change to array type.
                ((Record) symbol_table.lastElement()).ChangeKind("array");
                if (!token_id.isEmpty()) {
                    current[0] = (String) token_id.remove(0);
                    token_number.remove(0);
                    token_val.remove(0);
                }
                //System.out.println(space_num + "  " + token_lex.remove(0));
                prog_stack.add(space_num + "  " + token_lex.remove(0));



                // check for first int lit // or expression that returns integer
                int dim1 = 0, dim2 = -1;
                if (current[0].equals("INTLIT")) {
                    String test1 = "" + token_val.elementAt(0);
                    dim1 = Integer.parseInt(test1);
                    current[8] = test1;
                    current[9] = "-1";
                    test = true;

                } else if (expression(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table)) {
                    test = true;
                    dim1 = Integer.parseInt(current[2]);
                    current[8] = current[2];
                    current[9] = "-1";

                } else {
                    test = false;
                }
                if (test) {
                    if (!token_id.isEmpty()) {
                        current[0] = (String) token_id.remove(0);
                        token_number.remove(0);
                        token_val.remove(0);
                    }
                    //System.out.println(space_num + "  " + token_lex.remove(0)); // same level as LPAREN
                    prog_stack.add(space_num + "  " + token_lex.remove(0));

                    if (current[0].equals("RBRACK") && test) { // Case if  only one array parameter.
                        // create array in record
                        ((Record) symbol_table.lastElement()).arraySetup(dim1, dim2);
                        if (!token_id.isEmpty()) {
                            current[0] = (String) token_id.remove(0);
                            token_number.remove(0);
                            token_val.remove(0);
                            prog_stack.add(space_num + "  " + token_lex.remove(0));
                        } else {
                            //  System.out.println(space_num + "  " + token_lex.remove(0));
                            prog_stack.add(space_num + "  " + token_lex.remove(0));
                            return true;
                        }


                    } else if (test == true && current[0].equals("COMMA")) {
                        // next up is comma, then intlit, Lab 5 said only 2 dim.


                        if (!token_id.isEmpty()) {
                            current[0] = (String) token_id.remove(0);
                            token_number.remove(0);
                            token_val.remove(0);

                        }
                        //System.out.println(space_num + "  " + token_lex.remove(0)); // same level as LPAREN
                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                        // check for intlit next or expression

                        if (current[0].equals("INTLIT")) {
                            String test1 = "" + token_val.elementAt(0);
                            dim2 = Integer.parseInt(test1);
                            current[9] = test1;

                            test = true;

                        } else if (expression(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table)) {
                            test = true;
                            dim2 = Integer.parseInt(current[2]);
                            current[9] = current[2];

                        } else {
                            test = false;
                        }


                        if (test) { // once again could be expression that returns int
                            if (!token_id.isEmpty()) {
                                current[0] = (String) token_id.remove(0);
                                token_number.remove(0);
                                token_val.remove(0);
                            }
                            //System.out.println(space_num + "  " + token_lex.remove(0)); // same level as LPAREN
                            prog_stack.add(space_num + "  " + token_lex.remove(0));


                        }





                        // next should be the ending bracket.
                        if (current[0].equals("RBRACK") && test == true) { //
                             ((Record) symbol_table.lastElement()).arraySetup(dim1, dim2);
                            if (!token_id.isEmpty()) {
                               
                                current[0] = (String) token_id.remove(0);
                                token_number.remove(0);
                                token_val.remove(0);
                                prog_stack.add(space_num + "  " + token_lex.remove(0));
                            } else {
                                //  System.out.println(space_num + "  " + token_lex.remove(0));
                                prog_stack.add(space_num + "  " + token_lex.remove(0));
                                return true;
                            }


                        } else {
                            String temp;
                            temp = (String) prog_stack.pop(); // because not a simple type.
                            return false;
                        }
                    } else {
                        String temp;
                        temp = (String) prog_stack.pop(); // because not a simple type.
                        return false;
                    }

                } else {
                    String temp;
                    temp = (String) prog_stack.pop(); // because not a simple type.
                    return false;
                }
            } else {
                String temp;
                temp = (String) prog_stack.pop(); // because not a simple type.
                return false;

            }
        } else {
            String temp;
            temp = (String) prog_stack.pop(); // because not a simple type.
            return false;

        }

        return true;


    }

    public static boolean statement(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) {
        //Rule 6
        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //   for (int i = 0; i < spaces; i++) {
        //       space_num = space_num + " ";
        //   }
        step = space_num + "<statement>";
        prog_stack.add(step);
        boolean test;
        // Input, output, assignment, if or while.
        test = input_stmt(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

        if (test == false) {
            test = output_stmt(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

        }

        if (test == false) {
            test = assignment_stmt(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

        }
        if (test == false) {
            test = if_stmt(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

        }

        if (test == false) {
            test = while_stmt(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

        }

        if (test == false) {
            String temp;
            temp = (String) prog_stack.pop(); // because not a simple type.
            return false;

        } else {
            return true;
        }



    }

    public static boolean input_stmt(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 7
        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //    for (int i = 0; i < spaces; i++) {
        //        space_num = space_num + " ";
        //    }
        step = space_num + "<input_stmt>";
        prog_stack.add(step);
        boolean test;
        String temp1 = "";

        if (current[0].equals("READ")) {
            temp1 = space_num + "  READ";
            prog_stack.add(temp1);

            if (!token_id.isEmpty()) {
                current[0] = (String) token_id.remove(0);
                token_number.remove(0);
                token_val.remove(0);
                token_lex.remove(0);
            }
            if (current[0].equals("LPAREN")) {
                if (!token_id.isEmpty()) {
                    current[0] = (String) token_id.remove(0);
                    token_number.remove(0);
                    token_val.remove(0);
                }
                //System.out.println(space_num + "  " + token_lex.remove(0));
                prog_stack.add(space_num + "  " + token_lex.remove(0));


                // test for designator.
                String var = (String) token_val.elementAt(0);
                test = designator(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                if (test && current[0].equals("RPAREN")) {
                    String temp = "";
                    String temp2 = "";
                    Scanner in = new Scanner(System.in);
                    int i = -1;
                    while (!var.equalsIgnoreCase(temp)) {
                        i = i + 1;
                        temp = ((Record) symbol_table.elementAt(i)).getName();
                        temp2 = ((Record) symbol_table.elementAt(i)).GetType();

                    }
                    if (temp2.equals("INT")) {
                        try {


                            int answer = in.nextInt();
                            current[1] = Integer.toString(answer);
                            StoreMemory(var, current[1], symbol_table, current);
                        } catch (Exception e) {
                            return false;
                        }

                    } else if (temp2.equals("FLOAT")) {
                        try {


                            double answer = in.nextDouble();
                            current[1] = Double.toString(answer);
                            StoreMemory(var, current[1], symbol_table, current);
                        } catch (Exception e) {
                            return false;
                        }
                    } else if (temp2.equals("CHAR")) {
                        try {


                            String answer = in.nextLine();
                            if (answer.length() == 1) {
                                current[1] = answer;
                                StoreMemory(var, current[1], symbol_table, current);
                            }
                            return false;

                        } catch (Exception e) {
                            return false;
                        }

                    }






                    if (!token_id.isEmpty()) {
                        current[0] = (String) token_id.remove(0);
                        token_number.remove(0);
                        token_val.remove(0);
                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                    } else {
                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                        return true; // end of line!

                    }


                } else {
                    String temp;
                    temp = (String) prog_stack.pop();
                    return false;

                }

            } else {
                String temp;
                temp = (String) prog_stack.pop();
                return false;

            }


        } else {
            String temp;
            temp = (String) prog_stack.pop();
            return false;

        }


        return true;



    }

    public static boolean output_stmt(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 8.




        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //    for (int i = 0; i < spaces; i++) {
        //        space_num = space_num + " ";
        //    }
        step = space_num + "<output_stmt>";
        prog_stack.add(step);
        boolean test;
        String temp1 = "";
        String output = "";
        if (current[0].equals("WRITE")) {
            temp1 = space_num + "  WRITE";
            prog_stack.add(temp1);

            if (!token_id.isEmpty()) {
                current[0] = (String) token_id.remove(0);
                token_number.remove(0);
                token_val.remove(0);
                token_lex.remove(0);
            }

            if (current[0].equals("LPAREN")) {
                if (!token_id.isEmpty()) {
                    current[0] = (String) token_id.remove(0);
                    token_number.remove(0);
                    token_val.remove(0);
                }
                prog_stack.add(space_num + "  " + token_lex.remove(0));

                test = expression(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

                if (current[0].equals("RPAREN") && test) {
                    output = output + current[2];
                    if (!token_id.isEmpty()) {
                        current[0] = (String) token_id.remove(0);
                        token_number.remove(0);
                        token_val.remove(0);
                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                        System.out.println(output);
                    } else {
                        System.out.println(output);
                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                        return true; // end of line!

                    }


                } else if (current[0].equals("COMMA") && test) { // case for repeating expressions.

                    while (current[0].equals("COMMA") && test) {
                        output = output + current[2];
                        if (!token_id.isEmpty()) {

                            current[0] = (String) token_id.remove(0);
                            token_number.remove(0);
                            token_val.remove(0);

                        }
                        //System.out.println(space_num + "  " + token_lex.remove(0)); // same level as LPAREN
                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                        // check for expression
                        test = expression(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                        //if(!current[3].isEmpty()){
                        //    current[2] = current[3];
                        //  }
                    } // end while
                    current[3] = "";


                    // next should be the ending parentheses
                    if (current[0].equals("RPAREN") && test == true) { // Case if  only one array parameter.
                        output = output + current[2];
                        if (!token_id.isEmpty()) {
                            current[0] = (String) token_id.remove(0);
                            token_number.remove(0);
                            token_val.remove(0);
                            prog_stack.add(space_num + "  " + token_lex.remove(0));
                            System.out.println(output);
                        } else {
                            //  System.out.println(space_num + "  " + token_lex.remove(0));
                            prog_stack.add(space_num + "  " + token_lex.remove(0));
                            System.out.println(output);
                            return true;
                        }



                    } else {
                        String temp;
                        temp = (String) prog_stack.pop();
                        return false;

                    }

                } else {
                    String temp;
                    temp = (String) prog_stack.pop();
                    return false;

                }

            } else {
                String temp;
                temp = (String) prog_stack.pop();
                return false;

            }
        } else {
            String temp;
            temp = (String) prog_stack.pop();
            return false;

        }



        return true;
    }

    public static boolean assignment_stmt(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 9.

        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //  for (int i = 0; i < spaces; i++) {
        //      space_num = space_num + " ";
        //   }
        step = space_num + "<assignment_stmt>";
        prog_stack.add(step);
        boolean test;
        String temp1 = "";

        //need designator now.
        String var = (String) token_val.elementAt(0);
        test = designator(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
        // array test
String dim1, dim2;
dim1 = current[8];
dim2 = current[9];
        if (test && current[0].equals("ASSIGN")) {
            if (!token_id.isEmpty()) {
                current[0] = (String) token_id.remove(0);
                token_number.remove(0);
                token_val.remove(0);

            }
            //System.out.println(space_num + "  " + token_lex.remove(0)); // same level as LPAREN
            prog_stack.add(space_num + "  " + token_lex.remove(0));
            // time for expression
            test = expression(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);






        } else {
            String temp;
            temp = (String) prog_stack.pop();
            return false;
        }


        if (test) {
            // expr value stored in current[1].

            // find what record the variable is in and store current[1] there.
            //   current[1] = ((String) prog_stack.lastElement()).trim();
            current[1] = current[2];
            current[8]= dim1;
            current[9] = dim2;
            StoreMemory(var, current[1], symbol_table, current);

            return true;
        } else {
            String temp;
            temp = (String) prog_stack.pop();
            return false;
        }


    }

    public static void StoreMemory(String variable, String value, Vector table, String[] current) {

        // find what memory the value needs to be stored in
        String temp = "";
        int i = -1;
        while (!variable.equalsIgnoreCase(temp)) {
            i = i + 1;
            temp = ((Record) table.elementAt(i)).getName();

        }
        // put value into it.
        ((Record) table.elementAt(i)).ChangeContents(value, current);
        return;
    }

    public static String getMemoryValue(String variable, Vector table, String[] current) {

        // find what memory the value needs to be stored in
        String temp = "";
        int i = -1;
        while (!variable.equalsIgnoreCase(temp)) {
            i = i + 1;
            temp = ((Record) table.elementAt(i)).getName();

        }
        // put value into it.
        return ((String) ((Record) table.elementAt(i)).getContents(current));

    }

    public static boolean if_stmt(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 10

        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //   for (int i = 0; i < spaces; i++) {
        //       space_num = space_num + " ";
        //    }
        step = space_num + "<if_stmt>";
        prog_stack.add(step);
        boolean test;
        String temp1 = "";

        if (current[0].equals("IF")) {
            if (!token_id.isEmpty()) {
                current[0] = (String) token_id.remove(0);
                token_number.remove(0);
                token_val.remove(0);

            }
            //System.out.println(space_num + "  " + token_lex.remove(0)); // same level as LPAREN
            prog_stack.add(space_num + "  " + token_lex.remove(0));
            // now need LPAREN
            if (current[0].equals("LPAREN")) {
                if (!token_id.isEmpty()) {
                    current[0] = (String) token_id.remove(0);
                    token_number.remove(0);
                    token_val.remove(0);
                }
                prog_stack.add(space_num + "  " + token_lex.remove(0));

                test = expression(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

                if (current[0].equals("RPAREN")) {
                    if (!token_id.isEmpty()) {
                        current[0] = (String) token_id.remove(0);
                        token_number.remove(0);
                        token_val.remove(0);

                    }
                    prog_stack.add(space_num + "  " + token_lex.remove(0));

                    // Lab 4.
                    // If current[2] != true, then we need to skip to RBRACE.



                    // check for left brace
                    if (current[0].equals("LBRACE")) {
                        if (!token_id.isEmpty()) {
                            current[0] = (String) token_id.remove(0);
                            token_number.remove(0);
                            token_val.remove(0);

                        }
                        prog_stack.add(space_num + "  " + token_lex.remove(0));

                        if (current[2].equals("false")) {
                            //skipping to RBRACE.
                            // might have nested braces though.
                            int brace_count = 1; // from left brace we just ate.
                            boolean brace_test = false;
                            outer:
                            while (!current[0].equals("RBRACE") && brace_count >= 1) {

                                if (current[0].equals("LBRACE")) {
                                    brace_count = brace_count + 1;

                                }
                                if (current[0].equals("RBRACE")) {
                                    brace_count = brace_count - 1;
                                    if (brace_count == 0) {
                                        break outer;
                                    }

                                }
                                current[0] = (String) token_id.remove(0);

                                token_number.remove(0);
                                token_val.remove(0);
                                token_lex.remove(0);
                            }

                            // then remove RBRACE
                            // break should send you here.
                            if (!token_id.isEmpty()) {


                                current[0] = (String) token_id.remove(0);
                                token_number.remove(0);
                                token_val.remove(0);
                                token_lex.remove(0);
                                return true;
                            } else {
                                token_lex.remove(0);
                                return true;
                            }

                        } else {






                            test = statement(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                            // check for end brace
                            if (current[0].equals("RBRACE") && test == true) {
                                if (!token_id.isEmpty()) {
                                    current[0] = (String) token_id.remove(0);
                                    token_number.remove(0);
                                    token_val.remove(0);
                                    prog_stack.add(space_num + "  " + token_lex.remove(0));
                                } else {
                                    //  System.out.println(space_num + "  " + token_lex.remove(0));
                                    prog_stack.add(space_num + "  " + token_lex.remove(0));
                                    return true;
                                }
                            } else if (test) {
                                // check for more statements
                                // test = statement(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

                                while (test) {
                                    test = statement(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                                    if (current[0].equals("RBRACE")) {
                                        break;
                                    }
                                }

                                // check for right brace.
                                if (current[0].equals("RBRACE") && test == true) {
                                    if (!token_id.isEmpty()) {
                                        current[0] = (String) token_id.remove(0);
                                        token_number.remove(0);
                                        token_val.remove(0);
                                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                                    } else {
                                        //  System.out.println(space_num + "  " + token_lex.remove(0));
                                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                                        return true;
                                    }


                                } else {
                                    String temp;
                                    temp = (String) prog_stack.pop();
                                    return false;
                                }

                            } else {
                                String temp;
                                temp = (String) prog_stack.pop();
                                return false;
                            }
                        } // end for expression value check.
                    } else {
                        String temp;
                        temp = (String) prog_stack.pop();
                        return false;
                    }

                } else {
                    String temp;
                    temp = (String) prog_stack.pop();
                    return false;
                }
            } else {
                String temp;
                temp = (String) prog_stack.pop();
                return false;
            }
        } else {
            String temp;
            temp = (String) prog_stack.pop();
            return false;
        }



        return true;
    }

    public static boolean inner_while(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) {
        // evaulate expression.
        boolean test;
        test = expression(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
        if (current[0].equals("RPAREN")) {
            if (!token_id.isEmpty()) {
                current[0] = (String) token_id.remove(0);
                token_number.remove(0);
                token_val.remove(0);

            }

            token_lex.remove(0);
            //prog_stack.add(space_num + "  " + token_lex.remove(0));
            // check for left brace
            if (current[0].equals("LBRACE")) {
                if (!token_id.isEmpty()) {
                    current[0] = (String) token_id.remove(0);
                    token_number.remove(0);
                    token_val.remove(0);

                }

                token_lex.remove(0);
                //prog_stack.add(space_num + "  " + token_lex.remove(0));
                if (current[2].equals("false")) {
                    //skipping to RBRACE.
                    // might have nested braces though.
                    int brace_count = 1; // from left brace we just ate.
                    boolean brace_test = false;
                    outer:
                    while (!current[0].equals("RBRACE") && brace_count >= 1) {

                        if (current[0].equals("LBRACE")) {
                            brace_count = brace_count + 1;

                        }
                        if (current[0].equals("RBRACE")) {
                            brace_count = brace_count - 1;
                            if (brace_count == 0) {
                                break outer;
                            }

                        }
                        current[0] = (String) token_id.remove(0);

                        token_number.remove(0);
                        token_val.remove(0);
                        token_lex.remove(0);
                    }

                    // then remove RBRACE
                    // break should send you here.
                    if (!token_id.isEmpty()) {


                        current[0] = (String) token_id.remove(0);
                        token_number.remove(0);
                        token_val.remove(0);
                        token_lex.remove(0);
                        return false;
                    } else {
                        token_lex.remove(0);
                        return false;
                    }

                } else { // if expression is true.
                    boolean expr = true;



                    test = statement(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                    // check for end brace
                    if (current[0].equals("RBRACE") && test == true) {
                        if (!token_id.isEmpty()) {
                            current[0] = (String) token_id.remove(0);
                            token_number.remove(0);
                            token_val.remove(0);
                            //prog_stack.add(space_num + "  " + token_lex.remove(0));
                            token_lex.remove(0);

                            return true;
                        } else {
                            //  System.out.println(space_num + "  " + token_lex.remove(0));
                            // prog_stack.add(space_num + "  " + token_lex.remove(0));
                            // return true; // cannot return true yet.
                            token_lex.remove(0);
                            return true; // EOF
                        }
                    } else if (test) {
                        // check for more statements
                        //test = statement(current, prog_stack, token_lex, token_id, token_number, token_val, spaces);

                        while (test) {
                            test = statement(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                            if (current[0].equals("RBRACE")) {
                                break;
                            }
                        }

                        // check for right brace.
                        if (current[0].equals("RBRACE") && test == true) {
                            if (!token_id.isEmpty()) {
                                current[0] = (String) token_id.remove(0);
                                token_number.remove(0);
                                token_val.remove(0);
                                token_lex.remove(0);


                                return true;


                            } else {
                                //  System.out.println(space_num + "  " + token_lex.remove(0));
                                token_lex.remove(0);
                                return true;
                            }


                        } else {
                            String temp;
                            //  temp = (String) prog_stack.pop();
                            return false;
                        }

                    } else {
                        String temp;
                        //  temp = (String) prog_stack.pop();
                        return false;
                    }
                }
            } else {
                String temp;
                // temp = (String) prog_stack.pop();
                return false;
            }

        } else {
            String temp;
            // temp = (String) prog_stack.pop();
            return false;
        }



    }

    public static boolean while_stmt(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 11.

        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //   for (int i = 0; i < spaces; i++) {
        //      space_num = space_num + " ";
        //   }
        step = space_num + "<while_stmt>";
        prog_stack.add(step);
        boolean test, test1 = true;
        String temp1 = "";

        if (current[0].equals("WHILE")) {
            if (!token_id.isEmpty()) {
                current[0] = (String) token_id.remove(0);
                token_number.remove(0);
                token_val.remove(0);

            }
            //System.out.println(space_num + "  " + token_lex.remove(0)); // same level as LPAREN
            prog_stack.add(space_num + "  " + token_lex.remove(0));
            // now need LPAREN
            if (current[0].equals("LPAREN")) {
                if (!token_id.isEmpty()) {
                    current[0] = (String) token_id.remove(0);
                    token_number.remove(0);
                    token_val.remove(0);
                }
                prog_stack.add(space_num + "  " + token_lex.remove(0));

                // Before we test for expression we store  the rest of the program
                // into new vectors.
                // Each time while loop runs, original vectors are replaced by the copies, until while exits.

                Vector lex_copy, id_copy, val_copy, num_copy;
                String copy_current = current[0];
                lex_copy = (Vector) token_lex.clone();
                id_copy = (Vector) token_id.clone();
                num_copy = (Vector) token_number.clone();
                val_copy = (Vector) token_val.clone();
                boolean inner_loop = true;
                // Shallow copy is alright, because the content of each string will not change.



                test = expression(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

                if (current[0].equals("RPAREN")) {
                    if (!token_id.isEmpty()) {
                        current[0] = (String) token_id.remove(0);
                        token_number.remove(0);
                        token_val.remove(0);

                    }
                    prog_stack.add(space_num + "  " + token_lex.remove(0));
                    // check for left brace
                    if (current[0].equals("LBRACE")) {
                        if (!token_id.isEmpty()) {
                            current[0] = (String) token_id.remove(0);
                            token_number.remove(0);
                            token_val.remove(0);

                        }
                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                        if (current[2].equals("false")) {
                            //skipping to RBRACE.
                            // might have nested braces though.
                            int brace_count = 1; // from left brace we just ate.
                            boolean brace_test = false;
                            outer:
                            while (!current[0].equals("RBRACE") && brace_count >= 1) {

                                if (current[0].equals("LBRACE")) {
                                    brace_count = brace_count + 1;

                                }
                                if (current[0].equals("RBRACE")) {
                                    brace_count = brace_count - 1;
                                    if (brace_count == 0) {
                                        break outer;
                                    }

                                }
                                current[0] = (String) token_id.remove(0);

                                token_number.remove(0);
                                token_val.remove(0);
                                token_lex.remove(0);
                            }

                            // then remove RBRACE
                            // break should send you here.
                            if (!token_id.isEmpty()) {


                                current[0] = (String) token_id.remove(0);
                                token_number.remove(0);
                                token_val.remove(0);
                                token_lex.remove(0);
                                return true;
                            } else {
                                token_lex.remove(0);
                                return true;
                            }

                        } else { // if expression is true.
                            boolean expr = true;



                            test = statement(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                            // check for end brace
                            if (current[0].equals("RBRACE") && test == true) {
                                if (!token_id.isEmpty()) {
                                    current[0] = (String) token_id.remove(0);
                                    token_number.remove(0);
                                    token_val.remove(0);
                                    prog_stack.add(space_num + "  " + token_lex.remove(0));
                                    //return true; // cannot return true yet.
                                    while (inner_loop) {
                                        // recopy vectors.
                                        current[0] = copy_current;
                                        token_lex = (Vector) lex_copy.clone();
                                        token_id = (Vector) id_copy.clone();
                                        token_number = (Vector) num_copy.clone();
                                        token_val = (Vector) val_copy.clone();
                                        // inner_while function will evaluate the expression, then run till the Right Brace, then return.
                                        inner_loop = inner_while(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                                    }
                                    return true; // already took care of RBRACE?
                                } else {
                                    //  System.out.println(space_num + "  " + token_lex.remove(0));
                                    prog_stack.add(space_num + "  " + token_lex.remove(0));
                                    // return true; // cannot return true yet.
                                    while (inner_loop) {
                                        // recopy vectors.
                                        current[0] = copy_current;
                                        token_lex = (Vector) lex_copy.clone();
                                        token_id = (Vector) id_copy.clone();
                                        token_number = (Vector) num_copy.clone();
                                        token_val = (Vector) val_copy.clone();
                                        // inner_while function will evaluate the expression, then run till the Right Brace, then return.
                                        inner_loop = inner_while(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                                    }
                                    return true;
                                }
                            } else if (test) {
                                // check for more statements
                                //test = statement(current, prog_stack, token_lex, token_id, token_number, token_val, spaces);

                                while (test) {
                                    test = statement(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                                    if (current[0].equals("RBRACE")) {
                                        break;
                                    }
                                }

                                // check for right brace.
                                if (current[0].equals("RBRACE") && test == true) {
                                    if (!token_id.isEmpty()) {
                                        current[0] = (String) token_id.remove(0);
                                        token_number.remove(0);
                                        token_val.remove(0);
                                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                                        //return true; // for syntax this return true was ok, but we cannot return yet.
                                        while (inner_loop) {
                                            // recopy vectors.
                                            current[0] = copy_current;
                                            token_lex = (Vector) lex_copy.clone();
                                            token_id = (Vector) id_copy.clone();
                                            token_number = (Vector) num_copy.clone();
                                            token_val = (Vector) val_copy.clone();
                                            // inner_while function will evaluate the expression, then run till the Right Brace, then return.
                                            inner_loop = inner_while(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                                        }
                                        return true;


                                    } else {
                                        //  System.out.println(space_num + "  " + token_lex.remove(0));
                                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                                        // return true; // cannot return yet.
                                        while (inner_loop) {
                                            // recopy vectors.
                                            current[0] = copy_current;
                                            token_lex = (Vector) lex_copy.clone();
                                            token_id = (Vector) id_copy.clone();
                                            token_number = (Vector) num_copy.clone();
                                            token_val = (Vector) val_copy.clone();
                                            // inner_while function will evaluate the expression, then run till the Right Brace, then return.
                                            inner_loop = inner_while(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                                        }
                                        return true;
                                    }


                                } else {
                                    String temp;
                                    temp = (String) prog_stack.pop();
                                    return false;
                                }

                            } else {
                                String temp;
                                temp = (String) prog_stack.pop();
                                return false;
                            }
                        }
                    } else {
                        String temp;
                        temp = (String) prog_stack.pop();
                        return false;
                    }

                } else {
                    String temp;
                    temp = (String) prog_stack.pop();
                    return false;
                }
            } else {
                String temp;
                temp = (String) prog_stack.pop();
                return false;
            }
        } else {
            String temp;
            temp = (String) prog_stack.pop();
            return false;
        }




    }

    public static boolean designator(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 12
        // construction is IDENT followed by nothing
        // or  followed by [ expression, expression, expression........]



        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //   for (int i = 0; i < spaces; i++) {
        //       space_num = space_num + " ";
        //     }
        step = space_num + "<designator>";
        prog_stack.add(step);
        boolean test;
        String temp1 = "";
        String temp2 = "";
        // check for ident
        if (current[0].equals("IDENT")) {
            if (!token_id.isEmpty()) {
                current[0] = (String) token_id.remove(0);
                token_number.remove(0);
                temp2 = (String) token_val.remove(0);
                token_lex.remove(0);
                if (!current[0].equals("LBRACK")) {


                    current[2] = getMemoryValue(temp2, symbol_table, current);

                }


            }
            //System.out.println(space_num + "  " + token_lex.remove(0)); // same level as LPAREN
            // prog_stack.add(space_num + "  " + token_lex.remove(0));

            // check for left brace
            if (current[0].equals("LBRACK")) {
                if (!token_id.isEmpty()) {
                    current[0] = (String) token_id.remove(0);
                    token_number.remove(0);
                    token_val.remove(0);

                }
                prog_stack.add(space_num + "  " + token_lex.remove(0));

                test = expression(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                temp1 = current[2];
                // check for end brace
                if (current[0].equals("RBRACK") && test == true) {
                    current[8] = temp1;
                    current[9] = "-1";
                    current[2] = getMemoryValue(temp2, symbol_table, current);
                    if (!token_id.isEmpty()) {
                        current[0] = (String) token_id.remove(0);
                        token_number.remove(0);
                        token_val.remove(0);
                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                    } else {
                        //  System.out.println(space_num + "  " + token_lex.remove(0));
                        prog_stack.add(space_num + "  " + token_lex.remove(0));

                        return true;
                    }
                } else if (test && current[0].equals("COMMA")) {

                    // check for more statements
                    current[0] = (String) token_id.remove(0);
                    token_number.remove(0);
                    token_val.remove(0);
                    token_lex.remove(0);



                    // check for expression
                    test = expression(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);


                    // check for right bracket.
                    if (current[0].equals("RBRACK") && test == true) {
                        current[8] = temp1;
                        current[9] = current[2];
                        current[2] = getMemoryValue(temp2, symbol_table, current);
                        if (!token_id.isEmpty()) {
                            current[0] = (String) token_id.remove(0);
                            token_number.remove(0);
                            token_val.remove(0);
                            prog_stack.add(space_num + "  " + token_lex.remove(0));
                        } else {
                            //  System.out.println(space_num + "  " + token_lex.remove(0));
                            prog_stack.add(space_num + "  " + token_lex.remove(0));
                            return true;
                        }


                    } else {
                        String temp;
                        temp = (String) prog_stack.pop();
                        return false;
                    }

                } else {
                    String temp;
                    temp = (String) prog_stack.pop();
                    return false;
                }
            } else {

                // if did not have left brace, then it was just an IDENT, which is fine.

                return true;
            }




        } else {
            String temp;
            temp = (String) prog_stack.pop();
            return false;
        }


        return true;
    }

    public static boolean expression(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 13

        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //for (int i = 0; i < spaces; i++) {
        //     space_num = space_num + " ";
        // }
        step = space_num + "<expression>";
        prog_stack.add(step);
        boolean test;
        String temp1 = "";

        // For lab 3 add parethese
        // first record in symbol table has the expression vector.


        // construction is simple expression
        // or
        // simple expression followed by relational op followed by simple xpr.

        // check for simple_expr
        test = simple_expr(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

        if (test) {
            String b = "";
            // get value of simple_expr.
            // String first =  ((String) prog_stack.lastElement()).trim();
            // first = getMemoryValue(first,symbol_table);

            // current[2] should have the value of the designator.
            String a = current[2];
            if (!token_lex.isEmpty()) {
                b = (String) token_lex.elementAt(0);
            }
            test = relational_op(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
            if (test) {

                test = simple_expr(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

                if (!test) {
                    return false;
                }
                String c = current[2];
                boolean e = false;
                int ia = 0, ic = 0;
                double fa = 0.0, fc = 0.0;
                try {
                    ia = Integer.parseInt(a);
                    ic = Integer.parseInt(c);


                    if (b.equals("==")) {
                        if (ia == ic) {
                            e = true;
                        } else {
                            e = false;
                        }
                    }
                    if (b.equals("!=")) {
                        if (ia != ic) {
                            e = true;
                        } else {
                            e = false;
                        }
                    }
                    if (b.equals("<")) {
                        if (ia < ic) {
                            e = true;
                        } else {
                            e = false;
                        }
                    }
                    if (b.equals("<=")) {
                        if (ia <= ic) {
                            e = true;
                        } else {
                            e = false;
                        }
                    }
                    if (b.equals(">")) {
                        if (ia > ic) {
                            e = true;
                        } else {
                            e = false;
                        }
                    }
                    if (b.equals(">=")) {
                        if (ia >= ic) {
                            e = true;
                        } else {
                            e = false;
                        }
                    }


                } catch (Exception x) {
                    // its a float
                    try {
                        fa = Double.parseDouble(a);
                        fc = Double.parseDouble(c);

                        if (b.equals("==")) {
                            if (fa == fc) {
                                e = true;
                            } else {
                                e = false;
                            }
                        }

                        if (b.equals("!=")) {
                            if (fa != fc) {
                                e = true;
                            } else {
                                e = false;
                            }
                        }
                        if (b.equals(">")) {
                            if (fa > fc) {
                                e = true;
                            } else {
                                e = false;
                            }
                        }
                        if (b.equals(">=")) {
                            if (fa >= fc) {
                                e = true;
                            } else {
                                e = false;
                            }
                        }
                        if (b.equals("<=")) {
                            if (fa <= fc) {
                                e = true;
                            } else {
                                e = false;
                            }
                        }
                        if (b.equals("<")) {
                            if (fa < fc) {
                                e = true;
                            } else {
                                e = false;
                            }
                        }

                    } catch (Exception t) {
                    }
                }
                current[2] = Boolean.toString(e);
                ((Record) symbol_table.firstElement()).StoreExpr("( " + a + " " + b + " " + c + " )" + " = " + e);


            } else {



                return true;
            }


        } else {
            String temp;
            temp = (String) prog_stack.pop();
            return false;
        }






        return true;
    }

    public static boolean relational_op(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 14
        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //for (int i = 0; i < spaces; i++) {
        //     space_num = space_num + " ";
        //  }
        step = space_num + "<relational_op>";
        prog_stack.add(step);
        boolean test;
        String temp1 = "";

        // either a “==” | “!=” | “<” | “<=” | “>” | “>=”

//         String[] tok = {"LPAREN", "RPAREN", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "COMMA",
//                "NEG", "PLUS", "MINUS", "TIMES", "DIV", "IDIV", "MOD", "ASSIGN", "EQ", "NE", "LT", "LE", "GT",
//                "GE", "AND",
//                "OR", "NOT", "ARRAY", "CHAR", "FLOAT", "IF", "INT", "READ",
//                "WHILE", "WRITE", "IDENT", "INTLIT", "FLOLIT", "CHRLIT", "STRLIT"};


        if (current[0].equals("EQ") | current[0].equals("NE") | current[0].equals("LT")
                | current[0].equals("LE") | current[0].equals("GT") | current[0].equals("GE")) {
            //System.out.println("  " + token_lex.firstElement());
            prog_stack.add(space_num + "  " + token_lex.remove(0));
            //token_lex.remove(0);
            //token_id.remove(0);
            token_number.remove(0);
            token_val.remove(0);
            current[0] = (String) token_id.remove(0);
            return true;
        }
        String temp;
        temp = (String) prog_stack.pop(); // because not a simple type.
        return false; // Not a simple type.
    }

    public static boolean simple_expr(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 15
        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //   for (int i = 0; i < spaces; i++) {
        //        space_num = space_num + " ";
        //    }
        step = space_num + "<simple_expr>";
        prog_stack.add(step);
        boolean test;
        String temp1 = "";

        // 0 or 1 unary opt
        // followed by term
        // with a loop of add_op and term


        test = unary_op(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
        // You can have it all, my empire of dust.
        test = term(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
        if (test) {
            String a = current[2];
            String b = "";
            String c = "";
            int k = 0;
            Vector ex = new Vector<String>();
            int total = 0;
            double tot = 0;
            ex.add(a);
            while (test) {
                if (k == 1) {
                    ex.add(c); // makes sure bottom term executed at least once.
                }
                if (!token_lex.isEmpty()) {
                    b = (String) token_lex.firstElement();
                }
                test = add_op(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

                if (!test) {  // compute what this all equals and store in current[2].
                    // try to grab 3 elements first to add together.
                    temp1 = "( ";
                    String tem = (String) ex.firstElement();
                    ex.remove(0);
                    if (ex.isEmpty()) {
                        current[2] = tem;
                        return true;
                    }

// if not, then need sign.

                    String sig = (String) ex.firstElement();
                    ex.remove(0);
// 3rd number

                    String tem_end = (String) ex.firstElement();
                    ex.remove(0);
                    temp1 = temp1 + " " + tem;
                    temp1 = temp1 + " " + sig;
                    temp1 = temp1 + " " + tem_end;
                    temp1 = temp1 + " )";
                    try {
                        int temp = Integer.parseInt(tem);
                        int temp_end = Integer.parseInt(tem_end);
                        if (sig.equals("+")) {
                            total = temp + temp_end;
                        }
                        if (sig.equals("-")) {
                            total = temp - temp_end;
                        }
                        if (sig.equals("|")) {
                            total = temp | temp_end;
                        }





                    } catch (Exception xt) {
//double code.
                        try {
                            double temp = Double.parseDouble(tem);
                            double temp_end = Double.parseDouble(tem_end);

                            if (sig.equals("+")) {
                                tot = temp + temp_end;
                            }
                            if (sig.equals("-")) {
                                tot = temp - temp_end;
                            }
                            if (sig.equals("|")) {
                                tot = 0;
                            }


                        } catch (Exception txt) {
                        }

                    } // end double
                    while (!ex.isEmpty()) {
                        // grab Sign Value and Compute
                        sig = (String) ex.firstElement();
                        ex.remove(0);
                        tem = (String) ex.firstElement();
                        ex.remove(0);
                        temp1 = "( " + temp1;
                        temp1 = temp1 + " " + sig;
                        temp1 = temp1 + " " + tem;
                        temp1 = temp1 + " )";

                        try {
                            int tz = Integer.parseInt(tem);

                            if (sig.equals("+")) {
                                total = total + tz;
                            }
                            if (sig.equals("-")) {
                                total = total - tz;
                            }
                            if (sig.equals("|")) {
                                total = total | tz;
                            }

                        } catch (Exception xkcd) {
                            //double
                            Double temp = Double.parseDouble(tem);
                            if (sig.equals("+")) {
                                tot = tot + temp;
                            }
                            if (sig.equals("-")) {
                                tot = tot - temp;
                            }
                            if (sig.equals("|")) {
                                // cant apply to doubles.
                            }
                        }
                    }
                    //temp1 = temp1 + " )";

                    if (total > tot) {
                        current[2] = Integer.toString(total);
                        ((Record) symbol_table.firstElement()).StoreExpr(temp1 + " = " + total);
                    } else {
                        current[2] = Double.toString(tot);
                        ((Record) symbol_table.firstElement()).StoreExpr(temp1 + " = " + tot);
                    }
                    return true;
                }
                ex.add(b);
                test = term(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                c = current[2];
                k = 1;
            }

        } else {
            String temp;
            temp = (String) prog_stack.pop();
            return false;

        }


        return true;
    }

    public static boolean unary_op(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 16
        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //  for (int i = 0; i < spaces; i++) {
        //      space_num = space_num + " ";
        //   }
        step = space_num + "<unary_op>";
        prog_stack.add(step);
        boolean test;
        String temp1 = "";

        if (current[0].equals("NEG")) {
            prog_stack.add(space_num + "  " + token_lex.remove(0));
            //token_lex.remove(0);
            //token_id.remove(0);
            token_number.remove(0);
            token_val.remove(0);
            current[0] = (String) token_id.remove(0);


        } else {
            String temp;
            temp = (String) prog_stack.pop();
            return false;
        }


        return true;
    }

    public static boolean add_op(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 17
        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //   for (int i = 0; i < spaces; i++) {
        //        space_num = space_num + " ";
        //    }
        step = space_num + "<add_op>";
        prog_stack.add(step);
        boolean test;
        String temp1 = "";

        if (current[0].equals("PLUS") | current[0].equals("MINUS") | current[0].equals("OR")) {
            //System.out.println("  " + token_lex.firstElement());
            prog_stack.add(space_num + "  " + token_lex.remove(0));
            //token_lex.remove(0);
            //token_id.remove(0);
            token_number.remove(0);
            token_val.remove(0);
            current[0] = (String) token_id.remove(0);
            return true;
        }
        String temp;
        temp = (String) prog_stack.pop(); // because not a simple type.
        return false; // Not a simple type.
    }

    public static boolean term(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 18
        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //    for (int i = 0; i < spaces; i++) {
        //       space_num = space_num + " ";
        //   }
        step = space_num + "<term>";
        prog_stack.add(step);
        boolean test, mult = false, factor;
        String temp1 = "";

        // term is
        // factor
        // followed by 0 or more   mult_op factor

        // check for factor.
        test = factor(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
        int count = 0;
        if (test) {

            Vector ex = new Vector<String>();
            int total = 0;
            double tot = 0;
            String b = "";
            String c = "";
            ex.add(current[2]);
            while (test) {
                if (count > 0) {
                    ex.add(c);
                }
                count++;
                if (!token_lex.isEmpty()) {


                    b = (String) token_lex.firstElement();
                }
                mult = mult_op(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                if (!mult) {

                    break; // Not a simple type.
                }
                ex.add(b);

                test = factor(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
                c = current[2];

            }

            if (count == 1 && !test && !factor(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table)) {
                // there is nothing, so it is just a factor, which is alright.



                return true;
            }
            if (!mult && test) {
                // loop ended- factor was last, but no mult opt.
                temp1 = "( ";
                String tem = (String) ex.firstElement();
                ex.remove(0);

                if (ex.isEmpty()) {
                    current[2] = tem;
                    return true;
                }


// if not, then need sign.

                String sig = (String) ex.firstElement();
                ex.remove(0);
// 3rd number

                String tem_end = (String) ex.firstElement();
                ex.remove(0);
                temp1 = temp1 + " " + tem;
                temp1 = temp1 + " " + sig;
                temp1 = temp1 + " " + tem_end;
                temp1 = temp1 + " )";

                try {
                    int temp = Integer.parseInt(tem);
                    int temp_end = Integer.parseInt(tem_end);
                    if (sig.equals("*")) {
                        total = temp * temp_end;
                    }
                    if (sig.equals("/")) {
                        total = temp / temp_end;
                    }
                    if (sig.equals("//")) {
                        total = temp / temp_end;
                    }

                    if (sig.equals("%")) {
                        total = temp % temp_end;
                    }
                    if (sig.equals("&")) {
                        total = temp & temp_end;
                    }





                } catch (Exception xt) {
//double code.
                    try {
                        double temp = Double.parseDouble(tem);
                        double temp_end = Double.parseDouble(tem_end);

                        if (sig.equals("*")) {
                            tot = temp * temp_end;
                        }
                        if (sig.equals("/")) {
                            tot = temp / temp_end;
                        }
                        if (sig.equals("//")) {
                            tot = (int) temp / (int) temp_end;
                        }
                        if (sig.equals("%")) {
                            tot = temp % temp_end;
                        }
                        if (sig.equals("&")) {
                            tot = 0;
                        }


                    } catch (Exception txt) {
                    }

                } // end double
                while (!ex.isEmpty()) {
                    // grab Sign Value and Compute
                    sig = (String) ex.firstElement();
                    ex.remove(0);
                    tem = (String) ex.firstElement();
                    ex.remove(0);
                    temp1 = "( " + temp1;
                    temp1 = temp1 + " " + sig;
                    temp1 = temp1 + " " + tem;
                    temp1 = temp1 + " )";

                    try {
                        int tz = Integer.parseInt(tem);

                        if (sig.equals("*")) {
                            total = total * tz;
                        }
                        if (sig.equals("/")) {
                            total = total / tz;
                        }
                        if (sig.equals("//")) {
                            total = total / tz;
                        }
                        if (sig.equals("%")) {
                            total = total % tz;
                        }
                        if (sig.equals("&")) {
                            total = total & tz;
                        }


                    } catch (Exception xkcd) {
                        //double
                        Double temp = Double.parseDouble(tem);
                        if (sig.equals("*")) {
                            tot = tot * temp;
                        }
                        if (sig.equals("/")) {
                            tot = tot / temp;
                        }
                        if (sig.equals("//")) {

                            tot = tot / temp;
                        }
                        if (sig.equals("%")) {
                            tot = tot % temp;
                        }
                        if (sig.equals("&")) {
                            // cant do it.
                        }


                    }
                }


                if (total > tot) {
                    current[2] = Integer.toString(total);
                    ((Record) symbol_table.firstElement()).StoreExpr(temp1 + " = " + total);
                } else {
                    current[2] = Double.toString(tot);
                    ((Record) symbol_table.firstElement()).StoreExpr(temp1 + " = " + tot);
                }

                return true;

            }

            if (mult && !test) {
                // loop ended- factor was last, but no mult opt.
                String temp;

                temp = (String) prog_stack.pop(); // because not a simple type.

                return false;

            }



        } else {
            String temp;
            temp = (String) prog_stack.pop(); // because not a simple type.
            return false; // Not a simple type.
        }





        return true;
    }

    public static boolean mult_op(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 19
        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //  for (int i = 0; i < spaces; i++) {
        //       space_num = space_num + " ";
        //    }
        step = space_num + "<mult_op>";
        prog_stack.add(step);
        boolean test;
        String temp1 = "";
        //         String[] tok = {"LPAREN", "RPAREN", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "COMMA",
//                "NEG", "PLUS", "MINUS", "TIMES", "DIV", "IDIV", "MOD", "ASSIGN", "EQ", "NE", "LT", "LE", "GT",
//                "GE", "AND",
//                "OR", "NOT", "ARRAY", "CHAR", "FLOAT", "IF", "INT", "READ",
//                "WHILE", "WRITE", "IDENT", "INTLIT", "FLOLIT", "CHRLIT", "STRLIT"};


        if (current[0].equals("TIMES") | current[0].equals("DIV") | current[0].equals("IDIV") | current[0].equals("MOD") | current[0].equals("AND")) {
            //System.out.println("  " + token_lex.firstElement());
            prog_stack.add(space_num + "  " + token_lex.remove(0));
            //token_lex.remove(0);
            //token_id.remove(0);
            token_number.remove(0);
            token_val.remove(0);
            current[0] = (String) token_id.remove(0);
            return true;
        }
        String temp;
        temp = (String) prog_stack.pop(); // because not a simple type.
        return false; // Not a simple type.
    }

    public static boolean factor(String[] current, Stack prog_stack, Vector token_lex, Vector token_id, Vector token_number, Vector token_val, int spaces, Vector symbol_table) { // Rule 20
        spaces = spaces + 2; // Indentation.
        String step = "";
        String space_num = "";
        //   for (int i = 0; i < spaces; i++) {
        //        space_num = space_num + " ";
        //     }
        step = space_num + "<factor>";
        prog_stack.add(step);
        boolean test;
        String temp1 = "";

        //         String[] tok = {"LPAREN", "RPAREN", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "COMMA",
//                "NEG", "PLUS", "MINUS", "TIMES", "DIV", "IDIV", "MOD", "ASSIGN", "EQ", "NE", "LT", "LE", "GT",
//                "GE", "AND",
//                "OR", "NOT", "ARRAY", "CHAR", "FLOAT", "IF", "INT", "READ",
//                "WHILE", "WRITE", "IDENT", "INTLIT", "FLOLIT", "CHRLIT", "STRLIT"};


        if (current[0].equals("INTLIT") | current[0].equals("FLOLIT") | current[0].equals("CHRLIT") | current[0].equals("STRLIT")) {
            //System.out.println("  " + token_lex.firstElement());
            // prog_stack.add(space_num + "  " + token_lex.remove(0));
            //token_lex.remove(0);
            //token_id.remove(0);
            // token_number.remove(0);
            //  token_val.remove(0);
            //  current[0] = (String) token_id.remove(0);
            //  return true;

            if (!token_id.isEmpty()) {
                current[0] = (String) token_id.remove(0);
                token_number.remove(0);
                String test1 = "" + token_val.elementAt(0);
                current[2] = test1;
                token_val.remove(0);

                prog_stack.add(space_num + "  " + token_lex.remove(0));

                return true;
            } else {
                //  System.out.println(space_num + "  " + token_lex.remove(0));
                current[2] = "" + token_lex.elementAt(0);
                prog_stack.add(space_num + "  " + token_lex.remove(0));
                return true;
            }


        }
        String var = (String) token_val.elementAt(0);
        if (designator(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table)) {
            current[2] = getMemoryValue(var, symbol_table, current);
            current[3] = current[2];
            return true;
        }

        if (current[0].equals("LPAREN")) {
            prog_stack.add(space_num + "  " + token_lex.remove(0));
            //token_lex.remove(0);
            //token_id.remove(0);
            token_number.remove(0);
            token_val.remove(0);
            current[0] = (String) token_id.remove(0);


            test = expression(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);

            if (test) {

                if (current[0].equals("RPAREN")) {
                    if (!token_id.isEmpty()) {
                        current[0] = (String) token_id.remove(0);
                        token_number.remove(0);
                        token_val.remove(0);
                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                        return true;
                    } else {
                        //  System.out.println(space_num + "  " + token_lex.remove(0));
                        prog_stack.add(space_num + "  " + token_lex.remove(0));
                        return true;
                    }

                } else {
                    String temp;
                    temp = (String) prog_stack.pop(); // because not a simple type.
                    return false; // Not a simple type.
                }

            } else {
                String temp;
                temp = (String) prog_stack.pop(); // because not a simple type.
                return false; // Not a simple type.
            }

        } else if (current[0].equals("NOT")) {

            test = factor(current, prog_stack, token_lex, token_id, token_number, token_val, spaces, symbol_table);
            if (test) {
                return true;
            }
        } else {
            String temp;
            temp = (String) prog_stack.pop(); // because not a simple type.
            return false; // Not a simple type.

        }







        return true;
    }

    public static void Tokenize(String line, Vector tokens) {
        StringTokenizer str = new StringTokenizer(line);
        while (str.hasMoreTokens()) {
            tokens.add(str.nextToken());
        }
        return;

    }

    public static void write_Token(Vector tokens, Vector token_lex, Vector token_id, Vector token_number, Vector token_val) {
        try {
            // For lexical analyzer output.
            BufferedWriter out = new BufferedWriter(new FileWriter("tokens_out.txt"));
            out.write("Lexeme\t\tToken\t\tToken #\t\tValue");
            out.newLine();
            String[] sym = {"(", ")", "[", "]", "{", "}", ",", "_", "+", "-", "*", "/", "//", "%", "=", "==", "!=", "<", "<=", ">", ">=", "&", "|", "~", "array",
                "char", "float", "if", "int", "read", "while", "write"};
            String[] tok = {"LPAREN", "RPAREN", "LBRACK", "RBRACK", "LBRACE", "RBRACE", "COMMA",
                "NEG", "PLUS", "MINUS", "TIMES", "DIV", "IDIV", "MOD", "ASSIGN", "EQ", "NE", "LT", "LE", "GT",
                "GE", "AND",
                "OR", "NOT", "ARRAY", "CHAR", "FLOAT", "IF", "INT", "READ",
                "WHILE", "WRITE", "IDENT", "INTLIT", "FLOLIT", "CHRLIT", "STRLIT"};
            int found = 0;
            int expect_ident = 0; // Flag for expected identifier.
            String lexeme;
            while (!tokens.isEmpty()) {
                found = 0; // need to reset flag after every cycle.
                String element;
                element = tokens.firstElement().toString();

                // Checks for String Literal First.
                if (element.substring(0, 1).equals("\"")) {
                    if (element.length() > 1 && element.substring(element.length() - 1, element.length()).equals("\"")) { // Element is complete string literal. Length check prevents  length -1 from failing sometimes.
                        lexeme = element;
                        //System.out.println(lexeme+ "\t\t"+tok[36]+"\t\t37\t\t"+lexeme.substring(1,lexeme.length()-1)); // cuts out quotations for value.
                        out.write(lexeme + "\t\t" + tok[36] + "\t\t37\t\t" + lexeme.substring(1, lexeme.length() - 1));
                        out.newLine();
                        token_lex.add(lexeme);
                        token_id.add(tok[36]);
                        token_number.add("37");
                        token_val.add(lexeme.substring(1, lexeme.length() - 1));
                        found = 1;
                    }
                    // Keep reading tokens assuming that they are a part of the string.
                    if (found == 0) {
                        tokens.remove(0);
                        lexeme = element;
                        while (!tokens.isEmpty()) // keep reading searching for end quote.
                        {
                            element = tokens.firstElement().toString();
                            if (element.equals("\"") || (element.substring(element.length() - 1, element.length()).equals("\""))) {
                                lexeme = lexeme + " " + element;
                                // System.out.println(lexeme+ "\t\t"+tok[36]+"\t\t37\t\t"+lexeme.substring(1,lexeme.length()-1)); // cuts out quotations for value.
                                out.write(lexeme + "\t\t" + tok[36] + "\t\t37\t\t" + lexeme.substring(1, lexeme.length() - 1));
                                out.newLine();
                                token_lex.add(lexeme);
                                token_id.add(tok[36]);
                                token_number.add("37");
                                token_val.add(lexeme.substring(1, lexeme.length() - 1));
                                found = 1;
                                break;
                            } else {
                                lexeme = lexeme + " " + element;
                                tokens.remove(0);
                            }

                        } // end while
                        if (tokens.isEmpty()) {
                            //System.out.println(lexeme+"\t\t**unrecognized lexeme\t\t0");
                            out.write(lexeme + "\t\t**unrecognized lexeme\t\t0");
                            out.newLine();
                            return; // No more tokens to process. End quote was never reached.
                        }
                    }

                } // end if

                // Character literal Check.
                if (element.substring(0, 1).equals("\'")) {

                    if ((element.length() == 3 && element.substring(2, 3).equals("\'"))) { // Need to check length to prevent failure if length is less than 3
                        lexeme = element;
                        // System.out.println(lexeme+"\t\t"+tok[35]+"\t\t36\t\t"+lexeme.substring(1, 2));
                        out.write(lexeme + "\t\t" + tok[35] + "\t\t36\t\t" + lexeme.substring(1, 2));
                        out.newLine();
                        token_lex.add(lexeme);
                        token_id.add(tok[35]);
                        token_number.add("36");
                        token_val.add(lexeme.substring(1, 2));
                        found = 1;
                    }
                    if (found == 0) {
                        // may just be too big
                        if (element.length() > 4) {
                            lexeme = element;
                            // // System.out.println(lexeme+"\t\t**unrecognized lexeme\t\t0"+element);
                            found = 1;

                        }
                        lexeme = element;
                        // at this point lexeme needs to be ' c '
                        //  System.out.println(lexeme+"\t\t**unrecognized lexeme\t\t0"+element);
                        tokens.remove(0);
                        if (!tokens.isEmpty() && found == 0) // check to see if next character is single quote.
                        {
                            element = tokens.firstElement().toString();
                            if (element.length() == 1 && !element.equals("\'")) {
                                lexeme = lexeme + " " + element;
                                tokens.remove(0);
                            } else { // Does not fit critera for character literal. too long or 0 length
                                // But got to check if the previous lexeme was just missing a quote.
                                if (element.equals("\'") && lexeme.length() == 2 && lexeme.startsWith("\'") && lexeme.substring(1, 2).matches("/S")) {
                                    lexeme = lexeme + " " + element;
                                    // System.out.println(lexeme+"\t\t"+tok[35]+"\t\t36\t\t"+lexeme.substring(1,lexeme.length()-2));
                                    out.write(lexeme + "\t\t" + tok[35] + "\t\t36\t\t" + lexeme.substring(1, lexeme.length() - 2));
                                    out.newLine();
                                    token_lex.add(lexeme);
                                    token_id.add(tok[35]);
                                    token_number.add("36");
                                    token_val.add(lexeme.substring(1, lexeme.length() - 2));
                                    found = 1;
                                    tokens.remove(0);
                                }
                                if (found == 0) {

                                    lexeme = lexeme + " " + element;
                                    //System.out.println(lexeme+"\t\t**unrecognized lexeme\t\t0"+element);
                                    out.write(lexeme + "\t\t**unrecognized lexeme\t\t0" + element);
                                    out.newLine();
                                    found = 1; // To avoid duplicate error messages.
                                }

                            }

                        } // end if
                        else { // Does not fit critera for character literal. no end quotation.

                            //System.out.println(lexeme+"\t\t**unrecognized lexeme\t\t0");
                            out.write(lexeme + "\t\t**unrecognized lexeme\t\t0");
                            out.newLine();
                            if (tokens.isEmpty()) {
                                return;
                            }

                        }

                        // check for end quote
                        if (found == 0) {


                            element = tokens.firstElement().toString();
                            if (element.equals("\'")) {
                                lexeme = lexeme + " " + element;

                                // System.out.println(lexeme+"\t\t"+tok[35]+"\t\t36\t\t"+lexeme.substring(1, 2)); // Position of char is constant.
                                out.write(lexeme + "\t\t" + tok[35] + "\t\t36\t\t" + lexeme.substring(1, 2));
                                out.newLine();
                                token_lex.add(lexeme);
                                token_id.add(tok[35]);
                                token_number.add("36");
                                token_val.add(lexeme.substring(1, 2));
                                found = 1;
                            } else {
                                //System.out.println(lexeme+"\t\t**unrecognized lexeme\t\t0"+lexeme.substring(1,3));
                                out.write(lexeme + "\t\t**unrecognized lexeme\t\t0" + lexeme.substring(1, 3));
                                out.newLine();
                                found = 1; // To avoid duplicate error messages.
                            }
                        }
                    } // end found==0
                } // end character check.


                // Constant Check.
                for (int i = 0; i < sym.length; i++) {
                    if (element.equalsIgnoreCase(sym[i])) {

                        found = 1;
                        //System.out.println(element+"\t\t"+tok[i]+"\t\t"+(i+1)); // No value for constants.
                        out.write(element + "\t\t" + tok[i] + "\t\t" + (i + 1));
                        out.newLine();
                        token_lex.add(element);
                        token_id.add(tok[i]);
                        token_number.add((i + 1));
                        token_val.add("ignore");
                        break;
                    }
                } // end for loop



                // Might still be identifer  | number

                if (found == 0) {
                    // Number check
                    try { // Try integer.

                        int i = Integer.parseInt(element.trim());
                        // If gets this far, it passes.
                        // System.out.println(i+"\t\t"+tok[33]+"\t\t34\t\t"+i);
                        out.write(i + "\t\t" + tok[33] + "\t\t34\t\t" + i);
                        out.newLine();
                        token_lex.add(i);
                        token_id.add(tok[33]);
                        token_number.add("34");
                        token_val.add(i);
                        found = 1;

                    } catch (Exception e) {
                        // try to see if its float instead.
                        try {
                            double t = Double.parseDouble(element.trim());
                            //System.out.println(t+"\t\t"+tok[34]+"\t\t35\t\t"+t);
                            out.write(t + "\t\t" + tok[34] + "\t\t35\t\t" + t);
                            out.newLine();
                            token_lex.add(t);
                            token_id.add(tok[34]);
                            token_number.add("35");
                            token_val.add(t);
                            found = 1;
                        } catch (Exception t) {
                            // Not float either.
                        }// end catch


                    } // end catch


                }

                if (found == 0) {
                    // Must be a identifer or nothing at all.
                    // Identifers must have the first character be a letter, and less than 21 characters long.
                    if (element.length() < 21 && element.length() > 0 && !element.substring(0, 1).matches("\\d") && !element.substring(0, 1).matches("\\s") && !element.substring(0, 1).contentEquals("_") && element.substring(0, 1).matches("\\w")) {  // if statement returns true, then means that the element is 20 or less characters, but more than 0 characters                                                                                        // for any  a-z
                        // and does not have a whitespace, underscore, or number as the first character.

                        // Need to make sure everything is alphanumeric.
                        int valid = 0;
                        if (element.length() == 1 && element.matches("\\w")) {
                            valid = 1;
                        }
                        for (int k = 1; k < element.length(); k++) {
                            if (valid == 1) {
                                break;
                            }
                            if (element.substring(k - 1, k).matches("\\w")) { //http://www.leepoint.net/notes-java/data/strings/40regular_expressions/25sum-regex.html
                                // Don't have to worry about the case where a identifer equals a reserved word because we are not checking for syntax.
                                valid = 1;
                            } else {
                                valid = 0;
                                break;
                            }
                        }
                        if (valid == 1) {
                            //System.out.println(element+"\t\t"+tok[32]+"\t\t33\t\t"+element);
                            out.write(element + "\t\t" + tok[32] + "\t\t33\t\t" + element);
                            out.newLine();
                            token_lex.add(element);
                            token_id.add(tok[32]);
                            token_number.add("33");
                            token_val.add(element);
                        } else {
                            //System.out.println(element+"\t\t**unrecognized lexeme\t\t0\t\t"+element);
                            out.write(element + "\t\t**unrecognized lexeme\t\t0\t\t" + element);
                            out.newLine();
                        }
                        //  System.out.println(element+"\t\t"+tok[36]+"\t\t37\t\t"+element);
                    } else {
                        // System.out.println(element+"\t\t**unrecognized lexeme\t\t0\t\t"+element);
                        out.write(element + "\t\t**unrecognized lexeme\t\t0\t\t" + element);
                        out.newLine();
                    }

                } // end founnd if


                tokens.remove(0); // so while loop does not repeat. Should not be skipping any tokens either.
            } //end while

            out.close();
        } // end try
        catch (IOException e) {
            System.out.println("IO exception. ");
        }
    }

    public static void main(String[] args) {
        File file; // input.
        Stack program_stack;
        Vector symbol_table;
        Vector program_tokens;
        Vector token_lexeme, token_ident, token_num, token_value;
        Scanner scanner;
        String ans = " ";
        Scanner in = new Scanner(System.in);
        // First Step is to have a loop.
        while (true) {
            symbol_table = new Vector<Record>();
            program_stack = new Stack<String>();
            token_lexeme = new Vector<String>(); // For easy syntax evaluation.
            token_ident = new Vector<String>();
            token_num = new Vector<String>();
            token_value = new Vector<String>();

            program_tokens = new Vector<String>(); // So user can run another file without reopening the program.
            System.out.print("Enter file to open, type q to quit: ");
            ans = in.nextLine();
            if (ans.equals("q")) {
                break;
            }

            file = new File(ans);
            try { // Going to read entire file first, then lexilize.
                scanner = new Scanner(file);
                System.out.println("Lexical Analyzer Running.");
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    // Should probably tokenize each line as it is read in.
                    Tokenize(line, program_tokens);

                }



                // Now I need to analyze.
                //System.out.println("Lexeme\t\tToken\t\tToken #\t\tValue");
                write_Token(program_tokens, token_lexeme, token_ident, token_num, token_value);

                // Syntax Analyzer Starts.

                // Start by pushing program onto stack.
                boolean test = program(program_stack, token_lexeme, token_ident, token_num, token_value, symbol_table);

                //Print out program stack. For Lab 2.
                //   while (!program_stack.empty()) {
                //        System.out.println(program_stack.firstElement());
                //          program_stack.remove(0);
                //   }

                // Print out Symbol Table Lab 3
//                System.out.println("\nSymbol Table");
//                System.out.println("Name\tType\tKind\tAddress");
//                int i = 0;
//                while (i < symbol_table.size()) {
//                    System.out.println(((Record) symbol_table.get(i)).print());
//
//                    i = i + 1;
//                }
//                System.out.println("\nMemory");
//                System.out.println("Address\tContents");
//                i = 0;
//                while (i < symbol_table.size()) {
//                    System.out.println(((Record) symbol_table.get(i)).printMemory());
//
//                    i = i + 1;
//                }

//                System.out.println("Expressions");
//                ((Record) symbol_table.firstElement()).PrintExpr();
//
//                if (test == false) {
//                    System.out.println("Syntax Error");
//                } else {
//                    System.out.println("Correct Syntax.");
//
//                }

                // Lab 4. No need for running through any arrays after program exits.


            } catch (FileNotFoundException e) {
                System.out.println("File not found.");

            }


        } // End of while statement.


    }
}
