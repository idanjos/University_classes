/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helloworldswing;
import javax.swing.*;
/**
 *
 * @author mint
 */
public class HelloWorldSwing {

    /**
     * @param args the command line arguments
     */
    private static void createAndShowGUI(){
        JFrame frame = new JFrame("Helloworld");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel label = new JLabel("Hi");
        frame.getContentPane().add(label);
        
        frame.setSize(200, 200);
        //frame.pack();
        
        frame.setVisible(true);
    }
    
    private static void celciusTofar(){
        JFrame frame = new JFrame("Celsius Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JTextField txtbox = new JTextField("0");
        JLabel label = new JLabel("Celcius");
        JButton btn = new JButton("Convert");
        JLabel far = new JLabel("32 Fahrenheit");
        
        frame.getContentPane().add(txtbox);
        frame.getContentPane().add(label);
        frame.getContentPane().add(btn);
        frame.getContentPane().add(far);
        
        frame.setSize(200,200);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                //celciusTofar();
                new CelciusToFahr().setVisible(true);
            }
            
        });
    }
    
}
