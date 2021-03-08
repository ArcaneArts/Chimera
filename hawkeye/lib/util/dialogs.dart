import 'package:flutter/material.dart';
import 'package:hawkeye/util/fast_visibility.dart';
import 'package:hawkeye/util/paddings.dart';
import 'package:passwordfield/passwordfield.dart';

class Dialogs {
  static double rad = 7;
  static double ele = 36;

  static realDialog({
    BuildContext context,
    String title = "Title",
    String description = "Description",
    String buttonNameLeft = "Cancel",
    String buttonNameRight = "OK",
    VoidCallback clickLeft,
    VoidCallback clickRight,
  }) {
    showDialog(
        context: context,
        builder: (context) {
          return Dialog(
            shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.all(Radius.circular(rad))),
            elevation: ele,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              children: <Widget>[
                Padding(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisSize: MainAxisSize.min,
                    children: <Widget>[
                      PaddingBottom(
                        child: Text(
                          title,
                          style: TextStyle(fontSize: 24),
                        ),
                        padding: 7,
                      ),
                      Text(
                        description,
                        style: TextStyle(fontSize: 18),
                      ),
                    ],
                  ),
                  padding: EdgeInsets.only(left: 14, right: 14, top: 14),
                ),
                PaddingBottom(
                  child: Align(
                    alignment: Alignment.bottomRight,
                    child: Row(
                      crossAxisAlignment: CrossAxisAlignment.end,
                      mainAxisSize: MainAxisSize.min,
                      children: <Widget>[
                        FlatButton(
                            onPressed: () {
                              Navigator.pop(context);
                              clickLeft();
                            },
                            child: Text(buttonNameLeft)),
                        FlatButton(
                            onPressed: () {
                              Navigator.pop(context);
                              clickRight();
                            },
                            child: Text(buttonNameRight))
                      ],
                    ),
                  ),
                  padding: 7,
                )
              ],
            ),
          );
        });
  }

  static void dialog(
      {BuildContext context,
      String title = "Title",
      String description = "Description",
      String buttonNameLeft = "Cancel",
      String buttonNameRight = "OK",
      VoidCallback clickLeft,
      VoidCallback clickRight}) {
    showDialog(
        context: context,
        builder: (context) => Dialog(
              elevation: ele,
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.all(Radius.circular(rad))),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: <Widget>[
                  Padding(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisSize: MainAxisSize.min,
                      children: <Widget>[
                        PaddingBottom(
                          child: Text(
                            title,
                            style: TextStyle(fontSize: 24),
                          ),
                          padding: 7,
                        ),
                        Text(
                          description,
                          style: TextStyle(fontSize: 18),
                        ),
                      ],
                    ),
                    padding: EdgeInsets.only(left: 14, right: 14, top: 14),
                  ),
                  PaddingBottom(
                    child: Align(
                      alignment: Alignment.bottomRight,
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.end,
                        mainAxisSize: MainAxisSize.min,
                        children: <Widget>[
                          FastVisibility(
                            child: FlatButton(
                                onPressed: () {
                                  Navigator.pop(context);
                                  if (clickLeft != null) clickLeft();
                                },
                                child: Text(buttonNameLeft)),
                            visible: buttonNameLeft != null &&
                                buttonNameLeft.isNotEmpty,
                          ),
                          FlatButton(
                              onPressed: () {
                                Navigator.pop(context);
                                if (clickRight != null) clickRight();
                              },
                              child: Text(buttonNameRight))
                        ],
                      ),
                    ),
                    padding: 7,
                  )
                ],
              ),
            ));
  }

  static void dialog3(
      {BuildContext context,
      String title = "Title",
      String description = "Description",
      String buttonNameLeft = "Cancel",
      String buttonNameCenter = "...",
      String buttonNameRight = "OK",
      VoidCallback clickLeft,
      VoidCallback clickCenter,
      VoidCallback clickRight}) {
    showDialog(
        context: context,
        builder: (context) => Dialog(
              elevation: ele,
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.all(Radius.circular(rad))),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: <Widget>[
                  Padding(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisSize: MainAxisSize.min,
                      children: <Widget>[
                        PaddingBottom(
                          child: Text(
                            title,
                            style: TextStyle(fontSize: 24),
                          ),
                          padding: 7,
                        ),
                        Text(
                          description,
                          style: TextStyle(fontSize: 18),
                        ),
                      ],
                    ),
                    padding: EdgeInsets.only(left: 14, right: 14, top: 14),
                  ),
                  PaddingBottom(
                    child: Align(
                      alignment: Alignment.bottomRight,
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.end,
                        mainAxisSize: MainAxisSize.min,
                        children: <Widget>[
                          FastVisibility(
                            child: FlatButton(
                                onPressed: () {
                                  Navigator.pop(context);
                                  if (clickLeft != null) clickLeft();
                                },
                                child: Text(buttonNameLeft)),
                            visible: buttonNameLeft != null &&
                                buttonNameLeft.isNotEmpty,
                          ),
                          FastVisibility(
                            child: FlatButton(
                                onPressed: () {
                                  Navigator.pop(context);
                                  if (clickCenter != null) clickCenter();
                                },
                                child: Text(buttonNameCenter)),
                            visible: buttonNameCenter != null &&
                                buttonNameCenter.isNotEmpty,
                          ),
                          FlatButton(
                              onPressed: () {
                                Navigator.pop(context);
                                if (clickRight != null) clickRight();
                              },
                              child: Text(buttonNameRight))
                        ],
                      ),
                    ),
                    padding: 7,
                  )
                ],
              ),
            ));
  }

  static void dialogWidget(
      {BuildContext context,
      Widget w,
      String title = "Title",
      String buttonNameLeft = "Cancel",
      String buttonNameRight = "OK",
      VoidCallback clickLeft,
      VoidCallback clickRight}) {
    showDialog(
        context: context,
        builder: (context) => Dialog(
              elevation: ele,
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.all(Radius.circular(rad))),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: <Widget>[
                  Padding(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisSize: MainAxisSize.min,
                      children: <Widget>[
                        PaddingBottom(
                          child: Text(
                            title,
                            style: TextStyle(fontSize: 24),
                          ),
                          padding: 7,
                        ),
                        w,
                      ],
                    ),
                    padding: EdgeInsets.only(left: 14, right: 14, top: 14),
                  ),
                  PaddingBottom(
                    child: Align(
                      alignment: Alignment.bottomRight,
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.end,
                        mainAxisSize: MainAxisSize.min,
                        children: <Widget>[
                          FlatButton(
                              onPressed: () {
                                Navigator.pop(context);
                                clickLeft();
                              },
                              child: Text(buttonNameLeft)),
                          FlatButton(
                              onPressed: () {
                                Navigator.pop(context);
                                clickRight();
                              },
                              child: Text(buttonNameRight))
                        ],
                      ),
                    ),
                    padding: 7,
                  )
                ],
              ),
            ));
  }

  static void dialogForPassword(
      {BuildContext context,
      String title = "Title",
      String description = "Description",
      String buttonNameLeft = "Cancel",
      String buttonNameRight = "OK",
      String hint = "Confirm your Password",
      VoidCallback clickLeft,
      VoidCallback clickRight,
      TextEditingController controller}) {
    dialogWidget(
        context: context,
        title: title,
        buttonNameLeft: buttonNameLeft,
        buttonNameRight: buttonNameRight,
        clickRight: clickRight,
        clickLeft: clickLeft,
        w: Padding(
          padding: EdgeInsets.all(7),
          child: PasswordField(
            controller: controller,
            errorMessage: 'required at least 1 letter and number 5+ chars',
            pattern: r'',
            suffixIconEnabled: true,
            inputStyle: TextStyle(
                fontSize: 24,
                color: Theme.of(context)
                    .textTheme
                    .headline4
                    .color
                    .withOpacity(0.5)),
            color: Theme.of(context).textTheme.headline4.color.withOpacity(0.5),
            hintText: "Password",
          ),
        ));
  }

  static void dialogForText(
      {BuildContext context,
      String title = "Title",
      String description = "Description",
      String buttonNameLeft = "Cancel",
      String buttonNameRight = "OK",
      String hint = "Hint",
      VoidCallback clickLeft,
      VoidCallback clickRight,
      TextEditingController controller}) {
    dialogWidget(
        context: context,
        title: title,
        buttonNameLeft: buttonNameLeft,
        buttonNameRight: buttonNameRight,
        clickRight: clickRight,
        clickLeft: clickLeft,
        w: Padding(
          padding: EdgeInsets.all(7),
          child: TextField(
            controller: controller,
            autofocus: true,
            decoration: InputDecoration(hintText: hint),
            style: TextStyle(fontSize: 18),
          ),
        ));
  }

  static void dialogForTextMultiline(
      {BuildContext context,
      String title = "Title",
      String description = "Description",
      String buttonNameLeft = "Cancel",
      String buttonNameRight = "OK",
      String hint = "Hint",
      VoidCallback clickLeft,
      VoidCallback clickRight,
      TextEditingController controller}) {
    dialogWidget(
        context: context,
        title: title,
        buttonNameLeft: buttonNameLeft,
        buttonNameRight: buttonNameRight,
        clickRight: clickRight,
        clickLeft: clickLeft,
        w: Padding(
          padding: EdgeInsets.all(7),
          child: TextField(
            controller: controller,
            autofocus: true,
            maxLength: 500,
            maxLines: 5,
            keyboardType: TextInputType.multiline,
            decoration: InputDecoration(hintText: hint),
            style: TextStyle(fontSize: 18),
          ),
        ));
  }

  static void dialogForNum(
      {BuildContext context,
      String title = "Title",
      String description = "Description",
      String buttonNameLeft = "Cancel",
      String buttonNameRight = "OK",
      String hint = "Hint",
      VoidCallback clickLeft,
      VoidCallback clickRight,
      TextEditingController controller}) {
    dialogWidget(
        context: context,
        title: title,
        buttonNameLeft: buttonNameLeft,
        buttonNameRight: buttonNameRight,
        clickRight: clickRight,
        clickLeft: clickLeft,
        w: Padding(
          padding: EdgeInsets.all(7),
          child: TextField(
            keyboardType:
                TextInputType.numberWithOptions(signed: false, decimal: false),
            controller: controller,
            autofocus: true,
            decoration: InputDecoration(hintText: hint),
            style: TextStyle(fontSize: 18),
          ),
        ));
  }

  static void dialogForgot(
      {BuildContext context,
      String title = "Title",
      String description = "Description",
      String buttonNameLeft = "Cancel",
      String buttonNameRight = "OK",
      String hint = "Confirm your Password",
      VoidCallback clickLeft,
      VoidCallback clickRight,
      TextEditingController controller}) {
    dialogWidget(
        context: context,
        title: title,
        buttonNameLeft: buttonNameLeft,
        buttonNameRight: buttonNameRight,
        clickRight: clickRight,
        clickLeft: clickLeft,
        w: Padding(
          padding: EdgeInsets.all(7),
          child: PasswordField(
            controller: controller,
            errorMessage: 'required at least 1 letter and number 5+ chars',
            pattern: r'',
            suffixIconEnabled: true,
            inputStyle: TextStyle(
                fontSize: 24,
                color: Theme.of(context)
                    .textTheme
                    .headline4
                    .color
                    .withOpacity(0.5)),
            color: Theme.of(context).textTheme.headline4.color.withOpacity(0.5),
            hintText: "Password",
          ),
        ));
  }

  static void dialogForPassword3(
      {BuildContext context,
      String title = "Title",
      String description = "Description",
      String buttonNameLeft = "Cancel",
      String buttonNameRight = "OK",
      VoidCallback clickLeft,
      VoidCallback clickRight,
      TextEditingController controller1,
      TextEditingController controller2,
      TextEditingController controller3,
      String hint1,
      String hint2,
      String hint3}) {
    dialogWidget(
        context: context,
        title: title,
        buttonNameLeft: buttonNameLeft,
        buttonNameRight: buttonNameRight,
        clickRight: clickRight,
        clickLeft: clickLeft,
        w: Padding(
            padding: EdgeInsets.all(7),
            child: Column(
              children: <Widget>[
                PasswordField(
                  controller: controller1,
                  errorMessage:
                      'required at least 1 letter and number 5+ chars',
                  pattern: r'',
                  suffixIconEnabled: true,
                  inputStyle: TextStyle(
                      fontSize: 24,
                      color: Theme.of(context)
                          .textTheme
                          .headline4
                          .color
                          .withOpacity(0.5)),
                  color: Theme.of(context)
                      .textTheme
                      .headline4
                      .color
                      .withOpacity(0.5),
                  hintText: hint1,
                ),
                PasswordField(
                  controller: controller2,
                  errorMessage:
                      'required at least 1 letter and number 5+ chars',
                  pattern: r'',
                  suffixIconEnabled: true,
                  inputStyle: TextStyle(
                      fontSize: 24,
                      color: Theme.of(context)
                          .textTheme
                          .headline4
                          .color
                          .withOpacity(0.5)),
                  color: Theme.of(context)
                      .textTheme
                      .headline4
                      .color
                      .withOpacity(0.5),
                  hintText: hint2,
                ),
                PasswordField(
                  controller: controller3,
                  errorMessage:
                      'required at least 1 letter and number 5+ chars',
                  pattern: r'',
                  suffixIconEnabled: true,
                  inputStyle: TextStyle(
                      fontSize: 24,
                      color: Theme.of(context)
                          .textTheme
                          .headline4
                          .color
                          .withOpacity(0.5)),
                  color: Theme.of(context)
                      .textTheme
                      .headline4
                      .color
                      .withOpacity(0.5),
                  hintText: hint3,
                ),
              ],
            )));
  }

  static void dialogForChangeEmail(
      {BuildContext context,
      String title = "Title",
      String description = "Description",
      String buttonNameLeft = "Cancel",
      String buttonNameRight = "OK",
      VoidCallback clickLeft,
      VoidCallback clickRight,
      TextEditingController controller1,
      TextEditingController controller2,
      String hint1,
      String hint2}) {
    dialogWidget(
        context: context,
        title: title,
        buttonNameLeft: buttonNameLeft,
        buttonNameRight: buttonNameRight,
        clickRight: clickRight,
        clickLeft: clickLeft,
        w: Padding(
            padding: EdgeInsets.all(7),
            child: Column(
              children: <Widget>[
                PasswordField(
                  controller: controller1,
                  errorMessage:
                      'required at least 1 letter and number 5+ chars',
                  pattern: r'',
                  suffixIconEnabled: true,
                  inputStyle: TextStyle(
                      fontSize: 24,
                      color: Theme.of(context)
                          .textTheme
                          .headline4
                          .color
                          .withOpacity(0.5)),
                  color: Theme.of(context)
                      .textTheme
                      .headline4
                      .color
                      .withOpacity(0.5),
                  hintText: hint1,
                ),
                TextField(
                  controller: controller2,
                  style: TextStyle(fontSize: 18),
                  decoration: InputDecoration(hintText: hint2),
                ),
              ],
            )));
  }
}
