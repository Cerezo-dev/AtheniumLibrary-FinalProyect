package pe.edu.upeu.athenium.common.service.impl;

import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.common.dto.MenuMenuItenTO;
import pe.edu.upeu.athenium.common.service.IMenuMenuItemDao;

import java.util.*;

@Service
public class MenuMenuItemDaoImp implements IMenuMenuItemDao {
    @Override
    public List<MenuMenuItenTO> listaAccesos(String perfil, Properties idioma) {

        List<MenuMenuItenTO> lista = new ArrayList<>();

        // Rutas FXML actualizadas a los archivos que existen en resources/view
        lista.add(new MenuMenuItenTO("mDashboard", "/view/pMenus/mDashboard/mDashboard.fxml", "Principal", "Mi Dashboard", "Dashboard", "T"));
        lista.add(new MenuMenuItenTO("mCatalogo", "/view/mCatalogo.fxml", "Biblioteca", "Catálogo", "Catálogo", "T"));
        lista.add(new MenuMenuItenTO("mPrestamos", "/view/mPrestamos.fxml", "Biblioteca", "Gestionar Préstamos", "Préstamos", "T"));
        lista.add(new MenuMenuItenTO("mUsuarios", "/view/mUsuarios.fxml", "Admin", "Gestionar Usuarios", "Usuarios", "T"));
        lista.add(new MenuMenuItenTO("mReportes", "/view/mReportes.fxml", "Admin", "Reportes", "Reportes", "T"));
        // "Salir" redirige a login.fxml (tipo 'S' para redirección completa)
        lista.add(new MenuMenuItenTO("miprincipal", "/view/pMenus/usr-authAccess/login.fxml", idioma.getProperty("menu.nombre.principal"), idioma.getProperty("menuitem.nombre.salir"), "Salir", "S"));

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
                accesoReal = lista; // Ve todo (los elementos)
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
