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

                    // Mostrar con agrupación de 4 bits
                    String valorFormateado = formatearBinarioConEspacios(entry.getValue());
                    respuesta += direccionBinaria + "\t" + valorFormateado + "\n";
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
        StringBuilder respuesta = new StringBuilder();
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
                binario = rellenarA8Bits(binario);
                respuesta.append(binario).append("\n");
                bandera = true;
            } catch (Exception ignored) {}

            if (bandera) {
                continue;
            }

            String instr = instrucciones[i];

            if (instr.equals("MOV")) {
                opCode = "10111001";  // Corregí para que no tenga espacios y sea un byte completo
                if (i + 1 < instrucciones.length && instrucciones[i + 1].equals("AX,")) {
                    mod = "10";
                    reg = "000";
                    rm = "000";
                } else if (i + 1 < instrucciones.length && instrucciones[i + 1].equals("BX,")) {
                    mod = "10";
                    reg = "110";
                    rm = "110";
                } else {
                    mod = reg = rm = "";
                }
                String codigo = opCode + mod + reg + rm;
                codigo = rellenarA8Bits(codigo);
                respuesta.append(codigo).append("\n");
            } 
            else if (instr.equals("CMP")) {
                opCode = "11101011";
                if (i + 2 < instrucciones.length && instrucciones[i + 1].equals("AX,") && instrucciones[i + 2].equals("BX")) {
                    mod = "01";
                    reg = "1101";
                    rm = "1000";
                } else {
                    mod = reg = rm = "";
                }
                String codigo = opCode + mod + reg + rm;
                codigo = rellenarA8Bits(codigo);
                respuesta.append(codigo).append("\n");
            }
            else if (instr.equals("JE")) {
                String codigo = "11101000";
                codigo = rellenarA8Bits(codigo);
                respuesta.append(codigo).append("\n");
            }
            else if (instr.equals("JMP")) {
                String codigo = "11101011";
                codigo = rellenarA8Bits(codigo);
                respuesta.append(codigo).append("\n");
            }
            else if (instr.equals("LEA")) {
                // Ejemplo simple para LEA: opcode 10001101 seguido de mod/reg/rm simplificado
                String codigo = "10001101";
                if (i + 1 < instrucciones.length) {
                    if (instrucciones[i + 1].equals("BX,")) {
                        codigo += "10110000"; // mod/reg/rm simplificado
                    }
                }
                codigo = rellenarA8Bits(codigo);
                respuesta.append(codigo).append("\n");
            }
            else if (instr.equals("INT")) {
                // Opcode INT = 11001101 seguido del número de interrupción (1 byte)
                String codigo = "11001101";
                if (i + 1 < instrucciones.length) {
                    try {
                        int numInt = Integer.decode(instrucciones[i + 1]); // acepta hex como 0xXX
                        String numBin = String.format("%8s", Integer.toBinaryString(numInt & 0xFF)).replace(' ', '0');
                        codigo += numBin; // concatena el byte de número
                    } catch (Exception e) {
                        codigo += "00000000"; // por si hay error
                    }
                } else {
                    codigo += "00000000"; // si no hay número
                }
                // Ya tiene 16 bits (2 bytes) — NO rellenar a 8 bits más, porque estaría mal.
                respuesta.append(codigo).append("\n");
            }
            else if (instr.equals("*")) {
                if (i >= 3 && i + 1 < instrucciones.length) {
                    String resultado = instrucciones[i - 3];
                    String opIzq = instrucciones[i - 1];
                    String opDer = instrucciones[i + 1];
                    respuesta.append("\n\t;Multiplicacion\n\tMOV AX, ").append(opIzq);
                    respuesta.append("\n\tMUL AX, ").append(opDer);
                    respuesta.append("\n\tMOV ").append(resultado).append(", AX\n");
                }
            }
            else if (instr.equals("-")) {
                if (i >= 3 && i + 1 < instrucciones.length) {
                    String resultado = instrucciones[i - 3];
                    String opIzq = instrucciones[i - 1];
                    String opDer = instrucciones[i + 1];
                    respuesta.append("\n\t;Resta\n\tMOV AX, ").append(opIzq);
                    respuesta.append("\n\tSUB AX, ").append(opDer);
                    respuesta.append("\n\tMOV ").append(resultado).append(", AX\n");
                }
            }
            else if (instr.equals("+")) {
                if (i >= 3 && i + 1 < instrucciones.length) {
                    String resultado = instrucciones[i - 3];
                    String opIzq = instrucciones[i - 1];
                    String opDer = instrucciones[i + 1];
                    respuesta.append("\n\t;Suma\n\tMOV AX, ").append(opIzq);
                    respuesta.append("\n\tADD AX, ").append(opDer);
                    respuesta.append("\n\tMOV ").append(resultado).append(", AX\n");
                }
            }
            else if (instr.equals("/")) {
                if (i >= 3 && i + 1 < instrucciones.length) {
                    String resultado = instrucciones[i - 3];
                    String opIzq = instrucciones[i - 1];
                    String opDer = instrucciones[i + 1];
                    respuesta.append("\n\t;Division\n\tMOV AX, ").append(opIzq);
                    respuesta.append("\n\tDIV AX, ").append(opDer);
                    respuesta.append("\n\tMOV ").append(resultado).append(", AX\n");
                }
            }
        }

        return respuesta.toString();
    }

    private static String[] instrucciones() {
        String texto = Main.txtIntermedio.getText();
        String[] tokens = texto.split("\\s+");
        return tokens;
    }

    // Solo rellena si la longitud es menor a 8 bits, para completar 1 byte
    private static String rellenarA8Bits(String binario) {
        binario = binario.replaceAll(" ", "");
        int length = binario.length();

        if (length < 8) {
            int faltan = 8 - length;
            binario += "0".repeat(faltan);
        }
        // No rellena si ya tiene 8 o más bits
        return binario;
    }

    public static String formatearBinarioConEspacios(String binario) {
        binario = binario.replaceAll(" ", "");
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < binario.length(); i++) {
            resultado.append(binario.charAt(i));
            if ((i + 1) % 4 == 0 && i != binario.length() - 1) {
                resultado.append(' ');
            }
        }

        return resultado.toString();
    }
}
