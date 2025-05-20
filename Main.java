

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    // Instanciamos todos los componentes gráficos que necesitaremos
    static JFrame window = new JFrame("essence compiler");
    static JPanel panel = new JPanel(null);
    JLabel lblSourceCode = new JLabel("Programa Fuente:");
    JLabel lblScanner = new JLabel("Scanner:");
    JLabel lblParser = new JLabel("Parser:");
    JLabel lblSemantico = new JLabel("Semantico:");
    JLabel lblIntermedio = new JLabel("Intermedio:");
    JLabel lblObjeto = new JLabel(("Codigo Objeto:"));
    static JTextPane txtSourceCode = new JTextPane();
    static JTextArea txtScanner = new JTextArea();
    static JTextArea txtParser = new JTextArea();
    static JTextArea txtSemantico = new JTextArea();
    static JTextArea txtIntermedio = new JTextArea();
    static JTextArea txtObjeto = new JTextArea();
    static JTextArea txtSegmento = new JTextArea();

    JScrollPane scrollSourceCode = new JScrollPane(txtSourceCode);
    JScrollPane scrollScanner = new JScrollPane(txtScanner);
    JScrollPane scrollParser = new JScrollPane(txtParser);
    JScrollPane scrollSemantico = new JScrollPane(txtSemantico);
    JScrollPane scrollIntermedio = new JScrollPane(txtIntermedio);
    JScrollPane scrollObjeto = new JScrollPane(txtObjeto);
    JScrollPane scrollSegmento = new JScrollPane(txtSegmento);
    static JTextArea txtByteCode = new JTextArea();

    JScrollPane scrollByteCode = new JScrollPane(txtByteCode);
    static JButton btnScanner = new JButton("Scanner");
    static JButton btnParser = new JButton("Parser");
    static JButton btnSemantico = new JButton("Semantico");
    static JButton btnIntermedio = new JButton("Intermedio");
    static JButton btnObjeto = new JButton("Objeto");
    static JButton btnEjecutaTodo = new JButton();

    static File fileDocumento;
    JMenu menuArchivo = new JMenu("Archivo");
    static JMenuItem itemNuevo = new JMenuItem("Nuevo");
    static JMenuItem itemAbrir = new JMenuItem("Abrir");
    static JMenuItem itemGuardar = new JMenuItem("Guardar");
    static JMenuItem itemCerrar = new JMenuItem("Salir");
    JMenuBar barArchivo = new JMenuBar();

    JTabbedPane tbdArchivo = new JTabbedPane(); // El componente JTabbedPane


    // Obtiene el tamaño de la pantalla
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    // Cargar las imagenes
    ImageIcon imgEjecutaTodo = new ImageIcon("src/CypherScript/img/btnEjecutaTodo.png");
    ImageIcon imgLogo = new ImageIcon("src/CypherScript/img/logo.png");

    Font fontIntermedio = new Font("Arial", Font.PLAIN, 22);
    Font fontSourceCode = new Font("Arial", Font.BOLD, 22);
    Font fontScanner = new Font("Arial", Font.PLAIN, 23);
    Font fontSemantico_Parser = new Font("Arial", Font.PLAIN, 33);
    Font fontLabels = new Font("Arial", Font.PLAIN, 25).deriveFont(30.0f);


    public Main() {

        //Establezco la window
        window.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
        panel.setBackground(Color.WHITE);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Configuro todos los textArea
        scrollSourceCode.setBounds(10, 80, 430, 300);
        txtSourceCode.setFont(fontSourceCode);
        txtSourceCode.setBackground(Color.LIGHT_GRAY);
        panel.add(scrollSourceCode);

        scrollScanner.setBounds(450, 80, 500, 300);
        txtScanner.setEditable(false);
        txtScanner.setFont(fontScanner);
        txtScanner.setBackground(Color.LIGHT_GRAY);
        panel.add(scrollScanner);

        scrollParser.setBounds(990, 80, 340, 120);
        txtParser.setFont(fontSemantico_Parser);
        txtParser.setEditable(false);
        txtParser.setBackground(Color.LIGHT_GRAY);
        panel.add(scrollParser);

        scrollSemantico.setBounds(990, 260, 340, 120);
        txtSemantico.setFont(fontSemantico_Parser);
        txtSemantico.setEditable(false);
        txtSemantico.setBackground(Color.LIGHT_GRAY);
        panel.add(scrollSemantico);

        scrollIntermedio.setBounds(10, 440, 600, 250);
        txtIntermedio.setEditable(false);
        txtIntermedio.setFont(fontIntermedio);
        txtIntermedio.setBackground(Color.LIGHT_GRAY);
        panel.add(scrollIntermedio);

        scrollObjeto.setBounds(850, 440, 480, 250);
        txtObjeto.setEditable(false);
        txtObjeto.setFont(fontIntermedio);
        txtObjeto.setBackground(Color.LIGHT_GRAY);
        panel.add(scrollObjeto);

        scrollSegmento.setBounds(650,440,600,250);
        txtSegmento.setEditable(false);
        txtSegmento.setFont(fontIntermedio);
        txtSegmento.setBackground(Color.LIGHT_GRAY);
        panel.add(scrollSegmento);

        txtObjeto.setVisible(false);
        scrollObjeto.setVisible(false);
        txtSegmento.setVisible(false);
        scrollSegmento.setVisible(false);

        scrollByteCode.setBounds(650,440,600,250);
        txtByteCode.setEditable(false);
        txtByteCode.setFont(fontIntermedio);
        txtByteCode.setBackground(Color.LIGHT_GRAY);
        panel.add(scrollByteCode);

        //Agrego los labels
        lblSourceCode.setBounds(10, 25, 260, 50);
        lblSourceCode.setFont(fontLabels);
        panel.add(lblSourceCode);
        lblScanner.setBounds(450, 25, 150, 50);
        lblScanner.setFont(fontLabels);
        panel.add(lblScanner);
        lblParser.setBounds(990, 25, 150, 50);
        lblParser.setFont(fontLabels);
        panel.add(lblParser);
        lblSemantico.setBounds(990, 210, 150, 50);
        lblSemantico.setFont(fontLabels);
        panel.add(lblSemantico);
        lblIntermedio.setBounds(10, 390, 150, 50);
        lblIntermedio.setFont(fontLabels);
        panel.add(lblIntermedio);
        lblObjeto.setBounds(650, 390, 250, 50);
        lblObjeto.setFont(fontLabels);
        panel.add(lblObjeto);

        // Configuro los botones
        btnScanner.setBounds(860, 25, 90, 40);
        btnScanner.setBackground(Color.BLUE);
        panel.add(btnScanner);

        btnParser.setBounds(1240, 25, 90, 40);
        btnParser.setBackground(Color.BLUE);
        panel.add(btnParser);

        btnSemantico.setBounds(1230, 210, 100, 40);
        btnSemantico.setBackground(Color.BLUE);
        panel.add(btnSemantico);

        btnIntermedio.setBounds(510, 390, 100, 40);
        btnIntermedio.setBackground(Color.BLUE);
        panel.add(btnIntermedio);

        btnObjeto.setBounds(1230, 390, 100, 40);
        btnObjeto.setBackground(Color.BLUE);
        panel.add(btnObjeto);

        //agrego el menu
        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemCerrar);
        barArchivo.add(menuArchivo);
        window.setJMenuBar(barArchivo);

        //agrego los botones de los botones y demas componentes, lo module en otra clase
        // para que estuviera mas limpio el codigo
        Botones botones = new Botones();
        btnScanner.addActionListener(botones);
        btnParser.addActionListener(botones);
        btnSemantico.addActionListener(botones);
        btnIntermedio.addActionListener(botones);
        btnObjeto.addActionListener(botones);
        btnEjecutaTodo.addActionListener(botones);
        itemCerrar.addActionListener(botones);
        itemNuevo.addActionListener(botones);
        itemAbrir.addActionListener(botones);
        itemGuardar.addActionListener(botones);

        //Agrego el panel a la window y lo hago visible
        window.add(panel);
        window.setVisible(true);
    }
    static void valida() {
        window.revalidate();
        window.validate();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }
}