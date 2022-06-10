
package pollardrhoattack;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Francesco Mancuso
 */
public class PollardRhoAttack {
    private final Point g;
    private final BigInteger oG;
    private final Point q;
    private final BigInteger a;
    private final BigInteger b;
    private final BigInteger p;
    private final BigInteger s1;
    private final BigInteger s2;
    private final BigInteger s3;
    public static long executionTime;
    private final HashMap <BigInteger, Point> R = new HashMap<>();
    private final HashMap <BigInteger, BigInteger> aMap = new HashMap<>();
    private final HashMap <BigInteger, BigInteger> bMap = new HashMap<>();
    
    
    private BigInteger gcd(BigInteger n1, BigInteger n2){
        if (n2.compareTo(BigInteger.ZERO) == 0){
            return n1;
        }
        return gcd(n2, n1.mod(n2));
    }
    
    public PollardRhoAttack(Point g, BigInteger oG, Point q, BigInteger a, BigInteger b, BigInteger p){
        this.g = g;
        this.oG = oG;
        this.q = q;
        this.a = a;
        this.b = b;
        this.p = p;
        s1 = this.p.divide(new BigInteger("2"));
        s2 = this.p.multiply(new BigInteger("7")).divide(new BigInteger("8"));
        s3 = new BigInteger(this.p.toString());
    }
    
    private BigInteger f(BigInteger i){
        Point p1;  
        if (R.get(i).getX().compareTo(s1) <= 0){
            p1 = EllipticCurve.addition(g, R.get(i), a, this.p);
            R.put(i.add(BigInteger.ONE), p1);
            aMap.put(i.add(BigInteger.ONE), aMap.get(i).add(BigInteger.ONE).mod(oG));
            bMap.put(i.add(BigInteger.ONE), bMap.get(i));
        }
        else
        if ((R.get(i).getX().compareTo(s1) > 0) && (R.get(i).getX().compareTo(s2) <= 0)){
            p1 = EllipticCurve.multiplication(R.get(i), new BigInteger("2"), a, this.p);
            R.put(i.add(BigInteger.ONE), p1);
            aMap.put(i.add(BigInteger.ONE), aMap.get(i).multiply(new BigInteger("2")).mod(oG));
            bMap.put(i.add(BigInteger.ONE), bMap.get(i).multiply(new BigInteger("2")).mod(oG));
        }
        else
        if ((R.get(i).getX().compareTo(s2) > 0) && (R.get(i).getX().compareTo(s3) <= 0)){
            p1 = EllipticCurve.addition(q, R.get(i), a, this.p);
            R.put(i.add(BigInteger.ONE), p1);
            aMap.put(i.add(BigInteger.ONE), aMap.get(i).mod(oG));
            bMap.put(i.add(BigInteger.ONE), bMap.get(i).add(BigInteger.ONE).mod(oG));
        }
        
        return i.add(BigInteger.ONE);
    }
    
    private void verifyPrivateKey(BigInteger pKey){
        boolean verified = false;
        for(BigInteger j = BigInteger.ONE; j.compareTo(oG) < 0; j = j.add(BigInteger.ONE)){
            Point p1 = EllipticCurve.multiplication(g, j, a, this.p);
            if (((p1.getX().compareTo(q.getX()) == 0) && (p1.getY().compareTo(q.getY()) == 0)) && (j.compareTo(pKey) == 0)){
                verified = true; 
                break;
            }
        }
        if (verified)
            System.out.println("Private key verified");
        else
            System.err.println("Private key not verified");
    }
    
    private void printSteps(){
        for(BigInteger j = BigInteger.ONE; j.compareTo(BigInteger.valueOf(R.size()))<= 0; j = j.add(BigInteger.ONE)){
           System.out.println("R"+j + ": ("+R.get(j).getX()+","+R.get(j).getY()+") - a: " + aMap.get(j) + " - b: " +bMap.get(j));       
        }
    }
    
    public BigInteger pollardMethod(){
        long start = System.currentTimeMillis();
        BigInteger a0;
        BigInteger b0;
        Random random = new Random();
        do{
            a0 = new BigInteger(this.oG.subtract(BigInteger.ONE).bitLength(), random);
        }while ((a0.compareTo(this.oG) >= 0) || (a0.compareTo(BigInteger.ZERO) == 0));
        do{ 
            b0 = new BigInteger(this.oG.subtract(BigInteger.ONE).bitLength(), random);
        }while ((b0.compareTo(this.oG) >= 0) || (b0.compareTo(BigInteger.ZERO) == 0));
        Point r1 = EllipticCurve.multiplication(this.g, a0, this.a, this.p);
        Point r2 = EllipticCurve.multiplication(this.q, b0, this.a, this.p);
        Point r0 = EllipticCurve.addition(r1, r2, this.a, this.p);
        BigInteger i = BigInteger.ONE;
        BigInteger pKey = BigInteger.ZERO;
        R.put(i, r0);  aMap.put(i, a0); bMap.put(i, b0);
        do{
            f(i);
            f(f(i.multiply(new BigInteger("2"))));  
            i = i.add(BigInteger.ONE);
        }while ((R.get(i).getX().compareTo(R.get(i.multiply(new BigInteger("2"))).getX()) != 0) || (R.get(i).getY().compareTo(R.get(i.multiply(new BigInteger("2"))).getY()) != 0));           
       
        BigInteger d = gcd(bMap.get(i.multiply(new BigInteger("2"))).subtract(bMap.get(i)), oG);

        if (d.compareTo(BigInteger.ONE) == 0){
            pKey = aMap.get(i.multiply(new BigInteger("2"))).subtract(aMap.get(i)).multiply(bMap.get(i).subtract(bMap.get(i.multiply(new BigInteger("2")))).modInverse(oG)).mod(oG);
        }      
        
        if (pKey.compareTo(BigInteger.ZERO) != 0)
            System.out.println("Private key found: " + pKey);
        else{
            System.err.println("Private key not found");
        }

        long end = System.currentTimeMillis();
       
        executionTime += end - start;
        
        verifyPrivateKey(pKey); //verifica che la chiave privata sia corretta
        
        System.out.println("Execution time (ms): " + (end - start));
        System.out.println("--------------------------------------");
        

            
        return pKey;
    }
        
}
