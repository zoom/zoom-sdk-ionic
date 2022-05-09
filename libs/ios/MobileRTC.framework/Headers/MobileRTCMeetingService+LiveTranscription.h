//
//  MobileRTCMeetingService+LiveTranscription.h
//  MobileRTC
//
//  Created by Zoom Video Communications on 2021/10/27.
//  Copyright © 2021 Zoom Video Communications, Inc. All rights reserved.
//

#import <MobileRTC/MobileRTC.h>
#import "MobileRTCConstants.h"

@interface MobileRTCMeetingService (LiveTranscription)

/*!
 @brief Query if the current meeting is supported closed caption.
 @return YES means that the current meeting is supported close caption.
 */
- (BOOL)isMeetingSupportCC;

/*!
 @brief Query if the user is can be assigned to send closed caption.
 @param userid the user id.
 @return TRUE means that the user can be assigned to send closed caption.
 */
- (BOOL)canBeAssignedToSendCC:(NSUInteger)userId;

/*!
 @brief Assign the user privilege to send closed caption.
 @param userid the user id.
 @return If the function succeeds, the return value is YES. Otherwise failed.
 */
- (BOOL)assignCCPrivilege:(NSUInteger)userId;

/*!
 @brief WithDraw the user privilege to send closed caption.
 @param userid the user id.
 @return If the function succeeds, the return value is YES. Otherwise failed.
 */
- (BOOL)withdrawCCPrivilege:(NSUInteger)userId;

/*!
 @brief Query if the current user can assign the privilege of send closed caption to others.
 @return YES means that the user can assign others privilege to send closed caption.
 */
- (BOOL)canAssignOthersToSendCC;

/*!
 @brief Query if this meeting support the live transcription feature.
 @return YES means that the live transcription feature is supported.
 */
- (BOOL)isLiveTranscriptionFeatureEnabled;

/*!
 @brief Query the current live transcription status.
 @return @return the live transcription status.  For more details, see MobileRTCLiveTranscriptionStatus.
 */
- (MobileRTCLiveTranscriptionStatus)getLiveTranscriptionStatus;

/*!
 @brief Query if meeting participants can start live transcription.
 @return YES means that the participant can start live transcription.
 */
- (BOOL)canStartLiveTranscription;

/*!
 @brief Start live transcription.
 @return If the function succeeds, the return value is YES. Otherwise failed.
 */
- (BOOL)startLiveTranscription;

/*!
 @brief Stop live transcription.
 @return If the function succeeds, the return value is YES. Otherwise failed.
 */
- (BOOL)stopLiveTranscription;

/*!
 @brief The host enable/disable the request live transcription.
 @param enable YES means that enable the request live transcription; FALSE means that disable the request live transcription.
 @return If the function succeeds, the return value is YES. Otherwise failed.
 */
- (BOOL)enableRequestLiveTranscription:(BOOL)enable;

/*!
 @brief Query if it is enabled to request to start live transcription.
 @return YES means that it is enabled to request to start live transcription.
 */
- (BOOL)isRequestToStartLiveTranscriptionEnabled;

/*!
 @brief Request the host to start live transcription.
 @param requestAnonymous TRUE means that it is anonymous to request the host to start live transcription.
 @return If the function succeeds, the return value is YES. Otherwise failed.
 */
- (BOOL)requestToStartLiveTranscription:(BOOL)requestAnonymous;
@end

