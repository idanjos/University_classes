/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.test;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author mint
 */
@Named(value = "indexBean")
@RequestScoped
public class IndexBean {

    /**
     * Creates a new instance of IndexBean
     */
    private double simpleInterest;
    private double principle;
    private String message = "Hello, Welcome to JSF";
    private int years;
    private double interest;
    public String calculateSI(){
        simpleInterest = principle * interest * years;
        return "index";
    }

    public double getSimpleInterest() {
        return simpleInterest;
    }

    public void setSimpleInterest(double simpleInterest) {
        this.simpleInterest = simpleInterest;
    }

    public double getPrinciple() {
        return principle;
    }

    public void setPrinciple(double principle) {
        this.principle = principle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }
    public IndexBean() {
    }
    
}
