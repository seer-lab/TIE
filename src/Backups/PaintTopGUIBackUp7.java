package Backups;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class PaintTopGUIBackUp7 extends JPanel{
	
	// to generate random numbers
	private final int m = 256;
	private final int a = 67;
	private final int c = 27;
	private int x_new = 33;
	private int x_prev = 33;
	private Dimension scrnsize;
	private int sq_height;
	private int sq_width;
    private int max_Transitions;
	private final int y_offset = 0;
	private Color[] threadColor ;
	private List<Integer> transition_Num;
	private List<List<Integer>> transition_states;
	private List<List<String>> transition_states_info;
	private int trace_Num;
	private Dimension topDim;
	private Graphics gCopy;
	
    
	public PaintTopGUIBackUp7(int trace_Num, int thread_Num, List<Integer> transition_Num, List<List<Integer>> transition_states, List<List<String>> transition_states_info){
		int randomR = 0;
		int randomG = 0;
		int randomB = 0;
		
		this.transition_Num = transition_Num;
		this.transition_states = transition_states;
		this.transition_states_info = transition_states_info;
		this.trace_Num = trace_Num;
		
		// Defining a Color to each thread
		   threadColor = new Color[thread_Num];
		   for (int i=0; i < thread_Num; i++){						
			
			 randomR = this.genRandomNumbers();
			 randomG = this.genRandomNumbers();	 
		     randomB = this.genRandomNumbers();
					   
			 // fill color
			 threadColor[i] = new Color(randomR, randomG, randomB);
		  }	
		
		// get max transitions
		max_Transitions = 0;
		for(int i=0; i < transition_Num.size(); i++){
            if(transition_Num.get(i) > max_Transitions){
            	max_Transitions = transition_Num.get(i);
            }
	    }
		
		// Get the current screen size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		scrnsize = toolkit.getScreenSize();
		scrnsize.width = scrnsize.width;
		scrnsize.height = scrnsize.height - 20;
		
        JPanel topPanel = new JPanel();
        topPanel.setLayout(null);
        
        topPanel.setBackground(Color.WHITE);
        topPanel.setVisible(true);
      
        topDim = new Dimension();
        topDim.height = scrnsize.height / 3;
        topDim.width = scrnsize.width;
        topPanel.setBackground(Color.white);
        
		// initializing the square height
		sq_width = (int)(topDim.width / trace_Num);
		sq_height = (int)(topDim.height / max_Transitions);
		
        this.add(topPanel);
        this.setSize(topDim);
        this.setVisible(true);

	}
	
	
    public void paint(Graphics g) {		  
    	  gCopy = g;
		  g.setColor(Color.WHITE);
		  g.fillRect(0, 0, scrnsize.width, scrnsize.height);
		  int y = 0;
		  int x = 0;
		  int threadNum = 0;
		  
		  for(int j = 0; j < trace_Num; j++){
			x = j * sq_width;
			y = y_offset;
			
			// coloring the panel
			for(int i=0; i < transition_states.get(j).size(); i++){
				 y = y_offset + i*sq_height;
				
				 threadNum = transition_states.get(j).get(i);
			     g.setColor(threadColor[threadNum]);
			    
			     g.fillRect(x, y, sq_width, sq_height); 
			     
			     
			     //g.setColor(Color.black);
			     //g.drawRect(x, y, sq_width, sq_height);
			    
			}
			g.setColor(Color.WHITE);
			g.drawLine(x, 0, x, topDim.height);
			 
		  }
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

	
	/*
	 * used with mouse listener
	 */
	public int getTraceNumber(int x, int y){
		
		for (int i = 1; i < trace_Num ; i++){
			
			if((x > (i-1)*sq_width) && (x < i*sq_width)){
				
				this.getGraphics().setColor(Color.RED);
				this.getGraphics().drawLine((i-1) * sq_width, 0, i * sq_width, topDim.height);
				return (i-1);
			}
		}
		
		return trace_Num - 1;
	}

}
