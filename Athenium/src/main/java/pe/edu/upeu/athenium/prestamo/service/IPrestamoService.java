package pe.edu.upeu.athenium.prestamo.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import pe.edu.upeu.athenium.prestamo.entity.Prestamo;
import pe.edu.upeu.athenium.common.service.ICrudGenericoService;

import java.io.File;
import java.sql.SQLException;

public interface IPrestamoService extends ICrudGenericoService<Prestamo,Long> {

    File getFile(String filex);
    JasperPrint runReport(Long idv) throws JRException, SQLException;

}
