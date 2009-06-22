package headless;

import java.io.*;

import artofillusion.*;
import artofillusion.image.*;


public class HeadlessListener implements RenderListener
{
	private File target;

	public HeadlessListener(File out)
	{
		target = out;
	}

	public synchronized void imageComplete(ComplexImage image)
	{
		System.out.println("Finished.");

		try
		{
			ImageSaver.saveImage(image, target, 2, 95);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

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
