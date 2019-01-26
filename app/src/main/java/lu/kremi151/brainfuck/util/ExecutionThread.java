package lu.kremi151.brainfuck.util;

import android.support.annotation.NonNull;
import android.util.Log;

import lu.kremi151.brainfuck.exceptions.ScopeCalculationException;
import lu.kremi151.brainfuck.interfaces.IExecutable;
import lu.kremi151.brainfuck.interfaces.IExecutionListener;
import lu.kremi151.brainfuck.interfaces.IRunner;
import lu.kremi151.brainfuck.interfaces.SlotRegister;

/**
 * Created by michm on 17.10.2016.
 */

public class ExecutionThread extends Thread implements IExecutable{

    private volatile State state = State.RUNNING;
    private int index = 0, length;
    private String code;
    private boolean debug = false, started = false, stepping = false;
    private IRunner runner;
    private SlotRegister reg;
    private IExecutionListener l = null;
    private int scopes[] = new int[16], actual_scope = -1, scopes_added = 0, scopes_popped = 0, loops_skipped = 0;

    public ExecutionThread(@NonNull String code, @NonNull IRunner runner, @NonNull IExecutionListener l, @NonNull SlotRegister reg){
        if(code == null || runner == null || l == null || reg == null){
            throw new NullPointerException("Cannot work with null pointer references");
        }
        this.code = code;
        this.length = code.length();
        this.runner = runner;
        this.reg = reg;
        this.l = l;
    }

    public void setStepping(boolean v){
        this.stepping = v;
    }

    @Override
    public void run(){
        started = true;
        while(state.active && index < length){
            while(state == State.PAUSED){}
            if(!state.active){
                break;
            }

            try{
                char c = code.charAt(index);

                if(debug){
                    System.out.print("Execution:\t");
                    for(int i = 0 ; i < length ; i++){
                        if(i == index){
                            System.out.print("[" + code.charAt(i) + "]\t");
                        }else{
                            System.out.print(code.charAt(i) + "\t");
                        }
                    }
                    System.out.println(" (ptr: " + reg.ptr() + " value: " + (int)reg.value() + ")");
                }

                switch(c){
                    case '<':
                        runner.run(run_reg_left);
                        break;
                    case '>':
                        runner.run(run_reg_right);
                        break;
                    case '+':
                        runner.run(run_reg_increment);
                        break;
                    case '-':
                        runner.run(run_reg_decrement);
                        break;
                    case ',':
                        /*if(!reg.in(this)){
                            pauseBF();
                        }*/
                        //TODO
                        break;
                    case '.':
                        runner.run(run_reg_out);
                        break;
                    case '[':
                        if(reg.value() != (char)0){
                            addScope(index);
                        }else{
                            loops_skipped++;
                            int neutr = 0;
                            for(index++ ; index < length ; index++){
                                c = code.charAt(index);
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
                            index = getScope();
                        }else{
                            //if(scopes.size() > 0){
                            popScope();
                            //}
                        }

                        break;
                }
            }catch(ScopeCalculationException e){
                Log.e("Scope error", "ArrayIndexOutOfBoundsException: Probably a scope calculation error.\nScopes entered: " + scopes_added + "\nScopes left: " + scopes_popped + "\nLoops skipped: " + loops_skipped);
                throw(e);
            }
            index++;
            if(stepping)pauseBF();
        }

        runner.run(run_on_stopped);
    }

    public State getExecutorState(){
        return state;
    }

    private void addScope(int scope){
        if(actual_scope + 1 >= scopes.length){
            int tmp[] = scopes;
            scopes = new int[scopes.length + 16];
            for(int i = 0 ; i < tmp.length ; i++){
                scopes[i] = tmp[i];
            }
        }
        scopes[++actual_scope] = scope;
        scopes_added++;
        System.out.println("+ Scope added (a=" + scopes_added + ", p=" + scopes_popped + ")");
    }

    private int popScope(){
        scopes_popped++;
        System.out.println("- Scope popped (a=" + scopes_added + ", p=" + scopes_popped + ")");
        if(actual_scope >= 0){
            return scopes[actual_scope--];
        }else{
            throw new ScopeCalculationException("No super scope available");
        }
    }

    private int getScope(){
        if(actual_scope >= 0 && actual_scope < scopes.length){
            return scopes[actual_scope];
        }else{
            throw new ScopeCalculationException("Requested scope was out of bounds: index=" + actual_scope + ", length=" + scopes.length);
        }
    }

    private final Runnable run_reg_left = new Runnable() {
        @Override
        public void run() {
            reg.left();
        }
    };

    private final Runnable run_reg_right = new Runnable() {
        @Override
        public void run() {
            reg.right();
        }
    };

    private final Runnable run_reg_increment = new Runnable() {
        @Override
        public void run() {
            reg.increment();
        }
    };

    private final Runnable run_reg_decrement = new Runnable() {
        @Override
        public void run() {
            reg.decrement();
        }
    };

    private final Runnable run_reg_out = new Runnable() {
        @Override
        public void run() {
            reg.out();
        }
    };

    private final Runnable run_reg_in = new Runnable() {
        @Override
        public void run() {
            reg.in(null);//TODO
        }
    };

    private final Runnable run_on_stopped = new Runnable() {
        @Override
        public void run() {
            l.onStopped(ExecutionThread.this);
        }
    };

    @Override
    public boolean resumeBF() {
        if(state != State.FINISHED){
            if(!started){
                this.start();
            }
            state = State.RUNNING;
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
    public void stopBF(){
        state = State.FINISHED;
    }

    public enum State{
        READY(true),
        RUNNING(true),
        PAUSED(true),
        FINISHED(false);

        private final boolean active;

        State(boolean active){
            this.active = active;
        }
    }
}
