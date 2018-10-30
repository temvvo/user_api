# User api challenge


User api and tests 


## Prerequisites
1. Install Java [http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html#javasejdk]
2. Install Gradle [https://gradle.org/install/]
3. Install Postman [https://www.getpostman.com/]


## Local development

Update dependencies: 

```
$ cd api_challenge
$ vim src/main/resources/application.properties
$ ./gradlew build --refresh-dependencies
$ ./gradlew test
```

Run Tests:
```
$ cd api_challenge
$ ./gradlew test
```

Run app:
```
$ cd api_challenge
$ ./gradlew bootRun
```
 {{base_url}} = http://localhost:8080

1. Fill database with dummydata using this POST endpoint: {{base_url}}/public/testdata/user

2. Login with following POST endpoint: {{base_url}}/public/user/login

 ```   
    Body:
    {
      "email": "nacho@nubicall.com",
      "password": "{{petowner1pwHashed}}"
    }
    
    Pre request script:
    var pw = "Test1234";
    var hashedPw = String(CryptoJS.SHA256(pw));
    pm.environment.set("petowner1pwHashed", hashedPw);
    console.log("PW: "+pw+" petowner1pwHashed: "+pm.environment.get("petowner1pwHashed"));
    
    Tests:
    var jsonData = JSON.parse(responseBody);
    
    if (jsonData.token) {
        pm.environment.set("token", jsonData.token);
        
        var base64Url = jsonData.token.split('.')[1];
        var base64 = base64Url.replace('-', '+').replace('_', '/');
    
        var extractUserData = JSON.parse(atob(base64));
        pm.environment.set("currentUserId", parseInt(extractUserData.sub));
    
        console.log("Token is: " + pm.environment.get("token"));
        console.log("Current User id is: " + pm.environment.get("currentUserId"));
    
    }
```   
3. Use the token provided by login endpoint as Authorization, Type: Bearer token {{token}}   
4. Call any endpoint declared in user-api.yaml with this Autorization token. 

##Code Coverage Report:
/api_challenge/src/test/java/com/user/api_challenge/code/coverage/com.user.api_challenge.user.controller/.classes/UserController.html
## Responsible developer:
* Carlos Pereyra <temvvo@gmail.com>
