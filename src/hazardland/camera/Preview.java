package hazardland.camera;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

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
	    Main.lock = true;
		// TODO Auto-generated method stub
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (view.getSurface()==null)
        {
            alert ("no preview surface");
            return;
        }

        // stop preview before making changes
        try 
        {
            camera.stopPreview();
           
        } 
        catch (Exception e)
        {
            alert ("no preview to stop");
        }
		
/*        Camera.Parameters config = camera.getParameters();
        config.setPreviewSize(1600, 1200);
        camera.setParameters(config);*/
       
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
            alert ("Error starting camera preview: " + e.getMessage());
        }
        Main.lock = false;
        info();        
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
        // The Surface has been created, now tell the camera where to draw the preview.
//        try 
//        {
//            camera.setPreviewDisplay (holder);
//            camera.startPreview();
//        } 
//        catch (IOException e) 
//        {
//            debug ("Error setting camera preview: " + e.getMessage());
//        }
//        catch (Exception e) 
//        {
//			debug ("Unknown error " + e.getMessage());
//		}
		
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

    public void alert (String message)
    {
        Toast.makeText (getContext(), message, Toast.LENGTH_LONG).show();
    }
    
    public void info ()
    {
        Camera.Parameters config = camera.getParameters();        
        alert (config.getPictureSize().width+"x"+config.getPictureSize().height
                +" -> "+
                config.getPreviewSize().width+"x"+config.getPreviewSize().height
                +" "
                +"JPEG "+config.getJpegQuality()+"%"
                +" "
                +"FLASH "+config.getFlashMode());        
    }
	
}
