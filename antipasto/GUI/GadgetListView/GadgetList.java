package antipasto.GUI.GadgetListView;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;

import antipasto.GadgetFactory;
import antipasto.ModuleFactory;
import antipasto.GUI.GadgetListView.GadgetPanelEvents.IActiveGadgetChangedEventListener;
import antipasto.GUI.GadgetListView.GadgetPanelEvents.IActiveSketchChangingListener;
import antipasto.GUI.GadgetListView.GadgetPanelEvents.SketchChangingObject;
import antipasto.Interfaces.IGadget;
import antipasto.Interfaces.IModule;
import antipasto.Interfaces.IPackedFile;
import antipasto.Interfaces.ITemporary;

import java.io.*;
import java.util.Arrays;

public class GadgetList extends JList implements IGadgetWindowTransferFileEventListener, MouseListener{

    IGadget gadget;
    JList jList;
    GadgetCollection _collection;
    private String moduleDirectory;
    private boolean mouseIsOver = false;

    private EventListenerList activeGadgetChangedEventList = new EventListenerList();
    private EventListenerList activeSketchChangedEventList = new EventListenerList();

    public GadgetList(IGadget gadget, String gadgetDirectory){
        super();
        this.loadGadget(gadget);
        this.moduleDirectory = gadgetDirectory;
        this.addMouseListener(this);
    }

    public GadgetList(String gajDirectory){
        super();
        this.moduleDirectory = gajDirectory;
        this.addMouseListener(this);
    }

    public void loadGadget(IGadget gaj){
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gadget = gaj;
        _collection = new GadgetCollection(gaj);
        
        GadgetWindowTransferHandler transferHandler = new GadgetWindowTransferHandler();
        transferHandler.addTransferFileListener(this);
        this.setModel(new GadgetListModel(_collection));
        this.setCellRenderer(new GadgetCellRenderer());
        this.setTransferHandler(transferHandler);
    }

    public void addGadgetToCurrentSketchBook(File f) {
        if(f.getName().endsWith(".module")){
            ModuleFactory fact = new ModuleFactory();
            try {
                this.loadGadget(new GadgetFactory().AddModuleToGadget(gadget, fact.loadModule(f, System.getProperty("java.io.tmpdir"), true)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadGadget(File f) {
        if(f.getName().endsWith(".pde")){
            this.fireSketchChangingEvent(this.gadget, f);
        }
    }

    public void addActiveGadgetChangeEventListener(IActiveGadgetChangedEventListener listener) {
        this.activeGadgetChangedEventList.add(IActiveGadgetChangedEventListener.class, listener);
    }

    public void removeActiveGadgetChangeEventListener(IActiveGadgetChangedEventListener listener) {
        this.activeGadgetChangedEventList.remove(IActiveGadgetChangedEventListener.class, listener);
    }

    public void onFileTransfer(FileTransferObject ftObj) {
        File f = ftObj.getFile();

        if (f.getName().endsWith(IModule.moduleExtension)) {
            this.addGadgetToCurrentSketchBook(f);
        } else if (f.getName().endsWith(".pde") || f.getName().endsWith(".gadget")){
            this.loadGadget(f);
        }
    }

    public void closeCurrentSketch(){
        this.saveCurrentGadget();
    }

    public void saveCurrentGadget(){
        GadgetFactory factory = new GadgetFactory();
        try{
            //factory.writeSketchBookToFile(_book, sketchBookDirectory);
            String originalDirectory = ((IPackedFile)gadget).getPackedFile().getPath();
            String bookFileName = ((IPackedFile)gadget).getPackedFile().getName();
            antipasto.Interfaces.ITemporary temp = (antipasto.Interfaces.ITemporary)gadget;
            IModule[] newGadgets = new IModule[gadget.getModules().length];
            for(int i = 0; i < gadget.getModules().length; i++){
                IModule curGadget = gadget.getModules()[i];
                if(curGadget instanceof IPackedFile && curGadget instanceof ITemporary){
                    File gadgetDirectory = new File(((ITemporary)curGadget).getTempDirectory());
                    File packedFile = ((IPackedFile)curGadget).getPackedFile();

                    ModuleFactory fact = new ModuleFactory();
                    try{
                    	fact.WriteModuleToFile(curGadget, packedFile.getPath().substring(0,
                    			packedFile.getPath().length() - packedFile.getName().length()));
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    newGadgets[i] = curGadget;
                }
            }
            gadget.setModule(newGadgets);
            File tempDirectory = new File(temp.getTempDirectory());
            int endIndex = originalDirectory.length() - bookFileName.length();
            originalDirectory = originalDirectory.substring(0, endIndex);
            factory.writeGadgetToFile(gadget, originalDirectory);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public String getSketchBookDirectory() {
        return moduleDirectory;
    }

    public void setSketchBookDirectory(String sketchBookDirectory) {
        this.moduleDirectory = sketchBookDirectory;
    }

    public void addSketchChangingeListener(IActiveSketchChangingListener listener){
        this.activeSketchChangedEventList.add(IActiveSketchChangingListener.class, listener);
    }

    public void removeSketchChangingListener(IActiveSketchChangingListener listener){
        this.activeSketchChangedEventList.remove(IActiveSketchChangingListener.class, listener);
    }

    private void fireSketchChangingEvent(IGadget oldSketch, File newFile){
        Object[] listeners = activeSketchChangedEventList.getListenerList();
                    // Each listener occupies two elements - the first is the listener class
                    // and the second is the listener instance
                    for (int i=0; i<listeners.length; i+=2) {
                        if (listeners[i]== IActiveSketchChangingListener.class) {
                            ((IActiveSketchChangingListener)listeners[i+1]).onActiveSketchChanged(new SketchChangingObject(oldSketch, newFile));
                        }
                    }
    }

	public void doImportDragDrop(IModule module){
		//TODO: Do this later!
		System.out.println("Drag drop not implemented just yet.....I'm only on programmer!");
		/*System.out.println("Attempting import");
		if(module instanceof IPackedFile){
			IPackedFile file = (IPackedFile)module;
			String name = module.getName();
			int x = 0;
			while(this.checkName(this.gadget, name)){
				x++;
				name = name + "(" + x +")";
			}
			File dest = new File(this.getSketchBookDirectory() + File.separator + name + IModule.moduleExtension);
			try {
				GadgetList.copyFile(file.getPackedFile(), dest);
			} catch (IOException e) {
				e.printStackTrace();
			}
			IModule[] modules = this.gadget.getModules();
			IModule[] modules2 = new IModule[modules.length + 1];
			int i = 0;
			for(; i < modules.length; i++){
				modules2[i] = modules[i];
			}
			ITemporary tempFile = (ITemporary)(module);
			ModuleFactory fact = new ModuleFactory();
			String directory = tempFile.getTempDirectory();
			try {
				modules2[i] = fact.loadModule(dest, directory, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			GadgetFactory gadgetFactory = new GadgetFactory();
			try {
				gadgetFactory.AddModuleToGadget(gadget, modules2[i]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.saveCurrentGadget();
			this.loadGadget(gadget);
		}else{
			System.out.println("Error trying to import the module...");
		}*/
	}
	
	private boolean checkName(IGadget gadget, String name){
		IModule[] modules = gadget.getModules();
		int ret = 0;
		for(int i = 0; i < modules.length; i++){
			if(name.equalsIgnoreCase(modules[i].getName()))
				return true;
		}
		return false;
	}

	//Todo: clean up all of this mouse listener crap....
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent arg0) {
		//System.out.println("Mouse enetered");
		this.mouseIsOver = true;
		//int x = arg0.getLocationOnScreen().x;
		//int y = arg0.getLocationOnScreen().y;
		//if(this.bounds().inside(x, y)){
			
		//}
	}

	public void mouseExited(MouseEvent arg0) {
		//System.out.println("Mouse exited");
		this.mouseIsOver = false;
	}

	public void mousePressed(MouseEvent arg0) {
		
	}

	public void mouseReleased(MouseEvent arg0) {
	}
	//This code was copied and pasted directly from
	//http://www.codeandcoffee.com/2006/08/22/quick-snippets-copy-a-file-with-java/
	public static void copyFile(File fSource, File fDest) throws IOException
	{
		// Declare variables
		InputStream sIn = null;
		OutputStream sOut = null;
	
		try
		{
		// Declare variables
		int nLen = 0;
		sIn = new FileInputStream(fSource);
		sOut = new FileOutputStream(fDest);
	
		// Transfer bytes from in to out
		byte[] bBuffer = new byte[1024];
		while ((nLen = sIn.read(bBuffer)) > 0)
		{
		sOut.write(bBuffer, 0, nLen);
		}
	
		// Flush
		sOut.flush();
		}
		finally
		{
		// Close streams
		try
		{
		if (sIn != null)
		sIn.close();
		if (sOut != null)
		sOut.close();
		}
		catch (IOException eError)
		{
		}
		}
	}
}
