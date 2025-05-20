import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CodigoObjeto {

    public static String dezplazamiento() {
        String respuesta = "";
        HashMap<String, String> CodigoBinario = CodigoBinario();
        String partidaCondicion = "";
        String llegadaCondicion = "";
        String salidaJMP = "";
        String ultimaDireccion = "";

        int partidaNumero = 0;
        int salidaJMPNumero = 0;
        int llegadaCondicionNumero = 0;
        int ultimaDireccionNumero = 0;

        for (Map.Entry<String, String> entry : CodigoBinario.entrySet()) {
            ultimaDireccion = entry.getKey();

            if (entry.getValue().equals("1110 1000")) {
                partidaCondicion = entry.getKey();
                partidaNumero = obtenerNumeroDesdeDireccion(partidaCondicion);
            } else if (entry.getValue().equals("1110 1011")) {
                salidaJMP = entry.getKey();
                salidaJMPNumero = obtenerNumeroDesdeDireccion(salidaJMP);
            } else if (partidaNumero != 0 && llegadaCondicion.isEmpty()) {
                llegadaCondicion = entry.getKey();
                llegadaCondicionNumero = obtenerNumeroDesdeDireccion(llegadaCondicion);
            }
        }

        ultimaDireccionNumero = obtenerNumeroDesdeDireccion(ultimaDireccion);
        int diferenciaUltimaDireccion = ultimaDireccionNumero - salidaJMPNumero;
        String binarioDiferencia = String.format("%16s", Integer.toBinaryString(diferenciaUltimaDireccion)).replace(' ', '0');

        JOptionPane.showMessageDialog(null, "Diferencia\n Salida JMP:" + salidaJMP + " \n Última Dirección:" + ultimaDireccion + " \n\t" +
                binarioDiferencia + "\n(Decimal: " + diferenciaUltimaDireccion + ")");
        JOptionPane.showMessageDialog(null, "Diferencia Partida " + salidaJMP + " y Salida JMP :" + salidaJMPNumero + "\n" +
                String.format("%16s", Integer.toBinaryString(salidaJMPNumero - partidaNumero)).replace(' ', '0'));

        for (Map.Entry<String, String> entry : CodigoBinario.entrySet()) {
            if (entry.getKey().length() >= 5) {
                String parteNumerica = entry.getKey().substring(5);
                try {
                    int numeroDecimal = Integer.parseInt(parteNumerica, 2);
                    String direccionBinaria = String.format("%16s", Integer.toBinaryString(numeroDecimal)).replace(' ', '0');
                    respuesta += direccionBinaria + "\t" + entry.getValue() + "\n";
                } catch (NumberFormatException e) {
                    System.out.println("No se pudo convertir la parte numérica a número decimal.");
                }
            }
        }

        Main.txtByteCode.setText(respuesta);
        System.out.println(respuesta);
        return respuesta;
    }

    private static int obtenerNumeroDesdeDireccion(String direccion) {
        String parte = direccion.substring(5);
        try {
            return Integer.parseInt(parte, 2);
        } catch (NumberFormatException e) {
            System.out.println("Error al convertir dirección binaria: " + parte);
            return 0;
        }
    }

    public static HashMap<String, String> CodigoBinario() {
        String binario = segmento();
        LinkedHashMap<String, String> CodigoBinario = new LinkedHashMap<>();
        String[] lineas = binario.split("\n");

        for (String linea : lineas) {
            String[] partes = linea.trim().split(":\\t", 2);
            if (partes.length == 2) {
                String clave = partes[0].trim();
                String valor = partes[1].trim();
                CodigoBinario.put(clave, valor);
            }
        }

        return CodigoBinario;
    }

    public static String segmento() {
        String respuesta = "";
        String binario = ByteCode().trim();
        String[] lineas = binario.split("\n");
        int contador = 0;

        for (String linea : lineas) {
            String lineaSinEspacios = linea.replaceAll(" ", "");
            int gruposDe8 = contarGruposDe8(lineaSinEspacios);
            String direccionBinaria = String.format("%08d", Integer.parseInt(Integer.toBinaryString(contador)));
            respuesta += "0000:" + direccionBinaria + ":\t" + linea + "\n";
            contador += gruposDe8;
        }

        return respuesta;
    }

    private static int contarGruposDe8(String cadena) {
        return cadena.length() / 8;
    }

    public static String ByteCode() {
        String respuesta = "";
        String[] instrucciones = instrucciones();
        String opCode, mod, reg, rm;
        boolean bandera;
        String binario;
        int conversion;

        for (int i = 0; i < instrucciones.length; i++) {
            bandera = false;
            try {
                conversion = Integer.parseInt(instrucciones[i]);
                binario = Integer.toBinaryString(conversion);
                respuesta += binario;
                bandera = true;
            } catch (Exception ignored) {}

            if (bandera) {
                continue;
            }

            String instr = instrucciones[i];

            if (instr.equals("MOV")) {
                opCode = "1011 1001 ";
                if (i + 1 < instrucciones.length && instrucciones[i + 1].equals("AX,")) {
                    mod = "10 ";
                    reg = "000 ";
                    rm = "000 ";
                } else if (i + 1 < instrucciones.length && instrucciones[i + 1].equals("BX,")) {
                    mod = "10 ";
                    reg = "110 ";
                    rm = "110 ";
                } else {
                    mod = reg = rm = "";
                }
                if (!respuesta.isEmpty()) respuesta += "\n";
                respuesta += opCode + mod + reg;
            } 
            else if (instr.equals("CMP")) {
                opCode = "1110 1011 ";
                if (i + 2 < instrucciones.length && instrucciones[i + 1].equals("AX,") && instrucciones[i + 2].equals("BX")) {
                    mod = "01 ";
                    reg = "1101 ";
                    rm = "1000 ";
                } else {
                    mod = reg = rm = "";
                }
                respuesta += "\n" + opCode + mod + reg + rm;
            }
            else if (instr.equals("JE")) {
                respuesta += "\n1110 1000 ";
            }
            else if (instr.equals("JMP")) {
                respuesta += "\n1110 1011 ";
            }
            else if (instr.equals("LEA")) {
                // Ejemplo simple para LEA: opcode 10001101 seguido de mod/reg/rm simplificado
                respuesta += "\n1000 1101 "; // Opcode LEA
                if (i + 1 < instrucciones.length) {
                    if (instrucciones[i + 1].equals("BX,")) {
                        respuesta += "10110000"; // mod/reg/rm simplificado
                    }
                }
            }
            else if (instr.equals("INT")) {
                // Opcode INT = 11001101 seguido del número de interrupción
                respuesta += "\n1100 1101 ";
                if (i + 1 < instrucciones.length) {
                    try {
                        int numInt = Integer.decode(instrucciones[i + 1]);
                        String numBin = String.format("%8s", Integer.toBinaryString(numInt & 0xFF)).replace(' ', '0');
                        respuesta += numBin;
                    } catch (Exception e) {
                        respuesta += "00000000"; // valor por defecto si no se puede
                    }
                }
            }
            else if (instr.equals("*")) {
                if (i >= 3 && i + 1 < instrucciones.length) {
                    String resultado = instrucciones[i - 3];
                    String opIzq = instrucciones[i - 1];
                    String opDer = instrucciones[i + 1];
                    respuesta += "\n\t;Multiplicacion\n\tMOV AX, " + opIzq;
                    respuesta += "\n\tMUL AX, " + opDer;
                    respuesta += "\n\tMOV " + resultado + ", AX";
                }
            }
            else if (instr.equals("-")) {
                if (i >= 3 && i + 1 < instrucciones.length) {
                    String resultado = instrucciones[i - 3];
                    String opIzq = instrucciones[i - 1];
                    String opDer = instrucciones[i + 1];
                    respuesta += "\n\t;Resta\n\tMOV AX, " + opIzq;
                    respuesta += "\n\tSUB AX, " + opDer;
                    respuesta += "\n\tMOV " + resultado + ", AX";
                }
            }
            else if (instr.equals("+")) {
                if (i >= 3 && i + 1 < instrucciones.length) {
                    String resultado = instrucciones[i - 3];
                    String opIzq = instrucciones[i - 1];
                    String opDer = instrucciones[i + 1];
                    respuesta += "\n\t;Suma\n\tMOV AX, " + opIzq;
                    respuesta += "\n\tADD AX, " + opDer;
                    respuesta += "\n\tMOV " + resultado + ", AX";
                }
            }
            else if (instr.equals("/")) {
                if (i >= 3 && i + 1 < instrucciones.length) {
                    String resultado = instrucciones[i - 3];
                    String opIzq = instrucciones[i - 1];
                    String opDer = instrucciones[i + 1];
                    respuesta += "\n\t;Division\n\tMOV AX, " + opIzq;
                    respuesta += "\n\tDIV AX, " + opDer;
                    respuesta += "\n\tMOV " + resultado + ", AX";
                }
            }
            else if (instr.equals("=")) {
                if (i + 2 < instrucciones.length && instrucciones[i + 2].equals(";")) {
                    String destino = instrucciones[i - 1];
                    String fuente = instrucciones[i + 1];
                    respuesta += "\n\tMOV AX, " + fuente;
                    respuesta += "\n\tMOV " + destino + ", AX";
                }
            }
        }

        // --- Conversión de variables DW, DD, DB ---
        String texto = Main.txtIntermedio.getText();
        String[] lineas = texto.split("\n");

        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;

            if (linea.contains("DW")) {
                String[] partes = linea.split("\\s+");
                if (partes.length >= 3) {
                    try {
                        int valor = Integer.parseInt(partes[2]);
                        String bin = String.format("%16s", Integer.toBinaryString(valor)).replace(' ', '0');
                        respuesta += "\n" + bin;
                    } catch (Exception e) {
                        respuesta += "\n0000000000000000";
                    }
                } else {
                    respuesta += "\n0000000000000000";
                }
            } 
            else if (linea.contains("DD")) {
                String[] partes = linea.split("\\s+");
                if (partes.length >= 3) {
                    try {
                        float valor = Float.parseFloat(partes[2]);
                        int bits = Float.floatToIntBits(valor);
                        String bin = String.format("%32s", Integer.toBinaryString(bits)).replace(' ', '0');
                        respuesta += "\n" + bin;
                    } catch (Exception e) {
                        respuesta += "\n00000000000000000000000000000000";
                    }
                } else {
                    respuesta += "\n00000000000000000000000000000000";
                }
            }
            else if (linea.contains("DB")) {
                String[] partes = linea.split("\\s+");
                if (partes.length >= 3) {
                    try {
                        int valor = Integer.parseInt(partes[2]);
                        String bin = String.format("%8s", Integer.toBinaryString(valor)).replace(' ', '0');
                        respuesta += "\n" + bin;
                    } catch (Exception e) {
                        respuesta += "\n00000000";
                    }
                } else {
                    respuesta += "\n00000000";
                }
            }
        }

        return respuesta;
    }

    public static String[] instrucciones() {
        String texto = Main.txtIntermedio.getText();
        return texto.split("\\s+");
    }

}
