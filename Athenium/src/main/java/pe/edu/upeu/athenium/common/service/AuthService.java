package pe.edu.upeu.athenium.common.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.common.dto.SessionManager;

import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "athenium.auth.enabled", havingValue = "true", matchIfMissing = false)
public class AuthService {

    public enum Role { NONE, STUDENT, TEACHER, LIBRARIAN, ADMIN }

    private final Map<Role, Map<String, Object>> roleMap = new HashMap<>();

    public AuthService() {
        // Usar HashMap para permitir valores null y evitar Map.of que lanza NPE si algún valor es null
        Map<String, Object> none = new HashMap<>();
        none.put("id", null);
        none.put("name", "Público");
        none.put("role", "NONE");
        roleMap.put(Role.NONE, none);

        Map<String, Object> student = new HashMap<>();
        student.put("id", "u-123");
        student.put("name", "Ana Torres");
        student.put("role", "STUDENT");
        roleMap.put(Role.STUDENT, student);

        Map<String, Object> teacher = new HashMap<>();
        teacher.put("id", "u-456");
        teacher.put("name", "Dr. Carlos Ruiz");
        teacher.put("role", "TEACHER");
        roleMap.put(Role.TEACHER, teacher);

        Map<String, Object> librarian = new HashMap<>();
        librarian.put("id", "u-789");
        librarian.put("name", "Laura Gómez");
        librarian.put("role", "LIBRARIAN");
        roleMap.put(Role.LIBRARIAN, librarian);

        Map<String, Object> admin = new HashMap<>();
        admin.put("id", "u-001");
        admin.put("name", "Admin General");
        admin.put("role", "ADMIN");
        roleMap.put(Role.ADMIN, admin);
    }

    public void login(Role r) {
        Map<String, Object> data = roleMap.getOrDefault(r, roleMap.get(Role.NONE));
        // Evitamos setear userId porque SessionManager.userId es Long y nuestros ids son strings; solo seteamos nombre y perfil
        SessionManager.getInstance().setUserId(null);
        SessionManager.getInstance().setUserName((String) data.get("name"));
        SessionManager.getInstance().setUserPerfil((String) data.get("role"));
    }

    public void logout() {
        SessionManager.getInstance().setUserId(null);
        SessionManager.getInstance().setUserName(null);
        SessionManager.getInstance().setUserPerfil(null);
    }

}
