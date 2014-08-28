
package com.example.android.cutiacumesaje;
 
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
 
public class SingleListItem extends ListActivity{
    
	private ProgressDialog pDialog;
	
	private static String url;
	
	JSONArray data = null;
	
	ArrayList<HashMap<String,String>> listaMesaje;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_lista_mesaje);
         
        listaMesaje = new ArrayList<HashMap<String,String>>();
         
        Intent i = getIntent();
       
        String product = i.getStringExtra("item");
       
       
        if(product.equals("Anul Nou")) url = new String("http://www.conceptapps.ro/apps/messages/mesaje-get.php?catid=2");
        else if(product.equals("Craciun")) url = new String("http://www.conceptapps.ro/apps/messages/mesaje-get.php?catid=1");
        else if(product.equals("Mesaje de dragoste")) url =new String("http://www.conceptapps.ro/apps/messages/mesaje-get.php?catid=7");
        else if(product.equals("Mesaje Haioase")) url = new String("http://www.conceptapps.ro/apps/messages/mesaje-get.php?catid=6");
        
        ListView lv = getListView();
        lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String inf = ((TextView) view.findViewById(R.id.informal)).getText().toString();
				String form = ((TextView) view.findViewById(R.id.formal)).getText().toString();

				Intent msg =new Intent(getApplicationContext(),CompuneActivity.class);
				msg.putExtra("inf",inf);
				msg.putExtra("form",form);
				startActivity(msg);
			}
		});
        new GetData().execute();
        if(isNetworkAvailable()){
        	
        	
        } else {Toast.makeText(getBaseContext(), 
                "Verificati conexiunea la internet!", 
                Toast.LENGTH_LONG).show();}
         
    }
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	private class GetData extends AsyncTask<Void,Void,Void>{
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			pDialog = new ProgressDialog(SingleListItem.this);
			pDialog.setMessage("Se încarcă lista de mesaje...");
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... arg0){
			
			ServiceHandler sh = new ServiceHandler();
			String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
			
			if(jsonStr != null){
				try{
					JSONObject jsonObj = new JSONObject(jsonStr);
					
				 	data = jsonObj.getJSONArray("data");
					
					for(int i = 0; i< data.length(); i++)
					{
						JSONObject c = data.getJSONObject(i);
						
						String formal = c.getString("formal");
						String informal = c.getString("informal");
						
						HashMap<String, String> mesaj = new HashMap<String,String>(); 
						mesaj.put("formal",formal);
						mesaj.put("informal",informal);
						
						listaMesaje.add(mesaj);
					}
				} catch (JSONException e){ 
					e.printStackTrace();
				}
			}
			else {
				Log.e("ServiceHandler","Nu am putut gasi mesaje");
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result){
			super.onPostExecute(result);
			if(pDialog.isShowing()) pDialog.dismiss();
			
			ListAdapter adapter = new SimpleAdapter(SingleListItem.this, listaMesaje, R.layout.single_list_item_view,
					new String[]{"informal","formal"},new int[]{R.id.informal,R.id.formal});
			
			setListAdapter(adapter);
		}
	}
}
