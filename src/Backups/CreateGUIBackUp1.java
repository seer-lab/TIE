package Backups;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CreateGUIBackUp1 extends JFrame{
	
	// to generate random numbers
	private final int m = 256;
	private final int a = 67;
	private final int c = 27;
	private int x_new = 33;
	private int x_prev = 33;

	public CreateGUIBackUp1(int trace_Num, int thread_Num, int transition_Num, Vector<Integer> transition_states, Vector<String> transition_states_info){
		int state = 0;
		int randomR = 0;
		int randomG = 0;
		int randomB = 0;
		
		JPanel test = new JPanel();
		test.setLayout(new FlowLayout());
		
		Container pane = new Container();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		// Defining a Color to each thread
		Color[] threadColor = new Color[thread_Num];
		for (int i=0; i < thread_Num; i++){
			randomR = this.genRandomNumbers();
			randomG = this.genRandomNumbers();
			randomB = this.genRandomNumbers();
						
			// print
			//System.out.println("Red: " + randomR);
			//System.out.println("Green: " + randomG);
			//System.out.println("Blue: " + randomB);
			
			// fill color
			threadColor[i] = new Color(randomR, randomG, randomB);
		}
		
		// Text Area for transitions
		final JTextArea transitions = new JTextArea("");
		transitions.setColumns(100);
		transitions.setRows(20);
		transitions.setBackground(Color.white);
		
		JScrollPane scrollingArea = new JScrollPane(transitions);
		
		// Adding buttons for each transition
		JButton[] threadButton = new JButton[transition_Num];
		for(int i = 0; i < transition_Num; i++){
			threadButton[i] = new JButton("Transition: " + i);
		}
		
		for(int i=0; i < transition_states.size(); i++){
		   final String info = transition_states_info.get(i);
		   
		   c.gridx = 0;
		   c.gridy = i;
		   
		   state = transition_states.get(i);
		   System.out.println(i + " --> " + state);
		   threadButton[i].setText("Transition #" + i + " --> Thread: " + state);
		   threadButton[i].setBackground(threadColor[state]);
		   
		   // when you press this transition button, the transition info is displayed
		   threadButton[i].addActionListener(
				    new ActionListener() {
				        public void actionPerformed(ActionEvent e) {
				           transitions.setText(info);
				        }
				    }
				);

		   
		   pane.add(threadButton[i], c);
		}
		
        // defaults
		test.add(pane);
		test.add(scrollingArea);
        this.add(test);
        
        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
	}
	
	
	/*
	 * This method generates pseudo random numbers using the linear congruential method
	 * m = 256
	 * a = 67
	 * c = 27
	 * x0 = 33
	 * Since there are 3 values for RBG, this method can support up to 84 threads with different colours
	 */
	public int genRandomNumbers(){
		x_new = ((a*x_prev) + c ) % m;		
		x_prev = x_new;
		return x_new;
	}
}
