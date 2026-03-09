# 🎬 Cinema Booking System

> Hệ thống đặt vé xem phim trực tuyến hiện đại với Spring Boot 3

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 👨‍💻 Tác giả

**haivoDev**

---

## 📋 Mục lục

- [Giới thiệu](#-giới-thiệu)
- [Tính năng](#-tính-năng)
- [Công nghệ](#-công-nghệ)
- [Cài đặt](#-cài-đặt)
- [Cấu hình](#-cấu-hình)
- [Deployment](#-deployment)
- [API Documentation](#-api-documentation)
- [License](#-license)

---

## 🎯 Giới thiệu

Cinema Booking System là một ứng dụng web full-stack cho phép người dùng:
- Xem danh sách phim đang chiếu và sắp chiếu từ TMDB API
- Đặt vé xem phim trực tuyến
- Chọn ghế ngồi interactive
- Thanh toán giả lập và nhận mã QR
- Đăng nhập bằng Passkey (WebAuthn)

---

## ✨ Tính năng

### 🎥 Quản lý phim
- ✅ Hiển thị phim từ TMDB API (real-time)
- ✅ Chi tiết phim (poster, backdrop, mô tả, đánh giá, thể loại)
- ✅ Phim đang chiếu & sắp chiếu
- ✅ Tất cả hình ảnh từ TMDB CDN

### 🎟️ Đặt vé
- ✅ Xem lịch chiếu theo rạp và phòng
- ✅ Chọn ghế ngồi interactive (8 hàng x 12 ghế)
- ✅ Hiển thị ghế đã đặt real-time
- ✅ Tính tổng tiền tự động

### 💳 Thanh toán
- ✅ Thanh toán giả lập (MoMo, VNPay, ZaloPay)
- ✅ Tạo mã QR code cho vé
- ✅ Lưu QR code dạng base64 trong database
- ✅ Xem lịch sử đặt vé

### 🔐 Bảo mật
- ✅ Đăng ký/Đăng nhập với email & password
- ✅ Passkey authentication (WebAuthn)
- ✅ Spring Security với BCrypt
- ✅ Session management với Redis
- ✅ CSRF protection

### 👨‍💼 Admin Panel
- ✅ Tài khoản admin tự động tạo
- ✅ Dashboard thống kê
- ✅ Quản lý rạp chiếu, phòng chiếu, lịch chiếu

---

## 🛠️ Công nghệ

### Backend
- **Spring Boot 3.2.0** - Framework chính
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - ORM
- **MySQL** - Database chính
- **Redis** - Session & Cache
- **WebAuthn 2.4.0** - Passkey authentication

### Frontend
- **Thymeleaf** - Template engine
- **Tailwind CSS** - Styling
- **JavaScript** - Interactive features
- **Font Awesome** - Icons

### External APIs
- **TMDB API** - Movie data & images
- **ZXing** - QR code generation

### DevOps
- **Docker** - Containerization
- **Render** - Hosting
- **Railway** - MySQL & Redis hosting

---

## 🚀 Cài đặt

### Yêu cầu
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.0+
- Docker (optional)


### Cài đặt dependencies

```bash
mvn clean install
```

### Chạy ứng dụng

```bash
mvn spring-boot:run
```

Ứng dụng sẽ chạy tại: `http://localhost:8080`

---

## ⚙️ Cấu hình

### Environment Variables

Tạo file `.env` hoặc cấu hình trong `application.yml`:

```properties
# Database
MYSQL_PUBLIC_URL=mysql://user:password@host:port/database

# Redis
REDIS_PUBLIC_URL=redis://default:password@host:port

# TMDB API
TMDB_API_KEY=your_tmdb_bearer_token
TMDB_BASE_URL=https://api.themoviedb.org/3

# Application
APP_DOMAIN=localhost
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

### TMDB API Key

1. Đăng ký tài khoản tại [TMDB](https://www.themoviedb.org/)
2. Vào Settings → API → Create API Key
3. Copy Bearer Token và paste vào `TMDB_API_KEY`

---

## 🐳 Deployment

### Docker

```bash
# Build image
docker build -t cinema-booking .

# Run container
docker run -p 8080:8080 \
  -e MYSQL_PUBLIC_URL=mysql://... \
  -e REDIS_PUBLIC_URL=redis://... \
  -e TMDB_API_KEY=... \
  cinema-booking
```

### Docker Compose

```bash
docker-compose up -d
```

### Render + Railway

1. Fork repository
2. Tạo MySQL & Redis trên Railway
3. Tạo Web Service trên Render
4. Connect repository
5. Cấu hình Environment Variables
6. Deploy!

Chi tiết xem:
- [QUICK_START.md](QUICK_START.md) - Deploy nhanh trong 15 phút
- [DEPLOYMENT.md](DEPLOYMENT.md) - Hướng dẫn chi tiết
- [DOCKER_DEPLOYMENT.md](DOCKER_DEPLOYMENT.md) - Deploy với Docker

---

## 📚 API Documentation

### Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Trang chủ |
| GET | `/movies/{id}` | Chi tiết phim |
| GET | `/register` | Đăng ký |
| GET | `/login` | Đăng nhập |

### Authenticated Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/booking/showtime/{id}` | Chọn ghế |
| POST | `/booking/create` | Tạo booking |
| GET | `/booking/my-bookings` | Vé của tôi |
| GET | `/passkey/settings` | Quản lý Passkey |

### Admin Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin` | Dashboard |
| GET | `/admin/cinemas` | Quản lý rạp |
| GET | `/admin/screens` | Quản lý phòng |
| GET | `/admin/showtimes` | Quản lý lịch chiếu |

---






## 🔐 Passkey Authentication

Ứng dụng hỗ trợ đăng nhập bằng Passkey (WebAuthn):
- Đăng nhập bằng vân tay, Face ID
- An toàn hơn mật khẩu truyền thống
- Không thể bị phishing



---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📧 Contact

**haivoDev**

- GitHub: [haivoDA22TTD](https://github.com/haivoDA22TTD)
- Email: 110122068@st.tvu.edu.vn

---

## 🙏 Acknowledgments

- [TMDB](https://www.themoviedb.org/) - Movie database API
- [Spring Boot](https://spring.io/projects/spring-boot) - Framework
- [Tailwind CSS](https://tailwindcss.com/) - CSS framework
- [Font Awesome](https://fontawesome.com/) - Icons

---

<div align="center">
  <p>Made with ❤️ by haivoDev</p>
  <p>⭐ Star this repo if you like it!</p>
</div>
