import 'models/models.dart';
import 'plugin_impl.dart';

/// An Android ForegroundService.
abstract class BackgroundLocation {
  factory BackgroundLocation() => BackgroundLocationImpl();

  Future<PermissionStatus> getPermissionStatus();
  Future<PermissionStatus> requestPermission();

  Future<ServiceStatus> getServiceStatus();
  Future<ServiceStatus> requestService();

  /// Starts or resumes the location service with the supplied
  /// [RequestSpec].
  Future<void> start({RequestSpec spec = const RequestSpec()});

  /// Destroys the location service.
  ///
  /// You wont receive any more location updates until you call
  /// [start] again.
  Future<void> stop();

  /// Whether the Service is currently running.
  bool get isRunning;

  /// The location update stream.
  Stream<Location> get stream;

  /// Updates the Notification of the ForegroundService
  Future<void> setNotification(NotificationSpec spec);
}
