import 'dart:convert';
import 'dart:ui';

import 'package:crypto/crypto.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:hawkeye/chimera/protocol.dart';
import 'package:hawkeye/core/constant.dart';
import 'package:hawkeye/hawkeye.dart';
import 'package:hawkeye/util/linear_progress.dart';
import 'package:hawkeye/util/paddings.dart';

class Authenticator extends StatefulWidget {
  final IconData icon;
  final Color color;
  final String name;
  final MaterialPageRoute route;

  Authenticator(
      {this.icon = Icons.help,
      this.color = Colors.blue,
      @required this.route,
      this.name = "Hawkeye"}) {
    HawkeyeConstants.name = name;
    HawkeyeConstants.themeColor = color;
    HawkeyeConstants.icon = icon;
  }

  @override
  State<StatefulWidget> createState() => AuthenticatorState();
}

class AuthenticatorState extends State<Authenticator> {
  TextEditingController user = new TextEditingController(
      text: Storage.getTemp().get("field.user", defaultValue: ""));
  TextEditingController pass = new TextEditingController(
      text: Storage.getTemp().get("field.pass", defaultValue: ""));

  @override
  Widget build(BuildContext context) {
    return Center(
      child: FutureBuilder<bool>(
        future: ChimeraGateway.ping().then((value) => value ?? false),
        builder: (c, s) {
          if (!s.hasData) {
            return buildLoading(
                progress: 0.25, status: "Connecting to Chimera");
          }

          if (s.data) {
            return FutureBuilder<bool>(
              future: auth(),
              builder: (c, s) {
                if (!s.hasData) {
                  return buildLoading(progress: 0.75, status: "Authenticating");
                }

                if (s.data) {
                  return FutureBuilder<User>(
                      future: ChimeraAccount.getMe().then((value) =>
                          value == null ? (User()..id = null) : value),
                      builder: (c, s) {
                        if (!s.hasData) {
                          return buildLoading(
                              progress: 0.85, status: "Signing In");
                        }

                        if (s.data != null && s.data.id != null) {
                          Storage.getState().set("user", s.data);
                          Future.delayed(Duration(milliseconds: 500), () {
                            Navigator.pushReplacement(context, widget.route);
                          });
                          return buildLoading(
                              progress: 1,
                              status: "Welcome Back ${s.data.firstName}!");
                        } else {
                          Future.delayed(Duration(milliseconds: 500), () {
                            Storage.getState().clear();
                            setState(() {});
                          });
                          return buildLoading(
                              progress: 0.85, status: "Failed to Sign In!");
                        }
                      });
                } else {
                  Future.delayed(
                      Duration(milliseconds: 50),
                      () => Navigator.pushReplacement(
                          context,
                          SlowPageRoute(
                              builder: (context) => SignIn(
                                    name: widget.name,
                                    color: widget.color,
                                    route: widget.route,
                                    icon: widget.icon,
                                  ))));
                  return buildLoading(
                      progress: 0.85, status: "Sign In Required");
                }
              },
            );
          } else {
            Future.delayed(Duration(seconds: 3), () {
              setState(() {});
            });
            return buildLoading(progress: 0, status: "Reconnecting to Chimera");
          }
        },
      ),
    );
  }

  Widget buildLoading({double progress = 0, String status = ""}) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisSize: MainAxisSize.min,
      children: [
        Hero(
          tag: "icon",
          child: Material(
              child: Icon(
                widget.icon,
                size: 256,
                color: widget.color,
              ),
              color: Colors.transparent),
        ),
        Hero(
          tag: "card",
          child: Material(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisSize: MainAxisSize.min,
                children: [
                  Padding(
                    padding: EdgeInsets.only(top: 24),
                    child: Container(
                      width: 150,
                      child: LinearSmoothProgress(
                        value: progress,
                        curve: Curves.easeOutExpo,
                        ms: 2000,
                        color: widget.color.withAlpha(150),
                      ),
                    ),
                  ),
                  Text(
                    status,
                    style: TextStyle(
                        fontSize: 24, color: widget.color.withAlpha(150)),
                  )
                ],
              ),
              color: Colors.transparent),
        )
      ],
    );
  }

  Future<bool> auth() async {
    AccessToken token = Storage.getState().get("token");

    if (token == null) {
      return false;
    }

    return ChimeraAccount.validateToken(token);
  }
}

class SignIn extends StatefulWidget {
  final IconData icon;
  final Color color;
  final MaterialPageRoute route;
  final String name;

  SignIn(
      {this.icon = Icons.help,
      @required this.route,
      this.color = Colors.blue,
      this.name = "Hawkeye"});

  @override
  State<StatefulWidget> createState() => SignInState();
}

class SignInState extends State<SignIn> {
  FocusNode fnUser = FocusNode();
  FocusNode fnPass = FocusNode();
  TextEditingController user = new TextEditingController(
      text: Storage.getTemp().get("field.user", defaultValue: ""));
  TextEditingController pass = new TextEditingController(
      text: Storage.getTemp().get("field.pass", defaultValue: ""));
  bool emailError = false;
  bool passError = false;
  bool loading = false;

  @override
  void dispose() {
    fnUser.dispose();
    fnPass.dispose();

    super.dispose();
  }

  bool isActuallyValid() {
    return !emailError &&
        !passError &&
        !pass.text.isEmpty &&
        !user.text.isEmpty;
  }

  void validate() {
    Storage.getTemp().set("field.user", user.value.text);
    Storage.getTemp().set("field.pass", pass.value.text);

    setState(() {
      if (user.value.text.isEmpty) {
        emailError = false;
      } else {
        emailError =
            !user.value.text.contains("@") || !user.value.text.contains(".");
      }

      if (pass.value.text.isEmpty) {
        passError = false;
      } else {
        passError = pass.value.text.length < 8;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: Center(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisSize: MainAxisSize.min,
        children: [
          PaddingBottom(
            padding: 24,
            child: Hero(
              tag: "icon",
              child: Material(
                child: Flex(
                  mainAxisSize: MainAxisSize.min,
                  direction: Axis.horizontal,
                  children: [
                    Flexible(
                        child: Icon(
                      widget.icon,
                      size: 150,
                      color: widget.color,
                    )),
                    Flexible(
                        child: PaddingLeft(
                      child: Text(
                        widget.name,
                        maxLines: 1,
                        softWrap: false,
                        overflow: TextOverflow.fade,
                        style: TextStyle(fontSize: 32),
                      ),
                      padding: 14,
                    )),
                  ],
                ),
                color: Colors.transparent,
              ),
            ),
          ),
          PaddingHorizontal(
            padding: 14,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisSize: MainAxisSize.min,
              children: [
                ConstrainedBox(
                  constraints: BoxConstraints.loose(Size(470, 10000)),
                  child: Hero(
                    tag: "card",
                    child: Card(
                      child: Theme(
                          data: ThemeData(
                              primaryColor: widget.color,
                              accentColor: widget.color,
                              hintColor: widget.color.withAlpha(120)),
                          child: PaddingAll(
                            padding: 14,
                            child: Wrap(
                              children: [
                                Row(
                                  children: [
                                    Text(
                                      "Sign In",
                                      style: TextStyle(fontSize: 24),
                                    )
                                  ],
                                ),
                                Theme(
                                    data: ThemeData(
                                        primaryColor: emailError
                                            ? Colors.red
                                            : widget.color,
                                        accentColor: emailError
                                            ? Colors.red
                                            : widget.color,
                                        hintColor: (emailError
                                                ? Colors.red
                                                : widget.color)
                                            .withAlpha(200)),
                                    child: TextField(
                                      enabled: !loading,
                                      focusNode: fnUser,
                                      controller: user,
                                      cursorColor: emailError
                                          ? Colors.red
                                          : widget.color,
                                      autocorrect: false,
                                      autofocus: true,
                                      onSubmitted: (v) => fnPass.requestFocus(),
                                      maxLines: 1,
                                      onChanged: (a) => validate(),
                                      decoration: InputDecoration(
                                          hintText: "Email Address"),
                                    )),
                                Theme(
                                    data: ThemeData(
                                        primaryColor: passError
                                            ? Colors.red
                                            : widget.color,
                                        accentColor: passError
                                            ? Colors.red
                                            : widget.color,
                                        hintColor: (passError
                                                ? Colors.red
                                                : widget.color)
                                            .withAlpha(200)),
                                    child: TextField(
                                      controller: pass,
                                      enabled: !loading,
                                      focusNode: fnPass,
                                      cursorColor:
                                          passError ? Colors.red : widget.color,
                                      onChanged: (a) => validate(),
                                      onSubmitted: isActuallyValid()
                                          ? (v) => signIn(
                                              user.value.text, pass.value.text)
                                          : null,
                                      obscureText: true,
                                      autocorrect: false,
                                      autofocus: false,
                                      maxLines: 1,
                                      decoration:
                                          InputDecoration(hintText: "Password"),
                                    )),
                                PaddingTop(
                                  padding: 7,
                                  child: Row(
                                    children: [
                                      Spacer(),
                                      FlatButton(
                                          onPressed: isActuallyValid()
                                              ? () => signIn(user.value.text,
                                                  pass.value.text)
                                              : null,
                                          child: Text(
                                            "Sign In",
                                            style: TextStyle(
                                                color: isActuallyValid()
                                                    ? widget.color
                                                    : null),
                                          ))
                                    ],
                                  ),
                                )
                              ],
                            ),
                          )),
                    ),
                  ),
                ),
                Hero(
                    tag: "button",
                    child: FlatButton(
                        onPressed: () {
                          doCreateAccountPressed();
                        },
                        child: Text(
                          "Create Account",
                          softWrap: false,
                          maxLines: 1,
                          overflow: TextOverflow.fade,
                        )))
              ],
            ),
          ),
        ],
      ),
    ));
  }

  void signIn(String u, String p) {
    setState(() {
      loading = true;
    });
    ChimeraAccount.aquireToken(u, "${sha256.convert(utf8.encode(p))}")
        .then((a) {
      if (a != null) {
        Storage.getState().set("token", a);
        Future.delayed(
            Duration(milliseconds: 50),
            () => Navigator.pushReplacement(
                context,
                SlowPageRoute(
                    builder: (context) => Authenticator(
                          name: widget.name,
                          icon: widget.icon,
                          route: widget.route,
                          color: widget.color,
                        ))));
      } else {
        setState(() {
          loading = false;
        });
      }
    });
  }

  void doCreateAccountPressed() {
    Navigator.pushReplacement(
        context,
        SlowPageRoute(
            builder: (context) => SignUp(
                  route: widget.route,
                  icon: widget.icon,
                  name: widget.name,
                  color: widget.color,
                )));
  }
}

class SlowPageRoute extends MaterialPageRoute {
  SlowPageRoute({builder}) : super(builder: builder);
}

class SignUp extends StatefulWidget {
  final IconData icon;
  final Color color;
  final MaterialPageRoute route;
  final String name;

  SignUp(
      {this.icon = Icons.help,
      @required this.route,
      this.color = Colors.blue,
      this.name = "Hawkeye"});

  @override
  State<StatefulWidget> createState() => SignUpState();
}

class SignUpState extends State<SignUp> {
  FocusNode fnFname = FocusNode();
  FocusNode fnLname = FocusNode();
  FocusNode fnUser = FocusNode();
  FocusNode fnPass = FocusNode();
  TextEditingController fname = new TextEditingController(
      text: Storage.getTemp().get("field.fname", defaultValue: ""));
  TextEditingController lname = new TextEditingController(
      text: Storage.getTemp().get("field.lname", defaultValue: ""));
  TextEditingController user = new TextEditingController(
      text: Storage.getTemp().get("field.user", defaultValue: ""));
  TextEditingController pass = new TextEditingController(
      text: Storage.getTemp().get("field.pass", defaultValue: ""));
  bool fnameError = false;
  bool lnameError = false;
  bool emailError = false;
  bool passError = false;
  bool loading = false;

  @override
  void dispose() {
    fnUser.dispose();
    fnPass.dispose();

    super.dispose();
  }

  bool isActuallyValid() {
    return !emailError &&
        !fnameError &&
        !lnameError &&
        !passError &&
        !pass.text.isEmpty &&
        !user.text.isEmpty;
  }

  void validate() {
    Storage.getTemp().set("field.fname", fname.value.text);
    Storage.getTemp().set("field.lname", lname.value.text);
    Storage.getTemp().set("field.user", user.value.text);
    Storage.getTemp().set("field.pass", pass.value.text);

    setState(() {
      if (fname.value.text.isEmpty) {
        fnameError = false;
      } else {
        fnameError = fname.value.text.trim().length <= 1;
      }

      if (lname.value.text.isEmpty) {
        lnameError = false;
      } else {
        lnameError = lname.value.text.trim().length <= 1;
      }

      if (user.value.text.isEmpty) {
        emailError = false;
      } else {
        emailError =
            !user.value.text.contains("@") || !user.value.text.contains(".");
      }

      if (pass.value.text.isEmpty) {
        passError = false;
      } else {
        passError = pass.value.text.length < 8;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisSize: MainAxisSize.min,
          children: [
            PaddingBottom(
              padding: 24,
              child: Hero(
                tag: "icon",
                child: Material(
                    child: Icon(
                      widget.icon,
                      size: 120,
                      color: widget.color,
                    ),
                    color: Colors.transparent),
              ),
            ),
            PaddingHorizontal(
              padding: 14,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisSize: MainAxisSize.min,
                children: [
                  ConstrainedBox(
                    constraints: BoxConstraints.loose(Size(470, 10000)),
                    child: Hero(
                      tag: "card",
                      child: Card(
                        child: Theme(
                            data: ThemeData(
                                primaryColor: widget.color,
                                accentColor: widget.color,
                                hintColor: widget.color.withAlpha(120)),
                            child: PaddingAll(
                              padding: 14,
                              child: Wrap(
                                children: [
                                  Row(
                                    children: [
                                      Text(
                                        "Create an Account",
                                        style: TextStyle(fontSize: 24),
                                      )
                                    ],
                                  ),
                                  Theme(
                                      data: ThemeData(
                                          primaryColor: fnameError
                                              ? Colors.red
                                              : widget.color,
                                          accentColor: fnameError
                                              ? Colors.red
                                              : widget.color,
                                          hintColor: (fnameError
                                                  ? Colors.red
                                                  : widget.color)
                                              .withAlpha(200)),
                                      child: TextField(
                                        focusNode: fnFname,
                                        controller: fname,
                                        enabled: !loading,
                                        cursorColor: fnameError
                                            ? Colors.red
                                            : widget.color,
                                        autocorrect: false,
                                        autofocus: true,
                                        onSubmitted: (v) =>
                                            fnLname.requestFocus(),
                                        maxLines: 1,
                                        onChanged: (a) => validate(),
                                        decoration: InputDecoration(
                                            hintText: "First Name"),
                                      )),
                                  Theme(
                                      data: ThemeData(
                                          primaryColor: lnameError
                                              ? Colors.red
                                              : widget.color,
                                          accentColor: lnameError
                                              ? Colors.red
                                              : widget.color,
                                          hintColor: (lnameError
                                                  ? Colors.red
                                                  : widget.color)
                                              .withAlpha(200)),
                                      child: TextField(
                                        focusNode: fnLname,
                                        enabled: !loading,
                                        controller: lname,
                                        cursorColor: lnameError
                                            ? Colors.red
                                            : widget.color,
                                        autocorrect: false,
                                        autofocus: false,
                                        onSubmitted: (v) =>
                                            fnUser.requestFocus(),
                                        maxLines: 1,
                                        onChanged: (a) => validate(),
                                        decoration: InputDecoration(
                                            hintText: "Last Name"),
                                      )),
                                  Theme(
                                      data: ThemeData(
                                          primaryColor: emailError
                                              ? Colors.red
                                              : widget.color,
                                          accentColor: emailError
                                              ? Colors.red
                                              : widget.color,
                                          hintColor: (emailError
                                                  ? Colors.red
                                                  : widget.color)
                                              .withAlpha(200)),
                                      child: TextField(
                                        focusNode: fnUser,
                                        controller: user,
                                        enabled: !loading,
                                        cursorColor: emailError
                                            ? Colors.red
                                            : widget.color,
                                        autocorrect: false,
                                        autofocus: false,
                                        onSubmitted: (v) =>
                                            fnPass.requestFocus(),
                                        maxLines: 1,
                                        onChanged: (a) => validate(),
                                        decoration: InputDecoration(
                                            hintText: "Email Address"),
                                      )),
                                  Theme(
                                      data: ThemeData(
                                          primaryColor: passError
                                              ? Colors.red
                                              : widget.color,
                                          accentColor: passError
                                              ? Colors.red
                                              : widget.color,
                                          hintColor: (passError
                                                  ? Colors.red
                                                  : widget.color)
                                              .withAlpha(200)),
                                      child: TextField(
                                        controller: pass,
                                        focusNode: fnPass,
                                        enabled: !loading,
                                        cursorColor: passError
                                            ? Colors.red
                                            : widget.color,
                                        onChanged: (a) => validate(),
                                        onSubmitted: isActuallyValid()
                                            ? (v) => signUp(
                                                fname.value.text,
                                                lname.value.text,
                                                user.value.text,
                                                pass.value.text)
                                            : null,
                                        obscureText: true,
                                        autocorrect: false,
                                        autofocus: false,
                                        maxLines: 1,
                                        decoration: InputDecoration(
                                            hintText: "Password"),
                                      )),
                                  PaddingTop(
                                    padding: 7,
                                    child: Row(
                                      children: [
                                        Spacer(),
                                        FlatButton(
                                            onPressed:
                                                isActuallyValid() && !loading
                                                    ? () => signUp(
                                                        fname.value.text,
                                                        lname.value.text,
                                                        user.value.text,
                                                        pass.value.text)
                                                    : null,
                                            child: Text(
                                              "Create Account",
                                              style: TextStyle(
                                                  color: isActuallyValid()
                                                      ? widget.color
                                                      : null),
                                            ))
                                      ],
                                    ),
                                  )
                                ],
                              ),
                            )),
                      ),
                    ),
                  ),
                  Hero(
                      tag: "button",
                      child: FlatButton(
                          onPressed: () {
                            doSignInInsteadPress();
                          },
                          child: Text("Sign In Instead")))
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  void signUp(String a, String b, String u, String p) {
    setState(() {
      loading = true;
    });
    ChimeraAccount.signUp(a, b, u, "${sha256.convert(utf8.encode(p))}")
        .then((a) {
      if (a != null) {
        Storage.getState().set("user", a);
        setState(() {
          loading = true;
        });

        Future.delayed(Duration(milliseconds: 50), () {
          ChimeraAccount.aquireToken(u, "${sha256.convert(utf8.encode(p))}")
              .then((a) {
            if (a != null) {
              Storage.getState().set("token", a);
              Future.delayed(
                  Duration(milliseconds: 50),
                  () => Navigator.pushReplacement(
                      context,
                      SlowPageRoute(
                          builder: (context) => Authenticator(
                                name: widget.name,
                                icon: widget.icon,
                                route: widget.route,
                                color: widget.color,
                              ))));
            } else {
              setState(() {
                loading = false;
              });
            }
          });
        });
      } else {
        setState(() {
          loading = false;
        });
      }
    });
  }

  void doSignInInsteadPress() {
    Navigator.pushReplacement(
        context,
        SlowPageRoute(
            builder: (context) => SignIn(
                  route: widget.route,
                  icon: widget.icon,
                  name: widget.name,
                  color: widget.color,
                )));
  }
}
