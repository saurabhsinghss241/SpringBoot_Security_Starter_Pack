# SpringBoot_Security_Starter_Pack

## OBJECTIVE - Implement Spring Security to our Spring Boot Project.

Each stage of this process is captured in different branches.

Stage 1 - Main Branch (contains CRUD API endpoints responsible for USER, ROLE and PRIVILEGE) 
Stage 2 - BasicAuth (Secured our API endpoints using spring boot security)
Stage 3 - BasicAuthWithJPA (Changed the default basic auth implementation with custom implementation)

## Implementation

Stage 1 - Implement basic CRUD API endpoints for USER, ROLE and PRIVILEGE

Stage 2 - Add spring-boot-starter-security dependency to your pom.xml file.

Stage 3 - This is a bit complex.

### Workflow

#### What we want?
Remove default Basic Auth implementation.
We want to get user details form Database and authenticate based on that.

#### What we will need?
User, Role and Privilege info stored in Database. (Already completed in Stage1)
Mapping Roles with Privileges and Users with Roles (Already completed in Stage1)
Spring Data JPA to intract with Database and fetch required info (Completed in Stage1)

Now Security side of things.
A Quick overview how things are working under the hood.

User sends a request with credentials.
Authentication Filters capture this req and creates an instance of Authentication and store our credentials in it.
Now this Authentication instance will be passed on to AuthenticationManager.
AuthenticationManager is an interface with authenticate method.
AuthenticationMager will need an AuthenticationProvider.
AuthenticationProvider is an interface contains two methods authenticate(handles the actual authentication) and supports(informs if this AuthenticationProvider can handle the type of authentication requested by AuthenticationManager).
We can have have multiple implementaions for AuthenticationProvider and AuthenticationManager will decide which one to choose at runtime.
Now to authenticate a req AuthenticationProvider needs user information which it can crosscheck with provided data.
To get the user data AuthenticationProvider need help from UserDetailsService which has a method loadUserByUserName() which returns a UserDetails instance containing all the user realted information.
Based on this ApplicationProvider will authenticate our request if successfull it will return an instance of Authentication with principal and authorities information.
If it fails it throws and exception.

Authentication Filter -- Authentication(Cred) --> AuthenticationManager --> AuthenticationProvider --> UserDetailsService.
                                                                       <-- Authentication(Principal) --
Input - Credentials
Output - Principal (information about the logged in user)

Authentication - holds the credentials, when user is authenticated it holds the principal.

AuthenticationProvider - This is responsible for the actual authentication.
Input - Authentication instance with credentails.
Outout - Authentication instance with principal and authorities info.

AuthenticationManager - Cordinates multiple AuthenticationProvider and choose the right provider.
Finds the right provider based on the supports() method on AuthenticationProvider.

To authenticate AuthenticationProvider needs userinformation and that is done by UserDetailsService.
UserDetailsService returns UserDetails.

Now we understood that under the hood that actually does the authentication is AuthenticationProvider.
Now we need spring to do the authentication in the way we want.
User details should be fetched from our database and then the user should be authenticated.
We know that AuthenticationProvider needs UserDetailsService to get the user details so we will have to implement that also.

Task we need to do.
Problem - Implement UserDetailsService so that you can fetch users info from database.
Solution
Create a class MyUserdetailsService and implement UserDetailsService.
Provide implementaion for loadUserByUserName() (you can use userrepository or userservice to achive this)

Problem - loadUserByUserName returns an instance of UserDetails but have User instance.
Solution - 
Create a new class MyUserDetails that implements UserDetails.
define all the methods.
store your user info in this (private User user initilize using constructor)
Define getAuthorities (capture roles info from user and populate an arraylist of SimpleGrantedAuthorities and return)

Now after finding the User with respective username.
Return an instance of MyUserDetails with User info saved init.
return new MyUserDetails(user);

Problem - We have implemented our custom UserDetailsService now we need to implement our own AuthenticationProvider.
Solution - 
AuthenticationProvider need 2 things.
UserDetailsService - Aready resolved this by creating MyUserDetailsService
PasswordEncoder - This is needed because spring boot does't want us to store plain password and it expects that all the passwords store in our db will be encrypted.
Using this passwordencoder spring boot is going to encrypt our cred and match with the password saved in db.

```sh
@Bean
public PasswordEncoder passwordEncoder(){
  return new BCryptPasswordEncoder(10);
}
```
Here I am using BCrypt you can use PasswordEncoder of your own choice.

In our case The default SpringBoot AuthenticationProvider will also work.
If you want to create your own AuthenticationProvider.
Steps
Create an instance of DAOAuthenticationProvider.
provide an UserDetailsService and PasswordEncoder

```sh
@Bean
public DAOAuthenticationProvider daoAuthenticationProvider(){
  DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
  authProvider.setUserDetailsService(userDetailsService);
  authProvider.setPasswordEncoder(passwordEncoder);
}
```
Don't forget to add.
@Autowired 
private UserDetailsService userDetailsService;
@Autowired
private PasswordEncoder passwordEncoder;

Problem - We have implemented UserDetailsService and AuthenticationProvider now we have to tell AuthenticationManager to use which AuthenticationProvider for the incoming request.
Solution -
We cann't interact with AuthenticationManager directly.
But Spring Boot Security provides us with AuthenticationManagerBuilder using this we can intract with AuthenticationManager.

How to get hold of this AuthenticationManagerBuilder?
SpringBoot has a class WebSecurityConfigurerAdapter which has configure method that takes AuthenticationManagerBuilder as a parameter.
Create a SecurityConfiguration class and extend WebSecurityConfigurerAdapter.
Now we have to override configure method that takes AuthenticationManagerBuilder as parameter.
Now we can provide our own AuthenticationProvider.

```sh
@override
protected void configure(AuthenticationManagerBuilder auth){
  auth.authenticationProvider(daoAuthenticationProvider());
}

```

OR

In our case the default AuthenticationProvider will also work we just have to provide the UserServiceDetails.

```sh
@override
protected void configure(AuthenticationManagerBuilder auth){
  auth.userDetailsService(userDetailsService);
}

```
This will also work fine.

### NOTE

Always save roles in your database with prefix ROLE
example ROLE_ADMIN, ROLE_USER

Save encrypted password in the database.
Can be done when you recieve a user record from the client.
As soon as you recieve a new user.
User the PasswordEncoder and encode the password present in the request.
Then save this user info in DB.

userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));



