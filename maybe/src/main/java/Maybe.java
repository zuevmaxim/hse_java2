import java.util.function.Function;

public class Maybe<T> {
    private Boolean isPresent = false;
    private T value = null;

    public static  <T> Maybe<T> just(T t) throws IllegalArgumentException {
        if (t == null) {
            throw new IllegalArgumentException("Null param!");
        }
        var m = new Maybe<T>();
        m.value = t;
        m.isPresent = true;
        return m;
    }

    public static <T> Maybe<T> nothing() {
        return new Maybe<T>();
    }

    public T get() throws IllegalStateException {
        if (!isPresent) {
            throw new IllegalStateException("No value!");
        }
        return value;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public <U> Maybe<U> map(Function<?,?> mapper) {

    }
}
