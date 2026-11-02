# ---------- Etapa 1: build (compila e gera o .jar) ----------
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copia primeiro os arquivos do Gradle (aproveita o cache do Docker)
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Copia o codigo-fonte e gera o jar (sem rodar os testes)
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ---------- Etapa 2: runtime (imagem final, so com o JRE) ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Usuario sem privilegios (boa pratica de seguranca)
RUN useradd --system --no-create-home appuser
USER appuser

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
