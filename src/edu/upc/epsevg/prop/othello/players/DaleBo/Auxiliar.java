package edu.upc.epsevg.prop.othello.players.DaleBo;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
/**
 *
 * @author raulg i pere
 */
public class Auxiliar extends GameStatus{
    
    /**
     * Constructor per còpia d'un GameStatus
     * @param gs Estat actual de joc.
     */
    public Auxiliar(GameStatus gs){
        super(gs);
    }
    
    /**
     * Funció per posar com a jugador actual al pasat per paràmetre.
     * @param a Jugador actual.
     */
    public void transformEnemy(CellType a) {
        this.currentPlayer = a;
    }
}
