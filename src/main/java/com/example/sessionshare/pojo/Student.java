package com.example.sessionshare.pojo;

public class Student {

    private int id;
    private String name;
    private String age;

    public Student(){

    }

    public Student(Integer id, String name, String age){
        this.id = id;
        this.name=name;
        this.age=age;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
