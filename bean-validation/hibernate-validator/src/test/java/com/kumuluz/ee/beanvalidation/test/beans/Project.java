package com.kumuluz.ee.beanvalidation.test.beans;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Tilen
 */
public class Project {

    @NotNull
    @Size(min = 2, max = 30)
    private String name;

    @Size(min = 1, max = 3000)
    private String description;

    @NotNull
    private User user;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
