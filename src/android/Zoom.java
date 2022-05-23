package cordova.plugin.zoom;

import java.util.Locale;
import java.util.Locale.Builder;
import java.util.IllformedLocaleException;
import java.util.concurrent.FutureTask;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;

import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomError;

import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MeetingError;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.InstantMeetingOptions;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.StartMeetingParams4NormalUser;
import us.zoom.sdk.StartMeetingParamsWithoutLogin;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.MeetingViewsOptions;

import cordova.plugin.zoom.AuthThread;

/**
 * Zoom
 *
 * A Cordova Plugin to use Zoom Video Conferencing services on Cordova applications.
 *
 * @author  Zoom Video Communications, Inc.
 * @version v4.6.21666.0603
 */
public class Zoom extends CordovaPlugin implements ZoomSDKAuthenticationListener, MeetingServiceListener {
    /* Debug variables */
    private static final String TAG = "<------- ZoomIonicAngularPlugin ---------->";
    private static final boolean DEBUG = false;
    public static final Object LOCK = new Object();

    private String WEB_DOMAIN = "https://zoom.us";

    private ZoomSDK mZoomSDK;
    private CallbackContext callbackContext;

    /**
     * execute
     *
     * The bridging method to get parameters from JavaScript to execute the relevant Java methods.
     *
     * @param action            action name.
     * @param args              arguments.
     * @param callbackContext   callback context.
     * @return                  true if everything runs smooth / false if something is wrong.
     * @throws JSONException    might throw exceptions when parsing JSON arrays and objects.
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
            throws JSONException {
        if (DEBUG) {
            Log.v(TAG, "----- [execute , action =" + action + "]");
            Log.v(TAG, "----- [execute , args =" + args + "]");
        }

        cordova.setActivityResultCallback(this);
        this.callbackContext = callbackContext;
        this.mZoomSDK = ZoomSDK.getInstance();

        switch(action) {
            case "initialize":
                String appKey = args.getString(0);
                String appSecret = args.getString(1);
                this.initialize(appKey, appSecret, callbackContext);
                break;
            case "login":
                String username = args.getString(0);
                String password = args.getString(1);
                if (DEBUG) {
                    Log.v(TAG, "----- [username =" + username + "]");
                }
                this.login(username, password, callbackContext);
                break;
            case "logout":
                this.logout(callbackContext);
                break;
            case "isLoggedIn":
                this.isLoggedIn(callbackContext);
                break;
            case "joinMeeting":
                String meetingNo = args.getString(0);
                String meetingPassword = args.getString(1);
                String displayNameJ = args.getString(2);
                JSONObject optionsJ = args.getJSONObject(3);
                this.joinMeeting(meetingNo, meetingPassword, displayNameJ, optionsJ, callbackContext);
                break;
            case "startMeeting":
                String meetingNum = args.getString(0);
                String displayNameS = args.getString(1);
                String zoomToken = args.getString(2);
                String zoomAccessToken = args.getString(3);
                String userId = args.getString(4);
                JSONObject optionsS = args.getJSONObject(5);
                this.startMeeting(meetingNum, displayNameS, zoomToken,
                        zoomAccessToken, userId, optionsS, callbackContext);
                break;
            case "startInstantMeeting":
                JSONObject optionsI = args.getJSONObject(0);
                this.startInstantMeeting(optionsI, callbackContext);
                break;
            case "setLocale":
                String languageTag = args.getString(0);
                this.setLocale(languageTag, callbackContext);
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * initialize
     *
     * Initialize Zoom SDK.
     *
     * @param appKey        Zoom SDK app key.
     * @param appSecret     Zoom SDK app secret.
     * @param callbackContext Cordova callback context.
     */
    private void initialize(String appKey, String appSecret, CallbackContext callbackContext) {
        if (DEBUG) {
            Log.v(TAG, "********** Zoom's initialize called **********");
        }

        // If the SDK has been successfully initialized, simply return.
        if (mZoomSDK.isInitialized()) {
            return;
        }

        // Note: When "null" is pass from JS to Android, it is transferred as a word "null".
        if (appKey == null || appKey.trim().isEmpty() || appKey.equals("null")
                || appSecret == null || appSecret.trim().isEmpty() || appSecret.equals("null")) {
            callbackContext.error("Both SDK key and secret cannot be empty");
            return;
        }


        try {
            AuthThread at = new AuthThread();                           // Prepare Auth Thread
            at.setCordova(cordova);                                     // Set cordova
            at.setCallbackContext(callbackContext);                     // Set callback context
            at.setAction("initialize");                                 // Set action
            at.setLock(LOCK);
            at.setInitParameters(appKey, appSecret, this.WEB_DOMAIN);   // Set init parameters
            FutureTask<Boolean> fr = new FutureTask<Boolean>(at);

            cordova.getActivity().runOnUiThread(fr);                    // Run init method on main thread

            boolean threadSuccess = fr.get();                           // False if has error.
            if (DEBUG) {
                Log.v(TAG, "******************Return from Future is: " + threadSuccess);
            }

            if (threadSuccess) {
                // Wait until the initialize result is back.
                synchronized (LOCK) {
                    try {
                        if (DEBUG) {
                            Log.v(TAG, "Wait................................");
                        }
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        if (DEBUG) {
                            Log.v(TAG, e.getMessage());
                        }
                    }
                }
            }

            callbackContext.success("Initialize successfully!");
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    /**
     * login
     *
     * Log user in with Zoom username and password.
     *
     * @param username      user's Zoom username.
     * @param password      user's Zoom password.
     * @param callbackContext cordova callback context.
     */
    private void login(String username, String password, CallbackContext callbackContext) {

        if (!this.mZoomSDK.isInitialized()) {
            // Zoom SDK instance has not been initialized.
            android.widget.Toast.makeText(
                    cordova.getActivity().getApplicationContext(),
                    "ZoomSDK has not been initialized successfully",
                    android.widget.Toast.LENGTH_LONG
            ).show();
            callbackContext.error("ZoomSDK has not been initialized successfully");
            return;
        }

        // Empty username or password.
        if (username.length() == 0 || password.length() == 0) {
            callbackContext.error("Username and password cannot be empty.");
            return;
        }

        // bind listener.
        this.mZoomSDK.addAuthenticationListener(this);
        // Try to log user in.

        //exec() call to Zoom.login blocked the main thread for 28ms. Plugin should use CordovaInterface.getThreadPool().
        cordova.getThreadPool().execute(new Runnable() {
           @Override
           public void run() {
               ZoomSDK mZoomSDK = ZoomSDK.getInstance();

               int response = mZoomSDK.loginWithZoom(username, password);
               if (DEBUG) {
                   Log.v(TAG, "[Login response ^^^^^^^^^^^^^^]= " + response);
               }

               if (response != ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
                   PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR,
                           getApiErrorMessage(response));
                   pluginResult.setKeepCallback(true);
                   callbackContext.sendPluginResult(pluginResult);
               }
           }
        });
    }

    /**
     * logout
     *
     * Log user out.
     *
     * @param callbackContext   cordova callback context.
     */
    private void logout(CallbackContext callbackContext) {

        if (!this.mZoomSDK.isInitialized()) {
            // Zoom SDK instance has not been initialized.
            android.widget.Toast.makeText(
                    cordova.getActivity().getApplicationContext(),
                    "ZoomSDK has not been initialized successfully",
                    android.widget.Toast.LENGTH_LONG
            ).show();
            callbackContext.error("ZoomSDK has not been initialized successfully");
            return;
        }

        PluginResult pluginResult = null;
        // If user is not logged in.
        if (!this.mZoomSDK.isLoggedIn()) {
            pluginResult = new PluginResult(PluginResult.Status.ERROR, "You are not logged in.");
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return;
        }
        // Bind listener.
        this.mZoomSDK.addAuthenticationListener(this);
        // User is logged in, trying to log user out.
        if (!this.mZoomSDK.logoutZoom()) {
            // logout error.
            pluginResult = new PluginResult(PluginResult.Status.ERROR, false);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return;
        }

        // logout success.
        pluginResult = new PluginResult(PluginResult.Status.OK, true);
        if (DEBUG) {
            Log.v(TAG, "===============Logout Success!==============");
        }

        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }

    /**
     * isLoggedIn
     *
     * Check whether the user is logged in.
     *
     * @param callbackContext   cordova callback context.
     */
    private void isLoggedIn(CallbackContext callbackContext) {
        try {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (DEBUG) {
                        Log.v(TAG, "[#############isLoggedIn Thread run()##############]");
                    }
                    ZoomSDK zoomSDK = ZoomSDK.getInstance();
                    if (!zoomSDK.isInitialized()) {
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "ZoomSDK has not been initialized."));
                        return;
                    }
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, zoomSDK.isLoggedIn()));
                }
            });
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    /**
     * joinMeeting
     *
     * Join a meeting
     *
     * @param meetingNo         meeting number.
     * @param meetingPassword   meeting password
     * @param displayName       display name shown in meeting.
     * @param option            meeting options.
     * @param callbackContext   cordova callback context.
     */
    private void joinMeeting(String meetingNo, String meetingPassword, String displayName, JSONObject option, CallbackContext callbackContext) {

        if (DEBUG) { Log.v(TAG, "********** Zoom's join meeting called ,meetingNo=" + meetingNo + " **********"); }

        if (meetingNo == null || meetingNo.trim().isEmpty() || meetingNo.equals("null")) {
            callbackContext.error("Meeting number cannot be empty");
            return;
        }

        String meetingNumber = meetingNo.trim().replaceAll("[^0-9]", "");

        if (meetingNumber.length() < 9 || meetingNumber.length() > 11 || !meetingNumber.matches("\\d{8,11}")) {
            callbackContext.error("Please enter a valid meeting number.");
            return;
        }



        if (DEBUG) {
            Log.v(TAG, "[Going to Join Meeting]");
            Log.v(TAG, "[meetingNo=]" + meetingNumber);
        }
        PluginResult pluginResult = null;
        // If the meeting number is invalid, throw error.
        if (meetingNumber.length() == 0) {
            pluginResult =  new PluginResult(PluginResult.Status.ERROR, "You need to enter a meeting number which you want to join.");
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return;
        }

        // Get Zoom SDK instance.
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        // If the Zoom SDK instance is not initialized, throw error.
        if(!zoomSDK.isInitialized()) {
            pluginResult =  new PluginResult(PluginResult.Status.ERROR, "ZoomSDK has not been initialized successfully");
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return;
        }

        // Get meeting service instance.
        MeetingService meetingService = zoomSDK.getMeetingService();

        // Configure join meeting parameters.
        JoinMeetingParams params = new JoinMeetingParams();
        params.displayName = displayName;
        params.meetingNo = meetingNumber;

        // Set meeting password.
        if (meetingPassword.length() > 0) {
            params.password = meetingPassword;
        }

        if (option != null) {
            // If meeting option is provided, setup meeting options and join meeting.
            JoinMeetingOptions opts = new JoinMeetingOptions();
            try {
                opts.custom_meeting_id = option.isNull("custom_meeting_id")? null : option.getString("custom_meeting_id");
                opts.participant_id = option.isNull("participant_id")? null : option.getString("participant_id");
                opts.no_unmute_confirm_dialog = option.isNull("no_unmute_confirm_dialog")? false : option.getBoolean("no_unmute_confirm_dialog");
                opts.no_webinar_register_dialog = option.isNull("no_webinar_register_dialog")? false : option.getBoolean("no_webinar_register_dialog");
                opts.no_driving_mode = option.isNull("no_driving_mode")? false : option.getBoolean("no_driving_mode");
                opts.no_invite = option.isNull("no_invite")? false : option.getBoolean("no_invite");
                opts.no_meeting_end_message = option.isNull("no_meeting_end_message")? false : option.getBoolean("no_meeting_end_message");
                opts.no_titlebar = option.isNull("no_titlebar")? false : option.getBoolean("no_titlebar");
                opts.no_bottom_toolbar = option.isNull("no_bottom_toolbar")? false : option.getBoolean("no_bottom_toolbar");
                opts.no_dial_in_via_phone = option.isNull("no_dial_in_via_phone")? false : option.getBoolean("no_dial_in_via_phone");
                opts.no_dial_out_to_phone = option.isNull("no_dial_out_to_phone")? false : option.getBoolean("no_dial_out_to_phone");
                opts.no_disconnect_audio = option.isNull("no_disconnect_audio")? false : option.getBoolean("no_disconnect_audio");
                opts.no_share = option.isNull("no_share")? false : option.getBoolean("no_share");
                opts.no_audio = option.isNull("no_audio")? false : option.getBoolean("no_audio");
                opts.no_video = option.isNull("no_video")? false : option.getBoolean("no_video");
                opts.no_meeting_error_message = option.isNull("no_meeting_error_message")? false : option.getBoolean("no_meeting_error_message");
                opts.meeting_views_options = 0;

                if (!option.isNull("no_button_video") && option.getBoolean("no_button_video")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_VIDEO;
                }

                if (!option.isNull("no_button_audio") && option.getBoolean("no_button_audio")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_AUDIO;
                }

                if (!option.isNull("no_button_share") && option.getBoolean("no_button_share")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_SHARE;
                }

                if (!option.isNull("no_button_participants") && option.getBoolean("no_button_participants")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_PARTICIPANTS;
                }

                if (!option.isNull("no_button_more") && option.getBoolean("no_button_more")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_MORE;
                }

                if (!option.isNull("no_text_meeting_id") && option.getBoolean("no_text_meeting_id")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_TEXT_MEETING_ID;
                }

                if (!option.isNull("no_text_password") && option.getBoolean("no_text_password")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_TEXT_PASSWORD;
                }

                if (!option.isNull("no_button_leave") && option.getBoolean("no_button_leave")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_LEAVE;
                }

                if (!option.isNull("no_button_switch_camera") && option.getBoolean("no_button_switch_camera")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_SWITCH_CAMERA;
                }

                if (!option.isNull("no_button_switch_audio_source") && option.getBoolean("no_button_switch_audio_source")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_SWITCH_AUDIO_SOURCE;
                }

            } catch (JSONException ex) {
                if (DEBUG) { Log.i(TAG, ex.getMessage()); }
            }

            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    int response = meetingService.joinMeetingWithParams(
                            cordova.getActivity().getApplicationContext(),params, opts);
                    if (DEBUG) { Log.i(TAG, "In JoinMeeting, response=" + getMeetingErrorMessage(response)); }
                    PluginResult pluginResult = null;
                    if (response != MeetingError.MEETING_ERROR_SUCCESS) {
                        pluginResult =  new PluginResult(PluginResult.Status.ERROR, getMeetingErrorMessage(response));
                        pluginResult.setKeepCallback(true);
                        callbackContext.sendPluginResult(pluginResult);
                    } else {
                        pluginResult =  new PluginResult(PluginResult.Status.OK, getMeetingErrorMessage(response));
                        pluginResult.setKeepCallback(true);
                        callbackContext.sendPluginResult(pluginResult);
                    }
                }
            });
        } else {
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    // If meeting option is not provided, simply join meeting.
                    int response = meetingService.joinMeetingWithParams(
                            cordova.getActivity().getApplicationContext(), params);
                    if (DEBUG) { Log.i(TAG, "In JoinMeeting, response=" + getMeetingErrorMessage(response)); }
                    PluginResult pluginResult = null;
                    if (response != MeetingError.MEETING_ERROR_SUCCESS) {
                        pluginResult =  new PluginResult(PluginResult.Status.ERROR, getMeetingErrorMessage(response));
                        pluginResult.setKeepCallback(true);
                        callbackContext.sendPluginResult(pluginResult);
                    } else {
                        pluginResult =  new PluginResult(PluginResult.Status.OK, getMeetingErrorMessage(response));
                        pluginResult.setKeepCallback(true);
                        callbackContext.sendPluginResult(pluginResult);
                    }
                }
            });
        }
    }

    /**
     * startMeeting
     *
     * start an existing meeting.
     *
     * @param meetingNo         meeting number
     * @param displayName       display name shown in meeting
     * @param zoomToken         zoom token retrieved from Zoom REST API
     * @param zoomAccessToken   zoom access token retrieved from Zoom REST API
     * @param userId            userId retrieved from Zoom REST API
     * @param option            meeting option
     * @param callbackContext   cordova callback context
     */
    private void startMeeting(String meetingNo, String displayName, String zoomToken, String zoomAccessToken, String userId, JSONObject option, CallbackContext callbackContext) {

        if (meetingNo == null || meetingNo.trim().isEmpty() || meetingNo.equals("null")) {
            callbackContext.error("Meeting number cannot be empty");
            return;
        }

        // pre-process the meeting number to avoid crashing from invalid values.
        String meetingNumber = meetingNo.trim().replaceAll("[^0-9]", "");

        if (meetingNumber.length() < 9 || meetingNumber.length() > 11 || !meetingNumber.matches("\\d{8,11}")) {
            callbackContext.error("Please enter a valid meeting number.");
            return;
        }

        PluginResult pluginResult = null;

        if (DEBUG) {
            Log.v(TAG, "[startMeeting] meetingNumber=====" + meetingNumber);
        }

        // Get Zoom SDK instance.
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        // If Zoom SDK is not initialized, throw error.
        if (!zoomSDK.isInitialized()) {
            pluginResult =  new PluginResult(PluginResult.Status.ERROR, "ZoomSDK has not been initialized successfully");
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return;
        }

        // Get meeting service instance to check meeting status.
        final MeetingService meetingService = zoomSDK.getMeetingService();

        if (meetingService.getMeetingStatus() != MeetingStatus.MEETING_STATUS_IDLE) {
            long lMeetingNo = 0;
            try {
                lMeetingNo = Long.parseLong(meetingNumber);
            } catch (NumberFormatException e) {
                callbackContext.error("Invalid meeting number: " + meetingNumber);
                return;
            }

            if(meetingService.getCurrentRtcMeetingNumber() == lMeetingNo) {
                meetingService.returnToMeeting(cordova.getActivity().getApplicationContext());
                return;
            }

            new AlertDialog.Builder(cordova.getActivity().getApplicationContext())
                    .setMessage("Do you want to leave current meeting and start another?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            meetingService.leaveCurrentMeeting(false);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return;
        }

        // Configure meeting options.
        StartMeetingOptions opts = new StartMeetingOptions();
        if (option != null) {
            try {
                opts.custom_meeting_id = option.isNull("custom_meeting_id")? null : option.getString("custom_meeting_id");
                opts.participant_id = option.isNull("participant_id")? null : option.getString("participant_id");
                opts.no_unmute_confirm_dialog = option.isNull("no_unmute_confirm_dialog")? false : option.getBoolean("no_unmute_confirm_dialog");
                opts.no_webinar_register_dialog = option.isNull("no_webinar_register_dialog")? false : option.getBoolean("no_webinar_register_dialog");
                opts.no_driving_mode = option.isNull("no_driving_mode")? false : option.getBoolean("no_driving_mode");
                opts.no_invite = option.isNull("no_invite")? false : option.getBoolean("no_invite");
                opts.no_meeting_end_message = option.isNull("no_meeting_end_message")? false : option.getBoolean("no_meeting_end_message");
                opts.no_titlebar = option.isNull("no_titlebar")? false : option.getBoolean("no_titlebar");
                opts.no_bottom_toolbar = option.isNull("no_bottom_toolbar")? false : option.getBoolean("no_bottom_toolbar");
                opts.no_dial_in_via_phone = option.isNull("no_dial_in_via_phone")? false : option.getBoolean("no_dial_in_via_phone");
                opts.no_dial_out_to_phone = option.isNull("no_dial_out_to_phone")? false : option.getBoolean("no_dial_out_to_phone");
                opts.no_disconnect_audio = option.isNull("no_disconnect_audio")? false : option.getBoolean("no_disconnect_audio");
                opts.no_share = option.isNull("no_share")? false : option.getBoolean("no_share");
                opts.no_audio = option.isNull("no_audio")? false : option.getBoolean("no_audio");
                opts.no_video = option.isNull("no_video")? false : option.getBoolean("no_video");
                opts.no_meeting_error_message = option.isNull("no_meeting_error_message")? false : option.getBoolean("no_meeting_error_message");
                opts.meeting_views_options = 0;

                if (!option.isNull("no_button_video") && option.getBoolean("no_button_video")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_VIDEO;
                }

                if (!option.isNull("no_button_audio") && option.getBoolean("no_button_audio")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_AUDIO;
                }

                if (!option.isNull("no_button_share") && option.getBoolean("no_button_share")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_SHARE;
                }

                if (!option.isNull("no_button_participants") && option.getBoolean("no_button_participants")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_PARTICIPANTS;
                }

                if (!option.isNull("no_button_more") && option.getBoolean("no_button_more")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_MORE;
                }

                if (!option.isNull("no_text_meeting_id") && option.getBoolean("no_text_meeting_id")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_TEXT_MEETING_ID;
                }

                if (!option.isNull("no_text_password") && option.getBoolean("no_text_password")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_TEXT_PASSWORD;
                }

                if (!option.isNull("no_button_leave") && option.getBoolean("no_button_leave")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_LEAVE;
                }

                if (!option.isNull("no_button_switch_camera") && option.getBoolean("no_button_switch_camera")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_SWITCH_CAMERA;
                }

                if (!option.isNull("no_button_switch_audio_source") && option.getBoolean("no_button_switch_audio_source")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_SWITCH_AUDIO_SOURCE;
                }
            } catch(JSONException ex) {
                if (DEBUG) { Log.i(TAG, ex.getMessage()); }
            }
        }

        if (zoomSDK.isLoggedIn()) {
            // if user is logged in, just start the meeting.
            StartMeetingParams4NormalUser params = new StartMeetingParams4NormalUser();
            params.meetingNo = meetingNumber;
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    int response = meetingService.startMeetingWithParams(cordova.getActivity().getApplicationContext(), params, opts);
                    PluginResult pluginResult = null;
                    if (response != MeetingError.MEETING_ERROR_SUCCESS) {
                        pluginResult =  new PluginResult(PluginResult.Status.ERROR, getMeetingErrorMessage(response));
                        pluginResult.setKeepCallback(true);
                        callbackContext.sendPluginResult(pluginResult);
                    } else {
                        pluginResult =  new PluginResult(PluginResult.Status.OK, getMeetingErrorMessage(response));
                        pluginResult.setKeepCallback(true);
                        callbackContext.sendPluginResult(pluginResult);
                    }
                }
            });
        } else {
            // if user is not logged in, start the meeting with provided tokens.
            if (DEBUG) {
                Log.v(TAG, "[+++++++++++++++Going to start meeting with ZAK++++++++++++++++]");
                Log.v(TAG, "[userId==="+userId);
            }
            if (zoomToken.length() != 0 && zoomAccessToken.length() != 0 && userId.length() != 0) {
                StartMeetingParamsWithoutLogin params = new StartMeetingParamsWithoutLogin();
                params.userId = userId;
                params.zoomToken = zoomToken;
                params.userType = MeetingService.USER_TYPE_API_USER;
                params.displayName = displayName;
                params.zoomAccessToken = zoomAccessToken;
                params.meetingNo = meetingNumber;

                cordova.getThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        int response = meetingService.startMeetingWithParams(cordova.getActivity().getApplicationContext(), params, opts);
                        PluginResult pluginResult = null;
                        if (response != MeetingError.MEETING_ERROR_SUCCESS) {
                            pluginResult =  new PluginResult(PluginResult.Status.ERROR, getMeetingErrorMessage(response));
                            pluginResult.setKeepCallback(true);
                            callbackContext.sendPluginResult(pluginResult);
                        } else {
                            pluginResult =  new PluginResult(PluginResult.Status.OK, getMeetingErrorMessage(response));
                            pluginResult.setKeepCallback(true);
                            callbackContext.sendPluginResult(pluginResult);
                        }
                    }
                });
            } else {
                pluginResult =  new PluginResult(PluginResult.Status.ERROR, "Your zoom token, zoom access token, or userId are not valid");
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            }
        }
    }

    /**
     *
     * startInstantMeeting
     *
     * start an instant meeting.
     *
     * @param option            meeting options
     * @param callbackContext   cordova callback context
     */
    private void startInstantMeeting(JSONObject option, CallbackContext callbackContext) {
        PluginResult pluginResult = null;
        // Get Zoom SDK instance.
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        // If Zoom SDK is not initialized, throw error.
        if (!zoomSDK.isInitialized()) {
            pluginResult =  new PluginResult(PluginResult.Status.ERROR, "ZoomSDK has not been initialized successfully");
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return;
        }
        // If user is not logged in, throw error.
        if (!zoomSDK.isLoggedIn()) {
            pluginResult =  new PluginResult(PluginResult.Status.ERROR, "You are not logged in");
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return;
        }

        MeetingService meetingService = zoomSDK.getMeetingService();
        InstantMeetingOptions opts = new InstantMeetingOptions();
        // If user provides meeting options, configure them.
        if (option != null) {
            try {
                opts.custom_meeting_id = option.isNull("custom_meeting_id")? null : option.getString("custom_meeting_id");
                opts.participant_id = option.isNull("participant_id")? null : option.getString("participant_id");
                opts.no_unmute_confirm_dialog = option.isNull("no_unmute_confirm_dialog")? false : option.getBoolean("no_unmute_confirm_dialog");
                opts.no_webinar_register_dialog = option.isNull("no_webinar_register_dialog")? false : option.getBoolean("no_webinar_register_dialog");
                opts.no_driving_mode = option.isNull("no_driving_mode")? false : option.getBoolean("no_driving_mode");
                opts.no_invite = option.isNull("no_invite")? false : option.getBoolean("no_invite");
                opts.no_meeting_end_message = option.isNull("no_meeting_end_message")? false : option.getBoolean("no_meeting_end_message");
                opts.no_titlebar = option.isNull("no_titlebar")? false : option.getBoolean("no_titlebar");
                opts.no_bottom_toolbar = option.isNull("no_bottom_toolbar")? false : option.getBoolean("no_bottom_toolbar");
                opts.no_dial_in_via_phone = option.isNull("no_dial_in_via_phone")? false : option.getBoolean("no_dial_in_via_phone");
                opts.no_dial_out_to_phone = option.isNull("no_dial_out_to_phone")? false : option.getBoolean("no_dial_out_to_phone");
                opts.no_disconnect_audio = option.isNull("no_disconnect_audio")? false : option.getBoolean("no_disconnect_audio");
                opts.no_share = option.isNull("no_share")? false : option.getBoolean("no_share");
                opts.no_video = option.isNull("no_video")? false : option.getBoolean("no_video");
                opts.no_meeting_error_message = option.isNull("no_meeting_error_message")? false : option.getBoolean("no_meeting_error_message");
                opts.meeting_views_options = 0;

                if (!option.isNull("no_button_video") && option.getBoolean("no_button_video")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_VIDEO;
                }

                if (!option.isNull("no_button_audio") && option.getBoolean("no_button_audio")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_AUDIO;
                }

                if (!option.isNull("no_button_share") && option.getBoolean("no_button_share")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_SHARE;
                }

                if (!option.isNull("no_button_participants") && option.getBoolean("no_button_participants")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_PARTICIPANTS;
                }

                if (!option.isNull("no_button_more") && option.getBoolean("no_button_more")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_MORE;
                }

                if (!option.isNull("no_text_meeting_id") && option.getBoolean("no_text_meeting_id")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_TEXT_MEETING_ID;
                }

                if (!option.isNull("no_text_password") && option.getBoolean("no_text_password")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_TEXT_PASSWORD;
                }

                if (!option.isNull("no_button_leave") && option.getBoolean("no_button_leave")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_LEAVE;
                }

                if (!option.isNull("no_button_switch_camera") && option.getBoolean("no_button_switch_camera")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_SWITCH_CAMERA;
                }

                if (!option.isNull("no_button_switch_audio_source") && option.getBoolean("no_button_switch_audio_source")) {
                    opts.meeting_views_options += MeetingViewsOptions.NO_BUTTON_SWITCH_AUDIO_SOURCE;
                }
            } catch (JSONException ex) {
                Log.i(TAG, ex.getMessage());
            }
        }

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                int response = meetingService.startInstantMeeting(cordova.getActivity().getApplicationContext(), opts);
                if (DEBUG) {
                    Log.i(TAG, "onClickBtnLoginUserStartInstant, response=" + getMeetingErrorMessage(response));
                }
                PluginResult pluginResult = null;
                if (response != MeetingError.MEETING_ERROR_SUCCESS) {
                    pluginResult =  new PluginResult(PluginResult.Status.ERROR, getMeetingErrorMessage(response));
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                } else {
                    pluginResult =  new PluginResult(PluginResult.Status.OK, getMeetingErrorMessage(response));
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                }
            }
        });
    }

    /**
     * setLocale
     *
     * Change the in-meeting language.
     *
     * @param languageTag       IETF BCP 47 language tag string
     * @param callbackContext   cordova callback context
     */
    private void setLocale(String languageTag, CallbackContext callbackContext) {
        try {
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (DEBUG) {
                        Log.v(TAG, "[#############setLocale Thread run()##############]");
                    }
                    ZoomSDK zoomSDK = ZoomSDK.getInstance();
                    try {
                        Locale language = new Builder().setLanguageTag(languageTag.replaceAll("_","-")).build();
                        zoomSDK.setSdkLocale(cordova.getActivity().getApplicationContext(), language);
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "Successfully set language to " + languageTag));
                    } catch (IllformedLocaleException ie) {
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Please pass valid language and country codes. [ERROR:" + ie.getMessage() + "]"));
                    }
                }
            });
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }


    /**
     * onZoomSDKLoginResult
     *
     * A listener to get Zoom SDK login result.
     *
     * @param result result code as a number.
     */
    @Override
    public void onZoomSDKLoginResult(long result) {
            if (DEBUG) { Log.v(TAG, "*********onZoomSDKLoginResult********* result =====" + result); }

            try {
                JSONObject res = new JSONObject();
                PluginResult pluginResult = null;

                if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                    // login success
                    res.put("result", true);
                    res.put("message", "Logged in successfully");
                    pluginResult = new PluginResult(PluginResult.Status.OK, res);
                } else {
                    // login error
                    res.put("result", false);
                    res.put("message", "Login attempt failed! Reason: " + getAuthErrorMessage(result));
                    pluginResult = new PluginResult(PluginResult.Status.ERROR, res);
                }
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            } catch(JSONException e) {
                callbackContext.error(e.getMessage());
            }
    }

    /**
     * onZoomSDKLogoutResult
     *
     * A listener to get Zoom SDK logout result.
     *
     * @param result result code as a number.
     */
    @Override
    public void onZoomSDKLogoutResult(long result) {
            try {
                JSONObject res = new JSONObject();
                PluginResult pluginResult = null;

                if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                    // logout success
                    res.put("result", true);
                    res.put("message", "Logged out successfully");
                    pluginResult = new PluginResult(PluginResult.Status.OK, res);
                } else {
                    // logout error
                    res.put("result", false);
                    res.put("message", "Logout attempt failed! Reason: " + getAuthErrorMessage(result));
                    pluginResult = new PluginResult(PluginResult.Status.ERROR, res);
                }
                pluginResult.setKeepCallback(true);
                callbackContext.sendPluginResult(pluginResult);
            } catch (JSONException e) {
                callbackContext.error(e.getMessage());
            }
    }

    /**
     * getAuthErrorMessage
     *
     * Get the error message of auth process.
     *
     * @param errorCode error code.
     * @return A string message.
     */
    private String getAuthErrorMessage(long errorCode) {

        StringBuilder message = new StringBuilder();

        if (errorCode == ZoomAuthenticationError.ZOOM_AUTH_ERROR_USER_NOT_EXIST) {
            message.append("Failed due to user does not exist.");
        } else if (errorCode == ZoomAuthenticationError.ZOOM_AUTH_ERROR_WRONG_PASSWORD) {
            message.append("Failed due to wrong password.");
        } else if (errorCode == 300L) {
            // Validation error.
            message.append("Failed due to your input is not valid.");
        } else {
            message.append("Failed due to unknown reason.");
        }

        if (DEBUG) {
            Log.v(TAG, "******getAuthErrorMessage*********" + message.toString());
        }
        return message.toString();
    }

    /**
     * getApiErrorMessage
     *
     * Get the error message of api process.
     *
     * @param errorCode error code.
     * @return A string message.
     */
    private String getApiErrorMessage(int errorCode) {

        StringBuilder message = new StringBuilder();

        switch(errorCode) {
            case ZoomApiError.ZOOM_API_ERROR_FAILED_CLIENT_INCOMPATIBLE:
                message.append("Your Zoom SDK client is not compatible. " +
                        "Please download the latest version and try again.");
                break;
            case ZoomApiError.ZOOM_API_ERROR_FAILED_NULLPOINTER:
                message.append("Zoom SDK was not initialized successfully. ");
                break;
            case ZoomApiError.ZOOM_API_ERROR_FAILED_WRONGPARAMETERS:
                message.append("The combination of your username and password does not match our record.");
                break;
            case ZoomApiError.ZOOM_API_INVALID_STATUS:
                message.append("Our API is taking a break. Please try again later.");
                break;
            default:
                message.append("You are already logged in.");
                break;
        }

        if (DEBUG) {
            Log.v(TAG, "******getAuthErrorMessage*********" + message.toString());
        }

        return message.toString();
    }

    /**
     * onZoomAuthIdentityExpired
     * 
     * A listener to get notified when the authentication identity has expired.
     */
    @Override
    public void onZoomAuthIdentityExpired() {
        Log.v(TAG, "onZoomAuthIdentityExpired is triggered");
    }

    /**
     * onZoomIdentityExpired
     *
     * A listener to log user out once identity is expired.
     */
    @Override
    public void onZoomIdentityExpired() {
        if (mZoomSDK.isLoggedIn()) {
            mZoomSDK.logoutZoom();
        }
    }

    /**
     * onMeetingStatusChanged
     *
     * A listener to retrieve info when meeting status changed.
     *
     * @param meetingStatus     meeting status code.
     * @param errorCode         error code.
     * @param internalErrorCode internal error code.
     */
    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int errorCode,
                                       int internalErrorCode) {
        if (DEBUG) { Log.i(TAG, "onMeetingStatusChanged, meetingStatus=" + meetingStatus + ", errorCode=" + errorCode
                + ", internalErrorCode=" + internalErrorCode); }

        if(meetingStatus == MeetingStatus.MEETING_STATUS_FAILED && errorCode == MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE) {
            final android.widget.Toast toast = android.widget.Toast.makeText(
                    cordova.getActivity().getApplicationContext(),
                    "Version of ZoomSDK is too low!",
                    android.widget.Toast.LENGTH_LONG
            );
            toast.show();
        }
    }

    /**
     * getMeetingErrorMessage
     *
     * Get meeting error message.
     *
     * @param errorCode error code.
     * @return A string message.
     */
    private String getMeetingErrorMessage(int errorCode) {

        StringBuilder message = new StringBuilder();

        switch(errorCode) {
            case MeetingError.MEETING_ERROR_CLIENT_INCOMPATIBLE:
                message.append("Zoom SDK version is too low to connect to the meeting");
                break;
            case MeetingError.MEETING_ERROR_DISALLOW_HOST_RESGISTER_WEBINAR:
                message.append("Cannot register a webinar using the host email");
                break;
            case MeetingError.MEETING_ERROR_DISALLOW_PANELIST_REGISTER_WEBINAR:
                message.append("Cannot register a webinar using a panelist's email");
                break;
            case MeetingError.MEETING_ERROR_EXIT_WHEN_WAITING_HOST_START:
                message.append("User leave meeting when waiting host to start");
                break;
            case MeetingError.MEETING_ERROR_HOST_DENY_EMAIL_REGISTER_WEBINAR:
                message.append("The register to this webinar is denied by the host");
                break;
            case MeetingError.MEETING_ERROR_INCORRECT_MEETING_NUMBER:
                message.append("Incorrect meeting number");
                break;
            case MeetingError.MEETING_ERROR_INVALID_ARGUMENTS:
                message.append("Failed due to one or more invalid arguments.");
                break;
            case MeetingError.MEETING_ERROR_INVALID_STATUS:
                message.append("Meeting api can not be called now.");
                break;
            case MeetingError.MEETING_ERROR_LOCKED:
                message.append("Meeting is locked");
                break;
            case MeetingError.MEETING_ERROR_MEETING_NOT_EXIST:
                message.append("Meeting dose not exist");
                break;
            case MeetingError.MEETING_ERROR_MEETING_OVER:
                message.append("Meeting ended");
                break;
            case MeetingError.MEETING_ERROR_MMR_ERROR:
                message.append("Server error");
                break;
            case MeetingError.MEETING_ERROR_NETWORK_ERROR:
                message.append("Network error");
                break;
            case MeetingError.MEETING_ERROR_NETWORK_UNAVAILABLE:
                message.append("Network unavailable");
                break;
            case MeetingError.MEETING_ERROR_NO_MMR:
                message.append("No server is available for this meeting");
                break;
            case MeetingError.MEETING_ERROR_REGISTER_WEBINAR_FULL:
                message.append("Arrive maximum registers to this webinar");
                break;
            case MeetingError.MEETING_ERROR_RESTRICTED:
                message.append("Meeting is restricted");
                break;
            case MeetingError.MEETING_ERROR_RESTRICTED_JBH:
                message.append("Join this meeting before host is restricted");
                break;
            case MeetingError.MEETING_ERROR_SESSION_ERROR:
                message.append("Session error");
                break;
            case MeetingError.MEETING_ERROR_SUCCESS:
                message.append("Success");
                break;
            case MeetingError.MEETING_ERROR_TIMEOUT:
                message.append("Timeout");
                break;
            case MeetingError.MEETING_ERROR_UNKNOWN:
                message.append("Unknown error");
                break;
            case MeetingError.MEETING_ERROR_USER_FULL:
                message.append("Number of participants is full.");
                break;
            case MeetingError.MEETING_ERROR_WEB_SERVICE_FAILED:
                message.append("Request to web service failed.");
                break;
            case MeetingError.MEETING_ERROR_WEBINAR_ENFORCE_LOGIN:
                message.append("This webinar requires participants to login.");
                break;
            default:
                break;
        }

        if (DEBUG) {
            Log.v(TAG, "******getMeetingErrorMessage*********" + message.toString());
        }
        return message.toString();
    }
}
