/*
 * This file is part of Chimera by Arcane Arts.
 *
 * Chimera by Arcane Arts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Chimera by Arcane Arts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License in this package for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Chimera.  If not, see <https://www.gnu.org/licenses/>.
 */

import 'package:flutter/material.dart';
import 'package:hawkeye/chimera/protocol.dart';
import 'package:hawkeye/util/l.dart';
import 'package:hawkeye/util/loader.dart';
import 'package:hawkeye/widget/quick_animator.dart';

typedef Widget RippleReactionBuilder<T>(BuildContext context, T data);
typedef Widget RippleLoadingBuilder(BuildContext context);
typedef Future<T> RippleFuture<T>();
typedef void RippleReaction();

class RippleNetwork {
  static Map<String, List<RippleListener>> listeners =
      new Map<String, List<RippleListener>>();

  static void unregister(ID tag, ID listener) {
    if (listeners.containsKey(tag.toString())) {
      listeners[tag.toString()]
          .removeWhere((element) => element.id.id == listener.id);

      if (listeners[tag.toString()].isEmpty) {
        _unregisterNetwork(tag);
        listeners.remove(tag.toString());
      }
    }
  }

  static ID register(ID id, RippleListener l) {
    if (!listeners.containsKey(id.toString())) {
      listeners[id.toString()] = List<RippleListener>();
      _registerNetwork(id);
    }

    listeners[id.toString()].add(l);
    return l.id;
  }

  static void ripple(ID id) {
    if (listeners.containsKey(id.toString())) {
      listeners[id.toString()].forEach((l) {
        try {
          l.ripple();
        } catch (e) {
          L.f("Failed to ripple listener ${l.id} which was targeting $id");
          L.f(e.toString());
        }
      });
    } else {
      L.w("<RPL> No active listeners for $id. Unregistering from Ripple Network");
      _unregisterNetwork(id);
    }
  }

  static void _unregisterNetwork(ID id) async {
    L.i("<RPL> Unregistering Network Listener for $id.");
    await ChimeraGateway.unregisterAllWithTarget(id);
  }

  static void _registerNetwork(ID id) async {
    L.i("<RPL> Registering Network Listener for $id.");
    await ChimeraGateway.registerListener(id);
  }
}

class RippleListener {
  final RippleReaction _reaction;
  final ID id = ID.random();

  RippleListener(this._reaction);

  ripple() => _reaction();
}

class RippleBuilder<T> extends StatefulWidget {
  final ID target;
  final RippleLoadingBuilder loadingBuilder;
  final RippleReactionBuilder<T> builder;
  final RippleFuture<T> provider;
  final bool animate;

  RippleBuilder(
      {@required this.target,
      @required this.provider,
      @required this.builder,
      this.animate = true,
      this.loadingBuilder});

  @override
  State<StatefulWidget> createState() {
    return _RippleBuilderState<T>();
  }
}

class _RippleBuilderState<T> extends State<RippleBuilder<T>> {
  ID _listenerID;
  RippleListener _listener;
  Widget _child;

  @override
  void initState() {
    super.initState();
    _child = widget.loadingBuilder != null
        ? widget.loadingBuilder(context)
        : Loading.tiny(context);
    rebuild();
    _listener = RippleListener(() => rebuild());
    _listenerID = RippleNetwork.register(widget.target, _listener);
  }

  Widget enhance(Widget w) {
    if (widget.animate) {
      return Quick.fadeSwitch(
          child: w,
          curveIn: Curves.easeInOutQuad,
          curveOut: Curves.easeInOutQuad,
          durationIn: 544,
          durationOut: 544);
    }

    return w;
  }

  void rebuild() => widget.provider().then((value) =>
      setState(() => _child = enhance(widget.builder(context, value))));

  @override
  void dispose() {
    RippleNetwork.unregister(_listenerID, _listener.id);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return _child;
  }
}
