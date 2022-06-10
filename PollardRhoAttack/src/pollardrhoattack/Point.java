
package pollardrhoattack;

import java.math.BigInteger;

/**
 *
 * @author Francesco Mancuso
 */
public class Point {
    
    private final BigInteger x;
    private final BigInteger y;
    private final boolean infinite;
    
    public Point(BigInteger x, BigInteger y){
       this.x = x;
       this.y = y;
       this.infinite = (x.compareTo(new BigInteger("-1")) == 0) && (y.compareTo(new BigInteger("-1")) == 0);
    }
    
    public BigInteger getX(){
        return new BigInteger(this.x.toString());
    }
    
    public BigInteger getY(){
        return new BigInteger(this.y.toString());
    }
    
    public boolean isInfinite(){
        return this.infinite;
    }
    
    public void print_point(){
        System.out.println("(" + this.x + "," + this.y + ")");
    }
}
