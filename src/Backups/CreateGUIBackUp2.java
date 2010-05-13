package Backups;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CreateGUIBackUp2 extends JFrame{
	
	// to generate random numbers
	private final int m = 256;
	private final int a = 67;
	private final int c = 27;
	private int x_new = 33;
	private int x_prev = 33;

	public CreateGUIBackUp2(int trace_Num, int thread_Num, List<Integer> transition_Num, List<List<Integer>> transition_states, List<List<String>> transition_states_info){
		int state = 0;
		int randomR = 0;
		int randomG = 0;
		int randomB = 0;
		
		JPanel test = new JPanel();
		test.setLayout(new GridLayout(1, trace_Num));
		
		Vector<Container> panesArray = new Vector();
		Vector<Container> panesArrayWrap = new Vector();
		//List<List<JButton>> buttons = new ArrayList<List<JButton>>();

		// Defining a Color to each thread
		Color[] threadColor = new Color[thread_Num];
		   for (int i=0; i < thread_Num; i++){						
			
				 randomR = this.genRandomNumbers();
				 randomG = this.genRandomNumbers();
				 randomB = this.genRandomNumbers();
			   
			// fill color
			threadColor[i] = new Color(randomR, randomG, randomB);
		  }		

		for(int j = 0; j < trace_Num; j++){
			panesArray.add(new Container());
			panesArrayWrap.add(new Container());
			
			//buttons.add(new ArrayList<JButton>());
			
			panesArray.get(j).setLayout(new GridBagLayout());
			panesArrayWrap.get(j).setLayout(new BorderLayout());
			
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			
     	  // Text Area for transitions
		  final JTextArea transitions = new JTextArea("");
		  transitions.setColumns(100);
		  transitions.setRows(20);
		  transitions.setBackground(Color.white);
		
		  JScrollPane scrollingArea = new JScrollPane(transitions);
		
		  // Adding buttons for each transition
		  JButton[] threadButton = new JButton[transition_Num.get(j)];
		 // System.out.println(j + " --> Size: " + transition_Num.get(j));
		  for(int i = 0; i < transition_Num.get(j); i++){
			threadButton[i] = new JButton("Transition: " + i);
		  }
		
		  for(int i=0; i < transition_states.get(j).size(); i++){
		     final String info = transition_states_info.get(j).get(i);
		   
		     c.gridx = 0;
		     c.gridy = i;
		   
		     state = transition_states.get(j).get(i);
		     threadButton[i].setText("");
		     threadButton[i].setBackground(threadColor[state]);
		   
		     // when you press this transition button, the transition info is displayed
		     threadButton[i].addActionListener(
				    new ActionListener() {
				        public void actionPerformed(ActionEvent e) {
				           transitions.setText(info);
				        }
				    }
				);

		   
		     panesArray.get(j).add(threadButton[i], c);
		     panesArrayWrap.get(j).add(panesArray.get(j), BorderLayout.NORTH);
		}
		
        // defaults
		test.add(panesArrayWrap.get(j));
		
	    }
		//test.add(scrollingArea);
		
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
