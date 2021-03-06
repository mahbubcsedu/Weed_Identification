package com.mpss.weed.id.expert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.mpss.weed.id.R;
import com.mpss.weed.id.common.Request;
import com.mpss.weed.id.common.RequestList;
import com.mpss.weed.id.common.Weed;
import com.mpss.weed.id.utils.ActionItem;
import com.mpss.weed.id.utils.AppLog;
import com.mpss.weed.id.utils.CustomizeDialog;
import com.mpss.weed.id.utils.DownloadImageTask;
import com.mpss.weed.id.utils.GMailSender;
import com.mpss.weed.id.utils.NumberPicker;
import com.mpss.weed.id.utils.NumberPicker1;
import com.mpss.weed.id.utils.QuickAction;
import com.mpss.weed.id.utils.UploadtoServer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ExpertVarifyDiagnoseActivity extends Activity{

	Weed w;
	RequestList request;
	//ImageView previewImage;
	EditText txtComments;
	String strImagePath="",strAudioPath="",imageheight="",imagewidth="";
	String expertID,identificationID;
	int imageID1,imageID2,imageID3;
	int rankID1,rankID2,rankID3;
	Context context;
	Gallery pre_gallery;
	ProgressDialog progress;
	ArrayList<Weed> weeds_selected = new ArrayList<Weed>();
    int   ranklist[]=new int[3];
	WeedGalleryAdapter adapter;
	
	private static final String TAG="PreviewDiagnose";
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private MediaRecorder recorder = null;
	
	Chronometer mChronometer;
	private int currentFormat = 0;
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP }; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		context=this;
		setContentView(R.layout.preview_diagnose);		
		request=getIntent().getParcelableExtra("request");
		progress = new ProgressDialog(context);
		
		identificationID=request.getIdentificationId();
		txtComments=(EditText)findViewById(R.id.txtCommentsEx);
		
		weeds_selected=getIntent().getParcelableArrayListExtra("weeds_selected");
		ranklist=getIntent().getIntArrayExtra("ranking");
		
		if(weeds_selected.size()==3)
		{
		imageID1=weeds_selected.get(0).getId();
		rankID1=ranklist[0];
		imageID2=weeds_selected.get(1).getId();
		rankID2=ranklist[1];
		imageID3=weeds_selected.get(2).getId();
		rankID3=ranklist[2];
		}
		else if(weeds_selected.size()==2){
			imageID1=weeds_selected.get(0).getId();
			rankID1=ranklist[0];
			imageID2=weeds_selected.get(1).getId();
			rankID2=ranklist[1];
			imageID3=0;
			rankID3=0;
			}
		else if(weeds_selected.size()==1)
		{
			imageID1=weeds_selected.get(0).getId();
			rankID1=ranklist[0];
			imageID2=0;
			rankID2=0;
			imageID3=0;
			rankID3=0;
			}
		
		pre_gallery = (Gallery) findViewById(R.id.pre_diag_gallery);
		adapter = new WeedGalleryAdapter(this,
				android.R.layout.simple_gallery_item, weeds_selected);
		pre_gallery.setAdapter(adapter);
        
		pre_gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				
				Toast toast=Toast.makeText(getApplicationContext(), "Rank #" + ranklist[position],
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP|Gravity.TOP, 0, 0);
				
				toast.show();
				
				
			}
		});
		//setCurrentTimeOnView();
		//addListenerOnButton();
		
		setButtonHandlers();
		enableButtons(false);
		setFormatButtonCaption();
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
		    expertID= extras.getString("expertID");
	     }	
		Log.i(TAG,expertID);
		Button send = (Button) findViewById(R.id.sendEx);
		send.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {
				//btnHome.setEnabled(false);
				progress.setMessage("Loading weed database...");
				progress.setCancelable(false);
				progress.show();
				//SendEmail();
				new UploadExpertResponseTask().execute((Void[])null);
				
			}
		});
		
		
		
		
        
	}

	
	private void SendEmail(){
		try {   
            GMailSender sender = new GMailSender("crowdsourceagri@gmail.com", "crowdsourceagri123~");
            sender.sendMail("This is Subject",   
                    "This is Body",   
                    "mahbub2001@gmail.com",   
                    "mahbubcsedu@yahoo.com");   
        } catch (Exception e) {   
            Log.e("SendMail", e.getMessage(), e);   
        } 
	}
	/*
	private void setButtonHandlers() {
		((Button)findViewById(R.id.exprtbtnStart)).setOnClickListener(btnClick);
		((Button)findViewById(R.id.exprtbtnStop)).setOnClickListener(btnClick);
		((Button)findViewById(R.id.exprtbtnFormat)).setOnClickListener(btnClick);
		((Button)findViewById(R.id.exprtbtnReset)).setOnClickListener(btnClick);
		mChronometer = (Chronometer) findViewById(R.id.exprtchronometer);
	}

	private void enableButton(int id,boolean isEnable){
		((Button)findViewById(id)).setEnabled(isEnable);
	}

	private void enableButtons(boolean isRecording) {
		enableButton(R.id.exprtbtnStart,!isRecording);
		enableButton(R.id.exprtbtnFormat,!isRecording);
		enableButton(R.id.exprtbtnStop,isRecording);
		enableButton(R.id.exprtbtnReset,!isRecording);
	}

	private void setFormatButtonCaption(){
		((Button)findViewById(R.id.exprtbtnFormat)).setText(getString(R.string.audio_format) + " (" + file_exts[currentFormat] + ")");
	}

	private String getFilename(){
		String filePath="";
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);

		if(!file.exists()){
			file.mkdirs();
		}

		//can give the unique title of the record
		filePath=file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat];
		strAudioPath=filePath;
		return (strAudioPath);
	}

	private void startRecording(){
		recorder = new MediaRecorder();

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(output_formats[currentFormat]);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(getFilename());


		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);

		try {
			mChronometer.setBase(SystemClock.elapsedRealtime());
			recorder.prepare();
			recorder.start();
			mChronometer.start();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecording(){
		if(null != recorder){
			recorder.stop();
			recorder.reset();
			recorder.release();
			mChronometer.stop();
			recorder = null;
		}
	}

	private void displayFormatDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String formats[] = {"MPEG 4", "3GPP"};

		builder.setTitle(getString(R.string.choose_format_title))
		.setSingleChoiceItems(formats, currentFormat, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				currentFormat = which;
				setFormatButtonCaption();                                        
				dialog.dismiss();
			}
		})
		.show();
	}

	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			AppLog.logString("Error: " + what + ", " + extra);
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			AppLog.logString("Warning: " + what + ", " + extra);
		}
	};
	private void resetRecording(){
		mChronometer.setBase(SystemClock.elapsedRealtime());
		if(null!=recorder){
			recorder.reset();
			recorder=null;

		}
	}
	private View.OnClickListener btnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.exprtbtnStart:{
				AppLog.logString("Start Recording");                                        
				enableButtons(true);
				startRecording();                                                        
				break;
			}
			case R.id.exprtbtnStop:{
				AppLog.logString("Stop Recording");                                        
				enableButtons(false);
				stopRecording();                                        
				break;
			}
			case R.id.exprtbtnReset:{
				AppLog.logString("Resetting Recording");                                        
				enableButtons(false);
				resetRecording();                                        
				break;
			}
			case R.id.exprtbtnFormat:{
				displayFormatDialog();                                        
				break;
			}
			}
		}
	}; 
*/
	
	public void finishUploading(Boolean successful) {
		if (successful) {
			progress.dismiss();
			// Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(context, ExpertHomeActivity.class);
			intent.putExtra("expertID", expertID);
			startActivity(intent);
		} else {
			Toast.makeText(context, "uploading failed", Toast.LENGTH_SHORT).show();
		}
	}
	private class UploadExpertResponseTask extends AsyncTask<Void, Void, Boolean> {	
		@Override
		protected Boolean doInBackground(Void... voids) {
			//String temp = "http://mpss.csce.uark.edu/~mweathers/weedapp/insertrequest.php?image=";			
			UploadtoServer ut=new UploadtoServer(getString(R.string.expertresponse_url));
			ut.UploadExpertResponseInfo(identificationID,imageID1,imageID2,imageID3,rankID1,rankID2,rankID3, strAudioPath, txtComments.getText().toString(),expertID);
			
			/*ByteArrayOutputStream out = new ByteArrayOutputStream();			
			System.out.println(temp);
			HttpClient hc = new DefaultHttpClient();
			HttpPost post = new HttpPost(temp);

			String str = "***";
			try {
				HttpResponse rp = hc.execute(post);

				if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					str = EntityUtils.toString(rp.getEntity());
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(str);
			*/			
			return Boolean.TRUE;
			
		}

		@Override
		protected void onPostExecute(Boolean result) {			
			progress.dismiss();
			finishUploading(result);
		}
	}
	
	
	
	
	private void setButtonHandlers() {
		((ImageButton)findViewById(R.id.btnStartEx)).setOnClickListener(btnClick);
		((ImageButton)findViewById(R.id.btnStopEx)).setOnClickListener(btnClick);
		//((Button)findViewById(R.id.btnFormat)).setOnClickListener(btnClick);
		((ImageButton)findViewById(R.id.btnResetEx)).setOnClickListener(btnClick);
		mChronometer = (Chronometer) findViewById(R.id.chronometerEx);
	}

	private void enableButton(int id,boolean isEnable){
		((ImageButton)findViewById(id)).setEnabled(isEnable);
	}

	private void enableButtons(boolean isRecording) {
		enableButton(R.id.btnStartEx,!isRecording);
		//enableButton(R.id.btnFormat,!isRecording);
		enableButton(R.id.btnStopEx,isRecording);
		enableButton(R.id.btnResetEx,!isRecording);
	}

	private void setFormatButtonCaption(){
		//((Button)findViewById(R.id.btnFormat)).setText(getString(R.string.audio_format) + " (" + file_exts[currentFormat] + ")");
	}

	private String getFilename(){
		String filePath="";
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);

		if(!file.exists()){
			file.mkdirs();
		}

		//can give the unique title of the record
		//filePath=file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat];
		filePath=file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[0];
		strAudioPath=filePath;
		return (strAudioPath);
	}

	private void startRecording(){
		//((ImageButton)findViewById(R.id.btnStartEx)).setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_recorder_disable));
		((ImageButton)findViewById(R.id.btnStartEx)).setImageResource(R.drawable.btn_recorder_disable);
		((ImageButton)findViewById(R.id.btnStopEx)).setImageResource(R.drawable.btn_media_stop_enable);
		((ImageButton)findViewById(R.id.btnResetEx)).setImageResource(R.drawable.btn_reset_disable);
		recorder = new MediaRecorder();

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		//recorder.setOutputFormat(output_formats[currentFormat]);
		recorder.setOutputFormat(output_formats[0]);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(getFilename());


		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);

		try {
			mChronometer.setBase(SystemClock.elapsedRealtime());
			recorder.prepare();
			recorder.start();
			mChronometer.start();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecording(){
		((ImageButton)findViewById(R.id.btnStartEx)).setImageResource(R.drawable.btn_recorder_enable);
		((ImageButton)findViewById(R.id.btnStopEx)).setImageResource(R.drawable.btn_media_stop_disable);
		((ImageButton)findViewById(R.id.btnResetEx)).setImageResource(R.drawable.btn_reset_enable);
		if(null != recorder){
			recorder.stop();
			recorder.reset();
			recorder.release();
			mChronometer.stop();
			recorder = null;
		}
	}

	private void displayFormatDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String formats[] = {"MPEG 4", "3GPP"};

		builder.setTitle(getString(R.string.choose_format_title))
		.setSingleChoiceItems(formats, currentFormat, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				currentFormat = which;
				setFormatButtonCaption();                                        
				dialog.dismiss();
			}
		})
		.show();
	}

	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			AppLog.logString("Error: " + what + ", " + extra);
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			AppLog.logString("Warning: " + what + ", " + extra);
		}
	};
	private void resetRecording(){
		mChronometer.setBase(SystemClock.elapsedRealtime());
		((ImageButton)findViewById(R.id.btnStartEx)).setImageResource(R.drawable.btn_recorder_enable);
		((ImageButton)findViewById(R.id.btnStopEx)).setImageResource(R.drawable.btn_media_stop_disable);
		((ImageButton)findViewById(R.id.btnResetEx)).setImageResource(R.drawable.btn_reset_disable);
		if(null!=recorder){
			recorder.reset();			
			recorder=null;

		}
	}
	private View.OnClickListener btnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnStartEx:{
				AppLog.logString("Start Recording");                                        
				enableButtons(true);
				startRecording();                                                        
				break;
			}
			case R.id.btnStopEx:{
				AppLog.logString("Stop Recording");                                        
				enableButtons(false);
				stopRecording();                                        
				break;
			}
			case R.id.btnResetEx:{
				AppLog.logString("Resetting Recording");                                        
				enableButtons(false);
				resetRecording();                                        
				break;
			}
			/*case R.id.btnFormat:{
				displayFormatDialog();                                        
				break;
			}*/
			}
		}
	}; 
	
}
