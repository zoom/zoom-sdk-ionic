/**
 *  Zoom.h
 *
 *  @author Carson Chen (carson.chen@zoom.us)
 *  @version v4.4.55130.0712
 */

#import <Cordova/CDV.h>
#import <MobileRTC/MobileRTC.h>
/**
 *  Zoom
 *
 *  A Cordova Plugin to use Zoom Video Conferencing services on Cordova applications.
 */
@interface Zoom :  CDVPlugin<MobileRTCAuthDelegate, MobileRTCMeetingServiceDelegate> {
    NSString *callbackId;
    CDVPluginResult* pluginResult;
}
/**
 * initialize
 *
 * Initialize Zoom SDK.
 * @param CDVInvokedUrlCommand bridging method to get arguments, callbackId, className and methodName.
 */
- (void)initialize:(CDVInvokedUrlCommand*)command;
/**
 * login
 *
 * Log user in with Zoom username and password.
 * @param CDVInvokedUrlCommand bridging method to get arguments, callbackId, className and methodName.
 */
- (void)login:(CDVInvokedUrlCommand*)command;
/**
 * logout
 *
 * Log user out.
 * @param CDVInvokedUrlCommand bridging method to get arguments, callbackId, className and methodName.
 */
- (void)logout:(CDVInvokedUrlCommand*)command;
/**
 * isLoggedIn
 *
 * Check whether the user is logged in.
 * @param CDVInvokedUrlCommand bridging method to get arguments, callbackId, className and methodName.
 */
- (void)isLoggedIn:(CDVInvokedUrlCommand*)command;
/**
 * joinMeeting
 *
 * Join a meeting.
 * @param CDVInvokedUrlCommand bridging method to get arguments, callbackId, className and methodName.
 */
- (void)joinMeeting:(CDVInvokedUrlCommand*)command;
/**
 * startMeeting
 *
 * Start an existing meeting.
 * @param CDVInvokedUrlCommand bridging method to get arguments, callbackId, className and methodName.
 */
- (void)startMeeting:(CDVInvokedUrlCommand*)command;
/**
 * startInstantMeeting
 *
 * Start an instant meeting.
 * @param CDVInvokedUrlCommand bridging method to get arguments, callbackId, className and methodName.
 */
- (void)startInstantMeeting:(CDVInvokedUrlCommand*)command;
/**
 * setLocale
 *
 * Set in-meeting language.
 * @param CDVInvokedUrlCommand bridging method to get arguments, callbackId, className and methodName.
 */
- (void)setLocale:(CDVInvokedUrlCommand*)command;
@end
