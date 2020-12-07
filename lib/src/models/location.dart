import 'dart:convert';

class Location {
  final double latitude;
  final double longitude;
  final double velocity;
  final double altitude;
  final double bearing;
  final DateTime time;
  const Location({
    required this.latitude,
    required this.longitude,
    required this.velocity,
    required this.altitude,
    required this.bearing,
    required this.time,
  });

  factory Location.fromMap(Map<String, dynamic> map) {
    return Location(
      latitude: map['latitude'] ?? 0.0,
      longitude: map['longitude'] ?? 0.0,
      velocity: map['velocity'] ?? 0.0,
      altitude: map['altitude'] ?? 0.0,
      bearing: map['bearing'] ?? 0.0,
      time: DateTime.fromMillisecondsSinceEpoch(map['time'] ?? 0, isUtc: true).toLocal(),
    );
  }

  factory Location.fromJson(String source) => Location.fromMap(json.decode(source));

  @override
  String toString() {
    return 'Location(latitude: $latitude, longitude: $longitude, velocity: $velocity, altitude: $altitude, bearing: $bearing)';
  }

  @override
  bool operator ==(Object o) {
    if (identical(this, o)) return true;

    return o is Location &&
        o.latitude == latitude &&
        o.longitude == longitude &&
        o.velocity == velocity &&
        o.altitude == altitude &&
        o.bearing == bearing;
  }

  @override
  int get hashCode {
    return latitude.hashCode ^
        longitude.hashCode ^
        velocity.hashCode ^
        altitude.hashCode ^
        bearing.hashCode;
  }
}
