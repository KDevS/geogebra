package geogebra.touch.gui.laf;

import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.elements.header.TabletHeaderPanel;
import geogebra.touch.model.TouchModel;

public class WindowsStoreLAF extends DefaultLAF
{	
	@Override
	public void setTitle(String title)
	{

	}

	@Override
	public int getPanelsHeight()
	{
		return 60;
	}

	@Override
	public int getAppBarHeight()
	{
		return 0;
	}

	@Override
	public DefaultResources getIcons()
	{
		// FIXME return windows specific icons
		return DefaultResources.INSTANCE;
	}

	@Override
	public TabletHeaderPanel getTabletHeaderPanel()
	{
		return null;
	}
	
	@Override
	public void buildHeader(TabletGUI gui, TouchApp app1, TouchModel touchModel,
			FileManagerM fm) {

	}
	
	@Override
	public boolean isMouseDownIgnored()
	{
	  return true;
	}

	@Override
	public native void stateChanged(boolean saved) /*-{
		if (!$wnd.appbar) {
			return;
		}		
		$wnd.appbar.saveChanged(saved);				
	}-*/;
	
	@Override
	public void updateUndoSaveButtons(){
		if(this.getApp() != null){
			updateUndoSaveButtons(this.getApp().getKernel().undoPossible(), this.getApp().getKernel().undoPossible());
		}
	}
	
	
	public native void updateUndoSaveButtons(boolean undo, boolean redo) /*-{
		if (!$wnd.appbar) {
			return;
		}		
		$wnd.appbar.updateUndoRedo(undo, redo);			
	}-*/;
	
}
