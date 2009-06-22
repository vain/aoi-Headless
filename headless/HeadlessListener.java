package headless;

import artofillusion.*;
import artofillusion.image.*;

public class HeadlessListener implements RenderListener
{
	public synchronized void imageComplete(ComplexImage image)
	{
		System.out.println("Finished.");
		notifyAll();
	}

	public void imageUpdated(java.awt.Image image)
	{
		System.out.println("-- MARK --");
	}

	public void renderingCanceled()
	{
	}

	public void statusChanged(java.lang.String status)
	{
		System.out.println("HL: " + status);
	}
}
