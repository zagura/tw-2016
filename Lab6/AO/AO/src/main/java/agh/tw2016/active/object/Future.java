package agh.tw2016.active.object;

/**
 * ActiveObject
 * Created by Michał Zagórski on 28.04.16.
 */
public class Future {
    private boolean status = false;
    private String result = null;
    public boolean isReady(){
        return this.status;
    }
    void setResult(String res){
        this.result = res;
        this.status = true;
    }
    public String getResult(){
        return this.result;
    }
}
