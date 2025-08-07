# MyCHEF (in development)

**MyCHEF** is an Android application that connects home cooks (chefs) with users who seek culinary assistance. The app supports dual roles (Chef & User) and enables chefs to offer cooking guidance remotely.

## Project Status

This project is currently under active development. Core functionalities are in progress and subject to change.

## Features (Planned / In Progress)

- Role-based authentication (Chef / User)
- Chef profile creation & switching between roles
- Appointment request system between users and chefs
- Real-time notifications via Firebase Cloud Messaging (FCM)
- Chef status tracking (online/offline)
- Persistent login with last user session
- [Planned] Video call integration for live cooking help

## Tech Stack

- **Java** (Android)
- **Firebase Authentication**
- **Firebase Realtime Database**
- **Firebase Cloud Messaging (FCM)**

## Architecture

- MVVM-inspired modular structure
- Shared user authentication for dual-role accounts
- Realtime database-based messaging and status updates
- Push notifications managed by Firebase Admin SDK

⚠️ **Note**: `serviceAccountKey.json` is excluded from version control for security reasons.
