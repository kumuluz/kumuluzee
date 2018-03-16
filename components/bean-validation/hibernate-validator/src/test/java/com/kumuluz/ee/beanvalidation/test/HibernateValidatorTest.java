/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
*/
package com.kumuluz.ee.beanvalidation.test;

import com.kumuluz.ee.beanvalidation.test.beans.Project;
import com.kumuluz.ee.beanvalidation.test.beans.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

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
        Assert.assertEquals(4, constraintViolations.size());
    }
}
