package hazardland.camera;

import java.io.IOException;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;
import android.hardware.Camera;

public class Preview  extends SurfaceView implements SurfaceHolder.Callback 
{
    private SurfaceHolder view;
    private Camera camera;
    
	@SuppressWarnings("deprecation")
	public Preview (Context context, Camera camera) 
	{
		super(context);
		this.camera = camera;
		view = getHolder();
		view.addCallback (this);
		view.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
	{
		// TODO Auto-generated method stub
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (view.getSurface()==null)
        {
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try 
        {
            camera.stopPreview();
           
        } 
        catch (Exception e)
        {
          // ignore: tried to stop a non-existent preview
        }
		
       
        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try 
        {
            camera.setPreviewDisplay(holder);
            camera.startPreview();

        } 
        catch (Exception e)
        {
            debug ("Error starting camera preview: " + e.getMessage());
        }
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
        // The Surface has been created, now tell the camera where to draw the preview.
        try 
        {
            camera.setPreviewDisplay (holder);
            camera.startPreview();
        } 
        catch (IOException e) 
        {
            debug ("Error setting camera preview: " + e.getMessage());
        }
        catch (Exception e) 
        {
			debug ("Unknown error " + e.getMessage());
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		// TODO Auto-generated method stub
		
	}
	
	public static void debug (String message)
	{
		System.out.println ("Preview: " + message);
	}	

}
