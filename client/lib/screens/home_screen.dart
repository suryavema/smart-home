import 'dart:convert';
import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/material.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  bool _isConnected = false;
  List<bool> _switches = <bool>[false, false, false, false, false];
  Socket? socket;
  final _ipController = TextEditingController();
  String _ip = "10.0.2.2";
  @override
  void dispose() {
    socket?.destroy();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Switch control"),
        actions: [
          IconButton(
              onPressed: () {
                showDialog<void>(
                  context: context,
                  builder: (BuildContext context) {
                    return AlertDialog(
                      title: Text("Change IP Adress"),
                      content: SizedBox(
                        width: double.infinity,
                        child: TextField(
                          controller: _ipController,
                          decoration: const InputDecoration(
                            border: OutlineInputBorder(),
                            hintText: 'IP adress',
                          ),
                        ),
                      ),
                      actions: [
                        TextButton(
                                child: const Text('Submit'),
                                onPressed: () {
                                  setState(() {
                                    _ip = _ipController.text;
                                    // _isConnected = false;
                                  });
                                  Navigator.of(context).pop();
                                },
                              ),
                          TextButton(
                                child: const Text('Cancel'),
                                onPressed: () {
                                  Navigator.of(context).pop();
                                },
                              ),
                      ],
                    );
                  },
                );
              },
              icon: const Icon(Icons.settings))
        ],
      ),
      body: _isConnected
          ? Container(
            padding: const EdgeInsets.all(20),
              child: ListView(
              children: [
                ListTile(
                  tileColor: Theme.of(context).colorScheme.primaryContainer,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                  title: const Text("Light 1"),
                  trailing: Switch(
                    value: _switches[0],
                    onChanged: (value) {
                      if (value) {
                        socket?.writeln("CMD 0 ON");
                      } else {
                        socket?.writeln("CMD 0 OFF");
                      }
                    },
                  ),
                ),
                const SizedBox(
                  height: 20,
                ),
                ListTile(
                  tileColor: Theme.of(context).colorScheme.primaryContainer,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                  title: const Text("Light 2"),
                  trailing: Switch(
                    value: _switches[1],
                    onChanged: (value) {
                      if (value) {
                        socket?.writeln("CMD 1 ON");
                      } else {
                        socket?.writeln("CMD 1 OFF");
                      }
                    },
                  ),
                ),
                const SizedBox(
                  height: 20,
                ),
                ListTile(
                  tileColor: Theme.of(context).colorScheme.primaryContainer,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                  title: const Text("Light 3"),
                  trailing: Switch(
                    value: _switches[2],
                    onChanged: (value) {
                      if (value) {
                        socket?.writeln("CMD 2 ON");
                      } else {
                        socket?.writeln("CMD 2 OFF");
                      }
                    },
                  ),
                ),
                const SizedBox(
                  height: 20,
                ),
                ListTile(
                  tileColor: Theme.of(context).colorScheme.primaryContainer,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                  title: const Text("Light 4"),
                  trailing: Switch(
                    value: _switches[3],
                    onChanged: (value) {
                      if (value) {
                        socket?.writeln("CMD 3 ON");
                      } else {
                        socket?.writeln("CMD 3 OFF");
                      }
                    },
                  ),
                ),
                const SizedBox(
                  height: 20,
                ),
                ListTile(
                  tileColor: Theme.of(context).colorScheme.primaryContainer,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
                  title: const Text("Light 5"),
                  trailing: Switch(
                    value: _switches[4],
                    onChanged: (value) {
                      if (value) {
                        socket?.writeln("CMD 4 ON");
                      } else {
                        socket?.writeln("CMD 4 OFF");
                      }
                    },
                  ),
                ),
              ],

            ))
          : const Center(child: CircularProgressIndicator()),
      floatingActionButton: FloatingActionButton(
          onPressed: () async {
            if (_isConnected) {
              if (socket == null) {
                _isConnected = false;
              } else {
                socket!.destroy();
                _isConnected = false;
              }
            } else {
              try {
                socket = await Socket.connect(_ip, 5000);
                socket!.listen(
                  (Uint8List data) {
                    final serverData =
                        String.fromCharCodes(data.getRange(2, data.length));
                    setState(() {
                      _switches = ((json.decode(serverData) as List)
                          .map((i) => i as bool)).toList();
                    });
                    print(_switches);
                  },
                  onError: (error) {
                    socket!.destroy();
                    setState(
                      () {
                        _isConnected = false;
                      },
                    );
                  },
                  onDone: () {
                    socket!.destroy();
                    setState(() {
                      _isConnected = false;
                    });
                  },
                );
                setState(() {
                  socket?.writeln("CMD GET");
                  _isConnected = true;
                });
                // socket?.writelnln("CMD 0 ON \n");
              } catch (error) {
                print(error);
              }
            }
          },
          child: Icon(_isConnected
              ? Icons.signal_cellular_4_bar_rounded
              : Icons.signal_cellular_0_bar)),
    );
  }
}


