
package pollardrhoattack;

import java.math.BigInteger;

/**
 *
 * @author Francesco Mancuso
 */

//y^2 = x^3 + a*x + b Weyerstrass 
public abstract class EllipticCurve {
    
    
        
    public  static Point addition(Point p1, Point p2, BigInteger a, BigInteger m){
        Point p;
        BigInteger lambda;
        if (p1.isInfinite())
            return p2;
        if (p2.isInfinite())
            return p1;
        if ((p1.getX().compareTo(p2.getX()) == 0) && (p1.getY().compareTo(p2.getY()) != 0)){
            return new Point(new BigInteger("-1"), new BigInteger("-1"));
        }

        if ((p1.getX().compareTo(p2.getX()) != 0) || (p1.getY().compareTo(p2.getY()) != 0)){
            lambda = p2.getY().subtract(p1.getY()).multiply(p2.getX().subtract(p1.getX()).modInverse(m));
        }else
        {
            lambda = p1.getX().pow(2).multiply(new BigInteger("3")).add(a).multiply(p1.getY().multiply(new BigInteger("2")).modInverse(m));
        }
        BigInteger x =  lambda.pow(2).subtract(p1.getX()).subtract(p2.getX()).mod(m);
        BigInteger y = lambda.multiply(p1.getX().subtract(x)).subtract(p1.getY()).mod(m);
        p = new Point(x, y);
        return p;
    }
    
    public  static Point multiplication(Point p1, BigInteger n, BigInteger a, BigInteger m){
        Point p = new Point(p1.getX(), p1.getY());
        if (n.compareTo(BigInteger.valueOf(2)) < 0)
            return p;
        for(BigInteger i = BigInteger.valueOf(2); i.compareTo(n)<= 0; i = i.add(BigInteger.ONE)){
            p = addition(p1,p, a, m);
        }
        return p;
    }
    
    public static boolean is_on_curve(Point p, BigInteger a, BigInteger b, BigInteger m){   
        return (p.getY().pow(2).mod(m).compareTo(p.getX().pow(3).add(p.getX().multiply(a)).add(b).mod(m)) == 0);
    }
    
    public static Point inverse(Point p){
        return new Point(p.getX(), p.getY().negate());
    }
    
    public static Point[] encryption(Point g, Point pm, Point q, BigInteger k, BigInteger a, BigInteger m){
        Point[] c = new Point[2];
        c[0] = multiplication(g, k, a, m);
        c[1] = addition(pm, multiplication(q, k, a, m), a, m);
        return c;
    }
    
    public static Point decryption(Point[] c, BigInteger s, BigInteger a, BigInteger m){
        return addition(c[1], inverse(multiplication(c[0], s, a, m)), a, m);
    }   
   
        
}
