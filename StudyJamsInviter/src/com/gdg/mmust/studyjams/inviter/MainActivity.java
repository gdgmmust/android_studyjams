package com.gdg.mmust.studyjams.inviter;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	ProgressDialog dialog;
	TextView tvInvited;
	Button btInvite;
	EditText etMessage;
	
	SmsManager smsManager;
	public static final String SMS_SENT = "com.gdg.mmust.studyjams.inviter.SMS_SENT";
	public static final String SMS_DELIVERED="com.gdg.mmust.studyjams.inviter.SMS_DELIVERED";
	public static final String RECIPIENT = "recepient";
	SmsReceiver receiver;
	String [] recipients;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(receiver, new IntentFilter(SMS_SENT));
		registerReceiver(receiver, new IntentFilter(SMS_DELIVERED));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.quick_menu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==R.id.add_members){
			startActivity(new Intent(getApplicationContext(), Members.class));
		}
		return super.onOptionsItemSelected(item);
	}

	private void init() {
		// TODO Auto-generated method stub
		tvInvited = (TextView) findViewById(R.id.activity_main_textView_invited);
		btInvite = (Button) findViewById(R.id.activity_main_button_invite);
		etMessage = (EditText) findViewById(R.id.activity_main_editText_message);
		
		btInvite.setOnClickListener(this);
		receiver = new SmsReceiver();
		smsManager = SmsManager.getDefault();
		
		recipients = getMembers();
		Toast.makeText(MainActivity.this," Members " +recipients.length, Toast.LENGTH_SHORT).show();
	}

	private String[] getMembers() {
		// TODO Auto-generated method stub
		Database db = new Database(MainActivity.this);
		SQLiteDatabase sql = db.getReadableDatabase();
		Cursor cursor = sql.query("members_table", null,null,null,null,null,null);
		String [] phonenumbers = new String[cursor.getCount()];
		int x = 0;
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
			phonenumbers[x] = cursor.getString(cursor.getColumnIndex("person_number"));
			x++;
		}
		return phonenumbers;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(etMessage.getText().length()>0)
		sendMessage();
		else etMessage.setError("Enter message");
	}

	private void sendMessage() {
		// TODO Auto-generated method stub
		
			
		btInvite.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String message = etMessage.getText().toString();/*"Good evening, You are welcomed to take part in the android " +
						"fundamental studyjam tomorrow starting at 8am. Venue SPD 004. Please keep time. This is a no-reply message, For any queries post on the GDG Whatsapp group. Kind regards.";
				*/
				for(int x = 0; x<recipients.length;x++){
					Bundle extras = new Bundle();
					extras.putString(RECIPIENT, recipients[x]);
					PendingIntent sentIntent = PendingIntent.getBroadcast(MainActivity.this, new Random().nextInt(100), new Intent(SMS_SENT).putExtras(extras), PendingIntent.FLAG_ONE_SHOT);
					PendingIntent deliveryIntent = PendingIntent.getBroadcast(MainActivity.this, new Random().nextInt(100), new Intent(SMS_DELIVERED).putExtras(extras), PendingIntent.FLAG_ONE_SHOT);
					
					if(message.length()>160){
						
						ArrayList<String> parts = smsManager.divideMessage(message);
						
						ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();;
						ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
						
						for(int i =0;i<parts.size();i++){
							sentIntents.add(sentIntent);
							deliveryIntents.add(deliveryIntent);
						}
						
						smsManager.sendMultipartTextMessage(recipients[x], null, parts , sentIntents, deliveryIntents );
						}else{
							smsManager.sendTextMessage(recipients[x], null, message, sentIntent, deliveryIntent);
						}
					tvInvited.setText(tvInvited.getText().toString() +"\n" +"Sending message to "+recipients[x]);
			}}
		});
		}
	
	class SmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle bundle = intent.getExtras();
			if(intent.getAction().equals(SMS_SENT)){
				switch(getResultCode()){
				case Activity.RESULT_OK: Toast.makeText(context, "Sms sent to "+bundle.getString(RECIPIENT), Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE: Toast.makeText(context, "Fail : Sms not sent to "+bundle.getString(RECIPIENT)+", generic failure ", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE: Toast.makeText(context, "Fail : Sms not sent to "+bundle.getString(RECIPIENT)+", no service ", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU: Toast.makeText(context, "Fail : Sms not sent to "+bundle.getString(RECIPIENT)+", null pdu ", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:  Toast.makeText(context, "Fail : Sms not sent to "+bundle.getString(RECIPIENT)+", Radio off ", Toast.LENGTH_SHORT).show();
					break;
				default: Toast.makeText(context, "Fail : Sms not sent to "+bundle.getString(RECIPIENT)+", unknown error", Toast.LENGTH_SHORT).show();
					break;
					
				}
				
			}
			if(intent.getAction().equals(SMS_DELIVERED)){
				Toast.makeText(context, "Sms sent to "+bundle.getString(RECIPIENT), Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	
	
}
