package pe.edu.upeu.athenium.service.impl;

import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.dto.MenuMenuItenTO;
import pe.edu.upeu.athenium.service.IMenuMenuItemDao;

import java.util.*;

@Service
public class MenuMenuItemDaoImp implements IMenuMenuItemDao {
    @Override
    public List<MenuMenuItenTO> listaAccesos(String perfil, Properties idioma) {

        List<MenuMenuItenTO> lista = new ArrayList<>();

        lista.add(new MenuMenuItenTO("mDashboard", "/view/dashboard-view.fxml", "Principal", "Mi Dashboard", "Menu Principal", "T"));
        lista.add(new MenuMenuItenTO("mCatalogo", "/view/catalogo-view.fxml", "Biblioteca", "Catálogo", "Catalogo de libros", "T"));
        lista.add(new MenuMenuItenTO("mPrestamos", "/view/prestamos-view.fxml", "Biblioteca", "Gestionar Préstamos", "Gestion de prestamos", "T"));
        lista.add(new MenuMenuItenTO("mUsuarios", "/view/usuarios-view.fxml", "Admin", "Gestionar Usuarios", "Menu de usuarios", "T"));
        lista.add(new MenuMenuItenTO("mReportes", "/view/reportes-view.fxml", "Admin", "Reportes", "Reportes", "T"));
        lista.add(new MenuMenuItenTO("miprincipal", "/view/login.fxml", idioma.getProperty("menu.nombre.principal"), idioma.getProperty("menuitem.nombre.salir"),"Salir", "S"));

        List<MenuMenuItenTO> accesoReal = new ArrayList<>();

        accesoReal.add(lista.get(0));

        switch (perfil) {
            case "ESTUDIANTE":
                accesoReal.add(lista.get(0)); // Dashboard
                accesoReal.add(lista.get(1)); // Catálogo
                accesoReal.add(lista.get(5)); // Salir (Índice 5)
                break;

            case "DOCENTE":
                accesoReal.add(lista.get(0)); // Dashboard
                accesoReal.add(lista.get(1)); // Catálogo
                accesoReal.add(lista.get(5)); // Salir (Índice 5)
                break;

            case "BIBLIOTECARIO":
                accesoReal.add(lista.get(0)); // Dashboard (Lo necesita para iniciar)
                accesoReal.add(lista.get(1)); // Catálogo
                accesoReal.add(lista.get(2)); // Gestionar Préstamos
                accesoReal.add(lista.get(3)); // Gestionar Usuarios
                accesoReal.add(lista.get(5)); // Salir (Índice 5)
                break;

            case "ADMINISTRADOR":
                accesoReal = lista; // Ve todo (los 6 elementos)
                break;

            default: // Visitante o usuario sin rol
                accesoReal.add(lista.get(1)); // Solo Catálogo
                accesoReal.add(lista.get(5)); // Salir (Índice 5)
                break;
        }

        return accesoReal;

    }

    @Override
    public Map<String, String[]> accesosAutorizados(List<MenuMenuItenTO> accesos) {

        Map<String, String[]> menuConfig = new HashMap<>();

        for (MenuMenuItenTO menu : accesos) {
            menuConfig.put("mi"+menu.getIdNombreObj(), new String[]{menu.getRutaFile(), menu.getNombreTab(),menu.getTipoTab()});
        }

        return menuConfig;
    }

}
