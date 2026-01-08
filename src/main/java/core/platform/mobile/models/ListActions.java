package core.platform.mobile.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;



@Data
public class ListActions {
    private List<Actions> actions = new ArrayList<>();

    public static ListActions init(){
        return new ListActions();
    }


    public ListActions add(Actions action){
        this.actions.add(action);
        return this;
    }
}
