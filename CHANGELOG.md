# CHANGELOG

## 2020-06-03 @ v4.6.21666.0603

## Changed & Fixed
* Fixed an issue that the SDK crashes when joining a meeting on iOS.

## 2020-05-12 @ v4.6.21666.0512

## Added:
* Upgraded native SDK libraries to Android SDK v4.6.21666.0429 and iOS SDK v4.6.21666.0428, which includes the following major changes:
  * Added support for AES 256-bit GCM encryption
    *  **Please plan to upgrade your SDK accordingly. See the announcement in README for more information**
  * Added support for Android 10
  * Deprecated UIWebView
  * Upgraded OpenSSL to version 1.1.1e
  * For all other changes, please refer to the changelog of Android SDK and iOS SDK.
* Added new meeting options
 * `custom_meeting_id`
 * `participant_id`
 * `no_unmute_confirm_dialog` [Android only]
 * `no_webinar_register_dialog` [Android only]
 * `no_bottom_toolbar`
 * `no_button_video`
 * `no_button_audio`
 * `no_button_share`
 * `no_button_participants`
 * `no_button_more`
 * `no_text_meeting_id`
 * `no_text_password`
 * `no_button_leave`

## Changed & Fixed
* Fixed an issue that the SDK crashes after initialized multiple times
* Fixed an issue that user is not able to host a meeting with tokens

## 2019-07-15 @ v4.4.55130.0712

**Added**

* Added Simulators Support on Android & iOS
* Added Ionic 4 Support
* Added Android 64-bit Support
* Added AndroidX Support

**Changed & Fixed**
* Fixed an issue that sometimes the user is not able to start a meeting with ZAK

## 2019-03-25 @ v4.3.1.94401.0325

**Added**

* Add log feature. Now log are available on both platforms.
 1. To retrieve log on Android: [https://marketplace.zoom.us/docs/sdk/native-sdks/android/mastering-zoom-sdk/sdk-initialization#2-log-feature](https://marketplace.zoom.us/docs/sdk/native-sdks/android/mastering-zoom-sdk/sdk-initialization#2-log-feature);
 2. To retrieve log on iOS: [https://marketplace.zoom.us/docs/sdk/native-sdks/iOS/mastering-zoom-sdk/sdk-initialization#2-log-feature](https://marketplace.zoom.us/docs/sdk/native-sdks/iOS/mastering-zoom-sdk/sdk-initialization#2-log-feature)
* Start following official version number


## 2019-02-04 @ v1.0.0

**Added**

* Major features such as:
	* Login/Logout
	* Check login status
	* Join Meeting
	* Start Meeting
	* Start Instant Meeting
	* Set Languages
