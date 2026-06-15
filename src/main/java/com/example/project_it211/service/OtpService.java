package com.example.project_it211.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Quản lý OTP quên mật khẩu.
 * Lưu OTP trong bộ nhớ (ConcurrentHashMap) — tự hết hạn sau 5 phút.
 * Không cần Redis hay DB riêng, phù hợp cho demo và môi trường thực tế nhỏ.
 */
@Service
public class OtpService {

    // key = email, value = thông tin OTP
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    private static final int OTP_EXPIRY_MINUTES = 5;

    /** Tạo OTP 6 chữ số và lưu vào bộ nhớ */
    public String generateAndStoreOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        otpStore.put(email.toLowerCase(), new OtpEntry(otp, expiryTime));
        return otp;
    }

    /** Xác minh OTP: đúng mã + chưa hết hạn */
    public boolean verifyOtp(String email, String otp) {
        OtpEntry entry = otpStore.get(email.toLowerCase());
        if (entry == null) return false;
        if (LocalDateTime.now().isAfter(entry.expiryTime())) {
            otpStore.remove(email.toLowerCase()); // dọn dẹp OTP hết hạn
            return false;
        }
        return entry.otp().equals(otp);
    }

    /** Xóa OTP sau khi đã dùng */
    public void clearOtp(String email) {
        otpStore.remove(email.toLowerCase());
    }

    /** Kiểm tra có OTP đang chờ không */
    public boolean hasActiveOtp(String email) {
        OtpEntry entry = otpStore.get(email.toLowerCase());
        if (entry == null) return false;
        if (LocalDateTime.now().isAfter(entry.expiryTime())) {
            otpStore.remove(email.toLowerCase());
            return false;
        }
        return true;
    }

    // Record lưu thông tin OTP
    private record OtpEntry(String otp, LocalDateTime expiryTime) {}
}
