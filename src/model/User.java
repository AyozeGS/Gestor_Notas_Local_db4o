/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author Ayoze Gil
 */
public class User {
    
    private ArrayList<Integer> documents;
    private final String name;
    private String password;

    public User(){
        documents = new ArrayList<>();
        this.name = null;
        this.password = null;
    }
    
    public User(String name, String password) {
        documents = new ArrayList<>();
        this.name = name;
        this.password = password;
    }
    
    public void addDocument(Integer document) {
        documents.add(document);
    }

    public void setDocuments(ArrayList<Integer> documents) {
        this.documents = documents;
    }

    public ArrayList<Integer> getDocuments() {
        return documents;
    }
    
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
