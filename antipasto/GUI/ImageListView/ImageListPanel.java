package antipasto.GUI.ImageListView;

import javax.swing.BoxLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.*;

import antipasto.GUI.GadgetListView.GadgetPanel;
import antipasto.GUI.GadgetListView.GadgetPanelEvents.ActiveGadgetObject;
import antipasto.GUI.GadgetListView.GadgetPanelEvents.IActiveGadgetChangedEventListener;
import antipasto.Interfaces.IModule;

import processing.app.Serial;
import processing.app.SerialException;
import processing.app.FlashTransfer;
import javax.swing.TransferHandler;

import javax.swing.*;
import java.awt.datatransfer.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.Iterator;
import java.net.*;
import java.awt.Component;
import java.awt.Dimension;
import java.math.*;

import java.awt.BorderLayout;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ImageListPanel extends JPanel implements IActiveGadgetChangedEventListener {
	private ImageListView list;
	private FlashTransfer imageTransfer;
	private JButton removeButton;
	private JButton transferButton;
	private GadgetPanel panel;
	private IModule _module;
	private JProgressBar progressBar;
	
	public ImageListPanel(GadgetPanel panel, FlashTransfer imageTransfer){
		this.imageTransfer = imageTransfer;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.createTransferButton();
		this.transferButton.setVisible(true);
	}
	
	
	public void setModule(IModule module){
		if(module != null){
			if(list != null){
				this.remove(list);
				list.setVisible(false);
			}
			if(transferButton != null){
				this.remove(transferButton);
				this.transferButton.setVisible(false);
			   }
			if(removeButton != null){
				this.remove(removeButton);
				this.removeButton.setVisible(false);
			}
			if(progressBar != null){
				this.remove(progressBar);
				this.progressBar.setVisible(false);
			}
			this.setLayout(new BorderLayout());
			this.createTransferButton();
			this.createRemoveButton();
			JPanel northPanel = new JPanel();
			northPanel.add(transferButton);
			northPanel.add(removeButton);
			this.add(northPanel, BorderLayout.NORTH);
			File[] files = module.getData();
			list = new ImageListView(module);
			JScrollPane scrollPane = new JScrollPane(list);
			this.add(scrollPane, BorderLayout.CENTER);
			scrollPane.setSize(this.getWidth(), this.getHeight() - transferButton.getHeight());
			scrollPane.setVisible(true);
			list.setVisible(true);
			
			progressBar = new JProgressBar();
			this.add(progressBar, BorderLayout.SOUTH);
			progressBar.setVisible(true);
		
		}
		this.transferButton.setVisible(true);
		this.setSizesOfComponents();
		
		_module = module;
	}
	
	public IModule getModule(){
		return this._module;
	}
	
	public void paint(java.awt.Graphics g){
		this.setSizesOfComponents();
		super.paint(g);
	}
	
	private void setSizesOfComponents(){
		Dimension parentSize = this.getParent().getSize();
		Dimension btnSize = this.transferButton.getSize();
		double height = this.getParent().getSize().getHeight() - this.transferButton.getSize().getHeight();
		int heightI = (int)height;
		Dimension listViewSize = new Dimension(this.getParent().getWidth(), heightI);
		list.setSize(listViewSize);
		this.repaint();
	}
	
	private JButton createRemoveButton(){
		this.removeButton = new JButton("Remove File");
		this.removeButton.addMouseListener(
			new MouseListener(){
										   
			public void mouseClicked(MouseEvent arg0) {
				 list.removeSelected();
			}

			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
										   
			});
		return this.removeButton;
	}
	
	private void createTransferButton(){
		this.transferButton = new JButton("Transfer");
		this.transferButton.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent arg0) {
				transfer();
			}

			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	/*
	 *Transfers the files using the flash transferer
	 */
	private void transfer(){
		try {
			try{
				this.imageTransfer.setSerial(new Serial(true));
			}catch(SerialException ex){
				ex.printStackTrace();
			}
			final File file[] = this._module.getData();
			progressBar.setMaximum(file.length - 1);
			final FlashTransfer transfer = imageTransfer;
			new Thread(
					   new Runnable() {
					   public void run() {
					   for(int i = 0; i < file.length; i++){
					   System.out.println("sending: " + file[i].getName());
					   transfer.send(file[i]);
					   progressBar.setValue(i);
					   try{
					   Thread.sleep(1000);
					   }catch(Exception ex){
					   ex.printStackTrace();
					   }
					   }
					   }}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onActiveGadgetChanged(ActiveGadgetObject obj) {
	}
	
}
