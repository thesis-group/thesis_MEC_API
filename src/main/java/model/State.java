package model;

import java.util.Set;

public class State {
    int Z; //用户所在id的集合 1-20
    int N; //q区域内连接AP的节点数 50-100
    int V; //与用户相连的节点数 1-100
    int Q; //队列长度 1-10


    /**
     * 无参数构造方法
     */
    public State(){

    }

    /**
     * 具体化参数初始方法
     * @param z  zoneID
     * @param n  接入Ap节点数
     * @param v  与用户相连的节点数
     * @param q  队列长度
     */
    public State(int z, int n, int v, int q) {
        Z = z;
        N = n;
        V = v;
        Q = q;
    }

    /**
     * 根据State的hashID 构造state
     * @param stateID
     */
    public State(long stateID){
        this.Z  = (int) ((stateID /50000)%20+1);
        this.N = (int) ((stateID/1000)%50+50);
        this.V = (int) ((stateID/10)%100+1);
        this.Q = (int) (stateID%10 + 1);

    }

    /**
     * 获取该状态的状态ID
     * @return long
     */
    public long getStateID(){
        return (Z-1)*50000 + (N-50) * 1000 + (V-1)*10 + Q-1;
    }

    @Override
    public boolean equals(Object obj) {
        State state = (State)obj;
        return state.getStateID() == this.getStateID();
    }
}
