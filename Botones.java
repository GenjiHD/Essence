import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Botones implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    // Aquí manejarás los eventos de botones
    if (e.getSource() == Main.btnScanner) {
        Scanner.scan();
        return;
        // Lógica para el botón btnScanner
    } else if (e.getSource() == Main.btnParser) {
        Parser.Parsing();
        // Lógica para el botón btnParser
    } else if (e.getSource() == Main.btnSemantico) {
        Semantico.program();
    } else if (e.getSource() == Main.btnIntermedio) {
        Main.txtIntermedio.setText(Intermedio.variables() + Intermedio.code());
        Parser.aceptacion.clear();
        Parser.variables.clear();
        Parser.idRepetidos.clear();
        Parser.IDValorExacto.clear();
        Parser.expresionMatematica.clear();

    } else if (e.getSource() == Main.btnObjeto) {
        /*Objeto.Codigo();
        Objeto.segmentoCodigo();*/
        Main.txtByteCode.setText(CodigoObjeto.segmento());
        //CodigoObjeto.segmento();
        CodigoObjeto.dezplazamiento();
    } else if (e.getSource() == Main.itemCerrar) {
        System.exit(0);
    } else if (e.getSource() == Main.itemNuevo) {
        Main.txtSourceCode.setText("");
        Main.txtScanner.setText("");
        Main.txtParser.setText("");
        Main.txtSemantico.setText("");
        Main.txtIntermedio.setText("");

    } else if (e.getSource() == Main.itemGuardar) {
        JFileChooser fileChooser = new JFileChooser("essence\\Pruebas");
        int seleccion = fileChooser.showSaveDialog(null);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();

            try {
                FileWriter fileWriter = new FileWriter(archivo);
                fileWriter.write(Main.txtSourceCode.getText());
                fileWriter.close();
                JOptionPane.showMessageDialog(null, "Archivo guardado exitosamente.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al guardar el archivo.");
            }
        }
    } else if (e.getSource() == Main.itemAbrir) {
        JFileChooser fileChooser = new JFileChooser("essence/Pruebas"); // Mover la creación aquí
        int seleccion = fileChooser.showOpenDialog(null); // Mover la llamada aquí
        Main.fileDocumento = fileChooser.getSelectedFile();

        // Luego, puedes leer el contenido del archivo y mostrarlo en txtProgramaFuente
        try {
            FileReader fileReader = new FileReader(Main.fileDocumento);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder contenido = new StringBuilder();
            String linea;

            while ((linea = bufferedReader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
            bufferedReader.close();
            Main.txtSourceCode.setText(contenido.toString());
        } catch (IOException ex) {
            ex.printStackTrace(); // Manejo de errores
        }
    }
}
}
