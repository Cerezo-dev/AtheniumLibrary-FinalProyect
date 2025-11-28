package pe.edu.upeu.syslibrary.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pe.edu.upeu.syslibrary.model.Prestamo;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarNotificacionPrestamo(Prestamo prestamo) {
        String destinatario = prestamo.getUsuario().getEmail();
        String asunto = "Confirmación de Préstamo - SysLibrary";

        // Formatear la fecha para que se vea bien (ej. 28-11-2025)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String fechaDev = prestamo.getFechaDevolucion().format(formatter);

        // Construir el mensaje HTML
        String contenidoHtml = """
            <html>
            <body>
                <h2 style="color: #2ecc71;">¡Préstamo Exitoso!</h2>
                <p>Hola <b>%s</b>,</p>
                <p>Tu solicitud de préstamo ha sido procesada correctamente. Aquí están los detalles:</p>
                
                <div style="background-color: #f9f9f9; padding: 15px; border-radius: 5px; border-left: 5px solid #2ecc71;">
                    <p>📚 <b>Libro:</b> %s</p>
                    <p>📅 <b>Fecha Límite de Devolución:</b> %s</p>
                    <p>🔖 <b>Código de Ejemplar:</b> %s</p>
                </div>

                <p style="color: #e74c3c;">⚠️ Recuerda devolverlo a tiempo para evitar sanciones.</p>
                <br>
                <p>Atentamente,<br>Equipo de Biblioteca Virtual</p>
            </body>
            </html>
            """.formatted(
                prestamo.getUsuario().getNombre(),
                prestamo.getEjemplar().getLibro().getTitulo(),
                fechaDev,
                prestamo.getEjemplar().getCodigo()
        );

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenidoHtml, true); // true indica que es HTML

            mailSender.send(message);
            System.out.println("Correo enviado a: " + destinatario);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Error enviando correo: " + e.getMessage());
        }
    }
}