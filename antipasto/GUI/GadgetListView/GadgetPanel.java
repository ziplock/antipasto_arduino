package antipasto.GUI.GadgetListView;

import javax.imageio.ImageIO;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.*;

import antipasto.CoreFactory;
import antipasto.GadgetFactory;
import antipasto.ModuleFactory;
import antipasto.GUI.GadgetListView.AddGadgetMenu;
import antipasto.GUI.GadgetListView.GadgetPanelEvents.ActiveGadgetObject;
import antipasto.GUI.GadgetListView.GadgetPanelEvents.IActiveGadgetChangedEventListener;
import antipasto.GUI.GadgetListView.GadgetPanelEvents.IActiveSketchChangingListener;
import antipasto.GUI.GadgetListView.GadgetPanelEvents.SketchChangingObject;
import antipasto.Interfaces.IGadget;
import antipasto.Interfaces.IModule;
import antipasto.Interfaces.IPackedFile;
import antipasto.Interfaces.ITemporary;

import processing.app.Base;
import processing.app.Editor;
import processing.app.Serial;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class GadgetPanel extends JDialog implements ListSelectionListener, IActiveSketchChangingListener, ComponentListener,
													MouseListener, WindowListener
													{

 
    //JList list;
    private EventListenerList activeGadgetChangedEventList = new EventListenerList();
    private EventListenerList sketchBookChangedEventList = new EventListenerList();

    private GadgetFactory skbFact = new GadgetFactory();
    private CoreFactory coreFact= new CoreFactory();
    private ModuleFactory modFact = new ModuleFactory();
    private GadgetList list;
    private Serial serial;

    private File _sketchBookDirectory;

    private IModule activeModule;

    private GadgetListHorizontal libPanel;

    private String libraryDirectory;
    private IGadget _gadget;
    private JScrollPane scrollPanel;

    private JPanel messagePanel;
    private JLabel messageLabel;
    
    private JPanel gadgetDescPanel;
    private JLabel gadgetDescLabel;

    private Box box;
    
    private int cachedHeight = 425;
    private int cachedWidth = 300;

    public GadgetPanel(String sketchBookDirectory, JFrame frame, String libraryDirectory) {
    	super(frame, false);
        this.addMouseListener(new ModuleMouseAdapter());
        this.setUndecorated(true);
        frame.addWindowListener(this);
        this.libraryDirectory = libraryDirectory;
        this.init();
    }
    
    public void loadGadget(String gadget){
    	this.loadGadget(new File(gadget));
    }
    
    private void init(){
    	this.getContentPane().setLayout(new BorderLayout());
    	//JPanel top = new JPanel();
        //top.setBackground(new Color(0x04, 0x4F, 0x6F));
    	//this.getContentPane().add(top, BorderLayout.NORTH);
    	//top.setSize(new Dimension(this.getWidth(), 15));
        //top.setLayout(new FlowLayout());
    	//messagePanel.setLayout(new FlowLayout());
        
        /*
        try {
        	ImageIcon upperLeft = new ImageIcon("..\\lib\\upperleftgadget.png");
    		upperLeft.setImage(Base.getImage("..\\lib\\upperleftgadget.png", this));
    		JLabel upperImageLabel = new JLabel(upperLeft);
			top.add(upperImageLabel);
			upperImageLabel.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}*/

        /*
    	JPanel bottom = new JPanel();
    	bottom.setBackground(new Color(0x04, 0x4F, 0x6F));
    	this.getContentPane().add(bottom, BorderLayout.SOUTH);
    	bottom.setSize(new Dimension(this.getWidth(), 15));
    	bottom.setLayout(new FlowLayout());
        */ 

        /*
    	ImageIcon lowerRight = new ImageIcon();
    	try {
    		ImageIcon lowerLeftImage = new ImageIcon("\\lib\\lowerleftgadget.png");
			lowerLeftImage.setImage(Base.getImage("..\\lib\\lowerleftgadget.png", this));
    		JLabel lowerImageLabel = new JLabel(lowerLeftImage);
			top.add(lowerImageLabel);
			lowerImageLabel.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
        */
        
		this.setBackground(new Color(0x04, 0x4F, 0x6F));
		
    	/* The library description */
    	JPanel libDescPanel = new JPanel();
        JLabel libDescLabel = new JLabel(" Library: OpenHardware");
        libDescLabel.setForeground(Color.white);
        
        libDescPanel.setBackground(new Color(0x04, 0x4F, 0x6F));
        libDescPanel.setSize(new Dimension(300, 15));
    	libDescPanel.setLayout(new BorderLayout());
    	libDescPanel.add(libDescLabel, BorderLayout.WEST);  	

    	/* The gadget file description */
    	gadgetDescPanel = new JPanel();
        gadgetDescLabel = new JLabel(" Gadget: ");
        gadgetDescLabel.setForeground(Color.white);
        
        gadgetDescPanel.setBackground(new Color(0x04, 0x4F, 0x6F));
		gadgetDescPanel.setSize(new Dimension(300, 15));
        gadgetDescPanel.setLayout(new BorderLayout());
        gadgetDescPanel.add(gadgetDescLabel, BorderLayout.WEST);  
        
        /* The message panel */
    	messagePanel = new JPanel();
        messageLabel = new JLabel(" No messages.");
        messageLabel.setForeground(Color.white);
        
        messagePanel.setBackground(new Color(0x04, 0x4F, 0x6F));
        messagePanel.setSize(new Dimension(300, 15));
        messagePanel.setLayout(new BorderLayout());
        messagePanel.add(messageLabel, BorderLayout.WEST);          
        
        JButton button;
        
        //GadgetWindowTransferHandler transferHandler = new GadgetWindowTransferHandler();
       
    	box = Box.createVerticalBox();
        
        scrollPanel = new JScrollPane();
        scrollPanel.setPreferredSize(new Dimension(300, 300));
        scrollPanel.setSize(new Dimension(300, 300));
        
        libPanel = new GadgetListHorizontal(new File(this.libraryDirectory), list);
        libPanel.setSize(300, 70);
        libPanel.setPreferredSize(new Dimension(300, 70));
        
        box.setBackground(new Color(0x04, 0x4F, 0x6F));
        
        box.add(libDescPanel);
        box.add(libPanel);
        box.add(gadgetDescPanel);
        box.add(scrollPanel);
        box.add(messagePanel);

        this.getContentPane().add(box, BorderLayout.NORTH);
        
        //JPanel panel = new JPanel();
        //panel.setLayout(new FlowLayout());
        //panel.setSize(200, 100);

        /*button = new JButton("Add Gadget");
        button.addMouseListener(new AddGadgetMenu(menu, button));

        panel.add(button);
        button = new JButton("Remove Gadget");
        panel.add(button);
        */
        //this.getContentPane().add(panel, BorderLayout.PAGE_START);
        //panel.setVisible(true);

        //this.setSize(this.cachedWidth, this.cachedHeight);
        
        //libPanel.setVisible(true);
        //scrollPanel.setVisible(true);
        //libDescPanel.setVisible(true);
        //scrollDescPanel.setVisible(true);
        //top.setVisible(true);
        //bottom.setVisible(true);
        
        //this.setVisible(true);
    }
    
    public void loadGadget(File gadget){
    	if(gadget  != null){	
    		GadgetFactory fact = skbFact;
    		
    		/* Remove the current panel GUI elements, because 
    		 * we're going to update them....
    		 */
    		scrollPanel.setVisible(false);
	    	this.remove(scrollPanel);
    		this.remove(messagePanel);
    		this.remove(gadgetDescPanel);
    		
    		/* Setup the new panels */
	    	String dir = System.getProperty("java.io.tmpdir") + gadget.getName();
    		IGadget book = fact.loadGadget(gadget, dir);

    		this._gadget = book;
    		 
	        list = new GadgetList(book, gadget.getParent());
	        list.loadGadget(_gadget);
	        
	        scrollPanel = new JScrollPane((JList) list);
	        scrollPanel.setPreferredSize(new Dimension(300, 300));
            scrollPanel.setSize(new Dimension(300, 300));
            
            /* Describe what's going on here to the user and 
             * display the description in the panel labels.
             */
            gadgetDescLabel.setText(" Gadget: " + gadget.getName());
            messageLabel.setText(" Gadget Loaded.");
            
            /* Add all the updated GUI elements back to the panel */
            box.add(gadgetDescPanel);
            box.add(scrollPanel);
            box.add(messagePanel);
            scrollPanel.setVisible(true);
            
    	    list.addSketchChangingeListener(this);
    	    list.addListSelectionListener(this);

    		 this.activeModule = this.getActiveModule();
	    	//Save this for later!		
    	}else{
    		this.setVisible(false);
    		this.hide();				//gotta upgrade the java version....
    	}
    }

    public void setVisible(boolean b){
    	if(this._gadget != null){
    		super.setVisible(b);
    	}else{
    		super.setVisible(b);
    	}
		this.setSize(this.cachedWidth, this.cachedHeight);
    }
    
    /*
    * Called when the active listview is changed
    * */
    public void valueChanged(ListSelectionEvent e) {
        GadgetList list = (GadgetList) e.getSource();
        if(list.getSelectedValue() instanceof IModule){
            if(this.activeModule != list.getSelectedValue()){
                this.activeModule = (IModule) list.getSelectedValue();
                this.onActiveGadgetChange(new ActiveGadgetObject(this,
                        this.activeModule.getSketchFile(),
                        this.activeModule.getBoardsFile()));
            }
        }
    }

    public void addActiveGadgetChangedEventListener(IActiveGadgetChangedEventListener listener){
        this.activeGadgetChangedEventList.add(IActiveGadgetChangedEventListener.class, listener);
    }

    public void removeActiveGadgetChangedEventListener(IActiveGadgetChangedEventListener listener){
        this.activeGadgetChangedEventList.remove(IActiveGadgetChangedEventListener.class, listener);
    }

    private void onActiveGadgetChange(ActiveGadgetObject evObj){
         Object[] listeners = activeGadgetChangedEventList.getListenerList();
                    // Each listener occupies two elements - the first is the listener class
                    // and the second is the listener instance
                    for (int i=0; i<listeners.length; i+=2) {
                        if (listeners[i]== IActiveGadgetChangedEventListener.class) {
                            ((IActiveGadgetChangedEventListener)listeners[i+1]).onActiveGadgetChanged(evObj);
                        }
                    }
    }

    public void addSketchBookChangedEventListener(IActiveSketchChangingListener listener){
        this.sketchBookChangedEventList.add(IActiveSketchChangingListener.class, listener);
    }

     public void removeSketchBookChangedEventListener(IActiveSketchChangingListener listener){
        this.sketchBookChangedEventList.remove(IActiveSketchChangingListener.class, listener);
     }

    public void saveCurrentGadget(){
    	try{
    		if(list.gadget != null){
    			list.saveCurrentGadget();
    		}
    	}catch(Exception ex){
    	}
    }

    public void deleteCurrentSketchOutputDirectory() throws Exception {
        if(this._gadget instanceof ITemporary){
            File dir = new File(((ITemporary)_gadget).getTempDirectory());
            if(!dir.delete()){
                throw new Exception("New sketch output");
            }
        }
    }

    public Serial getSerial(){
    	return this.serial;
    }
    
    public void setSerial(Serial serial){
    	this.serial = serial;
    }
    
    public void loadSketchFile(File f){
        this.skbFact.loadGadget(f, System.getProperty("java.io.tmpdir") + File.separator + dropExtension(f.getName()));
    }

    public void onActiveSketchChanged(SketchChangingObject obj) {
        this.saveCurrentGadget();
        this.loadSketchFile(obj.newFile);
    }

    public String dropExtension(String fileName){
        return fileName.split(".")[0];
    }

    public IGadget getActiveGadget(){
    	if(this._gadget == null){
    		return null;
    	}
    	
        IModule[] gadgets = _gadget.getModules();
        for(int i=0; i < gadgets.length; i++){
            IModule gadget = gadgets[i];
            File boardsFile = gadget.getBoardsFile();
            File sketchFile = gadget.getSketchFile();
        }
        return this._gadget;
    }
    
    public IModule getActiveModule(){
    	if(this.activeModule == null){
    		if(this._gadget != null){
    			this.activeModule = this._gadget.getModules()[0];
    		}else{
    			return null;
    		}
    	}
    	return this.activeModule;
    }

	public void componentHidden(ComponentEvent arg0) {
		this.hide();
	}

	public void componentMoved(ComponentEvent arg0) {
		Editor editor = (Editor) arg0.getComponent();
		//int width = 300;
        //this.setSize(width, editor.textarea.getHeight());
		//this.setSize(this.cachedWidth, this.cachedHeight);
        this.setLocation(editor.getX() - this.getWidth(), editor.textarea.getLocationOnScreen().y);	
	}

	public void componentResized(ComponentEvent arg0) {
		//Editor editor = (Editor) arg0.getComponent();
        //this.setSize(this.cachedWidth, this.cachedHeight);
        //this.setLocation(editor.getX() - this.getWidth(), editor.textarea.getLocationOnScreen().y);	
		
	}

	public void componentShown(ComponentEvent arg0) {
		this.show();
	}
	
	public void closeActiveGadget(){
		this._gadget = null;
	}

	public void mouseClicked(MouseEvent e) {	
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}

	public void windowActivated(WindowEvent arg0) {
		//boolean isVis = this.isVisible();
		/*if(this._gadget != null){
			if(this.isVisible()){
				this.setAlwaysOnTop(true);
				this.setAlwaysOnTop(false);
			}
		}*/
	}

	public void windowClosed(WindowEvent arg0) {
		
	}

	public void windowClosing(WindowEvent arg0) {
		this.saveCurrentGadget();
	}

	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}


     


