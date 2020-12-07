enum PermissionStatus {
  alwaysGranted,
  grantedWhenInUse,
  denied,
  permanentlyDenied,
}

extension PermissionStatusExtensions on PermissionStatus {
  bool get isAlwaysGranted => this == PermissionStatus.alwaysGranted;
  bool get isGrantedWhenInUse =>
      isAlwaysGranted || this == PermissionStatus.grantedWhenInUse;
  bool get isDenied => isPermanentlyDenied || this == PermissionStatus.denied;
  bool get isPermanentlyDenied => this == PermissionStatus.permanentlyDenied;

  String get id {
    switch (this) {
      case PermissionStatus.alwaysGranted:
        return 'always_granted';
      case PermissionStatus.grantedWhenInUse:
        return 'granted_in_use';
      case PermissionStatus.denied:
        return 'denied';
      case PermissionStatus.permanentlyDenied:
        return 'permanently_denied';
    }
  }
}

extension FuturePermissionStatusExtensions on Future<PermissionStatus> {
  Future<bool> get isAlwaysGranted async =>
      (await this) == PermissionStatus.alwaysGranted;
  Future<bool> get isGrantedWhenInUse async =>
      (await this) == PermissionStatus.grantedWhenInUse;
  Future<bool> get isDenied async => (await this) == PermissionStatus.denied;
  Future<bool> get isPermanentlyDenied async =>
      (await this) == PermissionStatus.permanentlyDenied;
}
