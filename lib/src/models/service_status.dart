enum ServiceStatus {
  gps,
  network,
  disabled,
}

extension ServiceStatusExtensions on ServiceStatus {
  bool get isGPS => this == ServiceStatus.gps;
  bool get isNetwork => this == ServiceStatus.network;
  bool get isEnabled => isGPS || isNetwork;
  bool get isDisabled => this == ServiceStatus.disabled;

  String get id {
    switch (this) {
      case ServiceStatus.gps:
        return 'gps';
      case ServiceStatus.network:
        return 'network';
      case ServiceStatus.disabled:
        return 'disabled';
    }
  }
}

extension FutureServiceStatusExtensions on Future<ServiceStatus> {
  Future<bool> get isGPS async => (await this).isGPS;
  Future<bool> get isNetwork async => (await this).isNetwork;
  Future<bool> get isEnabled async => await isGPS || await isNetwork;
  Future<bool> get isDisabled async => (await this).isDisabled;
}
