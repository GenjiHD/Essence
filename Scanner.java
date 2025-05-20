
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Predicate;
import java.util.List;

public class Scanner {

  static String sumatoria = "";

  private static Font fuente = new Font("Tahoma", 16, 22);
  private static int LineaCont = 1;
  private static boolean isError = false;
  static ArrayList<String[]> tokens = new ArrayList<>();
  private static String[] palabrasReservadas = {
      "essence",
      "if",
      "else",
      "take",
      "show",
      "num",
      "frac",
      "use",
      "star",
      "declare"
  };
  private static final List<Predicate<String>> VALIDADORES = Arrays.asList(Scanner::validaComentario,
      Scanner::validaTipoDato, Scanner::validaID, Scanner::validaOprel, Scanner::validaFloat,
      Scanner::validaEntero, Scanner::validaAritmeticos, Scanner::validaDelimitadores, Scanner::validaTexto);
  static Hashtable<String, String> symbols = new Hashtable<String, String>();

  static void scan() {
    symbols.put("{", "left_curly_bracket");
    symbols.put("}", "right_curly_bracket");
    symbols.put("(", "left_parenthesis");
    symbols.put(")", "right_parenthesis");
    symbols.put("[", "left_square_bracket");
    symbols.put("]", "right_square_bracket");
    symbols.put(";", "semicolon");
    symbols.put("\"", "double_quote");
    symbols.put(",", "comma");
    LineaCont = 1;
    isError = false;
    tokens.clear();

    String[] lineas = Main.txtSourceCode.getText().split("\\r?\\n");
    for (String linea : lineas) {
      validarToken(linea);
      LineaCont++;
      if (isError)
        return;
    }
    ArrayList<String> labels = new ArrayList<String>();
    for (String[] token : tokens) {
      labels.add(token[0]);
      labels.add(token[1]);

      sumatoria = sumatoria + token[0] + "\t           " + token[1] + "\n";

    }

    Main.txtScanner.setText(sumatoria);

    Main.valida();
    Main.txtScanner.update(Main.txtScanner.getGraphics());
  }

  private static void validarToken(String linea) {
    String token = "", cadena = "";
    linea = linea.trim();
    if (linea.contains("\"")) {
      cadena = linea.replaceAll(".*\"([^\"]*)\".*", "$1 ");
      cadena = cadena.replaceAll(" ", "_");
      linea = linea.replaceAll("\"([^\"]*)\"", "\"" + cadena + "\"");
    }
    linea = linea.replaceAll("(.);", "$1 ;");
    linea = linea.replaceAll("\"(\\S*)", "\" $1");
    linea = linea.replaceAll("(\\S*)\"", "$1 \"");
    linea = linea.replaceAll("\\(\"", "( \"");
    linea = linea.replaceAll("\",", "\" ,");
    linea = linea.replaceAll("\"\\)", "\" )");
    linea = linea.replaceAll("([0-9])\\)", "$1 )");
    linea = linea.replaceAll("([a-zA-Z]+)\\(", "$1 (");
    linea = linea.replaceAll("(\\-\\-|\\+\\+|[a-zA-Z]{1}[a-zA-Z0-9]*)\\)", "$1 )");
    linea = linea.replaceAll("([a-zA-Z]{1}[a-zA-Z0-9]*)(\\-\\-|\\+\\+)", "$1 $2");
    linea = linea.replaceAll("(\\()([a-zA-Z]+|\\))", "$1 $2");
    linea = linea.replaceAll("\\s{2,}", " ");
    linea += " ";
    int length = linea.length();
    if (length <= 1) {

      return;
    }
    for (int i = 0; i < length; i++) {
      char actual = linea.charAt(i);
      if (actual == ' ') {
        String tokenAux = token;
        token = "";
        if (tokenAux.contains("_")) {
          cadena = cadena.replaceAll("_", " ");
          cadena = cadena.replaceAll("\"", "");
          tokens.add(new String[] { cadena, "cadena" });
          tokenAux = "";
          continue;
        }
        boolean valido = false;
        for (Predicate<String> validador : VALIDADORES)
          if (validador.test(tokenAux)) {
            valido = true;
            break;
          }
        if (!valido) {
          mensajeError("No es un token valido: '" + tokenAux + "'");
          return;
        }
        continue;
      }
      token += actual;
    }
  }

  private static boolean validaComentario(String token) {
    Pattern patron = Pattern.compile("\\/\\/.*");
    Matcher matcher = patron.matcher(token);
    if (!matcher.matches())
      return false;
    token = "";
    return true;
  }

  private static boolean validaID(String token) {
    Pattern patron = Pattern.compile("[a-zA-Z]{1}[a-zA-Z0-9]*");
    Matcher matcher = patron.matcher(token);
    if (!matcher.matches())
      return false;
    token = token.toLowerCase();
    if (Arrays.asList(palabrasReservadas).contains(token))
      tokens.add(new String[] { token, "Palabra reservada" });
    else
      tokens.add(new String[] { token, "Identificador" });
    token = "";
    return true;
  }

  private static boolean validaOprel(String token) {
    Pattern patron = Pattern.compile("(==|!=|<|<=|>|>=){1}");
    Matcher matcher = patron.matcher(token);
    if (!matcher.matches())
      return false;
    tokens.add(new String[] { token, "Operador Relacional" });
    token = "";
    return true;
  }

  private static boolean validaTipoDato(String token) {
    Pattern patron = Pattern.compile("(frac|num|string){1}");
    Matcher matcher = patron.matcher(token);
    if (!matcher.matches())
      return false;
    tokens.add(new String[] { token, "Tipo de Dato" });
    return true;
  }

  private static boolean validaFloat(String token) {
    Pattern patron = Pattern.compile("-?[0-9]+\\.[0-9]+");
    Matcher matcher = patron.matcher(token);
    if (!matcher.matches())
      return false;
    tokens.add(new String[] { token, "Frac" });
    return true;
  }

  private static boolean validaEntero(String token) {
    Pattern patron = Pattern.compile("-?[0-9]+");
    Matcher matcher = patron.matcher(token);
    if (!matcher.matches())
      return false;
    tokens.add(new String[] { token, "Entero" });
    return true;
  }

  private static boolean validaTexto(String token) {
    // Utilizamos comillas simples para representar cadenas de texto
    Pattern patron = Pattern.compile("'[^']*'");
    Matcher matcher = patron.matcher(token);
    if (!matcher.matches())
      return false;
    tokens.add(new String[] { token, "Texto" });
    return true;
  }

  private static boolean validaAritmeticos(String token) {
    Pattern patron = Pattern.compile("(\\+|\\-|\\/|\\*|\\^|\\%|\\+\\+|\\-\\-|=){1}");
    Matcher matcher = patron.matcher(token);
    if (!matcher.matches())
      return false;
    if (token.equals("="))
      tokens.add(new String[] { token, "Operador de asignacion" });
    else
      tokens.add(new String[] { token, "Operador Aritmetico" });
    token = "";
    return true;
  }

  private static boolean validaDelimitadores(String token) {
    Pattern patron = Pattern.compile("(\\{|\\}|\\(|\\)|\\[|\\]|;|\"|,){1}");
    Matcher matcher = patron.matcher(token);
    if (!matcher.matches())
      return false;
    tokens.add(new String[] { token, symbols.get(token) });
    token = "";
    return true;
  }

  private static void mensajeError(String mensaje) {
    isError = true;

    tokens.clear();

  }
}
