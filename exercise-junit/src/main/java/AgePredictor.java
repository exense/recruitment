import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;

public class AgePredictor {

    private final Map<String, Map<Integer, Long>> occurrencesPerFirstnameAndYear;

    public AgePredictor(Map<String, Map<Integer, Long>> occurrencesPerFirstnameAndYear) {
        this.occurrencesPerFirstnameAndYear = occurrencesPerFirstnameAndYear;
    }

    public long predictAge(String firstname) throws AgePredictorException {
        Objects.requireNonNull(firstname, "No firstname specified");
        Map<Integer, Long> occurrencesPerYear = occurrencesPerFirstnameAndYear.get(firstname);
        if (occurrencesPerYear == null) {
            throw new AgePredictorException("No data available for " + firstname);
        }
        Long sum = occurrencesPerYear.values().stream().reduce(0L, Long::sum);
        if(sum <= 0) {
            throw new RuntimeException("The sum of occurrences is lower than 1. This should never occur");
        }

        LongAdder adder = new LongAdder();
        occurrencesPerYear.forEach((year, count) -> {
            adder.add(count * year);
        });

        long l = adder.longValue() / sum;
        long medianAge = LocalDate.now().getYear() - l;
        return medianAge;
    }

    public static class AgePredictorException extends Throwable {

        public AgePredictorException(String message) {
            super(message);
        }
    }
}
