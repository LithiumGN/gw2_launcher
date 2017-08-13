package Frame;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class FastFrame extends JFrame{


	private JButton startwith= new JButton("Run with ArcDPS");
	private JButton startwithout= new JButton("Run only GW2 ");
    public JLabel status = new JLabel ("    Updater is starting...");
    private JLabel path= new JLabel("   Guild Wars 2 executable found");
	private JLabel arg=new JLabel("Arguments: ");
	private JCheckBox autostart= new JCheckBox("Start with these settings each time");
    public JTextField arg_string=new JTextField(20);
	private String path_string;





    public FastFrame(String dir, boolean hide){
        super("Guild Wars 2 Launcher");
        //Settings of the FastFrame object 
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(310,160);
        if (!hide) this.setVisible(true); //if background option is selected the frame will not show up
        
        //JPanel setup 
        JPanel grid=new JPanel(new GridLayout(2,1));
        grid.setBorder(BorderFactory.createTitledBorder("Status"));
        
        //Color setup
        status.setForeground(Color.orange);
        path.setForeground(new Color(0, 102, 51));
        
        //Add elements to the JPanel and FastFrame
        this.add(grid);
        grid.add(path);
        grid.add(status);




    }
}
