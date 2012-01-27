package kg.apc.jmeter.aws;

import kg.apc.jmeter.perfmon.*;
import java.io.IOException;

/**
 *
 * @author Nicolae Anca
 */
public class AWSMonException extends IOException {
   public AWSMonException(String message, Throwable cause) {
      super(message, cause);
   }

   public AWSMonException(String message) {
      super(message);
   }
}
