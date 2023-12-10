package com.fullcycle.admin.catalogo.infrastructure;


import com.fullcycle.admin.catalogo.application.UseCase;

public class Main {
    public static void main(String[] args) {
        System.out.println("Started application");
        System.out.println(new UseCase().execute());
    }
}