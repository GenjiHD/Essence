import java.util.ArrayList;
import java.util.HashMap;

public class Intermedio {

  static String expresion;

  static ArrayList<String> ListaID = new ArrayList<>();
  static HashMap<String, String> Simbolos = new HashMap<>();
  static HashMap<String, String> IDvalor = new HashMap<>();

  static ArrayList<String> listaExpresiones = new ArrayList<>();
  static ArrayList<String> listaExpresiones1 = new ArrayList<>();

  public static String variables() {
    Simbolos = Parser.variables;
    ListaID = Parser.idRepetidos;
    IDvalor = Parser.IDValorExacto;
    String cascaron = ".DATA\n\n";
    String id;
    String Entero = "\t DW\t?\n";
    String Decimal = "\t DD\t?\n";
    String Cadena = "\t DB\t?\n";

    for (int i = 0; i < ListaID.size(); i++) {
      id = ListaID.get(i);

      if (IDvalor.containsKey(id) && Simbolos.get(id).equals("num")) {
        cascaron = cascaron + id + "\t DW\t " + IDvalor.get(id) + "\n";
      } else {
        if (Simbolos.get(id).equals("num")) {
          cascaron = cascaron + id + Entero;
        }
      }
      if (IDvalor.containsKey(id) && Simbolos.get(id).equals("frac")) {
        cascaron = cascaron + id + "\t DD\t " + IDvalor.get(id) + "\n";
      } else {
        if (Simbolos.get(id).equals("frac")) {
          cascaron = cascaron + id + Decimal;
        }
      }
      if (IDvalor.containsKey(id) && Simbolos.get(id).equals("string")) {
        cascaron = cascaron + id + "\t DB\t " + IDvalor.get(id).substring(0, IDvalor.get(id).length() - 1) + "$'"
            + "\n";
      } else {
        if (Simbolos.get(id).equals("string")) {
          cascaron = cascaron + id + Cadena;
        }
      }
    }
    return cascaron + "\n";
  }

  public static String code() {
    String respuesta = ".CODE\n"; // string que retornare
    listaExpresiones1 = Parser.listaExpresiones1; // Listas de tokens
    listaExpresiones = Parser.expresionMatematica;
    String regMemoria; // Registros de memoria
    String ID; // Por si necesito tomar un ID en la concatenacion
    expresion = ""; // Guardo las exprsiones para mostrarlas en un JOption
    String bloques = "";
    String salto = ""; // Defino los J que utilizare
    int contador = 0;
    boolean dentroBloqueElse = false; // bandera para saber si estoy dentro de un else
    ArrayList<String> bloqueElse = new ArrayList<>();
    boolean elseEjecutado = false;

    for (int i = 0; i < listaExpresiones.size(); i++) {

      if (listaExpresiones.get(i).equals("else")) {
        dentroBloqueElse = true;
      }
      if (listaExpresiones.get(i).equals("}")) {
        dentroBloqueElse = false;
      }
      // Detecto la instruccion dentro del bloque else, y la concateno en su lugar
      // correcto
      if (!dentroBloqueElse && listaExpresiones.get(i).equals("{")) {
        respuesta += dentroElse() +
            "\n\tJMP\tFIN\n";

      }
      // Condicion para reconocer imprimir fuera de un bloque else
      if (listaExpresiones.get(i).equals("show") && !dentroBloqueElse) {
        ID = listaExpresiones.get(i + 2);
        respuesta += "\n\tMOV AH,\t09\n\tLEA DX,\t" + ID + "\n\tINT 21H\n\n";
      }
      // Condicion para reconocer los if
      if (listaExpresiones.get(i).equals("if") && listaExpresiones.get(i + 1).equals("(")) {
        // Establezco la el salto dependiendo del operador logico
        if (listaExpresiones.get(i + 3).equals("<")) {
          salto = " JL";
        }
        if (listaExpresiones.get(i + 3).equals("==")) {
          salto = " JE";
        }
        if (listaExpresiones.get(i + 3).equals(">")) {
          salto = " JG";
        }
        // Contador para dar con el ID actual
        contador++;
        respuesta += "\n\tMOV AX,\t" + listaExpresiones.get(i + 2) +
            "\n\tMOV BX,\t" + listaExpresiones.get(i + 4) + "\n\tCMP AX,\tBX\n" +
            "\n\t" + salto + "\tSi_" + contador + "\n";
      }
      // Contenido de los if { instrucciones }
      if (listaExpresiones.get(i).equals("{") && listaExpresiones.get(i - 6).equals("if")) {

        respuesta += "\nSi_" + contador + ":\n";
      }

      // Saco la expresion para debugear lo que hago
      expresion += listaExpresiones.get(i) + "\n";

      // Multiplicacion
      if (listaExpresiones.get(i).equals("*")) {
        respuesta += "\n\t;Hacemos la multiplicacion y la asignamos\n\tMOV AX,\t" + listaExpresiones.get(i - 1)
            + "\n\tMUL AX,\t" + listaExpresiones.get(i + 1) +
            "\n\tMOV " + listaExpresiones.get(i - 3) + ",\tAX\n";
      }
      // Division
      if (listaExpresiones.get(i).equals("-")) {
        respuesta += "\n\t;Hacemos la resta y la asignamos\n\tMOV AX,\t" + listaExpresiones.get(i - 1)
            + "\n\tSUB AX,\t" + listaExpresiones.get(i + 1) +
            "\n\tMOV " + listaExpresiones.get(i - 3) + ",\tAX\n";
      }
      // Suma
      if (listaExpresiones.get(i).equals("+")) {
        respuesta += "\n\t;Hacemos la suma y la asignamos\n\tMOV AX,\t" + listaExpresiones.get(i - 1) + "\n\tADD AX,\t"
            + listaExpresiones.get(i + 1) +
            "\n\tMOV " + listaExpresiones.get(i - 3) + ",\tAX\n";
      }
      // Asignacion
      if (listaExpresiones.get(i).equals("=") && listaExpresiones.get(i + 2).equals(";")) {
        respuesta += "\n\tMOV AX,\t" +
            listaExpresiones.get(i + 1) + "\n\tMOV " + listaExpresiones.get(i - 1) + "\tAX\n";
      }
    }
    // Etiqueta de Fin al final
    if (listaExpresiones.contains("if")) {
      respuesta += "\nFin :\n\n\n\n";
    }
    System.out.println(respuesta + "\nFin Intermedio");
    return respuesta;
  }

  public static String dentroElse() {
    listaExpresiones = Parser.expresionMatematica;
    ArrayList<String> contenidoElse = new ArrayList<>();
    boolean bandera = false;
    String bloques = "";
    String respuesta = "";
    String ID;

    for (int i = 0; i < listaExpresiones.size(); i++) {

      if (listaExpresiones.get(i).equals("{") && listaExpresiones.get(i - 1).equals("else")) {
        bandera = true;
      }
      if (bandera) {
        contenidoElse.add(listaExpresiones.get(i));
      }
      if (listaExpresiones.get(i).equals("}")) {
        bandera = false;
      }
    }
    for (String c : contenidoElse) {
      bloques += c;
    }
    for (int i = 0; i < contenidoElse.size(); i++) {
      if (contenidoElse.get(i).equals("show")) { // Condicion para reconocer show
        ID = contenidoElse.get(i + 2);
        respuesta += "\n\tMOV AH,\t09\n\tLEA DX,\t" + ID + "\n\tINT 21H\n";
      }
      if (contenidoElse.get(i).equals("=") && contenidoElse.get(i + 2).equals(";")) {
        respuesta += "\n\tMOV AX,\t" +
            contenidoElse.get(i + 1) + "\n\tMOV " + contenidoElse.get(i - 1) + "\tAX\n";
      }
    }
    // JOptionPane.showMessageDialog(null, "Dentro de else\n" + bloques);

    return respuesta;
  }

}
