package UI;

import Model.Grafo;
import Persistance.CustomOutputStream;
import Persistance.DataLoader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import static Model.Grafo.RDFSREASONER;
import static Model.Grafo.SIMPLEREASONER;
import static Model.Grafo.TURTTLE;

public class UI {
    public int selected = RDFSREASONER;

    private Grafo grafo;
    private JPanel contentPane;
    private JButton okButton;
    private JRadioButton rdfsReasonerRadioButton;
    private JRadioButton simpleReasonerRadioButton;
    private JTextField outputPath;
    private JPanel typePanel;
    private JPanel filePanel;
    private JPanel actionPanel;
    private JButton fileChooserButton;
    private JTextArea serialArea;
    private JScrollPane scrollPane;
    private JTextField outputFileName;

    public UI() {
        System.setProperty("javax.net.ssl.trustStore", "keystore.jks");
        grafo = new Grafo("datos.ttl", "esquema.ttl");
        grafo.union(DataLoader.cargaGrafoEstaciones());
        outputPath.setText(System.getProperty("user.dir"));
        fileChooserButton.addActionListener(filechooserListener());
        rdfsReasonerRadioButton.addActionListener(typeStateChange(rdfsReasonerRadioButton, RDFSREASONER));
        simpleReasonerRadioButton.addActionListener(typeStateChange(simpleReasonerRadioButton, SIMPLEREASONER));
        okButton.addActionListener(okActionListener());
    }

    private ActionListener okActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String file = outputPath.getText() + "\\" + outputFileName.getText();
                serialArea.setText(null);
                try {
                    CustomOutputStream output = new CustomOutputStream(serialArea, file);
                    grafo.print(output, TURTTLE);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        };
    }

    private ActionListener typeStateChange(JRadioButton button, int state) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (button.isSelected()) selected = state;
            }
        };
    }

    private ActionListener filechooserListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int open = fc.showOpenDialog(contentPane);
                if (open == JFileChooser.APPROVE_OPTION) {
                    outputPath.setText(fc.getSelectedFile().getAbsolutePath());
                }
            }
        };
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tarea 2. Juan Francisco Pérez");
        frame.setContentPane(new UI().contentPane);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
