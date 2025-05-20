
public class Segmento {

    public static void main(String[] args) {

        int numero =  10;

        // Convertir a hexadecimal
        String binario = Integer.toBinaryString(numero);

        System.out.println("Número en hexadecimal: " + binario);

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
}
