import 'package:flutter/material.dart';

import 'package:background_location/background_location.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  Location? location;
  int count = 0;

  @override
  void initState() {
    super.initState();

    final plugin = BackgroundLocation();

    plugin.setNotification(
      NotificationSpec(
        title: 'Pending',
        color: Colors.red,
      ),
    );

    plugin.stream.listen((event) {
      setState(() {
        location = event;
        count++;

        plugin.setNotification(
          NotificationSpec(
            title: '${event.latitude}, ${event.longitude}',
            color: Colors.red,
          ),
        );
      });
    });

    plugin.start(
      spec: const RequestSpec(
        priority: Priority.highAccuracy,
        interval: Duration(seconds: 2),
      ),
    );
  }

  void requestPermission() async {
    final status = BackgroundLocation().getPermissionStatus();

    await BackgroundLocation().requestPermission();

    print(status);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text(
            location == null
                ? 'Pending...'
                : '${location?.latitude}, ${location?.longitude}, $count',
          ),
        ),
      ),
    );
  }
}
