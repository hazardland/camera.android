package hazardland.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Main extends Activity implements Camera.AutoFocusCallback
{
	private Camera camera;
	private Preview preview;
	public static boolean lock = true;
	public Config config;
    private LinearLayout popup;
    private float brightness;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);	
		setContentView(R.layout.main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);		
		start ();
		((FrameLayout) findViewById(R.id.preview)).setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                if (camera==null)
                {
                    return;
                }
                if (!lock)
                {
                    lock = true;                    
                    //config.focus.lock (camera);
                    camera.autoFocus (Main.this);
                    //camera.takePicture(null, null, capture);
                }
            }
        });
		popup = (LinearLayout) findViewById(R.id.popup);
		WindowManager.LayoutParams layout = getWindow().getAttributes();
		brightness = layout.screenBrightness;
		layout.buttonBrightness = 0;
		getWindow().setAttributes(layout);
		//alert ("preversed brightness is "+brightness);
	}
	
	public void start ()
	{
        camera = camera();
        
        config = new Config(camera);
        config.load();
        config.apply(camera);
        
        preview = new Preview (this, camera);
        ((FrameLayout) findViewById(R.id.preview)).addView(preview);
        ((FrameLayout) findViewById(R.id.preview)).setSoundEffectsEnabled (false);  
        
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
	
    private void close()
    {
        if (camera!= null)
        {
            preview.getHolder().removeCallback(preview);            
            camera.stopPreview();            
            camera.release();
            camera = null;
        }
    }
    
    private PictureCallback capture = new PictureCallback() 
    {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) 
        {
            lock = false;
            //config.focus.unlock (camera);
            //camera.cancelAutoFocus();
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null)
            {
                alert ("Failed to create file");
                return;
            }

            try 
            {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                alert (pictureFile.getAbsolutePath());
            }
            catch (FileNotFoundException e) 
            {
                alert ("File not found: " + e.getMessage());
            }
            catch (IOException e)
            {
                alert ("Error accessing file: " + e.getMessage());
            }
            try
            {
                camera.startPreview();
            }
            catch (Exception e) 
            {
                alert ("failed to start preview");
            }
        }
    };    
	
    @Override
    protected void onPause() 
    {
        super.onPause();
        close();              // release the camera immediately on pause event
    }
    
    @Override
    protected void onStop ()
    {
        super.onStop ();
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.buttonBrightness = brightness;
        getWindow().setAttributes(layout);       
        
    }
    
    @Override
    protected void onRestart() 
    {
        super.onResume();
        start();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) 
    { 
        if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_CAMERA) 
        {
            if (popup.getVisibility()==popup.VISIBLE)
            {
                popup.setVisibility(popup.INVISIBLE);
                WindowManager.LayoutParams layout = getWindow().getAttributes();
                layout.screenBrightness = brightness;
                getWindow().setAttributes(layout);                
            }
            else
            {
                WindowManager.LayoutParams layout = getWindow().getAttributes();
                layout.screenBrightness = 0F;
                getWindow().setAttributes(layout);
                popup.setVisibility(popup.VISIBLE);
            }
            return true;
        }
        else if (keyCode==KeyEvent.KEYCODE_VOLUME_UP)
        {
            //alert ("vol up");
            return true;
        }
        else if (keyCode==KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            //alert ("vol down");
            return true;
        }
        return super.onKeyDown(keyCode, event); 
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
    
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type)
    {
          return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type)
    {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists())
        {
            if (! mediaStorageDir.mkdirs())
            {
                debug ("failed to create folder");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + ".jpg");
        } 
        else if(type == MEDIA_TYPE_VIDEO) 
        {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_"+ timeStamp + ".mp4");
        }
        else 
        {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera)
    {
        camera.takePicture(null, null, capture);
    }    
    
}
