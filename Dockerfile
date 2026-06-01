# ============================================
# Этап 1: Сборка бэкенда (Spring Boot)
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS backend-builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true -B

# ============================================
# Этап 2: Сборка фронтенда (React + Vite)
# ============================================
FROM node:20-alpine AS frontend-builder

WORKDIR /app

# Копируем package.json и устанавливаем зависимости
COPY quiz-client/package*.json ./
RUN npm install --legacy-peer-deps

# Копируем исходники фронтенда и собираем
COPY quiz-client ./
RUN npm run build

# ============================================
# Этап 3: Финальный образ
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копируем JAR бэкенда
COPY --from=backend-builder /app/target/*.jar app.jar

# Копируем собранный фронтенд
COPY --from=frontend-builder /app/dist /usr/share/nginx/html

# Устанавливаем Nginx
RUN apk add --no-cache nginx && \
    echo 'server { \
        listen 3000; \
        server_name localhost; \
        root /usr/share/nginx/html; \
        index index.html; \
        location / { \
            try_files $uri $uri/ /index.html; \
        } \
        location /api/ { \
            proxy_pass http://localhost:8080/api/; \
            proxy_set_header Host $host; \
        } \
    }' > /etc/nginx/http.d/default.conf

# Открываем порты
EXPOSE 8080 3000

# Запускаем Nginx (фронтенд) и Java (бэкенд)
CMD nginx && java -jar app.jar