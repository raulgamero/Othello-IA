package edu.upc.epsevg.prop.othello.players.DaleBo;

import edu.upc.epsevg.prop.othello.CellType;
import edu.upc.epsevg.prop.othello.GameStatus;
import edu.upc.epsevg.prop.othello.IAuto;
import edu.upc.epsevg.prop.othello.IPlayer;
import edu.upc.epsevg.prop.othello.Move;
import edu.upc.epsevg.prop.othello.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import edu.upc.epsevg.prop.othello.players.DaleBo.Pair;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author raulg i pere
 */
public class PlayerID implements IPlayer, IAuto {
    String name;
    private int millorHeuristica;
    private int contJugades = 0;
    private boolean fin = false;

    /**
     * Constructor del PlayerID
     *
     * @param name Nom del jugador.
     */
    public PlayerID(String name) {
        this.name = name;
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que ha de posar.
     * Ho fa mitjançant el IDS (Iterative Depeening)
     * @param s estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public Move move(GameStatus s) {
        
        ArrayList<Point> moves =  s.getMoves();
        Pair bestmoviment = new Pair(-1000000, null);
        
        millorHeuristica = -1000000;
        fin = false;
        int prof = 1;
        
        // cridem el minimax per a que ens retorni un moviment, anirem incrementant la profunditat fins que no salti el timeout
        while(!fin){
            Pair q = miniMax(s, prof);
            
            if(q.heuristica > bestmoviment.heuristica){
                bestmoviment = q;
            }
            prof+=1;            
        }
        return new Move( bestmoviment.punt, contJugades, prof, SearchType.MINIMAX_IDS);
    }
    
    /**
     * Funcio on començem el calcul del algoritme Min i Max per trobar la millor heuristica.
     * @param s el estat del joc.
     * @param prof numero de nodes als que baixarem per predir els moviments (profunditat del minimax).
     * @return el millor moviment depenent de la heuristica que haguem calculat.
     */
    private Pair miniMax(GameStatus s, int prof) {
        int millorMov = 0;
        int alpha = -1000;
        int beta = 1000;
        
        ArrayList<Point> moves = s.getMoves();

        // per cada moviment possible
        for (int i = 0; i < moves.size(); i++) {
            if (fin) break;
            // creem un game status auxiliar i li afegim la nova tirada
            GameStatus sAux = new GameStatus(s);
            sAux.movePiece(moves.get(i));
            // començem el min/max per el min amb profunditat -1 ja que ja hem fet una tirada
            int valorNou = min(sAux, prof-1, alpha, beta);
            // en cas de que la nova heuristica sigui millor que la anterior, actualitzarem la millor heuristica i el millor moviment
            if(valorNou > millorHeuristica){
                millorHeuristica = valorNou;
                millorMov = i;
            }
        }
        Point punto = moves.get(millorMov);
        Pair moviment = new Pair(millorHeuristica, punto);
        return moviment;
    }
    
    /**
     * Funcio que calcula la menor heuristica del seus nodes fills.
     * @param sAux Estat de joc auxiliar on s'ha afegit una nova tirada.
     * @param profunditat numero de nodes als que baixarem per predir els moviments (profunditat del minimax).
     * @param alpha Paramatre per la poda alpha-beta.
     * @param beta Paramatre per la poda alpha-beta.
     * @return La menor heuristica que ha calculat.
     */
    private int min(GameStatus sAux, int profunditat, int alpha, int beta) {
        // si la tirada realitzada resulta ser una solucio, tornem un valor molt alt per dir que hem guanyat la jugada i sumem 1 al numero de jugades
        if (sAux.checkGameOver()) {
            contJugades = contJugades + 1;
            return 100000;
            
        //si no es solucio i hem arribat a la profunditat 0 o ja no tenim mes opcions de tirada, sumarem 1 al numero de jugades i retornarem l'heuristica de la tirada.
        } else if (profunditat == 0 || (sAux.getMoves().isEmpty())) {
            contJugades = contJugades + 1;
            return heuristica(sAux, sAux.getCurrentPlayer());
        }
        
        int minValue = 10000;   
        
        ArrayList<Point> moves = sAux.getMoves();

        // per cada moviment possible
        for (int i = 0; i < moves.size(); i++) {
            if(fin) break;
            // creem un nou tauler auxiliar i li afegim la nova tirada (del rival)
            GameStatus tMin = new GameStatus(sAux);
            tMin.movePiece(moves.get(i));
            // si el max ens retorna un valor mes petit que el que ja tenim en el min, actualitzem aquest valor.
            minValue = Math.min(max(tMin, profunditat-1, alpha, beta), minValue);
            // calculem la beta entre el nou min_value i la beta que ja teniem
            beta = Math.min(beta, minValue);
            // si fem la poda alpha-beta i beta es menor a alpha, no fa falta mirar mes nodes
            //System.out.println(alpha + "  beta:" + beta);
            if (alpha >= beta) break;
        }
        
        return minValue;
    }
    
    /**
     * Funcio que calcula la menor heuristica del seus nodes fills.
     * @param sAux Estat de joc auxiliar on s'ha afegit una nova tirada.
     * @param profunditat numero de nodes als que baixarem per predir els moviments (profunditat del minimax).
     * @param alpha Paramatre per la poda alpha-beta.
     * @param beta Paramatre per la poda alpha-beta.
     * @return La menor heuristica que ha calculat.
     */
    private int max(GameStatus sAux, int profunditat, int alpha, int beta) {
        // si la tirada realitzada resulta ser una solucio, tornem un valor molt alt per dir que hem guanyat la jugada i sumem 1 al numero de jugades
        if (sAux.checkGameOver()) {
            contJugades = contJugades + 1;
            return -1000000000;
            
        //si no es solucio i hem arribat a la profunditat 0 o ja no tenim mes opcions de tirada, sumarem 1 al numero de jugades i retornarem l'heuristica de la tirada.
        } else if (profunditat == 0 || (sAux.getMoves().isEmpty())) {
            contJugades = contJugades + 1;
            return heuristica(sAux, sAux.getCurrentPlayer());
        }
        
        int maxValue = -10000;
        
        ArrayList<Point> moves = sAux.getMoves();

        // per cada moviment possible
        for (int i = 0; i < moves.size(); i++) {
            if(fin) break;
            // creem un nou tauler auxiliar i li afegim la nova tirada (del rival)
            GameStatus tMax = new GameStatus(sAux);
            tMax.movePiece(moves.get(i));
            // si el max ens retorna un valor mes petit que el que ja tenim en el min, actualitzem aquest valor.
            maxValue = Math.max(min(tMax, profunditat-1, alpha, beta), maxValue);
            // calculem la beta entre el nou min_value i la beta que ja teniem
            alpha = Math.max(alpha, maxValue);
            // si fem la poda alpha-beta i beta es menor a alpha, no fa falta mirar mes nodes
            //System.out.println(alpha + "  beta:" + beta);
            if (alpha >= beta) break;
        }
        
        return maxValue;
    }
    
    /**
     * Funcio que calcula l'heuristica de coin parity de la tirada.
     * @param t esta de joc on s'ha de calcular l'heuristica de la tirada.
     * @param player jugador del que calcularem l'heurísitca.
     * @return L'heuristica coin parity calculada de la tirada.
     */
    public int coin_parity(GameStatus t, CellType player) {
        int player_coins = t.getScore(player);
        int enemy_coins = t.getScore(CellType.opposite(player));
        
        return 25 * (player_coins - enemy_coins) / (player_coins + enemy_coins);
    }
    
    /**
     * Funcio que calcula l'heuristica corners_gotcha de la tirada.
     * @param Max_player_corners cantonades capturades pel jugador max.
     * @param Min_player_corners cantonades capturades pel jugador min.
     * @return L'heuristica corners_gotcha calculada de la tirada.
     */
    public int corners_gotcha(int Max_player_corners, int Min_player_corners){
        if (Max_player_corners + Min_player_corners != 0){
            return 500 * (Max_player_corners - Min_player_corners) / (Max_player_corners + Min_player_corners);
        }
        else
            return 0;
    }
    
    /**
     * Funcio que calcula l'heuristica mobilty de la tirada.
     * @param t esta de joc on s'ha de calcular l'heuristica de la tirada.
     * @param player jugador que tira.
     * @return L'heuristica mobilty calculada de la tirada.
     */
    public int mobilty(GameStatus t, CellType player) {
        int player_moves = t.getMoves().size();
        
        Auxiliar a = new Auxiliar(t);
        a.transformEnemy(CellType.opposite(player));
        int enemy_moves = a.getMoves().size();
        
        if (player_moves + enemy_moves != 0)
	return 100 * (player_moves - enemy_moves) / (player_moves + enemy_moves);
        else return 0;
    }
    
    /**
     * Funcio que calcula l'heuristica mobility
     * @param t Tauler on s'ha de calcular l'heuristica de la tirada.
     * @param player El jugador concret
     * @return L'heuristica general recollida
     */
    public int heuristica(GameStatus t, CellType player) {
        
        int heu = 0;
        int size = t.getSize();
        
        int player_corners = 0, enemy_corners = 0;
        
        // Comprobamos esquinas
        if (t.getPos(0,0) != CellType.EMPTY) {
            if (t.getPos(0,0) == player) player_corners += 1;
            else enemy_corners += 1;
        }
        if (t.getPos(0, size-1) != CellType.EMPTY) {
            if (t.getPos(0,size-1) == player) player_corners += 1;
            else enemy_corners += 1;
        }
        if (t.getPos(size-1, size-1)  != CellType.EMPTY) {
            if (t.getPos(size-1, size-1) == player) player_corners += 1;
            else enemy_corners += 1;
        }
        if (t.getPos(size-1,0)  != CellType.EMPTY) {
            if (t.getPos(size-1,0) == player) player_corners += 1;
            else enemy_corners += 1;
        }
        
        heu += corners_gotcha(player_corners, enemy_corners);
        heu += coin_parity(t, player);
        heu += mobilty(t, player);
        
        return heu;
    }
    
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     * En aquest cas posem el boolea fin a true, el que indicarà a les funcions que iteren els nivells que han de parar.
     */
    @Override
    public void timeout() {
        fin = true;
    }

    /**
     * Retorna el nom del jugador que s'utlilitza per visualització a la UI
     *
     * @return Nom del jugador
     */
    @Override
    public String getName() {
        return name;
    }
}