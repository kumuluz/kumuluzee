package com.kumuluz.ee.beanvalidation.test.beans;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Tilen
 */
public class User {

    @NotNull
    @Size(min = 2, max = 30)
    private String username;

    @NotNull
    @Pattern(regexp = "^(.+)@(.+)$")
    private String email;

    @Size(min = 2, max = 20)
    private String firstname;

    @Size(min = 2, max = 20)
    private String lastname;

    @NotNull
    @Min(0)
    private Integer age;

    @NotNull
    @Min(0)
    private Double salary;

    @NotNull
    private Date createdAt;

    private List<Project> projects;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
