# ChatApp – Java Socket-Based Chat System (with Swing GUI)

## Overview

This project delivers a lightweight, socket-driven chat platform built on standard Java networking.
It aligns with traditional client–server paradigms while packaging an optional Swing-based GUI for improved usability.
The system supports multi-client communication, real-time message broadcasting, and username-tagged messages.

The implementation intentionally keeps the core architecture unchanged:

* **Server.java** and **ClientHandler.java** manage networking and message routing.
* **Client.java** handles plain terminal-based messaging.
* **ServerGUI.java** and **ClientGUI.java** provide a graphical front-end without rewriting backend logic.

## Features

* Multi-client real-time messaging
* Server-side logging and client list visibility
* GUI for both server and client
* Username-tagged messages for conversation clarity
* Sender sees their own messages (full broadcast loop)
* Terminal clients remain supported
