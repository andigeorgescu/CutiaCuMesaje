package com.example.android.cutiacumesaje;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
 
public class Categorii extends ListActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorii);
        String[] categorii = getResources().getStringArray(R.array.Categorii);
         
       
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, R.id.textView1, categorii));
         
        ListView lv = getListView();
 
       lv.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			String item = ((TextView) view.findViewById(R.id.textView1))
                    .getText().toString();
			Intent in = new Intent(getApplicationContext(),SingleListItem.class);
			in.putExtra("item", item);
			startActivity(in);		
		}
    	   
    	   
	});
          
          }
     
      }

    

	