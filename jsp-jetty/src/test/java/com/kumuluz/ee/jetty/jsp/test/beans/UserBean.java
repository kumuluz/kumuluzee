package com.kumuluz.ee.jetty.jsp.test.beans;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Tilen
 */
public class UserBean {

    private String username;

    private String email;

    private String firstname;

    private String lastname;

    private Integer age;

    private String country;

    private Date created;

    public UserBean() {

        username = "ahughes";
        email = "amy.hughes@mac.com";
        firstname = "Amy";
        lastname = "Hughes";
        age = 1;
        country = "US";
        created = Date.from(
                LocalDate.of(2014, 3, 3)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant());
    }

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        country = country;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
