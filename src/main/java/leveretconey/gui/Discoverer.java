package leveretconey.gui;

import leveretconey.dependencyDiscover.Parallel.RunParallel;
import leveretconey.pre.transformer.Transformer;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

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
       inputF.setEditable(false);
       outputF.setEditable(false);

       normalCheckBox.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               ipF.setVisible(false);
               ipLabel.setVisible(false);
               parallelCheckBox.setSelected(false);
               distributedCheckBox.setSelected(false);
               progressBar.setStringPainted(false);
           }
       });

       normalCheckBox.addItemListener(new ItemListener() {
           @Override
           public void itemStateChanged(ItemEvent e) {
               inputF.setEditable(e.getStateChange() == ItemEvent.SELECTED);
               outputF.setEditable(e.getStateChange() == ItemEvent.SELECTED);
               progressBar.setStringPainted(false);
           }
       });

       parallelCheckBox.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               ipF.setVisible(false);
               ipLabel.setVisible(false);
               normalCheckBox.setSelected(false);
               distributedCheckBox.setSelected(false);
               progressBar.setStringPainted(false);
           }
       });

        parallelCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                inputF.setEditable(e.getStateChange() == ItemEvent.SELECTED);
                outputF.setEditable(e.getStateChange() == ItemEvent.SELECTED);
            }
        });


       distributedCheckBox.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               ipF.setVisible(true);
               ipLabel.setVisible(true);
               normalCheckBox.setSelected(false);
               parallelCheckBox.setSelected(false);
               progressBar.setStringPainted(false);
           }
       });

        distributedCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                inputF.setEditable(e.getStateChange() == ItemEvent.SELECTED);
                outputF.setEditable(e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        discoverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if((!inputF.getText().trim().isEmpty()&&!outputF.getText().trim().isEmpty()) && (formattedCheckBox.isSelected() || CSVCheckBox.isSelected() || otherCheckBox.isSelected())
                && ((distributedCheckBox.isSelected() && !ipF.getText().trim().isEmpty()) || !distributedCheckBox.isSelected())) {
                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {

                            RunParallel runner = new RunParallel(inputF.getText(), outputF.getText());
                            runner.run();
                            return null;
                        }

                        @Override
                        protected void done() {
                            progressBar.setIndeterminate(false);
                            progressBar.setValue(100);
                            progressBar.setString("Fertig!");
                        }
                    };
                    worker.execute();
                }else {
                    progressBar.setStringPainted(true);
                    progressBar.setString("Incomplete selection !!!");
                }
            }
        });


        inputF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setStringPainted(false);
            }
        });
        outputF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setStringPainted(false);
            }
        });
        ipF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setStringPainted(false);
            }
        });
        formattedCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setStringPainted(false);
                progressBar.setIndeterminate(true);
                CSVCheckBox.setSelected(false);
                otherCheckBox.setSelected(false);
                filteringCheckBox.setSelected(false);
            }
        });
        CSVCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setStringPainted(false);
                progressBar.setIndeterminate(true);
                formattedCheckBox.setSelected(false);
                otherCheckBox.setSelected(false);
            }
        });
        otherCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressBar.setStringPainted(false);
                progressBar.setIndeterminate(true);
                formattedCheckBox.setSelected(false);
                CSVCheckBox.setSelected(false);
            }
        });
        filteringCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formattedCheckBox.setSelected(false);
            }
        });
    }

    public void taskFinder() throws IOException {
        if(normalCheckBox.isSelected()){

        } else if(parallelCheckBox.isSelected()){

        } else if(distributedCheckBox.isSelected()){

        }
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
