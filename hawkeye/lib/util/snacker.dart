import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:hawkeye/core/constant.dart';
import 'package:hawkeye/util/dialogs.dart';
import 'package:hawkeye/util/m.dart';

class Snacker {
  static int nextAvailable = M.ms();

  static void queue(int msdur, VoidCallback c) {
    if (nextAvailable >= M.ms()) {
      Future.delayed(Duration(milliseconds: (nextAvailable - M.ms()) + 250), c);
    } else {
      c();
    }
    nextAvailable = M.ms() + msdur;
  }

  static void snack(String msg) async {
    try {
      snack2(null, msg);
    } catch (e) {}
  }

  static void snackConnectionError(String title, String msg) async {
    queue(8000, () {
      try {
        Get.snackbar(title, msg,
            forwardAnimationCurve: Curves.easeOutQuint,
            reverseAnimationCurve: Curves.easeOutQuint,
            animationDuration: Duration(milliseconds: 760),
            duration: Duration(seconds: 7),
            shouldIconPulse: true,
            icon: Icon(
              Icons.signal_cellular_connected_no_internet_4_bar,
              color: Colors.red,
              size: 28,
            ), onTap: (g) {
          Dialogs.dialogWidget(
              context: Get.context,
              buttonNameRight: "Darn",
              buttonNameLeft: "",
              title: title,
              clickLeft: () {},
              clickRight: () {},
              w: Column(
                children: [
                  Text(msg),
                ],
              ));
        },
            backgroundColor: Colors.white.withAlpha(145),
            snackPosition: SnackPosition.BOTTOM,
            margin: EdgeInsets.all(14),
            maxWidth: 400,
            colorText: Colors.black);
      } catch (e) {}
    });
  }

  static void snack2(String title, String msg) async {
    queue(6000, () {
      try {
        Get.snackbar(title, msg,
            forwardAnimationCurve: Curves.easeOutQuint,
            reverseAnimationCurve: Curves.easeOutQuint,
            animationDuration: Duration(milliseconds: 760),
            duration: Duration(seconds: 5),
            shouldIconPulse: true,
            icon: Icon(
              HawkeyeConstants.icon,
              color: HawkeyeConstants.themeColor,
              size: 36,
            ), onTap: (g) {
          Dialogs.dialogWidget(
              context: Get.context,
              buttonNameRight: "Ok",
              buttonNameLeft: "",
              title: title ?? "More Info",
              clickLeft: () {},
              clickRight: () {},
              w: Column(
                children: [
                  Text(msg),
                ],
              ));
        },
            backgroundColor: Colors.white.withAlpha(145),
            snackPosition: SnackPosition.BOTTOM,
            margin: EdgeInsets.all(14),
            maxWidth: 400,
            colorText: Colors.black);
      } catch (e) {}
    });
  }
}
