package pe.edu.upeu.athenium.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import pe.edu.upeu.athenium.model.Prestamo;

import java.io.File;
import java.sql.SQLException;

public interface IPrestamoService extends ICrudGenericoService<Prestamo,Long>{

    File getFile(String filex);
    JasperPrint runReport(Long idv) throws JRException, SQLException;

}
