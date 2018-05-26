package com.agrovizion.jonnathan.agrovizion_monitoreo_de_recorridos_agrovizion;

/**
 * Created by jonnathan on 9/11/17.
 */

public class Criticalpoint {

    private int estadoVisitado;
    private String titulo;
    private String imagen;

    public Criticalpoint() {}

    public Criticalpoint(int estado, String titulo, String imagen) {
        this.estadoVisitado = estado;
        this.titulo = titulo;
        this.imagen = imagen;
    }

    public int getEstadoVisitado(){
        return this.estadoVisitado;
    }
    public String getTitulo(){
        return this.titulo;
    }
    public String getImagen(){
        return this.imagen;
    }

    public void setEstadoVisitado(int e) {
        this.estadoVisitado = e;
    }

    public void setTitulo(String t) {
        this.titulo = t;
    }

    public void setImagen(String i) {
        this.imagen = i;
    }
}
