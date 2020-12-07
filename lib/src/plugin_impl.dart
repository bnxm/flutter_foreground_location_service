import 'dart:async';

import 'package:flutter/services.dart';

import 'models/models.dart';
import 'plugin.dart';

class BackgroundLocationImpl implements BackgroundLocation {
  BackgroundLocationImpl._() {
    _channel.setMethodCallHandler(_onMethodCall);
  }

  static BackgroundLocationImpl? _instance;
  factory BackgroundLocationImpl() => _instance ??= BackgroundLocationImpl._();

  static const MethodChannel _channel = MethodChannel('background_location');

  final StreamController<Location> _controller = StreamController.broadcast();

  bool _isRunning = false;
  NotificationSpec _notificationSpec = const NotificationSpec();

  @override
  Future<PermissionStatus> getPermissionStatus() async {
    final selected = (await _channel.invokeMethod('permission_status')) ?? '';

    return PermissionStatus.values.firstWhere(
      (status) => status.id == selected,
      orElse: () => PermissionStatus.denied,
    );
  }

  @override
  Future<PermissionStatus> requestPermission() async {
    final status = await getPermissionStatus();
    if (status.isGrantedWhenInUse) return status;

    await _channel.invokeMethod('request_permission');

    return getPermissionStatus();
  }

  @override
  Future<ServiceStatus> getServiceStatus() async {
    final selected = (await _channel.invokeMethod('is_service_enabled')) ?? '';

    return ServiceStatus.values.firstWhere(
      (status) => status.id == selected,
      orElse: () => ServiceStatus.disabled,
    );
  }

  @override
  Future<ServiceStatus> requestService() async {
    final status = await getServiceStatus();
    if (status.isEnabled) return status;

    await _channel.invokeMethod('request_service');

    return getServiceStatus();
  }

  @override
  bool get isRunning => _isRunning;

  @override
  Future<bool> start({RequestSpec spec = const RequestSpec()}) async {
    if (_isRunning) return false;

    // Request the permission if it hasn't been given
    // before
    if (await getPermissionStatus().isDenied) {
      if ((await requestPermission()).isDenied) {
        return false;
      }
    }

    print(await getServiceStatus());

    // Request the service if it is disabled
    if (await getServiceStatus().isDisabled) {
      if ((await requestService()).isDisabled) {
        return false;
      }
    }

    await _channel.invokeMethod('start', spec.toJson());

    return _isRunning = true;
  }

  @override
  Future<void> stop() async {
    if (!_isRunning) return;

    await _channel.invokeMethod('stop');
    _isRunning = false;
  }

  @override
  Stream<Location> get stream => _controller.stream;

  @override
  Future<void> setNotification(NotificationSpec spec) async {
    _notificationSpec = spec;
    await _channel.invokeMethod('notification', spec.toJson());
  }

  Future<dynamic> _onMethodCall(MethodCall call) async {
    final args = call.arguments;

    switch (call.method) {
      case 'location':
        _onLocationReceived(args);
        break;
      case 'action':
        _onActionPressed(args);
        break;
    }

    return true;
  }

  void _onLocationReceived(String args) {
    final location = Location.fromJson(args);
    _controller.add(location);
  }

  void _onActionPressed(String name) {
    for (final action in _notificationSpec.actions) {
      if (action.name == name) {
        action.fn();
      }
    }
  }
}
