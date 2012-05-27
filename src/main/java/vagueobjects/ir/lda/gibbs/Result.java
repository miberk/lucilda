package vagueobjects.ir.lda.gibbs;

import java.util.Arrays;

/**
 * Holds the result of computation.
 */
public class Result {
    final double [][] phi;
    final double [][] theta;

    public Result(Sampler sampler){
        this.phi = sampler.getPhi();
        this.theta = sampler.getTheta();
    }

    /** @return Topic-token associations*/
    public double[][] getPhi() {
        return phi;
    }
    /**@return Document-topic associations */
    public double[][] getTheta() {
        return theta;
    }

    @Override
    public String toString() {
        return "Result{" +
                "phi=" + (phi == null ? null : Arrays.asList(phi)) +
                ", theta=" + (theta == null ? null : Arrays.asList(theta)) +
                '}';
    }
}
