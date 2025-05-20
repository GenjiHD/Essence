

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Parser {
    private static int cont;
    private static ArrayList<String[]> lista;
    private static boolean valido;
    static JLabel lblTexto;
    static String resultado;

    // Aqui guardare la expresion, para despues convertirla a codigo intermedio
    static ArrayList<String> expresionMatematica = new ArrayList<>();

    static ArrayList<String> listaExpresiones1 = new ArrayList<>();

    // Aqui guardare los tipo de datoy ID de todas las variables declaradas, esto
    // para verificar que no se repitan
    static HashMap<String, String> variables = new HashMap<>();

    // Este es otro Array que puse para los errores, si hay un error agregare un
    // elemento
    static ArrayList<Integer> aceptacion = new ArrayList<>();

    // Guardare todos los ID, para verificar si hay alguno repetido
    static ArrayList<String> idRepetidos = new ArrayList<>();

    // Guardo el ID y su VALOR(entero, float, texto)
    static HashMap<String, String> IDValor = new HashMap<>();

    // Guardo el ID y su VALOR(Valor real)
    static HashMap<String, String> IDValorExacto = new HashMap<>();

    public static void Parsing() {
        cont = 0;
        valido = false;
        lista = Scanner.tokens;
        if (lista.size() == 0) {

            Main.txtParser.setText("Se requiere hacer el Scanner primero");
            return;
        }
        try {
            program();
        } catch (Exception e) {

        }
        if (!valido) {
            Main.txtParser.setForeground(Color.RED);
            Main.txtParser.setText("\n          Syntax error");
            return;
        }
        Main.txtParser.setForeground(Color.GREEN);
        Main.txtParser.setText("\n      Syntax perfect");

    }

    private static void program() {
        if (!lista.get(0)[0].equals("essence"))
            return;
        cont++;
        if (!lista.get(cont)[1].equals("Identificador"))
            return;
        cont++;
        if (!block())
            return;
        if (cont != lista.size() - 1)
            return;
        valido = true;
    }

    private static boolean block() {
        if (!lista.get(cont)[1].equals("left_curly_bracket"))
            return false;
        cont++;
        if (lista.get(cont)[0].equals("}")) {
            System.out.println("Metodo bloque");
            System.out.println("bloque final:Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
            expresionMatematica.add(lista.get(cont)[0]);
            listaExpresiones1.add(lista.get(cont)[1]);
        }
        if (!instruction())
            return false;
        if (!lista.get(cont)[1].equals("right_curly_bracket"))
            return false;
        return true;
    }

    private static boolean instruction() {

        if (!(assignment() || read() || write() || conditional() || declare()))
            return false;
        cont++;
        if (!lista.get(cont)[1].equals("right_curly_bracket"))
            return instruction();
        else
            return true;

    }

    private static boolean conditional() {
        System.out.println("Metodo if");
        if (lista.get(cont)[0].equals("if")) {
            System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
            expresionMatematica.add(lista.get(cont)[0]);
            listaExpresiones1.add(lista.get(cont)[1]);
        }
        if (!lista.get(cont)[0].equals("if"))
            return false;
        cont++;
        System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        if (!lista.get(cont)[1].equals("left_parenthesis"))
            return false;
        cont++;
        System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        if (!cond())
            return false;
        cont++;
        System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        if (!lista.get(cont)[1].equals("right_parenthesis"))
            return false;
        cont++;
        System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        if (!block())
            return false;
        if (lista.get(cont + 1)[0].equals("else")) {
            expresionMatematica.add("}");
        }
        cont++;
        System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        if (lista.get(cont)[0].equals("else")) {

            cont++;
            System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
            expresionMatematica.add(lista.get(cont)[0]);
            listaExpresiones1.add(lista.get(cont)[1]);
            if (!block())
                return false;
            expresionMatematica.add("}");
        } else
            cont--;
        return true;
    }

    private static boolean cond() {
        System.out.println("Metodo cond");
        if (!(lista.get(cont)[1].equals("Identificador") || lista.get(cont)[1].equals("Entero"))) {
            return false;
        }
        cont++;
        System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        if (!lista.get(cont)[1].equals("Operador Relacional"))
            return false;
        cont++;
        System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        if (!(lista.get(cont)[1].equals("Identificador") || lista.get(cont)[1].equals("Entero"))) {
            return false;
        }
        return true;
    }

    private static boolean write() {
        System.out.println("Metodo imprimir ");
        if (lista.get(cont)[0].equals("show")) {
            expresionMatematica.add(lista.get(cont)[0]);
            listaExpresiones1.add(lista.get(cont)[1]);
        }
        System.out.println("1.Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        if (!lista.get(cont)[0].equals("show"))
            return false;
        cont++;

        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        System.out.println("2.Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        if (!lista.get(cont)[1].equals("left_parenthesis"))
            return false;
        cont++;
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        System.out.println("3.Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        if (lista.get(cont)[1].equals("Identificador")) {
            if (!variables.containsKey(lista.get(cont)[0])) {
                aceptacion.add(1);
            }
            cont++;
            expresionMatematica.add(lista.get(cont)[0]);
            listaExpresiones1.add(lista.get(cont)[1]);
            System.out.println("4.Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
            if (!lista.get(cont)[1].equals("right_parenthesis"))
                return false;
            cont++;
            expresionMatematica.add(lista.get(cont)[0]);
            listaExpresiones1.add(lista.get(cont)[1]);
            System.out.println("5.Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
            if (lista.get(cont)[0].equals(";"))
                return true;
            return false;
        }
        if (!lista.get(cont)[0].equals("\""))
            return false;
        cont++;
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        System.out.println("6.Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        if (!lista.get(cont)[1].equals("cadena"))
            return false;
        cont++;
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        System.out.println("7.Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        if (!lista.get(cont)[0].equals("\""))
            return false;
        cont++;
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        System.out.println("8.Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        if (lista.get(cont)[1].equals("right_parenthesis")) {
            cont++;
            expresionMatematica.add(lista.get(cont)[0]);
            listaExpresiones1.add(lista.get(cont)[1]);
            System.out.println("9.Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
            if (lista.get(cont)[0].equals(";"))
                return true;
            return false;
        }
        if (!lista.get(cont)[1].equals("comma"))
            return false;
        cont++;
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        System.out.println("10.Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        if (!lista.get(cont)[1].equals("Identificador"))
            return false;
        cont++;
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        System.out.println("11.Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        if (!lista.get(cont)[1].equals("right_parenthesis"))
            return false;
        cont++;
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        System.out.println("12.Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        if (lista.get(cont)[0].equals(";"))
            return true;
        return false;
    }

    private static boolean read() {
        if (!lista.get(cont)[1].equals("Identificador"))
            return false;
        if (!variables.containsKey(lista.get(cont)[0])) {
            aceptacion.add(1);
        }
        cont++;
        if (!lista.get(cont)[0].equals("="))
            return false;
        cont++;
        if (!lista.get(cont)[0].equals("take"))
            return false;
        cont++;
        if (!lista.get(cont)[1].equals("left_parenthesis"))
            return false;
        cont++;
        if (!lista.get(cont)[1].equals("right_parenthesis"))
            return false;
        cont++;
        if (lista.get(cont)[0].equals(";"))
            return true;
        return false;
    }

    private static boolean assignment() {
        System.out.println("Metodo Asignar");
        if (!lista.get(cont)[1].equals("Identificador"))
            return false;

        if (!variables.containsKey(lista.get(cont)[0])) {
            aceptacion.add(1);
        }
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);

        System.out.println("Asig 1:Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        cont++;
        if (!lista.get(cont)[0].equals("=")) {
            cont--;
            return false;
        }
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        cont++;

        if (!calc())
            return false;
        return true;
    }

    private static boolean calc() {
        System.out.println("Metodo Calc");
        if (!(lista.get(cont)[1].equals("Identificador") || lista.get(cont)[1].equals("Entero")
                || lista.get(cont)[1].equals("Texto") || lista.get(cont)[1].equals("Frac"))) {

            cont -= 2;
            return false;
        }
        IDValor.put(lista.get(cont - 2)[0], lista.get(cont)[1]);
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        cont++;
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        if (lista.get(cont)[0].equals(";"))
            return true;
        if (!(lista.get(cont)[1].equals("Operador Aritmetico"))) {
            cont--;
            return false;
        }
        cont++;
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        if (!(lista.get(cont)[1].equals("Identificador") || lista.get(cont)[1].equals("Entero")
                || lista.get(cont)[1].equals("Texto") || lista.get(cont)[1].equals("Frac"))) {
            cont--;
            return false;
        }

        cont++;
        expresionMatematica.add(lista.get(cont)[0]);
        listaExpresiones1.add(lista.get(cont)[1]);
        System.out.println("Se guardaron los valores a sus listas :" + lista.get(cont)[0] + "***" + lista.get(cont)[1]);
        if (lista.get(cont)[0].equals(";"))
            return true;
        cont *= -1;
        return false;
    }

    private static boolean declare() {
        boolean bandera = true;
        String decision;

        if (!lista.get(cont)[0].equals("declare") && !lista.get(cont)[0].equals("star")) {
            bandera = false;
        }
        decision = lista.get(cont)[0];
        cont++;
        if (decision.equals("declare")) {
            if (!lista.get(cont)[1].equals("Tipo de Dato")) {
                bandera = false;
            }
            cont++;

            if (!lista.get(cont)[1].equals("Identificador")) {
                bandera = false;
            }
            cont++;

            if (!lista.get(cont)[0].equals(";")) {
                bandera = false;
            }
            idRepetidos.add(lista.get(cont - 1)[0]);
            variables.put(lista.get(cont - 1)[0], lista.get(cont - 2)[0]);
        }

        if (decision.equals("star")) {
            if (!lista.get(cont)[1].equals("Tipo de Dato")) {
                bandera = false;
            }
            cont++;
            if (!lista.get(cont)[1].equals("Identificador")) {
                bandera = false;
            }
            cont++;
            if (!lista.get(cont)[0].equals("=")) {
                bandera = false;
            }
            cont++;
            if (!(lista.get(cont)[1].equals("Identificador") || lista.get(cont)[1].equals("Entero")
                    || lista.get(cont)[1].equals("Texto") || lista.get(cont)[1].equals("Frac"))) {
                bandera = false;
            }
            IDValor.put(lista.get(cont - 2)[0], lista.get(cont)[1]);
            IDValorExacto.put(lista.get(cont - 2)[0], lista.get(cont)[0]);
            cont++;
            if (!lista.get(cont)[0].equals(";")) {
                bandera = false;
            }
            idRepetidos.add(lista.get(cont - 3)[0]);
            variables.put(lista.get(cont - 3)[0], lista.get(cont - 4)[0]);
        }
        return bandera;
    }

    private static boolean calcula() {
        boolean bandera = true;

        System.out.println("Metodo calcula");
        System.out.println("Token: " + lista.get(cont)[0] + "\tContador: " + cont);
        if (!lista.get(cont)[1].equals("Identificador")) {
            bandera = false;
        }
        expresionMatematica.add(lista.get(cont)[0]);
        cont++;
        System.out.println("Token: " + lista.get(cont)[0] + "\tContador: " + cont);
        if (!lista.get(cont)[0].equals("=")) {
            bandera = false;
        }
        expresionMatematica.add(lista.get(cont)[0]);
        cont++;
        System.out.println("Token: " + lista.get(cont)[0] + "\tContador: " + cont);
        if (!(lista.get(cont)[1].equals("Identificador") || lista.get(cont)[1].equals("Entero")
                || lista.get(cont)[1].equals("Texto") || lista.get(cont)[1].equals("Frac"))) {

            bandera = false;
        }
        expresionMatematica.add(lista.get(cont)[0]);

        cont++;
        System.out.println("Token: " + lista.get(cont)[0] + "\tContador: " + cont);

        do {
            System.out.println("Ciclo while");
            if (!lista.get(cont)[1].equals("Operador Aritmetico")) {
                bandera = false;
            }
            expresionMatematica.add(lista.get(cont)[0]);

            cont++;
            System.out.println("Token: " + lista.get(cont)[0] + "\tContador: " + cont);
            if (!(lista.get(cont)[1].equals("Identificador") || lista.get(cont)[1].equals("Entero")
                    || lista.get(cont)[1].equals("Texto") || lista.get(cont)[1].equals("Frac"))) {

                bandera = false;
            }
            expresionMatematica.add(lista.get(cont)[0]);

            cont++;
            System.out.println("Token: " + lista.get(cont)[0] + "\tContador: " + cont);

        } while (!lista.get(cont)[0].equals(";"));
        expresionMatematica.add(lista.get(cont)[0]);
        return bandera;
    }
}