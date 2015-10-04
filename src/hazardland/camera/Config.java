package hazardland.camera;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;

public class Config
{
    public Config.Picture picture;
    public Config.Preview preview;
    public Config.Focus focus;
    public Config.Flash flash;
    public Config (Camera camera)
    {
        Camera.Parameters config = camera.getParameters();
        picture = new Config.Picture(config.getPictureSize());
        picture.quality = config.getJpegQuality();
        preview = new Config.Preview(config.getPreviewSize());
        focus = new Config.Focus(config.getFocusMode(), config.getMeteringAreas());
        flash = new Config.Flash(config.getFlashMode());
        
        Config.Picture good;
        List<Size> sizes;
        
        debug ("Picture", true);
        good = new Config.Picture(0,0);
        sizes = config.getSupportedPictureSizes();
        for (Size size : sizes)
        {
            debug (size.width*9/16+" for "+size.width+"x"+size.height);
            if (size.width*9/16==size.height && size.width>good.width)
            {
                good.width = size.width;
                good.height = size.height;
                debug ("16:9");
            }
        }
        if (good.width>0)
        {
            picture.width = good.width;
            picture.height = good.height;
        }
        
        debug ("Preview", true);        
        good = new Config.Picture(0,0);
        sizes = config.getSupportedPreviewSizes();
        for (Size size : sizes)
        {
            debug (size.width*9/16+" for "+size.width+"x"+size.height);
            if ((size.width==864 && size.height==480) || (size.width*9/16==size.height && (size.width<good.width || good.width==0) && size.width>=640))
            {
                good.width = size.width;
                good.height = size.height;
                debug ("16:9");
            }
        }
        if (good.width>0)
        {
            preview.width = good.width;
            preview.height = good.height;
        }        
    }
    public void load ()
    {
        //preview.width = 864;
        //preview.height = 480;
        //picture.width = 3264;
        //picture.height = 1836;
        picture.quality = 100;
        focus.center();
        flash.off();
    }
    public void apply (Camera camera)
    {
        Camera.Parameters config = camera.getParameters();
        
        config.setPictureSize (picture.width, picture.height);
        
        config.setPreviewSize (preview.width, preview.height);
        
        config.setFocusMode (focus.mode);
        config.setMeteringAreas (focus.areas);
        
        config.setFlashMode (flash.mode);
        
        config.setJpegQuality(picture.quality);
                
        camera.setParameters (config);
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
    
    class Picture
    {
        public int width;
        public int height;
        public int quality;
        public Picture ()
        {
            
        }
        public Picture (Camera.Size size)
        {
            this.width = size.width;
            this.height = size.height;
        }
        public Picture (int width, int height)
        {
            this.width = width;
            this.height = height;
        }
    }

    class Preview
    {
        public int width;
        public int height;
        public Preview ()
        {
            
        }
        public Preview (Camera.Size size)
        {
            this.width = size.width;
            this.height = size.height;
        }
        public Preview (int width, int height)
        {
            this.width = width;
            this.height = height;
        }
    }
    
    class Flash
    {
        public String mode;
        public Flash ()
        {
            mode = Camera.Parameters.FLASH_MODE_OFF;
        }
        public Flash (String mode)
        {
            this.mode = mode;
        }
        public void on ()
        {
            this.mode = Camera.Parameters.FLASH_MODE_ON;
        }
        public void off ()
        {
            this.mode = Camera.Parameters.FLASH_MODE_OFF;
        }
        public void auto ()
        {
            this.mode = Camera.Parameters.FLASH_MODE_AUTO;
        }
        public void red ()
        {
            this.mode = Camera.Parameters.FLASH_MODE_RED_EYE;
        }
        public void torch ()
        {
            this.mode = Camera.Parameters.FLASH_MODE_TORCH;
        }
    }
    
    class Focus
    {
        public String mode;
        public List<Camera.Area> areas;
        public Focus ()
        {
            this.areas = new ArrayList<Camera.Area>();   
        }
        public Focus (String mode)
        {
            this.areas = new ArrayList<Camera.Area>();            
            this.mode = mode;
        }
        public Focus (String mode, List<Camera.Area> areas)
        {
            if (areas!=null)
            {
                this.areas = areas;    
            }
            else
            {
                this.areas = new ArrayList<Camera.Area>(); 
            }
            this.mode = mode;
        }
        public void area (int x1, int y1, int x2, int y2, int weight)
        {
            Rect area = new Rect (x1, y1, x2, y2);
            areas.add (new Camera.Area(area, weight));            
        }
        public void center ()
        {            
            area (-200, -200, 200, 200, 100);
            this.mode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
            
        }
        public void center (String mode)
        {
            area (-200, -200, 200, 200, 100);
            this.mode = mode;
        }
        public void center (int width)
        {
            area (-1*(width/2),-1*(width/2),(width/2),(width/2),100);
            this.mode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
        }
        public void center (int width, String mode)
        {
            area (-1*(width/2),-1*(width/2),(width/2),(width/2),100);
            this.mode = mode;
        }
        public void lock (Camera camera)
        {
            Camera.Parameters config = camera.getParameters();
            config.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
            camera.setParameters(config);            
        }
        public void unlock (Camera camera)
        {
            Camera.Parameters config = camera.getParameters();
            config.setFocusMode(mode);
            camera.setParameters(config);                        
        }
    }
}