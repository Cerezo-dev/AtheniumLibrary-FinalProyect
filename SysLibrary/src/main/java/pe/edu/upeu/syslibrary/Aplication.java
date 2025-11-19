// java
// Archivo: `SysLibrary/src/main/java/pe/edu/upeu/syslibrary/Aplication.java`
package pe.edu.upeu.syslibrary;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Aplication {
    public static void main(String[] args) {
        try {
            Path dataDir = Paths.get("data");
            Path logsDir = Paths.get("logs");
            Files.createDirectories(dataDir);
            Files.createDirectories(logsDir);
            System.out.println("Directorios verificados/creados: " + dataDir.toAbsolutePath() + ", " + logsDir.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error creando directorios: " + e.getMessage());
            // Opcional: detener la aplicación si es crítico
            // System.exit(1);
        }
        SysLibraryApplication.main(args);
    }
}
