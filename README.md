app/src/main/java/com/example/app
├── data
│   ├── api
│   │   ├── ApiClient.java          // Cả hai: Cấu hình Retrofit chung
│   │   ├── ApiService.java         // Cả hai: Interface cho tất cả API endpoints
│   │   └── AuthInterceptor.java    // Việt Anh: Thêm JWT token vào header
│   ├── model
│   │   ├── LoginRequest.java       // Việt Anh: Model cho đăng nhập
│   │   ├── LoginResponse.java      // Việt Anh: Model cho đăng nhập
│   │   ├── RegisterRequest.java    // Việt Anh: Model cho đăng ký
│   │   ├── RegisterResponse.java   // Việt Anh: Model cho đăng ký
│   │   ├── User.java               // Việt Anh: Model cho người dùng
│   │   ├── History.java            // Chiến: Model cho lịch sử
│   │   ├── Warning.java            // Chiến: Model cho cảnh báo
│   │   └── Mode.java               // Chiến: Model cho trạng thái Pi
│   └── repository
│       ├── AuthRepository.java     // Việt Anh: Đăng nhập/đăng ký
│       ├── PeopleRepository.java   // Việt Anh: Quản lý người dùng
│       ├── HistoryRepository.java  // Chiến: Lịch sử
│       ├── WarningRepository.java  // Chiến: Cảnh báo
│       └── ModeRepository.java     // Chiến: Trạng thái Pi
├── ui
│   ├── auth                        // Việt Anh: Đăng nhập/đăng ký
│   │   ├── LoginActivity.java      // Việt Anh
│   │   ├── RegisterActivity.java   // Việt Anh
│   │   └── SplashActivity.java     // Việt Anh
│   ├── people                      // Việt Anh: Quản lý người dùng
│   │   ├── PeopleManagementFragment.java // Việt Anh
│   │   ├── AddEditPersonFragment.java    // Việt Anh
│   │   └── PeopleAdapter.java            // Việt Anh
│   ├── dashboard                   // Chiến: Dashboard
│   │   ├── MainDashboardActivity.java    // Chiến
│   │   └── MainDashboardFragment.java    // Chiến
│   ├── history                     // Chiến: Lịch sử
│   │   ├── HistoryFragment.java    // Chiến
│   │   └── HistoryAdapter.java     // Chiến
│   └── warnings                    // Chiến: Cảnh báo
│       ├── WarningsFragment.java   // Chiến
│       └── WarningsAdapter.java    // Chiến
├── viewmodel
│   ├── AuthViewModel.java          // Việt Anh: Đăng nhập/đăng ký
│   ├── PeopleViewModel.java        // Việt Anh: Quản lý người dùng
│   ├── ModeViewModel.java          // Chiến: Trạng thái Pi
│   ├── HistoryViewModel.java       // Chiến: Lịch sử
│   └── WarningViewModel.java       // Chiến: Cảnh báo
├── service
│   └── FCMService.java             // Chiến: FCM
└── util                            // Cả hai: Tiện ích chung
    ├── Constants.java              // Cả hai: Hằng số
    ├── NetworkUtils.java           // Cả hai: Kiểm tra mạng
    ├── ImageUtils.java             // Cả hai: Xử lý ảnh
    └── SharedPrefsUtils.java       // Cả hai: Quản lý token



app/src/main/res
├── layout
│   ├── activity_splash.xml         // Việt Anh: Splash
│   ├── activity_login.xml          // Việt Anh: Login
│   ├── activity_register.xml       // Việt Anh: Register
│   ├── activity_main_dashboard.xml // Chiến: Dashboard
│   ├── fragment_people_management.xml // Việt Anh: Quản lý người dùng
│   ├── fragment_add_edit_person.xml   // Việt Anh: Thêm/sửa người dùng
│   ├── fragment_history.xml        // Chiến: Lịch sử
│   ├── fragment_warnings.xml       // Chiến: Cảnh báo
│   ├── item_person.xml             // Việt Anh: Item người dùng
│   ├── item_history.xml            // Chiến: Item lịch sử
│   └── item_warning.xml            // Chiến: Item cảnh báo
├── drawable
│   ├── ic_logo.png                 // Cả hai: Logo
│   └── ...                         // Cả hai: Resource ảnh
├── values
│   ├── strings.xml                 // Cả hai: Chuỗi
│   ├── colors.xml                  // Cả hai: Màu sắc
│   └── styles.xml                  // Cả hai: Style
└── navigation
    └── nav_graph.xml               // Việt Anh: Navigation component





