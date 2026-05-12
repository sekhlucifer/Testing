# Use the official Playwright Java image which comes with JDK 17, all browsers, and system dependencies pre-installed
FROM mcr.microsoft.com/playwright/java:v1.40.0-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source code into the container
COPY pom.xml .
COPY src ./src
COPY testng-crossbrowser.xml .

# Resolve all Maven dependencies (this caches them in the Docker layer so it doesn't download every time)
RUN mvn -B dependency:resolve dependency:resolve-plugins compile test-compile

# Command to run when the container starts
# This will execute the cross-browser TestNG suite headlessly
CMD ["mvn", "-B", "clean", "test"]
