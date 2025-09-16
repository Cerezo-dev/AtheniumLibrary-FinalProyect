package pe.edu.upeu.library.crudlibrary.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum Carrera {
    SISTEMAS(Facultad.FIA, "Sistemas"),
    CIVIL(Facultad.FIA, "Civil"),
    AMBIENTAL(Facultad.FIA, "Ambiental"),
    ARQUITECTURA(Facultad.FIA, "Arquitetura"),
    ALIMENTARIAS(Facultad.FIA, "Alimentaria"),

    ADMINISTRACION(Facultad.FCE, "Administracion"),
    CONTABILIDAD(Facultad.FCE, "Contabilidad"),

    NUTRICION(Facultad.FCS, "Nutricion"),
    ENFERMERIA(Facultad.FCS, "Enfermeria"),
    PSICOLOGIA(Facultad.FCS, "Psicologia"),


    EDUCACIONINICIAL(Facultad.FACIHED, "EducacionInicial"),
    EDUCACIONPRIMARIA(Facultad.FACIHED, "EducacionPrimaria"),
    EDUCACIONINGLES(Facultad.FACIHED, "EducacionIngles"),

    GENERAL(Facultad.GENERAL, "General");

    private Facultad facultad;
    private String descripcion;

    @Override
    public String toString() {
        return descripcion;
    }
}
