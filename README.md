Smart Recipe Recommendation

A modern, full-stack web application built with Java that acts as a personal AI chef. Users can input the ingredients they have on hand, and the application leverages a powerful Large Language Model (Groq API) to generate a unique, detailed, and beginner-friendly recipe tailored for Indian home cooking.

This project demonstrates the integration of a Java backend (using SparkJava) with a dynamic, server-rendered frontend (using Handlebars and Tailwind CSS), resulting in a professional and user-friendly website.

✨ Features

    AI-Powered Recipe Generation: Get custom recipes based on any combination of ingredients.

    Culturally Aware AI: The AI is specifically prompted to generate recipes with ingredients commonly found in an Indian kitchen.

    Detailed for Beginners: Recipes are extremely detailed, explaining every step, defining cooking terms, and providing sensory cues.

    Professional UI: A clean, responsive, and visually appealing two-page design with high-quality hero images.

    Server-Side Rendering: The Java backend intelligently parses the AI's response and renders a beautiful, structured HTML page for each recipe.

    Print-Friendly: A dedicated "Print" button on the recipe page generates a clean, ad-free version perfect for the kitchen.

🛠️ Tech Stack

    Backend:

        Java (JDK 17+)

        SparkJava: A micro web framework for creating the server.

        Apache Maven: For project management and dependencies.

    Frontend:

        HTML5

        Tailwind CSS: For modern and responsive styling.

        Handlebars: A templating engine for server-side rendering of HTML pages.

    AI Integration:

        Groq API: For fast and reliable Large Language Model access.

    Deployment:

        Render: For free, live hosting of the Java application.

    Libraries:

        Gson: For robust JSON parsing and creation.

🚀 How to Run Locally

To run this project on your own machine, follow these steps:

    Prerequisites:

        Make sure you have Java (JDK 17 or higher) installed.

        Make sure you have Apache Maven installed.

        You will need a free API key from Groq.

    Clone the Repository:

    git clone https://github.com/AaryanGaurat/smart-recipe-app
    cd smart-recipe-app

    Add Your API Key:

        Open the file src/main/java/com/recipeapp/WebApp.java.

        Find the line private static final String API_KEY = "YOUR_GROQ_API_KEY";

        Replace the placeholder with your actual Groq API key.

    Run the Application:

        Open a terminal in the project's root directory (where the pom.xml file is).

        Run the following Maven command:

        mvn compile exec:java

        The server will start. Open your web browser and go to http://localhost:4567.

☁️ Deployment

To make this website live for anyone to see, you can deploy it for free using Render.

    Upload to GitHub: Push your complete project code to a public GitHub repository.

    Connect to Render: Create a free account on Render and connect it to your GitHub account.

    Create a New Web Service:

        Point Render to your GitHub repository.

        Use the following settings:

            Runtime: Java

            Build Command: mvn clean package

            Start Command: java -jar target/smart-recipe-app-1.0-SNAPSHOT.jar

            Instance Type: Free

        Add an Environment Variable with the key GROQ_API_KEY and your API key as the value.

    Deploy: Click "Create Web Service". Render will build and deploy your application, giving you a public URL to share.

🧑‍💻 Developed By

    Tanmay Bhokare

    Aaryan Gaurat

    Md Samid

    Varad Chaudhari

📄 License

This project is licensed under the MIT License. See the LICENSE file for more details.
