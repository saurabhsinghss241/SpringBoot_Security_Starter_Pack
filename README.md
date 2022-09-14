# SpringBoot_Security_Starter_Pack

## OBJECTIVE - Implement Spring Security in our Spring Boot Project.

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
- Remove default Basic Auth implementation.<br />
- We want to get user details form Database and authenticate based on that.<br />

#### What we will need?
- User, Role and Privilege info stored in Database. (Already completed in Stage1)<br />
- Mapping Roles with Privileges and Users with Roles (Already completed in Stage1)<br />
- Spring Data JPA to intract with Database and fetch required info (Completed in Stage1)<br /><br />

Now Security side of things.<br />
A Quick overview how things are working under the hood.<br /><br />

- User sends a request with credentials.<br />
- Authentication Filters capture this req and creates an instance of Authentication and store our credentials in it.<br />
- Now this Authentication instance will be passed on to AuthenticationManager.<br />
- AuthenticationManager is an interface with authenticate method.<br />
- AuthenticationMager will need an AuthenticationProvider.<br />
- AuthenticationProvider is an interface contains two methods authenticate(handles the actual authentication) and supports(informs if this AuthenticationProvider can handle the type of authentication requested by AuthenticationManager).<br />
- We can have have multiple implementaions for AuthenticationProvider and AuthenticationManager will decide which one to choose at runtime.<br />
- Now to authenticate a req AuthenticationProvider needs user information which it can crosscheck with provided data.<br />
- To get the user data AuthenticationProvider need help from UserDetailsService which has a method loadUserByUserName() which returns a UserDetails instance containing all the user realted information.<br />
- Based on this ApplicationProvider will authenticate our request if successfull it will return an instance of Authentication with principal and authorities information.<br />
- If it fails it throws and exception.<br /><br />

Authentication Filter -- Authentication(Cred) --> AuthenticationManager --> AuthenticationProvider --> UserDetailsService.
                                                                       <-- Authentication(Principal) --
<br /><br />
**Input** - Credentials<br />
**Output** - Principal (information about the logged in user)<br /><br />

**Authentication** - holds the credentials, when user is authenticated it holds the principal.<br /><br />

**AuthenticationProvider** - This is responsible for the actual authentication.<br />
**Input** - Authentication instance with credentails.<br />
**Outout** - Authentication instance with principal and authorities info.<br /><br />

**AuthenticationManager** - Cordinates multiple AuthenticationProvider and choose the right provider.<br />
Finds the right provider based on the supports() method on AuthenticationProvider.<br /><br />

**UserDetailsService** - Responsible for retrieving user information.<br />
**Input** - Username<br />
**Output** - An instance of UserDetails containg all the information related to requested user.<br /><br />

- *To authenticate AuthenticationProvider needs userinformation and that is provided by UserDetailsService.*<br />
- *UserDetailsService returns UserDetails.*<br /><br />

Now we understood that under the hood that actually does the authentication is AuthenticationProvider.<br />
Now we need spring to do the authentication in the way we want.<br />
User details should be fetched from our database and then the user should be authenticated.<br />
We know that AuthenticationProvider needs UserDetailsService to get the user details so we will have to implement that also.<br /><br />

**Task we need to do.**<br /><br />
**Problem** - Implement UserDetailsService so that you can fetch users info from database.<br />
**Solution**<br />
Create a class MyUserdetailsService and implement UserDetailsService.<br />
Provide implementaion for loadUserByUserName() (you can use userrepository or userservice to achive this)<br /><br />

**Problem** - loadUserByUserName returns an instance of UserDetails but have User instance.<br />
**Solution** - <br />
Create a new class MyUserDetails that implements UserDetails.<br />
define all the methods.<br />
store your user info in this (private User user initilize using constructor)<br />
Define getAuthorities (capture roles info from user and populate an arraylist of SimpleGrantedAuthorities and return)<br />
Now after finding the User with respective username.<br />
Return an instance of MyUserDetails with User info saved init.<br />
return new MyUserDetails(user);<br /><br />

**Problem** - We have implemented our custom UserDetailsService now we need to implement our own AuthenticationProvider.<br />
**Solution** - <br />
AuthenticationProvider need 2 things.<br />
*UserDetailsService* - Aready resolved this by creating MyUserDetailsService<br />
*PasswordEncoder* - This is needed because spring boot does't want us to store plain password and it expects that all the passwords store in our db will be encrypted.<br />
Using this passwordencoder spring boot is going to encrypt our cred and match with the password saved in db.<br /><br />

```sh
@Bean
public PasswordEncoder passwordEncoder(){
  return new BCryptPasswordEncoder(10);
}
```
<br /><br />
Here I am using BCrypt you can use PasswordEncoder of your own choice.<br /><br />

In our case The default SpringBoot AuthenticationProvider will also work.<br />
If you want to create your own AuthenticationProvider.<br />
Steps<br />
Create an instance of DAOAuthenticationProvider.<br />
provide an UserDetailsService and PasswordEncoder<br /><br />

```sh
@Bean
public DAOAuthenticationProvider daoAuthenticationProvider(){
  DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
  authProvider.setUserDetailsService(userDetailsService);
  authProvider.setPasswordEncoder(passwordEncoder);
}
```
<br /><br />
Don't forget to add.<br />
```sh
@Autowired 
private UserDetailsService userDetailsService;
@Autowired
private PasswordEncoder passwordEncoder;
```

**Problem** - We have implemented UserDetailsService and AuthenticationProvider now we have to tell AuthenticationManager to use which AuthenticationProvider for the incoming request.<br />
**Solution** -<br />
Now we want to configue the AuthenticationManager because we want it to use our AuthenticationProvider to authenticate incoming request.
But we cann't interact with AuthenticationManager directly.<br />
So Spring Boot Security provides us with AuthenticationManagerBuilder using this we can configure AuthenticationManager.<br /><br />

**AuthenticationManagerBuilder** - We use AuthenticationManagerBuilder to configure what the AuthenticationManager should do.<br>
Now we need to do two things.
- Get hold of AuthenticationManagerBuilder.
- Set the configuration on it. <br><br>

How to get hold of this AuthenticationManagerBuilder?<br />
SpringBoot has a class WebSecurityConfigurerAdapter which has configure method that takes AuthenticationManagerBuilder as a parameter.<br />
Create a SecurityConfiguration class and extend WebSecurityConfigurerAdapter.<br />
Now we have to override configure method that takes AuthenticationManagerBuilder as parameter.<br />
Now we can provide our own AuthenticationProvider.<br /><br />

```sh
@override
protected void configure(AuthenticationManagerBuilder auth){
  auth.authenticationProvider(daoAuthenticationProvider());
}

```

OR<br />

In our case the default AuthenticationProvider will also work we just have to provide the UserServiceDetails.<br />

```sh
@override
protected void configure(AuthenticationManagerBuilder auth){
  auth.userDetailsService(userDetailsService);
}

```
This will also work fine.<br /><br />

### NOTE

Always save roles in your database with *prefix ROLE*<br />
example ROLE_ADMIN, ROLE_USER<br /><br />

Save *encrypted password* in the database.<br />
Can be done when you recieve a user record from the client.<br />
As soon as you recieve a new user.<br />
User the *PasswordEncoder* and encode the password present in the request.<br />
Then save this user info in DB.<br />
```sh
userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
```
<br /><br />
I have taken email as username in loadUserByUsername() method of MyUserDetailsService.



