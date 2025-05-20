

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Objeto {

    public static String[] instrucciones() {

        String texto = "" +
                "MOV AX, valor\n" +
                "MOV BX, 9\n" +
                "CMP AX, 160\n" +
                "JE  Si_1\n" +
                "MOV AX, 7\n" +
                "MOV valor, AX\n"+
                "Si_1:\n" +
                "MOV AX, 5\n" +
                "MOV valor, AX\n";
        String[] modulo = texto.split("\\s+");

        for (String palabra : modulo) {
            System.out.println(palabra);
        }
        return modulo;
    }

    public static String Binario() {
        String respuesta = "";
        String[] instrucciones = instrucciones();
        String opCode = "";
        String mod = "";
        String reg = "";
        String rm = "";
        boolean bandera = false;
        String binario = "";
        int conversion = 0;


        for (int i = 0; i < instrucciones.length; i++) {

            try {

                conversion = Integer.parseInt(instrucciones[i]);
                binario = Integer.toBinaryString(conversion);
                respuesta += binario;
                bandera = true;

            } catch (Exception e) {
                bandera = false;
            }

            if (instrucciones[i].equals("MOV")) {
                opCode = "1011 ";
                if (instrucciones[i + 1].equals("AX,")) {
                    mod = "10 ";
                    reg = "000 ";
                }
                if (instrucciones[i + 1].equals("BX,")) {
                    mod = "10 ";
                    reg = "110 ";
                }
                respuesta += "\n" + opCode + mod + reg;
            }
            if (instrucciones[i].equals("CMP")) {
                opCode = "1110 ";
                if (instrucciones[i + 1].equals("AX,")) {
                    mod = "01 ";
                    reg = "1101 ";
                    if (instrucciones[i + 2].equals("BX")) {
                        rm = "1000 ";
                    }
                }
                respuesta += "\n" + opCode + mod + reg + rm;
            }
            if (instrucciones[i].equals("JE")) {
                opCode = "1110 1000";
                respuesta += "\n" + opCode;
            }
            /*if (instrucciones[i].substring(0, instrucciones[i].length() - 2).equals("SI_")) {

            }*/


        }

        return respuesta;
    }

    public static String segmento() {

        String respuesta = "\n";
        String binario = Binario().trim();  // Ajusta aquí, aplicando trim para eliminar espacios en blanco adicionales
        String[] lineas = binario.split("\n");
        int contador = 0;

        for (int i = 0; i < lineas.length; i++) {
            // Eliminar espacios de la línea
            String lineaSinEspacios = lineas[i].replaceAll(" ", "");
            contador += contarGruposDe8(lineaSinEspacios);
            respuesta += "0X1000" + contador + ":\t" + lineas[i] + "\n"; // Ajusta aquí
        }
        return respuesta;
    }

    private static int contarGruposDe8(String linea) {
        int contador = 0;
        for (int i = 0; i <= linea.length() - 8; i += 8) {
            if (esGrupoDe0y1(linea.substring(i, i + 8))) {
                contador++;
            }
        }
        return contador;
    }

    private static boolean esGrupoDe0y1(String grupo) {
        // Verificar si el grupo consiste solo en caracteres '0' y '1'
        for (char c : grupo.toCharArray()) {
            if (c != '0' && c != '1') {
                return false;
            }
        }
        return true;
    }

    public static HashMap<String, String> Codigo() {

        HashMap<String, String> SegmentoBinario = new LinkedHashMap<>();
        String segmentos = Main.txtSegmento.getText().trim();  // Ajusta aquí, aplicando trim para eliminar espacios en blanco adicionales
        String binario = Main.txtObjeto.getText().trim();  // Ajusta aquí, aplicando trim para eliminar espacios en blanco adicionales
        var respuesta = "";

        // Dividir las líneas de segmentos y binario
        String[] segmentosArray = segmentos.split("\n");
        String[] binarioArray = binario.split("\n");

        // Asegurémonos de que haya la misma cantidad de líneas en segmentos y binario
        for (int i = 0; i < segmentosArray.length && i < binarioArray.length; i++) {
            // Verificar si la línea actual no es completamente vacía antes de agregar al HashMap
            if (!segmentosArray[i].isEmpty() && !binarioArray[i].isEmpty()) {
                SegmentoBinario.put(segmentosArray[i].trim(), binarioArray[i].trim());
            }
        }

        // Imprimir llaves y valores
        for (Map.Entry<String, String> entry : SegmentoBinario.entrySet()) {
            respuesta += entry.getKey() + "\t" + entry.getValue() + "\n";
        }
        Main.txtObjeto.setText(respuesta);
        return SegmentoBinario;
    }
    public static String segmentoCodigo() {
        String respuesta = "";
        HashMap<String, String> SegmentoBinario = new LinkedHashMap<>();
        SegmentoBinario = Codigo();

        for (Map.Entry<String, String> entry : SegmentoBinario.entrySet()) {
            respuesta += entry.getKey() + "\t" + entry.getValue() + "\n";
        }

        //System.out.println(respuesta);  // Puedes quitar esto si no necesitas imprimir en la consola
        return respuesta;
    }
}