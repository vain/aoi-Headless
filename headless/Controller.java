package headless;

import java.io.*;
import java.util.*;

import artofillusion.*;
import artofillusion.animation.*;
import artofillusion.image.filter.*;
import artofillusion.procedural.*;
import artofillusion.material.*;
import artofillusion.texture.*;
import artofillusion.object.*;
import artofillusion.ui.*;
import artofillusion.keystroke.*;

import java.awt.GraphicsEnvironment;


public class Controller
{
	public static void main(String[] args)
	{
		Map<String, String> env = System.getenv();
		System.out.println("DISPLAY = " + env.get("DISPLAY"));
		System.out.println("Am I headless? " + GraphicsEnvironment.isHeadless());

		// -- Do the whole initialization stuff
		Translate.setLocale(Locale.getDefault());
		PluginRegistry.addCategory(Plugin.class);
		PluginRegistry.addCategory(Renderer.class);
		PluginRegistry.addCategory(Translator.class);
		PluginRegistry.addCategory(ModellingTool.class);
		PluginRegistry.addCategory(Texture.class);
		PluginRegistry.addCategory(Material.class);
		PluginRegistry.addCategory(TextureMapping.class);
		PluginRegistry.addCategory(MaterialMapping.class);
		PluginRegistry.addCategory(ImageFilter.class);
		PluginRegistry.addCategory(Module.class);
		PluginRegistry.registerPlugin(new UniformTexture());
		PluginRegistry.registerPlugin(new ImageMapTexture());
		PluginRegistry.registerPlugin(new ProceduralTexture2D());
		PluginRegistry.registerPlugin(new ProceduralTexture3D());
		PluginRegistry.registerPlugin(new UniformMaterial());
		PluginRegistry.registerPlugin(new ProceduralMaterial3D());
		PluginRegistry.registerPlugin(new UniformMapping(null, null));
		PluginRegistry.registerPlugin(new ProjectionMapping(null, null));
		PluginRegistry.registerPlugin(new CylindricalMapping(null, null));
		PluginRegistry.registerPlugin(new SphericalMapping(null, null));
		PluginRegistry.registerPlugin(new UVMapping(null, null));
		PluginRegistry.registerPlugin(new LinearMapping3D(null, null));
		PluginRegistry.registerPlugin(new LinearMaterialMapping(null, null));
		PluginRegistry.registerPlugin(new BrightnessFilter());
		PluginRegistry.registerPlugin(new SaturationFilter());
		PluginRegistry.registerPlugin(new ExposureFilter());
		PluginRegistry.registerPlugin(new TintFilter());
		PluginRegistry.registerPlugin(new BlurFilter());
		PluginRegistry.registerPlugin(new GlowFilter());
		PluginRegistry.registerPlugin(new OutlineFilter());
		//PluginRegistry.registerPlugin(new NoiseReductionFilter());
		//PluginRegistry.registerPlugin(new DepthOfFieldFilter());
		PluginRegistry.registerResource("TranslateBundle", "artofillusion", ArtOfIllusion.class.getClassLoader(), "artofillusion", null);
		PluginRegistry.registerResource("UITheme", "default", ArtOfIllusion.class.getClassLoader(), "artofillusion/Icons/defaultTheme.xml", null);
		PluginRegistry.scanPlugins();
		ThemeManager.initThemes();

		// ******************************************************************
		// This is the point where I got stuck. The ObjectInfo.getBounds()
		// requires this variable to be != null, but ArtOfIllusion.preferences
		// has private access. Hence, it can't be set from the outside unless
		// ArtOfIllusion.main() is called -- which, in turn, would fire up a
		// GUI.

		// Let's assume this property has public access:
		ArtOfIllusion.preferences = new ApplicationPreferences();
		// ******************************************************************

		KeystrokeManager.loadRecords();

		List plugins = PluginRegistry.getPlugins(Plugin.class);
		for (int i = 0; i < plugins.size(); i++)
		{
			try
			{
				((Plugin) plugins.get(i)).processMessage(Plugin.APPLICATION_STARTING, new Object [0]);
			}
			catch (Throwable tx)
			{
				tx.printStackTrace();
				String name = plugins.get(i).getClass().getName();
				name = name.substring(name.lastIndexOf('.')+1);
				System.err.println(Translate.text("pluginInitError", name));

				if (tx instanceof java.awt.HeadlessException)
				{
					System.err.println("--- HeadlessException, ignoring.");
				}
				else
				{
					System.exit(1);
				}
			}
		}


		// -- Load the scene
		Scene sc = null;
		File f = new File(args[0]);
		System.out.println("Trying to load \"" + f + "\" ...");
		try
		{
			sc = new Scene(f, true);
			System.out.println("Scene loaded.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}


		// -- Find raytracer
		Renderer ray = null;
		List<Renderer> list = PluginRegistry.getPlugins(Renderer.class);
		for (Renderer i : list)
			if (i.getName().equals("Raytracer"))
				ray = i;

		if (ray == null)
		{
			System.err.println("Could not find raytracer.");
			System.exit(1);
		}

		// -- Configure
		ray.configurePreview();

		ObjectInfo mySceneCameraOI = sc.getObject("Camera 1");
		SceneCamera mySceneCamera  = (SceneCamera)(mySceneCameraOI.getObject());
		Camera myCamera = mySceneCamera.createCamera(640, 480, mySceneCameraOI.getCoords());

		HeadlessListener myListener = new HeadlessListener(new File(args[1]));

		// -- Render
		ray.renderScene(sc, myCamera, myListener, mySceneCamera);
		synchronized (myListener)
		{
			try
			{
				myListener.wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		System.out.println("Listener says: Done.");
	}
}
