package com.amazonaws.sns.samples.tools;

/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformApplicationResult;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.DeletePlatformApplicationRequest;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.sns.samples.tools.SampleMessageGenerator.Platform;
import com.amazonaws.services.sns.model.ListPlatformApplicationsResult;
import com.amazonaws.services.sns.model.PlatformApplication;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationRequest;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationResult;
import com.amazonaws.services.sns.model.Endpoint;

public class AmazonSNSClientWrapper {

	private final AmazonSNS snsClient;

	public AmazonSNSClientWrapper(AmazonSNS client) {
		this.snsClient = client;
	}

	private CreatePlatformApplicationResult createPlatformApplication(
			String applicationName, Platform platform, String principal,
			String credential) {
		CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest();
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("PlatformPrincipal", principal);
		attributes.put("PlatformCredential", credential);
		platformApplicationRequest.setAttributes(attributes);
		platformApplicationRequest.setName(applicationName);
		platformApplicationRequest.setPlatform(platform.name());
		return snsClient.createPlatformApplication(platformApplicationRequest);
	}

	private CreatePlatformEndpointResult createPlatformEndpoint(
			Platform platform, String customData, String platformToken,
			String applicationArn) {
		CreatePlatformEndpointRequest platformEndpointRequest = new CreatePlatformEndpointRequest();
		platformEndpointRequest.setCustomUserData(customData);
		String token = platformToken;
		String userId = null;
		if (platform == Platform.BAIDU) {
			String[] tokenBits = platformToken.split("\\|");
			token = tokenBits[0];
			userId = tokenBits[1];
			Map<String, String> endpointAttributes = new HashMap<String, String>();
			endpointAttributes.put("UserId", userId);
			endpointAttributes.put("ChannelId", token);
			platformEndpointRequest.setAttributes(endpointAttributes);
		}
		platformEndpointRequest.setToken(token);
		platformEndpointRequest.setPlatformApplicationArn(applicationArn);
		return snsClient.createPlatformEndpoint(platformEndpointRequest);
	}

	private void deletePlatformApplication(String applicationArn) {
		DeletePlatformApplicationRequest request = new DeletePlatformApplicationRequest();
		request.setPlatformApplicationArn(applicationArn);
		snsClient.deletePlatformApplication(request);
	}

	private PublishResult publish(String endpointArn, Platform platform,
			Map<Platform, Map<String, MessageAttributeValue>> attributesMap, String notifMessage) {
		PublishRequest publishRequest = new PublishRequest();
		Map<String, MessageAttributeValue> notificationAttributes = getValidNotificationAttributes(attributesMap
				.get(platform));
		if (notificationAttributes != null && !notificationAttributes.isEmpty()) {
			publishRequest.setMessageAttributes(notificationAttributes);
		}
		publishRequest.setMessageStructure("json");
		// If the message attributes are not set in the requisite method,
		// notification is sent with default attributes
		String message = getPlatformSampleMessage(platform);
		//Map<String, String> messageMap = new HashMap<String, String>();
		//messageMap.put(platform.name(), message);
		message =
				"{" +
						"\"GCM\": " +
						"\"{ \\\"notification\\\": " +
						"{ " +
						"\\\"body\\\": \\\"" + notifMessage + "\\\"" +
						"}, " +
						//"\\\"click_action\\\":\\\"OPEN_ACTIVITY\\\"},"+
						"\\\"data\\\": {}}\"}";//SampleMessageGenerator.jsonify(messageMap);
		// TODO: Add click_action payload to message
		// For direct publish to mobile end points, topicArn is not relevant.
		publishRequest.setTargetArn(endpointArn);

		// Display the message that will be sent to the endpoint/
		System.out.println("{Message Body: " + message + "}");
		StringBuilder builder = new StringBuilder();
		builder.append("{Message Attributes: ");
		for (Map.Entry<String, MessageAttributeValue> entry : notificationAttributes
				.entrySet()) {
			builder.append("(\"" + entry.getKey() + "\": \""
					+ entry.getValue().getStringValue() + "\"),");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append("}");
		System.out.println(builder.toString());

		publishRequest.setMessage(message);
		return snsClient.publish(publishRequest);
	}

	public void demoNotification(Platform platform, String principal,
			String credential, String platformToken, String applicationName,
			Map<Platform, Map<String, MessageAttributeValue>> attrsMap, String uid,
								 String notifMessage) {
		// Create Platform Application. This corresponds to an app on a
		// platform.
		CreatePlatformApplicationResult platformApplicationResult = null;
		// The Platform Application Arn can be used to uniquely identify the
		// Platform Application.
		String platformApplicationArn = platFormApplicationExists(applicationName);
		if (platformApplicationArn == null) {
			platformApplicationResult = createPlatformApplication(
					applicationName, platform, principal, credential);
			platformApplicationArn = platformApplicationResult.getPlatformApplicationArn();
			System.out.println(platformApplicationResult);
		}

		// Create an Endpoint. This corresponds to an app on a device.
		String endpointArn = endpointExists(platformApplicationArn, uid);
		CreatePlatformEndpointResult platformEndpointResult = null;
		if (endpointArn == null) {
			platformEndpointResult = createPlatformEndpoint(
					platform,
					uid,
					platformToken, platformApplicationArn);
			System.out.println(platformEndpointResult);
			endpointArn = platformEndpointResult.getEndpointArn();
		}

		// Publish a push notification to an Endpoint.
		PublishResult publishResult = publish(endpointArn, platform, attrsMap, notifMessage);
		System.out.println("Published! \n{MessageId="
				+ publishResult.getMessageId() + "}");

		// Delete the Platform Application since we will no longer be using it.
		//deletePlatformApplication(platformApplicationArn);
	}

	/**
	 *
	 * @param applicationName
	 * @return
     */
	private String platFormApplicationExists(String applicationName) {
		ListPlatformApplicationsResult apps = snsClient.listPlatformApplications();
		List<PlatformApplication> appsList = apps.getPlatformApplications();
		for (PlatformApplication p : appsList) {
			if (p.getPlatformApplicationArn().contains(applicationName)) {
				return p.getPlatformApplicationArn();
			}
		}
		return null;
	}

	/**
	 *
	 * @param platformApplicationArn
	 * @param targetUid
     * @return
     */
	private String endpointExists(String platformApplicationArn, String targetUid) {
		ListEndpointsByPlatformApplicationRequest endpointsReq = new ListEndpointsByPlatformApplicationRequest();
		endpointsReq.setPlatformApplicationArn(platformApplicationArn);
		ListEndpointsByPlatformApplicationResult endpoints =
				snsClient.listEndpointsByPlatformApplication(endpointsReq);
		List<Endpoint> endpointsList = endpoints.getEndpoints();
		for (Endpoint e : endpointsList) {
			Map<String, String> attributes = e.getAttributes();
			if (attributes.get("CustomUserData").equals(targetUid)) {
				return e.getEndpointArn();
			}
		}
		return null;
	}

	private String getPlatformSampleMessage(Platform platform) {
		switch (platform) {
		case APNS:
			return SampleMessageGenerator.getSampleAppleMessage();
		case APNS_SANDBOX:
			return SampleMessageGenerator.getSampleAppleMessage();
		case GCM:
			return SampleMessageGenerator.getSampleAndroidMessage();
		case ADM:
			return SampleMessageGenerator.getSampleKindleMessage();
		case BAIDU:
			return SampleMessageGenerator.getSampleBaiduMessage();
		case WNS:
			return SampleMessageGenerator.getSampleWNSMessage();
		case MPNS:
			return SampleMessageGenerator.getSampleMPNSMessage();
		default:
			throw new IllegalArgumentException("Platform not supported : "
					+ platform.name());
		}
	}

	public static Map<String, MessageAttributeValue> getValidNotificationAttributes(
			Map<String, MessageAttributeValue> notificationAttributes) {
		Map<String, MessageAttributeValue> validAttributes = new HashMap<String, MessageAttributeValue>();

		if (notificationAttributes == null) return validAttributes;

		for (Map.Entry<String, MessageAttributeValue> entry : notificationAttributes
				.entrySet()) {
			if (!StringUtils.isBlank(entry.getValue().getStringValue())) {
				validAttributes.put(entry.getKey(), entry.getValue());
			}
		}
		return validAttributes;
	}
}
