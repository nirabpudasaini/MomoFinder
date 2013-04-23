package com.nirab.momofinder;

import java.io.File;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.IArchiveFile;
import org.osmdroid.tileprovider.modules.MBTilesFileArchive;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import com.nirab.momofinder.MyLocation.LocationResult;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

public class Map extends Activity implements LocationListener{

	ProgressDialog mProgressDialog;
	private MapView mv;
	private MapController mc;
	String towers;
	int lat=0, lng=0;
	MyLocation myLocation = new MyLocation();
	GeoPoint currentloc;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		try{
		
		/**
		    * This whole thing revolves around instantiating a MapView class, way,
		    * way below. And MapView requires a ResourceProxy. Who are we to deny
		    * its needs? Let's create one!
		    *
		    * It would have been nice if this was taken care of in the MapView
		    * constructor. Interestingly MapView *has* a constructor that creates a
		    * new DefaultResourceProxyImpl but unfortunately that one doesn't allow
		    * us to specify the parameters we *do* need to set ...
		    */
		    DefaultResourceProxyImpl resProxy;
		    resProxy = new DefaultResourceProxyImpl(this.getApplicationContext());
		 
		    /**
		    * A class that implements the ITileSource interface knows how to
		    * convert an InputStream or a file path into a Drawable. It doesn't do
		    * much more than that. The real 'sourcery' is performed by
		    * MapTileFileArchiveProvider which will be introduced shortly.
		    *
		    * What we need is really a BitmapTileSourceBase instance, but this
		    * class is defined as abstract. XYTileSource is not and comes closest
		    * to what we want.
		    *
		    * Comment: I don't quite get why BitmapTileSource base is abstract; it
		    * doesn't contain any abstract methods.
		    */
		    XYTileSource tSource;
		    tSource = new XYTileSource("mbtiles",ResourceProxy.string.offline_mode,8, 15, 256, ".png", "http://who.cares/");
		 
		    /**
		    * Don't think the name SimpleRegisterReceiver is particularly well
		    * chosen. SimpleReceiverRegistrar would have been better because the
		    * only thing SimpleRegisterReceiver does, is wrap the methods
		    * Context.registerReceiver(..) and Context.unregisterReceiver(..). Have
		    * a look at the source if you don't believe me ;-).
		    *
		    * So why does it exist then?? Don't know, but it's quite possible to
		    * just ignore this step and state the Activity implements
		    * IRegisterReceiver and replace the 'simpleReceiver' variable with
		    * 'this' further down (no additional implementation required).
		    */
		    SimpleRegisterReceiver sr = new SimpleRegisterReceiver(this);
		 
		    /**
		    * The following looks complicated, but really only creates an
		    * iArchiveFile[]. Apparently Marc Kurtz and Nicolas Gramlich, the
		    * authors of MapTileFileArchiveProvider, figured it might be useful to
		    * support multiple files/sources. I guess that might make sense if
		    * you're providing separate files for, for example, cities.
		    *
		    * They also provided quite a bit of logic in MapTileFileArchiveProvider
		    * for handling SD Card inserts and ejects. Additionally, if files are
		    * not explicitly specified they can be dynamically loaded from the
		    * /mnt/sdcard/osmdroid directory, which is a nice feature.
		    */
		    String packageDir = "/osmdroid";
		    String p = Environment.getExternalStorageDirectory() + packageDir;
		    File f = new File(p, "momofind_ktm.mbtiles");
		    IArchiveFile[] files = { MBTilesFileArchive.getDatabaseFileArchive(f) };
		 
		    MapTileModuleProviderBase moduleProvider;
		    moduleProvider = new MapTileFileArchiveProvider(sr, tSource, files);
		 
		    /**
		    * So at this point we have a MapTileModuleProvider that provides
		    * MapTileModules: a MapTileModule looks at one or more sources, which
		    * are *not* ITileSources but IArchiveFiles and provides MapTiles. What,
		    * then, does MapTileProviderArray do? Well, it just adds another layer
		    * to the complexity cake: this makes it possible to set multiple
		    * MapTileModuleProviders, such as an on- and offline source. I'm sure
		    * it's useful for someone, but for simple applications it's probably
		    * too much.
		    */
		    MapTileModuleProviderBase[] pBaseArray;
		    pBaseArray = new MapTileModuleProviderBase[] { moduleProvider };
		 
		    MapTileProviderArray provider;
		    provider = new MapTileProviderArray(tSource, null, pBaseArray);
		 
		    /**
		    * Are we there yet??? Create the MapView already!
		    */
		     mv = new MapView(this, 256, resProxy, provider);
		    
		 
		    // Set the MapView as the root View for this Activity; done!
		    setContentView(mv);
		    
		}
		catch(Exception IO){
			setContentView(R.layout.map);
			mv = (MapView) findViewById(R.id.mapview);
			mv.setTileSource(TileSourceFactory.MAPNIK);
			}
		
	
		mv.setBuiltInZoomControls(true);
		mv.setMultiTouchControls(true);
		mv.setClickable(true);
		mc = mv.getController();
		mc.setZoom(14);
		findCurrentLocation();
		
		
		
	}
		
		
	

private void findCurrentLocation() {
        myLocation.getLocation(this, locationResult);
    }

public LocationResult locationResult = new LocationResult() {

        @Override
        public void gotLocation(Location location) {
            // TODO Auto-generated method stub
            if (location != null) {
            	currentloc = new GeoPoint (location.getLatitude(),location.getLongitude());
            	mc.animateTo(currentloc);
                String strloc  = location.getLatitude() + ","
                        + location.getLongitude();
                Toast.makeText(getApplicationContext(), strloc, Toast.LENGTH_LONG).show();
                
            }
        }
    };
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}

