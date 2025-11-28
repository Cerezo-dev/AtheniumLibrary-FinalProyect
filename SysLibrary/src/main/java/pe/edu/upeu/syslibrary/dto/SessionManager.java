package pe.edu.upeu.syslibrary.dto;

import lombok.Data;

@Data
public class SessionManager {

    private static SessionManager instance;

    // Campos de datos simples (No entidades completas)
    private Long userId;
    private String userName;
    private String userPerfil; // Ej: "ADMINISTRADOR", "ESTUDIANTE"

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // --- MÉTODOS DE COMPATIBILIDAD ---

    // Este método es necesario porque el MainguiController llama a "getNombrePerfil()"
    // Lo redirigimos para que devuelva el valor de "userPerfil"
    public String getNombrePerfil() {
        return this.userPerfil != null ? this.userPerfil : "INVITADO";
    }

    // Lombok ya genera getUserName(), así que no hace falta crearlo manualmente
}