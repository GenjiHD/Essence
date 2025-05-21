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
        String[] lineas = Main.txtIntermedio.getText().split("\n");
        HashMap<String, Integer> mapaSaltos = new HashMap<>();
        HashMap<String, String> mapaVariables = new HashMap<>();
        StringBuilder respuesta = new StringBuilder();
        int posicion = 0;
        int contadorDireccionVariable = 1000;

        // Primero, mapear etiquetas
        for (String linea : lineas) {
            String[] instrucciones = linea.split(" ");
            if (instrucciones[0].endsWith(":")) {
                mapaSaltos.put(instrucciones[0].replace(":", ""), posicion);
            } else {
                posicion++;
            }
        }

        // Luego traducir a binario
        posicion = 0;
        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;

            // Instrucciones de datos
            if (linea.contains("DW")) {
                String[] partes = linea.split("\\s+");
                if (partes.length >= 3) {
                    try {
                        int valor = Integer.parseInt(partes[2]);
                        String bin = String.format("%16s", Integer.toBinaryString(valor)).replace(' ', '0');
                        respuesta.append("\n").append(bin);
                    } catch (Exception e) {
                        respuesta.append("\n0000000000000000");
                    }
                } else {
                    respuesta.append("\n0000000000000000");
                }
                continue;
            } else if (linea.contains("DD")) {
                String[] partes = linea.split("\\s+");
                if (partes.length >= 3) {
                    try {
                        float valor = Float.parseFloat(partes[2]);
                        int bits = Float.floatToIntBits(valor);
                        String bin = String.format("%32s", Integer.toBinaryString(bits)).replace(' ', '0');
                        respuesta.append("\n").append(bin);
                    } catch (Exception e) {
                        respuesta.append("\n00000000000000000000000000000000");
                    }
                } else {
                    respuesta.append("\n00000000000000000000000000000000");
                }
                continue;
            } else if (linea.contains("DB")) {
                int index = linea.indexOf("'");
                if (index != -1 && linea.endsWith("'")) {
                    String contenido = linea.substring(index + 1, linea.length() - 1);
                    for (char c : contenido.toCharArray()) {
                        String bin = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
                        respuesta.append("\n").append(bin);
                    }
                } else {
                    respuesta.append("\n00000000");
                }
                continue;
            }

            // Instrucciones de código
            String[] instrucciones = linea.split(" ");
            if (instrucciones[0].endsWith(":")) continue;

            for (int i = 0; i < instrucciones.length; i++) {
                boolean bandera = false;

                // Instrucciones principales
                if (instrucciones[i].equals("MOV")) {
                    respuesta.append("10110000").append("\n");
                    bandera = true;
                } else if (instrucciones[i].equals("CMP")) {
                    respuesta.append("00111000").append("\n");
                    bandera = true;
                } else if (instrucciones[i].equals("JE")) {
                    respuesta.append("01110100").append("\n");
                    i++;
                    int destino = mapaSaltos.getOrDefault(instrucciones[i], 0);
                    int desplazamiento = destino - posicion - 1;
                    String bin = rellenarA8Bits(Integer.toBinaryString(desplazamiento & 0xFF));
                    respuesta.append(bin).append("\n");
                    bandera = true;
                } else if (instrucciones[i].equals("JMP")) {
                    respuesta.append("11101001").append("\n");
                    i++;
                    int destino = mapaSaltos.getOrDefault(instrucciones[i], 0);
                    int desplazamiento = destino - posicion - 1;
                    String bin = rellenarA8Bits(Integer.toBinaryString(desplazamiento & 0xFF));
                    respuesta.append(bin).append("\n");
                    bandera = true;
                } else if (instrucciones[i].equals("INT")) {
                    respuesta.append("11001101").append("\n");
                    i++;
                    String valor = instrucciones[i];
                    int numero;
                    try {
                        if (valor.startsWith("0x")) {
                            numero = Integer.parseInt(valor.substring(2), 16);
                        } else {
                            numero = Integer.parseInt(valor);
                        }
                        String binario = rellenarA8Bits(Integer.toBinaryString(numero));
                        respuesta.append(binario).append("\n");
                        bandera = true;
                    } catch (Exception e) {
                        respuesta.append("00000000").append("\n");
                        bandera = true;
                    }
                }

                // Variables
                if (!bandera) {
                    String posibleVar = instrucciones[i].replace(",", "");
                    if (!posibleVar.matches("[0-9]+") && !posibleVar.startsWith("0x")) {
                        if (!mapaVariables.containsKey(posibleVar)) {
                            String direccionBin = String.format("%8s", Integer.toBinaryString(contadorDireccionVariable)).replace(' ', '0');
                            mapaVariables.put(posibleVar, direccionBin);
                            contadorDireccionVariable++;
                        }
                        respuesta.append(mapaVariables.get(posibleVar)).append("\n");
                        bandera = true;
                    }
                }

                // Números literales
                if (!bandera) {
                    try {
                        int numero = Integer.parseInt(instrucciones[i].replace(",", ""));
                        String binario = rellenarA8Bits(Integer.toBinaryString(numero));
                        respuesta.append(binario).append("\n");
                        bandera = true;
                    } catch (Exception ignored) {}
                }
            }

            posicion++;
        }

        return respuesta.toString();
    }


    private static String[] instrucciones() {
        String texto = Main.txtIntermedio.getText();
        String[] tokens = texto.split("\\s+");
        return tokens;
    }

    private static String rellenarA8Bits(String binario) {
        int longitud = binario.length();
        if (longitud < 8) {
            return "0".repeat(8 - longitud) + binario;
        } else if (longitud > 8) {
            return binario.substring(longitud - 8);
        } else {
            return binario;
        }
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
