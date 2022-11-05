# Javalin MVC Web Starter
Example Web Application Using Javalin MVC

## Getting started
1. Download or clone the repository from GitHub
2. In Postgres, 
   * Create a new user named pg_user that can log in
   * Create a new database named web_starter
   > You can use a different database name, username, and password, you just need to update the `application-local.conf` file.
3. Make sure your `JAVA_HOME` is set to Java 11 or higher.
4. Run `mvn clean compile`
    * This will make sure everything is compiling for your machine
    * This will generate classes for the QueryDSL example
    * Rerun this command anytime you make model changes
   > Intellij doesn't seem to pick up the generated classes automatically. You can try marking the `common/target/generated-sources` as a generated source root.
5. Run the application
   * Make sure `web/` is your working directory. The application expects to find the `config/` and `public/` directories directly beneath it.
