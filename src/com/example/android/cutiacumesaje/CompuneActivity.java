package com.example.android.cutiacumesaje;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Switch;
import android.widget.Toast;
 
public class CompuneActivity extends Activity 
{	
	List<String> MobNumber = new ArrayList<String>();
	List<String> ContName = new ArrayList<String>();

	private static final int PICK_CONTACT = 1;
	Button btnSendSMS;
    EditText txtPhoneNo;
    EditText txtMessage;
    private static String number = null;
    private static String name = null;
    private ShareActionProvider mShareActionProvider;  
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_message);        
 
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
        txtMessage = (EditText) findViewById(R.id.txtMessage);
        
        Intent msg = getIntent();
        final String inf = msg.getStringExtra("inf");  // primeste mesajul selectat din lista
        final String form = msg.getStringExtra("form");
        
        Switch sw = (Switch) findViewById(R.id.switch1);
        sw.setChecked(false);
        txtMessage.setText(inf);
        
        sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

        	   @Override
        	   public void onCheckedChanged(CompoundButton buttonView,
        	     boolean isChecked) {

        	    if(isChecked){
        	     txtMessage.setText(form);
        	    }else{
        	     txtMessage.setText(inf);
        	    }

        	   }
        	  });
        
        btnSendSMS.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {               
            	  String phoneNo= txtPhoneNo.getText().toString();
//                int i=Integer.parseInt(txtPhoneNo.getText().toString().replaceAll("[\\D]",""));
//                String phoneNo = Integer.toString(i);
            	int k = 0;
                String message = txtMessage.getText().toString();                 
                if (phoneNo.length()>0 && message.length()>0)
                	{
                	if(MobNumber.size()>0)
                	{for(k =0; k< MobNumber.size() ; k++)
                		sendSMS(MobNumber.get(k).toString(),message);
                		k=0;
                		txtPhoneNo.setText("");
                		txtMessage.setText("");
                	}
                	else
                	{
                	sendSMS(phoneNo, message);
                	txtPhoneNo.setText("");
            		txtMessage.setText("");}}

                          
                
                else
                {
                    Toast.makeText(getBaseContext(), 
                        "Please enter both phone number and message.", 
                        Toast.LENGTH_SHORT).show();
            }
            }});        
    }    

   
	
    private void sendSMS(String phoneNumber, String message)
    {        
    	
    	final ProgressDialog ringProgressDialog = ProgressDialog.show(CompuneActivity.this, "Please wait ...", "Se trimite mesajul ...", true);
    	   ringProgressDialog.setCancelable(true);

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

 
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", 
                                Toast.LENGTH_SHORT).show();
                                ringProgressDialog.dismiss();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", 
                                Toast.LENGTH_SHORT).show();
                        ringProgressDialog.dismiss();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", 
                                Toast.LENGTH_SHORT).show();
                        ringProgressDialog.dismiss();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", 
                                Toast.LENGTH_SHORT).show();
                        ringProgressDialog.dismiss();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", 
                                Toast.LENGTH_SHORT).show();
                        ringProgressDialog.dismiss();
                        break;
                }
            }
        }, new IntentFilter(SENT));
 
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", 
                                Toast.LENGTH_SHORT).show();
                        break;                        
                }
            }
        }, new IntentFilter(DELIVERED));        
 
        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);   
        ArrayList<String> parts =sms.divideMessage(message);
        int numParts = parts.size();

        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();

        for (int i = 0; i < numParts; i++) {
        sentIntents.add(PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0));
        deliveryIntents.add(PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0));
        }

        sms.sendMultipartTextMessage(phoneNumber,null, parts, sentIntents, deliveryIntents);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// Inflate menu resource file.  
        getMenuInflater().inflate(R.menu.main, menu);  
        // Locate MenuItem with ShareActionProvider  
        MenuItem item = menu.findItem(R.id.menu_item_share);  
        // Fetch and store ShareActionProvider  
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();  
        setShareIntent(createShareIntent());  
        // Return true to display menu  
        return true;  
    }
    private void setShareIntent(Intent shareIntent) {  
        if (mShareActionProvider != null) {  
             mShareActionProvider.setShareIntent(shareIntent);  
        }  
   }  
   private Intent createShareIntent() {  
        Intent shareIntent = new Intent(Intent.ACTION_SEND);  
        shareIntent.setType("text/plain");  
        shareIntent.putExtra(Intent.EXTRA_TEXT,  
                  txtMessage.getText().toString());  
        return shareIntent;  
   }  
    
   public void selecteazaContact(View view){
	   Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
	    pickContactIntent.setType(Phone.CONTENT_TYPE);
	    startActivityForResult(pickContactIntent, PICK_CONTACT);
   }
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode == PICK_CONTACT) {
           
           if (resultCode == RESULT_OK) {
               
               Uri contactUri = data.getData();
               String[] projection = {Phone.NUMBER, Phone.DISPLAY_NAME};

               Cursor cursor = getContentResolver()
                       .query(contactUri, projection, null, null, null);
               
               
               int column = cursor.getColumnIndex(Phone.NUMBER);
               int colName = cursor.getColumnIndex(Phone.DISPLAY_NAME);
               
               for(cursor.moveToFirst();!cursor.isAfterLast(); cursor.moveToNext()){
            	    number =cursor.getString(column);
            	    name =cursor.getString(colName);
               }

               txtPhoneNo.setText(txtPhoneNo.getText()+name+"("+number+");");
               txtPhoneNo.setSelection(txtPhoneNo.getText().toString().length());
              
               	MobNumber.add(number);
//              ContName.add(name);
              
               //Creez o lista de numere de telefon pentru a trimite mai multe mesaje in acelasi timp
           }
       }
   }

}
