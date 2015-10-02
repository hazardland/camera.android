package hazardland.camera;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;

public class Main extends Activity 
{
	private Camera camera;
	private Preview preview;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);	
		setContentView(R.layout.main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		camera = camera();
		Camera.Parameters config = camera.getParameters();

		debug ("Focus modes" ,true);
		List<String> focus = config.getSupportedFocusModes();
		for (String item: focus) 
		{
			debug (item);
		}		

		debug ("Scene modes", true);
		List<String> scene = config.getSupportedSceneModes();
		for (String item: scene) 
		{
			debug (item);
		}		

		debug ("Image modes", true);
		List<Camera.Size> size = config.getSupportedPictureSizes();

		for (Camera.Size item: size) 
		{
			debug (""+item.width+"x"+item.height);
		}		
		
		
		config.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		//config.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		// set Camera parameters
		
		
		if (config.getMaxNumMeteringAreas() > 0)
		{ // check that metering areas are supported
		    List<Camera.Area> areas = new ArrayList<Camera.Area>();
		    Rect area1 = new Rect(-200, -200, 200, 200);    // specify an area in center of image
		    areas.add(new Camera.Area(area1, 100)); // set weight to 60%
/*		    Rect area2 = new Rect (800, -1000, 1000, -800);  // specify an area in upper right of image
		    areas.add(new Camera.Area(area2, 400)); // set weight to 40%
*/		    config.setMeteringAreas (areas);
		}
		
		
		camera.setParameters (config);		
		preview = new Preview (this, camera);
		((FrameLayout) findViewById(R.id.preview)).addView(preview);
		
		alert (size.get(0).width+"x"+size.get(0).height);		
	}
	/** A safe way to get an instance of the Camera object. */
	public static Camera camera()
	{
	    Camera camera = null;
	    try 
	    {
	        camera = Camera.open();
	        debug ("Camera opened");
	    }
	    catch (Exception e)
	    {
	    	debug ("Camera not available");
	    }
	    return camera;
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        release();              // release the camera immediately on pause event
    }


    private void release()
    {
        if (camera!= null)
        {
            camera.release();
            camera = null;
        }
    }	

    public static void debug (String message)
	{
		System.out.println (message);
	}

    public static void debug (String message, boolean title)
	{
    	System.out.println (" ");
		System.out.println (message);
		System.out.println ("-------------------");
	}
    
    public void alert (String message)
    {
    	Toast.makeText (Main.this, message, Toast.LENGTH_LONG).show();
    }
    
}
