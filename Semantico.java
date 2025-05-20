import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

public class Semantico {

  static ArrayList<Integer> Aceptacion = new ArrayList<>();
  static HashMap<String, String> TipoDato = new HashMap<>();
  static ArrayList<String> IDsRepetidos = new ArrayList<>();
  static HashMap<String, String> ValorID = new HashMap<>();

  public static boolean VariablesNoDeclaradas() {
    Aceptacion = Parser.aceptacion;
    boolean flag = true;

    if (Aceptacion.contains(1)) {
      flag = false;
    }
    return flag;
  }

  public static boolean DeclaracionRepetida() {
    boolean flag = true;
    ArrayList<String> IDRepetidos = Parser.idRepetidos;

    for (int i = 0; i < IDRepetidos.size(); i++) {
      String buscaID = IDRepetidos.get(i);
      int cont = 0;

      for (int j = 0; j < IDRepetidos.size(); j++) {
        if (buscaID.equals(IDsRepetidos.get(j))) {
          cont++;
        }
        if (cont > 1) {
          flag = false;
          break;
        }
      }
    }

    if (flag == false) {

    }
    return flag;
  }

  public static boolean TipoDeDato() {
    boolean flag = true;

    ValorID = Parser.IDValor;
    TipoDato = Parser.variables;
    IDsRepetidos = Parser.idRepetidos;
    String ID;

    for (int i = 0; i < IDsRepetidos.size(); i++) {
      ID = IDsRepetidos.get(i);

      if (TipoDato.containsKey(ID) && ValorID.containsKey(ID)) {

        if ("num".equalsIgnoreCase(TipoDato.get(ID)) && !"Entero".equalsIgnoreCase(ValorID.get(ID))) {
          flag = false;
        }

        if ("string".equalsIgnoreCase(TipoDato.get(ID)) && !"Texto".equalsIgnoreCase(ValorID.get(ID))) {
          flag = false;
        }

        if ("frac".equalsIgnoreCase(TipoDato.get(ID)) && !"Frac".equalsIgnoreCase(ValorID.get(ID))) {
          flag = false;
        }
      }
    }
    return flag;
  }

  public static void program() {

    if (VariablesNoDeclaradas() && DeclaracionRepetida() && TipoDeDato()) {
      Main.txtSemantico.setForeground(Color.GREEN);
      Main.txtSemantico.setText("\n    Semantic perfect");
    } else {
      Main.txtSemantico.setForeground(Color.RED);
      Main.txtSemantico.setText("\n    Semantic error");
    }

  }
}
