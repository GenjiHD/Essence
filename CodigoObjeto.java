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

            if (entry.getValue().equals("11101000")) {
                partidaCondicion = entry.getKey();
                partidaNumero = obtenerNumeroDesdeDireccion(partidaCondicion);
            } else if (entry.getValue().equals("11101011")) {
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
                    respuesta += direccionBinaria + "\t" + formatearABitsDe8(entry.getValue()) + "\n";
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
            String[] partes = linea.trim().split(":\t", 2);
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
            int gruposDe8 = (lineaSinEspacios.length() + 7) / 8;
            String direccionBinaria = String.format("%08d", Integer.parseInt(Integer.toBinaryString(contador)));
            respuesta += "0000:" + direccionBinaria + ":\t" + formatearABitsDe8(lineaSinEspacios) + "\n";
            contador += gruposDe8;
        }

        return respuesta;
    }

    public static String ByteCode() {
        StringBuilder respuesta = new StringBuilder();
        String[] instrucciones = instrucciones();
        boolean bandera;

        for (int i = 0; i < instrucciones.length; i++) {
            bandera = false;
            try {
                int conversion = Integer.parseInt(instrucciones[i]);
                String binario = Integer.toBinaryString(conversion);
                respuesta.append(formatearABitsDe8(binario));
                bandera = true;
            } catch (Exception ignored) {}

            if (bandera) continue;

            String instr = instrucciones[i];

            if (instr.equals("MOV")) {
                respuesta.append("\n10111001");
            } else if (instr.equals("CMP")) {
                respuesta.append("\n11101011");
            } else if (instr.equals("JE")) {
                respuesta.append("\n11101000");
            } else if (instr.equals("JMP")) {
                respuesta.append("\n11101011");
            } else if (instr.equals("LEA")) {
                respuesta.append("\n10001101");
                if (i + 1 < instrucciones.length && instrucciones[i + 1].equals("BX,")) {
                    respuesta.append("10110000");
                }
            } else if (instr.equals("INT")) {
                respuesta.append("\n11001101");
                if (i + 1 < instrucciones.length) {
                    try {
                        int numInt = Integer.decode(instrucciones[i + 1]);
                        respuesta.append(formatearABitsDe8(String.format("%8s", Integer.toBinaryString(numInt & 0xFF)).replace(' ', '0')));
                    } catch (Exception e) {
                        respuesta.append("00000000");
                    }
                }
            }
        }

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
                        respuesta.append("\n").append(formatearABitsDe8(bin));
                    } catch (Exception e) {
                        respuesta.append("\n0000000000000000");
                    }
                }
            } else if (linea.contains("DD")) {
                String[] partes = linea.split("\\s+");
                if (partes.length >= 3) {
                    try {
                        float valor = Float.parseFloat(partes[2]);
                        int bits = Float.floatToIntBits(valor);
                        String bin = String.format("%32s", Integer.toBinaryString(bits)).replace(' ', '0');
                        respuesta.append("\n").append(formatearABitsDe8(bin));
                    } catch (Exception e) {
                        respuesta.append("\n00000000000000000000000000000000");
                    }
                }
            } else if (linea.contains("DB")) {
                String[] partes = linea.split("\\s+");
                if (partes.length >= 3) {
                    try {
                        int valor = Integer.parseInt(partes[2]);
                        String bin = String.format("%8s", Integer.toBinaryString(valor)).replace(' ', '0');
                        respuesta.append("\n").append(formatearABitsDe8(bin));
                    } catch (Exception e) {
                        respuesta.append("\n00000000");
                    }
                }
            }
        }

        return respuesta.toString();
    }

    public static String[] instrucciones() {
        String texto = Main.txtIntermedio.getText();
        return texto.split("\\s+");
    }

    private static String formatearABitsDe8(String binario) {
        binario = binario.replaceAll(" ", "");
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < binario.length(); i += 8) {
            int fin = Math.min(i + 8, binario.length());
            String byte8 = binario.substring(i, fin);
            if (byte8.length() < 8) {
                byte8 = String.format("%-8s", byte8).replace(' ', '0');
            }
            // Separar en dos grupos de 4 bits
            String nibbleFormateado = byte8.substring(0, 4) + " " + byte8.substring(4, 8);
            resultado.append(nibbleFormateado).append(" ");
        }
        return resultado.toString().trim();
    }
}
