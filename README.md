# IOT Android Application

## Overview
This Android application provides a user interface for IoT device management and monitoring. The app allows users to login, register, and interact with IoT devices through a dashboard interface.

## Features
- User authentication (login/register)
- Dashboard for device management
- Camera functionality for device scanning/setup
- API integration with IoT backend services

## Technical Details
- **Minimum SDK:** 24 (Android 7.0 Nougat)
- **Target SDK:** 34 (Android 14)
- **Compile SDK:** 35
- **Architecture:** MVVM (Model-View-ViewModel)

## Libraries and Dependencies
- AndroidX and Material Design components
- Retrofit for API communication
- Gson for JSON parsing
- Glide for image loading
- Navigation Component for in-app navigation
- Lifecycle components for MVVM implementation

## Requirements
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 8

## Building the Application
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run the application

## Structure
- `data`: Contains data models, repositories, and API service interfaces
- `ui`: User interface components organized by feature
- `viewmodel`: ViewModels for UI components
- `util`: Utility classes

## License
[Your License Information]
