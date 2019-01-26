package lu.kremi151.brainfuck.util;

import android.util.Log;

import lu.kremi151.brainfuck.Brainfuck;
import lu.kremi151.brainfuck.directives.DirectiveBase;
import lu.kremi151.brainfuck.directives.DirectiveChar;
import lu.kremi151.brainfuck.directives.DirectiveNumber;
import lu.kremi151.brainfuck.directives.DirectiveString;
import lu.kremi151.brainfuck.exceptions.InfiniteLoopException;
import lu.kremi151.brainfuck.exceptions.ScopeCalculationException;
import lu.kremi151.brainfuck.interfaces.IExecutable;
import lu.kremi151.brainfuck.interfaces.IExecutionListener;
import lu.kremi151.brainfuck.interfaces.ISteppableExecutable;
import lu.kremi151.brainfuck.interfaces.ITickCounter;
import lu.kremi151.brainfuck.interfaces.SlotRegister;

/**
 * Created by michm on 12.10.2016.
 */

public class ExecutionIterator implements ISteppableExecutable, ITickCounter{

    private static DirectiveBase directiveRegister[] = new DirectiveBase[8];

    static{
        directiveRegister[0] = new DirectiveChar();
        directiveRegister[1] = new DirectiveNumber();
        directiveRegister[2] = new DirectiveString(true);
        directiveRegister[3] = new DirectiveString(false);
    }

    private SlotRegister reg;
    private String code;
    private AdvancedCharIterator codeIterator;
    private boolean debug = false, stepping = false, extended = false;
    private IExecutionListener l = null;
    private int actual_scope = -1, scopes_added = 0, scopes_popped = 0, loops_skipped = 0;
    private long tick_count = 0;
    private ScopeMeta scopes[] = new ScopeMeta[16];
    private State state = State.READY;

    public ExecutionIterator(SlotRegister reg, String code, int start){
        this.reg = reg;
        this.code = code;
        this.codeIterator = new AdvancedCharIterator(code);
    }

    public ExecutionIterator setListener(IExecutionListener l){
        this.l = l;
        return this;
    }

    @Override
    public void setStepping(boolean v){
        this.stepping = v;
    }

    public void setExtended(boolean v) { this.extended = v; }

    public SlotRegister getRegister(){
        return reg;
    }

    @Deprecated
    public void replaceRegister(SlotRegister reg){
        this.reg = reg;
    }

    public void reset(){
        tick_count = 0;
        codeIterator.reset();
        reg.reset();
        actual_scope = -1;
        scopes_added = 0;
        scopes_popped = 0;
        loops_skipped = 0;
    }

    public void resume(){
        this.state = State.RUNNING;
        if(l!=null)l.onResumed(this);

        do{
            if(!execute()){
                if(debug)System.out.println("execution > break");
                break;
            }
        }while(codeIterator.hasNext() && (!stepping));

        if(!codeIterator.hasNext()){
            this.state = State.FINISHED;
            if(l != null)l.onStopped(this);
        }else{
            this.state = State.PAUSED;
            if(l != null)l.onPaused(this);
        }
        //System.out.println("can continue: " + canContinue());
        //if(debug)System.out.println("i: " + index + "\tl: " + length);
    }

    private boolean execute(){
        if(codeIterator.hasNext()){
            tick_count++;
            try{
                char c = codeIterator.next();

                if(debug){
                    System.out.print("Execution:\t");
                    for(int i = 0 ; i < codeIterator.length() ; i++){
                        if(i == codeIterator.getIndex()){
                            System.out.print("[" + code.charAt(i) + "]\t");
                        }else{
                            System.out.print(code.charAt(i) + "\t");
                        }
                    }
                    System.out.println(" (ptr: " + reg.ptr() + " value: " + (int)reg.value() + ")");
                }

                switch(c){
                    case '<':
                        reg.left();
                        break;
                    case '>':
                        reg.right();
                        break;
                    case '+':
                        reg.increment();
                        break;
                    case '-':
                        reg.decrement();
                        break;
                    case ',':
                        if(!reg.in(this)){
                            if(debug)System.out.println("reg.in returned false");
                            codeIterator.goBack();
                            return false;
                        }
                        break;
                    case '.':
                        reg.out();
                        break;
                    case '[':
                        if(reg.value() != (char)0){
                            ScopeMeta sm = addScope(codeIterator.getIndex());
                            sm.ptr = reg.ptr();
                            sm.val = (int)reg.value();
                        }else{
                            loops_skipped++;
                            int neutr = 0;
                            while(codeIterator.hasNext()){
                                c = codeIterator.next();
                                if(c == '['){
                                    neutr++;
                                }else if(c == ']' && neutr-- == 0){
                                    break;
                                }
                            }
                        }
                        break;
                    case ']':
                        if(reg.value() != 0){
                            ScopeMeta sm = getScope();
                            if(Brainfuck.getInstance().isInfiniteLoopDetectionEnabled() && sm.ptr == reg.ptr() && sm.val == (int)reg.value()){
                                throw(new InfiniteLoopException("Infinte loop at code index " + codeIterator.getIndex()));
                            }
                            codeIterator.setIndex(sm.start);
                            sm.ptr = reg.ptr();
                            sm.val = (int)reg.value();
                        }else{
                            //if(scopes.size() > 0){
                            popScope();
                            //}
                        }

                        break;
                    default:
                        if(extended){
                            switch(c){
                                case ':':
                                    if(codeIterator.hasNext()){
                                        char directive = codeIterator.next();
                                        Optional<DirectiveBase> odb = findDirective(directive);
                                        if(odb.isPresent()){
                                            tick_count += odb.get().process(reg, codeIterator) - 1;
                                        }else{
                                            System.out.println("Unsupported directive: \"" + directive + "\", ignoring");
                                        }
                                    }
                                    break;
                            }
                        }
                        break;
                }
                return true;
            }catch(ScopeCalculationException e){
                Log.e("Scope error", "ArrayIndexOutOfBoundsException: Probably a scope calculation error.\nScopes entered: " + scopes_added + "\nScopes left: " + scopes_popped + "\nLoops skipped: " + loops_skipped);
                throw(e);
            }
        }else{
            return false;
        }
    }

    @Override
    public long tickCount(){
        return tick_count;
    }

    public static Optional<DirectiveBase> findDirective(char c){
        for(int d = 0 ; d < directiveRegister.length ; d++){
            DirectiveBase db = directiveRegister[d];
            if(db != null && db.getId() == c){
                return Optional.fromNullable(db);
            }
        }
        return Optional.absent();
    }

    public State getState(){
        return state;
    }

    private ScopeMeta addScope(int scope){
        if(actual_scope + 1 >= scopes.length){
            ScopeMeta tmp[] = scopes;
            scopes = new ScopeMeta[scopes.length + 16];
            for(int i = 0 ; i < tmp.length ; i++){
                scopes[i] = tmp[i];
            }
        }
        ScopeMeta nsm = new ScopeMeta();
        nsm.start = scope;
        scopes[++actual_scope] = nsm;
        scopes_added++;
        if(debug)System.out.println("+ Scope added (a=" + scopes_added + ", p=" + scopes_popped + ")");
        return nsm;
    }

    private ScopeMeta popScope(){
        scopes_popped++;
        if(debug)System.out.println("- Scope popped (a=" + scopes_added + ", p=" + scopes_popped + ")");
        if(actual_scope >= 0){
            ScopeMeta sm = scopes[actual_scope];
            scopes[actual_scope--] = null;
            return sm;
        }else{
            throw new ScopeCalculationException("No super scope available");
        }
    }

    private ScopeMeta getScope(){
        if(actual_scope >= 0 && actual_scope < scopes.length){
            return scopes[actual_scope];
        }else{
            throw new ScopeCalculationException("Requested scope was out of bounds: index=" + actual_scope + ", length=" + scopes.length);
        }
    }

    @Override
    public boolean resumeBF() {
        if(state != State.FINISHED){
            state = State.RUNNING;
            resume();
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void pauseBF() {
        state = State.PAUSED;
    }

    @Override
    public void stopBF() {
        state = State.FINISHED;
    }

    public enum State{
        READY,
        RUNNING,
        PAUSED,
        FINISHED
    }

    private static class ScopeMeta{
        int start, ptr, val;
    }

    private static class AdvancedCharIterator extends CharIterator{

        public AdvancedCharIterator(String base) {
            super(base);
        }

        private int getIndex(){
            return this.index;
        }

        private void setIndex(int index){
            if(index < 0 || index >= this.length())throw new ArrayIndexOutOfBoundsException();
            this.index = index;
        }

        private void goBack(){
            this.index--;
            if(index < -1)throw new ArrayIndexOutOfBoundsException("Index went under -1");
        }

        private void reset(){
            index = -1;
        }
    }

}
