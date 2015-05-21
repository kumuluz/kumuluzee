<jsp:useBean id="user" class="com.kumuluz.ee.jetty.jsp.test.beans.UserBean" scope="session"/>
<jsp:useBean id="time" class="com.kumuluz.ee.jetty.jsp.test.beans.TimeBean" scope="request"/>

<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>KumuluzEE Jsp test</title>
</head>
<body>

    <p>
        <span>The time is: <%= time.getCurrentTime() %></span>
        <br/>
        <br/>
        <span>The current user is:</span>
        <br/>
        <span>Username: <%= user.getUsername() %></span><br/>
        <span>Email: <%= user.getEmail() %></span><br/>
        <span>First name: <%= user.getFirstname() %></span><br/>
        <span>Last name: <%= user.getLastname() %></span><br/>
        <span>Age: <%= user.getAge() %></span><br/>
        <span>Country: <%= user.getCountry() %></span><br/>
        <span>Created: <%= user.getCreated() %></span><br/>
    </p>

</body>
</html>