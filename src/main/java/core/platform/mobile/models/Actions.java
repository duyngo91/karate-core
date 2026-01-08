package core.platform.mobile.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Actions {
    @SuppressWarnings("java:S1068")
    private String type;
    @SuppressWarnings("java:S1068")
    private String id;
    private Parameters parameters;
    private List<MouseAction> actions = new ArrayList<>();

    public Actions id(String id){
        this.id = id;
        return this;
    }

    public Actions type(String type){
        this.type = type;
        return this;
    }

    public static Actions builder(){
        return new Actions();
    }
    public class Parameters{
        private String pointerType;

        public String getPointerType() {
            return pointerType;
        }

        public Actions setPointerType(String pointerType){
            Actions.this.parameters.pointerType = pointerType;
            return Actions.this;
        }
    }

    public Parameters buildParameters(){
        this.parameters = new Parameters();
        return this.parameters;
    }

    public MouseAction buildAction(){
        MouseAction action = new MouseAction();
        this.actions.add(action);
        return this.actions.get(this.actions.size() - 1);
    }

    public class MouseAction {
        private String type;
        private Object duration;
        private Object x;
        private Object y;
        private Object button;
        private String origin;

        public String getType() { return type; }
        public Object getDuration() { return duration; }
        public Object getX() { return x; }
        public Object getY() { return y; }
        public Object getButton() { return button; }
        public String getOrigin() { return origin; }

        public MouseAction setType(String type){
            this.type = type;
            return this;
        }
        public MouseAction setButton(Object button){
            this.button = button;
            return this;
        }
        public MouseAction setDuration(Object duration){
            this.duration = duration;
            return this;
        }
        public MouseAction setX(Object x){
            this.x = x;
            return this;
        }
        public MouseAction setY(Object y){
            this.y = y;
            return this;
        }
        public MouseAction setOrigin(String origin){
            this.origin = origin;
            return this;
        }

        public MouseAction buildAction(){
            MouseAction action = new MouseAction();
            Actions.this.actions.add(action);
            return Actions.this.actions.get(Actions.this.actions.size() - 1);
        }

        public Actions done(){
            return Actions.this;
        }
    }
}
