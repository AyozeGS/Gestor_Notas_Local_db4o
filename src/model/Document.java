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
public class Document {
    
    private ArrayList<String> users;
    private int id;
    private String title;
    private String content;
    
    public Document(){
        id = (int) Math.round(Math.random()*100000000);
        users = new ArrayList<>();
        this.title = null;
        this.content = null;
    }
    
    public Document(String title, String content) {
        id = (int) Math.round(Math.random()*100000000);
        users = new ArrayList<>();
        this.title = title;
        this.content = content;
    }
    
    public void setDocument(Document document){
        this.id = document.id;
        this.title = document.getTitle();
        this.content = document.getContent();
        document.getUsers().forEach((s) -> {
            this.users.add(s);
        });
    }

    public int getId() {
        return id;
    }
    
    public ArrayList<String> getUsers() {
        return users;
    }

    public void addUser(String user){
        users.add(user);
    }
    
    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    
    
}
