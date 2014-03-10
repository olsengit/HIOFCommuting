package bachelor.tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bachelor.hiofcommuting.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;



public class TabMap extends Fragment {

	private GoogleMap googleMap;
	private SupportMapFragment fragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_tab_map, container,
				false);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		FragmentManager fm = getFragmentManager();
	    fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
	    if (fragment == null) {
	        fragment = SupportMapFragment.newInstance();
	        fm.beginTransaction().replace(R.id.map, fragment).commit();
	    }
	    else{
	    	initilizeMap();
	    	System.out.println("What");
	    	}
		/*try {
			initilizeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if (googleMap == null){
			googleMap = fragment.getMap();
			googleMap.addMarker(new MarkerOptions().position(new LatLng(0,0)));
			//initilizeMap();
		}
	}

	private void initilizeMap() {
		/*if (googleMap == null) {
			googleMap = ((SupportMapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
		}*/
		if (fragment == null) {
			System.out.println("fragment er null");
		}
		else {
			System.out.println("fragment er ikke null");
		}
		googleMap = fragment.getMap();
		
		LatLng Hi� = new LatLng(59.129443, 11.352908);
		LatLng greaker = new LatLng(59.26789, 11.03205);

		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(greaker, 10));

		googleMap.addMarker(new MarkerOptions().title("Gre�ker")
				.snippet("Et h�l").position(greaker));

		googleMap.addMarker(new MarkerOptions().title("Hi�")
				.snippet("Hi� Halden").position(Hi�));

		// googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		// check if map is created successfully or not
		if (googleMap == null) {
			Toast.makeText(getActivity().getApplicationContext(),
					"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
		}
	}

}
