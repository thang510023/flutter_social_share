import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:social_share/social_share.dart';

abstract class FlutterSocialShare extends SocialShare {
  static const MethodChannel _channel =
      const MethodChannel('flutter_social_share');

  static Future<String?> shareImageToTwitter(File image,
      {String msg = ''}) async {
    final Map<String, dynamic> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('msg', () => msg);
    arguments.putIfAbsent('image', () => image.path);
    try {
      return await _channel.invokeMethod('shareTwitter', arguments);
    } catch (e) {
      return "false";
    }
  }

  static Future<String?> shareImageToSMS(File image, {String msg = ''}) async {
    final Map<String, dynamic> arguments = Map<String, dynamic>();
    arguments.putIfAbsent('msg', () => msg);
    arguments.putIfAbsent('image', () => image.path);
    try {
      return await _channel.invokeMethod('shareSMS', arguments);
    } catch (e) {
      return "false";
    }
  }

  static Future<String?> shareInstagramStory(
    String imagePath, {
    String? backgroundTopColor,
    String? backgroundBottomColor,
    String? attributionURL,
    String? backgroundImagePath,
  }) =>
      SocialShare.shareInstagramStory(imagePath,
          backgroundBottomColor: backgroundBottomColor,
          backgroundTopColor: backgroundTopColor,
          backgroundImagePath: backgroundImagePath,
          attributionURL: attributionURL);

  static Future<String?> shareFacebookStory(
          String imagePath,
          String backgroundTopColor,
          String backgroundBottomColor,
          String attributionURL,
          {String? appId}) =>
      SocialShare.shareFacebookStory(
          imagePath, backgroundTopColor, backgroundBottomColor, attributionURL);

  static Future<String?> shareTwitter(String captionText,
          {List<String>? hashtags, String? url, String? trailingText}) =>
      SocialShare.shareTwitter(captionText,
          hashtags: hashtags, url: url, trailingText: trailingText);

  static Future<String?> shareSms(String message,
          {String? url, String? trailingText}) =>
      SocialShare.shareSms(message, url: url, trailingText: trailingText);

  static Future<bool?> copyToClipboard(content) =>
      SocialShare.copyToClipboard(content);

  static Future<bool?> shareOptions(String contentText, {String? imagePath}) =>
      SocialShare.shareOptions(contentText, imagePath: imagePath);

  static Future<String?> shareWhatsapp(String content) =>
      SocialShare.shareWhatsapp(content);

  static Future<Map?> checkInstalledAppsForShare() =>
      SocialShare.checkInstalledAppsForShare();

  static Future<String?> shareTelegram(String content) =>
      SocialShare.shareTelegram(content);
}
