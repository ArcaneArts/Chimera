import 'dart:math' as math;

import 'package:flutter/material.dart';

const double _kLinearProgressIndicatorHeight = 6.0;

class CircularSmoothProgress extends StatefulWidget {
  final Color color;
  final double value;
  final Curve curveMode;
  final double start;
  final double thickness;
  final int ms;

  CircularSmoothProgress(
      {Key key,
      this.start = 0,
      this.color = Colors.black,
      this.curveMode,
      this.thickness,
      this.value = 0,
      this.ms = 333})
      : super(key: key);

  @override
  _CircularSmoothProgressState createState() => _CircularSmoothProgressState();
}

class _CircularSmoothProgressState extends State<CircularSmoothProgress>
    with SingleTickerProviderStateMixin {
  AnimationController _controller;
  Tween<double> valueTween;
  Animation<double> curve;

  @override
  void initState() {
    super.initState();
    this._controller = AnimationController(
      vsync: this,
      duration: Duration(milliseconds: widget.ms),
    );
    this._controller.forward();
    this.valueTween = Tween<double>(
      begin: widget.start ?? 0,
      end: this.widget.value,
    );
    this.curve =
        CurvedAnimation(parent: this._controller, curve: widget.curveMode);
  }

  @override
  void dispose() {
    this._controller.dispose();
    super.dispose();
  }

  @override
  void didUpdateWidget(CircularSmoothProgress oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (this.widget.value != oldWidget.value) {
      double beginValue =
          this.valueTween?.evaluate(curve) ?? oldWidget?.value ?? 0;
      this.valueTween = Tween<double>(
        begin: beginValue,
        end: this.widget.value ?? 1,
      );
      this._controller
        ..value = 0
        ..forward();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      constraints: const BoxConstraints(
        minWidth: double.infinity,
        minHeight: _kLinearProgressIndicatorHeight,
      ),
      child: AnimatedBuilder(
          animation: _controller,
          builder: (context, child) {
            return CustomPaint(
              painter: _CircularProgressIndicatorPainter(
                backgroundColor: Colors.transparent,
                valueColor: widget.color,
                strokeWidth: widget.thickness ?? 4,
                value: valueTween.evaluate(curve), // may be null
              ),
            );
          }),
    );
  }
}

class _CircularProgressIndicatorPainter extends CustomPainter {
  _CircularProgressIndicatorPainter({
    this.backgroundColor,
    this.valueColor,
    this.value,
    this.headValue,
    this.tailValue,
    this.stepValue,
    this.rotationValue,
    this.strokeWidth,
  })  : arcStart = value != null
            ? _startAngle
            : _startAngle +
                tailValue * 3 / 2 * math.pi +
                rotationValue * math.pi * 1.7 -
                stepValue * 0.8 * math.pi,
        arcSweep = value != null
            ? (value.clamp(0.0, 1.0) as double) * _sweep
            : math.max(
                headValue * 3 / 2 * math.pi - tailValue * 3 / 2 * math.pi,
                _epsilon);

  final Color backgroundColor;
  final Color valueColor;
  final double value;
  final double headValue;
  final double tailValue;
  final int stepValue;
  final double rotationValue;
  final double strokeWidth;
  final double arcStart;
  final double arcSweep;

  static const double _twoPi = math.pi * 2.0;
  static const double _epsilon = .001;

  // Canvas.drawArc(r, 0, 2*PI) doesn't draw anything, so just get close.
  static const double _sweep = _twoPi - _epsilon;
  static const double _startAngle = -math.pi / 2.0;

  @override
  void paint(Canvas canvas, Size size) {
    final Paint paint = Paint()
      ..color = valueColor
      ..strokeWidth = strokeWidth
      ..style = PaintingStyle.stroke;
    if (backgroundColor != null) {
      final Paint backgroundPaint = Paint()
        ..color = backgroundColor
        ..strokeWidth = strokeWidth
        ..style = PaintingStyle.stroke;
      canvas.drawArc(Offset.zero & size, 0, _sweep, false, backgroundPaint);
    }

    if (value == null) // Indeterminate
      paint.strokeCap = StrokeCap.square;

    canvas.drawArc(Offset.zero & size, arcStart, arcSweep, false, paint);
  }

  @override
  bool shouldRepaint(_CircularProgressIndicatorPainter oldPainter) {
    return oldPainter.backgroundColor != backgroundColor ||
        oldPainter.valueColor != valueColor ||
        oldPainter.value != value ||
        oldPainter.headValue != headValue ||
        oldPainter.tailValue != tailValue ||
        oldPainter.stepValue != stepValue ||
        oldPainter.rotationValue != rotationValue ||
        oldPainter.strokeWidth != strokeWidth;
  }
}
