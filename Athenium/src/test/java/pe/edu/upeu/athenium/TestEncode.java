package pe.edu.upeu.athenium;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestEncode {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String raw = "123456";
        String hash = encoder.encode(raw);
        System.out.println("Hash: " + hash);
        System.out.println("Matches? " + encoder.matches(raw, hash));
    }
}
