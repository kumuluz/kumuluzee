package com.kumuluz.ee.beanvalidation.test;

import com.kumuluz.ee.beanvalidation.test.beans.Project;
import com.kumuluz.ee.beanvalidation.test.beans.User;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * @author Tilen
 */
public class HibernateValidatorTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testCorrectEntities() {

        Project p1 = new Project();
        p1.setName("Test project");
        p1.setDescription("Sample description of a project");

        User u1 = new User();
        u1.setUsername("ahughes");
        u1.setEmail("amy.hughes@mac.com");
        u1.setFirstname("Amy");
        u1.setLastname("Hughes");
        u1.setAge(20);
        u1.setSalary(100000d);
        u1.setCreatedAt(new Date());
        u1.setProjects(new ArrayList<>());

        u1.getProjects().add(p1);
        p1.setUser(u1);

        Set<ConstraintViolation<User>> constraintViolations =
                validator.validate(u1);

        Assert.assertNotNull(constraintViolations);
        Assert.assertEquals(0, constraintViolations.size());
    }

    @Test
    public void testWrongEntities() {

        Project p1 = new Project();
        p1.setName("T");

        User u1 = new User();
        u1.setUsername("ahughes");
        u1.setEmail("amy.hughesmac.com");
        u1.setFirstname("");
        u1.setLastname("Hughes");
        u1.setAge(-10);
        u1.setSalary(100000d);
        u1.setCreatedAt(new Date());
        u1.setProjects(new ArrayList<>());

        u1.getProjects().add(p1);
        p1.setUser(u1);

        Set<ConstraintViolation<User>> constraintViolations =
                validator.validate(u1);

        Assert.assertNotNull(constraintViolations);
        Assert.assertEquals(3, constraintViolations.size());
    }
}
