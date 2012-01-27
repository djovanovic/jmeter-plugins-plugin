package kg.apc.jmeter.aws;

import kg.apc.jmeter.perfmon.*;
import org.apache.jmeter.samplers.SampleResult;

/**
 *
 * @author APC
 */
public class AWSMonSampleResult
        extends SampleResult {

    private final long ts;

    public AWSMonSampleResult() {
        ts = System.currentTimeMillis();
    }

    // store as responseTime, multiply by 1000 to keep floating precision
    public void setValue(double value) {
        setStartTime(ts);
        setEndTime(ts + (long) (value * 1000));
    }

    @Deprecated
    public double getValue() {
        return ((double) getTime()) / 1000d;
    }

    //needed for CSV reload as object created by JMeter is not AWSMonSampleResult but SampleResult
    public static double getValue(SampleResult res) {
        return ((double) res.getTime()) / 1000d;
    }
}
