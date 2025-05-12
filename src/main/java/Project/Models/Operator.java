package Project.Models;

public class Operator {
    private String name;
    private boolean isBusy;
    private Call currentCall;

    public Operator(String name) {
        this.name = name;
        this.isBusy = false;
        this.currentCall = null;
    }

    public String getName() {
        return name;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void assignCall(Call call) {
        this.currentCall = call;
        this.isBusy = true;
    }

    public void finishCall() {
        this.currentCall = null;
        this.isBusy = false;
    }

    public Call getCurrentCall() {
        return currentCall;
    }

    @Override
    public String toString() {
        return name + (isBusy ? " (Meşgul)" : " (Boşta)");
    }
}
