package org.casbin.resp;

import java.util.ArrayList;

public class ResponseBody {

    private Boolean allow;

    private ArrayList<?> explain;

    public ResponseBody() {

    }

    public ResponseBody(Boolean allow, ArrayList<?> explain) {
        this.allow = allow;
        this.explain = explain;
    }

    public Boolean getAllow() {
        return allow;
    }

    public void setAllow(Boolean allow) {
        this.allow = allow;
    }

    public ArrayList<?> getExplain() {
        return explain;
    }

    public void setExplain(ArrayList<?> explain) {
        this.explain = explain;
    }

    @Override
    public String toString() {
        return "ResponseBody{" +
                "allow=" + allow +
                ", explain=" + explain +
                '}';
    }
}
