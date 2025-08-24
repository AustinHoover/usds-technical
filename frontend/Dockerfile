# ---------- Build stage ----------
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
# For CRA: RUN npm run build
# For Vite: VITE env already passed as build arg; use:
RUN npm run build

# ---------- Runtime stage ----------
FROM nginx:1.27-alpine
# Nginx config replaces default server: serves SPA and proxies /api to Spring
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Copy compiled assets
COPY --from=build /app/build /usr/share/nginx/html
# (If Vite: /app/dist instead of /app/build)
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]