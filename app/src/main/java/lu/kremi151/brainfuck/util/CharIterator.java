package lu.kremi151.brainfuck.util;

import java.util.Iterator;

/**
 * Created by michm on 22.10.2016.
 */

public class CharIterator implements Iterator<Character> {

    protected int index = -1;
    private char base[];

    public CharIterator(String base){
        this.base = base.toCharArray();
    }

    public CharIterator(char[] base){
        this.base = base;
    }

    @Override
    public boolean hasNext() {
        return base.length != 0 && index < base.length - 1;
    }

    public boolean hasPrevious(){
        return base.length != 0 && index > 0;
    }

    @Override
    public Character next() {
        return base[++index];
    }

    public Character previous(){
        return base[--index];
    }

    @Override
    public void remove() {
        throw new RuntimeException("Not supported");
    }

    public int length(){
        return base.length;
    }
}
