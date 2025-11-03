package pe.edu.upeu.athenium.common.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pe.edu.upeu.athenium.common.dto.UsuarioDto;

import java.io.IOException;

public  class ConsultaDNI {

    public UsuarioDto consultarDNI(String dni){

        UsuarioDto usuarioDto =new UsuarioDto();
        String url = "https://eldni.com/pe/buscar-datos-por-dni";
        try {

            // Primero: hacer GET para obtener el _token
            Connection.Response getResponse = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .execute();

            Document doc = getResponse.parse();
            String token = doc.select("input[name=_token]").attr("value");

            // Ahora hacer POST con el DNI y el token
            Connection.Response postResponse = Jsoup.connect(url)
                    .cookies(getResponse.cookies()) // mantener la sesión
                    .data("_token", token)
                    .data("dni", dni)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .execute();
            // Paso 3: parsear respuesta
            Document resultDoc = Jsoup.parse(postResponse.body());
            Element fila = resultDoc.select("table tbody tr").first();
            if (fila != null) {
                Elements celdas = fila.select("td");
                usuarioDto.setDni(celdas.get(0).text());
                usuarioDto.setNombre(celdas.get(1).text());
                usuarioDto.setApellidoPaterno(celdas.get(2).text());
                usuarioDto.setApellidoMaterno(celdas.get(3).text());
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return usuarioDto;
    }

    public static void main(String[] args) {
        ConsultaDNI c=new ConsultaDNI();
        UsuarioDto p=c.consultarDNI("43631917");
        System.out.println(p.getDni()+" "+p.getNombre()
                +"  "+p.getApellidoPaterno()+"  "+p.getApellidoMaterno());
    }

}
