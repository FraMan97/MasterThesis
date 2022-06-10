
package pollardrhoattack;

import java.math.BigInteger;


/**
 *
 * @author Francesco Mancuso
 */



public class main {

    static final BigInteger P = new BigInteger("23");
    static final BigInteger A = new BigInteger("1");
    static final BigInteger B = new BigInteger("4");
    static final Point G = new Point(new BigInteger("0"), new BigInteger("2"));
    static final BigInteger OG = new BigInteger("29"); 
    static final BigInteger INITIAL_SEED = new BigInteger("17");
    static final int NPUZZLES =  4;
    
    
    private static BigInteger right_exp_mod(BigInteger base, BigInteger exp, BigInteger mod){
        BigInteger result = BigInteger.ONE;
        base = base.mod(mod);
        while(exp.compareTo(BigInteger.ZERO) > 0){
            if (exp.mod(new BigInteger("2")).compareTo(BigInteger.ONE) == 0){
                result = result.multiply(base).mod(mod);
            }
            exp = exp.shiftRight(1);
            base = base.pow(2).mod(mod);
        }
        return result;
    }
    
    private static Point icart_function(BigInteger u){
        BigInteger exp = P.multiply(new BigInteger("2")).subtract(BigInteger.ONE).multiply(new BigInteger("3").modInverse(P)).mod(P);
        BigInteger v = A.multiply(new BigInteger("3")).subtract(u.pow(4)).multiply(new BigInteger("6").multiply(u).modInverse(P));
        BigInteger x = v.pow(2).subtract(B).subtract(u.pow(6).multiply(new BigInteger("27").modInverse(P)));
        x = right_exp_mod(x, exp, P);
        x = x.add(u.pow(2).multiply(new BigInteger("3").modInverse(P))).mod(P);    
        BigInteger y = u.multiply(x).add(v).mod(P).add(P).mod(P); 
        return new Point(x, y);
    }

    public static void main(String[] args) throws InterruptedException {        
        PollardRhoAttack pra;
        PollardRhoAttack.executionTime = 0;
        BigInteger nextSeed = INITIAL_SEED;
        
        System.out.println("Pollard's Rho attack");
        System.out.println("Curve E(" + A + "," + B + ")");
        System.out.println("Prime P: " + P);
        System.out.print("Base point G: ");
        G.print_point();
        System.out.println("--------------------------------------");
     
        for(BigInteger i = BigInteger.ZERO; i.compareTo(BigInteger.valueOf(NPUZZLES)) < 0; i = i.add(BigInteger.ONE)){         
            System.out.println("Puzzle: " + i.add(BigInteger.ONE));
            Point q = icart_function(nextSeed);     
            System.out.print("Public key: " );
            q.print_point();
            pra = new PollardRhoAttack(G, OG, q, A, B, P);
            nextSeed = pra.pollardMethod();
            if (nextSeed == BigInteger.ZERO)
                break;
        }
        System.out.println("Total execution time (ms): " + PollardRhoAttack.executionTime);
    }

}
