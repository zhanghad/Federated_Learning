package com.fed.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Clients {

    //所有在线的参与者
    public ArrayList<ClientInfo> clients=new ArrayList<ClientInfo>();

    private void sort(){
        Collections.sort(clients, new Comparator<ClientInfo>() {
            @Override
            public int compare(ClientInfo o1, ClientInfo o2) {
                return (int) (o2.getClientSocre()-o1.getClientSocre());
            }
        });
    }


    //选择参与者
    public ArrayList<ClientInfo> selectClients(int clientNum){
        ArrayList<ClientInfo> selected=new ArrayList<ClientInfo>();
        if(clients.size()<=clientNum){
            selected=clients;
        }else {
            this.sort();
            for (int i=0;i<clientNum;i++){
                selected.add(clients.get(i));
            }
        }
        return selected;
    }



}
