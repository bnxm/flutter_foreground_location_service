import 'dart:convert';

import 'package:flutter/material.dart';

class NotificationSpec {
  final String title;
  final String text;
  final String summary;
  final String icon;
  final Color color;
  final List<NotificationAction> actions;
  const NotificationSpec({
    this.title = 'Title',
    this.text = 'Body',
    this.summary = '',
    this.icon = 'ic_location',
    this.color = Colors.red,
    this.actions = const [],
  });

  Map<String, dynamic> toMap() {
    return {
      'title': title,
      'text': text,
      'summary': summary,
      'icon': icon,
      'color': '#${color.value.toRadixString(16)}',
      'actions': actions.map((e) => e.toMap()).toList(),
    };
  }

  String toJson() => json.encode(toMap());

  @override
  String toString() {
    return 'NotificationSpec(title: $title, text: $text, summary: $summary, icon: $icon, color: $color)';
  }

  @override
  bool operator ==(Object o) {
    if (identical(this, o)) return true;

    return o is NotificationSpec &&
        o.title == title &&
        o.text == text &&
        o.summary == summary &&
        o.icon == icon &&
        o.color == color;
  }

  @override
  int get hashCode {
    return title.hashCode ^
        text.hashCode ^
        summary.hashCode ^
        icon.hashCode ^
        color.hashCode;
  }
}

class NotificationAction {
  final String name;
  final String icon;
  final Function() fn;
  const NotificationAction({
    required this.name,
    required this.icon,
    required this.fn,
  });

  Map<String, dynamic> toMap() {
    return {
      'name': name,
      'icon': icon,
    };
  }

  @override
  String toString() => 'NotificationAction(name: $name, icon: $icon, onPressed: $fn)';

  @override
  bool operator ==(Object o) {
    if (identical(this, o)) return true;

    return o is NotificationAction && o.name == name && o.icon == icon && o.fn == fn;
  }

  @override
  int get hashCode => name.hashCode ^ icon.hashCode ^ fn.hashCode;
}
