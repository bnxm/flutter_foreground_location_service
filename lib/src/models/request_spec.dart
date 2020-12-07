import 'dart:convert';

class RequestSpec {
  final Priority priority;
  final Duration interval;
  const RequestSpec({
    this.priority = Priority.balancedPowerAccuracy,
    this.interval = const Duration(seconds: 5),
  });

  Map<String, dynamic> toMap() {
    return {
      'priority': priority.id,
      'interval': interval.inMilliseconds,
    };
  }

  String toJson() => json.encode(toMap());

  @override
  String toString() => 'RequestSpec(priority: $priority, interval: $interval)';

  @override
  bool operator ==(Object o) {
    if (identical(this, o)) return true;

    return o is RequestSpec && o.priority == priority && o.interval == interval;
  }

  @override
  int get hashCode => priority.hashCode ^ interval.hashCode;
}

enum Priority {
  highAccuracy,
  balancedPowerAccuracy,
  lowPower,
  noPower,
}

extension PriorityExtensions on Priority {
  String get id {
    switch (this) {
      case Priority.highAccuracy:
        return 'high';
      case Priority.balancedPowerAccuracy:
        return 'balanced';
      case Priority.lowPower:
        return 'low_power';
      case Priority.noPower:
        return 'no_power';
    }
  }
}
