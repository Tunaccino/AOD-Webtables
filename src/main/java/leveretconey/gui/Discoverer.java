package leveretconey.gui;

import leveretconey.dependencyDiscover.Parallel.RunParallel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Discoverer extends JFrame{
    private JPanel contentPane;
    private JLabel label;
    private JCheckBox normalCheckBox;
    private JCheckBox filteringCheckBox;
    private JCheckBox distributedCheckBox;
    private JCheckBox parallelCheckBox;
    private JButton discoverButton;
    private JProgressBar progressBar;
    private JTextField inputF;
    private JTextField ipF;
    private JTextField outputF;
    private JCheckBox formattedCheckBox;
    private JCheckBox CSVCheckBox;
    private JCheckBox otherCheckBox;
    private JLabel ipLabel;

    Discoverer(){
       setContentPane(contentPane);
       setTitle("Discover");
       setSize(800, 600);
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       setResizable(false);
       setVisible(true);

       normalCheckBox.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               ipF.setVisible(false);
               ipLabel.setVisible(false);
               parallelCheckBox.setSelected(false);
               distributedCheckBox.setSelected(false);
           }
       });

       parallelCheckBox.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               ipF.setVisible(false);
               ipLabel.setVisible(false);
               normalCheckBox.setSelected(false);
               distributedCheckBox.setSelected(false);
           }
       });


       distributedCheckBox.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               ipF.setVisible(true);
               ipLabel.setVisible(true);
               normalCheckBox.setSelected(false);
               parallelCheckBox.setSelected(false);
           }
       });


        discoverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RunParallel runner = new RunParallel(inputF.getText(), outputF.getText());
                progressBar.setStringPainted(true);
                progressBar.isIndeterminate();
                runner.run();
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(Discoverer::new);
    }

}
