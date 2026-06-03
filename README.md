# File Integrity Monitor

A Java-based file integrity monitoring system that detects 
unauthorized file modifications, deletions, and additions 
in real time using SHA-256 cryptographic hashing.

## Features
- SHA-256 cryptographic file hashing
- Persistent baseline snapshot storage
- Real time change detection
- Color coded console alerts
- Desktop popup notifications
- Timestamped alert logging
- Modern dark UI with FlatLaf

## Technologies
- Java 21
- Java Swing + FlatLaf
- SHA-256 (java.security)
- Object Serialization

## How It Works
1. Select a folder to monitor
2. Create a baseline snapshot
3. Run integrity checks to detect changes
4. Alerts display modified, deleted, or added files

## Security Concepts Demonstrated
- File integrity monitoring (FIM)
- Host-based intrusion detection (HIDS)
- Cryptographic hashing
- Audit logging
- Incident detection
