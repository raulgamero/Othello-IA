package edu.upc.epsevg.prop.othello.players.DaleBo;

import java.awt.Point;

/**
 *
 * @author Pere i Raul
 */
// Clase auxiliar que usarem per guardar el moviment juntament amb el seu valor heuristic per tal de poder reassignar en cas de trobar un nou moviment.
public class Pair {
    int heuristica;
    Point punt;
    
    public Pair(int heuristica, Point moviment) {
        this.heuristica = heuristica;
        this.punt = moviment;
    }
}