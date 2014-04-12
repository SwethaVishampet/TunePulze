package com.example.tunepulze;


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;



import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import nus.dtn.util.DtnMessage;
import nus.dtn.util.Descriptor;
import nus.dtn.api.fwdlayer.ForwardingLayerProxy;
import nus.dtn.api.fwdlayer.ForwardingLayerInterface;
import nus.dtn.api.fwdlayer.MessageListener;
import nus.dtn.middleware.api.DtnMiddlewareInterface;
import nus.dtn.middleware.api.DtnMiddlewareProxy;
import nus.dtn.middleware.api.MiddlewareEvent;
import nus.dtn.middleware.api.MiddlewareListener;

/** App that broadcasts messages to everyone using a Mobile DTN. */
public class BroadcastAppActivity extends YouTubeBaseActivity implements OnInitializedListener {

	public static String URL ="xS0XiOLW_Qk";
	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
		      boolean wasRestored) {
		    if (!wasRestored) {
		    	// player.cueVideo("xS0XiOLW_Qk");
		    	this.player = player;
		    	
		    }
	}
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );

        try {

            // Specify what GUI to use
            setContentView ( R.layout.music );
            
            
            //createToast("Test");
            // Set a handler to the current UI thread
            handler = new Handler();

            // Get references to the GUI widgets
            textView_Message = (TextView) findViewById ( R.id.TextView_Message );
            editText_Message = (EditText) findViewById ( R.id.EditText_Message );
            button_Send = (Button) findViewById ( R.id.Button_Send );
//            button_Heart = (Button) findViewById ( R.id.Button_Heart_Rate );
//            button_pedometer = (Button) findViewById(R.id.Button_Steps_Taken);
            YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
            youTubeView.initialize("AIzaSyB_P66YnFwE2P0dWYanQx6Tic4MW-ejagU", this);
            
//           button_pedometer.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					Thread clickThread = new Thread() {
//						public void run() {
//							try {
//								createToast ( "Started Pedometer thread" );
//								Intent intent = new Intent(getApplicationContext(), Pedometer.class);
//								startActivity(intent);
//							}
//							catch(Exception e) {
//								Log.e(STORAGE_SERVICE, "Unable to start pedometer thread");
//							}
//						}
//					};
//					
//					clickThread.start();
//					
//				}
//			});
//            button_Heart.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					
//					Thread clickThread = new Thread() {
//						public void run() {
//							try {
//								createToast ( "Started thread" );
//								Intent intent = new Intent(getApplicationContext(), HeartRateActivity.class);
//								startActivity(intent);
//							}
//							catch(Exception e) {
//								Log.e(STORAGE_SERVICE, "Unable to start thread");
//							}
//						}
//					};
//					
//					clickThread.start();
//				}
//			});
            
            // Set the button's click listener
            button_Send.setOnClickListener ( new View.OnClickListener() {
                    public void onClick ( View v ) {

                        // Good practise to do I/O in a new thread
                        Thread clickThread = new Thread() {
                                public void run() {

                                    try {
                                    	//  player.cueVideo("xS0XiOLW_Qk");
                                        // Construct the DTN message
                                        DtnMessage message = new DtnMessage();
                                        String chatMessage =URL;
                                        // Data part
                                        message.addData()                  // Create data chunk
                                            .writeString ( chatMessage );  // Chat message

                                        // Broadcast the message using the fwd layer interface
                                        fwdLayer.sendMessage ( descriptor , message , "everyone" , null );

                                        // Tell the user that the message has been sent
                                        createToast ( "Chat message broadcast!" );
                                        
                                    }
                                    catch ( Exception e ) {
                                        // Log the exception
                                        Log.e ( "BroadcastApp" , "Exception while sending message" , e );
                                        // Inform the user
                                        createToast ( "Exception while sending message, check log" );
                                    }
                                }
                            };
                        clickThread.start();

                        // Inform the user
                        createToast ( "Broadcasting message..." );
                    } 
                } );

            // Start the middleware
            middleware = new DtnMiddlewareProxy ( getApplicationContext() );
            middleware.start ( new MiddlewareListener() {
                    public void onMiddlewareEvent ( MiddlewareEvent event ) {
                        try {

                            // Check if the middleware failed to start
                            if ( event.getEventType() != MiddlewareEvent.MIDDLEWARE_STARTED ) {
                                throw new Exception( "Middleware failed to start, is it installed?" );
                            }

                            // Get the fwd layer API
                            fwdLayer = new ForwardingLayerProxy ( middleware );

                            // Get a descriptor for this user
                            // Typically, the user enters the username, but here we simply use IMEI number
                            TelephonyManager telephonyManager = 
                                (TelephonyManager) getSystemService ( Context.TELEPHONY_SERVICE );
                            descriptor = fwdLayer.getDescriptor ( "nus.dtn.app.broadcast" , telephonyManager.getDeviceId() );

                            // Set the broadcast address
                            fwdLayer.setBroadcastAddress ( "nus.dtn.app.broadcast" , "everyone" );

                            // Register a listener for received chat messages
                            ChatMessageListener messageListener = new ChatMessageListener();
                            fwdLayer.addMessageListener ( descriptor , messageListener );
                        }
                        catch ( Exception e ) {
                            // Log the exception
                            Log.e ( "BroadcastApp" , "Exception in middleware start listener" , e );
                            // Inform the user
                            createToast ( "Exception in middleware start listener, check log" );
                        }
                    }
                } );
        }
        catch ( Exception e ) {
            // Log the exception
            Log.e ( "BroadcastApp" , "Exception in onCreate()" , e );
            // Inform the user
            createToast ( "Exception in onCreate(), check log" );
        }
    }

    /** Called when the activity is destroyed. */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            // Stop the middleware
            // Note: This automatically stops the API proxies, and releases descriptors/listeners
            middleware.stop();
        }
        catch ( Exception e ) {
            // Log the exception
            Log.e ( "BroadcastApp" , "Exception on stopping middleware" , e );
            // Inform the user
            createToast ( "Exception while stopping middleware, check log" );
        }
    }

    /** Listener for received chat messages. */
    private class ChatMessageListener 
        implements MessageListener {

        /** {@inheritDoc} */
        public void onMessageReceived ( String source , 
                                        String destination , 
                                        DtnMessage message ) {

            try { 

                // Read the DTN message
                // Data part
                message.switchToData();
                final String chatMessage = message.readString();
                createToast("Message Received " + chatMessage);
                // Update the text view in Main UI thread
                handler.post ( new Runnable() {
                        public void run() {
                            player.cueVideo(chatMessage);
                        }
                    } );
            }
            catch ( Exception e ) {
                // Log the exception
                Log.e ( "BroadcastApp" , "Exception on message event" , e );
                // Tell the user
                createToast ( "Exception on message event, check log" );
            }
        }
    }

    /** Helper method to create toasts. */
    private void createToast ( String toastMessage ) {

        // Use a 'final' local variable, otherwise the compiler will complain
        final String toastMessageFinal = toastMessage;

        // Post a runnable in the Main UI thread
        handler.post ( new Runnable() {
                @Override
                public void run() {
                    Toast.makeText ( getApplicationContext() , 
                                     toastMessageFinal , 
                                     Toast.LENGTH_SHORT ).show();
                }
            } );
    }

    /** Text View (displays messages). */
    private TextView textView_Message;
    /** Edit Text (user enters message here). */
    private EditText editText_Message;
    /** Button to trigger action (sending message). */
    private Button button_Send;
    
    private Button button_Heart;
    
    private Button button_pedometer;
    /** DTN Middleware API. */
    private DtnMiddlewareInterface middleware;
    /** Fwd layer API. */
    private ForwardingLayerInterface fwdLayer;

    /** Sender's descriptor. */
    private Descriptor descriptor;
    
    /** Handler to the main thread to do UI stuff. */
    private Handler handler;

    private YouTubePlayer player;
	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
			createToast("Initialization failed");
		
	}
}
