public class Maybe<T> {
    private Boolean isPresent = false;
    private T value = null;
    public static  <T> Maybe<T> just(T t) {
        var m = new Maybe<T>();
        m.value = t;
        return m;
    }
    public static <T> Maybe<T> nothing() {}

    public T get() {

    }

    public boolean isPresent() {

    }
}
