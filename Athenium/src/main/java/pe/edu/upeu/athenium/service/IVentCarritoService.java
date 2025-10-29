package pe.edu.upeu.athenium.service;


import pe.edu.upeu.athenium.model.VentCarrito;

import java.util.List;

public interface IVentCarritoService extends  ICrudGenericoService<VentCarrito,Long>{
    List<VentCarrito> listaCarritoCliente(String dni);
    void deleteCarAll(String dniruc);
}
