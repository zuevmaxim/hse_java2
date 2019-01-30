import java.util.function.Function;

public class Maybe<T> {
    private Boolean isPresent = false;
    private T value = null;
    public static  <T> Maybe<T> just(T t) {
        var m = new Maybe<T>();
        m.value = t;
        m.isPresent = true;
        return m;
    }
    public static <T> Maybe<T> nothing() {
        var m = new Maybe<T>();
        return m;
    }

    public T get() {
        if (!isPresent) {
            throw new ("No value!");
        }
    }

    public boolean isPresent() {

    }

    public <U> Maybe<U> map(Function<?,?> mapper) {

    }
}
