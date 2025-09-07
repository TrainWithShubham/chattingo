# Stage 1: Node.js build environment
FROM node:18-alpine AS build-env
WORKDIR /app
COPY package*.json ./
RUN npm install --only=production

# Stage 2: Build React application
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 3: Lightweight runtime with built files
FROM node:18-alpine AS runtime
WORKDIR /app
COPY --from=build /app/build ./build
RUN npm install -g serve
EXPOSE 3000
CMD ["serve", "-s", "build", "-l", "3000"]
