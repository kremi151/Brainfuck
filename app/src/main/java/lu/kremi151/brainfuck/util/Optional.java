package lu.kremi151.brainfuck.util;

/**
 * Created by michm on 24.10.2016.
 */

public class Optional<E> {

    private E obj;

    private Optional(E obj){
        this.obj = obj;
    }

    public boolean isPresent(){
        return obj != null;
    }

    public E get(){
        if(obj == null){
            throw new NullPointerException();
        }
        return obj;
    }

    public static <T> Optional<T> fromNullable(T obj){
        return new Optional<>(obj);
    }

    public static <T> Optional<T> absent(){
        return new Optional<>(null);
    }
}
